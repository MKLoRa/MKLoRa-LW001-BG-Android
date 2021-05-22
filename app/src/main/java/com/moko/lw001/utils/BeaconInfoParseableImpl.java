package com.moko.lw001.utils;

import android.os.ParcelUuid;
import android.os.SystemClock;
import android.util.SparseArray;

import com.moko.ble.lib.utils.MokoUtils;
import com.moko.lw001.entity.BeaconInfo;
import com.moko.support.lw001.entity.DeviceInfo;
import com.moko.support.lw001.service.DeviceInfoParseable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import no.nordicsemi.android.support.v18.scanner.ScanRecord;
import no.nordicsemi.android.support.v18.scanner.ScanResult;

public class BeaconInfoParseableImpl implements DeviceInfoParseable<BeaconInfo> {
    private HashMap<String, BeaconInfo> beaconInfoHashMap;

    public BeaconInfoParseableImpl() {
        this.beaconInfoHashMap = new HashMap<>();
    }

    @Override
    public BeaconInfo parseDeviceInfo(DeviceInfo deviceInfo) {
        ScanResult result = deviceInfo.scanResult;
        ScanRecord record = result.getScanRecord();
        Map<ParcelUuid, byte[]> map = record.getServiceData();
        if (map == null || map.isEmpty())
            return null;
        SparseArray<byte[]> manufacturer = result.getScanRecord().getManufacturerSpecificData();
        if (manufacturer == null || manufacturer.size() == 0)
            return null;
        byte[] manufacturerSpecificDataByte = record.getManufacturerSpecificData(manufacturer.keyAt(0));
        if (manufacturerSpecificDataByte.length != 11)
            return null;
        int battery = 0;
        int deviceType = 0;
        String tempStr = "";
        String humiStr = "";
        Iterator iterator = map.keySet().iterator();
        if (iterator.hasNext()) {
            ParcelUuid parcelUuid = (ParcelUuid) iterator.next();
            if (parcelUuid.toString().startsWith("0000aa00")) {
                byte[] bytes = map.get(parcelUuid);
                if (bytes != null) {
                    deviceType = bytes[0] & 0xFF;
                }
            } else {
                return null;
            }
        }
        battery = manufacturerSpecificDataByte[6] & 0xFF;
        byte[] tempBytes = Arrays.copyOfRange(manufacturerSpecificDataByte, 7, 9);
        byte[] humiBytes = Arrays.copyOfRange(manufacturerSpecificDataByte, 9, 11);
        tempStr = MokoUtils.getDecimalFormat("#.##").format(MokoUtils.toIntSigned(tempBytes) * 0.01);
        humiStr = MokoUtils.getDecimalFormat("#.##").format(MokoUtils.toInt(humiBytes) * 0.01);
        BeaconInfo beaconInfo;
        if (beaconInfoHashMap.containsKey(deviceInfo.mac)) {
            beaconInfo = beaconInfoHashMap.get(deviceInfo.mac);
            beaconInfo.name = deviceInfo.name;
            beaconInfo.rssi = deviceInfo.rssi;
            beaconInfo.battery = battery;
            beaconInfo.deviceType = deviceType;
            beaconInfo.temp = tempStr;
            beaconInfo.humi = humiStr;
            long currentTime = SystemClock.elapsedRealtime();
            long intervalTime = currentTime - beaconInfo.scanTime;
            beaconInfo.intervalTime = intervalTime;
            beaconInfo.scanTime = currentTime;
        } else {
            beaconInfo = new BeaconInfo();
            beaconInfo.name = deviceInfo.name;
            beaconInfo.mac = deviceInfo.mac;
            beaconInfo.rssi = deviceInfo.rssi;
            beaconInfo.battery = battery;
            beaconInfo.deviceType = deviceType;
            beaconInfo.temp = tempStr;
            beaconInfo.humi = humiStr;
            beaconInfo.scanTime = SystemClock.elapsedRealtime();
            beaconInfoHashMap.put(deviceInfo.mac, beaconInfo);
        }

        return beaconInfo;
    }
}
