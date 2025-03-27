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
import com.moko.lib.loraui.utils.ToastUtils;
import com.moko.lw001.R;
import com.moko.lw001.databinding.Lw001ActivityAuxiliaryOperationBinding;
import com.moko.support.lw001.LoRaLW001MokoSupport;
import com.moko.support.lw001.OrderTaskAssembler;
import com.moko.support.lw001.entity.OrderCHAR;
import com.moko.support.lw001.entity.ParamsKeyEnum;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class AuxiliaryOperationActivity extends BaseActivity {

    private Lw001ActivityAuxiliaryOperationBinding mBind;
    private boolean mReceiverTag = false;
    private boolean savedParamsError;
    private boolean mTamperAlarmEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = Lw001ActivityAuxiliaryOperationBinding.inflate(getLayoutInflater());
        setContentView(mBind.getRoot());
        EventBus.getDefault().register(this);
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mReceiverTag = true;
        showSyncingProgressDialog();
        LoRaLW001MokoSupport.getInstance().sendOrder(OrderTaskAssembler.getTamperAlarm());
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
                                    case KEY_TAMPER_ALARM:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        if (savedParamsError) {
                                            ToastUtils.showToast(AuxiliaryOperationActivity.this, "Opps！Save failed. Please check the input characters and try again.");
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
                                    case KEY_TAMPER_ALARM:
                                        if (length > 0) {
                                            int enable = value[4] & 0xFF;
                                            mTamperAlarmEnable = enable == 1;
                                            mBind.ivTamperAlarm.setImageResource(mTamperAlarmEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
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

    public void onDownlinkForPos(View view) {
        if (isWindowLocked())
            return;
        startActivity(new Intent(this, DownlinkForPosActivity.class));
    }

    public void onVibrationDetection(View view) {
        if (isWindowLocked())
            return;
        startActivity(new Intent(this, VibrationDetectionActivity.class));
    }

    public void onManDownDetection(View view) {
        if (isWindowLocked())
            return;
        startActivity(new Intent(this, ManDownDetectionActivity.class));
    }

    public void onActiveStateCount(View view) {
        if (isWindowLocked())
            return;
        startActivity(new Intent(this, ActiveStateCountActivity.class));
    }

    public void onTamperAlarm(View view) {
        if (isWindowLocked())
            return;
        mTamperAlarmEnable = !mTamperAlarmEnable;
        savedParamsError = false;
        showSyncingProgressDialog();
        ArrayList<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setTamperAlarm(mTamperAlarmEnable ? 1 : 0));
        orderTasks.add(OrderTaskAssembler.getTamperAlarm());
        LoRaLW001MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }
}
