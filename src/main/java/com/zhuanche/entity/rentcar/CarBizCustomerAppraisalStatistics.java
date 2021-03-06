package com.zhuanche.entity.rentcar;

import java.util.Date;

public class CarBizCustomerAppraisalStatistics {
    private Integer appraisalStatisticsId;

    private Integer driverId;

    private String driverName;

    private String driverPhone;

    private String instrumentAndService;

    private String environmentAndEquipped;

    private String efficiencyAndSafety;

    private String evaluateScore;

    private String createDate;

    private Date sysDate;

    public Integer getAppraisalStatisticsId() {
        return appraisalStatisticsId;
    }

    public void setAppraisalStatisticsId(Integer appraisalStatisticsId) {
        this.appraisalStatisticsId = appraisalStatisticsId;
    }

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
        this.driverName = driverName == null ? null : driverName.trim();
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone == null ? null : driverPhone.trim();
    }

    public String getInstrumentAndService() {
        return instrumentAndService;
    }

    public void setInstrumentAndService(String instrumentAndService) {
        this.instrumentAndService = instrumentAndService == null ? null : instrumentAndService.trim();
    }

    public String getEnvironmentAndEquipped() {
        return environmentAndEquipped;
    }

    public void setEnvironmentAndEquipped(String environmentAndEquipped) {
        this.environmentAndEquipped = environmentAndEquipped == null ? null : environmentAndEquipped.trim();
    }

    public String getEfficiencyAndSafety() {
        return efficiencyAndSafety;
    }

    public void setEfficiencyAndSafety(String efficiencyAndSafety) {
        this.efficiencyAndSafety = efficiencyAndSafety == null ? null : efficiencyAndSafety.trim();
    }

    public String getEvaluateScore() {
        return evaluateScore;
    }

    public void setEvaluateScore(String evaluateScore) {
        this.evaluateScore = evaluateScore == null ? null : evaluateScore.trim();
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate == null ? null : createDate.trim();
    }

    public Date getSysDate() {
        return sysDate;
    }

    public void setSysDate(Date sysDate) {
        this.sysDate = sysDate;
    }
}