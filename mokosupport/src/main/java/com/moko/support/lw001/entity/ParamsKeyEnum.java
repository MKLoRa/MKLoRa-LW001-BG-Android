package com.moko.support.lw001.entity;


import java.io.Serializable;

public enum ParamsKeyEnum implements Serializable {

    KEY_RESTART(0x01),
    KEY_RESTORE(0x02),
    KEY_TIME(0x03),
    KEY_TIME_ZONE(0x04),
    // 密码
    KEY_PASSWORD(0x05),
    // 工作模式选择
    KEY_WORK_MODE(0x06),
    // 上电状态
    KEY_POWER_STATUS(0x07),
    // 设备心跳间隔
    KEY_HEARTBEAT_INTERVAL(0x08),
    // 磁簧开关关机功能
    KEY_REED_SWITCH(0x09),
    // 关机信息上报
    KEY_SHUTDOWN_INFO_REPORT(0x0A),
    // 离线定位
    KEY_OFFLINE_LOCATION(0x0B),
    // 低电
    KEY_LOW_POWER(0x0C),
    // 指示灯功能
    KEY_INDICATOR_LIGHT(0x0D),
    // 芯片温度
    KEY_CHIP_TEMP(0x0E),
    // 读取当前系统时间
    KEY_SYSTEM_TIME(0x0F),
    // 读取当前需求版本
    KEY_DEMAND_VERSION(0x10),
    // 电池电量
    KEY_BATTERY_POWER(0x11),
    // 芯片MAC
    KEY_CHIP_MAC(0x12),

    // 定期模式定位策略
    KEY_PERIODIC_MODE_POS_STRATEGY(0x20),
    // 定期模式上报间隔
    KEY_PERIODIC_MODE_REPORT_INTERVAL(0x21),
    // 定时模式定位策略
    KEY_TIME_MODE_POS_STRATEGY(0x22),
    // 定时模式时间点
    KEY_TIME_MODE_REPORT_TIME_POINT(0x23),
    // 运动模式事件
    KEY_MOTION_MODE_EVENT(0x24),
    // 运动开始定位上报次数
    KEY_MOTION_MODE_START_NUMBER(0x25),
    // 运动开始定位策略
    KEY_MOTION_MODE_START_POS_STRATEGY(0x26),
    // 运动中定位间隔
    KEY_MOTION_MODE_TRIP_REPORT_INTERVAL(0x27),
    // 运动中定位策略
    KEY_MOTION_MODE_TRIP_POS_STRATEGY(0x28),
    // 运动结束判断时间
    KEY_MOTION_MODE_END_TIMEOUT(0x29),
    // 运动结束定位次数
    KEY_MOTION_MODE_END_NUMBER(0x2A),
    // 运动结束定位间隔
    KEY_MOTION_MODE_END_REPORT_INTERVAL(0x2B),
    // 运动结束定位策略
    KEY_MOTION_MODE_END_POS_STRATEGY(0x2C),
    // 下行请求定位策略
    KEY_DOWN_LINK_POS_STRATEGY(0x2D),

