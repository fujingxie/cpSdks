package gsm;

public class GsmProtocolConst {

    public static final byte Query_Response = 0x11;//查询应答
    public static final byte Setting_Response = 0x12;//设置应答
    public static final byte Scan_Response = 0x13;//网络扫描应答
    public static final byte Openfrequency_Response = 0x15;//开启射频应答
    public static final byte Closefrequency_Response = 0x16;//关闭射频应答
    public static final byte CarrierWave_Reset_Response = 0x17;//载波复位应答
    public static final byte Report_User_Information = 0x18;//用户信息上报
    public static final byte Reboot_Information = 0x19;//重启系统应答
    public static final byte Reboot1_Information = 0x1E;//
    public static final byte Location_Response = 0x1F;//定位应答

    interface funcationNo {
        static final String KEY_0C01 = "0C01";//重新捕获间隔时间
        static final String KEY_0A01 = "0A01";//配置模式
        static final String KEY_5001 = "5001";//载波频点
        static final String KEY_0B01 = "0B01";//工作模式
        static final String KEY_5101 = "5101";//下行衰减
        static final String KEY_5201 = "5201";//上行衰减
        static final String KEY_5F01 = "5F01";//频率偏移
        static final String KEY_0601 = "0601";//小区重选偏置CRO
        static final String KEY_0301 = "0301";//位置区号(LAC)
        static final String KEY_0401 = "0401";//小区号(CI)
        static final String KEY_0101 = "0101";//国家码(MCC)
        static final String KEY_0201 = "0201";//网络码(MNC)
        static final String KEY_5601 = "5601";//起始频点1
        static final String KEY_5701 = "5701";//结束频点1
        static final String KEY_5801 = "5801";//起始频点2
        static final String KEY_5901 = "5901";//结束频点2

        static final String KEY_1102 = "1102";//IMSI
        static final String KEY_1202 = "1202";//IMEI
        static final String KEY_1302 = "1302";//Tmsi
        static final String KEY_1402 = "1402";//ESN

    }
}
