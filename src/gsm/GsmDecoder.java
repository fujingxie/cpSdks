package gsm;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import tool.CharacterConversionTool;

import java.util.HashMap;
import java.util.Map;

public class GsmDecoder extends CumulativeProtocolDecoder{
	/**
	 * 服务器接收到信息，并返回信息
	 */
	@Override
	protected boolean doDecode(IoSession arg0, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		// TODO Auto-generated method stub
//	   if (!(message instanceof IoBuffer))   
//        {   
//           return false;   
//        }   
//        IoBuffer ioBuffer = (IoBuffer) message;   
//        byte[] b = new byte [ioBuffer.limit()];   
//        ioBuffer.get(b);   
//         
//        System.out.print("-----:"+CharacterConversionTool.bytesToHexString(b));
//        out.write( "1");
//	    return true;
		//-------------------------------------------------------------------------------
		
		  // 如果没有接收完Header部分（4字节），直接返回false
		   if(in.remaining() < 4) {		   
		     return false;		     
		   } else {		   
			 GsmReciveBean bean=new GsmReciveBean();			 
			 Map<String, byte[]> map = new HashMap<>();			 
		     // 标记开始位置，如果一条消息没传输完成则返回到这个位置			 
		     in.mark();
		     byte[] bytes = new byte[8];		     
		     in.get(bytes); // 读取8字节的Header		     
//		     System.out.print("-----消息长度:"+(bytes[0] & 0xFF)+"\n");
//		     System.out.print("-----消息編號:"+bytes[5]+"\n");
//		     System.out.print("-----消息頭部分消息內容:"+CharacterConversionTool.bytesToHexString(bytes)+"\n");
		     int bodyLength = (bytes[0] & 0xFF)-8; //除去消息頭部分		     
//		     System.out.print("-----除去消息頭部分消息长度:"+bodyLength+"\n");
		     bean.setMessageNumber(bytes[5]);
		     bean.setZaibo(bytes[6]);
		     bean.setFuncationNo(bytes[7]);
		     // 如果body没有接收完整，直接返回false
		     if(in.remaining() < bodyLength) {		    	 
		       in.reset(); // IoBuffer position回到原来标记的地方		       
//		       System.out.print("-----body没有接收完整"+"\n");
		       return false;
		     } else {	    	 
		       byte[] bodyBytes = new byte[bodyLength];		       
		       in.get(bodyBytes);
//		       System.out.print("-----除去消息頭部分消息內容:"+CharacterConversionTool.bytesToHexString(bodyBytes)+"\n");
		       int bodyLenth=bodyBytes.length;		     
		       int i=0;
		       int pos=0;
		       while(bodyLenth!=0) {
		    	 int lenth=bodyBytes[pos];
		    	 byte[] data_no=new byte[2];		    
		    	 System.arraycopy(bodyBytes, pos+1, data_no, 0, 2);
		    	 byte[] data=new byte[lenth-3];		    
		    	 System.arraycopy(bodyBytes, pos+3, data, 0, lenth-3);
		    	 map.put(CharacterConversionTool.toHexString(data_no), data) ; 	 
//		    	 System.out.print(i+"  消息体长度："+lenth+"\n"
//		    	 +"消息体编号："+CharacterConversionTool.toHexString(data_no)+"\n"
//		    	 +"消息体内容："+CharacterConversionTool.bytesToHexString(data)+"\n");
		    	 bodyLenth=bodyLenth-lenth;
		    	 i++;
		    	 pos= pos+lenth;
		       }
		       bean.setBodyMap(map);
		       out.write(bean); // 解析出一条消息
		       return true;
		     }
		    
		   }
	}
}
