package com.zhuanche.serv.driverteam;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.zhuanche.common.cache.RedisCacheUtil;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.dutyEnum.ServiceReturnCodeEnum;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.constant.Constants;
import com.zhuanche.dto.CarDriverInfoDTO;
import com.zhuanche.dto.CarDriverTeamDTO;
import com.zhuanche.entity.mdbcarmanage.CarDriverTeam;
import com.zhuanche.entity.mdbcarmanage.CarRelateGroup;
import com.zhuanche.entity.mdbcarmanage.CarRelateTeam;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.request.CommonRequest;
import com.zhuanche.request.DriverTeamRequest;
import com.zhuanche.request.DutyParamRequest;
import com.zhuanche.request.TeamGroupRequest;
import com.zhuanche.serv.CarBizCityService;
import com.zhuanche.serv.CarBizDriverInfoService;
import com.zhuanche.serv.CarBizSupplierService;
import com.zhuanche.serv.common.CitySupplierTeamCommonService;
import com.zhuanche.serv.common.DataPermissionHelper;
import com.zhuanche.serv.driverScheduling.AsyncDutyService;
import com.zhuanche.serv.driverwide.DriverWideMongoService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.Check;
import mapper.mdbcarmanage.CarDriverTeamMapper;
import mapper.mdbcarmanage.CarRelateGroupMapper;
import mapper.mdbcarmanage.CarRelateTeamMapper;
import mapper.mdbcarmanage.ex.CarDriverTeamExMapper;
import mapper.mdbcarmanage.ex.CarRelateGroupExMapper;
import mapper.mdbcarmanage.ex.CarRelateTeamExMapper;
import mapper.rentcar.ex.CarBizDriverInfoExMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
 * @create: 2018-08-29 16:54
 *
 */
@Service
public class CarDriverTeamService{

	private static final Logger logger = LoggerFactory.getLogger(CarDriverTeamService.class);

	@Autowired
	private DataPermissionHelper dataPermissionHelper;

	@Autowired
	private CarBizCityService carBizCityService;

	@Autowired
	private CarBizSupplierService carBizSupplierService;

	@Autowired
	private CarDriverTeamExMapper carDriverTeamExMapper;

	@Autowired
	private CarDriverTeamMapper carDriverTeamMapper;

	@Autowired
	private CitySupplierTeamCommonService citySupplierTeamCommonService;

	@Autowired
	private CarRelateTeamExMapper carRelateTeamExMapper;

	@Autowired
	private CarRelateGroupExMapper carRelateGroupExMapper;

	@Autowired
	private CarRelateGroupMapper carRelateGroupMapper;

	@Autowired
	private CarRelateTeamMapper carRelateTeamMapper;

	@Autowired
	private CarBizDriverInfoExMapper carBizDriverInfoExMapper;

	@Autowired
	private AsyncDutyService asyncDutyService;

	@Autowired
	private CarBizDriverInfoService carBizDriverInfoService;

	@Autowired
	private DriverWideMongoService driverWideMongoService;

	/**
	 * @Desc: ??????????????????????????????
	 * @param:
	 * @return:
	 * @Author: lunan
	 * @Date: 2018/9/5
	 */
	public int updateTeamDuty(CarDriverTeam record){
		int result = carDriverTeamExMapper.updateTeamDuty(record);
		DynamicRoutingDataSource.DataSourceMode mdbcarManageMode = DynamicRoutingDataSource.getMasterSlave("mdbcarmanage-DataSource");
		try{
			DynamicRoutingDataSource.setMasterSlave("mdbcarmanage-DataSource", DynamicRoutingDataSource.DataSourceMode.SLAVE);
			if(result >0){
				List<Integer> teamDrivers = carRelateTeamExMapper.queryDriverIdsByTeamId(record.getId());
				if(Check.NuNCollection(teamDrivers)){
					List<Integer> groupDrivers = carRelateGroupExMapper.queryDriverIdsByGroupId(record.getId());
					for (Integer driverId : groupDrivers) {
						logger.info("????????????????????????key"+Constants.REDISKEYPREFIX_ISDUTYTIME+"_"+driverId);
						RedisCacheUtil.delete(Constants.REDISKEYPREFIX_ISDUTYTIME+"_"+driverId);
						logger.info("??????key??????teamid="+record.getId()+"driverId="+driverId+"??????=???"+RedisCacheUtil.get(Constants.REDISKEYPREFIX_ISDUTYTIME+"_"+driverId,String.class));
					}
				}else{
					for(Integer driverId : teamDrivers){
						logger.info("????????????????????????key??????=="+RedisCacheUtil.get(Constants.REDISKEYPREFIX_ISDUTYTIME+"_"+driverId,String.class));
						logger.info("????????????????????????key"+Constants.REDISKEYPREFIX_ISDUTYTIME+"_"+driverId);
						RedisCacheUtil.delete(Constants.REDISKEYPREFIX_ISDUTYTIME+"_"+driverId);
						logger.info("??????key??????teamid="+record.getId()+"driverId="+driverId+"??????=???"+RedisCacheUtil.get(Constants.REDISKEYPREFIX_ISDUTYTIME+"_"+driverId,String.class));
					}
				}
			}
		}finally {
			DynamicRoutingDataSource.setMasterSlave("mdbcarmanage-DataSource",mdbcarManageMode);
		}
		return result;
	}

