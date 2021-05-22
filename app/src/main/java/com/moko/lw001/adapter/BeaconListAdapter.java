package com.moko.lw001.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.moko.lw001.R;
import com.moko.lw001.entity.BeaconInfo;

public class BeaconListAdapter extends BaseQuickAdapter<BeaconInfo, BaseViewHolder> {
    public BeaconListAdapter() {
        super(R.layout.lw001_list_item_device);
    }

    @Override
    protected void convert(BaseViewHolder helper, BeaconInfo item) {
        final String rssi = String.format("%ddBm", item.rssi);
        helper.setText(R.id.tv_rssi, rssi);
        final String name = TextUtils.isEmpty(item.name) ? "N/A" : item.name;
        helper.setText(R.id.tv_name, name);
        helper.setText(R.id.tv_mac, String.format("MAC:%s", item.mac));

        helper.setText(R.id.tv_battery, String.format("%d%%", item.battery));
        final String intervalTime = item.intervalTime == 0 ? "<->N/A" : String.format("<->%dms", item.intervalTime);
        helper.setText(R.id.tv_track_interval, intervalTime);
        helper.setText(R.id.tv_temp, String.format("%s℃", TextUtils.isEmpty(item.temp) ? "N/A" : item.temp));
        helper.setText(R.id.tv_humi, String.format("%s%%RH", TextUtils.isEmpty(item.humi) ? "N/A" : item.humi));
        helper.addOnClickListener(R.id.tv_connect);
    }
}
