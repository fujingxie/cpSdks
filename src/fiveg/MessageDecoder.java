package fiveg;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class MessageDecoder {

    private static final int HEADER_SIZE = 6;

    public Message decode(Socket clientSocket, ByteBuffer in) throws IOException {
        // 检查是否有足够的数据处理
        if (in.remaining() >= HEADER_SIZE) {
            in.mark();  // 标记当前位置

            // 读取消息头
            byte protocolNumber = in.get();
            byte signalType = in.get();
            int length = in.getInt();  // 读取消息体长度

            // 检查合法性
            if (length <= 0 || length > in.remaining()) {
                in.reset();  // 数据不足，重置
                return null;
            }

            // 读取消息体
            byte[] messageBody = new byte[length];
            in.get(messageBody);

            // 解析 Tags
            Map<Integer, Tag> tags = parseTags(ByteBuffer.wrap(messageBody));

            // 创建 Message 对象
            return new Message(protocolNumber, signalType, tags);
        }
        return null;
    }

    private Map<Integer, Tag> parseTags(ByteBuffer buffer) {
        Map<Integer, Tag> tags = new HashMap<>();

        // 动态解析 Tag 列表
        while (buffer.remaining() >= 5) {
            int tagId = buffer.get() & 0xFF;
            int tagLength = buffer.getInt();

            if (tagLength < 0 || buffer.remaining() < tagLength) {
                break;
            }

            byte[] tagContent = new byte[tagLength];
            buffer.get(tagContent);

            Tag tag = new Tag(tagId, tagLength, tagContent);
            tags.put(tagId, tag);
        }

        return tags;
    }
}

