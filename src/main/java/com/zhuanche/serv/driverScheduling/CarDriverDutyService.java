package com.zhuanche.serv.driverScheduling;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.dutyEnum.ServiceReturnCodeEnum;
import com.zhuanche.dto.CarDriverInfoDTO;
import com.zhuanche.dto.driver.CarDriverDayDutyDTO;
import com.zhuanche.entity.mdbcarmanage.CarDriverTeam;
import com.zhuanche.request.CommonRequest;
import com.zhuanche.request.DriverTeamRequest;
import com.zhuanche.request.DutyParamRequest;
import com.zhuanche.serv.common.CitySupplierTeamCommonService;
import com.zhuanche.serv.common.DataPermissionHelper;
import com.zhuanche.serv.driverteam.CarDriverTeamService;
import com.zhuanche.util.Check;
import com.zhuanche.util.DriverUtils;
import mapper.mdbcarmanage.ex.CarDriverDayDutyExMapper;
import mapper.mdbcarmanage.ex.CarDriverTeamExMapper;
import mapper.mdbcarmanage.ex.DriverDutyTimeInfoExMapper;
import mapper.rentcar.ex.CarBizDriverInfoExMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

	@Autowired
	private CitySupplierTeamCommonService citySupplierTeamCommonService;

	@Autowired
	private AsyncDutyService asyncDutyService;

	@Autowired
	private CarDriverTeamExMapper carDriverTeamExMapper;
	@Autowired
	private CarDriverTeamService carDriverTeamService;

	private static final ExecutorService es = Executors.newCachedThreadPool();

	/**
	 * @Desc: 查询司机排班 (包含发布司机排班数据列表 status 1)
	 * @param:
	 * @return:
	 * @Author: lunan
	 * @Date: 2018/9/1
	 */
	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
	} )
	public PageInfo<CarDriverDayDutyDTO> queryDriverDayDutyList(DutyParamRequest dutyParamRequest){

		//发布司机排班的查询功能 上层返回提示语
		if(!Check.NuNObj(dutyParamRequest) && dutyParamRequest.getUnpublishedFlag() == 1){
			dutyParamRequest.setStatus(1);
		}
		try{
			//手机号转换driverId进行查询
			if(!Check.NuNStr(dutyParamRequest.getPhone())) {
				DutyParamRequest request = new DutyParamRequest();
				request.setPhone(dutyParamRequest.getPhone());

				CarDriverInfoDTO driverInfo = carBizDriverInfoExMapper.queryOneDriver(request);
				if(driverInfo == null){
					PageInfo<CarDriverDayDutyDTO> pageInfo = new PageInfo<>();
					pageInfo.setTotal(0);
					pageInfo.setPages(1);
					pageInfo.setList(null);
					return pageInfo;
				}
				dutyParamRequest.setDriverId(Integer.parseInt(driverInfo.getDriverId()));
			}

			//组装权限参数
			dutyParamRequest = generateDutyParamRequestByUser(dutyParamRequest);
			if(dutyParamRequest.getTeamId() != null){
				Set<Integer> authTeamIdSet =  dutyParamRequest.getTeamIds();
				if(authTeamIdSet != null && !authTeamIdSet.isEmpty()){
					if(!authTeamIdSet.contains(dutyParamRequest.getTeamId())){
						PageInfo<CarDriverDayDutyDTO> pageInfo = new PageInfo<>(new ArrayList<>());
						logger.info("无权查看小队【"+dutyParamRequest.getTeamId() +"】的排班信息");
						return pageInfo;
					}else {
						Set<Integer> teamIdSet = new HashSet<>();
						teamIdSet.add(dutyParamRequest.getTeamId());
						Set<Integer> driverIdSet = DriverUtils.getDriverIdsByUserTeamsV2(  carDriverTeamService , teamIdSet);
						if( driverIdSet == null || driverIdSet.isEmpty()){
							PageInfo<CarDriverDayDutyDTO> pageInfo = new PageInfo<>(new ArrayList<>());
							logger.info("查看小队【"+dutyParamRequest.getTeamId() +"】的排班信息,司机id为空");
							return pageInfo;
						}else {
							dutyParamRequest.setDriverIdSet(driverIdSet);
						}
					}
				}
			}

			//组装获取team状态不为2的数据
			DriverTeamRequest driverTeamRequest = new DriverTeamRequest();
			Set<String> cityIdStringSet = new HashSet<>();
			if(dutyParamRequest.getCityIds() != null){
				for(Integer cityIdTemp :dutyParamRequest.getCityIds()){
					cityIdStringSet.add(cityIdTemp+"");
				}
				driverTeamRequest.setCityIds(cityIdStringSet);
			}
			Set<String> supplierIdStringSet = new HashSet<>();
			if(dutyParamRequest.getSupplierIds() != null){
				for(Integer idTemp :dutyParamRequest.getSupplierIds()){
					supplierIdStringSet.add(idTemp+"");
				}
				driverTeamRequest.setSupplierIds(supplierIdStringSet);
			}
			Set<String> teamIdStringSet = new HashSet<>();
			if(dutyParamRequest.getTeamIds() != null){
				for(Integer idTemp :dutyParamRequest.getTeamIds()){
					teamIdStringSet.add(idTemp+"");
				}
				driverTeamRequest.setTeamIds(teamIdStringSet);
			}
			driverTeamRequest.setCityId(dutyParamRequest.getCityId()+"");
			driverTeamRequest.setSupplierId(dutyParamRequest.getSupplierId()+"");
			driverTeamRequest.setTeamId(dutyParamRequest.getTeamId());

			List<CarDriverTeam> carDriverTeamList = carDriverTeamExMapper.queryForListByStatusNotEq2(driverTeamRequest);

			if((dutyParamRequest.getTeamIds() != null && dutyParamRequest.getTeamIds().size() >= 1) ||
					dutyParamRequest.getTeamId() != null){
				//移除状态为“删除状态【2】的team”
				if(carDriverTeamList != null){
					Map<String,CarDriverTeam> carDriverTeamMap = new HashMap<>();
					for(CarDriverTeam temp : carDriverTeamList){
						carDriverTeamMap.put("team_"+temp.getId(),temp);
					}

					//保证teamId都是有效的id，已经删除的teamId则不在范围内
					Set<Integer> newTeamSet = new HashSet<>();
					Set<Integer> teamSet = dutyParamRequest.getTeamIds();
					if(teamSet.isEmpty()){
						for(Integer teamId : teamSet){
							if(carDriverTeamMap.get("team_"+teamId) != null){
								newTeamSet.add(teamId);
							}
						}
					}
					dutyParamRequest.setTeamIds(newTeamSet);

					if(carDriverTeamMap.get("team_"+dutyParamRequest.getTeamId()) == null){
						dutyParamRequest.setTeamId(-1);
					}
				}
			}else if(dutyParamRequest.getTeamIds() != null){
				Set<Integer> newTeamSet = new HashSet<>();
				for(CarDriverTeam team : carDriverTeamList){
					newTeamSet.add(team.getId());
				}
				dutyParamRequest.setTeamIds(newTeamSet);
			}

			PageHelper.startPage(dutyParamRequest.getPageNo(), dutyParamRequest.getPageSize());

			logger.info("查询司机排班的参数为："+JSON.toJSONString(dutyParamRequest));
			List<CarDriverDayDutyDTO> listData = carDriverDayDutyExMapper.selectForListV2(dutyParamRequest);
			PageInfo<CarDriverDayDutyDTO> pageInfo = new PageInfo<>(listData);
			List<CarDriverDayDutyDTO> list = pageInfo.getList();
			if(list != null){
				Set<Integer> driverSet = new HashSet<>();
				for (CarDriverDayDutyDTO carDriverDayDuty : list) {
					driverSet.add(carDriverDayDuty.getDriverId());
				}

				List<CarDriverInfoDTO> driverList = new ArrayList<>();
				if(!driverSet.isEmpty()){
					driverList =carBizDriverInfoExMapper.queryListDriverByDriverIds(driverSet);
				}
				Map<String,CarDriverInfoDTO> driverCache = new HashMap<>();
				for(CarDriverInfoDTO item : driverList){
					driverCache.put("d_"+item.getDriverId(),item);
				}
				CarDriverInfoDTO info = null;
				for (CarDriverDayDutyDTO carDriverDayDuty : list) {
					info = driverCache.get("d_"+carDriverDayDuty.getDriverId());
					if(!Check.NuNObj(info)){
						carDriverDayDuty.setPhone(info.getPhone());
					}
				}
			}

			return pageInfo;
		}catch (Exception e){
			logger.error("查询排班司机列表异常，参数dutyParamRequest="+(dutyParamRequest==null?"null":JSON.toJSONString(dutyParamRequest)),e);
			return new PageInfo<>();
		}
	}

	/**
	 * 重新组装请求参数，并返回 DutyParamRequest参数
	 * @param dutyParamRequest
	 * @return
	 */
	private DutyParamRequest generateDutyParamRequestByUser(DutyParamRequest dutyParamRequest){
		//发布司机排班的查询功能 上层返回提示语
		if(!Check.NuNObj(dutyParamRequest) && dutyParamRequest.getUnpublishedFlag() == 1){
			dutyParamRequest.setStatus(1);
		}
		try{
			/** 数据权限处理开始 */
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
			//参数转换
			dutyParamRequest.setCityIds(citySupplierTeamCommonService.setStringShiftInteger(resultParmam.getCityIds()));
			dutyParamRequest.setSupplierIds(citySupplierTeamCommonService.setStringShiftInteger(resultParmam.getSupplierIds()));
			dutyParamRequest.setTeamIds(resultParmam.getTeamIds());
			/** 数据权限处理结束 */


			 return dutyParamRequest;
		}catch (Exception e){
			logger.error("组装权限异常:dutyParamRequest="+(dutyParamRequest==null?"null":JSON.toJSONString(dutyParamRequest))+";"+JSON.toJSONString(e));
			return dutyParamRequest;
		}
	}

	/**
	 * @Desc: 发布排班
	 * @param:
	 * @return:
	 * @Author: lunan
	 * @Date: 2018/9/3
	 */
	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="rentcar-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
	} )
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
			logger.error("发布数据异常:{}"+JSON.toJSONString(e));
			return 0;
		}

	}

	class AffirmDriverDayDuty implements Runnable{

		private DutyParamRequest dutyParamRequest;

		public AffirmDriverDayDuty(DutyParamRequest dutyParamRequest){
			this.dutyParamRequest = dutyParamRequest;
		}
		@Override
		public void run() {
			try {
				// 发布数据
				asyncDutyService.affirmDriverDayDuty(dutyParamRequest);
			} catch (Exception e) {
				logger.error("AffirmDriverDayDuty"+JSON.toJSONString(e));
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
			logger.error("时间参数错误"+JSON.toJSONString(e));
		}

		return startTime;
	}




}