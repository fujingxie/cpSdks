package fourg;

import fourg.data.*;
import fourg.interfaces.DataReciveCallBackListerner;
import fourg.interfaces.ServerConnectListerner;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import tool.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static java.lang.Thread.sleep;

public class FourGManager extends IoHandlerAdapter {

    private DataReciveCallBackListerner daCallBackListerner = null;//消息回调

    private static FourGManager instance = null;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private NioSocketAcceptor acceptor;

    private  DefaultIoFilterChainBuilder filterChain;

    private int seq=0;

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    private AsyncLocationLoader loader = null;

    private FourGManager() {
        if (loader==null){
            loader = new AsyncLocationLoader();
            loader.loadLocationsAsync("./resources/telnumber.txt");
            Constant.devStationHashMap.clear();
        }
    }

    public static FourGManager getInstance()   {
        // 如果instance未被初始化，则初始化该类实例
        if (instance == null) {
            instance = new FourGManager();
        }
        return instance;
    }

    public  void send(IoSession session,byte[] cmd) {
        if (session==null){return;}
        session.write(cmd);
    }

    public boolean closeSession(IoSession session){
        if (session==null||session.isClosing()){
            return false;
        }
        session.close(true);
        return true;
    }
    public void startServer(ServerConnectListerner connectListerner) {
        acceptor = new NioSocketAcceptor();

        filterChain = acceptor.getFilterChain();
        // 添加编码过滤器 处理乱码、编码问题
        filterChain.addLast("codec", new ProtocolCodecFilter(new CharsetCodeFactory()));

        acceptor.setReuseAddress(true);

        //为接收器设置管理服务（核心处理）
        acceptor.setHandler(this);

        acceptor.setReuseAddress(true);//加上这句话，避免重启时提示地址被占用
        try {
            acceptor.bind(new InetSocketAddress(Constant.TCP_PORT));
            connectListerner.onSuccess();
        } catch (IOException e) {
            e.printStackTrace();
            connectListerner.onFail(e.getMessage());

        }
    }


