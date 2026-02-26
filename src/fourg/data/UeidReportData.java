package fourg.data;

public class UeidReportData {
    private String imsi;
    private String imei;
    private String district;
    private String phoneNum;
    private String operator;

    private boolean isHaveImei;

    public UeidReportData( ) {
    }
    public UeidReportData(String imsi, String imei,boolean isHaveImei) {
        this.imsi = imsi;
        this.imei = imei;
        this.isHaveImei=isHaveImei;

    }
    public UeidReportData(String imsi, String imei,String district,boolean isHaveImei) {
        this.imsi = imsi;
        this.imei = imei;
        this.isHaveImei=isHaveImei;
        this.district=district;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public boolean isHaveImei() {
        return isHaveImei;
    }

    public void setHaveImei(boolean haveImei) {
        isHaveImei = haveImei;
    }

    @Override
    public String toString() {
        return "UeidReportData{" +
                "imsi='" + imsi + '\'' +
                ", imei='" + imei + '\'' +
                ", district='" + district + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", operator='" + operator + '\'' +
                ", isHaveImei=" + isHaveImei +
                '}';
    }
}
