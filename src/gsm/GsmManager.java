package gsm;

import gsm.data.QueryResponseData;
import gsm.data.ReportInfo;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import tool.CharacterConversionTool;
import tool.Constant;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.Set;


public class GsmManager extends IoHandlerAdapter {

    private DataReciveCallBackListerner daCallBackListerner = null;//消息回调

    private static GsmManager instance = null;

    private static IoSession session = null;

    private GsmManager() throws IOException {
        NioDatagramAcceptor acceptor = new NioDatagramAcceptor();
        acceptor.setHandler(this);

//		acceptor.getFilterChain().addLast("logging", new LoggingFilter());
        acceptor.getFilterChain().addLast("coderc", new ProtocolCodecFilter(new GsmFactory()));

        DatagramSessionConfig dcfg = acceptor.getSessionConfig();
        dcfg.setReuseAddress(true);
        acceptor.getSessionConfig().setReceiveBufferSize(2048 * 5000);// 接收缓冲区1M
        acceptor.getSessionConfig().setBothIdleTime(30);
        // 设置session配置，30秒内无操作进入空闲状态
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, Constant.GSM_IDELTIMEOUT);

        //心跳配置
//		KeepAliveMessageFactory heartBeatFactory = new KeepAliveMessageFactoryImpl();
//		KeepAliveRequestTimeoutHandler heartBeatHandler = new KeepAliveRequestTimeoutHandlerImpl();
//		KeepAliveFilter heartBeat = new KeepAliveFilter(heartBeatFactory, IdleStatus.BOTH_IDLE, heartBeatHandler);
//		// 是否回发
//		heartBeat.setForwardEvent(false);
//		// 发送频率
//		heartBeat.setRequestInterval(Constant.GSM_HEARTBEATRATE);
//		
//		acceptor.getFilterChain().addLast("heartbeat", heartBeat);
        acceptor.bind(new InetSocketAddress(Constant.GSM_RECEIVE_PORT));

