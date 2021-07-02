package com.moko.lw001.activity;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.lw001.R;
import com.moko.lw001.R2;
import com.moko.lw001.adapter.TimePointAdapter;
import com.moko.lw001.dialog.AlertMessageDialog;
import com.moko.lw001.dialog.BottomDialog;
import com.moko.lw001.dialog.LoadingMessageDialog;
import com.moko.lw001.entity.TimePoint;
import com.moko.lw001.utils.ToastUtils;
import com.moko.support.lw001.LoRaLW001MokoSupport;
import com.moko.support.lw001.OrderTaskAssembler;
import com.moko.support.lw001.entity.OrderCHAR;
import com.moko.support.lw001.entity.ParamsKeyEnum;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TimingModeActivity extends BaseActivity implements BaseQuickAdapter.OnItemChildClickListener, OnItemMoveListener {

    @BindView(R2.id.tv_timing_pos_strategy)
    TextView tvTimingPosStrategy;
    @BindView(R2.id.srv_time_point)
    SwipeRecyclerView srvTimePoint;
    private boolean mReceiverTag = false;
    private boolean savedParamsError;
    private ArrayList<String> mValues;
    private int mSelected;
    private int mShowSelected;
    private ArrayList<TimePoint> mTimePoints;
    private TimePointAdapter mAdapter;
    private ArrayList<String> mHourValues;
    private ArrayList<String> mMinValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lw001_activity_timing_mode);
        ButterKnife.bind(this);
        mValues = new ArrayList<>();
        mValues.add("WIFI");
        mValues.add("BLE");
        mValues.add("GPS");
        mValues.add("WIFI+GPS");
        mValues.add("BLE+GPS");
        mValues.add("WIFI+BLE");
        mValues.add("WIFI+BLE+GPS");
        mHourValues = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            mHourValues.add(String.format("%02d", i));
        }
        mMinValues = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            mMinValues.add(String.format("%02d", i * 15));
        }
        mTimePoints = new ArrayList<>();
        mAdapter = new TimePointAdapter();
        mAdapter.openLoadAnimation();
        mAdapter.setOnItemChildClickListener(this);
        mAdapter.replaceData(mTimePoints);
        srvTimePoint.setLayoutManager(new LinearLayoutManager(this));
        srvTimePoint.setAdapter(mAdapter);
        srvTimePoint.setItemViewSwipeEnabled(true);
        srvTimePoint.setOnItemMoveListener(this);
        EventBus.getDefault().register(this);
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mReceiverTag = true;
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.getTimePosStrategy());
        orderTasks.add(OrderTaskAssembler.getTimePosReportPoints());
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
                                    case KEY_TIME_MODE_POS_STRATEGY:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        break;
                                    case KEY_TIME_MODE_REPORT_TIME_POINT:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        if (savedParamsError) {
                                            ToastUtils.showToast(TimingModeActivity.this, "Opps！Save failed. Please check the input characters and try again.");
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
                                    case KEY_TIME_MODE_POS_STRATEGY:
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
                                                mShowSelected = 7;
                                            }
                                            tvTimingPosStrategy.setText(mValues.get(mShowSelected));
                                        }
                                        break;
                                    case KEY_TIME_MODE_REPORT_TIME_POINT:
                                        if (length > 0) {
                                            for (int i = 0; i < length; i++) {
                                                int point = value[4 + i] & 0xFF;
                                                int min = point * 15;
                                                int hour = min / 60;
                                                min = min % 60;
                                                TimePoint timePoint = new TimePoint();
                                                timePoint.name = String.format("Time Point %d", i + 1);
                                                timePoint.hour = String.format("%02d", hour);
                                                timePoint.min = String.format("%02d", min);
                                                mTimePoints.add(timePoint);
                                                mAdapter.replaceData(mTimePoints);
                                            }
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

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (isWindowLocked())
            return;
        TimePoint timePoint = (TimePoint) adapter.getItem(position);
        if (view.getId() == R.id.tv_point_hour) {
            int select = Integer.parseInt(timePoint.hour);
            BottomDialog dialog = new BottomDialog();
            dialog.setDatas(mHourValues, select);
            dialog.setListener(value -> {
                timePoint.hour = mHourValues.get(value);
                adapter.notifyItemChanged(position);
            });
            dialog.show(getSupportFragmentManager());
        }
        if (view.getId() == R.id.tv_point_min) {
            int select = Integer.parseInt(timePoint.min) / 15;
            BottomDialog dialog = new BottomDialog();
            dialog.setDatas(mMinValues, select);
            dialog.setListener(value -> {
                timePoint.min = mMinValues.get(value);
                adapter.notifyItemChanged(position);
            });
            dialog.show(getSupportFragmentManager());
        }
    }

    @Override
    public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
        return false;
    }

    @Override
    public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
        int position = srcHolder.getAdapterPosition();
        mTimePoints.remove(position);
        int size = mTimePoints.size();
        for (int i = 0; i < size; i++) {
            TimePoint point = mTimePoints.get(i);
            point.name = String.format("Time Point %d", i);
        }
        mAdapter.replaceData(mTimePoints);
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
            tvTimingPosStrategy.setText(mValues.get(value));
        });
        dialog.show(getSupportFragmentManager());
    }

    public void onTimePointAdd(View view) {
        if (isWindowLocked())
            return;
        int size = mTimePoints.size();
        if (size >= 10)
            return;
        TimePoint timePoint = new TimePoint();
        timePoint.name = String.format("Time Point %d", size);
        timePoint.hour = String.format("%02d", 0);
        timePoint.min = String.format("%02d", 0);
        mTimePoints.add(timePoint);
        mAdapter.replaceData(mTimePoints);
    }

    public void onSave(View view) {
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setTimePosStrategy(mSelected));
        ArrayList<Integer> points = new ArrayList<>();
        for (TimePoint point : mTimePoints) {
            int hour = Integer.parseInt(point.hour);
            int min = Integer.parseInt(point.min);
            points.add((hour * 60 + min) / 15);
        }
        orderTasks.add(OrderTaskAssembler.setTimePosReportPoints(points));
        LoRaLW001MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }
}