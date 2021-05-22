package com.moko.support.lw001.service;

import com.moko.support.lw001.entity.DeviceInfo;

public interface DeviceInfoParseable<T> {
    T parseDeviceInfo(DeviceInfo deviceInfo);
}
