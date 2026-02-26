package fiveg;


import java.net.Socket;

public interface MessageReceivedCallback {
    void onMessageReceived(Socket session, Message message);
}

