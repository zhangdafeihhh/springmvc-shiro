package com.zhuanche.controller.financial;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.financial.FinancialBasicsVehiclesDTO;
import com.zhuanche.entity.driver.FinancialBasicsVehicles;
import com.zhuanche.serv.financial.FinancialBasicsVehiclesService;

/**  
 * ClassName:FinancialBasicsVehiclesController <br/>  
 * Date:     2019年4月23日 下午6:50:17 <br/>  
 * @author   baiyunlong
 * @version  1.0.0      
 */
@RestController
@RequestMapping("/financialBasicsVehicles")
public class FinancialBasicsVehiclesController {
    private static final Logger logger = LoggerFactory.getLogger(FinancialBasicsVehiclesController.class);
    
	@Autowired
    private FinancialBasicsVehiclesService financialBasicsVehiclesService;
    /**
     * queryFinancialBasicsVehiclesForList:(查询车型库列表信息). <br/>  
     * @param page
     * @param pageSize
     * @param vehiclesDetailedName
     * @param energyType
     * @return
     */
	@RequestMapping(value = "/queryFinancialBasicsVehiclesForList")
	public AjaxResponse queryFinancialBasicsVehiclesForList(
			@Verify(param = "page", rule = "required|min(1)") Integer page,
			@Verify(param = "pageSize", rule = "required|min(10)") Integer pageSize,
			String vehiclesDetailedName,
			Integer energyType
			) {
		logger.info("FinancialBasicsVehiclesController--getFinancialBasicsVehiclesList--查询车型库信息，入参：page--{}，pageSize--{}，vehiclesDetailedName--{}，energyType--{}",page,
				pageSize,vehiclesDetailedName,energyType);
		try{
			PageDTO pageDTO = financialBasicsVehiclesService.queryFinancialBasicsVehiclesForList(page,
					pageSize,vehiclesDetailedName,energyType);
			return AjaxResponse.success(pageDTO);
		}catch(Exception e){
			logger.error("FinancialBasicsVehiclesController--getFinancialBasicsVehiclesList--查询车型库信息，入参：page--{}，pageSize--{}，vehiclesDetailedName--{}，energyType--{}",page,
					pageSize,vehiclesDetailedName,energyType);
			return AjaxResponse.fail(RestErrorCode.QUERY_BASICSVEHICLE_ERROR);
		}
	}
	
	/**
	 * saveFinancialBasicsVehicles:(添加). <br/>  
	 * @param vehiclesDetailedName
	 * @param brandId
	 * @param modelId
	 * @param vehicleStyle
	 * @param yearStyle
	 * @param energyType
	 * @param variableBox
	 * @param guidancePrice
	 * @param discharge
	 * @param mileage
	 * @param autoHomeUrl
	 * @param lengthWidthHeight
	 * @param qualityAssurance
	 * @param wheelbase
	 * @param environmentalProtectionStandard
	 * @param fastChargingTime
	 * @param slowChargingTime
	 * @param fastPercentage
	 * @param enableStatus
	 * @return
	 */
	@RequestMapping(value = "/saveFinancialBasicsVehicles")
	public AjaxResponse saveFinancialBasicsVehicles(
			@Verify(param = "vehiclesDetailedName", rule = "required")String vehiclesDetailedName,
			@Verify(param = "brandId", rule = "required|min(1)")Integer brandId,
			@Verify(param = "modelId", rule = "required|min(1)")Integer modelId,
			@Verify(param = "vehicleStyle", rule = "required")String vehicleStyle,
			@Verify(param = "yearStyle", rule = "required")String yearStyle,
			@Verify(param = "energyType", rule = "required")Integer energyType,
			@Verify(param = "variableBox", rule = "required|min(1)")Integer variableBox,
			@Verify(param = "guidancePrice", rule = "required")BigDecimal guidancePrice,
			@Verify(param = "discharge", rule = "min(1)")Integer discharge,
			@Verify(param = "mileage", rule = "min(1)")Integer mileage,
			String autoHomeUrl,
			@Verify(param = "lengthWidthHeight", rule = "required")String lengthWidthHeight,
			@Verify(param = "qualityAssurance", rule = "required")String qualityAssurance,
			@Verify(param = "wheelbase", rule = "required")Integer wheelbase,
			String environmentalProtectionStandard,
			Integer fastChargingTime,
			Integer slowChargingTime,
			Double fastPercentage
			) {
		  logger.info("");
		  FinancialBasicsVehicles financialBasicsVehicles=new FinancialBasicsVehicles();
		  financialBasicsVehicles.setVehiclesDetailedName(vehiclesDetailedName);
		  financialBasicsVehicles.setBrandId(brandId);
		  financialBasicsVehicles.setModelId(modelId);
		  financialBasicsVehicles.setVehicleStyle(vehicleStyle);
		  financialBasicsVehicles.setYearStyle(yearStyle);
		  financialBasicsVehicles.setEnergyType(energyType);
		  financialBasicsVehicles.setVariableBox(variableBox);
		  financialBasicsVehicles.setGuidancePrice(guidancePrice);
		  financialBasicsVehicles.setDischarge(discharge);
		  financialBasicsVehicles.setMileage(mileage);
		  financialBasicsVehicles.setAutoHomeUrl(autoHomeUrl);
		  financialBasicsVehicles.setLengthWidthHeight(lengthWidthHeight);
		  financialBasicsVehicles.setQualityAssurance(qualityAssurance);
		  financialBasicsVehicles.setWheelbase(wheelbase);
		  financialBasicsVehicles.setEnvironmentalProtectionStandard(environmentalProtectionStandard);
		  financialBasicsVehicles.setFastChargingTime(fastChargingTime);
		  financialBasicsVehicles.setSlowChargingTime(slowChargingTime);
		  financialBasicsVehicles.setFastPercentage(fastPercentage);
		  int i = financialBasicsVehiclesService.saveFinancialBasicsVehicles(financialBasicsVehicles);
		  return AjaxResponse.success(true);
	}
	
