package com.zhuanche.vo.busManage;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * @program: mp-manage
 * @description: 巴士司机账户余额vo
 * @author: niuzilian
 * @create: 2018-12-10 11:53
 **/
public class AccountBalanceVO {
    /**
     * 账户名称
     */
    private String accountName;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 城市ID
     */
    private Integer cityCode;
    /**
     * 城市名称
     */
    private String cityName;
    /**
     * 可提现金额
     */
    private BigDecimal canUseAmount;
    /**
     * 冻结金额
     */
    private BigDecimal waitingUseAmount;
    /**
     * 累计提现
     */
    private BigDecimal hasUsedAmount;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public BigDecimal getCanUseAmount() {
        return canUseAmount;
    }

    public void setCanUseAmount(BigDecimal canUseAmount) {
        this.canUseAmount = canUseAmount;
    }

    public BigDecimal getWaitingUseAmount() {
        return waitingUseAmount;
    }

    public void setWaitingUseAmount(BigDecimal waitingUseAmount) {
        this.waitingUseAmount = waitingUseAmount;
    }

    public BigDecimal getHasUsedAmount() {
        return hasUsedAmount;
    }

    public void setHasUsedAmount(BigDecimal hasUsedAmount) {
        this.hasUsedAmount = hasUsedAmount;
    }

    public Integer getCityCode() {
        return cityCode;
    }

    public void setCityCode(Integer cityCode) {
        this.cityCode = cityCode;
    }

    @Override
    public String toString() {
        //String ACCOUNT_EXPORT_HEAD="账户名称,手机号,城市名称,可提现金额,冻结金额,累计提现";
        StringBuffer sb = new StringBuffer();
        sb.append(StringUtils.defaultString(accountName)).append(",\t")
                .append(StringUtils.defaultString(phone)).append(",")
                .append(StringUtils.defaultString(cityName)).append(",\t")
                .append(StringUtils.defaultIfEmpty(canUseAmount + "", "0")).append(",")
                .append(StringUtils.defaultIfEmpty(waitingUseAmount + "", "0")).append(",")
                .append(StringUtils.defaultIfEmpty(hasUsedAmount + "", "0"));
        return sb.toString();
    }
}
