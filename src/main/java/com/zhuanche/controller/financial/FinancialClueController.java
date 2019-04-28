package com.zhuanche.controller.financial;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.financial.FinancialClueDTO;
import com.zhuanche.serv.financial.FinancialClueService;
/**  
 * ClassName:FinancialClueController <br/>  
 * Date:     2019年4月23日 下午6:51:44 <br/>  
 * @author   baiyunlong
 * @version  1.0.0      
 */
@RestController
@RequestMapping("/financialClue")
public class FinancialClueController {
	private static final Logger logger = LoggerFactory.getLogger(FinancialClueController.class);
	@Autowired
	private FinancialClueService financialClueService;

	/**
	 * queryfinancialClueForList:(线索列表). <br/>  
	 * @author baiyunlong
	 * @param page
	 * @param pageSize
	 * @param purposeName
	 * @param goodsId
	 * @param startDate
	 * @param endDate
	 * @param supplierId
	 * @param cityId
	 * @param status
	 * @return
	 */
	@RequestMapping(value = "/queryfinancialClueForList")
	public AjaxResponse queryfinancialClueForList(
			@Verify(param = "page", rule = "required|min(1)") Integer page,
			@Verify(param = "pageSize", rule = "required|min(10)") Integer pageSize,
			String purposeName,
			@Verify(param = "goodsId", rule = "min(1)")Integer goodsId,
			String startDate,
			String endDate,
			@Verify(param = "supplierId", rule = "min(1)")Integer supplierId,
			@Verify(param = "cityId", rule = "min(1)")Integer cityId,
			Byte status
			) {
		logger.info("--FinancialClueController--方法:queryfinancialClueForList--参数:"
				+ "--page--{},--pageSize--{},--purposeName--{}"
				+ "--goodsId--{},--startDate--{},--endDate--{}"
				+ "--supplierId--{},--cityId--{},--status--{}",page,pageSize,purposeName,
				goodsId,startDate,endDate,supplierId,cityId,status);
		try {
			PageDTO pageDTO = financialClueService.queryfinancialClueForList(page,pageSize,purposeName,
					goodsId,startDate,endDate,supplierId,cityId,status);
			return AjaxResponse.success(pageDTO);
		} catch (Exception e) {
			logger.error("--FinancialClueController--方法:queryfinancialClueForList--参数:"
					+ "--page--{},--pageSize--{},--purposeName--{}"
					+ "--goodsId--{},--startDate--{},--endDate--{}"
					+ "--supplierId--{},--cityId--{},--status--{}",page,pageSize,purposeName,
					goodsId,startDate,endDate,supplierId,cityId,status);
			return AjaxResponse.fail(RestErrorCode.QUERY_BASICSVEHICLE_ERROR);
		}
	}
	
	/**
	 * queryfinancialClueById:(查询线索详情). <br/>  
	 * @author baiyunlong
	 * @param clueId
	 * @return
	 */
	@RequestMapping(value = "/queryfinancialClueById")
	public AjaxResponse queryfinancialClueById(
			@Verify(param = "clueId", rule = "required|min(1)") Integer clueId
			) {
		logger.info("--FinancialClueController--方法:queryfinancialClueById--参数:"
				+ "--clueId--{}",clueId);
		Map resultMap = financialClueService.queryfinancialClueById(clueId);
		return AjaxResponse.success(resultMap);
	}
}
  
