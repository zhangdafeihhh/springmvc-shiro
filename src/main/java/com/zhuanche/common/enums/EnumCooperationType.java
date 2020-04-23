package com.zhuanche.common.enums;


/** 
 * ClassName: EnumCooperationType
 */
public enum EnumCooperationType {
	//5-首约自营,7-企业合作加盟,9-个人加盟,11-测试加盟,13-三方平台加盟
	//测试环境数据：4-测试，6-长春加盟，8-测试0111，10-加盟001
	COOPERATION_TYPE_SHOUYUEZIYING(5, "首约自营"),
	COOPERATION_TYPE_QIYEHEZUOJIAMENG(7, "企业合作加盟"),
	COOPERATION_TYPE_GERENJIAMENG(9, "个人加盟"),
	COOPERATION_TYPE_CESHIJIAMENG(11, "测试加盟"),
	COOPERATION_TYPE_SANFANGPINGTAIJIAMENG(13, "三方平台加盟"),
	COOPERATION_TYPE_CESHI(4, "测试"),
	COOPERATION_TYPE_CHANGCHUNJIAMENG(6, "长春加盟"),
	COOPERATION_TYPE_CESHI0111(8, "测试0111"),
	COOPERATION_TYPE_JIAMENG011(10, "加盟001");

	public int value;
	private String i18n;

	EnumCooperationType(int value, String i18n) {
		this.value = value;
		this.i18n = i18n;
	}

	//根据key获取枚举
	public static String getEnumByKey(int value){
		for(EnumCooperationType temp:EnumCooperationType.values()){
			if(temp.getValue() == value){
				return temp.getI18n();
			}
		}
		return null;
	}
	
	public String getI18n() {
		return this.i18n;
	}
	public int getValue() {
		return this.value;
	}
	
	public String getValueStr() {
		return String.valueOf(this.value);
	}

	public static void main(String[] args) {
	}
}
  