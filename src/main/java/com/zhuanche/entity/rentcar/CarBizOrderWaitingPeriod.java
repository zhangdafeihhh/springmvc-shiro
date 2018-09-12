package com.zhuanche.entity.rentcar;  

import java.io.Serializable;

/** 
 * ClassName: CarBizOrderWaitingPeriodVO 
 * date: 2017年1月16日 上午10:25:24 
 * 
 * @author zhulingling 
 * @version  
 * @since JDK 1.6 
 */
public class CarBizOrderWaitingPeriod implements Serializable{
	private static final long serialVersionUID = 1L;
	private String startWaitingTime ;
	private String endWaitingTime ;
	private String orderNo ;
	public String getStartWaitingTime() {
		return startWaitingTime;
	}
	public void setStartWaitingTime(String startWaitingTime) {
		this.startWaitingTime = startWaitingTime;
	}
	public String getEndWaitingTime() {
		return endWaitingTime;
	}
	public void setEndWaitingTime(String endWaitingTime) {
		this.endWaitingTime = endWaitingTime;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	
}
  