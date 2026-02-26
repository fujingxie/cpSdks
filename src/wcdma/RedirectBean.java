package wcdma;

public class RedirectBean {
    String socket;
    String ncc;
    String bcc;
    String arfcn;

    public RedirectBean(String socket, String ncc,String bcc,String arfcn){
        this.socket=socket;
        this.ncc=ncc;
        this.bcc=bcc;
        this.arfcn=arfcn;
    }

    public String getSocket() {
        return socket;
    }

    public void setSocket(String socket) {
        this.socket = socket;
    }

    public String getNcc() {
        return ncc;
    }

    public void setNcc(String ncc) {
        this.ncc = ncc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getArfcn() {
        return arfcn;
    }

    public void setArfcn(String arfcn) {
        this.arfcn = arfcn;
    }

    @Override
    public String toString() {
        return " " + socket + " " + ncc + " "
                + bcc + " " + arfcn;
    }
}
