package com.zhuanche.util.objcompare.entity.supplier;

import com.zhuanche.util.objcompare.FieldNote;

import lombok.Data;

@Data
public class BusSupplierBaseCO {

	@FieldNote("城市")
	private String cityName;

	@FieldNote("供应商名称")
	private String supplierName;

	@FieldNote("企业联系人")
	private String contacts;

	@FieldNote("企业联系人电话 ")
	private String contactsPhone;

	@FieldNote("调度员电话")
	private String dispatcherPhone;

	@FieldNote("加盟类型")
	private String cooperationName;

	@FieldNote("状态")
	private String status;

	@FieldNote("供应商地址")
	private String address;

	@FieldNote("供应商类型")
	private String supplierType;

	@FieldNote("是否客运企业")
	private String enterpriseType;

	@FieldNote("是否双班供应商")
	private String isTwoShifts;

}