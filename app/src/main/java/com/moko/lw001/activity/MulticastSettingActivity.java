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

import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MulticastSettingActivity extends BaseActivity {

    @BindView(R2.id.cb_multicast_group)
    CheckBox cbMulticastGroup;
    @BindView(R2.id.et_mc_addr)
    EditText etMcAddr;
    @BindView(R2.id.et_mc_app_skey)
    EditText etMcAppSkey;
    @BindView(R2.id.et_mc_nwk_skey)
    EditText etMcNwkSkey;
    @BindView(R2.id.cl_multicast_group)
    ConstraintLayout clMulticastGroup;
    private boolean mReceiverTag = false;
    private boolean savedParamsError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lw001_activity_multicast_group);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        cbMulticastGroup.setOnCheckedChangeListener((buttonView, isChecked) -> {
            clMulticastGroup.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mReceiverTag = true;
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.getMulticastEnable());
        orderTasks.add(OrderTaskAssembler.getMulticastAddr());
        orderTasks.add(OrderTaskAssembler.getMulticastAppSKey());
        orderTasks.add(OrderTaskAssembler.getMulticastNwkSkey());
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
                                    case KEY_MULTICAST_ADDR:
                                    case KEY_MULTICAST_APPSKEY:
                                    case KEY_MULTICAST_NWKSKEY:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        break;
                                    case KEY_MULTICAST_ENABLE:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        if (savedParamsError) {
                                            ToastUtils.showToast(MulticastSettingActivity.this, "Opps！Save failed. Please check the input characters and try again.");
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
                                    case KEY_MULTICAST_ENABLE:
                                        if (length > 0) {
                                            int enable = value[4] & 0xFF;
                                            cbMulticastGroup.setChecked(enable == 1);
                                        }
                                        break;
                                    case KEY_MULTICAST_ADDR:
                                        if (length > 0) {
                                            byte[] rawDataBytes = Arrays.copyOfRange(value, 4, 4 + length);
                                            String addrStr = MokoUtils.bytesToHexString(rawDataBytes);
                                            etMcAddr.setText(addrStr);
                                        }
                                        break;
                                    case KEY_MULTICAST_APPSKEY:
                                        if (length > 0) {
                                            byte[] rawDataBytes = Arrays.copyOfRange(value, 4, 4 + length);
                                            String appSkeyStr = MokoUtils.bytesToHexString(rawDataBytes);
                                            etMcAppSkey.setText(appSkeyStr);
                                        }
                                        break;
                                    case KEY_MULTICAST_NWKSKEY:
                                        if (length > 0) {
                                            byte[] rawDataBytes = Arrays.copyOfRange(value, 4, 4 + length);
                                            String nwkSkey = MokoUtils.bytesToHexString(rawDataBytes);
                                            etMcNwkSkey.setText(nwkSkey);
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
        if (cbMulticastGroup.isChecked()) {
            final String addrStr = etMcAddr.getText().toString();
            if (TextUtils.isEmpty(addrStr))
                return false;
            if (addrStr.length() != 8) {
                return false;
            }
            final String appSkeyStr = etMcAppSkey.getText().toString();
            if (TextUtils.isEmpty(appSkeyStr))
                return false;
            if (appSkeyStr.length() != 32) {
                return false;
            }
            final String nwkSkeyStr = etMcNwkSkey.getText().toString();
            if (TextUtils.isEmpty(nwkSkeyStr))
                return false;
            if (nwkSkeyStr.length() != 32) {
                return false;
            }
            return true;
        } else {
            return true;
        }
    }


    private void saveParams() {
        if (cbMulticastGroup.isChecked()) {
            final String addrStr = etMcAddr.getText().toString();
            final String appSkeyStr = etMcAppSkey.getText().toString();
            final String nwkSkeyStr = etMcNwkSkey.getText().toString();
            List<OrderTask> orderTasks = new ArrayList<>();
            orderTasks.add(OrderTaskAssembler.setMulticastAddr(addrStr));
            orderTasks.add(OrderTaskAssembler.setMulticastAppSKey(appSkeyStr));
            orderTasks.add(OrderTaskAssembler.setMulticastNwkSKey(nwkSkeyStr));
            orderTasks.add(OrderTaskAssembler.setMulticastEnable(1));
            LoRaLW001MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
        } else {
            LoRaLW001MokoSupport.getInstance().sendOrder(
                    OrderTaskAssembler.setMulticastEnable(0));
        }
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
