package com.zhuanche.dto.busManage;

import com.zhuanche.dto.BaseDTO;

/**
 * @ClassName: BusDriverDTO
 * @Description: 查询巴士可用司机(接口参数接收DTO)
 * @author: yanyunpeng
 * @date: 2018年10月30日 下午5:18:17
 * 
 */
public class BusDriverDTO extends BaseDTO {

	private static final long serialVersionUID = -2269670155840044133L;

	// 订单号
	private String orderNo;
	// 车型类别ID（预定车型类别）
	private Integer groupId;
	// 城市ID
	private Integer cityId;
	// 供应商ID
	private Integer supplierId;
	// 司机名称
	private String name;
	// 司机手机号
	private String phone;
	// 业务类型
	private Integer type;
	/** 车型类别 （查询条件）**/
	private Integer groupIdSel;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public void setPageSize(Integer pageSize) {
		super.pageSize = pageSize == null ? 20 : pageSize;
	}

	public Integer getGroupIdSel() {
		return groupIdSel;
	}

	public void setGroupIdSel(Integer groupIdSel) {
		this.groupIdSel = groupIdSel;
	}
}
