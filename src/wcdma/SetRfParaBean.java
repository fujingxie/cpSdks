package wcdma;

/**
 * Created by jeam on 2019/5/19.
 */

public class SetRfParaBean {
    public   String mcc ;
    public   String mnc ;
    public   String urfcn ;
    public   String cpi;
    public   String lac ;
    public   String cellid ;
    public   String fasonggonlv;
    public   String jiantingpindian;
    public   String jiantingcpi;

    public SetRfParaBean(String mcc,String mnc,String urfcn,
                         String cpi ,String lac,String cellid,
                         String fasonggonlv,String jiantingpindian,String jiantingcpi){
        this.mcc=mcc;
        this.mnc=mnc;
        this.urfcn=urfcn;
        this.cpi=cpi;
        this.lac=lac;
        this.cellid=cellid;
        this.fasonggonlv=fasonggonlv;
        this.jiantingpindian=jiantingpindian;
        this.jiantingcpi=jiantingcpi;
    }
    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getMnc() {
        return mnc;
    }

    public void setMnc(String mnc) {
        this.mnc = mnc;
    }

    public String getUrfcn() {
        return urfcn;
    }

    public void setUrfcn(String urfcn) {
        this.urfcn = urfcn;
    }

    public String getCpi() {
        return cpi;
    }

    public void setCpi(String cpi) {
        this.cpi = cpi;
    }

    public String getLac() {
        return lac;
    }

    public void setLac(String lac) {
        this.lac = lac;
    }

    public String getCellid() {
        return cellid;
    }

    public void setCellid(String cellid) {
        this.cellid = cellid;
    }

    public String getFasonggonlv() {
        return fasonggonlv;
    }

    public void setFasonggonlv(String fasonggonlv) {
        this.fasonggonlv = fasonggonlv;
    }

    public String getJiantingpindian() {
        return jiantingpindian;
    }

    public void setJiantingpindian(String jiantingpindian) {
        this.jiantingpindian = jiantingpindian;
    }

    public String getJiantingcpi() {
        return jiantingcpi;
    }

    public void setJiantingcpi(String jiantingcpi) {
        this.jiantingcpi = jiantingcpi;
    }

    @Override
    public String toString() {
        return " " + mcc + " " + mnc + " "
                + urfcn + " " + cpi + " " + lac + " " + cellid + " " + fasonggonlv + " "
                + jiantingpindian + " " + jiantingcpi;
    }
}
