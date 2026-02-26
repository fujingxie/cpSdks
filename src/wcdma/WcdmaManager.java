package wcdma;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import tool.CharacterConversionTool;
import tool.Constant;

import java.io.IOException;
import java.net.InetSocketAddress;



public class WcdmaManager extends IoHandlerAdapter {
	private DataReciveCallBackListerner daCallBackListerner=null;//消息回调

	private static WcdmaManager instance = null;

	private static IoSession session = null;

	private WcdmaManager() throws IOException {
		NioDatagramAcceptor acceptor = new NioDatagramAcceptor();
		acceptor.setHandler(this);
       
		acceptor.getFilterChain().addLast("logging", new LoggingFilter());
		acceptor.getFilterChain().addLast("coderc", new ProtocolCodecFilter(new WcdmaFactory()));

		DatagramSessionConfig dcfg = acceptor.getSessionConfig();
		dcfg.setReuseAddress(true);
		acceptor.getSessionConfig().setReceiveBufferSize(2048 * 5000);// 接收缓冲区1M
		acceptor.getSessionConfig().setBothIdleTime(30);
		// 设置session配置，30秒内无操作进入空闲状态
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, Constant.WCDMA_IDELTIMEOUT);

		//心跳配置
		KeepAliveMessageFactory heartBeatFactory = new KeepAliveMessageFactoryImpl();
		KeepAliveRequestTimeoutHandler heartBeatHandler = new KeepAliveRequestTimeoutHandlerImpl();
		KeepAliveFilter heartBeat = new KeepAliveFilter(heartBeatFactory, IdleStatus.BOTH_IDLE, heartBeatHandler);
		// 是否回发
		heartBeat.setForwardEvent(false);
		// 发送频率
		heartBeat.setRequestInterval(Constant.WCDMA_HEARTBEATRATE);
		
		acceptor.getFilterChain().addLast("heartbeat", heartBeat);
		acceptor.bind(new InetSocketAddress(Constant.WCDMA_SEDN_PORT));

		System.out.println("Server started...");
	}

	public static WcdmaManager getInstance() throws IOException {
		// 如果instance未被初始化，则初始化该类实例
		if (instance == null) {
			instance = new WcdmaManager();
		}
		return instance;
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		// TODO Auto-generated method stub
		super.messageReceived(session, message);
		this.session = session;
		String udpstring = "";
		if (message != null && message.getClass() == String.class) {
			udpstring = (String) message;
		}
		System.out.println("Wcdma messageReceived..." + message);
		Object obj = WcdmaDataHelper.parseData(udpstring);
		if (obj == null) {
			return;
		}
		if (obj.getClass() == ReporBean.class) {
			ReporBean bean = (ReporBean) obj;
			if (WcdmaDataHelper.HeartBeatAck_IndiId.equalsIgnoreCase(bean.getIndiNum())) {
				// 心跳上报
				if(daCallBackListerner!=null) {
					daCallBackListerner.onHeartReport(bean);
				}
			} else {
				if(daCallBackListerner!=null) {
					daCallBackListerner.onReport(bean);
				}
			}
		} else if (obj.getClass() == CmdAckBean.class) {
			CmdAckBean cmdAckBean = (CmdAckBean) obj;
			if (cmdAckBean.getIndiName().equalsIgnoreCase(WcdmaDataHelper.KEY_GetRfPara)) {
				// 获取小区配置参数结果
				String sucStus = WcdmaDataHelper.parseValueofKey(cmdAckBean.getContent(), WcdmaDataHelper.KEY_RESULT);
				if (!CharacterConversionTool.isEmpty(sucStus)
						&& WcdmaDataHelper.KEY_SUCCESS.equalsIgnoreCase(sucStus)) {
					if(daCallBackListerner!=null) {
						daCallBackListerner.onGetRfPara(cmdAckBean);
					}
				}
			} else if (cmdAckBean.getIndiName().equalsIgnoreCase(WcdmaDataHelper.KEY_StartCell)) {
				String sucStus = WcdmaDataHelper.parseValueofKey(cmdAckBean.getContent(), WcdmaDataHelper.KEY_RESULT);
				if (!CharacterConversionTool.isEmpty(sucStus)
						&& !WcdmaDataHelper.KEY_SUCCESS.equalsIgnoreCase(sucStus)) {
					// 开启小区或停止小区结果
					if(daCallBackListerner!=null) {
						daCallBackListerner.onStartCell(true);
					}
				} else {
					if(daCallBackListerner!=null) {
						daCallBackListerner.onStartCell(false);
					}
				}
			} else if (cmdAckBean.getIndiName().equalsIgnoreCase(WcdmaDataHelper.KEY_StopCell)) {
				String sucStus = WcdmaDataHelper.parseValueofKey(cmdAckBean.getContent(), WcdmaDataHelper.KEY_RESULT);
				if (!CharacterConversionTool.isEmpty(sucStus)
						&& !WcdmaDataHelper.KEY_SUCCESS.equalsIgnoreCase(sucStus)) {
					// 开启小区或停止小区结果
					if(daCallBackListerner!=null) {
						daCallBackListerner.onStartCell(true);
					}
				} else {
					if(daCallBackListerner!=null) {
						daCallBackListerner.onStartCell(true);
					}
				}
			}
			if(daCallBackListerner!=null) {
				daCallBackListerner.onCmdAck(cmdAckBean);
			}
		}

	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		System.out.println("Wcdma created...");

	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		System.out.println("Wcdma closed...");

	}

	public static void main(String[] args) throws IOException {
		WcdmaManager.getInstance();
	}

	public static void send(String cmd) {
		if (session != null) {
			session.write(cmd);
		}
	}
	
	public void setOnReciveCallBackListerner(DataReciveCallBackListerner daCallBackListerner) {
		this.daCallBackListerner=daCallBackListerner;
	}
	public interface DataReciveCallBackListerner{
		
		public void onHeartReport(ReporBean obj);
		
		public void onReport(ReporBean obj);
		
		public void onGetRfPara(CmdAckBean cmdAckBean);
		
		public void onStartCell(boolean isSucc);
		
		public void onStopCell(boolean isSucc);
		
		public void onCmdAck(CmdAckBean cmdAckBean);
	}

}
