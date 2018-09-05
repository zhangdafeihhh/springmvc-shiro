package com.zhuanche.controller.statisticalAnalysis;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.serv.statisticalAnalysis.StatisticalAnalysisService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;

/**
 * 
 * ClassName: 司机运营详情分析
 * date: 2018年9月01日 下午7:19:45 
 * @author jiadongdong
 *
 */
@Controller
@RequestMapping("/driverOperateDetail")
public class DriverOperateDetailController{
	private static final Logger logger = LoggerFactory.getLogger(DriverOperateDetailController.class);
	 
	/* //司机运营详情查询
	 @Value("${statistics.driveroperatedetail.queryList.url}")
	 String  driveroperatedetailQueryListApiUrl;
	 
	 //司机运营详情下载
	 @Value("${statistics.driveroperatedetail.download.url}")
	 String  driveroperatedetailDownloadApiUrl;*/
	 
	 @Value("${saas.bigdata.api.url}")
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
    @RequestMapping(value = "/queryDriverOperateDetailData", method = { RequestMethod.POST,RequestMethod.GET })
    public AjaxResponse queryDriverOperateDetailData(
    										  String driverCityId,
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
        paramMap.put("driverCityId", driverCityId);//司机所属城市ID
        paramMap.put("genderId", genderId);//性别ID
        paramMap.put("driverTypeId", driverTypeId);//司机类型ID
        paramMap.put("allianceId", allianceId);//加盟商ID
        paramMap.put("motorcadeId", motorcadeId);//车队ID
        paramMap.put("driverName", driverName);//司机姓名
		paramMap.put("queryDate", queryDate);//查询日期
        // 数据权限设置
		SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息
        Set<Integer> suppliers = currentLoginUser.getSupplierIds();// 获取用户可见的供应商信息
        Set<Integer> teamIds = currentLoginUser.getTeamIds();// 获取用户可见的车队信息
        Set<Integer> cityIds = currentLoginUser.getCityIds();// 获取用户可见的城市ID
        // 供应商信息
		String[] visibleAllianceIds = statisticalAnalysisService.setToArray(suppliers);
		// 车队信息
		String[] visibleMotocadeIds = statisticalAnalysisService.setToArray(teamIds);
		// 可见城市
		String[] visibleCityIds = statisticalAnalysisService.setToArray(cityIds);
		if(null == visibleAllianceIds || null == visibleMotocadeIds || visibleCityIds == null ){
			return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
		}
        paramMap.put("visibleAllianceIds", visibleAllianceIds); // 可见加盟商ID
        paramMap.put("visibleMotorcadeIds", visibleMotocadeIds); // 可见车队ID
        paramMap.put("visibleCityIds", visibleCityIds); //可见城市ID
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
										String driverCityId,
										String genderId,
										String driverTypeId,
							            String allianceId,
							            String motorcadeId,
							            String driverName,
							            @Verify(param = "queryDate",rule = "required") String queryDate,
	                                    HttpServletRequest request,
	                                    HttpServletResponse response){
	        logger.info("【运营管理-统计分析】导出,导出司机运营详情数据:DriverOperateDetail");
	      try {
	    	  Map<String, Object> paramMap = new HashMap<String, Object>();
	          paramMap.put("driverCityId", driverCityId);//司机所属城市ID
	          paramMap.put("genderId", genderId);//性别ID
	          paramMap.put("driverTypeId", driverTypeId);//司机类型ID
	          paramMap.put("allianceId", allianceId);//加盟商ID
	          paramMap.put("motorcadeId", motorcadeId);//车队ID
	          paramMap.put("driverName", driverName);//司机姓名
	  		  paramMap.put("queryDate", queryDate);//查询日期
	          // 数据权限设置
	  		  SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息
	          Set<Integer> suppliers = currentLoginUser.getSupplierIds();// 获取用户可见的供应商信息
	          Set<Integer> teamIds = currentLoginUser.getTeamIds();// 获取用户可见的车队信息
	          Set<Integer> cityIds = currentLoginUser.getCityIds();// 获取用户可见的城市ID
	          // 供应商信息
	  		  String[] visibleAllianceIds = statisticalAnalysisService.setToArray(suppliers);
	  		  // 车队信息
	  		  String[] visibleMotocadeIds = statisticalAnalysisService.setToArray(teamIds);
	  		  // 可见城市
	  		  String[] visibleCityIds = statisticalAnalysisService.setToArray(cityIds);
	  		  if(null == visibleAllianceIds || null == visibleMotocadeIds || visibleCityIds == null ){
	  			 return;
	  		  }
	          paramMap.put("visibleAllianceIds", visibleAllianceIds); // 可见加盟商ID
	          paramMap.put("visibleMotorcadeIds", visibleMotocadeIds); // 可见车队ID
	          paramMap.put("visibleCityIds", visibleCityIds); //可见城市ID
	          statisticalAnalysisService.downloadCsvFromTemplet(paramMap,
	        		  	saasBigdataApiUrl+"/driverOperateDetail/download" ,
						request.getRealPath("/")+File.separator+"template"+File.separator+"driveroperatedetail_info.csv");
			  statisticalAnalysisService.exportCsvFromTemplet(response,
						new String("司机运营详情分析".getBytes("gb2312"), "iso8859-1"),
						request.getRealPath("/")+File.separator+"template"+File.separator+"driveroperatedetail_info.csv");
      }  catch (Exception e) {
          e.printStackTrace();
      }
  }

}