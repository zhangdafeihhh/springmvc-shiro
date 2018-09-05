package com.zhuanche.serv.driverteam;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.dutyEnum.ServiceReturnCodeEnum;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.dto.CarDriverInfoDTO;
import com.zhuanche.entity.mdbcarmanage.CarRelateGroup;
import com.zhuanche.entity.mdbcarmanage.CarRelateTeam;
import com.zhuanche.request.DriverTeamRequest;
import com.zhuanche.request.CommonRequest;
import com.zhuanche.request.TeamGroupRequest;
import com.zhuanche.serv.CarBizCityService;
import com.zhuanche.serv.CarBizSupplierService;
import com.zhuanche.serv.common.CitySupplierTeamCommonService;
import com.zhuanche.util.Check;
import mapper.mdbcarmanage.CarDriverTeamMapper;
import mapper.mdbcarmanage.CarRelateGroupMapper;
import mapper.mdbcarmanage.CarRelateTeamMapper;
import mapper.mdbcarmanage.ex.CarRelateGroupExMapper;
import mapper.mdbcarmanage.ex.CarRelateTeamExMapper;
import mapper.rentcar.ex.CarBizDriverInfoExMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.zhuanche.dto.CarDriverTeamDTO;
import com.zhuanche.entity.mdbcarmanage.CarDriverTeam;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.serv.common.DataPermissionHelper;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;