    public void destroyServer(){
        if (acceptor==null){return;}
        acceptor.unbind();
        acceptor.dispose(true);
        acceptor=null;
    }
    public static void main(String[] args) {
        FourGManager.getInstance().startServer(new ServerConnectListerner() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFail(String errorMsg) {

            }
        });
        FourGManager.getInstance().setOnReciveCallBackListerner(new DataReciveCallBackListerner() {
            @Override
            public void sessionCreated(IoSession session) {
                System.out.println( "sessionCreated:"+session.getRemoteAddress().toString());

            }

            @Override
            public void sessionClosed(IoSession session) {
                System.out.println( "sessionClosed:"+session.getRemoteAddress().toString());
            }

            @Override
            public void messageSent(IoSession ioSession, String message) {
                System.out.println( "messageSent:"+message);
            }

            @Override
            public void messageReceived(String protocolName, IoSession session, Object data, boolean isReturnData) {
                System.out.println( "protocolName:"+protocolName);
                if (protocolName.equalsIgnoreCase(FourGProtocolConst.KEY_RUN_TIME_PARAMETERS_CONFIGURE_RESPONSE)){
                    FourGManager.getInstance().scheduleNextCommand(() -> {
                        session.write(FourGData.OPEN_DBM(getInstance().seq++,(byte)30 ));
                    }, 10);//460000580711781
                }else if (protocolName.equalsIgnoreCase(FourGProtocolConst.KEY_SYSTEM_STATUS_REPORT)){
                    SystemStatus systemStatus= (SystemStatus) data;
                    System.out.println(""+systemStatus.toString());
                }else if (protocolName.equalsIgnoreCase(FourGProtocolConst.KEY_UEID_REPORT)){
                    UeidReportData ueidReportData= (UeidReportData) data;
                    System.out.println(""+ueidReportData);
                }else if (protocolName.equalsIgnoreCase(FourGProtocolConst.KEY_SINR_RPT_FOR_MC_MODE)){
                    FieldStrengthData fieldStrengthData= (FieldStrengthData) data;
                    System.out.println(fieldStrengthData.toString());
                }
            }
        });
    }

    /**
     * 切换单个板卡侦码开关
     * @param session
     * @param status
     */
    private void CodeDetectionSwitch(IoSession session,boolean status){
        if (session==null){return;}
        if (status==true){
            session.write(FourGData.OPEN_DBM(getInstance().seq++,(byte)30 ));
        }else {
            session.write(FourGData.CLOSE_DBM(getInstance().seq++));
        }
    }

    /**
     * 切换全部板卡侦码开关
     * @param status
     */
    private void OpenCodeDetectionSwitch(boolean status){
        List<DevStation> list=Constant.getDevStationList();
        for (DevStation devStation:list){
            IoSession session=devStation.getSession();
            CodeDetectionSwitch(session,status);
        }
    }

    /**
     * 定位黑名单
     * @param imsiList
     */
    private void OpenLocatingImsi(ArrayList<String > imsiList) {
        List<DevStation> list=Constant.getDevStationList();
        for (DevStation devStation:list){
            IoSession session=devStation.getSession();
            if (session!=null){
                session.write(FourGData.LOCATION_MODE_SETTING(seq++,imsiList));
            }
        }
    }

    /**
     * 关闭定位黑名单
     */
    private void CloseLocatingImsi() {
        List<DevStation> list=Constant.getDevStationList();
        for (DevStation devStation:list){
            IoSession session=devStation.getSession();
            if (session!=null){
                session.write(FourGData.CLOSE_LOCATION(seq++));
            }
        }
    }
    public void restartServer(){

        if (acceptor!=null){
            acceptor.unbind();
            acceptor.dispose(true);
            acceptor = new NioSocketAcceptor();

            filterChain = acceptor.getFilterChain();
            // 添加编码过滤器 处理乱码、编码问题
            filterChain.addLast("codec", new ProtocolCodecFilter(new CharsetCodeFactory()));

            //为接收器设置管理服务（核心处理）
            acceptor.setHandler(this);
            acceptor.setReuseAddress(true);//加上这句话，避免重启时提示地址被占用
            try {
                acceptor.bind(new InetSocketAddress(Constant.TCP_PORT));
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
         //当接收到新的消息的时候，此方法被调用。
        ArrayList<Integer> msg = (ArrayList<Integer>) message;
         String ip=session.getRemoteAddress().toString();
//        daCallBackListerner.onReceived( msg.toString());
        if (msg.size()>0&&msg.size()<10){
            if (msg.get(0) == 2 && msg.get(1) == 52) {
                if (msg.get(4) == 2 ) {
                    //状态码：
                    //0：成功；非0表示错误，错误码待定
                    int code=msg.get(7);
                    daCallBackListerner.messageReceived(FourGProtocolConst.KEY_SCAN_MODE_RSP,session,
                            new ResultCode(code),true);
                }else if (msg.get(4) == 1&& msg.get(11) == 2) {
                    int code=msg.get(14);
                    daCallBackListerner.messageReceived(FourGProtocolConst.KEY_SCAN_MODE_RSP,session,
                            new ResultCode(code),true);
                }
            }else if (msg.get(0)==2&&msg.get(1)==64) {
                //0x02 0x40 0x00 0x0x 0x58 0x00 0x02 0x06 0x72 0x01 0x0x 0x04 0x00 0x00 0x00 0x01
                if (msg.get(4) == 88 ) {
                    //状态码：
                    //0：成功；非0表示错误，错误码待定
                    try {
                        byte a=msg.get(7).byteValue();
                        byte b=msg.get(8).byteValue();
                        byte[] code=new byte[]{a,b};

                        int value=CharacterConversionTool.bytes2ShortBIG_ENDIAN(code)&0xffff;
                        daCallBackListerner.messageReceived(FourGProtocolConst.KEY_REPORT_USING_DL_EARFCN,session,
                                new ReportPoint(value,code),true);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }

            return;
        }
        if (msg.size() > 10) {
            //0x01表示业务相关的，0x02表示操作维护类
			if (msg.get(0) == 0x01 && msg.get(1) == 0x01) {
				// TODO: 2020-07-10 设备启动通知
				int SupportBand = 0,TDDtype= 0;
				Map<Integer, ArrayList<Integer>> map = new HashMap<>();
				while (msg.size() > 4) {
					int tag = msg.remove(4);
					int tagLength = 0;
					if (msg.size() > 6) {
						tagLength = (msg.remove(4) & 0xFF) * 256 + msg.remove(4) & 0xFF;
					}
					ArrayList<Integer> list = new ArrayList<>();
					while (tagLength > 0) {
						tagLength--;
						if (msg.size() > 4) {
							list.add(msg.remove(4));
						}
					}
					map.put(tag, list);
				}
				for (int i : map.keySet()) {
					switch (i) {
						case 21: {
							if (map.get(i).size() > 0) {
								SupportBand=map.get(i).get(0);
							}
							break;
						}
						case 22: {
							if (map.get(i).size() > 0) {
								TDDtype=map.get(i).get(0);
							}
							break;
						}
					}
				}
				DevStation devStation=Constant.getDevStation(ip);
                devStation.setSupportBand(SupportBand);
                devStation.setTDDtype(TDDtype);
                Constant.upDateDevStation(ip,devStation);
                send(session,FourGData.INIT_NOTIFICATION_RSP(seq++));
                scheduleNextCommand(() -> {
                   send(session,FourGData.INIT_CONFIG(seq++,devStation.getInitConfig().getBandwidth(),
                           devStation.getInitConfig().getTimeDelayField(),devStation.getInitConfig().getSynchronousMode(),
                           devStation.getInitConfig().getFrequencyOffset(),devStation.getInitConfig().getOperatingBand()));
                }, 1);
                daCallBackListerner.messageReceived(FourGProtocolConst.KEY_INIT_NOTIFICATION,session,
                        new DeviceStartupNotification(SupportBand,TDDtype),true);
			}else if (msg.get(0) == 0x01 && msg.get(1) == 0x03){
			    DevStation devStation=Constant.getDevStation(ip);
			    if (devStation.getTDDtype()==-1){
                    devStation.setTDDtype(-2);
                    devStation.setInitConfigSuc(true);
                    Constant.upDateDevStation(ip,devStation);
                    send(session,FourGData.SYSTEM_STATUS_REQ(seq++));
                }

				// TODO: 2020-07-10 心跳
                daCallBackListerner.messageReceived(FourGProtocolConst.KEY_HEARBEAT_ACK,session,
                        null,false);
			}else if (msg.get(0) == 1 && msg.get(1) == 8){
                // TODO: 2020-07-10 停止扫频响应
                daCallBackListerner.messageReceived(FourGProtocolConst.KEY_SNIFFER_STOP_RSP,session,
                        null,false);
            }else if (msg.get(0) == 1 && msg.get(1) == 10) {
                // TODO: 2020-07-10 复位扫频响应
                daCallBackListerner.messageReceived(FourGProtocolConst.KEY_SNIFFER_REST_RSP,session,
                        null,false);
            }else if (msg.get(0) == 2 && msg.get(1) == 29) {
                // TODO: 2020-07-10 初始配置完成
                if (msg.get(4) == 2 && msg.get(7) == 0) {
                    send(session,FourGData.SYSTEM_TIME(seq++));
                    daCallBackListerner.messageReceived(FourGProtocolConst.KEY_INIT_CONFIG_COMP,session,
                            null,false);
                }else if (msg.get(4) == 1){
                    if (msg.get(11) == 2 && msg.get(14) == 0) {
                        send(session,FourGData.SYSTEM_TIME(seq++));
                        daCallBackListerner.messageReceived(FourGProtocolConst.KEY_INIT_CONFIG_COMP,session,
                                null,false);
                    }
                }
            }else if (msg.get(0) == 2 && msg.get(1) == 2) {
                // TODO: 2020-07-10 设置系统时间响应
                if (msg.get(4) == 2 && msg.get(7) == 0) {
                    send(session,FourGData.CLOSE_DBM(seq++));
                    //返回状态码，0表示成功，非0表示错误原因
                    daCallBackListerner.messageReceived(FourGProtocolConst.KEY_SYSTEM_TIME_RSP,session,
                            null,false);
                }else if (msg.get(4) == 1) {
                    if (msg.get(11) == 2 && msg.get(14) == 0) {
                        send(session,FourGData.CLOSE_DBM(seq++));
                        daCallBackListerner.messageReceived(FourGProtocolConst.KEY_SYSTEM_TIME_RSP,session,
                                null,false);
                    }
                }
            }else if (msg.get(0) == 2 && msg.get(1) == 48) {
                // TODO: 2020-07-10 返回配置DBM结果
                if (msg.get(4) == 2 && msg.get(7) == 0) {
                    DevStation devStation=Constant.getDevStation(ip);
                    if (devStation.isInitConfigSuc()==false){
                        send(session,FourGData.SYSTEM_STATUS_REQ(seq++));
                    }

                    //配置DBM成功
                    daCallBackListerner.messageReceived(FourGProtocolConst.KEY_TX_POWER_DBM_CONFIG_RSP,session,
                            null,false);
                } else if (msg.get(4) == 1) {
                    if (msg.get(11) == 2 && msg.get(14) == 0) {
                        DevStation devStation=Constant.getDevStation(ip);
                        if (devStation.isInitConfigSuc()==false){
                            send(session,FourGData.SYSTEM_STATUS_REQ(seq++));
                        }
                        //配置DBM成功
                        daCallBackListerner.messageReceived(FourGProtocolConst.KEY_TX_POWER_DBM_CONFIG_RSP,session,
                                null,false);
                    }
                }
            } else if (msg.get(0) == 2 && msg.get(1) == 0x0b) {
                // TODO: 2020-07-10 系统状态上报
                SystemStatus systemStatus=new SystemStatus();
                ArrayList<Integer> ms = msg;
                int length = ms.get(20);
                byte[] soft_state = new byte[length];
                for (int i = 0; i < length; i++) {
                    soft_state[i] = ms.get(21 + i).byteValue();
                }
                systemStatus.setSoft_state(ms.get(17));
                if (ms.size() > length + 20 + 4) {
                    systemStatus.setCpu_tem(ms.get(length + 20 + 4));
                }
                if (ms.size() > length + 20 + 8) {
                    systemStatus.setCpu_use(ms.get(length + 20 + 8));
                }
                if (ms.size() > length + 20 + 12) {
                    systemStatus.setRom_use(ms.get(length + 20 + 12));
                }
                if (ms.size() > length + 20 + 16) {
                    systemStatus.setTem(ms.get(length + 20 + 16));
                }
                DevStation devStation=Constant.getDevStation(ip);
                if (devStation.isInitConfigSuc()==false){
                    send(session,FourGData.SNIFFER_START(seq++,devStation.getScanSet().getPciList(),
                            devStation.getScanSet().getEarfchList(),devStation.getScanSet().getRssi(),
                            devStation.getScanSet().getScan_result()));
                }else {
                    scheduleNextCommand(() -> {
                        send(session,FourGData.SYSTEM_STATUS_REQ(seq++));
                    }, 20);
                }
                daCallBackListerner.messageReceived(FourGProtocolConst.KEY_SYSTEM_STATUS_REPORT,session,
                        systemStatus,true);
            }else if (msg.get(0) == 1 && msg.get(1) == 0x0b) {
                // TODO: 2020-07-10 扫频结果上报
                if (((msg.get(2) & 0xFF) * 256) + (msg.get(3) & 0xFF) == msg.size() - 4) {
                    ArrayList<ArrayList<Integer>> mList = new ArrayList<>();
                    while (msg.size() > 6) {
                        int tag = msg.remove(4);
                        int tagLeng = 0;
                        if (msg.size() > 6) {
                            tagLeng = (msg.remove(4) & 0xFF) * 256 + msg.remove(4) & 0xFF;
                        }
                        if (tag == 11) {
                            ArrayList<Integer> list = new ArrayList<>();
                            while (tagLeng > 0) {
                                tagLeng--;
                                if (msg.size() > 4) {
                                    list.add(msg.remove(4));
                                }
                            }
                            mList.add(list);
                        } else {
                            while (tagLeng > 0) {
                                tagLeng--;
                                if (msg.size() > 4) {
                                    msg.remove(4);
                                }
                            }
                        }

                    }

                    List< ScanResult> ScanResultlist=new ArrayList<>();
                    for (ArrayList<Integer> list : mList) {
                        if (list.size() > 7) {
                            ScanResult scanResult = new ScanResult();
                            scanResult.setTime(System.currentTimeMillis());
                            scanResult.setFrequency(((list.get(0) & 0xFF) * 256) + (list.get(1) & 0xFF));
                            scanResult.setPci(((list.get(2) & 0xFF) * 256) + (list.get(3) & 0xFF));
                            scanResult.setTAC(((list.get(4) & 0xFF) * 256) + (list.get(5) & 0xFF));
                            scanResult.setRSSI(((list.get(6) & 0xFF) * 256) + (list.get(7) & 0xFF));
                            if (list.size() > 9) {
                                scanResult.setPriority(((list.get(8) & 0xFF) * 256) + (list.get(9) & 0xFF));
                            }
                            ScanResultlist.add(scanResult);
                        }
                    }
                    send(session,FourGData.SNIFFER_RESULT_REPORT_RSP(seq++));
                    daCallBackListerner.messageReceived(FourGProtocolConst.KEY_SNIFFER_RESULT_REPORT,session,
                            ScanResultlist,true);
                }
            } else if (msg.get(0) == 1 && msg.get(1) == 0x0d) {
                send(session,FourGData.SNIFFER_RESULT_REPORT_END_RSP(seq++));
                scheduleNextCommand(() -> {
                    DevStation devStation=Constant.getDevStation(ip);
                    if (devStation.isInitConfigSuc()==false){
                        send(session,FourGData.CellConfig(devStation.getCellConfig().getPciList(),devStation.getCellConfig().getPlmn(),
                                devStation.getCellConfig().getPilot_frequency_list(),devStation.getCellConfig().getDownlink_frequency_point(),
                                devStation.getCellConfig().getCell_pci(),devStation.getCellConfig().getTac(),
                                devStation.getCellConfig().getUplink_frequency_point(),devStation.getCellConfig().getMeasure(),
                                devStation.getCellConfig().getTransmitted_power(),devStation.getCellConfig().getTac_cycle()));
                    }

                }, 1);
                // TODO: 2020-07-10 扫频结果上报结束
                daCallBackListerner.messageReceived(FourGProtocolConst.KEY_SNIFFER_RESULT_REPORT_END,session,
                        null,false);
            }else if (msg.get(0) == 1 && msg.get(1) == 16) {
                // TODO: 2020-07-10 小区配置响应
                if (msg.get(4) == 6 && msg.get(7) == 0) {
                    DevStation devStation=Constant.getDevStation(ip);
                    if (devStation.isInitConfigSuc()==false){
                        send(session,FourGData.SOFTWARE_INFO_REQ(seq++));
                        scheduleNextCommand(() -> {
                            DevStation devStation1=Constant.getDevStation(ip);
                            devStation1.setInitConfigSuc(true);
                            Constant.upDateDevStation(ip,devStation1);
                            send(session,FourGData.run_time_para_cfg(seq++));
                        }, 3);
                    }

                    //0表示成功
                    daCallBackListerner.messageReceived(FourGProtocolConst.KEY_CELL_CONFIG_RSP,session,
                            new ResultCode(0),true);
                } else if (msg.get(4) == 1) {
                    if (msg.get(11) == 6 && msg.get(14) == 0) {
                        DevStation devStation=Constant.getDevStation(ip);
                        if (devStation.isInitConfigSuc()==false){
                            send(session,FourGData.SOFTWARE_INFO_REQ(seq++));
                            scheduleNextCommand(() -> {
                                DevStation devStation1=Constant.getDevStation(ip);
                                devStation1.setInitConfigSuc(true);
                                Constant.upDateDevStation(ip,devStation1);
                                send(session,FourGData.run_time_para_cfg(seq++));
                            }, 3);
                        }
                        //0表示成功
                        daCallBackListerner.messageReceived(FourGProtocolConst.KEY_CELL_CONFIG_RSP,session,
                                new ResultCode(0),true);
                    } else if (msg.get(11) == 6 && msg.get(14) != 0) {
                        DevStation devStation=Constant.getDevStation(ip);
                        if (devStation.isInitConfigSuc()==false){
                            send(session,FourGData.SOFTWARE_INFO_REQ(seq++));
                            scheduleNextCommand(() -> {
                                DevStation devStation1=Constant.getDevStation(ip);
                                devStation1.setInitConfigSuc(true);
                                Constant.upDateDevStation(ip,devStation1);
                                send(session,FourGData.run_time_para_cfg(seq++));
                            }, 3);
                        }
                        //1：表示解析失败
                        //2：表示该频点不支持
                        //3：软件内部错误
                        daCallBackListerner.messageReceived(FourGProtocolConst.KEY_CELL_CONFIG_RSP,session,
                                new ResultCode(msg.get(14)),true);
                    }
                } else if (msg.get(4) == 6 && msg.get(7) != 0) {
                    DevStation devStation=Constant.getDevStation(ip);
                    if (devStation.isInitConfigSuc()==false){
                        send(session,FourGData.SOFTWARE_INFO_REQ(seq++));
                        scheduleNextCommand(() -> {
                            DevStation devStation1=Constant.getDevStation(ip);
                            devStation1.setInitConfigSuc(true);
                            Constant.upDateDevStation(ip,devStation1);
                            send(session,FourGData.run_time_para_cfg(seq++));
                        }, 3);
                    }
                    //1：表示解析失败
                    //2：表示该频点不支持
                    //3：软件内部错误
                    daCallBackListerner.messageReceived(FourGProtocolConst.KEY_CELL_CONFIG_RSP,session,
                            new ResultCode(msg.get(14)),true);
                }
            }else if (msg.get(0) == 1 && msg.get(1) == 19) {
                // TODO: 2020-07-13 侦码上报
                if ((msg.get(2) & 0xFF) * 256 + (msg.get(3) & 0xFF) == msg.size() - 4) {
                    if (msg.get(15) == 28) {
                        for (int i = 18; i < (msg.size() - 29); i += 30) {
                            byte[] imsi = new byte[15];
                            for (int j = 0; j < 15; j++) {
                                imsi[j] = msg.get(i + j).byteValue();
                            }
                            UeidReportData data=new UeidReportData();
                            data.setHaveImei(false);
                            byte[] imei = new byte[15];
                            boolean isHasImei = false;
                            for (int j = 0; j < 15; j++) {
                                if (msg.get(15 + i + j) != 0) {
                                    imei[j] = msg.get(15 + i + j).byteValue();
                                    isHasImei = true;
                                } else {
                                    imei[j] = 0;
                                }
                            }
                            data.setImsi(CharacterConversionTool.hexStr2Str(CharacterConversionTool.bytesToHexString1(imsi)));
                            if (isHasImei) {
                                data.setHaveImei(true);
                                data.setImei(CharacterConversionTool.hexStr2Str(CharacterConversionTool.bytesToHexString1(imei)));
                            }
                            data.setOperator(CharacterConversionTool.getOperatorByIMSI(data.getImsi()));
                            String numHead= SignalDistanceCalculator.imsi2MobilePhoneNumberHead(data.getImsi());
                            data.setPhoneNum(numHead);
                            data.setDistrict(loader.getLocation(numHead));
                            daCallBackListerner.messageReceived(FourGProtocolConst.KEY_UEID_REPORT,session,
                                    data,true);
                        }
                    }
                } else {
                    // TODO: 2020-07-13 ??? 
//                    stationInfo.setMsg(msg);
                }
            }else if (msg.get(0) == 2 && msg.get(1) == 6) {
                // TODO: 2020-07-13 软件版本查询响应
                int softVerLength = msg.get(13);
                byte[] softVer = new byte[softVerLength];
                for (int i = 0; i < softVerLength; i++) {
                    softVer[i] = msg.get(i + 14).byteValue();
                }
                int Length1 = msg.get(14 + softVerLength + 2);
                byte[] softVer1 = new byte[Length1];
                for (int i = 0; i < Length1; i++) {
                    softVer1[i] = msg.get(i + 14 + softVerLength + 3).byteValue();
                }
                int Length2 = msg.get(14 + softVerLength + 3 + Length1 + 2);
                byte[] softVer2 = new byte[Length2];
                for (int i = 0; i < Length2; i++) {
                    softVer2[i] = msg.get(i + 14 + softVerLength + 3 + Length1 + 3).byteValue();
                }
                int Length3 = msg.get(14 + softVerLength + 3 + Length1 + 3 + Length2 + 2);
                byte[] softVer3 = new byte[Length3];
                for (int i = 0; i < Length3; i++) {
                    softVer3[i] = msg.get(i + 14 + softVerLength + 3 + Length1 + 3 + Length2 + 3).byteValue();
                }
                VersionData data=new VersionData();
                data.setSoftVersion(CharacterConversionTool.hexStr2Str(CharacterConversionTool.bytesToHexString1(softVer)));
                data.setPhysicalVersion(CharacterConversionTool.hexStr2Str(CharacterConversionTool.bytesToHexString1(softVer1)));
                data.setKernelVersion(CharacterConversionTool.hexStr2Str(CharacterConversionTool.bytesToHexString1(softVer2)));
                data.setHardwareVersion(CharacterConversionTool.hexStr2Str(CharacterConversionTool.bytesToHexString1(softVer3)));
                daCallBackListerner.messageReceived(FourGProtocolConst.KEY_SOFTWARE_INFO_REPORT,session,
                        data,false);
                scheduleNextCommand(() -> {
                    send(session,FourGData.SYSTEM_STATUS_REQ(seq++));
                }, 10);
            }else if (msg.get(0) == 1 && msg.get(1) == 18){
                // TODO: 2020-07-13 小区更新响应
                if (msg.get(4) == 6 && msg.get(7) == 0) {
                    //0表示成功
                    daCallBackListerner.messageReceived(FourGProtocolConst.KEY_CELL_UPDATE_RSP,session,
                            new ResultCode(0),true);
                } else if (msg.get(4) == 1) {
                    if (msg.get(11) == 6 && msg.get(14) == 0) {
                        //0表示成功
                        daCallBackListerner.messageReceived(FourGProtocolConst.KEY_CELL_UPDATE_RSP,session,
                                new ResultCode(0),true);
                    } else if (msg.get(11) == 6 && msg.get(14) != 0) {

                        daCallBackListerner.messageReceived(FourGProtocolConst.KEY_CELL_UPDATE_RSP,session,
                                new ResultCode(msg.get(14)),true);
                    }
                } else if (msg.get(4) == 6 && msg.get(7) != 0) {

                    daCallBackListerner.messageReceived(FourGProtocolConst.KEY_CELL_UPDATE_RSP,session,
                            new ResultCode(msg.get(14)),true);
                }
            }else if (msg.get(0) == 2 && msg.get(1) == 15) {
                // TODO: 2020-07-13 设备复位响应
                daCallBackListerner.messageReceived(FourGProtocolConst.KEY_EQUIP_RESET_RSP,session,
                        null,false);
            }else if (msg.get(0) == 2 && msg.get(1) == 40) {
                // TODO: 2020-07-13 定位场强上报
                Map<Integer, ArrayList<Integer>> map = new HashMap<>();
                while (msg.size() > 4) {
                    int tag = msg.remove(4);
                    int tagLength = 0;
                    if (msg.size() > 6) {
                        tagLength = (msg.remove(4) & 0xFF) * 256 + msg.remove(4) & 0xFF;
                    }
                    ArrayList<Integer> list = new ArrayList<>();
                    while (tagLength > 0) {
                        tagLength--;
                        if (msg.size() > 4) {
                            list.add(msg.remove(4));
                        }
                    }
                    map.put(tag, list);
                }
                TargetBean targetBean = new TargetBean();

                targetBean.setTime(System.currentTimeMillis());
                for (int i : map.keySet()) {
                    switch (i) {
                        case 53: {
                            byte[] bytes = new byte[map.get(i).size()];
                            int j = 0;
                            for (Integer integer : map.get(i)) {
                                bytes[j] = integer.byteValue();
                                j++;
                            }
                            targetBean.setImsi(CharacterConversionTool.hexStr2Str(CharacterConversionTool.bytesToHexString1(bytes)));
                            break;
                        }
                        case 54: {
                            byte[] bytes = new byte[map.get(i).size()];
                            int j = 0;
                            for (Integer integer : map.get(i)) {
                                bytes[j] = integer.byteValue();
                                j++;
                            }
                            targetBean.setDelay((CharacterConversionTool.bytesToInt4(bytes, 0)) + "");
                            break;
                        }
                        case 55: {
                            byte[] bytes = new byte[map.get(i).size()];
                            int j = 0;
                            for (Integer integer : map.get(i)) {
                                bytes[j] = integer.byteValue();
                                j++;
                            }
                            targetBean.setSinr((CharacterConversionTool.bytesToInt2(bytes, 0)));
                            break;
                        }
                        case 70: {
                            byte[] bytes = new byte[map.get(i).size()];
                            int j = 0;
                            for (Integer integer : map.get(i)) {
                                bytes[j] = integer.byteValue();
                                j++;
                            }
                            targetBean.setRsrp((CharacterConversionTool.bytesToInt4(bytes, 0)));
                            break;
                        }
                        case 71: {
                            byte[] bytes = new byte[map.get(i).size()];
                            int j = 0;
                            for (Integer integer : map.get(i)) {
                                bytes[j] = integer.byteValue();
                                j++;
                            }
                            targetBean.setFreq((CharacterConversionTool.bytesToInt4(bytes, 0)) + "");
                            break;
                        }
                    }
                }
                daCallBackListerner.messageReceived(FourGProtocolConst.KEY_NMM_DELAY_REPORT_MSG,session,
                        targetBean,true);
            } else if (msg.get(0) == 1 && msg.get(1) == 28) {
                // TODO: 2020-07-13 重定向响应
                Map<Integer, ArrayList<Integer>> map = new HashMap<>();
                while (msg.size() > 4) {
                    int tag = msg.remove(4);
                    int tagLength = 0;
                    if (msg.size() > 6) {
                        tagLength = (msg.remove(4) & 0xFF) * 256 + msg.remove(4) & 0xFF;
                    }
                    ArrayList<Integer> list = new ArrayList<>();
                    while (tagLength > 0) {
                        tagLength--;
                        if (msg.size() > 4) {
                            list.add(msg.remove(4));
                        }
                    }
                    map.put(tag, list);
                }
                for (int i : map.keySet()) {
                    if (i == 6) {
                        //0表示成功，非0表示错误，错误码待定
                        //1：消息解析失败
                        //2：语法检查不通过
                        //3：软件错误
                        //4：重定向4G时，BBU未配置工作频点
                        //5：重定向4G时，频点与工作频带重复
                        daCallBackListerner.messageReceived(FourGProtocolConst.KEY_UE_REDIREC_RSP,session,
                                map.get(i).get(0),true);
                    }
                }
            }else if (msg.get(0) == 2 && msg.get(1) == 72) {
                daCallBackListerner.messageReceived(FourGProtocolConst.KEY_CHANG_LTE_MODE_RSP,session,
                        null,false);
            }else if (msg.get(0) == 1 && msg.get(1) == 6) {
                daCallBackListerner.messageReceived(FourGProtocolConst.KEY_SNIFFER_START_RSP,session,
                        null,false);
            }else if (msg.get(0) == 1 && msg.get(1) == 20) {
                daCallBackListerner.messageReceived(FourGProtocolConst.KEY_UEID_REPORT_RSP,session,
                        null,false);
            }else if (msg.get(0) == 2 && msg.get(1) ==33) {
                daCallBackListerner.messageReceived(FourGProtocolConst.KEY_LOCATION_MODE_SETTING_RSP,session,
                        null,false);
            }else if (msg.get(0) == 2 && msg.get(1) ==20) {
                int pos=4;
                SyncStatus syncStatus=new SyncStatus();
                while (msg.size() > 4) {
                    int tag = (int) msg.get(pos);
                    int tagLength = 0;
                    if (msg.size() > 6) {
                        int lenth1= (int) msg.get(pos+1);
                        int  lenth2= (int) msg.get(pos+2);

                        int contentLenth=CharacterConversionTool.bytes2Short(new byte[]{(byte) lenth1, (byte) lenth2});

                        byte[] value=new byte[contentLenth];
                        for (int i=0;i<contentLenth;i++){
                            int code= (int) msg.get(pos+3+i);
                            System.arraycopy(new byte[]{(byte)code},0,value,i,1);
                        }

                        if (tag==15){
                            syncStatus.setTypeCode((int)value[0]);
                        }else if (tag==16){
                            syncStatus.setStatus((int)value[0]);
                        }else if (tag==38){
                            syncStatus.setPoint(CharacterConversionTool.bytes2Short(value));
                        }else if (tag==39){
                            syncStatus.setPci(CharacterConversionTool.bytes2Short(value));
                        }else if (tag==40){
                            syncStatus.setRssi(CharacterConversionTool.bytes2Short(value));
                        }else if (tag==41){
                            syncStatus.setTac(CharacterConversionTool.bytes2Short(value));
                        }
                        tagLength = 3+contentLenth;
                    }
                    pos=pos+tagLength;
                    if (pos==msg.size()){
                        break;
                    }
                }
                daCallBackListerner.messageReceived(FourGProtocolConst.KEY_SYNC_STATUS_REPORT,session,
                        syncStatus,true);
            }else if (msg.get(0) == 2 && msg.get(1) ==8){
                if (msg.get(4) == 2 ) {
                    //状态码：
                    //0：成功下载；1：解析错误
                    //2：软件内部错误
                    //3：下载失败
                    int code=msg.get(7);
                    daCallBackListerner.messageReceived(FourGProtocolConst.KEY_SOFTWARE_UPGRADE_RSP,session,
                            new ResultCode(code),true);
                }else if (msg.get(4) == 1&& msg.get(11) == 2) {
                    int code=msg.get(14);
                    daCallBackListerner.messageReceived(FourGProtocolConst.KEY_SOFTWARE_UPGRADE_RSP,session,
                            new ResultCode(code),true);
                }
			}else if (msg.get(0) == 2 && msg.get(1) ==9){
                if (msg.get(4) == 2 ) {
                    //状态码：
                    //0：成功；非0表示错误，错误码待定
                    int code=msg.get(7);
                    daCallBackListerner.messageReceived(FourGProtocolConst.KEY_SOFTWARE_UPGRADE_REPORT,session,
                            new ResultCode(code),true);
                }else if (msg.get(4) == 1&& msg.get(11) == 2) {
                    int code=msg.get(14);
                    daCallBackListerner.messageReceived(FourGProtocolConst.KEY_SOFTWARE_UPGRADE_REPORT,session,
                            new ResultCode(code),true);
                }
            }else if (msg.get(0) == 2 && msg.get(1) == 63) {
                try {
                    FieldStrengthData imsiData = new FieldStrengthData();
                    imsiData.setTime(System.currentTimeMillis());
                    int pos = 4;
                    while (msg.size() > 4) {

                        int tag = (int) msg.get(pos);
                        int tagLength = 0;
                        if (msg.size() > 6) {

                            int lenth1 = (int) msg.get(pos + 1);
                            int lenth2 = (int) msg.get(pos + 2);
                            int contentLenth = CharacterConversionTool.bytes2ShortBIG_ENDIAN(new byte[]{(byte) lenth1, (byte) lenth2});

                            byte[] value = new byte[contentLenth];
                            for (int i = 0; i < contentLenth; i++) {
                                int code = (int) msg.get(pos + 3 + i);

                                System.arraycopy(new byte[]{(byte) code}, 0, value, i, 1);
                            }
                            if (tag == 53) {
                                String imsi = CharacterConversionTool.hexStr2Str(CharacterConversionTool.bytesToHexString1(value));
                                if (null==imsi||imsi=="") {
                                    imsi = "";
                                }
                                imsiData.setImsi(imsi);
                            } else if (tag == 55) {
                                imsiData.setFieldstrength( CharacterConversionTool.bytes2Int(value) * 2);
                            }
                            tagLength = 3 + contentLenth;
                        }
                        pos = pos + tagLength;

                        if (pos >= msg.size()) {

                            break;
                        }

                    }
                    daCallBackListerner.messageReceived(FourGProtocolConst.KEY_SINR_RPT_FOR_MC_MODE,session,
                            imsiData,true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if (msg.get(0) == 2 && msg.get(1) == 52) {
                if (msg.get(4) == 2 ) {
                    //状态码：
                    //0：成功；非0表示错误，错误码待定
                    int code=msg.get(7);
                    daCallBackListerner.messageReceived(FourGProtocolConst.KEY_SCAN_MODE_RSP,session,
                            new ResultCode(code),true);
                }else if (msg.get(4) == 1&& msg.get(11) == 2) {
                    int code=msg.get(14);
                    daCallBackListerner.messageReceived(FourGProtocolConst.KEY_SCAN_MODE_RSP,session,
                            new ResultCode(code),true);
                }
            }else if (msg.get(0) == 2 && msg.get(1) == 56) {
                System.out.println(" ==:"+msg);
                daCallBackListerner.messageReceived(FourGProtocolConst.KEY_RUN_TIME_PARAMETERS_CONFIGURE_RESPONSE,session,
                            new ResultCode(1),true);
//                if (msg.get(4) == 2 ) {
//                    //状态码：
//                    //0：成功；非0表示错误，错误码待定
//                    int code=msg.get(7);
//                    daCallBackListerner.messageReceived(FourGProtocolConst.KEY_RUN_TIME_PARAMETERS_CONFIGURE_RESPONSE,session,
//                            new ResultCode(code),true);
//                }else if (msg.get(4) == 1&& msg.get(11) == 2) {
//                    int code=msg.get(14);
//                    daCallBackListerner.messageReceived(FourGProtocolConst.KEY_RUN_TIME_PARAMETERS_CONFIGURE_RESPONSE,session,
//                            new ResultCode(code),true);
//                }
            } else if (msg.get(0)==2&&msg.get(1)==64) {
			    //0x02 0x40 0x00 0x0x 0x58 0x00 0x02 0x06 0x72 0x01 0x0x 0x04 0x00 0x00 0x00 0x01
                if (msg.get(4) == 88 ) {
                    //状态码：
                    //0：成功；非0表示错误，错误码待定
                    try {
                        byte a=msg.get(7).byteValue();
                        byte b=msg.get(8).byteValue();
                        byte[] code=new byte[]{a,b};

                        int value=CharacterConversionTool.bytes2ShortBIG_ENDIAN(code)&0xffff;
                        daCallBackListerner.messageReceived(FourGProtocolConst.KEY_REPORT_USING_DL_EARFCN,session,
                                new ReportPoint(value,code),true);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
//                else if (msg.get(4) == 1&& msg.get(11) == 88) {
//                    //0x02 0x40 0x00 0x0x 0x01 0x0x 0x04 0x00 0x00 0x00 0x01  0x58 0x00 0x02 0x06 0x72
//                    byte a=msg.get(14).byteValue();
//                    byte b=msg.get(15).byteValue();
//                    byte[] code=new byte[]{a,b};
//                    short value=CharacterConversionTool.bytes2Short(code);
//                    daCallBackListerner.messageReceived(FourGProtocolConst.KEY_REPORT_USING_DL_EARFCN,session,
//                            new ResultCode(value),true);
//                }
            }
        }
    }

    
    @Override
    public void sessionCreated(IoSession session) throws Exception {
        //当有新的连接建立的时候，该方法被调用。

        SocketSessionConfig cfg = (SocketSessionConfig) session.getConfig();
        cfg.setReceiveBufferSize(1024);
        cfg.setReadBufferSize(1024);
        cfg.setKeepAlive(true);
        cfg.setSoLinger(0);
        String ip=session.getRemoteAddress().toString();
        DevStation devStation=Constant.getDevStation(ip);
        devStation.setIp(ip);
        devStation.setConnected(true);
        devStation.setSession(session);
        Constant.upDateDevStation(ip,devStation);
        daCallBackListerner.sessionCreated(session);

    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        //当连接被关闭的时候，此方法被调用。

        CloseFuture closeFuture = session.close(true);
        closeFuture.addListener(new IoFutureListener<IoFuture>() {
            public void operationComplete(IoFuture future) {
                if (future instanceof CloseFuture) {
                    ((CloseFuture) future).setClosed();
                }
            }
        });
        session.close(true);// 关闭session
        String ip=session.getRemoteAddress().toString();
        DevStation devStation=Constant.getDevStation(ip);
        devStation.setInitConfigSuc(false);
        devStation.setSession(null);
        devStation.setConnected(false);
        Constant.upDateDevStation(ip,devStation);
        daCallBackListerner.sessionClosed(session);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        //当 I/O 处理器的实现或是 Apache MINA 中有异常抛出的时候，此方法被调用。

        super.exceptionCaught(session, cause);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
        //当有新的连接打开的时候，该方法被调用。该方法在 sessionCreated之后被调用。
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
        //当消息被成功发送出去的时候，此方法被调用。
        daCallBackListerner.messageSent(session,CharacterConversionTool.bytesToHexString((byte[]) message));
    }

    public void setOnReciveCallBackListerner(DataReciveCallBackListerner daCallBackListerner) {
        this.daCallBackListerner = daCallBackListerner;
    }

    // 定时调度发送指令
    private  void scheduleNextCommand(Runnable command, int delaySeconds) {
        scheduler.schedule(command, delaySeconds, TimeUnit.SECONDS);
    }
}
