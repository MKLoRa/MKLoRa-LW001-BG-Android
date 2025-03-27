package com.moko.lw001.activity;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.lib.loraui.dialog.AlertMessageDialog;
import com.moko.lib.loraui.dialog.BottomDialog;
import com.moko.lib.loraui.utils.ToastUtils;
import com.moko.lw001.AppConstants;
import com.moko.lw001.R;
import com.moko.lw001.databinding.Lw001ActivityOnOffSettingsBinding;
import com.moko.lw001.utils.SPUtiles;
import com.moko.support.lw001.LoRaLW001MokoSupport;
import com.moko.support.lw001.OrderTaskAssembler;
import com.moko.support.lw001.entity.OrderCHAR;
import com.moko.support.lw001.entity.ParamsKeyEnum;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class OnOffActivity extends BaseActivity {

    private Lw001ActivityOnOffSettingsBinding mBind;
    private boolean mReceiverTag = false;
    private boolean savedParamsError;
    private ArrayList<String> mValues;
    private int mSelected;
    private ArrayList<String> mMethodValues;
    private int mMethodSelected;
    private boolean mMagnetEnable;
    private boolean mAutoPowerOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = Lw001ActivityOnOffSettingsBinding.inflate(getLayoutInflater());
        setContentView(mBind.getRoot());
        int mFirmwareCode = getIntent().getIntExtra(AppConstants.EXTRA_KEY_FIRMWARE_CODE, 0);
        int deviceType = SPUtiles.getIntValue(this, AppConstants.SP_KEY_DEVICE_TYPE, 0);
        mValues = new ArrayList<>();
        mValues.add("OFF");
        mValues.add("Revert to last mode");
        mMethodValues = new ArrayList<>();
        mMethodValues.add("Multiple approaches");
        mMethodValues.add("Continuous approach");
        EventBus.getDefault().register(this);
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mReceiverTag = true;
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>();
        if (mFirmwareCode >= 107) {
            mBind.clOnOffMethod.setVisibility(View.VISIBLE);
            orderTasks.add(OrderTaskAssembler.getOnOffMethod());
        }
        if (deviceType == 0x21) {
            mBind.clAutoPowerOn.setVisibility(View.VISIBLE);
            orderTasks.add(OrderTaskAssembler.getAutoPowerOn());
        }
        orderTasks.add(OrderTaskAssembler.getFirmwareVersion());
        orderTasks.add(OrderTaskAssembler.getReedSwitch());
        orderTasks.add(OrderTaskAssembler.getPowerStatus());
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
                                    case KEY_POWER_STATUS:
                                    case KEY_ON_OFF_METHOD:
                                    case KEY_REED_SWITCH:
                                    case KEY_AUTO_POWER_ON_ENABLE:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        if (savedParamsError) {
                                            ToastUtils.showToast(OnOffActivity.this, "Opps！Save failed. Please check the input characters and try again.");
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
                                    case KEY_ON_OFF_METHOD:
                                        if (length > 0) {
                                            int method = value[4] & 0xFF;
                                            mMethodSelected = method;
                                            mBind.tvOnOffMethod.setText(mMethodValues.get(mMethodSelected));
                                        }
                                        break;
                                    case KEY_REED_SWITCH:
                                        if (length > 0) {
                                            int enable = value[4] & 0xFF;
                                            mMagnetEnable = enable == 1;
                                            mBind.ivMagnet.setImageResource(mMagnetEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                                        }
                                        break;
                                    case KEY_AUTO_POWER_ON_ENABLE:
                                        if (length > 0) {
                                            int enable = value[4] & 0xFF;
                                            mAutoPowerOn = enable == 1;
                                            mBind.ivAutoPowerOn.setImageResource(mAutoPowerOn ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                                        }
                                        break;
                                    case KEY_POWER_STATUS:
                                        if (length > 0) {
                                            int status = value[4] & 0xFF;
                                            mSelected = status;
                                            mBind.tvDefaultMode.setText(mValues.get(status));
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

    public void selectDefaultMode(View view) {
        if (isWindowLocked())
            return;
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mValues, mSelected);
        dialog.setListener(value -> {
            mSelected = value;
            mBind.tvDefaultMode.setText(mValues.get(value));
            savedParamsError = false;
            showSyncingProgressDialog();
            LoRaLW001MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setPowerStatus(value));
        });
        dialog.show(getSupportFragmentManager());
    }

    public void onMagnet(View view) {
        if (isWindowLocked())
            return;
        mMagnetEnable = !mMagnetEnable;
        savedParamsError = false;
        showSyncingProgressDialog();
        ArrayList<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setReedSwitch(mMagnetEnable ? 1 : 0));
        orderTasks.add(OrderTaskAssembler.getReedSwitch());
        LoRaLW001MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    public void onAutoPowerOn(View view) {
        if (isWindowLocked())
            return;
        mAutoPowerOn = !mAutoPowerOn;
        savedParamsError = false;
        showSyncingProgressDialog();
        ArrayList<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setAutoPowerOn(mAutoPowerOn ? 1 : 0));
        orderTasks.add(OrderTaskAssembler.getAutoPowerOn());
        LoRaLW001MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    public void selectOnOffMethod(View view) {
        if (isWindowLocked())
            return;
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mMethodValues, mMethodSelected);
        dialog.setListener(value -> {
            mMethodSelected = value;
            mBind.tvOnOffMethod.setText(mMethodValues.get(value));
            savedParamsError = false;
            showSyncingProgressDialog();
            LoRaLW001MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setOnOffMethod(value));
        });
        dialog.show(getSupportFragmentManager());
    }
}
