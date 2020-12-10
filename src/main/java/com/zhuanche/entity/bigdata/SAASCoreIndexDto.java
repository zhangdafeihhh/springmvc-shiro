package com.zhuanche.entity.bigdata;

/**
 * @Author fanht
 * @Description  迁移大数据首页统计指标
 * @Date 2019/3/29 下午5:40
 * @Version 1.0
 */
public class SAASCoreIndexDto {

    private String registVehicleAmount;//注册车辆
    private String activeVehicleAmount;//激活车辆
    private String onlineVehicleAvg;//日均在线车辆
    private String serviceVehicleAvg;//日均运营车辆
    private String completeOrderAmount;//完成单量
    private String incomeAmount;//总流水
    private String orderPerVehicle;//车均订单
    private String incomePerVehicle;//车均流水
    private String criticismRate;//差评率
    private String orderCntNotchannel; //非渠道单

    private String badEvaluateNum; //有效差评

    private String badEvaluateAllNum; //差评单量
    //司机上线时长（分钟）
    private String onlineDriverTime;

    public String getRegistVehicleAmount() {
        return registVehicleAmount;
    }

    public void setRegistVehicleAmount(String registVehicleAmount) {
        this.registVehicleAmount = registVehicleAmount;
    }

    public String getActiveVehicleAmount() {
        return activeVehicleAmount;
    }

    public void setActiveVehicleAmount(String activeVehicleAmount) {
        this.activeVehicleAmount = activeVehicleAmount;
    }

    public String getOnlineVehicleAvg() {
        return onlineVehicleAvg;
    }

    public void setOnlineVehicleAvg(String onlineVehicleAvg) {
        this.onlineVehicleAvg = onlineVehicleAvg;
    }

    public String getServiceVehicleAvg() {
        return serviceVehicleAvg;
    }

    public void setServiceVehicleAvg(String serviceVehicleAvg) {
        this.serviceVehicleAvg = serviceVehicleAvg;
    }

    public String getCompleteOrderAmount() {
        return completeOrderAmount;
    }

    public void setCompleteOrderAmount(String completeOrderAmount) {
        this.completeOrderAmount = completeOrderAmount;
    }

    public String getIncomeAmount() {
        return incomeAmount;
    }

    public void setIncomeAmount(String incomeAmount) {
        this.incomeAmount = incomeAmount;
    }

    public String getOrderPerVehicle() {
        return orderPerVehicle;
    }

    public void setOrderPerVehicle(String orderPerVehicle) {
        this.orderPerVehicle = orderPerVehicle;
    }

    public String getIncomePerVehicle() {
        return incomePerVehicle;
    }

    public void setIncomePerVehicle(String incomePerVehicle) {
        this.incomePerVehicle = incomePerVehicle;
    }

    public String getCriticismRate() {
        return criticismRate;
    }

    public void setCriticismRate(String criticismRate) {
        this.criticismRate = criticismRate;
    }

    public String getOrderCntNotchannel() {
        return orderCntNotchannel;
    }

    public void setOrderCntNotchannel(String orderCntNotchannel) {
        this.orderCntNotchannel = orderCntNotchannel;
    }

    public String getBadEvaluateNum() {
        return badEvaluateNum;
    }

    public void setBadEvaluateNum(String badEvaluateNum) {
        this.badEvaluateNum = badEvaluateNum;
    }

    public String getBadEvaluateAllNum() {
        return badEvaluateAllNum;
    }

    public void setBadEvaluateAllNum(String badEvaluateAllNum) {
        this.badEvaluateAllNum = badEvaluateAllNum;
    }

    public String getOnlineDriverTime() {
        return onlineDriverTime;
    }

    public void setOnlineDriverTime(String onlineDriverTime) {
        this.onlineDriverTime = onlineDriverTime;
    }
}
