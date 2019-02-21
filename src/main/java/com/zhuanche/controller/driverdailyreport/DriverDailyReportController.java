package com.zhuanche.controller.driverdailyreport;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.controller.DriverQueryController;
import com.zhuanche.dto.DriverDailyReportDTO;
import com.zhuanche.entity.mdbcarmanage.DriverDailyReport;
import com.zhuanche.entity.mdbcarmanage.DriverDailyReportParams;
import com.zhuanche.serv.DriverDailyReportExService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.DateUtil;
import com.zhuanche.util.excel.CsvUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.zhuanche.common.enums.MenuEnum.*;

/**
 *   司机日报、周报、月报查询接口
 *   @Auther  wanghongdong
 */
@Controller
@RequestMapping(value = "/driverDailyReport")
public class DriverDailyReportController extends DriverQueryController {

	private static Logger log =  LoggerFactory.getLogger(DriverDailyReportController.class);

	@Autowired
	private DriverDailyReportExService driverDailyReportExService;

	/**
	 * 日报查询
	 * @param licensePlates 车牌号
	 * @param driverName 司机姓名
	 * @param driverIds 司机id
	 * @param teamIds 车队id
	 * @param suppliers 供应商id
	 * @param cities 城市id
	 * @param statDateStart 开始时间
	 * @param statDateEnd 结束时间
	 * @param sortName 排序名称
	 * @param sortOrder 排序顺序
	 * @param groupIds 组id
	 * @param page  当前页
	 * @param pageSize  页面展示数量
	 * @param reportType  报表类型  0 日报 1周报 2 月报  默认为0，查询日报
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryDriverReportData")
	@RequiresPermissions(value = { "DriverDaily_look" } )
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
	})
	@RequestFunction(menu = DRIVER_WORK_REPORT_LIST)
	public AjaxResponse queryDriverWeekReportDataNew(String licensePlates, String driverName, String driverIds, String teamIds,
													 String suppliers,
													 @Verify(rule = "required",param = "cities") String cities,
													 @Verify(rule = "required",param = "statDateStart") String statDateStart,
													 @Verify(rule = "required",param = "statDateEnd") String statDateEnd, String sortName, String sortOrder, String groupIds, Integer page, Integer pageSize, Integer reportType) throws ParseException {
		//默认报告类型为日报
		reportType = reportType == null ? 0 : reportType;
		if (reportType.equals(0)){
			statDateEnd = statDateStart;
		}else if(reportType.equals(1)){
			//如果是周报，但是开始时间和结束时间不再同一周，不可以
			if (statDateStart.compareTo(statDateEnd) > 0 ){
				return AjaxResponse.fail(RestErrorCode.STARTTIME_GREATE_ENDTIME);
			}
			if (!DateUtil.isWeekSame(statDateStart,statDateEnd)){
				return AjaxResponse.fail(RestErrorCode.ONLY_QUERY_WEEK);
			}
		}else if (reportType.equals(2)){
			//如果是月报，但是开始时间和结束时间不再同一月，不可以
			if (statDateStart.compareTo(statDateEnd) > 0 ){
				return AjaxResponse.fail(RestErrorCode.STARTTIME_GREATE_ENDTIME);
			}
			if (!statDateStart.substring(0,7).equals(statDateEnd.substring(0,7))){
				return AjaxResponse.fail(RestErrorCode.ONLY_QUERY_ONE_MONTH);
			}
		}
		//初始化查询参数
		DriverDailyReportParams params = new DriverDailyReportParams(licensePlates,driverName,driverIds,teamIds,suppliers,cities,statDateStart,statDateEnd,sortName,sortOrder,groupIds,page,pageSize);

		log.info("司机周报列表数据:queryDriverDailyReportData，参数："+(params==null?"null": JSON.toJSONString(params)));
		int total = 0;
		//判断权限   如果司机id为空为查询列表页
		if(StringUtils.isEmpty(params.getDriverIds())){
			String driverList = "";
			//如果页面输入了小组id
			if(StringUtils.isNotEmpty(params.getGroupIds())){
				//通过小组id查询司机id, 如果用户
				driverList = super.queryAuthorityDriverIdsByTeamAndGroup(null, String.valueOf(params.getGroupIds()));
				//如果该小组下无司机，返回空
				if(StringUtils.isEmpty(driverList)){
					log.info("司机日报列表-有选择小组查询条件-该小组下没有司机groupId=="+params.getGroupIds());
					PageDTO pageDTO = new PageDTO(params.getPage(), params.getPageSize(), total, null);
					return AjaxResponse.success(pageDTO);
				}
			}
			params.setDriverIds(driverList);
		}
		//根据 参数重新整理 入参条件 ,如果页面没有传入参数，则使用该用户绑定的权限
		params = this.chuliDriverDailyReportEntity(params);
		List<DriverDailyReport> x = null;
		//开始查询
		PageInfo<DriverDailyReport> pages = null;
		if ( reportType==0 ) {
			pages = driverDailyReportExService.findDayDriverDailyReportByparam(params);
		}else{
			pages = driverDailyReportExService.findWeekDriverDailyReportByparam(params,  statDateStart,    statDateEnd);
		}

		//如果不为空，进行查询供应商名称
		List<DriverDailyReportDTO> dtoList = driverDailyReportExService.selectSuppierNameAndCityNameDays(pages.getList(),reportType,statDateStart,statDateEnd);
		PageDTO pageDTO =  new PageDTO();
		pageDTO.setPage(params.getPage());
		pageDTO.setPageSize(params.getPageSize());
		pageDTO.setTotal(new Integer(pages.getTotal()+""));
		pageDTO.setResult(dtoList);

		return AjaxResponse.success(pageDTO);
	}



	/**
	 * 周报、月报详情
	 * @param driverIds 司机id
	 * @param statDateStart 开始时间
	 * @param statDateEnd 结束时间
	 * @param sortName 排序名称
	 * @param sortOrder 排序顺序
	 * @param page  当前页
	 * @param pageSize  页面展示数量
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryDriverReportDataDetail")
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
	})
	@RequestFunction(menu = DRIVER_WORK_REPORT_DETAIL)
	public AjaxResponse queryDriverWeekReportDataNew(@Verify(rule = "required",param = "statDateStart") String driverIds,
													 @Verify(rule = "required",param = "statDateStart") String statDateStart,
													 @Verify(rule = "required",param = "statDateEnd") String statDateEnd, String sortName, String sortOrder, Integer page, Integer pageSize) throws ParseException {
		if (statDateStart.compareTo(statDateEnd) > 0 ){
			return AjaxResponse.fail(RestErrorCode.STARTTIME_GREATE_ENDTIME);
		}
		//初始化查询参数
		DriverDailyReportParams params = new DriverDailyReportParams(driverIds,statDateStart,statDateEnd,sortName,sortOrder,page,pageSize);
		//根据 参数重新整理 入参条件 ,如果页面没有传入参数，则使用该用户绑定的权限
		params = this.chuliDriverDailyReportEntity(params);
		log.info("司机周报、月报详情列表数据:queryDriverReportDataDetail，参数："+params.toString());
		int total = 0;
		//根据 参数重新整理 入参条件 ,如果页面没有传入参数，则使用该用户绑定的权限
		List<DriverDailyReport> list = null;
		//开始查询
		Page<DriverDailyReport> p = PageHelper.startPage(params.getPage(), params.getPageSize());
		try {
			list = this.driverDailyReportExService.queryDriverReportData(params);
			total = (int) p.getTotal();
		} finally {
			PageHelper.clearPage();
		}
		//如果不为空，进行查询供应商名称
		List<DriverDailyReportDTO> dtoList = driverDailyReportExService.selectSuppierNameAndCityNameDays(list,0,statDateStart,statDateEnd);
		PageDTO pageDTO = new PageDTO(params.getPage(), params.getPageSize(), total, dtoList);
		return AjaxResponse.success(pageDTO);
	}


	/**
	 * excel导出
	 * @param licensePlates 车牌号
	 * @param driverName 司机姓名
	 * @param driverIds 司机id
	 * @param teamIds 车队id
	 * @param suppliers 供应商id
	 * @param cities 城市id
	 * @param statDateStart 开始时间
	 * @param statDateEnd 结束时间
	 * @param sortName 排序名称
	 * @param sortOrder 排序顺序
	 * @param groupIds 组id
	 * @param reportType  报表类型  0 日报 1周报 2 月报  默认为0，查询日报
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/exportDriverReportData")
	@RequiresPermissions(value = { "DriverDaily_export" } )
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
	})
	@RequestFunction(menu = DRIVER_WORK_REPORT_EXPORT)
	public String exportDriverReportData(String licensePlates, String driverName, String driverIds, String teamIds,
											 String suppliers,
											   @Verify(rule = "required",param = "cities") String cities,
											   @Verify(rule = "required",param = "statDateStart") String statDateStart,
											   String statDateEnd,
											   String sortName, String sortOrder, String groupIds, Integer reportType, HttpServletRequest request, HttpServletResponse response) throws ParseException {

		JSONObject searchParam = new JSONObject();
		searchParam.put("teamIds",teamIds);
		searchParam.put("suppliers",suppliers);
		searchParam.put("cities",cities);
		searchParam.put("statDateStart",statDateStart);
		searchParam.put("statDateEnd",statDateEnd);
		searchParam.put("sortName",sortName);
		searchParam.put("sortOrder",sortOrder);
		searchParam.put("groupIds",groupIds);
		searchParam.put("reportType",reportType);


		//默认报告类型为日报
		String fileTag = "";
		reportType = reportType == null ? 0 : reportType;
		if (reportType.equals(0)){
			fileTag = "工作报告日报";
			statDateEnd = statDateStart;
		}else if(reportType.equals(1)){
			fileTag = "工作报告周报";
			//如果是周报，但是开始时间和结束时间不再同一周，不可以
			if (statDateStart.compareTo(statDateEnd) > 0 ){
//				return AjaxResponse.fail(RestErrorCode.STARTTIME_GREATE_ENDTIME);
				return "";
			}
			if (!DateUtil.isWeekSame(statDateStart,statDateEnd)){
//				return AjaxResponse.fail(RestErrorCode.ONLY_QUERY_WEEK);
				return "";
			}
		}else if (reportType.equals(2)){
			fileTag = "工作报告月报";
			if (statDateStart.compareTo(statDateEnd) > 0 ){
//				return AjaxResponse.fail(RestErrorCode.STARTTIME_GREATE_ENDTIME);
				return "";
			}
			//如果是月报，但是开始时间和结束时间不再同一月，不可以
			if (!statDateStart.substring(0,7).equals(statDateEnd.substring(0,7))){
//				return AjaxResponse.fail(RestErrorCode.ONLY_QUERY_ONE_MONTH);
				return "";
			}
		}
		List<String> headerList = new ArrayList<>();
		String fileName = "";
		List<String> csvDataList = new ArrayList<>();

		long  start = System.currentTimeMillis();

		try {
			headerList.add("车牌号,姓名,供应商,车队,小组,上线时间,总在线时长（小时）,班在线时长（min）,计价前时间(min),计价前里程(km),载客中时间(min),载客里程(km)," +
					"总服务时间(min),总服务里程(km),计算异动时间(min),结算异动里程（km）,司机营业额(元),价外费用（元）,绑单完成数,抢单完成数," +
					"后台派单,接机,送机,完成单数,日期"
			);

			fileName = fileTag+""+ com.zhuanche.util.dateUtil.DateUtil.dateFormat(new Date(), com.zhuanche.util.dateUtil.DateUtil.intTimestampPattern)+".csv";
			String agent = request.getHeader("User-Agent").toUpperCase(); //获得浏览器信息并转换为大写
			if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO")>0 && agent.indexOf("RV:11")>0)) {  //IE浏览器和Edge浏览器

				fileName = URLEncoder.encode(fileName, "UTF-8");

			} else {  //其他浏览器
				fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
			}

			DriverDailyReportParams params = new DriverDailyReportParams(licensePlates,driverName,driverIds,teamIds,suppliers,cities,statDateStart,statDateEnd,sortName,sortOrder,groupIds,null,null);


			List<DriverDailyReportDTO> rows = new ArrayList<>();
			String driverList = null;
			if (StringUtils.isEmpty(params.getDriverIds())){
				//判断权限   如果司机id为空为查询列表页
				//如果页面输入了小组id
				if(StringUtils.isNotEmpty(params.getGroupIds())){
					//通过小组id查询司机id, 如果用户
					driverList = super.queryAuthorityDriverIdsByTeamAndGroup(null, String.valueOf(params.getGroupIds()));
					//如果该小组下无司机，返回空
					if(StringUtils.isEmpty(driverList)){
						csvDataList.add("没有查到符合条件的数据");
						CsvUtils entity = new CsvUtils();
						try {
							entity.exportCsvV2(response,csvDataList,headerList,fileName,true,true);
						} catch (IOException e) {
							e.printStackTrace();
						}
//						return AjaxResponse.success("没有查到符合条件的数据");
						return "没有查到符合条件的数据";

					}
				}
			}else{
				driverList = params.getDriverIds();
			}
			params.setPageSize(CsvUtils.downPerSize);
			if(!(StringUtils.isNotEmpty(params.getGroupIds()) && (StringUtils.isEmpty(driverList)))){
				params.setDriverIds(driverList);
				//根据 参数重新整理 入参条件 ,如果页面没有传入参数，则使用该用户绑定的权限
				params = this.chuliDriverDailyReportEntity(params);
				//开始查询
				PageInfo<DriverDailyReport> pageInfos = null;
				if ( reportType==0 ) {

					pageInfos = driverDailyReportExService.findDayDriverDailyReportByparam(params);
					List<DriverDailyReport> result = pageInfos.getList();
					if(result == null || result.size() == 0){
						csvDataList.add("没有查到符合条件的数据");
						CsvUtils entity = new CsvUtils();
						entity.exportCsvV2(response,csvDataList,headerList,fileName,true,true);
//						return AjaxResponse.success("没有查到符合条件的数据");
						return "没有查到符合条件的数据";
					}
					int pages = pageInfos.getPages();//临时计算总页数
					boolean isFirst = true;
					boolean isLast = false;
					if(pages == 1 ||pages == 0 ){
						isLast = true;
					}

					rows = driverDailyReportExService.selectSuppierNameAndCityNameDays(result,reportType,statDateStart,statDateEnd);
					dataTrans(rows,csvDataList,reportType);
					log.info("工作日报:第1页/共"+pages+"页，查询条件为："+JSON.toJSONString(params));
					CsvUtils entity = new CsvUtils();
					entity.exportCsvV2(response,csvDataList,headerList,fileName,isFirst,isLast);
					csvDataList = null;
					isFirst = false;
					for(int pageNumber = 2;pageNumber <= pages ; pageNumber++){
						params.setPage(pageNumber);
						rows = null;
						log.info("工作日报:第"+pageNumber+"页/共"+pages+"页，查询条件为："+JSON.toJSONString(params));
						pageInfos = driverDailyReportExService.findDayDriverDailyReportByparam(params);
						result = pageInfos.getList();
						csvDataList = new ArrayList<>();
						if(pageNumber == pages){
							isLast = true;
						}
						rows = driverDailyReportExService.selectSuppierNameAndCityNameDays(result,reportType,statDateStart,statDateEnd);
						dataTrans(rows,csvDataList,reportType);
						entity.exportCsvV2(response,csvDataList,headerList,fileName,isFirst,isLast);
					}


				}else{

					pageInfos = driverDailyReportExService.findWeekDriverDailyReportByparam(params,  statDateStart,    statDateEnd);
					int pages = pageInfos.getPages();//临时计算总页数
					log.info(fileTag+":第"+1+"页/共"+pages+"页，查询条件为："+JSON.toJSONString(params));
					List<DriverDailyReport> result = pageInfos.getList();
					if(result == null || result.size() == 0){
						csvDataList.add("没有查到符合条件的数据");
						CsvUtils entity = new CsvUtils();
						entity.exportCsvV2(response,csvDataList,headerList,fileName,true,true);
						return "";
					}

					boolean isFirst = true;
					boolean isLast = false;
					if(pages == 1){
						isLast = true;
					}
					rows = driverDailyReportExService.selectSuppierNameAndCityNameDays(result,reportType,statDateStart,statDateEnd);
					dataTrans(rows,csvDataList,reportType);

					CsvUtils entity = new CsvUtils();
					entity.exportCsvV2(response,csvDataList,headerList,fileName,isFirst,isLast);
					csvDataList = null;
					isFirst = false;
					for(int pageNumber = 2;pageNumber <= pages ; pageNumber++){
						params.setPage(pageNumber);
						log.info(fileTag+":第"+pageNumber+"页/共"+pages+"页，查询条件为："+JSON.toJSONString(params));
						rows = null;
						pageInfos = driverDailyReportExService.findWeekDriverDailyReportByparam(params,  statDateStart,    statDateEnd);
						result = pageInfos.getList();
						csvDataList = new ArrayList<>();
						if(pageNumber == pages){
							isLast = true;
						}
						rows = driverDailyReportExService.selectSuppierNameAndCityNameDays(result,reportType,statDateStart,statDateEnd);
						dataTrans(rows,csvDataList,reportType);
						entity.exportCsvV2(response,csvDataList,headerList,fileName,isFirst,isLast);
					}

				}
				long  end = System.currentTimeMillis();
				log.info("工作报告导出-查询耗时："+(end-start)+"毫秒");
			}
			return "";
		} catch (Exception e) {
			log.error("导出失败哦，参数为："+(searchParam.toJSONString()),e);
			return "";
		}
	}

	private  void dataTrans(List<DriverDailyReportDTO> result, List<String> csvDataList ,int reportType){
		if(null == result){
			return;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 24小时制

		for (DriverDailyReportDTO s : result) {
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append(s.getLicensePlates() != null ? ""
					+ s.getLicensePlates() + "" : "");
			stringBuffer.append(",");


			stringBuffer.append(s.getDriverName() != null ? ""
					+ s.getDriverName() + "" : "");
			stringBuffer.append(",");


			stringBuffer.append(s.getSupplierName() != null ? ""
					+ s.getSupplierName() + "" : "");
			stringBuffer.append(",");


			stringBuffer.append(s.getTeamName()!= null ? ""
					+ s.getTeamName() + "" : "");
			stringBuffer.append(",");


			stringBuffer.append(s.getGroupName()!= null ? ""
					+ s.getGroupName() + "" : "");
			stringBuffer.append(",");


			stringBuffer.append(s.getUpOnlineTime()==null?"":"\t"+s.getUpOnlineTime().replace(".0",""));
			stringBuffer.append(",");


			stringBuffer.append(s.getOnlineTime() );
			stringBuffer.append(",");

			stringBuffer.append(s.getForcedTime());
			stringBuffer.append(",");

			stringBuffer.append(s.getTravelTimeStart());
			stringBuffer.append(",");

			stringBuffer.append(s.getTravelMileageStart());
			stringBuffer.append(",");

			stringBuffer.append(s.getTravelTime());
			stringBuffer.append(",");

			stringBuffer.append(s.getTravelMileage());
			stringBuffer.append(",");

			stringBuffer.append(s.getServiceTime());
			stringBuffer.append(",");

			stringBuffer.append(s.getServiceMileage());
			stringBuffer.append(",");

			stringBuffer.append(s.getTravelTimeEnd());
			stringBuffer.append(",");

			stringBuffer.append(s.getTravelMileageEnd());
			stringBuffer.append(",");

			stringBuffer.append(s.getActualPay());
			stringBuffer.append(",");

			stringBuffer.append(s.getDriverOutPay());
			stringBuffer.append(",");

			stringBuffer.append(s.getAssignOrderNum());
			stringBuffer.append(",");

			stringBuffer.append(s.getContendOrderNum());
			stringBuffer.append(",");

			stringBuffer.append(s.getPlatformOrderNum());
			stringBuffer.append(",");

			stringBuffer.append(s.getGetPlaneNum());
			stringBuffer.append(",");

			stringBuffer.append(s.getOutPlaneNum()); //送机数
			stringBuffer.append(",");

			stringBuffer.append(s.getOperationNum()==null?"":s.getOperationNum());//完成单数
			stringBuffer.append(",");

			if (reportType == 0){
				stringBuffer.append(s.getStatDate()==null?"":("\t"+s.getStatDate()));
			}else{
				stringBuffer.append("("+s.getStatDateStart()+")-("+s.getStatDateEnd()+")");
			}

			csvDataList.add(stringBuffer.toString());
		}
	}


	/**
	 * <p>Title: chuliDriverDailyReportEntity</p>
	 * <p>Description: 根据 参数重新整理 入参条件 ,如果页面没有传入参数，则使用该用户绑定的权限 </p>
	 * @param driverDailyReportBean
	 * @return
	 * return: DriverDailyReportDTO
	 */
	public DriverDailyReportParams chuliDriverDailyReportEntity(DriverDailyReportParams driverDailyReportBean){
		//整理排序字段
		if(!"".equals(driverDailyReportBean.getSortName())&&driverDailyReportBean.getSortName()!=null){
			String sortName = pingSortName(driverDailyReportBean.getSortName());
			driverDailyReportBean.setSortName(sortName);
		}
		//获取当前 用户
		SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
		//当前用户权限
		String cities = currentLoginUser.getCityIds().toString();
		String suppliers = currentLoginUser.getSupplierIds().toString();
		String teamIds = currentLoginUser.getTeamIds().toString();
		if("".equals(driverDailyReportBean.getCities())||driverDailyReportBean.getCities()==null){
			driverDailyReportBean.setCities(cities.substring(1, cities.length()-1));
		}
		if("".equals(driverDailyReportBean.getSuppliers())||driverDailyReportBean.getSuppliers()==null){
			driverDailyReportBean.setSuppliers(suppliers.substring(1, suppliers.length()-1));
		}
		if("".equals(driverDailyReportBean.getTeamIds())||driverDailyReportBean.getTeamIds()==null){
			driverDailyReportBean.setTeamIds(teamIds.substring(1, teamIds.length()-1));
		}
		String tableName = "driver_daily_report";
		String statDateStart = driverDailyReportBean.getStatDateStart();
		if(StringUtils.isNotEmpty(statDateStart)){
			Integer year = Integer.parseInt(statDateStart.substring(0,4));
			if(year!=null && year>2018){
				tableName += "_" + driverDailyReportBean.getStatDateStart().substring(0, 7).replace("-", "_");
				driverDailyReportBean.setValue(1);
			}
		}
		driverDailyReportBean.setTableName(tableName);
		return driverDailyReportBean;
	}
}