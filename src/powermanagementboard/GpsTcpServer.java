package powermanagementboard;

import java.io.*;
import java.net.*;

public class GpsTcpServer {
    private ServerSocket serverSocket;
    private GpsDataListener gpsDataListener;
    private Socket clientSocket;  // 用于存储最新连接的客户端Socket
    private PrintWriter out;      // 用于向客户端发送数据

    private int Port=8083;
    public GpsTcpServer(GpsDataListener listener) {
        this.gpsDataListener = listener;
    }

    // 启动服务
    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server is listening on port " + port);

            while (true) {
                clientSocket = serverSocket.accept();
                out = new PrintWriter(clientSocket.getOutputStream(), true); // 初始化输出流
                new ClientHandler(clientSocket, gpsDataListener).start();
            }
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }
    public void start() {
        try {
            serverSocket = new ServerSocket(Port);
            System.out.println("Server is listening on port " + Port);

            while (true) {
                clientSocket = serverSocket.accept();
                out = new PrintWriter(clientSocket.getOutputStream(), true); // 初始化输出流
                new ClientHandler(clientSocket, gpsDataListener).start();
            }
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }
    // 停止服务
    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Error stopping server: " + e.getMessage());
        }
    }

    // 发送数据到客户端
    public void sendData(String data) {
        if (out != null) {
            out.println(data);
            System.out.println("Sent: " + data);
        } else {
            System.out.println("No client is currently connected.");
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket clientSocket;
        private final GpsDataListener gpsDataListener;

        public ClientHandler(Socket socket, GpsDataListener listener) {
            this.clientSocket = socket;
            this.gpsDataListener = listener;
        }

        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Received: " + inputLine);

                    // 解析收到的GPS数据
                    if (inputLine.startsWith("$GNGLL")) {
                        try {
                            GpgllParser.GpgllData data = GpgllParser.parseGpgll(inputLine);
                            if (gpsDataListener != null) {
                                gpsDataListener.onGpsDataReceived(data);
                            }
                        } catch (IllegalArgumentException e) {
                            if (gpsDataListener != null) {
                                gpsDataListener.onError("Error parsing GPGLL sentence: " + e.getMessage());
                            }
                        }
                    }
                }
            } catch (IOException e) {
                if (gpsDataListener != null) {
                    gpsDataListener.onError("Client connection error: " + e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        GpsTcpServer server = new GpsTcpServer(new GpsDataListener() {
            @Override
            public void onGpsDataReceived(GpgllParser.GpgllData data) {
                System.out.println("GPS Data: " + data);
            }

            @Override
            public void onError(String errorMessage) {
                System.err.println("Error: " + errorMessage);
            }
        });

        server.start();

        // 示例：发送AT指令
        try {
            Thread.sleep(5000); // 等待客户端连接后发送数据
            server.sendData("AT+GPS=?");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


