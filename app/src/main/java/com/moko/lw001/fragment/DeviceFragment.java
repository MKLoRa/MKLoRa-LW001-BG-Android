package com.moko.lw001.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moko.lw001.R;
import com.moko.lw001.R2;
import com.moko.lw001.activity.DeviceInfoActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceFragment extends Fragment {
    private static final String TAG = DeviceFragment.class.getSimpleName();
    @BindView(R2.id.tv_battery_voltage)
    TextView tvBatteryVoltage;
    @BindView(R2.id.tv_mac_address)
    TextView tvMacAddress;
    @BindView(R2.id.tv_product_model)
    TextView tvProductModel;
    @BindView(R2.id.tv_software_version)
    TextView tvSoftwareVersion;
    @BindView(R2.id.tv_firmware_version)
    TextView tvFirmwareVersion;
    @BindView(R2.id.tv_hardware_version)
    TextView tvHardwareVersion;
    @BindView(R2.id.tv_manufacture)
    TextView tvManufacture;


    private DeviceInfoActivity activity;

    public DeviceFragment() {
    }


    public static DeviceFragment newInstance() {
        DeviceFragment fragment = new DeviceFragment();
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
        View view = inflater.inflate(R.layout.lw001_fragment_device, container, false);
        ButterKnife.bind(this, view);
        activity = (DeviceInfoActivity) getActivity();
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

    public void setBatteryValtage(int battery) {
        tvBatteryVoltage.setText(String.format("%d%%", battery));
    }

    public void setMacAddress(String macAddress) {
        tvMacAddress.setText(macAddress);
    }

    public void setProductModel(String productModel) {
        tvProductModel.setText(productModel);
    }

    public void setSoftwareVersion(String softwareVersion) {
        tvSoftwareVersion.setText(softwareVersion);
    }

    public void setFirmwareVersion(String firmwareVersion) {
        tvFirmwareVersion.setText(firmwareVersion);
    }

    public void setHardwareVersion(String hardwareVersion) {
        tvHardwareVersion.setText(hardwareVersion);
    }

    public void setManufacture(String manufacture) {
        tvManufacture.setText(manufacture);
    }
}
