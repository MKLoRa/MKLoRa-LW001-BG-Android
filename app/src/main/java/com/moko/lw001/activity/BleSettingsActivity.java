package com.moko.lw001.activity;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;

import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.lib.loraui.dialog.AlertMessageDialog;
import com.moko.lib.loraui.dialog.BottomDialog;
import com.moko.lib.loraui.utils.ToastUtils;
import com.moko.lw001.R;
import com.moko.lw001.databinding.Lw001ActivityBleSettingsBinding;
import com.moko.support.lw001.LoRaLW001MokoSupport;
import com.moko.support.lw001.OrderTaskAssembler;
import com.moko.support.lw001.entity.OrderCHAR;
import com.moko.support.lw001.entity.ParamsKeyEnum;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class BleSettingsActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    private Lw001ActivityBleSettingsBinding mBind;;
    private boolean mReceiverTag = false;
    private boolean savedParamsError;
    private ArrayList<String> mValues;
    private int mSelected;
    private int mShowSelected;
    private boolean mConnectableEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = Lw001ActivityBleSettingsBinding.inflate(getLayoutInflater());
        setContentView(mBind.getRoot());
        mValues = new ArrayList<>();
        mValues.add("1M PHY(BLE 4.x)");
        mValues.add("1M PHY(BLE 5)");
        mValues.add("1M PHY(BLE 4.x+BLE 5)");
        mValues.add("Coded PHY(BLE 5)");
        mBind.cbBeaconMode.setOnCheckedChangeListener(this);
        EventBus.getDefault().register(this);
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mReceiverTag = true;
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.getBeaconEnable());
        orderTasks.add(OrderTaskAssembler.getConnectable());
        orderTasks.add(OrderTaskAssembler.getAdvInterval());
        orderTasks.add(OrderTaskAssembler.getAdvTimeout());
        orderTasks.add(OrderTaskAssembler.getScanType());
        LoRaLW001MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 200)
    public void onConnectStatusEvent(ConnectStatusEvent event) {
        final String action = event.getAction();
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_DISCONNECTED.equals(action)) {
                finish();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 200)
    public void onOrderTaskResponseEvent(OrderTaskResponseEvent event) {
        final String action = event.getAction();
        if (!MokoConstants.ACTION_CURRENT_DATA.equals(action))
            EventBus.getDefault().cancelEventDelivery(event);
        runOnUiThread(() -> {
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
                                    case KEY_BEACON_ENABLE:
                                    case KEY_CONNECTABLE:
                                    case KEY_ADV_INTERVAL:
                                    case KEY_ADV_TIMEOUT:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        break;
                                    case KEY_SCAN_TYPE:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        if (savedParamsError) {
                                            ToastUtils.showToast(BleSettingsActivity.this, "Opps！Save failed. Please check the input characters and try again.");
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
                                    case KEY_BEACON_ENABLE:
                                        if (length > 0) {
                                            int enable = value[4] & 0xFF;
                                            mBind.cbBeaconMode.setChecked(enable == 1);
                                            if (mBind.cbBeaconMode.isChecked()) {
                                                mBind.clBeaconModeOpen.setVisibility(View.VISIBLE);
                                                mBind.clBeaconModeClose.setVisibility(View.GONE);
                                            } else {
                                                mBind.clBeaconModeOpen.setVisibility(View.GONE);
                                                mBind.clBeaconModeClose.setVisibility(View.VISIBLE);
                                            }
                                        }
                                        break;
                                    case KEY_CONNECTABLE:
                                        if (length > 0) {
                                            int enable = value[4] & 0xFF;
                                            mConnectableEnable = enable == 1;
                                            mBind.ivConnectable.setImageResource(mConnectableEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                                        }
                                        break;
                                    case KEY_ADV_INTERVAL:
                                        if (length > 0) {
                                            int interval = value[4] & 0xFF;
                                            mBind.etAdvInterval.setText(String.valueOf(interval));
                                        }
                                        break;
                                    case KEY_ADV_TIMEOUT:
                                        if (length > 0) {
                                            int timeout = value[4] & 0xFF;
                                            mBind.etAdvTimeout.setText(String.valueOf(timeout));
                                        }
                                        break;
                                    case KEY_SCAN_TYPE:
                                        if (length > 0) {
                                            int type = value[4] & 0xFF;
                                            mSelected = type;
                                            if (type == 0) {
                                                mShowSelected = 0;
                                            } else if (type == 1) {
                                                mShowSelected = 1;
                                            } else if (type == 2) {
                                                mShowSelected = 3;
                                            } else if (type == 3) {
                                                mShowSelected = 2;
                                            }
                                            mBind.tvScanType.setText(mValues.get(mShowSelected));
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
                            finish();
                            break;
                    }
                }
            }
        }
    };

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


    public void onBack(View view) {
        backHome();
    }

    @Override
    public void onBackPressed() {
        backHome();
    }

    private void backHome() {
        setResult(RESULT_OK);
        finish();
    }

    public void selectScanType(View view) {
        if (isWindowLocked())
            return;
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mValues, mShowSelected);
        dialog.setListener(value -> {
            mShowSelected = value;
            if (value == 0) {
                mSelected = 0;
            } else if (value == 1) {
                mSelected = 1;
            } else if (value == 2) {
                mSelected = 3;
            } else if (value == 3) {
                mSelected = 2;
            }
            mBind.tvScanType.setText(mValues.get(value));
        });
        dialog.show(getSupportFragmentManager());
    }

    public void onSave(View view) {
        if (isWindowLocked())
            return;
        if (isValid()) {
            showSyncingProgressDialog();
            saveParams();
        } else {
            ToastUtils.showToast(this, "Opps！Save failed. Please check the input characters and try again.");
        }
    }

    private boolean isValid() {
        if (mBind.cbBeaconMode.isChecked()) {
            final String intervalStr = mBind.etAdvInterval.getText().toString();
            if (TextUtils.isEmpty(intervalStr))
                return false;
            final int interval = Integer.parseInt(intervalStr);
            if (interval < 1 || interval > 100) {
                return false;
            }
        } else {
            final String timeoutStr = mBind.etAdvTimeout.getText().toString();
            if (TextUtils.isEmpty(timeoutStr))
                return false;
            final int timeout = Integer.parseInt(timeoutStr);
            if (timeout < 1 || timeout > 60) {
                return false;
            }
        }
        return true;

    }


    private void saveParams() {
        final String intervalStr = mBind.etAdvInterval.getText().toString();
        final String timeoutStr = mBind.etAdvTimeout.getText().toString();
        final int interval = Integer.parseInt(intervalStr);
        final int timeout = Integer.parseInt(timeoutStr);
        savedParamsError = false;
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setBeaconEnable(mBind.cbBeaconMode.isChecked() ? 1 : 0));
        if (mBind.cbBeaconMode.isChecked()) {
            orderTasks.add(OrderTaskAssembler.setConnectable(mConnectableEnable ? 1 : 0));
            orderTasks.add(OrderTaskAssembler.setAdvInterval(interval));
        } else {
            orderTasks.add(OrderTaskAssembler.setAdvTimeout(timeout));
        }
        orderTasks.add(OrderTaskAssembler.setScanType(mSelected));
        LoRaLW001MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mBind.clBeaconModeOpen.setVisibility(View.VISIBLE);
            mBind.clBeaconModeClose.setVisibility(View.GONE);
        } else {
            mBind.clBeaconModeOpen.setVisibility(View.GONE);
            mBind.clBeaconModeClose.setVisibility(View.VISIBLE);
        }
    }

    public void onAdvContent(View view) {
        if (isWindowLocked())
            return;
        startActivity(new Intent(this, AdvInfoActivity.class));
    }

    public void onConnectable(View view) {
        if (mConnectableEnable) {
            AlertMessageDialog dialog = new AlertMessageDialog();
            dialog.setTitle("Warning!");
            dialog.setMessage("Are you sure to make the device unconnectable？");
            dialog.setOnAlertConfirmListener(() -> {
                mConnectableEnable = false;
                mBind.ivConnectable.setImageResource(mConnectableEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);

            });
            dialog.show(getSupportFragmentManager());
        } else {
            AlertMessageDialog dialog = new AlertMessageDialog();
            dialog.setTitle("Warning!");
            dialog.setMessage("Are you sure to make the device connectable？");
            dialog.setOnAlertConfirmListener(() -> {
                mConnectableEnable = true;
                mBind.ivConnectable.setImageResource(mConnectableEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
            });
            dialog.show(getSupportFragmentManager());
        }
    }
}
