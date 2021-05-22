package com.moko.support.lw001.task;

import android.text.TextUtils;

import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.utils.MokoUtils;
import com.moko.support.lw001.entity.OrderCHAR;
import com.moko.support.lw001.entity.ParamsKeyEnum;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.IntRange;

public class ParamsTask extends OrderTask {
    public byte[] data;

    public ParamsTask() {
        super(OrderCHAR.CHAR_PARAMS, OrderTask.RESPONSE_TYPE_WRITE_NO_RESPONSE);
    }

    @Override
    public byte[] assemble() {
        return data;
    }

    public void setData(ParamsKeyEnum key) {
        switch (key) {
            case KEY_DEVICE_INFO_INTERVAL:
            case KEY_POWER_STATUS:
            case KEY_TAMPER_DETECTION:
            case KEY_DATA_REPORT_INTERVAL:
//            case KEY_DATA_SAVED_INTERVAL:
            case KEY_TRACKING_FILTER_REPEAT:
            case KEY_UPLINK_DATA_TYPE:
            case KEY_UPLINK_DATA_MAX_LENGTH:
            case KEY_DEVICE_MAC:
            case KEY_UPLINK_DATA_CONTENT:
            case KEY_OVER_LIMIT_ENABLE:
            case KEY_OVER_LIMIT_DURATION:
            case KEY_OVER_LIMIT_QTY:
            case KEY_OVER_LIMIT_RSSI:

            case KEY_LORA_REGION:
            case KEY_LORA_MODE:
            case KEY_LORA_CLASS_TYPE:
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
            case KEY_LORA_ADR:
            case KEY_MULTICAST_ENABLE:
            case KEY_MULTICAST_ADDR:
            case KEY_MULTICAST_APPSKEY:
            case KEY_MULTICAST_NWKSKEY:
            case KEY_NETWORK_CHECK_INTERVAL:
            case KEY_LORA_UPLINK_DELL_TIME:
            case KEY_LORA_DUTY_CYCLE_ENABLE:
            case KEY_TIME_SYNC_INTERVAL:

            case KEY_ADV_NAME:
            case KEY_ADV_INTERVAL:
            case KEY_SCAN_ENABLE:
            case KEY_SCAN_PARAMS:

            case KEY_TRACKING_FILTER_A_B_RELATION:
            case KEY_TRACKING_FILTER_SWITCH_A:
            case KEY_TRACKING_FILTER_ADV_NAME_A:
            case KEY_TRACKING_FILTER_MAC_A:
            case KEY_TRACKING_FILTER_MAJOR_RANGE_A:
            case KEY_TRACKING_FILTER_MINOR_RANGE_A:
            case KEY_TRACKING_FILTER_ADV_RAW_DATA_A:
            case KEY_TRACKING_FILTER_UUID_A:
            case KEY_TRACKING_FILTER_RSSI_A:

            case KEY_TRACKING_FILTER_SWITCH_B:
            case KEY_TRACKING_FILTER_ADV_NAME_B:
            case KEY_TRACKING_FILTER_MAC_B:
            case KEY_TRACKING_FILTER_MAJOR_RANGE_B:
            case KEY_TRACKING_FILTER_MINOR_RANGE_B:
            case KEY_TRACKING_FILTER_ADV_RAW_DATA_B:
            case KEY_TRACKING_FILTER_UUID_B:
            case KEY_TRACKING_FILTER_RSSI_B:
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

    public void setFilterRssiA(@IntRange(from = -127, to = 0) int rssi) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_TRACKING_FILTER_RSSI_A.getParamsKey(),
                (byte) 0x01,
                (byte) rssi
        };
    }

