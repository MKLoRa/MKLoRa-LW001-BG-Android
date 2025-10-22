package com.moko.lw001.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.ble.lib.utils.MokoUtils;
import com.moko.lib.loraui.dialog.BottomDialog;
import com.moko.lib.loraui.utils.ToastUtils;
import com.moko.lw001.R;
import com.moko.lw001.adapter.TimeSegmentedPointAdapter;
import com.moko.lw001.databinding.Lw001ActivityTimeSegmentedModeBinding;
import com.moko.lw001.entity.TimeSegmentedPoint;
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
import java.util.Locale;

public class TimeSegmentedModeActivity extends BaseActivity implements BaseQuickAdapter.OnItemChildClickListener {
    private Lw001ActivityTimeSegmentedModeBinding mBind;
    private boolean mReceiverTag = false;
    private final ArrayList<TimeSegmentedPoint> mTimePoints = new ArrayList<>();
    private TimeSegmentedPointAdapter mAdapter;
    private final ArrayList<String> mHourValues = new ArrayList<>();
    private final ArrayList<String> mMinValues = new ArrayList<>();
    private final String[] mValues = {"WIFI", "BLE", "WIFI+BLE", "GPS", "WIFI+GPS", "BLE+GPS", "WIFI+BLE+GPS", "BLE&GPS"};
    private int mSelected;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = Lw001ActivityTimeSegmentedModeBinding.inflate(getLayoutInflater());
        setContentView(mBind.getRoot());
        for (int i = 0; i <= 24; i++) {
            mHourValues.add(String.format("%02d", i));
        }
        for (int i = 0; i < 60; i++) {
            mMinValues.add(String.format("%02d", i));
        }
        mAdapter = new TimeSegmentedPointAdapter(mTimePoints);
        mAdapter.setOnItemChildClickListener(this);
        mAdapter.openLoadAnimation();
        mBind.rvTimePoint.setAdapter(mAdapter);

