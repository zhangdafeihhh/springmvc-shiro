package com.zhuanche.common.dutyEnum;

public enum EnumDriverDutyPeakTimes {
	DRIVEDUTYPEAKTIMES_ONE(1,"A:早高峰"),
	DRIVEDUTYPEAKTIMES_TWO(2,"B:晚高峰"),
	DRIVEDUTYPEAKTIMES_THREE(3,"X1:其他时段1"),
	DRIVEDUTYPEAKTIMES_FOUR(4,"X2:其他时段2"),
	DRIVEDUTYPEAKTIMES_FIVE(5,"C:非工作日早班"),
	DRIVEDUTYPEAKTIMES_SIX(6,"D:非工作日晚班"),
	DRIVEDUTYPEAKTIMES_SEV(7,"E:无障碍自由班");
	
	private int value;
	private String key;
	
	EnumDriverDutyPeakTimes(int value, String key) {
		this.value = value;
		this.key = key;
	}
	public int getValue() {
		return value;
	}
	public String getKey() {
		return key;
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
