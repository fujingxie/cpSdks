package fourg.data;

public class FieldStrengthData {

    private long currentTimeMillis;
    private String imsi;
    private int fieldstrength;

    public FieldStrengthData() {
    }

    public long getCurrentTimeMillis() {
        return currentTimeMillis;
    }

    public String getImsi() {
        return imsi;
    }

    public int getFieldstrength() {
        return fieldstrength;
    }

    public void setTime(long currentTimeMillis) {
        this.currentTimeMillis = currentTimeMillis;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public void setFieldstrength(int fieldstrength) {
        this.fieldstrength = fieldstrength;
    }

    @Override
    public String toString() {
        return "FieldStrengthData{" +
                "currentTimeMillis=" + currentTimeMillis +
                ", imsi='" + imsi + '\'' +
                ", fieldstrength=" + fieldstrength +
                '}';
    }
}
