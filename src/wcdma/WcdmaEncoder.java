package wcdma;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class WcdmaEncoder extends ProtocolEncoderAdapter {

	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		// TODO Auto-generated method stub
		CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();

		String value = message == null ? "" : message.toString();
		IoBuffer buf = IoBuffer.allocate(value.length()).setAutoExpand(true);
		buf.putString(value, encoder);
		buf.flip();
		out.write(buf);
	}

}
