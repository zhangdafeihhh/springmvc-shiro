package com.zhuanche.dto.rentcar;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;

/**
 * 司机派单分每日更新记录
 *
 * @author wuqiang
 * @date 2020.03.09
 */
public class DriverDispatchScoreDailyUpdateRecord {

    /**
     * 司机ID
     */
    private Integer driverId;

    /**
     * 司机姓名
     */
    private String driverName;

    /**
     * 司机手机号
     */
    private String driverPhone;

    /**
     * 当前派单分
     */
    private String currentDispatchScore;

    /**
     * 服务基础分
     */
    private String serviceBaseScore;

    /**
     * 服务分加速分
     */
    private String serviceAccelerateScore;

    /**
     * 时长基础分
     */
    private String timeLengthBaseScore;

    /**
     * 时长分加速分
     */
    private String timeLengthAccelerateScore;

    /**
     * 不良行为扣分
     */
    private String badBehaviorDeductionScore;

    /**
     * 比前一日分数
     */
    private String changeScore;

    /**
     * 更新时间<br>
     *     YYYY-MM-DD
     */
    private String updateDate;

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
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

    public String getCurrentDispatchScore() {
        return currentDispatchScore;
    }

    public void setCurrentDispatchScore(String currentDispatchScore) {
        this.currentDispatchScore = currentDispatchScore;
    }

    public String getServiceBaseScore() {
        return serviceBaseScore;
    }

    public void setServiceBaseScore(String serviceBaseScore) {
        this.serviceBaseScore = serviceBaseScore;
    }

    public String getServiceAccelerateScore() {
        return serviceAccelerateScore;
    }

    public void setServiceAccelerateScore(String serviceAccelerateScore) {
        this.serviceAccelerateScore = serviceAccelerateScore;
    }

    public String getTimeLengthBaseScore() {
        return timeLengthBaseScore;
    }

    public void setTimeLengthBaseScore(String timeLengthBaseScore) {
        this.timeLengthBaseScore = timeLengthBaseScore;
    }

    public String getTimeLengthAccelerateScore() {
        return timeLengthAccelerateScore;
    }

    public void setTimeLengthAccelerateScore(String timeLengthAccelerateScore) {
        this.timeLengthAccelerateScore = timeLengthAccelerateScore;
    }

    public String getBadBehaviorDeductionScore() {
        return badBehaviorDeductionScore;
    }

    public void setBadBehaviorDeductionScore(String badBehaviorDeductionScore) {
        this.badBehaviorDeductionScore = badBehaviorDeductionScore;
    }

    public String getChangeScore() {
        return changeScore;
    }

    public void setChangeScore(String changeScore) {
        this.changeScore = changeScore;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public DriverDispatchScoreDailyUpdateRecord buildDriverId(Integer driverId) {
        this.driverId = driverId;
        return this;
    }

    public DriverDispatchScoreDailyUpdateRecord buildDriverName(String driverName) {
        if(StringUtils.isBlank(driverName) || "null".equals(driverName)){
            driverName = "";
        }
        this.driverName = driverName;
        return this;
    }

    public DriverDispatchScoreDailyUpdateRecord buildDriverPhone(String driverPhone) {
        if(StringUtils.isBlank(driverPhone) || "null".equals(driverPhone)){
            driverPhone = "";
        }
        this.driverPhone = driverPhone;
        return this;
    }

    public DriverDispatchScoreDailyUpdateRecord buildCurrentDispatchScore(String currentDispatchScore) {
        if(StringUtils.isBlank(currentDispatchScore) || "null".equals(currentDispatchScore)){
            currentDispatchScore = "";
        }
        this.currentDispatchScore = currentDispatchScore;
        return this;
    }

    public DriverDispatchScoreDailyUpdateRecord buildServiceBaseScore(String serviceBaseScore) {
        if(StringUtils.isBlank(serviceBaseScore) || "null".equals(serviceBaseScore)){
            serviceBaseScore = "";
        }
        this.serviceBaseScore = serviceBaseScore;
        return this;
    }

    public DriverDispatchScoreDailyUpdateRecord buildServiceAccelerateScore(String serviceAccelerateScore) {
        if(StringUtils.isBlank(serviceAccelerateScore) || "null".equals(serviceAccelerateScore)){
            serviceAccelerateScore = "";
        }
        this.serviceAccelerateScore = serviceAccelerateScore;
        return this;
    }

    public DriverDispatchScoreDailyUpdateRecord buildTimeLengthBaseScore(String timeLengthBaseScore) {
        if(StringUtils.isBlank(timeLengthBaseScore) || "null".equals(timeLengthBaseScore)){
            timeLengthBaseScore = "";
        }
        this.timeLengthBaseScore = timeLengthBaseScore;
        return this;
    }

    public DriverDispatchScoreDailyUpdateRecord buildTimeLengthAccelerateScore(String timeLengthAccelerateScore) {
        if(StringUtils.isBlank(timeLengthAccelerateScore) || "null".equals(timeLengthAccelerateScore)){
            timeLengthAccelerateScore = "";
        }
        this.timeLengthAccelerateScore = timeLengthAccelerateScore;
        return this;
    }

    public DriverDispatchScoreDailyUpdateRecord buildBadBehaviorDeductionScore(String badBehaviorDeductionScore) {
        if(StringUtils.isBlank(badBehaviorDeductionScore) || "null".equals(badBehaviorDeductionScore)){
            badBehaviorDeductionScore = "";
        }
        this.badBehaviorDeductionScore = badBehaviorDeductionScore;
        return this;
    }

    public DriverDispatchScoreDailyUpdateRecord buildChangeScore(String changeScore) {
        if(StringUtils.isBlank(changeScore) || "null".equals(changeScore)){
            changeScore = "";
        }
        this.changeScore = changeScore;
        return this;
    }

    public DriverDispatchScoreDailyUpdateRecord buildUpdateDate(String updateDate) {
        if(StringUtils.isBlank(updateDate) || "null".equals(updateDate)){
            updateDate = "";
        }
        this.updateDate = updateDate;
        return this;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
