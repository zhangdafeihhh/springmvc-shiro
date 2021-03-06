package com.zhuanche.serv.driverScheduling;

import com.alibaba.fastjson.JSON;
import com.zhuanche.common.cache.RedisCacheUtil;
import com.zhuanche.common.dutyEnum.EnumDriverDutyTimeFlag;
import com.zhuanche.common.rocketmq.CommonRocketProducer;
import com.zhuanche.common.rocketmq.CommonRocketProducerDouble;
import com.zhuanche.common.rocketmq.DriverWideRocketProducer;
import com.zhuanche.common.rocketmq.ExcelProducerDouble;
import com.zhuanche.constant.Constants;
import com.zhuanche.constant.EnvUtils;
import com.zhuanche.dto.CarDriverInfoDTO;
import com.zhuanche.dto.driver.CarDriverDayDutyDTO;
import com.zhuanche.dto.driverDuty.CarDriverDurationDTO;
import com.zhuanche.entity.mdbcarmanage.CarDriverDayDuty;
import com.zhuanche.entity.mdbcarmanage.CarDriverTeam;
import com.zhuanche.entity.mdbcarmanage.DriverDutyTimeInfo;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.entity.rentcar.CarBizDriverInfo;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.request.CommonRequest;
import com.zhuanche.request.DriverTeamRequest;
import com.zhuanche.request.DutyParamRequest;
import com.zhuanche.serv.common.CitySupplierTeamCommonService;
import com.zhuanche.util.Check;
import mapper.mdbcarmanage.ex.CarDriverDayDutyExMapper;
import mapper.mdbcarmanage.ex.CarDriverTeamExMapper;
import mapper.mdbcarmanage.ex.CarDutyDurationExMapper;
import mapper.mdbcarmanage.ex.DriverDutyTimeInfoExMapper;
import mapper.rentcar.CarBizDriverInfoMapper;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


