package com.moko.lw001.utils;

import android.os.ParcelUuid;
import android.os.SystemClock;
import android.util.SparseArray;

import com.moko.ble.lib.utils.MokoUtils;
import com.moko.lw001.entity.AdvInfo;
import com.moko.support.lw001.entity.DeviceInfo;
import com.moko.support.lw001.service.DeviceInfoParseable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import no.nordicsemi.android.support.v18.scanner.ScanRecord;
import no.nordicsemi.android.support.v18.scanner.ScanResult;

public class BeaconInfoParseableImpl implements DeviceInfoParseable<AdvInfo> {
    private HashMap<String, AdvInfo> advInfoHashMap;

    public BeaconInfoParseableImpl() {
        this.advInfoHashMap = new HashMap<>();
    }

    @Override
    public AdvInfo parseDeviceInfo(DeviceInfo deviceInfo) {
        ScanResult result = deviceInfo.scanResult;
        ScanRecord record = result.getScanRecord();
        Map<ParcelUuid, byte[]> map = record.getServiceData();
        if (map == null || map.isEmpty())
            return null;
        SparseArray<byte[]> manufacturer = result.getScanRecord().getManufacturerSpecificData();
        if (manufacturer == null || manufacturer.size() == 0)
            return null;
        byte[] manufacturerSpecificDataByte = record.getManufacturerSpecificData(manufacturer.keyAt(0));
        if (manufacturerSpecificDataByte.length != 21)
            return null;
        int battery = -1;
        int powerState = -1;
        int deviceType = -1;
        int txPower = 0;
        int measurePower = 0;
        String uuid = "";
        int major = 0;
        int minor = 0;
        byte[] uuidBytes = Arrays.copyOfRange(manufacturerSpecificDataByte, 0, 16);
        uuid = MokoUtils.bytesToHexString(uuidBytes).toLowerCase();
        StringBuffer sb = new StringBuffer(uuid);
        sb.insert(8, "-");
        sb.insert(13, "-");
        sb.insert(18, "-");
        sb.insert(23, "-");
        uuid = sb.toString();
        byte[] majorBytes = Arrays.copyOfRange(manufacturerSpecificDataByte, 16, 18);
        byte[] minorBytes = Arrays.copyOfRange(manufacturerSpecificDataByte, 18, 20);
        measurePower = manufacturerSpecificDataByte[20];
        major = MokoUtils.toInt(majorBytes);
        minor = MokoUtils.toInt(minorBytes);
        Iterator iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            ParcelUuid parcelUuid = (ParcelUuid) iterator.next();
            if (parcelUuid.toString().startsWith("0000aa02")) {
                byte[] bytes = map.get(parcelUuid);
                if (bytes != null) {
                    deviceType = bytes[0] & 0xFF;
                    txPower = bytes[1];
                    String binary = MokoUtils.hexString2binaryString(MokoUtils.byte2HexString(bytes[2]));
                    powerState = Integer.parseInt(binary.substring(5, 6));
                    battery = MokoUtils.toInt(Arrays.copyOfRange(bytes, 3, 5));
                }
            }
        }
        if (deviceType == -1)
            return null;

        AdvInfo advInfo;
        if (advInfoHashMap.containsKey(deviceInfo.mac)) {
            advInfo = advInfoHashMap.get(deviceInfo.mac);
            advInfo.name = deviceInfo.name;
            advInfo.rssi = deviceInfo.rssi;
            advInfo.battery = battery;
            advInfo.deviceType = deviceType;
            long currentTime = SystemClock.elapsedRealtime();
            long intervalTime = currentTime - advInfo.scanTime;
            advInfo.intervalTime = intervalTime;
            advInfo.scanTime = currentTime;
            advInfo.powerState = powerState;
            advInfo.txPower = txPower;
            advInfo.uuid = uuid;
            advInfo.measurePower = measurePower;
            advInfo.major = major;
            advInfo.minor = minor;
        } else {
            advInfo = new AdvInfo();
            advInfo.name = deviceInfo.name;
            advInfo.mac = deviceInfo.mac;
            advInfo.rssi = deviceInfo.rssi;
            advInfo.battery = battery;
            advInfo.powerState = powerState;
            advInfo.deviceType = deviceType;
            advInfo.scanTime = SystemClock.elapsedRealtime();
            advInfo.deviceType = deviceType;
            advInfo.txPower = txPower;
            advInfo.uuid = uuid;
            advInfo.measurePower = measurePower;
            advInfo.major = major;
            advInfo.minor = minor;
            advInfoHashMap.put(deviceInfo.mac, advInfo);
        }

        return advInfo;
    }
}
