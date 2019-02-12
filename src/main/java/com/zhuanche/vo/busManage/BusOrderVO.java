package com.zhuanche.vo.busManage;

import java.util.Date;

import lombok.Data;

@Data
public class BusOrderVO { // carGroupName riderCount factDate factStartAddr factEndAddr supplierName

	// 订单id
	private Integer orderId;
	// 订单号
	private String orderNo;
	// 订单类型(1:普通用户订单, 2:企业用户订单)
	private Integer type;
	// 订单类型 0 安卓 1 | IOS 2 客服后台创建 | 3机构后台创建
	private Integer orderType;
	// 是否往返 0-否（即单程） 1-是（即往返）
	private Integer isReturn;
	// 订单类型ID
	private Integer serviceTypeId;
	// 订单类型名称
	private String serviceTypeName;
	// 预订人id
	private Integer bookingUserId;
	// 预订人电话
	private String bookingUserPhone;
	// 预定车型
	private String bookingGroupid;
	// 预定车型名称
	private String bookingGroupName;
	// 实际车型ID
	private Integer carGroupId;
	// 实际车型名称
	private String carGroupName;
	// 预约用车时间
	private Date bookingDate;
	// 预估里程（单位/公里）
	private Double estimateDistance;
	// 预收费用
	private Double estimatedAmountYuan;
	// 下单时间
	private Date createDate;
	// 城市id
	private Integer cityId;
	private String cityName;
	// 乘车人姓名
	private String riderName;
	// 乘车人手机号
	private String riderPhone;
	// 上车地点
	private String bookingStartAddr;
	// 预定开始地点短地址
	private String bookingStartAddrShort;
	// 预定上车坐标点
	private String bookingStartPoint;
	// 下车地点
	private String bookingEndAddr;
	// 预定结束地点短地址
	private String bookingEndAddrShort;
	// 预定下车坐标点
	private String bookingEndPoint;
	// 实际下车时间
	private Date factEndDate;
	/*
	 * 
	 * 订单状态：10100, 预定中/待支付 10103, 支付成功/待接单 10105, 待服务 10200, 已出发 10205, 已到达 10300,
	 * 服务中 10305, 服务结束 10400, 待结算 10402,支付中 10403,扣款中 10404,后付 10405, 已结算 10500, 已完成
	 * 10505, 订单异议 10600, 已取消
	 */
	private Integer status;
	// 取消原因
	private String memo;
	// 车牌号
	private String licensePlates;
	// 司机ID
	private Integer driverId;
	// 司机姓名
	private String driverName;
	// 司机手机号
	private String driverPhone;
	// 本单司机评分
	private String driverScore;
	// 付款类型（费用类型：1 预付费；-1 后付费）
	private Integer companyType;
	// 企业ID
	private Integer companyId;
	// 企业名称
	private String companyName;
	// 企业折扣
	private Double percent;
	//供应商名称
	private String supplierName;

}
