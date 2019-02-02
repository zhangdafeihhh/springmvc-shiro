package com.zhuanche.controller.statisticalAnalysis;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.util.StringUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.serv.statisticalAnalysis.StatisticalAnalysisService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.ValidateUtils;

/**
 * 
 * ClassName: 加盟商考核 
 * date: 2018年9月01日 下午7:19:45 
 * @author jiadongdong
 *
 */
@Controller
@RequestMapping("/allianceCheck")
public class AllianceCheckController{
	private static final Logger logger = LoggerFactory.getLogger(AllianceCheckController.class);
	 
	 @Value("${bigdata.saas.data.url}")
	 String  saasBigdataApiUrl;
	 
	 @Autowired
	 private StatisticalAnalysisService statisticalAnalysisService;
	 
		 /**
		    * 查询加盟商考核  列表
		    * @param queryDate	查询日期
			* @param allianceId	加盟商ID
			* @param cityId	城市ID
			* @param pageNo	页号
			* @param pageSize	每页记录数
			* @param orderByColumnCode	排序字段代码 （1：司机ID 2：入职日期 3：累计差评数 4：完成订单 5：差评数）
			* @param orderTypeCode	排序方式代码（1：升序 2：降序）
		    * @return
		  */
		@ResponseBody
	    @RequestMapping(value = "/queryAllianceCheckData", method = { RequestMethod.POST,RequestMethod.GET })
		@RequiresPermissions(value = { "JoinBusinessAssessment_look" } )
	    public AjaxResponse queryAllianceCheckData(
	    										  @Verify(param = "queryDate",rule = "required") String queryDate,
	                                              String allianceId,
	                                              Long cityId,
	                                              @Verify(param = "pageNo",rule = "required") Integer pageNo,
	                                              @Verify(param = "pageSize",rule = "required") Integer pageSize,
	                                              @Verify(param = "orderByColumnCode",rule = "required") String orderByColumnCode,
	                                              @Verify(param = "orderTypeCode",rule = "required")  String orderTypeCode
	                                              ){
	        logger.info("【运营管理-统计分析】加盟商考核  列表数据:queryAllianceCheckData");
	        Map<String, Object> paramMap = new HashMap<String, Object>();
	        if(StringUtil.isNotEmpty(allianceId)){
	        	paramMap.put("allianceId", allianceId);//加盟商ID
	        }
	        if(null!=cityId){
	        	paramMap.put("cityId", cityId);//城市ID
	        }
	        paramMap.put("orderByColumnCode", orderByColumnCode);//排序字段代码 
	        paramMap.put("orderTypeCode", orderTypeCode);//排序方式代码
			paramMap.put("queryDate", queryDate);//查询日期
			 // 数据权限设置
	        paramMap = statisticalAnalysisService.getCurrentLoginUserParamMap(paramMap,cityId,allianceId,null);
	        logger.info("【运营管理-统计分析】加盟商考核  列表数据:paramMap"+paramMap);
		      if(paramMap==null){
		    	  return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
		      }
	        if(null != pageNo && pageNo > 0)
	        	paramMap.put("pageNo", pageNo);//页号
	        if(null != pageSize && pageSize > 0)
	        	paramMap.put("pageSize", pageSize);//每页记录数
	        
	    	// 从大数据仓库获取统计数据
	        AjaxResponse result = statisticalAnalysisService.parseResult(saasBigdataApiUrl+"/allianceCheck/queryList",paramMap);
	        return result;
	    }
	    
	    /**
		    * 导出 加盟商考核 分析 
		   	* @param queryDate	查询日期
			* @param allianceId	加盟商ID
			* @param cityId	城市ID
			* @param orderByColumnCode	排序字段代码 （1：司机ID 2：入职日期 3：累计差评数 4：完成订单 5：差评数）
			* @param orderTypeCode	排序方式代码（1：升序 2：降序）
		    * @return
		  */
  	@RequestMapping(value = "/exportAllianceCheckData", method = { RequestMethod.POST,RequestMethod.GET })
	@RequiresPermissions(value = { "JoinBusinessAssessment_export" } )
	public void exportAllianceCheckData( 
										 @Verify(param = "queryDate",rule = "required") String queryDate,
							             String allianceId,
							             Long cityId,
							             @Verify(param = "orderByColumnCode",rule = "required") String orderByColumnCode,
							             @Verify(param = "orderTypeCode",rule = "required") String orderTypeCode,
	                                     HttpServletRequest request,
	                                     HttpServletResponse response){
	        logger.info("【运营管理-统计分析】导出,导出 加盟商考核  列表数据:exportAllianceCheckData");
      try {
    	    Map<String, Object> paramMap = new HashMap<String, Object>();
    	    if(StringUtil.isNotEmpty(allianceId)){
	        	paramMap.put("allianceId", allianceId);//加盟商ID
	        }
	        if(null!=cityId){
	        	paramMap.put("cityId", cityId);//城市ID
	        }
	        paramMap.put("orderByColumnCode", orderByColumnCode);//排序字段代码 
	        paramMap.put("orderTypeCode", orderTypeCode);//排序方式代码
			paramMap.put("queryDate", queryDate);//查询日期
	        // 数据权限设置
	        paramMap = statisticalAnalysisService.getCurrentLoginUserParamMap(paramMap,cityId,allianceId,null);
		      if(paramMap==null){
		    	  return;
		      }
	        String jsonString = JSON.toJSONString(paramMap);
	        
		    statisticalAnalysisService.exportCsvFromToPage(
					response,
					jsonString,
					saasBigdataApiUrl+"/allianceCheck/download" ,
					new String("加盟商考核".getBytes("gb2312"), "iso8859-1"),
					request.getRealPath("/")+File.separator+"template"+File.separator+"alliancecheck_info.csv");
      } catch (Exception e) {
          e.printStackTrace();
      }
  }

}