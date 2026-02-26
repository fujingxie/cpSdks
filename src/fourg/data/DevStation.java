package fourg.data;

import org.apache.mina.core.session.IoSession;

import java.net.Socket;
import java.util.ArrayList;

public class DevStation {

    private String ip;

    private int SupportBand;

    private int TDDtype=-1;

    private boolean initConfigSuc=false;

    private boolean isConnected=false;

    private IoSession session;

    private String version;
    private int SyncState;
    private int type;
    private long currentTime;
    private int cpu_tem;

    private int cpu_use;

    private int rom_use;
    private int bbuState;
    private int tem;
    private CellConfig5g cellConfig5g;

    private Socket socket;
    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public IoSession getSession() {
        return session;
    }

    public void setSession(IoSession session) {
        this.session = session;
    }

    public CellConfig getCellConfig() {
        return cellConfig;
    }

    public void setCellConfig(CellConfig cellConfig) {
        this.cellConfig = cellConfig;
    }

    public boolean isInitConfigSuc() {
        return initConfigSuc;
    }

    public void setInitConfigSuc(boolean initConfigSuc) {
        this.initConfigSuc = initConfigSuc;
    }
    public String getBbuState() {
        switch (bbuState){
            case 0:
                return "BBU正在初始化";
            case 1:
                return "BBU搜索主控板";
            case 2:
                return "BBU配置";
            case 3:
                return "BBU待命";
            case 4:
                return "BBU正在扫频";
            case 5:
                return "BBU正在配置小区";
            case 6:
                return "BBU正在侦码";
        }
        return "BBU正在初始化";
    }
    public CellConfig5g getCellConfig5g() {
        return cellConfig5g;
    }

    public void setCellConfig5g(CellConfig5g cellConfig5g) {
        this.cellConfig5g = cellConfig5g;
    }

