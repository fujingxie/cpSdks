package fourg.data;


import java.util.ArrayList;

public class CellConfig5g  {
    private Long ID;
    private ArrayList<Integer> plmnIds;
    private int tac;
    private int pci;
    private int band;
    private int ssbArfcn;
    private int syncMode;
    private int Power;
    private int rxGain;
    private int tacUpdatePeriod;
    private int REJ_CAUSE;

    public ArrayList<Integer> getPlmnIds() {
        return plmnIds;
    }

    public void setPlmnIds(ArrayList<Integer> plmnIds) {
        this.plmnIds = plmnIds;
    }

    public int getTac() {
        return tac;
    }

    public void setTac(int tac) {
        this.tac = tac;
    }

    public int getPci() {
        return pci;
    }

    public void setPci(int pci) {
        this.pci = pci;
    }

    public int getBand() {
        return band;
    }

    public void setBand(int band) {
        this.band = band;
    }

    public int getSsbArfcn() {
        return ssbArfcn;
    }

    public void setSsbArfcn(int ssbArfcn) {
        this.ssbArfcn = ssbArfcn;
    }

    public int getSyncMode() {
        return syncMode;
    }

    public void setSyncMode(int syncMode) {
        this.syncMode = syncMode;
    }

    public int getPower() {
        return Power;
    }

    public void setPower(int power) {
        Power = power;
    }

    public int getRxGain() {
        return rxGain;
    }

    public void setRxGain(int rxGain) {
        this.rxGain = rxGain;
    }

    public int getTacUpdatePeriod() {
        return tacUpdatePeriod;
    }

    public void setTacUpdatePeriod(int tacUpdatePeriod) {
        this.tacUpdatePeriod = tacUpdatePeriod;
    }

    public int getREJ_CAUSE() {
        return REJ_CAUSE;
    }

    public void setREJ_CAUSE(int REJ_CAUSE) {
        this.REJ_CAUSE = REJ_CAUSE;
    }

    public void setId(Long id){
        this.ID =id;
    }
    public Long getId(){
        return ID;
    }
    public CellConfig5g(){}




    @Override
    public String toString() {
        return "CellConfig5g{" +
                "ID=" + ID +
                ", plmnIds=" + plmnIds.size() +
                ", tac=" + tac +
                ", pci=" + pci +
                ", band=" + band +
                ", ssbArfcn=" + ssbArfcn +
                ", syncMode=" + syncMode +
                ", Power=" + Power +
                ", rxGain=" + rxGain +
                ", tacUpdatePeriod=" + tacUpdatePeriod +
                ", REJ_CAUSE=" + REJ_CAUSE +
                '}';
    }
}