    // WIFI定位次数
    KEY_WIFI_POS_NUMBER(0x30),
    // WIFI定位成功BSSID数量
    KEY_WIFI_POS_BSSID_NUMBER(0x31),
    // 蓝牙定位超时时间
    KEY_BLE_POS_TIMEOUT(0x32),
    // 蓝牙定位成功MAC数量
    KEY_BLE_POS_MAC_NUMBER(0x33),
    // 蓝牙过滤规则开关与逻辑
    KEY_FILTER_A_B_RELATION(0x34),
    // 规则1开关
    KEY_FILTER_SWITCH_A(0x35),
    // 规则1过滤广播名称
    KEY_FILTER_ADV_NAME_A(0x36),
    // 规则1过滤MAC
    KEY_FILTER_MAC_A(0x37),
    // 规则1过滤MAJOR范围
    KEY_FILTER_MAJOR_RANGE_A(0x38),
    // 规则1过滤MINOR范围
    KEY_FILTER_MINOR_RANGE_A(0x39),
    // 规则1过滤原始数据
    KEY_FILTER_ADV_RAW_DATA_A(0x3A),
    // 规则1过滤UUID
    KEY_FILTER_UUID_A(0x3B),
    // 规则1过滤RSSI
    KEY_FILTER_RSSI_A(0x3C),
    // 规则1过滤PHY
    KEY_FILTER_PHY_A(0x3D),
    // 规则2开关
    KEY_FILTER_SWITCH_B(0x3E),
    // 规则2过滤广播名称
    KEY_FILTER_ADV_NAME_B(0x3F),
    // 规则2过滤MAC
    KEY_FILTER_MAC_B(0x40),
    // 规则22过滤MAJOR范围
    KEY_FILTER_MAJOR_RANGE_B(0x41),
    // 规则2过滤MINOR范围
    KEY_FILTER_MINOR_RANGE_B(0x42),
    // 规则2过滤原始数据
    KEY_FILTER_ADV_RAW_DATA_B(0x43),
    // 规则2过滤UUID
    KEY_FILTER_UUID_B(0x44),
    // 规则2过滤RSSI
    KEY_FILTER_RSSI_B(0x45),
    // 规则2过滤PHY
    KEY_FILTER_PHY_B(0x46),
    // GPS冷启动超时
    KEY_GPS_COLD_START_TIMEOUT(0x47),
    // GPS粗定位精度
    KEY_GPS_COARSE_ACCURACY_MASK(0x48),
    // GPS精确定位精度
    KEY_GPS_FINE_ACCURACY_MASK(0x49),
    // GPS粗定位超时
    KEY_GPS_COARSE_TIMEOUT(0x4A),
    // GPS精确定位超时
    KEY_GPS_FINE_TIMEOUT(0x4B),
    // GPS位置精度因子
    KEY_GPS_PDOP_LIMIT(0x4C),
    // GPS搜星模式
    KEY_GPS_FIX_MODE(0x4D),
    // GPS模式
    KEY_GPS_MODEL(0x4E),
    // GPS定位预算
    KEY_GPS_TIME_BUDGET(0x4F),
    // 辅助定位
    KEY_GPS_AUTONOMOUS_AIDING(0x50),
    // 辅助定位精度
    KEY_GPS_AIDING_ACCURACY(0x51),
    // 辅助定位超时时间
    KEY_GPS_AIDING_TIMEOUT(0x52),
    // GPS上传数据类型
    KEY_GPS_EXTREME_MODE(0x53),

    // lora
    KEY_LORA_REGION(0x60),
    KEY_LORA_MODE(0x61),
    KEY_NETWORK_STATUS(0x62),
    KEY_LORA_DEV_EUI(0x63),
    KEY_LORA_APP_EUI(0x64),
    KEY_LORA_APP_KEY(0x65),
    KEY_LORA_DEV_ADDR(0x66),
    KEY_LORA_APP_SKEY(0x67),
    KEY_LORA_NWK_SKEY(0x68),
    KEY_LORA_MESSAGE_TYPE(0x69),
    KEY_LORA_CH(0x6A),
    KEY_LORA_DR(0x6B),
    KEY_LORA_UPLINK_STRATEGY(0x6C),
    KEY_LORA_DUTYCYCLE(0x6D),
    KEY_LORA_TIME_SYNC_INTERVAL(0x6E),
    KEY_LORA_RECONNECT_INTERVAL(0x6F),

    // ble
    KEY_BEACON_ENABLE(0x70),
    KEY_ADV_INTERVAL(0x71),
    KEY_CONNECTABLE(0x72),
    KEY_ADV_TIMEOUT(0x73),
    KEY_ADV_UUID(0x74),
    KEY_ADV_MAJOR(0x75),
    KEY_ADV_MINOR(0x76),
    KEY_ADV_RSSI(0x77),
    KEY_ADV_TX_POWER(0x78),
    KEY_ADV_NAME(0x79),
    KEY_SCAN_TYPE(0x7A),

    // Auxiliary Operation
    KEY_WAKEUP_CONDITION(0x80),
    KEY_MOTION_DETECTION(0x81),
    KEY_VIBRATION_ENABLE(0x82),
    KEY_VIBRATION_THRESHOLD(0x83),
    KEY_VIBRATION_REPORT_INTERVAL(0x84),
    KEY_VIBRATION_TIMEOUT(0x85),
    KEY_MAN_DOWN_ENABLE(0x86),
    KEY_MAN_DOWN_IDLE_TIMEOUT(0x87),
    KEY_TAMPER_ALARM(0x88),
    KEY_ACTIVE_STATE_ENABLE(0x89),
    KEY_ACTIVE_STATE_TIMEOUT(0x8A),
    KEY_MAN_DOWN_IDLE_CLEAR(0x8B),

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
