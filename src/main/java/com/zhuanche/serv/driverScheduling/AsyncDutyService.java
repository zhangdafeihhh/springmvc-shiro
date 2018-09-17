package com.zhuanche.serv.driverScheduling;

import com.alibaba.fastjson.JSON;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.dutyEnum.EnumDriverDutyTimeFlag;
import com.zhuanche.common.rocketmq.CommonRocketProducer;
import com.zhuanche.dto.CarDriverInfoDTO;
import com.zhuanche.dto.driver.CarDriverDayDutyDTO;
import com.zhuanche.dto.driverDuty.CarDriverDurationDTO;
import com.zhuanche.entity.mdbcarmanage.CarDriverDayDuty;
import com.zhuanche.entity.mdbcarmanage.CarDriverTeam;
import com.zhuanche.entity.mdbcarmanage.DriverDutyTimeInfo;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.request.DriverTeamRequest;
import com.zhuanche.request.DutyParamRequest;
import com.zhuanche.util.Check;
import mapper.mdbcarmanage.ex.CarDriverDayDutyExMapper;
import mapper.mdbcarmanage.ex.CarDriverTeamExMapper;
import mapper.mdbcarmanage.ex.CarDutyDurationExMapper;
import mapper.mdbcarmanage.ex.DriverDutyTimeInfoExMapper;
import mapper.rentcar.ex.CarBizCityExMapper;
import mapper.rentcar.ex.CarBizDriverInfoExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import java.util.*;


/**
 * @description: 查看排班
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
 * @create: 2018-09-01 12:54
 *
 */
@Service
public class AsyncDutyService {

	private static final Logger logger = LoggerFactory.getLogger(AsyncDutyService.class);

	@Autowired
	private CarDriverDayDutyExMapper carDriverDayDutyExMapper;

	@Autowired
	private DriverDutyTimeInfoExMapper driverDutyTimeInfoExMapper;

	@Autowired
	private CarDutyDurationExMapper carDutyDurationExMapper;

	@Autowired
	private CarBizCityExMapper carBizCityExMapper;

	@Autowired
	private CarBizSupplierExMapper carBizSupplierExMapper;
	
	@Autowired
	private CarDriverTeamExMapper carDriverTeamExMapper;

	@Autowired
	private CarBizDriverInfoExMapper carBizDriverInfoExMapper;


	public Map<String, Object> saveDriverDayDutyList(DutyParamRequest dutyParamRequest) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (null == dutyParamRequest) {
			result.put("result", "0");
			result.put("msg", "参数错误！");
			return result;
		}
		String driverIds = dutyParamRequest.getDriverIds();
		String times = dutyParamRequest.getTimes();
		String forcedIds = dutyParamRequest.getForcedIds(); // 强制上班设置
		String dutyIds = dutyParamRequest.getDutyIds(); // 排班时长
		if (null == driverIds) {
			result.put("result", "0");
			result.put("msg", "请选定要排班的司机！");
			return result;
		}
		if (null == times) {
			result.put("result", "0");
			result.put("msg", "请选定要排班的日期！");
			return result;
		}
		if (null == forcedIds) {
			result.put("result", "0");
			result.put("msg", "请选择强制上班设置！");
			return result;
		}
		if (null == dutyIds) {
			result.put("result", "0");
			result.put("msg", "请选择排班时长设置！");
			return result;
		}
		String[] driverIdArray = driverIds.split(",");
		String[] timeArray = times.split(",");
		if (null == driverIdArray || driverIdArray.length <= 0) {
			result.put("result", "0");
			result.put("msg", "请选定要排班的司机！");
			return result;
		}
		if (null == timeArray || timeArray.length <= 0) {
			result.put("result", "0");
			result.put("msg", "请选定要排班的日期！");
			return result;
		}
		// 设置获取强制上班设置(上层已处理)

		// 设置排班时长设置
		String dutyTimes = getDutyTimes(dutyIds);
		dutyParamRequest.setDutyTimes(dutyTimes);

