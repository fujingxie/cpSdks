package gsm;


import tool.CharacterConversionTool;

public class GsmDataHelper {

    /**
     * gsm定位
     *
     * @param imsi
     * @return
     */
    public static byte[] sendGsmLocation(byte[] udpNo, int msgTyp1e, String imsi) {
        byte[] no = udpNo;
        byte[] msgType = msgTyp1e == 0 ? new byte[]{0x00} : new byte[]{0x01};
        byte[] MsgLent = new byte[]{0x1f, 0x00, 0x00, 0x00};
        byte[] MsgNo = no;
        byte[] MsgNum = new byte[]{0x0f};
        byte[] MsgType = msgType;
        byte[] MsgBodyLenth = new byte[]{0x17};
        byte[] MsgFuncation = new byte[]{0x00};
        byte[] MsgBodyNum = new byte[]{0x11, 0x02};
        byte[] MsgBody = new byte[20];
        byte[] Data = new byte[31];
        byte[] test = CharacterConversionTool.toBytes(CharacterConversionTool.asciiToHex(imsi));
        System.arraycopy(test, 0, MsgBody, 0, test.length);
        System.arraycopy(MsgLent, 0, Data, 0, MsgLent.length);
        int pos = MsgLent.length;
        System.arraycopy(MsgNo, 0, Data, pos, MsgNo.length);
        pos = pos + MsgNo.length;
        System.arraycopy(MsgNum, 0, Data, pos, MsgNum.length);
        pos = pos + MsgNum.length;
        System.arraycopy(MsgType, 0, Data, pos, MsgType.length);
        pos = pos + MsgType.length;
        System.arraycopy(MsgFuncation, 0, Data, pos, MsgFuncation.length);
        pos = pos + MsgFuncation.length;
        System.arraycopy(MsgBodyLenth, 0, Data, pos, MsgBodyLenth.length);
        pos = pos + MsgBodyLenth.length;
        System.arraycopy(MsgBodyNum, 0, Data, pos, MsgBodyNum.length);
        pos = pos + MsgBodyNum.length;
        System.arraycopy(MsgBody, 0, Data, pos, MsgBody.length);

        return Data;
    }

    /**
     * gsm屏蔽模式
     *
     * @param msgTyp1e
     */
    public static byte[] GsmShield(byte[] udpNo, int msgTyp1e, boolean isZhenma) {
        byte[] no = udpNo;
        byte[] msgType = msgTyp1e == 0 ? new byte[]{0x00} : new byte[]{0x01};
        byte[] MsgLent = new byte[]{0x0c, 0x00, 0x00, 0x00};
        byte[] MsgNo = no;
        byte[] MsgNum = new byte[]{0x02};
        byte[] MsgType = msgType;
        byte[] MsgFuncation = new byte[]{0x00};
        byte[] MsgBodyLenth = new byte[]{0x04};
        byte[] MsgBodyNum = new byte[]{0x0b, 0x01};
        byte[] MsgBody = isZhenma ? new byte[]{0x01} : new byte[]{0x03};
        byte[] Data = new byte[12];

        System.arraycopy(MsgLent, 0, Data, 0, MsgLent.length);
        int pos = MsgLent.length;
        System.arraycopy(MsgNo, 0, Data, pos, MsgNo.length);
        pos = pos + MsgNo.length;
        System.arraycopy(MsgNum, 0, Data, pos, MsgNum.length);
        pos = pos + MsgNum.length;
        System.arraycopy(MsgType, 0, Data, pos, MsgType.length);
        pos = pos + MsgType.length;
        System.arraycopy(MsgFuncation, 0, Data, pos, MsgFuncation.length);
        pos = pos + MsgFuncation.length;
        System.arraycopy(MsgBodyLenth, 0, Data, pos, MsgBodyLenth.length);
        pos = pos + MsgBodyLenth.length;
        System.arraycopy(MsgBodyNum, 0, Data, pos, MsgBodyNum.length);
        pos = pos + MsgBodyNum.length;
        System.arraycopy(MsgBody, 0, Data, pos, MsgBody.length);


        return Data;
    }


