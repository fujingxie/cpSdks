package gsm;

import java.util.Map;

public class GsmReciveBean {

	private byte MessageNumber;//消息编号

	private byte zaibo;//载波指示

	private byte FuncationNo;//消息功能参数
	
	private  Map<String, byte[]> BodyMap;

	public byte getMessageNumber() {
		return MessageNumber;
	}

	public void setMessageNumber(byte messageNumber) {
		MessageNumber = messageNumber;
	}

	public byte getZaibo() {
		return zaibo;
	}

	public void setZaibo(byte zaibo) {
		this.zaibo = zaibo;
	}

	public byte getFuncationNo() {
		return FuncationNo;
	}

	public void setFuncationNo(byte funcationNo) {
		FuncationNo = funcationNo;
	}

	public Map<String, byte[]> getBodyMap() {
		return BodyMap;
	}

	public void setBodyMap(Map<String, byte[]> bodyMap) {
		BodyMap = bodyMap;
	}

	@Override
	public String toString() {
		return "GsmReciveBean{" +
				"MessageNumber=" + MessageNumber +
				", zaibo=" + zaibo +
				", FuncationNo=" + FuncationNo +
				", BodyMap=" + BodyMap +
				'}';
	}
}
