package com.zhuanche.dto.busManage;

import com.zhuanche.dto.BaseDTO;

/**
 * @ClassName: BusCarDTO
 * @Description:查询巴士可用车辆(接口参数接收DTO)
 * @author: yanyunpeng
 * @date: 2018年10月25日 上午11:39:47
 * 
 */
public class BusCarDTO extends BaseDTO {

	private static final long serialVersionUID = 1471996705747997478L;

	/** 订单号 **/
	private String orderNo;
	/** 车型类别ID **/
	private Integer groupId;
	/** 城市ID **/
	private Integer cityId;
	/** 供应商ID **/
	private Integer supplierId;
	/** 车牌号 **/
	private String licensePlates;

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

	public String getLicensePlates() {
		return licensePlates;
	}

	public void setLicensePlates(String licensePlates) {
		this.licensePlates = licensePlates;
	}

	public void setPageSize(Integer pageSize) {
		super.pageSize = pageSize == null ? 20 : pageSize;
	}

}
