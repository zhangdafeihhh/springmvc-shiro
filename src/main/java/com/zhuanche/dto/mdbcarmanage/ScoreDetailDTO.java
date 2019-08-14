package com.zhuanche.dto.mdbcarmanage;

/**
 * @Author fanht
 * @Description
 * @Date 2019/8/8 下午4:37
 * @Version 1.0
 */
public class ScoreDetailDTO {

    private Integer driverId;

    private String name;

    private String phone;

    private String hourScore; //时长分

    private Integer isTotal;//是否计入总分 1 计入总分 0 不计入总分

    private String scoreDetailDate;

    private String baseTripScore;//基础分时长分

    private String sumOfTripScore;//计分周期时长分(含新手时长分)之和

    private String sumOfDispatchScore;//计分周期调度分之和

    private String calScoreDay;//服务时长分回滚统计周期（自然日）

    private String finishOrderDay;//服务时长分计分周期（完单日）

    private String dispatchRollDay;//调度计分周期


    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHourScore() {
        return hourScore;
    }

    public void setHourScore(String hourScore) {
        this.hourScore = hourScore;
    }

    public Integer getIsTotal() {
        return isTotal;
    }

    public void setIsTotal(Integer isTotal) {
        this.isTotal = isTotal;
    }

    public String getScoreDetailDate() {
        return scoreDetailDate;
    }

    public void setScoreDetailDate(String scoreDetailDate) {
        this.scoreDetailDate = scoreDetailDate;
    }

    public String getBaseTripScore() {
        return baseTripScore;
    }

    public void setBaseTripScore(String baseTripScore) {
        this.baseTripScore = baseTripScore;
    }

    public String getSumOfTripScore() {
        return sumOfTripScore;
    }

    public void setSumOfTripScore(String sumOfTripScore) {
        this.sumOfTripScore = sumOfTripScore;
    }

    public String getSumOfDispatchScore() {
        return sumOfDispatchScore;
    }

    public void setSumOfDispatchScore(String sumOfDispatchScore) {
        this.sumOfDispatchScore = sumOfDispatchScore;
    }

    public String getCalScoreDay() {
        return calScoreDay;
    }

    public void setCalScoreDay(String calScoreDay) {
        this.calScoreDay = calScoreDay;
    }

    public String getFinishOrderDay() {
        return finishOrderDay;
    }

    public void setFinishOrderDay(String finishOrderDay) {
        this.finishOrderDay = finishOrderDay;
    }

    public String getDispatchRollDay() {
        return dispatchRollDay;
    }

    public void setDispatchRollDay(String dispatchRollDay) {
        this.dispatchRollDay = dispatchRollDay;
    }
}
