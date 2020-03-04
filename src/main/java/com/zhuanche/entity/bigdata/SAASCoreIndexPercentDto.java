package com.zhuanche.entity.bigdata;

/**
 * @Author yxp
 * @Date 2020.03.04
 * @Version 1.0
 */
public class SAASCoreIndexPercentDto {

    private String ciFactEndNum;//安装ci完单总量
    private String ciDriverNum;//安装ci司机数
    private String driverNum;//总司机数
    private String completeOrderAmount;//总完单量
    private String ciOrderNum;//安装ci的下单量
    private String ciOrderCntNotChannel;//安装ci的完单量，剔除渠道
    private String ciFactOverAmount;//安装ci的司机端完单流水
    private String factOverAmount;//司机端完单流水
    private String ciBadEvaluateAllNum;//安装ci的总差评单数
    private String ciBadEvaluateNum;//安装ci的有效差评单数

    private String completeOrderAmountPerecnt;//完成单量
    private String criticismRatePercent;//差评率
    private String incomeAmountPercent;//总流水
    private String orderPerVehiclePercent;//车均订单
    private String incomePerVehiclePercent;//车均流水
    private String badEvaluateAllNumPercent; //差评单量
    private String badEvaluateNumPercent; //有效差评
    private String criticismRate;//差评率


    public String getCompleteOrderAmountPerecnt() {
        return completeOrderAmountPerecnt;
    }

    public String getCiFactEndNum() {
        return ciFactEndNum;
    }

    public void setCiFactEndNum(String ciFactEndNum) {
        this.ciFactEndNum = ciFactEndNum;
    }

    public String getCiDriverNum() {
        return ciDriverNum;
    }

    public void setCiDriverNum(String ciDriverNum) {
        this.ciDriverNum = ciDriverNum;
    }

    public String getDriverNum() {
        return driverNum;
    }

    public void setDriverNum(String driverNum) {
        this.driverNum = driverNum;
    }

    public String getCompleteOrderAmount() {
        return completeOrderAmount;
    }

    public void setCompleteOrderAmount(String completeOrderAmount) {
        this.completeOrderAmount = completeOrderAmount;
    }

    public void setCompleteOrderAmountPerecnt(String completeOrderAmountPerecnt) {
        this.completeOrderAmountPerecnt = completeOrderAmountPerecnt;
    }

    public String getCriticismRatePercent() {
        return criticismRatePercent;
    }

    public void setCriticismRatePercent(String criticismRatePercent) {
        this.criticismRatePercent = criticismRatePercent;
    }

    public String getIncomeAmountPercent() {
        return incomeAmountPercent;
    }

    public void setIncomeAmountPercent(String incomeAmountPercent) {
        this.incomeAmountPercent = incomeAmountPercent;
    }

    public String getOrderPerVehiclePercent() {
        return orderPerVehiclePercent;
    }

    public void setOrderPerVehiclePercent(String orderPerVehiclePercent) {
        this.orderPerVehiclePercent = orderPerVehiclePercent;
    }

    public String getIncomePerVehiclePercent() {
        return incomePerVehiclePercent;
    }

    public void setIncomePerVehiclePercent(String incomePerVehiclePercent) {
        this.incomePerVehiclePercent = incomePerVehiclePercent;
    }

    public String getBadEvaluateAllNumPercent() {
        return badEvaluateAllNumPercent;
    }

    public void setBadEvaluateAllNumPercent(String badEvaluateAllNumPercent) {
        this.badEvaluateAllNumPercent = badEvaluateAllNumPercent;
    }

    public String getBadEvaluateNumPercent() {
        return badEvaluateNumPercent;
    }

    public void setBadEvaluateNumPercent(String badEvaluateNumPercent) {
        this.badEvaluateNumPercent = badEvaluateNumPercent;
    }

    public String getCiFactOverAmount() {
        return ciFactOverAmount;
    }

    public void setCiFactOverAmount(String ciFactOverAmount) {
        this.ciFactOverAmount = ciFactOverAmount;
    }

    public String getFactOverAmount() {
        return factOverAmount;
    }

    public void setFactOverAmount(String factOverAmount) {
        this.factOverAmount = factOverAmount;
    }

    public String getCiOrderNum() {
        return ciOrderNum;
    }

    public void setCiOrderNum(String ciOrderNum) {
        this.ciOrderNum = ciOrderNum;
    }

    public String getCiBadEvaluateAllNum() {
        return ciBadEvaluateAllNum;
    }

    public void setCiBadEvaluateAllNum(String ciBadEvaluateAllNum) {
        this.ciBadEvaluateAllNum = ciBadEvaluateAllNum;
    }

    public String getCiBadEvaluateNum() {
        return ciBadEvaluateNum;
    }

    public void setCiBadEvaluateNum(String ciBadEvaluateNum) {
        this.ciBadEvaluateNum = ciBadEvaluateNum;
    }

    public String getCiOrderCntNotChannel() {
        return ciOrderCntNotChannel;
    }

    public void setCiOrderCntNotChannel(String ciOrderCntNotChannel) {
        this.ciOrderCntNotChannel = ciOrderCntNotChannel;
    }

    public String getCriticismRate() {
        return criticismRate;
    }

    public void setCriticismRate(String criticismRate) {
        this.criticismRate = criticismRate;
    }
}
