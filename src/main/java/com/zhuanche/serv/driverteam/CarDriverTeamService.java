package com.zhuanche.serv.driverteam;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.dutyEnum.ServiceReturnCodeEnum;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.entity.mdbcarmanage.CarRelateGroup;
import com.zhuanche.entity.mdbcarmanage.CarRelateTeam;
import com.zhuanche.request.DutyRequest;
import com.zhuanche.request.SchCommonRequest;
import com.zhuanche.request.TeamGroupRequest;
import com.zhuanche.serv.CarBizCityService;
import com.zhuanche.serv.CarBizSupplierService;
import com.zhuanche.serv.common.CitySupplierTeamCommonService;
import com.zhuanche.util.Check;
import mapper.mdbcarmanage.CarDriverTeamMapper;
import mapper.mdbcarmanage.ex.CarRelateGroupExMapper;
import mapper.mdbcarmanage.ex.CarRelateTeamExMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhuanche.common.web.LayUIPage;
import com.zhuanche.dto.CarDriverTeamDTO;
import com.zhuanche.entity.mdbcarmanage.CarDriverTeam;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.serv.common.DataPermissionHelper;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;

import mapper.mdbcarmanage.ex.CarDriverTeamExMapper;
import mapper.rentcar.ex.CarBizCityExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
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

	/** 
	* @Desc: 查询
	* @param:  
	* @return:  
	* @Author: lunan
	* @Date: 2018/8/30 
	*/ 
	public List selectDriverList(DutyRequest dutyRequest){
		logger.info("请求车队司机列表入参:{}"+JSON.toJSONString(dutyRequest));
		try{
			if(Check.NuNObj(dutyRequest)){
				return null;
			}
			//TODO 数据权限
			CarDriverTeam carDriverTeam = carDriverTeamMapper.selectByPrimaryKey(dutyRequest.getId());
			//判断是小组还是车队
			if(Check.NuNObj(carDriverTeam)){
				logger.info("该车队/小组"+dutyRequest.getTeamId()+"下没有司机");
				return null;
			}
			TeamGroupRequest teamGroupRequest = new TeamGroupRequest();
			List<CarRelateGroup> groups = new ArrayList<>();
			List<CarRelateTeam> teams = new ArrayList<>();
			Set<String> driverIds = new HashSet<>();
			if(!Check.NuNObj(carDriverTeam.getpId())){
				//包含pid 是小组
				teamGroupRequest.setGroupId(String.valueOf(dutyRequest.getTeamId()));
				groups = carRelateGroupExMapper.queryDriverGroupRelationList(teamGroupRequest);
			}else{
				teamGroupRequest.setTeamId(String.valueOf(dutyRequest.getTeamId()));
				teams = carRelateTeamExMapper.queryDriverTeamRelationList(teamGroupRequest);
			}
			if(Check.NuNCollection(groups) && Check.NuNCollection(teams)){
				logger.info("该车队/小组"+dutyRequest.getTeamId()+"下没有司机");
				return null;
			}else if(Check.NuNCollection(groups)){
				driverIds = dealDriverids(teams,CarRelateTeam.class);
			}else if(Check.NuNCollection(teams)){
				driverIds = dealDriverids(groups,CarRelateGroup.class);
			}else{
				logger.info("该车队/小组"+dutyRequest.getTeamId()+"司机信息异常");
				return null;
			}
			if(Check.NuNCollection(driverIds)){
				logger.info("该车队/小组"+dutyRequest.getTeamId()+"下没有司机");
				return null;
			}


		}catch (Exception e){

		}


		return null;
	}

	public  <T> Set<String> dealDriverids(List srcList, Class<T> destClass){
		if(srcList==null){
			return null;
		}
		try{
			Set<String> driverIds = new HashSet<>();
			T param = destClass.newInstance();
			if(param instanceof CarRelateGroup){
				for(int i=0;i<srcList.size();i++ ){
					Object srcObj = srcList.get(i);
					CarRelateGroup data = new CarRelateGroup();
					BeanUtils.copyProperties(srcObj,data);
					driverIds.add(String.valueOf(data.getDriverId()));
				}
				return driverIds;
			}else{
				for(int i=0;i<srcList.size();i++ ){
					Object srcObj = srcList.get(i);
					CarRelateTeam data = new CarRelateTeam();
					BeanUtils.copyProperties(srcObj,data);
					driverIds.add(String.valueOf(data.getDriverId()));
				}
				return driverIds;
			}
		}catch(Exception e){
			logger.error("关联表分离driverid异常:{}",e);
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
	public CarDriverTeamDTO selectOneDriverTeam(DutyRequest dutyRequest){
		if(Check.NuNObj(dutyRequest) || Check.NuNObj(dutyRequest.getId())){
			return null;
		}
		try{
			CarDriverTeamDTO dto = new CarDriverTeamDTO();
			//根据名称查询是否存在，新增使用判断
			if(StringUtils.isNotEmpty(dutyRequest.getTeamName())){
				CarDriverTeam carDriverTeam = carDriverTeamExMapper.selectByCondition(dutyRequest);
				if(Check.NuNObj(carDriverTeam)){
					return null;
				}
				BeanUtils.copyProperties(dto,carDriverTeam);
				return dto;
			}
			//正常查询详情业务逻辑
			CarDriverTeam carDriverTeam = carDriverTeamMapper.selectByPrimaryKey(dutyRequest.getId());
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
			DutyRequest dutyRequest = new DutyRequest();
			dutyRequest.setTeamName(paramDto.getTeamName());
			CarDriverTeamDTO carDriverTeamDTO = this.selectOneDriverTeam(dutyRequest);
			if(!Check.NuNObj(carDriverTeamDTO)){
				return ServiceReturnCodeEnum.RECODE_EXISTS.getCode();
			}
			//TODO 获取登录用户id
			CarDriverTeam record = new CarDriverTeam();
			BeanUtils.copyProperties(record,paramDto);
			//TODO 设置创建者
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
	public PageDTO queryDriverTeamPage(DutyRequest dutyRequest) {
		if(Check.NuNObj(dutyRequest)){
			return null;
		}
		logger.info("查询车队入参:{}"+ JSON.toJSONString(dutyRequest));
		//----------------------------------------------------------------------------------首先，如果是普通管理员，校验数据权限（cityId、supplierId、teamId）
		Set<Integer> permOfCity        = new HashSet<Integer>();//普通管理员可以管理的所有城市ID
		Set<Integer> permOfSupplier = new HashSet<Integer>();//普通管理员可以管理的所有供应商ID
		Set<Integer> permOfTeam     = new HashSet<Integer>();//普通管理员可以管理的所有车队ID
		if(WebSessionUtil.isSupperAdmin() == false) {//如果是普通管理员
			permOfCity        = dataPermissionHelper.havePermOfCityIds("");
			permOfSupplier = dataPermissionHelper.havePermOfSupplierIds("");
			permOfTeam     = dataPermissionHelper.havePermOfDriverTeamIds("");
			if( permOfCity.size()==0 || (StringUtils.isNotEmpty(dutyRequest.getCityId())
					&& permOfCity.contains(Integer.valueOf(dutyRequest.getCityId()))==false)  ) {
				return null;
			}
			if( permOfSupplier.size()==0 || (StringUtils.isNotEmpty(dutyRequest.getSupplierId())
					&& permOfSupplier.contains(Integer.valueOf(dutyRequest.getSupplierId()))==false)   ) {
				return null;
			}
			if( permOfTeam.size()==0 || (dutyRequest.getTeamId() != null
					&& permOfTeam.contains(Integer.valueOf(dutyRequest.getTeamId())) == false )   ) {
//				return LayUIPage.build("您没有查询此车队的权限！", 0, null);
				return null;
			}
		}
		SchCommonRequest paramRequest = new SchCommonRequest();
		BeanUtils.copyProperties(dutyRequest,paramRequest);
		paramRequest.setPermOfCity(permOfCity);
		paramRequest.setPermOfSupplier(permOfSupplier);
		paramRequest.setPermOfTeam(permOfTeam);
		final SchCommonRequest schCommonRequest = citySupplierTeamCommonService.paramDeal(paramRequest);
		//B----------------------------------------------------------------------------------进行分页查询
		PageDTO pageDTO = new PageDTO();
		int total = 0;
		List<CarDriverTeam> driverTeams = null;
		PageInfo<CarDriverTeam> pageInfo =null;
		try{
			pageInfo =
					PageHelper.startPage(dutyRequest.getPageNo(), dutyRequest.getPageSize(), true).doSelectPageInfo(()
							-> carDriverTeamExMapper.queryDriverTeam(schCommonRequest.getCityIds(), schCommonRequest.getSupplierIds(), schCommonRequest.getTeamIds()));
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
}