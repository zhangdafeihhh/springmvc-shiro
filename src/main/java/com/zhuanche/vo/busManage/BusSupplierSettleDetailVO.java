package com.zhuanche.vo.busManage;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;

/**
 * @program: mp-manage
 * @description: 巴士订单列表对应的实体
 * @author: niuzilian
 * @create: 2018-12-17 20:28
 **/
public class BusSupplierSettleDetailVO {
    /**
     * 供应商id
     */
   private Integer supplierId;
    /**
     * 供应商名称
     */
   private String supplierName;
    /**
     * 城市名称
     */
   private String cityName;
    /**
     * 类型
     */
   private Integer type;
    /**
     * 账单主键（结算编号）
     */
   private String supplierBillId;
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * 到期付款日期
     */
    private String aaa;//TODO
    /**
     * 订单金额
     */
    private BigDecimal billAmount;
    /**
     * 结算金额
     */
    private BigDecimal bbb;//TODO

    /**
     * 结算类型
     */
    private Integer settleType;
    /**
     * 分佣方式
     */
    private Integer shareWay;
    /**
     * 分佣类型
     */
    private Integer shareType;
    /**
     * 账单状态
     */
    private Integer status;

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getSupplierBillId() {
        return supplierBillId;
    }

    public void setSupplierBillId(String supplierBillId) {
        this.supplierBillId = supplierBillId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getAaa() {
        return aaa;
    }

    public void setAaa(String aaa) {
        this.aaa = aaa;
    }

    public BigDecimal getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(BigDecimal billAmount) {
        this.billAmount = billAmount;
    }


    public Integer getSettleType() {
        return settleType;
    }

    public void setSettleType(Integer settleType) {
        this.settleType = settleType;
    }

    public Integer getShareWay() {
        return shareWay;
    }

    public void setShareWay(Integer shareWay) {
        this.shareWay = shareWay;
    }

    public Integer getShareType() {
        return shareType;
    }

    public void setShareType(Integer shareType) {
        this.shareType = shareType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
