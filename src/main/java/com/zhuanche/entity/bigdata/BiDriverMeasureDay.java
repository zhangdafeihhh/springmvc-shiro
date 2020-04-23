package com.zhuanche.entity.bigdata;

import com.zhuanche.entity.common.BaseEntity;

import java.math.BigDecimal;

public class BiDriverMeasureDay extends BaseEntity {

    private Integer id;

    private String dataDate;

    private Integer supplierId;

    private String supplierName;

    private Integer teamId;

    private String teamName;

    private Integer carGroupId;

    private String carGroupName;

    private Integer comOrderNum;

    private Integer factOverOrderNum;

    private BigDecimal factOverAmount;

    private Integer inUseDriverNum;

    private Integer orderNum;

    private Integer pushOrderNum;

    private Integer bindOrderNum;

    private Integer onlineDriverNum;

    private Integer totalDriverNum;

    private Integer bindOrderDriverCnt;

    private Integer finishClOrderNum;

    private Integer responsibleComplaintNum;


    private String startDate;

    private String endDate;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDataDate() {
        return dataDate;
    }

    public void setDataDate(String dataDate) {
        this.dataDate = dataDate == null ? null : dataDate.trim();
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
        this.supplierName = supplierName == null ? null : supplierName.trim();
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName == null ? null : teamName.trim();
    }

    public Integer getCarGroupId() {
        return carGroupId;
    }

    public void setCarGroupId(Integer carGroupId) {
        this.carGroupId = carGroupId;
    }

    public String getCarGroupName() {
        return carGroupName;
    }

    public void setCarGroupName(String carGroupName) {
        this.carGroupName = carGroupName == null ? null : carGroupName.trim();
    }

    public Integer getComOrderNum() {
        return comOrderNum;
    }

    public void setComOrderNum(Integer comOrderNum) {
        this.comOrderNum = comOrderNum;
    }

    public Integer getFactOverOrderNum() {
        return factOverOrderNum;
    }

    public void setFactOverOrderNum(Integer factOverOrderNum) {
        this.factOverOrderNum = factOverOrderNum;
    }

    public BigDecimal getFactOverAmount() {
        return factOverAmount;
    }

    public void setFactOverAmount(BigDecimal factOverAmount) {
        this.factOverAmount = factOverAmount;
    }

    public Integer getInUseDriverNum() {
        return inUseDriverNum;
    }

    public void setInUseDriverNum(Integer inUseDriverNum) {
        this.inUseDriverNum = inUseDriverNum;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Integer getPushOrderNum() {
        return pushOrderNum;
    }

    public void setPushOrderNum(Integer pushOrderNum) {
        this.pushOrderNum = pushOrderNum;
    }

    public Integer getBindOrderNum() {
        return bindOrderNum;
    }

    public void setBindOrderNum(Integer bindOrderNum) {
        this.bindOrderNum = bindOrderNum;
    }

    public Integer getOnlineDriverNum() {
        return onlineDriverNum;
    }

    public void setOnlineDriverNum(Integer onlineDriverNum) {
        this.onlineDriverNum = onlineDriverNum;
    }

    public Integer getTotalDriverNum() {
        return totalDriverNum;
    }

    public void setTotalDriverNum(Integer totalDriverNum) {
        this.totalDriverNum = totalDriverNum;
    }

    public Integer getBindOrderDriverCnt() {
        return bindOrderDriverCnt;
    }

    public void setBindOrderDriverCnt(Integer bindOrderDriverCnt) {
        this.bindOrderDriverCnt = bindOrderDriverCnt;
    }

    public Integer getFinishClOrderNum() {
        return finishClOrderNum;
    }

    public void setFinishClOrderNum(Integer finishClOrderNum) {
        this.finishClOrderNum = finishClOrderNum;
    }

    public Integer getResponsibleComplaintNum() {
        return responsibleComplaintNum;
    }

    public void setResponsibleComplaintNum(Integer responsibleComplaintNum) {
        this.responsibleComplaintNum = responsibleComplaintNum;
    }
}