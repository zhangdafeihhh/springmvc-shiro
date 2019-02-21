package com.zhuanche.util.objcompare.entity.supplier;

import com.zhuanche.util.objcompare.FieldNote;

import lombok.Data;

/**
 * @ClassName: BusSupplierProrateDTO
 * @Description: 供应商返点信息
 * @author: yanyunpeng
 * @date: 2018年12月17日 上午11:43:55
 * 
 */
@Data
public class BusSupplierRebateCO {

	@FieldNote("返点比例")
	private String rebateRate;

	@FieldNote("金额")
	private String maxMoney;

	@FieldNote("生效起始时间")
	private String startTime;

	@FieldNote("生效结束时间")
	private String endTime;

}