/**
 * @description: ????????????
 *
 * <PRE>
 * <BR>	????????????
 * <BR>-----------------------------------------------
 * <BR>	????????????			?????????			????????????
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

	@Autowired
	private CarBizDriverInfoMapper carBizDriverInfoMapper;

	@Autowired
	private CitySupplierTeamCommonService citySupplierTeamCommonService;


	public Map<String, Object> saveDriverDayDutyList(DutyParamRequest dutyParamRequest) {
		logger.info("???????????????????????????????????????????????????"+JSON.toJSONString(dutyParamRequest));
		Map<String, Object> result = new HashMap<String, Object>();
		if (null == dutyParamRequest) {
			result.put("result", "0");
			result.put("msg", "???????????????");
			return result;
		}
		String driverIds = dutyParamRequest.getDriverIds();
		String times = dutyParamRequest.getTimes();
		String forcedIds = dutyParamRequest.getForcedIds(); // ??????????????????
		String dutyIds = dutyParamRequest.getDutyIds(); // ????????????
		if (null == driverIds) {
			result.put("result", "0");
			result.put("msg", "??????????????????????????????");
			return result;
		}
		if (null == times) {
			result.put("result", "0");
			result.put("msg", "??????????????????????????????");
			return result;
		}
		if (null == forcedIds) {
			result.put("result", "0");
			result.put("msg", "??????????????????????????????");
			return result;
		}
		if (null == dutyIds) {
			result.put("result", "0");
			result.put("msg", "??????????????????????????????");
			return result;
		}
		String[] driverIdArray = driverIds.split(",");
		String[] timeArray = times.split(",");
		if (null == driverIdArray || driverIdArray.length <= 0) {
			result.put("result", "0");
			result.put("msg", "??????????????????????????????");
			return result;
		}
		if (null == timeArray || timeArray.length <= 0) {
			result.put("result", "0");
			result.put("msg", "??????????????????????????????");
			return result;
		}
		// ??????????????????????????????(???????????????)

		// ????????????????????????
		String dutyTimes = getDutyTimes(dutyIds);
		dutyParamRequest.setDutyTimes(dutyTimes);

		// ??????????????????????????????????????????
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
		// ???????????????????????????????????????
		Map<String, Object> data = getDriverDayDutyListData(driverIdArray, timeArray, dutyParamRequest);
		List<CarDriverDayDuty> insertList = (List<CarDriverDayDuty>)data.get("insertList"); // ??????????????????
		List<CarDriverDayDuty> updateList = (List<CarDriverDayDuty>)data.get("updateList"); // ??????????????????
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
			errorMsgBuffer.append("????????????[");
			for(String key:errorMap.keySet()) {
				errorMsgBuffer.append(key + ":" + errorMap.get(key) + ",");
			}
			errorMsgBuffer.append("]");
			errorMsg = errorMsgBuffer.toString();
		}
		result.put("result", "1");
		result.put("msg", "???????????????");
		if (errorMsg != null) {
			result.put("errorMsg", errorMsg);
		}
		return result;
	}


	/**
	 * ??????????????????????????????????????????????????????
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
				// ??????????????????
				DutyParamRequest param = new DutyParamRequest();
				param.setDriverId(Integer.valueOf(driverId));
				CarDriverInfoDTO driverInfo = carBizDriverInfoExMapper.queryOneDriver(param);
				for (String timeStr : timeArray) {
					try {
						String time = timeStr;
						if (null == driverInfo) {
							errorMap.put(driverId + " " + timeStr, "???????????????????????????");
							continue;
						}
						// ????????????????????????????????????
						Integer id = null;
						param.setTime(time);
						List<CarDriverDayDutyDTO> oldRecordList = carDriverDayDutyExMapper.selectForList(param);
						logger.info("??????????????????,??????ID="+driverId+",??????="+time+",????????????="+oldRecordList.size());
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
						errorMap.put(driverId + " " + timeStr, "?????????????????????");
						logger.error(driverId + " " + timeStr + "?????????????????????", e);
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
	 * ???????????????????????????
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
			//??????,???????????????????????????????????????
			dutyTimesBuffer.append(",");

			if (null != insideDuration.getDutyName() && !"".equals(insideDuration.getDutyName())) {
				dutyTimesBuffer.append(insideDuration.getDutyName()).append(":");
			}
			if (null != insideDuration.getStartDate()) {
				dutyTimesBuffer.append(insideDuration.getStartDate());
			}
			dutyTimesBuffer.append("???");
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

	//????????????
	@SuppressWarnings("unchecked")
	public void affirmDriverDayDuty(DutyParamRequest dutyParamRequest) {
		logger.info("??????????????????????????????:{}"+JSON.toJSONString(dutyParamRequest));
		try{
			CommonRequest commonRequest = new CommonRequest();
			if(!Check.NuNObj(dutyParamRequest.getCityId())){
				commonRequest.setCityId(String.valueOf(dutyParamRequest.getCityId()));
			}
			if(!Check.NuNObj(dutyParamRequest.getSupplierId())){
				commonRequest.setSupplierId(String.valueOf(dutyParamRequest.getSupplierId()));
			}
			if(!Check.NuNObj(dutyParamRequest.getTeamId())){
				commonRequest.setTeamId(dutyParamRequest.getTeamId());
			}
			CommonRequest resultParmam = citySupplierTeamCommonService.paramDeal(commonRequest);
			dutyParamRequest.setCityIds(citySupplierTeamCommonService.setStringShiftInteger(resultParmam.getCityIds()));
			dutyParamRequest.setSupplierIds(citySupplierTeamCommonService.setStringShiftInteger(resultParmam.getSupplierIds()));
			dutyParamRequest.setTeamIds(resultParmam.getTeamIds());

			logger.info("????????????????????????:{}"+JSON.toJSONString(dutyParamRequest));
			List<CarDriverDayDutyDTO> list = carDriverDayDutyExMapper.selectForList(dutyParamRequest);
			if(Check.NuNCollection(list)){
				logger.info("??????????????????????????????????????????:{}"+ JSON.toJSONString(dutyParamRequest));
				return;
			}
			Map<String, Object> data = this.getAffirmDriverDayDutyData(list);
			List<DriverDutyTimeInfo> insertList = (List<DriverDutyTimeInfo>) data.get("insertList");
			List<DriverDutyTimeInfo> updateList = (List<DriverDutyTimeInfo>) data.get("updateList");
			Map<String, Object> paramMap = new HashMap<String, Object>();
			if (!CollectionUtils.isEmpty(insertList)) {
				paramMap.put("list", insertList);
				// ????????????
				Integer result = driverDutyTimeInfoExMapper.insertDriverDutyTimeInfoList(paramMap);
				logger.info("???????????????????????????{}"+JSON.toJSONString(dutyParamRequest)+"??????:"+result);
			}
			if (!CollectionUtils.isEmpty(updateList)) {
				paramMap.put("list", updateList);
				// ????????????
				Integer timeInfoLines = 0;
				String yearMonthStr = updateList.get(0).getTime();
				for (DriverDutyTimeInfo timeInfo : updateList){
					timeInfoLines += driverDutyTimeInfoExMapper.updateDriverDutyTimeInfoOne(timeInfo);
					RedisCacheUtil.delete(Constants.REDISKEYPREFIX_DRIVERDUTYINFO+"_"+yearMonthStr+"_"+timeInfo.getDriverId());
				}
//				Integer result = driverDutyTimeInfoExMapper.updateDriverDutyTimeInfoList(paramMap);
				logger.info("???????????????????????????{}"+JSON.toJSONString(dutyParamRequest)+"??????:"+timeInfoLines);
			}
			// ?????????????????????????????????
			Map<String, Object> dayDutyParams = new HashMap<String, Object>();
			dayDutyParams.put("list", list);
			Integer dayDutyLines = 0;
			for(CarDriverDayDutyDTO dayDuty : list){
				dayDutyLines +=carDriverDayDutyExMapper.updateDriverDayDutyOne(dayDuty);
			}
//			Integer result = carDriverDayDutyExMapper.updateDriverDayDutyList(dayDutyParams);
			logger.info("?????????????????????????????????{}"+JSON.toJSONString(dutyParamRequest)+"??????:"+dayDutyLines);
			return;
		}catch (Exception e){
			logger.error("????????????:{}",e);
			logger.error("??????????????????:{}"+JSON.toJSONString(dutyParamRequest));
		}
	}

	/**
	 * ????????????????????? ???????????????????????????????????????????????????
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
				//????????????????????????
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
	 * ????????????????????????
	 * @param forcedTimes
	 * @return
	 */
	private String getMustBDutyTimeByForcedTimes(String forcedTimes) {
		String mustBDutyTime = null;
		if (!StringUtils.isEmpty(forcedTimes)) {
			Map<String,String> map = new HashMap<String,String>();
			forcedTimes = forcedTimes.replace("???", "-");
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
	 * ????????????????????????
	 * @param dutyTimes
	 * @return
	 */
	private String getDutyTimeByDutyTimes(String dutyTimes) {
		String dutyTime = null;
		if (!StringUtils.isEmpty(dutyTimes)) {
			StringBuffer dutyTimeBuffer = new StringBuffer();
			dutyTimes = dutyTimes.replace("???", "-");
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
		//????????????
		driverInfo.setTeamId(teamId);
		driverInfo.setTeamName(teamName);
		driverInfo.setGroupId(null);
		driverInfo.setCarGroupName("");
		sendDriverToMq(driverInfo, "UPDATE");
	}

	public void sendDriverToMq(CarDriverInfoDTO driver, String method){
		//MQ????????????
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
			logger.info("????????????id??????????????????driverId={}" , driver.getDriverId());
			CarBizDriverInfo driverInfo = carBizDriverInfoMapper.selectByPrimaryKey(Integer.parseInt(driver.getDriverId()));
			if (Objects.nonNull(driverInfo)) {
				logger.info("?????????????????????????????????????????????messageMap");
				messageMap.put("licensePlates" , driverInfo.getLicensePlates());
			}
			String messageStr = JSONObject.fromObject(messageMap).toString();
			logger.info("????????????????????????????????????" + messageStr);
			//TODO 20190619????????????????????????????????????MQ
			DriverWideRocketProducer.publishMessage(DriverWideRocketProducer.TOPIC, method, String.valueOf(driver.getDriverId()), messageMap);
			CommonRocketProducer.publishMessage("driver_info", method,String.valueOf(driver.getDriverId()),messageMap);
			String envName = EnvUtils.ENVIMENT;
			if (Objects.nonNull(envName) && Arrays.asList(new String[]{"online","prod"}).contains(envName)){
				CommonRocketProducerDouble.publishMessage("driver_info",method,String.valueOf(driver.getDriverId()),messageMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}




}