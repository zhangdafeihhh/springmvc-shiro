package com.zhuanche.dto.busManage;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * @ClassName: BusSupplierProrateDTO
 * @Description: 供应商分佣信息
 * @author: yanyunpeng
 * @date: 2018年12月17日 上午11:43:55
 * 
 */
public class BusSupplierProrateDTO {

	/** 主键 **/
	private Long id;

	/** 供应商ID **/
	private Integer supplierId;

	/** 供应商比例 **/
	@DecimalMin(value = "0", inclusive = true, message = "供应商比例必须大于或等于0")
	@DecimalMax(value = "100", inclusive = true, message = "供应商比例必须小于或等于100")
	private BigDecimal supplierRate;

	/** 协议开始时间 **/
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date startTime;

	/** 协议结束日期 **/
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date endTime;

	/** 创建人 **/
	private String createName;

	/** 更新人 **/
	private String updateName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public BigDecimal getSupplierRate() {
		return supplierRate;
	}

	public void setSupplierRate(BigDecimal supplierRate) {
		this.supplierRate = supplierRate;
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

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public String getUpdateName() {
		return updateName;
	}

	public void setUpdateName(String updateName) {
		this.updateName = updateName;
	}

}
