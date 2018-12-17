package com.zhuanche.dto.busManage;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import com.zhuanche.common.web.datavalidate.custom.InArray;

/**
 * @ClassName: BusSupplierCommissionInfoDTO
 * @Description: 供应商分佣基本信息
 * @author: yanyunpeng
 * @date: 2018年12月17日 上午10:25:30
 * 
 */
/**
 * @ClassName: BusSupplierCommissionInfoDTO
 * @Description: TODO
 * @author: yanyunpeng
 * @date: 2018年12月17日 下午5:27:40
 * 
 */
public class BusSupplierCommissionInfoDTO {

	/** 供应商分佣基本信息主键 **/
	private Long id;

	/** 供应商ID **/
	private Integer supplierId;

	/** 是否允许提前结算 0 不 1可以 **/
	@NotNull(message = "是否允许提前结算不能为空")
	@InArray(values = { "0", "1" }, message = "是否允许提前结算不在有效值范围内")
	private Integer isAdvance;

	/** 提前结算方式 0金额 **/
	@NotNull(message = "提前结算方式不能为空")
	@InArray(values = { "0" }, message = "提前结算方式不在有效值范围内")
	private Integer advanceType;

	/** 触发提前结算的金额 允许结算 >0 ；不允许 = 0 **/
	@NotNull(message = "触发提前结算的金额允许结算不能为空")
	@DecimalMin(value = "0", inclusive = false, message = "触发提前结算的金额 允许结算 >0 ；不允许 = 0")
	private BigDecimal settleAmount;

	/** 是否返点 0是 1不 **/
	@NotNull(message = "是否返点不能为空")
	@InArray(values = { "0", "1" }, message = "是否返点不在有效值范围内")
	private Integer isRebate;

	/** 账单周期 0周 1月 2季 **/
	@NotNull(message = "账单周期不能为空")
	@InArray(values = { "0", "1", "2" }, message = "账单周期不在有效值范围内")
	private Integer settleCycle;

	/** 返点类型 0金额 **/
	@NotNull(message = "返点类型不能为空")
	@InArray(values = { "0" }, message = "返点类型不在有效值范围内")
	private Integer rebateType;

	/** 返点周期0 月 1周 **/
	@NotNull(message = "返点周期不能为空")
	@InArray(values = { "0", "1" }, message = "返点周期不在有效值范围内")
	private Integer rebateCycle;

	/** 结算周期天数 >0 **/
	@NotNull(message = "结算周期天数不能为空")
	@DecimalMin(value = "0", inclusive = false, message = "结算周期天数必须大于0")
	private Integer settleBillCycle;

	/** 结算方式 0 手动 1自动 **/
	@NotNull(message = "结算方式不能为空")
	@InArray(values = { "0", "1" }, message = "结算方式不在有效值范围内")
	private Integer settleType;

	/** 业务类型 0 大巴车 **/
	@NotNull(message = "业务类型不能为空")
	@InArray(values = { "0" }, message = "业务类型不在有效值范围内")
	private Integer roleType;

	/** 分佣类别 0订单 **/
	@NotNull(message = "分佣类别不能为空")
	@InArray(values = { "0" }, message = "分佣类别不在有效值范围内")
	private Integer shareType;

	/** 分佣方式 0 固定比例 **/
	@NotNull(message = "分佣方式不能为空")
	@InArray(values = { "0" }, message = "分佣方式不在有效值范围内")
	private Integer shareWay;

	/** 创建人 **/
	private String createName;

	/** 更新人（和更新人一致） **/
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

	public Integer getIsAdvance() {
		return isAdvance;
	}

	public void setIsAdvance(Integer isAdvance) {
		this.isAdvance = isAdvance;
	}

	public Integer getAdvanceType() {
		return advanceType;
	}

	public void setAdvanceType(Integer advanceType) {
		this.advanceType = advanceType;
	}

	public BigDecimal getSettleAmount() {
		return settleAmount;
	}

	public void setSettleAmount(BigDecimal settleAmount) {
		this.settleAmount = settleAmount;
	}

	public Integer getIsRebate() {
		return isRebate;
	}

	public void setIsRebate(Integer isRebate) {
		this.isRebate = isRebate;
	}

	public Integer getSettleCycle() {
		return settleCycle;
	}

	public void setSettleCycle(Integer settleCycle) {
		this.settleCycle = settleCycle;
	}

	public Integer getRebateType() {
		return rebateType;
	}

	public void setRebateType(Integer rebateType) {
		this.rebateType = rebateType;
	}

	public Integer getRebateCycle() {
		return rebateCycle;
	}

	public void setRebateCycle(Integer rebateCycle) {
		this.rebateCycle = rebateCycle;
	}

	public Integer getSettleBillCycle() {
		return settleBillCycle;
	}

	public void setSettleBillCycle(Integer settleBillCycle) {
		this.settleBillCycle = settleBillCycle;
	}

	public Integer getSettleType() {
		return settleType;
	}

	public void setSettleType(Integer settleType) {
		this.settleType = settleType;
	}

	public Integer getRoleType() {
		return roleType;
	}

	public void setRoleType(Integer roleType) {
		this.roleType = roleType;
	}

	public Integer getShareType() {
		return shareType;
	}

	public void setShareType(Integer shareType) {
		this.shareType = shareType;
	}

	public Integer getShareWay() {
		return shareWay;
	}

	public void setShareWay(Integer shareWay) {
		this.shareWay = shareWay;
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
