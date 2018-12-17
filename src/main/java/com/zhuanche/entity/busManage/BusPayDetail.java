package com.zhuanche.entity.busManage;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class BusPayDetail implements Serializable {

	private static final long serialVersionUID = -1329629061137685839L;

	/**
	 * 支付方式 1.个人账户 2.机构账户 3.支付宝 4.微信
	 */
	private String channelCode;
	/**
	 * 用户唯一标识
	 */
	private String channelUniqueUserId;
	/**
	 * 支付状态 0 支付失败 1支付成功 2受理成功 3等待付款 4未知
	 */
	private Integer payStatus;
	/**
	 * 金额
	 */
	private BigDecimal payAmt;
	/**
	 * 用户id
	 */
	private String userId;
	/**
	 * 订单存在标识 0存在 1订单不存在
	 */
	private Integer existed;
	/**
	 * 订单创建日期
	 */
	@JSONField(format="yyyy-MM-dd HH:mm:ss")
	private Date createDate;
	/**
	 * 支付日期
	 */
	@JSONField(format="yyyy-MM-dd HH:mm:ss")
	private Date finishDate;

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public String getChannelUniqueUserId() {
		return channelUniqueUserId;
	}

	public void setChannelUniqueUserId(String channelUniqueUserId) {
		this.channelUniqueUserId = channelUniqueUserId;
	}

	public Integer getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(Integer payStatus) {
		this.payStatus = payStatus;
	}

	public BigDecimal getPayAmt() {
		return payAmt;
	}

	public void setPayAmt(BigDecimal payAmt) {
		this.payAmt = payAmt;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getExisted() {
		return existed;
	}

	public void setExisted(Integer existed) {
		this.existed = existed;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

}
