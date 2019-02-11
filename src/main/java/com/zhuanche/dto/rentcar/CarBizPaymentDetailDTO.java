package com.zhuanche.dto.rentcar;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Date;

public class CarBizPaymentDetailDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    // alias
    public static final String TABLE_ALIAS = "CarBizPaymentDetail";
    public static final String ALIAS_ID = "id";
    public static final String ALIAS_RELATIVE_NO = "相关单据号,如果是订单支付,记录订单号;如果是乘客绑定信用卡/司机绑定信用卡,记录乘客手机号";
    public static final String ALIAS_PAYMENT_TYPE = "支付类型,1:乘客绑定信用卡;2:乘客结算订单;3:司机绑卡;4:司机代收";
    public static final String ALIAS_PAYMENT_NO = "快钱系统交易流水号";
    public static final String ALIAS_PAYMENT_USERTYPE = "支付人类型,1:乘客;2:司机";
    public static final String ALIAS_PAYMENT_USER = "支付人,乘客/司机手机号";
    public static final String ALIAS_AMOUNT = "amount";
    public static final String ALIAS_STATUS = "第三方返回的状态";
    public static final String ALIAS_EXTERNAL_NO = "外部流水号,快钱返回的号";
    public static final String ALIAS_CREATEDATE = "createdate";

    // date formats
    public static final String FORMAT_CREATEDATE = "yyyy-MM-dd";

    // 可以直接使用: @Length(max=50,message="用户名长度不能大于50")显示错误消息
    // columns START
    //
    private Integer id;
    // @Length(max=50)
    private String relativeNo;
    //
    private Integer paymentType;
    // @Length(max=100)
    private String paymentNo;
    //
    private Integer paymentUsertype;
    // @Length(max=50)
    private String paymentUser;
    //
    private Double amount;
    // @Length(max=10)
    private String status;
    // @Length(max=50)
    private String externalNo;
    //
    private String createdate;

    // 预授权待确认状态
    private Integer preauthStatus;
    // 预授权成功日期
    private String preauthDate;
    // 预授权结算待确认状态
    private Integer preauthSureStatus;
    // 预授权结算成功日期
    private String preauthSureDate;

    private Date updateDate;

    // add by zhou , 增加预授权相关信息
    private String errorMsg;

    // 状态名字
    private String statusName;


    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getPreauthSureStatus() {
        return preauthSureStatus;
    }

    public void setPreauthSureStatus(Integer preauthSureStatus) {
        this.preauthSureStatus = preauthSureStatus;
    }

    public String getPreauthSureDate() {
        return preauthSureDate;
    }

    public void setPreauthSureDate(String preauthSureDate) {
        this.preauthSureDate = preauthSureDate;
    }

    public Integer getPreauthStatus() {
        return preauthStatus;
    }

    public void setPreauthStatus(Integer preauthStatus) {
        this.preauthStatus = preauthStatus;
    }

    public String getPreauthDate() {
        return preauthDate;
    }

    public void setPreauthDate(String preauthDate) {
        this.preauthDate = preauthDate;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public void setErrorMsg(String errormsg) {
        this.errorMsg = errormsg;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    private String authorizationcCode;

    public String getAuthorizationCode() {
        return authorizationcCode;
    }

    public void setAuthorizationCode(String authorizationcode) {
        this.authorizationcCode = authorizationcode;
    }

    // add by zhou end
    // columns END

    public CarBizPaymentDetailDTO() {
    }

    public CarBizPaymentDetailDTO(Integer id) {
        this.id = id;
    }

    public void setId(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return this.id;
    }

    public void setRelativeNo(String value) {
        this.relativeNo = value;
    }

    public String getRelativeNo() {
        return this.relativeNo;
    }

    public void setPaymentType(Integer value) {
        this.paymentType = value;
    }

    public Integer getPaymentType() {
        return this.paymentType;
    }

    public void setPaymentNo(String value) {
        this.paymentNo = value;
    }

    public String getPaymentNo() {
        return this.paymentNo;
    }

    public void setPaymentUsertype(Integer value) {
        this.paymentUsertype = value;
    }

    public Integer getPaymentUsertype() {
        return this.paymentUsertype;
    }

    public void setPaymentUser(String value) {
        this.paymentUser = value;
    }

    public String getPaymentUser() {
        return this.paymentUser;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setStatus(String value) {
        this.status = value;
    }

    public String getStatus() {
        return this.status;
    }

    public void setExternalNo(String value) {
        this.externalNo = value;
    }

    public String getExternalNo() {
        return this.externalNo;
    }

    public String getCreatedate() {
        return createdate;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("Id", getId()).append("RelativeNo", getRelativeNo())
                .append("PaymentType", getPaymentType())
                .append("PaymentNo", getPaymentNo())
                .append("PaymentUsertype", getPaymentUsertype())
                .append("PaymentUser", getPaymentUser())
                .append("Amount", getAmount()).append("Status", getStatus())
                .append("ExternalNo", getExternalNo())
                .append("Createdate", getCreatedate()).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getId()).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CarBizPaymentDetailDTO == false)
            return false;
        if (this == obj)
            return true;
        CarBizPaymentDetailDTO other = (CarBizPaymentDetailDTO) obj;
        return new EqualsBuilder().append(getId(), other.getId()).isEquals();
    }
}