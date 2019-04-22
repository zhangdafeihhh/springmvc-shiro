package com.zhuanche.controller.statisticalAnalysis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.constant.Constants;
import com.zhuanche.entity.bigdata.SAASDriverEvaluateDetailDto;
import com.zhuanche.entity.bigdata.SAASEvaluateDetailQuery;
import com.zhuanche.http.HttpClientUtil;
import com.zhuanche.serv.statisticalAnalysis.StatisticalAnalysisService;
import com.zhuanche.util.dateUtil.DateUtil;
import mapper.bigdata.ex.DriverEvaluateDetailExMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.entity.ContentType;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.zhuanche.common.enums.MenuEnum.DRIVER_EVALUATE_DETAIL;
import static com.zhuanche.common.enums.MenuEnum.DRIVER_EVALUATE_EXPORT;

/**
 * 
 * ClassName: 对司机评级详情分析 
 * date: 2018年9月01日 下午7:19:45 
 * @author jiadongdong
 *
 */
@Controller
@RequestMapping("/driverEvaluate")
public class DriverEvaluateController{
	private static final Logger logger = LoggerFactory.getLogger(DriverEvaluateController.class);
	 
	 @Value("${bigdata.saas.data.url}")
	 String  saasBigdataApiUrl;

	 @Value("${driver.evaluate.query.url}")
	 String evaluateQueryUrl;

	 @Value("${driver.evaluate.export.url}")
	 String evaluateExportUrl;
	 private static final String SAAS_BI_DRIVER_EVALUATE_DETAIL = "bi_driver_evaluate_detail"; // 司机评价详情
	 private static final String COMM = "_";
	 @Autowired
	 private StatisticalAnalysisService statisticalAnalysisService;
	 @Autowired
	 private DriverEvaluateDetailExMapper driverEvaluateDetailExMapper;

	// 司机评价详情
	private static final String DRIVEREVALUATEDETAIL_TITLE = "司机ID,司机姓名,车牌号,司机评分,司机所属城市名称,评价时间,司机类型名称,加盟商,车队名称,班组名称," +
			"订单号,司机评价内容,备注,评价用户ID,会员级别名称,乘客姓名,订单来源名称,实际上车时间,订单完成时间," +
			"实际上车地点,实际下车地点,APP评价分数,APP评价内容,APP评价备注,操作系统,操作系统版本,服务监督号,服务类型";
		 /**
		    * 查询对司机评级详情分析 列表
		    * @param queryDate	查询日期
			* @param orderCityId 订单城市ID	
			* @param driverTypeId 司机类型ID	
			* @param allianceId 加盟商ID	
			* @param motorcadeId 车队ID	
			* @param driverScore 司机评价分数	
			* @param appScore APP评价分数	
			* @param serviceTypeId 服务类型
			* @param pageNo	页号
			* @param pageSize	每页记录数
		    * @return
		  */
		@ResponseBody
	    @RequestMapping(value = "/queryDriverEvaluateData", method = { RequestMethod.POST,RequestMethod.GET })
		@RequiresPermissions(value = { "DriverEvaluateDetail_look" } )
		@RequestFunction(menu = DRIVER_EVALUATE_DETAIL)
	    public AjaxResponse queryDriverEvaluateData(
	    										  Long orderCityId,
	    										  String driverTypeId,
	                                              String allianceId,
	                                              String motorcadeId,
	                                              String driverScore,
	                                              String appScore,
	                                              String classId,
	                                              String serviceTypeId,
	                                              @Verify(param = "queryDate",rule = "required") String queryDate,
	                                              @Verify(param = "pageNo",rule = "required") Integer pageNo,
	                                              @Verify(param = "pageSize",rule = "required") Integer pageSize
	                                              ){
	        logger.info("【运营管理-统计分析】对司机评级详情分析 列表数据:queryDriverEvaluateData");
			//SAASEvaluateDetailQuery saasQuery = new SAASEvaluateDetailQuery();
	        Map<String, Object> paramMap = new HashMap<String, Object>();
	        if(null!=orderCityId){
				//saasQuery.setOrderCityId(String.valueOf(orderCityId));
	        	 paramMap.put("orderCityId", orderCityId);//订单城市ID
	        }
	        if(StringUtil.isNotEmpty(allianceId)){
				//saasQuery.setAllianceId(allianceId);
	        	 paramMap.put("allianceId", allianceId);//加盟商ID
	        }
	        if(StringUtil.isNotEmpty(motorcadeId)){
				//saasQuery.setMotorcadeId(motorcadeId);
	        	 paramMap.put("motorcadeId", motorcadeId);//车队ID
	        }
	        if(StringUtil.isNotEmpty(driverTypeId)){
				//saasQuery.setDriverTypeId(driverTypeId);
	        	paramMap.put("driverTypeId", driverTypeId);//司机类型ID
	        }
	        if(StringUtil.isNotEmpty(driverScore)){
				//saasQuery.setDriverScore(driverScore);
	        	paramMap.put("driverScore", driverScore);//司机评价分数
	        }
	        if(StringUtil.isNotEmpty(appScore)){
				//saasQuery.setAppScore(appScore);
	        	 paramMap.put("appScore", appScore);//APP评价分数
	        }
	        if (StringUtils.isNotEmpty(classId)){
				//saasQuery.setClassId(classId);
	        	paramMap.put("classId", classId);//班组id
			}
			if (StringUtils.isNotBlank(serviceTypeId)){
				//saasQuery.setServiceTypeId(serviceTypeId);
				paramMap.put("serviceTypeId", serviceTypeId);//服务类型
			}
			paramMap.put("queryDate", queryDate);//查询日期
			//saasQuery.setQueryDate(queryDate);
			paramMap = statisticalAnalysisService.getCurrentLoginUserParamMap(paramMap,orderCityId,allianceId,motorcadeId);
			if(paramMap==null){
				return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
			}
		   if(null != pageNo && pageNo > 0)
	        	paramMap.put("pageNo", pageNo);//页号
	        if(null != pageSize && pageSize > 0)
	        	paramMap.put("pageSize", pageSize);//每页记录数
	        // 从大数据仓库获取统计数据
	        AjaxResponse result = parseResultsQueryData(paramMap);
	        return result;
	    }
	    
