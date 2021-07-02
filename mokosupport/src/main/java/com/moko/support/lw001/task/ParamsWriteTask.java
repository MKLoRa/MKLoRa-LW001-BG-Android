package com.moko.support.lw001.task;

import android.text.TextUtils;

import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.utils.MokoUtils;
import com.moko.support.lw001.entity.OrderCHAR;
import com.moko.support.lw001.entity.ParamsKeyEnum;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.IntRange;

public class ParamsWriteTask extends OrderTask {
    public byte[] data;

    public ParamsWriteTask() {
        super(OrderCHAR.CHAR_PARAMS, OrderTask.RESPONSE_TYPE_WRITE_NO_RESPONSE);
    }

    @Override
    public byte[] assemble() {
        return data;
    }


    public void restart() {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_RESTART.getParamsKey(),
                (byte) 0x00
        };
    }

    public void restore() {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_RESTORE.getParamsKey(),
                (byte) 0x00
        };
    }

    public void setTime() {
        Calendar calendar = Calendar.getInstance();
        long time = calendar.getTimeInMillis() / 1000;
        byte[] bytes = new byte[4];
        for (int i = 0; i < 4; ++i) {
            bytes[i] = (byte) (time >> 8 * (3 - i) & 255);
        }
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_TIME.getParamsKey(),
                (byte) 0x04,
                bytes[0],
                bytes[1],
                bytes[2],
                bytes[3],
        };
    }

    public void setTimeZone(@IntRange(from = -12, to = 12) int timeZone) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_TIME_ZONE.getParamsKey(),
                (byte) 0x01,
                (byte) timeZone
        };
    }

    public void changePassword(String password) {
        byte[] passwordBytes = password.getBytes();
        int length = passwordBytes.length;
        data = new byte[length + 4];
        data[0] = (byte) 0xED;
        data[1] = (byte) 0x01;
        data[2] = (byte) ParamsKeyEnum.KEY_PASSWORD.getParamsKey();
        data[3] = (byte) length;
        for (int i = 0; i < passwordBytes.length; i++) {
            data[i + 4] = passwordBytes[i];
        }
    }

    public void setWorkMode(@IntRange(from = 0, to = 4) int workMode) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_WORK_MODE.getParamsKey(),
                (byte) 0x01,
                (byte) workMode
        };
    }

    public void setPowerStatus(@IntRange(from = 0, to = 1) int status) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_POWER_STATUS.getParamsKey(),
                (byte) 0x01,
                (byte) status
        };
    }

    public void setHeartBeatInterval(@IntRange(from = 300, to = 86400) int interval) {
        byte[] intervalBytes = MokoUtils.toByteArray(interval, 4);
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_HEARTBEAT_INTERVAL.getParamsKey(),
                (byte) 0x04,
                intervalBytes[0],
                intervalBytes[1],
                intervalBytes[2],
                intervalBytes[3],
        };
    }

    public void setReedSwitch(@IntRange(from = 0, to = 1) int status) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_REED_SWITCH.getParamsKey(),
                (byte) 0x01,
                (byte) status
        };
    }

    public void setShutdownInfoReport(@IntRange(from = 0, to = 1) int onoff) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_SHUTDOWN_INFO_REPORT.getParamsKey(),
                (byte) 0x01,
                (byte) onoff
        };
    }

    public void setOfflineLocation(@IntRange(from = 0, to = 1) int onoff) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_OFFLINE_LOCATION.getParamsKey(),
                (byte) 0x01,
                (byte) onoff
        };
    }

    public void setLowPower(@IntRange(from = 0, to = 4) int status) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LOW_POWER.getParamsKey(),
                (byte) 0x01,
                (byte) status
        };
    }

    public void setIndicatorLight(@IntRange(from = 0, to = 65535) int status) {
        byte[] statusBytes = MokoUtils.toByteArray(status, 2);
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_INDICATOR_LIGHT.getParamsKey(),
                (byte) 0x02,
                statusBytes[0],
                statusBytes[1]
        };
    }

    public void setPeriodicPosStrategy(@IntRange(from = 1, to = 7) int strategy) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_PERIODIC_MODE_POS_STRATEGY.getParamsKey(),
                (byte) 0x01,
                (byte) strategy
        };
    }

    public void setPeriodicReportInterval(@IntRange(from = 30, to = 86400) int interval) {
        byte[] intervalBytes = MokoUtils.toByteArray(interval, 4);
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_PERIODIC_MODE_REPORT_INTERVAL.getParamsKey(),
                (byte) 0x04,
                intervalBytes[0],
                intervalBytes[1],
                intervalBytes[2],
                intervalBytes[3],
        };
    }

    public void setTimePosReportPoints(@IntRange(from = 1, to = 7) int strategy) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_TIME_MODE_POS_STRATEGY.getParamsKey(),
                (byte) 0x01,
                (byte) strategy
        };
    }

    public void setTimePosReportPoints(ArrayList<Integer> timePoints) {
        if (timePoints == null || timePoints.size() == 0) {
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) ParamsKeyEnum.KEY_TIME_MODE_REPORT_TIME_POINT.getParamsKey(),
                    (byte) 0x00
            };
        } else {
            int length = timePoints.size();
            data = new byte[4 + length];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = (byte) ParamsKeyEnum.KEY_TIME_MODE_REPORT_TIME_POINT.getParamsKey();
            data[3] = (byte) length;
            for (int i = 0; i < length; i++) {
                data[4 + i] = timePoints.get(i).byteValue();
            }
        }
    }

    public void setMotionModeEvent(int event) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_MOTION_MODE_EVENT.getParamsKey(),
                (byte) 0x01,
                (byte) event
        };
    }

    public void setMotionModeStartNumber(@IntRange(from = 1, to = 255) int number) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_MOTION_MODE_START_NUMBER.getParamsKey(),
                (byte) 0x01,
                (byte) number
        };
    }

    public void setMotionStartPosStrategy(@IntRange(from = 1, to = 7) int strategy) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_MOTION_MODE_START_POS_STRATEGY.getParamsKey(),
                (byte) 0x01,
                (byte) strategy
        };
    }

    public void setMotionTripInterval(@IntRange(from = 10, to = 86400) int interval) {
        byte[] intervalBytes = MokoUtils.toByteArray(interval, 4);
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_MOTION_MODE_TRIP_REPORT_INTERVAL.getParamsKey(),
                (byte) 0x04,
                intervalBytes[0],
                intervalBytes[1],
                intervalBytes[2],
                intervalBytes[3],
        };
    }


    public void setMotionTripPosStrategy(@IntRange(from = 1, to = 7) int strategy) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_MOTION_MODE_TRIP_POS_STRATEGY.getParamsKey(),
                (byte) 0x01,
                (byte) strategy
        };
    }

    public void setMotionEndTimeout(@IntRange(from = 3, to = 180) int timeout) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_MOTION_MODE_END_TIMEOUT.getParamsKey(),
                (byte) 0x01,
                (byte) timeout
        };
    }

    public void setMotionEndNumber(@IntRange(from = 1, to = 255) int number) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_MOTION_MODE_END_NUMBER.getParamsKey(),
                (byte) 0x01,
                (byte) number
        };
    }

    public void setMotionEndInterval(@IntRange(from = 10, to = 300) int interval) {
        byte[] intervalBytes = MokoUtils.toByteArray(interval, 2);
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_MOTION_MODE_END_REPORT_INTERVAL.getParamsKey(),
                (byte) 0x02,
                intervalBytes[0],
                intervalBytes[1],
        };
    }

    public void setMotionEndPosStrategy(@IntRange(from = 1, to = 7) int strategy) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_MOTION_MODE_END_POS_STRATEGY.getParamsKey(),
                (byte) 0x01,
                (byte) strategy
        };
    }

    public void setDownLinkPosStrategy(@IntRange(from = 1, to = 7) int strategy) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_DOWN_LINK_POS_STRATEGY.getParamsKey(),
                (byte) 0x01,
                (byte) strategy
        };
    }

    public void setWifiPosNumber(@IntRange(from = 1, to = 5) int number) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_WIFI_POS_NUMBER.getParamsKey(),
                (byte) 0x01,
                (byte) number
        };
    }

    public void setWifiPosBSSIDNumber(@IntRange(from = 1, to = 5) int number) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_WIFI_POS_BSSID_NUMBER.getParamsKey(),
                (byte) 0x01,
                (byte) number
        };
    }

    public void setBlePosTimeout(@IntRange(from = 1, to = 10) int timeout) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_BLE_POS_TIMEOUT.getParamsKey(),
                (byte) 0x01,
                (byte) timeout
        };
    }

    public void setBlePosNumber(@IntRange(from = 1, to = 5) int number) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_BLE_POS_MAC_NUMBER.getParamsKey(),
                (byte) 0x01,
                (byte) number
        };
    }

    public void setFilterABRelation(@IntRange(from = 0, to = 1) int relation) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_FILTER_A_B_RELATION.getParamsKey(),
                (byte) 0x01,
                (byte) relation,
        };
    }

    public void setFilterSwitchA(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_FILTER_SWITCH_A.getParamsKey(),
                (byte) 0x01,
                (byte) enable,
        };
    }

    public void setFilterNameA(String name, boolean isReverse) {
        if (TextUtils.isEmpty(name)) {
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) ParamsKeyEnum.KEY_FILTER_ADV_NAME_A.getParamsKey(),
                    (byte) 0x01,
                    (byte) 0x00,
            };
        } else {
            byte[] nameBytes = name.getBytes();
            int length = nameBytes.length + 1;
            data = new byte[4 + length];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = (byte) ParamsKeyEnum.KEY_FILTER_ADV_NAME_A.getParamsKey();
            data[3] = (byte) length;
            data[4] = (byte) (isReverse ? 0x02 : 0x01);
            for (int i = 0; i < nameBytes.length; i++) {
                data[5 + i] = nameBytes[i];
            }
        }
    }


    public void setFilterMacA(String mac, boolean isReverse) {
        if (TextUtils.isEmpty(mac)) {
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) ParamsKeyEnum.KEY_FILTER_MAC_A.getParamsKey(),
                    (byte) 0x01,
                    (byte) 0x00,
            };

        } else {
            byte[] macBytes = MokoUtils.hex2bytes(mac);
            int length = macBytes.length + 1;
            data = new byte[4 + length];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = (byte) ParamsKeyEnum.KEY_FILTER_MAC_A.getParamsKey();
            data[3] = (byte) length;
            data[4] = (byte) (isReverse ? 0x02 : 0x01);
            for (int i = 0; i < macBytes.length; i++) {
                data[5 + i] = macBytes[i];
            }
        }
    }

    public void setFilterMajorRangeA(@IntRange(from = 0, to = 1) int enable,
                                     @IntRange(from = 0, to = 65535) int majorMin,
                                     @IntRange(from = 0, to = 65535) int majorMax,
                                     boolean isReverse) {
        if (enable == 0) {
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) ParamsKeyEnum.KEY_FILTER_MAJOR_RANGE_A.getParamsKey(),
                    (byte) 0x01,
                    (byte) 0x00,
            };
        } else {
            byte[] majorMinBytes = MokoUtils.toByteArray(majorMin, 2);
            byte[] majorMaxBytes = MokoUtils.toByteArray(majorMax, 2);
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) ParamsKeyEnum.KEY_FILTER_MAJOR_RANGE_A.getParamsKey(),
                    (byte) 0x05,
                    (byte) (isReverse ? 0x02 : 0x01),
                    majorMinBytes[0],
                    majorMinBytes[1],
                    majorMaxBytes[0],
                    majorMaxBytes[1],
            };
        }
    }

    public void setFilterMinorRangeA(@IntRange(from = 0, to = 1) int enable,
                                     @IntRange(from = 0, to = 65535) int minorMin,
                                     @IntRange(from = 0, to = 65535) int minorMax,
                                     boolean isReverse) {
        if (enable == 0) {
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) ParamsKeyEnum.KEY_FILTER_MINOR_RANGE_A.getParamsKey(),
                    (byte) 0x01,
                    (byte) 0x00,
            };
        } else {
            byte[] minorMinBytes = MokoUtils.toByteArray(minorMin, 2);
            byte[] minorMaxBytes = MokoUtils.toByteArray(minorMax, 2);
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) ParamsKeyEnum.KEY_FILTER_MINOR_RANGE_A.getParamsKey(),
                    (byte) 0x05,
                    (byte) (isReverse ? 0x02 : 0x01),
                    minorMinBytes[0],
                    minorMinBytes[1],
                    minorMaxBytes[0],
                    minorMaxBytes[1],
            };
        }
    }

    public void setFilterRawDataA(ArrayList<String> filterRawDatas, boolean isReverse) {
        if (filterRawDatas == null || filterRawDatas.size() == 0) {
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) ParamsKeyEnum.KEY_FILTER_ADV_RAW_DATA_A.getParamsKey(),
                    (byte) 0x01,
                    (byte) 0x00,
            };
        } else {
            StringBuffer stringBuffer = new StringBuffer();
            for (String rawData : filterRawDatas) {
                stringBuffer.append(rawData);
            }
            byte[] mRawDatas = MokoUtils.hex2bytes(stringBuffer.toString());
            final int length = mRawDatas.length + 1;
            data = new byte[length + 4];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = (byte) ParamsKeyEnum.KEY_FILTER_ADV_RAW_DATA_A.getParamsKey();
            data[3] = (byte) length;
            data[4] = (byte) (isReverse ? 0x02 : 0x01);
            for (int i = 0; i < mRawDatas.length; i++) {
                data[5 + i] = mRawDatas[i];
            }
        }
    }

    public void setFilterUUIDA(String uuid, boolean isReverse) {
        if (TextUtils.isEmpty(uuid)) {
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) ParamsKeyEnum.KEY_FILTER_UUID_A.getParamsKey(),
                    (byte) 0x01,
                    (byte) 0x00,
            };
        } else {
            byte[] uuidBytes = MokoUtils.hex2bytes(uuid);
            int length = uuidBytes.length + 1;
            data = new byte[4 + length];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = (byte) ParamsKeyEnum.KEY_FILTER_UUID_A.getParamsKey();
            data[3] = (byte) length;
            data[4] = (byte) (isReverse ? 0x02 : 0x01);
            for (int i = 0; i < uuidBytes.length; i++) {
                data[5 + i] = uuidBytes[i];
            }
        }
    }

    public void setFilterRssiA(@IntRange(from = -127, to = 0) int rssi) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_FILTER_RSSI_A.getParamsKey(),
                (byte) 0x01,
                (byte) rssi
        };
    }

    public void setFilterPHYA(@IntRange(from = 0, to = 1) int enable, @IntRange(from = 0, to = 2) int phy) {
        if (enable == 0) {
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) ParamsKeyEnum.KEY_FILTER_PHY_A.getParamsKey(),
                    (byte) 0x01,
                    (byte) enable,
            };
        } else {
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) ParamsKeyEnum.KEY_FILTER_PHY_A.getParamsKey(),
                    (byte) 0x02,
                    (byte) enable,
                    (byte) phy,
            };
        }
    }

    public void setFilterSwitchB(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_FILTER_SWITCH_B.getParamsKey(),
                (byte) 0x01,
                (byte) enable,
        };
    }

    public void setFilterNameB(String name, boolean isReverse) {
        if (TextUtils.isEmpty(name)) {
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) ParamsKeyEnum.KEY_FILTER_ADV_NAME_B.getParamsKey(),
                    (byte) 0x01,
                    (byte) 0x00,
            };
        } else {
            byte[] nameBytes = name.getBytes();
            int length = nameBytes.length + 1;
            data = new byte[4 + length];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = (byte) ParamsKeyEnum.KEY_FILTER_ADV_NAME_B.getParamsKey();
            data[3] = (byte) length;
            data[4] = (byte) (isReverse ? 0x02 : 0x01);
            for (int i = 0; i < nameBytes.length; i++) {
                data[5 + i] = nameBytes[i];
            }
        }
    }

    public void setFilterMacB(String mac, boolean isReverse) {
        if (TextUtils.isEmpty(mac)) {
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) ParamsKeyEnum.KEY_FILTER_MAC_B.getParamsKey(),
                    (byte) 0x01,
                    (byte) 0x00,
            };

        } else {
            byte[] macBytes = MokoUtils.hex2bytes(mac);
            int length = macBytes.length + 1;
            data = new byte[4 + length];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = (byte) ParamsKeyEnum.KEY_FILTER_MAC_B.getParamsKey();
            data[3] = (byte) length;
            data[4] = (byte) (isReverse ? 0x02 : 0x01);
            for (int i = 0; i < macBytes.length; i++) {
                data[5 + i] = macBytes[i];
            }
        }
    }

    public void setFilterMajorRangeB(@IntRange(from = 0, to = 1) int enable,
                                     @IntRange(from = 0, to = 65535) int majorMin,
                                     @IntRange(from = 0, to = 65535) int majorMax,
                                     boolean isReverse) {
        if (enable == 0) {
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) ParamsKeyEnum.KEY_FILTER_MAJOR_RANGE_B.getParamsKey(),
                    (byte) 0x01,
                    (byte) 0x00,
            };
        } else {
            byte[] majorMinBytes = MokoUtils.toByteArray(majorMin, 2);
            byte[] majorMaxBytes = MokoUtils.toByteArray(majorMax, 2);
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) ParamsKeyEnum.KEY_FILTER_MAJOR_RANGE_B.getParamsKey(),
                    (byte) 0x05,
                    (byte) (isReverse ? 0x02 : 0x01),
                    majorMinBytes[0],
                    majorMinBytes[1],
                    majorMaxBytes[0],
                    majorMaxBytes[1],
            };
        }
    }

    public void setFilterMinorRangeB(@IntRange(from = 0, to = 1) int enable,
                                     @IntRange(from = 0, to = 65535) int minorMin,
                                     @IntRange(from = 0, to = 65535) int minorMax,
                                     boolean isReverse) {
        if (enable == 0) {
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) ParamsKeyEnum.KEY_FILTER_MINOR_RANGE_B.getParamsKey(),
                    (byte) 0x01,
                    (byte) 0x00,
            };
        } else {
            byte[] minorMinBytes = MokoUtils.toByteArray(minorMin, 2);
            byte[] minorMaxBytes = MokoUtils.toByteArray(minorMax, 2);
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) ParamsKeyEnum.KEY_FILTER_MINOR_RANGE_B.getParamsKey(),
                    (byte) 0x05,
                    (byte) (isReverse ? 0x02 : 0x01),
                    minorMinBytes[0],
                    minorMinBytes[1],
                    minorMaxBytes[0],
                    minorMaxBytes[1],
            };
        }
    }

    public void setFilterRawDataB(ArrayList<String> filterRawDatas, boolean isReverse) {
        if (filterRawDatas == null || filterRawDatas.size() == 0) {
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) ParamsKeyEnum.KEY_FILTER_ADV_RAW_DATA_B.getParamsKey(),
                    (byte) 0x01,
                    (byte) 0x00,
            };
        } else {
            StringBuffer stringBuffer = new StringBuffer();
            for (String rawData : filterRawDatas) {
                stringBuffer.append(rawData);
            }
            byte[] mRawDatas = MokoUtils.hex2bytes(stringBuffer.toString());
            final int length = mRawDatas.length + 1;
            data = new byte[length + 4];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = (byte) ParamsKeyEnum.KEY_FILTER_ADV_RAW_DATA_B.getParamsKey();
            data[3] = (byte) length;
            data[4] = (byte) (isReverse ? 0x02 : 0x01);
            for (int i = 0; i < mRawDatas.length; i++) {
                data[5 + i] = mRawDatas[i];
            }
        }
    }

    public void setFilterUUIDB(String uuid, boolean isReverse) {
        if (TextUtils.isEmpty(uuid)) {
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) ParamsKeyEnum.KEY_FILTER_UUID_B.getParamsKey(),
                    (byte) 0x01,
                    (byte) 0x00,
            };
        } else {
            byte[] uuidBytes = MokoUtils.hex2bytes(uuid);
            int length = uuidBytes.length + 1;
            data = new byte[4 + length];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = (byte) ParamsKeyEnum.KEY_FILTER_UUID_B.getParamsKey();
            data[3] = (byte) length;
            data[4] = (byte) (isReverse ? 0x02 : 0x01);
            for (int i = 0; i < uuidBytes.length; i++) {
                data[5 + i] = uuidBytes[i];
            }
        }
    }

    public void setFilterRssiB(@IntRange(from = -127, to = 0) int rssi) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_FILTER_RSSI_B.getParamsKey(),
                (byte) 0x01,
                (byte) rssi
        };
    }


    public void setFilterPHYB(@IntRange(from = 0, to = 1) int enable, @IntRange(from = 0, to = 2) int phy) {
        if (enable == 0) {
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) ParamsKeyEnum.KEY_FILTER_PHY_B.getParamsKey(),
                    (byte) 0x01,
                    (byte) enable,
            };
        } else {
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) ParamsKeyEnum.KEY_FILTER_PHY_B.getParamsKey(),
                    (byte) 0x02,
                    (byte) enable,
                    (byte) phy,
            };
        }
    }


    public void setGPSColdStartTimeout(@IntRange(from = 3, to = 15) int timeout) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_GPS_COLD_START_TIMEOUT.getParamsKey(),
                (byte) 0x01,
                (byte) timeout,
        };
    }

    public void setGPSCoarseAccuracyMask(@IntRange(from = 5, to = 100) int mask) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_GPS_COARSE_ACCURACY_MASK.getParamsKey(),
                (byte) 0x01,
                (byte) mask,
        };
    }

    public void setGPSFineAccuracyMask(@IntRange(from = 5, to = 100) int mask) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_GPS_FINE_ACCURACY_MASK.getParamsKey(),
                (byte) 0x01,
                (byte) mask,
        };
    }

    public void setGPSCoarseTimeout(@IntRange(from = 1, to = 7620) int timeout) {
        byte[] timeoutBytes = MokoUtils.toByteArray(timeout, 2);
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_GPS_COARSE_TIMEOUT.getParamsKey(),
                (byte) 0x02,
                timeoutBytes[0],
                timeoutBytes[1]
        };
    }

    public void setGPSFineTimeout(@IntRange(from = 0, to = 76200) int timeout) {
        byte[] timeoutBytes = MokoUtils.toByteArray(timeout, 4);
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_GPS_FINE_TIMEOUT.getParamsKey(),
                (byte) 0x04,
                timeoutBytes[0],
                timeoutBytes[1],
                timeoutBytes[2],
                timeoutBytes[3]
        };
    }

    public void setGPSPDOPlimit(@IntRange(from = 25, to = 100) int limit) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_GPS_PDOP_LIMIT.getParamsKey(),
                (byte) 0x01,
                (byte) limit,
        };
    }

    public void setGPSFixMode(@IntRange(from = 0, to = 2) int mode) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_GPS_FIX_MODE.getParamsKey(),
                (byte) 0x01,
                (byte) mode,
        };
    }

    public void setGPSModel(@IntRange(from = 0, to = 9) int model) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_GPS_MODEL.getParamsKey(),
                (byte) 0x01,
                (byte) model,
        };
    }

    public void setGPSTimeBudget(@IntRange(from = 0, to = 76200) int budget) {
        byte[] budgetBytes = MokoUtils.toByteArray(budget, 4);
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_GPS_TIME_BUDGET.getParamsKey(),
                (byte) 0x04,
                budgetBytes[0],
                budgetBytes[1],
                budgetBytes[2],
                budgetBytes[3]
        };
    }

    public void setGPSAutonomousAiding(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_GPS_AUTONOMOUS_AIDING.getParamsKey(),
                (byte) 0x01,
                (byte) enable,
        };
    }

    public void setGPSAidingAccuracy(@IntRange(from = 5, to = 1000) int accuracy) {
        byte[] accuracyBytes = MokoUtils.toByteArray(accuracy, 2);
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_GPS_AIDING_ACCURACY.getParamsKey(),
                (byte) 0x02,
                accuracyBytes[0],
                accuracyBytes[1]
        };
    }

    public void setGPSAidingTimeout(@IntRange(from = 1, to = 7620) int timeout) {
        byte[] timeoutBytes = MokoUtils.toByteArray(timeout, 2);
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_GPS_AIDING_TIMEOUT.getParamsKey(),
                (byte) 0x02,
                timeoutBytes[0],
                timeoutBytes[1]
        };
    }

    public void setGPSExtremeMode(@IntRange(from = 0, to = 1) int mode) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_GPS_EXTREME_MODE.getParamsKey(),
                (byte) 0x01,
                (byte) mode
        };
    }

    public void setLoraRegion(@IntRange(from = 0, to = 9) int region) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LORA_REGION.getParamsKey(),
                (byte) 0x01,
                (byte) region
        };
    }

    public void setLoraUploadMode(@IntRange(from = 1, to = 2) int mode) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LORA_MODE.getParamsKey(),
                (byte) 0x01,
                (byte) mode
        };
    }

    public void setLoraDevEUI(String devEui) {
        byte[] rawDataBytes = MokoUtils.hex2bytes(devEui);
        int length = rawDataBytes.length;
        data = new byte[4 + length];
        data[0] = (byte) 0xED;
        data[1] = (byte) 0x01;
        data[2] = (byte) ParamsKeyEnum.KEY_LORA_DEV_EUI.getParamsKey();
        data[3] = (byte) length;
        for (int i = 0; i < length; i++) {
            data[i + 4] = rawDataBytes[i];
        }
    }

    public void setLoraAppEUI(String appEui) {
        byte[] rawDataBytes = MokoUtils.hex2bytes(appEui);
        int length = rawDataBytes.length;
        data = new byte[4 + length];
        data[0] = (byte) 0xED;
        data[1] = (byte) 0x01;
        data[2] = (byte) ParamsKeyEnum.KEY_LORA_APP_EUI.getParamsKey();
        data[3] = (byte) length;
        for (int i = 0; i < length; i++) {
            data[i + 4] = rawDataBytes[i];
        }
    }

    public void setLoraAppKey(String appKey) {
        byte[] rawDataBytes = MokoUtils.hex2bytes(appKey);
        int length = rawDataBytes.length;
        data = new byte[4 + length];
        data[0] = (byte) 0xED;
        data[1] = (byte) 0x01;
        data[2] = (byte) ParamsKeyEnum.KEY_LORA_APP_KEY.getParamsKey();
        data[3] = (byte) length;
        for (int i = 0; i < length; i++) {
            data[i + 4] = rawDataBytes[i];
        }
    }

    public void setLoraDevAddr(String devAddr) {
        byte[] rawDataBytes = MokoUtils.hex2bytes(devAddr);
        int length = rawDataBytes.length;
        data = new byte[4 + length];
        data[0] = (byte) 0xED;
        data[1] = (byte) 0x01;
        data[2] = (byte) ParamsKeyEnum.KEY_LORA_DEV_ADDR.getParamsKey();
        data[3] = (byte) length;
        for (int i = 0; i < length; i++) {
            data[i + 4] = rawDataBytes[i];
        }
    }

    public void setLoraAppSKey(String appSkey) {
        byte[] rawDataBytes = MokoUtils.hex2bytes(appSkey);
        int length = rawDataBytes.length;
        data = new byte[4 + length];
        data[0] = (byte) 0xED;
        data[1] = (byte) 0x01;
        data[2] = (byte) ParamsKeyEnum.KEY_LORA_APP_SKEY.getParamsKey();
        data[3] = (byte) length;
        for (int i = 0; i < length; i++) {
            data[i + 4] = rawDataBytes[i];
        }
    }

    public void setLoraNwkSKey(String nwkSkey) {
        byte[] rawDataBytes = MokoUtils.hex2bytes(nwkSkey);
        int length = rawDataBytes.length;
        data = new byte[4 + length];
        data[0] = (byte) 0xED;
        data[1] = (byte) 0x01;
        data[2] = (byte) ParamsKeyEnum.KEY_LORA_NWK_SKEY.getParamsKey();
        data[3] = (byte) length;
        for (int i = 0; i < length; i++) {
            data[i + 4] = rawDataBytes[i];
        }
    }

    public void setLoraMessageType(@IntRange(from = 0, to = 1) int type) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LORA_MESSAGE_TYPE.getParamsKey(),
                (byte) 0x01,
                (byte) type
        };
    }

    public void setLoraCH(int ch1, int ch2) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LORA_CH.getParamsKey(),
                (byte) 0x02,
                (byte) ch1,
                (byte) ch2
        };
    }

    public void setLoraDR(int dr1) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LORA_DR.getParamsKey(),
                (byte) 0x01,
                (byte) dr1
        };
    }

    public void setLoraUplinkStrategy(int adr, int number, int dr1, int dr2) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LORA_UPLINK_STRATEGY.getParamsKey(),
                (byte) 0x04,
                (byte) adr,
                (byte) number,
                (byte) dr1,
                (byte) dr2
        };
    }


    public void setLoraDutyCycleEnable(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LORA_DUTYCYCLE.getParamsKey(),
                (byte) 0x01,
                (byte) enable
        };
    }

    public void setLoraTimeSyncInterval(@IntRange(from = 0, to = 255) int interval) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LORA_TIME_SYNC_INTERVAL.getParamsKey(),
                (byte) 0x01,
                (byte) interval
        };
    }

    public void setLoraReconnectInterval(@IntRange(from = 0, to = 30) int days) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LORA_RECONNECT_INTERVAL.getParamsKey(),
                (byte) 0x01,
                (byte) days
        };
    }

    public void setBeaconEnable(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_BEACON_ENABLE.getParamsKey(),
                (byte) 0x01,
                (byte) enable
        };
    }

    public void setAdvInterval(@IntRange(from = 0, to = 100) int advInterval) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_ADV_INTERVAL.getParamsKey(),
                (byte) 0x01,
                (byte) advInterval
        };
    }

    public void setConnectable(@IntRange(from = 0, to = 1) int connectable) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_CONNECTABLE.getParamsKey(),
                (byte) 0x01,
                (byte) connectable
        };
    }

    public void setAdvTimeout(@IntRange(from = 1, to = 60) int timeout) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_ADV_TIMEOUT.getParamsKey(),
                (byte) 0x01,
                (byte) timeout
        };
    }

    public void setAdvUUID(String uuid) {
        byte[] uuidBytes = MokoUtils.hex2bytes(uuid);
        int length = uuidBytes.length;
        data = new byte[length + 4];
        data[0] = (byte) 0xED;
        data[1] = (byte) 0x01;
        data[2] = (byte) ParamsKeyEnum.KEY_ADV_UUID.getParamsKey();
        data[3] = (byte) length;
        for (int i = 0; i < uuidBytes.length; i++) {
            data[i + 4] = uuidBytes[i];
        }
    }

    public void setAdvMajor(@IntRange(from = 0, to = 65535) int major) {
        byte[] majorBytes = MokoUtils.toByteArray(major, 2);
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_ADV_MAJOR.getParamsKey(),
                (byte) 0x02,
                majorBytes[0],
                majorBytes[1],
        };
    }

    public void setAdvMinor(@IntRange(from = 0, to = 65535) int minor) {
        byte[] minorBytes = MokoUtils.toByteArray(minor, 2);
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_ADV_MINOR.getParamsKey(),
                (byte) 0x02,
                minorBytes[0],
                minorBytes[1],
        };
    }

    public void setAdvRSSI(int rssi) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_ADV_RSSI.getParamsKey(),
                (byte) 0x01,
                (byte) rssi
        };
    }

    public void setAdvTxPower(int txPower) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_ADV_TX_POWER.getParamsKey(),
                (byte) 0x01,
                (byte) txPower
        };
    }

    public void setAdvName(String advName) {
        byte[] advNameBytes = advName.getBytes();
        int length = advNameBytes.length;
        data = new byte[length + 4];
        data[0] = (byte) 0xED;
        data[1] = (byte) 0x01;
        data[2] = (byte) ParamsKeyEnum.KEY_ADV_NAME.getParamsKey();
        data[3] = (byte) length;
        for (int i = 0; i < advNameBytes.length; i++) {
            data[i + 4] = advNameBytes[i];
        }
    }

    public void setScanType(int scanType) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_SCAN_TYPE.getParamsKey(),
                (byte) 0x01,
                (byte) scanType
        };
    }

    public void setWakeupCondition(@IntRange(from = 1, to = 20) int threshold,
                                   @IntRange(from = 1, to = 10) int time) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_WAKEUP_CONDITION.getParamsKey(),
                (byte) 0x02,
                (byte) threshold,
                (byte) time
        };
    }

    public void setMotionDetection(@IntRange(from = 10, to = 250) int threshold,
                                   @IntRange(from = 1, to = 15) int time) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_MOTION_DETECTION.getParamsKey(),
                (byte) 0x02,
                (byte) threshold,
                (byte) time
        };
    }

    public void setVibrationEnable(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_VIBRATION_ENABLE.getParamsKey(),
                (byte) 0x01,
                (byte) enable
        };
    }

    public void setVibrationThreshold(@IntRange(from = 10, to = 255) int threshold) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_VIBRATION_THRESHOLD.getParamsKey(),
                (byte) 0x01,
                (byte) threshold
        };
    }

    public void setVibrationReportInterval(@IntRange(from = 3, to = 255) int interval) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_VIBRATION_REPORT_INTERVAL.getParamsKey(),
                (byte) 0x01,
                (byte) interval
        };
    }

    public void setVibrationTimeout(@IntRange(from = 1, to = 20) int timeout) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_VIBRATION_TIMEOUT.getParamsKey(),
                (byte) 0x01,
                (byte) timeout
        };
    }

    public void setManDownEnable(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_MAN_DOWN_ENABLE.getParamsKey(),
                (byte) 0x01,
                (byte) enable
        };
    }

    public void setManDownIdleTimeout(@IntRange(from = 1, to = 8760) int timeout) {
        byte[] timeoutBytes = MokoUtils.toByteArray(timeout, 2);
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_MAN_DOWN_IDLE_TIMEOUT.getParamsKey(),
                (byte) 0x02,
                timeoutBytes[0],
                timeoutBytes[1]
        };
    }

    public void setTamperAlarm(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_TAMPER_ALARM.getParamsKey(),
                (byte) 0x01,
                (byte) enable
        };
    }

    public void setActiveStateEnable(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_ACTIVE_STATE_ENABLE.getParamsKey(),
                (byte) 0x01,
                (byte) enable
        };
    }

    public void setActiveStateTimeout(@IntRange(from = 1, to = 86400) int interval) {
        byte[] intervalBytes = MokoUtils.toByteArray(interval, 4);
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_ACTIVE_STATE_TIMEOUT.getParamsKey(),
                (byte) 0x04,
                intervalBytes[0],
                intervalBytes[1],
                intervalBytes[2],
                intervalBytes[3]
        };
    }

    public void setManDownIdleClear() {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_MAN_DOWN_IDLE_CLEAR.getParamsKey(),
                (byte) 0x00
        };
    }

    public void readStorageData(@IntRange(from = 1, to = 65535) int time) {
        byte[] rawDataBytes = MokoUtils.toByteArray(time, 2);
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_READ_STORAGE_DATA.getParamsKey(),
                (byte) 0x02,
                rawDataBytes[0],
                rawDataBytes[1]
        };
    }

    public void setSyncEnable(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_SYNC_ENABLE.getParamsKey(),
                (byte) 0x01,
                (byte) enable
        };
    }

    public void clearStorageData() {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_CLEAR_STORAGE_DATA.getParamsKey(),
                (byte) 0x00
        };
    }
}
