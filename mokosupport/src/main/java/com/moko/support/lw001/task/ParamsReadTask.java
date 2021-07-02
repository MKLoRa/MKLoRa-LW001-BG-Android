package com.moko.support.lw001.task;

import com.moko.ble.lib.task.OrderTask;
import com.moko.support.lw001.entity.OrderCHAR;
import com.moko.support.lw001.entity.ParamsKeyEnum;

public class ParamsReadTask extends OrderTask {
    public byte[] data;

    public ParamsReadTask() {
        super(OrderCHAR.CHAR_PARAMS, OrderTask.RESPONSE_TYPE_WRITE_NO_RESPONSE);
    }

    @Override
    public byte[] assemble() {
        return data;
    }

    public void setData(ParamsKeyEnum key) {
        switch (key) {
            case KEY_TIME_ZONE:
            case KEY_WORK_MODE:
            case KEY_POWER_STATUS:
            case KEY_HEARTBEAT_INTERVAL:
            case KEY_REED_SWITCH:
            case KEY_SHUTDOWN_INFO_REPORT:
            case KEY_OFFLINE_LOCATION:
            case KEY_LOW_POWER:
            case KEY_INDICATOR_LIGHT:
            case KEY_CHIP_TEMP:
            case KEY_SYSTEM_TIME:
            case KEY_DEMAND_VERSION:
            case KEY_BATTERY_POWER:
            case KEY_CHIP_MAC:

            case KEY_PERIODIC_MODE_POS_STRATEGY:
            case KEY_PERIODIC_MODE_REPORT_INTERVAL:
            case KEY_TIME_MODE_POS_STRATEGY:
            case KEY_TIME_MODE_REPORT_TIME_POINT:
            case KEY_MOTION_MODE_EVENT:
            case KEY_MOTION_MODE_START_NUMBER:
            case KEY_MOTION_MODE_START_POS_STRATEGY:
            case KEY_MOTION_MODE_TRIP_REPORT_INTERVAL:
            case KEY_MOTION_MODE_TRIP_POS_STRATEGY:
            case KEY_MOTION_MODE_END_TIMEOUT:
            case KEY_MOTION_MODE_END_NUMBER:
            case KEY_MOTION_MODE_END_REPORT_INTERVAL:
            case KEY_MOTION_MODE_END_POS_STRATEGY:
            case KEY_DOWN_LINK_POS_STRATEGY:

            case KEY_WIFI_POS_NUMBER:
            case KEY_WIFI_POS_BSSID_NUMBER:
            case KEY_BLE_POS_TIMEOUT:
            case KEY_BLE_POS_MAC_NUMBER:
            case KEY_FILTER_A_B_RELATION:
            case KEY_FILTER_SWITCH_A:
            case KEY_FILTER_ADV_NAME_A:
            case KEY_FILTER_MAC_A:
            case KEY_FILTER_MAJOR_RANGE_A:
            case KEY_FILTER_MINOR_RANGE_A:
            case KEY_FILTER_ADV_RAW_DATA_A:
            case KEY_FILTER_UUID_A:
            case KEY_FILTER_RSSI_A:
            case KEY_FILTER_SWITCH_B:
            case KEY_FILTER_ADV_NAME_B:
            case KEY_FILTER_MAC_B:
            case KEY_FILTER_MAJOR_RANGE_B:
            case KEY_FILTER_MINOR_RANGE_B:
            case KEY_FILTER_ADV_RAW_DATA_B:
            case KEY_FILTER_UUID_B:
            case KEY_FILTER_RSSI_B:
            case KEY_GPS_COLD_START_TIMEOUT:
            case KEY_GPS_COARSE_ACCURACY_MASK:
            case KEY_GPS_FINE_ACCURACY_MASK:
            case KEY_GPS_COARSE_TIMEOUT:
            case KEY_GPS_FINE_TIMEOUT:
            case KEY_GPS_PDOP_LIMIT:
            case KEY_GPS_FIX_MODE:
            case KEY_GPS_MODEL:
            case KEY_GPS_TIME_BUDGET:
            case KEY_GPS_AUTONOMOUS_AIDING:
            case KEY_GPS_AIDING_ACCURACY:
            case KEY_GPS_AIDING_TIMEOUT:
            case KEY_GPS_EXTREME_MODE:

            case KEY_LORA_REGION:
            case KEY_LORA_MODE:
            case KEY_NETWORK_STATUS:
            case KEY_LORA_DEV_EUI:
            case KEY_LORA_APP_EUI:
            case KEY_LORA_APP_KEY:
            case KEY_LORA_DEV_ADDR:
            case KEY_LORA_APP_SKEY:
            case KEY_LORA_NWK_SKEY:
            case KEY_LORA_MESSAGE_TYPE:
            case KEY_LORA_CH:
            case KEY_LORA_DR:
            case KEY_LORA_UPLINK_STRATEGY:
            case KEY_LORA_DUTYCYCLE:
            case KEY_LORA_TIME_SYNC_INTERVAL:
            case KEY_LORA_RECONNECT_INTERVAL:

            case KEY_BEACON_ENABLE:
            case KEY_ADV_INTERVAL:
            case KEY_CONNECTABLE:
            case KEY_ADV_TIMEOUT:
            case KEY_ADV_UUID:
            case KEY_ADV_MAJOR:
            case KEY_ADV_MINOR:
            case KEY_ADV_RSSI:
            case KEY_ADV_TX_POWER:
            case KEY_ADV_NAME:
            case KEY_SCAN_TYPE:

            case KEY_WAKEUP_CONDITION:
            case KEY_MOTION_DETECTION:
            case KEY_VIBRATION_ENABLE:
            case KEY_VIBRATION_THRESHOLD:
            case KEY_VIBRATION_REPORT_INTERVAL:
            case KEY_VIBRATION_TIMEOUT:
            case KEY_MAN_DOWN_ENABLE:
            case KEY_MAN_DOWN_IDLE_TIMEOUT:
            case KEY_TAMPER_ALARM:
            case KEY_ACTIVE_STATE_ENABLE:
            case KEY_ACTIVE_STATE_TIMEOUT:
//            case KEY_MANDOWN_IDLE_STATUS:
                createGetConfigData(key.getParamsKey());
                break;
        }
    }

    private void createGetConfigData(int configKey) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x00,
                (byte) configKey,
                (byte) 0x00
        };
    }
}
