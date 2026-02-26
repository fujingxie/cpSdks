package fiveg;

import java.nio.ByteBuffer;
import java.util.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class Message {

    private static int sequenceNumber = 1;  // 自增序列号
    private byte protocolNumber;
    private byte signalType;
    private int length;
    private Map<Integer, Tag> tags = new HashMap<>();

    public Message(byte protocolNumber, byte signalType, Map<Integer, Tag> tags,boolean addNo) {
        this.protocolNumber = protocolNumber;
        this.signalType = signalType;
        this.tags = tags;
        if (addNo==true){
            addSequenceNumber();  // 自动添加序列号
        }
        this.length = calculateLength();  // 自动计算消息长度
    }
    public Message(byte protocolNumber, byte signalType, Map<Integer, Tag> tags) {
        this.protocolNumber = protocolNumber;
        this.signalType = signalType;
        this.tags = tags;
        this.length = calculateLength();  // 自动计算消息长度
    }
    public Message() {
    }

    // 将 Message 转换为字节数组
    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(6 + getTotalTagLength());
        buffer.put(protocolNumber);
        buffer.put(signalType);
        buffer.putInt(length);

        // 把所有的 Tag 放入字节流
        for (Tag tag : tags.values()) {
            buffer.put(tag.toByteArray());
        }

        return buffer.array();
    }

    // 从字节数组解析 Message
    public static Message fromBytes(byte[] bytes) {
        Message message = new Message();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        message.protocolNumber = buffer.get();
        message.signalType = buffer.get();
        message.length = buffer.getInt();

        // 解析 Tag
        while (buffer.remaining() > 0) {
            int tagId = buffer.get();
            int tagLength = buffer.getInt();
            byte[] tagContent = new byte[tagLength];
            buffer.get(tagContent);

            Tag tag = new Tag(tagId, tagLength, tagContent);
            message.tags.put(tagId, tag);
        }

        return message;
    }

    // 自动添加 sequence number 的 Tag
    private void addSequenceNumber() {
        sequenceNumber++;  // 每发送一条消息，序列号递增
        tags.put(1, new Tag(1, 4,ByteBuffer.allocate(4).putInt(sequenceNumber).array()));  // TAG_SEQ_NUMBER = 1
    }

    // 获取 Tag 的长度总和
    private int getTotalTagLength() {
        int total = 0;
        for (Tag tag : tags.values()) {
            total += tag.getTotalLength();
        }
        return total;
    }
    // 获取业务类型
    public BusinessType getBusinessType() {
        return BusinessType.from(protocolNumber, signalType);
    }
    // 自动计算消息的长度
    private int calculateLength() {
        return getTotalTagLength();  // 不包含消息头部的6个字节
    }

    // Getter 和 Setter 方法
    public byte getProtocolNumber() {
        return protocolNumber;
    }

    public void setProtocolNumber(byte protocolNumber) {
        this.protocolNumber = protocolNumber;
    }

    public byte getSignalType() {
        return signalType;
    }

    public void setSignalType(byte signalType) {
        this.signalType = signalType;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Map<Integer, Tag> getTags() {
        return tags;
    }

    public void setTags(Map<Integer, Tag> tags) {
        this.tags = tags;
    }

    // 根据 Tag ID 获取特定 Tag
    public Tag getTag(int tagId) {
        return tags.get(tagId);
    }

    public static Message createHeartbeatResponseMessage() {
        return new Message((byte) 0x01, (byte) 0x03, new HashMap<>(),true);  // TAG_SEQ_NUMBER 会自动添加
    }
    public static Message createInitNotificationResponse() {
        Map<Integer, Tag> tags = new HashMap<>();
        tags.put(5, new Tag(5, 1, new byte[]{0x00}));  // 状态码为0（成功）
        return new Message((byte) 0x01, (byte) 0x02, tags);
    }
    public static Message createPowerSettingMessage(int powerValue) {
        Map<Integer, Tag> tags = new HashMap<>();
        tags.put(53, new Tag(53, 1, new byte[]{(byte) powerValue}));  // TAG_TX_PWR = 53
        return new Message((byte) 0x01, (byte) 0x15, tags);
    }

    public static Message createCellUpdateMessage(
            ArrayList<Integer> plmnIds, int tac, int pci, int band, int ssbArfcn, int syncMode,
            int Power, int rxGain, int tacUpdatePeriod, int REJ_CAUSE) {

        Map<Integer, Tag> tags = new HashMap<>();
        // 添加 Band 的 Tag (TAG_BAND)
        tags.put(13, new Tag(13, 2, ByteBuffer.allocate(2).putShort((short) band).array()));  // TAG_BAND
        // 添加 GPS delay
        tags.put(52, new Tag(52, 4, ByteBuffer.allocate(4).putInt(3000).array())); // TAG_GPS_DELAY
        // 添加 PCI 的 Tag (TAG_PCI)
        tags.put(16, new Tag(16, 8, ByteBuffer.allocate(8).putLong(19079732).array()));  // TAG_PCI
        // 添加 PCI 的 Tag (TAG_PCI)
        tags.put(17, new Tag(17, 1,new byte[]{(byte) 0x23}));  // TAG_PCI
        tags.put(26, new Tag(26, 2, ByteBuffer.allocate(2).putShort((short) -64).array()));  // TAG_BAND
        tags.put(19, new Tag(19, 1, new byte[]{(byte) 0}));  // TAG_BAND
        tags.put(21, new Tag(21, 1, new byte[]{(byte) 0}));  // TAG_BAND
        tags.put(23, new Tag(23, 1, new byte[]{(byte) 3}));  // TAG_BAND

        int[] band_list=new int[]{0,0,0};
        for (int i = 0; i < band_list.length; i++) {
            ByteBuffer plmnBuffer = ByteBuffer.allocate(4 );  // 每个 PLMN ID 是 4 字节
            plmnBuffer.putInt(band_list[i]);
            tags.put(81+i, new Tag(81+i, 4 , plmnBuffer.array()));  // TAG_PLMN, 包含所有的 PLMN ID
        }

        tags.put(69, new Tag(69, 4, ByteBuffer.allocate(4).putInt(16777215).array())); // TAG_GPS_DELAY
        tags.put(70, new Tag(70, 4, ByteBuffer.allocate(4).putInt(1).array())); // TAG_GPS_DELAY

        // 添加 PCI 的 Tag (TAG_PCI)
        tags.put(11, new Tag(11, 2, ByteBuffer.allocate(2).putShort((short) pci).array()));  // TAG_PCI
        // 添加接收增益的 Tag (TAG_RX_GAIN)
        tags.put(18, new Tag(18, 1, new byte[]{(byte) rxGain}));  // TAG_RX_GAIN

        // 添加 PLMN 数量的 Tag (TAG_NO_OF_PLMN)
        tags.put(8, new Tag(8, 4, ByteBuffer.allocate(4).putInt(plmnIds.size()).array()));  // TAG_NO_OF_PLMN

        // 添加 PLMN ID 列表的 Tag (TAG_PLMN)
        for (int i = 0; i < plmnIds.size(); i++) {
            ByteBuffer plmnBuffer = ByteBuffer.allocate(4 );  // 每个 PLMN ID 是 4 字节
            plmnBuffer.putInt(plmnIds.get(i));
            tags.put(71+i, new Tag(71+i, 4 , plmnBuffer.array()));  // TAG_PLMN, 包含所有的 PLMN ID
        }
        // 添加 SSB ARFCN 的 Tag (TAG_SSB_ARFCN)
        tags.put(12, new Tag(12, 4, ByteBuffer.allocate(4).putInt(ssbArfcn).array()));  // TAG_SSB_ARFCN
        // 添加 TAC 的 Tag (TAG_TAC)
        tags.put(10, new Tag(10, 4, ByteBuffer.allocate(4).putInt(tac).array()));  // TAG_TAC
        // 添加 小区发射功率 Tag (TAG_TX_PWR)
        tags.put(53, new Tag(53, 1,new byte[]{(byte) Power}));  // TAG_TAC_UPDATE_PERIOD_IN_SEC
        // 添加同步模式的 Tag (TAG_SYNC_MODE)
        tags.put(15, new Tag(15, 1, new byte[]{(byte) syncMode}));  // TAG_SYNC_MODE
        // 添加 TAC 更新周期的 Tag (TAG_TAC_UPDATE_PERIOD_IN_SEC)
        tags.put(30, new Tag(30, 4, ByteBuffer.allocate(4).putInt(tacUpdatePeriod).array()));  // TAG_TAC_UPDATE_PERIOD_IN_SEC
        // 添加 拒绝因子 Tag (TAG_CMM_REJ_CAUSE)
        tags.put(32, new Tag(32, 1,new byte[]{(byte) REJ_CAUSE}));  // TAG_CMM_REJ_CAUSE


        // 返回构建完成的消息
        return new Message((byte) 0x01, (byte) 0x05, tags);  // SignalType = 0x05 for CELL_UPDATE
    }
    /**
     * 小区配置消息 (CELL_UPDATE)
     * @param plmnIds
     * @param tac
     * @param pci
     * @param band
     * @return
     */
    public static Message createCellUpdateMessage(int[] plmnIds, int tac, int pci, int band) {
        Map<Integer, Tag> tags = new HashMap<>();

        // 添加 PLMN 数量的 Tag (TAG_NO_OF_PLMN)
        tags.put(8, new Tag(8, 4, ByteBuffer.allocate(4).putInt(plmnIds.length).array()));  // TAG_NO_OF_PLMN

        // 添加 PLMN ID 列表的 Tag (TAG_PLMN)
//        ByteBuffer plmnBuffer = ByteBuffer.allocate(4 * plmnIds.length);  // 每个 PLMN ID 是 4 字节
        for (int i = 0; i < plmnIds.length; i++) {
            ByteBuffer plmnBuffer = ByteBuffer.allocate(4 );  // 每个 PLMN ID 是 4 字节
            plmnBuffer.putInt(plmnIds[i]);
            tags.put(71+i, new Tag(71+i, 4 , plmnBuffer.array()));  // TAG_PLMN, 包含所有的 PLMN ID
        }
        // 添加其他参数，如 TAC、PCI 和 BAND
        tags.put(10, new Tag(10, 4, ByteBuffer.allocate(4).putInt(tac).array()));  // TAG_TAC
        tags.put(11, new Tag(11, 2, ByteBuffer.allocate(2).putShort((short) pci).array()));  // TAG_PCI
        tags.put(13, new Tag(13, 2, ByteBuffer.allocate(2).putShort((short) band).array()));  // TAG_BAND

        return new Message((byte) 0x01, (byte) 0x05, tags);  // SignalType = 0x05 for CELL_UPDATE
    }

    /**
     * 系统时间设置
     * @param unixTime
     * @return
     */
    public static Message createSystemTimeMessage(long unixTime) {
        Map<Integer, Tag> tags = new HashMap<>();
        tags.put(3, new Tag(3, 8, ByteBuffer.allocate(8).putLong(unixTime).array()));  // TAG_UTC_TIME
        return new Message((byte) 0x02, (byte) 0x01, tags);  // SignalType = 0x01 for SYSTEM_TIME
    }

    /**
     * 工作模式设置
     * @param workType
     * @return
     */
    public static Message createWorkModelSettingMessage(int workType) {
        Map<Integer, Tag> tags = new HashMap<>();
        tags.put(27, new Tag(27, 1, new byte[]{(byte) workType}));  // TAG_WORKTYPE
        return new Message((byte) 0x01, (byte) 0x18, tags);  // SignalType = 0x17 for WORK_MODEL_SETTING
    }

    /**
     * 白名单配置
     * @param imsi
     * @return
     */
    public static Message createWhiteListSettingMessage(String imsi) {
        Map<Integer, Tag> tags = new HashMap<>();
        tags.put(67, new Tag(67, imsi.length(), imsi.getBytes()));  // TAG_WHITE_LIST, IMSI as string
        return new Message((byte) 0x01, (byte) 0x15, tags);  // SignalType = 0x15 for WHITE_LIST_SETTING
    }

    /**
     * 黑名单配置
     * @param imsi
     * @return
     */
    public static Message createBlackListSettingMessage(String imsi) {
        Map<Integer, Tag> tags = new HashMap<>();
        tags.put(66, new Tag(66, imsi.length(), imsi.getBytes()));  // TAG_BLACK_LIST, IMSI as string
        return new Message((byte) 0x01, (byte) 0x13, tags);  // SignalType = 0x13 for BLACK_LIST_SETTING
    }

    /**
     * 重定向消息
     * @param mcc
     * @param mnc
     * @param rat
     * @param band
     * @param fcn
     * @return
     */
    public static Message createRedirectMessage(int mcc, int mnc, int rat, int band, int fcn) {
        Map<Integer, Tag> tags = new HashMap<>();
        tags.put(45, new Tag(45, 1, new byte[]{(byte) mcc}));  // TAG_ITEM_MCC
        tags.put(46, new Tag(46, 1, new byte[]{(byte) mnc}));  // TAG_ITEM_MNC
        tags.put(47, new Tag(47, 1, new byte[]{(byte) rat}));  // TAG_ITEM_RAT
        tags.put(48, new Tag(48, 2, ByteBuffer.allocate(2).putShort((short) band).array()));  // TAG_ITEM_BAND
        tags.put(49, new Tag(49, 4, ByteBuffer.allocate(4).putInt(fcn).array()));  // TAG_ITEM_FCN
        return new Message((byte) 0x01, (byte) 0x09, tags);  // SignalType = 0x09 for REDIRECT_MSG
    }

    /**
     * 小区配置查询
     * @return
     */
    public static Message createCellConfigSearchMessage() {
        return new Message((byte) 0x01, (byte) 0x07, new HashMap<>());  // SignalType = 0x07 for CELL_CONFIG_SEARCH
    }

    /**
     * 系统重启
     * @param rebootReason
     * @return
     */
    public static Message createSystemRebootMessage(int rebootReason) {
        Map<Integer, Tag> tags = new HashMap<>();
        tags.put(4, new Tag(4, 1, new byte[]{(byte) rebootReason}));  // TAG_REBOOT_REASON, 1 字节
        return new Message((byte) 0x02, (byte) 0x06, tags);  // SignalType = 0x06 for SYSTEM_REBOOT
    }
    /**
     * 系统重启
     * @param rebootReason
     * @return
     */
    public static Message createSystemRebootMessage() {
        Map<Integer, Tag> tags = new HashMap<>();
        tags.put(4, new Tag(4, 6, new byte[]{(byte) 0xe5,(byte)0xa4, (byte)0x8d, (byte)0xe4 ,(byte)0xbd, (byte)0x8d}));  // TAG_REBOOT_REASON, 1 字节
        return new Message((byte) 0x02, (byte) 0x06, tags);  // SignalType = 0x06 for SYSTEM_REBOOT
    }
    /**
     * 通道设置
     * @param band
     * @param svsAnt
     * @param snifferAnt
     * @param txAttInDb
     * @return
     */
    public static Message createChannelSettingMessage(int band, int svsAnt, int snifferAnt, int txAttInDb) {
        Map<Integer, Tag> tags = new HashMap<>();
        tags.put(13, new Tag(48, 2, ByteBuffer.allocate(2).putShort((short) band).array()));  // TAG_BAND
        tags.put(62, new Tag(62, 1, new byte[]{(byte) svsAnt}));  // TAG_SVS_ANT, 射频输出通道
        tags.put(63, new Tag(63, 1, new byte[]{(byte) snifferAnt}));  // TAG_SNIFFER_ANT, 扫频通道
        tags.put(64, new Tag(64, 1, new byte[]{(byte) txAttInDb}));  // TAG_TX_ATT_IN_DB, 定标功率
        return new Message((byte) 0x01, (byte) 0x22, tags);  // SignalType = 0x22 for SET_BAND_RF_CFG_REQ
    }

    /**
     * 开始定位模
     * @param imsiList
     * @return
     */
    public static Message createStartMeasureMessage(String imsiList) {
        Map<Integer, Tag> tags = new HashMap<>();
        tags.put(56, new Tag(56, imsiList.length(), imsiList.getBytes()));  // TAG_IMSI, IMSI 列表
        return new Message((byte) 0x01, (byte) 0x1B, tags);  // SignalType = 0x1B for START_MEASURE_REQ
    }

    /**
     * 停止定位模式
     * @return
     */
    public static Message createStopMeasureMessage() {
        return new Message((byte) 0x01, (byte) 0x1D, new HashMap<>());  // SignalType = 0x1D for STOP_MEASURE_REQ
    }

    /**
     * 版本查询
     * @return
     */
    public static Message queryVersionMessage() {
        return new Message((byte) 0x01, (byte) 0x26, new HashMap<>());  // SignalType = 0x1D for STOP_MEASURE_REQ
    }
    /**
     * 工作模式查询响应
     * @param workType
     * @return
     */
    public static Message createWorkModelQueryResponse(int workType) {
        Map<Integer, Tag> tags = new HashMap<>();

        // TAG_WORKTYPE，1 字节表示工作模式类型
        tags.put(27, new Tag(27, 1, new byte[]{(byte) workType}));  // TAG_WORKTYPE

        return new Message((byte) 0x01, (byte) 0x1A, tags);  // SignalType = 0x18 for WORK_MODEL_QUERY_RSP
    }

    /**
     * 开始定位模式响应
     * @param responseCode
     * @return
     */
    public static Message createStartMeasureResponse(int responseCode) {
        Map<Integer, Tag> tags = new HashMap<>();

        // TAG_RESPONSE_VALUE，1 字节表示操作状态码（0 成功，非 0 表示错误）
        tags.put(5, new Tag(5, 1, new byte[]{(byte) responseCode}));  // TAG_RESPONSE_VALUE

        return new Message((byte) 0x01, (byte) 0x1C, tags);  // SignalType = 0x1C for START_MEASURE_RSP
    }

    /**
     * 停止定位模式响应
     * @param responseCode
     * @return
     */
    public static Message createStopMeasureResponse(int responseCode) {
        Map<Integer, Tag> tags = new HashMap<>();

        // TAG_RESPONSE_VALUE，1 字节表示操作状态码（0 成功，非 0 表示错误）
        tags.put(5, new Tag(5, 1, new byte[]{(byte) responseCode}));  // TAG_RESPONSE_VALUE

        return new Message((byte) 0x01, (byte) 0x1E, tags);  // SignalType = 0x1E for STOP_MEASURE_RSP
    }

    public static Message createCellUpdateMessage(int Power) {

        Map<Integer, Tag> tags = new HashMap<>();
        // 添加 小区发射功率 Tag (TAG_TX_PWR)
        tags.put(53, new Tag(53, 1,new byte[]{(byte) Power}));  // TAG_TAC_UPDATE_PERIOD_IN_SEC
        // 返回构建完成的消息
        return new Message((byte) 0x01, (byte) 0x15, tags);  // SignalType = 0x05 for CELL_UPDATE
    }
}



