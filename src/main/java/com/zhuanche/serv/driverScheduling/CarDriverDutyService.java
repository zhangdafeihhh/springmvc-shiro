package com.zhuanche.serv.driverScheduling;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.dutyEnum.EnumDriverDutyTimeFlag;
import com.zhuanche.common.dutyEnum.ServiceReturnCodeEnum;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.dto.CarDriverInfoDTO;
import com.zhuanche.dto.driver.CarDriverDayDutyDTO;
import com.zhuanche.entity.mdbcarmanage.CarDriverDayDuty;
import com.zhuanche.entity.mdbcarmanage.DriverDutyTimeInfo;
import com.zhuanche.request.DutyParamRequest;
import com.zhuanche.serv.common.DataPermissionHelper;
import com.zhuanche.util.Check;
import com.zhuanche.util.Common;
import mapper.mdbcarmanage.ex.CarDriverDayDutyExMapper;
import mapper.mdbcarmanage.ex.DriverDutyTimeInfoExMapper;
import mapper.rentcar.ex.CarBizDriverInfoExMapper;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @description: 车队设置
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
public class CarDriverDutyService {

	private static final Logger logger = LoggerFactory.getLogger(CarDriverDutyService.class);

	@Autowired
	private DataPermissionHelper dataPermissionHelper;

	@Autowired
	private CarBizDriverInfoExMapper carBizDriverInfoExMapper;

	@Autowired
	private CarDriverDayDutyExMapper carDriverDayDutyExMapper;

	@Autowired
	private DriverDutyTimeInfoExMapper driverDutyTimeInfoExMapper;

	private static final ExecutorService es = Executors.newCachedThreadPool();

	/** 
	* @Desc: 查询司机排班 (包含发布司机排班数据列表 status 1)
	* @param:
	* @return:  
	* @Author: lunan
	* @Date: 2018/9/1 
	*/ 
	public PageDTO queryDriverDayDutyList(DutyParamRequest dutyParamRequest){

		//TODO 数据权限
		if(Check.NuNObj(dutyParamRequest)){
			return null;
		}
		//发布司机排班的查询功能 上层返回提示语
		if(dutyParamRequest.getUnpublishedFlag() == 1){
			dutyParamRequest.setStatus(1);
		}
		try{
			DutyParamRequest request = new DutyParamRequest();
			//手机号转换driverId进行查询
			if(!Check.NuNStr(dutyParamRequest.getPhone())){
				request.setPhone(dutyParamRequest.getPhone());
				CarDriverInfoDTO driverInfo = carBizDriverInfoExMapper.queryOneDriver(request);
				request.setPhone(null);
				if(Check.NuNObj(driverInfo)){
					return null;
				}
				dutyParamRequest.setDriverId(Integer.valueOf(driverInfo.getDriverId()));
			}

			PageInfo<CarDriverDayDutyDTO> pageInfo = PageHelper.startPage(dutyParamRequest.getPageNo(), dutyParamRequest.getPageSize(), true).doSelectPageInfo(()
					-> carDriverDayDutyExMapper.queryForList(dutyParamRequest));
			List<CarDriverDayDutyDTO> list = pageInfo.getList();

			for (CarDriverDayDutyDTO carDriverDayDuty : list) {
				request.setDriverId(carDriverDayDuty.getDriverId());
				CarDriverInfoDTO info = carBizDriverInfoExMapper.queryOneDriver(request);
				carDriverDayDuty.setPhone(info.getPhone());
			}
			PageDTO pageDTO = new PageDTO();
			pageDTO.setTotal((int)pageInfo.getTotal());
			pageDTO.setResult(list);
			return pageDTO;
		}catch (Exception e){
			logger.error("查询排班司机列表异常:{}",e);
			return null;
		}
	}

	/** 
	* @Desc: 发布排班 
	* @param:  
	* @return:  
	* @Author: lunan
	* @Date: 2018/9/1 
	*/ 
	public int issueDriverDuty(DutyParamRequest dutyParamRequest){
		if(Check.NuNObj(dutyParamRequest)){
			return 0;
		}
		String startTime = this.getStartTimeForParams(dutyParamRequest);
		if (null != startTime) {
			dutyParamRequest.setStartTime(startTime);
		}
		try{
			if (Check.NuNObj(dutyParamRequest.getCityId())) {
				return ServiceReturnCodeEnum.NONE_CITY_EXISTS.getCode();
			}
			if (Check.NuNObj(dutyParamRequest.getSupplierId())) {
				return ServiceReturnCodeEnum.NONE_SUPPLIER_EXISTS.getCode();
			}
			if (Check.NuNObj(dutyParamRequest.getTeamId())) {
				return ServiceReturnCodeEnum.NONE_TEAM_EXISTS.getCode();
			}
			DutyParamRequest request = new DutyParamRequest();
			//手机号转换driverId进行查询
			if(!Check.NuNStr(dutyParamRequest.getPhone())){
				request.setPhone(dutyParamRequest.getPhone());
				CarDriverInfoDTO driverInfo = carBizDriverInfoExMapper.queryOneDriver(request);
				request.setPhone(null);
				if(Check.NuNObj(driverInfo)){
					return ServiceReturnCodeEnum.NONE_RECODE_EXISTS.getCode();
				}
				dutyParamRequest.setDriverId(Integer.valueOf(driverInfo.getDriverId()));
			}
			dutyParamRequest.setStatus(1);
			Integer total = carDriverDayDutyExMapper.getUnIssueCount(dutyParamRequest);
			if(total <= 0){
				return ServiceReturnCodeEnum.NONE_RECODE_EXISTS.getCode();
			}
			es.submit(new AffirmDriverDayDuty(dutyParamRequest));
			return ServiceReturnCodeEnum.DEAL_SUCCESS_MSG.getCode();
		}catch (Exception e){
			logger.error("发布数据异常:{}",e);
			return 0;
		}

	}

	//发布数据
	@SuppressWarnings("unchecked")
	public void affirmDriverDayDuty(DutyParamRequest dutyParamRequest) {
		logger.info("开始执行排班入参:{}"+JSON.toJSONString(dutyParamRequest));
		try{
			List<CarDriverDayDutyDTO> list = carDriverDayDutyExMapper.queryForList(dutyParamRequest);
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

	class AffirmDriverDayDuty implements Runnable{

		private DutyParamRequest dutyParamRequest;

		@Autowired
		private CarDriverDutyService carDriverDutyService;
		public AffirmDriverDayDuty(DutyParamRequest dutyParamRequest){
			this.dutyParamRequest = dutyParamRequest;
		}
		@Override
		public void run() {
			try {
				// 发布数据
				carDriverDutyService.affirmDriverDayDuty(dutyParamRequest);
			} catch (Exception e) {
				logger.error("AffirmDriverDayDuty",e);
			}
		}
	}

	private String getStartTimeForParams(DutyParamRequest params) {
		String startTime = null;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String sysDateStr = sdf.format(new Date());
		try {
			Date sysDate = sdf.parse(sysDateStr);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sysDate);
			calendar.add(Calendar.DAY_OF_YEAR, 1);
			Date defaultStartTime = calendar.getTime();
			startTime = sdf.format(defaultStartTime);

			if (null != params && !StringUtils.isEmpty(params.getStartTime())) {
				Date searchStartTime = sdf.parse(params.getStartTime());
				if (searchStartTime.after(defaultStartTime)) {
					startTime = null;
				}
			}
		} catch (ParseException e) {
			logger.error("时间参数错误", e);
		}

		return startTime;
	}




}