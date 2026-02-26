package fiveg;

public enum BusinessType {
    // 心跳消息
    HEARTBEAT(0x01, 0x03, "Heartbeat Message"),
    HEARTBEAT_ACK(0x01, 0x04, "Heartbeat Acknowledgment"),

    // 设备启动通知
    INIT_NOTIFICATION(0x01, 0x01, "Init Notification"),
    INIT_NOTIFICATION_RSP(0x01, 0x02, "Init Notification Response"),

    // 版本查询
    GET_VERSION_REQ(0x01, 0x26, "Version Query Request"),
    GET_VERSION_RSP(0x01, 0x27, "Version Query Response"),

    // 功率设置
    POWER_SETTING(0x01, 0x15, "Power Setting Request"),
    POWER_SETTING_RSP(0x01, 0x16, "Power Setting Response"),

    // 小区配置
    CELL_UPDATE(0x01, 0x05, "Cell Update Request"),
    CELL_UPDATE_RSP(0x01, 0x06, "Cell Update Response"),

    // 重定向配置
    REDIRECT_MSG(0x01, 0x09, "Redirect Configuration Request"),
    REDIRECT_RSP(0x01, 0x0A, "Redirect Configuration Response"),

    // 黑名单设置
    BLACK_LIST_SETTING(0x01, 0x13, "Blacklist Setting Request"),
    BLACK_LIST_SETTING_RSP(0x01, 0x14, "Blacklist Setting Response"),

    // 白名单设置
    WHITE_LIST_SETTING(0x01, 0x15, "Whitelist Setting Request"),
    WHITE_LIST_SETTING_RSP(0x01, 0x16, "Whitelist Setting Response"),

    // 系统时间设置
    SYSTEM_TIME_REQ(0x02, 0x01, "System Time Request"),
    SYSTEM_TIME_RSP(0x02, 0x02, "System Time Response"),

    // 系统重启
    SYSTEM_REBOOT(0x02, 0x06, "System Reboot Request"),

    //系统状态上报
    SYSTEM_STATUS_REPORTING(0x02, 0x08, "System status reporting"),

    REPORT_UE_WITH_RSSI(0x02, 0x09, "REPORT_UE_WITH_RSSI"),
    REPORT_MEASURE(0x01, 0x1F, "REPORT_MEASURE"),

    // 通道设置
    SET_BAND_RF_CFG_REQ(0x01, 0x22, "Channel Setting Request"),
    SET_BAND_RF_CFG_RSP(0x01, 0x23, "Channel Setting Response"),

    // 工作模式查询
    WORK_MODEL_QUERY(0x01, 0x17, "Work Mode Query Request"),
    WORK_MODEL_QUERY_RSP(0x01, 0x18, "Work Mode Query Response"),

    // 定位模式
    START_MEASURE(0x01, 0x1B, "Start Measure Request"),
    START_MEASURE_RSP(0x01, 0x1C, "Start Measure Response"),
    STOP_MEASURE(0x01, 0x1D, "Stop Measure Request"),
    CELL_CONFIGURATION_QUERY_RESPONSE(0x01,0x08,"Cell configuration query Response"),

    STOP_MEASURE_RSP(0x01, 0x1E, "Stop Measure Response");

    private final byte protocolNumber;
    private final byte signalType;
    private final String description;

    BusinessType(int protocolNumber, int signalType, String description) {
        this.protocolNumber = (byte) protocolNumber;
        this.signalType = (byte) signalType;
        this.description = description;
    }

    public byte getProtocolNumber() {
        return protocolNumber;
    }

    public byte getSignalType() {
        return signalType;
    }

    public String getDescription() {
        return description;
    }

    // 根据 Protocol Number 和 Signal Type 获取对应的业务类型
    public static BusinessType from(byte protocolNumber, byte signalType) {
        for (BusinessType type : BusinessType.values()) {
            if (type.getProtocolNumber() == protocolNumber && type.getSignalType() == signalType) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown Business Type: ProtocolNumber=" + protocolNumber + " SignalType=" + signalType);
    }
}
