package com.zhuanche.entity.rentcar;

import java.util.List;

public class DriverEntityBusVo extends CarBizDriverInfo {


	private List<Integer> ids;
	
	private List<Integer> licensePlatesList;
	
	private String color;

	public List<Integer> getIds() {
		return ids;
	}

	public void setIds(List<Integer> ids) {
		this.ids = ids;
	}

	public List<Integer> getLicensePlatesList() {
		return licensePlatesList;
	}

	public void setLicensePlatesList(List<Integer> licensePlatesList) {
		this.licensePlatesList = licensePlatesList;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
	

}
