package wcdma;

public class DevStaus {
    public static final int KEY_disConnect = 1;
    public static final int KEY_upDateDb= 2;
    int stausCode;
     public DevStaus(int stausCode){
         this.stausCode=stausCode;
     }

    public int getStausCode() {
        return stausCode;
    }

    public void setStausCode(int stausCode) {
        this.stausCode = stausCode;
    }
}
