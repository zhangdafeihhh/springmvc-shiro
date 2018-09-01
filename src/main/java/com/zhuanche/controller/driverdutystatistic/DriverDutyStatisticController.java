package com.zhuanche.controller.driverdutystatistic;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.controller.DriverQueryController;
import com.zhuanche.dto.rentcar.DriverDutyStatisticDTO;
import com.zhuanche.entity.mdbcarmanage.DriverDailyReport;
import com.zhuanche.entity.rentcar.DriverDutyStatistic;
import com.zhuanche.entity.rentcar.DriverDutyStatisticParams;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import mapper.rentcar.ex.DriverDutyStatisticExMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
	
	/**
	 * 司机考勤查询
	 * @param cityId
	 * @param supplierId
	 * @param teamId
	 * @param groupIds
	 * @param name
	 * @param phone
	 * @param licensePlates
	 * @param startDate
	 * @param endDate
	 * @param startTime
	 * @param endTime
	 * @param page
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/driverDutyStatisticDailData")
	@ResponseBody
	public AjaxResponse driverDutyStatisticDailData(String cityId, String supplierId, String teamId, String groupIds, String name,String driverId,
		String  phone, String licensePlates, String startDate, String endDate, String startTime, String endTime, Integer page, Integer pageSize) {
		//初始化参数
		DriverDutyStatisticParams params = new DriverDutyStatisticParams(cityId, supplierId, teamId, groupIds, name,driverId,phone, licensePlates, startDate, endDate, startTime, endTime, page, pageSize);
		log.info("司机考勤记录列表数据:driverDutyStatisticDailData,参数"+ params.toString());
		int total = 0;
		//判断权限  如果司机id为空为查询列表页
		if (StringUtils.isEmpty(params.getDriverIds())){
			String driverList = null;
			//如果页面输入了小组id或者车队id
			if(StringUtils.isNotEmpty(params.getGroupIds()) ||  StringUtils.isNotEmpty(params.getTeamId())){
				//通过小组id查询司机id, 如果用户
				driverList = super.queryAuthorityDriverIdsByTeamAndGroup(params.getTeamId(), String.valueOf(params.getGroupIds()));
				//如果该小组下无司机，返回空
				if(StringUtils.isEmpty(driverList)){
					log.info("司机考勤3.0列表-有选择小组查询条件-该小组下没有司机groupId=="+params.getGroupIds());
					log.info("或者司机考勤3.0列表-有选择车队查询条件-该车队下没有司机teamId=="+params.getTeamId());
					PageDTO pageDTO = new PageDTO(page, pageSize, total, null);
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
		Page<DriverDailyReport> p = PageHelper.startPage(page, pageSize);
		List<DriverDutyStatistic> list = this.driverDutyStatisticExMapper.queryForListObject(params);
		total = (int) p.getTotal();
		//数据转换
		List<DriverDutyStatisticDTO> dtoList = selectSuppierNameAndCityName(list);
		PageDTO pageDTO = new PageDTO(page, pageSize, total, dtoList);
		return AjaxResponse.success(pageDTO);
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
		if(!"".equals(params.getSortname())&&params.getSortname()!=null){
			String sortName = pingSortName(params.getSortname());
			params.setSortname(sortName);
		}
		//获取当前 用户
		SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
		//当前用户权限
		String cities = currentLoginUser.getCityIds().toString();
		String suppliers = currentLoginUser.getSupplierIds().toString();
		String teamIds = currentLoginUser.getTeamIds().toString();
		if("".equals(params.getCities())||params.getCities()==null){
			params.setCities(cities.substring(1, cities.length()-1));
		}
		if("".equals(params.getSuppliers())||params.getSuppliers()==null){
			params.setSuppliers(suppliers.substring(1, suppliers.length()-1));
		}
		if("".equals(params.getTeamIds())||params.getTeamIds()==null){
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
			list = BeanUtil.copyList(rows, DriverDutyStatisticDTO.class);
			for(DriverDutyStatisticDTO driverDutyStatisticDTO : list){
				//查询城市名称和供应商名称
				Map<String, Object> result = super.querySupplierName(driverDutyStatisticDTO.getCityid(), driverDutyStatisticDTO.getSupplierid());
				driverDutyStatisticDTO.setCityName((String)result.get("cityName"));
				driverDutyStatisticDTO.setSupplierName((String)result.get("supplierName"));
			}
		}
		return list;
	}

//	@RequestMapping(value = "/driverDutyStatisticMonthData")
//	@ResponseBody
//	public Object driverDutyStatisticMonthData(DriverDutyStatistic DriverDutyStatistic, DriverTeamRelationEntity driverTeamRelationEntity) {
//		try {
//			logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"司机考勤记录月统计列表数据");
//		} catch (Exception e) {
//		}
//		logger.info("司机考勤记录列表数据:driverDutyStatisticMonthData,参数"+DriverDutyStatistic.toString());
//		List<DriverDutyStatistic> rows = new ArrayList<DriverDutyStatistic>();
//		int total = 0;
//		String driverList = "";
//		if(DriverDutyStatistic.getGroupIds()!=null&&!"".equals(DriverDutyStatistic.getGroupIds())
//				||DriverDutyStatistic.getTeamId()!=null&&!"".equals(DriverDutyStatistic.getTeamId())){
//			driverList = super.queryAuthorityDriverIdsByTeamAndGroup(DriverDutyStatistic.getTeamId(), String.valueOf(DriverDutyStatistic.getGroupIds()));
//			if(driverList==null || "".equals(driverList)){
//				logger.info("司机考勤3.0列表-有选择小组查询条件-该小组下没有司机groupId=="+DriverDutyStatistic.getGroupIds());
//				logger.info("或者司机考勤3.0列表-有选择车队查询条件-该车队下没有司机teamId=="+DriverDutyStatistic.getTeamId());
//				return this.gridJsonFormate(rows, total);
//			}
//		}
//		DriverDutyStatistic.setDriverIds(driverList);
//
//		String suppliers = ToolUtils.getSecurityUser().getSuppliers();
//		String cities = ToolUtils.getSecurityUser().getCities();
//		DriverDutyStatistic.setSuppliers(suppliers);
//		DriverDutyStatistic.setCities(cities);
//
//		if(DriverDutyStatistic.getStartTime()!=null && DriverDutyStatistic.getStartTime()!=""){
//			String time = DriverDutyStatistic.getStartTime();
//			int value = setDriverDutyStatisticValue(time);
//			DriverDutyStatistic.setValue(value);
//			DriverDutyStatistic.setTable("statistic_duty_"+time.substring(0,7).replaceAll("-", "_"));
//		}
//		//查数量
//		total = this.driverDutyStatisticService.queryDriverMonthDutyListCountNew(DriverDutyStatistic);
//		if(total==0){
//			return this.gridJsonFormate(rows, total);
//		}
//		//查数据
//		rows = this.driverDutyStatisticService.queryDriverMonthDutyListNew(DriverDutyStatistic);
//		rows = selectSuppierNameAndCityName(rows);
//		return this.gridJsonFormate(rows, total);
//	}
	
//	@RequestMapping(value = "/driverDutyStatisticInfoData")
//	@ResponseBody
//	public Object driverDutyStatisticInfoData(DriverDutyStatistic DriverDutyStatistic,HttpServletRequest request) {
//		try {
//			logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"司机考勤记录列表按车牌号查询数据");
//		} catch (Exception e) {
//		}
//		logger.info("司机考勤记录列表按车牌号查询数据:driverDutyStatisticInfoData,参数"+DriverDutyStatistic.toString());
//		String licensePlates = request.getParameter("licensePlates");
//		DriverDutyStatistic.setLicensePlates(licensePlates);
//		List<DriverDutyStatistic> rows = driverDutyStatisticService.queryDriverDutyInfo(DriverDutyStatistic);
//		int count = this.driverDutyStatisticService.queryDriverDutyInfoCount(DriverDutyStatistic);
//		return this.gridJsonFormate(rows, count);
//	}
	
//	@RequestMapping(value = "/driverDutyStatisticHalfData")
//	@ResponseBody
//	public Object driverDutyStatisticHalfData(StatisticsDutyEntity params,HttpServletRequest request) {
//		try {
//			logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"司机考勤记录列表按司机id查询数据");
//		} catch (Exception e) {
//		}
//		logger.info("司机考勤记录列表按司机id查询数据:driverDutyStatisticHalfData,参数"+params.toString());
//		String driverId = request.getParameter("driverId");
//		params.setDriverId(Integer.parseInt(driverId));
//		String time = request.getParameter("time");
//		if(time !=null && !"".equals(time)){
//			int value=0;
//			try {
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//				Date dateBegin=sdf.parse("2017-09-14");
//				Date dateEnd=sdf.parse(time);
//				if(dateBegin.getTime()>dateEnd.getTime()){
//					value = 0;
//				}else{
//					value = 1;
//				}
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//			params.setValue(value);
//			params.setTable("statistic_duty_half_"+time.substring(0,10).replaceAll("-", "_"));
////			statisticsDutyEntity.setStartTime(time+" 00:00:00");
////			statisticsDutyEntity.setEndTime(time+" 59:59:59");
//		}
//		int count = this.statisticsDutyService.queryDriverDutyHalfByDriverIdCount(params);
//		if(count==0){
//			return this.gridJsonFormate(null, count);
//		}
//		List<StatisticsDutyEntity> rows = statisticsDutyService.queryDriverDutyHalfByDriverId(params);
//		return this.gridJsonFormate(rows, count);
//	}
	
//	/**
//	 *导出司机考勤操作
//	 * @param request
//	 * @param response
//	 * @return
//	 */
//	@RequestMapping("/exportDriverDutyStatistic")
//	public void exportDriverDuty(DriverDutyStatistic DriverDutyStatistic, HttpServletRequest request, HttpServletResponse response){
//		try {
//			logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"导出司机考勤");
//		} catch (Exception e) {
//		}
//		logger.info("导出司机考勤操作");
//		List<DriverDutyStatistic> rows = new ArrayList<DriverDutyStatistic>();
//		String driverList = "";
//		if(DriverDutyStatistic.getGroupIds()!=null&&!"".equals(DriverDutyStatistic.getGroupIds())
//				||DriverDutyStatistic.getTeamId()!=null&&!"".equals(DriverDutyStatistic.getTeamId())){
//			driverList = super.queryAuthorityDriverIdsByTeamAndGroup(DriverDutyStatistic.getTeamId(), String.valueOf(DriverDutyStatistic.getGroupIds()));
//			if(driverList==null || "".equals(driverList)){
//				logger.info("司机考勤3.0列表-有选择小组查询条件-该小组下没有司机groupId=="+DriverDutyStatistic.getGroupIds());
//				logger.info("或者司机考勤3.0列表-有选择车队查询条件-该车队下没有司机teamId=="+DriverDutyStatistic.getTeamId());
//			}
//		}
//		if((DriverDutyStatistic.getGroupIds()!=null&&!"".equals(DriverDutyStatistic.getGroupIds())
//				||DriverDutyStatistic.getTeamId()!=null&&!"".equals(DriverDutyStatistic.getTeamId()))
//				&&(driverList==null || "".equals(driverList))){
//		}else{
//			DriverDutyStatistic.setDriverIds(driverList);
//			if(DriverDutyStatistic.getStartTime()!=null && DriverDutyStatistic.getStartTime()!=""){
//				String time = DriverDutyStatistic.getStartTime();
//				int value = setDriverDutyStatisticValue(time);
//				DriverDutyStatistic.setValue(value);
//				DriverDutyStatistic.setTable("statistic_duty_"+time.substring(0,7).replaceAll("-", "_"));
//			}
//			rows = driverDutyStatisticService.queryList(DriverDutyStatistic);
//			rows = selectSuppierNameAndCityName(rows);
//		}
//		try {
//			@SuppressWarnings("deprecation")
//			Workbook wb = driverDutyStatisticService.exportExcelTongyong(rows,request.getRealPath("/")+File.separator+"template"+File.separator+"driverDuty2_info.xlsx");
////			Workbook wb = driverDutyStatisticService.exportExcel(DriverDutyStatistic,request.getRealPath("/")+File.separator+"template"+File.separator+"driverDuty2_info.xlsx");
//			super.exportExcelFromTemplet(request, response, wb, new String("司机考勤2列表".getBytes("utf-8"), "iso8859-1"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	/**
//	  *
//	 *导出司机考勤操作月报
//	 * @param request
//	 * @param response
//	 * @return
//	 */
//	@AuthPassport
//	@RequestMapping("/exportDriverMonthDutyStatistic")
//	public void exportDriverMonthDuty(DriverDutyStatistic DriverDutyStatistic, DriverTeamRelationEntity driverTeamRelationEntity, HttpServletRequest request, HttpServletResponse response){
//		try {
//			logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"导出司机考勤月报");
//		} catch (Exception e) {
//		}
//		logger.info("导出司机考勤操作");
//		List<DriverDutyStatistic> rows = new ArrayList<DriverDutyStatistic>();
//		String driverList = "";
//		if(DriverDutyStatistic.getGroupIds()!=null&&!"".equals(DriverDutyStatistic.getGroupIds())
//				||DriverDutyStatistic.getTeamId()!=null&&!"".equals(DriverDutyStatistic.getTeamId())){
//			driverList = super.queryAuthorityDriverIdsByTeamAndGroup(DriverDutyStatistic.getTeamId(), String.valueOf(DriverDutyStatistic.getGroupIds()));
//			if(driverList==null || "".equals(driverList)){
//				logger.info("司机考勤3.0列表-有选择小组查询条件-该小组下没有司机groupId=="+DriverDutyStatistic.getGroupIds());
//				logger.info("或者司机考勤3.0列表-有选择车队查询条件-该车队下没有司机teamId=="+DriverDutyStatistic.getTeamId());
//			}
//		}
//		if((DriverDutyStatistic.getGroupIds()!=null&&!"".equals(DriverDutyStatistic.getGroupIds())
//				||DriverDutyStatistic.getTeamId()!=null&&!"".equals(DriverDutyStatistic.getTeamId()))
//				&&(driverList==null || "".equals(driverList))){
//		}else{
//			DriverDutyStatistic.setDriverIds(driverList);
//			if(DriverDutyStatistic.getStartTime()!=null && DriverDutyStatistic.getStartTime()!=""){
//				String time = DriverDutyStatistic.getStartTime();
//				int value= setDriverDutyStatisticValue(time);
//				DriverDutyStatistic.setValue(value);
//				DriverDutyStatistic.setTable("statistic_duty_"+time.substring(0,7).replaceAll("-", "_"));
//			}
//			rows = driverDutyStatisticService.queryMonthDutyList(DriverDutyStatistic);
//			rows = selectSuppierNameAndCityName(rows);
//		}
//		try {
//			@SuppressWarnings("deprecation")
//			Workbook wb = driverDutyStatisticService.exportExcelTongyong(rows,request.getRealPath("/")+File.separator+"template"+File.separator+"driverDuty2_info.xlsx");
////			Workbook wb = driverDutyStatisticService.exportExcel(DriverDutyStatistic,request.getRealPath("/")+File.separator+"template"+File.separator+"driverDuty2_info.xlsx");
//			super.exportExcelFromTemplet(request, response, wb, new String("司机考勤2列表".getBytes("utf-8"), "iso8859-1"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
//	public List<DriverDutyStatistic> selectSuppierNameAndCityName(List<DriverDutyStatistic> rows){
//		if(rows!=null&&rows.size()>0){
//			for(DriverDutyStatistic ddre : rows){
//				Map<String, Object> result = new HashMap<String, Object>();
//				result = super.querySupplierName(ddre.getCityId(), ddre.getSupplierId());
//				ddre.setCityName((String)result.get("cityName"));
////				ddre.setSupplierName((String)result.get("supplierName"));
//			}
//		}
//		return rows;
//	}

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
//
//	@AuthPassport
//	@RequestMapping(value = "/toDelete")
//	public String toDelete(){
//		return "driverDutyStatistic/delete";
//	}
//
//	@AuthPassport
//	@RequestMapping(value = "/delete")
//	@ResponseBody
//	public Object delete(String ids, String dateStr){
//		try {
//			logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"删除考勤ids="+ids+",dateStr="+ dateStr);
//		} catch (Exception e) {
//		}
//		logger.info("删除考勤ids="+ids+",dateStr="+ dateStr);
//		Map<String,Object> result = new HashMap<String,Object>();
//		if(StringUtils.isEmpty(ids)||StringUtils.isEmpty(dateStr)){
//			result.put("result", 0);
//			result.put("msg", "请求参数不可为空");
//			return result;
//		}
//		String table = "statistic_duty_"+dateStr.substring(0,7).replaceAll("-", "_");
//
////		Map<String,Object> map = new HashMap<String,Object>();
////		map.put("ids", ids);
////		map.put("table", table);
//		logger.info("删除考勤ids="+ids+",table="+ table);
//		DriverDutyStatistic params = new DriverDutyStatistic();
//		params.setDriverIds(ids);//借用一下driverIds代表的意义ids
//		params.setTable(table);
//		this.driverDutyStatisticService.delete(params);
//		result.put("result", 1);
//		return result;
//	}
}
