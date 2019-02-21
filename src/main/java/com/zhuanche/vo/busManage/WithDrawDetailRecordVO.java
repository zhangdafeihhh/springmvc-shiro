package com.zhuanche.vo.busManage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * @program: car-manage
 * @description: 分佣提现
 * @author: niuzilian
 * @create: 2018-10-10 17:37
 **/
@JsonIgnoreProperties(ignoreUnknown = true)
public class WithDrawDetailRecordVO {
    /**
     * 账户名称
     */
    private String accountName;
    /**
     * 司机手机号
     */
    private String phone;
    /**
     * 城市编号
     */
    private Integer cityCode;
    /**
     * 城市名称
     */
    private String cityName;
    /**
     * 结算时间
     */
    private String createDate;
    /**
     * 提现金额
     */
    private BigDecimal withdrawAmount;
    /***
     * 银行卡号
     */
    private String bankCard;
    /**
     * 银行名称
     */
    private String bankName;

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

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public BigDecimal getWithdrawAmount() {
        return withdrawAmount;
    }

    public void setWithdrawAmount(BigDecimal withdrawAmount) {
        this.withdrawAmount = withdrawAmount;
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

    public Integer getCityCode() {
        return cityCode;
    }

    public void setCityCode(Integer cityCode) {
        this.cityCode = cityCode;
    }

    @Override
    public String toString() {
        //String DRAW_EXPORT_HEAD="账户名称,手机号,城市名称,结算时间,到账金额,银行卡号,银行名称";
        StringBuffer sb = new StringBuffer();
        sb.append(StringUtils.defaultString(accountName)).append(",\t")
                .append(StringUtils.defaultString(phone)).append(",")
                .append(StringUtils.defaultString(cityName)).append(",\t")
                .append(StringUtils.defaultString(createDate)).append(",")
                .append(StringUtils.defaultIfEmpty(withdrawAmount+"","0")).append(",\t")
                .append(StringUtils.defaultString(bankCard)).append(",")
                .append(StringUtils.defaultString(bankName));
        return sb.toString();
    }
}
