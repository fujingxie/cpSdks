package wcdma;

/**
 * 消息号 indi消息名 后续内容长度 围栏名 板卡硬件ID 返回具体Indi内容
 * Created by jeam on 2019/5/18.
 */

public class ReporBean {

    /**
     * 消息号
     */
    private String indiNum;

    /**
     *  indi消息名
     */
    private String indiName;

    /**
     * 后续内容长度
     */
    private int contentLenth;
    /**
     * 围栏名
     */
    private String weiLanName;
    /**
     * 板卡硬件ID
     */
    private String banKaIDl;
    /**
     * 返回具体Indi内容
     */
    private String content;

    public String getIndiNum() {
        return indiNum;
    }

    public void setIndiNum(String indiNum) {
        this.indiNum = indiNum;
    }

    public String getIndiName() {
        return indiName;
    }

    public void setIndiName(String indiName) {
        this.indiName = indiName;
    }

    public int getContentLenth() {
        return contentLenth;
    }

    public void setContentLenth(int contentLenth) {
        this.contentLenth = contentLenth;
    }

    public String getWeiLanName() {
        return weiLanName;
    }

    public void setWeiLanName(String weiLanName) {
        this.weiLanName = weiLanName;
    }

    public String getBanKaIDl() {
        return banKaIDl;
    }

    public void setBanKaIDl(String banKaIDl) {
        this.banKaIDl = banKaIDl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
