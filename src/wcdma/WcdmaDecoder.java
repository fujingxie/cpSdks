package wcdma;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import tool.CharacterConversionTool;

public class WcdmaDecoder extends CumulativeProtocolDecoder{
	/**
	 * 服务器接收到信息，并返回信息
	 */
	@Override
	protected boolean doDecode(IoSession arg0, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		// TODO Auto-generated method stub
	
        String message1=CharacterConversionTool.ioBufferToString(in);
       
        out.write(message1);   
     
        return true;
	}

}
