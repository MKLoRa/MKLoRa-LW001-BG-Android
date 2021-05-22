package com.moko.support.lw001;

import com.moko.ble.lib.task.OrderTask;
import com.moko.support.lw001.entity.ParamsKeyEnum;
import com.moko.support.lw001.task.GetBatteryTask;
import com.moko.support.lw001.task.GetFirmwareRevisionTask;
import com.moko.support.lw001.task.GetHardwareRevisionTask;
import com.moko.support.lw001.task.GetManufacturerNameTask;
import com.moko.support.lw001.task.GetModelNumberTask;
import com.moko.support.lw001.task.GetSoftwareRevisionTask;
import com.moko.support.lw001.task.ParamsTask;
import com.moko.support.lw001.task.SetPasswordTask;

import java.util.ArrayList;

public class OrderTaskAssembler {
    ///////////////////////////////////////////////////////////////////////////
    // READ
    ///////////////////////////////////////////////////////////////////////////

    public static OrderTask getManufacturer() {
        GetManufacturerNameTask getManufacturerTask = new GetManufacturerNameTask();
        return getManufacturerTask;
    }

    public static OrderTask getDeviceModel() {
        GetModelNumberTask getDeviceModelTask = new GetModelNumberTask();
        return getDeviceModelTask;
    }

    public static OrderTask getHardwareVersion() {
        GetHardwareRevisionTask getHardwareVersionTask = new GetHardwareRevisionTask();
        return getHardwareVersionTask;
    }

    public static OrderTask getFirmwareVersion() {
        GetFirmwareRevisionTask getFirmwareVersionTask = new GetFirmwareRevisionTask();
        return getFirmwareVersionTask;
    }

    public static OrderTask getSoftwareVersion() {
        GetSoftwareRevisionTask getSoftwareVersionTask = new GetSoftwareRevisionTask();
        return getSoftwareVersionTask;
    }

    public static OrderTask getBattery() {
        GetBatteryTask getBatteryTask = new GetBatteryTask();
        return getBatteryTask;
    }