	    /**
		    * 导出对司机评级详情分析 
		    * @param queryDate	查询日期
			* @param orderCityId 订单城市ID	
			* @param driverTypeId 司机类型ID	
			* @param allianceId 加盟商ID	
			* @param motorcadeId 车队ID	
			* @param driverScore 司机评价分数	
			* @param appScore APP评价分数
		    * @param serviceTypeId 服务类型
		    * @return
		  */
  	@RequestMapping(value = "/exportDriverEvaluateData", method = { RequestMethod.POST,RequestMethod.GET })
	@RequiresPermissions(value = { "DriverEvaluateDetail_export" } )
	@RequestFunction(menu = DRIVER_EVALUATE_EXPORT)
	public void exportDriverEvaluateData( 
										Long orderCityId,
										String driverTypeId,
							            String allianceId,
							            String motorcadeId,
							            String driverScore,
							            String appScore,
							            String classId,
                                        String serviceTypeId,
							            @Verify(param = "queryDate",rule = "required") String queryDate,
	                                    HttpServletRequest request,
	                                    HttpServletResponse response){
	        logger.info("【运营管理-统计分析】导出,导出对司机评级详情列表数据:exportCancelOrderData");
      try {
    	  Map<String, Object> paramMap = new HashMap<String, Object>();
	        if(null!=orderCityId){
	        	 paramMap.put("orderCityId", orderCityId);//订单城市ID	
	        }
	        if(StringUtil.isNotEmpty(allianceId)){
	        	 paramMap.put("allianceId", allianceId);//加盟商ID
	        }
	        if(StringUtil.isNotEmpty(motorcadeId)){
	        	 paramMap.put("motorcadeId", motorcadeId);//车队ID
	        }
	        if(StringUtil.isNotEmpty(driverTypeId)){
	        	paramMap.put("driverTypeId", driverTypeId);//司机类型ID
	        }
	        if(StringUtil.isNotEmpty(driverScore)){
	        	paramMap.put("driverScore", driverScore);//司机评价分数	
	        }
	        if(StringUtil.isNotEmpty(appScore)){
	        	 paramMap.put("appScore", appScore);//APP评价分数	
	        }
	        if (StringUtils.isNotEmpty(classId)){
	        	paramMap.put("classId", classId);//班组id
			}
			if (StringUtils.isNotBlank(serviceTypeId))
			    paramMap.put("serviceTypeId", serviceTypeId);
			paramMap.put("queryDate", queryDate);//查询日期
	        // 数据权限设置
			paramMap = statisticalAnalysisService.getCurrentLoginUserParamMap(paramMap,orderCityId,allianceId,motorcadeId);
			if(paramMap==null){
				return;
			}
	        //String jsonString = JSON.toJSONString(paramMap);
		  List<SAASDriverEvaluateDetailDto> driverEvaluateList = dataExportList(paramMap);
		  OutputStream out=null;
		  OutputStreamWriter osw=null;
		  if (!CollectionUtils.isEmpty(driverEvaluateList)) {
			  response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode("司机评价详情.csv", "UTF-8"));
			  response.setContentType("text/csv;charset=utf-8");
			  out = response.getOutputStream();
			  out.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF}); // bom 头
			  osw = new OutputStreamWriter(out, StandardCharsets.UTF_8);
			  CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(DRIVEREVALUATEDETAIL_TITLE.split(","));
			  CSVPrinter csvPrinter = new CSVPrinter(osw, csvFormat);
			  for (SAASDriverEvaluateDetailDto dto : driverEvaluateList) {
				  csvPrinter.printRecord(dto.getDriverId(), dto.getDriverName(), dto.getVehiclePlateNo(), dto.getDriverScore(), dto.getDriverCityName(), dto.getEvaluateTime(), dto.getDriverTypeName(), dto.getAllianceName(), dto.getMotorcardName(), dto.getClassName(),
						  dto.getOrderNo(), dto.getDriverEvaluateText(), dto.getDriverEvaluateRemark(), dto.getEvaluateCustomerId(), dto.getMemberRankName(), dto.getCustomerName(), dto.getOrderSourceName(), dto.getActualAboardTime(), dto.getCompleteTime(),
						  dto.getActualOnboardLocation(), dto.getActualDebusLocation(), dto.getAppScore(), dto.getAppEvaluateText(), dto.getAppRemark(), dto.getOsName(), dto.getOsVersion(), dto.getSupervisorNo(), dto.getServiceTypeName());
			  }
			  csvPrinter.flush();
			  csvPrinter.close();
			  osw.close();
			  out.close();
		  }else{
		  	logger.info("导出无数据"+DateUtil.timestampFormat(new Date()));
		  }
      } catch (Exception e) {
          e.printStackTrace();
      }
  }


	/** 调用企业平台接口迁移查询司机评价列表改成直接查库 */
	public  AjaxResponse parseResultsQueryData(Map<String, Object> paramMap) {
		try {
			String jsonString = JSON.toJSONString(paramMap);
			logger.info("查询大数据库，参数--" + jsonString);
			SAASEvaluateDetailQuery saasQuery = JSON.parseObject(jsonString,SAASEvaluateDetailQuery.class);
			saasQuery.setTableName(SAAS_BI_DRIVER_EVALUATE_DETAIL + COMM + saasQuery.getQueryDate().replace("-", "_"));
			PageHelper.startPage(Integer.parseInt(saasQuery.getPageNo()), Integer.parseInt(saasQuery.getPageSize()));
			PageHelper.orderBy("fact_end_date");
			List<SAASDriverEvaluateDetailDto> driverEvaluateDetailList = driverEvaluateDetailExMapper.getDriverEvaluateDetail(saasQuery);
			PageInfo<SAASDriverEvaluateDetailDto> pageInfo = new PageInfo<>(driverEvaluateDetailList);
			if (pageInfo != null){
				JSONObject transData = new JSONObject();
				transData.put(Constants.RECORD_LIST, pageInfo.getList());
				transData.put(Constants.PAGE_NO, pageInfo.getPageNum());
				transData.put(Constants.TOTAL, pageInfo.getTotal());
				return AjaxResponse.success(transData);
			}
			return AjaxResponse.success(null);
		} catch (Exception e) {
			logger.error("查询bi列表数据库异常，"+SAAS_BI_DRIVER_EVALUATE_DETAIL, e);
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}
	}

    /** 调用企业平台接口迁移导出司机评价改成直接查库 */
    public  List<SAASDriverEvaluateDetailDto> dataExportList(Map<String, Object> paramMap) {
        try {
            List<SAASDriverEvaluateDetailDto> resultList = new ArrayList<>();
            String jsonString = JSON.toJSONString(paramMap);
            logger.info("查询大数据库，参数--" + jsonString);
            SAASEvaluateDetailQuery saasQuery = JSON.parseObject(jsonString,SAASEvaluateDetailQuery.class);
            saasQuery.setTableName(SAAS_BI_DRIVER_EVALUATE_DETAIL + COMM + saasQuery.getQueryDate().replace("-", "_"));
            resultList = driverEvaluateDetailExMapper.getDriverEvaluateDetailList(saasQuery);
            return resultList;
        } catch (Exception e) {
            logger.error("查询bi导出数据库异常，"+SAAS_BI_DRIVER_EVALUATE_DETAIL, e);
            return new ArrayList<>();
        }
    }

}