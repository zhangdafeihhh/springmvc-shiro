package com.zhuanche.dto.financial;

import com.zhuanche.constants.financial.FinancialConst.ClueStatus;
import com.zhuanche.constants.financial.FinancialConst.GoodsTypeSelect;
import com.zhuanche.entity.driver.FinancialClue;

/**  
 * ClassName:FinancialClueDTO <br/>  
 * Date:     2019年4月24日 上午11:57:49 <br/>  
 * @author   baiyunlong
 * @version  1.0.0      
 */
public class FinancialClueDTO extends FinancialClue{
	private String statusName;
	private String whetherName;
    private Byte goodsType;
	private String goodsTypeName;
	public String getStatusName() {
		if (getStatus()==ClueStatus.PROCESSED) {
			statusName="已分发";
		}
		if (getStatus()==ClueStatus.UNTREATED) {
			statusName="待分发";
		}
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public String getWhetherName() {
		if (getWhether()==0) {
			whetherName="否";
		}
		if (getWhether()==1) {
			whetherName="是";
		}
		return whetherName;
	}
	public void setWhetherName(String whetherName) {
		this.whetherName = whetherName;
	}
	public Byte getGoodsType() {
		return goodsType;
	}
	public void setGoodsType(Byte goodsType) {
		this.goodsType = goodsType;
	}
	public String getGoodsTypeName() {
		if (getGoodsType()!=null && getGoodsType()==GoodsTypeSelect.GOODS_TYPE_FINANCING) {
			goodsTypeName="融租";
		}
		if (getGoodsType()!=null && getGoodsType()==GoodsTypeSelect.GOODS_TYPE_RENT) {
			goodsTypeName="经租";
		}
		return goodsTypeName;
	}
	public void setGoodsTypeName(String goodsTypeName) {
		this.goodsTypeName = goodsTypeName;
	}
	
}
  