        System.out.println("Server started...");
    }

    public static GsmManager getInstance() throws IOException {
        // 如果instance未被初始化，则初始化该类实例
        if (instance == null) {
            instance = new GsmManager();
        }
        return instance;
    }

    public static void main(String[] args) throws IOException {
        GsmManager.getInstance().setOnReciveCallBackListerner(new DataReciveCallBackListerner() {
            @Override
            public void sessionCreated(IoSession session) {
                System.out.println("sessionCreated "+session.getRemoteAddress().toString());
            }

            @Override
            public void sessionClosed() {
                System.out.println("sessionClosed ");
            }

            @Override
            public void messageSent(Object message) {
                System.out.println("messageSent "+message);
            }

            @Override
            public void messageReceived(byte protocolName, String remoteIp, Object data, boolean isReturnData) {
                System.out.println("messageReceived "+protocolName);
            }
        });
        new MemMonClient();
        //Gsm messageReceived...GsmReciveBean{MessageNumber=58, zaibo=0, FuncationNo=0,
        // BodyMap={0002=[B@4844e769, 015D=[B@3cfce44c, 00BC=[B@9a90312, 015E=[B@18c50e41, 015C=[B@77d5cd9f}}
    }

    public static void send(String cmd, String ip) {
        if (session != null) {
            SocketAddress address = new InetSocketAddress(ip, Constant.GSM_SEDN_PORT);
            session.write(cmd, address);
        }
    }

    public void setOnReciveCallBackListerner(DataReciveCallBackListerner daCallBackListerner) {
        this.daCallBackListerner = daCallBackListerner;
    }

    public interface DataReciveCallBackListerner {
        void sessionCreated(IoSession session);

        void sessionClosed();

        void messageSent(Object message);

        /**
         * @param protocolName 协议名称
         * @param remoteIp     远程设备ip
         * @param data         数据
         * @param isReturnData 是否返回数据
         */
        void messageReceived(byte protocolName, String remoteIp, Object data, boolean isReturnData);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        // TODO Auto-generated method stub
        String ip = session.getRemoteAddress().toString();
        if (message == null && message.getClass() != GsmReciveBean.class) {
            return;
        }
        GsmReciveBean bean = (GsmReciveBean) message;
        Set<Map.Entry<String, byte[]>> entrySet = bean.getBodyMap().entrySet();
        System.out.println("Gsm messageReceived..." + bean.toString());
        switch (bean.getMessageNumber()) {
            case GsmProtocolConst.Query_Response:
                //查询应答
                QueryResponseData data=new QueryResponseData();
                for (Map.Entry<String, byte[]> entry : entrySet) {
                    switch (entry.getKey()){
                        case GsmProtocolConst.funcationNo.KEY_0A01:
                            data.setConfigModel(entry.getValue()[0] );
                            break;
                        case GsmProtocolConst.funcationNo.KEY_0C01:
                            data.setRecaptureTime(CharacterConversionTool.little_bytesToInt(entry.getValue()) );
                            break;
                        case GsmProtocolConst.funcationNo.KEY_5001:
                            data.setCarrierFrequencyPoint(CharacterConversionTool.hexStr2Str(CharacterConversionTool.bytesToHexString1(entry.getValue()))+"");
                            break;
                        case GsmProtocolConst.funcationNo.KEY_0B01:

                            data.setWorkModel(entry.getValue()[0] );
                            break;
                        case GsmProtocolConst.funcationNo.KEY_5101:

                            data.setDownlinkAttenuation(CharacterConversionTool.
                                    hexStr2Str(CharacterConversionTool.bytesToHexString1(entry.getValue())));
                            break;
                        case GsmProtocolConst.funcationNo.KEY_5201:

                            data.setUplinkAttenuation(CharacterConversionTool.
                                    hexStr2Str(CharacterConversionTool.bytesToHexString1(entry.getValue())));
                            break;
                        case GsmProtocolConst.funcationNo.KEY_5F01:

                            data.setFrequencyOffset( CharacterConversionTool.
                                    little_bytesToInt(entry.getValue()));
                            break;
                        case GsmProtocolConst.funcationNo.KEY_0601:

                            data.setCRO(CharacterConversionTool.
                                    hexStr2Str(CharacterConversionTool.bytesToHexString1(entry.getValue())));
                            break;
                        case GsmProtocolConst.funcationNo.KEY_0301:

                            data.setLAC(CharacterConversionTool.
                                    hexStr2Str(CharacterConversionTool.bytesToHexString1(entry.getValue())));
                            break;
                        case GsmProtocolConst.funcationNo.KEY_0401:

                            data.setCI(CharacterConversionTool.
                                    hexStr2Str(CharacterConversionTool.bytesToHexString1(entry.getValue())));
                            break;
                        case GsmProtocolConst.funcationNo.KEY_0101:

                            data.setMCC(CharacterConversionTool.
                                    hexStr2Str(CharacterConversionTool.bytesToHexString1(entry.getValue())));
                            break;
                        case GsmProtocolConst.funcationNo.KEY_0201:

                            data.setMNC(CharacterConversionTool.
                                    hexStr2Str(CharacterConversionTool.bytesToHexString1(entry.getValue())));
                            break;
                        case GsmProtocolConst.funcationNo.KEY_5601:

                            data.setStartingFrequencyPoint1(CharacterConversionTool.
                                    little_bytesToInt(entry.getValue()));
                            break;
                        case GsmProtocolConst.funcationNo.KEY_5701:

                            data.setEndFrequencyPoint1(CharacterConversionTool.
                                    little_bytesToInt(entry.getValue()));
                            break;
                        case GsmProtocolConst.funcationNo.KEY_5801:

                            data.setStartingFrequencyPoint2(CharacterConversionTool.
                                    little_bytesToInt(entry.getValue()));
                            break;
                        case GsmProtocolConst.funcationNo.KEY_5901:

                            data.setEndFrequencyPoint2(CharacterConversionTool.
                                    little_bytesToInt(entry.getValue()));
                            break;
                    }

                }
                daCallBackListerner.messageReceived(bean.getMessageNumber(),ip,data,true);
                break;
            case GsmProtocolConst.Report_User_Information:

                ReportInfo imsiData = new ReportInfo();
                for (Map.Entry<String, byte[]> entry : entrySet) {
                    System.out.println("1212212  "+entry.getKey());
                    switch (entry.getKey()){
                        case GsmProtocolConst.funcationNo.KEY_1102:
                            imsiData.setImsi(CharacterConversionTool.
                                    hexStr2Str(CharacterConversionTool.bytesToHexString1(entry.getValue())).trim());
                            break;
                        case GsmProtocolConst.funcationNo.KEY_1202:
                            imsiData.setImei(CharacterConversionTool.hexStr2Str(CharacterConversionTool.
                                    bytesToHexString1(entry.getValue())).trim());
                            break;
                        case GsmProtocolConst.funcationNo.KEY_1302:
                            imsiData.setTmsi(CharacterConversionTool.hexStr2Str(CharacterConversionTool.
                                    bytesToHexString1(entry.getValue())).trim());
                            break;
                        case GsmProtocolConst.funcationNo.KEY_1402:
                            imsiData.setEsn(CharacterConversionTool.hexStr2Str(CharacterConversionTool.
                                    bytesToHexString1(entry.getValue())).trim());
                            break;
                    }
                }
                daCallBackListerner.messageReceived(bean.getMessageNumber(),ip,imsiData,true);
                break;
            case GsmProtocolConst.Setting_Response:
                daCallBackListerner.messageReceived(bean.getMessageNumber(),ip,null,false);
                break;
            case GsmProtocolConst.Scan_Response:
                daCallBackListerner.messageReceived(bean.getMessageNumber(),ip,null,false);
                break;
            case GsmProtocolConst.Openfrequency_Response:
                daCallBackListerner.messageReceived(bean.getMessageNumber(),ip,null,false);
                break;
            case GsmProtocolConst.Closefrequency_Response:
                daCallBackListerner.messageReceived(bean.getMessageNumber(),ip,null,false);
                break;
            case GsmProtocolConst.CarrierWave_Reset_Response:
                daCallBackListerner.messageReceived(bean.getMessageNumber(),ip,null,false);
                break;
            case GsmProtocolConst.Reboot_Information:
                daCallBackListerner.messageReceived(bean.getMessageNumber(),ip,null,false);
                break;
            case GsmProtocolConst.Reboot1_Information:
                daCallBackListerner.messageReceived(bean.getMessageNumber(),ip,null,false);
                break;
            case GsmProtocolConst.Location_Response:
                daCallBackListerner.messageReceived(bean.getMessageNumber(),ip,null,false);
                break;
        }
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        daCallBackListerner.sessionCreated(session);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        System.out.println("Gsm closed...");
        daCallBackListerner.sessionClosed();
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
        daCallBackListerner.messageSent(message);
    }
}
