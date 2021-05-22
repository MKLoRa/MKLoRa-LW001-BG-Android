package com.moko.lw001.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.moko.ble.lib.task.OrderTask;
import com.moko.lw001.R;
import com.moko.lw001.R2;
import com.moko.lw001.activity.DeviceInfoActivity;
import com.moko.support.lw001.LoRaLW001MokoSupport;
import com.moko.support.lw001.OrderTaskAssembler;

import java.util.ArrayList;

import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ScannerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = ScannerFragment.class.getSimpleName();
    @BindView(R2.id.cb_scan_switch)
    CheckBox cbScanSwitch;
    @BindView(R2.id.et_scan_window)
    EditText etScanWindow;
    @BindView(R2.id.cl_scan)
    ConstraintLayout clScan;
    @BindView(R2.id.cb_over_limit_indication)
    CheckBox cbOverLimitIndication;
    @BindView(R2.id.sb_over_limit_rssi)
    SeekBar sbOverLimitRssi;
    @BindView(R2.id.tv_over_limit_rssi_value)
    TextView tvOverLimitRssiValue;
    @BindView(R2.id.et_over_limit_mac_qty)
    EditText etOverLimitMacQty;
    @BindView(R2.id.et_over_limit_duration)
    EditText etOverLimitDuration;
    @BindView(R2.id.cl_over_limit)
    ConstraintLayout clOverLimit;


    private DeviceInfoActivity activity;

    public ScannerFragment() {
    }


    public static ScannerFragment newInstance() {
        ScannerFragment fragment = new ScannerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.lw001_fragment_scanner, container, false);
        ButterKnife.bind(this, view);
        activity = (DeviceInfoActivity) getActivity();
        sbOverLimitRssi.setOnSeekBarChangeListener(this);
        cbScanSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            clScan.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });
        cbOverLimitIndication.setOnCheckedChangeListener((buttonView, isChecked) -> {
            clOverLimit.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });
        return view;
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
    }

    public boolean isValid() {
        if (cbScanSwitch.isChecked()) {
            final String scanWindowStr = etScanWindow.getText().toString();
            if (TextUtils.isEmpty(scanWindowStr))
                return false;
            final int scanWindow = Integer.parseInt(scanWindowStr);
            if (scanWindow < 1 || scanWindow > 16)
                return false;
        }
        if (cbOverLimitIndication.isChecked()) {
            final String qtyStr = etOverLimitMacQty.getText().toString();
            if (TextUtils.isEmpty(qtyStr))
                return false;
            final int qty = Integer.parseInt(qtyStr);
            if (qty < 1 || qty > 255)
                return false;
            final String durationStr = etOverLimitDuration.getText().toString();
            if (TextUtils.isEmpty(durationStr))
                return false;
            final int duration = Integer.parseInt(durationStr);
            if (duration < 1 || duration > 600)
                return false;
        }
        return true;
    }

    public void saveParams() {
        ArrayList<OrderTask> orderTasks = new ArrayList<>();
        if (cbScanSwitch.isChecked()) {
            final String scanWindowStr = etScanWindow.getText().toString();
            final int scanWindow = Integer.parseInt(scanWindowStr);
            orderTasks.add(OrderTaskAssembler.setScanParams(scanWindow));
            orderTasks.add(OrderTaskAssembler.setScanEnable(1));
        } else {
            orderTasks.add(OrderTaskAssembler.setScanEnable(0));
        }
        if (cbOverLimitIndication.isChecked()) {
            final String qtyStr = etOverLimitMacQty.getText().toString();
            final String durationStr = etOverLimitDuration.getText().toString();
            final int qty = Integer.parseInt(qtyStr);
            final int duration = Integer.parseInt(durationStr);
            final int rssi = sbOverLimitRssi.getProgress() - 127;

            orderTasks.add(OrderTaskAssembler.setOverLimitRssi(rssi));
            orderTasks.add(OrderTaskAssembler.setOverLimitQty(qty));
            orderTasks.add(OrderTaskAssembler.setOverLimitDuration(duration));
            orderTasks.add(OrderTaskAssembler.setOverLimitEnable(1));
        } else {
            orderTasks.add(OrderTaskAssembler.setOverLimitEnable(0));
        }
        LoRaLW001MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    public void setScanEnable(int enable) {
        cbScanSwitch.setChecked(enable == 1);
    }

    public void setScanParams(int window) {
        etScanWindow.setText(String.valueOf(window));
    }

    public void setOverLimitEnable(int enable) {
        cbOverLimitIndication.setChecked(enable == 1);
    }


    public void setOverLimitRssi(int rssi) {
        int progress = rssi + 127;
        sbOverLimitRssi.setProgress(progress);
        tvOverLimitRssiValue.setText(String.format("%ddBm", rssi));
    }

    public void setOverLimitQty(int qty) {
        etOverLimitMacQty.setText(String.valueOf(qty));
    }

    public void setOverLimitDuration(int duration) {
        etOverLimitDuration.setText(String.valueOf(duration));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int value = progress - 127;
        tvOverLimitRssiValue.setText(String.format("%ddBm", value));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
