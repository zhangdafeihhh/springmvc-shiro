package com.zhuanche.dto.financial;
import com.zhuanche.constants.financial.MileageEnum;
import com.zhuanche.constants.financial.VehicleAgeEnum;
import com.zhuanche.constants.financial.FinancialConst.GoodsState;
import com.zhuanche.constants.financial.FinancialConst.GoodsTypeSelect;
import com.zhuanche.constants.financial.FinancialConst.SalesTargetSelect;
import com.zhuanche.entity.driver.FinancialClueGoods;
/**
 * ClassName:FinancialClueGoodsDTO <br/>  
 * Date:     2019年4月28日 下午3:39:59 <br/>  
 * @author   baiyunlong
 * @version  1.0.0      
 */
public class FinancialClueGoodsDTO extends FinancialClueGoods{
	
	private String vehiclesDetailedName;
	private String goodsTypeName;
	private String salesTargetName;
	private String vehicleAgeName;
	private String mileageName;
	
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
}
  
