package com.moko.lw001.activity;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.ble.lib.utils.MokoUtils;
import com.moko.lw001.AppConstants;
import com.moko.lw001.R;
import com.moko.lw001.R2;
import com.moko.lw001.dialog.AlertMessageDialog;
import com.moko.lw001.dialog.ChangePasswordDialog;
import com.moko.lw001.dialog.LoadingMessageDialog;
import com.moko.lw001.fragment.DeviceFragment;
import com.moko.lw001.fragment.GeneralFragment;
import com.moko.lw001.fragment.LoRaFragment;
import com.moko.lw001.fragment.PositionFragment;
import com.moko.lw001.utils.ToastUtils;
import com.moko.support.lw001.LoRaLW001MokoSupport;
import com.moko.support.lw001.OrderTaskAssembler;
import com.moko.support.lw001.entity.OrderCHAR;
import com.moko.support.lw001.entity.ParamsKeyEnum;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.IdRes;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceInfoActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    @BindView(R2.id.frame_container)
    FrameLayout frameContainer;
    @BindView(R2.id.radioBtn_lora)
    RadioButton radioBtnLora;
    @BindView(R2.id.radioBtn_position)
    RadioButton radioBtnPosition;
    @BindView(R2.id.radioBtn_general)
    RadioButton radioBtnGeneral;
    @BindView(R2.id.radioBtn_device)
    RadioButton radioBtnDevice;
    @BindView(R2.id.rg_options)
    RadioGroup rgOptions;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.iv_save)
    ImageView ivSave;
    private FragmentManager fragmentManager;
    private LoRaFragment loraFragment;
    private PositionFragment posFragment;
    private GeneralFragment generalFragment;
    private DeviceFragment deviceFragment;
    private ArrayList<String> mUploadMode;
    private ArrayList<String> mRegions;
    private int mSelectedRegion;
    private int mSelectUploadMode;
    private boolean mReceiverTag = false;
    private int disConnectType;

    private boolean savedParamsError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lw001_activity_device_info);
        ButterKnife.bind(this);
        fragmentManager = getFragmentManager();
        initFragment();
        radioBtnLora.setChecked(true);
        tvTitle.setText(R.string.title_lora);
        rgOptions.setOnCheckedChangeListener(this);
        EventBus.getDefault().register(this);
        mUploadMode = new ArrayList<>();
        mUploadMode.add("ABP");
        mUploadMode.add("OTAA");
        mRegions = new ArrayList<>();
        mRegions.add("AS923");
        mRegions.add("AU915");
        mRegions.add("CN470");
        mRegions.add("CN779");
        mRegions.add("EU433");
        mRegions.add("EU868");
        mRegions.add("KR920");
        mRegions.add("IN865");
        mRegions.add("US915");
        mRegions.add("RU864");
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mReceiverTag = true;
        if (!LoRaLW001MokoSupport.getInstance().isBluetoothOpen()) {
            LoRaLW001MokoSupport.getInstance().enableBluetooth();
        } else {
            showSyncingProgressDialog();
            List<OrderTask> orderTasks = new ArrayList<>();
            // sync time after connect success;
            orderTasks.add(OrderTaskAssembler.setTime());
            // get lora params
            orderTasks.add(OrderTaskAssembler.getLoraRegion());
            orderTasks.add(OrderTaskAssembler.getLoraUploadMode());
            orderTasks.add(OrderTaskAssembler.getLoraNetworkStatus());
            LoRaLW001MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
        }
    }

    private void initFragment() {
        loraFragment = LoRaFragment.newInstance();
        posFragment = PositionFragment.newInstance();
        generalFragment = GeneralFragment.newInstance();
        deviceFragment = DeviceFragment.newInstance();
        fragmentManager.beginTransaction()
                .add(R.id.frame_container, loraFragment)
                .add(R.id.frame_container, posFragment)
                .add(R.id.frame_container, generalFragment)
                .add(R.id.frame_container, deviceFragment)
                .show(loraFragment)
                .hide(posFragment)
                .hide(generalFragment)
                .hide(deviceFragment)
                .commit();
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 100)
    public void onConnectStatusEvent(ConnectStatusEvent event) {
        EventBus.getDefault().cancelEventDelivery(event);
        final String action = event.getAction();
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_DISCONNECTED.equals(action)) {
                if (LoRaLW001MokoSupport.getInstance().exportDatas != null) {
                    LoRaLW001MokoSupport.getInstance().exportDatas.clear();
                    LoRaLW001MokoSupport.getInstance().storeString = null;
                    LoRaLW001MokoSupport.getInstance().startTime = 0;
                    LoRaLW001MokoSupport.getInstance().sum = 0;
                }
                showDisconnectDialog();
            }
            if (MokoConstants.ACTION_DISCOVER_SUCCESS.equals(action)) {
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 100)
    public void onOrderTaskResponseEvent(OrderTaskResponseEvent event) {
        EventBus.getDefault().cancelEventDelivery(event);
        final String action = event.getAction();
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_CURRENT_DATA.equals(action)) {
                OrderTaskResponse response = event.getResponse();
                OrderCHAR orderCHAR = (OrderCHAR) response.orderCHAR;
                int responseType = response.responseType;
                byte[] value = response.responseValue;
                switch (orderCHAR) {
                    case CHAR_DISCONNECTED_NOTIFY:
                        final int length = value.length;
                        if (length != 5)
                            return;
                        int header = value[0] & 0xFF;
                        int flag = value[1] & 0xFF;
                        int cmd = value[2] & 0xFF;
                        int len = value[3] & 0xFF;
                        int type = value[4] & 0xFF;
                        if (header == 0xED && flag == 0x02 && cmd == 0x01 && len == 0x01) {
                            disConnectType = type;
                            if (type == 1) {
                                // valid password timeout
                            } else if (type == 2) {
                                // change password success
                            } else if (type == 3) {
                                // no data exchange timeout
                            } else if (type == 4) {
                                // reset success
                            }
                        }
                        break;
                }
            }
            if (MokoConstants.ACTION_ORDER_TIMEOUT.equals(action)) {
            }
            if (MokoConstants.ACTION_ORDER_FINISH.equals(action)) {
                dismissSyncProgressDialog();
            }
            if (MokoConstants.ACTION_ORDER_RESULT.equals(action)) {
                OrderTaskResponse response = event.getResponse();
                OrderCHAR orderCHAR = (OrderCHAR) response.orderCHAR;
                int responseType = response.responseType;
                byte[] value = response.responseValue;
                switch (orderCHAR) {
                    case CHAR_PARAMS:
                        if (value.length >= 4) {
                            int header = value[0] & 0xFF;// 0xED
                            int flag = value[1] & 0xFF;// read or write
                            int cmd = value[2] & 0xFF;
                            if (header != 0xED)
                                return;
                            ParamsKeyEnum configKeyEnum = ParamsKeyEnum.fromParamKey(cmd);
                            if (configKeyEnum == null) {
                                return;
                            }
                            int length = value[3] & 0xFF;
                            if (flag == 0x01) {
                                // write
                                int result = value[4] & 0xFF;
                                switch (configKeyEnum) {
                                    case KEY_TIME:
                                        if (result == 1)
                                            ToastUtils.showToast(DeviceInfoActivity.this, "Time sync completed!");
                                        break;
                                    case KEY_OFFLINE_LOCATION:
                                    case KEY_HEARTBEAT_INTERVAL:
                                    case KEY_TIME_ZONE:
                                    case KEY_SHUTDOWN_INFO_REPORT:
                                    case KEY_LOW_POWER:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        if (savedParamsError) {
                                            ToastUtils.showToast(DeviceInfoActivity.this, "Opps！Save failed. Please check the input characters and try again.");
                                        } else {
                                            AlertMessageDialog dialog = new AlertMessageDialog();
                                            dialog.setMessage("Saved Successfully！");
                                            dialog.setConfirm("OK");
                                            dialog.setCancelGone();
                                            dialog.show(getSupportFragmentManager());
                                        }
                                        break;
                                }
                            }
                            if (flag == 0x00) {
                                // read
                                switch (configKeyEnum) {
                                    case KEY_LORA_REGION:
                                        if (length > 0) {
                                            final int region = value[4] & 0xFF;
                                            mSelectedRegion = region;
                                        }
                                        break;
                                    case KEY_LORA_MODE:
                                        if (length > 0) {
                                            final int mode = value[4];
                                            mSelectUploadMode = mode;
                                            String loraInfo = String.format("%s/%s/ClassA",
                                                    mUploadMode.get(mSelectUploadMode - 1),
                                                    mRegions.get(mSelectedRegion));
                                            loraFragment.setLoRaInfo(loraInfo);
                                        }
                                        break;
                                    case KEY_NETWORK_STATUS:
                                        if (length > 0) {
                                            int networkStatus = value[4] & 0xFF;
                                            loraFragment.setLoraStatus(networkStatus);
                                        }
                                        break;
                                    case KEY_OFFLINE_LOCATION:
                                        if (length > 0) {
                                            int enable = value[4] & 0xFF;
                                            posFragment.setOfflineFix(enable);
                                        }
                                        break;
                                    case KEY_HEARTBEAT_INTERVAL:
                                        if (length > 0) {
                                            byte[] intervalBytes = Arrays.copyOfRange(value, 4, 4 + length);
                                            generalFragment.setHeartbeatInterval(MokoUtils.toInt(intervalBytes));
                                        }
                                        break;
                                    case KEY_TIME_ZONE:
                                        if (length > 0) {
                                            int timeZone = value[4];
                                            deviceFragment.setTimeZone(timeZone);
                                        }
                                        break;
                                    case KEY_SHUTDOWN_INFO_REPORT:
                                        if (length > 0) {
                                            int enable = value[4] & 0xFF;
                                            deviceFragment.setShutdownPayload(enable);
                                        }
                                        break;
                                    case KEY_LOW_POWER:
                                        if (length > 0) {
                                            int lowPower = value[4] & 0xFF;
                                            deviceFragment.setLowPower(lowPower);
                                        }
                                        break;
                                }
                            }

                        }
                        break;
                }
            }
        });
    }

    private void showDisconnectDialog() {
        if (disConnectType == 2) {
            AlertMessageDialog dialog = new AlertMessageDialog();
            dialog.setTitle("Change Password");
            dialog.setMessage("Password changed successfully!Please reconnect the device.");
            dialog.setConfirm("OK");
            dialog.setCancelGone();
            dialog.setOnAlertConfirmListener(() -> {
                setResult(RESULT_OK);
                finish();
            });
            dialog.show(getSupportFragmentManager());
        } else if (disConnectType == 3) {
            AlertMessageDialog dialog = new AlertMessageDialog();
            dialog.setMessage("No data communication for 2 minutes, the device is disconnected.");
            dialog.setConfirm("OK");
            dialog.setCancelGone();
            dialog.setOnAlertConfirmListener(() -> {
                setResult(RESULT_OK);
                finish();
            });
            dialog.show(getSupportFragmentManager());
        } else if (disConnectType == 4) {
            AlertMessageDialog dialog = new AlertMessageDialog();
            dialog.setTitle("Factory Reset");
            dialog.setMessage("Factory reset successfully!Please reconnect the device.");
            dialog.setConfirm("OK");
            dialog.setCancelGone();
            dialog.setOnAlertConfirmListener(() -> {
                setResult(RESULT_OK);
                finish();
            });
            dialog.show(getSupportFragmentManager());
        } else if (disConnectType == 1) {
            AlertMessageDialog dialog = new AlertMessageDialog();
            dialog.setMessage("The device is disconnected!");
            dialog.setConfirm("OK");
            dialog.setCancelGone();
            dialog.setOnAlertConfirmListener(() -> {
                setResult(RESULT_OK);
                finish();
            });
            dialog.show(getSupportFragmentManager());
        } else {
            if (LoRaLW001MokoSupport.getInstance().isBluetoothOpen()) {
                AlertMessageDialog dialog = new AlertMessageDialog();
                dialog.setTitle("Dismiss");
                dialog.setMessage("The device disconnected!");
                dialog.setConfirm("Exit");
                dialog.setCancelGone();
                dialog.setOnAlertConfirmListener(() -> {
                    setResult(RESULT_OK);
                    finish();
                });
                dialog.show(getSupportFragmentManager());
            }
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null) {
                String action = intent.getAction();
                if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            dismissSyncProgressDialog();
                            AlertDialog.Builder builder = new AlertDialog.Builder(DeviceInfoActivity.this);
                            builder.setTitle("Dismiss");
                            builder.setCancelable(false);
                            builder.setMessage("The current system of bluetooth is not available!");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DeviceInfoActivity.this.setResult(RESULT_OK);
                                    finish();
                                }
                            });
                            builder.show();
                            break;

                    }
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.REQUEST_CODE_LORA_CONN_SETTING) {
            if (resultCode == RESULT_OK) {
                ivSave.postDelayed(() -> {
                    showSyncingProgressDialog();
                    List<OrderTask> orderTasks = new ArrayList<>();
                    // setting
                    orderTasks.add(OrderTaskAssembler.getLoraRegion());
                    orderTasks.add(OrderTaskAssembler.getLoraUploadMode());
                    orderTasks.add(OrderTaskAssembler.getLoraNetworkStatus());
                    LoRaLW001MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
                }, 500);
            }
        }
        if (requestCode == AppConstants.REQUEST_CODE_SYSTEM_INFO) {
            if (resultCode == RESULT_OK) {
                AlertMessageDialog dialog = new AlertMessageDialog();
                dialog.setTitle("Update Firmware");
                dialog.setMessage("Update firmware successfully!\nPlease reconnect the device.");
                dialog.setConfirm("OK");
                dialog.setCancelGone();
                dialog.setOnAlertConfirmListener(() -> {
                    setResult(RESULT_OK);
                    finish();
                });
                dialog.show(getSupportFragmentManager());
            }
            if (resultCode == RESULT_FIRST_USER) {
                showDisconnectDialog();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiverTag) {
            mReceiverTag = false;
            // 注销广播
            unregisterReceiver(mReceiver);
        }
        EventBus.getDefault().unregister(this);
    }

    private LoadingMessageDialog mLoadingMessageDialog;

    public void showSyncingProgressDialog() {
        mLoadingMessageDialog = new LoadingMessageDialog();
        mLoadingMessageDialog.setMessage("Syncing..");
        mLoadingMessageDialog.show(getSupportFragmentManager());

    }

    public void dismissSyncProgressDialog() {
        if (mLoadingMessageDialog != null)
            mLoadingMessageDialog.dismissAllowingStateLoss();
    }

    public void onBack(View view) {
        back();
    }

    public void onSave(View view) {
        if (isWindowLocked())
            return;
        if (radioBtnGeneral.isChecked()) {
            if (generalFragment.isValid()) {
                showSyncingProgressDialog();
                generalFragment.saveParams();
            } else {
                ToastUtils.showToast(this, "Opps！Save failed. Please check the input characters and try again.");
            }
        }
    }

    private void back() {
        LoRaLW001MokoSupport.getInstance().disConnectBle();
//        mIsClose = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        if (checkedId == R.id.radioBtn_lora) {
            showLoRaAndGetData();
        } else if (checkedId == R.id.radioBtn_position) {
            showPosAndGetData();
        } else if (checkedId == R.id.radioBtn_general) {
            showGeneralAndGetData();
        } else if (checkedId == R.id.radioBtn_device) {
            showDeviceAndGetData();
        }
    }

    private void showDeviceAndGetData() {
        tvTitle.setText("Device Settings");
        ivSave.setVisibility(View.GONE);
        fragmentManager.beginTransaction()
                .hide(loraFragment)
                .hide(posFragment)
                .hide(generalFragment)
                .show(deviceFragment)
                .commit();
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>();
        // device
        orderTasks.add(OrderTaskAssembler.getTimeZone());
        orderTasks.add(OrderTaskAssembler.getShutdownInfoReport());
        orderTasks.add(OrderTaskAssembler.getLowPower());
        LoRaLW001MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    private void showGeneralAndGetData() {
        tvTitle.setText("General Setting");
        ivSave.setVisibility(View.VISIBLE);
        fragmentManager.beginTransaction()
                .hide(loraFragment)
                .hide(posFragment)
                .show(generalFragment)
                .hide(deviceFragment)
                .commit();
        showSyncingProgressDialog();
        LoRaLW001MokoSupport.getInstance().sendOrder(OrderTaskAssembler.getHeartBeatInterval());
    }

    private void showPosAndGetData() {
        tvTitle.setText("Positioning Strategy");
        ivSave.setVisibility(View.GONE);
        fragmentManager.beginTransaction()
                .hide(loraFragment)
                .show(posFragment)
                .hide(generalFragment)
                .hide(deviceFragment)
                .commit();
        showSyncingProgressDialog();
        LoRaLW001MokoSupport.getInstance().sendOrder(OrderTaskAssembler.getOfflineLocation());
    }

    private void showLoRaAndGetData() {
        tvTitle.setText(R.string.title_lora);
        ivSave.setVisibility(View.GONE);
        fragmentManager.beginTransaction()
                .show(loraFragment)
                .hide(posFragment)
                .hide(generalFragment)
                .hide(deviceFragment)
                .commit();
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>();
        // get lora params
        orderTasks.add(OrderTaskAssembler.getLoraRegion());
        orderTasks.add(OrderTaskAssembler.getLoraUploadMode());
        orderTasks.add(OrderTaskAssembler.getLoraNetworkStatus());
        LoRaLW001MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    public void onChangePassword(View view) {
        if (isWindowLocked())
            return;
        final ChangePasswordDialog dialog = new ChangePasswordDialog(this);
        dialog.setOnPasswordClicked(password -> {
            showSyncingProgressDialog();
            LoRaLW001MokoSupport.getInstance().sendOrder(OrderTaskAssembler.changePassword(password));
        });
        dialog.show();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override

            public void run() {
                runOnUiThread(() -> dialog.showKeyboard());
            }
        }, 200);
    }

    public void onLoRaConnSetting(View view) {
        if (isWindowLocked())
            return;
        Intent intent = new Intent(this, LoRaConnSettingActivity.class);
        startActivityForResult(intent, AppConstants.REQUEST_CODE_LORA_CONN_SETTING);
    }

    public void onLoRaAppSetting(View view) {
        if (isWindowLocked())
            return;
        Intent intent = new Intent(this, LoRaAppSettingActivity.class);
        startActivity(intent);
    }

    public void onWifiFix(View view) {
        if (isWindowLocked())
            return;
        Intent intent = new Intent(this, PosWifiFixActivity.class);
        startActivity(intent);
    }

    public void onBleFix(View view) {
        if (isWindowLocked())
            return;
        Intent intent = new Intent(this, PosBleFixActivity.class);
        startActivity(intent);
    }

    public void onGPSFix(View view) {
        if (isWindowLocked())
            return;
        Intent intent = new Intent(this, PosGpsFixActivity.class);
        startActivity(intent);
    }

    public void onDeviceMode(View view) {
        if (isWindowLocked())
            return;
        Intent intent = new Intent(this, DeviceModeActivity.class);
        startActivity(intent);
    }

    public void onAuxiliaryInterval(View view) {
        if (isWindowLocked())
            return;
        Intent intent = new Intent(this, AuxiliaryOperationActivity.class);
        startActivity(intent);
    }

    public void onBleSettings(View view) {
        if (isWindowLocked())
            return;
        Intent intent = new Intent(this, BleSettingsActivity.class);
        startActivity(intent);
    }

    public void onAxisSettings(View view) {
        if (isWindowLocked())
            return;
        Intent intent = new Intent(this, AxisSettingActivity.class);
        startActivity(intent);
    }

    public void onLocalDataSync(View view) {
        if (isWindowLocked())
            return;
        startActivity(new Intent(this, ExportDataActivity.class));
    }

    public void onIndicatorSettings(View view) {
        if (isWindowLocked())
            return;
        startActivity(new Intent(this, IndicatorSettingsActivity.class));
    }

    public void selectTimeZone(View view) {
        if (isWindowLocked())
            return;
        deviceFragment.showTimeZoneDialog();
    }

    public void onOnOff(View view) {
        if (isWindowLocked())
            return;
        startActivity(new Intent(this, OnOffActivity.class));
    }

    public void selectLowPowerPrompt(View view) {
        if (isWindowLocked())
            return;
        deviceFragment.showLowPowerDialog();
    }

    public void onDeviceInfo(View view) {
        if (isWindowLocked())
            return;
        startActivityForResult(new Intent(this, SystemInfoActivity.class), AppConstants.REQUEST_CODE_SYSTEM_INFO);
    }

    public void onFactoryReset(View view) {
        if (isWindowLocked())
            return;
        AlertMessageDialog dialog = new AlertMessageDialog();
        dialog.setTitle("Factory Reset!");
        dialog.setMessage("After factory reset,all the data will be reseted to the factory values.");
        dialog.setConfirm("OK");
        dialog.setOnAlertConfirmListener(() -> {
            showSyncingProgressDialog();
            LoRaLW001MokoSupport.getInstance().sendOrder(OrderTaskAssembler.restore());
        });
        dialog.show(getSupportFragmentManager());
    }

    public void onPowerOff(View view) {
        if (isWindowLocked())
            return;
        AlertMessageDialog dialog = new AlertMessageDialog();
        dialog.setTitle("Warning!");
        dialog.setMessage("Are you sure to turn off the device? Please make sure the device has a button to turn on!");
        dialog.setConfirm("OK");
        dialog.setOnAlertConfirmListener(() -> {
            showSyncingProgressDialog();
            LoRaLW001MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setWorkMode(0));
        });
        dialog.show(getSupportFragmentManager());
    }

    public void onOfflineFix(View view) {
        if (isWindowLocked())
            return;
        posFragment.changeOfflineFix();
    }

    public void onShutdownPayload(View view) {
        if (isWindowLocked())
            return;
        deviceFragment.changeShutdownPayload();
    }

    public void onLowPowerPayload(View view) {
        if (isWindowLocked())
            return;
        deviceFragment.changeLowPowerPayload();
    }
}
