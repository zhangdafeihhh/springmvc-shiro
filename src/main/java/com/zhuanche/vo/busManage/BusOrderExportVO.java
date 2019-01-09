package com.zhuanche.vo.busManage;

import java.io.Serializable;

/**
 * @program: car-manage
 * @description:导出巴士订单列表及详情
 * @author: admin
 * @create: 2018-08-20 16:43
 **/
public class BusOrderExportVO implements Serializable {
    /**
     * 订单id
     */
    private String orderId;
    /**
     * 订单编号
     */
    private String orderNo;
    /**
     * 下单时间
     */
    private String createDate;
    /**
     * 预定用车时间
     */
    private String bookingDate;
    /**
     * 预约上车地点
     */
    private String bookingStartAddr;
    /**
     * 预约下车地点
     */
    private String bookingEndAddr;
    /**
     * 城市
     */
    private String cityName;
    /**
     * 服务类别,根据serviceTypeId查找
     */
    private String serviceName;
    /**
     * 预约车型类别
     */
    private String bookingGroupName;
    /**
     * 车型类别 carGroupName 得到
     */
    private String factGroupName;
    /**
     * 订单类别(订单类型 0 安卓 1 | IOS 2 客服后台创建 | 3机构后台创建)
     */
    private String orderType;
    /**
     * 乘车人数量
     */
    private String riderCount;
    /**
     * 行李数数量
     */
    private String luggageCount;
    /**
     * 预定人手机号
     */
    private String bookingUserPhone;
    /**
     * 乘车人姓名
     */
    private String riderName;
    /**
     * 乘车人手机号
     */
    private String riderPhone;
    /**
     * 车牌号
     */
    private String licensePlates;
    /**
     * 完成时间
     */
    private String factEndDate;
    /**
     * 实际上车时间
     */
    private String factDate;
    /**
     * 实际上车地点
     */
    private String factStartAddr;
    /**
     * 实际下车地点
     */
    private String factEndAddr;
    /**
     * 单程/往返
     */
    private String isReturn;
    /**
     * 状态
     */
    private String status;
    /**
     * 取消原因
     */
    private String memo;
    /**
     * 总支付金额
     */
    private String amount;
    /**
     * 预收费用(订单)
     */
    private String estimatedAmountYuan;
    /**
     * 代收费用
     */
    private String settleAmount;

    /**
     * 违约金额
     */
    private String damageFee;
    /**
     * 优惠金额
     */
    private String couponAmount;
    /**
     * 供应商
     */
    private String supplierName;
    /**
     * 订单来源
     */
    private String type;
    /**
     * 预订人(待定)
     */
    private String bookingUserName;
    /**
     * 实际里程
     */
    private String distance;
    /**
     * 实际时长
     */
    private String duration;
    /**
     * 预收支付方式(支付)
     */
    private String payToolName;
    /**
     * 代收支付方式(计费)
     */
    private String payType;
    /**
     * 司机姓名
     */
    private String driverName;
    /**
     * 司机手机号
     */
    private String driverPhone;
    /**
     * 停车费
     */
    private String tcFee;
    /**
     * 高速费
     */
    private String gsFee;
    /**
     * 司机住宿费
     */
    private String hotelFee;
    /**
     * 司机餐费
     */
    private String mealFee ;
    /**
     * 其他费
     */
    private String qtFee;
    /**
     * 预付费支付时间
     */
    private String finishDate;
    /**
     * 代收费支付时
     */
    private String settleDate;
    /**
     * 指派时间
     */
    private String assigTime;
    /**
     * 改派时间
     */
    private String reassingTime;

    /**
     * 企业名称
     */
    private String businessName;
    /**
     * 企业付款类型 预付费 后付费
     */
    private String orgCostType;
    /**
     * 折扣信息
     */
    private String percent;

    /**
     * 评分
     * @return
     */
    private String evaluateScore;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
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

    public String getBookingStartAddr() {
        return bookingStartAddr;
    }

    public void setBookingStartAddr(String bookingStartAddr) {
        this.bookingStartAddr = bookingStartAddr;
    }

    public String getBookingEndAddr() {
        return bookingEndAddr;
    }

