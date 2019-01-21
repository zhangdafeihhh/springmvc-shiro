package com.zhuanche.util.objcompare.entity.supplier;

import com.zhuanche.util.objcompare.FieldNote;

import lombok.Data;

/**
 * @ClassName: BusSupplierCommissionInfoCO
 * @Description: 供应商分佣基本信息
 * @author: yanyunpeng
 * @date: 2019年1月18日 上午10:06:15
 * 
 */
@Data
public class BusSupplierCommissionInfoCO {

	@FieldNote("业务类型")
	private String roleType;

	@FieldNote("结算周期")
	private String settleCycle;

	@FieldNote("结算天数")
	private String settleBillCycle;

	@FieldNote("结算方式")
	private String settleType;

	@FieldNote("分佣方式")
	private String shareWay;

	@FieldNote("分佣类型")
	private String shareType;

	@FieldNote("是否返点")
	private String isRebate;

	@FieldNote("返点条件")
	private String rebateType;

	@FieldNote("返点时间")
	private String rebateCycle;

	@FieldNote("是否提前结算")
	private String isAdvance;

	@FieldNote("提前结算条件")
	private String advanceType;

	@FieldNote("触发提前结算的金额")
	private String settleAmount;

}
