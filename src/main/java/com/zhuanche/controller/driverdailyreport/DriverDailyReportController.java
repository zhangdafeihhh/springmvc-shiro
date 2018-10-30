package com.zhuanche.controller.driverdailyreport;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.controller.DriverQueryController;
import com.zhuanche.dto.DriverDailyReportDTO;
import com.zhuanche.entity.mdbcarmanage.DriverDailyReport;
import com.zhuanche.entity.mdbcarmanage.DriverDailyReportParams;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.serv.common.DataPermissionHelper;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.DateUtil;
import com.zhuanche.util.MyRestTemplate;
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

		log.info("司机周报列表数据:queryDriverDailyReportData，参数："+params.toString());
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
		List<DriverDailyReport> list = null;
		//开始查询
		Page<DriverDailyReport> p = PageHelper.startPage(params.getPage(), params.getPageSize());

		try {
			if ( reportType==0 ) {
				list = this.driverDailyReportExMapper.queryForListObject(params);
			}else{
				list = this.driverDailyReportExMapper.queryWeekForListObject(params);
				if(list!=null && list.size()>0){
					for (DriverDailyReport report: list) {
						report.setStatDateStart(statDateStart);
						report.setStatDateEnd(statDateEnd);
					}
				}
			}
			total = (int) p.getTotal();
		} finally {
			PageHelper.clearPage();
		}

		//如果不为空，进行查询供应商名称
		List<DriverDailyReportDTO> dtoList = this.selectSuppierNameAndCityNameDays(list,reportType);
		PageDTO pageDTO = new PageDTO(params.getPage(), params.getPageSize(), total, dtoList);

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
		List<DriverDailyReportDTO> dtoList = this.selectSuppierNameAndCityNameDays(list,0);
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

		long time = new Date().getTime();
		long time2 = 1;
		String filename = "司机周/月报列表";
		if(!(StringUtils.isNotEmpty(params.getGroupIds()) && (StringUtils.isEmpty(driverList)))){
			params.setDriverIds(driverList);
			//根据 参数重新整理 入参条件 ,如果页面没有传入参数，则使用该用户绑定的权限
			params = this.chuliDriverDailyReportEntity(params);
			//开始查询
			if ( reportType==0 ) {
				list = this.driverDailyReportExMapper.queryForListObject(params);
				filename = "司机日报列表";
			}else {
				list = this.driverDailyReportExMapper.queryWeekForListObject(params);
				if(list!=null && list.size()>0){
					for (DriverDailyReport report: list) {
						report.setStatDateStart(statDateStart);
						report.setStatDateEnd(statDateEnd);
					}
				}
			}

			long time1 = new Date().getTime();
			log.info("month report queryDataBase time :"+ (time1-time));

			rows = this.selectSuppierNameAndCityNameDays(list,reportType);

			time2 = new Date().getTime();
			log.info("month report queryService time :"+ (time2-time1));
		}
		try {
			Workbook wb = this.exportExcel(rows,request.getRealPath("/")+ File.separator+"template"+File.separator+"driverDailyReport_info.xlsx",reportType);
			long time3 = new Date().getTime();
			log.info("month report exportExcel time :"+ (time3-time2));
			this.exportExcelFromTemplet(request, response, wb, new String(filename.getBytes("gb2312"), "iso8859-1"));
			return AjaxResponse.success("文件导出成功");
		} catch (Exception e) {
			if(rows != null){
				rows.clear();
			}
			log.error("导出失败哦！");
			return AjaxResponse.fail(RestErrorCode.FILE_EXCEL_REPORT_FAIL);
		}
	}

	/**
	 * <p>Title: selectSuppierNameAndCityNameDays</p>
	 * <p>Description: 转换</p>
	 * @param rows
	 * @return
	 * return: List<DriverDailyReportDTO>
	 */
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
	})
	public List<DriverDailyReportDTO> selectSuppierNameAndCityNameDays(List<DriverDailyReport> rows,Integer reportType) throws ParseException {
		List<DriverDailyReportDTO> list = null;
		//不为空进行转换并查询城市名称和供应商名称
		if(rows!=null&&rows.size()>0){
			list = BeanUtil.copyList(rows, DriverDailyReportDTO.class);
			Set<Integer> s = new HashSet();
			for(DriverDailyReportDTO driverDailyReport : list){
				s.add(driverDailyReport.getSupplierId());
			}
			//查询供应商名称，一次查询出来避免多次读库
			List<CarBizSupplier>  names = this.carBizSupplierExMapper.queryNamesByIds(s);
			for (DriverDailyReportDTO dto: list) {
				for (CarBizSupplier name: names) {
					if (name.getSupplierId().equals(dto.getSupplierId())){
						dto.setSupplierName(name.getSupplierFullName());
						break;
					}
				}
				//司机营业信息查询
				if (reportType==0){
					this.modifyDriverVolume(dto, dto.getStatDate());
				}else{
					this.modifyMonthDriverVolume(dto,dto.getStatDateStart(),dto.getStatDateEnd());
					dto.setStatDate("("+dto.getStatDateStart()+")-("+dto.getStatDateEnd()+")");
				}
			}
		}
		return list;
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

	/**
	 *
	 * <p>Title: modifyDriverVolume</p>
	 * <p>Description: 司机营业信息查询</p>
	 * @param ddre
	 * @param statDateStart
	 * return: void
	 */
	private void modifyDriverVolume(DriverDailyReportDTO ddre, String statDateStart) {
		if(StringUtils.isNotEmpty(statDateStart) && statDateStart.compareTo("2018-01-01") >0 ){
			String url = "/driverIncome/getDriverIncome?driverId="+ddre.getDriverId()+"&incomeDate=" + statDateStart;
			String result = busOrderCostTemplate.getForObject(url, String.class);

			Map<String, Object> resultMap = JSONObject.parseObject(result, HashMap.class);
			if (null == resultMap || !String.valueOf(resultMap.get("code")).equals("0")) {
				log.info("modifyDriverVolume查询接口【/driverIncome/getDriverIncome】返回异常,code:"+String.valueOf(resultMap.get("code")));
				return;
			}

			String reData = String.valueOf(resultMap.get("data"));
			if (StringUtils.isBlank(reData)) {
				log.info("modifyDriverVolume查询接口【/driverIncome/getDriverIncome】返回data为空.");
				return;
			}

			Map dataMap = JSONObject.parseObject(String.valueOf(resultMap.get("data")), Map.class);
			if (dataMap!=null){
				String driverIncome = String.valueOf(dataMap.get("driverIncome"));
				if (StringUtils.isBlank(driverIncome)) {
					log.info("modifyDriverVolume查询接口【/driverIncome/getDriverIncome】返回driverIncome为空.");
					return;
				}
				JSONObject jsonObject = JSONObject.parseObject(driverIncome);
//				log.info("modifyDriverVolume查询接口【/driverIncome/getDriverIncome】返回jsonObject成功."+jsonObject);
				// 当日完成订单量
				Integer orderCounts= Integer.valueOf(String.valueOf(jsonObject.get("orderCounts")));
				ddre.setOperationNum(orderCounts);
				// 当日营业额
				BigDecimal todayIncomeAmount = new BigDecimal(String.valueOf(jsonObject.get("todayIncomeAmount")));
				ddre.setActualPay(todayIncomeAmount.doubleValue());
				// 当日载客里程
				BigDecimal todayTravelMileage = new BigDecimal(String.valueOf(jsonObject.get("todayTravelMileage")));
				ddre.setServiceMileage(todayTravelMileage.doubleValue());
				// 当日司机代付价外费
				BigDecimal todayOtherFee = new BigDecimal(String.valueOf(jsonObject.get("todayOtherFee")));
				ddre.setDriverOutPay(todayOtherFee.doubleValue());
				// 当日司机代收
				BigDecimal todayDriverPay = new BigDecimal(String.valueOf(jsonObject.get("todayDriverPay")));
			}
		}
	}

	private void modifyMonthDriverVolume(DriverDailyReportDTO ddre, String statDateStart,String endDateStart) throws ParseException {
		if(StringUtils.isNotEmpty(statDateStart) && statDateStart.compareTo("2018-01-01") >0 ){
			long statTime = DateUtil.DATE_SIMPLE_FORMAT.parse(statDateStart).getTime();
			long endTime = DateUtil.DATE_SIMPLE_FORMAT.parse(endDateStart).getTime();
			String url = "/driverIncome/getDriverDateIncome?driverId="+ddre.getDriverId()+"&startDate=" + statTime+"&endDate=" + endTime;
			String result = busOrderCostTemplate.getForObject(url, String.class);

			Map<String, Object> resultMap = JSONObject.parseObject(result, HashMap.class);
			if (null == resultMap || !String.valueOf(resultMap.get("code")).equals("0")) {
				log.info("modifyMonthDriverVolume查询接口【/driverIncome/getDriverIncome】返回异常,code:"+String.valueOf(resultMap.get("code")));
				return;
			}

			String reData = String.valueOf(resultMap.get("data"));
			if (StringUtils.isBlank(reData)) {
				log.info("modifyMonthDriverVolume查询接口【/driverIncome/getDriverIncome】返回data为空.");
				return;
			}

			Map dataMap = JSONObject.parseObject(String.valueOf(resultMap.get("data")), Map.class);
			if (dataMap!=null){
				String driverIncome = String.valueOf(dataMap.get("driverIncome"));
				if (StringUtils.isBlank(driverIncome)) {
					log.info("modifyMonthDriverVolume查询接口【/driverIncome/getDriverIncome】返回driverIncome为空.");
					return;
				}

				JSONObject jsonObject = JSONObject.parseObject(driverIncome);
//				log.info("modifyMonthDriverVolume查询接口【/driverIncome/getDriverIncome】返回jsonObject成功."+jsonObject);
				// 当段日期完成订单量
				Integer orderCounts= Integer.valueOf(String.valueOf(jsonObject.get("orderCounts")));
				ddre.setOperationNum(orderCounts);
				// 当段日期营业额
				BigDecimal incomeAmount = new BigDecimal(String.valueOf(jsonObject.get("incomeAmount")));
				ddre.setActualPay(incomeAmount.doubleValue());
//					// 当段日期载客里程
//					BigDecimal todayTravelMileage = new BigDecimal(String.valueOf(jsonObject.get("todayTravelMileage")));
//					ddre.setServiceMileage(todayTravelMileage.doubleValue());
//					// 当段日期司机代付价外费
//					BigDecimal todayOtherFee = new BigDecimal(String.valueOf(jsonObject.get("todayOtherFee")));
//					ddre.setDriverOutPay(todayOtherFee.doubleValue());
//					// 当段日期司机代收
//					BigDecimal todayDriverPay = new BigDecimal(String.valueOf(jsonObject.get("todayDriverPay")));
			}
		}
	}


	/**
	 * 整理excel 行
	 * @param list
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public Workbook exportExcel(List<DriverDailyReportDTO> list, String path, Integer reportType)
			throws Exception {
		FileInputStream io = new FileInputStream(path);
		Workbook wb = new XSSFWorkbook(io);
		if (list != null && list.size() > 0) {
			Sheet sheet = wb.getSheetAt(0);
			Cell cell = null;
			int i = 1;
			for (DriverDailyReportDTO s : list) {
				Row row = sheet.createRow(i + 1);
				cell = row.createCell(0);
				cell.setCellValue(s.getLicensePlates() != null ? ""
						+ s.getLicensePlates() + "" : "");

				cell = row.createCell(1);
				cell.setCellValue(s.getDriverName() != null ? ""
						+ s.getDriverName() + "" : "");

				cell = row.createCell(2);
				cell.setCellValue(s.getSupplierName() != null ? ""
						+ s.getSupplierName() + "" : "");

				cell = row.createCell(3);
				cell.setCellValue(s.getTeamName()!= null ? ""
						+ s.getTeamName() + "" : "");

				cell = row.createCell(4);
				cell.setCellValue(s.getGroupName()!= null ? ""
						+ s.getGroupName() + "" : "");

				cell = row.createCell(5);
				cell.setCellValue(s.getUpOnlineTime());

				cell = row.createCell(6);
				cell.setCellValue(s.getOnlineTime());

				cell = row.createCell(7);
				cell.setCellValue(s.getForcedTime());

				cell = row.createCell(8);
				cell.setCellValue(s.getTravelTimeStart());

				cell = row.createCell(9);
				cell.setCellValue(s.getTravelMileageStart());

				cell = row.createCell(10);
				cell.setCellValue(s.getTravelTime());

				cell = row.createCell(11);
				cell.setCellValue(s.getTravelMileage());

				cell = row.createCell(12);
				cell.setCellValue(s.getServiceTime());

				cell = row.createCell(13);
				cell.setCellValue(s.getServiceMileage());

				cell = row.createCell(14);
				cell.setCellValue(s.getTravelTimeEnd());

				cell = row.createCell(15);
				cell.setCellValue(s.getTravelMileageEnd());

				cell = row.createCell(16);
				cell.setCellValue(s.getActualPay());

				cell = row.createCell(17);
				cell.setCellValue(s.getDriverOutPay());

				cell = row.createCell(18);
				cell.setCellValue(s.getAssignOrderNum());

				cell = row.createCell(19);
				cell.setCellValue(s.getContendOrderNum());

				cell = row.createCell(20);
				cell.setCellValue(s.getPlatformOrderNum());

				cell = row.createCell(21);
				cell.setCellValue(s.getGetPlaneNum());

				cell = row.createCell(22);
				cell.setCellValue(s.getOutPlaneNum());

				cell = row.createCell(23);
				cell.setCellValue(s.getOperationNum());

				cell = row.createCell(24);
				if (reportType==0){
					cell.setCellValue(s.getStatDate());
				}else{
					cell.setCellValue("("+s.getStatDateStart()+")-("+s.getStatDateEnd()+")");
				}

				i++;
			}
		}
		return wb;
	}

}