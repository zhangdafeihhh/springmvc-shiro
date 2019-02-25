package com.zhuanche.entity.driverPreparate;

import java.util.List;

/**
 *  司机报备详情
 *  @auther wanghongdong
 */
public class DriverPreparate {

    /**
     *  报备id
     */
    private Integer id;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     *  订单id
     */
    private String orderId;
    /**
     * 司机姓名
     */
    private String driverName;
    /**
     * 司机手机号
     */
    private String driverPhone;
    /**
     * 车牌号
     */
    private String licensePlates;
    /**
     * 报备信息
     */
    private String reportMessage;
    /**
     * 创建时间
     */
    private String createDate;
    /**
     * 修改时间
     */
    private String updateDate;
    /**
     * 图片集合
     */
    private List<CarBizReportPictureExt> picUrls;
    /**
     * 图片地址
     */
    private List<CarBizReportItemExt> typeItems;

    private String createDateStr;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates;
    }

    public String getReportMessage() {
        return reportMessage;
    }

    public void setReportMessage(String reportMessage) {
        this.reportMessage = reportMessage;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public List<CarBizReportPictureExt> getPicUrls() {
        return picUrls;
    }

    public void setPicUrls(List<CarBizReportPictureExt> picUrls) {
        this.picUrls = picUrls;
    }

    public List<CarBizReportItemExt> getTypeItems() {
        return typeItems;
    }

    public void setTypeItems(List<CarBizReportItemExt> typeItems) {
        this.typeItems = typeItems;
    }

    public String getCreateDateStr() {
        return createDateStr;
    }

    public void setCreateDateStr(String createDateStr) {
        this.createDateStr = createDateStr;
    }
}
