package wcdma;



import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import tool.CharacterConversionTool;

public class WcdmaDataHelper {

    private static final String TAG = "WcdmaDataHelper";
    public static final String KEY_CPUTEMP = "CPUTEMP";
    public static final String KEY_RESULT = "RESULT";
    public static final String KEY_INFO = "INFO";
    public static final String KEY_SUCCESS = "SUCCESS";
    public static final String KEY_imsi = "imsi";
    public static final String KEY_imei = "imei";
    public static final String KEY_ID = "ID";
    //----------------------------------小区参数-------------------------------------//
    public static  String mcc = "";
    public static  String mnc = "";
    public static  String urfcn = "";
    public static  String cpi = "";
    public static  String lac = "";
    public static  String cellid = "";
    public static  String fasonggonlv = "";
    public static  String jiantingpindian = "";
    public static  String jiantingcpi = "";

    //----------------------------------小区参数-------------------------------------//

    //----------------------------------数据上报-------------------------------------//
    /**
     * 心跳
     */
    public static final String HeatBeatAck = "HeatBeatAck";
    /**
     * 心跳消息号
     */
    public static final String HeartBeatAck_IndiId="101";
    /**
     * 状态上报
     */
    public static final String KEY_StatusRptIndi = "StatusRptIndi";
    /**
     * 状态上报消息号
     */
    public static final String  KEY_StatusRptIndi_id ="116";

    /**
     * 板卡异常上报
     */
    public static final String KEY_ErrorIndi = "ErrorIndi";
    /**
     * 板卡异常上报消息号
     */
    public static final String  KEY_ErrorIndi_id ="108";
    /**
     * 实时上报ue
     */
    public static final String KEY_OneUeInfoIndi = "OneUeInfoIndi";
    /**
     * 实时上报ue消息号
     */
    public static final String  KEY_OneUeInfoIndi_id ="103";

    //----------------------------------数据上报-------------------------------------//

    //----------------------------------控制指令------------------------------------//
    /**
     * 获取小区参数
     */
    public static final String KEY_GetRfPara = "GetRfPara";

    /**
     * 配置小区参数
     */
    public static final String KEY_SetRfPara = "SetRfPara";


    /**
     * 配置重定向
     */
    public static final String KEY_SetRedirectionInfo = "SetRedirectionInfo";
    /**
     * 109Cmd消息号对于主控 发送配置命令（Set前缀命令）
     * 和控制命令(Statcell, StopCell，DownloadPack 等命令 )
     * 都会获得 109 CmdAck 返回的响应结果
     */
    public static final String  KEY_109Cmd_id ="109";

    /**
     * 开启小区
     */
    public static final String KEY_StartCell = "StartCell";

    /**
     * 关闭小区
     */
    public static final String KEY_StopCell = "StopCell";
    //----------------------------------控制指令-------------------------------------//


    //----------------------------------查询指令-------------------------------------//


    //----------------------------------查询指令-------------------------------------//
    /**
     * 组包
     * @param type
     * @param parm
     */
    public static String getCmd(String type,Object parm){
        if (CharacterConversionTool.isEmpty(type)){return "";}
        String string="";
        switch (type){
            case HeatBeatAck:
                string=HeatBeatAck;
                break;
            case KEY_SetRfPara:
                SetRfParaBean bean= (SetRfParaBean) parm;
                string=KEY_SetRfPara+bean.toString();
                break;
            case KEY_StartCell:
                string=KEY_StartCell;
                break;
            case KEY_StopCell:
                string=KEY_StopCell;
                break;
            case KEY_SetRedirectionInfo:
                RedirectBean bean1= (RedirectBean) parm;
                string=KEY_SetRedirectionInfo+bean1.toString();
                break;
        }

        return string;
    }


