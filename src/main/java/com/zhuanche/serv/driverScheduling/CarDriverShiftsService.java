package com.zhuanche.serv.driverScheduling;


import com.alibaba.fastjson.JSON;
import com.zhuanche.common.dutyEnum.EnumDriverDutyPeakTimes;
import com.zhuanche.dto.CarDriverInfoDTO;
import com.zhuanche.dto.driverDuty.CarDriverDurationDTO;
import com.zhuanche.dto.driverDuty.CarDriverMustDutyDTO;
import com.zhuanche.entity.mdbcarmanage.CarRelateGroup;
import com.zhuanche.entity.mdbcarmanage.CarRelateTeam;
import com.zhuanche.request.DriverTeamRequest;
import com.zhuanche.request.DutyParamRequest;
import com.zhuanche.request.TeamGroupRequest;
import com.zhuanche.serv.common.CitySupplierTeamCommonService;
import com.zhuanche.util.Check;
import mapper.mdbcarmanage.ex.CarDriverMustDutyExMapper;
import mapper.mdbcarmanage.ex.CarDutyDurationExMapper;
import mapper.mdbcarmanage.ex.CarRelateGroupExMapper;
import mapper.mdbcarmanage.ex.CarRelateTeamExMapper;
import mapper.rentcar.ex.CarBizDriverInfoExMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @description: 班制排班
 *
 * <PRE>
 * <BR>	修改记录
 * <BR>-----------------------------------------------
 * <BR>	修改日期			修改人			修改内容
 * </PRE>
 *
 * @author lunan
 * @version 1.0
 * @since 1.0
 * @create: 2018-09-03 12:54
 *
 */
@Service
public class CarDriverShiftsService {

	private static final Logger logger = LoggerFactory.getLogger(CarDriverShiftsService.class);

	@Autowired
	private CarDriverMustDutyExMapper carDriverMustDutyExMapper;

	@Autowired
	private CarDutyDurationExMapper carDutyDurationExMapper;

	@Autowired
	private CarRelateTeamExMapper carRelateTeamExMapper;

	@Autowired
	private CitySupplierTeamCommonService citySupplierTeamCommonService;

	@Autowired
	private CarBizDriverInfoExMapper carBizDriverInfoExMapper;

	@Autowired
	private CarRelateGroupExMapper carRelateGroupExMapper;

	@Autowired
	private AsyncDutyService asyncDutyService;

	private static final ExecutorService es = Executors.newCachedThreadPool();

	/** 
	* @Desc: 查询司机强制排班时间段 
	* @param:
	* @return:  
	* @Author: lunan
	* @Date: 2018/9/3 
	*/ 
	public List<CarDriverMustDutyDTO> queryMustListByField(Integer teamId){
		logger.info("查询司机强制排班时间段入参:{}"+"--teamId:"+teamId);
		if(Check.NuNObj(teamId)){
			return null;
		}
		try{
			DutyParamRequest dutyParamRequest = new DutyParamRequest();
			dutyParamRequest.setTeamId(teamId);
			List<CarDriverMustDutyDTO> list = carDriverMustDutyExMapper.selectDriverMustDutyListByField(dutyParamRequest);
			return list;
		}catch (Exception e){
			logger.error("查询司机强制排班时间段异常:{}",e);
			return null;
		}
	}

	/** 
	* @Desc: 获取排班时长时间段 
	* @param:
	* @return:  
	* @Author: lunan
	* @Date: 2018/9/3 
	*/ 
	public List<CarDriverDurationDTO> queryDurationListByField(Integer cityId,Integer supplierId,Integer teamId){
		logger.info("获取排班时长时间段入参:{}"+"cityId:"+cityId+"--supplierId:"+supplierId+"--teamId:"+teamId);
		if(Check.NuNObjs(cityId,supplierId,teamId)){
			return null;
		}
		try{
			DutyParamRequest dutyParamRequest = new DutyParamRequest();
			dutyParamRequest.setCityId(cityId);
			dutyParamRequest.setSupplierId(supplierId);
			dutyParamRequest.setTeamId(teamId);
			List<CarDriverDurationDTO> list = carDutyDurationExMapper.queryDutyDurationListByField(dutyParamRequest);
			return list;
		}catch (Exception e){
			logger.error("查询排班时长时间段异常:{}",e);
			return null;
		}

	}


