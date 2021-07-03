package com.moko.lw001.activity;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.ble.lib.utils.MokoUtils;
import com.moko.lw001.R;
import com.moko.lw001.R2;
import com.moko.lw001.dialog.AlertMessageDialog;
import com.moko.lw001.dialog.LoadingMessageDialog;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class IndicatorSettingsActivity extends BaseActivity {

    @BindView(R2.id.cb_tamper)
    CheckBox cbTamper;
    @BindView(R2.id.cb_low_power)
    CheckBox cbLowPower;
    @BindView(R2.id.cb_wifi_fix)
    CheckBox cbWifiFix;
    @BindView(R2.id.cb_wifi_fix_success)
    CheckBox cbWifiFixSuccess;
    @BindView(R2.id.cb_wifi_fix_fail)
    CheckBox cbWifiFixFail;
    @BindView(R2.id.cb_ble_fix)
    CheckBox cbBleFix;
    @BindView(R2.id.cb_ble_fix_success)
    CheckBox cbBleFixSuccess;
    @BindView(R2.id.cb_ble_fix_fail)
    CheckBox cbBleFixFail;
    @BindView(R2.id.cb_gps_fix)
    CheckBox cbGpsFix;
    @BindView(R2.id.cb_gps_fix_success)
    CheckBox cbGpsFixSuccess;
    @BindView(R2.id.cb_gps_fix_fail)
    CheckBox cbGpsFixFail;
    private boolean mReceiverTag = false;
    private boolean savedParamsError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lw001_activity_indicator_settings);
        ButterKnife.bind(this);

        EventBus.getDefault().register(this);
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mReceiverTag = true;
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.getIndicatorLight());
        LoRaLW001MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 300)
    public void onConnectStatusEvent(ConnectStatusEvent event) {
        final String action = event.getAction();
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_DISCONNECTED.equals(action)) {
                finish();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 300)
    public void onOrderTaskResponseEvent(OrderTaskResponseEvent event) {
        EventBus.getDefault().cancelEventDelivery(event);
        final String action = event.getAction();
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
                                    case KEY_INDICATOR_LIGHT:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        if (savedParamsError) {
                                            ToastUtils.showToast(IndicatorSettingsActivity.this, "Opps！Save failed. Please check the input characters and try again.");
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
                                    case KEY_INDICATOR_LIGHT:
                                        if (length > 0) {
                                            byte[] indicatorBytes = Arrays.copyOfRange(value, 4, 4 + length);
                                            int indicator = MokoUtils.toInt(indicatorBytes);
                                            cbTamper.setChecked((indicator & 1) == 1);
                                            cbLowPower.setChecked((indicator & 2) == 2);
                                            cbBleFix.setChecked((indicator & 4) == 4);
                                            cbBleFixSuccess.setChecked((indicator & 8) == 8);
                                            cbBleFixFail.setChecked((indicator & 16) == 16);
                                            cbGpsFix.setChecked((indicator & 32) == 32);
                                            cbGpsFixSuccess.setChecked((indicator & 64) == 64);
                                            cbGpsFixFail.setChecked((indicator & 128) == 128);
                                            cbWifiFix.setChecked((indicator & 256) == 256);
                                            cbWifiFixSuccess.setChecked((indicator & 512) == 512);
                                            cbWifiFixFail.setChecked((indicator & 1024) == 1024);
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

    public void onSave(View view) {
        if (isWindowLocked())
            return;
        int indicator = (cbTamper.isChecked() ? 1 : 0)
                | (cbLowPower.isChecked() ? 2 : 0)
                | (cbBleFix.isChecked() ? 4 : 0)
                | (cbBleFixSuccess.isChecked() ? 8 : 0)
                | (cbBleFixFail.isChecked() ? 16 : 0)
                | (cbGpsFix.isChecked() ? 32 : 00)
                | (cbGpsFixSuccess.isChecked() ? 64 : 0)
                | (cbGpsFixFail.isChecked() ? 128 : 0)
                | (cbWifiFix.isChecked() ? 256 : 0)
                | (cbWifiFixSuccess.isChecked() ? 512 : 0)
                | (cbWifiFixFail.isChecked() ? 1024 : 0);
        showSyncingProgressDialog();
        LoRaLW001MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setIndicatorLight(indicator));
    }
}
