package com.zhuanche.controller.financial;

import static com.zhuanche.common.enums.MenuEnum.DRIVER_INFO_LIST_EXPORT;

import java.math.BigDecimal;
import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.syslog.SysLogAnn;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.financial.FinancialBasicsVehiclesDTO;
import com.zhuanche.entity.driver.FinancialBasicsVehicles;
import com.zhuanche.serv.financial.FinancialBasicsVehiclesService;

import mapper.driver.ex.FinancialBasicsVehiclesExMapper;

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
	
	@Autowired
	private FinancialBasicsVehiclesExMapper financialBasicsVehiclesExMapper;
    /**
     * queryFinancialBasicsVehiclesForList:(查询车型库列表信息). <br/>  
     * @param page
     * @param pageSize
     * @param vehiclesDetailedName
     * @param energyType
     * @return
     */
	@RequiresPermissions(value = { "CarTypeManage_look" } )
	@RequestMapping(value = "/queryFinancialBasicsVehiclesForList")
	@MasterSlaveConfigs(configs={ 
			@MasterSlaveConfig(databaseTag="driver-DataSource",mode=DataSourceMode.SLAVE )
	} )
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
	@RequiresPermissions(value = { "CarTypeManage_save" } )
	@RequestMapping(value = "/saveFinancialBasicsVehicles")
	@MasterSlaveConfigs(configs={ 
			@MasterSlaveConfig(databaseTag="driver-DataSource",mode=DataSourceMode.MASTER )
	} )
	@SysLogAnn(module="FinancialBasicsVehicles",methods="save",parameterType="Integer",parameterKey="basicsVehiclesId",objClass=FinancialBasicsVehiclesDTO.class )
	public AjaxResponse saveFinancialBasicsVehicles(
			@Verify(param = "vehiclesDetailedName", rule = "required")String vehiclesDetailedName,
			@Verify(param = "brandId", rule = "required|min(1)")Long brandId,
			@Verify(param = "modelId", rule = "required|min(1)")Long modelId,
			@Verify(param = "vehicleStyle", rule = "required")String vehicleStyle,
			@Verify(param = "yearStyle", rule = "required")String yearStyle,
			@Verify(param = "energyType", rule = "required")Integer energyType,
			@Verify(param = "variableBox", rule = "required")Integer variableBox,
			@Verify(param = "guidancePrice", rule = "required")BigDecimal guidancePrice,
			@Verify(param = "discharge", rule = "min(1)")Integer discharge,
			@Verify(param = "mileage", rule = "min(1)")Integer mileage,
			String autoHomeUrl,
			@Verify(param = "lengthWidthHeight", rule = "required")String lengthWidthHeight,
			@Verify(param = "qualityAssurance", rule = "required")String qualityAssurance,
			@Verify(param = "wheelbase", rule = "required")Integer wheelbase,
			String environmentalProtectionStandard,
			@Verify(param = "imgUrl", rule = "required") String imgUrl,
			Integer fastChargingTime,
			Integer slowChargingTime,
			Double fastPercentage
			) {
		  logger.info("--请求FinancialBasicsVehiclesController--方法saveFinancialBasicsVehicles--参数:--vehiclesDetailedName--{},"
		  		+ "--brandId--{},--modelId--{},--vehicleStyle--{},--yearStyle--{},"
		  		+ "--energyType--{},--variableBox--{},--guidancePrice--{},--discharge--{},"
		  		+ "--mileage--{},--autoHomeUrl--{},--lengthWidthHeight--{},--qualityAssurance--{},"
		  		+ "--wheelbase--{},--environmentalProtectionStandard--{},--fastChargingTime--{},--slowChargingTime--{},"
		  		+ "--fastPercentage--{}",vehiclesDetailedName,brandId,modelId,vehicleStyle,yearStyle
		  		,energyType,variableBox,guidancePrice,discharge,mileage,autoHomeUrl,lengthWidthHeight,qualityAssurance,
				 wheelbase,environmentalProtectionStandard,fastChargingTime,slowChargingTime,fastPercentage);
		  
		  FinancialBasicsVehicles basicsVehicles=financialBasicsVehiclesExMapper.queryFinancialBasicsVehiclesByName(vehiclesDetailedName);
		  
          if (basicsVehicles!=null) {
        	  return AjaxResponse.fail(RestErrorCode.BASICSVEHICLE_EXISTS);
		  }
				  
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
		  financialBasicsVehicles.setImgUrl(imgUrl);
		  financialBasicsVehicles = financialBasicsVehiclesService.saveFinancialBasicsVehicles(financialBasicsVehicles);
		  return AjaxResponse.success(financialBasicsVehicles);
	}
	
	/**
	 * queryFinancialBasicsVehiclesById:(查询详情). <br/>  
	 * @author baiyunlong
	 * @param basicsVehiclesId
	 * @return
	 */
	@RequiresPermissions(value = { "CarTypeManage_look" } )
	@MasterSlaveConfigs(configs={ 
			@MasterSlaveConfig(databaseTag="driver-DataSource",mode=DataSourceMode.SLAVE )
	} )
	@RequestMapping(value = "/queryFinancialBasicsVehiclesById")
	public AjaxResponse queryFinancialBasicsVehiclesById(
			@Verify(param = "basicsVehiclesId", rule = "required|min(1)")Integer basicsVehiclesId
			) {
		  logger.info("--请求FinancialBasicsVehiclesController--方法queryFinancialBasicsVehiclesById--参数:--basicsVehiclesId--{}");
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
	@RequiresPermissions(value = { "CarTypeManage_update" } )
	@RequestMapping(value = "/updateFinancialBasicsVehicles")
	@MasterSlaveConfigs(configs={ 
			@MasterSlaveConfig(databaseTag="driver-DataSource",mode=DataSourceMode.MASTER )
	} )
	@SysLogAnn(module="FinancialBasicsVehicles",methods="update",
    serviceClass="financialBasicsVehiclesService",queryMethod="queryFinancialBasicsVehiclesById",parameterType="Integer",parameterKey="basicsVehiclesId",objClass=FinancialBasicsVehiclesDTO.class )
	public AjaxResponse updateFinancialBasicsVehicles(
			@Verify(param = "basicsVehiclesId", rule = "required|min(1)")Integer basicsVehiclesId,
			@Verify(param = "vehiclesDetailedName", rule = "required")String vehiclesDetailedName,
			@Verify(param = "brandId", rule = "required|min(1)")Long brandId,
			@Verify(param = "modelId", rule = "required|min(1)")Long modelId,
			@Verify(param = "vehicleStyle", rule = "required")String vehicleStyle,
			@Verify(param = "yearStyle", rule = "required")String yearStyle,
			@Verify(param = "energyType", rule = "required")Integer energyType,
			@Verify(param = "variableBox", rule = "required")Integer variableBox,
			@Verify(param = "guidancePrice", rule = "required")BigDecimal guidancePrice,
			@Verify(param = "discharge", rule = "min(1)")Integer discharge,
			@Verify(param = "mileage", rule = "min(1)")Integer mileage,
			String autoHomeUrl,
			@Verify(param = "lengthWidthHeight", rule = "required")String lengthWidthHeight,
			@Verify(param = "qualityAssurance", rule = "required")String qualityAssurance,
			@Verify(param = "wheelbase", rule = "required")Integer wheelbase,
			@Verify(param = "imgUrl", rule = "required") String imgUrl,
			String environmentalProtectionStandard,
			Integer fastChargingTime,
			Integer slowChargingTime,
			Double fastPercentage
			) {
		  logger.info("--请求FinancialBasicsVehiclesController--方法updateFinancialBasicsVehicles--参数:--basicsVehiclesId--{},--vehiclesDetailedName--{},"
			  		+ "--brandId--{},--modelId--{},--vehicleStyle--{},--yearStyle--{},"
			  		+ "--energyType--{},--variableBox--{},--guidancePrice--{},--discharge--{},"
			  		+ "--mileage--{},--autoHomeUrl--{},--lengthWidthHeight--{},--qualityAssurance--{},"
			  		+ "--wheelbase--{},--environmentalProtectionStandard--{},--fastChargingTime--{},--slowChargingTime--{},"
			  		+ "--fastPercentage--{}",basicsVehiclesId,vehiclesDetailedName,brandId,modelId,vehicleStyle,yearStyle
			  		,energyType,variableBox,guidancePrice,discharge,mileage,autoHomeUrl,lengthWidthHeight,qualityAssurance,
					 wheelbase,environmentalProtectionStandard,fastChargingTime,slowChargingTime,fastPercentage);
		  FinancialBasicsVehicles basicsVehicles=financialBasicsVehiclesExMapper.queryFinancialBasicsVehiclesByName(vehiclesDetailedName);
		  
          if (basicsVehicles!=null && basicsVehicles.getBasicsVehiclesId().intValue()!=basicsVehiclesId.intValue()) {
        	  return AjaxResponse.fail(RestErrorCode.BASICSVEHICLE_EXISTS);
		  }
		  
		  FinancialBasicsVehicles financialBasicsVehicles=new FinancialBasicsVehicles();
		  financialBasicsVehicles.setBasicsVehiclesId(basicsVehiclesId);
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
		  financialBasicsVehicles.setImgUrl(imgUrl);
		  financialBasicsVehicles = financialBasicsVehiclesService.updateFinancialBasicsVehicles(financialBasicsVehicles);
		  return AjaxResponse.success(financialBasicsVehicles);
	}
	
	
	/**
	 * updateFinancialBasicsVehiclesForStatus:(启用停用). <br/>  
	 * @author baiyunlong
	 * @param basicsVehiclesId
	 * @param enableStatus
	 * @return
	 */
	@RequiresPermissions(value = { "CarTypeManage_updateStatus" } )
	@MasterSlaveConfigs(configs={ 
			@MasterSlaveConfig(databaseTag="driver-DataSource",mode=DataSourceMode.MASTER )
	} )
	@RequestMapping(value = "/updateFinancialBasicsVehiclesForStatus")
	public AjaxResponse updateFinancialBasicsVehiclesForStatus(
			@Verify(param = "basicsVehiclesId", rule = "required|min(1)")Integer basicsVehiclesId,
			@Verify(param = "basicsVehiclesId", rule = "required")Byte enableStatus
			) {
		  logger.info("--请求FinancialBasicsVehiclesController--方法updateFinancialBasicsVehicles--参数:--basicsVehiclesId--{},--enableStatus--{}",
			  		basicsVehiclesId,enableStatus);
		  FinancialBasicsVehicles financialBasicsVehicles=new FinancialBasicsVehicles();
		  financialBasicsVehicles.setBasicsVehiclesId(basicsVehiclesId);
		  financialBasicsVehicles.setEnableStatus(enableStatus);
		  financialBasicsVehicles = financialBasicsVehiclesService.updateFinancialBasicsVehicles(financialBasicsVehicles);
		  return AjaxResponse.success(true);
	}
	
	
	/**
	 * queryBasicsVehiclesAllList:(查询全部车型库信息). <br/>  
	 * @author baiyunlong
	 * @return
	 */
	@MasterSlaveConfigs(configs={ 
			@MasterSlaveConfig(databaseTag="driver-DataSource",mode=DataSourceMode.SLAVE )
	} )
	@RequestMapping(value = "/queryBasicsVehiclesAllList")
	public AjaxResponse queryBasicsVehiclesAllList() {
		try{
			List<FinancialBasicsVehicles> financialBasicsVehicless = financialBasicsVehiclesService.queryBasicsVehiclesAllList();
			return AjaxResponse.success(financialBasicsVehicless);
		}catch(Exception e){
			return AjaxResponse.fail(RestErrorCode.QUERY_BASICSVEHICLE_ERROR);
		}
	}
}
  
