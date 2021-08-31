package com.moko.lw001.activity;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
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
import com.moko.lw001.utils.ToastUtils;
import com.moko.support.lw001.LoRaLW001MokoSupport;
import com.moko.support.lw001.OrderTaskAssembler;
import com.moko.support.lw001.entity.DataTypeEnum;
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

public class FilterOptionsBActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {
    private final String FILTER_ASCII = "[ -~]*";
    @BindView(R2.id.sb_rssi_filter)
    SeekBar sbRssiFilter;
    @BindView(R2.id.tv_rssi_filter_value)
    TextView tvRssiFilterValue;
    @BindView(R2.id.tv_rssi_filter_tips)
    TextView tvRssiFilterTips;
    @BindView(R2.id.iv_mac_address)
    ImageView ivMacAddress;
    @BindView(R2.id.et_mac_address)
    EditText etMacAddress;
    @BindView(R2.id.iv_adv_name)
    ImageView ivAdvName;
    @BindView(R2.id.et_adv_name)
    EditText etAdvName;
    @BindView(R2.id.iv_ibeacon_major)
    ImageView ivIbeaconMajor;
    @BindView(R2.id.iv_ibeacon_minor)
    ImageView ivIbeaconMinor;
    @BindView(R2.id.iv_raw_adv_data)
    ImageView ivRawAdvData;
    @BindView(R2.id.et_ibeacon_major_min)
    EditText etIbeaconMajorMin;
    @BindView(R2.id.et_ibeacon_major_max)
    EditText etIbeaconMajorMax;
    @BindView(R2.id.ll_ibeacon_major)
    LinearLayout llIbeaconMajor;
    @BindView(R2.id.et_ibeacon_minor_min)
    EditText etIbeaconMinorMin;
    @BindView(R2.id.et_ibeacon_minor_max)
    EditText etIbeaconMinorMax;
    @BindView(R2.id.ll_ibeacon_minor)
    LinearLayout llIbeaconMinor;
    @BindView(R2.id.iv_raw_data_del)
    ImageView ivRawDataDel;
    @BindView(R2.id.iv_raw_data_add)
    ImageView ivRawDataAdd;
    @BindView(R2.id.ll_raw_data_filter)
    LinearLayout llRawDataFilter;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.cb_mac_address)
    CheckBox cbMacAddress;
    @BindView(R2.id.cb_adv_name)
    CheckBox cbAdvName;
    @BindView(R2.id.iv_ibeacon_uuid)
    ImageView ivIbeaconUuid;
    @BindView(R2.id.cb_ibeacon_uuid)
    CheckBox cbIbeaconUuid;
    @BindView(R2.id.et_ibeacon_uuid)
    EditText etIbeaconUuid;
    @BindView(R2.id.cb_ibeacon_major)
    CheckBox cbIbeaconMajor;
    @BindView(R2.id.cb_ibeacon_minor)
    CheckBox cbIbeaconMinor;
    @BindView(R2.id.cb_raw_adv_data)
    CheckBox cbRawAdvData;
    @BindView(R2.id.tv_condition)
    TextView tvCondition;
    @BindView(R2.id.iv_condition)
    ImageView ivCondition;
    @BindView(R2.id.tv_condition_tips)
    TextView tvConditionTips;
    private boolean mReceiverTag = false;
    private boolean savedParamsError;

    private ArrayList<String> filterRawDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lw001_activity_filter);
        ButterKnife.bind(this);

        tvTitle.setText("Filter Condition B");
        tvCondition.setText("Filter Condition B");
        tvConditionTips.setText(getString(R.string.condition_tips, "B", "B"));

        sbRssiFilter.setOnSeekBarChangeListener(this);
        InputFilter inputFilter = (source, start, end, dest, dstart, dend) -> {
            if (!(source + "").matches(FILTER_ASCII)) {
                return "";
            }

            return null;
        };
        etAdvName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(29), inputFilter});
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
            orderTasks.add(OrderTaskAssembler.getFilterSwitchB());
            orderTasks.add(OrderTaskAssembler.getFilterRssiB());
            orderTasks.add(OrderTaskAssembler.getFilterMacB());
            orderTasks.add(OrderTaskAssembler.getFilterNameB());
            orderTasks.add(OrderTaskAssembler.getFilterUUIDB());
            orderTasks.add(OrderTaskAssembler.getFilterMajorRangeB());
            orderTasks.add(OrderTaskAssembler.getFilterMinorRangeB());
            orderTasks.add(OrderTaskAssembler.getFilterRawDataB());
            LoRaLW001MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
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
                                    case KEY_FILTER_RSSI_B:
                                    case KEY_FILTER_MAC_B:
                                    case KEY_FILTER_ADV_NAME_B:
                                    case KEY_FILTER_UUID_B:
                                    case KEY_FILTER_MAJOR_RANGE_B:
                                    case KEY_FILTER_MINOR_RANGE_B:
                                    case KEY_FILTER_ADV_RAW_DATA_B:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        break;
                                    case KEY_FILTER_SWITCH_B:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        if (savedParamsError) {
                                            ToastUtils.showToast(FilterOptionsBActivity.this, "Opps！Save failed. Please check the input characters and try again.");
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
                                    case KEY_FILTER_SWITCH_B:
                                        if (length == 1) {
                                            final int enable = value[4] & 0xFF;
                                            filterSwitchEnable = enable == 1;
                                            ivCondition.setImageResource(filterSwitchEnable ? R.drawable.lw001_ic_checked : R.drawable.lw001_ic_unchecked);
                                        }
                                        break;
                                    case KEY_FILTER_RSSI_B:
                                        if (length == 1) {
                                            final int rssi = value[4];
                                            int progress = rssi + 127;
                                            sbRssiFilter.setProgress(progress);
                                            tvRssiFilterValue.setText(String.format("%ddBm", rssi));
                                            tvRssiFilterTips.setText(getString(R.string.rssi_filter, rssi));
                                        }
                                        break;
                                    case KEY_FILTER_MAC_B:
                                        if (length > 0) {
                                            final int enable = value[4] & 0xFF;
                                            filterMacEnable = enable > 0;
                                            ivMacAddress.setImageResource(filterMacEnable ? R.drawable.lw001_ic_checked : R.drawable.lw001_ic_unchecked);
                                            etMacAddress.setVisibility(filterMacEnable ? View.VISIBLE : View.GONE);
                                            cbMacAddress.setVisibility(filterMacEnable ? View.VISIBLE : View.GONE);
                                            cbMacAddress.setChecked(enable > 1);
                                            if (length > 1) {
                                                byte[] macBytes = Arrays.copyOfRange(value, 5, 4 + length);
                                                String filterMac = MokoUtils.bytesToHexString(macBytes).toUpperCase();
                                                etMacAddress.setText(filterMac);
                                            }
                                        }
                                        break;
                                    case KEY_FILTER_ADV_NAME_B:
                                        if (length > 0) {
                                            final int enable = value[4] & 0xFF;
                                            filterNameEnable = enable > 0;
                                            ivAdvName.setImageResource(filterNameEnable ? R.drawable.lw001_ic_checked : R.drawable.lw001_ic_unchecked);
                                            etAdvName.setVisibility(filterNameEnable ? View.VISIBLE : View.GONE);
                                            cbAdvName.setVisibility(filterNameEnable ? View.VISIBLE : View.GONE);
                                            cbAdvName.setChecked(enable > 1);
                                            if (length > 1) {
                                                byte[] nameBytes = Arrays.copyOfRange(value, 5, 4 + length);
                                                String filterName = new String(nameBytes);
                                                etAdvName.setText(filterName);
                                            }
                                        }
                                        break;
                                    case KEY_FILTER_UUID_B:
                                        if (length > 0) {
                                            final int enable = value[4] & 0xFF;
                                            filterUUIDEnable = enable > 0;
                                            ivIbeaconUuid.setImageResource(filterUUIDEnable ? R.drawable.lw001_ic_checked : R.drawable.lw001_ic_unchecked);
                                            etIbeaconUuid.setVisibility(filterUUIDEnable ? View.VISIBLE : View.GONE);
                                            cbIbeaconUuid.setVisibility(filterUUIDEnable ? View.VISIBLE : View.GONE);
                                            cbIbeaconUuid.setChecked(enable > 1);
                                            if (length > 1) {
                                                byte[] uuidBytes = Arrays.copyOfRange(value, 5, 4 + length);
                                                String filterUUID = MokoUtils.bytesToHexString(uuidBytes).toUpperCase();
                                                etIbeaconUuid.setText(filterUUID);
                                            }
                                        }
                                        break;
                                    case KEY_FILTER_MAJOR_RANGE_B:
                                        if (length > 0) {
                                            final int enable = value[4] & 0xFF;
                                            filterMajorEnable = enable > 0;
                                            ivIbeaconMajor.setImageResource(filterMajorEnable ? R.drawable.lw001_ic_checked : R.drawable.lw001_ic_unchecked);
                                            llIbeaconMajor.setVisibility(filterMajorEnable ? View.VISIBLE : View.GONE);
                                            cbIbeaconMajor.setVisibility(filterMajorEnable ? View.VISIBLE : View.GONE);
                                            cbIbeaconMajor.setChecked(enable > 1);
                                            if (length > 1) {
                                                byte[] majorMinBytes = Arrays.copyOfRange(value, 5, 7);
                                                int majorMin = MokoUtils.toInt(majorMinBytes);
                                                etIbeaconMajorMin.setText(String.valueOf(majorMin));
                                                byte[] majorMaxBytes = Arrays.copyOfRange(value, 7, 9);
                                                int majorMax = MokoUtils.toInt(majorMaxBytes);
                                                etIbeaconMajorMax.setText(String.valueOf(majorMax));
                                            }
                                        }
                                        break;
                                    case KEY_FILTER_MINOR_RANGE_B:
                                        if (length > 0) {
                                            final int enable = value[4] & 0xFF;
                                            filterMinorEnable = enable > 0;
                                            ivIbeaconMinor.setImageResource(filterMinorEnable ? R.drawable.lw001_ic_checked : R.drawable.lw001_ic_unchecked);
                                            llIbeaconMinor.setVisibility(filterMinorEnable ? View.VISIBLE : View.GONE);
                                            cbIbeaconMinor.setVisibility(filterMinorEnable ? View.VISIBLE : View.GONE);
                                            cbIbeaconMinor.setChecked(enable > 1);
                                            if (length > 1) {
                                                byte[] minorMinBytes = Arrays.copyOfRange(value, 5, 7);
                                                int minorMin = MokoUtils.toInt(minorMinBytes);
                                                etIbeaconMinorMin.setText(String.valueOf(minorMin));
                                                byte[] minorMaxBytes = Arrays.copyOfRange(value, 7, 9);
                                                int minorMax = MokoUtils.toInt(minorMaxBytes);
                                                etIbeaconMinorMax.setText(String.valueOf(minorMax));
                                            }
                                        }
                                        break;
                                    case KEY_FILTER_ADV_RAW_DATA_B:
                                        if (length > 0) {
                                            final int enable = value[4] & 0xFF;
                                            filterRawAdvDataEnable = enable > 0;
                                            ivRawAdvData.setImageResource(filterRawAdvDataEnable ? R.drawable.lw001_ic_checked : R.drawable.lw001_ic_unchecked);
                                            llRawDataFilter.setVisibility(filterRawAdvDataEnable ? View.VISIBLE : View.GONE);
                                            ivRawDataAdd.setVisibility(filterRawAdvDataEnable ? View.VISIBLE : View.GONE);
                                            ivRawDataDel.setVisibility(filterRawAdvDataEnable ? View.VISIBLE : View.GONE);
                                            cbRawAdvData.setVisibility(filterRawAdvDataEnable ? View.VISIBLE : View.GONE);
                                            cbRawAdvData.setChecked(enable > 1);
                                            if (length > 1) {
                                                byte[] rawDataBytes = Arrays.copyOfRange(value, 5, 4 + length);
                                                for (int i = 0, l = rawDataBytes.length; i < l; ) {
                                                    View v = LayoutInflater.from(FilterOptionsBActivity.this).inflate(R.layout.lw001_item_raw_data_filter, llRawDataFilter, false);
                                                    EditText etDataType = v.findViewById(R.id.et_data_type);
                                                    EditText etMin = v.findViewById(R.id.et_min);
                                                    EditText etMax = v.findViewById(R.id.et_max);
                                                    EditText etRawData = v.findViewById(R.id.et_raw_data);
                                                    int filterLength = rawDataBytes[i] & 0xFF;
                                                    i++;
                                                    String type = MokoUtils.byte2HexString(rawDataBytes[i]);
                                                    i++;
                                                    String min = String.valueOf((rawDataBytes[i] & 0xFF));
                                                    i++;
                                                    String max = String.valueOf((rawDataBytes[i] & 0xFF));
                                                    i++;
                                                    String data = MokoUtils.bytesToHexString(Arrays.copyOfRange(rawDataBytes, i, i + filterLength - 3));
                                                    i += filterLength - 3;
                                                    etDataType.setText(type);
                                                    etMin.setText(min);
                                                    etMax.setText(max);
                                                    etRawData.setText(data);
                                                    llRawDataFilter.addView(v);
                                                }
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

    private boolean filterSwitchEnable;
    private boolean filterMacEnable;
    private boolean filterNameEnable;
    private boolean filterUUIDEnable;
    private boolean filterMajorEnable;
    private boolean filterMinorEnable;
    private boolean filterRawAdvDataEnable;

    public void onBack(View view) {
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

    public void onMacAddress(View view) {
        filterMacEnable = !filterMacEnable;
        ivMacAddress.setImageResource(filterMacEnable ? R.drawable.lw001_ic_checked : R.drawable.lw001_ic_unchecked);
        etMacAddress.setVisibility(filterMacEnable ? View.VISIBLE : View.GONE);
        cbMacAddress.setVisibility(filterMacEnable ? View.VISIBLE : View.GONE);
    }

    public void onAdvName(View view) {
        filterNameEnable = !filterNameEnable;
        ivAdvName.setImageResource(filterNameEnable ? R.drawable.lw001_ic_checked : R.drawable.lw001_ic_unchecked);
        etAdvName.setVisibility(filterNameEnable ? View.VISIBLE : View.GONE);
        cbAdvName.setVisibility(filterNameEnable ? View.VISIBLE : View.GONE);
    }

    public void oniBeaconUUID(View view) {
        filterUUIDEnable = !filterUUIDEnable;
        ivIbeaconUuid.setImageResource(filterUUIDEnable ? R.drawable.lw001_ic_checked : R.drawable.lw001_ic_unchecked);
        etIbeaconUuid.setVisibility(filterUUIDEnable ? View.VISIBLE : View.GONE);
        cbIbeaconUuid.setVisibility(filterUUIDEnable ? View.VISIBLE : View.GONE);
    }

    public void oniBeaconMajor(View view) {
        filterMajorEnable = !filterMajorEnable;
        ivIbeaconMajor.setImageResource(filterMajorEnable ? R.drawable.lw001_ic_checked : R.drawable.lw001_ic_unchecked);
        llIbeaconMajor.setVisibility(filterMajorEnable ? View.VISIBLE : View.GONE);
        cbIbeaconMajor.setVisibility(filterMajorEnable ? View.VISIBLE : View.GONE);
    }

    public void oniBeaconMinor(View view) {
        filterMinorEnable = !filterMinorEnable;
        ivIbeaconMinor.setImageResource(filterMinorEnable ? R.drawable.lw001_ic_checked : R.drawable.lw001_ic_unchecked);
        llIbeaconMinor.setVisibility(filterMinorEnable ? View.VISIBLE : View.GONE);
        cbIbeaconMinor.setVisibility(filterMinorEnable ? View.VISIBLE : View.GONE);
    }

    public void onRawAdvData(View view) {
        filterRawAdvDataEnable = !filterRawAdvDataEnable;
        ivRawAdvData.setImageResource(filterRawAdvDataEnable ? R.drawable.lw001_ic_checked : R.drawable.lw001_ic_unchecked);
        llRawDataFilter.setVisibility(filterRawAdvDataEnable ? View.VISIBLE : View.GONE);
        ivRawDataAdd.setVisibility(filterRawAdvDataEnable ? View.VISIBLE : View.GONE);
        ivRawDataDel.setVisibility(filterRawAdvDataEnable ? View.VISIBLE : View.GONE);
        cbRawAdvData.setVisibility(filterRawAdvDataEnable ? View.VISIBLE : View.GONE);
    }

    public void onRawDataAdd(View view) {
        if (isWindowLocked())
            return;
        int count = llRawDataFilter.getChildCount();
        if (count > 4) {
            ToastUtils.showToast(this, "You can set up to 5 filters!");
            return;
        }
        View v = LayoutInflater.from(this).inflate(R.layout.lw001_item_raw_data_filter, llRawDataFilter, false);
        llRawDataFilter.addView(v);
    }

    public void onRawDataDel(View view) {
        if (isWindowLocked())
            return;
        final int c = llRawDataFilter.getChildCount();
        if (c == 0) {
            ToastUtils.showToast(this, "There are currently no filters to delete");
            return;
        }
        AlertMessageDialog dialog = new AlertMessageDialog();
        dialog.setTitle("Warning");
        dialog.setMessage("Please confirm whether to delete  a filter option，If yes，the last option will be deleted. ");
        dialog.setOnAlertConfirmListener(() -> {
            int count = llRawDataFilter.getChildCount();
            if (count > 0) {
                llRawDataFilter.removeViewAt(count - 1);
            }
        });
        dialog.show(getSupportFragmentManager());
    }

    public void onCondition(View view) {
        filterSwitchEnable = !filterSwitchEnable;
        ivCondition.setImageResource(filterSwitchEnable ? R.drawable.lw001_ic_checked : R.drawable.lw001_ic_unchecked);
    }

    private void saveParams() {
        final int progress = sbRssiFilter.getProgress();
        int filterRssi = progress - 127;
        final String mac = etMacAddress.getText().toString();
        final String name = etAdvName.getText().toString();
        final String uuid = etIbeaconUuid.getText().toString();
        final String majorMin = etIbeaconMajorMin.getText().toString();
        final String majorMax = etIbeaconMajorMax.getText().toString();
        final String minorMin = etIbeaconMinorMin.getText().toString();
        final String minorMax = etIbeaconMinorMax.getText().toString();

        savedParamsError = false;
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setFilterRssiB(filterRssi));
        orderTasks.add(OrderTaskAssembler.setFilterMacB(filterMacEnable ? mac : "", cbMacAddress.isChecked()));
        orderTasks.add(OrderTaskAssembler.setFilterNameB(filterNameEnable ? name : "", cbAdvName.isChecked()));
        orderTasks.add(OrderTaskAssembler.setFilterUUIDB(filterUUIDEnable ? uuid : "", cbIbeaconUuid.isChecked()));
        orderTasks.add(OrderTaskAssembler.setFilterMajorRangeB(
                filterMajorEnable ? 1 : 0,
                filterMajorEnable ? Integer.parseInt(majorMin) : 0,
                filterMajorEnable ? Integer.parseInt(majorMax) : 0,
                cbIbeaconMajor.isChecked()));
        orderTasks.add(OrderTaskAssembler.setFilterMinorRangeB(
                filterMinorEnable ? 1 : 0,
                filterMinorEnable ? Integer.parseInt(minorMin) : 0,
                filterMinorEnable ? Integer.parseInt(minorMax) : 0,
                cbIbeaconMinor.isChecked()));
        orderTasks.add(OrderTaskAssembler.setFilterRawDataB(filterRawAdvDataEnable ? filterRawDatas : null
                , cbRawAdvData.isChecked()));
        orderTasks.add(OrderTaskAssembler.setFilterSwitchB(filterSwitchEnable ? 1 : 0));
        LoRaLW001MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    private boolean isValid() {
        final String mac = etMacAddress.getText().toString();
        final String name = etAdvName.getText().toString();
        final String uuid = etIbeaconUuid.getText().toString();
        final String majorMin = etIbeaconMajorMin.getText().toString();
        final String majorMax = etIbeaconMajorMax.getText().toString();
        final String minorMin = etIbeaconMinorMin.getText().toString();
        final String minorMax = etIbeaconMinorMax.getText().toString();
        if (filterMacEnable) {
            if (TextUtils.isEmpty(mac))
                return false;
            int length = mac.length();
            if (length % 2 != 0)
                return false;
        }
        if (filterNameEnable) {
            if (TextUtils.isEmpty(name))
                return false;
        }
        if (filterUUIDEnable) {
            if (TextUtils.isEmpty(uuid))
                return false;
            int length = uuid.length();
            if (length % 2 != 0)
                return false;
        }
        if (filterMajorEnable) {
            if (TextUtils.isEmpty(majorMin))
                return false;
            if (Integer.parseInt(majorMin) > 65535)
                return false;
            if (TextUtils.isEmpty(majorMax))
                return false;
            if (Integer.parseInt(majorMax) > 65535)
                return false;
            if (Integer.parseInt(majorMin) > Integer.parseInt(majorMax))
                return false;

        }
        if (filterMinorEnable) {
            if (TextUtils.isEmpty(minorMin))
                return false;
            if (Integer.parseInt(minorMin) > 65535)
                return false;
            if (TextUtils.isEmpty(minorMax))
                return false;
            if (Integer.parseInt(minorMax) > 65535)
                return false;
            if (Integer.parseInt(minorMin) > Integer.parseInt(minorMax))
                return false;
        }
        filterRawDatas = new ArrayList<>();
        if (filterRawAdvDataEnable) {
            // 发送设置的过滤RawData
            int count = llRawDataFilter.getChildCount();
            if (count == 0)
                return false;

            for (int i = 0; i < count; i++) {
                View v = llRawDataFilter.getChildAt(i);
                EditText etDataType = v.findViewById(R.id.et_data_type);
                EditText etMin = v.findViewById(R.id.et_min);
                EditText etMax = v.findViewById(R.id.et_max);
                EditText etRawData = v.findViewById(R.id.et_raw_data);
                final String dataTypeStr = etDataType.getText().toString();
                final String minStr = etMin.getText().toString();
                final String maxStr = etMax.getText().toString();
                final String rawDataStr = etRawData.getText().toString();

                if (TextUtils.isEmpty(dataTypeStr))
                    return false;

                final int dataType = Integer.parseInt(dataTypeStr, 16);
                final DataTypeEnum dataTypeEnum = DataTypeEnum.fromDataType(dataType);
                if (dataTypeEnum == null)
                    return false;
                if (TextUtils.isEmpty(rawDataStr))
                    return false;
                int length = rawDataStr.length();
                if (length % 2 != 0)
                    return false;
                int min = 0;
                if (!TextUtils.isEmpty(minStr))
                    min = Integer.parseInt(minStr);
                int max = 0;
                if (!TextUtils.isEmpty(maxStr))
                    max = Integer.parseInt(maxStr);
                if (min == 0 && max != 0)
                    return false;
                if (min > 62)
                    return false;
                if (max > 62)
                    return false;
                if (max < min)
                    return false;
                if (min > 0) {
                    int interval = max - min;
                    if (length != ((interval + 1) * 2))
                        return false;
                }
                int rawDataLength = 3 + length / 2;
                StringBuffer rawData = new StringBuffer();
                rawData.append(MokoUtils.int2HexString(rawDataLength));
                rawData.append(MokoUtils.int2HexString(dataType));
                rawData.append(MokoUtils.int2HexString(min));
                rawData.append(MokoUtils.int2HexString(max));
                rawData.append(rawDataStr);
                filterRawDatas.add(rawData.toString());
            }
        }
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int rssi = progress - 127;
        tvRssiFilterValue.setText(String.format("%ddBm", rssi));
        tvRssiFilterTips.setText(getString(R.string.rssi_filter, rssi));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
