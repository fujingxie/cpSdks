package gsm;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class GsmEncoder extends ProtocolEncoderAdapter {

	// ˝æ› ‰»Î
	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		// TODO Auto-generated method stub
		 byte[] bytes = (byte[]) message;

	     IoBuffer buffer = IoBuffer.allocate(1024);
	     buffer.setAutoExpand(true);

	     buffer.put(bytes);
	     buffer.flip();

	     out.write(buffer);
	     out.flush();

	     buffer.free();
	}

}
