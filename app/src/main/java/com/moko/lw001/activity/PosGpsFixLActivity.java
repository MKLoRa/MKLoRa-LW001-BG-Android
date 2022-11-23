package com.moko.lw001.activity;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.ble.lib.utils.MokoUtils;
import com.moko.lw001.BuildConfig;
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

public class PosGpsFixLActivity extends BaseActivity {

    @BindView(R2.id.et_coarse_timeout)
    EditText etCoarseTimeout;
    @BindView(R2.id.et_pdop_limit)
    EditText etPdopLimit;
    @BindView(R2.id.et_time_budget)
    EditText etTimeBudget;
    @BindView(R2.id.cb_extreme_mode)
    CheckBox cbExtremeMode;
    private boolean mReceiverTag = false;
    private boolean savedParamsError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lw001_activity_pos_gps_l);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mReceiverTag = true;
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>();
        if (!BuildConfig.IS_LIBRARY) {
            orderTasks.add(OrderTaskAssembler.getGPSColdStartTimeout());
        }
        orderTasks.add(OrderTaskAssembler.getGPSCoarseTimeout());
        orderTasks.add(OrderTaskAssembler.getGPSPDOPLimit());
        orderTasks.add(OrderTaskAssembler.getGPSTimeBudget());
        orderTasks.add(OrderTaskAssembler.getGPSExtremeMode());
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
                                    case KEY_GPS_COARSE_TIMEOUT:
                                    case KEY_GPS_PDOP_LIMIT:
                                    case KEY_GPS_TIME_BUDGET:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        break;
                                    case KEY_GPS_EXTREME_MODE:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        if (savedParamsError) {
                                            ToastUtils.showToast(PosGpsFixLActivity.this, "Opps！Save failed. Please check the input characters and try again.");
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
                                    case KEY_GPS_COARSE_TIMEOUT:
                                        if (length > 0) {
                                            byte[] timeoutBytes = Arrays.copyOfRange(value, 4, 6);
                                            int timeout = MokoUtils.toInt(timeoutBytes);
                                            etCoarseTimeout.setText(String.valueOf(timeout));
                                        }
                                        break;
                                    case KEY_GPS_PDOP_LIMIT:
                                        if (length > 0) {
                                            int limit = value[4] & 0xFF;
                                            etPdopLimit.setText(String.valueOf(limit));
                                        }
                                        break;
                                    case KEY_GPS_TIME_BUDGET:
                                        if (length > 0) {
                                            byte[] budgetBytes = Arrays.copyOfRange(value, 4, 8);
                                            int budget = MokoUtils.toInt(budgetBytes);
                                            etTimeBudget.setText(String.valueOf(budget));
                                        }
                                        break;
                                    case KEY_GPS_EXTREME_MODE:
                                        if (length > 0) {
                                            int enable = value[4] & 0xFF;
                                            cbExtremeMode.setChecked(enable == 1);
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
        final String coarseTimeoutStr = etCoarseTimeout.getText().toString();
        if (TextUtils.isEmpty(coarseTimeoutStr))
            return false;
        final int coarseTimeout = Integer.parseInt(coarseTimeoutStr);
        if (coarseTimeout < 1 || coarseTimeout > 7620) {
            return false;
        }
        final String pdopLimitStr = etPdopLimit.getText().toString();
        if (TextUtils.isEmpty(pdopLimitStr))
            return false;
        final int pdopLimit = Integer.parseInt(pdopLimitStr);
        if (pdopLimit < 25 || pdopLimit > 100) {
            return false;
        }
        final String timeBudgetStr = etTimeBudget.getText().toString();
        if (TextUtils.isEmpty(timeBudgetStr))
            return false;
        final int timeBudget = Integer.parseInt(timeBudgetStr);
        if (timeBudget < 0 || timeBudget > 76200) {
            return false;
        }
        return true;

    }


    private void saveParams() {
        final String coarseTimeoutStr = etCoarseTimeout.getText().toString();
        final int coarseTimeout = Integer.parseInt(coarseTimeoutStr);
        final String pdopLimitStr = etPdopLimit.getText().toString();
        final int pdopLimit = Integer.parseInt(pdopLimitStr);
        final String timeBudgetStr = etTimeBudget.getText().toString();
        final int timeBudget = Integer.parseInt(timeBudgetStr);
        savedParamsError = false;
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setGPSCoarseTimeout(coarseTimeout));
        orderTasks.add(OrderTaskAssembler.setGPSPDOPLimit(pdopLimit));
        orderTasks.add(OrderTaskAssembler.setGPSTimeBudget(timeBudget));
        orderTasks.add(OrderTaskAssembler.setGPSExtremeMode(cbExtremeMode.isChecked() ? 1 : 0));
        LoRaLW001MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
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
}