	/**
	 * queryFinancialBasicsVehiclesById:(查询详情). <br/>  
	 * @author baiyunlong
	 * @param basicsVehiclesId
	 * @return
	 */
	@RequestMapping(value = "/queryFinancialBasicsVehiclesById")
	public AjaxResponse queryFinancialBasicsVehiclesById(
			Integer basicsVehiclesId
			) {
		  logger.info("");
		  FinancialBasicsVehiclesDTO financialBasicsVehiclesDTO = financialBasicsVehiclesService.queryFinancialBasicsVehiclesById(basicsVehiclesId);
		  return AjaxResponse.success(financialBasicsVehiclesDTO);
	}
	
	/**
	 * updateFinancialBasicsVehicles:(修改). <br/>  
	 * @author baiyunlong
	 * @param basicsVehiclesId
	 * @param vehiclesDetailedName
	 * @param brandId
	 * @param modelId
	 * @param vehicleStyle
	 * @param yearStyle
	 * @param energyType
	 * @param variableBox
	 * @param guidancePrice
	 * @param discharge
	 * @param mileage
	 * @param autoHomeUrl
	 * @param lengthWidthHeight
	 * @param qualityAssurance
	 * @param wheelbase
	 * @param environmentalProtectionStandard
	 * @param fastChargingTime
	 * @param slowChargingTime
	 * @param fastPercentage
	 * @param enableStatus
	 * @return
	 */
	@RequestMapping(value = "/updateFinancialBasicsVehicles")
	public AjaxResponse updateFinancialBasicsVehicles(
			Integer basicsVehiclesId,
			String vehiclesDetailedName,
			Integer brandId,
			Integer modelId,
			String vehicleStyle,
			String yearStyle,
			Byte energyType,
			Integer variableBox,
			BigDecimal guidancePrice,
			Integer discharge,
			Integer mileage,
			String autoHomeUrl,
			String lengthWidthHeight,
			String qualityAssurance,
			Integer wheelbase,
			String environmentalProtectionStandard,
			Integer fastChargingTime,
			Integer slowChargingTime,
			Double fastPercentage,
			Byte enableStatus
			) {
		  logger.info("");
		  FinancialBasicsVehicles financialBasicsVehicles=new FinancialBasicsVehicles();
		  int i = financialBasicsVehiclesService.updateFinancialBasicsVehicles(financialBasicsVehicles);
		  return AjaxResponse.success(true);
	}
}
  
