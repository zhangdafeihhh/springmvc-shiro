package com.zhuanche.common.dutyEnum;

public enum EnumDriverDutyPeakTimes {
	DRIVEDUTYPEAKTIMES_ONE(1,"早高峰"),
	DRIVEDUTYPEAKTIMES_TWO(2,"晚高峰"),
	DRIVEDUTYPEAKTIMES_THREE(3,"其他时段1"),
	DRIVEDUTYPEAKTIMES_FOUR(4,"其他时段2");
	
	public int value;
	private String key;
	
	private EnumDriverDutyPeakTimes(int value, String key) {
		this.value = value;
		this.key = key;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	private static EnumDriverDutyPeakTimes[] enums  = EnumDriverDutyPeakTimes.values();
	public static String getKey(int value) {
		for (EnumDriverDutyPeakTimes keyEnum: enums) {
			if (value == keyEnum.value) {
				return keyEnum.getKey();
			}
		}
		return null;
	}
	
	public static Integer getValue(String key) {
		for (EnumDriverDutyPeakTimes keyEnum: enums) {
			if (key.equals(keyEnum.getKey())) {
				return keyEnum.value;
			}
		}
		return null;
	}

}