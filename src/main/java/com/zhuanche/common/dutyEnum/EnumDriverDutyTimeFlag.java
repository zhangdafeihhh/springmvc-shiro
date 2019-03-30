package com.zhuanche.common.dutyEnum;

public enum EnumDriverDutyTimeFlag {
	DRIVEDUTYPEAKTIMES_ONE("m_1","A:早高峰"),
	DRIVEDUTYPEAKTIMES_TWO("m_2","B:晚高峰"),
	DRIVEDUTYPEAKTIMES_THREE("m_3","X1:其他时段1"),
	DRIVEDUTYPEAKTIMES_FOUR("m_4","X2:其他时段2"),
	DRIVEDUTYPEAKTIMES_FIVE("m_5","C:非工作日早班"),
	DRIVEDUTYPEAKTIMES_SIX("m_6","D:非工作日晚班"),
	DRIVEDUTYPEAKTIMES_SEV("m_7","E:无障碍自由班");
	
	
	private String key;
	public String value;
	
	private EnumDriverDutyTimeFlag(String key, String value) {
		this.key = key;
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	private static EnumDriverDutyTimeFlag[] enums  = EnumDriverDutyTimeFlag.values();
	public static String getKey(String value) {
		for (EnumDriverDutyTimeFlag keyEnum: enums) {
			if (value.equals(keyEnum.value)) {
				return keyEnum.getKey();
			}
		}
		return null;
	}
	
	public static String getValue(String key) {
		for (EnumDriverDutyTimeFlag keyEnum: enums) {
			if (key.equals(keyEnum.getKey())) {
				return keyEnum.value;
			}
		}
		return null;
	}

}
