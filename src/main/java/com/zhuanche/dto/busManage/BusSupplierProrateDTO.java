package com.zhuanche.dto.busManage;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

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
	@NotNull(message = "供应商比例不能为空")
	@DecimalMin(value = "0", inclusive = false, message = "供应商比例必须大于0")
	@DecimalMax(value = "100", inclusive = false, message = "供应商比例必须小于100")
	private BigDecimal supplierRate;

	/** 协议开始时间 **/
	@NotNull(message = "协议开始时间不能为空")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date startTime;

	/** 协议结束日期 **/
	@NotNull(message = "协议结束时间不能为空")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date endTime;

	/** 人为终结日期 **/
	private Date stopTime;

	/** 状态 0 弃用 1启用 **/
	private Integer status;

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

	public Date getStopTime() {
		return stopTime;
	}

	public void setStopTime(Date stopTime) {
		this.stopTime = stopTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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
