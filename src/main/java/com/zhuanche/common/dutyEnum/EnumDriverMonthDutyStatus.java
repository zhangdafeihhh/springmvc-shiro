package com.zhuanche.common.dutyEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EnumDriverMonthDutyStatus {
	DRIVERMONTHDUTYSTATUS_WORKING(1,"上班"),
	DRIVERMONTHDUTYSTATUS_REST(2,"休息"),
	DRIVERMONTHDUTYSTATUS_FALLS(3,"调休"),
	DRIVERMONTHDUTYSTATUS_REPLAY(4,"替班"),
	DRIVERMONTHDUTYSTATUS_CHANGE(5,"换班"),
	DRIVERMONTHDUTYSTATUS_EARLY(6,"早班"),
	DRIVERMONTHDUTYSTATUS_NIGHT(7,"晚班"),
	DRIVERMONTHDUTYSTATUS_PERSONAL(8,"事假"),
	DRIVERMONTHDUTYSTATUS_SICK(9,"病假");
	
	public int value;
	private String status;
	
	private EnumDriverMonthDutyStatus(int value, String status) {
		this.value = value;
		this.status = status;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	private static EnumDriverMonthDutyStatus[] statusEnums  = EnumDriverMonthDutyStatus.values();

	private static Map<String,String> cacheMap = new HashMap<>();
	public static String getStatus(int value) {
		if(cacheMap.isEmpty()){
			for (EnumDriverMonthDutyStatus statusEnum: statusEnums) {
				cacheMap.put("cache_"+statusEnum.value,statusEnum.getStatus());
			}
		}

		return cacheMap.get("cache_"+value);
	}
	
	public static Integer getValue(String status) {
		for (EnumDriverMonthDutyStatus statusEnum: statusEnums) {
			if (status.equals(statusEnum.getStatus())) {
				return statusEnum.value;
			}
		}
		return null;
	}
	
	public static List<Map<String,Object>> getStatusMap() {
		List<Map<String,Object>> statusMapList = new ArrayList<Map<String,Object>>();
		for (EnumDriverMonthDutyStatus statusEnum: statusEnums) {
			Map<String,Object> statusMap = new HashMap<String,Object>();
			statusMap.put("status", statusEnum.value);
			statusMap.put("text", statusEnum.getStatus());
			statusMapList.add(statusMap);
		}
		return statusMapList;
	}
	
}
