package fiveg;

import fiveg.data.ImsiData;
import fourg.data.CellConfig5g;
import fourg.data.DevStation;
import fourg.data.TargetBean;
import tool.CharacterConversionTool;
import tool.Constant;
import tool.DateUtils;
import tool.SignalDistanceCalculator;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static fiveg.BusinessType.SYSTEM_STATUS_REPORTING;
import static fiveg.BusinessType.HEARTBEAT;

public enum GzsjSdkManager {
    INSTANCE;

    public GzsjSDK sdk;

    public static void main(String[] args) {
        System.err.println("hello world");
    }

    public void init() throws IOException {

        sdk = new GzsjSDK();
        sdk.startServer(new ConnectionStateListener() {
            @Override
            public void onClientConnected(Socket session) {
                addIoSession(session);
            }

            @Override
            public void onClientDisconnected(Socket session) {
                removeIoSession(session);
            }
        });

        // 模拟接收心跳消息
        sdk.setMessageReceivedCallback((session, message) -> {
            exitSession(session);
            // 获取业务类型
            BusinessType businessType = message.getBusinessType();
            // 根据业务类型执行不同的逻辑
            switch (businessType) {
                case GET_VERSION_RSP:
                    handleGetVersionResponse(session, message);
                    break;
                case HEARTBEAT:
                    processingHeartBeatResponses(session, message);
                    break;
                case SYSTEM_STATUS_REPORTING:
                    processingSystemStatusReportingResponses(session, message);
                    break;
                case INIT_NOTIFICATION:
                    processingInitNotificationResponses(session, message);
                    break;
                case START_MEASURE:
                    break;
                case CELL_CONFIGURATION_QUERY_RESPONSE:

                    processingCellConfigurationQueryResponses(session, message);
                    break;
                case REPORT_UE_WITH_RSSI:
                    processingReportUeWithResponse(session, message);
                    break;
                case REPORT_MEASURE:
                    processingReportMeasureResponse(session, message);
                    break;
                case POWER_SETTING_RSP:
                    processingPowerSetResponse(session, message);
                    break;
                case CELL_UPDATE_RSP:
                    handleCellUpdateResponse(session, message);
                    break;
                default:
                    break;
            }
        });
    }

    private void handleGetVersionResponse(Socket session, Message message) {
        Tag tag = message.getTag(65);
        if (tag != null) {
            String version = tag.getContentAsString();
            String ip = String.valueOf(session.getRemoteSocketAddress());

        }
    }

    private void handleCellUpdateResponse(Socket session, Message message) {
        Tag tag = message.getTag(5);
        if (tag != null) {
            int result = tag.getContentAsByte();
            if (result == 0) {

            }
        }
    }

    private void processingPowerSetResponse(Socket session, Message message) {
        Tag tag = message.getTag(5);
        if (tag != null) {
            int result = tag.getContentAsByte();
            if (result == 0) {

            }
        }
    }

    private void exitSession(Socket session) {
        int lenth = 0;

        String ip = String.valueOf(session.getRemoteSocketAddress());
        DevStation devStation = Constant.getDevStation(ip);
        devStation.setConnected(true);
        devStation.setSocket(session);
        Constant.upDateDevStation(ip, devStation);
    }

    private void processingReportMeasureResponse(Socket session, Message message) {
        String ip = String.valueOf(session.getRemoteSocketAddress());
        DevStation devStation = Constant.getDevStation(ip);
        TargetBean targetBean = new TargetBean();
        Tag TAG_UEID = message.getTag(56);
        if (TAG_UEID != null) {
            byte[] TAG_UEID_VALUE = TAG_UEID.getContent();
            String imsi = CharacterConversionTool.bytesToAsciiString(TAG_UEID_VALUE);
            targetBean.setImsi(imsi);
        }
        Tag TAG_RSSI = message.getTag(57);
        int rssi = 0;
        if (TAG_RSSI != null) {
            rssi = TAG_RSSI.getContentAsByte();
            int TAG_RSSI_VALUE = (int) (TAG_RSSI.getContentAsByte() * 1.9);
            targetBean.setSinr(TAG_RSSI_VALUE);
        }
        int distance = (int) Math.round(SignalDistanceCalculator
                .calculate5G(rssi,
                        SignalDistanceCalculator.ssbConvertPoint(devStation.getCellConfig5g().getSsbArfcn())));
        targetBean.setDistance(distance < 1 ? 1 : distance);

        if (devStation.getIp().contains("231")) {
            targetBean.setBbu("12-5G");
        } else {
            targetBean.setBbu("13-5G");
        }
        targetBean.setFreq("" + devStation.getCellConfig5g().getSsbArfcn());
        targetBean.setPci("" + devStation.getCellConfig5g().getPci());
        targetBean.setTime(System.currentTimeMillis());

    }

