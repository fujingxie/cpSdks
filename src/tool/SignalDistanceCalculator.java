package tool;

public class SignalDistanceCalculator {
    public static int ssbConvertPoint(int arfcn){
        int point=2000;
        if (arfcn>0&&arfcn<=599){
            //b1
            return 1950;
        }
        if (arfcn>1200&&arfcn<1949){
            //b3
            return 1745;
        }
        if (arfcn>=36200&&arfcn<=36349){
            //b34
            return 2000;
        }
        if (arfcn>38250&&arfcn<38649){
            //b39
            return 1900;
        }
        if (arfcn>38650&&arfcn<39649){
            //b40
            return 2350;
        }
        if (arfcn>=39650&&arfcn<=41589){
            //b41
            return 2595;
        }

        if (arfcn>=37750&&arfcn<=38249){
            //b38
            return 2595;
        }
        if (arfcn>0&&arfcn<=599){
            //b1
            return 1950;
        }

        if (arfcn>=42000&&arfcn<=434000){
            //N1
            return 1950;
        }
        if (arfcn>=151600&&arfcn<=160600){
            //N28
            return 1950;
        }
        if (arfcn>=499200&&arfcn<=537999){
            //N41
            return 1950;
        }
        if (arfcn>=620000&&arfcn<=653333){
            //N78
            return 2600;
        }
        if (arfcn>=693334&&arfcn<=733333){
            //N79
            return 3600;
        }
        return point;
    }
    // 计算4G FDD的距离
    public static double calculate4GFDD(int P, int F) {
        double maxStrength = 164; // 最大场强
        double G = 35;            // 增益因子
        double L = (maxStrength - P) / 2; // 计算损耗L
        return 1000 * Math.pow(10, (L + G - 32.5 - 20 * Math.log10(F)) / 20);
    }

    // 计算4G TDD的距离
    public static double calculate4GTDD(int P, int F) {
        double maxStrength = 180; // 最大场强
        double G = 35;            // 增益因子
        double L = (maxStrength - P) / 2; // 计算损耗L
        return 1000 * Math.pow(10, (L + G - 32.5 - 20 * Math.log10(F)) / 20);
    }

    // 计算5G的距离
    public static double calculate5G(int P, int F) {
        double maxStrength = 90;  // 最大场强
        double G = 23;            // 增益因子
        double L = (100 - P);     // 计算损耗L
        return 1000 * Math.pow(10, (L + G - 32.5 - 20 * Math.log10(F)) / 20);
    }

    public static void main(String[] args) {
        // 假设你获得了场强值
        int P4GFDD = 86;  // 4G FDD 场强
        int F4GFDD = 1800; // 4G FDD 频率 (MHz)
        System.out.println("4G FDD 距离: " + calculate4GFDD(P4GFDD, F4GFDD) + " 米");

        int P4GTDD = 132;  // 4G TDD 场强
        int F4GTDD = 1800; // 4G TDD 频率 (MHz)
        System.out.println("4G TDD 距离: " + calculate4GTDD(P4GTDD, F4GTDD) + " 米");

        int P5G = 35;      // 5G 场强
        int F5G = 2600;    // 5G 频率 (MHz)
        System.out.println("5G 距离: " + calculate5G(P5G, F5G) + " 米");
    }

