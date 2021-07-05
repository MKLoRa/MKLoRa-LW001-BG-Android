package com.moko.lw001.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moko.ble.lib.task.OrderTask;
import com.moko.lw001.R;
import com.moko.lw001.R2;
import com.moko.lw001.activity.DeviceInfoActivity;
import com.moko.lw001.dialog.BottomDialog;
import com.moko.support.lw001.LoRaLW001MokoSupport;
import com.moko.support.lw001.OrderTaskAssembler;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceFragment extends Fragment {
    private static final String TAG = DeviceFragment.class.getSimpleName();
    @BindView(R2.id.tv_time_zone)
    TextView tvTimeZone;
    @BindView(R2.id.iv_shutdown_payload)
    ImageView ivShutdownPayload;
    @BindView(R2.id.iv_low_power_payload)
    ImageView ivLowPowerPayload;
    @BindView(R2.id.tv_low_power_prompt)
    TextView tvLowPowerPrompt;
    @BindView(R2.id.tv_low_power_prompt_tips)
    TextView tvLowPowerPromptTips;
    @BindView(R2.id.iv_power_off)
    ImageView ivPowerOff;
    private ArrayList<String> mTimeZones;
    private int mSelectedTimeZone;
    private ArrayList<String> mLowPowerPrompts;
    private int mSelectedLowPowerPrompt;
    private boolean mShutdownPayloadEnable;
    private boolean mLowPowerPayloadEnable;


    private DeviceInfoActivity activity;

    public DeviceFragment() {
    }


    public static DeviceFragment newInstance() {
        DeviceFragment fragment = new DeviceFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.lw001_fragment_device, container, false);
        ButterKnife.bind(this, view);
        activity = (DeviceInfoActivity) getActivity();
        mTimeZones = new ArrayList<>();
        for (int i = -12; i < 13; i++) {
            if (i < 0) {
                mTimeZones.add(String.format("UTC%d", i));
            } else if (i == 0) {
                mTimeZones.add("UTC");
            } else {
                mTimeZones.add(String.format("UTC+%d", i));
            }
        }
        mLowPowerPrompts = new ArrayList<>();
        mLowPowerPrompts.add("5%");
        mLowPowerPrompts.add("10%");
        return view;
    }

    public void setTimeZone(int timeZone) {
        mSelectedTimeZone = timeZone + 12;
        tvTimeZone.setText(mTimeZones.get(mSelectedTimeZone));
    }

    public void showTimeZoneDialog() {
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mTimeZones, mSelectedTimeZone);
        dialog.setListener(value -> {
            mSelectedTimeZone = value;
            tvTimeZone.setText(mTimeZones.get(value));
            activity.showSyncingProgressDialog();
            LoRaLW001MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setTimeZone(value - 12));
        });
        dialog.show(activity.getSupportFragmentManager());
    }


    public void setShutdownPayload(int enable) {
        mShutdownPayloadEnable = enable == 1;
        ivShutdownPayload.setImageResource(mShutdownPayloadEnable ? R.drawable.lw001_ic_checked : R.drawable.lw001_ic_unchecked);
    }

    public void setLowPower(int lowPower) {
        if ((lowPower & 1) == 1) {
            mSelectedLowPowerPrompt = 1;
        } else {
            mSelectedLowPowerPrompt = 0;
        }
        tvLowPowerPrompt.setText(mLowPowerPrompts.get(mSelectedLowPowerPrompt));
        tvLowPowerPromptTips.setText(getString(R.string.low_power_prompt_tips, mLowPowerPrompts.get(mSelectedLowPowerPrompt)));
        if ((lowPower & 2) == 2) {
            mLowPowerPayloadEnable = true;
            ivLowPowerPayload.setImageResource(R.drawable.lw001_ic_checked);
        } else {
            mLowPowerPayloadEnable = false;
            ivLowPowerPayload.setImageResource(R.drawable.lw001_ic_unchecked);
        }
    }

    public void showLowPowerDialog() {
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mLowPowerPrompts, mSelectedLowPowerPrompt);
        dialog.setListener(value -> {
            mSelectedLowPowerPrompt = value;
            tvLowPowerPrompt.setText(mLowPowerPrompts.get(value));
            tvLowPowerPromptTips.setText(getString(R.string.low_power_prompt_tips, mLowPowerPrompts.get(value)));
            int lowPower = mSelectedLowPowerPrompt | (mLowPowerPayloadEnable ? 2 : 0);
            activity.showSyncingProgressDialog();
            LoRaLW001MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setLowPower(lowPower));
        });
        dialog.show(activity.getSupportFragmentManager());

    }

    public void changeShutdownPayload() {
        mShutdownPayloadEnable = !mShutdownPayloadEnable;
        activity.showSyncingProgressDialog();
        ArrayList<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setShutdownInfoReport(mShutdownPayloadEnable ? 1 : 0));
        orderTasks.add(OrderTaskAssembler.getShutdownInfoReport());
        LoRaLW001MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    public void changeLowPowerPayload() {
        mLowPowerPayloadEnable = !mLowPowerPayloadEnable;
        activity.showSyncingProgressDialog();
        int lowPower = mSelectedLowPowerPrompt | (mLowPowerPayloadEnable ? 2 : 0);
        ArrayList<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setLowPower(lowPower));
        orderTasks.add(OrderTaskAssembler.getLowPower());
        LoRaLW001MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }
}
