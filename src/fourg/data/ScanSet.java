package fourg.data;

import java.util.ArrayList;

public class ScanSet {
    private ArrayList<Integer> pciList;
    private ArrayList<Integer> earfchList;
    private byte rssi;
    private byte scan_result = -1;

    public ArrayList<Integer> getPciList() {
        return pciList;
    }

    public void setPciList(ArrayList<Integer> pciList) {
        this.pciList = pciList;
    }

    public ArrayList<Integer> getEarfchList() {
        return earfchList;
    }

    public void setEarfchList(ArrayList<Integer> earfchList) {
        this.earfchList = earfchList;
    }

    public byte getRssi() {
        return rssi;
    }

    public void setRssi(byte rssi) {
        this.rssi = rssi;
    }

    public byte getScan_result() {
        return scan_result;
    }

    public void setScan_result(byte scan_result) {
        this.scan_result = scan_result;
    }

    @Override
    public String toString() {
        return "ScanSet{" +
                "pciList=" + pciList +
                ", earfchList=" + earfchList +
                ", rssi=" + rssi +
                ", scan_result=" + scan_result +
                '}';
    }
}
