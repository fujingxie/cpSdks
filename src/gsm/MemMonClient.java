package gsm;

import java.net.InetSocketAddress;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import tool.Constant;

public class MemMonClient extends IoHandlerAdapter {
	private final static Logger LOGGER = LoggerFactory
			.getLogger(MemMonClient.class);

	private IoSession session;

	private IoConnector connector;

	/**
	 * Default constructor.
	 */
	public MemMonClient() {

		LOGGER.debug("UDPClient::UDPClient");
		LOGGER.debug("Created a datagram connector");
		connector = new NioDatagramConnector();

		LOGGER.debug("Setting the handler");
		connector.setHandler(this);

		LOGGER.debug("About to connect to the server...");
		ConnectFuture connFuture = connector.connect(new InetSocketAddress(
				"localhost", Constant.GSM_RECEIVE_PORT));

		LOGGER.debug("About to wait.");
		connFuture.awaitUninterruptibly();

		LOGGER.debug("Adding a future listener.");
		connFuture.addListener(new IoFutureListener<ConnectFuture>() {
			public void operationComplete(ConnectFuture future) {
				if (future.isConnected()) {
					LOGGER.debug("...connected");
					session = future.getSession();
					try {
						sendData();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					LOGGER.error("Not connected...exiting");
				}
			}
		});
	}

	private void sendData() throws InterruptedException {
		for (int i = 0; i < 1; i++) {
			byte[] data=new byte[] {(byte) 0x88,0x00,0x00,0x00,0x02,0x11,0x00,0x00,0x0b,0x01,
					0x01,0x34,0x35,0x34,0x00,0x00,0x00,0x00,0x00,0x0b,0x02,0x01,0x31,0x32,0x00,
					0x00,0x00,0x00,0x00,0x00,0x0b,0x03,0x01,0x31,0x32,0x35,0x36,0x00,0x00,0x00,
					0x00,0x0b,0x04,0x01,0x34,0x32,0x32,0x35,0x36,0x00,0x00,0x00,0x0b,0x06,0x01,
					0x36,0x30,0x00,0x00,0x00,0x00,0x00,0x00,0x04,0x0b,0x01,0x00,0x04,0x0a,0x01,
					0x00,0x07,0x0c,0x01,0x58,0x02,0x00,0x00,0x0b,0x50,0x01,0x33,0x35,0x00,0x00,
					0x00,0x00,0x00,0x00,0x0b,0x51,0x01,0x32,0x32,0x00,0x00,0x00,0x00,0x00,0x00,
					0x0b,0x52,0x01,0x35,0x35,0x00,0x00,0x00,0x00,0x00,0x00,0x05,0x56,0x01,0x01,
					0x00,0x05,0x57,0x01,0x5e,0x00,0x05,0x58,0x01,0x00,0x00,0x05,0x59,0x01,0x00,
					0x00,0x05,0x5f,0x01,0x00,0x00
					};
 			long free = Runtime.getRuntime().freeMemory();
			IoBuffer buffer = IoBuffer.allocate(1024);
			buffer.put(data);
			buffer.flip();
			session.write(buffer);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				throw new InterruptedException(e.getMessage());
			}
		}
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		cause.printStackTrace();
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		LOGGER.debug("Session recv...");
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		LOGGER.debug("Message sent...");
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		LOGGER.debug("Session closed...");
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		LOGGER.debug("Session created...");
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		LOGGER.debug("Session idle...");
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		LOGGER.debug("Session opened...");
	}

	public static void main(String[] args) {
		
		new MemMonClient();
	}
}
