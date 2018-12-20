package com.zhuanche.dto.busManage;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * @ClassName: BusSupplierProrateDTO
 * @Description: 供应商返点信息
 * @author: yanyunpeng
 * @date: 2018年12月17日 上午11:43:55
 * 
 */
public class BusSupplierRebateDTO {

	/** 主键 **/
	private Integer id;

	/** 供应商ID **/
	private Integer supplierId;

	/** 返佣比例 **/
	@DecimalMin(value = "0", inclusive = false, message = "返佣比例必须大于0")
	@DecimalMax(value = "100", inclusive = false, message = "返佣比例必须小于100")
	private BigDecimal rebateRate;

	/** 最小金额 **/
	@DecimalMin(value = "0", inclusive = true, message = "最小金额必须大于0")
	private BigDecimal minMoney;

	/** 最大金额 **/
	@DecimalMin(value = "0", inclusive = false, message = "最大金额必须大于0")
	private BigDecimal maxMoney;

	/** 开始时间 **/
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date startTime;

	/** 结束时间 **/
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date endTime;

	/** 创建时间 **/
	private Date createTime;

	/** 创建人 **/
	private String createName;

	/** 更新时间 **/
	private Date updateTime;

	/** 更新人 **/
	private String updateName;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public BigDecimal getRebateRate() {
		return rebateRate;
	}

	public void setRebateRate(BigDecimal rebateRate) {
		this.rebateRate = rebateRate;
	}

	public BigDecimal getMinMoney() {
		return minMoney;
	}

	public void setMinMoney(BigDecimal minMoney) {
		this.minMoney = minMoney;
	}

	public BigDecimal getMaxMoney() {
		return maxMoney;
	}

	public void setMaxMoney(BigDecimal maxMoney) {
		this.maxMoney = maxMoney;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdateName() {
		return updateName;
	}

	public void setUpdateName(String updateName) {
		this.updateName = updateName;
	}

}
