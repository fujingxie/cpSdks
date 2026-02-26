package fourg;

import tool.CharacterConversionTool;
import tool.Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FourGData {
    /**
     * 设置pci
     * @param cell_pci 小区PCI
     * @param seqNo seqNo
     */
    public static byte[] setPci(
            int cell_pci,
            int seqNo
    ){
        byte[] cmd=null;
        byte[] cellPciTag = new byte[]{0x09,0x00,0x02, (byte) ((cell_pci >> 8) & 0xFF),(byte) (cell_pci & 0xFF)};
        int length = 0;
        length += cellPciTag.length;
        cmd = new byte[length];
        System.arraycopy(cellPciTag, 0, cmd, 0, cellPciTag.length);
        byte[] data = cmd;
        byte[] headData = new byte[]{0x01, 0x11, 0x00, (byte) (data.length + 7), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
        byte[] sendData = new byte[data.length + headData.length];
        System.arraycopy(headData, 0, sendData, 0, headData.length);
        System.arraycopy(data, 0, sendData, 0 + headData.length, data.length);
        return sendData;
    }
    /**
     * 小区配置
     * @param pciList pci列表
     * @param plmn PLMN list
     * @param pilot_frequency_list 异频频点
     * @param downlink_frequency_point 下行频点
     * @param cell_pci pci
     * @param tac TAC
     * @param uplink_frequency_point 上行频点
     * @param measure 是否启用测量
     * @param transmitted_power
     * @param seqNo seqNo
     */
    public static byte[] CellConfig(ArrayList<Integer> pciList,
                                  ArrayList<Integer> plmn,
                                  ArrayList<Integer> pilot_frequency_list,
                                  int downlink_frequency_point,
                                    int cell_pci,
                                  int tac,
                                  int uplink_frequency_point,
                                  int measure,
                                  int transmitted_power,

                                  int seqNo
                                  ){
        byte[] cmd=null;
        byte[] downLink_frequency_pointTag = new byte[]{0x08,0x00,0x02, (byte) ((downlink_frequency_point >> 8) & 0xFF),
                (byte) (downlink_frequency_point & 0xFF)};

        byte[] cellPciTag = new byte[]{0x09,0x00,0x02, (byte) ((cell_pci >> 8) & 0xFF),(byte) (cell_pci & 0xFF)};

        byte[] tacTag = new byte[]{14,0x00,0x02,(byte) ((tac >> 8) & 0xFF),(byte) (tac & 0xFF)};

        byte[] pLmnNumberTag = new byte[]{34,0x00,0x04,(byte) ((plmn.size() >> 24) & 0xFF),(byte) (plmn.size()>>16 & 0xFF),
                (byte) ((plmn.size() >> 8) & 0xFF),(byte) (plmn.size() & 0xFF)};

        byte[] pLmnListTag = new byte[0];

        if(plmn != null && plmn.size() != 0){
            pLmnListTag = new byte[7*plmn.size()];
            for(int i = 0; i< plmn.size();i++){
                pLmnListTag[i*7] = 23;
                pLmnListTag[i*7+1] = 0x00;
                pLmnListTag[i*7+2] = 0x04;
                pLmnListTag[i*7+3] = (byte) ((plmn.get(i) >> 24) & 0xFF);
                pLmnListTag[i*7+4] = (byte) ((plmn.get(i) >> 16) & 0xFF);
                pLmnListTag[i*7+5] = (byte) ((plmn.get(i) >> 8) & 0xFF);
                pLmnListTag[i*7+6] = (byte) ((plmn.get(i) >> 0) & 0xFF);
            }
        }
        byte[] pciNumberTag = new byte[0];
        byte[] pciListTag = new byte[0];
        if(pciList == null){
            pciNumberTag = new byte[]{2,0x00,0x01, 0};
        }else {
            pciNumberTag = new byte[]{2,0x00,0x01, (byte) pciList.size()};
            if(pciList.size() != 0){
                pciListTag = new byte[pciList.size()*2+3];
                pciListTag[0] = 0x03;
                pciListTag[1] = 0x00;
                pciListTag[2] = (byte) (pciList.size()*2);
                for(int i = 0;i<pciList.size();i++){
                    pciListTag[2 * i + 3] = (byte) ((pciList.get(i) >> 8) & 0xFF);
                    pciListTag[2 * i + 4] = (byte) ((pciList.get(i)) & 0xFF);
                }
            }
        }



        byte[] pilot_frequency_numberTag = new byte[]{0x07,0x00,0x01, (byte) pilot_frequency_list.size()};
        byte[] pilot_frequency_listTag = new byte[0];
        if(pilot_frequency_list.size() != 0){
            pilot_frequency_listTag = new byte[pilot_frequency_list.size()*2+3];
            pilot_frequency_listTag[0] = 24;
            pilot_frequency_listTag[1] = 0x00;
            pilot_frequency_listTag[2] = (byte) (pilot_frequency_list.size()*2);
            for(int i = 0;i<pilot_frequency_list.size();i++){
                pilot_frequency_listTag[2 * i + 3] = (byte) ((pilot_frequency_list.get(i) >> 8) & 0xFF);
                pilot_frequency_listTag[2 * i + 4] = (byte) ((pilot_frequency_list.get(i)) & 0xFF);
            }
        }
        byte[] uplink_frequency_pointTag = new byte[0];
        if(uplink_frequency_point != -1){
            uplink_frequency_pointTag = new byte[]{31,0x00,0x02, (byte) ((uplink_frequency_point >> 8) & 0xFF),(byte) (uplink_frequency_point & 0xFF)};
        }
        byte[] measureTag = new byte[0];
        if(measure != -1){
            measureTag = new byte[]{33,0x00,0x01, (byte) measure };
        }
        byte[] transmitted_powerTag = new byte[0];
        if(transmitted_power != -1){
            transmitted_powerTag = new byte[]{32,0x00,0x02, (byte) (( transmitted_power>> 8) & 0xFF),(byte) (transmitted_power & 0xFF)};
        }
//        byte[] tac_cycleTag = new byte[0];
//        if(tac_cycle != -1){
//            tac_cycleTag = new byte[]{35,0x00,0x04,(byte) ((tac_cycle >> 24) & 0xFF),(byte) (tac_cycle>>16 & 0xFF),(byte) ((tac_cycle >> 8) & 0xFF),(byte) (tac_cycle & 0xFF)};
//        }

        int length = 0;
        length += downLink_frequency_pointTag.length;
        length += cellPciTag.length;
        length += tacTag.length;
        length += pLmnNumberTag.length;
        length += pLmnListTag.length;
        length += pciNumberTag.length;
        length += pciListTag.length;
        length += pilot_frequency_numberTag.length;
        length += pilot_frequency_listTag.length;
        length += uplink_frequency_pointTag.length;
        length += measureTag.length;
        length += transmitted_powerTag.length;
//        length += tac_cycleTag.length;
        cmd = new byte[length];
        System.arraycopy(downLink_frequency_pointTag, 0, cmd, 0, downLink_frequency_pointTag.length);

        System.arraycopy(cellPciTag, 0, cmd, downLink_frequency_pointTag.length, cellPciTag.length);

        System.arraycopy(tacTag, 0, cmd, downLink_frequency_pointTag.length+ cellPciTag.length , tacTag.length);

        System.arraycopy(pLmnNumberTag, 0, cmd, downLink_frequency_pointTag.length + cellPciTag.length+ tacTag.length, pLmnNumberTag.length);

        System.arraycopy(pLmnListTag, 0, cmd, downLink_frequency_pointTag.length  + cellPciTag.length+ tacTag.length + pLmnNumberTag.length, pLmnListTag.length);

        System.arraycopy(pciNumberTag, 0, cmd, downLink_frequency_pointTag.length + cellPciTag.length + tacTag.length + pLmnNumberTag.length + pLmnListTag.length, pciNumberTag.length);

        System.arraycopy(pciListTag, 0, cmd, downLink_frequency_pointTag.length  + cellPciTag.length+ tacTag.length + pLmnNumberTag.length + pLmnListTag.length +
                pciNumberTag.length, pciListTag.length);

        System.arraycopy(pilot_frequency_numberTag, 0, cmd, downLink_frequency_pointTag.length  + cellPciTag.length+ tacTag.length + pLmnNumberTag.length + pLmnListTag.length +
                pciNumberTag.length + pciListTag.length, pilot_frequency_numberTag.length);

        System.arraycopy(pilot_frequency_listTag, 0, cmd, downLink_frequency_pointTag.length + cellPciTag.length+ tacTag.length + pLmnNumberTag.length + pLmnListTag.length +
                pciNumberTag.length + pciListTag.length+pilot_frequency_numberTag.length, pilot_frequency_listTag.length);

        System.arraycopy(uplink_frequency_pointTag, 0, cmd, downLink_frequency_pointTag.length  + cellPciTag.length+ tacTag.length + pLmnNumberTag.length + pLmnListTag.length +
                pciNumberTag.length + pciListTag.length+pilot_frequency_numberTag.length+pilot_frequency_listTag.length, uplink_frequency_pointTag.length);

        System.arraycopy(measureTag, 0, cmd, downLink_frequency_pointTag.length  + cellPciTag.length+ tacTag.length + pLmnNumberTag.length + pLmnListTag.length +
                pciNumberTag.length + pciListTag.length+pilot_frequency_numberTag.length+pilot_frequency_listTag.length+uplink_frequency_pointTag.length, measureTag.length);

        System.arraycopy(transmitted_powerTag, 0, cmd, downLink_frequency_pointTag.length + cellPciTag.length + tacTag.length + pLmnNumberTag.length + pLmnListTag.length +
                pciNumberTag.length + pciListTag.length+pilot_frequency_numberTag.length+pilot_frequency_listTag.length+uplink_frequency_pointTag.length+measureTag.length, transmitted_powerTag.length);

//        System.arraycopy(tac_cycleTag, 0, cmd, downLink_frequency_pointTag.length + cellPciTag.length+ tacTag.length + pLmnNumberTag.length + pLmnListTag.length +
//                pciNumberTag.length + pciListTag.length+pilot_frequency_numberTag.length+pilot_frequency_listTag.length+uplink_frequency_pointTag.length+measureTag.length+transmitted_powerTag.length, tac_cycleTag.length);
        byte[] data = cmd;
        byte[] headData = new byte[]{0x01, 0x0f, 0x00, (byte) (data.length + 7), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
        byte[] sendData = new byte[data.length + headData.length];
        System.arraycopy(headData, 0, sendData, 0, headData.length);
        System.arraycopy(data, 0, sendData, 0 + headData.length, data.length);
        return sendData;
    }

    public static void main(String[] args) {
        ArrayList<Integer> pciList=new ArrayList<>();
        pciList.add(11);

        ArrayList<Integer> plmnList=new ArrayList<>();
        plmnList.add(4600);

        ArrayList<Integer> pilot_list=new ArrayList<>();
        pilot_list.add(0);

        int downlink_frequency_point=1;
        int cell_pci=1;
        int tac=1;
        int uplink_frequency_point=1;
        int measure=1;
        int transmitted_power=1;
        int tac_cycle=1;
        int seqNo=1;
        byte[] data=CellConfig(pciList,plmnList,pilot_list,
                downlink_frequency_point,cell_pci,tac,uplink_frequency_point,measure,transmitted_power,
                tac_cycle,seqNo);
        System.out.printf("=="+CharacterConversionTool.bytesToHexString(data));
    }
    public static byte[] CellConfig(ArrayList<Integer> pciList,
                                    ArrayList<Integer> plmn,
                                    ArrayList<Integer> pilot_frequency_list,
                                    int downlink_frequency_point,
                                    int cell_pci,
                                    int tac,
                                    int uplink_frequency_point,
                                    int measure,
                                    int transmitted_power,
                                    int tac_cycle,
                                    int seqNo
    ){
        byte[] cmd=null;
        byte[] downLink_frequency_pointTag = new byte[]{0x08,0x00,0x02, (byte) ((downlink_frequency_point >> 8) & 0xFF),
                (byte) (downlink_frequency_point & 0xFF)};

        byte[] cellPciTag = new byte[]{0x09,0x00,0x02, (byte) ((cell_pci >> 8) & 0xFF),(byte) (cell_pci & 0xFF)};

        byte[] tacTag = new byte[]{14,0x00,0x02,(byte) ((tac >> 8) & 0xFF),(byte) (tac & 0xFF)};

        byte[] pLmnNumberTag = new byte[]{34,0x00,0x04,(byte) ((plmn.size() >> 24) & 0xFF),(byte) (plmn.size()>>16 & 0xFF),
                (byte) ((plmn.size() >> 8) & 0xFF),(byte) (plmn.size() & 0xFF)};

        byte[] pLmnListTag = new byte[0];

        if(plmn != null && plmn.size() != 0){
            pLmnListTag = new byte[7*plmn.size()];
            for(int i = 0; i< plmn.size();i++){
                pLmnListTag[i*7] = 23;
                pLmnListTag[i*7+1] = 0x00;
                pLmnListTag[i*7+2] = 0x04;
                pLmnListTag[i*7+3] = (byte) ((plmn.get(i) >> 24) & 0xFF);
                pLmnListTag[i*7+4] = (byte) ((plmn.get(i) >> 16) & 0xFF);
                pLmnListTag[i*7+5] = (byte) ((plmn.get(i) >> 8) & 0xFF);
                pLmnListTag[i*7+6] = (byte) ((plmn.get(i) >> 0) & 0xFF);
            }
        }
        byte[] pciNumberTag = new byte[0];
        byte[] pciListTag = new byte[0];
        if(pciList == null){
            pciNumberTag = new byte[]{2,0x00,0x01, 0};
        }else {
            pciNumberTag = new byte[]{2,0x00,0x01, (byte) pciList.size()};
            if(pciList.size() != 0){
                pciListTag = new byte[pciList.size()*2+3];
                pciListTag[0] = 0x03;
                pciListTag[1] = 0x00;
                pciListTag[2] = (byte) (pciList.size()*2);
                for(int i = 0;i<pciList.size();i++){
                    pciListTag[2 * i + 3] = (byte) ((pciList.get(i) >> 8) & 0xFF);
                    pciListTag[2 * i + 4] = (byte) ((pciList.get(i)) & 0xFF);
                }
            }
        }



        byte[] pilot_frequency_numberTag = new byte[]{0x07,0x00,0x01, (byte) pilot_frequency_list.size()};
        byte[] pilot_frequency_listTag = new byte[0];
        if(pilot_frequency_list.size() != 0){
            pilot_frequency_listTag = new byte[pilot_frequency_list.size()*2+3];
            pilot_frequency_listTag[0] = 24;
            pilot_frequency_listTag[1] = 0x00;
            pilot_frequency_listTag[2] = (byte) (pilot_frequency_list.size()*2);
            for(int i = 0;i<pilot_frequency_list.size();i++){
                pilot_frequency_listTag[2 * i + 3] = (byte) ((pilot_frequency_list.get(i) >> 8) & 0xFF);
                pilot_frequency_listTag[2 * i + 4] = (byte) ((pilot_frequency_list.get(i)) & 0xFF);
            }
        }
        byte[] uplink_frequency_pointTag = new byte[0];
        if(uplink_frequency_point != -1){
            uplink_frequency_pointTag = new byte[]{31,0x00,0x02, (byte) ((uplink_frequency_point >> 8) & 0xFF),(byte) (uplink_frequency_point & 0xFF)};
        }
        byte[] measureTag = new byte[0];
        if(measure != -1){
            measureTag = new byte[]{33,0x00,0x01, (byte) measure };
        }
        byte[] transmitted_powerTag = new byte[0];
        if(transmitted_power != -1){
            transmitted_powerTag = new byte[]{32,0x00,0x02, (byte) (( transmitted_power>> 8) & 0xFF),(byte) (transmitted_power & 0xFF)};
        }
        byte[] tac_cycleTag = new byte[0];
        if(tac_cycle != -1){
            tac_cycleTag = new byte[]{35,0x00,0x04,(byte) ((tac_cycle >> 24) & 0xFF),(byte) (tac_cycle>>16 & 0xFF),(byte) ((tac_cycle >> 8) & 0xFF),(byte) (tac_cycle & 0xFF)};
        }

        int length = 0;
        length += downLink_frequency_pointTag.length;
        length += cellPciTag.length;
        length += tacTag.length;
        length += pLmnNumberTag.length;
        length += pLmnListTag.length;
        length += pciNumberTag.length;
        length += pciListTag.length;
        length += pilot_frequency_numberTag.length;
        length += pilot_frequency_listTag.length;
        length += uplink_frequency_pointTag.length;
        length += measureTag.length;
        length += transmitted_powerTag.length;
        length += tac_cycleTag.length;
        cmd = new byte[length];
        System.arraycopy(downLink_frequency_pointTag, 0, cmd, 0, downLink_frequency_pointTag.length);

        System.arraycopy(cellPciTag, 0, cmd, downLink_frequency_pointTag.length, cellPciTag.length);

        System.arraycopy(tacTag, 0, cmd, downLink_frequency_pointTag.length+ cellPciTag.length , tacTag.length);

        System.arraycopy(pLmnNumberTag, 0, cmd, downLink_frequency_pointTag.length + cellPciTag.length+ tacTag.length, pLmnNumberTag.length);

        System.arraycopy(pLmnListTag, 0, cmd, downLink_frequency_pointTag.length  + cellPciTag.length+ tacTag.length + pLmnNumberTag.length, pLmnListTag.length);

        System.arraycopy(pciNumberTag, 0, cmd, downLink_frequency_pointTag.length + cellPciTag.length + tacTag.length + pLmnNumberTag.length + pLmnListTag.length, pciNumberTag.length);

        System.arraycopy(pciListTag, 0, cmd, downLink_frequency_pointTag.length  + cellPciTag.length+ tacTag.length + pLmnNumberTag.length + pLmnListTag.length +
                pciNumberTag.length, pciListTag.length);

        System.arraycopy(pilot_frequency_numberTag, 0, cmd, downLink_frequency_pointTag.length  + cellPciTag.length+ tacTag.length + pLmnNumberTag.length + pLmnListTag.length +
                pciNumberTag.length + pciListTag.length, pilot_frequency_numberTag.length);

        System.arraycopy(pilot_frequency_listTag, 0, cmd, downLink_frequency_pointTag.length + cellPciTag.length+ tacTag.length + pLmnNumberTag.length + pLmnListTag.length +
                pciNumberTag.length + pciListTag.length+pilot_frequency_numberTag.length, pilot_frequency_listTag.length);

        System.arraycopy(uplink_frequency_pointTag, 0, cmd, downLink_frequency_pointTag.length  + cellPciTag.length+ tacTag.length + pLmnNumberTag.length + pLmnListTag.length +
                pciNumberTag.length + pciListTag.length+pilot_frequency_numberTag.length+pilot_frequency_listTag.length, uplink_frequency_pointTag.length);

        System.arraycopy(measureTag, 0, cmd, downLink_frequency_pointTag.length  + cellPciTag.length+ tacTag.length + pLmnNumberTag.length + pLmnListTag.length +
                pciNumberTag.length + pciListTag.length+pilot_frequency_numberTag.length+pilot_frequency_listTag.length+uplink_frequency_pointTag.length, measureTag.length);

        System.arraycopy(transmitted_powerTag, 0, cmd, downLink_frequency_pointTag.length + cellPciTag.length + tacTag.length + pLmnNumberTag.length + pLmnListTag.length +
                pciNumberTag.length + pciListTag.length+pilot_frequency_numberTag.length+pilot_frequency_listTag.length+uplink_frequency_pointTag.length+measureTag.length, transmitted_powerTag.length);

        System.arraycopy(tac_cycleTag, 0, cmd, downLink_frequency_pointTag.length + cellPciTag.length+ tacTag.length + pLmnNumberTag.length + pLmnListTag.length +
                pciNumberTag.length + pciListTag.length+pilot_frequency_numberTag.length+pilot_frequency_listTag.length+uplink_frequency_pointTag.length+measureTag.length+transmitted_powerTag.length, tac_cycleTag.length);
        byte[] data = cmd;
        byte[] headData = new byte[]{0x01, 0x0f, 0x00, (byte) (data.length + 7), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
        byte[] sendData = new byte[data.length + headData.length];
        System.arraycopy(headData, 0, sendData, 0, headData.length);
        System.arraycopy(data, 0, sendData, 0 + headData.length, data.length);
        return sendData;
    }

    /**
     * 关闭dbm
     * @param seqNo
     */
    public static byte[] CLOSE_DBM(int seqNo){
        byte[] headData = new byte[]{0x02, 0x2f, 0x00, 0x0b, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF), 0x3a, 0x00, 0x01, 0x7f};
        return headData;
    }

    /**
     * 打开dbm
     * @param seqNo
     * @param DBM
     * @return
     */
    public static byte[] OPEN_DBM(int seqNo,byte DBM){
        byte[] headData = new byte[]{0x02, 0x2f, 0x00, 0x0b, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF), 0x3a, 0x00, 0x01, DBM};
        return headData;
    }

    /**
     * 系统状态查询
     * @param seqNo
     * @return
     */
    public static byte[] SYSTEM_STATUS_REQ(int seqNo){
        byte[] headData = new byte[]{0x02, 0x0a, 0x00, 0x07, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
        return headData;
    }

    /**
     * 4.4.5 启动扫频
     * @param seqNo
     * @param pciList PCI列表
     * @param earfchList 频点范围列表
     * @param Rssi RSSI值
     * @param Scan_result 扫频结果上报策略
     * @return
     */
    public static  byte[] SNIFFER_START(int seqNo,ArrayList<Integer> pciList,ArrayList<Integer> earfchList,byte Rssi,byte Scan_result){
        byte[] cmd;
        byte[] pciNumber = new byte[]{2, 0x00, 0x01, (byte) pciList.size()};
        byte[] pciList1 = new byte[0];
        if (pciList.size() != 0) {
            pciList1 = new byte[pciList.size() * 2 + 3];
            pciList1[0] = 3;
            pciList1[1] = 0;
            pciList1[2] = (byte) (pciList.size() * 2);
            for (int i = 0; i < pciList.size(); i++) {
                pciList1[2 * i + 3] = (byte) ((pciList.get(i) >> 8) & 0xFF);
                pciList1[2 * i + 4] = (byte) ((pciList.get(i)) & 0xFF);
            }
        }
        byte[] earfchNumber = new byte[]{4, 0x00, 0x01, (byte) (earfchList.size()/2)};
        byte[] earfchList1 = new byte[0];
        ;
        if (earfchList.size() != 0) {
            earfchList1 = new byte[earfchList.size() * 2 + 3];
            earfchList1[0] = 5;
            earfchList1[1] = 0;
            earfchList1[2] = (byte) (earfchList.size() * 2);
            for (int i = 0; i < earfchList.size(); i++) {
                earfchList1[2 * i + 3] = (byte) ((earfchList.get(i) >> 8) & 0xFF);
                earfchList1[2 * i + 4] = (byte) ((earfchList.get(i)) & 0xFF);
            }
        }
        byte[] rssi = new byte[]{13, 0x00, 0x02, (byte) ((Rssi >> 8) & 0xFF), (byte) (Rssi& 0xFF)};
        byte[] scan_result = new byte[0];
        if (Scan_result != -1) {
            scan_result = new byte[]{26, 0x00, 0x01, Scan_result};
        }
        int length = 0;
        length += pciList1.length;
        length += earfchList1.length;
        length += scan_result.length;
        length += pciNumber.length;
        length += earfchNumber.length;
        length += rssi.length;
        cmd = new byte[length];
        System.arraycopy(pciNumber, 0, cmd, 0, pciNumber.length);
        System.arraycopy(pciList1, 0, cmd, 0 + pciNumber.length, pciList1.length);
        System.arraycopy(earfchNumber, 0, cmd, 0 + pciNumber.length + pciList1.length, earfchNumber.length);
        System.arraycopy(earfchList1, 0, cmd, 0 + pciNumber.length + pciList1.length + earfchNumber.length, earfchList1.length);
        System.arraycopy(rssi, 0, cmd, 0 + pciNumber.length + pciList1.length + earfchNumber.length + earfchList1.length, rssi.length);
        System.arraycopy(scan_result, 0, cmd, 0 + pciNumber.length + pciList1.length + earfchNumber.length + earfchList1.length + rssi.length, scan_result.length);

        byte[] data = cmd;
        byte[] headData = new byte[]{0x01, 0x05, 0x00, (byte) (data.length + 7), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
        byte[] sendData = new byte[data.length + headData.length];
        System.arraycopy(headData, 0, sendData, 0, headData.length);
        System.arraycopy(data, 0, sendData, 0 + headData.length, data.length);
        return sendData;
    }

    /**
     * 小区更新
     * @param seqNo
     * @param mPlmn PLMN list
     * @param measure 是否启用测量
     * @param downlink_frequency_point 下行频点
     * @param uplink_frequency_point 上行频点
     * @param transmitted_power
     * @param tac TAC
     */
    public static byte[] CELL_UPDATE(int seqNo,ArrayList<Integer> mPlmn,int measure,int downlink_frequency_point,
                                  int uplink_frequency_point,int transmitted_power,int tac){
        byte[] cmd1;
        byte[] pLmnNumberTag = new byte[]{34,0x00,0x04,(byte) ((mPlmn.size() >> 24) & 0xFF),(byte) (mPlmn.size()>>16 & 0xFF),(byte) ((mPlmn.size() >> 8) & 0xFF),(byte) (mPlmn.size() & 0xFF)};
        byte[] pLmnListTag = new byte[0];
        if(mPlmn != null && mPlmn.size() != 0){
            pLmnListTag = new byte[7*mPlmn.size()];
            for(int i = 0; i< mPlmn.size();i++){
                pLmnListTag[i*7] = 23;
                pLmnListTag[i*7+1] = 0x00;
                pLmnListTag[i*7+2] = 0x04;
                pLmnListTag[i*7+3] = (byte) ((mPlmn.get(i) >> 24) & 0xFF);
                pLmnListTag[i*7+4] = (byte) ((mPlmn.get(i) >> 16) & 0xFF);
                pLmnListTag[i*7+5] = (byte) ((mPlmn.get(i) >> 8) & 0xFF);
                pLmnListTag[i*7+6] = (byte) ((mPlmn.get(i) >> 0) & 0xFF);
            }
        }
        byte[] measureTag = new byte[0];
        if(measure != -1){
            measureTag = new byte[]{33,0x00,0x01, (byte) measure };
        }
        byte[] downLink_frequency_pointTag = new byte[]{0x08,0x00,0x02, (byte) ((downlink_frequency_point >> 8) & 0xFF),(byte) (downlink_frequency_point & 0xFF)};
        byte[] uplink_frequency_pointTag = new byte[0];
        if(uplink_frequency_point != -1){
            uplink_frequency_pointTag = new byte[]{31,0x00,0x02, (byte) ((uplink_frequency_point >> 8) & 0xFF),(byte) (uplink_frequency_point & 0xFF)};
        }
        byte[] transmitted_powerTag = new byte[0];
        if(transmitted_power != -1){
            transmitted_powerTag = new byte[]{32,0x00,0x02, (byte) (( transmitted_power>> 8) & 0xFF),(byte) (transmitted_power & 0xFF)};
        }
        int length = 0;
        length += pLmnNumberTag.length;
        length += pLmnListTag.length;
        length += measureTag.length;
        length += downLink_frequency_pointTag.length;
        length += transmitted_powerTag.length;
        length += uplink_frequency_pointTag.length;
        cmd1 = new byte[length];
        System.arraycopy(pLmnNumberTag, 0, cmd1, 0, pLmnNumberTag.length);
        System.arraycopy(pLmnListTag, 0, cmd1, pLmnNumberTag.length, pLmnListTag.length);
        System.arraycopy(measureTag, 0, cmd1, pLmnNumberTag.length+pLmnListTag.length, measureTag.length);
        System.arraycopy(downLink_frequency_pointTag, 0, cmd1, pLmnNumberTag.length+pLmnListTag.length+measureTag.length, downLink_frequency_pointTag.length);
        System.arraycopy(transmitted_powerTag, 0, cmd1, pLmnNumberTag.length+pLmnListTag.length+measureTag.length+downLink_frequency_pointTag.length, transmitted_powerTag.length);
        System.arraycopy(uplink_frequency_pointTag, 0, cmd1, pLmnNumberTag.length+pLmnListTag.length+measureTag.length+downLink_frequency_pointTag.length+transmitted_powerTag.length, uplink_frequency_pointTag.length);

        byte[] plmn =cmd1;
        byte[] data = new byte[]{0x01, 17, 0x00, (byte) (12+plmn.length),14,0x00,0x02,(byte) ((tac >> 8) & 0xFF),(byte) (tac & 0xFF), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
        byte[] sendData = new byte[plmn.length +data.length];
        System.arraycopy(data, 0, sendData, 0, data.length);
        System.arraycopy(plmn, 0, sendData, 0 + data.length, plmn.length);
        return sendData;
    }

    /**
     * 重启复位
     * @param seqNo
     * @return
     */
    public static byte[] EQUIP_RESET(int seqNo){
        String restart = "重启";
        byte[] bytes = restart.getBytes();
        byte[] headData = new byte[]{0x02, 14, 0x00, (byte) (0x0a + bytes.length), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF), 5, 0x00, (byte) bytes.length};
        byte[] data = new byte[bytes.length + headData.length];
        System.arraycopy(headData, 0, data, 0, headData.length);
        System.arraycopy(bytes, 0, data, 0 + headData.length, bytes.length);
        return data;
    }

    /**
     * 扫频结束
     * @param seqNo
     * @return
     */
    public static byte[] SNIFFER_STOP(int seqNo){
        byte[] data = new byte[]{0x01, 0x07, 0x00, 7, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
        return data;
    }

    /**
     * 复位扫频
     * @param seqNo
     * @return
     */
    public static byte[] SNIFFER_REST(int seqNo){
        byte[] data = new byte[]{0x01, 0x09, 0x00, 7, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
        return data;
    }

    /**
     * 软件版本查询
     * @param seqNo
     * @return
     */
    public static byte[] SOFTWARE_INFO_REQ(int seqNo){
        byte[] data = new byte[]{0x02, 0x05, 0x00, 0x07, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
        return data;
    }

    /**
     * 设置系统时间
     * @param seqNo
     * @return
     */
    public static byte[] SYSTEM_TIME(int seqNo){
        long times = System.currentTimeMillis()/ 1000L;
        byte[] data = new byte[]{0x02, 0x01, 0x00, 0x12, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF),
                0x03, 0x00, 0x08, (byte) ((times >> 56) & 0xFF), (byte) ((times >> 48) & 0xFF), (byte) ((times >> 40) & 0xFF), (byte) ((times >> 32) & 0xFF), (byte) ((times >> 24) & 0xFF), (byte) ((times >> 16) & 0xFF), (byte) ((times >> 8) & 0xFF), (byte) (times & 0xFF)};
        return data;
    }

    /**
     * 重定向
     * @param seqNo
     * @param isOpen
     * @param lte_type 制式
     * @param band 频带band
     * @param point_list 频点列表
     * @param imsiList IMSI列表
     * @return
     */
    public static  byte[] UE_REDIREC(int seqNo,boolean isOpen,byte lte_type,
                                     int band,ArrayList<Integer> point_list,ArrayList<String> imsiList){
        if(isOpen){
            byte[] openTag = new byte[]{29, 0, 1, 1};
            byte[] typeTag = new byte[]{25, 0, 1, (byte) lte_type};

            byte[] bandTag = new byte[]{21, 0, 1, (byte) band};

            byte[] pilot_numberTag = new byte[]{0x07, 0x00, 0x01, (byte) point_list.size()};

            byte[] pilot_listTag = new byte[0];
            if (point_list.size() != 0) {
                pilot_listTag = new byte[point_list.size() * 2 + 3];
                pilot_listTag[0] = 24;
                pilot_listTag[1] = 0x00;
                pilot_listTag[2] = (byte) (point_list.size() * 2);
                for (int i = 0; i < point_list.size(); i++) {
                    pilot_listTag[2 * i + 3] = (byte) ((point_list.get(i) >> 8) & 0xFF);
                    pilot_listTag[2 * i + 4] = (byte) ((point_list.get(i)) & 0xFF);
                }
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (String imsi : imsiList) {
                stringBuilder.append(imsi);
            }
            byte[] bytes = CharacterConversionTool.hexStr2Bytes(stringBuilder.toString());
            byte[] bytes1 = new byte[]{30, (byte) ((bytes.length >> 8) & 0xFF), (byte) (bytes.length & 0xFF)};
            byte[] imsiTag = new byte[bytes.length + bytes1.length];
            System.arraycopy(bytes1, 0, imsiTag, 0, bytes1.length);
            System.arraycopy(bytes, 0, imsiTag, bytes1.length, bytes.length);
            byte[] data = new byte[openTag.length + typeTag.length + bandTag.length + pilot_numberTag.length +
                    pilot_listTag.length + imsiTag.length];
            System.arraycopy(openTag, 0, data, 0, openTag.length);
            System.arraycopy(typeTag, 0, data, 0 + openTag.length, typeTag.length);
            System.arraycopy(bandTag, 0, data, 0 + openTag.length + typeTag.length, bandTag.length);
            System.arraycopy(pilot_numberTag, 0, data, 0 + openTag.length + typeTag.length + bandTag.length, pilot_numberTag.length);
            System.arraycopy(pilot_listTag, 0, data, 0 + openTag.length + typeTag.length + bandTag.length + pilot_numberTag.length, pilot_listTag.length);
            System.arraycopy(imsiTag, 0, data, 0 + openTag.length + typeTag.length + bandTag.length + pilot_numberTag.length + pilot_listTag.length, imsiTag.length);
            byte[] data1 = data;
            byte[] headData = new byte[]{0x01, 27, 0x00, (byte) (data.length + 7), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
            byte[] sendData3 = new byte[data.length + headData.length];
            System.arraycopy(headData, 0, sendData3, 0, headData.length);
            System.arraycopy(data, 0, sendData3, 0 + headData.length, data.length);
            return data1;
        }else {
            byte[] openTag = new byte[]{29, 0, 1, 0};
            StringBuilder stringBuilder = new StringBuilder();
            for (String imsi : imsiList) {
                stringBuilder.append(imsi);
            }
            byte[] bytes = CharacterConversionTool.hexStr2Bytes(stringBuilder.toString());
            byte[] bytes1 = new byte[]{30, (byte) ((bytes.length >> 8) & 0xFF), (byte) (bytes.length & 0xFF)};
            byte[] imsiTag = new byte[bytes.length + bytes1.length];
            System.arraycopy(bytes1, 0, imsiTag, 0, bytes1.length);
            System.arraycopy(bytes, 0, imsiTag, bytes1.length, bytes.length);
            byte[] data = new byte[openTag.length  + imsiTag.length];
            System.arraycopy(openTag, 0, data, 0, openTag.length);
            System.arraycopy(imsiTag, 0, data, 0 + openTag.length, imsiTag.length);
            byte[] data1 = data;
            byte[] headData = new byte[]{0x01, 27, 0x00, (byte) (data.length + 7), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
            byte[] sendData3 = new byte[data.length + headData.length];
            System.arraycopy(headData, 0, sendData3, 0, headData.length);
            System.arraycopy(data, 0, sendData3, 0 + headData.length, data.length);
            return data1;
        }

    }


    /**
     * 启动通知响应
     * @param seqNo
     * @return
     */
    public static byte[] INIT_NOTIFICATION_RSP(int seqNo){
        byte[] data = new byte[]{0x01, 0x02, 0x00, 0x07, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};

        return data;
    }

    /**
     * 初始化配置
     * @param seqNo
     * @param Bandwidth 带宽：0表示10M，1表示20M, 2表示5M
     * @param TimeDelayField 时延域（单位0.01us，取值为1PPS相对于帧投的时延）
     * @param SynchronousMode 同步模式：0：CNM 1：GPS 2：nmm辅助纠频偏 3：混合模式（优先GPS，同步失败采用空口同步）4：自动同步（暂不支持）
     * @param FrequencyOffset 是否保存频偏
     * @param OperatingBand 工作频带
     * @return
     */
    public static byte[] INIT_CONFIG(int seqNo, int Bandwidth,int TimeDelayField,int SynchronousMode,int FrequencyOffset,int OperatingBand){
        byte[] bindwith = new byte[]{27, 0x00, 0x01, (byte)Bandwidth};
        byte[] timeDelayField = new byte[]{46, 0x00, 0x04, (byte) ((TimeDelayField >> 24) & 0xFF), (byte) ((TimeDelayField>> 16) & 0xFF), (byte) ((TimeDelayField >> 8) & 0xFF), (byte) (TimeDelayField & 0xFF)};
        byte[] synchronousMode = new byte[]{14, 0x00, 0x01, (byte) SynchronousMode};
        byte[] frequencyOffset = new byte[]{47, 0x00, 0x01, (byte) FrequencyOffset};
        byte[] operatingBand = new byte[]{50, 0x00, 0x01, (byte) OperatingBand};

        return new byte[]{0x02, 28, 0x00, 30, bindwith[0], bindwith[1], bindwith[2], bindwith[3],
                timeDelayField[0], timeDelayField[1], timeDelayField[2], timeDelayField[3], timeDelayField[4], timeDelayField[5], timeDelayField[6],
                synchronousMode[0], synchronousMode[1], synchronousMode[2], synchronousMode[3],
                frequencyOffset[0], frequencyOffset[1], frequencyOffset[2], frequencyOffset[3],
                operatingBand[0], operatingBand[1], operatingBand[2], operatingBand[3],
                0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};

    }

    /**
     * 扫频结果上报响应
     * @param seqNo
     * @return
     */
    public static  byte[] SNIFFER_RESULT_REPORT_RSP(int seqNo){
        byte[] data = new byte[]{0x01, 0x0c, 0x00, 0x07, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
        return data;
    }

    /**
     * 扫频结果上报结束响应
     * @param seqNo
     * @return
     */
    public static byte[]  SNIFFER_RESULT_REPORT_END_RSP(int seqNo){
        byte[] data = new byte[]{0x01, 0x0e, 0x00, 0x07, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
        return data;
    }

    /**
     * 侦码响应
     * @param seqNo
     * @return
     */
    public static  byte[]  UEID_REPORT_RSP(int seqNo){
        byte[] data = new byte[]{0x01, 20, 0x00, 0x07, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
        return data;
    }

    /**
     * IMSI定位模式设置
     * @param seqNo
     * @param imsiList
     * @return
     */
    public static byte[] LOCATION_MODE_SETTING(int seqNo,ArrayList<String > imsiList){
        StringBuilder stringBuilder = new StringBuilder();
        for (String imsi : imsiList) {
            stringBuilder.append(imsi);
        }
        byte[] bytes = CharacterConversionTool.hexStr2Bytes(stringBuilder.toString());
        byte[] headData = new byte[]{0x02, 32, 0x00, (byte) (14 + bytes.length), 36, 0x00, 0x01, 0x01, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF), 37, (byte) ((bytes.length >> 8) & 0xFF), (byte) (bytes.length & 0xFF)};
        final byte[] data = new byte[bytes.length + headData.length];
        System.arraycopy(headData, 0, data, 0, headData.length);
        System.arraycopy(bytes, 0, data, 0 + headData.length, bytes.length);
        return data;
    }

    /**
     * 关闭定位
     * @param seqNo
     * @return
     */
    public static byte[] CLOSE_LOCATION(int seqNo){
        byte[] data = new byte[]{0x02, 32, 0x00, 11, 36, 0x00, 0x01, 0x00, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
        return data;
    }

    /**
     * 心跳
     * @param seqNo
     * @return
     */
    public static byte[] HEARBEAT(int seqNo){
        byte[] data = new byte[]{0x01, 0x04, 0x00, 0x07, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
        return data;
    }

    /**
     * 切换TDD Fdd
     * @param seqNo
     * @param value 0��TDD 1��FDD
     * @return
     */
    public static byte[] SWICH_TDD_FDD(int seqNo,byte value){
        byte[] data = new byte[]{0x02, 0x47, 0x00, 0x0b, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF),
                (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF), 0x5f, 0x00, 0x01, (byte) value};
        return data;
    }

    /**
     * 同步状态响应
     * @param seqNo
     * @return
     */
    public static byte[] SYNC_STATUS_REQ(int seqNo){
        byte[] data = new byte[]{0x02, 0x13, 0x00, 0x07, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
        return data;
    }

    /**
     * 版本更新
     *
     * @param seqNo
     * @param fileName
     * @param Md5
     * @param type
     * @return
     */
    public static byte[] ver_update(int seqNo, String ftp_filePath, String fileName, String Md5, int type) {


        byte[] seqdate = new byte[]{0x01, 0x00, 0x04,
                (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)
        };
        byte[] server_url = ftp_filePath.getBytes();

        byte[] file_name = fileName.getBytes();
        ;
        byte[] md5 = Md5.getBytes();

        byte[] pro_sig = new byte[]{0x02, 0x07};

        byte[] tag_7 = new byte[]{0x07, 0x00, (byte) (server_url.length)};
        byte[] value_tag_7 = new byte[3 + server_url.length];
        System.arraycopy(tag_7, 0, value_tag_7, 0, tag_7.length);
        System.arraycopy(server_url, 0, value_tag_7, tag_7.length, server_url.length);
        byte[] tag_13 = new byte[]{0x0d, 0x00, (byte) (md5.length)};
        byte[] value_tag_13 = new byte[3 + md5.length];
        System.arraycopy(tag_13, 0, value_tag_13, 0, tag_13.length);
        System.arraycopy(md5, 0, value_tag_13, tag_13.length, md5.length);
        byte[] tag_28 = new byte[]{0x1c, 0x00, (byte) (file_name.length)};
        byte[] value_tag_28 = new byte[3 + file_name.length];
        System.arraycopy(tag_28, 0, value_tag_28, 0, tag_28.length);
        System.arraycopy(file_name, 0, value_tag_28, tag_28.length, file_name.length);

        byte[] value_tag_35 = new byte[]{0x23, 0x00, 0x01, (byte) type};
        short message_length = (short) (value_tag_7.length + value_tag_13.length +
                value_tag_28.length + value_tag_35.length);
        byte[] length_message = CharacterConversionTool.shortTobytes(message_length);

        byte[] data = new byte[message_length + 4 + 7];

        System.arraycopy(pro_sig, 0, data, 0, pro_sig.length);
        System.arraycopy(length_message, 0, data, pro_sig.length, length_message.length);
        System.arraycopy(value_tag_7, 0, data, pro_sig.length + length_message.length, value_tag_7.length);
        System.arraycopy(value_tag_13, 0, data, pro_sig.length + length_message.length + value_tag_7.length, value_tag_13.length);
        System.arraycopy(value_tag_28, 0, data, pro_sig.length + length_message.length + value_tag_7.length + value_tag_13.length, value_tag_28.length);
        System.arraycopy(value_tag_35, 0, data, pro_sig.length + length_message.length + value_tag_7.length + value_tag_13.length + value_tag_28.length, value_tag_35.length);
        System.arraycopy(seqdate, 0, data, pro_sig.length + length_message.length + value_tag_7.length + value_tag_13.length + value_tag_28.length + value_tag_35.length, seqdate.length);

        return data;
    }

    /**
     * 运行时参数配置
     * @param seqNo
     * @return
     */
    public static byte[] run_time_para_cfg(int seqNo){
        return  new byte[]{0x02, 0x37, 0x00, 0x0b, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF),
                0x57, 0x00, 0x01, 0x01};
    }
    public static byte[] run_time_para_cfg_point_cfg(int seqNo,boolean isReportPoint){
        return  new byte[]{
                0x02, 0x37, 0x00, 0x0f,
                0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF),
                (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF),
                0x57, 0x00, 0x01, 0x01,
                0x59, 0x00, 0x01, (byte) (isReportPoint?1:0)};
    }
    public static byte[] openScanMode(int seqNo,int scanTime, List<Integer> pointList){
        byte[] data=null;

        byte[] seqData=new byte[]{0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF),
                (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF),
                (byte) (seqNo & 0xFF)};

        byte[] scanTimeData=new byte[7];
        byte[] scanNumber=new byte[]{0x49,0x00,0x04};
        byte[] scanVlue=CharacterConversionTool.intToBytes2( scanTime);

        System.arraycopy(scanNumber, 0, scanTimeData, 0, scanNumber.length);
        System.arraycopy(scanVlue, 0, scanTimeData, scanNumber.length, scanVlue.length);
        byte[] pointListData=new byte[3+pointList.size()*2];
        byte[] len=CharacterConversionTool.shortTobytes((short) (pointList.size()*2));
        byte[] pointNumber=new byte[3];
        byte[] pointNo=new byte[]{0x48};
        System.arraycopy(pointNo, 0, pointNumber, 0, pointNo.length);
        System.arraycopy(len, 0, pointNumber,  pointNo.length, len.length);

        System.arraycopy(pointNumber, 0, pointListData, 0, pointNumber.length);
        for (int i=0;i<pointList.size();i++){
            int value=pointList.get(i);
            byte[] value_data=CharacterConversionTool.shortTobytes((short) value);
            System.arraycopy(value_data, 0, pointListData, 3+i*2, value_data.length);
        }
        int content_len=7+7+3+pointList.size()*2;
        data=new byte[4+content_len];

        byte[] head_no=new byte[]{0x02,0x33};
        byte[] content_lent=CharacterConversionTool.shortTobytes((short) content_len);
        System.arraycopy(head_no, 0, data, 0, head_no.length);
        System.arraycopy(content_lent, 0, data, head_no.length, content_lent.length);

        System.arraycopy(seqData, 0, data, head_no.length+content_lent.length, seqData.length);
        System.arraycopy(scanTimeData, 0, data, head_no.length+content_lent.length+seqData.length, scanTimeData.length);
        System.arraycopy(pointListData, 0, data, head_no.length+content_lent.length+seqData.length+scanTimeData.length, pointListData.length);

        return data;
    }


    public static byte[] update_reason(int seqNo,int value){
        return  new byte[]{0x02, 0x37, 0x00, 0x0f, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF),
                0x4c, 0x00, 0x01, 0x00,0x4d, 0x00, 0x01, (byte) value};
    }

    /**
     *
     * @param seqNo
     * @param tac
     * @return
     */
    public static byte[] setTac(int seqNo,int tac){
        byte[] data = new byte[]{0x01, 17, 0x00, (byte) 12, 14, 0x00, 0x02, (byte) ((tac >> 8) & 0xFF), (byte) (tac & 0xFF), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
        byte[] sendData_tac = new byte[ data.length];
        System.arraycopy(data, 0, sendData_tac, 0, data.length);
        return sendData_tac;
    }
}
