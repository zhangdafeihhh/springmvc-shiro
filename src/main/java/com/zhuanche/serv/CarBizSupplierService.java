package com.zhuanche.serv;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constant.Constants;
import com.zhuanche.dto.driver.supplier.SupplierCooperationAgreementDTO;
import com.zhuanche.entity.driver.*;
import com.zhuanche.entity.rentcar.*;
import com.zhuanche.http.HttpClientUtil;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.serv.deiver.CarBizCarInfoTempService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.DateUtils;
import mapper.driver.SupplierCooperationAgreementMapper;
import mapper.driver.SupplierCooperationAgreementUrlMapper;
import mapper.driver.SupplierExperienceMapper;
import mapper.driver.SupplierExtDtoMapper;
import mapper.driver.ex.*;
import mapper.mdbcarmanage.ex.CarAdmUserExMapper;
import mapper.mdbcarmanage.ex.SaasPermissionExMapper;
import mapper.rentcar.CarBizSupplierMapper;
import mapper.rentcar.ex.CarBizCarGroupExMapper;
import mapper.rentcar.ex.CarBizCityExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**供应商信息 的 基本服务层**/
@Service
public class CarBizSupplierService{

	private static final Logger logger = LoggerFactory.getLogger(CarBizSupplierService.class);

	// 根据供应商ID，批量清理司机redis缓存
	public static final String DRIVER_FLASH_REDIS_BY_SUPPLIERID_URL = "/api/v2/driver/flash/driverInfoBySupplierId";

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

	@Value("${commission.url}")
	String commissionUrl;

	@Value("${driver.server.api.url}")
	String driverServiceApiUrl;

	@Autowired
	private CarBizCityExMapper carBizCityExMapper;

	@Autowired
	private SaasPermissionExMapper saasPermissionExMapper;

	private static final ExecutorService es = Executors.newCachedThreadPool();

	@Value("${settle.api.url}")
	String settleApiUrl;

	@Autowired
	private SupplierAccountApplyExMapper supplierAccountApplyExMapper;

	@Autowired
	private SupplierCooperationAgreementExMapper supplierCooperationAgreementExMapper;

	@Autowired
	private SupplierExperienceExMapper supplierExperienceExMapper;

	@Autowired
	private SupplierCooperationAgreementUrlExMapper supplierCooperationAgreementUrlExMapper;

	@Autowired
	private SupplierCooperationAgreementMapper supplierCooperationAgreementMapper;

	@Autowired
	private SupplierExperienceMapper supplierExperienceMapper;

