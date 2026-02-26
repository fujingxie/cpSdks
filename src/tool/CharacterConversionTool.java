package tool;


import org.apache.mina.core.buffer.IoBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class CharacterConversionTool {
    public static void main(String[] args) {
        byte[] data=new byte[]{0x1c,0x01,0x00,0x00};
        int a=bytes2int(data);
        System.out.println("+"+a);
    }
    public static String bytesToAsciiString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            // 将byte转换为无符号整数，确保ASCII字符在0-127范围内
            int unsignedByte = b & 0xFF;
            // 将无符号整数转换为字符，并添加到StringBuilder中
            sb.append((char) unsignedByte);
        }
        return sb.toString();
    }
    public static int  bytes2Int(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put(bytes);

        int result = buffer.getInt(0); // ����� int ���� getInteger  ����� short ����gtShort
        return result;
    }
    public static ArrayList<Integer> byteArrayToIntegerList(byte[] byteArray, int pos) {
        ArrayList<Integer> integerList = new ArrayList<>();

        if (byteArray.length>=4*pos){
            for (int i = 0; i < pos; i+=4) {
                byte[] plmn = Arrays.copyOfRange(byteArray, i, 4+i);
                int value=bytes2Int(plmn);
                integerList.add(value);
            }
        }
        return integerList;
    }
    public static short bytes2ShortBIG_ENDIAN(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put(bytes);

        short result = buffer.getShort(0);
        return result;
    }

    public static byte[] little_intToByte(int i, int len) {
        byte[] abyte = new byte[len];
        if (len == 1) {
            abyte[0] = (byte) (0xff & i);
        } else if (len == 2) {
            abyte[0] = (byte) (0xff & i);
            abyte[1] = (byte) ((0xff00 & i) >> 8);
        } else {
            abyte[0] = (byte) (0xff & i);
            abyte[1] = (byte) ((0xff00 & i) >> 8);
            abyte[2] = (byte) ((0xff0000 & i) >> 16);
            abyte[3] = (byte) ((0xff000000 & i) >> 24);
        }
        return abyte;
    }
    /**
     * ��ָ���ַ���src����ÿ�����ַ��ָ�ת��Ϊ16������ʽ
     * �磺"2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF, 0xD9}
     *
     * @param src String
     * @return byte[]
     **/
    public static byte[] HexString2Bytes(String src, int length) {
        byte[] ret = new byte[length];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < length; i++) {
            if (tmp.length > i) {
                ret[i] = tmp[i];
            } else {
                ret[i] = (byte) 0;
            }
        }
        return ret;
    }
    public static int little_bytesToInt(byte[] bytes) {
        int addr = 0;
        if (bytes.length == 1) {
            addr = bytes[0] & 0xFF;
        } else if (bytes.length == 2) {
            addr = bytes[0] & 0xFF;
            addr |= (((int) bytes[1] << 8) & 0xFF00);
        } else {
            addr = bytes[0] & 0xFF;
            addr |= (((int) bytes[1] << 8) & 0xFF00);
            addr |= (((int) bytes[2] << 16) & 0xFF0000);
            addr |= (((int) bytes[3] << 24) & 0xFF000000);
        }
        return addr;
    }
    /**
     * bytes�ַ���ת��ΪByteֵ
     *
     * @param  src Byte�ַ�����ÿ��Byte֮��û�зָ���
     * @return byte[]
     */
    public static byte[] hexStr2Bytes(String src) {
        char c;
        StringBuilder str = new StringBuilder();
        int intAsc;
        String strHex;
        for (int i = 0; i < src.length(); i++) {
            c = src.charAt(i);
            intAsc = (int) c;
            strHex = Integer.toHexString(intAsc);
            str.append(strHex);
        }
        int m = 0, n = 0;
        int l = str.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            m = i * 2 + 1;
            n = m + 1;
            ret[i] = Integer.decode("0x" + str.substring(i * 2, m) + str.substring(m, n)).byteValue();
        }
        return ret;
    }
    /**
     * byte������ȡint��ֵ��������������(��λ�ں󣬸�λ��ǰ)��˳�򡣺�intToBytes2��������ʹ��
     */
    public static int bytesToInt2(byte[] src, int offset) {
        int value;
        value = (int) ( ((src[offset] & 0xFF)<<24)
                |((src[offset+1] & 0xFF)<<16)
                |((src[offset+2] & 0xFF)<<8)
                |(src[offset+3] & 0xFF));
        return value;
    }
    /**
     * byte������ȡint��ֵ��������������(��λ�ں󣬸�λ��ǰ)��˳�򡣺�intToBytes2��������ʹ��
     */
    public static int bytesToInt4(byte[] src, int offset) {
        if(src.length >= 2){
            int value;
            value = (int) (((src[offset] & 0xFF)<<8)
                    |(src[offset+1] & 0xFF));
            return value;
        }
        return 0;
    }

    public static short getShortByLow(byte[] b, int offset) {
        short n = 0;
        int len = b.length;
        if (len >= offset + 2) {
            int byte0 = b[offset] & 0xff;
            int byte1 = b[offset + 1] & 0xff;
            n = (short) (byte0 | byte1 << 8);
        }
        return n;
    }
    /**
     * ʮ������ת���ַ���
     *
     * @return String ��Ӧ���ַ���
     */
    public static String hexStr2Str(String hexStr) {

        byte[] baKeyword = new byte[hexStr.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(hexStr.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            hexStr = new String(baKeyword, "utf-8");//UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return hexStr;
    }

    public static int byteToShort(byte[] shortByte) {
        int fromByte = 0;
        for (int i = 0; i < 2; i++) {
            int n = (shortByte[i] < 0 ? (short) shortByte[i] + 256 : (short) shortByte[i]) << (8 * i);
            fromByte += n;
        }
        return fromByte;
    }

	/**
	 * �ж��ַ�����Ϊ��
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
	 
		return str == null && "".equals(str);
	}
    
   
    /** 
    * 16���Ƶ��ַ�����ʾת���ֽ����� 
    * 
    * @param hexString 
    * 16���Ƹ�ʽ���ַ��� 
    * @return ת������ֽ����� 
    **/

    public static byte[] toByteArray(String hexString) {
        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() / 2];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) {// ��Ϊ��16���ƣ����ֻ��ռ��4λ��ת�����ֽ���Ҫ����16���Ƶ��ַ�����λ����
            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;
        }
        return byteArray;
    }
    
    /** 
    * �ֽ�����ת��16���Ʊ�ʾ��ʽ���ַ��� 
    * 
    * @param byteArray 
    * ��Ҫת�����ֽ����� 
    * @return 16���Ʊ�ʾ��ʽ���ַ��� 
    **/

     public static String toHexString(byte[] byteArray) {
        String str = null;
        if (byteArray != null && byteArray.length > 0) {
            StringBuffer stringBuffer = new StringBuffer(byteArray.length);
            for (byte byteChar : byteArray) {
                stringBuffer.append(String.format("%02X", byteChar));
            }
            str = stringBuffer.toString();
        }
        return str;
     }
     
     /**
      * ascll��תΪ16����
      * @param asciiStr
      * @return
      */
     public static String asciiToHex(String asciiStr) {
         char[] chars = asciiStr.toCharArray();
         StringBuilder hex = new StringBuilder();
         for (char ch : chars) {
             hex.append(Integer.toHexString((int) ch));
         }
         return hex.toString();
     }
     
     /**
     * 16�����ַ���תbyte[]
     * @param str
     * @return
     */
    public static byte[] toBytes(String str) {
        if(str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for(int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }

    /**
     * byte[]����ת�ַ���
     * @param bArray
     * @return
     */
    public static String bytesToHexString(byte[] bArray) {
        if (bArray == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(bArray.length);
        String sTemp;
        for (byte aBArray : bArray) {
            sTemp = Integer.toHexString(0xFF & aBArray);
            sb.append("0x");
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toLowerCase());
            sb.append(",");
        }
        return sb.toString();
    }

    /**
     * byte[]s����ת��16�����ַ��� û��0x����
     * @param bArray
     * @return
     */
    public static String bytesToHexString1(byte[] bArray) {
        if (bArray == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(bArray.length);
        String sTemp;
        for (byte aBArray : bArray) {
            sTemp = Integer.toHexString(0xFF & aBArray);

            sb.append(sTemp.toLowerCase());

        }
        return sb.toString();
    }

    private static String StrToHex(String asciiStr) {

        StringBuilder hex = new StringBuilder();
        if(asciiStr.length()==2) {
            hex.append(asciiStr);
        }else if(asciiStr.length()==1) {
            hex.append("0");
            hex.append(asciiStr);
        }

        return hex.toString();
    }

    public static byte[] shortTobytes(short value){
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort(value);
        return buffer.array();
    }
    public static short bytes2Short(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put(bytes);

        short result = buffer.getShort(0); // ����� int ���� getInteger  ����� short ����gtShort
        return result;
    }

    /**
     * 16����תascll
     * @param hex
     * @return
     */
    public static String convertHexToString(String hex){

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for( int i=0; i<hex.length()-1; i+=2 ){

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char)decimal);

            temp.append(decimal);
        }

        return sb.toString();
    }
    
    public static int bytes2int(byte[] l) {
        if(l.length == 4){
            return (l[2]& 0xFF)*256+(l[3]& 0xFF);
        }
        return 0;
    }

    public static byte[] getMd5OfFile(String filePath){
        String path = filePath;
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(Files.readAllBytes(Paths.get(path)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        byte[] digest = md.digest();
        return digest;
    }

    public static byte[] intToBytes2(int value)
    {
        byte[] src = new byte[4];
        src[0] = (byte) ((value>>24) & 0xFF);
        src[1] = (byte) ((value>>16)& 0xFF);
        src[2] = (byte) ((value>>8)&0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }
    /**
     * ��IoBufferת����string
     *
     * @param butBuffer
     */
    public static String ioBufferToString(Object message) {
        if (!(message instanceof IoBuffer)) {
            return "";
        }
        IoBuffer ioBuffer = (IoBuffer) message;
        byte[] b = new byte[ioBuffer.limit()];
        ioBuffer.get(b);
        StringBuffer stringBuffer = new StringBuffer();

        for (int i = 0; i < b.length; i++) {

            stringBuffer.append((char) b[i]);
        }
        return stringBuffer.toString();
    }
    // 根据IMSI前5位判断运营商
    public static String getOperatorByIMSI(String imsi) {
        if (imsi == null || imsi.length() < 5) {
            return "未知运营商";
        }

        // 获取IMSI的前5位
        String imsiPrefix = imsi.substring(0, 5);

        switch (imsiPrefix) {
            // 中国移动
            case "46000":
            case "46002":
            case "46007":
            case "46008":
                return "中国移动";

            // 中国联通
            case "46001":
            case "46006":
            case "46009":
                return "中国联通";

            // 中国电信
            case "46003":
            case "46005":
            case "46011":
                return "中国电信";

            // 其他未知的前缀
            default:
                return "未知运营商";
        }
    }
}
