package powermanagementboard;

public class GpgllParser {
    // 方法解析GPGLL语句，提取经纬度和定位状态
    public static GpgllData parseGpgll(String gpgllSentence) throws IllegalArgumentException {
        // 校验GPGLL语句是否以$GPGLL开始并包含校验和
        if (!gpgllSentence.startsWith("$GNGLL") || !gpgllSentence.contains("*")) {
            throw new IllegalArgumentException("无效的GNGLL语句");
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

        if (latitude.isEmpty()||longitude.isEmpty()){
            return null;
        }
        if (latitude.isEmpty()){
            latitude="000";
        }
        if (latitudeHemisphere.isEmpty()){
            latitudeHemisphere="S";
        }
        if (longitude.isEmpty()){
            longitude="000";
        }
        if (longitudeHemisphere.isEmpty()){
            longitudeHemisphere="W";
        }
        if (locationStatus.isEmpty()){
            locationStatus="N";
        }
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
        );
    }

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

}
