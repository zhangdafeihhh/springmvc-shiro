package com.zhuanche.controller.financial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.financial.FinancialGoodsDTO;
import com.zhuanche.dto.financial.FinancialGoodsParamDTO;
import com.zhuanche.serv.financial.FinancialGoodsService;

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
			PageDTO pageDTO = financialGoodsService.queryFinancialGoodsForList(page,pageSize,goodsName,
					basicsVehiclesId,salesTarget,supplierId,cityId,status);
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
	 * @author baiyunlong
	 * @param goodsName
	 * @param salesTarget
	 * @param goodsType
	 * @param supplierId
	 * @param cityId
	 * @param channel
	 * @param reason
	 * @param explain
	 * @param pictureUrl
	 * @param keyword
	 * @param vehicleAge
	 * @param mileage
	 * @param vehicleProperties
	 * @param color
	 * @param basicsVehiclesId
	 * @param stock
	 * @param sourceFundsId
	 * @param leaseTerm
	 * @param rentEveryTerm
	 * @param firstRent
	 * @param securityDeposit
	 * @param totalPrice
	 * @param frontMoney
	 * @param additionalServicesId
	 * @param additionalServicesInfo
	 * @param additionalClause
	 * @return
	 */
	@RequestMapping(value = "/saveFinancialGoods")
	public AjaxResponse saveFinancialGoods(
			/*String goodsName,
			Byte salesTarget,
			Byte goodsType,
			Integer supplierId,
			Integer cityId,
			String channel,	
			String reason,
			String explain,
			String pictureUrl,
			String keyword,
			Integer vehicleAge,
			Integer mileage,
			Integer vehicleProperties,
			String color,
			Integer basicsVehiclesId,
			Integer stock,
			
			Integer sourceFundsId,
			Integer leaseTerm,
			BigDecimal rentEveryTerm,
			BigDecimal firstRent,
			BigDecimal securityDeposit,
			BigDecimal totalPrice,
			BigDecimal frontMoney,
			
			String additionalServicesId,
			String additionalServicesInfo,
			String additionalClause*/
			FinancialGoodsParamDTO financialGoodsParamDTO
			) {
		int i=financialGoodsService.saveFinancialGoods(financialGoodsParamDTO);
		logger.info("");
	    return AjaxResponse.success(true);
	}
	
	/**
	 * updateFinancialGoods:(修改商品信息). <br/>  
	 * @author baiyunlong
	 * @param financialGoodsParamDTO
	 * @return
	 */
	@RequestMapping(value = "/updateFinancialGoods")
	public AjaxResponse updateFinancialGoods(
			FinancialGoodsParamDTO financialGoodsParamDTO
			) {
		int i=financialGoodsService.updateFinancialGoods(financialGoodsParamDTO);
		logger.info("");
	    return AjaxResponse.success(true);
	}
	
	/**
	 * updateFinancialGoodsByStatus:(修改状态). <br/>  
	 * @author baiyunlong
	 * @param goodsId
	 * @param status
	 * @return
	 */
	@RequestMapping(value = "/updateFinancialGoodsByStatus")
	public AjaxResponse updateFinancialGoodsByStatus(Integer goodsId,Byte status
			) {
		int i=financialGoodsService.updateFinancialGoodsByStatus(goodsId,status);
		logger.info("");
	    return AjaxResponse.success(true);
	}
	
	/**
	 * queryFinancialGoodsById:(查询详情). <br/>  
	 * @author baiyunlong
	 * @param goodsId
	 * @return
	 */
	@RequestMapping(value = "/queryFinancialGoodsById")
	public AjaxResponse queryFinancialGoodsById(Integer goodsId
			) {
		FinancialGoodsDTO financialGoodsDTO=financialGoodsService.queryFinancialGoodsById(goodsId);
		logger.info("");
	    return AjaxResponse.success(financialGoodsDTO);
	}
}
