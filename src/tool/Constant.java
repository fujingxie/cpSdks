package tool;

import fourg.data.DevStation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Constant {
	public static HashMap<String, DevStation> devStationHashMap=new HashMap<>();

	// 30秒后超时
	public static final int WCDMA_IDELTIMEOUT = 30;
	// 15秒发送一次心跳包
	public static final int WCDMA_HEARTBEATRATE = 15;
	public static final int WCDMA_SEDN_PORT = 9001;

	// 30秒后超时
	public static final int GSM_IDELTIMEOUT = 30;
	// 15秒发送一次心跳包
	public static final int GSM_HEARTBEATRATE = 15;
	public static final int GSM_SEDN_PORT = 5558;
	public static final int GSM_RECEIVE_PORT = 5557;

	// 30秒后超时
	public static final int CMDA_IDELTIMEOUT = 30;
	// 15秒发送一次心跳包
	public static final int CMDA_HEARTBEATRATE = 15;
	public static final int CMDA_ = 6668;
	public static final int CMDA_RECEIVE_PORT = 6667;

	//4g协议------------------------------------------------------>>>>>

	public static final int TCP_PORT = 32790;


	//ftp config---------------------------------------------------->
	public static final String FTP_USER = "admin";
	public static final String FTP_PASSWORD = "123456";
	public static final int FTP_PORT = 9988;


	public static void upDateDevStation(String ip, DevStation devStation) {
		Constant.devStationHashMap.put(ip,devStation);
	}

	public static DevStation getDevStation(String ip) {
		DevStation devStation=Constant.devStationHashMap.get(ip);
		if (devStation==null){
			devStation=new DevStation();
		}
		return devStation;
	}

	public static List<DevStation> getDevStationList(){
		if (Constant.devStationHashMap==null){return new ArrayList<>();}
		return new ArrayList<>(Constant.devStationHashMap.values());
	}

	public static long restart5GTime=0L;

}
