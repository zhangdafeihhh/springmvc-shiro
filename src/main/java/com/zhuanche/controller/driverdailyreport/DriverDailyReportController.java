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
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.controller.DriverQueryController;
import com.zhuanche.dto.DriverDailyReportDTO;
import com.zhuanche.dto.driver.CarDriverDayDutyDTO;
import com.zhuanche.entity.mdbcarmanage.DriverDailyReport;
import com.zhuanche.entity.mdbcarmanage.DriverDailyReportParams;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.serv.DriverDailyReportExService;
import com.zhuanche.serv.common.DataPermissionHelper;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.DateUtil;
import com.zhuanche.util.MyRestTemplate;
import com.zhuanche.util.excel.CsvUtils;
import mapper.mdbcarmanage.ex.CarRelateGroupExMapper;
import mapper.mdbcarmanage.ex.DriverDailyReportExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.*;

/**
 *   司机日报、周报、月报查询接口
 *   @Auther  wanghongdong
 */
@Controller
@RequestMapping(value = "/driverDailyReport")
public class DriverDailyReportController extends DriverQueryController {

	private static Logger log =  LoggerFactory.getLogger(DriverDailyReportController.class);

	@Autowired
	private DriverDailyReportExMapper driverDailyReportExMapper;

	@Autowired
	private DataPermissionHelper dataPermissionHelper;

	@Autowired
	private CarRelateGroupExMapper carRelateGroupExMapper;

	@Autowired
	private CarBizSupplierExMapper carBizSupplierExMapper;


	@Autowired
	@Qualifier("busOrderCostTemplate")
	private MyRestTemplate busOrderCostTemplate;

