package com.zhuanche.entity.risk;



import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
@Data
public class RiskCarManagerOrderComplainEntity   implements Serializable{

    private static final long serialVersionUID = -1962168490971137026L;
    /**
     * 主键
     */
    private String riskOrderId;

    /**
     * 订单号
     */
    private String  orderNo;
    
    /**
     * 订单ID
     */
    private String orderId;
    /**
     * 司机ID
     */
    private String driverId;
    /**
     * 司机姓名
     */
    private String driverName;
    /**
     * 司机手机号
     */
    private String driverPhone;
    /**
     * 订单风控状态1：待处理,2:扣除成功，3：扣除失败
     */
    private String orderRiskStatus;
    /**
     * 申诉状态，1:未申诉；2:待处理;3:申诉成功;3申诉驳回
     */
    private Integer appealStatus;
    /**
     * 申诉处理时间
     */
    private String appealProcessAt;
    /**
     * 申诉处理人
     */
    private String appealProcessBy;
    
    /**
     * 风控处理时间
     */
    private String createAt;
    private String createAtStart;//开始查询
    private String createAtEnd;//结束查询

    /**
     * 订单完成时间
     */
    private String orderEndDate;
    private String orderEndDateStart;//开始查询
    private String orderEndDateEnd;//结束查询

    /**
     * 费用总计
     */
    private BigDecimal totalCost;
   

    /**
     * 供应商ID
     */
    private String supplierId;
    
    /**
     * 供应商名称
     */
    private String leasingCompany;
    
    /**
     * 司机城市
     */
    private String cityId;

    /**
     * 司机城市名字
     */
    private String driverCityName;

    /**
     * 权限 供应商IDS
     */
    private String supplierIds;
    /**
     * 权限 司机城市
     */
    private String driverCityIds;

	private String ruleName;



}