import mapper.mdbcarmanage.ex.CarDriverTeamExMapper;

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

	/**
	* @Desc: 添加司机到车队/小组
	* @param:
	* @return:
	* @Author: lunan
	* @Date: 2018/8/31
	*/
	public int addDriverToTeam(DriverTeamRequest driverTeamRequest){

		logger.info("添加司机到车队/小组入参:{}"+JSON.toJSONString(driverTeamRequest));
		if(Check.NuNObj(driverTeamRequest) || Check.NuNObj(driverTeamRequest.getTeamId()) || Check.NuNStr(driverTeamRequest.getPlates())){
			logger.info("添加司机到车队/小组参数无效");
			return ServiceReturnCodeEnum.DEAL_FAILURE.getCode();
		}
		CarDriverTeam carDriverTeam = carDriverTeamMapper.selectByPrimaryKey(driverTeamRequest.getTeamId());
		if(Check.NuNObj(carDriverTeam)){
			logger.info("车队/小组不存在");
			return ServiceReturnCodeEnum.DEAL_FAILURE.getCode();
		}
		String[] licenses = driverTeamRequest.getPlates().split(",");
		int result = 0;
		CarRelateGroup group = new CarRelateGroup();
		CarRelateTeam team = new CarRelateTeam();
		for(int i= 0; i < licenses.length; i++) {
			driverTeamRequest.setLicense(licenses[i]);
			String driverId = carBizDriverInfoExMapper.queryListByLimits(driverTeamRequest).get(0).getDriverId();
			if(!Check.NuNObj(driverTeamRequest.getpId())){
				//小组
				group.setGroupId(driverTeamRequest.getpId());
				group.setDriverId(Integer.valueOf(driverId));
				CarRelateGroup existsGroup = carRelateGroupExMapper.selectOneGroup(group);
				if(Check.NuNObj(existsGroup)){
					result += carRelateGroupMapper.insertSelective(group);
				}
			}else{
				team.setTeamId(driverTeamRequest.getTeamId());
				team.setDriverId(Integer.valueOf(driverId));
				CarRelateTeam existsTeam = carRelateTeamExMapper.selectOneTeam(team);
				if(Check.NuNObj(existsTeam)){
					result += carRelateTeamMapper.insertSelective(team);
				}
			}
		}
		//TODO 处理司机ID，发动司机变更MQ 从车队新增司机 driverId; driverTeam.getTeamId();driverTeam.getTeamName()
		//driverService.processingData(Integer.parseInt(driverId), driverTeam.getTeamId(), driverTeam.getTeamName(), 2);
		return result;
	}

	/** 
	* @Desc: 查询可添加司机列表 
	* @param:
	* @return:  
	* @Author: lunan
	* @Date: 2018/8/31 
	*/ 
	public PageDTO selectAddDriverList(DriverTeamRequest driverTeamRequest){
		logger.info("请求可添加司机列表入参:{}"+JSON.toJSONString(driverTeamRequest));
		if(Check.NuNObj(driverTeamRequest) || Check.NuNObj(driverTeamRequest.getTeamId())){
			return null;
		}
		try{
			TeamGroupRequest teamGroupRequest = new TeamGroupRequest();
			if(!Check.NuNObj(driverTeamRequest.getpId())){
				//小组逻辑
				//1.查询小组所属车队下关联列表
				teamGroupRequest.setTeamId(String.valueOf(driverTeamRequest.getpId()));
				List<CarRelateTeam> carRelateTeams = carRelateTeamExMapper.queryDriverTeamRelationList(teamGroupRequest);

				if(Check.NuNCollection(carRelateTeams)){
					logger.info("该车队/小组"+ driverTeamRequest.getTeamId()+"下没有司机");
					return null;
				}
				Set<String> driverIdsAll = citySupplierTeamCommonService.dealDriverids(carRelateTeams, CarRelateTeam.class);
				//2.查询小组所属车队下其他小组以及绑定的司机关联
				List<CarDriverTeam> carDriverTeams = carDriverTeamExMapper.queryForListByPid(driverTeamRequest);
				//2.1 车队有几个小组，查询小组下绑定的司机
				Set<String> teamIds = citySupplierTeamCommonService.dealDriverids(carDriverTeams,CarDriverTeam.class);
				teamGroupRequest.setGroupIds(teamIds);
				List<CarRelateGroup> carRelateGroups = carRelateGroupExMapper.queryDriverGroupRelationList(teamGroupRequest);
				Set<String> existsGroups = citySupplierTeamCommonService.dealDriverids(carRelateGroups, CarRelateGroup.class);
				//去掉其他小组绑定的司机
				driverIdsAll.removeAll(existsGroups);
				if(Check.NuNCollection(driverIdsAll)){
					logger.info("该车队/小组"+ driverTeamRequest.getTeamId()+"下没有司机");
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
				//当前车队信息
				CarDriverTeam carDriverTeam = carDriverTeamMapper.selectByPrimaryKey(driverTeamRequest.getTeamId());
				//设置登录用户城市和供应商数据权限
				driverTeamRequest.setCityIds(BeanUtil.copySet(WebSessionUtil.getCurrentLoginUser().getCityIds(),String.class));
				driverTeamRequest.setSupplierIds(BeanUtil.copySet(WebSessionUtil.getCurrentLoginUser().getSupplierIds(),String.class));
				//查询车队上级供应商级别下司机列表
				List<CarDriverInfoDTO> limitsDrivers = carBizDriverInfoExMapper.queryListByLimits(driverTeamRequest);
				if(Check.NuNCollection(limitsDrivers)){
					logger.info("该车队/小组"+ driverTeamRequest.getTeamId()+"下没有司机");
					return null;
				}
				Set<String> driverIdsAll = citySupplierTeamCommonService.dealDriverids(limitsDrivers, CarDriverInfoDTO.class);
				//查询同级车队绑定了多少司机
				List<CarDriverTeam> carDriverTeams = carDriverTeamExMapper.queryDriverTeam(null, null, null);
				Set<String> teamIds = citySupplierTeamCommonService.dealDriverids(carDriverTeams,CarDriverTeam.class);
				teamGroupRequest.setTeamIds(teamIds);
				List<CarRelateTeam> carRelateTeams = carRelateTeamExMapper.queryDriverTeamRelationList(teamGroupRequest);
				Set<String> teamDrivers = citySupplierTeamCommonService.dealDriverids(carRelateTeams, CarRelateTeam.class);
				//去掉其他车队绑定的司机
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
			logger.error("查询可添加司机列表异常:{}",e);
			return null;
		}
	}



	/** 
	* @Desc: 查询车队/小组已存在司机列表
	* @param:  
	* @return:  
	* @Author: lunan
	* @Date: 2018/8/30 
	*/ 
	public PageDTO selectTeamExistsDriverList(DriverTeamRequest driverTeamRequest){
		logger.info("请求车队司机列表入参:{}"+JSON.toJSONString(driverTeamRequest));
		try{
			if(Check.NuNObj(driverTeamRequest)){
				return null;
			}
			CarDriverTeam carDriverTeam = carDriverTeamMapper.selectByPrimaryKey(driverTeamRequest.getId());
			//判断是小组还是车队
			if(Check.NuNObj(carDriverTeam)){
				logger.info("该车队/小组"+ driverTeamRequest.getTeamId()+"下没有司机");
				return null;
			}
			TeamGroupRequest teamGroupRequest = new TeamGroupRequest();
			List<CarRelateGroup> groups = new ArrayList<>();
			List<CarRelateTeam> teams = new ArrayList<>();
			Set<String> driverIds = new HashSet<>();
			if(!Check.NuNObj(carDriverTeam.getpId())){
				//包含pid 是小组
				teamGroupRequest.setGroupId(String.valueOf(driverTeamRequest.getTeamId()));
				groups = carRelateGroupExMapper.queryDriverGroupRelationList(teamGroupRequest);
			}else{
				teamGroupRequest.setTeamId(String.valueOf(driverTeamRequest.getTeamId()));
				teams = carRelateTeamExMapper.queryDriverTeamRelationList(teamGroupRequest);
			}
			if(Check.NuNCollection(groups) && Check.NuNCollection(teams)){
				logger.info("该车队/小组"+ driverTeamRequest.getTeamId()+"下没有司机");
				return null;
			}else if(Check.NuNCollection(groups)){
				driverIds = citySupplierTeamCommonService.dealDriverids(teams,CarRelateTeam.class);
			}else if(Check.NuNCollection(teams)){
				driverIds = citySupplierTeamCommonService.dealDriverids(groups,CarRelateGroup.class);
			}else{
				logger.info("该车队/小组"+ driverTeamRequest.getTeamId()+"司机信息异常");
				return null;
			}
			if(Check.NuNCollection(driverIds)){
				logger.info("该车队/小组"+ driverTeamRequest.getTeamId()+"下没有司机");
				return null;
			}
			//设置属于车队/小组司机Id查询司机信息
			driverTeamRequest.setDriverIds(driverIds);
			PageInfo<CarDriverInfoDTO> pageInfo = PageHelper.startPage(driverTeamRequest.getPageNo(), driverTeamRequest.getPageSize(), true).doSelectPageInfo(()
					-> carBizDriverInfoExMapper.selectDriverList(driverTeamRequest));
			List<CarDriverInfoDTO> list = pageInfo.getList();
			if(Check.NuNCollection(list)){
				logger.info("该车队/小组"+ driverTeamRequest.getTeamId()+"司机信息异常");
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
			logger.error("查询车队/小组司机已有司机异常:{}",e);
			return null;
		}
	}
	


	/**
	* @Desc: 查询车队详情
	* @param:
	* @return:
	* @Author: lunan
	* @Date: 2018/8/30
	*/
	public CarDriverTeamDTO selectOneDriverTeam(DriverTeamRequest driverTeamRequest){
		if(Check.NuNObj(driverTeamRequest) || Check.NuNObj(driverTeamRequest.getId())){
			return null;
		}
		try{
			CarDriverTeamDTO dto = new CarDriverTeamDTO();
			//根据名称查询是否存在，新增使用判断
			if(StringUtils.isNotEmpty(driverTeamRequest.getTeamName())){
				CarDriverTeam carDriverTeam = carDriverTeamExMapper.selectByCondition(driverTeamRequest);
				if(Check.NuNObj(carDriverTeam)){
					return null;
				}
				BeanUtils.copyProperties(dto,carDriverTeam);
				return dto;
			}
			//正常查询详情业务逻辑
			CarDriverTeam carDriverTeam = carDriverTeamMapper.selectByPrimaryKey(driverTeamRequest.getId());
			if(Check.NuNObj(carDriverTeam)){
				return null;
			}
			BeanUtils.copyProperties(dto,carDriverTeam);
			//查询此结果中的 城市信息和供应商信息
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
			logger.error("查询车队详情失败:{}",e);
			return null;
		}
	}

	/** 
	* @Desc: 修改车队 
	* @param:
	* @return:  
	* @Author: lunan
	* @Date: 2018/8/30 
	*/ 
	public int updateOneDriverTeam(CarDriverTeamDTO paramDto){
		if(Check.NuNObj(paramDto)){
			return ServiceReturnCodeEnum.DEAL_FAILURE.getCode();
		}
		try{
			CarDriverTeam existsTeam = carDriverTeamMapper.selectByPrimaryKey(paramDto.getId());
			if(Check.NuNObj(existsTeam)){
				return ServiceReturnCodeEnum.NONE_RECODE_EXISTS.getCode();
			}
			//开启关闭逻辑
			if(paramDto.getOpenCloseFlag() !=0 && paramDto.getStatus() != existsTeam.getStatus()){
				existsTeam.setStatus(paramDto.getStatus());
				return carDriverTeamMapper.updateByPrimaryKeySelective(existsTeam);
			}else if(paramDto.getOpenCloseFlag() !=0 && paramDto.getStatus() == existsTeam.getStatus()){
				return ServiceReturnCodeEnum.DEAL_SUCCESS.getCode();
			}
			existsTeam.setCharge1(paramDto.getCharge1());
			existsTeam.setCharge2(paramDto.getCharge2());
			existsTeam.setCharge3(paramDto.getCharge3());
			existsTeam.setRemark(paramDto.getRemark());
			return carDriverTeamMapper.updateByPrimaryKeySelective(existsTeam);
		}catch (Exception e){
			logger.error("更新车队失败:{}",e);
			return ServiceReturnCodeEnum.DEAL_FAILURE.getCode();
		}
	}

	/**
	* @Desc:  新增车队
	* @param:
	* @return:
	* @Author: lunan
	* @Date: 2018/8/30
	*/
	public int saveOneDriverTeam(CarDriverTeamDTO paramDto){
		if(Check.NuNObj(paramDto)){
			return ServiceReturnCodeEnum.DEAL_FAILURE.getCode();
		}
		try{
			DriverTeamRequest driverTeamRequest = new DriverTeamRequest();
			driverTeamRequest.setTeamName(paramDto.getTeamName());
			CarDriverTeamDTO carDriverTeamDTO = this.selectOneDriverTeam(driverTeamRequest);
			if(!Check.NuNObj(carDriverTeamDTO)){
				return ServiceReturnCodeEnum.RECODE_EXISTS.getCode();
			}
			CarDriverTeam record = new CarDriverTeam();
			BeanUtils.copyProperties(record,paramDto);
			record.setCreateBy(String.valueOf(WebSessionUtil.getCurrentLoginUser().getId()));
			return carDriverTeamMapper.insertSelective(record);
		}catch (Exception e){
			logger.error("新增车队失败:{}",e);
			return ServiceReturnCodeEnum.DEAL_FAILURE.getCode();
		}
	}


	/**
	* @Desc: 查询车队列表 
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
			return null;
		}
		logger.info("查询车队入参:{}"+ JSON.toJSONString(driverTeamRequest));

		CommonRequest paramRequest = new CommonRequest();
		BeanUtils.copyProperties(driverTeamRequest,paramRequest);

		final CommonRequest commonRequest = citySupplierTeamCommonService.paramDeal(paramRequest);
		//B----------------------------------------------------------------------------------进行分页查询
		PageDTO pageDTO = new PageDTO();
		int total = 0;
		List<CarDriverTeam> driverTeams = null;
		PageInfo<CarDriverTeam> pageInfo =null;
		try{
			pageInfo =
					PageHelper.startPage(driverTeamRequest.getPageNo(), driverTeamRequest.getPageSize(), true).doSelectPageInfo(()
							-> carDriverTeamExMapper.queryDriverTeam(commonRequest.getCityIds(), commonRequest.getSupplierIds(), commonRequest.getTeamIds()));
//			driverTeams = carDriverTeamExMapper.queryDriverTeam(cityIds, supplierIds, teamIds);
			driverTeams = pageInfo.getList();
			total = (int)pageInfo.getTotal();
		}catch (Exception e){
			logger.error("查询车队异常:{}",e);
			return new PageDTO();
		}finally {
			PageHelper.clearPage();
			System.gc();
		}
		if(driverTeams==null || driverTeams.size()==0) {
			return new PageDTO();
		}

		//C----------------------------------------------------------------------------------将分页结果转换为DTO，并填全城市名称、供应商名称
		List<CarDriverTeamDTO> dtos = BeanUtil.copyList(driverTeams, CarDriverTeamDTO.class);
		//查询此结果中的 城市信息和供应商信息
		Set<Integer> resultOfcityIds        = new HashSet<Integer>();
		Set<Integer> resultOfsupplierIds = new HashSet<Integer>();
		for(CarDriverTeamDTO dto : dtos) {
			if( StringUtils.isNotEmpty( dto.getCity() )) {
				resultOfcityIds.add( Integer.valueOf(dto.getCity()) ) ;
			}
			if( StringUtils.isNotEmpty( dto.getSupplier() )) {
				resultOfsupplierIds.add( Integer.valueOf(dto.getSupplier()) ) ;
			}
		}
		Map<Integer, CarBizCity>     cityMapping     = carBizCityService.queryCity( resultOfcityIds );
		Map<Integer, CarBizSupplier> supplierMapping = carBizSupplierService.querySupplier(null, resultOfsupplierIds);
		//填充城市名称、供应商名称
		for(CarDriverTeamDTO dto : dtos ) {
			if( StringUtils.isNotEmpty( dto.getCity() )) {
				CarBizCity city = cityMapping.get( Integer.valueOf(dto.getCity()));
				if(city!=null) {
					dto.setCityName(  city.getCityName() );
				}
			}
			if( StringUtils.isNotEmpty( dto.getSupplier() )) {
				CarBizSupplier supplier = supplierMapping.get( Integer.valueOf(dto.getSupplier())  );
				if( supplier!=null ) {
					dto.setSupplierName( supplier.getSupplierFullName() );
				}
			}
		}
		pageDTO.setResult(dtos);
		pageDTO.setTotal(total);
		return pageDTO;
	}

	/**
	 * 查询所给小组ID下的所有司机ID,
	 * 查询所给车队ID下的所有司机ID
	 * @param groupId
	 * @param teamId
	 * @param teamIds
	 * @return
	 */
	public Set<Integer> selectDriverIdsByTeamIdAndGroupId(Integer groupId, Integer teamId, Set<Integer> teamIds){
		Set<Integer> set = new HashSet<Integer>();
		if(groupId!=null){//有车队下小组ID传入，故以此ID为主
			List<Integer> integers = carRelateGroupExMapper.queryDriverIdsByGroupId(groupId);
			if(integers!=null && integers.size()>0){
				set = new HashSet<Integer>(integers);
				return set;
			}else {//不存在司机ID，即返回
				return set;
			}
		}else {//没有车队下小组ID传入，以传入车队ID以及当前用户的数据权限下车队ID查询sijiID
			List<Integer> integers = carRelateTeamExMapper.queryDriverIdsByTeamId(teamId, teamIds);
			if(integers!=null && integers.size()>0){
				set = new HashSet<Integer>(integers);
				return set;
			}else {//不存在司机ID，即返回
				return set;
			}
		}
	}
}