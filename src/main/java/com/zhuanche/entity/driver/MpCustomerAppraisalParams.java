package com.zhuanche.entity.driver;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class MpCustomerAppraisalParams implements Serializable{

    private static final long serialVersionUID = 8155190408775970333L;

    private String cityId;
    private String supplierId;
    private String teamId;
    private String groupIds;
    private String driverName;
    private String driverPhone;
    private String orderNo;
    private String orderNos;
    private String createDateBegin;
    private String createDateEnd;
    private String evaluateScore;
    private Integer page;
    private Integer pageSize;
    private String driverIds;
    private String cities;
    private String suppliers;
    private String licensePlates;
    private String sortName;
    private String sortorder = "desc";

    private String orderFinishTime;

    private String orderFinishTimeBegin;
    private String orderFinishTimeEnd;
    /**
     * 评论是否有效 0:有效 1:无效
     */
    private Integer appraisalStatus;
    /**
     * 是否允许申诉：是否可申诉：0：否 1：是
     */
    private Integer isAllowedAppeal;

    /**
     * 申诉状态  申诉状态：0:未申诉   1:已申诉（待审核） 2:申诉成功  3:申诉失败
     */
    private Integer appealStatus;

    /**
     * 是否已申诉
     * */
    private Integer isAlreadyAppeal;

    private List<Integer> appraisalIds;

    private Integer minId;

    private Integer maxId;


    public MpCustomerAppraisalParams(String cityId, String supplierId, String teamId, String groupIds, String driverName, String driverPhone, String orderNo,
                                     String createDateBegin, String createDateEnd, String evaluateScore, String sortName, String sortorder, Integer page, Integer pageSize) {
        this.cityId = cityId;
        this.supplierId = supplierId;
        this.teamId = teamId;
        this.groupIds = groupIds;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.orderNo = orderNo;
        this.createDateBegin = createDateBegin;
        this.createDateEnd = createDateEnd;
        this.evaluateScore = evaluateScore;
        this.page = page;
        this.pageSize = pageSize;
        this.sortName = sortName;
        this.sortorder = sortorder;
    }

    public MpCustomerAppraisalParams(String cityId, String supplierId, String driverName, String driverPhone, String orderNo,
                                     String createDateBegin, String createDateEnd, String orderFinishTimeBegin,String orderFinishTimeEnd,
                                     String evaluateScore,Integer appraisalStatus,
                                     Integer isAllowedAppeal,String sortName, String sortorder) {
        this.cityId = cityId;
        this.supplierId = supplierId;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.orderNo = orderNo;
        this.createDateBegin = createDateBegin;
        this.createDateEnd = createDateEnd;
        this.orderFinishTimeBegin=orderFinishTimeBegin;
        this.orderFinishTimeEnd=orderFinishTimeEnd;
        this.evaluateScore = evaluateScore;
        this.appraisalStatus=appraisalStatus;
        this.isAllowedAppeal=isAllowedAppeal;
        this.sortName = sortName;
        this.sortorder = sortorder;
    }

    public MpCustomerAppraisalParams(){};



    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(String groupIds) {
        this.groupIds = groupIds;
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

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getCreateDateBegin() {
        return createDateBegin;
    }

    public void setCreateDateBegin(String createDateBegin) {
        this.createDateBegin = createDateBegin;
    }

    public String getCreateDateEnd() {
        return createDateEnd;
    }

    public void setCreateDateEnd(String createDateEnd) {
        this.createDateEnd = createDateEnd;
    }

    public String getEvaluateScore() {
        return evaluateScore;
    }

    public void setEvaluateScore(String evaluateScore) {
        this.evaluateScore = evaluateScore;
    }

    public Integer getPage() {
        return page == null ? 1 : page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize == null ? 30 : pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getCities() {
        return cities;
    }

    public void setCities(String cities) {
        this.cities = cities;
    }

    public String getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(String suppliers) {
        this.suppliers = suppliers;
    }

    public String getDriverIds() {
        return driverIds;
    }

    public void setDriverIds(String driverIds) {
        this.driverIds = driverIds;
    }

    public String getOrderNos() {
        return orderNos;
    }

    public void setOrderNos(String orderNos) {
        this.orderNos = orderNos;
    }

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public String getSortorder() {
        return sortorder;
    }

    public void setSortorder(String sortorder) {
        this.sortorder = sortorder;
    }

    public Integer getAppraisalStatus() {
        return appraisalStatus;
    }

    public void setAppraisalStatus(Integer appraisalStatus) {
        this.appraisalStatus = appraisalStatus;
    }

    public String getOrderFinishTime() {
        return orderFinishTime;
    }

    public void setOrderFinishTime(String orderFinishTime) {
        this.orderFinishTime = orderFinishTime;
    }

    public String getOrderFinishTimeBegin() {
        return orderFinishTimeBegin;
    }

    public void setOrderFinishTimeBegin(String orderFinishTimeBegin) {
        this.orderFinishTimeBegin = orderFinishTimeBegin;
    }

    public String getOrderFinishTimeEnd() {
        return orderFinishTimeEnd;
    }

    public void setOrderFinishTimeEnd(String orderFinishTimeEnd) {
        this.orderFinishTimeEnd = orderFinishTimeEnd;
    }

    public Integer getIsAllowedAppeal() {
        return isAllowedAppeal;
    }

    public void setIsAllowedAppeal(Integer isAllowedAppeal) {
        this.isAllowedAppeal = isAllowedAppeal;
    }

    public Integer getAppealStatus() {
        return appealStatus;
    }

    public void setAppealStatus(Integer appealStatus) {
        this.appealStatus = appealStatus;
    }

    public Integer getIsAlreadyAppeal() {
        return isAlreadyAppeal;
    }

    public void setIsAlreadyAppeal(Integer isAlreadyAppeal) {
        this.isAlreadyAppeal = isAlreadyAppeal;
    }

    public List<Integer> getAppraisalIds() {
        return appraisalIds;
    }

    public void setAppraisalIds(List<Integer> appraisalIds) {
        this.appraisalIds = appraisalIds;
    }

    public Integer getMinId() {
        return minId;
    }

    public void setMinId(Integer minId) {
        this.minId = minId;
    }

    public Integer getMaxId() {
        return maxId;
    }

    public void setMaxId(Integer maxId) {
        this.maxId = maxId;
    }

    @Override
    public String toString() {
        return "CarBizCustomerAppraisalParams{" +
                "cityId='" + cityId + '\'' +
                ", supplierId='" + supplierId + '\'' +
                ", teamId='" + teamId + '\'' +
                ", groupIds='" + groupIds + '\'' +
                ", driverName='" + driverName + '\'' +
                ", driverPhone='" + driverPhone + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", createDateBegin='" + createDateBegin + '\'' +
                ", createDateEnd='" + createDateEnd + '\'' +
                ", evaluateScore='" + evaluateScore + '\'' +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", driverIds='" + driverIds + '\'' +
                ", cities='" + cities + '\'' +
                ", suppliers='" + suppliers + '\'' +
                '}';
    }
}
