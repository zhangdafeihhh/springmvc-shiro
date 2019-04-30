package com.zhuanche.dto.financial;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.zhuanche.constants.financial.FinancialConst.GoodsState;
import com.zhuanche.constants.financial.FinancialConst.GoodsTypeSelect;
import com.zhuanche.constants.financial.FinancialConst.SalesTargetSelect;
import com.zhuanche.constants.financial.MileageEnum;
import com.zhuanche.constants.financial.VehicleAgeEnum;
import com.zhuanche.entity.driver.FinancialAdditionalClause;
import com.zhuanche.entity.driver.FinancialGoods;
/**  
 * ClassName:FinancialGoodsParamDTO <br/>  
 * Date:     2019年4月24日 上午10:43:15 <br/>  
 * @author   baiyunlong
 * @version  1.0.0      
 */

public class FinancialGoodsInfoDTO{
	
	private Integer goodsId;

    private Integer basicsVehiclesId;

    private String goodsNumber;

    private String goodsName;

    private Byte salesTarget;

    private Byte goodsType;

    private Integer supplierId;

    private String supplierFullName;

    private Integer cityId;

    private String cityName;

    private Integer channelId;

    private String reason;

    private String expInfo;

    private String pictureUrl;

    private String keyword;

    private Integer vehicleAge;

    private Integer mileage;

    private Integer vehicleProperties;

    private Integer sourceFundsId;

    private Integer leaseTerm;

    private BigDecimal rentEveryTerm;

    private BigDecimal frontMoney;

    private BigDecimal firstRent;

    private BigDecimal securityDeposit;

    private BigDecimal totalPrice;

    private String color;

    private String additionalServicesId;

    private String additionalServicesInfo;

    private Integer vehicleStyle;

    private Integer stock;

    private Byte status;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;
	
	private String vehiclesDetailedName;
	private String goodsTypeName;
	private String salesTargetName;
	private String vehicleAgeName;
	private String mileageName;
	private String statusName;
	private List<FinancialAdditionalClause> financialAdditionalClauses;  
	
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
	public List<FinancialAdditionalClause> getFinancialadditionalclauses() {
		return financialAdditionalClauses;
	}
	public void setFinancialadditionalclauses(List<FinancialAdditionalClause> financialAdditionalClauses) {
		this.financialAdditionalClauses = financialAdditionalClauses;
	}
	public Integer getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Integer goodsId) {
		this.goodsId = goodsId;
	}
	public Integer getBasicsVehiclesId() {
		return basicsVehiclesId;
	}
	public void setBasicsVehiclesId(Integer basicsVehiclesId) {
		this.basicsVehiclesId = basicsVehiclesId;
	}
	public String getGoodsNumber() {
		return goodsNumber;
	}
	public void setGoodsNumber(String goodsNumber) {
		this.goodsNumber = goodsNumber;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public Byte getSalesTarget() {
		return salesTarget;
	}
	public void setSalesTarget(Byte salesTarget) {
		this.salesTarget = salesTarget;
	}
	public Byte getGoodsType() {
		return goodsType;
	}
	public void setGoodsType(Byte goodsType) {
		this.goodsType = goodsType;
	}
	public Integer getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}
	public String getSupplierFullName() {
		return supplierFullName;
	}
	public void setSupplierFullName(String supplierFullName) {
		this.supplierFullName = supplierFullName;
	}
	public Integer getCityId() {
		return cityId;
	}
	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public Integer getChannelId() {
		return channelId;
	}
	public void setChannelId(Integer channelId) {
		this.channelId = channelId;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getExpInfo() {
		return expInfo;
	}
	public void setExpInfo(String expInfo) {
		this.expInfo = expInfo;
	}
	public String getPictureUrl() {
		return pictureUrl;
	}
	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public Integer getVehicleAge() {
		return vehicleAge;
	}
	public void setVehicleAge(Integer vehicleAge) {
		this.vehicleAge = vehicleAge;
	}
	public Integer getMileage() {
		return mileage;
	}
	public void setMileage(Integer mileage) {
		this.mileage = mileage;
	}
	public Integer getVehicleProperties() {
		return vehicleProperties;
	}
	public void setVehicleProperties(Integer vehicleProperties) {
		this.vehicleProperties = vehicleProperties;
	}
	public Integer getSourceFundsId() {
		return sourceFundsId;
	}
	public void setSourceFundsId(Integer sourceFundsId) {
		this.sourceFundsId = sourceFundsId;
	}
	public Integer getLeaseTerm() {
		return leaseTerm;
	}
	public void setLeaseTerm(Integer leaseTerm) {
		this.leaseTerm = leaseTerm;
	}
	public BigDecimal getRentEveryTerm() {
		return rentEveryTerm;
	}
	public void setRentEveryTerm(BigDecimal rentEveryTerm) {
		this.rentEveryTerm = rentEveryTerm;
	}
	public BigDecimal getFrontMoney() {
		return frontMoney;
	}
	public void setFrontMoney(BigDecimal frontMoney) {
		this.frontMoney = frontMoney;
	}
	public BigDecimal getFirstRent() {
		return firstRent;
	}
	public void setFirstRent(BigDecimal firstRent) {
		this.firstRent = firstRent;
	}
	public BigDecimal getSecurityDeposit() {
		return securityDeposit;
	}
	public void setSecurityDeposit(BigDecimal securityDeposit) {
		this.securityDeposit = securityDeposit;
	}
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getAdditionalServicesId() {
		return additionalServicesId;
	}
	public void setAdditionalServicesId(String additionalServicesId) {
		this.additionalServicesId = additionalServicesId;
	}
	public String getAdditionalServicesInfo() {
		return additionalServicesInfo;
	}
	public void setAdditionalServicesInfo(String additionalServicesInfo) {
		this.additionalServicesInfo = additionalServicesInfo;
	}
	public Integer getVehicleStyle() {
		return vehicleStyle;
	}
	public void setVehicleStyle(Integer vehicleStyle) {
		this.vehicleStyle = vehicleStyle;
	}
	public Integer getStock() {
		return stock;
	}
	public void setStock(Integer stock) {
		this.stock = stock;
	}
	public Byte getStatus() {
		return status;
	}
	public void setStatus(Byte status) {
		this.status = status;
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
	public List<FinancialAdditionalClause> getFinancialAdditionalClauses() {
		return financialAdditionalClauses;
	}
	public void setFinancialAdditionalClauses(List<FinancialAdditionalClause> financialAdditionalClauses) {
		this.financialAdditionalClauses = financialAdditionalClauses;
	}
	
}
  
