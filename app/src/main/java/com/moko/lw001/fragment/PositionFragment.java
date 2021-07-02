package com.moko.lw001.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.moko.lw001.R;
import com.moko.lw001.activity.DeviceInfoActivity;
import com.moko.support.lw001.LoRaLW001MokoSupport;
import com.moko.support.lw001.OrderTaskAssembler;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PositionFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = PositionFragment.class.getSimpleName();
    @BindView(R.id.cb_offline_fix)
    CheckBox cbOfflineFix;


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
        View view = inflater.inflate(R.layout.lw001_fragment_pos, container, false);
        ButterKnife.bind(this, view);
        activity = (DeviceInfoActivity) getActivity();
        cbOfflineFix.setOnCheckedChangeListener(this);
        return view;
    }

    public void setOfflineFix(int enable) {
        cbOfflineFix.setChecked(enable == 1);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        activity.showSyncingProgressDialog();
        LoRaLW001MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setOfflineLocation(isChecked ? 1 : 0));
    }
}
