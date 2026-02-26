package power;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;

import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Test {
    // 方法解析GPGLL语句，提取经纬度和定位状态
    public static GpgllData parseGpgll(String gpgllSentence) throws IllegalArgumentException {
        // 校验GPGLL语句是否以$GPGLL开始并包含校验和 *
//        $GNGLL,,,,,,V,M*79
        System.out.println(""+("M*79".contains("*")));
        System.out.println(""+(!gpgllSentence.startsWith("$GPGLL")));
        System.out.println(""+(!gpgllSentence.contains("*")));
        if (!gpgllSentence.startsWith("$GPGLL") || !gpgllSentence.contains("*")) {
            throw new IllegalArgumentException("无效的GPGLL语句");
        }

        // 提取校验和之前的部分，并分割成字段
        String[] parts = gpgllSentence.split("\\*")[0].split(",");

        if (parts.length < 7) {
            throw new IllegalArgumentException("GPGLL语句格式不完整");
        }

        // 提取纬度
        String latitude = parts[1];
        String latitudeHemisphere = parts[2];
        // 提取经度
        String longitude = parts[3];
        String longitudeHemisphere = parts[4];
        // 定位状态
        String locationStatus = parts[6];

        // 解析纬度
        double latDegrees = Double.parseDouble(latitude.substring(0, 2));
        double latMinutes = Double.parseDouble(latitude.substring(2));
        double latDecimal = latDegrees + (latMinutes / 60);
        if (latitudeHemisphere.equals("S")) {
            latDecimal = -latDecimal;
        }

        // 解析经度
        double lonDegrees = Double.parseDouble(longitude.substring(0, 3));
        double lonMinutes = Double.parseDouble(longitude.substring(3));
        double lonDecimal = lonDegrees + (lonMinutes / 60);
        if (longitudeHemisphere.equals("W")) {
            lonDecimal = -lonDecimal;
        }

        // 返回结果
        return new GpgllData(
                Double.parseDouble(String.format("%.6f", latDecimal)),
                Double.parseDouble(String.format("%.6f", lonDecimal)),
                locationStatus.equals("A")
        );    }

    // 数据结构存储经纬度及定位状态
    public static class GpgllData {
        private final double latitude;
        private final double longitude;
        private final boolean validLocation;

        public GpgllData(double latitude, double longitude, boolean validLocation) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.validLocation = validLocation;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public boolean isValidLocation() {
            return validLocation;
        }

        @Override
        public String toString() {
            return String.format("Latitude: %.5f, Longitude: %.5f, Valid Location: %s",
                    latitude, longitude, validLocation ? "Yes" : "No");
        }
    }

    public static void main(String[] args) {
        String gpgllSentence = "$GPGLL,2232.07113,N,11356.86116,E,072053.000,A,A*49";
        try {
            GpgllData data = parseGpgll(gpgllSentence);
            longitude=data.longitude;
            latitude=data.latitude;
            System.out.println(data);
            System.out.println(latitude);
        } catch (IllegalArgumentException e) {
            System.err.println("Error parsing GPGLL sentence: " + e.getMessage());
        }

//        double dis=  calculate4GTDD(0,2600);
//        System.out.println(""+dis);
    }
    public static double longitude=0;//经度
    public static double latitude=0;//纬度
    public static double calculate5G(int P, int F) {
        double maxStrength = 90;  // 最大场强
        double G = 23;            // 增益因子
        double L = (100 - P);     // 计算损耗L
        return 1000 * Math.pow(10, (L + G - 32.5 - 20 * Math.log10(F)) / 20);
    }

    public static double calculate4GTDD(int P, int F) {
        double maxStrength = 180; // 最大场强
        double G = 35;            // 增益因子
        double L = (maxStrength - P) / 2; // 计算损耗L
        return 1000 * Math.pow(10, (L + G - 32.5 - 20 * Math.log10(F)) / 20);
    }
    public static double calculate4GFDD(int P, int F) {
        double maxStrength = 164; // 最大场强
        double G = 35;            // 增益因子
        double L = (maxStrength - P) / 2; // 计算损耗L
        return 1000 * Math.pow(10, (L + G - 32.5 - 20 * Math.log10(F)) / 20);
    }
}
