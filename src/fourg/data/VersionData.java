package fourg.data;

public class VersionData {
    //软件版本号
    private String softVersion;
    //物理层版本号
    private String physicalVersion;
    //内核版本号
    private String kernelVersion;
    //硬件版本号
    private String hardwareVersion;

    public String getSoftVersion() {
        return softVersion;
    }

    public void setSoftVersion(String softVersion) {
        this.softVersion = softVersion;
    }

    public String getPhysicalVersion() {
        return physicalVersion;
    }

    public void setPhysicalVersion(String physicalVersion) {
        this.physicalVersion = physicalVersion;
    }

    public String getKernelVersion() {
        return kernelVersion;
    }

    public void setKernelVersion(String kernelVersion) {
        this.kernelVersion = kernelVersion;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }
}