		// 设置城市名，供应商名，车队名
		if (null != dutyParamRequest.getCityId()) {
			Set<Integer> paramSet = new HashSet<>();
			paramSet.add(dutyParamRequest.getCityId());
			List<CarBizCity> city = carBizCityExMapper.queryByIds(paramSet);
			if(!Check.NuNCollection(city)){
				dutyParamRequest.setCityName(city.get(0).getCityName());
			}
		}
		if (null != dutyParamRequest.getSupplierId()) {
			CarBizSupplier supplier = new CarBizSupplier();
			supplier.setSupplierId(dutyParamRequest.getSupplierId());
			CarBizSupplier existsSupplier = carBizSupplierExMapper.queryForObject(supplier);
			if(!Check.NuNObj(existsSupplier)){
				dutyParamRequest.setSupplierName(existsSupplier.getSupplierFullName());
			}
		}
		if (null != dutyParamRequest.getTeamId()) {
			DriverTeamRequest paramRequest = new DriverTeamRequest();
			paramRequest.setTeamId(dutyParamRequest.getTeamId());
			CarDriverTeam carDriverTeam = carDriverTeamExMapper.selectByCondition(paramRequest);
			dutyParamRequest.setTeamName(carDriverTeam.getTeamName());
		}
		// 获取要批量插入或修改的数据
		Map<String, Object> data = getDriverDayDutyListData(driverIdArray, timeArray, dutyParamRequest);
		List<CarDriverDayDuty> insertList = (List<CarDriverDayDuty>)data.get("insertList"); // 要插入的数据
		List<CarDriverDayDuty> updateList = (List<CarDriverDayDuty>)data.get("updateList"); // 要修改的数据
		Map<String, String> errorMap = (Map<String, String>)data.get("errorMap");
		Map<String, Object> params = new HashMap<String, Object>();
		if (null != insertList && !insertList.isEmpty()) {
			params.put("list", insertList);
			carDriverDayDutyExMapper.insertDriverDayDutyList(params);
		}
		if (null != updateList && !updateList.isEmpty()) {
			params.remove("list");
			params.put("list", updateList);
			carDriverDayDutyExMapper.updateDriverDayDutyList(params);
		}
		String errorMsg = null;
		if (null != errorMap && !errorMap.isEmpty()) {
			StringBuffer errorMsgBuffer = new StringBuffer();
			errorMsgBuffer.append("错误数据[");
			for(String key:errorMap.keySet()) {
				errorMsgBuffer.append(key + ":" + errorMap.get(key) + ",");
			}
			errorMsgBuffer.append("]");
			errorMsg = errorMsgBuffer.toString();
		}
		result.put("result", "1");
		result.put("msg", "操作成功！");
		if (errorMsg != null) {
			result.put("errorMsg", errorMsg);
		}
		return result;
	}


	/**
	 * 获取要批量插入、修改的数据、错误数据
	 * @param driverIdArray
	 * @param timeArray
	 * @param
	 * @return
	 */
	private Map<String, Object> getDriverDayDutyListData(String[] driverIdArray, String[] timeArray,
														 DutyParamRequest dutyParamRequest) {
		Map<String, Object> data = new HashMap<String, Object>();
		List<CarDriverDayDuty> insertList = new ArrayList<CarDriverDayDuty>();
		List<CarDriverDayDuty> updateList = new ArrayList<CarDriverDayDuty>();
		Map<String, String> errorMap = new HashMap<String, String>();
		if (null != driverIdArray && driverIdArray.length > 0
				&& null != timeArray && driverIdArray.length > 0) {
			for (String driverId : driverIdArray) {
				// 设置司机姓名
				DutyParamRequest param = new DutyParamRequest();
				param.setDriverId(Integer.valueOf(driverId));
				CarDriverInfoDTO driverInfo = carBizDriverInfoExMapper.queryOneDriver(param);
				for (String timeStr : timeArray) {
					try {
						String time = timeStr;
						if (null == driverInfo) {
							errorMap.put(driverId + " " + timeStr, "未找到选定的司机！");
							continue;
						}
						// 校验该司机改天是否已排班
						Integer id = null;
						param.setTime(time);
						List<CarDriverDayDutyDTO> oldRecordList = carDriverDayDutyExMapper.selectForList(param);
						logger.info("司机班制排班,司机ID="+driverId+",时间="+time+",查询结果="+oldRecordList.size());
						if(!CollectionUtils.isEmpty(oldRecordList)){
							id = oldRecordList.get(0).getId();
						}
						CarDriverDayDuty newRecord = new CarDriverDayDuty();
						newRecord.setDriverId(Integer.parseInt(driverId));
						newRecord.setDriverName(driverInfo.getName());
						newRecord.setCityId(dutyParamRequest.getCityId());
						newRecord.setSupplierId(dutyParamRequest.getSupplierId());
						newRecord.setTeamId(dutyParamRequest.getTeamId());
						newRecord.setTime(time);
						newRecord.setForcedIds(dutyParamRequest.getForcedIds());
						newRecord.setDutyIds(dutyParamRequest.getDutyIds());
						newRecord.setForcedTimes(dutyParamRequest.getForcedTimes());
						newRecord.setDutyTimes(dutyParamRequest.getDutyTimes());
						newRecord.setType(dutyParamRequest.getType());
						newRecord.setStatus(1);
						newRecord.setCityName(dutyParamRequest.getCityName());
						newRecord.setSupplierName(dutyParamRequest.getSupplierName());
						newRecord.setTeamName(dutyParamRequest.getTeamName());
						if (id == null || id == 0) {
							insertList.add(newRecord);
						} else{
							newRecord.setId(id);
							updateList.add(newRecord);
						}
					} catch (Exception e) {
						errorMap.put(driverId + " " + timeStr, "处理数据失败！");
						logger.error(driverId + " " + timeStr + "处理数据失败！", e);
						continue;
					}
				}
			}
		}
		data.put("insertList", insertList);
		data.put("updateList", updateList);
		data.put("errorMap", errorMap);
		return data;
	}

	/**
	 * 获取排班时长设置值
	 * @param dutyIds
	 * @return
	 */
	private String getDutyTimes(String dutyIds) {
		DutyParamRequest dutyParamRequest = new DutyParamRequest();
		dutyParamRequest.setDutyIds(dutyIds);
		List<CarDriverDurationDTO> list = carDutyDurationExMapper.selectDutyDurationList(dutyParamRequest);
		if(Check.NuNCollection(list)){
			return null;
		}
		String resultStr = "";
		StringBuffer dutyTimesBuffer = new StringBuffer();
		for (CarDriverDurationDTO insideDuration : list) {
			//拼接,号，首个多余，最后处理去掉
			dutyTimesBuffer.append(",");

			if (null != insideDuration.getDutyName() && !"".equals(insideDuration.getDutyName())) {
				dutyTimesBuffer.append(insideDuration.getDutyName()).append(":");
			}
			if (null != insideDuration.getStartDate()) {
				dutyTimesBuffer.append(insideDuration.getStartDate());
			}
			dutyTimesBuffer.append("—");
			if (null != insideDuration.getEndDate()) {
				dutyTimesBuffer.append(insideDuration.getEndDate());
			}
		}
		resultStr = dutyTimesBuffer.toString();
		if(!Check.NuNStr(resultStr)){
			resultStr = resultStr.substring(1,resultStr.length());
		}
		return resultStr;
	}

	//发布数据
	@SuppressWarnings("unchecked")
	public void affirmDriverDayDuty(DutyParamRequest dutyParamRequest) {
		logger.info("开始执行排班入参:{}"+JSON.toJSONString(dutyParamRequest));
		try{
			List<CarDriverDayDutyDTO> list = carDriverDayDutyExMapper.selectForList(dutyParamRequest);
			if(Check.NuNCollection(list)){
				logger.info("没有发现需要发布的数据，入参:{}"+ JSON.toJSONString(dutyParamRequest));
				return;
			}
			Map<String, Object> data = this.getAffirmDriverDayDutyData(list);
			List<DriverDutyTimeInfo> insertList = (List<DriverDutyTimeInfo>) data.get("insertList");
			List<DriverDutyTimeInfo> updateList = (List<DriverDutyTimeInfo>) data.get("updateList");
			Map<String, Object> paramMap = new HashMap<String, Object>();
			if (!CollectionUtils.isEmpty(insertList)) {
				paramMap.put("list", insertList);
				// 批量插入
				Integer result = driverDutyTimeInfoExMapper.insertDriverDutyTimeInfoList(paramMap);
				logger.info("批量插入排班：入参{}"+JSON.toJSONString(dutyParamRequest)+"结果:"+result);
			}
			if (!CollectionUtils.isEmpty(updateList)) {
				paramMap.put("list", updateList);
				// 批量修改
				Integer result = driverDutyTimeInfoExMapper.updateDriverDutyTimeInfoList(paramMap);
				logger.info("批量更新排班：入参{}"+JSON.toJSONString(dutyParamRequest)+"结果:"+result);
			}
			// 将排班数据设置为已发布
			Map<String, Object> dayDutyParams = new HashMap<String, Object>();
			dayDutyParams.put("list", list);
			Integer result = carDriverDayDutyExMapper.updateDriverDayDutyList(dayDutyParams);
			logger.info("更新排版数据状态：入参{}"+JSON.toJSONString(dutyParamRequest)+"结果:"+result);
			return;
		}catch (Exception e){
			logger.error("排班异常:{}",e);
			logger.error("排班异常入参:{}"+JSON.toJSONString(dutyParamRequest));
		}
	}

	/**
	 * 获取发布信息中 新增的数据，修改的数据，错误的数据
	 * @param
	 * @return
	 */
	private Map<String, Object> getAffirmDriverDayDutyData(List<CarDriverDayDutyDTO> list) {
		Map<String, Object> data = new HashMap<String, Object>();
		List<DriverDutyTimeInfo> insertList = new ArrayList<DriverDutyTimeInfo>();
		List<DriverDutyTimeInfo> updateList = new ArrayList<DriverDutyTimeInfo>();
		if (null != list) {
			for(CarDriverDayDutyDTO driverDayDuty : list) {
				DriverDutyTimeInfo newRecord = new DriverDutyTimeInfo();
				newRecord.setDriverId(driverDayDuty.getDriverId());
				newRecord.setTime(driverDayDuty.getTime());
				String mustDutyTime = this.getMustBDutyTimeByForcedTimes(driverDayDuty.getForcedTimes());
				String dutyTime = this.getDutyTimeByDutyTimes(driverDayDuty.getDutyTimes());;
				newRecord.setMustDutyTime(mustDutyTime);
				newRecord.setDutyTime(dutyTime);
				DriverDutyTimeInfo params = new DriverDutyTimeInfo();
				params.setDriverId(driverDayDuty.getDriverId());
				params.setTime(driverDayDuty.getTime());
				//是否存在排班记录
				DriverDutyTimeInfo existsDutyTimeInfo = driverDutyTimeInfoExMapper.selectOne(params);
				if (Check.NuNObj(existsDutyTimeInfo)) {
					insertList.add(newRecord);
				} else {
					newRecord.setId(existsDutyTimeInfo.getId());
					updateList.add(newRecord);
				}
				driverDayDuty.setStatus(2);
			}
		}
		data.put("insertList", insertList);
		data.put("updateList", updateList);
		return data;
	}

	/**
	 * 解析强制排班设置
	 * @param forcedTimes
	 * @return
	 */
	private String getMustBDutyTimeByForcedTimes(String forcedTimes) {
		String mustBDutyTime = null;
		if (!StringUtils.isEmpty(forcedTimes)) {
			Map<String,String> map = new HashMap<String,String>();
			forcedTimes = forcedTimes.replace("—", "-");
			String[] forcedTimeArray = forcedTimes.split(",");
			for (String forcedTime : forcedTimeArray) {
				if (!StringUtils.isEmpty(forcedTime) && forcedTime.length() > 18) {
					String key = forcedTime.substring(0, forcedTime.length() - 18);
					String value = forcedTime.substring(forcedTime.length() - 17);
					key = EnumDriverDutyTimeFlag.getKey(key);
					map.put(key, value);
				}
			}
			JSONObject newObj = JSONObject.fromObject(map);
			mustBDutyTime = newObj.toString();
		}
		return mustBDutyTime;
	}

	/**
	 * 解析排班时长设置
	 * @param dutyTimes
	 * @return
	 */
	private String getDutyTimeByDutyTimes(String dutyTimes) {
		String dutyTime = null;
		if (!StringUtils.isEmpty(dutyTimes)) {
			StringBuffer dutyTimeBuffer = new StringBuffer();
			dutyTimes = dutyTimes.replace("—", "-");
			String[] dutyTimeArray = dutyTimes.split(",");
			int num = 0;
			for (String dutyTimeStr : dutyTimeArray) {
				if (!StringUtils.isEmpty(dutyTimeStr)) {
					String value = null;
					if (dutyTimeStr.length() > 18) {
						value = dutyTimeStr.substring(dutyTimeStr.length() - 17);
					} else {
						value = dutyTimeStr;
					}
					if (0 != num++) {
						dutyTimeBuffer.append(",");
					}
					dutyTimeBuffer.append(value);
				}
			}
			dutyTime = dutyTimeBuffer.toString();
		}
		return dutyTime;
	}

	public void processingData(Integer driverId, String teamId, String teamName, Integer value) {
		CarDriverInfoDTO driverInfo = carBizDriverInfoExMapper.selectDriverInfoByDriverId(driverId);
		if(Check.NuNObj(driverInfo)){
			return ;
		}
		//车队信息
		driverInfo.setTeamId(teamId);
		driverInfo.setTeamName(teamName);
		driverInfo.setGroupId(null);
		driverInfo.setCarGroupName("");
		sendDriverToMq(driverInfo, "UPDATE");
	}

	public void sendDriverToMq(CarDriverInfoDTO driver, String method){
		//MQ消息写入
		try {
			Map<String, Object> messageMap = new HashMap<String, Object>();
			messageMap.put("carNumber", driver.getLicensePlates());
			messageMap.put("city", driver.getServiceCity());
			messageMap.put("cityId",driver.getCityId());
			messageMap.put("createBy", driver.getUpdateBy()==null?"1":driver.getUpdateBy());
			messageMap.put("driverId", driver.getDriverId());
			messageMap.put("driverName",driver.getName());
			messageMap.put("driverPhone", driver.getPhone()==null?"":driver.getPhone());
			messageMap.put("status",driver.getStatus());
			messageMap.put("supplierFullName", driver.getSupplierName());
			messageMap.put("supplierId", driver.getSupplierId());
			messageMap.put("cooperationType", driver.getCooperationType());
			messageMap.put("groupId",driver.getGroupId());
			messageMap.put("create_date",driver.getCreateDate());
			messageMap.put("carType",driver.getCarGroupName()==null?"":driver.getCarGroupName());
			messageMap.put("teamId", driver.getTeamId()==null?"":driver.getTeamId());
			messageMap.put("teamName", driver.getTeamName()==null?"":driver.getTeamName());
			messageMap.put("teamGroupId", driver.getGroupId()==null?"":driver.getGroupId());
			messageMap.put("teamGroupName", driver.getCarGroupName()==null?"":driver.getCarGroupName());

			String messageStr = JSONObject.fromObject(messageMap).toString();
			logger.info("专车司机，同步发送数据：" + messageStr);
			CommonRocketProducer.publishMessage("driver_info", method,String.valueOf(driver.getDriverId()),messageMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}




}