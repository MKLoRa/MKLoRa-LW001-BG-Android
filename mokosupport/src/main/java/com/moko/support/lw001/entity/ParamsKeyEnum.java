package com.moko.support.lw001.entity;


import java.io.Serializable;

public enum ParamsKeyEnum implements Serializable {
    KEY_RESET(0x01),
    // 设备信息同步间隔
    KEY_DEVICE_INFO_INTERVAL(0X02),
    // 设备时间
    KEY_TIME(0x03),
    // 密码
    KEY_PASSWORD(0x04),
    // 上电状态
    KEY_POWER_STATUS(0x05),
    // 防拆
    KEY_TAMPER_DETECTION(0x06),
    // 数据上报间隔
    KEY_DATA_REPORT_INTERVAL(0X07),
    // 数据保存间隔
//    KEY_DATA_SAVED_INTERVAL(0X08),
    // 重复数据过滤
    KEY_TRACKING_FILTER_REPEAT(0x09),
    // 上报数据类型
    KEY_UPLINK_DATA_TYPE(0x0A),
    // 上发数据长度
    KEY_UPLINK_DATA_MAX_LENGTH(0x0B),
    // 蓝牙MAC
    KEY_DEVICE_MAC(0x0D),
    // 上发数据选择
    KEY_UPLINK_DATA_CONTENT(0x0E),
    // 扫描MAC超限开关
    KEY_OVER_LIMIT_ENABLE(0x0F),
    // 扫描MAC超限间隔
    KEY_OVER_LIMIT_DURATION(0x10),
    // 扫描MAC超限数量
    KEY_OVER_LIMIT_QTY(0x11),
    // 扫描MAC超限触发RSSI
    KEY_OVER_LIMIT_RSSI(0x12),
    // lora
    KEY_LORA_REGION(0x21),
    KEY_LORA_MODE(0x22),
     KEY_LORA_CLASS_TYPE(0x23),
    // lorawan网络状态
    KEY_NETWORK_STATUS(0x24),
    KEY_LORA_DEV_EUI(0x25),
    KEY_LORA_APP_EUI(0x26),
    KEY_LORA_APP_KEY(0x27),
    KEY_LORA_DEV_ADDR(0x28),
    KEY_LORA_APP_SKEY(0x29),
    KEY_LORA_NWK_SKEY(0x2A),
    KEY_LORA_MESSAGE_TYPE(0x2B),
    KEY_LORA_CH(0x2C),
    KEY_LORA_DR(0x2D),
    KEY_LORA_ADR(0x2E),
    KEY_MULTICAST_ENABLE(0x2F),
    KEY_MULTICAST_ADDR(0x30),
    KEY_MULTICAST_APPSKEY(0x31),
    KEY_MULTICAST_NWKSKEY(0x32),
    // 网络检测间隔
    KEY_NETWORK_CHECK_INTERVAL(0x33),
    KEY_LORA_UPLINK_DELL_TIME(0x34),
    KEY_LORA_DUTY_CYCLE_ENABLE(0x35),
    // 时间同步间隔
    KEY_TIME_SYNC_INTERVAL(0x36),
    // 广播
    KEY_ADV_NAME(0x50),
    KEY_ADV_INTERVAL(0x51),
    // 扫描开关
    KEY_SCAN_ENABLE(0x52),
    // 扫描参数
    KEY_SCAN_PARAMS(0x53),
    // filter
    KEY_TRACKING_FILTER_A_B_RELATION(0x60),
    KEY_TRACKING_FILTER_SWITCH_A(0x61),
    KEY_TRACKING_FILTER_ADV_NAME_A(0x62),
    KEY_TRACKING_FILTER_MAC_A(0x63),
    KEY_TRACKING_FILTER_MAJOR_RANGE_A(0x64),
    KEY_TRACKING_FILTER_MINOR_RANGE_A(0x65),
    KEY_TRACKING_FILTER_ADV_RAW_DATA_A(0x66),
    KEY_TRACKING_FILTER_UUID_A(0x67),
    KEY_TRACKING_FILTER_RSSI_A(0x68),

    KEY_TRACKING_FILTER_SWITCH_B(0x69),
    KEY_TRACKING_FILTER_ADV_NAME_B(0x6A),
    KEY_TRACKING_FILTER_MAC_B(0x6B),
    KEY_TRACKING_FILTER_MAJOR_RANGE_B(0x6C),
    KEY_TRACKING_FILTER_MINOR_RANGE_B(0x6D),
    KEY_TRACKING_FILTER_ADV_RAW_DATA_B(0x6E),
    KEY_TRACKING_FILTER_UUID_B(0x6F),
    KEY_TRACKING_FILTER_RSSI_B(0x70),
    // storage data
    KEY_READ_STORAGE_DATA(0xA0),
    KEY_CLEAR_STORAGE_DATA(0xA1),
    KEY_SYNC_ENABLE(0xA2),
    ;

    private int paramsKey;

    ParamsKeyEnum(int paramsKey) {
        this.paramsKey = paramsKey;
    }


    public int getParamsKey() {
        return paramsKey;
    }

    public static ParamsKeyEnum fromParamKey(int paramsKey) {
        for (ParamsKeyEnum paramsKeyEnum : ParamsKeyEnum.values()) {
            if (paramsKeyEnum.getParamsKey() == paramsKey) {
                return paramsKeyEnum;
            }
        }
        return null;
    }
}
