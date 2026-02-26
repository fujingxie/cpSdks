package wcdma;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String msg="101 HeartBeat 101 94_wcdma S12345678 VERSION[wl_0.5.4_rr_2.7.8] BUILD_DATE[Nov 26 2018] TEMP[60] GPS[FAIL] STATUS[CLOSED]";
		 String[] words = msg.split("\\s+");
         for(String word : words){
             System.out.println(word);
         }
		Object obj = WcdmaDataHelper.parseData(msg);
		if(obj==null) {
			 System.out.println("obj==null" );
		}else {
			 System.out.println("obj!=null" );
		}
	}

}
