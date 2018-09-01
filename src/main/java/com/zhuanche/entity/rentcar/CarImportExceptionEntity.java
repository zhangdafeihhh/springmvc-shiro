package com.zhuanche.entity.rentcar;


import com.zhuanche.entity.common.BaseEntity;

public class CarImportExceptionEntity extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String licensePlates;//车牌号
	private String reson;//异常原因
	public String getLicensePlates() {
		return licensePlates;
	}
	public void setLicensePlates(String licensePlates) {
		this.licensePlates = licensePlates;
	}
	public String getReson() {
		return reson;
	}
	public void setReson(String reson) {
		this.reson = reson;
	}
	
	
	
}
