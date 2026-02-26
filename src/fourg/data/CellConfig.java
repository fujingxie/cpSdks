package fourg.data;

import java.util.ArrayList;

public class CellConfig {
    private int downlink_frequency_point;//下行频点

    private int cell_pci;//小区PCI

    private ArrayList<Integer> plmn;//plmn列表

    private int tac;//tac

    private ArrayList<Integer> pciList;//pci 列表

    private int tac_cycle = -1;//tac更新周期

    private ArrayList<Integer> pilot_frequency_list;//异频频点列表

    private int uplink_frequency_point = -1;//上行频点

    private int transmitted_power = -1;//发射功率

    private int measure =-1;//是否启用测量

    private boolean cellUpReady;

    private int configmode;

    public int getDownlink_frequency_point() {
        return downlink_frequency_point;
    }

    public void setDownlink_frequency_point(int downlink_frequency_point) {
        this.downlink_frequency_point = downlink_frequency_point;
    }

    public int getCell_pci() {
        return cell_pci;
    }

    public void setCell_pci(int cell_pci) {
        this.cell_pci = cell_pci;
    }

    public ArrayList<Integer> getPlmn() {
        return plmn;
    }

    public void setPlmn(ArrayList<Integer> plmn) {
        this.plmn = plmn;
    }

    public int getTac() {
        return tac;
    }

    public void setTac(int tac) {
        this.tac = tac;
    }

    public ArrayList<Integer> getPciList() {
        return pciList;
    }

    public void setPciList(ArrayList<Integer> pciList) {
        this.pciList = pciList;
    }

    public int getTac_cycle() {
        return tac_cycle;
    }

    public void setTac_cycle(int tac_cycle) {
        this.tac_cycle = tac_cycle;
    }

    public ArrayList<Integer> getPilot_frequency_list() {
        return pilot_frequency_list;
    }

    public void setPilot_frequency_list(ArrayList<Integer> pilot_frequency_list) {
        this.pilot_frequency_list = pilot_frequency_list;
    }

    public int getUplink_frequency_point() {
        return uplink_frequency_point;
    }

    public void setUplink_frequency_point(int uplink_frequency_point) {
        this.uplink_frequency_point = uplink_frequency_point;
    }

    public int getTransmitted_power() {
        return transmitted_power;
    }

    public void setTransmitted_power(int transmitted_power) {
        this.transmitted_power = transmitted_power;
    }

    public int getMeasure() {
        return measure;
    }

    public void setMeasure(int measure) {
        this.measure = measure;
    }

    public boolean isCellUpReady() {
        return cellUpReady;
    }

    public void setCellUpReady(boolean cellUpReady) {
        this.cellUpReady = cellUpReady;
    }

    public int getConfigmode() {
        return configmode;
    }

    public void setConfigmode(int configmode) {
        this.configmode = configmode;
    }

    @Override
    public String toString() {
        return "CellConfig{" +
                "downlink_frequency_point=" + downlink_frequency_point +
                ", cell_pci=" + cell_pci +
                ", plmn=" + plmn +
                ", tac=" + tac +
                ", pciList=" + pciList +
                ", tac_cycle=" + tac_cycle +
                ", pilot_frequency_list=" + pilot_frequency_list +
                ", uplink_frequency_point=" + uplink_frequency_point +
                ", transmitted_power=" + transmitted_power +
                ", measure=" + measure +
                ", cellUpReady=" + cellUpReady +
                ", configmode=" + configmode +
                '}';
    }
}
