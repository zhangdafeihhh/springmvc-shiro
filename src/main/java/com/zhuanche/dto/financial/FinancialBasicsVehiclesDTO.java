package com.zhuanche.dto.financial;

import com.zhuanche.constants.financial.EnergyTypeEnum;
import com.zhuanche.constants.financial.FinancialConst.EnableStatusSelect;
import com.zhuanche.entity.driver.FinancialBasicsVehicles;

/**  
 * ClassName:FinancialBasicsVehiclesDTO <br/>  
 * Date:     2019年4月23日 下午8:18:35 <br/>  
 * @author   baiyunlong
 * @version  1.0.0      
 */
public class FinancialBasicsVehiclesDTO extends FinancialBasicsVehicles{
	
	private String brandName;
	
	private String vehicleName;
	
	private String variableBoxName;
	
	private String energyTypeName;
	
	private String enableStatusName;

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getVehicleName() {
		return vehicleName;
	}

	public void setVehicleName(String vehicleName) {
		this.vehicleName = vehicleName;
	}

	public String getVariableBoxName() {
		if (getVariableBox()==1) {
			variableBoxName="自动";
		}
		if (getVariableBox()==0) {
			variableBoxName="手动";
		}
		return variableBoxName;
	}

	public void setVariableBoxName(String variableBoxName) {
		this.variableBoxName = variableBoxName;
	}

	public String getEnergyTypeName() {
		EnergyTypeEnum energyTypeEnum=EnergyTypeEnum.indexOf(getEnableStatus());
		if (energyTypeEnum!=null) {
			energyTypeName=energyTypeEnum.getEnergyTypeName();
		}
		return energyTypeName;
	}

	public void setEnergyTypeName(String energyTypeName) {
		this.energyTypeName = energyTypeName;
	}

	public String getEnableStatusName() {
		if (getEnableStatus()==EnableStatusSelect.DISCONTINUE) {
			enableStatusName="停用";
		}
		if (getEnableStatus()==EnableStatusSelect.ENABLESTATUS) {
			enableStatusName="启用";
		}
		return enableStatusName;
	}

	public void setEnableStatusName(String enableStatusName) {
		this.enableStatusName = enableStatusName;
	}
	
}
  
