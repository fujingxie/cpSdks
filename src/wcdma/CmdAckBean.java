package wcdma;

/**
 * Created by jeam on 2019/5/18.
 */

public class CmdAckBean {
    /**
     * 消息号
     */
    private int cmdNum;

    /**
     *  主控发送指令名
     */
    private String cmdName;

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

    public int getIndiNum() {
        return cmdNum;
    }

    public void setIndiNum(int cmdNum) {
        this.cmdNum = cmdNum;
    }

    public String getIndiName() {
        return cmdName;
    }

    public void setIndiName(String cmdName) {
        this.cmdName = cmdName;
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
