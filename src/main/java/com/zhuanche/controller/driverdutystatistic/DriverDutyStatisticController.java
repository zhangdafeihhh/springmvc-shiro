package com.zhuanche.controller.driverdutystatistic;

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
import com.zhuanche.dto.rentcar.DriverDutyStatisticDTO;
import com.zhuanche.entity.mdbcarmanage.DriverDutyStatistic;
import com.zhuanche.entity.mdbcarmanage.DriverDutyStatisticParams;
import com.zhuanche.entity.mdblog.StatisticDutyHalf;
import com.zhuanche.entity.mdblog.StatisticDutyHalfParams;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.serv.DriverDutyStatisticService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.excel.CsvUtils;
import mapper.mdbcarmanage.ex.DriverDutyStatisticExMapper;
import mapper.mdblog.ex.StatisticDutyHalfExMapper;
import mapper.rentcar.ex.CarBizCityExMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 司机考勤查询
 * @Auther  wanghongdong
 */
@Controller
@RequestMapping(value = "/driverDutyStatistic")
public class DriverDutyStatisticController extends DriverQueryController{

	private static Logger log =  LoggerFactory.getLogger(DriverDutyStatisticController.class);

	@Autowired
	private DriverDutyStatisticService driverDutyStatisticService;

	@Autowired
	private StatisticDutyHalfExMapper statisticDutyHalfExMapper;


