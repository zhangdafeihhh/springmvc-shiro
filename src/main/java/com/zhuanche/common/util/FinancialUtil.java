package com.zhuanche.common.util;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class FinancialUtil{
	private static final String NumberChars           =   "0123456789";
	private static final String NumCharacterChars =   "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static final SecureRandom rnd               = new SecureRandom();
	
	/**生成长编号（18位）**/
	public static String genLongNum( NumType type ) {
		StringBuffer sb = new StringBuffer(16);
		sb.append(type.value());
		sb.append( new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) );
		for(int i=0;i<4;i++) {
			char c = NumberChars.charAt(  rnd.nextInt(NumberChars.length())  );
			sb.append(c);
		}
		return sb.toString();
	}
	/**生成短编号（9位）**/
	public static String genShortNum( NumType type ) {
		StringBuffer sb = new StringBuffer(8);
		sb.append(type.value());
		for(int i=0;i<8;i++) {
			char c = NumCharacterChars.charAt(  rnd.nextInt(NumCharacterChars.length())  );
			sb.append(c);
		}
		return sb.toString();
	}
	
	public static enum NumType{
		/**商品编号**/
		GOODS_SP("SP");
		  
		private String type;
		NumType(String type) {
			this.type=type;
		}
		String value() {
			return this.type;
		}
	}

	
	
	
	//-----------------------------------------------------------------------------------------------------------------debug and test
	public static void main(String[] args) {
		for(int i=0;i<200;i++) {
			System.out.println( "--------------------------------------------------------------------------------------------------");
			System.out.println( "商品编号(长)："+ FinancialUtil.genLongNum(NumType.GOODS_SP));
			
		}
	}
}