    public static String imsi2MobilePhoneNumberHead(String imsi) {

        String mobilePhoneNumberHead = "";

        // 如参有效性检查逻辑
        if (imsi == null || imsi.length() < 15)
            return mobilePhoneNumberHead;

        // 判断并解析出IMSI对应的手机号码
        if (imsi.charAt(3) == '0' && (imsi.charAt(4) == '1' || imsi.charAt(4) == '6' || imsi.charAt(4) == '9'))// 该用户为联通用户
        {
            if (imsi.charAt(4) == '9') {
                mobilePhoneNumberHead = "176";
            }else{
                if (imsi.charAt(9) == '0' || imsi.charAt(9) == '1')// 130用户
                {
                    mobilePhoneNumberHead = "130";
                } else if (imsi.charAt(9) == '2')// 132用户
                {
                    mobilePhoneNumberHead = "132";
                } else if (imsi.charAt(9) == '3')// 156用户
                {
                    mobilePhoneNumberHead = "156";
                } else if (imsi.charAt(9) == '4')// 155用户
                {
                    mobilePhoneNumberHead = "155";
                } else if (imsi.charAt(9) == '6')// 186用户
                {
                    mobilePhoneNumberHead = "186";
                } else if (imsi.charAt(9) == '5')// 185用户
                {
                    mobilePhoneNumberHead = "185";
                } else if (imsi.charAt(9) == '7')// 132用户
                {
                    mobilePhoneNumberHead = "136";
                    // 131用户
                } else if (imsi.charAt(9) == '9') {
                    mobilePhoneNumberHead = "131";
                }
            }
            mobilePhoneNumberHead += imsi.charAt(8);
            mobilePhoneNumberHead += imsi.charAt(5);
            mobilePhoneNumberHead += imsi.charAt(6);
            mobilePhoneNumberHead += imsi.charAt(7);
        }
        else if (imsi.charAt(3) == '0' && (imsi.charAt(4) == '0' || imsi.charAt(4) == '2'
                || imsi.charAt(4) == '7' || imsi.charAt(4) == '8'))// 该用户为移动用户(8未解析)
        {
            if (imsi.charAt(4) == '0')// 13X用户
            {
                mobilePhoneNumberHead += "13";
                if (imsi.charAt(8) == '5' || imsi.charAt(8) == '6'
                        || imsi.charAt(8) == '7' || imsi.charAt(8) == '8'
                        || imsi.charAt(8) == '9') {
                    mobilePhoneNumberHead += imsi.charAt(8);
                    mobilePhoneNumberHead += '0';
                    mobilePhoneNumberHead += imsi.charAt(5);
                    mobilePhoneNumberHead += imsi.charAt(6);
                    mobilePhoneNumberHead += imsi.charAt(7);
                } else if (imsi.charAt(8) == '0' || imsi.charAt(8) == '1'
                        || imsi.charAt(8) == '2' || imsi.charAt(8) == '3'
                        || imsi.charAt(8) == '4') {
                    mobilePhoneNumberHead += String.valueOf(imsi.charAt(8)-48 + 5);
                    mobilePhoneNumberHead += imsi.charAt(9);
                    mobilePhoneNumberHead += imsi.charAt(5);
                    mobilePhoneNumberHead += imsi.charAt(6);
                    mobilePhoneNumberHead += imsi.charAt(7);
                }
            } else if (imsi.charAt(4) == '2') {
                if (imsi.charAt(5) == '0')// 134用户
                {
                    mobilePhoneNumberHead += "134";
                }
                if (imsi.charAt(5) == '1')// 151用户
                {
                    mobilePhoneNumberHead += "151";
                } else if (imsi.charAt(5) == '2')// 152用户
                {
                    mobilePhoneNumberHead += "152";
                } else if (imsi.charAt(5) == '3')// 150用户
                {
                    mobilePhoneNumberHead += "150";
                } else if (imsi.charAt(5) == '7')// 187用户
                {
                    mobilePhoneNumberHead += "187";
                } else if (imsi.charAt(5) == '8')// 158用户
                {
                    mobilePhoneNumberHead += "158";
                } else if (imsi.charAt(5) == '9')// 159用户
                {
                    mobilePhoneNumberHead += "159";
                }else if (imsi.charAt(5) == '6')// 182用户
                {
                    mobilePhoneNumberHead += "182";
                }
                else if (imsi.charAt(5) == '5')// 183用户
                {
                    mobilePhoneNumberHead += "183";
                }
                else if (imsi.charAt(5) == '4')// 184用户
                {
                    mobilePhoneNumberHead += "184";
                }
                mobilePhoneNumberHead += imsi.charAt(6);
                mobilePhoneNumberHead += imsi.charAt(7);
                mobilePhoneNumberHead += imsi.charAt(8);
                mobilePhoneNumberHead += imsi.charAt(9);
            } else if (imsi.charAt(4) == '7') {
                if (imsi.charAt(5) == '5'){	// 178用户
                    mobilePhoneNumberHead += "178";
                } else if (imsi.charAt(5) == '7')// 157用户
                {
                    mobilePhoneNumberHead += "157";
                } else if (imsi.charAt(5) == '8')// 188用户
                {
                    mobilePhoneNumberHead += "188";
                } else if (imsi.charAt(5) == '9')// 147用户
                {
                    mobilePhoneNumberHead += "147";
                }
                mobilePhoneNumberHead += imsi.charAt(6);
                mobilePhoneNumberHead += imsi.charAt(7);
                mobilePhoneNumberHead += imsi.charAt(8);
                mobilePhoneNumberHead += imsi.charAt(9);
            }

        }
        else if (imsi.charAt(4) == '3' || imsi.charAt(4) == '5'
                || (imsi.charAt(3) == '1' && imsi.charAt(4) == '1')) //电信
        {

        }
        else if ((imsi.charAt(3) == '2' && imsi.charAt(4) == '0')) //铁通
        {

        }
        return mobilePhoneNumberHead;
    }
}