    /**
     * 查询参数（0101,0102,0103,0104,0106,010b,010a,010c,0150,0151,0152,0156,0157,0158,0159,015f）
     *
     * @param messageNo
     * @param zaibo     载波指示
     * @return
     */
    public static byte[] checkInfo(byte messageNo, byte zaibo) {
        byte[] gsmBytes = new byte[]{(byte) 0x88, 00, 00, 00,
                messageNo, 01, zaibo, 0,
                0x0b, 01, 01, 0, 0, 0, 0, 0, 0, 0, 0,
                0x0b, 02, 01, 0, 0, 0, 0, 0, 0, 0, 0,
                0x0b, 03, 01, 0, 0, 0, 0, 0, 0, 0, 0,
                0x0b, 04, 01, 0, 0, 0, 0, 0, 0, 0, 0,
                0x0b, 06, 01, 0, 0, 0, 0, 0, 0, 0, 0,
                04, 0x0b, 01, 00,
                04, 0x0a, 01, 00,
                07, 0x0c, 01, 0, 0, 0, 0,
                0x0b, 0x50, 01, 0, 0, 0, 0, 0, 0, 0, 0,
                0x0b, 0x51, 01, 0, 0, 0, 0, 0, 0, 0, 0,
                0x0b, 0x52, 01, 0, 0, 0, 0, 0, 0, 0, 0,
                05, 0x56, 01, 01, 00,
                05, 0x57, 01, 0x5f, 00,
                05, 0x58, 01, 00, 02,
                05, 0x59, 01, 0x7c, 02,
                05, 0x5f, 01, 00, 00};
        return gsmBytes;
    }

    /**
     * 开启射频
     *
     * @param messageNo
     * @param zaibo     载波指示
     * @return
     */
    public static byte[] Openfrequency(byte messageNo, byte zaibo) {
        return new byte[]{8, 00, 00, 00, messageNo, 5, zaibo, 0};
    }

    /**
     * 关闭射频
     *
     * @param messageNo
     * @param zaibo     载波指示
     * @return
     */
    public static byte[] Closefrequency(byte messageNo, byte zaibo) {
        return new byte[]{8, 00, 00, 00, messageNo, 6, zaibo, 0};
    }

    /**
     * 网络扫描
     *
     * @param messageNo
     * @param zaibo     载波指示
     * @return
     */
    public static byte[] Scan(byte messageNo, byte zaibo) {
        return new byte[]{8, 00, 00, 00, messageNo, 03, zaibo, 0};
    }

    /**
     * 重启系统
     *
     * @param messageNo
     * @param zaibo     载波指示
     * @return
     */
    public static byte[] Reboot(byte messageNo, byte zaibo) {
        return new byte[]{8, 00, 00, 00, messageNo, 9, zaibo, 0};
    }

    /**
     * 载波复位
     *
     * @param messageNo
     * @param zaibo     载波指示
     * @return
     */
    public static byte[] ZaiBoReset(byte messageNo, byte zaibo) {
        return new byte[]{8, 00, 00, 00, messageNo, 7, zaibo, 0};
    }

