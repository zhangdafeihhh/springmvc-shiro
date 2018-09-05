package com.zhuanche.entity.creditCard;

public class RegisterCard {

	String pan; // 卡号 （必填）
	String phoneNO; // 手机号码（必填）
	String customerId; // 客户号（系统用户ID）（必填）
	String externalRefNumber; // 外部跟踪号（订单编号）（必填）
	String cardHolderName; // 持卡人姓名（必填）
	String cardHolderId; // 证件号
	String expiredDate; // 卡有效期（必填）
	String cvv2; // 卡校验码（必填）

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getPhoneNO() {
		return phoneNO;
	}

	public void setPhoneNO(String phoneNO) {
		this.phoneNO = phoneNO;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getExternalRefNumber() {
		return externalRefNumber;
	}

	public void setExternalRefNumber(String externalRefNumber) {
		this.externalRefNumber = externalRefNumber;
	}

	public String getCardHolderName() {
		return cardHolderName;
	}

	public void setCardHolderName(String cardHolderName) {
		this.cardHolderName = cardHolderName;
	}

	public String getCardHolderId() {
		return cardHolderId;
	}

	public void setCardHolderId(String cardHolderId) {
		this.cardHolderId = cardHolderId;
	}

	public String getExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(String expiredDate) {
		this.expiredDate = expiredDate;
	}

	public String getCvv2() {
		return cvv2;
	}

	public void setCvv2(String cvv2) {
		this.cvv2 = cvv2;
	}


}