	/** 
	* @Desc: 获取班制设置司机列表 
	* @param:
	* @return:  
	* @Author: lunan
	* @Date: 2018/9/3 
	*/ 
	public List<CarDriverInfoDTO> queryDriverTeamReList(TeamGroupRequest teamGroupRequest){
		logger.info("获取班制设置司机列表入参:{}"+ JSON.toJSONString(teamGroupRequest));
		if(Check.NuNObj(teamGroupRequest) || Check.NuNObj(teamGroupRequest.getTeamId())){
			return null;
		}
		try{
			List<CarRelateTeam> carRelateTeams = carRelateTeamExMapper.queryDriverTeamRelationList(teamGroupRequest);
			if(Check.NuNCollection(carRelateTeams)){
				return null;
			}
			Set<String> driverids = citySupplierTeamCommonService.dealDriverids(carRelateTeams, CarRelateTeam.class,"driverId");
			DriverTeamRequest driverTeamRequest = new DriverTeamRequest();
			driverTeamRequest.setDriverIds(driverids);
			driverTeamRequest.setStatus(1);
			List<CarDriverInfoDTO> driverInfoList = carBizDriverInfoExMapper.selectDriverList(driverTeamRequest);
			if(Check.NuNCollection(driverInfoList)){
				return null;
			}
			for (CarDriverInfoDTO carDriverInfoDTO : driverInfoList) {
				CarRelateTeam carRelateTeam = new CarRelateTeam();
				carRelateTeam.setDriverId(Integer.valueOf(carDriverInfoDTO.getDriverId()));
				CarRelateTeam teamRelate = carRelateTeamExMapper.selectOneTeam(carRelateTeam);
				if(!Check.NuNObj(teamRelate)){
					carDriverInfoDTO.setTeamName(teamRelate.getTeamName());
					carDriverInfoDTO.setTeamId(String.valueOf(teamRelate.getTeamId()));
				}
				CarRelateGroup carRelateGroup = new CarRelateGroup();
				carRelateGroup.setDriverId(Integer.valueOf(carDriverInfoDTO.getDriverId()));
				CarRelateGroup group = carRelateGroupExMapper.selectOneGroup(carRelateGroup);
				if(!Check.NuNObj(group)){
					carDriverInfoDTO.setCarGroupName(group.getTeamName());
					carDriverInfoDTO.setGroupId(group.getGroupId());
				}
			}
			return driverInfoList;
		}catch (Exception e){
			logger.error("获取班制设置司机列表异常:{}",e);
			return null;
		}
	}

	/** 
	* @Desc: 保存司机日排班信息 
	* @param:
	* @return:  
	* @Author: lunan
	* @Date: 2018/9/3 
	*/
	public String saveDriverDayDuty(DutyParamRequest dutyParamRequest){
		if(Check.NuNObj(dutyParamRequest)){
			return null;
		}
		try{
			if(Check.NuNStr(dutyParamRequest.getDriverIds())){
				return "请选定要排班的司机！";
			}
			if(Check.NuNStr(dutyParamRequest.getForcedIds())){
				return "请选择强制上班设置！";
			}
			if(Check.NuNStr(dutyParamRequest.getTimes())){
				return "请选定要排班的日期！";
			}
			if(Check.NuNStr(dutyParamRequest.getDutyIds())){
				return "请选择排班时长设置！";
			}
			String driverIds = dutyParamRequest.getDriverIds();
			String times = dutyParamRequest.getTimes();
			String forcedIds = dutyParamRequest.getForcedIds();
			String dutyIds = dutyParamRequest.getDutyIds();
			String[] driverIdArray = driverIds.split(",");
			String[] timeArray = times.split(",");
			if (null == driverIdArray || driverIdArray.length <= 0) {
				return "请选定要排班的司机！";
			}
			if (null == timeArray || timeArray.length <= 0) {
				return "请选定要排班的日期！";
			}
			String forceIdsResult = dealForceIds(forcedIds);
			if ("repeat".equals(forceIdsResult)) {
				return "高峰时段选择重复！";
			} else if ("repeatTime".equals(forceIdsResult)) {
				return "强制上班时间段重叠！";
			}
			//设置强制上班结果
			dutyParamRequest.setForcedTimes(forceIdsResult);
			es.submit(new DayDutyTasker(dutyParamRequest));

		}catch (Exception e){
			logger.error("保存排班信息异常:{}",e);
		}
		return "已接收请求，正在处理，稍后可在发布司机排版菜单查看";
	}