	/**
	 * @Desc: ????????????
	 * @param:
	 * @return:
	 * @Author: lunan
	 * @Date: 2018/9/10
	 */
	public int removeDriverToTeam(Integer id, Integer driverId){
		logger.info("??????/??????????????????service??????:"+ "??????/??????id???"+id+"??????????????????id???"+driverId);
		try{
			//???????????????????????????
			RedisCacheUtil.delete(Constants.REDISKEYPREFIX_ISDUTYTIME+"_"+driverId);
			DynamicRoutingDataSource.DataSourceMode mdbcarManageMode = DynamicRoutingDataSource.getMasterSlave("mdbcarmanage-DataSource");
			CarDriverTeam carDriverTeam = null;
			try{
				DynamicRoutingDataSource.setMasterSlave("mdbcarmanage-DataSource", DynamicRoutingDataSource.DataSourceMode.SLAVE);
				carDriverTeam = carDriverTeamMapper.selectByPrimaryKey(id);
			}finally {
				DynamicRoutingDataSource.setMasterSlave("mdbcarmanage-DataSource",mdbcarManageMode);
			}

			if(Check.NuNObj(carDriverTeam)){
				logger.info("??????--??????/???????????????" + id + driverId);
				return ServiceReturnCodeEnum.NONE_RECODE_EXISTS.getCode();
			}
			CarRelateTeam carRelateTeam = new CarRelateTeam();
			carRelateTeam.setTeamId(id);
			carRelateTeam.setDriverId(driverId);
			if(Check.NuNObj(carDriverTeam.getpId())){
				//????????????????????????,??????????????????????????????????????????
				int result = carRelateTeamExMapper.deleteDriverFromTeam(id, driverId);
				logger.info("??????:"+id + "????????????"+driverId+"??????:"+result);
				CarRelateGroup group = new CarRelateGroup();
				group.setDriverId(driverId);
				CarRelateGroup existsGroup = carRelateGroupExMapper.selectOneGroup(group);
				if(!Check.NuNObj(existsGroup)){
					result = carRelateGroupExMapper.deleteDriverFromGroup(existsGroup.getGroupId(),driverId);
				}
				try {
					// ?????????????????????key
					carBizDriverInfoService.flashDriverInfo(driverId);
				} catch (Exception e) {
					logger.info("??????driverId={},??????,????????????????????????={}", driverId, e.getMessage());
				}
				//TODO ????????????ID?????????????????????MQ ?????????????????????  ????????????
				this.asyncDutyService.processingData(driverId, "", carDriverTeam.getTeamName(), 1);
				return result;
			}else{
				//????????????????????????
				  int code = carRelateGroupExMapper.deleteDriverFromGroup(id,driverId);
				  if(code > 0){
					  try {
						  // ?????????????????????key
						  carBizDriverInfoService.flashDriverInfo(driverId);
					  } catch (Exception e) {
						  logger.info("??????driverId={},??????,????????????????????????={}", driverId, e.getMessage());
					  }
					  //TODO ????????????ID?????????????????????MQ ?????????????????????
					  this.asyncDutyService.processingData(driverId, String.valueOf(carDriverTeam.getpId()), carDriverTeam.getTeamName(), 1);
				  }
				return code;
			}
		}catch (Exception e){
			logger.error("??????:"+id + "????????????"+driverId+"??????:"+JSON.toJSONString(e));
			return 0;
		}

	}

