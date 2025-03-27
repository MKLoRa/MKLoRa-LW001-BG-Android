package com.moko.lw001.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moko.ble.lib.task.OrderTask;
import com.moko.lib.loraui.dialog.BottomDialog;
import com.moko.lw001.R;
import com.moko.lw001.activity.DeviceInfoActivity;
import com.moko.lw001.databinding.Lw001FragmentDeviceBinding;
import com.moko.support.lw001.LoRaLW001MokoSupport;
import com.moko.support.lw001.OrderTaskAssembler;

import java.util.ArrayList;

public class DeviceFragment extends Fragment {
    private static final String TAG = DeviceFragment.class.getSimpleName();
    private Lw001FragmentDeviceBinding mBind;
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
        mBind = Lw001FragmentDeviceBinding.inflate(inflater, container, false);
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
        if (activity.mDeviceType != 0x21) {
            mLowPowerPrompts.add("5%");
            mLowPowerPrompts.add("10%");
        } else {
            mLowPowerPrompts.add("10%");
            mLowPowerPrompts.add("20%");
            mLowPowerPrompts.add("30%");
            mLowPowerPrompts.add("40%");
            mLowPowerPrompts.add("50%");
        }
        return mBind.getRoot();
    }

    public void setTimeZone(int timeZone) {
        mSelectedTimeZone = timeZone + 12;
        mBind.tvTimeZone.setText(mTimeZones.get(mSelectedTimeZone));
    }

    public void showTimeZoneDialog() {
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mTimeZones, mSelectedTimeZone);
        dialog.setListener(value -> {
            mSelectedTimeZone = value;
            mBind.tvTimeZone.setText(mTimeZones.get(value));
            activity.showSyncingProgressDialog();
            LoRaLW001MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setTimeZone(value - 12));
        });
        dialog.show(activity.getSupportFragmentManager());
    }


    public void setShutdownPayload(int enable) {
        mShutdownPayloadEnable = enable == 1;
        mBind.ivShutdownPayload.setImageResource(mShutdownPayloadEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
    }

    public void setLowPower(int lowPower) {
        if ((lowPower & 1) == 1) {
            mSelectedLowPowerPrompt = 1;
        } else {
            mSelectedLowPowerPrompt = 0;
        }
        mBind.tvLowPowerPrompt.setText(mLowPowerPrompts.get(mSelectedLowPowerPrompt));
        mBind.tvLowPowerPromptTips.setText(activity.getString(R.string.low_power_prompt_tips, mLowPowerPrompts.get(mSelectedLowPowerPrompt)));
        if ((lowPower & 2) == 2) {
            mLowPowerPayloadEnable = true;
            mBind.ivLowPowerPayload.setImageResource(R.drawable.ic_checked);
        } else {
            mLowPowerPayloadEnable = false;
            mBind.ivLowPowerPayload.setImageResource(R.drawable.ic_unchecked);
        }
    }

    public void setLowPowerEnable(int enable) {
        mLowPowerPayloadEnable = enable == 1;
        mBind.ivLowPowerPayload.setImageResource(mLowPowerPayloadEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
    }

    public void setLowPowerPercent(int percent) {
        mSelectedLowPowerPrompt = percent;
        mBind.tvLowPowerPrompt.setText(mLowPowerPrompts.get(mSelectedLowPowerPrompt));
        mBind.tvLowPowerPromptTips.setText(activity.getString(R.string.low_power_prompt_tips, mLowPowerPrompts.get(mSelectedLowPowerPrompt)));
    }

    public void showLowPowerDialog() {
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mLowPowerPrompts, mSelectedLowPowerPrompt);
        dialog.setListener(value -> {
            mSelectedLowPowerPrompt = value;
            mBind.tvLowPowerPrompt.setText(mLowPowerPrompts.get(value));
            mBind.tvLowPowerPromptTips.setText(activity.getString(R.string.low_power_prompt_tips, mLowPowerPrompts.get(value)));
            activity.showSyncingProgressDialog();
            if (activity.mDeviceType != 0x21) {
                int lowPower = mSelectedLowPowerPrompt | (mLowPowerPayloadEnable ? 2 : 0);
                LoRaLW001MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setLowPower(lowPower));
            } else {
                ArrayList<OrderTask> orderTasks = new ArrayList<>();
                orderTasks.add(OrderTaskAssembler.setLowPowerPercent(mSelectedLowPowerPrompt));
                orderTasks.add(OrderTaskAssembler.getLowPowerPercent());
                LoRaLW001MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
            }
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
        if (activity.mDeviceType != 0x21) {
            int lowPower = mSelectedLowPowerPrompt | (mLowPowerPayloadEnable ? 2 : 0);
            ArrayList<OrderTask> orderTasks = new ArrayList<>();
            orderTasks.add(OrderTaskAssembler.setLowPower(lowPower));
            orderTasks.add(OrderTaskAssembler.getLowPower());
            LoRaLW001MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
        } else {
            ArrayList<OrderTask> orderTasks = new ArrayList<>();
            orderTasks.add(OrderTaskAssembler.setLowPowerEnable(mLowPowerPayloadEnable ? 1 : 0));
            orderTasks.add(OrderTaskAssembler.getLowPowerEnable());
            LoRaLW001MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
        }
    }
}