	@Autowired
	private SupplierCooperationAgreementUrlMapper supplierCooperationAgreementUrlMapper;

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
			String supplierNameOld = null;
			SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
			supplier.setUpdateBy(currentLoginUser.getId());
			supplier.setUpdateName(currentLoginUser.getName());
            if (supplier.getSupplierId() == null || supplier.getSupplierId() == 0){
				method = Constants.CREATE;
				supplier.setCreateBy(currentLoginUser.getId());
				supplier.setCreateName(currentLoginUser.getName());
				logger.info("新增供应商信息: supplierInfo {}", JSON.toJSONString(supplier));
				try{
					SupplierExtDto extDto = generateSupplierExtDto(supplier, true);
					carBizSupplierExMapper.insertSelective(supplier);
					extDto.setSupplierId(supplier.getSupplierId());
					supplierExtDtoMapper.insertSelective(extDto);
					logger.info("新增供应商扩展信息：supplierExtInfo {}", JSON.toJSONString(extDto));
				}catch (IllegalArgumentException e){
					return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, e.getMessage());
				}
			}else {
            	try {
                    CarBizSupplierVo vo = carBizSupplierExMapper.querySupplierById(supplier.getSupplierId());
                    if(vo!=null){
                        cooperationTypeOld = vo.getCooperationType();
						supplierNameOld = vo.getSupplierFullName();
                    }
					SupplierExtDto extDto = generateSupplierExtDto(supplier, false);
					int count = supplierExtDtoExMapper.selectCountBySupplierId(supplier.getSupplierId());
					carBizSupplierExMapper.updateByPrimaryKeySelective(supplier);
					logger.info("更新供应商信息: supplierInfo {}", JSON.toJSONString(supplier));
					if (count == 0) {
						extDto.setCreateDate(new Date());
						supplierExtDtoMapper.insertSelective(extDto);
						logger.info("新增供应商扩展信息 : supplierExtInfo {}", JSON.toJSONString(extDto));
					} else {
						supplierExtDtoExMapper.updateBySupplierId(extDto);
						logger.info("更新供应商扩展信息 : supplierExtInfo {}", JSON.toJSONString(extDto));
					}
				}catch (IllegalArgumentException e){
            		return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, e.getMessage());
				}
			}

			Integer supplierId = supplier.getSupplierId();
			Integer userId = currentLoginUser.getId();
			String userName = currentLoginUser.getName();
			try {
				//处理合作协议
				if (Constants.UPDATE.equals(method)) {
					//删除合作协议，附件
					supplierCooperationAgreementExMapper.deleteBySupplierId(supplierId);
					supplierCooperationAgreementUrlExMapper.deleteBySupplierId(supplierId);
				}
				//新的合作协议
				if(StringUtils.isNoneBlank(supplier.getAgreementListStr()) && !" null".equals(supplier.getAgreementListStr())){
					List<SupplierCooperationAgreementDTO> agreementList = JSON.parseArray(supplier.getAgreementListStr(), SupplierCooperationAgreementDTO.class);
					agreementList.forEach(agreementDTO -> {
						SupplierCooperationAgreement agreement = new SupplierCooperationAgreement();
						BeanUtils.copyProperties(agreementDTO, agreement);
						agreement.setSupplierId(supplierId);
						agreement.setCreateBy(userId);
						agreement.setCreateName(userName);
						supplierCooperationAgreementMapper.insertSelective(agreement);
						List<SupplierCooperationAgreementUrl> agreementUrlList = agreementDTO.getAgreementUrlList();
						if(agreementUrlList!=null && agreementUrlList.size()>0){
							agreementUrlList.forEach(agreementUrl -> {
								agreementUrl.setSupplierId(supplierId);
								agreementUrl.setAgreementId(agreement.getId());
								agreementUrl.setCreateBy(userId);
								agreementUrl.setCreateName(userName);
								supplierCooperationAgreementUrlMapper.insertSelective(agreementUrl);
							});
						}
					});
				}
			} catch (Exception e) {
				logger.info("供应商ID={}, 处理合作协议 异常={}", supplierId, e);
			}
			try {
				//处理供应商经历
				if (Constants.UPDATE.equals(method)) {
					//删除供应商经历
					supplierExperienceExMapper.deleteBySupplierId(supplierId);
				}
				//新的供应商经历
				if(StringUtils.isNoneBlank(supplier.getExperienceListStr()) && !" null".equals(supplier.getExperienceListStr())) {
					List<SupplierExperience> experienceList = JSON.parseArray(supplier.getExperienceListStr(), SupplierExperience.class);

					experienceList.forEach(supplierExperience -> {
						supplierExperience.setSupplierId(supplierId);
						supplierExperience.setCreateBy(userId);
						supplierExperience.setCreateName(userName);
						supplierExperienceMapper.insertSelective(supplierExperience);
					});
				}
			} catch (Exception e) {
				logger.info("供应商ID={}, 处理合供应商经历 异常={}", supplierId, e);
			}

			try{
            	//TODO 将老的发送mq的方式改为请求接口
				/*Map<String, Object> messageMap = new HashMap<String, Object>();
				messageMap.put("method",method);
				Object json = JSON.toJSON(supplier);
				messageMap.put("data", json);
				String messageStr = JSON.toJSONStringWithDateFormat(messageMap, JSON.DEFFAULT_DATE_FORMAT);
				logger.info("专车供应商，同步发送数据：" + messageStr);
				CommonRocketProducer.publishMessage(Constants.SUPPLIER_TOPIC, method, String.valueOf(supplier.getSupplierId()), messageMap);*/
				if (Constants.CREATE.equals(method)) {
					// 请求参数
					String jsonString = JSON.toJSONStringWithDateFormat(supplier, JSON.DEFFAULT_DATE_FORMAT);
					JSONObject json = (JSONObject) JSONObject.parse(jsonString);
					Map<String, Object> params = json.getInnerMap();
					params.put("memo",supplier.getMemo()==null?"":supplier.getMemo());
					params.put("isCommission",supplier.getIscommission()==null?2:supplier.getIscommission());
					params.put("posPayFlag",supplier.getPospayflag()==null?0:supplier.getPospayflag());
					params.put("settleDay",supplier.getSettlementDay()==null?0:supplier.getSettlementDay());
					JSONObject result = MpOkHttpUtil.okHttpPostBackJson(settleApiUrl + "/api/settle/supplier/info/add", params, 1, "增加供应商信息");
					logger.info("调用分佣接口增加供应商返回结果：{}",result.toJSONString());
					if (result.getIntValue("code") != Constants.SUCCESS_CODE) {
						String errorMsg = result.getString("msg");
						logger.info("[ CarBizSupplierService-saveSupplierInfo ] 增加供应商调用分佣增加供应商接口出错,params={},errorMsg={}", params, errorMsg);
					}
				}
				Map<String,Object> settleMap = new HashMap<>();
				if(supplier.getSettlementCycle()!=null){
					settleMap.put("settleType",supplier.getSettlementCycle());
				}
				if(supplier.getSettlementDay()!=null){
					settleMap.put("settleDay",supplier.getSettlementDay());
				}
				if(supplier.getSupplierId()!=null){
					settleMap.put("supplierId",supplier.getSupplierId());
				}
				if(supplier.getUpdateBy()!=null){
					settleMap.put("createName",supplier.getUpdateBy());
				}
				JSONObject result = MpOkHttpUtil.okHttpPostBackJson(settleApiUrl + "/api/settle/supplier/info/update", settleMap, 1, "增加供应商结算信息");
				logger.info("调用分佣接口增加供应商结算信息返回结果：{}",result.toJSONString());
				if (result.getIntValue("code") != Constants.SUCCESS_CODE) {
					String errorMsg = result.getString("msg");
					logger.info("[ CarBizSupplierService-saveSupplierInfo ] 增加供应商调用分佣修改结算接口出错,params={},errorMsg={}", settleMap, errorMsg);
				}


			}catch (Exception e){
				logger.error(Constants.SUPPLIER_MQ_SEND_FAILED, e);
			}
			if (Constants.UPDATE.equals(method)
					&& ((cooperationTypeOld!=null && !cooperationTypeOld.equals(cooperationTypeNew))
			              || (StringUtils.isNotEmpty(supplierNameOld) && !supplierNameOld.equals(supplier.getSupplierFullName())))){

				carBizDriverInfoService.updateDriverCooperationTypeBySupplierId(supplier.getSupplierId(), supplier.getCooperationType());
				carBizCarInfoTempService.updateDriverCooperationTypeBySupplierId(supplier.getSupplierId(), supplier.getCooperationType());

				try {
					es.submit(new SupplierTasker(supplier.getSupplierId(), supplier.getSupplierFullName(),
							supplier.getSupplierCity(), cooperationTypeNew));
				} catch (Exception e) {
					logger.info("供应商修改加盟类型,调用清除接口异常="+e.getMessage());
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
		List<Integer> idList = list.stream().filter(Objects::nonNull)
				.map(CarBizSupplier::getSupplierId).collect(Collectors.toList());
		if (idList.size() == 0){
			return;
		}
		//补全供应商邮箱,简称字段
		List<SupplierExtDto> extInfo = supplierExtDtoExMapper.queryExtDtoByIdList(idList);
		Map<Integer, SupplierExtDto> supplierExtDtoMap =
				extInfo.stream().filter(Objects::nonNull).collect(Collectors.toMap(SupplierExtDto::getSupplierId, value -> value));
		list.forEach( supplierVo -> {
			if (supplierVo != null){
				Integer supplierId = supplierVo.getSupplierId();

				SupplierExtDto supplierExtDto = supplierExtDtoMap.get(supplierId);
				if (supplierExtDto != null) {
					BeanUtils.copyProperties(supplierExtDto, supplierVo);
//					supplierVo.setSupplierShortName(supplierExtDto.getSupplierShortName());
//					supplierVo.setEmail(supplierExtDto.getEmail());
				}

				//补全合作协议信息
				SupplierCooperationAgreement agreement = supplierCooperationAgreementExMapper.selectLastAgreementBySupplierId(supplierId);
				if(agreement!=null){
					if(agreement.getAgreementStartTime()!=null){
						supplierVo.setAgreementStartTime(DateUtils.formatDate(agreement.getAgreementStartTime()));
					}
					if(agreement.getAgreementEndTime()!=null){
						supplierVo.setAgreementEndTime(DateUtils.formatDate(agreement.getAgreementEndTime()));
					}
//					supplierVo.setAgreementStartTime(agreement.getAgreementStartTime());
//					supplierVo.setAgreementEndTime(agreement.getAgreementEndTime());
					supplierVo.setMarginAmount(agreement.getMarginAmount());
				}
				//补充申请修改账户信息
				SupplierAccountApply apply = supplierAccountApplyExMapper.selectApplyStatusBySupplierId(supplierId);
				if(apply!=null){
					if(apply.getCreateDate()!=null){
						supplierVo.setApplyCreateDate(DateUtils.formatDate(apply.getCreateDate()));
					}
//					supplierVo.setApplyCreateDate(apply.getCreateDate());
					supplierVo.setApplyStatus(apply.getStatus());
				}
			}
		});
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
		//补全供应商扩展信息
		SupplierExtDto supplierExtDto = supplierExtDtoExMapper.selectBySupplierId(supplierId);
		if (supplierExtDto != null) {
			BeanUtils.copyProperties(supplierExtDto, vo);
			vo.setSupplierId(supplierId);
			if(supplierExtDto.getFirstSignTime()!=null){
				vo.setFirstSignTime(DateUtils.formatDate(supplierExtDto.getFirstSignTime()));
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
			//调用分佣接口获取分佣协议信息
			JSONObject jsonObject = MpOkHttpUtil.okHttpPostBackJson(commissionUrl, params, 1, Constants.GROUP_INFO_TAG);
			logger.info("分佣信息协议查询结果 {}", jsonObject == null ? "empty" : jsonObject.toJSONString());
			if (jsonObject != null && Constants.SUCCESS_CODE == jsonObject.getInteger(Constants.CODE)) {
				JSONArray jsonArray = jsonObject.getJSONArray(Constants.DATA);
				List<Integer> idList = new ArrayList<>();
				List<GroupInfo> groupInfos = new ArrayList<>();
				jsonArray.forEach(elem -> {
					JSONObject jsonData = (JSONObject) elem;
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
					//补全车型名称
					List<CarBizCarGroup> carBizCarGroups = carBizCarGroupExMapper.queryGroupNameByIds(idList);
					Map<Integer, CarBizCarGroup> carBizCarGroupMap =
							carBizCarGroups.stream().filter(Objects::nonNull).collect(Collectors.toMap(CarBizCarGroup::getGroupId, v -> v));
					groupInfos.forEach( groupInfo -> {
						CarBizCarGroup group = carBizCarGroupMap.get(groupInfo.getGroupId());
						groupInfo.setGroupName(group.getGroupName());
					});
					vo.setGroupList(groupInfos);
				}
			}
		}catch (Exception e){
			logger.info("获取分佣信息失败", e);
			return AjaxResponse.fail(RestErrorCode.GET_SUPPLIER_COMMISSION_INFO_FAILED);
		}

		try {
			//合作协议列表
			List<SupplierCooperationAgreementDTO> agreementList = Lists.newArrayList();
			List<SupplierCooperationAgreement> agreements = supplierCooperationAgreementExMapper.selectAllBySupplierId(supplierId);
			agreements.forEach( agreemenInfo -> {
				if(agreemenInfo!=null){
					SupplierCooperationAgreementDTO agreementDTO = new SupplierCooperationAgreementDTO();
					BeanUtils.copyProperties(agreemenInfo, agreementDTO);
					//根据合作协议ID查询附件
					List<SupplierCooperationAgreementUrl> agreementUrlList = supplierCooperationAgreementUrlExMapper.selectUrlByAgreementId(agreemenInfo.getId());
					agreementDTO.setAgreementUrlList(agreementUrlList);
					agreementList.add(agreementDTO);
				}

			});
			vo.setAgreementList(agreementList);

			//加盟商经历列表
			List<SupplierExperience> experienceList = supplierExperienceExMapper.selectAllBySupplierId(supplierId);
			vo.setExperienceList(experienceList);
		} catch (Exception e) {
			logger.info("supplierId={},查询合作协议列表异常={}", supplierId, e);
		}

		return AjaxResponse.success(vo);
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

	private SupplierExtDto generateSupplierExtDto(CarBizSupplierVo supplier, boolean create){
		SupplierExtDto extDto = new SupplierExtDto();
		BeanUtils.copyProperties(supplier, extDto);
		extDto.setUpdateDate(new Date());
		if (create) {
			extDto.setCreateDate(new Date());
		}
		if(StringUtils.isNoneBlank(supplier.getFirstSignTime())){
			try {
				extDto.setFirstSignTime(DateUtils.getDate1(supplier.getFirstSignTime()));
			} catch (ParseException e) {
				logger.info("时间转换异常 error={}", e);
			}
		}
		if (!modifyAccept(supplier)){
			throw new IllegalArgumentException("结算信息输入错误");
		}
		return extDto;
	}

	class SupplierTasker implements Runnable{

		private Integer supplierId;
		private String supplierName;
		private Integer cityId;
		private Integer cooperationType;

		public SupplierTasker(Integer supplierId, String supplierName, Integer cityId, Integer cooperationType){
			this.supplierId = supplierId;
			this.supplierName = supplierName;
			this.cityId = cityId;
			this.cooperationType = cooperationType;
		}
		@Override
		public void run() {
			try {
				// 根据城市id获取城市名称
				String cityName = carBizCityExMapper.queryNameById(cityId);
				String url = driverServiceApiUrl + DRIVER_FLASH_REDIS_BY_SUPPLIERID_URL
						+ "?supplierId=" + supplierId
						+ "&supplierName=" + supplierName
						+ "&cityId=" + cityId
						+ "&cityName=" + cityName
						+ "&cooperationType=" + cooperationType;
				String result = HttpClientUtil.buildGetRequest(url).execute();
				logger.info("删除司机信息缓存,删除失败不影响业务,调用结果返回={}", result);
			} catch (Exception e) {
				logger.info("供应商修改加盟类型,调用清除接口异常="+e.getMessage());
			}

		}
	}


	private boolean modifyAccept(CarBizSupplierVo supplierVo){

		//结算周期为(2-半月结 3-周结)时，前端不传结算日，赋默认值0
		Integer settlementCycle = supplierVo.getSettlementCycle();
		if (settlementCycle  != null &&  (settlementCycle == 2 || settlementCycle == 3)){
			supplierVo.setSettlementDay(0);
		}

		//根据不同的角色拥有的权限不同~
		SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
		List<String> perString = saasPermissionExMapper.queryPermissionCodesOfUser(user.getId() );

		boolean modifyAll = true;

		boolean notModify = true;

		if(perString.contains("SupplierChange_modify1")){

		}else if(perString.contains("SupplierChange_modify2")){
			
		}else if(perString.contains("SupplierChange_modify3")){
			modifyAll =StringUtils.isNotBlank(supplierVo.getSettlementFullName()) &&
					StringUtils.isNotBlank(supplierVo.getSettlementAccount()) &&
					Objects.nonNull(supplierVo.getSettlementCycle()) &&
					Objects.nonNull(supplierVo.getSettlementType()) &&
					Objects.nonNull(supplierVo.getSettlementDay())&&
					StringUtils.isNotBlank(supplierVo.getBankIdentify()) &&
					StringUtils.isNotBlank(supplierVo.getBankName()) &&
					StringUtils.isNotBlank(supplierVo.getBankAccount()) ;
			notModify =StringUtils.isBlank(supplierVo.getSettlementFullName()) &&
					StringUtils.isBlank(supplierVo.getSettlementAccount()) &&
					Objects.isNull(supplierVo.getSettlementCycle()) &&
					Objects.isNull(supplierVo.getSettlementType()) &&
					Objects.isNull(supplierVo.getSettlementDay()) &&
					StringUtils.isBlank(supplierVo.getBankIdentify()) &&
					StringUtils.isBlank(supplierVo.getBankName()) &&
					StringUtils.isBlank(supplierVo.getBankAccount());
		}


		return modifyAll || notModify;
	}

	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="rentcar-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
	} )
	public PageInfo<CarBizSupplierVo> querySupplierPage(CarBizSupplierQuery queryParam, int pageNo, int pageSize) {
		PageHelper.startPage(pageNo, pageSize, true);
		List<CarBizSupplierVo> list = carBizSupplierExMapper.findByParams(queryParam);
		this.addExtInfo(list);
		PageInfo<CarBizSupplierVo> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}

	public void dataTrans(List<CarBizSupplierVo> result, List<String> csvDataList){
		if(null == result){
			return;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 24小时制
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// 24小时制

		for (CarBizSupplierVo s : result) {
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append(s.getSupplierId() != null ? ""
					+ s.getSupplierId() + "" : "");
			stringBuffer.append(",");


			stringBuffer.append(s.getSupplierFullName() != null ? ""
					+ s.getSupplierFullName() + "" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getSupplierShortName() != null ? ""
					+ s.getSupplierShortName() + "" : "");
			stringBuffer.append(",");


			stringBuffer.append(s.getSupplierCityName()!= null ? ""
					+ s.getSupplierCityName() + "" : "");
			stringBuffer.append(",");


			stringBuffer.append(s.getContacts()!= null ? ""
					+ s.getContacts() + "" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getContactsPhone()!= null ? ""
					+ s.getContactsPhone() + "" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getEmail()!= null ? ""
					+ s.getEmail() + "" : "");
			stringBuffer.append(",");

			stringBuffer.append((s.getAgreementStartTime()!= null && s.getAgreementEndTime()!=null) ? ""
					+ s.getAgreementStartTime() + "~" + s.getAgreementEndTime() + "" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getMarginAmount()!= null? ""
					+ s.getMarginAmount() + "" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getCooperationName()!= null ? ""
					+ s.getCooperationName() + "" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getStatus()!= null ? s.getStatus()==1 ? "有效" : "无效" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getIsTwoShifts()!= null ? s.getIsTwoShifts()==1 ? "双班" : "单班" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getAddress()!= null? ""
					+ s.getAddress() + "" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getApplyStatus()!= null ? s.getApplyStatus()==1 ? "申请中" : "已更新" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getApplyCreateDate()!= null? ""
					+ s.getApplyCreateDate() + "" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getUpdateDate()!= null? ""
					+ format.format(s.getUpdateDate()) + "" : "");
			stringBuffer.append(",");

			// 结算类型 0 无效类型 1自主结算 2 对公结算 3其他
			Integer settlementType = s.getSettlementType();
			String settlementTypeName = "";
			if(settlementType!=null && settlementType==1){
				settlementTypeName = "自主结算";
			} else if(settlementType!=null && settlementType==2) {
				settlementTypeName = "对公结算";
			} else if(settlementType!=null && settlementType==3) {
				settlementTypeName = "其他";
			}
			stringBuffer.append(settlementTypeName!= null? ""
					+ settlementTypeName + "" : "");
			stringBuffer.append(",");

			// 结算周期 0 无效周期 1月结 2 半月结 3 周结
			Integer settlementCycle = s.getSettlementCycle();
			String settlementCycleName = "";
			if(settlementCycle!=null && settlementCycle==1){
				settlementCycleName = "月结";
			} else if(settlementCycle!=null && settlementCycle==2) {
				settlementCycleName = "半月结";
			} else if(settlementCycle!=null && settlementCycle==3) {
				settlementCycleName = "周结";
			}
			stringBuffer.append(settlementCycleName!= null? ""
					+ settlementCycleName + "" : "");
			stringBuffer.append(",");

			stringBuffer.append((s.getSettlementDay()!= null&&s.getSettlementDay()!=0)? ""
					+ s.getSettlementDay() + "" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getTaxRate()!= null? ""
					+ s.getTaxRate() + "" : "");
			stringBuffer.append(",");

			// 发票类型 0.无效发票类型 1.专票、2.普票、3.电子发票（普票）
			Integer invoiceType = s.getInvoiceType();
			String invoiceTypeName = "";
			if(invoiceType!=null && invoiceType==1){
				invoiceTypeName = "专票";
			} else if(invoiceType!=null && invoiceType==2) {
				invoiceTypeName = "普票";
			} else if(invoiceType!=null && invoiceType==3) {
				invoiceTypeName = "电子发票（普票）";
			}
			stringBuffer.append(invoiceTypeName!= null? ""
					+ invoiceTypeName + "" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getSettlementFullName()!= null? ""
					+ s.getSettlementFullName() + "" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getSettlementAccount()!= null? ""
					+ s.getSettlementAccount() + "" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getBankAccount()!= null? ""
					+ s.getBankAccount() + "" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getBankName()!= null? ""
					+ s.getBankName() + "" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getBankIdentify()!= null? ""
					+ s.getBankIdentify() + "" : "");
			stringBuffer.append(",");

			csvDataList.add(stringBuffer.toString());
		}
	}
}