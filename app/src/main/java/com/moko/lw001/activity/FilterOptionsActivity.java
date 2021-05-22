package com.moko.lw001.activity;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.lw001.AppConstants;
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
import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterOptionsActivity extends BaseActivity {

    @BindView(R2.id.tv_condition_a)
    TextView tvConditionA;
    @BindView(R2.id.tv_condition_b)
    TextView tvConditionB;
    @BindView(R2.id.tv_relation)
    TextView tvRelation;
    @BindView(R2.id.tv_repeat)
    TextView tvRepeat;
    private boolean mReceiverTag = false;
    private boolean savedParamsError;
    private ArrayList<String> mValues;
    private int mSelected;
    private ArrayList<String> mRepeatValues;
    private int mRepeatSelected;

    private boolean isFilterAEnable;
    private boolean isFilterBEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lw001_activity_filter_relation);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
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
            orderTasks.add(OrderTaskAssembler.getFilterSwitchA());
            orderTasks.add(OrderTaskAssembler.getFilterSwitchB());
            orderTasks.add(OrderTaskAssembler.getFilterABRelation());
            orderTasks.add(OrderTaskAssembler.getFilterRepeat());
            LoRaLW001MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
        }
        mValues = new ArrayList<>();
        mValues.add("Or");
        mValues.add("And");
        mRepeatValues = new ArrayList<>();
        mRepeatValues.add("No");
        mRepeatValues.add("MAC");
        mRepeatValues.add("MAC+Data Type");
        mRepeatValues.add("MAC+Raw Data");
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 200)
    public void onConneStatusEvent(ConnectStatusEvent event) {
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
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_ORDER_TIMEOUT.equals(action)) {
            }
            if (MokoConstants.ACTION_ORDER_FINISH.equals(action)) {
                dismissSyncProgressDialog();
            }
            if (MokoConstants.ACTION_ORDER_RESULT.equals(action)) {
                EventBus.getDefault().cancelEventDelivery(event);
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
                                    case KEY_TRACKING_FILTER_REPEAT:
                                    case KEY_TRACKING_FILTER_A_B_RELATION:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        if (savedParamsError) {
                                            ToastUtils.showToast(FilterOptionsActivity.this, "Opps！Save failed. Please check the input characters and try again.");
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
                                    case KEY_TRACKING_FILTER_SWITCH_A:
                                        if (length == 1) {
                                            final int enable = value[4] & 0xFF;
                                            tvConditionA.setText(enable == 0 ? "OFF" : "ON");
                                            isFilterAEnable = enable == 1;
                                        }
                                        break;
                                    case KEY_TRACKING_FILTER_SWITCH_B:
                                        if (length == 1) {
                                            final int enable = value[4] & 0xFF;
                                            tvConditionB.setText(enable == 0 ? "OFF" : "ON");
                                            isFilterBEnable = enable == 1;
                                            if (isFilterAEnable && isFilterBEnable) {
                                                tvRelation.setEnabled(true);
                                            } else {
                                                tvRelation.setEnabled(false);
                                            }
                                        }
                                        break;
                                    case KEY_TRACKING_FILTER_A_B_RELATION:
                                        if (length == 1) {
                                            final int relation = value[4] & 0xFF;
                                            tvRelation.setText(relation == 1 ? "And" : "Or");
                                            mSelected = relation;
                                        }
                                        break;
                                    case KEY_TRACKING_FILTER_REPEAT:
                                        if (length == 1) {
                                            final int repeat = value[4] & 0xFF;
                                            tvRepeat.setText(mRepeatValues.get(repeat));
                                            mRepeatSelected = repeat;
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
        finish();
    }

    public void onRelation(View view) {
        if (isWindowLocked())
            return;
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mValues, mSelected);
        dialog.setListener(value -> {
            tvRelation.setText(value == 1 ? "And" : "Or");
            mSelected = value;
            showSyncingProgressDialog();
            LoRaLW001MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setFilterABRelation(value));
        });
        dialog.show(getSupportFragmentManager());
    }

    public void onRepeat(View view) {
        if (isWindowLocked())
            return;
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mRepeatValues, mRepeatSelected);
        dialog.setListener(value -> {
            tvRepeat.setText(mRepeatValues.get(value));
            mRepeatSelected = value;
            showSyncingProgressDialog();
            LoRaLW001MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setFilterRepeat(value));
        });
        dialog.show(getSupportFragmentManager());
    }

    public void onFilterA(View view) {
        if (isWindowLocked())
            return;
        startActivityForResult(new Intent(this, FilterOptionsAActivity.class), AppConstants.REQUEST_CODE_FILTER);
    }

    public void onFilterB(View view) {
        if (isWindowLocked())
            return;
        startActivityForResult(new Intent(this, FilterOptionsBActivity.class), AppConstants.REQUEST_CODE_FILTER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.REQUEST_CODE_FILTER) {
            tvRelation.postDelayed(() -> {
                showSyncingProgressDialog();
                List<OrderTask> orderTasks = new ArrayList<>();
                orderTasks.add(OrderTaskAssembler.getFilterSwitchA());
                orderTasks.add(OrderTaskAssembler.getFilterSwitchB());
                orderTasks.add(OrderTaskAssembler.getFilterABRelation());
                LoRaLW001MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
            }, 500);
        }
    }
}
