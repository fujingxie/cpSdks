package power;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;


public class CharsetDecoder extends CumulativeProtocolDecoder {
    public static String bytesToHexString(byte[] bArray) {
        if (bArray == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(bArray.length);
        String sTemp;
        for (byte aBArray : bArray) {
            sTemp = Integer.toHexString(0xFF & aBArray);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
            sb.append(",");
        }
        return sb.toString();
    }
    private static String TAG = "CharsetDecoder";
    private static final byte FRAME_START = 0x7E;
    private static final byte[] FRAME_END = new byte[]{0x0D, 0x0A};

    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {

        // 如果有足够的数据至少读取帧头和长度
        if (in.remaining() > 2) {
            in.mark(); // 标记位置

            System.out.println("11");
            // 读取帧头
            if (in.get() != FRAME_START) {
                return false; // 帧头不匹配，不处理
            }
            System.out.println("22");
            // 读取长度字节
            byte length = in.get();

            // 检查是否有足够的数据，包括帧头和帧尾
            if (in.remaining() < (length - 2)) { // 总长度减去已经读取的帧头和长度字节
                in.reset(); // 没有足够的数据，重置位置
                return false; // 返回false以累积更多数据
            }
            System.out.println("33:"+length);
            // 读取预期的数据加上帧尾
            byte[] data = new byte[length - 4]; // 减去帧头和长度字节
            in.get(data, 0, length - 4);
            System.out.println("44:"+bytesToHexString(data));
            // 预期帧尾
            byte frameEnd1 = in.get();
            byte frameEnd2 = in.get();
            System.out.println("55:"+frameEnd1);
            System.out.println("66:"+frameEnd2);
            // 检查帧尾
            if (frameEnd1 == FRAME_END[0] && frameEnd2 == FRAME_END[1]) {
                // 校验数据，如果校验失败可以抛出异常或者返回false
                if (!verifyCRC(data)) {
                    // 校验失败的处理
                    return false;
                }
                System.out.println("77");
                // 提取读操作和内容数据
                byte readOperation = data[0]; // 读操作
                byte[] content = new byte[data.length - 2]; // 内容数据长度 = 总数据长度 - 读操作(1) - 校验字节(1) - 帧尾长度(1)
                System.arraycopy(data, 1, content, 0, content.length);

                // 组合读操作和内容数据后输出
                IoBuffer resultBuffer = IoBuffer.allocate(1 + content.length);
                resultBuffer.put(readOperation);
                resultBuffer.put(content);
                resultBuffer.flip(); // 准备读取

                out.write(resultBuffer.array());

                return true; // 成功处理消息
            } else {
                // 数据不正确，可能是因为未正确找到帧尾
                in.reset(); // 重置读取位置
                return false; // 请求更多数据
            }
        }

        // 数据不足，等待更多数据
        return false;
    }

    private boolean verifyCRC(byte[] data) {
        // 实现CRC校验逻辑，这里应该校验从读操作到校验字节前的所有数据
        // ...
        return true; // 假设校验总是成功
    }
}
