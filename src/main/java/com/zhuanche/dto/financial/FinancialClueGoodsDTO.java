package com.zhuanche.dto.financial;
import com.zhuanche.constants.financial.MileageEnum;
import com.zhuanche.constants.financial.VehicleAgeEnum;
import com.zhuanche.constants.financial.FinancialConst.GoodsState;
import com.zhuanche.constants.financial.FinancialConst.GoodsTypeSelect;
import com.zhuanche.constants.financial.FinancialConst.SalesTargetSelect;
import com.zhuanche.constants.financial.FinancialConst.VehicleProperties;
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
	private String vehiclePropertiesName; 
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
	public String getVehiclePropertiesName() {
		if (getVehicleProperties()==VehicleProperties.VehicleProperties1) {
			vehiclePropertiesName="非营运";
		}
		if (getVehicleProperties()==VehicleProperties.VehicleProperties2) {
			vehiclePropertiesName="出租客运";
		}
		if (getVehicleProperties()==VehicleProperties.VehicleProperties3) {
			vehiclePropertiesName="租赁";
		}
		if (getVehicleProperties()==VehicleProperties.VehicleProperties4) {
			vehiclePropertiesName="营转非";
		}
		if (getVehicleProperties()==VehicleProperties.VehicleProperties5) {
			vehiclePropertiesName="出租转非";
		}
		return vehiclePropertiesName;
	}
	public void setVehiclePropertiesName(String vehiclePropertiesName) {
		this.vehiclePropertiesName = vehiclePropertiesName;
	}
	
}
  