	class DayDutyTasker implements Runnable{

		private DutyParamRequest dutyParamRequest;

		public DayDutyTasker(DutyParamRequest dutyParamRequest){
			this.dutyParamRequest = dutyParamRequest;
		}
		@Override
		public void run() {
			try {
				logger.info("保存司机日排班信息 driverIds"+dutyParamRequest.getDutyIds()+",times"+dutyParamRequest.getTimes());
				Map<String,Object> result = new HashMap<String,Object>();
				asyncDutyService.saveDriverDayDutyList(dutyParamRequest);
			} catch (Exception e) {
				logger.error("DayDutyTasker",e);
			}
		}
	}

	/** 
	* @Desc: 处理强制上班结果集 
	* @param:
	* @return:  
	* @Author: lunan
	* @Date: 2018/9/3 
	*/ 
	public String dealForceIds(String forceIds){
		if(Check.NuNStr(forceIds)){
			return null;
		}
		try{
			String resultStr = "";
			DutyParamRequest searchParam = new DutyParamRequest();
			searchParam.setForcedIds(forceIds);
			List<CarDriverMustDutyDTO> mustList = carDriverMustDutyExMapper.selectDriverMustDutyList(searchParam);
			StringBuffer forcedTimesBuffer = new StringBuffer();
			for (CarDriverMustDutyDTO mustDuty : mustList) {

				for (CarDriverMustDutyDTO repeatDto : mustList) {
					if (mustDuty.getPeakTimes().intValue()
							== repeatDto.getPeakTimes().intValue()) {  // 高峰时段不能重复
						return "repeat";
					}
					if (checkRepeatTime(mustDuty, repeatDto)) { // 检查强制上班时间段是否有重叠
						return "repeatTime";
					}
				}
				//拼接,号，首个多余，最后处理去掉
				forcedTimesBuffer.append(",");
				if (null != mustDuty.getPeakTimes()) {
					String peakTimes = EnumDriverDutyPeakTimes.getKey(mustDuty.getPeakTimes().intValue());
					if (null != peakTimes) {
						forcedTimesBuffer.append(peakTimes + ":");
					}
				}
				if (null != mustDuty.getStartDate()) {
					forcedTimesBuffer.append(mustDuty.getStartDate());
				}
				forcedTimesBuffer.append("—");
				if (null != mustDuty.getEndDate()) {
					forcedTimesBuffer.append(mustDuty.getEndDate());
				}
			}
			resultStr = forcedTimesBuffer.toString();
			if(!Check.NuNStr(resultStr)){
				resultStr = resultStr.substring(1,resultStr.length());
			}
			return resultStr;
		}catch (Exception e){
			logger.error("处理强制上班时间异常:{}",e);
			return null;
		}
	}

	/**
	 * 检查强制上班时间段是否有重叠
	 * @param driverMustDuty1
	 * @param driverMustDuty2
	 * @return true 有重叠； false 无重叠
	 */
	private boolean checkRepeatTime(CarDriverMustDutyDTO driverMustDuty1, CarDriverMustDutyDTO driverMustDuty2) {
		boolean result = false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			Date startTime1 = sdf.parse("2000-01-01 " + driverMustDuty1.getStartDate());
			Date endTime1 = sdf.parse("2000-01-01 " + driverMustDuty1.getEndDate());
			Date startTime2 = sdf.parse("2000-01-01 " + driverMustDuty2.getStartDate());
			Date endTime2 = sdf.parse("2000-01-01 " + driverMustDuty2.getEndDate());
			if (startTime1.getTime() >= endTime2.getTime()
					|| endTime1.getTime() <= startTime2.getTime()) {
				result = false;
			} else {
				result = true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}
}