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
import com.moko.lib.loraui.dialog.AlertMessageDialog;
import com.moko.lib.loraui.dialog.BottomDialog;
import com.moko.lib.loraui.utils.ToastUtils;
import com.moko.lw001.databinding.Lw001ActivityPeriodicModeBinding;
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

public class PeriodicModeActivity extends BaseActivity {

    private Lw001ActivityPeriodicModeBinding mBind;
    private boolean mReceiverTag = false;
    private boolean savedParamsError;
    private ArrayList<String> mValues;
    private int mSelected;
    private int mShowSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = Lw001ActivityPeriodicModeBinding.inflate(getLayoutInflater());
        setContentView(mBind.getRoot());
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
        orderTasks.add(OrderTaskAssembler.getPeriodicPosStrategy());
        orderTasks.add(OrderTaskAssembler.getPeriodicReportInterval());
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
                                    case KEY_PERIODIC_MODE_POS_STRATEGY:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        break;
                                    case KEY_PERIODIC_MODE_REPORT_INTERVAL:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        if (savedParamsError) {
                                            ToastUtils.showToast(PeriodicModeActivity.this, "Opps！Save failed. Please check the input characters and try again.");
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
                                    case KEY_PERIODIC_MODE_POS_STRATEGY:
                                        if (length > 0) {
                                            int strategy = value[4] & 0xFF;
                                            mSelected = strategy;
                                            if (strategy == 1) {
                                                mShowSelected = 0;
                                            } else if (strategy == 2) {
                                                mShowSelected = 1;
                                            } else if (strategy == 3) {
                                                mShowSelected = 5;
                                            } else if (strategy == 4) {
                                                mShowSelected = 2;
                                            } else if (strategy == 5) {
                                                mShowSelected = 3;
                                            } else if (strategy == 6) {
                                                mShowSelected = 4;
                                            } else if (strategy == 7) {
                                                mShowSelected = 6;
                                            }
                                            mBind.tvPeriodicPosStrategy.setText(mValues.get(mShowSelected));
                                        }
                                        break;
                                    case KEY_PERIODIC_MODE_REPORT_INTERVAL:
                                        if (length > 0) {
                                            byte[] intervalBytes = Arrays.copyOfRange(value, 4, 4 + length);
                                            int interval = MokoUtils.toInt(intervalBytes);
                                            mBind.etReportInterval.setText(String.valueOf(interval));
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

    public void selectPosStrategy(View view) {
        if (isWindowLocked())
            return;
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mValues, mShowSelected);
        dialog.setListener(value -> {
            mShowSelected = value;
            if (value == 0) {
                mSelected = 1;
            } else if (value == 1) {
                mSelected = 2;
            } else if (value == 2) {
                mSelected = 4;
            } else if (value == 3) {
                mSelected = 5;
            } else if (value == 4) {
                mSelected = 6;
            } else if (value == 5) {
                mSelected = 3;
            } else if (value == 6) {
                mSelected = 7;
            }
            mBind.tvPeriodicPosStrategy.setText(mValues.get(value));
        });
        dialog.show(getSupportFragmentManager());
    }

    public void onSave(View view) {
        final String intervalStr = mBind.etReportInterval.getText().toString();
        if (TextUtils.isEmpty(intervalStr)) {
            ToastUtils.showToast(this, "Opps！Save failed. Please check the input characters and try again.");
            return;
        }
        final int interval = Integer.parseInt(intervalStr);
        if (interval < 30 || interval > 86400) {
            ToastUtils.showToast(this, "Opps！Save failed. Please check the input characters and try again.");
            return;
        }
        savedParamsError = false;
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setPeriodicPosStrategy(mSelected));
        orderTasks.add(OrderTaskAssembler.setPeriodicReportInterval(interval));
        LoRaLW001MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }
}