	@RequestMapping("/list")
	public String list(){
		return "driverdailyreport/driverlist";
	}

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
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
	})
	public AjaxResponse queryDriverWeekReportDataNew(String licensePlates, String driverName, String driverIds, String teamIds,
													 @Verify(rule = "required",param = "suppliers") String suppliers,
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
		List<DriverDailyReportDTO> dtoList = driverDailyReportExService.selectSuppierNameAndCityNameDays(pages.getList(),reportType);
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
	public AjaxResponse queryDriverWeekReportDataNew(@Verify(rule = "required",param = "statDateStart") String driverIds,
													 @Verify(rule = "required",param = "statDateStart") String statDateStart,
													 @Verify(rule = "required",param = "statDateEnd") String statDateEnd, String sortName, String sortOrder, Integer page, Integer pageSize) throws ParseException {
		if (statDateStart.compareTo(statDateEnd) > 0 ){
			return AjaxResponse.fail(RestErrorCode.STARTTIME_GREATE_ENDTIME);
		}
		//初始化查询参数
		DriverDailyReportParams params = new DriverDailyReportParams(driverIds,statDateStart,statDateEnd,sortName,sortOrder,page,pageSize);
		log.info("司机周报、月报详情列表数据:queryDriverReportDataDetail，参数："+params.toString());
		int total = 0;
		//根据 参数重新整理 入参条件 ,如果页面没有传入参数，则使用该用户绑定的权限
		List<DriverDailyReport> list = null;
		//开始查询
		Page<DriverDailyReport> p = PageHelper.startPage(params.getPage(), params.getPageSize());
		try {
			list = this.driverDailyReportExMapper.queryDriverReportData(params);
			total = (int) p.getTotal();
		} finally {
			PageHelper.clearPage();
		}
		//如果不为空，进行查询供应商名称
		List<DriverDailyReportDTO> dtoList = driverDailyReportExService.selectSuppierNameAndCityNameDays(list,0);
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
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
	})
	public AjaxResponse exportDriverReportData(String licensePlates, String driverName, String driverIds, String teamIds,
											   @Verify(rule = "required",param = "suppliers") String suppliers,
											   @Verify(rule = "required",param = "cities") String cities,
											   @Verify(rule = "required",param = "statDateStart") String statDateStart, String statDateEnd, String sortName, String sortOrder, String groupIds, Integer reportType, HttpServletRequest request, HttpServletResponse response) throws ParseException {

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
			if (statDateStart.compareTo(statDateEnd) > 0 ){
				return AjaxResponse.fail(RestErrorCode.STARTTIME_GREATE_ENDTIME);
			}
			//如果是月报，但是开始时间和结束时间不再同一月，不可以
			if (!statDateStart.substring(0,7).equals(statDateEnd.substring(0,7))){
				return AjaxResponse.fail(RestErrorCode.ONLY_QUERY_ONE_MONTH);
			}
		}

		DriverDailyReportParams params = new DriverDailyReportParams(licensePlates,driverName,driverIds,teamIds,suppliers,cities,statDateStart,statDateEnd,sortName,sortOrder,groupIds,null,null);

		log.info("司机周报列表数据:queryDriverDailyReportData");
		List<DriverDailyReportDTO> rows = new ArrayList<>();
		List<DriverDailyReport> list = new ArrayList<>();

		String driverList = null;
		if (StringUtils.isEmpty(params.getDriverIds())){
			//判断权限   如果司机id为空为查询列表页
			//如果页面输入了小组id
			if(StringUtils.isNotEmpty(params.getGroupIds())){
				//通过小组id查询司机id, 如果用户
				driverList = super.queryAuthorityDriverIdsByTeamAndGroup(null, String.valueOf(params.getGroupIds()));
				//如果该小组下无司机，返回空
				if(StringUtils.isEmpty(driverList)){
					log.info("司机日报列表-有选择小组查询条件-该小组下没有司机groupId=="+params.getGroupIds());
					list = new ArrayList<DriverDailyReport>();
				}
			}
		}else{
			driverList = params.getDriverIds();
		}


		long  start = System.currentTimeMillis();
		if(!(StringUtils.isNotEmpty(params.getGroupIds()) && (StringUtils.isEmpty(driverList)))){
			params.setDriverIds(driverList);
			//根据 参数重新整理 入参条件 ,如果页面没有传入参数，则使用该用户绑定的权限
			params = this.chuliDriverDailyReportEntity(params);
			//开始查询
			//开始查询
			PageInfo<DriverDailyReport> pages = null;
			if ( reportType==0 ) {
				pages = driverDailyReportExService.findDayDriverDailyReportByparam(params);
				list.addAll(pages.getList());
				if(pages.getPages() >=2 ){
					for(int i=2;i<pages.getPages();i++){
						params.setPage(i);
						pages = driverDailyReportExService.findDayDriverDailyReportByparam(params);
						list.addAll(pages.getList());
					}
				}
			}else{
				pages = driverDailyReportExService.findWeekDriverDailyReportByparam(params,  statDateStart,    statDateEnd);
				list.addAll(pages.getList());
				if(pages.getPages() >=2 ){
					for(int i=2;i<pages.getPages();i++){
						params.setPage(i);
						pages = driverDailyReportExService.findWeekDriverDailyReportByparam(params,  statDateStart,    statDateEnd);
						list.addAll(pages.getList());
					}
				}
			}
			long  end = System.currentTimeMillis();
			log.info("工作报告导出-查询耗时："+(end-start)+"毫秒");

			rows = driverDailyReportExService.selectSuppierNameAndCityNameDays(list,reportType);
			long  end1 = System.currentTimeMillis();
			log.info("工作报告导出-数据转化耗时："+(end1-end)+"毫秒");

		}
		try {

			List<String> headerList = new ArrayList<>();
			headerList.add("车牌号,姓名,供应商,车队,小组,上线时间,总在线时长（小时）,班在线时长（min）,计价前时间(min),计价前里程(km),载客中时间(min),载客里程(km)," +
					"总服务时间(min),总服务里程(km),计算异动时间(min),结算异动里程（km）,订单流水(元),价外流水（元）,价外费用,绑单完成数,抢单完成数," +
							"后台派单,接机,送机,完成单数,日期"
						);

			if(rows == null){
				rows = new ArrayList<>();
			}
			List<String> csvDataList  = new ArrayList<>(rows.size());
			dataTrans(rows,csvDataList,reportType);

			String fileName = "司机工作报告"+ com.zhuanche.util.dateUtil.DateUtil.dateFormat(new Date(), com.zhuanche.util.dateUtil.DateUtil.intTimestampPattern)+".csv";
			String agent = request.getHeader("User-Agent").toUpperCase(); //获得浏览器信息并转换为大写
			if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO")>0 && agent.indexOf("RV:11")>0)) {  //IE浏览器和Edge浏览器
				fileName = URLEncoder.encode(fileName, "UTF-8");
			} else {  //其他浏览器
				fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
			}
			CsvUtils.exportCsv(response,csvDataList,headerList,fileName);

			return AjaxResponse.success("文件导出成功");
		} catch (Exception e) {
			if(rows != null){
				rows.clear();
			}
			log.error("导出失败哦！");
			return AjaxResponse.fail(RestErrorCode.FILE_EXCEL_REPORT_FAIL);
		}
	}

	private  void dataTrans(List<DriverDailyReportDTO> result, List<String> csvDataList ,int reportType){
		if(null == result){
			return;
		}
//			"车牌号,姓名,供应商,车队,小组,上线时间,总在线时长（小时）,班在线时长（min）,计价前时间(min),计价前里程(km),载客中时间(min),载客里程(km)," +
//					"总服务时间(min),总服务里程(km),计算异动时间(min),结算异动里程（km）,订单流水(元),价外流水（元）,价外费用,绑单完成数,抢单完成数," +
//					"后台派单,接机,送机,完成单数,日期"

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


			stringBuffer.append(s.getUpOnlineTime()==null?"":s.getUpOnlineTime());
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

			stringBuffer.append(s.getOperationNum());//完成单数
			stringBuffer.append(",");

			if (reportType == 0){
				stringBuffer.append(s.getStatDate());
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
		return driverDailyReportBean;
	}


	 
}