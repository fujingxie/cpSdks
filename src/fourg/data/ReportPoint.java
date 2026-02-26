package fourg.data;

public class ReportPoint {
    int point ;

    byte[]  source;

    public ReportPoint(int point, byte[] source) {
        this.point = point;
        this.source = source;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public byte[] getSource() {
        return source;
    }

    public void setSource(byte[] source) {
        this.source = source;
    }
}