        SwipeToDeleteCallback callback = new SwipeToDeleteCallback();
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mBind.rvTimePoint);

        EventBus.getDefault().register(this);
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mReceiverTag = true;
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>(2);
        orderTasks.add(OrderTaskAssembler.getTimePeriodicPosStrategy());
        orderTasks.add(OrderTaskAssembler.getTimePeriodicPosReportPoints());
        LoRaLW001MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[0]));
        mBind.tvTimingPosStrategy.setOnClickListener(v -> onStrategyClick());
    }

    private void onStrategyClick() {
        if (isWindowLocked()) return;
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(new ArrayList<>(Arrays.asList(mValues)), mSelected);
        dialog.setListener(value -> {
            mSelected = value;
            mBind.tvTimingPosStrategy.setText(mValues[value]);
        });
        dialog.show(getSupportFragmentManager());
    }

    public class SwipeToDeleteCallback extends ItemTouchHelper.Callback {
        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            // 不支持拖动
            int dragFlags = 0;
            // 支持从右向左滑动
            int swipeFlags = ItemTouchHelper.START;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAbsoluteAdapterPosition();
            mTimePoints.remove(position);
            int size = mTimePoints.size();
            for (int i = 1; i <= size; i++) {
                TimeSegmentedPoint point = mTimePoints.get(i - 1);
                point.name = String.format("Time Period %d", i);
                point.intervalStr = String.format("Report Interval %d", i);
            }
            mAdapter.replaceData(mTimePoints);
        }
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

    @SuppressLint("DefaultLocale")
    @Subscribe(threadMode = ThreadMode.POSTING, priority = 300)
    public void onOrderTaskResponseEvent(OrderTaskResponseEvent event) {
        final String action = event.getAction();
        if (!MokoConstants.ACTION_CURRENT_DATA.equals(action))
            EventBus.getDefault().cancelEventDelivery(event);
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_ORDER_FINISH.equals(action)) {
                dismissSyncProgressDialog();
            }
            if (MokoConstants.ACTION_ORDER_RESULT.equals(action)) {
                OrderTaskResponse response = event.getResponse();
                OrderCHAR orderCHAR = (OrderCHAR) response.orderCHAR;
                byte[] value = response.responseValue;
                if (orderCHAR == OrderCHAR.CHAR_PARAMS) {
                    if (value.length >= 4) {
                        int header = value[0] & 0xFF;// 0xED
                        int flag = value[1] & 0xFF;// read or write
                        int cmd = value[2] & 0xff;
                        if (header != 0xED) return;
                        ParamsKeyEnum configKeyEnum = ParamsKeyEnum.fromParamKey(cmd);
                        if (configKeyEnum == null) {
                            return;
                        }
                        int length = value[3] & 0xFF;
                        if (flag == 0x01) {
                            // write
                            int result = value[4] & 0xFF;
                            if (configKeyEnum == ParamsKeyEnum.KEY_TIME_PERIODIC_MODE_REPORT_TIME_POINT) {
                                if (result != 1) {
                                    ToastUtils.showToast(this, "Opps！Save failed. Please check the input characters and try again.");
                                } else {
                                    ToastUtils.showToast(this, "Save Successfully！");
                                }
                            }
                        } else if (flag == 0x00) {
                            // read
                            if (configKeyEnum == ParamsKeyEnum.KEY_TIME_PERIODIC_MODE_REPORT_TIME_POINT) {
                                if (length > 0) {
                                    for (int i = 0; i < length; i += 8) {
                                        int start = MokoUtils.toInt(Arrays.copyOfRange(value, 4 + i, 6 + i));
                                        int end = MokoUtils.toInt(Arrays.copyOfRange(value, 6 + i, 8 + i));
                                        int interval = MokoUtils.toInt(Arrays.copyOfRange(value, 8 + i, 12 + i));
                                        TimeSegmentedPoint timePoint = new TimeSegmentedPoint();
                                        timePoint.name = String.format("Time Period %d", i / 8 + 1);
                                        timePoint.intervalStr = String.format("Report Interval %d", i / 8 + 1);
                                        timePoint.start = start;
                                        int startHour = start / 60;
                                        int startMin = start % 60;
                                        timePoint.startHour = mHourValues.get(startHour);
                                        timePoint.startMin = mMinValues.get(startMin);
                                        timePoint.end = end;
                                        int endHour = end / 60;
                                        int endMin = end % 60;
                                        timePoint.endHour = mHourValues.get(endHour);
                                        timePoint.endMin = mMinValues.get(endMin);
                                        timePoint.interval = interval;
                                        mTimePoints.add(timePoint);
                                        mAdapter.replaceData(mTimePoints);
                                    }
                                }
                            } else if (configKeyEnum == ParamsKeyEnum.KEY_TIME_PERIODIC_MODE_POS_STRATEGY) {
                                mSelected = value[4] - 1;
                                mBind.tvTimingPosStrategy.setText(mValues[mSelected]);
                            }
                        }
                    }
                }
            }
        });
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    if (blueState == BluetoothAdapter.STATE_TURNING_OFF) {
                        dismissSyncProgressDialog();
                        finish();
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

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (isWindowLocked()) return;
        TimeSegmentedPoint timePoint = (TimeSegmentedPoint) adapter.getItem(position);
        EditText etInterval = (EditText) adapter.getViewByPosition(mBind.rvTimePoint, position, R.id.et_report_interval);
        if (!TextUtils.isEmpty(etInterval.getText())) {
            timePoint.interval = Integer.parseInt(etInterval.getText().toString());
        }
        if (view.getId() == R.id.tv_point_start_hour) {
            int select = Integer.parseInt(timePoint.startHour);
            BottomDialog dialog = new BottomDialog();
            dialog.setDatas(mHourValues, select);
            dialog.setListener(value -> {
                timePoint.start = value * 60 + Integer.parseInt(timePoint.startMin);
                timePoint.startHour = mHourValues.get(value);
                adapter.notifyItemChanged(position);
            });
            dialog.show(getSupportFragmentManager());
        }
        if (view.getId() == R.id.tv_point_start_min) {
            int select = Integer.parseInt(timePoint.startMin);
            BottomDialog dialog = new BottomDialog();
            dialog.setDatas(mMinValues, select);
            dialog.setListener(value -> {
                timePoint.start = Integer.parseInt(timePoint.startHour) * 60 + value;
                timePoint.startMin = mMinValues.get(value);
                adapter.notifyItemChanged(position);
            });
            dialog.show(getSupportFragmentManager());
        }
        if (view.getId() == R.id.tv_point_end_hour) {
            int select = Integer.parseInt(timePoint.endHour);
            BottomDialog dialog = new BottomDialog();
            dialog.setDatas(mHourValues, select);
            dialog.setListener(value -> {
                timePoint.end = value * 60 + Integer.parseInt(timePoint.endMin);
                timePoint.endHour = mHourValues.get(value);
                adapter.notifyItemChanged(position);
            });
            dialog.show(getSupportFragmentManager());
        }
        if (view.getId() == R.id.tv_point_end_min) {
            int select = Integer.parseInt(timePoint.endMin);
            BottomDialog dialog = new BottomDialog();
            dialog.setDatas(mMinValues, select);
            dialog.setListener(value -> {
                timePoint.end = Integer.parseInt(timePoint.endHour) * 60 + value;
                timePoint.endMin = mMinValues.get(value);
                adapter.notifyItemChanged(position);
            });
            dialog.show(getSupportFragmentManager());
        }
    }

    public void onTimePointAdd(View view) {
        if (isWindowLocked()) return;
        int size = mTimePoints.size();
        if (size >= 4) {
            ToastUtils.showToast(this, "You can set up to 4 time points!");
            return;
        }
        TimeSegmentedPoint timePoint = new TimeSegmentedPoint();
        timePoint.name = String.format(Locale.getDefault(), "Time Period %d", size + 1);
        timePoint.intervalStr = String.format(Locale.getDefault(), "Report Interval %d", size + 1);
        timePoint.start = 0;
        timePoint.startHour = "0";
        timePoint.startMin = "0";
        timePoint.end = 0;
        timePoint.endHour = "0";
        timePoint.endMin = "0";
        timePoint.interval = 600;
        mTimePoints.add(timePoint);
        mAdapter.replaceData(mTimePoints);
    }

    public void onSave(View view) {
        int failedCode = isValid();
        if (failedCode == 1) {
            ToastUtils.showToast(this, "Para error!");
            return;
        }
        if (failedCode == 2) {
            ToastUtils.showToast(this, "The start time must be earlier than the end time!");
            return;
        }
        if (failedCode == 3) {
            ToastUtils.showToast(this, "Time ranges cannot overlap!");
            return;
        }
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>(2);
        orderTasks.add(OrderTaskAssembler.setTimePeriodicPosStrategy(mSelected + 1));
        List<String> points = new ArrayList<>(4);
        if (!mTimePoints.isEmpty()) {
            for (TimeSegmentedPoint point : mTimePoints) {
                StringBuilder builder = new StringBuilder();
                builder.append(int2HexString(point.start, 4)).append(int2HexString(point.end, 4)).
                        append(int2HexString(point.interval, 8));
                points.add(builder.toString());
            }
        }
        orderTasks.add(OrderTaskAssembler.setTimePeriodicPosReportPoints(points));
        LoRaLW001MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[0]));
    }

    private String int2HexString(int b, int length) {
        StringBuilder builder = new StringBuilder(String.format("%02X", b));
        while (builder.length() < length) builder.insert(0, "0");
        return builder.toString();
    }

    private int isValid() {
        int count = mAdapter.getItemCount();
        for (int i = 0; i < count; i++) {
            EditText etInterval = (EditText) mAdapter.getViewByPosition(mBind.rvTimePoint, i, R.id.et_report_interval);
            if (TextUtils.isEmpty(etInterval.getText()))
                return 1;
            String intervalStr = etInterval.getText().toString();
            int interval = Integer.parseInt(intervalStr);
            if (interval < 30 || interval > 86400) return 1;
            mTimePoints.get(i).interval = Integer.parseInt(intervalStr);
        }
        for (int i = 0; i < count; i++) {
            int start = mTimePoints.get(i).start;
            int end = mTimePoints.get(i).end;
            if (start > 1440 || end > 1440)
                return 1;
        }
        if (count > 0) {
            if (mTimePoints.get(0).start >= mTimePoints.get(0).end)
                return 2;
        }
        if (count > 1) {
            if (mTimePoints.get(1).start >= mTimePoints.get(1).end)
                return 2;
            if (mTimePoints.get(0).start < mTimePoints.get(1).end && mTimePoints.get(1).start < mTimePoints.get(0).end)
                return 3;
        }
        if (count > 2) {
            if (mTimePoints.get(2).start >= mTimePoints.get(2).end)
                return 2;
            if (mTimePoints.get(0).start < mTimePoints.get(2).end && mTimePoints.get(2).start < mTimePoints.get(0).end)
                return 3;
            if (mTimePoints.get(1).start < mTimePoints.get(2).end && mTimePoints.get(2).start < mTimePoints.get(1).end)
                return 3;
        }
        return 0;
    }
}
