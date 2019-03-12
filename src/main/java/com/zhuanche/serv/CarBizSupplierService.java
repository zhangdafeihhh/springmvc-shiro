package com.zhuanche.serv;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.rocketmq.CommonRocketProducer;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constant.Constants;
import com.zhuanche.dto.CarDriverInfoDTO;
import com.zhuanche.entity.driver.SupplierExtDto;
import com.zhuanche.entity.driver.TwoLevelCooperationDto;
import com.zhuanche.entity.rentcar.*;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.request.DriverTeamRequest;
import com.zhuanche.serv.deiver.CarBizCarInfoTempService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import mapper.driver.SupplierExtDtoMapper;
import mapper.driver.ex.SupplierExtDtoExMapper;
import mapper.driver.ex.TwoLevelCooperationExMapper;
import mapper.mdbcarmanage.ex.CarAdmUserExMapper;
import mapper.rentcar.CarBizSupplierMapper;
import mapper.rentcar.ex.CarBizCarGroupExMapper;
import mapper.rentcar.ex.CarBizDriverInfoExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**供应商信息 的 基本服务层**/
@Service
public class CarBizSupplierService{

	private static final Logger logger = LoggerFactory.getLogger(CarBizSupplierService.class);

	@Autowired
	private CarBizSupplierExMapper carBizSupplierExMapper;

	@Autowired
	private CarBizSupplierMapper carBizSupplierMapper;

	@Autowired
	private SupplierExtDtoMapper supplierExtDtoMapper;

	@Autowired
	private SupplierExtDtoExMapper supplierExtDtoExMapper;

	@Autowired
	private CarBizDriverInfoService carBizDriverInfoService;

	@Autowired
	private CarBizCarInfoTempService carBizCarInfoTempService;

	@Autowired
	private CarBizCarGroupExMapper carBizCarGroupExMapper;

	@Autowired
	private CarAdmUserExMapper carAdmUserExMapper;

	@Autowired
	private TwoLevelCooperationExMapper twoLevelCooperationExMapper;

	@Value("${commission.url}")
	String commissionUrl;

	@Autowired
	private CarBizDriverInfoExMapper carBizDriverInfoExMapper;