    private void processingReportUeWithResponse(Socket session, Message message) {
        String ip = String.valueOf(session.getRemoteSocketAddress());
        DevStation devStation = Constant.getDevStation(ip);
        if (devStation == null) {
            return;
        }
        ImsiData imsiData = new ImsiData();
        Tag TAG_UEID = message.getTag(10);
        if (TAG_UEID != null) {
            byte[] TAG_UEID_VALUE = TAG_UEID.getContent();
            String imsi = CharacterConversionTool.bytesToAsciiString(TAG_UEID_VALUE);
            if (imsi.length() > 15) {
                imsi = imsi.substring(0, 5) + imsi.substring(14, imsi.length());

            }
            imsiData.setImsi(imsi);
        }

        Tag TAG_SINR = message.getTag(11);
        if (TAG_SINR != null) {
            int TAG_SINR_VALUE = TAG_SINR.getContentAsInt();

        }
        Tag TAG_RSSI = message.getTag(12);
        int rssi = 0;
        if (TAG_RSSI != null) {
            rssi = TAG_RSSI.getContentAsShort();
            int TAG_RSSI_VALUE = (int) (TAG_RSSI.getContentAsShort() * 1.9);
            imsiData.setFieldstrength(TAG_RSSI_VALUE);
        }
        imsiData.setStationName(devStation.getIp());
        imsiData.setTime(System.currentTimeMillis());
        if (devStation.getIp().contains("231")) {
            imsiData.setBbu("12-5G");
        } else {
            imsiData.setBbu("13-5G");
        }
        int distance = (int) Math.round(SignalDistanceCalculator
                .calculate5G(rssi,
                        SignalDistanceCalculator.ssbConvertPoint(devStation.getCellConfig5g().getSsbArfcn())));
        imsiData.setDistance(distance < 1 ? 1 : distance);

    }

    private void processingInitNotificationResponses(Socket session, Message message) {
        Tag TAG_TDD_FDD_MODE = message.getTag(2);
        if (TAG_TDD_FDD_MODE != null) {
            int TAG_TDD_FDD_MODE_VALUE = TAG_TDD_FDD_MODE.getContentAsInt();
        }
        Tag TAG_IP_ADDR = message.getTag(3);
        if (TAG_IP_ADDR != null) {
            String ip = TAG_IP_ADDR.getContentAsString();
        }

    }

    private void processingHeartBeatResponses(Socket session, Message message) {
        String ip = String.valueOf(session.getRemoteSocketAddress());

    }

    private void processingCellConfigurationQueryResponses(Socket session, Message message) {

        Tag TAG_NO_OF_PLMN = message.getTag(8);
        int TAG_NO_OF_PLMN_VALUE = TAG_NO_OF_PLMN.getContentAsInt();

        Tag TAG_PLMN = message.getTag(9);
        byte[] TAG_PLMN_VALUE = TAG_PLMN.getContent();
        ArrayList<Integer> list = CharacterConversionTool.byteArrayToIntegerList(TAG_PLMN_VALUE,
                TAG_NO_OF_PLMN_VALUE / 2);

        Tag TAG_TAC = message.getTag(10);
        int TAG_TAC_VALUE = TAG_TAC.getContentAsInt();

        Tag TAG_PCI = message.getTag(11);
        int TAG_PCI_VALUE = TAG_PCI.getContentAsShort();

        Tag TAG_SSB_ARFCN = message.getTag(12);
        int TAG_SSB_ARFCN_VALUE = TAG_SSB_ARFCN.getContentAsInt();

        Tag TAG_BAND = message.getTag(13);
        int TAG_BAND_VALUE = TAG_BAND.getContentAsShort();

        Tag TAG_SYNC_MODE = message.getTag(15);
        int TAG_SYNC_MODE_VALUE = TAG_SYNC_MODE.getContentAsByte();

        Tag TAG_SSB_POWER = message.getTag(17);
        int TAG_SSB_POWER_VALUE = TAG_SSB_POWER.getContentAsByte();

        Tag TAG_RX_GAIN = message.getTag(18);
        int TAG_RX_GAIN_VALUE = TAG_RX_GAIN.getContentAsByte();

        Tag TAG_CMM_REJ_CAUSE = message.getTag(32);
        int TAG_CMM_REJ_CAUSE_VALUE = TAG_CMM_REJ_CAUSE.getContentAsByte();

        Tag TAG_TAC_UPDATE_PERIOD_IN_SEC = message.getTag(30);
        int TAG_TAC_UPDATE_PERIOD_IN_SEC_VALUE = TAG_TAC_UPDATE_PERIOD_IN_SEC.getContentAsInt();

    }

    private static void processingSystemStatusReportingResponses(Socket session, Message message) {
        Tag tag = message.getTag(15);
        if (tag == null) {
            return;
        }
        byte[] data = tag.getContent();
        byte[] earfcn = Arrays.copyOfRange(data, 0, 4);
        byte[] tac = Arrays.copyOfRange(data, 4, 8);
        byte SyncState = data[8];
        byte bbuState = data[9];
        byte CupUsage = data[10];
        byte memUsage = data[11];
        byte Temp = data[12];
        byte RfState = data[13];
        byte[] ssbArfcn = Arrays.copyOfRange(data, 13, 17);

    }

