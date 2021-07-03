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
import android.widget.TextView;

import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.ble.lib.utils.MokoUtils;
import com.moko.lw001.R;
import com.moko.lw001.R2;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class MotionModeActivity extends BaseActivity {

    @BindView(R2.id.cb_fix_on_start)
    CheckBox cbFixOnStart;
    @BindView(R2.id.et_fix_on_start_number)
    EditText etFixOnStartNumber;
    @BindView(R2.id.tv_pos_strategy_on_start)
    TextView tvPosStrategyOnStart;
    @BindView(R2.id.cb_fix_in_trip)
    CheckBox cbFixInTrip;
    @BindView(R2.id.et_report_interval_in_trip)
    EditText etReportIntervalInTrip;
    @BindView(R2.id.tv_pos_strategy_in_trip)
    TextView tvPosStrategyInTrip;
    @BindView(R2.id.cb_fix_on_end)
    CheckBox cbFixOnEnd;
    @BindView(R2.id.et_trip_end_timeout)
    EditText etTripEndTimeout;
    @BindView(R2.id.et_fix_on_end_number)
    EditText etFixOnEndNumber;
    @BindView(R2.id.et_report_interval_on_end)
    EditText etReportIntervalOnEnd;
    @BindView(R2.id.tv_pos_strategy_on_end)
    TextView tvPosStrategyOnEnd;
    @BindView(R2.id.cb_notify_on_start)
    CheckBox cbNotifyOnStart;
    @BindView(R2.id.cb_notify_in_trip)
    CheckBox cbNotifyInTrip;
    @BindView(R2.id.cb_notify_on_end)
    CheckBox cbNotifyOnEnd;
    private boolean mReceiverTag = false;
    private boolean savedParamsError;
    private ArrayList<String> mValues;
    private int mStartSelected;
    private int mStartShowSelected;
    private int mTripSelected;
    private int mTripShowSelected;
    private int mEndSelected;
    private int mEndShowSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lw001_activity_motion_mode);
        ButterKnife.bind(this);
        mValues = new ArrayList<>();
        mValues.add("WIFI");
        mValues.add("BLE");
        mValues.add("GPS");
        mValues.add("WIFI+GPS");
        mValues.add("BLE+GPS");
        mValues.add("WIFI+BLE");
        mValues.add("WIFI+BLE+GPS");
        EventBus.getDefault().register(this);
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mReceiverTag = true;
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.getMotionModeEvent());
        orderTasks.add(OrderTaskAssembler.getMotionModeStartNumber());
        orderTasks.add(OrderTaskAssembler.getMotionStartPosStrategy());
        orderTasks.add(OrderTaskAssembler.getMotionTripInterval());
        orderTasks.add(OrderTaskAssembler.getMotionTripPosStrategy());
        orderTasks.add(OrderTaskAssembler.getMotionEndTimeout());
        orderTasks.add(OrderTaskAssembler.getMotionEndNumber());
        orderTasks.add(OrderTaskAssembler.getMotionEndInterval());
        orderTasks.add(OrderTaskAssembler.getMotionEndPosStrategy());
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
                                    case KEY_MOTION_MODE_START_NUMBER:
                                    case KEY_MOTION_MODE_START_POS_STRATEGY:
                                    case KEY_MOTION_MODE_TRIP_REPORT_INTERVAL:
                                    case KEY_MOTION_MODE_TRIP_POS_STRATEGY:
                                    case KEY_MOTION_MODE_END_TIMEOUT:
                                    case KEY_MOTION_MODE_END_NUMBER:
                                    case KEY_MOTION_MODE_END_REPORT_INTERVAL:
                                    case KEY_MOTION_MODE_END_POS_STRATEGY:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        break;
                                    case KEY_MOTION_MODE_EVENT:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        if (savedParamsError) {
                                            ToastUtils.showToast(MotionModeActivity.this, "Opps！Save failed. Please check the input characters and try again.");
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
                                    case KEY_MOTION_MODE_EVENT:
                                        if (length > 0) {
                                            int modeEvent = value[4] & 0xFF;
                                            cbNotifyOnStart.setChecked((modeEvent & 1) == 1);
                                            cbFixOnStart.setChecked((modeEvent & 2) == 2);
                                            cbNotifyInTrip.setChecked((modeEvent & 4) == 4);
                                            cbFixInTrip.setChecked((modeEvent & 8) == 8);
                                            cbNotifyOnEnd.setChecked((modeEvent & 16) == 16);
                                            cbFixOnEnd.setChecked((modeEvent & 32) == 32);
                                        }
                                        break;
                                    case KEY_MOTION_MODE_START_NUMBER:
                                        if (length > 0) {
                                            int number = value[4] & 0xFF;
                                            etFixOnStartNumber.setText(String.valueOf(number));
                                        }
                                        break;
                                    case KEY_MOTION_MODE_START_POS_STRATEGY:
                                        if (length > 0) {
                                            int strategy = value[4] & 0xFF;
                                            mStartSelected = strategy;
                                            if (strategy == 1) {
                                                mStartShowSelected = 0;
                                            } else if (strategy == 2) {
                                                mStartShowSelected = 1;
                                            } else if (strategy == 3) {
                                                mStartShowSelected = 5;
                                            } else if (strategy == 4) {
                                                mStartShowSelected = 2;
                                            } else if (strategy == 5) {
                                                mStartShowSelected = 3;
                                            } else if (strategy == 6) {
                                                mStartShowSelected = 4;
                                            } else if (strategy == 7) {
                                                mStartShowSelected = 7;
                                            }
                                            tvPosStrategyOnStart.setText(mValues.get(mStartShowSelected));
                                        }
                                        break;
                                    case KEY_MOTION_MODE_TRIP_REPORT_INTERVAL:
                                        if (length > 0) {
                                            byte[] intervalBytes = Arrays.copyOfRange(value, 4, 4 + length);
                                            int interval = MokoUtils.toInt(intervalBytes);
                                            etReportIntervalInTrip.setText(String.valueOf(interval));
                                        }
                                        break;
                                    case KEY_MOTION_MODE_TRIP_POS_STRATEGY:
                                        if (length > 0) {
                                            int strategy = value[4] & 0xFF;
                                            mTripSelected = strategy;
                                            if (strategy == 1) {
                                                mTripShowSelected = 0;
                                            } else if (strategy == 2) {
                                                mTripShowSelected = 1;
                                            } else if (strategy == 3) {
                                                mTripShowSelected = 5;
                                            } else if (strategy == 4) {
                                                mTripShowSelected = 2;
                                            } else if (strategy == 5) {
                                                mTripShowSelected = 3;
                                            } else if (strategy == 6) {
                                                mTripShowSelected = 4;
                                            } else if (strategy == 7) {
                                                mTripShowSelected = 7;
                                            }
                                            tvPosStrategyInTrip.setText(mValues.get(mTripShowSelected));
                                        }
                                        break;
                                    case KEY_MOTION_MODE_END_TIMEOUT:
                                        if (length > 0) {
                                            int timeout = value[4] & 0xFF;
                                            etTripEndTimeout.setText(String.valueOf(timeout));
                                        }
                                        break;
                                    case KEY_MOTION_MODE_END_NUMBER:
                                        if (length > 0) {
                                            int number = value[4] & 0xFF;
                                            etFixOnEndNumber.setText(String.valueOf(number));
                                        }
                                        break;
                                    case KEY_MOTION_MODE_END_REPORT_INTERVAL:
                                        if (length > 0) {
                                            byte[] intervalBytes = Arrays.copyOfRange(value, 4, 4 + length);
                                            int interval = MokoUtils.toInt(intervalBytes);
                                            etReportIntervalOnEnd.setText(String.valueOf(interval));
                                        }
                                        break;
                                    case KEY_MOTION_MODE_END_POS_STRATEGY:
                                        if (length > 0) {
                                            int strategy = value[4] & 0xFF;
                                            mEndSelected = strategy;
                                            if (strategy == 1) {
                                                mEndShowSelected = 0;
                                            } else if (strategy == 2) {
                                                mEndShowSelected = 1;
                                            } else if (strategy == 3) {
                                                mEndShowSelected = 5;
                                            } else if (strategy == 4) {
                                                mEndShowSelected = 2;
                                            } else if (strategy == 5) {
                                                mEndShowSelected = 3;
                                            } else if (strategy == 6) {
                                                mEndShowSelected = 4;
                                            } else if (strategy == 7) {
                                                mEndShowSelected = 7;
                                            }
                                            tvPosStrategyOnEnd.setText(mValues.get(mEndShowSelected));
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
        if (isValid()) {
            showSyncingProgressDialog();
            saveParams();
        } else {
            ToastUtils.showToast(this, "Opps！Save failed. Please check the input characters and try again.");
        }
    }

    private boolean isValid() {
        final String startNumberStr = etFixOnStartNumber.getText().toString();
        if (TextUtils.isEmpty(startNumberStr))
            return false;
        final int startNumber = Integer.parseInt(startNumberStr);
        if (startNumber < 1 || startNumber > 255)
            return false;
        final String intervalTripStr = etReportIntervalInTrip.getText().toString();
        if (TextUtils.isEmpty(intervalTripStr))
            return false;
        final int intervalTrip = Integer.parseInt(intervalTripStr);
        if (intervalTrip < 10 || intervalTrip > 86400)
            return false;
        final String endTimeoutStr = etTripEndTimeout.getText().toString();
        if (TextUtils.isEmpty(endTimeoutStr))
            return false;
        final int endTimeout = Integer.parseInt(endTimeoutStr);
        if (endTimeout < 3 || endTimeout > 180)
            return false;
        final String endNumberStr = etFixOnEndNumber.getText().toString();
        if (TextUtils.isEmpty(endNumberStr))
            return false;
        final int endNumber = Integer.parseInt(endNumberStr);
        if (endNumber < 1 || endNumber > 255)
            return false;
        final String endIntervalStr = etReportIntervalOnEnd.getText().toString();
        if (TextUtils.isEmpty(endIntervalStr))
            return false;
        final int endInterval = Integer.parseInt(endIntervalStr);
        if (endInterval < 10 || endInterval > 300)
            return false;
        return true;

    }

    private void saveParams() {
        final String startNumberStr = etFixOnStartNumber.getText().toString();
        final int startNumber = Integer.parseInt(startNumberStr);
        final String intervalTripStr = etReportIntervalInTrip.getText().toString();
        final int intervalTrip = Integer.parseInt(intervalTripStr);
        final String endTimeoutStr = etTripEndTimeout.getText().toString();
        final int endTimeout = Integer.parseInt(endTimeoutStr);
        final String endNumberStr = etFixOnEndNumber.getText().toString();
        final int endNumber = Integer.parseInt(endNumberStr);
        final String endIntervalStr = etReportIntervalOnEnd.getText().toString();
        final int endInterval = Integer.parseInt(endIntervalStr);

        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setMotionModeStartNumber(startNumber));
        orderTasks.add(OrderTaskAssembler.setMotionStartPosStrategy(mStartSelected));
        orderTasks.add(OrderTaskAssembler.setMotionTripInterval(intervalTrip));
        orderTasks.add(OrderTaskAssembler.setMotionTripPosStrategy(mTripSelected));
        orderTasks.add(OrderTaskAssembler.setMotionEndTimeout(endTimeout));
        orderTasks.add(OrderTaskAssembler.setMotionEndNumber(endNumber));
        orderTasks.add(OrderTaskAssembler.setMotionEndInterval(endInterval));
        orderTasks.add(OrderTaskAssembler.setMotionEndPosStrategy(mEndSelected));
        int motionMode = (cbNotifyOnStart.isChecked() ? 1 : 0)
                | (cbFixOnStart.isChecked() ? 2 : 0)
                | (cbNotifyInTrip.isChecked() ? 4 : 0)
                | (cbFixInTrip.isChecked() ? 8 : 0)
                | (cbNotifyOnEnd.isChecked() ? 16 : 0)
                | (cbFixOnEnd.isChecked() ? 32 : 0);
        orderTasks.add(OrderTaskAssembler.setMotionModeEvent(motionMode));
        LoRaLW001MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    public void selectPosStrategyStart(View view) {
        if (isWindowLocked())
            return;
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mValues, mStartShowSelected);
        dialog.setListener(value -> {
            mStartShowSelected = value;
            if (value == 0) {
                mStartSelected = 1;
            } else if (value == 1) {
                mStartSelected = 2;
            } else if (value == 2) {
                mStartSelected = 4;
            } else if (value == 3) {
                mStartSelected = 5;
            } else if (value == 4) {
                mStartSelected = 6;
            } else if (value == 5) {
                mStartSelected = 3;
            } else if (value == 6) {
                mStartSelected = 7;
            }
            tvPosStrategyOnStart.setText(mValues.get(value));
        });
        dialog.show(getSupportFragmentManager());
    }

    public void selectPosStrategyTrip(View view) {
        if (isWindowLocked())
            return;
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mValues, mTripShowSelected);
        dialog.setListener(value -> {
            mTripShowSelected = value;
            if (value == 0) {
                mTripSelected = 1;
            } else if (value == 1) {
                mTripSelected = 2;
            } else if (value == 2) {
                mTripSelected = 4;
            } else if (value == 3) {
                mTripSelected = 5;
            } else if (value == 4) {
                mTripSelected = 6;
            } else if (value == 5) {
                mTripSelected = 3;
            } else if (value == 6) {
                mTripSelected = 7;
            }
            tvPosStrategyInTrip.setText(mValues.get(value));
        });
        dialog.show(getSupportFragmentManager());
    }

    public void selectPosStrategyEnd(View view) {
        if (isWindowLocked())
            return;
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mValues, mEndShowSelected);
        dialog.setListener(value -> {
            mEndShowSelected = value;
            if (value == 0) {
                mEndSelected = 1;
            } else if (value == 1) {
                mEndSelected = 2;
            } else if (value == 2) {
                mEndSelected = 4;
            } else if (value == 3) {
                mEndSelected = 5;
            } else if (value == 4) {
                mEndSelected = 6;
            } else if (value == 5) {
                mEndSelected = 3;
            } else if (value == 6) {
                mEndSelected = 7;
            }
            tvPosStrategyOnEnd.setText(mValues.get(value));
        });
        dialog.show(getSupportFragmentManager());
    }
}
