package gsm;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;



public class GsmFactory implements ProtocolCodecFactory{
	@Override
    public ProtocolDecoder getDecoder(IoSession session) throws Exception {//返回一个解码器
        return new GsmDecoder();
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession session) throws Exception {//返回一个编码器
        return new GsmEncoder();
    }

}
