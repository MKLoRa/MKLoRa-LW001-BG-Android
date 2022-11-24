package com.moko.lw001.activity;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.ble.lib.utils.MokoUtils;
import com.moko.lw001.BuildConfig;
import com.moko.lw001.databinding.Lw001ActivityPosGpsBinding;
import com.moko.lw001.dialog.AlertMessageDialog;
import com.moko.lw001.dialog.BottomDialog;
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

public class PosGpsFixActivity extends BaseActivity {


    private Lw001ActivityPosGpsBinding mBind;
    private boolean mReceiverTag = false;
    private boolean savedParamsError;

    private ArrayList<String> mFixModeValues;
    private int mFixModeSelected;
    private ArrayList<String> mGPSModelValues;
    private int mGPSModelSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = Lw001ActivityPosGpsBinding.inflate(getLayoutInflater());
        setContentView(mBind.getRoot());
        mFixModeValues = new ArrayList<>();
        mFixModeValues.add("2D");
        mFixModeValues.add("3D");
        mFixModeValues.add("Auto");
        mGPSModelValues = new ArrayList<>();
        mGPSModelValues.add("Portable");
        mGPSModelValues.add("Stationary");
        mGPSModelValues.add("Pedestrian");
        mGPSModelValues.add("Automotive");
        mGPSModelValues.add("At sea");
        mGPSModelValues.add("Airborne<1g");
        mGPSModelValues.add("Airborne<2g");
        mGPSModelValues.add("Airborne<4g");
        mGPSModelValues.add("Wrist");
        mGPSModelValues.add("Bike");
        mBind.clColdStartTimeout.setVisibility(BuildConfig.IS_LIBRARY ? View.GONE : View.VISIBLE);
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
        orderTasks.add(OrderTaskAssembler.getGPSCoarseAccuracyMask());
        orderTasks.add(OrderTaskAssembler.getGPSCoarseTimeout());
        orderTasks.add(OrderTaskAssembler.getGPSFineAccuracyMask());
        orderTasks.add(OrderTaskAssembler.getGPSFineTimeout());
        orderTasks.add(OrderTaskAssembler.getGPSPDOPLimit());
        orderTasks.add(OrderTaskAssembler.getGPSAutonomousAiding());
        orderTasks.add(OrderTaskAssembler.getGPSAidingAccuracy());
        orderTasks.add(OrderTaskAssembler.getGPSAidingTimeout());
        orderTasks.add(OrderTaskAssembler.getGPSFixMode());
        orderTasks.add(OrderTaskAssembler.getGPSModel());
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
                                    case KEY_GPS_COLD_START_TIMEOUT:
                                    case KEY_GPS_COARSE_ACCURACY_MASK:
                                    case KEY_GPS_COARSE_TIMEOUT:
                                    case KEY_GPS_FINE_ACCURACY_MASK:
                                    case KEY_GPS_FINE_TIMEOUT:
                                    case KEY_GPS_PDOP_LIMIT:
                                    case KEY_GPS_AUTONOMOUS_AIDING:
                                    case KEY_GPS_AIDING_ACCURACY:
                                    case KEY_GPS_AIDING_TIMEOUT:
                                    case KEY_GPS_FIX_MODE:
                                    case KEY_GPS_MODEL:
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
                                            ToastUtils.showToast(PosGpsFixActivity.this, "Opps！Save failed. Please check the input characters and try again.");
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
                                    case KEY_GPS_COLD_START_TIMEOUT:
                                        if (length > 0) {
                                            int timeout = value[4] & 0xFF;
                                            mBind.etColdStartTimeout.setText(String.valueOf(timeout));
                                        }
                                        break;
                                    case KEY_GPS_COARSE_ACCURACY_MASK:
                                        if (length > 0) {
                                            int mask = value[4] & 0xFF;
                                            mBind.etCoarseAccMask.setText(String.valueOf(mask));
                                        }
                                        break;
                                    case KEY_GPS_COARSE_TIMEOUT:
                                        if (length > 0) {
                                            byte[] timeoutBytes = Arrays.copyOfRange(value, 4, 6);
                                            int timeout = MokoUtils.toInt(timeoutBytes);
                                            mBind.etCoarseTimeout.setText(String.valueOf(timeout));
                                        }
                                        break;
                                    case KEY_GPS_FINE_ACCURACY_MASK:
                                        if (length > 0) {
                                            int mask = value[4] & 0xFF;
                                            mBind.etFineAccTarget.setText(String.valueOf(mask));
                                        }
                                        break;
                                    case KEY_GPS_FINE_TIMEOUT:
                                        if (length > 0) {
                                            byte[] timeoutBytes = Arrays.copyOfRange(value, 4, 8);
                                            int timeout = MokoUtils.toInt(timeoutBytes);
                                            mBind.etFineTimeout.setText(String.valueOf(timeout));
                                        }
                                        break;
                                    case KEY_GPS_PDOP_LIMIT:
                                        if (length > 0) {
                                            int limit = value[4] & 0xFF;
                                            mBind.etPdopLimit.setText(String.valueOf(limit));
                                        }
                                        break;
                                    case KEY_GPS_AUTONOMOUS_AIDING:
                                        if (length > 0) {
                                            int enable = value[4] & 0xFF;
                                            mBind.cbAutonomousAiding.setChecked(enable == 1);
                                        }
                                        break;
                                    case KEY_GPS_AIDING_ACCURACY:
                                        if (length > 0) {
                                            byte[] accBytes = Arrays.copyOfRange(value, 4, 6);
                                            int acc = MokoUtils.toInt(accBytes);
                                            mBind.etAidingAccuracy.setText(String.valueOf(acc));
                                        }
                                        break;
                                    case KEY_GPS_AIDING_TIMEOUT:
                                        if (length > 0) {
                                            byte[] timeoutBytes = Arrays.copyOfRange(value, 4, 6);
                                            int timeout = MokoUtils.toInt(timeoutBytes);
                                            mBind.etAidingTimeout.setText(String.valueOf(timeout));
                                        }
                                        break;
                                    case KEY_GPS_FIX_MODE:
                                        if (length > 0) {
                                            int mode = value[4] & 0xFF;
                                            mFixModeSelected = mode;
                                            mBind.tvFixMode.setText(mFixModeValues.get(mode));
                                        }
                                        break;
                                    case KEY_GPS_MODEL:
                                        if (length > 0) {
                                            int model = value[4] & 0xFF;
                                            mGPSModelSelected = model;
                                            mBind.tvGpsModel.setText(mGPSModelValues.get(model));
                                        }
                                        break;
                                    case KEY_GPS_TIME_BUDGET:
                                        if (length > 0) {
                                            byte[] budgetBytes = Arrays.copyOfRange(value, 4, 8);
                                            int budget = MokoUtils.toInt(budgetBytes);
                                            mBind.etTimeBudget.setText(String.valueOf(budget));
                                        }
                                        break;
                                    case KEY_GPS_EXTREME_MODE:
                                        if (length > 0) {
                                            int enable = value[4] & 0xFF;
                                            mBind.cbExtremeMode.setChecked(enable == 1);
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
        if (!BuildConfig.IS_LIBRARY) {
            final String coldStartTimeoutStr = mBind.etColdStartTimeout.getText().toString();
            if (TextUtils.isEmpty(coldStartTimeoutStr))
                return false;
            final int coldStartTimeout = Integer.parseInt(coldStartTimeoutStr);
            if (coldStartTimeout < 3 || coldStartTimeout > 15) {
                return false;
            }
        }
        final String coarseAccMaskStr = mBind.etCoarseAccMask.getText().toString();
        if (TextUtils.isEmpty(coarseAccMaskStr))
            return false;
        final int coarseAccMask = Integer.parseInt(coarseAccMaskStr);
        if (coarseAccMask < 5 || coarseAccMask > 100) {
            return false;
        }
        final String coarseTimeoutStr = mBind.etCoarseTimeout.getText().toString();
        if (TextUtils.isEmpty(coarseTimeoutStr))
            return false;
        final int coarseTimeout = Integer.parseInt(coarseTimeoutStr);
        if (coarseTimeout < 1 || coarseTimeout > 7620) {
            return false;
        }
        final String fineAccTargetStr = mBind.etFineAccTarget.getText().toString();
        if (TextUtils.isEmpty(fineAccTargetStr))
            return false;
        final int fineAccTarget = Integer.parseInt(fineAccTargetStr);
        if (fineAccTarget < 5 || fineAccTarget > 100) {
            return false;
        }
        final String fineTimeoutStr = mBind.etFineTimeout.getText().toString();
        if (TextUtils.isEmpty(fineTimeoutStr))
            return false;
        final int fineTimeout = Integer.parseInt(fineTimeoutStr);
        if (fineTimeout < 0 || fineTimeout > 76200) {
            return false;
        }
        final String pdopLimitStr = mBind.etPdopLimit.getText().toString();
        if (TextUtils.isEmpty(pdopLimitStr))
            return false;
        final int pdopLimit = Integer.parseInt(pdopLimitStr);
        if (pdopLimit < 25 || pdopLimit > 100) {
            return false;
        }
        final String aidingAccStr = mBind.etAidingAccuracy.getText().toString();
        if (TextUtils.isEmpty(aidingAccStr))
            return false;
        final int aidingAcc = Integer.parseInt(aidingAccStr);
        if (aidingAcc < 5 || aidingAcc > 1000) {
            return false;
        }
        final String aidingTimeoutStr = mBind.etAidingTimeout.getText().toString();
        if (TextUtils.isEmpty(aidingTimeoutStr))
            return false;
        final int aidingTimeout = Integer.parseInt(aidingTimeoutStr);
        if (aidingTimeout < 1 || aidingTimeout > 7620) {
            return false;
        }
        final String timeBudgetStr = mBind.etTimeBudget.getText().toString();
        if (TextUtils.isEmpty(timeBudgetStr))
            return false;
        final int timeBudget = Integer.parseInt(timeBudgetStr);
        if (timeBudget < 0 || timeBudget > 76200) {
            return false;
        }
        return true;

    }


