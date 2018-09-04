package com.zhuanche.entity.rentcar;

import java.math.BigDecimal;
import java.util.Date;

public class CarBizPaymentDetail {
    private Integer id;

    private String relativeNo;

    private Integer paymentType;

    private String paymentNo;

    private Integer paymentUsertype;

    private String paymentUser;

    private String paymentCustomerid;

    private BigDecimal amount;

    private String status;

    private String preauthDate;

    private Integer preauthStatus;

    private String preauthSureDate;

    private Integer preauthSureStatus;

    private String externalNo;

    private Date createdate;

    private Date updateDate;

    private String errormsg;

    private String authorizationcode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRelativeNo() {
        return relativeNo;
    }

    public void setRelativeNo(String relativeNo) {
        this.relativeNo = relativeNo == null ? null : relativeNo.trim();
    }

    public Integer getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentNo() {
        return paymentNo;
    }

    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo == null ? null : paymentNo.trim();
    }

    public Integer getPaymentUsertype() {
        return paymentUsertype;
    }

    public void setPaymentUsertype(Integer paymentUsertype) {
        this.paymentUsertype = paymentUsertype;
    }

    public String getPaymentUser() {
        return paymentUser;
    }

    public void setPaymentUser(String paymentUser) {
        this.paymentUser = paymentUser == null ? null : paymentUser.trim();
    }

    public String getPaymentCustomerid() {
        return paymentCustomerid;
    }

    public void setPaymentCustomerid(String paymentCustomerid) {
        this.paymentCustomerid = paymentCustomerid == null ? null : paymentCustomerid.trim();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getPreauthDate() {
        return preauthDate;
    }

    public void setPreauthDate(String preauthDate) {
        this.preauthDate = preauthDate == null ? null : preauthDate.trim();
    }

    public Integer getPreauthStatus() {
        return preauthStatus;
    }

    public void setPreauthStatus(Integer preauthStatus) {
        this.preauthStatus = preauthStatus;
    }

    public String getPreauthSureDate() {
        return preauthSureDate;
    }

    public void setPreauthSureDate(String preauthSureDate) {
        this.preauthSureDate = preauthSureDate == null ? null : preauthSureDate.trim();
    }

    public Integer getPreauthSureStatus() {
        return preauthSureStatus;
    }

    public void setPreauthSureStatus(Integer preauthSureStatus) {
        this.preauthSureStatus = preauthSureStatus;
    }

    public String getExternalNo() {
        return externalNo;
    }

    public void setExternalNo(String externalNo) {
        this.externalNo = externalNo == null ? null : externalNo.trim();
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getErrormsg() {
        return errormsg;
    }

    public void setErrormsg(String errormsg) {
        this.errormsg = errormsg == null ? null : errormsg.trim();
    }

    public String getAuthorizationcode() {
        return authorizationcode;
    }

    public void setAuthorizationcode(String authorizationcode) {
        this.authorizationcode = authorizationcode == null ? null : authorizationcode.trim();
    }
}