package com.moko.lw001.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moko.ble.lib.task.OrderTask;
import com.moko.lw001.R;
import com.moko.lw001.activity.DeviceInfoActivity;
import com.moko.lw001.databinding.Lw001FragmentPosBinding;
import com.moko.support.lw001.LoRaLW001MokoSupport;
import com.moko.support.lw001.OrderTaskAssembler;

import java.util.ArrayList;

public class PositionFragment extends Fragment {
    private static final String TAG = PositionFragment.class.getSimpleName();
    private Lw001FragmentPosBinding mBind;
    private boolean mOfflineFixEnable;

    private DeviceInfoActivity activity;

    public PositionFragment() {
    }


    public static PositionFragment newInstance() {
        PositionFragment fragment = new PositionFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        mBind = Lw001FragmentPosBinding.inflate(inflater, container, false);
        activity = (DeviceInfoActivity) getActivity();
        return mBind.getRoot();
    }

    public void setOfflineFix(int enable) {
        mOfflineFixEnable = enable == 1;
        mBind.ivOfflineFix.setImageResource(mOfflineFixEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
    }

    public void changeOfflineFix() {
        mOfflineFixEnable = !mOfflineFixEnable;
        activity.showSyncingProgressDialog();
        ArrayList<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setOfflineLocation(mOfflineFixEnable ? 1 : 0));
        orderTasks.add(OrderTaskAssembler.getOfflineLocation());
        LoRaLW001MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }
}
