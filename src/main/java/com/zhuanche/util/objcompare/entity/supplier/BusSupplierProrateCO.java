package com.zhuanche.util.objcompare.entity.supplier;

import com.zhuanche.util.objcompare.FieldNote;

import lombok.Data;

/**
 * @ClassName: BusSupplierProrateCO
 * @Description: 供应商分佣信息
 * @author: yanyunpeng
 * @date: 2019年1月17日 下午7:10:40
 * 
 */
@Data
public class BusSupplierProrateCO {

	@FieldNote("结算比例")
	private String supplierRate;

	@FieldNote("生效起始时间")
	private String startTime;

	@FieldNote("生效结束时间")
	private String endTime;

}
