package fourg.data;

public class DeviceStartupNotification {
    private int SupportBand;
    private int TDDtype;

    public DeviceStartupNotification(int supportBand, int TDDtype) {
        SupportBand = supportBand;
        this.TDDtype = TDDtype;
    }

    public int getSupportBand() {
        return SupportBand;
    }

    public void setSupportBand(int supportBand) {
        SupportBand = supportBand;
    }

    public int getTDDtype() {
        return TDDtype;
    }

    public void setTDDtype(int TDDtype) {
        this.TDDtype = TDDtype;
    }
}
