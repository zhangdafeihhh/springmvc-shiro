package com.zhuanche.dto.financial;
import com.zhuanche.constants.financial.FinancialConst.GoodsState;
import com.zhuanche.constants.financial.FinancialConst.GoodsTypeSelect;
import com.zhuanche.constants.financial.FinancialConst.SalesTargetSelect;
import com.zhuanche.constants.financial.MileageEnum;
import com.zhuanche.constants.financial.VehicleAgeEnum;
import com.zhuanche.entity.driver.FinancialGoods;
/**  
 * ClassName:FinancialGoodsParamDTO <br/>  
 * Date:     2019年4月24日 上午10:43:15 <br/>  
 * @author   baiyunlong
 * @version  1.0.0      
 */

public class FinancialGoodsDTO extends FinancialGoods{
	
	private String vehiclesDetailedName;
	private String goodsTypeName;
	private String salesTargetName;
	private String vehicleAgeName;
	private String mileageName;
	private String statusName;
	
	
	public String getVehiclesDetailedName() {
		return vehiclesDetailedName;
	}
	public void setVehiclesDetailedName(String vehiclesDetailedName) {
		this.vehiclesDetailedName = vehiclesDetailedName;
	}
	public String getGoodsTypeName() {
		if (getGoodsType()==GoodsTypeSelect.GOODS_TYPE_FINANCING) {
			goodsTypeName="融租";
		}
		if (getGoodsType()==GoodsTypeSelect.GOODS_TYPE_RENT) {
			goodsTypeName="经租";
		}
		return goodsTypeName;
	}
	public void setGoodsTypeName(String goodsTypeName) {
		this.goodsTypeName = goodsTypeName;
	}
	public String getSalesTargetName() {
		if (getSalesTarget()==SalesTargetSelect.SALES_TARGET_TOC) {
			salesTargetName="ToC";
		}
		if (getSalesTarget()==SalesTargetSelect.SALES_TARGET_TOB) {
			salesTargetName="ToB";
		}
		return salesTargetName;
	}
	public void setSalesTargetName(String salesTargetName) {
		this.salesTargetName = salesTargetName;
	}
	public String getVehicleAgeName() {
		VehicleAgeEnum vehicleAgeEnum=VehicleAgeEnum.indexOf(getVehicleAge());
		if (vehicleAgeEnum!=null) {
			vehicleAgeName=vehicleAgeEnum.getVehicleAge();
		}
		return vehicleAgeName;
	}
	public void setVehicleAgeName(String vehicleAgeName) {
		this.vehicleAgeName = vehicleAgeName;
	}
	public String getMileageName() {
		MileageEnum mileageEnum=MileageEnum.indexOf(getMileage());
		if (mileageEnum!=null) {
			mileageName=mileageEnum.getMileage();
		}
		return mileageName;
	}
	public void setMileageName(String mileageName) {
		this.mileageName = mileageName;
	}
	public String getStatusName() {
		if (getStatus()==GoodsState.STAY_ON_THE_SHELF) {
			statusName="已上架";
		}
		if (getStatus()==GoodsState.ON_SHELVES) {
			statusName="已下架";	
		}
		if (getStatus()==GoodsState.DELETE) {
			statusName="删除";	
		}
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	
}
  
