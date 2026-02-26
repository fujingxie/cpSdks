package fiveg;

import fourg.data.DevStation;
import tool.CharacterConversionTool;
import tool.Constant;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class GzsjSDK {
    private ServerSocket serverSocket;

    private long lastHeartbeatTime_1 = System.currentTimeMillis();
    private long lastHeartbeatTime_2 = System.currentTimeMillis();
    private Socket clientSocket_1=null;
    private Socket clientSocket_2=null;

    private static final long TIMEOUT_THRESHOLD = 10*1000;  // 60 秒

    // 每隔 10 秒检查一次是否超时
    private ScheduledExecutorService timeoutChecker =null;
    // 开启服务
    public void startServer(ConnectionStateListener listener)  {
        try {
            serverSocket = new ServerSocket(42790);
            System.out.println("TCP Server started on port: " + 42790);
            timeoutChecker=Executors.newScheduledThreadPool(1);
            timeoutChecker.scheduleAtFixedRate(() -> {
                if (System.currentTimeMillis() - lastHeartbeatTime_1 > TIMEOUT_THRESHOLD) {
                    System.out.println("连接超时");
                    // 进行重连或其他处理逻辑
                    if (listener != null) {
                        listener.onClientDisconnected(clientSocket_1);
                    }
                }
                if (System.currentTimeMillis() - lastHeartbeatTime_2 > TIMEOUT_THRESHOLD) {
                    System.out.println("连接超时");
                    // 进行重连或其他处理逻辑
                    if (listener != null) {
                        listener.onClientDisconnected(clientSocket_2);
                    }
                }
            }, 0, 10, TimeUnit.SECONDS);

        } catch (IOException e) {
            e.printStackTrace();
        }
        // 创建一个新线程，用于监听客户端连接
        new Thread(() -> {
            while (true) {
                try {
                    // 接受客户端连接
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Device connected: " + clientSocket.getRemoteSocketAddress());
                    if (clientSocket.getRemoteSocketAddress().toString().equals("231")){
                        clientSocket_1=clientSocket;
                    }else if (clientSocket.getRemoteSocketAddress().toString().equals("232")){
                        clientSocket_2=clientSocket;
                    }

                    // 处理客户端连接
                    handleClient(clientSocket, listener);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // 处理客户端连接
    private void handleClient(Socket clientSocket, ConnectionStateListener listener) {
        new Thread(() -> {
            try {
                if (listener != null) {
                    listener.onClientConnected(clientSocket);
                }

                // 不断读取和处理消息
                while (true) {
                    // 读取和处理消息
                    processMessage(clientSocket);
                }

            } catch (IOException e) {
                System.out.println("Device disconnected: " + clientSocket.getRemoteSocketAddress());
                if (listener != null) {
                    listener.onClientDisconnected(clientSocket);
                }
            }
        }).start();
    }

    // 停止服务
    public void stopServer() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
            System.out.println("TCP Server stopped.");
        }
    }

    private void processMessage(Socket clientSocket) throws IOException {
        // 获取输入流
        InputStream inStream = clientSocket.getInputStream();
        ByteBuffer buffer = ByteBuffer.allocate(2048);

        int bytesRead = inStream.read(buffer.array());
        if (bytesRead != -1) {
            buffer.limit(bytesRead);
            MessageDecoder decoder = new MessageDecoder();
            Message message = decoder.decode(clientSocket, buffer);

            if (message != null) {
                handleMessage(clientSocket, message);
            }
        }
    }

    private void handleMessage(Socket clientSocket, Message message) {
        if (messageReceivedCallback != null && message instanceof Message) {
            Message message1= (Message) message;
            messageReceivedCallback.onMessageReceived(clientSocket, (Message) message);
        }
        if (message instanceof Message) {
            Message msg = (Message) message;
            handleMessages(clientSocket, msg);
        }
    }

    private void handleMessages(Socket session, Message message) {

        BusinessType businessType = message.getBusinessType();

        switch (businessType) {
            case INIT_NOTIFICATION:
                handleInitNotification(session);
                break;
            case HEARTBEAT:
                if (session.getRemoteSocketAddress().toString().equals("231")){
                    lastHeartbeatTime_1 = System.currentTimeMillis();
                }else if (session.getRemoteSocketAddress().toString().equals("232")){
                    lastHeartbeatTime_2 = System.currentTimeMillis();
                }
                processingHeartBeatResponses(session,message);
                break;
            case SYSTEM_TIME_RSP:
                handleSystemTimeResponse(session);
                break;

            case CELL_UPDATE_RSP:
                handleCellUpdateResponse(session);
                break;
            case GET_VERSION_RSP:
                handleGetVersionResponse(session,message);
                break;
            case SYSTEM_STATUS_REPORTING:
                processingSystemStatusReportingResponses(session, message);
                break;
            case CELL_CONFIGURATION_QUERY_RESPONSE:

                processingCellConfigurationQueryResponses(session, message);
                break;
            default:
//                System.out.println("Unhandled Message Type: " + businessType.getDescription());
                break;
        }
    }
    private void processingCellConfigurationQueryResponses(Socket session, Message message) {

        Tag TAG_NO_OF_PLMN=message.getTag(8);
        int TAG_NO_OF_PLMN_VALUE=TAG_NO_OF_PLMN.getContentAsInt();

        Tag TAG_PLMN=message.getTag(9);
        byte[] TAG_PLMN_VALUE=TAG_PLMN.getContent();
        ArrayList<Integer> list= CharacterConversionTool.byteArrayToIntegerList(TAG_PLMN_VALUE,TAG_NO_OF_PLMN_VALUE/2);

        Tag TAG_TAC=message.getTag(10);
        int TAG_TAC_VALUE=TAG_TAC.getContentAsInt();

        Tag TAG_PCI=message.getTag(11);
        int  TAG_PCI_VALUE=TAG_PCI.getContentAsShort();

        Tag TAG_SSB_ARFCN=message.getTag(12);
        int TAG_SSB_ARFCN_VALUE=TAG_SSB_ARFCN.getContentAsInt();

        Tag TAG_BAND=message.getTag(13);
        int TAG_BAND_VALUE=TAG_BAND.getContentAsShort();

        Tag TAG_SYNC_MODE=message.getTag(15);
        int TAG_SYNC_MODE_VALUE=TAG_SYNC_MODE.getContentAsByte();

        Tag TAG_SSB_POWER=message.getTag(17);
        int TAG_SSB_POWER_VALUE=TAG_SSB_POWER.getContentAsByte();

        Tag TAG_RX_GAIN=message.getTag(18);
        int TAG_RX_GAIN_VALUE=TAG_RX_GAIN.getContentAsByte();

        Tag TAG_CMM_REJ_CAUSE=message.getTag(32);
        int TAG_CMM_REJ_CAUSE_VALUE=TAG_CMM_REJ_CAUSE.getContentAsByte();

        Tag TAG_TAC_UPDATE_PERIOD_IN_SEC=message.getTag(30);
        int TAG_TAC_UPDATE_PERIOD_IN_SEC_VALUE=TAG_TAC_UPDATE_PERIOD_IN_SEC.getContentAsInt();

        String ip=String.valueOf(session.getRemoteSocketAddress());
        DevStation devStation=Constant.getDevStation(ip);
        devStation.getCellConfig5g().setTacUpdatePeriod(TAG_TAC_UPDATE_PERIOD_IN_SEC_VALUE);
        devStation.getCellConfig5g().setPower(TAG_SSB_POWER_VALUE);
        devStation.getCellConfig5g().setSyncMode(TAG_SYNC_MODE_VALUE);
        devStation.getCellConfig5g().setPci(TAG_PCI_VALUE);
        devStation.getCellConfig5g().setBand(TAG_BAND_VALUE);
        devStation.getCellConfig5g().setPlmnIds(list);
        devStation.getCellConfig5g().setREJ_CAUSE(TAG_CMM_REJ_CAUSE_VALUE);
        devStation.getCellConfig5g().setSsbArfcn(TAG_SSB_ARFCN_VALUE);
        devStation.getCellConfig5g().setRxGain(TAG_RX_GAIN_VALUE);
        devStation.getCellConfig5g().setTac(TAG_TAC_VALUE);
        Constant.upDateDevStation(ip,devStation);
    }

    private static void processingSystemStatusReportingResponses(Socket session, Message message) {
        Tag tag=message.getTag(15);
        if (tag==null){return;}
        byte[] data=tag.getContent();
        byte[] earfcn= Arrays.copyOfRange(data, 0, 4);
        byte[] tac= Arrays.copyOfRange(data, 4, 8);
        byte SyncState= data[8];
        byte bbuState= data[9];
        byte CupUsage= data[10];
        byte memUsage= data[11];
        byte Temp= data[12];
        byte RfState= data[13];
        byte[] ssbArfcn= Arrays.copyOfRange(data, 13, 17);

        String ip=String.valueOf(session.getRemoteSocketAddress());
        DevStation devStation=Constant.getDevStation(ip);
        devStation.setTem(( Temp& 0xFF));
        devStation.setCpu_tem(( CupUsage& 0xFF));
        devStation.setRom_use(( memUsage& 0xFF));
        devStation.setSyncState(( SyncState& 0xFF));
        devStation.setBbuState(( bbuState& 0xFF));
        Constant.upDateDevStation(ip,devStation);

    }
    private void processingHeartBeatResponses(Socket session, Message message) {
        sendMessage(session,Message.createHeartbeatResponseMessage());
        String ip=String.valueOf(session.getRemoteSocketAddress());
        DevStation devStation= Constant.getDevStation(ip);
        if (devStation.getType()==6){
            devStation.setCurrentTime(System.currentTimeMillis());
            Constant.upDateDevStation(ip,devStation);
        }
    }
    private void handleGetVersionResponse(Socket session, Message message) {
        Tag tag=message.getTag(65);
        if (tag!=null){
            String version=tag.getContentAsString();
            String ip=String.valueOf(session.getRemoteSocketAddress());
            DevStation devStation= Constant.getDevStation(ip);
            devStation.setVersion(version);
            Constant.upDateDevStation(ip,devStation);
            if (!devStation.getSyncState().equalsIgnoreCase("未开始同步")){
                 sendMessage(session,Message.createCellUpdateMessage(127));
            }
        }
    }
    private void handleGetVersionResponse(Socket session) {

//        ArrayList<Integer> plmnIds=new ArrayList<>();
//        plmnIds.add(46000);
//        int tac=3990;
//        int pci=501;
//        int band=41;
//        int ssbArfcn=504990;
//        int syncMode=0;
//        int Power=5;
//        int rxGain=40;
//        int tacUpdatePeriod=30;
//        int REJ_CAUSE=22;
//        Message cellUpdateRequest = Message.
//                createCellUpdateMessage(plmnIds, tac, pci, band,ssbArfcn,syncMode,Power,rxGain,tacUpdatePeriod,REJ_CAUSE);
//        sendMessage(session,cellUpdateRequest);
    }

    private void handleInitNotification(Socket session)  {
        System.out.println("Received INIT_NOTIFICATION, sending INIT_NOTIFICATION_RSP...");
        Message initNotificationRsp = Message.createInitNotificationResponse();
        sendMessage(session,initNotificationRsp);
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Sending System Time Sync Request...");
        long currentTime = System.currentTimeMillis() / 1000L;
        Message systemTimeRequest = Message.createSystemTimeMessage(currentTime);
        sendMessage(session,systemTimeRequest);

//        try {
//            sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        Message queryVersion=Message.queryVersionMessage();
//        sendMessage(session,queryVersion);
    }

    private void handleSystemTimeResponse(Socket session) {

    }

    private void handleCellUpdateResponse(Socket session) {
//        String selectImsi="460000580711781";
//        Message message= Message.createStartMeasureMessage(selectImsi);
//        sendMessage(session,message);
    }
    private MessageReceivedCallback messageReceivedCallback;
    public void setMessageReceivedCallback(MessageReceivedCallback callback) {
        this.messageReceivedCallback = callback;
    }
    // 发送消息的通用方法
    public void sendMessage(Socket clientSocket, Message message)  {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    byte[] messageBytes = message.toByteArray();

                    // 通过输出流发送数据
                    OutputStream outputStream = null;
                    try {
                        outputStream = clientSocket.getOutputStream();
                        outputStream.write(messageBytes);
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    System.out.println("Message sent to " + clientSocket.getRemoteSocketAddress());
                } else {
                    System.err.println("Cannot send message, socket is closed.");
                }
            }
        }).start();
    }
}