	/**
	 * @Desc: ?????????????????????/??????
	 * @param:
	 * @return:
	 * @Author: lunan
	 * @Date: 2018/8/31
	 */
	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="rentcar-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
	} )
	public int addDriverToTeam(DriverTeamRequest driverTeamRequest){

		logger.info("?????????????????????/????????????:{}"+JSON.toJSONString(driverTeamRequest));
		if(Check.NuNObj(driverTeamRequest) || Check.NuNObj(driverTeamRequest.getTeamId()) || Check.NuNStr(driverTeamRequest.getDrivers())){
			logger.info("?????????????????????/??????????????????");
			return ServiceReturnCodeEnum.DEAL_FAILURE.getCode();
		}

		DynamicRoutingDataSource.DataSourceMode mdbcarManageMode = DynamicRoutingDataSource.getMasterSlave("mdbcarmanage-DataSource");
		 CarDriverTeam carDriverTeam = null;
		try{
			DynamicRoutingDataSource.setMasterSlave("mdbcarmanage-DataSource", DynamicRoutingDataSource.DataSourceMode.SLAVE);
			carDriverTeam = carDriverTeamMapper.selectByPrimaryKey(driverTeamRequest.getTeamId());
		}finally {
			DynamicRoutingDataSource.setMasterSlave("mdbcarmanage-DataSource",mdbcarManageMode);
		}

		if(Check.NuNObj(carDriverTeam)){
			logger.info("??????/???????????????");
			return ServiceReturnCodeEnum.DEAL_FAILURE.getCode();
		}
		String[] driverIds = driverTeamRequest.getDrivers().split(",");
		int result = 0;
		CarRelateGroup group = new CarRelateGroup();
		CarRelateTeam team = new CarRelateTeam();
		for(String id : driverIds) {
			DutyParamRequest param = new DutyParamRequest();
			param.setDriverId(Integer.valueOf(id));
			String driverId = carBizDriverInfoExMapper.queryOneDriver(param).getDriverId();
			if(!Check.NuNObj(carDriverTeam.getpId())){
				//??????
				group.setGroupId(driverTeamRequest.getTeamId());
				group.setDriverId(Integer.valueOf(driverId));
				CarRelateGroup existsGroup = null;
				try{
					DynamicRoutingDataSource.setMasterSlave("mdbcarmanage-DataSource", DynamicRoutingDataSource.DataSourceMode.SLAVE);
					existsGroup = carRelateGroupExMapper.selectOneGroup(group);
				}finally {
					DynamicRoutingDataSource.setMasterSlave("mdbcarmanage-DataSource",mdbcarManageMode);
				}
				if(Check.NuNObj(existsGroup)){
					result += carRelateGroupMapper.insertSelective(group);
				}
			}else{
				team.setTeamId(driverTeamRequest.getTeamId());
				team.setDriverId(Integer.valueOf(driverId));
				CarRelateTeam existsTeam = null;
				try{
					DynamicRoutingDataSource.setMasterSlave("mdbcarmanage-DataSource", DynamicRoutingDataSource.DataSourceMode.SLAVE);
					existsTeam = carRelateTeamExMapper.selectOneTeam(team);
				}finally {
					DynamicRoutingDataSource.setMasterSlave("mdbcarmanage-DataSource",mdbcarManageMode);
				}
				if(Check.NuNObj(existsTeam)){
					//?????????????????? ?????? ???????????? ???????????????????????????
					try {
						carRelateTeamExMapper.deleteByDriverId(team.getDriverId());
					} catch (Exception e) {
						logger.info("????????????" + e);
					}
					result += carRelateTeamMapper.insertSelective(team);
				}
			}

			ExecutorService executor = Executors.newCachedThreadPool();
			CarDriverTeam copyCarDriverTeam = carDriverTeam;
			Future<Integer> future = executor.submit(new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					try {
						// ?????????????????????key
						carBizDriverInfoService.flashDriverInfo(Integer.parseInt(driverId));
					} catch (Exception e) {
						logger.info("??????driverId={},??????,????????????????????????={}", driverId, e.getMessage());
					}
					//TODO ????????????ID?????????????????????MQ ????????????????????? driverId; driverTeam.getTeamId();driverTeam.getTeamName()
					asyncDutyService.processingData(Integer.parseInt(driverId), String.valueOf(copyCarDriverTeam.getId()), copyCarDriverTeam.getTeamName(), 2);
					logger.info("mq??????????????????");
					return 1;
				}
			});

		}

		return result;
	}

	/**
	 * @Desc: ???????????????????????????
	 * @param:
	 * @return:
	 * @Author: lunan
	 * @Date: 2018/8/31
	 */
	@SuppressWarnings("rawtypes")
	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="rentcar-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE ),
			@MasterSlaveConfig(databaseTag="driver-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE ),
			@MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
	} )
	public PageDTO selectAddDriverList(DriverTeamRequest driverTeamRequest){
		logger.info("?????????????????????????????????:{}"+JSON.toJSONString(driverTeamRequest));
		if(Check.NuNObj(driverTeamRequest) || Check.NuNObj(driverTeamRequest.getTeamId())){
			return null;
		}
		try{
			TeamGroupRequest teamGroupRequest = new TeamGroupRequest();
			if(!Check.NuNObj(driverTeamRequest.getpId())){
				//????????????
				//1.???????????????????????????????????????
				teamGroupRequest.setTeamId(String.valueOf(driverTeamRequest.getpId()));
				List<CarRelateTeam> carRelateTeams = carRelateTeamExMapper.queryDriverTeamRelationList(teamGroupRequest);

				if(Check.NuNCollection(carRelateTeams)){
					logger.info("?????????/??????"+ driverTeamRequest.getTeamId()+"???????????????");
					return null;
				}
				Set<String> driverIdsAll = citySupplierTeamCommonService.dealDriverids(carRelateTeams, CarRelateTeam.class,"driverId");
				//2.??????????????????????????????????????????????????????????????????
				List<CarDriverTeam> carDriverTeams = carDriverTeamExMapper.queryForListByPid(driverTeamRequest);
				//2.1 ??????????????????????????????????????????????????????
				Set<String> teamIds = citySupplierTeamCommonService.dealDriverids(carDriverTeams,CarDriverTeam.class,"id");
				teamGroupRequest.setGroupIds(teamIds);
				List<CarRelateGroup> carRelateGroups = carRelateGroupExMapper.queryDriverGroupRelationList(teamGroupRequest);
				Set<String> existsGroups = citySupplierTeamCommonService.dealDriverids(carRelateGroups, CarRelateGroup.class,"driverId");
				//?????????????????????????????????
				driverIdsAll.removeAll(existsGroups);
				if(Check.NuNCollection(driverIdsAll)){
					logger.info("?????????/??????"+ driverTeamRequest.getTeamId()+"???????????????");
					return null;
				}
				driverTeamRequest.setDriverIds(driverIdsAll);
				PageInfo<CarDriverInfoDTO> pageInfo = PageHelper.startPage(driverTeamRequest.getPageNo(), driverTeamRequest.getPageSize(), true).doSelectPageInfo(()
						-> carBizDriverInfoExMapper.selectDriverList(driverTeamRequest));
				PageDTO pageDTO = new PageDTO();
				pageDTO.setResult(pageInfo.getList());
				pageDTO.setTotal((int)pageInfo.getTotal());
				return pageDTO;
			}else{
				//??????????????????
				CarDriverTeam carDriverTeam = carDriverTeamMapper.selectByPrimaryKey(driverTeamRequest.getTeamId());
				//???????????????????????????????????????????????? ---??????????????????????????????????????????
				Set<String> setCities = new HashSet<>();
				setCities.add(driverTeamRequest.getCityId());
				Set<String> setSuppliers =  new HashSet<>();
				setSuppliers.add(driverTeamRequest.getSupplierId());
				driverTeamRequest.setCityIds(setCities);
				driverTeamRequest.setSupplierIds(setSuppliers);
				//????????????????????????????????????????????????
				List<CarDriverInfoDTO> limitsDrivers = carBizDriverInfoExMapper.queryListByLimits(driverTeamRequest);
				if(Check.NuNCollection(limitsDrivers)){
					logger.info("?????????/??????"+ driverTeamRequest.getTeamId()+"???????????????");
					return null;
				}
				Set<String> driverIdsAll = citySupplierTeamCommonService.dealDriverids(limitsDrivers, CarDriverInfoDTO.class,"driverId");
				//???????????????????????????????????????
				List<CarDriverTeam> carDriverTeams = carDriverTeamExMapper.queryDriverTeam(null, null, null);
				Set<String> teamIds = citySupplierTeamCommonService.dealDriverids(carDriverTeams,CarDriverTeam.class,"id");
				teamGroupRequest.setTeamIds(teamIds);
				List<CarRelateTeam> carRelateTeams = carRelateTeamExMapper.queryDriverTeamRelationList(teamGroupRequest);
				Set<String> teamDrivers = citySupplierTeamCommonService.dealDriverids(carRelateTeams, CarRelateTeam.class,"driverId");
				//?????????????????????????????????
				driverIdsAll.removeAll(teamDrivers);
				driverTeamRequest.setDriverIds(driverIdsAll);
				PageInfo<CarDriverInfoDTO> pageInfo = PageHelper.startPage(driverTeamRequest.getPageNo(), driverTeamRequest.getPageSize(), true).doSelectPageInfo(()
						-> carBizDriverInfoExMapper.selectDriverList(driverTeamRequest));
				PageDTO pageDTO = new PageDTO();
				pageDTO.setResult(pageInfo.getList());
				pageDTO.setTotal((int)pageInfo.getTotal());
				return pageDTO;
			}
		}catch (Exception e){
			logger.error("?????????????????????????????????:{}"+JSON.toJSONString(e));
			return null;
		}
	}



	/**
	 * @Desc: ????????????/???????????????????????????
	 * @param:
	 * @return:
	 * @Author: lunan
	 * @Date: 2018/8/30
	 */
	@SuppressWarnings("rawtypes")
	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="driver-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE ),
			@MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
	} )
	public PageDTO selectTeamExistsDriverList(DriverTeamRequest driverTeamRequest){
		logger.info("??????????????????????????????:{}"+JSON.toJSONString(driverTeamRequest));
		try{
			if(Check.NuNObj(driverTeamRequest)){
				return null;
			}
			CarDriverTeam carDriverTeam = carDriverTeamMapper.selectByPrimaryKey(driverTeamRequest.getId());
			//???????????????????????????
			if(Check.NuNObj(carDriverTeam)){
				logger.info("?????????/??????"+ driverTeamRequest.getTeamId()+"???????????????");
				return null;
			}
			TeamGroupRequest teamGroupRequest = new TeamGroupRequest();
			List<CarRelateGroup> groups = new ArrayList<>();
			List<CarRelateTeam> teams = new ArrayList<>();
			Set<String> driverIds = new HashSet<>();
			if(!Check.NuNObj(carDriverTeam.getpId())){
				//??????pid ?????????
				teamGroupRequest.setGroupId(String.valueOf(driverTeamRequest.getTeamId()));
				groups = carRelateGroupExMapper.queryDriverGroupRelationList(teamGroupRequest);
			}else{
				teamGroupRequest.setTeamId(String.valueOf(driverTeamRequest.getTeamId()));
				teams = carRelateTeamExMapper.queryDriverTeamRelationList(teamGroupRequest);
			}
			if(Check.NuNCollection(groups) && Check.NuNCollection(teams)){
				logger.info("?????????/??????"+ driverTeamRequest.getTeamId()+"???????????????");
				return null;
			}else if(Check.NuNCollection(groups)){
				driverIds = citySupplierTeamCommonService.dealDriverids(teams,CarRelateTeam.class,"driverId");
			}else if(Check.NuNCollection(teams)){
				driverIds = citySupplierTeamCommonService.dealDriverids(groups,CarRelateGroup.class,"driverId");
			}else{
				logger.info("?????????/??????"+ driverTeamRequest.getTeamId()+"??????????????????");
				return null;
			}
			if(Check.NuNCollection(driverIds)){
				logger.info("?????????/??????"+ driverTeamRequest.getTeamId()+"???????????????");
				return null;
			}
			//??????????????????/????????????Id??????????????????
			driverTeamRequest.setDriverIds(driverIds);
			PageInfo<CarDriverInfoDTO> pageInfo = PageHelper.startPage(driverTeamRequest.getPageNo(), driverTeamRequest.getPageSize(), true).doSelectPageInfo(()
					-> carBizDriverInfoExMapper.selectDriverList(driverTeamRequest));
			List<CarDriverInfoDTO> list = pageInfo.getList();
			if(Check.NuNCollection(list)){
				logger.info("?????????/??????"+ driverTeamRequest.getTeamId()+"??????????????????");
				return null;
			}
			for (CarDriverInfoDTO carDriverInfoDTO : list) {
				carDriverInfoDTO.setTeamId(String.valueOf(driverTeamRequest.getTeamId()));
				carDriverInfoDTO.setTeamName(driverTeamRequest.getTeamName());
				carDriverInfoDTO.setPid(String.valueOf(driverTeamRequest.getpId()));
			}
			PageDTO pageDTO = new PageDTO();
			pageDTO.setResult(list);
			pageDTO.setTotal((int)pageInfo.getTotal());
			return pageDTO;
		}catch (Exception e){
			logger.error("????????????/??????????????????????????????:{}"+JSON.toJSONString(e));
			return null;
		}
	}



	/**
	 * @Desc: ??????????????????
	 * @param:
	 * @return:
	 * @Author: lunan
	 * @Date: 2018/8/30
	 */
	@SuppressWarnings("rawtypes")
	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="driver-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE ),
			@MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
	} )
	public CarDriverTeamDTO selectOneDriverTeam(DriverTeamRequest driverTeamRequest){
		if(Check.NuNObj(driverTeamRequest) || Check.NuNObj(driverTeamRequest.getId())){
			return null;
		}
		try{
			CarDriverTeamDTO dto = new CarDriverTeamDTO();
			//???????????????????????????????????????????????????
			/*if(StringUtils.isNotEmpty(driverTeamRequest.getTeamName())){
				CarDriverTeam carDriverTeam = carDriverTeamExMapper.selectByCondition(driverTeamRequest);
				if(Check.NuNObj(carDriverTeam)){
					return null;
				}
				BeanUtils.copyProperties(dto,carDriverTeam);
				return dto;
			}*/
			//??????????????????????????????
			CarDriverTeam carDriverTeam = carDriverTeamMapper.selectByPrimaryKey(driverTeamRequest.getId());
			if(Check.NuNObj(carDriverTeam)){
				return null;
			}
			BeanUtils.copyProperties(dto,carDriverTeam);
			//????????????????????? ??????????????????????????????
			Set<Integer> resultOfcityIds        = new HashSet<Integer>();
			resultOfcityIds.add(Integer.valueOf(carDriverTeam.getCity()));
			Set<Integer> resultOfsupplierIds = new HashSet<Integer>();
			resultOfsupplierIds.add(Integer.valueOf(carDriverTeam.getSupplier()));
			Map<Integer, CarBizCity>     cityMapping     = carBizCityService.queryCity( resultOfcityIds );
			Map<Integer, CarBizSupplier> supplierMapping = carBizSupplierService.querySupplier(null, resultOfsupplierIds);
			dto.setCityName(cityMapping.get(Integer.valueOf(carDriverTeam.getCity())).getCityName());
			dto.setCityName(supplierMapping.get(Integer.valueOf(carDriverTeam.getSupplier())).getSupplierFullName());
			return dto;
		}catch (Exception e){
			logger.error("????????????????????????:{}"+JSON.toJSONString(e));
			return null;
		}
	}

	/**
	 * @Desc: ????????????
	 * @param:
	 * @return:
	 * @Author: lunan
	 * @Date: 2018/8/30
	 */
	public int updateOneDriverTeam(CarDriverTeamDTO paramDto){
		CarDriverTeam existsTeam = null;
		try{
			DynamicRoutingDataSource.DataSourceMode mdbcarManageMode = DynamicRoutingDataSource.getMasterSlave("mdbcarmanage-DataSource");
			logger.info("-updateOneDriverTeam-mdb-datasource-first1={}",DynamicRoutingDataSource.getMasterSlave("mdbcarmanage-DataSource"));
			try{
				DynamicRoutingDataSource.setMasterSlave("mdbcarmanage-DataSource", DynamicRoutingDataSource.DataSourceMode.SLAVE);
				existsTeam = carDriverTeamMapper.selectByPrimaryKey(paramDto.getId());
				logger.info("-updateOneDriverTeam-mdb-datasource-second2={}",DynamicRoutingDataSource.getMasterSlave("mdbcarmanage-DataSource"));
			}finally {
				DynamicRoutingDataSource.setMasterSlave("mdbcarmanage-DataSource",mdbcarManageMode);
				logger.info("-updateOneDriverTeam-mdb-datasource-third3={}",DynamicRoutingDataSource.getMasterSlave("mdbcarmanage-DataSource"));
			}
			if(Check.NuNObj(existsTeam)){
				return ServiceReturnCodeEnum.NONE_RECODE_EXISTS.getCode();
			}
			if(existsTeam != null && StringUtils.isNotEmpty(existsTeam.getSupplier())){
				paramDto.setSupplier(existsTeam.getSupplier());
			}
			if(existsTeam != null && StringUtils.isNotEmpty(existsTeam.getTeamName())){
				paramDto.setTeamName(existsTeam.getTeamName());
			}
			//??????????????????
			if(paramDto.getOpenCloseFlag() !=0 && !paramDto.getOpenCloseFlag().equals(existsTeam.getStatus())){
				existsTeam.setStatus(paramDto.getOpenCloseFlag());
				//????????????????????????????????????mq ??????????????????????????????????????????????????????????????????????????????
				if(existsTeam != null && existsTeam.getpId() != null  && existsTeam.getpId() > 0){
					paramDto.setpId(existsTeam.getpId());

					List<CarRelateGroup> groups = new ArrayList<>();
					TeamGroupRequest teamGroupRequest = new TeamGroupRequest();
					teamGroupRequest.setGroupId(String.valueOf(existsTeam.getId()));
					groups = carRelateGroupExMapper.queryDriverGroupRelationList(teamGroupRequest);
					if(CollectionUtils.isNotEmpty(groups)){
						for (CarRelateGroup carRelateGroup : groups){
							try {
								// ?????????????????????key
								carBizDriverInfoService.flashDriverInfo(carRelateGroup.getDriverId());
							} catch (Exception e) {
								logger.info("??????driverId={},??????,????????????????????????={}", carRelateGroup.getDriverId(), e.getMessage());
							}
							/**????????????????????????????????????????????????*/
							List<Integer> groupDriverIds = carRelateGroupExMapper.queryDriverIdsByGroupId(carRelateGroup.getGroupId());
							if(CollectionUtils.isNotEmpty(groupDriverIds) && paramDto.getOpenCloseFlag() == 2){
								/**???????????????????????? ????????? ?????????????????????*/
								int code = 0;
								ReentrantLock lock = new ReentrantLock();
								lock.lock();
								try {
									 code = carRelateGroupExMapper.deleteByGroupId(carRelateGroup.getGroupId());
									logger.info("====????????????success===" + code);
									Thread.sleep(1000);
								} catch (Exception e) {
									e.printStackTrace();
								}finally {
									lock.unlock();
								}

								if(code > 0){
									carRelateTeamExMapper.batchInsertDriverIds(existsTeam.getpId(),groupDriverIds);
								}
							}
							//???????????????????????????
							this.asyncDutyService.processingData(carRelateGroup.getDriverId(), existsTeam.getpId().toString(), "", 0);
						}
					}
				}else {
					List<CarRelateTeam> teams = new ArrayList<>();
					TeamGroupRequest teamGroupRequest = new TeamGroupRequest();
					teamGroupRequest.setTeamId(String.valueOf(existsTeam.getId()));
					teams = carRelateTeamExMapper.queryDriverTeamRelationList(teamGroupRequest);
					if(CollectionUtils.isNotEmpty(teams)){
						for(CarRelateTeam carRelateTeam : teams){
							try {
								// ?????????????????????key
								carBizDriverInfoService.flashDriverInfo(carRelateTeam.getDriverId());
							} catch (Exception e) {
								logger.info("??????driverId={},??????,????????????????????????={}", carRelateTeam.getDriverId(), e.getMessage());
							}

							/**????????????????????????*/
							if(paramDto.getOpenCloseFlag() == 2){
								try {
									carRelateTeamExMapper.deleteByTeamId(carRelateTeam.getTeamId());
								} catch (Exception e) {
									logger.error("========????????????====",e);
								}
							}

							//????????????????????????????????????
							this.asyncDutyService.processingData(carRelateTeam.getDriverId(), "", "", 0);
						}
					}
				}
				int result = carDriverTeamMapper.updateByPrimaryKeySelective(existsTeam);;
				try{
					driverWideMongoService.updateTeamNameAndTeamGrpupName(existsTeam,paramDto);
				}catch (Exception e){
					logger.error("??????????????????mongodb??????:{}",e);
				}
				return result;
			}else if(paramDto.getOpenCloseFlag() !=0 && paramDto.getOpenCloseFlag().equals(existsTeam.getStatus())){
				existsTeam.setStatus(paramDto.getOpenCloseFlag());
				if(existsTeam != null && existsTeam.getpId() != null){
					paramDto.setpId(existsTeam.getpId());
				}
				return ServiceReturnCodeEnum.DEAL_SUCCESS.getCode();
			}
			existsTeam.setUpdateDate(new Date());
			existsTeam.setUpdateBy(String.valueOf(WebSessionUtil.getCurrentLoginUser().getId()));
			existsTeam.setCharge1(paramDto.getCharge1());
			existsTeam.setCharge2(paramDto.getCharge2());
			existsTeam.setCharge3(paramDto.getCharge3());
			existsTeam.setRemark(paramDto.getRemark());
			existsTeam.setShortName(paramDto.getShortName());
			int result = carDriverTeamMapper.updateByPrimaryKeySelective(existsTeam);
			try{
				driverWideMongoService.updateTeamNameAndTeamGrpupName(existsTeam,paramDto);
			}catch (Exception e){
				logger.error("??????????????????mongodb??????:{}",e);
			}
			return result;
		}catch (Exception e){
			logger.error("??????????????????!", e );
			return ServiceReturnCodeEnum.DEAL_FAILURE.getCode();
		}
	}



	/**
	 * @Desc:  ????????????1113
	 * @param:
	 * @return:
	 * @Author: lunan
	 * @Date: 2018/8/30
	 */
	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.MASTER )
	} )
	public int saveOneDriverTeam(CarDriverTeamDTO paramDto){
		if(Check.NuNObj(paramDto) || Check.NuNStr(paramDto.getCity()) || Check.NuNStr(paramDto.getSupplier())){
			return ServiceReturnCodeEnum.DEAL_FAILURE.getCode();
		}
		try{
			DriverTeamRequest driverTeamRequest = new DriverTeamRequest();
			driverTeamRequest.setTeamName(paramDto.getTeamName());
			DynamicRoutingDataSource.DataSourceMode mdbcarManageMode = DynamicRoutingDataSource.getMasterSlave("mdbcarmanage-DataSource");
			logger.info("-saveOneDriverTeam-mdb-datasource-first1={}",DynamicRoutingDataSource.getMasterSlave("mdbcarmanage-DataSource"));
			CarDriverTeam carDriverTeam = null;
			try{
				DynamicRoutingDataSource.setMasterSlave("mdbcarmanage-DataSource", DynamicRoutingDataSource.DataSourceMode.SLAVE);
				logger.info("-saveOneDriverTeam-mdb-datasource-second2={}",DynamicRoutingDataSource.getMasterSlave("mdbcarmanage-DataSource"));
				carDriverTeam = carDriverTeamExMapper.selectByCondition(driverTeamRequest);
			}finally {
				DynamicRoutingDataSource.setMasterSlave("mdbcarmanage-DataSource",mdbcarManageMode);
				logger.info("-saveOneDriverTeam-mdb-datasource-third3={}",DynamicRoutingDataSource.getMasterSlave("mdbcarmanage-DataSource"));
			}

//			CarDriverTeamDTO carDriverTeamDTO = this.selectOneDriverTeam(driverTeamRequest);
			if(!Check.NuNObj(carDriverTeam)){
				return ServiceReturnCodeEnum.RECODE_EXISTS.getCode();
			}
			CarDriverTeam record = new CarDriverTeam();
			record.setSupplier(paramDto.getSupplier());
			record.setCity(paramDto.getCity());
			record.setCreateDate(new Date());
			record.setCreateBy(String.valueOf(WebSessionUtil.getCurrentLoginUser().getId()));
			if(!Check.NuNObj(paramDto.getpId())){
				record.setpId(paramDto.getpId());
			}
			record.setTeamName(paramDto.getTeamName());
			if(Check.NuNObj(paramDto.getStatus())){
				record.setStatus(1);
			}
			record.setCharge1(paramDto.getCharge1());
			record.setCharge2(paramDto.getCharge2());
			record.setCharge3(paramDto.getCharge3());
			record.setRemark(paramDto.getRemark());
//			BeanUtils.copyProperties(record,paramDto);
			record.setCreateBy(String.valueOf(WebSessionUtil.getCurrentLoginUser().getId()));
			record.setShortName(paramDto.getShortName());
			int code = carDriverTeamMapper.insertSelective(record);
			if(code > 0){
				//???????????????????????????
				paramDto.setId(record.getId());
			}
			return code;
		}catch (Exception e){
			logger.error("??????????????????!", e );
			return ServiceReturnCodeEnum.DEAL_FAILURE.getCode();
		}
	}


	/**
	 * @Desc: ??????????????????
	 * @param:
	 * @return:
	 * @Author: lunan
	 * @Date: 2018/8/29
	 */
	@SuppressWarnings("rawtypes")
	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="driver-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE ),
			@MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
	} )
	public PageDTO queryDriverTeamPage(DriverTeamRequest driverTeamRequest) {
		if(Check.NuNObj(driverTeamRequest)){
			return new PageDTO();
		}
		logger.info("??????????????????:{}"+ JSON.toJSONString(driverTeamRequest));

		CommonRequest paramRequest = new CommonRequest();
		BeanUtils.copyProperties(driverTeamRequest,paramRequest);

		final CommonRequest commonRequest = citySupplierTeamCommonService.paramDeal(paramRequest);
		if(Check.NuNObj(commonRequest)){
			return new PageDTO();
		}
		//B----------------------------------------------------------------------------------??????????????????
		PageDTO pageDTO = new PageDTO();
		int total = 0;
		List<CarDriverTeamDTO> driverTeams = null;
		PageInfo<CarDriverTeamDTO> pageInfo =null;
		try{
			pageInfo =
					PageHelper.startPage(driverTeamRequest.getPageNo(), driverTeamRequest.getPageSize(), true)
							.doSelectPageInfo(() -> carDriverTeamExMapper.queryDriverTeamAndGroup(commonRequest.getCityIds(), commonRequest.getSupplierIds(), commonRequest.getTeamIds()));
//			driverTeams = carDriverTeamExMapper.queryDriverTeam(cityIds, supplierIds, teamIds);
			driverTeams = pageInfo.getList();
			total = (int)pageInfo.getTotal();
		}catch (Exception e){
			logger.error("??????????????????:{}"+JSON.toJSONString(e));
			return new PageDTO();
		}finally {
			PageHelper.clearPage();
			//System.gc();
		}
		if(driverTeams==null || driverTeams.size()==0) {
			return new PageDTO();
		}

		//C----------------------------------------------------------------------------------????????????????????????DTO??????????????????????????????????????????

		//????????????????????? ??????????????????????????????
		Set<Integer> resultOfcityIds        = new HashSet<Integer>();
		Set<Integer> resultOfsupplierIds = new HashSet<Integer>();
		for(CarDriverTeamDTO dto : driverTeams) {
			if( StringUtils.isNotEmpty( dto.getCity() )) {
				resultOfcityIds.add( Integer.valueOf(dto.getCity()) ) ;
			}
			if( StringUtils.isNotEmpty( dto.getSupplier() )) {
				resultOfsupplierIds.add( Integer.valueOf(dto.getSupplier()) ) ;
			}
		}
		Map<Integer, CarBizCity>     cityMapping     = carBizCityService.queryCity( resultOfcityIds );
		Map<Integer, CarBizSupplier> supplierMapping = carBizSupplierService.querySupplier(null, resultOfsupplierIds);
		//????????????????????????????????????
		for(CarDriverTeamDTO dto : driverTeams ) {
			if( StringUtils.isNotEmpty( dto.getCity() )) {
				CarBizCity city = cityMapping.get( Integer.valueOf(dto.getCity()));
				if(city!=null) {
					dto.setCityName(  city.getCityName() );
					List<CarDriverTeam> groups = dto.getGroups();
					if(!Check.NuNCollection(groups)){
						for (CarDriverTeam group : groups) {
							group.setCityName(city.getCityName());
						}
					}
				}
			}
			if( StringUtils.isNotEmpty( dto.getSupplier() )) {
				CarBizSupplier supplier = supplierMapping.get( Integer.valueOf(dto.getSupplier())  );
				if( supplier!=null ) {
					dto.setSupplierName( supplier.getSupplierFullName() );
					List<CarDriverTeam> groups = dto.getGroups();
					if(!Check.NuNCollection(groups)){
						for (CarDriverTeam group : groups) {
							group.setSupplierName(supplier.getSupplierFullName());
						}
					}
				}
			}
		}
		pageDTO.setResult(driverTeams);
		pageDTO.setTotal(total);
		return pageDTO;
	}

	/**
	 * ??????????????????ID??????????????????ID,
	 * ??????????????????ID??????????????????ID
	 * @param groupId
	 * @param teamId
	 * @param teamIds
	 * @return
	 */
	public Set<Integer> selectDriverIdsByTeamIdAndGroupId(Integer groupId, Integer teamId, Set<Integer> teamIds){
		Set<Integer> set = new HashSet<Integer>();
		if(groupId!=null){//??????????????????ID??????????????????ID??????
			List<Integer> integers = carRelateGroupExMapper.queryDriverIdsByGroupId(groupId);
			if(integers!=null && integers.size()>0){
				set = new HashSet<Integer>(integers);
				return set;
			}else {//???????????????ID????????????
				return set;
			}
		}else if(teamId!=null) {//?????????????????????ID????????????????????????ID??????????????????????????????????????????ID??????sijiID
			List<Integer> integers = carRelateTeamExMapper.queryDriverIdsByTeamId(teamId);
			if(integers!=null && integers.size()>0){
				set = new HashSet<Integer>(integers);
				return set;
			}else {//???????????????ID????????????
				return set;
			}
		}else if(teamIds !=null && teamIds.size()>0){
			List<Integer> integers = carRelateTeamExMapper.queryDriverIdsByTeamIdss(teamIds);
			if(integers!=null && integers.size()>0){
				set = new HashSet<Integer>(integers);
				return set;
			}else {//???????????????ID????????????
				return set;
			}
		}
		return set;
	}

	/**
	 * ?????????????????????Map
	 * @param cityId
	 * @param supplierid
	 * @return
	 */
	public Map<Integer, String> queryDriverTeamList( Integer cityId, Integer supplierid ){
		List<CarDriverTeam> list = carDriverTeamExMapper.queryDriverTeamList(cityId, supplierid);
		if(list==null||list.size()==0) {
			return new HashMap<Integer, String>(4);
		}
		Map<Integer, String> result = Maps.newHashMap();
		for(CarDriverTeam c : list) {
			result.put(c.getId(),  c.getTeamName());
		}
		return result;
	}

	/**
	 * ????????????ID?????????????????????
	 * @param driverIds
	 * @return
	 */
	public Map<Integer, String> queryDriverTeamListByDriverId(String driverIds){
		List<CarRelateTeam> list = carDriverTeamExMapper.queryDriverTeamListByDriverId(driverIds);
		if(list==null||list.size()==0) {
			return new HashMap<Integer, String>(4);
		}
		Map<Integer, String> result = Maps.newHashMap();
		for(CarRelateTeam c : list) {
			result.put(c.getDriverId(),  c.getTeamName());
		}
		return result;
	}

	/**
	 * ????????????ID?????????????????????
	 * @param driverIds
	 * @return
	 */
	public Map<Integer, String> queryDriverTeamGroupListByDriverId(String driverIds){
		List<CarRelateTeam> list = carDriverTeamExMapper.queryDriverTeamListByDriverId(driverIds);
		if(list==null||list.size()==0) {
			return new HashMap<Integer, String>(4);
		}
		Map<Integer, String> result = Maps.newHashMap();
		for(CarRelateTeam c : list) {
			result.put(c.getDriverId(),  c.getGroupName());
		}
		return result;
	}

 
	public Map<Integer, String> queryDriverTeamListByDriverId(List<String> driverIds) {
		if(driverIds==null||driverIds.size()==0) {
			return new HashMap<>(4);
		}
		List<CarRelateTeam> list = carDriverTeamExMapper.queryDriverTeamListByDriverIdList(driverIds);
		if(list==null||list.size()==0) {
			return new HashMap<>(4);
		}
		Map<Integer, String> result = Maps.newHashMap();
		for(CarRelateTeam c : list) {
			result.put(c.getDriverId(),  c.getTeamName());
		}
		return result;
	}

	public Map<Integer, String> queryDriverTeamGroupListByDriverId(List<String> driverIds) {
		if(driverIds==null||driverIds.size()==0) {
			return new HashMap<>(4);
		}
		List<CarRelateTeam> list = carDriverTeamExMapper.queryDriverTeamListByDriverIdList(driverIds);
		if(list==null||list.size()==0) {
			return new HashMap<>(4);
		}
		Map<Integer, String> result = Maps.newHashMap();
		for(CarRelateTeam c : list) {
			result.put(c.getDriverId(),  c.getGroupName());
		}
		return result;
	}
	public List<Integer> queryDriverIdsByTeamIdss(Set<Integer> teamIds){
		return carRelateTeamExMapper.queryDriverIdsByTeamIdss(  teamIds);

	}
}