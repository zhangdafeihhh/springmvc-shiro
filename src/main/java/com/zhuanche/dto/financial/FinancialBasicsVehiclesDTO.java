package com.zhuanche.dto.financial;

import java.math.BigDecimal;
import java.util.Date;

import com.zhuanche.common.syslog.Column;
import com.zhuanche.constants.financial.EnergyTypeEnum;
import com.zhuanche.constants.financial.FinancialConst.EnableStatusSelect;
import com.zhuanche.entity.driver.FinancialBasicsVehicles;

/**  
 * ClassName:FinancialBasicsVehiclesDTO <br/>  
 * Date:     2019年4月23日 下午8:18:35 <br/>  
 * @author   baiyunlong
 * @version  1.0.0      
 */
public class FinancialBasicsVehiclesDTO{
    @Column(desc="基础车型库ID")
	private Integer basicsVehiclesId;
    @Column(desc="车型车款名称")
    private String vehiclesDetailedName;
    @Column(desc="品牌id")
    private Long brandId;
    @Column(desc="型号id")
    private Long modelId;
    @Column(desc="车款")
    private String vehicleStyle;
    @Column(desc="年款")
    private String yearStyle;
    @Column(desc="能源类型")
    private Integer energyType;
    @Column(desc="变速箱")
    private Integer variableBox;
    @Column(desc="厂家指导价")
    private BigDecimal guidancePrice;
    @Column(desc="排量")
    private Double discharge;
    @Column(desc="续航里程")
    private Double mileage;
    @Column(desc="汽车之家")
    private String autoHomeUrl;
    @Column(desc="长宽高")
    private String lengthWidthHeight;
    @Column(desc="质保")
    private String qualityAssurance;
    @Column(desc="轴距")
    private Integer wheelbase;
    @Column(desc="环保标准")
    private String environmentalProtectionStandard;
    @Column(desc="快充时间(小时)")
    private Double fastChargingTime;
    @Column(desc="慢充时间(小时)")
    private Double slowChargingTime;
    @Column(desc="快充电量 百分比")
    private Double fastPercentage;

    private Byte enableStatus;
    private String imgUrl;
    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;
    
	private String brandName;
	
	private String vehicleName;
	
	private String variableBoxName;
	
	private String energyTypeName;
    @Column(desc="状态")
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
		if (getVariableBox()!=null && getVariableBox()==1) {
			variableBoxName="自动";
		}
		if (getVariableBox()!=null && getVariableBox()==0) {
			variableBoxName="手动";
		}
		return variableBoxName;
	}

	public void setVariableBoxName(String variableBoxName) {
		this.variableBoxName = variableBoxName;
	}

	public String getEnergyTypeName() {
		if (getEnergyType()!=null) {
			EnergyTypeEnum energyTypeEnum=EnergyTypeEnum.indexOf(getEnergyType());
			if (energyTypeEnum!=null) {
				energyTypeName=energyTypeEnum.getEnergyTypeName();
			}
		}
		return energyTypeName;
	}

	public void setEnergyTypeName(String energyTypeName) {
		this.energyTypeName = energyTypeName;
	}

	public String getEnableStatusName() {
		if (getEnableStatus()!=null && getEnableStatus()==EnableStatusSelect.DISCONTINUE) {
			enableStatusName="停用";
		}
		if (getEnableStatus()!=null && getEnableStatus()==EnableStatusSelect.ENABLESTATUS) {
			enableStatusName="启用";
		}
		return enableStatusName;
	}

	public void setEnableStatusName(String enableStatusName) {
		this.enableStatusName = enableStatusName;
	}

	public Integer getBasicsVehiclesId() {
		return basicsVehiclesId;
	}

	public void setBasicsVehiclesId(Integer basicsVehiclesId) {
		this.basicsVehiclesId = basicsVehiclesId;
	}

	public String getVehiclesDetailedName() {
		return vehiclesDetailedName;
	}

	public void setVehiclesDetailedName(String vehiclesDetailedName) {
		this.vehiclesDetailedName = vehiclesDetailedName;
	}

	public Long getBrandId() {
		return brandId;
	}

	public void setBrandId(Long brandId) {
		this.brandId = brandId;
	}

	public Long getModelId() {
		return modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}

	public String getVehicleStyle() {
		return vehicleStyle;
	}

	public void setVehicleStyle(String vehicleStyle) {
		this.vehicleStyle = vehicleStyle;
	}

	public String getYearStyle() {
		return yearStyle;
	}

	public void setYearStyle(String yearStyle) {
		this.yearStyle = yearStyle;
	}

	public Integer getEnergyType() {
		return energyType;
	}

	public void setEnergyType(Integer energyType) {
		this.energyType = energyType;
	}

	public Integer getVariableBox() {
		return variableBox;
	}

	public void setVariableBox(Integer variableBox) {
		this.variableBox = variableBox;
	}

	public BigDecimal getGuidancePrice() {
		return guidancePrice;
	}

	public void setGuidancePrice(BigDecimal guidancePrice) {
		this.guidancePrice = guidancePrice;
	}

	public String getAutoHomeUrl() {
		return autoHomeUrl;
	}

	public void setAutoHomeUrl(String autoHomeUrl) {
		this.autoHomeUrl = autoHomeUrl;
	}

	public String getLengthWidthHeight() {
		return lengthWidthHeight;
	}

	public void setLengthWidthHeight(String lengthWidthHeight) {
		this.lengthWidthHeight = lengthWidthHeight;
	}

	public String getQualityAssurance() {
		return qualityAssurance;
	}

	public void setQualityAssurance(String qualityAssurance) {
		this.qualityAssurance = qualityAssurance;
	}

	public Integer getWheelbase() {
		return wheelbase;
	}

	public void setWheelbase(Integer wheelbase) {
		this.wheelbase = wheelbase;
	}

	public String getEnvironmentalProtectionStandard() {
		return environmentalProtectionStandard;
	}

	public void setEnvironmentalProtectionStandard(String environmentalProtectionStandard) {
		this.environmentalProtectionStandard = environmentalProtectionStandard;
	}

	public Double getDischarge() {
		return discharge;
	}

	public void setDischarge(Double discharge) {
		this.discharge = discharge;
	}

	public Double getMileage() {
		return mileage;
	}

	public void setMileage(Double mileage) {
		this.mileage = mileage;
	}

	public Double getFastChargingTime() {
		return fastChargingTime;
	}

	public void setFastChargingTime(Double fastChargingTime) {
		this.fastChargingTime = fastChargingTime;
	}

	public Double getSlowChargingTime() {
		return slowChargingTime;
	}

	public void setSlowChargingTime(Double slowChargingTime) {
		this.slowChargingTime = slowChargingTime;
	}

	public Double getFastPercentage() {
		return fastPercentage;
	}

	public void setFastPercentage(Double fastPercentage) {
		this.fastPercentage = fastPercentage;
	}

	public Byte getEnableStatus() {
		return enableStatus;
	}

	public void setEnableStatus(Byte enableStatus) {
		this.enableStatus = enableStatus;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	
}
  