	/**
	 * 司机考勤查询
	 * @param cityId 城市ID
	 * @param supplierId 供应商ID
	 * @param teamId 车队ID
	 * @param groupIds 小组ID
	 * @param name 司机名称
	 * @param phone 司机电话
	 * @param licensePlates 车牌号
	 * @param startTime  开始时间，格式YYYY-MM-DD
	 * @param endTime 结束时间，格式YYYY-MM-DD
	 * @param sortName 排序字段名称
	 * @param sortOrder 正序（ASC）、倒序（DESC）
	 * @param page 当前页
	 * @param pageSize 页面展示数量
	 * @param reportType 查询类型，0：日统计，1：月统计
	 * @return
	 */
	@RequestMapping(value = "/driverDutyStatisticDailData")
	@ResponseBody
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
	})
	public AjaxResponse driverDutyStatisticDailData(@Verify(param = "cityId",rule = "required") String cityId, @Verify(param = "supplierId",rule = "required")String supplierId, String teamId,
			String groupIds, String name, String driverId, String  phone, String licensePlates,
			@Verify(param = "startTime",rule = "required") String startTime, String endTime, String sortName, String sortOrder, Integer page, Integer pageSize, Integer reportType) throws ParseException {
		//默认日统计
		reportType = reportType == null ? 0 : reportType;
		//如果是日统计，开始时间和结束时间不能为空并且开始时间和结束时间在一个月内
		if (reportType.equals(0)){
			if(StringUtils.isEmpty(endTime)){
				return AjaxResponse.fail(RestErrorCode.ENDTIME_IS_NULL);
			}
			if (startTime.compareTo(endTime) > 0 ){
				return AjaxResponse.fail(RestErrorCode.STARTTIME_GREATE_ENDTIME);
			}
			if(!startTime.substring(0,7).equals(endTime.substring(0,7))){
				return AjaxResponse.fail(RestErrorCode.ONLY_QUERY_ONE_MONTH);
			}
		}

		//初始化参数
		DriverDutyStatisticParams params = new DriverDutyStatisticParams(cityId, supplierId, teamId, groupIds, name,driverId,phone, licensePlates, startTime, endTime, sortName, sortOrder, page, pageSize);
		log.info("司机考勤记录列表数据:driverDutyStatisticDailData,参数"+ params.toString());
		int total = 0;
		//判断权限  如果司机id为空为查询列表页
		if (StringUtils.isEmpty(params.getDriverIds())){
			String driverList = "";
			//如果页面输入了小组id或者车队id
			if(StringUtils.isNotEmpty(params.getGroupIds()) ||  StringUtils.isNotEmpty(params.getTeamId())){
				//通过小组id查询司机id, 如果用户
				driverList = super.queryAuthorityDriverIdsByTeamAndGroup(params.getTeamId(), String.valueOf(params.getGroupIds()));
				//如果该小组下无司机，返回空
				if(StringUtils.isEmpty(driverList)){
					log.info("司机考勤3.0列表-有选择小组查询条件-该小组下没有司机groupId=="+params.getGroupIds());
					log.info("或者司机考勤3.0列表-有选择车队查询条件-该车队下没有司机teamId=="+params.getTeamId());
					PageDTO pageDTO = new PageDTO(params.getPage(), params.getPageSize(), total, null);
					return AjaxResponse.success(pageDTO);
				}
			}
			params.setDriverIds(driverList);
		}
		//如果输入开始时间重置查询表名称
		if(StringUtils.isNotEmpty(params.getStartTime())){
			String time = params.getStartTime();
			int value = setDriverDutyStatisticValue(time);
			params.setValue(value);
			params.setTable("statistic_duty_"+time.substring(0,7).replaceAll("-", "_"));
		}
		//处理参数
		params = chuliDriverDutyStatisticParams(params);
		//开始查询

		List<DriverDutyStatistic> list = null;
		PageInfo<DriverDutyStatistic> pageInfo = null;

		if (reportType.equals(0)){
			pageInfo =  driverDutyStatisticService.queryDriverDayDutyList(params);
			if(pageInfo != null){
				list = pageInfo.getList();
			}
		}else{
			pageInfo =  driverDutyStatisticService.queryDriverMonthDutyList(params);
			if(pageInfo != null){
				list = pageInfo.getList();
			}
		}
		total = (int) pageInfo.getTotal();

		//数据转换
		List<DriverDutyStatisticDTO> dtoList = driverDutyStatisticService.selectSuppierNameAndCityName(list);
		PageDTO pageDTO = new PageDTO(params.getPage(), params.getPageSize(), total, dtoList);
		return AjaxResponse.success(pageDTO);
	}

	/**
	 * 司机个人考勤列表  司机考勤记录列表按司机id查询数据
	 * @param driverId 司机id
	 * @param time 时间
	 * @param page 当前页
	 * @param pageSize 列表页
	 * @return AjaxResponse
	 */
	@RequestMapping(value = "/driverDutyStatisticHalfData")
	@ResponseBody
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
	})
	public AjaxResponse driverDutyStatisticHalfData(@Verify(param = "driverId",rule = "required") String driverId,@Verify(param = "time",rule = "required") String time, Integer page, Integer pageSize) throws ParseException {

		StatisticDutyHalfParams params = new StatisticDutyHalfParams(driverId, time, page, pageSize);
		log.info("司机考勤记录列表按司机id查询数据:driverDutyStatisticHalfData,参数"+params.toString());
		if(time !=null && !"".equals(time)){
			int value=0;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date dateBegin=sdf.parse("2017-09-14");
			Date dateEnd=sdf.parse(time);
			if(dateBegin.getTime()>dateEnd.getTime()){
				value = 0;
			}else{
				value = 1;
			}
			params.setValue(value);
			params.setTable("statistic_duty_half_"+time.substring(0,10).replaceAll("-", "_"));
		}
		//开始查询
		Page<StatisticDutyHalf> p = PageHelper.startPage(params.getPage(), params.getPageSize());
		List<StatisticDutyHalf> list = this.statisticDutyHalfExMapper.queryDriverDutyHalfByDriverId(params);
		int total = (int) p.getTotal();
		PageDTO pageDTO = new PageDTO(params.getPage(), params.getPageSize(), total, list);
		return AjaxResponse.success(pageDTO);
	}

	/**
	 * 导出司机考勤操作
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/exportDriverDutyStatistic")
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
	})
	public AjaxResponse exportDriverDuty(@Verify(param = "cityId",rule = "required") String cityId, @Verify(param = "supplierId",rule = "required") String supplierId, String teamId, String groupIds, String name,
		 String  phone, String licensePlates,@Verify(param = "startTime",rule = "required")  String startTime,
		 String endTime, Integer reportType, HttpServletRequest request, HttpServletResponse response) throws ParseException {

		//默认日统计
		reportType = reportType == null ? 0 : reportType;
		//如果是日统计，开始时间和结束时间不能为空并且开始时间和结束时间在一个月内
		if (reportType.equals(0)){
			if(StringUtils.isEmpty(endTime)){
				return AjaxResponse.fail(RestErrorCode.ENDTIME_IS_NULL);
			}
			if (startTime.compareTo(endTime) > 0 ){
				return AjaxResponse.fail(RestErrorCode.STARTTIME_GREATE_ENDTIME);
			}
			if(!startTime.substring(0,7).equals(endTime.substring(0,7))){
				return AjaxResponse.fail(RestErrorCode.ONLY_QUERY_ONE_MONTH);
			}
		}
		//初始化参数
		DriverDutyStatisticParams params = new DriverDutyStatisticParams(cityId,supplierId,teamId,groupIds,name,
				null,phone,licensePlates,startTime,endTime,null, null,null,null);
		log.info("导出司机考勤操作，入参：" + params.toString());
		String driverList = "";
		List<DriverDutyStatisticDTO> rows = new ArrayList<>();
		//如果页面输入了小组id或者车队id
		if(StringUtils.isNotEmpty(params.getGroupIds()) ||  StringUtils.isNotEmpty(params.getTeamId())){
			//通过小组id查询司机id, 如果用户
			driverList = super.queryAuthorityDriverIdsByTeamAndGroup(params.getTeamId(), String.valueOf(params.getGroupIds()));
			//如果该小组下无司机，返回空
			if(StringUtils.isEmpty(driverList)){
				log.info("司机考勤3.0列表-有选择小组查询条件-该小组下没有司机groupId=="+params.getGroupIds());
				log.info("或者司机考勤3.0列表-有选择车队查询条件-该车队下没有司机teamId=="+params.getTeamId());
			}
		}
		params.setPageSize(10000);
		if ((StringUtils.isEmpty(params.getGroupIds()) &&  StringUtils.isEmpty(params.getTeamId()) && StringUtils.isEmpty(driverList)) || StringUtils.isNotEmpty(driverList)){
			params.setDriverIds(driverList);
			params = chuliDriverDutyStatisticParams(params);
			if(StringUtils.isNotEmpty(params.getStartTime())){
				String time = params.getStartTime();
				int value = setDriverDutyStatisticValue(time);
				params.setValue(value);
				params.setTable("statistic_duty_"+time.substring(0,7).replaceAll("-", "_"));
			}
			List<DriverDutyStatistic> list = new ArrayList<>();
			PageInfo<DriverDutyStatistic> pageInfo = null;
			long start = System.currentTimeMillis();
			if (reportType.equals(0)){
				pageInfo =  driverDutyStatisticService.queryDriverDayDutyList(params);
				if(pageInfo != null){
					list.addAll( pageInfo.getList());

					for(int pageno = 2 ;pageno < pageInfo.getPages();pageno ++ ){
						params.setPage(pageno);
						pageInfo =  driverDutyStatisticService.queryDriverDayDutyList(params);
						list.addAll( pageInfo.getList());
					}
				}
			}else{
				pageInfo =  driverDutyStatisticService.queryDriverMonthDutyList(params);
				if(pageInfo != null){
					list.addAll( pageInfo.getList());
					for(int pageno = 2 ;pageno < pageInfo.getPages();pageno ++ ){
						params.setPage(pageno);
						pageInfo =  driverDutyStatisticService.queryDriverMonthDutyList(params);
						list.addAll( pageInfo.getList());
					}
				}
			}
			//设置供应商名称和城市名称
			rows = driverDutyStatisticService.selectSuppierNameAndCityName(list);
		}

		try {
			List<String> headerList = new ArrayList<>();
			headerList.add("司机姓名,手机号,车牌号,班制之内上班上线时_有效,强制上班内上班上线时长_有效,加班时长,班制内上班上线时长,强制上班内上班上线时长,城市,早高峰在线时长,晚高峰在线时长,其他时段1在线时长,其他时段2在线时长"
			);

			if(rows == null){
				rows = new ArrayList<>();
			}
			List<String> csvDataList  = new ArrayList<>(rows.size());
			dataTrans(rows,csvDataList);

			String fileName = "司机考勤报告"+ com.zhuanche.util.dateUtil.DateUtil.dateFormat(new Date(), com.zhuanche.util.dateUtil.DateUtil.intTimestampPattern)+".csv";
			String agent = request.getHeader("User-Agent").toUpperCase(); //获得浏览器信息并转换为大写
			if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO")>0 && agent.indexOf("RV:11")>0)) {  //IE浏览器和Edge浏览器
				fileName = URLEncoder.encode(fileName, "UTF-8");
			} else {  //其他浏览器
				fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
			}
			CsvUtils.exportCsv(response,csvDataList,headerList,fileName);

			return AjaxResponse.success("导出司机考勤操作成功");
		} catch (Exception e) {
			if(rows != null){
				rows.clear();
			}
			log.error("导出司机考勤操作异常！",e);
			return AjaxResponse.fail(RestErrorCode.FILE_EXCEL_REPORT_FAIL);
		}
	}
	private void dataTrans(List<DriverDutyStatisticDTO> result,List<String>  csvDataList){
		if(null == result){
			return;
		}
		for(DriverDutyStatisticDTO s:result){
			StringBuffer stringBuffer = new StringBuffer();

			stringBuffer.append(s.getName());
			stringBuffer.append(",");

			stringBuffer.append(s.getPhone());
			stringBuffer.append(",");

			stringBuffer.append(s.getLicenseplates());
			stringBuffer.append(",");

			stringBuffer.append(s.getDutytime());
			stringBuffer.append(",");

			stringBuffer.append(s.getForcedtime());
			stringBuffer.append(",");

			stringBuffer.append(s.getOvertime());
			stringBuffer.append(",");

			stringBuffer.append(s.getDutyTimeAll());
			stringBuffer.append(",");

			stringBuffer.append(s.getForcedTimeAll());
			stringBuffer.append(",");

			stringBuffer.append(s.getCityName());
			stringBuffer.append(",");

			stringBuffer.append(s.getForcedtime1());
			stringBuffer.append(",");

			stringBuffer.append(s.getForcedtime2());
			stringBuffer.append(",");

			stringBuffer.append(s.getForcedtime3());
			stringBuffer.append(",");

			stringBuffer.append(s.getForcedtime4());

			csvDataList.add(stringBuffer.toString());
		}

	}

	/**
	 * <p>Title: chuliDriverDutyStatisticParams</p>
	 * <p>Description: 根据 参数重新整理 入参条件 ,如果页面没有传入参数，则使用该用户绑定的权限 </p>
	 * @param params
	 * @return
	 * return: DriverDutyStatisticParams
	 */
	public DriverDutyStatisticParams chuliDriverDutyStatisticParams(DriverDutyStatisticParams params){
		//整理排序字段
		if(!"".equals(params.getSortName())&&params.getSortName()!=null){
			String sortName = pingSortName(params.getSortName());
			params.setSortName(sortName);
		}
		//获取当前 用户
		SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
		//当前用户权限
		String cities = currentLoginUser.getCityIds().toString();
		String suppliers = currentLoginUser.getSupplierIds().toString();
		String teamIds = currentLoginUser.getTeamIds().toString();
		if("".equals(params.getCityId())||params.getCityId()==null){
			params.setCities(cities.substring(1, cities.length()-1));
		}
		if("".equals(params.getSupplierId())||params.getSupplierId()==null){
			params.setSuppliers(suppliers.substring(1, suppliers.length()-1));
		}
		if("".equals(params.getTeamId())||params.getTeamId()==null){
			params.setTeamIds(teamIds.substring(1, teamIds.length()-1));
		}
		return params;
	}



	/**
	 *  如果输入时间小于2017-09，则返回0，否则返回1
	 * @param time
	 * @return
	 */
	public int setDriverDutyStatisticValue(String time) throws ParseException {
		int value=0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		Date dateBegin = sdf.parse("2017-09");
		Date dateEnd = sdf.parse(time);
		if(dateBegin.getTime() > dateEnd.getTime()){
			value = 0;
		}else{
			value = 1;
		}
		return value;
	}

	public Workbook exportMonthExcelTongyong(List<DriverDutyStatisticDTO> list, String path) throws Exception{
		FileInputStream io = new FileInputStream(path);
		Workbook wb = new XSSFWorkbook(io);
		if(list != null && list.size()>0){
			Sheet sheet = wb.getSheetAt(0);
			Cell cell = null;
			int i=0;
			for(DriverDutyStatisticDTO s:list){
				Row row = sheet.createRow(i + 1);
				cell = row.createCell(0);
				cell.setCellValue(s.getName());

				cell = row.createCell(1);
				cell.setCellValue(s.getPhone());

				cell = row.createCell(2);
				cell.setCellValue(s.getLicenseplates());

				cell = row.createCell(3);
				cell.setCellValue(s.getDutytime());

				cell = row.createCell(4);
				cell.setCellValue(s.getForcedtime());

				cell = row.createCell(5);
				cell.setCellValue(s.getOvertime());

				cell = row.createCell(6);
				cell.setCellValue(s.getDutyTimeAll());

				cell = row.createCell(7);
				cell.setCellValue(s.getForcedTimeAll());

				cell = row.createCell(8);
				cell.setCellValue(s.getCityName());

				cell = row.createCell(9);
				cell.setCellValue(s.getForcedtime1());

				cell = row.createCell(10);
				cell.setCellValue(s.getForcedtime2());

				cell = row.createCell(11);
				cell.setCellValue(s.getForcedtime3());

				cell = row.createCell(12);
				cell.setCellValue(s.getForcedtime4());

				i++;
			}
		}
		return wb;
	}
}
