package com.zhuanche.entity.bigdata;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * bi_saas_supplier_rank_data
 * @author 
 */
public class BiSaasSupplierRankData implements Serializable {
    /**
     * 自增ID
     */
    private Integer id;

    /**
     * 数据日期(开始日期)
     */
    private String startTime;

    /**
     * 数据日期(结束日期)
     */
    private String endTime;

    /**
     * 数据日期（月）
     */
    private String dataMonth;

    /**
     * 结算日期
     */
    private Integer settleDay;

    /**
     * 供应商id
     */
    private Integer supplierId;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 司机端金额
     */
    private BigDecimal channelDiscountDriver;

    /**
     * 运营司机基数
     */
    private Integer factOrderDriverCnt;

    /**
     * 有效差评单量
     */
    private Integer badEvaluateNum;

    /**
     * 剔除渠道完单数
     */
    private Integer orderCntNotchannel;

    /**
     * 有效差评率
     */
    private BigDecimal badEvaluateRate;

    /**
     * 运营司机数环比量
     */
    private Integer factOrderDriverCntHb;

    /**
     * 运营司机数环比率(增幅系数)
     */
    private BigDecimal factOrderDriverCntHbRate;

    /**
     * 规模分数
     */
    private Integer scaleScore;

    /**
     * 效率分数
     */
    private Integer efficiencyScore;

    /**
     * 服务分数
     */
    private Integer serviceScore;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getDataMonth() {
        return dataMonth;
    }

    public void setDataMonth(String dataMonth) {
        this.dataMonth = dataMonth;
    }

    public Integer getSettleDay() {
        return settleDay;
    }

    public void setSettleDay(Integer settleDay) {
        this.settleDay = settleDay;
    }

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

    public BigDecimal getChannelDiscountDriver() {
        return channelDiscountDriver;
    }

    public void setChannelDiscountDriver(BigDecimal channelDiscountDriver) {
        this.channelDiscountDriver = channelDiscountDriver;
    }

    public Integer getFactOrderDriverCnt() {
        return factOrderDriverCnt;
    }

    public void setFactOrderDriverCnt(Integer factOrderDriverCnt) {
        this.factOrderDriverCnt = factOrderDriverCnt;
    }

    public Integer getBadEvaluateNum() {
        return badEvaluateNum;
    }

    public void setBadEvaluateNum(Integer badEvaluateNum) {
        this.badEvaluateNum = badEvaluateNum;
    }

    public Integer getOrderCntNotchannel() {
        return orderCntNotchannel;
    }

    public void setOrderCntNotchannel(Integer orderCntNotchannel) {
        this.orderCntNotchannel = orderCntNotchannel;
    }

    public BigDecimal getBadEvaluateRate() {
        return badEvaluateRate;
    }

    public void setBadEvaluateRate(BigDecimal badEvaluateRate) {
        this.badEvaluateRate = badEvaluateRate;
    }

    public Integer getFactOrderDriverCntHb() {
        return factOrderDriverCntHb;
    }

    public void setFactOrderDriverCntHb(Integer factOrderDriverCntHb) {
        this.factOrderDriverCntHb = factOrderDriverCntHb;
    }

    public BigDecimal getFactOrderDriverCntHbRate() {
        return factOrderDriverCntHbRate;
    }

    public void setFactOrderDriverCntHbRate(BigDecimal factOrderDriverCntHbRate) {
        this.factOrderDriverCntHbRate = factOrderDriverCntHbRate;
    }

    public Integer getScaleScore() {
        return scaleScore;
    }

    public void setScaleScore(Integer scaleScore) {
        this.scaleScore = scaleScore;
    }

    public Integer getEfficiencyScore() {
        return efficiencyScore;
    }

    public void setEfficiencyScore(Integer efficiencyScore) {
        this.efficiencyScore = efficiencyScore;
    }

    public Integer getServiceScore() {
        return serviceScore;
    }

    public void setServiceScore(Integer serviceScore) {
        this.serviceScore = serviceScore;
    }
}