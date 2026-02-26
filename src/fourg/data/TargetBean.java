package fourg.data;

/**
 * @createAuthor zfb
 * @createTime 2017/3/22${Time}
 * @describe ${}
 */

public class TargetBean {


    /**
     * imsi : 460000560736203
     * delay : 11
     * sinr : 12
     * freq : 39098
     * bbu : 1
     * rsrp : 12
     */

    private String imsi;
    private String delay;
    private String freq;
    private String BBU;
    private float rsrp;
    private float sinr;
    private String pci;
    private String current_pci;
    private boolean isSelect;
    private boolean online=true;

    public String getCurrent_pci() {
        return current_pci;
    }

    public void setCurrent_pci(String current_pci) {
        this.current_pci = current_pci;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private long time;
    private int    count;
    private int distance;

    public String getImsi() {
        if (imsi.isEmpty()){
            return "1";
        }
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getDelay() {
        return delay;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }

    public String getFreq() {
        return freq;
    }

    public void setFreq(String freq) {
        this.freq = freq;
    }

    public String getBbu() {
        return BBU;
    }

    public void setBbu(String BBU) {
        this.BBU = BBU;
    }

    public Float getRsrp() {
        return rsrp;
    }

    public void setSinr(float sinr) {
        this.sinr = sinr;
    }

    public Float getSinr() {
        return sinr;
    }

    public void setRsrp(float rsrp) {
        this.rsrp = rsrp;
    }

    public String getPci() {
        return pci;
    }

    public void setPci(String pci) {
        this.pci = pci;
    }
    private String mode;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "TargetBean{" +
                "imsi='" + imsi + '\'' +
                ", delay='" + delay + '\'' +
                ", freq='" + freq + '\'' +
                ", BBU='" + BBU + '\'' +
                ", rsrp=" + rsrp +
                ", sinr=" + sinr +
                ", pci='" + pci + '\'' +
                ", current_pci='" + current_pci + '\'' +
                ", isSelect=" + isSelect +
                ", online=" + online +
                ", time=" + time +
                ", count=" + count +
                ", distance=" + distance +
                ", mode='" + mode + '\'' +
                '}';
    }

    public int getDistance() {
        if (distance<1){
            return 1;
        }
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