    public void setBbuState(int bbuState) {
        this.bbuState = bbuState;
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

    public void setTem(int tem) {
        this.tem = tem;
    }

    public int getTem() {
        return tem;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    public void setSyncState(int syncState) {
        SyncState = syncState;
    }

    public String getSyncState() {
        switch (SyncState){
            case 0:
                return "未开始同步";
            case 1:
                return "空口初始同步失败";
            case 2:
                return "空口初始同步成功";
            case 3:
                return "空口同步失败";
            case 4:
                return "空口同步成功";
            case 5:
                return "同步失败";
            case 6:
                return "GPS同步失败";
            case 7:
                return "GPS同步成功";
            case 8:
                return "GPS串口初始化失败";
            case 10:
                return "GPS锁定失败";
            case 11:
                return "GPS初始化";
            case 12:
                return "GPS锁定";
        }
        return "未开始同步";
    }
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
        String last_ip=getLastSegment(ip);
        System.out.println("last_ip:"+last_ip);
        switch (last_ip){
            case "211":{
                initConfig=new InitConfig();
                initConfig.setBandwidth(2);
                initConfig.setFrequencyOffset(0);
                initConfig.setOperatingBand(38);
                initConfig.setSynchronousMode(2);
                initConfig.setTimeDelayField(0);
                scanSet=new ScanSet();
                scanSet.setRssi((byte) 60);
                scanSet.setScan_result((byte) 0);
                ArrayList<Integer> arrayList = new ArrayList<>();
                arrayList.add(37900);
                arrayList.add(37900);
                arrayList.add(38098);
                arrayList.add(38098);
                scanSet.setEarfchList(arrayList);
                ArrayList<Integer> arrayList1 = new ArrayList<>();
                arrayList1.add(0);
                scanSet.setPciList(arrayList1);
                cellConfig = new CellConfig();
                cellConfig.setDownlink_frequency_point(37900);
                cellConfig.setCell_pci(392);
                cellConfig.setConfigmode(1);
                ArrayList<Integer> arrayList2 = new ArrayList<>();
                arrayList2.add(Integer.parseInt("46000f",16));
                arrayList2.add(Integer.parseInt("46015f",16));
                cellConfig.setPlmn(arrayList2);
                cellConfig.setTac_cycle(10);
                cellConfig.setTac(9880);
                cellConfig.setPciList(arrayList1);
                cellConfig.setPilot_frequency_list(arrayList1);
                cellConfig.setUplink_frequency_point(37900);
                cellConfig.setTransmitted_power(30);
                cellConfig.setMeasure(0);
                setInitConfig(initConfig);
                setCellConfig(cellConfig);
                setScanSet(scanSet);
            }
                break;
            case "212":{
                initConfig=new InitConfig();
                initConfig.setBandwidth(2);
                initConfig.setFrequencyOffset(0);
                initConfig.setOperatingBand(39);
                initConfig.setSynchronousMode(2);
                initConfig.setTimeDelayField(0);
                scanSet = new ScanSet();
                scanSet.setRssi((byte) 60);
                scanSet.setScan_result((byte) 0);
                ArrayList<Integer> arrayList = new ArrayList<>();
                arrayList.add(38400);
                arrayList.add(38400);
                scanSet.setEarfchList(arrayList);
                ArrayList<Integer> arrayList1 = new ArrayList<>();
                arrayList1.add(0);
                scanSet.setPciList(arrayList1);
                cellConfig = new CellConfig();
                cellConfig.setDownlink_frequency_point(38400);
                cellConfig.setCell_pci(392);
                ArrayList<Integer> arrayList2 = new ArrayList<>();
                arrayList2.add(Integer.parseInt("46000f", 16));
                arrayList2.add(Integer.parseInt("46015f",16));
                cellConfig.setPlmn(arrayList2);
                cellConfig.setTac_cycle(10);
                cellConfig.setTac(9880);
                cellConfig.setConfigmode(1);
                cellConfig.setPciList(arrayList1);
                cellConfig.setPilot_frequency_list(arrayList1);
                cellConfig.setUplink_frequency_point(38400);
                cellConfig.setTransmitted_power(30);
                cellConfig.setMeasure(0);
                setInitConfig(initConfig);
                setCellConfig(cellConfig);
                setScanSet(scanSet);
            }
                break;
            case "213":{
                initConfig=new InitConfig();
                initConfig.setBandwidth(2);
                initConfig.setFrequencyOffset(0);
                initConfig.setOperatingBand(40);
                initConfig.setSynchronousMode(2);
                initConfig.setTimeDelayField(0);
                scanSet = new ScanSet();
                scanSet.setRssi((byte) 60);
                scanSet.setScan_result((byte) 0);
                ArrayList<Integer> arrayList = new ArrayList<>();
                arrayList.add(38950);
                arrayList.add(38950);
                arrayList.add(39148);
                arrayList.add(39148);
                scanSet.setEarfchList(arrayList);
                ArrayList<Integer> arrayList1 = new ArrayList<>();
                arrayList1.add(0);
                scanSet.setPciList(arrayList1);
                cellConfig = new CellConfig();
                cellConfig.setDownlink_frequency_point(38950);
                cellConfig.setCell_pci(392);
                ArrayList<Integer> arrayList2 = new ArrayList<>();
                arrayList2.add(Integer.parseInt("46000f", 16));
                arrayList2.add(Integer.parseInt("46015f",16));
                cellConfig.setPlmn(arrayList2);
                cellConfig.setTac_cycle(10);
                cellConfig.setTac(9880);
                cellConfig.setConfigmode(1);
                cellConfig.setPciList(arrayList1);
                cellConfig.setPilot_frequency_list(arrayList1);
                cellConfig.setUplink_frequency_point(38950);
                cellConfig.setTransmitted_power(30);
                cellConfig.setMeasure(0);
                setInitConfig(initConfig);
                setCellConfig(cellConfig);
                setScanSet(scanSet);
            }
                break;
            case "216":{
                initConfig=new InitConfig();
                initConfig.setBandwidth(2);
                initConfig.setFrequencyOffset(0);
                initConfig.setOperatingBand(3);
                initConfig.setSynchronousMode(2);
                initConfig.setTimeDelayField(0);
                scanSet = new ScanSet();
                scanSet.setRssi((byte) 60);
                scanSet.setScan_result((byte) 0);
                ArrayList<Integer> arrayList = new ArrayList<>();
                arrayList.add(1450);
                arrayList.add(1450);
                scanSet.setEarfchList(arrayList);
                ArrayList<Integer> arrayList1 = new ArrayList<>();
                arrayList1.add(0);
                scanSet.setPciList(arrayList1);
                cellConfig = new CellConfig();
                cellConfig.setDownlink_frequency_point(1850);
                cellConfig.setCell_pci(392);
                ArrayList<Integer> arrayList2 = new ArrayList<>();
                arrayList2.add(Integer.parseInt("46011f", 16));
                arrayList2.add(Integer.parseInt("46000f", 16));
                arrayList2.add(Integer.parseInt("46001f", 16));
                cellConfig.setPlmn(arrayList2);
                cellConfig.setTac_cycle(10);
                cellConfig.setTac(9880);
                cellConfig.setConfigmode(1);
                cellConfig.setPciList(arrayList1);
                cellConfig.setPilot_frequency_list(arrayList1);
                cellConfig.setUplink_frequency_point(19850);
                cellConfig.setTransmitted_power(30);
                cellConfig.setMeasure(0);
                setInitConfig(initConfig);
                setCellConfig(cellConfig);
                setScanSet(scanSet);
            }
                break;
            case "217":{
                initConfig=new InitConfig();
                initConfig.setBandwidth(2);
                initConfig.setFrequencyOffset(0);
                initConfig.setOperatingBand(40);
                initConfig.setSynchronousMode(2);
                initConfig.setTimeDelayField(3);
                scanSet = new ScanSet();
                scanSet.setRssi((byte) 60);
                scanSet.setScan_result((byte) 0);
                ArrayList<Integer> arrayList = new ArrayList<>();
                arrayList.add(1450);
                arrayList.add(1450);
                scanSet.setEarfchList(arrayList);
                ArrayList<Integer> arrayList1 = new ArrayList<>();
                arrayList1.add(0);
                scanSet.setPciList(arrayList1);
                cellConfig = new CellConfig();
                cellConfig.setDownlink_frequency_point(1650);
                cellConfig.setCell_pci(392);
                ArrayList<Integer> arrayList2 = new ArrayList<>();
                arrayList2.add(Integer.parseInt("46001f", 16));
                arrayList2.add(Integer.parseInt("46011f", 16));
                cellConfig.setPlmn(arrayList2);
                cellConfig.setTac_cycle(10);
                cellConfig.setTac(9880);
                cellConfig.setConfigmode(1);
                cellConfig.setPciList(arrayList1);
                cellConfig.setPilot_frequency_list(arrayList1);
                cellConfig.setUplink_frequency_point(19650);
                cellConfig.setTransmitted_power(30);
                cellConfig.setMeasure(0);
                setInitConfig(initConfig);
                setCellConfig(cellConfig);
                setScanSet(scanSet);
            }
                break;
        }
    }

    public int getSupportBand() {
        return SupportBand;
    }

    public void setSupportBand(int supportBand) {
        SupportBand = supportBand;
    }

    public int getTDDtype() {
        return TDDtype;
    }

    public void setTDDtype(int TDDtype) {
        this.TDDtype = TDDtype;
    }

    private InitConfig initConfig;

    private ScanSet scanSet;

    private CellConfig cellConfig;

    public ScanSet getScanSet() {
        return scanSet;
    }

    public void setScanSet(ScanSet scanSet) {
        this.scanSet = scanSet;
    }

    public InitConfig getInitConfig() {
        return initConfig;
    }

    public void setInitConfig(InitConfig initConfig) {
        this.initConfig = initConfig;
    }


    // 提取最后一段的方法
    public  String getLastSegment(String ip) {
        // 按 "." 分割字符串
        String[] segments = ip.split("\\.");
        // 返回最后一个分段
        return segments[segments.length - 1].substring(0,3);
    }

    @Override
    public String toString() {
        return "DevStation{" +
                "ip='" + ip + '\'' +
                ", SupportBand=" + SupportBand +
                ", TDDtype=" + TDDtype +
                ", initConfigSuc=" + initConfigSuc +
                ", initConfig=" + initConfig +
                ", scanSet=" + scanSet+
                ", cellConfig=" + cellConfig +
                '}';
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setSocket(Socket socket) {
        this.socket=socket;
    }

    public Socket getSocket() {
        return socket;
    }
}
