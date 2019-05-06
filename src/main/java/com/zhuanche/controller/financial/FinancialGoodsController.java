package com.zhuanche.controller.financial;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.common.web.datavalidate.sequence.SeqAll;
import com.zhuanche.dto.financial.FinancialGoodsDTO;
import com.zhuanche.dto.financial.FinancialGoodsInfoDTO;
import com.zhuanche.dto.financial.FinancialGoodsParamDTO;
import com.zhuanche.entity.driver.FinancialGoods;
import com.zhuanche.serv.financial.FinancialGoodsService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;

/**
 * ClassName:FinancialGoodsController <br/>
 * Date: 2019年4月23日 下午6:51:06 <br/>
 * @author baiyunlong
 * @version 1.0.0
 */
@RestController
@RequestMapping("/financialGoods")
public class FinancialGoodsController {
	private static final Logger logger = LoggerFactory.getLogger(FinancialBasicsVehiclesController.class);
	@Autowired
	private FinancialGoodsService financialGoodsService;

	/**
	 * queryFinancialGoodsForList:(查询商品列表). <br/>  
	 * @author baiyunlong
	 * @param page
	 * @param pageSize
	 * @param goodsName
	 * @param basicsVehiclesId
	 * @param salesTarget
	 * @param supplierId
	 * @param cityId
	 * @param status
	 * @return
	 */
	@RequiresPermissions(value = { "GoodsManage_look" } )
	@MasterSlaveConfigs(configs={ 
			@MasterSlaveConfig(databaseTag="driver-DataSource",mode=DataSourceMode.SLAVE )
	} )
	@RequestMapping(value = "/queryFinancialGoodsForList")
	public AjaxResponse queryFinancialGoodsForList(
			@Verify(param = "page", rule = "required|min(1)") Integer page,
			@Verify(param = "pageSize", rule = "required|min(10)") Integer pageSize,
			String goodsName,
			Integer basicsVehiclesId,
			Byte salesTarget,
			Integer supplierId,
			Integer cityId,
			Byte status
			) {
		logger.info("--FinancialGoodsController--方法:queryFinancialGoodsForList--参数:"
				+ "--page--{},--pageSize--{},--goodsName--{}"
				+ "--basicsVehiclesId--{},--salesTarget--{},--supplierId--{}"
				+ "--cityId--{},--status--{}",page,pageSize,goodsName,
				basicsVehiclesId,salesTarget,supplierId,cityId,status);
		try {
			SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
		    
			Set<Integer> cityIds=new HashSet<>();
			Set<Integer> supplierIds=new HashSet<>();
		    
		    if(cityId != null){
		    	cityIds.add(cityId);
	        }else{
	        	cityIds = user.getCityIds();
	        }
	        if(supplierId != null){
	        	supplierIds.add(supplierId);
	        }else{
	        	supplierIds = user.getSupplierIds();
	        }
			
			PageDTO pageDTO = financialGoodsService.queryFinancialGoodsForList(page,pageSize,goodsName,
					basicsVehiclesId,salesTarget,supplierIds,cityIds,status);
			return AjaxResponse.success(pageDTO);
		} catch (Exception e) {
			logger.error("--FinancialGoodsController--方法:queryFinancialGoodsForList--参数:"
					+ "--page--{},--pageSize--{},--goodsName--{}"
					+ "--basicsVehiclesId--{},--salesTarget--{},--supplierId--{}"
					+ "--cityId--{},--status--{}",page,pageSize,goodsName,
					basicsVehiclesId,salesTarget,supplierId,cityId,status);
			return AjaxResponse.fail(RestErrorCode.QUERY_BASICSVEHICLE_ERROR);
		}
	}
	
	/**
	 * saveFinancialGoods:(新增商品). <br/>  
	 * @return
	 */
	@RequiresPermissions(value = { "GoodsManage_save" } )
	@MasterSlaveConfigs(configs={ 
			@MasterSlaveConfig(databaseTag="driver-DataSource",mode=DataSourceMode.MASTER )
	} )
	@RequestMapping(value = "/saveFinancialGoods")
	public AjaxResponse saveFinancialGoods(@Validated(SeqAll.class)
			FinancialGoodsParamDTO financialGoodsParamDTO
			) {
		FinancialGoods financialGoods=financialGoodsService.saveFinancialGoods(financialGoodsParamDTO);
	    return AjaxResponse.success(true);
	}
	
	/**
	 * updateFinancialGoods:(修改商品信息). <br/>  
	 * @author baiyunlong
	 * @param financialGoodsParamDTO
	 * @return
	 */
	@RequiresPermissions(value = { "GoodsManage_update" } )
	@MasterSlaveConfigs(configs={ 
			@MasterSlaveConfig(databaseTag="driver-DataSource",mode=DataSourceMode.MASTER )
	} )
	@RequestMapping(value = "/updateFinancialGoods")
	public AjaxResponse updateFinancialGoods(@Validated(SeqAll.class) FinancialGoodsParamDTO financialGoodsParamDTO
			) {
		
		if (financialGoodsParamDTO.getGoodsId()==null||financialGoodsParamDTO.getGoodsId()==0) {
			return AjaxResponse.fail(RestErrorCode.GOODSIDISNULL);
		}
		
		FinancialGoods financialGoods=financialGoodsService.updateFinancialGoods(financialGoodsParamDTO);
	    return AjaxResponse.success(true);
	}
	
	/**
	 * updateFinancialGoodsByStatus:(修改状态). <br/>  
	 * @author baiyunlong
	 * @param goodsId
	 * @param status
	 * @return
	 */
	@RequiresPermissions(value = { "GoodsManage_updateStatus" } )
	@MasterSlaveConfigs(configs={ 
			@MasterSlaveConfig(databaseTag="driver-DataSource",mode=DataSourceMode.MASTER )
	} )
	@RequestMapping(value = "/updateFinancialGoodsByStatus")
	public AjaxResponse updateFinancialGoodsByStatus(
			@Verify(param = "goodsId", rule = "required|min(1)")Integer goodsId,
			@Verify(param = "status", rule = "required")Byte status
			) {
		int i=financialGoodsService.updateFinancialGoodsByStatus(goodsId,status);
	    return AjaxResponse.success(true);
	}
	
	/**
	 * queryFinancialGoodsById:(查询详情). <br/>  
	 * @author baiyunlong
	 * @param goodsId
	 * @return
	 */
	@RequestMapping(value = "/queryFinancialGoodsById")
	@MasterSlaveConfigs(configs={ 
			@MasterSlaveConfig(databaseTag="driver-DataSource",mode=DataSourceMode.SLAVE )
	} )
	public AjaxResponse queryFinancialGoodsById(@Verify(param = "goodsId", rule = "required|min(1)")Integer goodsId
			) {
		FinancialGoodsInfoDTO financialGoodsDTO=financialGoodsService.queryFinancialGoodsById(goodsId);
		logger.info("");
	    return AjaxResponse.success(financialGoodsDTO);
	}
	
	
	@RequestMapping(value = "/selectFinancialGoodsForList")
	@MasterSlaveConfigs(configs={ 
			@MasterSlaveConfig(databaseTag="driver-DataSource",mode=DataSourceMode.SLAVE )
	} )
	public AjaxResponse selectFinancialGoodsForList() {
			SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
			Set<Integer> cityIds = user.getCityIds();
			Set<Integer> supplierIds = user.getSupplierIds();
			List<FinancialGoodsDTO> financialGoodsDTOs = financialGoodsService.selectFinancialGoodsForList(
					supplierIds,cityIds);
			return AjaxResponse.success(financialGoodsDTOs);
	}
}
