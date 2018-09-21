package com.zhuanche.entity.DriverOrderRecord;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.zhuanche.util.DateUtil;

public class OrderTimeEntity {
	
	private static final long serialVersionUID = -4145690312367498736L;
	public OrderTimeEntity(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
		Date tableDate = DateUtil.beforeNDayDate(new Date(), 1);
		this.checkTime = sdf.format(tableDate);
		this.tableName = "car_biz_driver_record_" + sdf.format(tableDate);
	}
	
	private String checkTime;
	// 要查询的数据表名
	private String tableName; 
	// 下单时间
	private String createDate;
	// 预约时间
	private String bookingDate;
	// 司机出发时间
	private String driverBeginTime;
	// 司机到达时间
	private String driverArriveTime;
	// 司机开始服务时间
	private String driverStartServiceTime;
	// 司机结算时间
	private String driverOrderCoformTime;
	// 司机服务完成时间
	private String driverOrderEndTime;
	// 订单取消时间
	private String orderCancleTime;

	public String getOrderCancleTime() {
		return orderCancleTime;
	}

	public void setOrderCancleTime(String orderCancleTime) {
		this.orderCancleTime = orderCancleTime;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(String bookingDate) {
		this.bookingDate = bookingDate;
	}

	public String getDriverBeginTime() {
		return driverBeginTime;
	}

	public void setDriverBeginTime(String driverBeginTime) {
		this.driverBeginTime = driverBeginTime;
	}

	public String getDriverArriveTime() {
		return driverArriveTime;
	}

	public void setDriverArriveTime(String driverArriveTime) {
		this.driverArriveTime = driverArriveTime;
	}

	public String getDriverStartServiceTime() {
		return driverStartServiceTime;
	}

	public void setDriverStartServiceTime(String driverStartServiceTime) {
		this.driverStartServiceTime = driverStartServiceTime;
	}

	public String getDriverOrderCoformTime() {
		return driverOrderCoformTime;
	}

	public void setDriverOrderCoformTime(String driverOrderCoformTime) {
		this.driverOrderCoformTime = driverOrderCoformTime;
	}

	public String getDriverOrderEndTime() {
		return driverOrderEndTime;
	}

	public void setDriverOrderEndTime(String driverOrderEndTime) {
		this.driverOrderEndTime = driverOrderEndTime;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(Date time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
		this.tableName = "car_biz_driver_record_" + sdf.format(time);
	}
	public String getCheckTime() {
		return checkTime;
	}
	public void setCheckTime(String checkTime) {
		this.checkTime = checkTime;
	}
}
