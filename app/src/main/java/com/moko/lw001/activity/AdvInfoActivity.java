package com.moko.lw001.activity;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
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
import com.moko.lw001.dialog.LoadingMessageDialog;
import com.moko.lw001.entity.TxPowerEnum;
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
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdvInfoActivity extends BaseActivity implements OnSeekBarChangeListener {

    public static final String UUID_PATTERN = "[A-Fa-f0-9]{8}-(?:[A-Fa-f0-9]{4}-){3}[A-Fa-f0-9]{12}";
    private final String FILTER_ASCII = "\\A\\p{ASCII}*\\z";
    @BindView(R2.id.et_adv_name)
    EditText etAdvName;
    @BindView(R2.id.et_uuid)
    EditText etUuid;
    @BindView(R2.id.et_major)
    EditText etMajor;
    @BindView(R2.id.et_minor)
    EditText etMinor;
    @BindView(R2.id.sb_rssi_1m)
    SeekBar sbRssi1m;
    @BindView(R2.id.tv_rssi_1m_value)
    TextView tvRssi1mValue;
    @BindView(R2.id.sb_tx_power)
    SeekBar sbTxPower;
    @BindView(R2.id.tv_tx_power_value)
    TextView tvTxPowerValue;

    private Pattern pattern;
    private boolean mReceiverTag = false;
    private boolean savedParamsError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lw001_activity_adv);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        sbRssi1m.setOnSeekBarChangeListener(this);
        sbTxPower.setOnSeekBarChangeListener(this);
        pattern = Pattern.compile(UUID_PATTERN);
        etUuid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                if (!pattern.matcher(input).matches()) {
                    if (input.length() == 9 && !input.endsWith("-")) {
                        String show = input.substring(0, 8) + "-" + input.substring(8, input.length());
                        etUuid.setText(show);
                        etUuid.setSelection(show.length());
                    }
                    if (input.length() == 14 && !input.endsWith("-")) {
                        String show = input.substring(0, 13) + "-" + input.substring(13, input.length());
                        etUuid.setText(show);
                        etUuid.setSelection(show.length());
                    }
                    if (input.length() == 19 && !input.endsWith("-")) {
                        String show = input.substring(0, 18) + "-" + input.substring(18, input.length());
                        etUuid.setText(show);
                        etUuid.setSelection(show.length());
                    }
                    if (input.length() == 24 && !input.endsWith("-")) {
                        String show = input.substring(0, 23) + "-" + input.substring(23, input.length());
                        etUuid.setText(show);
                        etUuid.setSelection(show.length());
                    }
                    if (input.length() == 32 && input.indexOf("-") < 0) {
                        StringBuilder stringBuilder = new StringBuilder(input);
                        stringBuilder.insert(8, "-");
                        stringBuilder.insert(13, "-");
                        stringBuilder.insert(18, "-");
                        stringBuilder.insert(23, "-");
                        etUuid.setText(stringBuilder.toString());
                        etUuid.setSelection(stringBuilder.toString().length());
                    }
                }
            }
        });
        InputFilter inputFilter = (source, start, end, dest, dstart, dend) -> {
            if (!(source + "").matches(FILTER_ASCII)) {
                return "";
            }

            return null;
        };
        etAdvName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(13), inputFilter});

        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mReceiverTag = true;
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.getAdvName());
        orderTasks.add(OrderTaskAssembler.getAdvUUID());
        orderTasks.add(OrderTaskAssembler.getAdvMajor());
        orderTasks.add(OrderTaskAssembler.getAdvMinor());
        orderTasks.add(OrderTaskAssembler.getAdvTxPower());
        orderTasks.add(OrderTaskAssembler.getAdvRSSI());
        LoRaLW001MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 300)
    public void onConnectStatusEvent(ConnectStatusEvent event) {
        final String action = event.getAction();
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_DISCONNECTED.equals(action)) {
                setResult(RESULT_OK);
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
                                    case KEY_ADV_NAME:
                                    case KEY_ADV_UUID:
                                    case KEY_ADV_MAJOR:
                                    case KEY_ADV_MINOR:
                                    case KEY_ADV_RSSI:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        break;
                                    case KEY_ADV_TX_POWER:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        if (savedParamsError) {
                                            ToastUtils.showToast(AdvInfoActivity.this, "Opps！Save failed. Please check the input characters and try again.");
                                        } else {
                                            setResult(RESULT_OK);
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
                                    case KEY_ADV_NAME:
                                        if (length > 0) {
                                            byte[] rawDataBytes = Arrays.copyOfRange(value, 4, 4 + length);
                                            final String deviceName = new String(rawDataBytes);
                                            setDeviceName(deviceName);
                                        }
                                        break;
                                    case KEY_ADV_UUID:
                                        if (length > 0) {
                                            byte[] rawDataBytes = Arrays.copyOfRange(value, 4, 4 + length);
                                            final String uuid = MokoUtils.bytesToHexString(rawDataBytes);
                                            setUUID(uuid);
                                        }
                                        break;
                                    case KEY_ADV_MAJOR:
                                        if (length > 0) {
                                            byte[] rawDataBytes = Arrays.copyOfRange(value, 4, 4 + length);
                                            final int major = MokoUtils.toInt(rawDataBytes);
                                            setMajor(major);
                                        }
                                        break;
                                    case KEY_ADV_MINOR:
                                        if (length > 0) {
                                            byte[] rawDataBytes = Arrays.copyOfRange(value, 4, 4 + length);
                                            final int minor = MokoUtils.toInt(rawDataBytes);
                                            setMinor(minor);
                                        }
                                        break;
                                    case KEY_ADV_RSSI:
                                        if (length > 0) {
                                            int rssi_1m = value[4];
                                            setMeasurePower(rssi_1m);
                                        }
                                        break;
                                    case KEY_ADV_TX_POWER:
                                        if (length > 0) {
                                            int txPower = value[4];
                                            setTransmission(txPower);
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

    private void setDeviceName(String deviceName) {
        etAdvName.setText(deviceName);
        etAdvName.setSelection(deviceName.length());
    }

    private void setUUID(String uuid) {
        StringBuilder stringBuilder = new StringBuilder(uuid);
        stringBuilder.insert(8, "-");
        stringBuilder.insert(13, "-");
        stringBuilder.insert(18, "-");
        stringBuilder.insert(23, "-");
        etUuid.setText(stringBuilder.toString());
        int length = stringBuilder.toString().length();
        etUuid.setSelection(length);
    }

    private void setMajor(int major) {
        etMajor.setText(String.valueOf(major));
        etMajor.setSelection(String.valueOf(major).length());
    }

    private void setMinor(int minor) {
        etMinor.setText(String.valueOf(minor));
        etMinor.setSelection(String.valueOf(minor).length());
    }

    private void setMeasurePower(int rssi_1m) {
        int progress = rssi_1m + 127;
        sbRssi1m.setProgress(progress);
        tvRssi1mValue.setText(String.format("%ddBm", rssi_1m));
    }

    private void setTransmission(int txPower) {
        int progress = TxPowerEnum.fromTxPower(txPower).ordinal();
        sbTxPower.setProgress(progress);
        tvTxPowerValue.setText(String.format("%ddBm", txPower));
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
        final String advNameStr = etAdvName.getText().toString();
        final String uuidStr = etUuid.getText().toString();
        final String majorStr = etMajor.getText().toString();
        final String minorStr = etMinor.getText().toString();
        if (TextUtils.isEmpty(advNameStr))
            return false;
        if (TextUtils.isEmpty(uuidStr) || uuidStr.length() != 36)
            return false;
        if (TextUtils.isEmpty(majorStr))
            return false;
        int major = Integer.parseInt(majorStr);
        if (major < 0 || major > 65535)
            return false;
        if (TextUtils.isEmpty(minorStr))
            return false;
        int minor = Integer.parseInt(minorStr);
        if (minor < 0 || minor > 65535)
            return false;
        return true;
    }


    private void saveParams() {
        final String advNameStr = etAdvName.getText().toString();
        final String uuidStr = etUuid.getText().toString();
        final String majorStr = etMajor.getText().toString();
        final String minorStr = etMinor.getText().toString();
        List<OrderTask> orderTasks = new ArrayList<>();

        orderTasks.add(OrderTaskAssembler.setAdvName(advNameStr));

        String uuid = uuidStr.replaceAll("-", "");
        orderTasks.add(OrderTaskAssembler.setAdvUUID(uuid));

        int major = Integer.parseInt(majorStr);
        orderTasks.add(OrderTaskAssembler.setAdvMajor(major));

        int minor = Integer.parseInt(minorStr);
        orderTasks.add(OrderTaskAssembler.setAdvMinor(minor));

        int rssi1mProgress = sbRssi1m.getProgress();
        int rssi1m = rssi1mProgress - 127;
        orderTasks.add(OrderTaskAssembler.setAdvRSSI(rssi1m));

        int txPowerProgress = sbTxPower.getProgress();
        int txPower = TxPowerEnum.fromOrdinal(txPowerProgress).getTxPower();
        orderTasks.add(OrderTaskAssembler.setAdvTxPower(txPower));
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
                            AdvInfoActivity.this.setResult(RESULT_OK);
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int id = seekBar.getId();
        if (id == R.id.sb_rssi_1m) {
            int rssi_1m = progress - 127;
            tvRssi1mValue.setText(String.format("%ddBm", rssi_1m));
        } else if (id == R.id.sb_tx_power) {
            TxPowerEnum txPowerEnum = TxPowerEnum.fromOrdinal(progress);
            if (txPowerEnum == null)
                return;
            int txPower = txPowerEnum.getTxPower();
            tvTxPowerValue.setText(String.format("%ddBm", txPower));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
