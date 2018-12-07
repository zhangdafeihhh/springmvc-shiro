package com.zhuanche.dto.busManage;

import java.util.Date;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.zhuanche.dto.BaseDTO;

/**
 * @ClassName: BusDriverQueryDTO
 * @Description: 巴士司机查询DTO
 * @author: yanyunpeng
 * @date: 2018年11月22日 下午4:23:15
 * 
 */
public class BusDriverQueryDTO extends BaseDTO {

	/**
	 * 导出分组
	 */
	public interface Export {
	}

	private static final long serialVersionUID = -5047520202731725106L;

	/** 城市ID **/
	@NotNull(groups = { Export.class }, message = "城市不能为空")
	private Integer cityId;

	/** 供应商ID **/
	private Integer supplierId;

	/** 车型类型ID **/
	private Integer groupId;

	/** 司机姓名 **/
	private String name;

	/** 司机手机号 **/
	private String phone;

	/** 创建时间开始 **/
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createDateBegin;

	/** 创建时间结束 **/
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createDateEnd;

	// ============================其它查询条件==============================

	/** 司机ID集合 **/
	private Set<Integer> driverIds;

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Date getCreateDateBegin() {
		return createDateBegin;
	}

	public void setCreateDateBegin(Date createDateBegin) {
		this.createDateBegin = createDateBegin;
	}

	public Date getCreateDateEnd() {
		return createDateEnd;
	}

	public void setCreateDateEnd(Date createDateEnd) {
		this.createDateEnd = createDateEnd;
	}

	public Set<Integer> getDriverIds() {
		return driverIds;
	}

	public void setDriverIds(Set<Integer> driverIds) {
		this.driverIds = driverIds;
	}

}