    private void removeIoSession(Socket session) {
        if (session == null) {
            return;
        }
        String ip = String.valueOf(session.getRemoteSocketAddress());
        DevStation devStation = Constant.getDevStation(ip);
        devStation.setConnected(false);
        Constant.upDateDevStation(ip, devStation);

    }

    private void addIoSession(Socket session) {
        String ip = String.valueOf(session.getRemoteSocketAddress());
        DevStation devStation = Constant.getDevStation(ip);
        devStation.setConnected(false);
        devStation.setSocket(session);
        Constant.upDateDevStation(ip, devStation);
    }

    public void openDbm() {
        for (DevStation stationInfo : Constant.getDevStationList()) {
            if (stationInfo.getType() == 6) {
                CellConfig5g cellConfig5g = stationInfo.getCellConfig5g();
                Message message = Message.createCellUpdateMessage(cellConfig5g.getPlmnIds(), cellConfig5g.getTac(),
                        cellConfig5g.getPci(), cellConfig5g.getBand(), cellConfig5g.getSsbArfcn(),
                        cellConfig5g.getSyncMode(), cellConfig5g.getPower(),
                        stationInfo.getCellConfig5g().getRxGain(), cellConfig5g.getTacUpdatePeriod(),
                        cellConfig5g.getREJ_CAUSE());
                sdk.sendMessage(stationInfo.getSocket(), message);
            }
        }
    }

    public void closeDbm() {
        for (DevStation stationInfo : Constant.getDevStationList()) {
            if (stationInfo.getType() == 6) {
                CellConfig5g cellConfig5g = stationInfo.getCellConfig5g();
                Message message = Message.createCellUpdateMessage(cellConfig5g.getPlmnIds(), cellConfig5g.getTac(),
                        cellConfig5g.getPci(), cellConfig5g.getBand(), cellConfig5g.getSsbArfcn(),
                        cellConfig5g.getSyncMode(), 127,
                        stationInfo.getCellConfig5g().getRxGain(), cellConfig5g.getTacUpdatePeriod(),
                        cellConfig5g.getREJ_CAUSE());
                sdk.sendMessage(stationInfo.getSocket(), message);
            }
        }
    }

    public void closeDbm(Socket socket) {
        sdk.sendMessage(socket, Message.createCellUpdateMessage(127));
    }

    public void setPositionOn(List<String> imsiList) {
        if (imsiList.size() == 0) {
            return;
        }
        for (String imsi : imsiList) {
            String selectImsi = imsi;
            boolean isYd = DateUtils.getOpera1(selectImsi) == 1;
            Message message = Message.createStartMeasureMessage(selectImsi);
            for (DevStation stationInfo : Constant.getDevStationList()) {
                if (stationInfo.getType() == 6) {
                    if (isYd == true && stationInfo.getIp().contains("231")) {
                        sdk.sendMessage(stationInfo.getSocket(), message);
                    } else if (isYd == false && stationInfo.getIp().contains("232")) {
                        sdk.sendMessage(stationInfo.getSocket(), message);
                    }
                }
            }
        }

    }

    public void setPositionOn(String imsi) {
        String selectImsi = imsi;
        boolean isYd = DateUtils.getOpera1(selectImsi) == 1;
        Message message = Message.createStartMeasureMessage(selectImsi);
        for (DevStation stationInfo : Constant.getDevStationList()) {
            if (stationInfo.getType() == 6) {
                if (isYd == true && stationInfo.getIp().contains("231")) {
                    sdk.sendMessage(stationInfo.getSocket(), message);
                } else if (isYd == false && stationInfo.getIp().contains("232")) {
                    sdk.sendMessage(stationInfo.getSocket(), message);
                }
            }
        }
    }

    public void setPositionOFF(String imsi) {

        String selectImsi = imsi;
        boolean isYd = DateUtils.getOpera1(selectImsi) == 1;
        Message message = Message.createStopMeasureMessage();
        for (DevStation stationInfo : Constant.getDevStationList()) {
            if (stationInfo.getType() == 6) {
                if (isYd == true && stationInfo.getIp().contains("231")) {
                    sdk.sendMessage(stationInfo.getSocket(), message);
                } else if (isYd == false && stationInfo.getIp().contains("232")) {
                    sdk.sendMessage(stationInfo.getSocket(), message);
                }
            }
        }
    }

    public void setPositionOFF() {
        Message message = Message.createStopMeasureMessage();
        for (DevStation stationInfo : Constant.getDevStationList()) {
            if (stationInfo.getType() == 6) {
                sdk.sendMessage(stationInfo.getSocket(), message);
            }
        }
    }

    public void setOpenDbm(int progress) {
        Message message = Message.createPowerSettingMessage(progress);
        for (DevStation stationInfo : Constant.getDevStationList()) {
            if (stationInfo.getType() == 6) {
                sdk.sendMessage(stationInfo.getSocket(), message);
            }
        }
    }
}