	/**查询供应商信息**/
	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="rentcar-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
	} )
	public Map<Integer, CarBizSupplier> querySupplier( Integer cityId,  Set<Integer> supplierids ){
        Set<Integer> cityIds = Sets.newHashSet();
        cityIds.add(cityId);
		List<CarBizSupplier> list = carBizSupplierExMapper.querySuppliers(cityIds, supplierids);
		if(list==null||list.size()==0) {
			return new HashMap<Integer, CarBizSupplier>(4);
		}
		Map<Integer, CarBizSupplier> result = new HashMap<Integer, CarBizSupplier>();
		for(CarBizSupplier c : list) {
			result.put(c.getSupplierId(),  c);
		}
		return result;
	}

	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="rentcar-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
	} )
    public CarBizSupplier queryForObject(CarBizSupplier carBizSupplier){
	    return carBizSupplierExMapper.queryForObject(carBizSupplier);
    }

	/**
	 * 根据供应商ID查询供应商信息
	 * @param supplierId
	 * @return
	 */
	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="rentcar-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
	} )
	public CarBizSupplier selectByPrimaryKey(Integer supplierId){
		return carBizSupplierMapper.selectByPrimaryKey(supplierId);
	}

	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="rentcar-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
	} )
    public List<CarBizSupplier> findByIdSet(Set<Integer> supplierIdSet) {
		return carBizSupplierExMapper.findByIdSet(supplierIdSet);
    }

	public CarBizSupplier queryQianLiYanSupplierByCityId(CarBizSupplier carBizSupplier){
		return carBizSupplierExMapper.queryQianLiYanSupplierByCityId(carBizSupplier);
	}

	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="rentcar-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
	} )
	public List<CarBizSupplierVo> findSupplierListByPage(CarBizSupplierQuery queryParam) {
		return carBizSupplierExMapper.findByParams(queryParam);
	}

	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="rentcar-DataSource"),
			@MasterSlaveConfig(databaseTag="driver-DataSource")
	} )
    public AjaxResponse saveSupplierInfo(CarBizSupplierVo supplier) {
		try{
			if (supplier.getSupplierShortName() != null && supplier.getSupplierShortName().length() > 10){
				return AjaxResponse.fail(RestErrorCode.GET_SUPPLIER_SHORT_NAME_INVALID);
			}
			String method = Constants.UPDATE;
			Integer cooperationTypeNew = supplier.getCooperationType();
			Integer cooperationTypeOld = null;
			SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
			supplier.setUpdateBy(currentLoginUser.getId());
			supplier.setUpdateName(currentLoginUser.getName());
            TwoLevelCooperationDto twoLevelCooperation = twoLevelCooperationExMapper.
                    getTwoLevelCooperation(supplier.getCooperationType(), supplier.getTwoLevelCooperation());
            int twoLevelId = twoLevelCooperation != null ? twoLevelCooperation.getId() : 0;
            if (supplier.getSupplierId() == null || supplier.getSupplierId() == 0){
				method = Constants.CREATE;
				supplier.setCreateBy(currentLoginUser.getId());
				supplier.setCreateName(currentLoginUser.getName());
				carBizSupplierExMapper.insertSelective(supplier);
				SupplierExtDto extDto = new SupplierExtDto();
				extDto.setEmail(supplier.getEmail());
				extDto.setSupplierShortName(supplier.getSupplierShortName());
				extDto.setSupplierId(supplier.getSupplierId());
				extDto.setCreateDate(new Date());
				extDto.setUpdateDate(new Date());
				extDto.setTwoLevelCooperation(twoLevelId);
				supplierExtDtoMapper.insertSelective(extDto);
			}else {
				CarBizSupplierVo vo = carBizSupplierExMapper.querySupplierById(supplier.getSupplierId());
				if(vo!=null){
					cooperationTypeOld = vo.getCooperationType();
				}
				carBizSupplierExMapper.updateByPrimaryKeySelective(supplier);
				SupplierExtDto extDto = new SupplierExtDto();
				extDto.setEmail(supplier.getEmail());
				extDto.setSupplierShortName(supplier.getSupplierShortName());
				extDto.setSupplierId(supplier.getSupplierId());
				extDto.setStatus(supplier.getStatus().byteValue());
				extDto.setUpdateDate(new Date());
				extDto.setTwoLevelCooperation(twoLevelId);
				SupplierExtDto supplierExtDto = supplierExtDtoExMapper.selectBySupplierId(supplier.getSupplierId());
				if (supplierExtDto == null){
					extDto.setCreateDate(new Date());
					supplierExtDtoMapper.insertSelective(extDto);
				}else {
					supplierExtDtoExMapper.updateBySupplierId(extDto);
				}
			}
			try{
				Map<String, Object> messageMap = new HashMap<String, Object>();
				messageMap.put("method",method);
				Object json = JSON.toJSON(supplier);
				messageMap.put("data", json);
				String messageStr = JSON.toJSONStringWithDateFormat(messageMap, JSON.DEFFAULT_DATE_FORMAT);
				logger.info("专车供应商，同步发送数据：" + messageStr);
				CommonRocketProducer.publishMessage(Constants.SUPPLIER_TOPIC, method, String.valueOf(supplier.getSupplierId()), messageMap);
			}catch (Exception e){
				logger.error(Constants.SUPPLIER_MQ_SEND_FAILED, e);
			}
			if (Constants.UPDATE.equals(method) && cooperationTypeOld!=null && !cooperationTypeOld.equals(cooperationTypeNew)){
				carBizDriverInfoService.updateDriverCooperationTypeBySupplierId(supplier.getSupplierId(), supplier.getCooperationType());
				carBizCarInfoTempService.updateDriverCooperationTypeBySupplierId(supplier.getSupplierId(), supplier.getCooperationType());

				try {
					DriverTeamRequest driverTeamRequest = new DriverTeamRequest();
					Set set=new HashSet();
					set.add(supplier.getSupplierId());
					driverTeamRequest.setSupplierIds(set);
					//当前供应商底下的司机ID
					List<CarDriverInfoDTO> limitsDrivers = carBizDriverInfoExMapper.queryListByLimits(driverTeamRequest);

					if(limitsDrivers!=null && limitsDrivers.size()>0){
						for (int i = 0; i < limitsDrivers.size(); i++) {
							CarDriverInfoDTO carDriverInfoDTO = limitsDrivers.get(i);
							if(carDriverInfoDTO==null){
								continue;
							}
							try {
								// 调用接口清除，key
								carBizDriverInfoService.flashDriverInfo(Integer.parseInt(carDriverInfoDTO.getDriverId()));
							} catch (Exception e) {
								logger.info("司机driverId={},供应商修改加盟类型,调用清除接口异常={}", carDriverInfoDTO.getDriverId(), e.getMessage());
							}
						}
					}
				} catch (Exception e) {
					logger.info("供应商修改加盟类型,调用清除接口异常={}", e.getMessage());
				}
			}
			logger.info(Constants.SAVE_SUPPLIER_SUCCESS);
			return AjaxResponse.success(Constants.SAVE_SUPPLIER_SUCCESS);
		}catch (Exception e){
			logger.error(Constants.SAVE_SUPPLIER_ERROR, e);
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}
    }

	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="rentcar-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE ),
			@MasterSlaveConfig(databaseTag="driver-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
	} )
	public void addExtInfo(List<CarBizSupplierVo> list) {
		List<Integer> idList = new ArrayList<>(list.size());
		list.forEach( carBizSupplierVo -> idList.add(carBizSupplierVo.getSupplierId()));
		if (idList.size() == 0){
			return;
		}
		List<SupplierExtDto> extDtos = supplierExtDtoExMapper.queryExtDtoByIdList(idList);
		for (SupplierExtDto supplierExtDto : extDtos){
			for (CarBizSupplierVo vo : list){
				if (vo != null && vo.getSupplierId().equals(supplierExtDto.getSupplierId())){
					vo.setSupplierShortName(supplierExtDto.getSupplierShortName());
					vo.setEmail(supplierExtDto.getEmail());
					break;
				}
			}
		}
	}

	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="rentcar-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE ),
			@MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE ),
			@MasterSlaveConfig(databaseTag="driver-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
	} )
    public AjaxResponse querySupplierById(Integer supplierId) {
		CarBizSupplierVo vo = carBizSupplierExMapper.querySupplierById(supplierId);
		if (vo == null){
			return AjaxResponse.fail(RestErrorCode.SUPPLIER_NOT_EXIST);
		}
		SupplierExtDto supplierExtDto = supplierExtDtoExMapper.selectBySupplierId(supplierId);
		if (supplierExtDto != null) {
			vo.setEmail(supplierExtDto.getEmail());
			vo.setSupplierShortName(supplierExtDto.getSupplierShortName());
			TwoLevelCooperationDto twoLevelCooperationDto;
			if ((twoLevelCooperationDto = hasTwoLevelCooperation(supplierExtDto)) != null){
				vo.setTwoLevelCooperationName(twoLevelCooperationDto.getCooperationName());
				vo.setTwoLevelCooperation(twoLevelCooperationDto.getId());
			}
		}
		if (vo.getCreateBy() != null && vo.getCreateBy() > Constants.ZERO){
			String create = carAdmUserExMapper.queryNameById(vo.getCreateBy());
			vo.setCreateName(create);
		}
		if (vo.getUpdateBy() != null && vo.getUpdateBy() > Constants.ZERO){
			String update = carAdmUserExMapper.queryNameById(vo.getUpdateBy());
			vo.setUpdateName(update);
		}
		Map<String, Object> params = Maps.newHashMap();
		params.put(Constants.SUPPLIER_ID, vo.getSupplierId());
		try {
			com.alibaba.fastjson.JSONObject jsonObject = MpOkHttpUtil.okHttpPostBackJson(commissionUrl, params, 1, Constants.GROUP_INFO_TAG);
			if (jsonObject != null && Constants.SUCCESS_CODE == jsonObject.getInteger(Constants.CODE)) {
				JSONArray jsonArray = jsonObject.getJSONArray(Constants.DATA);
				List<Integer> idList = new ArrayList<>();
				List<GroupInfo> groupInfos = new ArrayList<>();
				jsonArray.forEach(elem -> {
					com.alibaba.fastjson.JSONObject jsonData = (com.alibaba.fastjson.JSONObject) elem;
					Integer id = jsonData.getInteger(Constants.GROUP_ID);
					if (id != null && id > Constants.ZERO) {
						idList.add(id);
					}
					GroupInfo info = new GroupInfo();
					info.setGroupId(id == null ? 0 : id);
					info.setSupplierId(jsonData.getIntValue(Constants.SUPPLIER_ID));
					info.setActiveStartDate(jsonData.getDate(Constants.ACTIVE_START_DATE));
					info.setActiveEndDate(jsonData.getDate(Constants.ACTIVE_END_DATE));
					info.setProrateId(jsonData.getString(Constants.PRORATE_ID));
					groupInfos.add(info);
				});
				if (!idList.isEmpty()) {
					List<CarBizCarGroup> carBizCarGroups = carBizCarGroupExMapper.queryGroupNameByIds(idList);
					for (CarBizCarGroup groupInfo : carBizCarGroups) {
						for (GroupInfo group : groupInfos) {
							Integer id = group.getGroupId();
							if (id.equals(groupInfo.getGroupId())) {
								group.setGroupName(groupInfo.getGroupName());
								break;
							}
						}
					}
					vo.setGroupList(groupInfos);
				}
			}
			return AjaxResponse.success(vo);
		}catch (Exception e){
			logger.info("获取分佣信息失败", e);
			return AjaxResponse.fail(RestErrorCode.GET_SUPPLIER_COMMISSION_INFO_FAILED);
		}
    }

	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="rentcar-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
	} )
	public AjaxResponse checkSupplierFullName(String supplierName) {
		int count = carBizSupplierExMapper.checkSupplierFullName(supplierName);
		if (count > 0){
			return AjaxResponse.failMsg(RestErrorCode.PARAMS_ERROR, Constants.SUPPLIER_NAME_EXIST);
		}
		return AjaxResponse.success(Constants.SUPPLIER_NAME_AVAILABLE);
	}
	/**
	 * 根据供应商ID查询供应商名称
	 * @param supplierId
	 * @return
	 */
	public String getSupplierNameById(Integer supplierId){
		return carBizSupplierExMapper.getSupplierNameById(supplierId);
	}

	private TwoLevelCooperationDto hasTwoLevelCooperation(SupplierExtDto supplierExtDto){
		int id;
		if (supplierExtDto.getTwoLevelCooperation() != null && (id = supplierExtDto.getTwoLevelCooperation()) > 0){
			TwoLevelCooperationDto twoLevelCooperation = twoLevelCooperationExMapper.getTwoLevelCooperationById(id);
			if (twoLevelCooperation != null){
				return twoLevelCooperation;
			}
		}
		return null;
	}
}