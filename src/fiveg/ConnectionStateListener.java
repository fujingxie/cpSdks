package fiveg;

import java.net.Socket;

public interface ConnectionStateListener {
    // 当客户端连接时调用
    void onClientConnected(Socket session);

    // 当客户端断开连接时调用
    void onClientDisconnected(Socket session);
}
