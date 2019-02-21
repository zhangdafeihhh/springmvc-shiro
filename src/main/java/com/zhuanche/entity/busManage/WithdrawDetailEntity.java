package com.zhuanche.entity.busManage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * @program: car-manage
 * @description:自营司机提现记录
 * @author: niuzilian
 * @create: 2018-10-09 18:42
 **/
@JsonIgnoreProperties(ignoreUnknown = true)
public class WithdrawDetailEntity  {
    /**
     * 账户id
     */
    private String accountId;
    /**
     * 账户名称
     */
    private String accountName;
    /**
     * 账户类型
     */
    private Integer accountType;
    /**
     * 提现金额
     */
    private BigDecimal withdrawAmount;
    /**
     * 待结算金额
     */
    private BigDecimal waitingUseAmount;
    /**
     * 可用金额
     */
    private BigDecimal canUseAmount;
    /**
     * 已使用金额
     */
    private BigDecimal hasUsedAmount;


    /***
     * 银行卡号
     */
    private String bankCard;
    /**
     * 银行名称
     */
    private String bankName;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 提现时间
     */
    private String createDate;
    /**
     * 城市id
     */
    private Integer cityCode;
    /**
     * 城市名称
     */
    private String cityName;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 开始时间
     */
    private String startDate;
    /**
     * 结束时间
     */
    private String endDate;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getWithdrawAmount() {
        return withdrawAmount;
    }

    public void setWithdrawAmount(BigDecimal withdrawAmount) {
        this.withdrawAmount = withdrawAmount;
    }

    public BigDecimal getWaitingUseAmount() {
        return waitingUseAmount;
    }

    public void setWaitingUseAmount(BigDecimal waitingUseAmount) {
        this.waitingUseAmount = waitingUseAmount;
    }

    public BigDecimal getCanUseAmount() {
        return canUseAmount;
    }

    public void setCanUseAmount(BigDecimal canUseAmount) {
        this.canUseAmount = canUseAmount;
    }

    public BigDecimal getHasUsedAmount() {
        return hasUsedAmount;
    }

    public void setHasUsedAmount(BigDecimal hasUsedAmount) {
        this.hasUsedAmount = hasUsedAmount;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Integer getCityCode() {
        return cityCode;
    }

    public void setCityCode(Integer cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

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

}