    /**
     * 拆包
     * @param str
     */
    public static Object parseData(String str){

        try {
            if (CharacterConversionTool.isEmpty(str)){return null;}
            String[] words = str.split("\\s+");
//            for(String word : words){
//                System.out.println(word);
//            }
            List<String> list=Arrays.asList(words);
            if (list.size()<0){return null;}
            String id=list.get(0);
            ReporBean bean=null;
            CmdAckBean cmdAckBean=null;
            switch (id){
                case HeartBeatAck_IndiId:
                case KEY_StatusRptIndi_id:
                case KEY_OneUeInfoIndi_id:
                    bean=new ReporBean();
                    bean.setIndiNum(list.get(0));
                    bean.setIndiName(list.get(1));
                    bean.setContentLenth(Integer.parseInt(list.get(2)));
                    bean.setWeiLanName(list.get(3));
                    if (list.size()>4){
                        bean.setContent(list.subList(4,list.size()).toString());
                        
                    }
                    return bean;
                case KEY_109Cmd_id:
                    cmdAckBean=new CmdAckBean();
                    cmdAckBean.setIndiNum(Integer.parseInt(list.get(0)));
                    cmdAckBean.setIndiName(list.get(4));
                    cmdAckBean.setContentLenth(Integer.parseInt(list.get(2)));
                    cmdAckBean.setWeiLanName(list.get(3));
                    if (list.size()>4){
                        cmdAckBean.setContent(list.subList(5,list.size()).toString());
                       
                    }
                    return cmdAckBean;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 从数据中抽取值
     * @param content
     * @param key
     */
    public static String parseValueofKey(String content,String key){
        String value="";
        try {
            if (CharacterConversionTool.isEmpty(content)){return "";}
            //VERSION[wl_0.5.4_rr_2.7.8] BUILD_DATE[Nov 26 2018] TEMP[60] GPS[FAIL] STATUS[CLOSED]
            value=content.substring(content.indexOf(key));
            System.out.print("-----"+value);
            value=value.substring(value.indexOf(key)+key.length()+1,value.indexOf("]"));
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
        return value;
    }

    /**
     * 功率等级
     * @param process
     * @return
     */
    public  static String setDb(int process){
        //5：（30，29，28 ）4：（27，26，25） 3：（24,23,22）2:(21,20,19) 1:(18,17,16) 0:(15,14,13)

        String i="2";
        switch (process){
            case 30:
            case 29:
            case 28:
                return "5";
            case 27:
            case 26:
            case 25:
                return "4";
            case 24:
            case 23:
            case 22:
                return "3";
            case 21:
            case 20:
            case 19:
                return "2";
            case 18:
            case 17:
            case 16:
                return "1";
        }
        return i;
    }

    /**
     * 锁定功率
     * @param pocess
     */
    public static void setXiaoQu(int pocess){
        SetRfParaBean bean=new SetRfParaBean(mcc ,mnc,urfcn ,cpi , lac, cellid ,
                setDb(pocess) ,jiantingpindian ,jiantingcpi);
        String cmd = WcdmaDataHelper.getCmd(WcdmaDataHelper.KEY_SetRfPara,bean);
        try {
			WcdmaManager.getInstance().send(cmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public static String getDb(){
        String gonglv=fasonggonlv.trim();
        if (CharacterConversionTool.isEmpty(gonglv)){
            return "---dbm";
        }
        if ("1".equals(gonglv)){
            gonglv="18dbm";
            return gonglv;
        }else if ("2".equals(gonglv)){
             
            gonglv="21dbm";
            return gonglv;
        }else if ("3".equals(gonglv)){
            gonglv="24dbm";
            return gonglv;
        }else if ("4".equals(gonglv)){
            gonglv="27dbm";
            return gonglv;
        }else if ("5".equals(gonglv)){
            gonglv="30dbm";
            return gonglv;
        }
        
        return gonglv;
    }
    /**
     * 启动小区
     */
    public static void StartCell(){
        
        String cmd = WcdmaDataHelper.getCmd(WcdmaDataHelper.KEY_StartCell,null);
        try {
			WcdmaManager.getInstance().send(cmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * 关闭小区
     */
    public static void StopCell(){
      
        String cmd = WcdmaDataHelper.getCmd(WcdmaDataHelper.KEY_StopCell,null);
        try {
			WcdmaManager.getInstance().send(cmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
