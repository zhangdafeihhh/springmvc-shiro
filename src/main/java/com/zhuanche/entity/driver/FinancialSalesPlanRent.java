package com.zhuanche.entity.driver;

import java.math.BigDecimal;
import java.util.Date;

public class FinancialSalesPlanRent {
    private Integer salesId;

    private String goodsNumber;

    private Integer leaseTerm;

    private BigDecimal frontMoney;

    private BigDecimal rentEveryTerm;

    private BigDecimal firstRent;

    private BigDecimal securityDeposit;

    private BigDecimal totalPrice;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    public Integer getSalesId() {
        return salesId;
    }

    public void setSalesId(Integer salesId) {
        this.salesId = salesId;
    }

    public String getGoodsNumber() {
        return goodsNumber;
    }

    public void setGoodsNumber(String goodsNumber) {
        this.goodsNumber = goodsNumber == null ? null : goodsNumber.trim();
    }

    public Integer getLeaseTerm() {
        return leaseTerm;
    }

    public void setLeaseTerm(Integer leaseTerm) {
        this.leaseTerm = leaseTerm;
    }

    public BigDecimal getFrontMoney() {
        return frontMoney;
    }

    public void setFrontMoney(BigDecimal frontMoney) {
        this.frontMoney = frontMoney;
    }

    public BigDecimal getRentEveryTerm() {
        return rentEveryTerm;
    }

    public void setRentEveryTerm(BigDecimal rentEveryTerm) {
        this.rentEveryTerm = rentEveryTerm;
    }

    public BigDecimal getFirstRent() {
        return firstRent;
    }

    public void setFirstRent(BigDecimal firstRent) {
        this.firstRent = firstRent;
    }

    public BigDecimal getSecurityDeposit() {
        return securityDeposit;
    }

    public void setSecurityDeposit(BigDecimal securityDeposit) {
        this.securityDeposit = securityDeposit;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy == null ? null : createBy.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy == null ? null : updateBy.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}