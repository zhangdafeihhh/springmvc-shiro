package com.zhuanche.common.dutyEnum;

public enum EnumDriverDutyTimeFlag {
	DRIVEDUTYPEAKTIMES_ONE("m_1","早高峰"),
	DRIVEDUTYPEAKTIMES_TWO("m_2","晚高峰"),
	DRIVEDUTYPEAKTIMES_THREE("m_3","其他时段1"),
	DRIVEDUTYPEAKTIMES_FOUR("m_4","其他时段2");
	
	
	private String key;
	private String value;
	
	EnumDriverDutyTimeFlag(String key, String value) {
		this.key = key;
		this.value = value;
	}
	public String getValue() {
		return value;
	}

	public String getKey() {
		return key;
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
