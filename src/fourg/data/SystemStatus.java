package fourg.data;

public class SystemStatus {
    private int soft_state;

    private int cpu_tem;

    private int cpu_use;

    private int rom_use;
    private int tem;

    public int getTem() {
        return tem;
    }

    public void setTem(int tem) {
        this.tem = tem;
    }

    public int getSoft_state() {
        return soft_state;
    }

    public void setSoft_state(int soft_state) {
        this.soft_state = soft_state;
    }

    public int getCpu_tem() {
        return cpu_tem;
    }

    public void setCpu_tem(int cpu_tem) {
        this.cpu_tem = cpu_tem;
    }

    public int getCpu_use() {
        return cpu_use;
    }

    public void setCpu_use(int cpu_use) {
        this.cpu_use = cpu_use;
    }

    public int getRom_use() {
        return rom_use;
    }

    public void setRom_use(int rom_use) {
        this.rom_use = rom_use;
    }

    @Override
    public String toString() {
        return "SystemStatus{" +
                "soft_state=" + soft_state +
                ", cpu_tem=" + cpu_tem +
                ", cpu_use=" + cpu_use +
                ", rom_use=" + rom_use +
                ", tem=" + tem +
                '}';
    }
}
