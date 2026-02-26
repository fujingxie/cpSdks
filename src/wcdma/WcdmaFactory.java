package wcdma;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;



public class WcdmaFactory implements ProtocolCodecFactory{
	private final WcdmaEncoder encoder; 
    private final WcdmaDecoder decoder; 
        
    public WcdmaFactory() { 
        this.encoder = new WcdmaEncoder(); 
        this.decoder = new WcdmaDecoder(); 
    } 
 
	@Override
	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub
		 return decoder; 
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub
		 return encoder; 
	}

}