    /**
     * 设置参数配置
     * @param messageNo
     * @param zaibo 载波指示
     * @param MCC
     * @param MNC
     * @param BCC   载波频点
     * @param LAC   位置区号(LAC
     * @param CRO   小区重选偏置CRO
     * @param CAPTIME   重新捕获间隔时间
     * @param LOWATT    下行衰减
     * @param UPATT     上行衰减
     * @param CONFIGMODE    配置模式
     * @param WORKMODE  工作模式
     * @return
     */
    public static byte[] SetConfig(byte messageNo, byte zaibo,
                                   String MCC, String MNC, String BCC, String LAC, String CRO,
                                   String CAPTIME, String LOWATT, String UPATT, String CONFIGMODE, String WORKMODE) {
        byte[] cmd1;
        byte[] mcc1 = new byte[0];
        if (MCC != null) {
            mcc1 = new byte[0x0b];
            mcc1[0] = 0x0b;
            mcc1[1] = 0x01;
            mcc1[2] = 0x01;
            byte[] bytes = CharacterConversionTool.HexString2Bytes(MCC, 8);
            System.arraycopy(bytes, 0, mcc1, 3, bytes.length);
            ;
        }
        byte[] mnc1 = new byte[0];
        if (MNC != null) {
            mnc1 = new byte[0x0b];
            mnc1[0] = 0x0b;
            mnc1[1] = 0x02;
            mnc1[2] = 0x01;
            byte[] bytes = CharacterConversionTool.HexString2Bytes(MNC, 8);
            System.arraycopy(bytes, 0, mnc1, 3, bytes.length);
            ;
        }
        byte[] bcc1 = new byte[0];
        if (BCC != null) {
            bcc1 = new byte[0x0b];
            bcc1[0] = 0x0b;
            bcc1[1] = 0x50;
            bcc1[2] = 0x01;
            byte[] bytes = CharacterConversionTool.HexString2Bytes(BCC, 8);
            System.arraycopy(bytes, 0, bcc1, 3, bytes.length);
            ;
        }
        byte[] lac1 = new byte[0];
        if (LAC != null) {
            lac1 = new byte[0x0b];
            lac1[0] = 0x0b;
            lac1[1] = 0x03;
            lac1[2] = 0x01;
            byte[] bytes = CharacterConversionTool.HexString2Bytes(LAC, 8);
            System.arraycopy(bytes, 0, lac1, 3, bytes.length);
            ;
        }
        byte[] cro1 = new byte[0];
        if (CRO != null) {
            cro1 = new byte[0x0b];
            cro1[0] = 0x0b;
            cro1[1] = 0x06;
            cro1[2] = 0x01;
            byte[] bytes = CharacterConversionTool.HexString2Bytes(CRO, 8);
            System.arraycopy(bytes, 0, cro1, 3, bytes.length);
            ;
        }
        byte[] cap1 = new byte[0];
        if (CAPTIME != null) {
            cap1 = new byte[7];
            cap1[0] = 0x07;
            cap1[1] = 0x0c;
            cap1[2] = 0x01;
            byte[] bytes = null;
            try {
                bytes = CharacterConversionTool.little_intToByte(Integer.parseInt(CAPTIME), 4);
            } catch (Exception ignored) {

            }
            if (bytes != null) {
                System.arraycopy(bytes, 0, cap1, 3, bytes.length);
                ;
            }
        }
        byte[] low1 = new byte[0];
        if (LOWATT != null) {
            low1 = new byte[0x0b];
            low1[0] = 0x0b;
            low1[1] = 0x51;
            low1[2] = 0x01;
            byte[] bytes = CharacterConversionTool.HexString2Bytes(LOWATT, 8);
            System.arraycopy(bytes, 0, low1, 3, bytes.length);
            ;
        }
        byte[] up1 = new byte[0];
        if (UPATT != null) {
            up1 = new byte[0x0b];
            up1[0] = 0x0b;
            up1[1] = 0x52;
            up1[2] = 0x01;
            byte[] bytes = CharacterConversionTool.HexString2Bytes(UPATT, 8);
            System.arraycopy(bytes, 0, up1, 3, bytes.length);
            ;
        }
        byte[] config1 = new byte[0];
        if (CONFIGMODE != null) {
            config1 = new byte[0x04];
            config1[0] = 0x04;
            config1[1] = 0x0a;
            config1[2] = 0x01;
            try {
                config1[3] = Byte.parseByte(CAPTIME);
            } catch (Exception ignored) {

            }
        }
        byte[] work1 = new byte[0];
        if (WORKMODE != null) {
            work1 = new byte[0x04];
            work1[0] = 0x04;
            work1[1] = 0x0b;
            work1[2] = 0x01;
            try {
                work1[3] = Byte.parseByte(WORKMODE);
            } catch (Exception ignored) {

            }
        }
        int length = 8 + mcc1.length + mnc1.length + bcc1.length + lac1.length + cro1.length
                + cap1.length + low1.length + up1.length + config1.length + work1.length;
        cmd1 = new byte[length];
        byte[] dataHead = new byte[8];
        byte[] dataLength = CharacterConversionTool.little_intToByte(length, 4);
        System.arraycopy(dataLength, 0, dataHead, 0, dataLength.length);
        dataHead[4] = messageNo;
        dataHead[5] = 0x02;
        dataHead[6] = zaibo;
        dataHead[7] = 0;
        System.arraycopy(dataHead, 0, cmd1, 0, dataHead.length);
        System.arraycopy(mcc1, 0, cmd1, 8, mcc1.length);
        System.arraycopy(mnc1, 0, cmd1, 8 + mcc1.length, mnc1.length);
        System.arraycopy(bcc1, 0, cmd1, 8 + mcc1.length + mnc1.length, bcc1.length);
        System.arraycopy(lac1, 0, cmd1, 8 + mcc1.length + mnc1.length + bcc1.length, lac1.length);
        System.arraycopy(cro1, 0, cmd1, 8 + mcc1.length + mnc1.length + bcc1.length + lac1.length, cro1.length);
        System.arraycopy(cap1, 0, cmd1, 8 + mcc1.length + mnc1.length + bcc1.length + lac1.length + cro1.length, cap1.length);
        System.arraycopy(low1, 0, cmd1, 8 + mcc1.length + mnc1.length + bcc1.length + lac1.length + cro1.length + cap1.length, low1.length);
        System.arraycopy(up1, 0, cmd1, 8 + mcc1.length + mnc1.length + bcc1.length + lac1.length + cro1.length + cap1.length + low1.length, up1.length);
        System.arraycopy(config1, 0, cmd1, 8 + mcc1.length + mnc1.length + bcc1.length + lac1.length + cro1.length + cap1.length + low1.length + up1.length, config1.length);
        System.arraycopy(work1, 0, cmd1, 8 + mcc1.length + mnc1.length + bcc1.length + lac1.length + cro1.length + cap1.length + low1.length + up1.length + config1.length, work1.length);
        return cmd1;
    }


}