    public void setBookingEndAddr(String bookingEndAddr) {
        this.bookingEndAddr = bookingEndAddr;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getBookingGroupName() {
        return bookingGroupName;
    }

    public void setBookingGroupName(String bookingGroupName) {
        this.bookingGroupName = bookingGroupName;
    }

    public String getFactGroupName() {
        return factGroupName;
    }

    public void setFactGroupName(String factGroupName) {
        this.factGroupName = factGroupName;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getRiderCount() {
        return riderCount;
    }

    public void setRiderCount(String riderCount) {
        this.riderCount = riderCount;
    }

    public String getLuggageCount() {
        return luggageCount;
    }

    public void setLuggageCount(String luggageCount) {
        this.luggageCount = luggageCount;
    }

    public String getBookingUserPhone() {
        return bookingUserPhone;
    }

    public void setBookingUserPhone(String bookingUserPhone) {
        this.bookingUserPhone = bookingUserPhone;
    }

    public String getRiderName() {
        return riderName;
    }

    public void setRiderName(String riderName) {
        this.riderName = riderName;
    }

    public String getRiderPhone() {
        return riderPhone;
    }

    public void setRiderPhone(String riderPhone) {
        this.riderPhone = riderPhone;
    }

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates;
    }

    public String getFactEndDate() {
        return factEndDate;
    }

    public void setFactEndDate(String factEndDate) {
        this.factEndDate = factEndDate;
    }

    public String getFactDate() {
        return factDate;
    }

    public void setFactDate(String factDate) {
        this.factDate = factDate;
    }

    public String getFactStartAddr() {
        return factStartAddr;
    }

    public void setFactStartAddr(String factStartAddr) {
        this.factStartAddr = factStartAddr;
    }

    public String getFactEndAddr() {
        return factEndAddr;
    }

    public void setFactEndAddr(String factEndAddr) {
        this.factEndAddr = factEndAddr;
    }

    public String getIsReturn() {
        return isReturn;
    }

    public void setIsReturn(String isReturn) {
        this.isReturn = isReturn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getEstimatedAmountYuan() {
        return estimatedAmountYuan;
    }

    public void setEstimatedAmountYuan(String estimatedAmountYuan) {
        this.estimatedAmountYuan = estimatedAmountYuan;
    }

    public String getSettleAmount() {
        return settleAmount;
    }

    public void setSettleAmount(String settleAmount) {
        this.settleAmount = settleAmount;
    }

    public String getDamageFee() {
        return damageFee;
    }

    public void setDamageFee(String damageFee) {
        this.damageFee = damageFee;
    }

    public String getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(String couponAmount) {
        this.couponAmount = couponAmount;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBookingUserName() {
        return bookingUserName;
    }

    public void setBookingUserName(String bookingUserName) {
        this.bookingUserName = bookingUserName;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPayToolName() {
        return payToolName;
    }

    public void setPayToolName(String payToolName) {
        this.payToolName = payToolName;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getTcFee() {
        return tcFee;
    }

    public void setTcFee(String tcFee) {
        this.tcFee = tcFee;
    }

    public String getGsFee() {
        return gsFee;
    }

    public void setGsFee(String gsFee) {
        this.gsFee = gsFee;
    }

    public String getHotelFee() {
        return hotelFee;
    }

    public void setHotelFee(String hotelFee) {
        this.hotelFee = hotelFee;
    }

    public String getMealFee() {
        return mealFee;
    }

    public void setMealFee(String mealFee) {
        this.mealFee = mealFee;
    }

    public String getQtFee() {
        return qtFee;
    }

    public void setQtFee(String qtFee) {
        this.qtFee = qtFee;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public String getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

    public String getAssigTime() {
        return assigTime;
    }

    public void setAssigTime(String assigTime) {
        this.assigTime = assigTime;
    }

    public String getReassingTime() {
        return reassingTime;
    }

    public void setReassingTime(String reassingTime) {
        this.reassingTime = reassingTime;
    }

    public String getEvaluateScore() {
        return evaluateScore;
    }

    public void setEvaluateScore(String evaluateScore) {
        this.evaluateScore = evaluateScore;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getOrgCostType() {
        return orgCostType;
    }

    public void setOrgCostType(String orgCostType) {
        this.orgCostType = orgCostType;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }


}
