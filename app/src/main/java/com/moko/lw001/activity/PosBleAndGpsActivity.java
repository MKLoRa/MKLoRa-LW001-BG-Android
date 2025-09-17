package com.moko.lw001.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.ble.lib.utils.MokoUtils;
import com.moko.lib.loraui.utils.ToastUtils;
import com.moko.lw001.databinding.Lw001ActivityBleGpsBinding;
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
import java.util.Objects;

public class PosBleAndGpsActivity extends BaseActivity {
    private Lw001ActivityBleGpsBinding mBind;
    private boolean savedParamsError;
    private boolean isTimeout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = Lw001ActivityBleGpsBinding.inflate(getLayoutInflater());
        setContentView(mBind.getRoot());
        EventBus.getDefault().register(this);
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>(2);
        orderTasks.add(OrderTaskAssembler.getOutdoorBleReportInterval());
        orderTasks.add(OrderTaskAssembler.getOutdoorGpsReportInterval());
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
                ToastUtils.showToast(this, "time out");
                isTimeout = true;
            }
            if (MokoConstants.ACTION_ORDER_FINISH.equals(action)) {
                dismissSyncProgressDialog();
            }
            if (MokoConstants.ACTION_ORDER_RESULT.equals(action)) {
                OrderTaskResponse response = event.getResponse();
                OrderCHAR orderCHAR = (OrderCHAR) response.orderCHAR;
                byte[] value = response.responseValue;
                if (Objects.requireNonNull(orderCHAR) == OrderCHAR.CHAR_PARAMS) {
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
                            switch (configKeyEnum) {
                                case KEY_OUTDOOR_BLE_REPORT_INTERVAL:
                                    if (result != 1) {
                                        savedParamsError = true;
                                    }
                                    break;
                                case KEY_OUTDOOR_GPS_REPORT_INTERVAL:
                                    if (result != 1) {
                                        savedParamsError = true;
                                    }
                                    if (savedParamsError || isTimeout) {
                                        ToastUtils.showToast(this, "Opps！Save failed. Please check the input characters and try again.");
                                    } else {
                                        ToastUtils.showToast(this, "Save Successfully！");
                                    }
                                    break;
                            }
                        }
                        if (flag == 0x00) {
                            // read
                            switch (configKeyEnum) {
                                case KEY_OUTDOOR_BLE_REPORT_INTERVAL:
                                    if (length > 0) {
                                        byte[] intervalBytes = Arrays.copyOfRange(value, 4, 4 + length);
                                        int interval = MokoUtils.toInt(intervalBytes);
                                        mBind.etOutdoorBleReportInterval.setText(String.valueOf(interval));
                                    }
                                    break;
                                case KEY_OUTDOOR_GPS_REPORT_INTERVAL:
                                    if (length > 0) {
                                        byte[] intervalBytes = Arrays.copyOfRange(value, 4, 4 + length);
                                        int interval = MokoUtils.toInt(intervalBytes);
                                        mBind.etOutdoorGpsReportInterval.setText(String.valueOf(interval));
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
        });
    }

    public void onSave(View view) {
        if (isWindowLocked()) return;
        if (isValid()) {
            showSyncingProgressDialog();
            saveParams();
        } else {
            ToastUtils.showToast(this, "Para error!");
        }
    }

    private boolean isValid() {
        final String bleReportInterval = mBind.etOutdoorBleReportInterval.getText().toString();
        if (TextUtils.isEmpty(bleReportInterval))
            return false;
        final int bleInterval = Integer.parseInt(bleReportInterval);
        if (bleInterval < 1 || bleInterval > 100) {
            return false;
        }
        final String gpsReportInterval = mBind.etOutdoorGpsReportInterval.getText().toString();
        if (TextUtils.isEmpty(gpsReportInterval))
            return false;
        final int gpsInterval = Integer.parseInt(gpsReportInterval);
        return gpsInterval >= 1 && gpsInterval <= 14400;
    }

    private void saveParams() {
        final String bleReportInterval = mBind.etOutdoorBleReportInterval.getText().toString();
        final int bleInterval = Integer.parseInt(bleReportInterval);
        final String gpsReportInterval = mBind.etOutdoorGpsReportInterval.getText().toString();
        final int gpsInterval = Integer.parseInt(gpsReportInterval);
        savedParamsError = false;
        isTimeout = false;
        List<OrderTask> orderTasks = new ArrayList<>(2);
        orderTasks.add(OrderTaskAssembler.setOutdoorBleReportInterval(bleInterval));
        orderTasks.add(OrderTaskAssembler.setOutdoorGpsReportInterval(gpsInterval));
        LoRaLW001MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
}