    public void setFilterMacA(String mac, boolean isReverse) {
        if (TextUtils.isEmpty(mac)) {
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) ParamsKeyEnum.KEY_TRACKING_FILTER_MAC_A.getParamsKey(),
                    (byte) 0x01,
                    (byte) 0x00,
            };

        } else {
            byte[] macBytes = MokoUtils.hex2bytes(mac);
            int length = macBytes.length + 1;
            data = new byte[4 + length];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = (byte) ParamsKeyEnum.KEY_TRACKING_FILTER_MAC_A.getParamsKey();
            data[3] = (byte) length;
            data[4] = (byte) (isReverse ? 0x02 : 0x01);
            for (int i = 0; i < macBytes.length; i++) {
                data[5 + i] = macBytes[i];
            }
        }
    }

    public void setFilterNameA(String name, boolean isReverse) {
        if (TextUtils.isEmpty(name)) {
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) ParamsKeyEnum.KEY_TRACKING_FILTER_ADV_NAME_A.getParamsKey(),
                    (byte) 0x01,
                    (byte) 0x00,
            };
        } else {
            byte[] nameBytes = name.getBytes();
            int length = nameBytes.length + 1;
            data = new byte[4 + length];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = (byte) ParamsKeyEnum.KEY_TRACKING_FILTER_ADV_NAME_A.getParamsKey();
            data[3] = (byte) length;
            data[4] = (byte) (isReverse ? 0x02 : 0x01);
            for (int i = 0; i < nameBytes.length; i++) {
                data[5 + i] = nameBytes[i];
            }
        }
    }

    public void setFilterUUIDA(String uuid, boolean isReverse) {
        if (TextUtils.isEmpty(uuid)) {
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) ParamsKeyEnum.KEY_TRACKING_FILTER_UUID_A.getParamsKey(),
                    (byte) 0x01,
                    (byte) 0x00,
            };
        } else {
            byte[] uuidBytes = MokoUtils.hex2bytes(uuid);
            int length = uuidBytes.length + 1;
            data = new byte[4 + length];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = (byte) ParamsKeyEnum.KEY_TRACKING_FILTER_UUID_A.getParamsKey();
            data[3] = (byte) length;
            data[4] = (byte) (isReverse ? 0x02 : 0x01);
            for (int i = 0; i < uuidBytes.length; i++) {
                data[5 + i] = uuidBytes[i];
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
                    (byte) ParamsKeyEnum.KEY_TRACKING_FILTER_MAJOR_RANGE_A.getParamsKey(),
                    (byte) 0x01,
                    (byte) 0x00,
            };
        } else {
            byte[] majorMinBytes = MokoUtils.toByteArray(majorMin, 2);
            byte[] majorMaxBytes = MokoUtils.toByteArray(majorMax, 2);
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) ParamsKeyEnum.KEY_TRACKING_FILTER_MAJOR_RANGE_A.getParamsKey(),
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
                    (byte) ParamsKeyEnum.KEY_TRACKING_FILTER_MINOR_RANGE_A.getParamsKey(),
                    (byte) 0x01,
                    (byte) 0x00,
            };
        } else {
            byte[] minorMinBytes = MokoUtils.toByteArray(minorMin, 2);
            byte[] minorMaxBytes = MokoUtils.toByteArray(minorMax, 2);
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) ParamsKeyEnum.KEY_TRACKING_FILTER_MINOR_RANGE_A.getParamsKey(),
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
                    (byte) ParamsKeyEnum.KEY_TRACKING_FILTER_ADV_RAW_DATA_A.getParamsKey(),
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
            data[2] = (byte) ParamsKeyEnum.KEY_TRACKING_FILTER_ADV_RAW_DATA_A.getParamsKey();
            data[3] = (byte) length;
            data[4] = (byte) (isReverse ? 0x02 : 0x01);
            for (int i = 0; i < mRawDatas.length; i++) {
                data[5 + i] = mRawDatas[i];
            }
        }
    }

    public void setFilterSwitchA(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_TRACKING_FILTER_SWITCH_A.getParamsKey(),
                (byte) 0x01,
                (byte) enable,
        };
    }

    public void setFilterRssiB(@IntRange(from = -127, to = 0) int rssi) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_TRACKING_FILTER_RSSI_B.getParamsKey(),
                (byte) 0x01,
                (byte) rssi
        };
    }

    public void setFilterMacB(String mac, boolean isReverse) {
        if (TextUtils.isEmpty(mac)) {
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) ParamsKeyEnum.KEY_TRACKING_FILTER_MAC_B.getParamsKey(),
                    (byte) 0x01,
                    (byte) 0x00,
            };

        } else {
            byte[] macBytes = MokoUtils.hex2bytes(mac);
            int length = macBytes.length + 1;
            data = new byte[4 + length];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = (byte) ParamsKeyEnum.KEY_TRACKING_FILTER_MAC_B.getParamsKey();
            data[3] = (byte) length;
            data[4] = (byte) (isReverse ? 0x02 : 0x01);
            for (int i = 0; i < macBytes.length; i++) {
                data[5 + i] = macBytes[i];
            }
        }
    }

    public void setFilterNameB(String name, boolean isReverse) {
        if (TextUtils.isEmpty(name)) {
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) ParamsKeyEnum.KEY_TRACKING_FILTER_ADV_NAME_B.getParamsKey(),
                    (byte) 0x01,
                    (byte) 0x00,
            };
        } else {
            byte[] nameBytes = name.getBytes();
            int length = nameBytes.length + 1;
            data = new byte[4 + length];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = (byte) ParamsKeyEnum.KEY_TRACKING_FILTER_ADV_NAME_B.getParamsKey();
            data[3] = (byte) length;
            data[4] = (byte) (isReverse ? 0x02 : 0x01);
            for (int i = 0; i < nameBytes.length; i++) {
                data[5 + i] = nameBytes[i];
            }
        }
    }

    public void setFilterUUIDB(String uuid, boolean isReverse) {
        if (TextUtils.isEmpty(uuid)) {
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) ParamsKeyEnum.KEY_TRACKING_FILTER_UUID_B.getParamsKey(),
                    (byte) 0x01,
                    (byte) 0x00,
            };
        } else {
            byte[] uuidBytes = MokoUtils.hex2bytes(uuid);
            int length = uuidBytes.length + 1;
            data = new byte[4 + length];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = (byte) ParamsKeyEnum.KEY_TRACKING_FILTER_UUID_B.getParamsKey();
            data[3] = (byte) length;
            data[4] = (byte) (isReverse ? 0x02 : 0x01);
            for (int i = 0; i < uuidBytes.length; i++) {
                data[5 + i] = uuidBytes[i];
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
                    (byte) ParamsKeyEnum.KEY_TRACKING_FILTER_MAJOR_RANGE_B.getParamsKey(),
                    (byte) 0x01,
                    (byte) 0x00,
            };
        } else {
            byte[] majorMinBytes = MokoUtils.toByteArray(majorMin, 2);
            byte[] majorMaxBytes = MokoUtils.toByteArray(majorMax, 2);
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) ParamsKeyEnum.KEY_TRACKING_FILTER_MAJOR_RANGE_B.getParamsKey(),
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
                    (byte) ParamsKeyEnum.KEY_TRACKING_FILTER_MINOR_RANGE_B.getParamsKey(),
                    (byte) 0x01,
                    (byte) 0x00,
            };
        } else {
            byte[] minorMinBytes = MokoUtils.toByteArray(minorMin, 2);
            byte[] minorMaxBytes = MokoUtils.toByteArray(minorMax, 2);
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) ParamsKeyEnum.KEY_TRACKING_FILTER_MINOR_RANGE_B.getParamsKey(),
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
                    (byte) ParamsKeyEnum.KEY_TRACKING_FILTER_ADV_RAW_DATA_B.getParamsKey(),
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
            data[2] = (byte) ParamsKeyEnum.KEY_TRACKING_FILTER_ADV_RAW_DATA_B.getParamsKey();
            data[3] = (byte) length;
            data[4] = (byte) (isReverse ? 0x02 : 0x01);
            for (int i = 0; i < mRawDatas.length; i++) {
                data[5 + i] = mRawDatas[i];
            }
        }
    }

    public void setFilterSwitchB(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_TRACKING_FILTER_SWITCH_B.getParamsKey(),
                (byte) 0x01,
                (byte) enable,
        };
    }

    public void setFilterABRelation(@IntRange(from = 0, to = 1) int relation) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_TRACKING_FILTER_A_B_RELATION.getParamsKey(),
                (byte) 0x01,
                (byte) relation,
        };
    }

    public void setFilterRepeat(@IntRange(from = 0, to = 3) int repeat) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_TRACKING_FILTER_REPEAT.getParamsKey(),
                (byte) 0x01,
                (byte) repeat,
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


    public void setAdvInterval(int advInterval) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_ADV_INTERVAL.getParamsKey(),
                (byte) 0x01,
                (byte) advInterval
        };
    }


    public void setDeviceInfoInterval(@IntRange(from = 1, to = 14400) int interval) {
        byte[] intervalBytes = MokoUtils.toByteArray(interval, 2);
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_DEVICE_INFO_INTERVAL.getParamsKey(),
                (byte) 0x02,
                intervalBytes[0],
                intervalBytes[1]
        };
    }

    public void setDataReportInterval(@IntRange(from = 10, to = 65535) int interval) {
        byte[] intervalBytes = MokoUtils.toByteArray(interval, 2);
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_DATA_REPORT_INTERVAL.getParamsKey(),
                (byte) 0x02,
                intervalBytes[0],
                intervalBytes[1]
        };
    }

    public void setUplinkDataType(@IntRange(from = 0, to = 255) int payload) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_UPLINK_DATA_TYPE.getParamsKey(),
                (byte) 0x01,
                (byte) payload
        };
    }

    public void setUplinkDataMaxLength(@IntRange(from = 0, to = 1) int maxLength) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_UPLINK_DATA_MAX_LENGTH.getParamsKey(),
                (byte) 0x01,
                (byte) maxLength
        };
    }

    public void setUplinkDataContent(@IntRange(from = 0, to = 255) int payload) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_UPLINK_DATA_CONTENT.getParamsKey(),
                (byte) 0x01,
                (byte) payload
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

    public void reset() {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_RESET.getParamsKey(),
                (byte) 0x00
        };
    }

    public void setScanEnable(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_SCAN_ENABLE.getParamsKey(),
                (byte) 0x01,
                (byte) enable
        };
    }

    public void setScanParams(@IntRange(from = 1, to = 16) int window) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_SCAN_PARAMS.getParamsKey(),
                (byte) 0x01,
                (byte) window
        };
    }

    public void setOverLimitEnable(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_OVER_LIMIT_ENABLE.getParamsKey(),
                (byte) 0x01,
                (byte) enable
        };
    }

    public void setOverLimitRssi(@IntRange(from = -127, to = 0) int rssi) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_OVER_LIMIT_RSSI.getParamsKey(),
                (byte) 0x01,
                (byte) rssi
        };
    }

    public void setOverLimitQty(@IntRange(from = 1, to = 255) int qty) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_OVER_LIMIT_QTY.getParamsKey(),
                (byte) 0x01,
                (byte) qty
        };
    }

    public void setOverLimitDuration(@IntRange(from = 1, to = 600) int duration) {
        byte[] rawDataBytes = MokoUtils.toByteArray(duration, 2);
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_OVER_LIMIT_DURATION.getParamsKey(),
                (byte) 0x02,
                rawDataBytes[0],
                rawDataBytes[1]
        };
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

    public void setLoraDevEui(String devEui) {
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

    public void setLoraAppEui(String appEui) {
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

    public void setLoraUploadMode(int mode) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LORA_MODE.getParamsKey(),
                (byte) 0x01,
                (byte) mode
        };
    }

    public void setLoraClassType(int type) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LORA_CLASS_TYPE.getParamsKey(),
                (byte) 0x01,
                (byte) type
        };
    }

    public void setLoraMessageType(int type) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LORA_MESSAGE_TYPE.getParamsKey(),
                (byte) 0x01,
                (byte) type
        };
    }

    public void setLoraRegion(int region) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LORA_REGION.getParamsKey(),
                (byte) 0x01,
                (byte) region
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

    public void setLoraDutyCycleEnable(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LORA_DUTY_CYCLE_ENABLE.getParamsKey(),
                (byte) 0x01,
                (byte) enable
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

    public void setLoraADR(int adr) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LORA_ADR.getParamsKey(),
                (byte) 0x01,
                (byte) adr
        };
    }

    public void setLoraUplinkDellTime(@IntRange(from = 0, to = 1) int uplinkDellTime) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LORA_UPLINK_DELL_TIME.getParamsKey(),
                (byte) 0x01,
                (byte) uplinkDellTime
        };
    }


    public void setTimeSyncInterval(@IntRange(from = 0, to = 240) int timeSyncInterval) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_TIME_SYNC_INTERVAL.getParamsKey(),
                (byte) 0x01,
                (byte) timeSyncInterval
        };
    }

    public void setNetworkCheckInterval(@IntRange(from = 0, to = 720) int interval) {
        byte[] rawDataBytes = MokoUtils.toByteArray(interval, 2);
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_NETWORK_CHECK_INTERVAL.getParamsKey(),
                (byte) 0x02,
                rawDataBytes[0],
                rawDataBytes[1]
        };
    }

    public void setMulticastEnable(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_MULTICAST_ENABLE.getParamsKey(),
                (byte) 0x01,
                (byte) enable
        };
    }

    public void setMulticastAddr(String addr) {
        byte[] rawDataBytes = MokoUtils.hex2bytes(addr);
        int length = rawDataBytes.length;
        data = new byte[4 + length];
        data[0] = (byte) 0xED;
        data[1] = (byte) 0x01;
        data[2] = (byte) ParamsKeyEnum.KEY_MULTICAST_ADDR.getParamsKey();
        data[3] = (byte) length;
        for (int i = 0; i < length; i++) {
            data[i + 4] = rawDataBytes[i];
        }
    }

    public void setMulticastAppSKey(String appSkey) {
        byte[] rawDataBytes = MokoUtils.hex2bytes(appSkey);
        int length = rawDataBytes.length;
        data = new byte[4 + length];
        data[0] = (byte) 0xED;
        data[1] = (byte) 0x01;
        data[2] = (byte) ParamsKeyEnum.KEY_MULTICAST_APPSKEY.getParamsKey();
        data[3] = (byte) length;
        for (int i = 0; i < length; i++) {
            data[i + 4] = rawDataBytes[i];
        }
    }

    public void setMulticastNwkSKey(String nwkSkey) {
        byte[] rawDataBytes = MokoUtils.hex2bytes(nwkSkey);
        int length = rawDataBytes.length;
        data = new byte[4 + length];
        data[0] = (byte) 0xED;
        data[1] = (byte) 0x01;
        data[2] = (byte) ParamsKeyEnum.KEY_MULTICAST_NWKSKEY.getParamsKey();
        data[3] = (byte) length;
        for (int i = 0; i < length; i++) {
            data[i + 4] = rawDataBytes[i];
        }
    }


    public void setPowerStatus(@IntRange(from = 0, to = 2) int status) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_POWER_STATUS.getParamsKey(),
                (byte) 0x01,
                (byte) status
        };
    }

    public void setTamperDetection(@IntRange(from = 0, to = 1) int enable,
                                   @IntRange(from = 0, to = 240) int triggerSensitivity) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_TAMPER_DETECTION.getParamsKey(),
                (byte) 0x02,
                (byte) enable,
                (byte) triggerSensitivity
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
