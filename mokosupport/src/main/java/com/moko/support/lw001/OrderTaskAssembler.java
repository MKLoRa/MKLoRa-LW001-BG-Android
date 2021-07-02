package com.moko.support.lw001;

import com.moko.ble.lib.task.OrderTask;
import com.moko.support.lw001.entity.ParamsKeyEnum;
import com.moko.support.lw001.task.GetFirmwareRevisionTask;
import com.moko.support.lw001.task.GetHardwareRevisionTask;
import com.moko.support.lw001.task.GetManufacturerNameTask;
import com.moko.support.lw001.task.GetModelNumberTask;
import com.moko.support.lw001.task.GetSoftwareRevisionTask;
import com.moko.support.lw001.task.ParamsReadTask;
import com.moko.support.lw001.task.ParamsWriteTask;
import com.moko.support.lw001.task.SetPasswordTask;

import java.util.ArrayList;

import androidx.annotation.IntRange;

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
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_BATTERY_POWER);
        return task;
    }

    public static OrderTask getMacAddress() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_CHIP_MAC);
        return task;
    }

    public static OrderTask getTimeZone() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_TIME_ZONE);
        return task;
    }

    public static OrderTask getWorkMode() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_WORK_MODE);
        return task;
    }

    public static OrderTask getPowerStatus() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_POWER_STATUS);
        return task;
    }

    public static OrderTask getHeartBeatInterval() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_HEARTBEAT_INTERVAL);
        return task;
    }

    public static OrderTask getReedSwitch() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_REED_SWITCH);
        return task;
    }

    public static OrderTask getShutdownInfoReport() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_SHUTDOWN_INFO_REPORT);
        return task;
    }

    public static OrderTask getOfflineLocation() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_OFFLINE_LOCATION);
        return task;
    }

    public static OrderTask getLowPower() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LOW_POWER);
        return task;
    }

    public static OrderTask getIndicatorLight() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_INDICATOR_LIGHT);
        return task;
    }

    public static OrderTask getPeriodicPosStrategy() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_PERIODIC_MODE_POS_STRATEGY);
        return task;
    }

    public static OrderTask getPeriodicReportInterval() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_PERIODIC_MODE_REPORT_INTERVAL);
        return task;
    }

    public static OrderTask getTimePosStrategy() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_TIME_MODE_POS_STRATEGY);
        return task;
    }

    public static OrderTask getTimePosReportPoints() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_TIME_MODE_REPORT_TIME_POINT);
        return task;
    }

    public static OrderTask getMotionModeEvent() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_MOTION_MODE_EVENT);
        return task;
    }

    public static OrderTask getMotionModeStartNumber() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_MOTION_MODE_START_NUMBER);
        return task;
    }

    public static OrderTask getMotionStartPosStrategy() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_MOTION_MODE_START_POS_STRATEGY);
        return task;
    }

    public static OrderTask getMotionTripInterval() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_MOTION_MODE_TRIP_REPORT_INTERVAL);
        return task;
    }


    public static OrderTask getMotionTripPosStrategy() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_MOTION_MODE_TRIP_POS_STRATEGY);
        return task;
    }

    public static OrderTask getMotionEndTimeout() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_MOTION_MODE_END_TIMEOUT);
        return task;
    }

    public static OrderTask getMotionEndNumber() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_MOTION_MODE_END_NUMBER);
        return task;
    }

    public static OrderTask getMotionEndInterval() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_MOTION_MODE_END_REPORT_INTERVAL);
        return task;
    }

    public static OrderTask getMotionEndPosStrategy() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_MOTION_MODE_END_POS_STRATEGY);
        return task;
    }

    public static OrderTask getDownLinkPosStrategy() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_DOWN_LINK_POS_STRATEGY);
        return task;
    }

    public static OrderTask getWifiPosNumber() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_WIFI_POS_NUMBER);
        return task;
    }

    public static OrderTask getWifiPosBSSIDNumber() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_WIFI_POS_BSSID_NUMBER);
        return task;
    }

    public static OrderTask getBlePosTimeout() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_BLE_POS_TIMEOUT);
        return task;
    }

    public static OrderTask getBlePosNumber() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_BLE_POS_MAC_NUMBER);
        return task;
    }

    public static OrderTask getFilterABRelation() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_FILTER_A_B_RELATION);
        return task;
    }

    public static OrderTask getFilterSwitchA() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_FILTER_SWITCH_A);
        return task;
    }

    public static OrderTask getFilterNameA() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_FILTER_ADV_NAME_A);
        return task;
    }


    public static OrderTask getFilterMacA() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_FILTER_MAC_A);
        return task;
    }

    public static OrderTask getFilterMajorRangeA() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_FILTER_MAJOR_RANGE_A);
        return task;
    }

    public static OrderTask getFilterMinorRangeA() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_FILTER_MINOR_RANGE_A);
        return task;
    }

    public static OrderTask getFilterRawDataA() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_FILTER_ADV_RAW_DATA_A);
        return task;
    }

    public static OrderTask getFilterUUIDA() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_FILTER_UUID_A);
        return task;
    }

    public static OrderTask getFilterRssiA() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_FILTER_RSSI_A);
        return task;
    }

    public static OrderTask getFilterPHYA() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_FILTER_PHY_A);
        return task;
    }

    public static OrderTask getFilterSwitchB() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_FILTER_SWITCH_B);
        return task;
    }

    public static OrderTask getFilterNameB() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_FILTER_ADV_NAME_B);
        return task;
    }

    public static OrderTask getFilterMacB() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_FILTER_MAC_B);
        return task;
    }

    public static OrderTask getFilterMajorRangeB() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_FILTER_MAJOR_RANGE_B);
        return task;
    }

    public static OrderTask getFilterMinorRangeB() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_FILTER_MINOR_RANGE_B);
        return task;
    }

    public static OrderTask getFilterRawDataB() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_FILTER_ADV_RAW_DATA_B);
        return task;
    }

    public static OrderTask getFilterUUIDB() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_FILTER_UUID_B);
        return task;
    }

    public static OrderTask getFilterRssiB() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_FILTER_RSSI_B);
        return task;
    }


    public static OrderTask getFilterPHYB() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_FILTER_PHY_B);
        return task;
    }


    public static OrderTask getGPSColdStartTimeout() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_GPS_COLD_START_TIMEOUT);
        return task;
    }

    public static OrderTask getGPSCoarseAccuracyMask() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_GPS_COARSE_ACCURACY_MASK);
        return task;
    }

    public static OrderTask getGPSFineAccuracyMask() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_GPS_FINE_ACCURACY_MASK);
        return task;
    }

    public static OrderTask getGPSCoarseTimeout() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_GPS_COARSE_TIMEOUT);
        return task;
    }

    public static OrderTask getGPSFineTimeout() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_GPS_FINE_TIMEOUT);
        return task;
    }

    public static OrderTask getGPSPDOPLimit() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_GPS_PDOP_LIMIT);
        return task;
    }

    public static OrderTask getGPSFixMode() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_GPS_FIX_MODE);
        return task;
    }

    public static OrderTask getGPSModel() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_GPS_MODEL);
        return task;
    }

    public static OrderTask getGPSTimeBudget() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_GPS_TIME_BUDGET);
        return task;
    }

    public static OrderTask getGPSAutonomousAiding() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_GPS_AUTONOMOUS_AIDING);
        return task;
    }

    public static OrderTask getGPSAidingAccuracy() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_GPS_AIDING_ACCURACY);
        return task;
    }

    public static OrderTask getGPSAidingTimeout() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_GPS_AIDING_TIMEOUT);
        return task;
    }

    public static OrderTask getGPSExtremeMode() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_GPS_EXTREME_MODE);
        return task;
    }

    public static OrderTask getLoraRegion() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_REGION);
        return task;
    }

    public static OrderTask getLoraUploadMode() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_MODE);
        return task;
    }

    public static OrderTask getLoraNetworkStatus() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_NETWORK_STATUS);
        return task;
    }

    public static OrderTask getLoraDevEUI() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_DEV_EUI);
        return task;
    }

    public static OrderTask getLoraAppEUI() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_APP_EUI);
        return task;
    }

    public static OrderTask getLoraAppKey() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_APP_KEY);
        return task;
    }

    public static OrderTask getLoraDevAddr() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_DEV_ADDR);
        return task;
    }

    public static OrderTask getLoraAppSKey() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_APP_SKEY);
        return task;
    }

    public static OrderTask getLoraNwkSKey() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_NWK_SKEY);
        return task;
    }

    public static OrderTask getLoraMessageType() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_MESSAGE_TYPE);
        return task;
    }

    public static OrderTask getLoraCH() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_CH);
        return task;
    }

    public static OrderTask getLoraDR() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_DR);
        return task;
    }

    public static OrderTask getLoraUplinkStrategy() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_UPLINK_STRATEGY);
        return task;
    }


    public static OrderTask getLoraDutyCycleEnable() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_DUTYCYCLE);
        return task;
    }

    public static OrderTask getLoraTimeSyncInterval() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_TIME_SYNC_INTERVAL);
        return task;
    }

    public static OrderTask getLoraReconnectInterval() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_RECONNECT_INTERVAL);
        return task;
    }

    public static OrderTask getBeaconEnable() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_BEACON_ENABLE);
        return task;
    }

    public static OrderTask getAdvInterval() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_ADV_INTERVAL);
        return task;
    }

    public static OrderTask getConnectable() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_CONNECTABLE);
        return task;
    }

    public static OrderTask getAdvTimeout() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_ADV_TIMEOUT);
        return task;
    }

    public static OrderTask getAdvUUID() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_ADV_UUID);
        return task;
    }

    public static OrderTask getAdvMajor() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_ADV_MAJOR);
        return task;
    }

    public static OrderTask getAdvMinor() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_ADV_MINOR);
        return task;
    }

    public static OrderTask getAdvRSSI() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_ADV_RSSI);
        return task;
    }

    public static OrderTask getAdvTxPower() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_ADV_TX_POWER);
        return task;
    }

    public static OrderTask getAdvName() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_ADV_NAME);
        return task;
    }

    public static OrderTask getScanType() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_SCAN_TYPE);
        return task;
    }

    public static OrderTask getWakeupCondition() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_WAKEUP_CONDITION);
        return task;
    }

    public static OrderTask getMotionDetection() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_MOTION_DETECTION);
        return task;
    }

    public static OrderTask getVibrationEnable() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_VIBRATION_ENABLE);
        return task;
    }

    public static OrderTask getVibrationThreshold() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_VIBRATION_THRESHOLD);
        return task;
    }

    public static OrderTask getVibrationReportInterval() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_VIBRATION_REPORT_INTERVAL);
        return task;
    }

    public static OrderTask getVibrationTimeout() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_VIBRATION_TIMEOUT);
        return task;
    }

    public static OrderTask getManDownEnable() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_MAN_DOWN_ENABLE);
        return task;
    }

    public static OrderTask getManDownIdleTimeout() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_MAN_DOWN_IDLE_TIMEOUT);
        return task;
    }

    public static OrderTask getTamperAlarm() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_TAMPER_ALARM);
        return task;
    }

    public static OrderTask getActiveStateEnable() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_ACTIVE_STATE_ENABLE);
        return task;
    }

    public static OrderTask getActiveStateTimeout() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_ACTIVE_STATE_TIMEOUT);
        return task;
    }


    ///////////////////////////////////////////////////////////////////////////
    // WIRTE
    ///////////////////////////////////////////////////////////////////////////
    public static OrderTask setPassword(String password) {
        SetPasswordTask task = new SetPasswordTask();
        task.setData(password);
        return task;
    }

    public static OrderTask restart() {
        ParamsWriteTask task = new ParamsWriteTask();
        task.restart();
        return task;
    }

    public static OrderTask restore() {
        ParamsWriteTask task = new ParamsWriteTask();
        task.restore();
        return task;
    }

    public static OrderTask setTime() {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setTime();
        return task;
    }

    public static OrderTask setTimeZone(int timeZone) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setTimeZone(timeZone);
        return task;
    }

    public static OrderTask changePassword(String password) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.changePassword(password);
        return task;
    }

    public static OrderTask setWorkMode(@IntRange(from = 0, to = 4) int workMode) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setWorkMode(workMode);
        return task;
    }

    public static OrderTask setPowerStatus(@IntRange(from = 0, to = 1) int status) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setPowerStatus(status);
        return task;
    }

    public static OrderTask setHeartBeatInterval(@IntRange(from = 300, to = 86400) int interval) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setHeartBeatInterval(interval);
        return task;
    }

    public static OrderTask setReedSwitch(@IntRange(from = 0, to = 1) int status) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setReedSwitch(status);
        return task;
    }

    public static OrderTask setShutdownInfoReport(@IntRange(from = 0, to = 1) int onoff) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setShutdownInfoReport(onoff);
        return task;
    }

    public static OrderTask setOfflineLocation(@IntRange(from = 0, to = 1) int onoff) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setOfflineLocation(onoff);
        return task;
    }

    public static OrderTask setLowPower(@IntRange(from = 0, to = 4) int status) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLowPower(status);
        return task;
    }

    public static OrderTask setIndicatorLight(@IntRange(from = 0, to = 65535) int status) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setIndicatorLight(status);
        return task;
    }

    public static OrderTask setPeriodicPosStrategy(@IntRange(from = 1, to = 7) int strategy) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setPeriodicPosStrategy(strategy);
        return task;
    }

    public static OrderTask setPeriodicReportInterval(@IntRange(from = 30, to = 86400) int interval) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setPeriodicReportInterval(interval);
        return task;
    }

    public static OrderTask setTimePosStrategy(@IntRange(from = 1, to = 7) int strategy) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setTimePosReportPoints(strategy);
        return task;
    }

    public static OrderTask setTimePosReportPoints(ArrayList<Integer> timePoints) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setTimePosReportPoints(timePoints);
        return task;
    }

    public static OrderTask setMotionModeEvent(int event) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setMotionModeEvent(event);
        return task;
    }

    public static OrderTask setMotionModeStartNumber(int number) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setMotionModeStartNumber(number);
        return task;
    }

    public static OrderTask setMotionStartPosStrategy(@IntRange(from = 1, to = 7) int strategy) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setMotionStartPosStrategy(strategy);
        return task;
    }

    public static OrderTask setMotionTripInterval(@IntRange(from = 10, to = 86400) int interval) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setMotionTripInterval(interval);
        return task;
    }


    public static OrderTask setMotionTripPosStrategy(@IntRange(from = 1, to = 7) int strategy) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setMotionTripPosStrategy(strategy);
        return task;
    }

    public static OrderTask setMotionEndTimeout(@IntRange(from = 3, to = 180) int timeout) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setMotionEndTimeout(timeout);
        return task;
    }

    public static OrderTask setMotionEndNumber(@IntRange(from = 1, to = 255) int number) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setMotionEndNumber(number);
        return task;
    }

    public static OrderTask setMotionEndInterval(@IntRange(from = 10, to = 300) int interval) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setMotionEndInterval(interval);
        return task;
    }

    public static OrderTask setMotionEndPosStrategy(@IntRange(from = 1, to = 7) int strategy) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setMotionEndPosStrategy(strategy);
        return task;
    }

    public static OrderTask setDownLinkPosStrategy(@IntRange(from = 1, to = 7) int strategy) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setDownLinkPosStrategy(strategy);
        return task;
    }

    public static OrderTask setWifiPosNumber(@IntRange(from = 1, to = 5) int number) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setWifiPosNumber(number);
        return task;
    }

    public static OrderTask setWifiPosBSSIDNumber(@IntRange(from = 1, to = 5) int number) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setWifiPosBSSIDNumber(number);
        return task;
    }

    public static OrderTask setBlePosTimeout(@IntRange(from = 1, to = 10) int timeout) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setBlePosTimeout(timeout);
        return task;
    }

    public static OrderTask setBlePosNumber(@IntRange(from = 1, to = 5) int number) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setBlePosNumber(number);
        return task;
    }

    public static OrderTask setFilterABRelation(@IntRange(from = 0, to = 1) int relation) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setFilterABRelation(relation);
        return task;
    }

    public static OrderTask setFilterSwitchA(@IntRange(from = 0, to = 1) int enable) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setFilterSwitchA(enable);
        return task;
    }

    public static OrderTask setFilterNameA(String name, boolean isReverse) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setFilterNameA(name, isReverse);
        return task;
    }


    public static OrderTask setFilterMacA(String mac, boolean isReverse) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setFilterMacA(mac, isReverse);
        return task;
    }

    public static OrderTask setFilterMajorRangeA(@IntRange(from = 0, to = 1) int enable,
                                                 @IntRange(from = 0, to = 65535) int majorMin,
                                                 @IntRange(from = 0, to = 65535) int majorMax,
                                                 boolean isReverse) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setFilterMajorRangeA(enable, majorMin, majorMax, isReverse);
        return task;
    }

    public static OrderTask setFilterMinorRangeA(@IntRange(from = 0, to = 1) int enable,
                                                 @IntRange(from = 0, to = 65535) int minorMin,
                                                 @IntRange(from = 0, to = 65535) int minorMax,
                                                 boolean isReverse) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setFilterMinorRangeA(enable, minorMin, minorMax, isReverse);
        return task;
    }

    public static OrderTask setFilterRawDataA(ArrayList<String> filterRawDatas, boolean isReverse) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setFilterRawDataA(filterRawDatas, isReverse);
        return task;
    }

    public static OrderTask setFilterUUIDA(String uuid, boolean isReverse) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setFilterUUIDA(uuid, isReverse);
        return task;
    }

    public static OrderTask setFilterRssiA(@IntRange(from = -127, to = 0) int rssi) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setFilterRssiA(rssi);
        return task;
    }

    public static OrderTask setFilterPHYA(@IntRange(from = 0, to = 1) int enable, @IntRange(from = 0, to = 2) int phy) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setFilterPHYA(enable, phy);
        return task;
    }

    public static OrderTask setFilterSwitchB(@IntRange(from = 0, to = 1) int enable) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setFilterSwitchB(enable);
        return task;
    }

    public static OrderTask setFilterNameB(String name, boolean isReverse) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setFilterNameB(name, isReverse);
        return task;
    }

    public static OrderTask setFilterMacB(String mac, boolean isReverse) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setFilterMacB(mac, isReverse);
        return task;
    }

    public static OrderTask setFilterMajorRangeB(@IntRange(from = 0, to = 1) int enable,
                                                 @IntRange(from = 0, to = 65535) int majorMin,
                                                 @IntRange(from = 0, to = 65535) int majorMax,
                                                 boolean isReverse) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setFilterMajorRangeB(enable, majorMin, majorMax, isReverse);
        return task;
    }

    public static OrderTask setFilterMinorRangeB(@IntRange(from = 0, to = 1) int enable,
                                                 @IntRange(from = 0, to = 65535) int minorMin,
                                                 @IntRange(from = 0, to = 65535) int minorMax,
                                                 boolean isReverse) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setFilterMinorRangeB(enable, minorMin, minorMax, isReverse);
        return task;
    }

    public static OrderTask setFilterRawDataB(ArrayList<String> filterRawDatas, boolean isReverse) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setFilterRawDataB(filterRawDatas, isReverse);
        return task;
    }

    public static OrderTask setFilterUUIDB(String uuid, boolean isReverse) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setFilterUUIDB(uuid, isReverse);
        return task;
    }

    public static OrderTask setFilterRssiB(@IntRange(from = -127, to = 0) int rssi) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setFilterRssiB(rssi);
        return task;
    }


    public static OrderTask setFilterPHYB(@IntRange(from = 0, to = 1) int enable, @IntRange(from = 0, to = 2) int phy) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setFilterPHYB(enable, phy);
        return task;
    }


    public static OrderTask setGPSColdStartTimeout(@IntRange(from = 3, to = 15) int timeout) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setGPSColdStartTimeout(timeout);
        return task;
    }

    public static OrderTask setGPSCoarseAccuracyMask(@IntRange(from = 5, to = 100) int mask) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setGPSCoarseAccuracyMask(mask);
        return task;
    }

    public static OrderTask setGPSFineAccuracyMask(@IntRange(from = 5, to = 100) int mask) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setGPSFineAccuracyMask(mask);
        return task;
    }

    public static OrderTask setGPSCoarseTimeout(@IntRange(from = 1, to = 7620) int timeout) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setGPSCoarseTimeout(timeout);
        return task;
    }

    public static OrderTask setGPSFineTimeout(@IntRange(from = 0, to = 76200) int timeout) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setGPSFineTimeout(timeout);
        return task;
    }

    public static OrderTask setGPSPDOPLimit(@IntRange(from = 25, to = 100) int limit) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setGPSPDOPlimit(limit);
        return task;
    }

    public static OrderTask setGPSFixMode(@IntRange(from = 0, to = 2) int mode) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setGPSFixMode(mode);
        return task;
    }

    public static OrderTask setGPSModel(@IntRange(from = 0, to = 9) int model) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setGPSModel(model);
        return task;
    }

    public static OrderTask setGPSTimeBudget(@IntRange(from = 0, to = 76200) int budget) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setGPSTimeBudget(budget);
        return task;
    }

    public static OrderTask setGPSAutonomousAiding(@IntRange(from = 0, to = 1) int enable) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setGPSAutonomousAiding(enable);
        return task;
    }

    public static OrderTask setGPSAidingAccuracy(@IntRange(from = 5, to = 1000) int accuracy) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setGPSAidingAccuracy(accuracy);
        return task;
    }

    public static OrderTask setGPSAidingTimeout(@IntRange(from = 1, to = 7620) int timeout) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setGPSAidingTimeout(timeout);
        return task;
    }

    public static OrderTask setGPSExtremeMode(@IntRange(from = 0, to = 1) int mode) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setGPSExtremeMode(mode);
        return task;
    }

    public static OrderTask setLoraRegion(@IntRange(from = 0, to = 9) int region) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoraRegion(region);
        return task;
    }

    public static OrderTask setLoraUploadMode(@IntRange(from = 1, to = 2) int mode) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoraUploadMode(mode);
        return task;
    }

    public static OrderTask setLoraDevEUI(String devEUI) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoraDevEUI(devEUI);
        return task;
    }

    public static OrderTask setLoraAppEUI(String appEUI) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoraAppEUI(appEUI);
        return task;
    }

    public static OrderTask setLoraAppKey(String appKey) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoraAppKey(appKey);
        return task;
    }

    public static OrderTask setLoraDevAddr(String devAddr) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoraDevAddr(devAddr);
        return task;
    }

    public static OrderTask setLoraAppSKey(String appSkey) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoraAppSKey(appSkey);
        return task;
    }

    public static OrderTask setLoraNwkSKey(String nwkSkey) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoraNwkSKey(nwkSkey);
        return task;
    }

    public static OrderTask setLoraMessageType(@IntRange(from = 0, to = 1) int type) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoraMessageType(type);
        return task;
    }

    public static OrderTask setLoraCH(int ch1, int ch2) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoraCH(ch1, ch2);
        return task;
    }

    public static OrderTask setLoraDR(int dr1) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoraDR(dr1);
        return task;
    }

    public static OrderTask setLoraUplinkStrategy(int adr, int number, int dr1, int dr2) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoraUplinkStrategy(adr, number, dr1, dr2);
        return task;
    }


    public static OrderTask setLoraDutyCycleEnable(@IntRange(from = 0, to = 1) int enable) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoraDutyCycleEnable(enable);
        return task;
    }

    public static OrderTask setLoraTimeSyncInterval(@IntRange(from = 0, to = 255) int interval) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoraTimeSyncInterval(interval);
        return task;
    }

    public static OrderTask setLoraReconnectInterval(@IntRange(from = 0, to = 30) int days) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoraReconnectInterval(days);
        return task;
    }

    public static OrderTask setBeaconEnable(@IntRange(from = 0, to = 1) int enable) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setBeaconEnable(enable);
        return task;
    }

    public static OrderTask setAdvInterval(@IntRange(from = 0, to = 100) int advInterval) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setAdvInterval(advInterval);
        return task;
    }

    public static OrderTask setConnectable(@IntRange(from = 0, to = 1) int connectable) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setConnectable(connectable);
        return task;
    }

    public static OrderTask setAdvTimeout(@IntRange(from = 1, to = 60) int timeout) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setAdvTimeout(timeout);
        return task;
    }

    public static OrderTask setAdvUUID(String uuid) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setAdvUUID(uuid);
        return task;
    }

    public static OrderTask setAdvMajor(@IntRange(from = 0, to = 65535) int major) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setAdvMajor(major);
        return task;
    }

    public static OrderTask setAdvMinor(@IntRange(from = 0, to = 65535) int minor) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setAdvMinor(minor);
        return task;
    }

    public static OrderTask setAdvRSSI(int rssi) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setAdvRSSI(rssi);
        return task;
    }

    public static OrderTask setAdvTxPower(int txPower) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setAdvTxPower(txPower);
        return task;
    }

    public static OrderTask setAdvName(String advName) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setAdvName(advName);
        return task;
    }

    public static OrderTask setScanType(int scanType) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setScanType(scanType);
        return task;
    }

    public static OrderTask setWakeupCondition(@IntRange(from = 1, to = 20) int threshold,
                                               @IntRange(from = 1, to = 10) int time) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setWakeupCondition(threshold, time);
        return task;
    }

    public static OrderTask setMotionDetection(@IntRange(from = 10, to = 250) int threshold,
                                               @IntRange(from = 1, to = 15) int time) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setMotionDetection(threshold, time);
        return task;
    }

    public static OrderTask setVibrationEnable(@IntRange(from = 0, to = 1) int enable) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setVibrationEnable(enable);
        return task;
    }

    public static OrderTask setVibrationThreshold(@IntRange(from = 10, to = 255) int threshold) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setVibrationThreshold(threshold);
        return task;
    }

    public static OrderTask setVibrationReportInterval(@IntRange(from = 3, to = 255) int interval) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setVibrationReportInterval(interval);
        return task;
    }

    public static OrderTask setVibrationTimeout(@IntRange(from = 1, to = 20) int timeout) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setVibrationTimeout(timeout);
        return task;
    }

    public static OrderTask setManDownEnable(@IntRange(from = 0, to = 1) int enable) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setManDownEnable(enable);
        return task;
    }

    public static OrderTask setManDownIdleTimeout(@IntRange(from = 1, to = 8760) int timeout) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setManDownIdleTimeout(timeout);
        return task;
    }

    public static OrderTask setTamperAlarm(@IntRange(from = 0, to = 1) int enable) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setTamperAlarm(enable);
        return task;
    }

    public static OrderTask setActiveStateEnable(@IntRange(from = 0, to = 1) int enable) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setActiveStateEnable(enable);
        return task;
    }

    public static OrderTask setActiveStateTimeout(@IntRange(from = 1, to = 86400) int interval) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setActiveStateTimeout(interval);
        return task;
    }

    public static OrderTask setManDownIdleClear() {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setManDownIdleClear();
        return task;
    }

    public static OrderTask readStorageData(int time) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.readStorageData(time);
        return task;
    }

    public static OrderTask setSyncEnable(int enable) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setSyncEnable(enable);
        return task;
    }

    public static OrderTask clearStorageData() {
        ParamsWriteTask task = new ParamsWriteTask();
        task.clearStorageData();
        return task;
    }

}