    public static OrderTask getMacAddress() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_DEVICE_MAC);
        return task;
    }

    public static OrderTask getAdvInterval() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_ADV_INTERVAL);
        return task;
    }

    public static OrderTask getAdvName() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_ADV_NAME);
        return task;
    }

    public static OrderTask getScanEnable() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_SCAN_ENABLE);
        return task;
    }

    public static OrderTask getScanParams() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_SCAN_PARAMS);
        return task;
    }

    public static OrderTask getOverLimitEnable() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_OVER_LIMIT_ENABLE);
        return task;
    }

    public static OrderTask getOverLimitRssi() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_OVER_LIMIT_RSSI);
        return task;
    }

    public static OrderTask getOverLimitQty() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_OVER_LIMIT_QTY);
        return task;
    }

    public static OrderTask getOverLimitDuration() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_OVER_LIMIT_DURATION);
        return task;
    }

    public static OrderTask getLoRaConnectable() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_NETWORK_STATUS);
        return task;
    }

    public static OrderTask getTamperDetection() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_TAMPER_DETECTION);
        return task;
    }

    public static OrderTask getDefaultPowerStatus() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_POWER_STATUS);
        return task;
    }


    public static OrderTask getDeviceInfoInterval() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_DEVICE_INFO_INTERVAL);
        return task;
    }

    public static OrderTask getDataReportInterval() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_DATA_REPORT_INTERVAL);
        return task;
    }

    public static OrderTask getUplinkDataType() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_UPLINK_DATA_TYPE);
        return task;
    }

    public static OrderTask getUplinkDataMaxLength() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_UPLINK_DATA_MAX_LENGTH);
        return task;
    }

    public static OrderTask getUplinkDataContent() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_UPLINK_DATA_CONTENT);
        return task;
    }

    public static OrderTask getFilterSwitchA() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_TRACKING_FILTER_SWITCH_A);
        return task;
    }

    public static OrderTask getFilterRssiA() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_TRACKING_FILTER_RSSI_A);
        return task;
    }

    public static OrderTask getFilterMacA() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_TRACKING_FILTER_MAC_A);
        return task;
    }

    public static OrderTask getFilterNameA() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_TRACKING_FILTER_ADV_NAME_A);
        return task;
    }

    public static OrderTask getFilterUUIDA() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_TRACKING_FILTER_UUID_A);
        return task;
    }

    public static OrderTask getFilterAdvRawDataA() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_TRACKING_FILTER_ADV_RAW_DATA_A);
        return task;
    }

    public static OrderTask getFilterMajorRangeA() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_TRACKING_FILTER_MAJOR_RANGE_A);
        return task;
    }

    public static OrderTask getFilterMinorRangeA() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_TRACKING_FILTER_MINOR_RANGE_A);
        return task;
    }

    public static OrderTask getFilterSwitchB() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_TRACKING_FILTER_SWITCH_B);
        return task;
    }

    public static OrderTask getFilterRssiB() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_TRACKING_FILTER_RSSI_B);
        return task;
    }

    public static OrderTask getFilterMacB() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_TRACKING_FILTER_MAC_B);
        return task;
    }

    public static OrderTask getFilterNameB() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_TRACKING_FILTER_ADV_NAME_B);
        return task;
    }

    public static OrderTask getFilterUUIDB() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_TRACKING_FILTER_UUID_B);
        return task;
    }

    public static OrderTask getFilterAdvRawDataB() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_TRACKING_FILTER_ADV_RAW_DATA_B);
        return task;
    }

    public static OrderTask getFilterMajorRangeB() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_TRACKING_FILTER_MAJOR_RANGE_B);
        return task;
    }

    public static OrderTask getFilterMinorRangeB() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_TRACKING_FILTER_MINOR_RANGE_B);
        return task;
    }

    public static OrderTask getFilterABRelation() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_TRACKING_FILTER_A_B_RELATION);
        return task;
    }

    public static OrderTask getFilterRepeat() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_TRACKING_FILTER_REPEAT);
        return task;
    }

    public static OrderTask getLoraMode() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LORA_MODE);
        return task;
    }

    public static OrderTask getLoraClassType() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LORA_CLASS_TYPE);
        return task;
    }

    public static OrderTask getLoraDevEUI() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LORA_DEV_EUI);
        return task;
    }

    public static OrderTask getLoraAppEUI() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LORA_APP_EUI);
        return task;
    }

    public static OrderTask getLoraAppKey() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LORA_APP_KEY);
        return task;
    }

    public static OrderTask getLoraDevAddr() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LORA_DEV_ADDR);
        return task;
    }

    public static OrderTask getLoraAppSKey() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LORA_APP_SKEY);
        return task;
    }

    public static OrderTask getLoraNwkSKey() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LORA_NWK_SKEY);
        return task;
    }

    public static OrderTask getLoraRegion() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LORA_REGION);
        return task;
    }

    public static OrderTask getLoraMessageType() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LORA_MESSAGE_TYPE);
        return task;
    }

    public static OrderTask getLoraCH() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LORA_CH);
        return task;
    }

    public static OrderTask getLoraDutyCycleEnable() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LORA_DUTY_CYCLE_ENABLE);
        return task;
    }

    public static OrderTask getLoraDR() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LORA_DR);
        return task;
    }

    public static OrderTask getLoraADR() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LORA_ADR);
        return task;
    }

    public static OrderTask getLoraUplinkDellTime() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LORA_UPLINK_DELL_TIME);
        return task;
    }

    public static OrderTask getTimeSyncInterval() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_TIME_SYNC_INTERVAL);
        return task;
    }

    public static OrderTask getNetworkInterval() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_NETWORK_CHECK_INTERVAL);
        return task;
    }

    public static OrderTask getMulticastEnable() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_MULTICAST_ENABLE);
        return task;
    }

    public static OrderTask getMulticastAddr() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_MULTICAST_ADDR);
        return task;
    }

    public static OrderTask getMulticastAppSKey() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_MULTICAST_APPSKEY);
        return task;
    }

    public static OrderTask getMulticastNwkSkey() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_MULTICAST_NWKSKEY);
        return task;
    }

    ///////////////////////////////////////////////////////////////////////////
    // WRITE
    ///////////////////////////////////////////////////////////////////////////

    public static ParamsTask setWriteConfig(ParamsKeyEnum configKeyEnum) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setData(configKeyEnum);
        return writeConfigTask;
    }

    public static OrderTask setPassword(String password) {
        SetPasswordTask setPasswordTask = new SetPasswordTask();
        setPasswordTask.setData(password);
        return setPasswordTask;
    }

    public static ParamsTask setTime() {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setTime();
        return writeConfigTask;
    }

    public static OrderTask setDeviceName(String deviceName) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setAdvName(deviceName);
        return writeConfigTask;
    }

    public static OrderTask setAdvInterval(int advInterval) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setAdvInterval(advInterval);
        return writeConfigTask;
    }

    public static OrderTask setDeviceInfoInterval(int interval) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setDeviceInfoInterval(interval);
        return writeConfigTask;
    }

    public static OrderTask setDataReportInterval(int interval) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setDataReportInterval(interval);
        return writeConfigTask;
    }

    public static OrderTask setUplinkDataType(int type) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setUplinkDataType(type);
        return writeConfigTask;
    }

    public static OrderTask setUplinkDataMaxLength(int maxLength) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setUplinkDataMaxLength(maxLength);
        return writeConfigTask;
    }

    public static OrderTask setUplinkDataContent(int content) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setUplinkDataContent(content);
        return writeConfigTask;
    }

    public static OrderTask changePassword(String password) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.changePassword(password);
        return writeConfigTask;
    }

    public static OrderTask setReset() {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.reset();
        return writeConfigTask;
    }

    public static OrderTask setScanEnable(int enable) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setScanEnable(enable);
        return writeConfigTask;
    }

    public static OrderTask setScanParams(int window) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setScanParams(window);
        return writeConfigTask;
    }

    public static OrderTask setOverLimitEnable(int enable) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setOverLimitEnable(enable);
        return writeConfigTask;
    }

    public static OrderTask setOverLimitRssi(int rssi) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setOverLimitRssi(rssi);
        return writeConfigTask;
    }

    public static OrderTask setOverLimitQty(int qty) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setOverLimitQty(qty);
        return writeConfigTask;
    }

    public static OrderTask setOverLimitDuration(int duration) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setOverLimitDuration(duration);
        return writeConfigTask;
    }

    public static OrderTask setFilterRssiA(int rssi) {
        ParamsTask task = new ParamsTask();
        task.setFilterRssiA(rssi);
        return task;
    }

    public static OrderTask setFilterMacA(String mac, boolean isReverse) {
        ParamsTask task = new ParamsTask();
        task.setFilterMacA(mac, isReverse);
        return task;
    }

    public static OrderTask setFilterNameA(String name, boolean isReverse) {
        ParamsTask task = new ParamsTask();
        task.setFilterNameA(name, isReverse);
        return task;
    }

    public static OrderTask setFilterUUIDA(String uuid, boolean isReverse) {
        ParamsTask task = new ParamsTask();
        task.setFilterUUIDA(uuid, isReverse);
        return task;
    }

    public static OrderTask setFilterAdvRawDataA(ArrayList<String> filterRawDatas, boolean isReverse) {
        ParamsTask task = new ParamsTask();
        task.setFilterRawDataA(filterRawDatas, isReverse);
        return task;
    }

    public static OrderTask setFilterMajorRangeA(int enable, int majorMin, int majorMax, boolean isReverse) {
        ParamsTask task = new ParamsTask();
        task.setFilterMajorRangeA(enable, majorMin, majorMax, isReverse);
        return task;
    }

    public static OrderTask setFilterMinorRangeA(int enable, int majorMin, int majorMax, boolean isReverse) {
        ParamsTask task = new ParamsTask();
        task.setFilterMinorRangeA(enable, majorMin, majorMax, isReverse);
        return task;
    }

    public static OrderTask setFilterSwitchA(int enable) {
        ParamsTask task = new ParamsTask();
        task.setFilterSwitchA(enable);
        return task;
    }

    public static OrderTask setFilterRssiB(int rssi) {
        ParamsTask task = new ParamsTask();
        task.setFilterRssiB(rssi);
        return task;
    }

    public static OrderTask setFilterMacB(String mac, boolean isReverse) {
        ParamsTask task = new ParamsTask();
        task.setFilterMacB(mac, isReverse);
        return task;
    }

    public static OrderTask setFilterNameB(String name, boolean isReverse) {
        ParamsTask task = new ParamsTask();
        task.setFilterNameB(name, isReverse);
        return task;
    }

    public static OrderTask setFilterUUIDB(String uuid, boolean isReverse) {
        ParamsTask task = new ParamsTask();
        task.setFilterUUIDB(uuid, isReverse);
        return task;
    }

    public static OrderTask setFilterAdvRawDataB(ArrayList<String> filterRawDatas, boolean isReverse) {
        ParamsTask task = new ParamsTask();
        task.setFilterRawDataB(filterRawDatas, isReverse);
        return task;
    }

    public static OrderTask setFilterMajorRangeB(int enable, int majorMin, int majorMax, boolean isReverse) {
        ParamsTask task = new ParamsTask();
        task.setFilterMajorRangeB(enable, majorMin, majorMax, isReverse);
        return task;
    }

    public static OrderTask setFilterMinorRangeB(int enable, int majorMin, int majorMax, boolean isReverse) {
        ParamsTask task = new ParamsTask();
        task.setFilterMinorRangeB(enable, majorMin, majorMax, isReverse);
        return task;
    }

    public static OrderTask setFilterSwitchB(int enable) {
        ParamsTask task = new ParamsTask();
        task.setFilterSwitchB(enable);
        return task;
    }

    public static OrderTask setFilterABRelation(int relation) {
        ParamsTask task = new ParamsTask();
        task.setFilterABRelation(relation);
        return task;
    }

    public static OrderTask setFilterRepeat(int relation) {
        ParamsTask task = new ParamsTask();
        task.setFilterRepeat(relation);
        return task;
    }

    public static OrderTask setLoraDevAddr(String devAddr) {
        ParamsTask task = new ParamsTask();
        task.setLoraDevAddr(devAddr);
        return task;
    }

    public static OrderTask setLoraNwkSKey(String nwkSKey) {
        ParamsTask task = new ParamsTask();
        task.setLoraNwkSKey(nwkSKey);
        return task;
    }

    public static OrderTask setLoraAppSKey(String appSKey) {
        ParamsTask task = new ParamsTask();
        task.setLoraAppSKey(appSKey);
        return task;
    }

    public static OrderTask setLoraAppEui(String appEui) {
        ParamsTask task = new ParamsTask();
        task.setLoraAppEui(appEui);
        return task;
    }

    public static OrderTask setLoraDevEui(String devEui) {
        ParamsTask task = new ParamsTask();
        task.setLoraDevEui(devEui);
        return task;
    }

    public static OrderTask setLoraAppKey(String appKey) {
        ParamsTask task = new ParamsTask();
        task.setLoraAppKey(appKey);
        return task;
    }

    public static OrderTask setLoraUploadMode(int mode) {
        ParamsTask task = new ParamsTask();
        task.setLoraUploadMode(mode);
        return task;
    }

    public static OrderTask setLoraMessageType(int type) {
        ParamsTask task = new ParamsTask();
        task.setLoraMessageType(type);
        return task;
    }

    public static OrderTask setLoraClassType(int type) {
        ParamsTask task = new ParamsTask();
        task.setLoraClassType(type);
        return task;
    }

    public static OrderTask setLoraRegion(int region) {
        ParamsTask task = new ParamsTask();
        task.setLoraRegion(region);
        return task;
    }

    public static OrderTask setLoraCH(int ch1, int ch2) {
        ParamsTask task = new ParamsTask();
        task.setLoraCH(ch1, ch2);
        return task;
    }

    public static OrderTask setLoraDutyCycleEnable(int enable) {
        ParamsTask task = new ParamsTask();
        task.setLoraDutyCycleEnable(enable);
        return task;
    }

    public static OrderTask setLoraDR(int dr1) {
        ParamsTask task = new ParamsTask();
        task.setLoraDR(dr1);
        return task;
    }

    public static OrderTask setLoraADR(int adr) {
        ParamsTask task = new ParamsTask();
        task.setLoraADR(adr);
        return task;
    }

    public static OrderTask setLoraUplinkDellTime(int uplinkDellTime) {
        ParamsTask task = new ParamsTask();
        task.setLoraUplinkDellTime(uplinkDellTime);
        return task;
    }

    public static OrderTask setTimeSyncInterval(int timeSyncInterval) {
        ParamsTask task = new ParamsTask();
        task.setTimeSyncInterval(timeSyncInterval);
        return task;
    }

    public static OrderTask setNetworkCheckInterval(int interval) {
        ParamsTask task = new ParamsTask();
        task.setNetworkCheckInterval(interval);
        return task;
    }

    public static OrderTask setMulticastEnable(int enable) {
        ParamsTask task = new ParamsTask();
        task.setMulticastEnable(enable);
        return task;
    }

    public static OrderTask setMulticastAddr(String addr) {
        ParamsTask task = new ParamsTask();
        task.setMulticastAddr(addr);
        return task;
    }

    public static OrderTask setMulticastAppSKey(String appSkey) {
        ParamsTask task = new ParamsTask();
        task.setMulticastAppSKey(appSkey);
        return task;
    }

    public static OrderTask setMulticastNwkSKey(String nwkSkey) {
        ParamsTask task = new ParamsTask();
        task.setMulticastNwkSKey(nwkSkey);
        return task;
    }

    public static OrderTask setPowerStatus(int status) {
        ParamsTask task = new ParamsTask();
        task.setPowerStatus(status);
        return task;
    }

    public static OrderTask setTamperDetection(int enable, int triggerSensitivity) {
        ParamsTask task = new ParamsTask();
        task.setTamperDetection(enable, triggerSensitivity);
        return task;
    }

    public static OrderTask readStorageData(int time) {
        ParamsTask task = new ParamsTask();
        task.readStorageData(time);
        return task;
    }

    public static OrderTask setSyncEnable(int enable) {
        ParamsTask task = new ParamsTask();
        task.setSyncEnable(enable);
        return task;
    }

    public static OrderTask clearStorageData() {
        ParamsTask task = new ParamsTask();
        task.clearStorageData();
        return task;
    }

}
