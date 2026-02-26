package fourg.data;

public class SyncStatus {

    private int typeCode;

    private int status;

    private short point;

    private short pci;

    private short rssi;

    private short tac;

    public int getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(int typeCode) {
        this.typeCode = typeCode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public short getPoint() {
        return point;
    }

    public void setPoint(short point) {
        this.point = point;
    }

    public short getPci() {
        return pci;
    }

    public void setPci(short pci) {
        this.pci = pci;
    }

    public short getRssi() {
        return rssi;
    }

    public void setRssi(short rssi) {
        this.rssi = rssi;
    }

    public short getTac() {
        return tac;
    }

    public void setTac(short tac) {
        this.tac = tac;
    }
}
