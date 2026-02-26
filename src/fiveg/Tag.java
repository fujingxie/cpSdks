package fiveg;

import java.nio.ByteBuffer;

import java.nio.ByteBuffer;

public class Tag {
    private int tagId;
    private int tagLength;
    private byte[] content;

    public Tag(int tagId, int tagLength, byte[] content) {
        this.tagId = tagId;
        this.tagLength = tagLength;
        this.content = content;
    }

    // 将 Tag 的内容解析为整数 (uint32)
    public int getContentAsInt() {
        if (content.length == 4) {
            return ByteBuffer.wrap(content).getInt();
        }
        return 0;
    }
    public int getTotalLength() {
        return 5 + content.length;
    }
    // 将 Tag 的内容解析为短整型 (uint16)
    public int getContentAsShort() {
        if (content.length == 2) {
            return ByteBuffer.wrap(content).getShort() & 0xFFFF;  // 转换为无符号
        }
        return 0;
    }
    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(5 + content.length);  // Tag ID (1 byte) + Length (4 bytes) + Content
        buffer.put(tagId>70?9:(byte) tagId);
        buffer.putInt(tagLength);
        buffer.put(content);
        return buffer.array();
    }
    // 将 Tag 的内容解析为字节 (uint8)
    public int getContentAsByte() {
        if (content.length == 1) {
            return content[0] & 0xFF;  // 转换为无符号
        }
        return 0;
    }

    // 将 Tag 的内容解析为字符串
    public String getContentAsString() {
        return new String(content);
    }

    // Getter 和 Setter
    public int getTagId() {
        return tagId;
    }

    public int getTagLength() {
        return tagLength;
    }

    public byte[] getContent() {
        return content;
    }
}

