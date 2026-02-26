package fourg.interfaces;

import org.apache.mina.core.session.IoSession;

public interface DataReciveCallBackListerner {

    void sessionCreated(IoSession session);

    void sessionClosed(IoSession session);

    void messageSent( IoSession ioSession,String message);
    /**
     *
     * @param protocolName 协议名称
     * @param session  远程设备连接对象
     * @param data  数据
     * @param isReturnData  是否返回数据
     */
    void messageReceived(String protocolName,IoSession session,Object data,boolean isReturnData);

}
