package com.zhuanche.controller.statisticalAnalysis;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
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
 * ClassName: 司机运营详情分析
 * date: 2018年9月01日 下午7:19:45 
 * @author jiadongdong
 *  /driverOperateDetail/exportDriverOperateDetailData.json
 */
@Controller
@RequestMapping("/driverOperateDetail")
public class DriverOperateDetailController{
	private static final Logger logger = LoggerFactory.getLogger(DriverOperateDetailController.class);

	 @Value("${bigdata.saas.data.url}")
	 String  saasBigdataApiUrl;
	 
	 @Autowired
	 private StatisticalAnalysisService statisticalAnalysisService;
	 /**
	    * 司机运营详情分析 
		* @param 	queryDate	查询日期
		* @param 	driverCityId	司机所属城市ID
		* @param 	genderId	性别ID
		* @param 	driverTypeId	司机类型ID
		* @param 	allianceId	加盟商ID
		* @param 	motorcadeId	车队ID
		* @param 	driverName	司机姓名
		* @param 	pageNo	页号
		* @param 	pageSize	每页记录数
		* @param 	visibleCityIds	可见城市ID
		* @param 	visibleAllianceIds	可见加盟商ID
		* @param 	visibleMotorcadeIds	可见车队ID
	    * @return
	  */
	@ResponseBody
    @RequestMapping(value = "/queryDriverOperateDetailData", method = { RequestMethod.POST,RequestMethod.GET })
	@RequiresPermissions(value = { "DriverOperateDetail_look" } )
    public AjaxResponse queryDriverOperateDetailData(
    										  Long driverCityId,
    										  String genderId,
    										  String driverTypeId,
                                              String allianceId,
                                              String motorcadeId,
                                              String driverName,
                                              @Verify(param = "queryDate",rule = "required") String queryDate,
                                              @Verify(param = "pageNo",rule = "required") Integer pageNo,
                                              @Verify(param = "pageSize",rule = "required") Integer pageSize
                                              ){
        logger.info("【运营管理-统计分析】司机运营详情分析  列表数据:queryDriverOperateDetailData");
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if(null!=driverCityId){
        	paramMap.put("driverCityId", driverCityId);//司机所属城市ID
	    }
	    if(StringUtil.isNotEmpty(genderId)){
	    	   paramMap.put("genderId", genderId);//性别ID
	    }
	    if(StringUtil.isNotEmpty(driverTypeId)){
	    	 paramMap.put("driverTypeId", driverTypeId);//司机类型ID
	    }
	    if(StringUtil.isNotEmpty(allianceId)){
	    	paramMap.put("allianceId", allianceId);//加盟商ID
	    }
	    if(StringUtil.isNotEmpty(motorcadeId)){
	    	paramMap.put("motorcadeId", motorcadeId);//车队ID
	    }
	    if(StringUtil.isNotEmpty(driverName)){
	    	paramMap.put("driverName", driverName);//司机姓名
	    }
		paramMap.put("queryDate", queryDate);//查询日期
		paramMap = statisticalAnalysisService.getCurrentLoginUserParamMap(paramMap,driverCityId,allianceId,motorcadeId);
		if(paramMap==null){
			return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
		}
        if(null != pageNo && pageNo > 0)
        	paramMap.put("pageNo", pageNo);//页号
        if(null != pageSize && pageSize > 0)
        	paramMap.put("pageSize", pageSize);//每页记录数
        
        // 从大数据仓库获取统计数据
        AjaxResponse result = statisticalAnalysisService.parseResult(saasBigdataApiUrl+"/driverOperateDetail/queryList",paramMap);
        return result;
    }
	    
    /**
	    * 导出 司机运营详情数据
		* @param 	queryDate	查询日期
		* @param 	driverCityId	司机所属城市ID
		* @param 	genderId	性别ID
		* @param 	driverTypeId	司机类型ID
		* @param 	allianceId	加盟商ID
		* @param 	motorcadeId	车队ID
		* @param 	driverName	司机姓名
		* @param 	visibleCityIds	可见城市ID
		* @param 	visibleAllianceIds	可见加盟商ID
		* @param 	visibleMotorcadeIds	可见车队ID
	    * @return
	  */
  	@RequestMapping(value = "/exportDriverOperateDetailData", method = { RequestMethod.POST,RequestMethod.GET })
	public void exportDriverOperateDetailData( 
										Long driverCityId,
										String genderId,
										String driverTypeId,
							            String allianceId,
							            String supplier,
                                        String teamId,
							            String motorcadeId,
							            String driverName,
							            @Verify(param = "queryDate",rule = "required") String queryDate,
	                                    HttpServletRequest request,
	                                    HttpServletResponse response){
	        logger.info("【运营管理-统计分析】导出,导出司机运营详情数据:DriverOperateDetail");
	      try {
    	    Map<String, Object> paramMap = new HashMap<String, Object>();
    	    if(null!=driverCityId){
          	 	paramMap.put("driverCityId", driverCityId);//司机所属城市ID
	  	    }
	  	    if(StringUtil.isNotEmpty(genderId)){
	  	    	   paramMap.put("genderId", genderId);//性别ID
	  	    }
	  	    if(StringUtil.isNotEmpty(driverTypeId)){
	  	    	 paramMap.put("driverTypeId", driverTypeId);//司机类型ID
	  	    }
	  	    if(StringUtil.isNotEmpty(supplier)){
	  	    	paramMap.put("allianceId", supplier);//加盟商ID
	  	    }
	  	    if(StringUtil.isNotEmpty(teamId)){
	  	    	paramMap.put("motorcadeId", teamId);//车队ID
	  	    }
	  	    if(StringUtil.isNotEmpty(driverName)){
	  	    	paramMap.put("driverName", driverName);//司机姓名
	  	    }
	  		  paramMap.put("queryDate", queryDate);//查询日期
		  		paramMap = statisticalAnalysisService.getCurrentLoginUserParamMap(paramMap,driverCityId,allianceId,motorcadeId);
				if(paramMap==null){
					return;
				}
			
	          String jsonString = JSON.toJSONString(paramMap);
	          
			  statisticalAnalysisService.exportCsvFromToPage(
					response,
					jsonString,
					saasBigdataApiUrl+"/driverOperateDetail/download",
					new String("司机运营详情分析".getBytes("gb2312"), "iso8859-1"),
					request.getRealPath("/")+File.separator+"template"+File.separator+"driveroperatedetail_info.csv");
				
				/*
	          statisticalAnalysisService.downloadCsvFromTemplet(jsonString,
	        		  	saasBigdataApiUrl+"/driverOperateDetail/download" ,
						request.getRealPath("/")+File.separator+"template"+File.separator+"driveroperatedetail_info.csv");
			  statisticalAnalysisService.exportCsvFromTemplet(response,
						new String("司机运营详情分析".getBytes("gb2312"), "iso8859-1"),
						request.getRealPath("/")+File.separator+"template"+File.separator+"driveroperatedetail_info.csv");*/
      }  catch (Exception e) {
          e.printStackTrace();
      }
  }

}