package com.zhuanche.dto.rentcar;

import com.zhuanche.entity.common.Base;

import java.util.Date;

public class CarBizCustomerAppraisalStatisticsDTO extends Base {

    private static final long serialVersionUID = 4959453183678167556L;

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

    private Integer cityId;
    private Integer supplierId;

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

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