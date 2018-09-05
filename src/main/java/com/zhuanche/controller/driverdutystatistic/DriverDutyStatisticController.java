package com.zhuanche.controller.driverdutystatistic;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.controller.DriverQueryController;
import com.zhuanche.dto.rentcar.DriverDutyStatisticDTO;
import com.zhuanche.entity.mdblog.StatisticDutyHalf;
import com.zhuanche.entity.mdblog.StatisticDutyHalfParams;
import com.zhuanche.entity.mdbcarmanage.DriverDutyStatistic;
import com.zhuanche.entity.mdbcarmanage.DriverDutyStatisticParams;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import mapper.mdblog.ex.StatisticDutyHalfExMapper;
import mapper.mdbcarmanage.ex.DriverDutyStatisticExMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 司机考勤查询
 * @Auther  wanghongdong
 */
@Controller
@RequestMapping(value = "/driverDutyStatistic")
public class DriverDutyStatisticController extends DriverQueryController{

	private static Log log =  LogFactory.getLog(DriverDutyStatisticController.class);

	@Autowired
	private DriverDutyStatisticExMapper driverDutyStatisticExMapper;
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
	public AjaxResponse driverDutyStatisticDailData(@Verify(param = "cityId",rule = "required") String cityId, @Verify(param = "supplierId",rule = "required")String supplierId, String teamId,
			String groupIds, String name, String driverId, String  phone, String licensePlates,
			@Verify(param = "startTime",rule = "required") String startTime, String endTime, String sortName, String sortOrder, Integer page, Integer pageSize, Integer reportType) {
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
		Page<DriverDutyStatistic> p = PageHelper.startPage(params.getPage(), params.getPageSize());
		List<DriverDutyStatistic> list = null;
		try{
			if (reportType.equals(0)){
				list = this.driverDutyStatisticExMapper.queryForListObject(params);
			}else{
				list = this.driverDutyStatisticExMapper.queryDriverMonthDutyList(params);
			}
			total = (int) p.getTotal();
		}finally {
			PageHelper.clearPage();
		}
		//数据转换
		List<DriverDutyStatisticDTO> dtoList = selectSuppierNameAndCityName(list);
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
	public AjaxResponse driverDutyStatisticHalfData(@Verify(param = "driverId",rule = "required") String driverId,@Verify(param = "time",rule = "required") String time, Integer page, Integer pageSize) {

		StatisticDutyHalfParams params = new StatisticDutyHalfParams(driverId, time, page, pageSize);
		log.info("司机考勤记录列表按司机id查询数据:driverDutyStatisticHalfData,参数"+params.toString());
		if(time !=null && !"".equals(time)){
			int value=0;
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date dateBegin=sdf.parse("2017-09-14");
				Date dateEnd=sdf.parse(time);
				if(dateBegin.getTime()>dateEnd.getTime()){
					value = 0;
				}else{
					value = 1;
				}
			} catch (ParseException e) {
				e.printStackTrace();
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
	public AjaxResponse exportDriverDuty(@Verify(param = "cityId",rule = "required") String cityId, @Verify(param = "supplierId",rule = "required") String supplierId, String teamId, String groupIds, String name,
		 String  phone, String licensePlates,@Verify(param = "startTime",rule = "required")  String startTime,
		 String endTime, Integer reportType, HttpServletRequest request, HttpServletResponse response){

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
		if ((StringUtils.isEmpty(params.getGroupIds()) &&  StringUtils.isEmpty(params.getTeamId()) && StringUtils.isEmpty(driverList)) || StringUtils.isNotEmpty(driverList)){
			params.setDriverIds(driverList);
			params = chuliDriverDutyStatisticParams(params);
			if(StringUtils.isNotEmpty(params.getStartTime())){
				String time = params.getStartTime();
				int value = setDriverDutyStatisticValue(time);
				params.setValue(value);
				params.setTable("statistic_duty_"+time.substring(0,7).replaceAll("-", "_"));
			}
			List<DriverDutyStatistic> list = null;
			if (reportType.equals(0)){
				list = this.driverDutyStatisticExMapper.queryForListObject(params);
			}else{
				list = this.driverDutyStatisticExMapper.queryDriverMonthDutyList(params);
			}
			//设置供应商名称和城市名称
			rows = selectSuppierNameAndCityName(list);
		}
		try {
			Workbook wb = null;
			if (reportType==0){
				wb = this.exportExcelTongyong(rows,request.getRealPath("/")+ File.separator+"template"+File.separator+"driverDuty2_info.xlsx");
			}else{
				wb = this.exportMonthExcelTongyong(rows,request.getRealPath("/")+ File.separator+"template"+File.separator+"driverDuty2_info_month.xlsx");
			}
			super.exportExcelFromTemplet(request, response, wb, new String("司机考勤2列表".getBytes("utf-8"), "iso8859-1"));
			return AjaxResponse.success("文件导出成功");
		} catch (Exception e) {
			log.error("导出司机考勤操作失败",e);
			return AjaxResponse.fail(RestErrorCode.FILE_EXCEL_REPORT_FAIL);
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
	 * <p>Title: selectSuppierNameAndCityNameDays</p>
	 * <p>Description: 转换</p>
	 * @param rows
	 * @return
	 * return: List<DriverDailyReportDTO>
	 */
	public List<DriverDutyStatisticDTO> selectSuppierNameAndCityName(List<DriverDutyStatistic> rows){
		List<DriverDutyStatisticDTO> list = null;
		//不为空进行转换并查询城市名称和供应商名称
		if(rows!=null&&rows.size()>0){
			for(DriverDutyStatistic driverDutyStatistic : rows){
				//查询城市名称和供应商名称
				Map<String, Object> result = super.querySupplierName(driverDutyStatistic.getCityid(), driverDutyStatistic.getSupplierid());
				driverDutyStatistic.setCityName((String)result.get("cityName"));
				driverDutyStatistic.setSupplierName((String)result.get("supplierName"));
			}
			list = BeanUtil.copyList(rows, DriverDutyStatisticDTO.class);
		}
		return list;
	}

	/**
	 *  如果输入时间小于2017-09，则返回0，否则返回1
	 * @param time
	 * @return
	 */
	public int setDriverDutyStatisticValue(String time){
		int value=0;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
			Date dateBegin = sdf.parse("2017-09");
			Date dateEnd = sdf.parse(time);
			if(dateBegin.getTime() > dateEnd.getTime()){
				value = 0;
			}else{
				value = 1;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return value;
	}

	/** 整理excel
	 * @param list
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public Workbook exportExcelTongyong(List<DriverDutyStatisticDTO> list, String path) throws Exception{
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
				cell.setCellValue(s.getTime());

				cell = row.createCell(2);
				cell.setCellValue(s.getPhone());

				cell = row.createCell(3);
				cell.setCellValue(s.getLicenseplates());

				cell = row.createCell(4);
				cell.setCellValue(s.getDutytime());

				cell = row.createCell(5);
				cell.setCellValue(s.getForcedtime());

				cell = row.createCell(6);
				cell.setCellValue(s.getOvertime());

				cell = row.createCell(7);
				cell.setCellValue(s.getCityName());

				cell = row.createCell(8);
				cell.setCellValue(s.getForcedtime1());

				cell = row.createCell(9);
				cell.setCellValue(s.getForcedtime2());

				cell = row.createCell(10);
				cell.setCellValue(s.getForcedtime3());

				cell = row.createCell(11);
				cell.setCellValue(s.getForcedtime4());

				i++;
			}
		}
		return wb;
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
