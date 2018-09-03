package com.zhuanche.entity.rentcar;

import java.math.BigDecimal;

public class CarBizDriverAccount {
    private Integer accountId;

    private Integer driverId;

    private BigDecimal creditBalance;

    private BigDecimal creditBalanceHisall;

    private BigDecimal accountAmount;

    private BigDecimal frozenAmount;

    private BigDecimal withdrawDeposit;

    private BigDecimal settleAccount;

    private BigDecimal currAccount;

    private BigDecimal hisallAccount;

    private BigDecimal outCurrAccount;

    private BigDecimal outHisallAccount;

    private Integer version;

    private BigDecimal settleOtmFee;

    private BigDecimal currOtmFee;

    private BigDecimal hisallOtmFee;

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public BigDecimal getCreditBalance() {
        return creditBalance;
    }

    public void setCreditBalance(BigDecimal creditBalance) {
        this.creditBalance = creditBalance;
    }

    public BigDecimal getCreditBalanceHisall() {
        return creditBalanceHisall;
    }

    public void setCreditBalanceHisall(BigDecimal creditBalanceHisall) {
        this.creditBalanceHisall = creditBalanceHisall;
    }

    public BigDecimal getAccountAmount() {
        return accountAmount;
    }

    public void setAccountAmount(BigDecimal accountAmount) {
        this.accountAmount = accountAmount;
    }

    public BigDecimal getFrozenAmount() {
        return frozenAmount;
    }

    public void setFrozenAmount(BigDecimal frozenAmount) {
        this.frozenAmount = frozenAmount;
    }

    public BigDecimal getWithdrawDeposit() {
        return withdrawDeposit;
    }

    public void setWithdrawDeposit(BigDecimal withdrawDeposit) {
        this.withdrawDeposit = withdrawDeposit;
    }

    public BigDecimal getSettleAccount() {
        return settleAccount;
    }

    public void setSettleAccount(BigDecimal settleAccount) {
        this.settleAccount = settleAccount;
    }

    public BigDecimal getCurrAccount() {
        return currAccount;
    }

    public void setCurrAccount(BigDecimal currAccount) {
        this.currAccount = currAccount;
    }

    public BigDecimal getHisallAccount() {
        return hisallAccount;
    }

    public void setHisallAccount(BigDecimal hisallAccount) {
        this.hisallAccount = hisallAccount;
    }

    public BigDecimal getOutCurrAccount() {
        return outCurrAccount;
    }

    public void setOutCurrAccount(BigDecimal outCurrAccount) {
        this.outCurrAccount = outCurrAccount;
    }

    public BigDecimal getOutHisallAccount() {
        return outHisallAccount;
    }

    public void setOutHisallAccount(BigDecimal outHisallAccount) {
        this.outHisallAccount = outHisallAccount;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public BigDecimal getSettleOtmFee() {
        return settleOtmFee;
    }

    public void setSettleOtmFee(BigDecimal settleOtmFee) {
        this.settleOtmFee = settleOtmFee;
    }

    public BigDecimal getCurrOtmFee() {
        return currOtmFee;
    }

    public void setCurrOtmFee(BigDecimal currOtmFee) {
        this.currOtmFee = currOtmFee;
    }

    public BigDecimal getHisallOtmFee() {
        return hisallOtmFee;
    }

    public void setHisallOtmFee(BigDecimal hisallOtmFee) {
        this.hisallOtmFee = hisallOtmFee;
    }
}