    private void saveParams() {
        final String coarseAccMaskStr = mBind.etCoarseAccMask.getText().toString();
        final int coarseAccMask = Integer.parseInt(coarseAccMaskStr);
        final String coarseTimeoutStr = mBind.etCoarseTimeout.getText().toString();
        final int coarseTimeout = Integer.parseInt(coarseTimeoutStr);
        final String fineAccTargetStr = mBind.etFineAccTarget.getText().toString();
        final int fineAccTarget = Integer.parseInt(fineAccTargetStr);
        final String fineTimeoutStr = mBind.etFineTimeout.getText().toString();
        final int fineTimeout = Integer.parseInt(fineTimeoutStr);
        final String pdopLimitStr = mBind.etPdopLimit.getText().toString();
        final int pdopLimit = Integer.parseInt(pdopLimitStr);
        final String aidingAccStr = mBind.etAidingAccuracy.getText().toString();
        final int aidingAcc = Integer.parseInt(aidingAccStr);
        final String aidingTimeoutStr = mBind.etAidingTimeout.getText().toString();
        final int aidingTimeout = Integer.parseInt(aidingTimeoutStr);
        final String timeBudgetStr = mBind.etTimeBudget.getText().toString();
        final int timeBudget = Integer.parseInt(timeBudgetStr);
        savedParamsError = false;
        List<OrderTask> orderTasks = new ArrayList<>();
        if (!BuildConfig.IS_LIBRARY) {
            final String coldStartTimeoutStr = mBind.etColdStartTimeout.getText().toString();
            final int coldStartTimeout = Integer.parseInt(coldStartTimeoutStr);
            orderTasks.add(OrderTaskAssembler.setGPSColdStartTimeout(coldStartTimeout));
        }
        orderTasks.add(OrderTaskAssembler.setGPSCoarseAccuracyMask(coarseAccMask));
        orderTasks.add(OrderTaskAssembler.setGPSCoarseTimeout(coarseTimeout));
        orderTasks.add(OrderTaskAssembler.setGPSFineAccuracyMask(fineAccTarget));
        orderTasks.add(OrderTaskAssembler.setGPSFineTimeout(fineTimeout));
        orderTasks.add(OrderTaskAssembler.setGPSPDOPLimit(pdopLimit));
        orderTasks.add(OrderTaskAssembler.setGPSAutonomousAiding(mBind.cbAutonomousAiding.isChecked() ? 1 : 0));
        orderTasks.add(OrderTaskAssembler.setGPSAidingAccuracy(aidingAcc));
        orderTasks.add(OrderTaskAssembler.setGPSAidingTimeout(aidingTimeout));
        orderTasks.add(OrderTaskAssembler.setGPSFixMode(mFixModeSelected));
        orderTasks.add(OrderTaskAssembler.setGPSModel(mGPSModelSelected));
        orderTasks.add(OrderTaskAssembler.setGPSTimeBudget(timeBudget));
        orderTasks.add(OrderTaskAssembler.setGPSExtremeMode(mBind.cbExtremeMode.isChecked() ? 1 : 0));
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

    public void selectFixMode(View view) {
        if (isWindowLocked())
            return;
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mFixModeValues, mFixModeSelected);
        dialog.setListener(value -> {
            mBind.tvFixMode.setText(mFixModeValues.get(value));
            mFixModeSelected = value;
        });
        dialog.show(getSupportFragmentManager());
    }

    public void selectGpsModel(View view) {
        if (isWindowLocked())
            return;
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mGPSModelValues, mGPSModelSelected);
        dialog.setListener(value -> {
            mBind.tvGpsModel.setText(mGPSModelValues.get(value));
            mGPSModelSelected = value;
        });
        dialog.show(getSupportFragmentManager());
    }
}
