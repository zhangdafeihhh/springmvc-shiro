package com.zhuanche.controller.busManage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.sms.SmsSendUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.constants.BusConst;
import com.zhuanche.constants.busManage.BusConstant;
import com.zhuanche.dto.busManage.BusDriverQueryDTO;
import com.zhuanche.dto.busManage.BusDriverSaveDTO;
import com.zhuanche.dto.rentcar.CarBizDriverInfoDetailDTO;
import com.zhuanche.entity.rentcar.CarBizDriverInfo;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.mongo.busManage.BusDriverInfoAudit;
import com.zhuanche.serv.CarBizDriverInfoDetailService;
import com.zhuanche.serv.CarBizDriverInfoService;
import com.zhuanche.serv.busManage.BusBizChangeLogService;
import com.zhuanche.serv.busManage.BusCarBizDriverInfoService;
import com.zhuanche.serv.busManage.BusCarDriverTeamService;
import com.zhuanche.serv.busManage.BusCommonService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.excel.CsvUtils;
import com.zhuanche.util.excel.ExportExcelUtil;
import com.zhuanche.vo.busManage.*;
import mapper.mdbcarmanage.ex.BusBizChangeLogExMapper.BusinessType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/bus/driverInfo")
@Validated
public class BusDriverInfoController extends BusBaseController {

	private static final Logger logger = LoggerFactory.getLogger(BusDriverInfoController.class);

	// ===========================专车业务拓展service==================================
	@Autowired
	private CarBizDriverInfoService carBizDriverInfoService;

	@Autowired
	private CarBizDriverInfoDetailService carBizDriverInfoDetailService;

	// ===========================巴士业务拓展service==================================
	@Autowired
	private BusBizChangeLogService busBizChangeLogService;
	
	@Autowired
	private BusCarBizDriverInfoService busCarBizDriverInfoService;

	@Autowired
	private BusCarDriverTeamService busCarDriverTeamService;
	@Autowired
	private BusCommonService commonService;

	@Value("${driver.message.url}")
	private String mp_rest_url;

	/**
	 * @Title: findDriverList
	 * @Description: 查询司机列表
	 * @param queryDTO
	 * @return AjaxResponse
	 * @throws
	 */
	@SuppressWarnings("resource")
	@RequestMapping(value = "/findDriverList")
	public AjaxResponse findDriverList(@Validated BusDriverQueryDTO queryDTO) {

		logger.info("当前登录人信息={}", JSON.toJSONString(WebSessionUtil.getCurrentLoginUser()));

		// 数据权限控制SSOLoginUser
		Set<Integer> permOfCity = WebSessionUtil.getCurrentLoginUser().getCityIds(); // 普通管理员可以管理的所有城市ID
		Set<Integer> permOfSupplier = WebSessionUtil.getCurrentLoginUser().getSupplierIds(); // 普通管理员可以管理的所有供应商ID
		Set<Integer> permOfTeam = WebSessionUtil.getCurrentLoginUser().getTeamIds(); // 普通管理员可以管理的所有车队ID
		queryDTO.setAuthOfCity(permOfCity);
		queryDTO.setAuthOfSupplier(permOfSupplier);
		queryDTO.setAuthOfTeam(permOfTeam);

		// 查询结果
		PageDTO pageDTO = new PageDTO(queryDTO.getPageNum(), queryDTO.getPageSize(), 0, null);

		// 查询车队、小组查询司机
		Set<Integer> driverIds = null;
		if (permOfTeam != null && !permOfTeam.isEmpty()) {
			driverIds = busCarDriverTeamService.selectDriverIdsByTeamIdOrGroupId(null, null, permOfTeam);
			if (driverIds == null || driverIds.isEmpty()) {
				logger.info("[ BusDriverInfoController-findDriverList ] 当前用户拥有的车队权限teamIds={}没有司机信息", permOfTeam);
				return AjaxResponse.success(pageDTO);
			}
		}

		// 根据条件查询司机
		queryDTO.setDriverIds(driverIds);
		List<BusDriverInfoPageVO> list = busCarBizDriverInfoService.queryDriverPageList(queryDTO);
		Page<BusDriverInfoPageVO> page = (Page<BusDriverInfoPageVO>) list;
		pageDTO = new PageDTO(page.getPageNum(), page.getPageSize(), page.getTotal(), list);
		return AjaxResponse.success(pageDTO);
	}

	/**
	 * 查询司机审核列表
	 * @param busDriverDTO
	 * @return
	 */
	@RequestMapping("/findAuditDriverList")
	public AjaxResponse findAuditDriverList(BusDriverQueryDTO busDriverDTO){
		Set<Integer> supplierIds = commonService.getAuthSupplierIds();
		if(supplierIds==null){
			return AjaxResponse.fail(RestErrorCode.PERMISSION_NOT_EXIST);
		}
		if(busDriverDTO.getSupplierId()!=null){
			if(supplierIds.isEmpty()||supplierIds.contains(busDriverDTO.getSupplierId())){
				supplierIds.clear();
				supplierIds.add(busDriverDTO.getSupplierId());
			}else{
				return AjaxResponse.fail(RestErrorCode.PERMISSION_NOT_EXIST);
			}
		}
		busDriverDTO.setAuthOfSupplier(supplierIds);
		return  busCarBizDriverInfoService.queryBusDriverAuditList(busDriverDTO);
	}
	/**
	 * @Title: saveDriver
	 * @Description: 保存司机信息
	 * @param saveDTO
	 * @return 
	 * @return AjaxResponse
	 * @throws
	 */
	@RequestMapping(value = "/saveDriver")
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public AjaxResponse saveDriver(@Validated BusDriverSaveDTO saveDTO) {
		
		/** 补充默认信息(用户不想填但业务需要的字段)*/
		AjaxResponse checkResult = busCarBizDriverInfoService.completeInfo(saveDTO);
		//校验mongo中保存信息
		AjaxResponse checkMongoResult = busCarBizDriverInfoService.checkMongoInfo(saveDTO);
		if (!checkResult.isSuccess()) {
			return checkResult;
		}
		if(!checkMongoResult.isSuccess()){
			return checkMongoResult;
		}
		
		Integer driverId = saveDTO.getDriverId();
		String phone = saveDTO.getPhone();
		Integer groupId = saveDTO.getGroupId();
		Integer cooperationType = saveDTO.getCooperationType();
		// 首约自营需要的补充的信息
		if (cooperationType != null && cooperationType == 5) {
			if (saveDTO.getAge() == null || StringUtils.isEmpty(saveDTO.getCurrentAddress())
			// || StringUtils.isEmpty(saveDTO.getEmergencyContactPerson())
			// || StringUtils.isEmpty(saveDTO.getEmergencyContactNumber())
					|| StringUtils.isEmpty(saveDTO.getSuperintendNo())
					|| StringUtils.isEmpty(saveDTO.getDrivingLicenseType()) || saveDTO.getDrivingYears() == null
					|| StringUtils.isEmpty(saveDTO.getArchivesNo()) || saveDTO.getIssueDate() == null
					// || saveDTO.getExpireDate() == null
					|| StringUtils.isEmpty(saveDTO.getDriverlicensenumber())
					|| StringUtils.isEmpty(saveDTO.getNationality())
					|| StringUtils.isEmpty(saveDTO.getHouseholdregister()) || StringUtils.isEmpty(saveDTO.getNation())
					|| StringUtils.isEmpty(saveDTO.getMarriage())
					// || StringUtils.isEmpty(saveDTO.getForeignlanguage())
					|| StringUtils.isEmpty(saveDTO.getEducation())
					|| StringUtils.isEmpty(saveDTO.getFirstdrivinglicensedate())
					|| StringUtils.isEmpty(saveDTO.getFirstmeshworkdrivinglicensedate())
					|| StringUtils.isEmpty(saveDTO.getCorptype()) || StringUtils.isEmpty(saveDTO.getSigndate())
					|| StringUtils.isEmpty(saveDTO.getSigndateend()) || StringUtils.isEmpty(saveDTO.getContractdate())
					// || saveDTO.getIsxydriver() == null
					|| StringUtils.isEmpty(saveDTO.getXyDriverNumber())
					|| StringUtils.isEmpty(saveDTO.getParttimejobdri()) || StringUtils.isEmpty(saveDTO.getPhonetype())
					|| StringUtils.isEmpty(saveDTO.getPhonecorp())
					// || StringUtils.isEmpty(saveDTO.getEmergencycontactaddr())
					|| StringUtils.isEmpty(saveDTO.getDriverlicenseissuingdatestart())
					|| StringUtils.isEmpty(saveDTO.getDriverlicenseissuingdateend())
					|| StringUtils.isEmpty(saveDTO.getDriverlicenseissuingcorp())
					 || StringUtils.isEmpty(saveDTO.getDriverlicenseissuingnumber())
					// || StringUtils.isEmpty(saveDTO.getDriverLicenseIssuingRegisterDate())
					|| StringUtils.isEmpty(saveDTO.getDriverLicenseIssuingFirstDate())
					|| StringUtils.isEmpty(saveDTO.getDriverLicenseIssuingGrantDate())
					|| StringUtils.isEmpty(saveDTO.getBirthDay())
					|| StringUtils.isEmpty(saveDTO.getHouseHoldRegisterPermanent()) || groupId == null) {
				return AjaxResponse.fail(RestErrorCode.INFORMATION_NOT_COMPLETE);
			}
		}

		if (driverId != null) {
			logger.info("[ BusDriverInfoController-saveDriver ] 操作方式：编辑");
			//查询司机是否有服务中订单
			/*Boolean isInService = busCarBizDriverInfoService.isInService(driverId);
			if(isInService){
			return AjaxResponse.fail(RestErrorCode.IN_SERVICE);

			}*/

			// 司机获取派单的接口，是否可以修改
			Map<String, Object> updateDriverMap = carBizDriverInfoService.isUpdateDriver(driverId, phone);
			if (updateDriverMap != null && "2".equals(updateDriverMap.get("result").toString())) {
				return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, updateDriverMap.get("msg").toString());
			}
			//查询修改之前的数据
			AjaxResponse detail = findDriverInfoByDriverId(driverId);
			BusDriverDetailInfoVO data =(BusDriverDetailInfoVO) detail.getData();
			AjaxResponse response = null;
			if(data.getPhone().equals(saveDTO.getPhone()) && data.getIdCardNo().equals(saveDTO.getIdCardNo()) && data.getXyDriverNumber().equals(saveDTO.getXyDriverNumber())){
				 //直接修改
				response = busCarBizDriverInfoService.updateDriver(saveDTO);
				if(response.isSuccess()){
					busCarBizDriverInfoService.saveUpdateLog(data,driverId);
				}
			}else{
				//修改信息进入审核列表
				 response = busCarBizDriverInfoService.saveUpdateAuditDriverToMongo(saveDTO);
			}
			// 调用接口清除，key
			carBizDriverInfoService.flashDriverInfo(driverId);
			return response;
		} else {
			logger.info("[ BusDriverInfoController-saveDriver ] 操作方式：新建");
			AjaxResponse response = busCarBizDriverInfoService.saveAuditDriverToMongo(saveDTO);
			//AjaxResponse response = busCarBizDriverInfoService.saveDriver(saveDTO);
			return response;
		}

	}

	/**
	 * 根据物理主键查询审核司机详情
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/findAuditDriverInfoById")
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public AjaxResponse findAuditDriverInfoById(@NotNull(message = "主键id不能为空") String id) {
		logger.info("[ BusDriverInfoController-findAuditDriverInfoById ] 操作方式：查询审核司机详情");
		BusDriverInfoAudit busDriverInfoAudit = busCarBizDriverInfoService.findAuditDriverInfoById(id);
		if (busDriverInfoAudit == null) {
			return AjaxResponse.failMsg(RestErrorCode.DRIVER_NOT_EXIST, "司机不存在");
		}
		BusDriverDetailInfoVO busDriverDetailInfoVO = BeanUtil.copyObject(busDriverInfoAudit, BusDriverDetailInfoVO.class);
		// 查询城市名称，供应商名称，服务类型，加盟类型
		busCarBizDriverInfoService.getBaseStatis(busDriverDetailInfoVO);
		return AjaxResponse.success(busDriverDetailInfoVO);


	}
	/**
	 * 修改审核列表司机
	 * @param saveDTO
	 * @return
	 */
	@RequestMapping(value = "/updateAuditDriver")
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public AjaxResponse updateAuditDriver(@Validated BusDriverSaveDTO saveDTO) {
		//审核表司机物理主键
		if(StringUtils.isBlank(saveDTO.getId())){
			return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
		}
		/** 补充默认信息(用户不想填但业务需要的字段)*/
		AjaxResponse checkResult = busCarBizDriverInfoService.completeInfo(saveDTO);
		if (!checkResult.isSuccess()) {
			return checkResult;
		}
		//校验mongo中保存信息
		AjaxResponse checkMongoResult = busCarBizDriverInfoService.checkMongoInfo(saveDTO);
		if (!checkMongoResult.isSuccess()) {
			return checkMongoResult;
		}
		logger.info("[ BusDriverInfoController-updateAuditDriver ] 操作方式：编辑审核司机");
		AjaxResponse response = busCarBizDriverInfoService.updateAuditDriverToMongo(saveDTO);
		return response;


	}

	/**
	 * 审核司机
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/auditDriver")
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public AjaxResponse auditDriver(@NotNull(message = "主键id不能为空") String id) {

		logger.info("[ BusDriverInfoController-auditDriver ] 操作方式：审核司机");
		AjaxResponse response = busCarBizDriverInfoService.auditDriver(id);
		return response;


	}

	/*private void saveUpdateLog(BusDriverDetailInfoVO driverInfo,Integer driverId){
		try {
			BusDriverCompareEntity oldDriver = new BusDriverCompareEntity();
			BeanUtils.copyProperties(driverInfo,oldDriver);
			oldDriver.setStatus(driverInfo.getStatus()==1?"有效":"无效");
			oldDriver.setGender(driverInfo.getGender()==0?"女":"男");
			String oldLicenseType = this.getDrivingLicenseType(driverInfo.getDrivingLicenseType());
			oldDriver.setDrivingLicenseType(oldLicenseType);
			BusDriverCompareEntity newDriver=new BusDriverCompareEntity();
			//查询最新的信息
			AjaxResponse detail = findDriverInfoByDriverId(driverId);
			BusDriverDetailInfoVO carBizDriverInfo  = (BusDriverDetailInfoVO)detail.getData();
			BeanUtils.copyProperties(carBizDriverInfo,newDriver);
			newDriver.setStatus(carBizDriverInfo.getStatus()==1?"有效":"无效");
			newDriver.setGender(carBizDriverInfo.getGender()==0?"女":"男");
			String newLicenseType = this.getDrivingLicenseType(carBizDriverInfo.getDrivingLicenseType());
			newDriver.setDrivingLicenseType(newLicenseType);
			List<Object> objects = CompareObjectUtils.contrastObj(oldDriver, newDriver, null);
			if(objects.size()!=0){
				String join = StringUtils.join(objects, ",");
				busBizChangeLogService.insertLog(BusinessType.DRIVER, String.valueOf(driverId),join, new Date());
			}
		} catch (BeansException e) {
			logger.error("[ BusDriverInfoController-saveUpdateLog ] 保存操作日志异常", e);
		}
	}*/

	/*private String getDrivingLicenseType(String drivingLicenseType){
		String value="";
		switch (drivingLicenseType) {
			case "1":
				value = "A1";
				break;
			case "2":
				value = "A2";
				break;
			case "3":
				value = "B1";
				break;
			case "4":
				value = "B2";
				break;
			case "5":
				value = "C1";
				break;
			case "6":
				value = "C2";
				break;
			default:
				value = drivingLicenseType;
				break;
		}
		return value;
	}
*/


	/**
	 * @Title: resetIMEI
	 * @Description: 重置IMEI
	 * @param driverId
	 * @return 
	 * @return AjaxResponse
	 * @throws
	 */
	@RequestMapping(value = "/resetIMEI")
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public AjaxResponse resetIMEI(@NotNull(message = "司机ID不能为空") Integer driverId) {
		logger.info("[ BusDriverInfoController-resetIMEI ] 司机driverId={} 重置imei", driverId);
		
		CarBizDriverInfo carBizDriverInfo = carBizDriverInfoService.selectByPrimaryKey(driverId);
		if (carBizDriverInfo == null) {
			return AjaxResponse.fail(RestErrorCode.DRIVER_NOT_EXIST);
		}
		int i = busCarBizDriverInfoService.resetIMEI(driverId);
		if(i>0){
			busBizChangeLogService.insertLog(BusinessType.DRIVER, String.valueOf(driverId),"重置IMEI", new Date());
		}
		return AjaxResponse.success(null);
	}

	
    /**
     * @Title: updateDriverStatus
     * @Description: 修改司机状态信息 ,司机设置为无效后释放其绑定的车辆
     * @param driverId
     * @return 
     * @return AjaxResponse
     * @throws
     */
    @RequestMapping(value = "/updateDriverStatus")
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public AjaxResponse updateDriverStatus(@NotNull(message = "司机ID不能为空") Integer driverId) {
    	logger.info("[ BusDriverInfoController-updateDriverStatus ] 司机driverId={} 状态置为无效", driverId);

        CarBizDriverInfo carBizDriverInfo = carBizDriverInfoService.selectByPrimaryKey(driverId);
        if(carBizDriverInfo==null){
            return AjaxResponse.fail(RestErrorCode.DRIVER_NOT_EXIST);
        }
        // 司机获取派单的接口，是否可以修改
        Map<String, Object> updateDriverMap = carBizDriverInfoService.isUpdateDriver(driverId, carBizDriverInfo.getPhone());
        if(updateDriverMap!=null && (int)updateDriverMap.get("result")==2){
            return AjaxResponse.fail(RestErrorCode.CAR_API_ERROR, updateDriverMap.get("msg").toString());
        }
        
        // 允许修改
        try {
            // 调用接口清除，key
            carBizDriverInfoService.flashDriverInfo(driverId);
        } catch (Exception e) {
            logger.error("[ BusDriverInfoController-updateDriverStatus ] 司机driverId={} 状态置为无效,调用清除接口异常={}", driverId, e.getMessage(), e);
        }
        // 获取当前用户Id
        carBizDriverInfo.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
        carBizDriverInfo.setStatus(0);
        
        // 复制对象
        BusDriverSaveDTO driverSaveDTO = new BusDriverSaveDTO();
        BeanUtils.copyProperties(carBizDriverInfo, driverSaveDTO);
        int rtn = 0;
        try {
            rtn = busCarBizDriverInfoService.updateDriverByXiao(driverSaveDTO);
        } catch (Exception e) {
           logger.error("[ BusDriverInfoController-updateDriverStatus ] 司机driverId={},状态置为无效,释放车辆资源error={}", driverId, e.getMessage(), e);
        }
        if (rtn == 0) {
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }
        // 创建操作记录
        busBizChangeLogService.insertLog(BusinessType.DRIVER, String.valueOf(driverId),"置为无效", new Date());
        try {
            // 查询城市名称，供应商名称，服务类型，加盟类型
        	busCarBizDriverInfoService.getBaseStatis(driverSaveDTO);
        	busCarBizDriverInfoService.sendDriverToMq(driverSaveDTO, "DELETE");
        } catch (Exception e) {
            logger.error("[ BusDriverInfoController-updateDriverStatus ] 司机driverId={},状态置为,发MQ出现error={}", driverId, e.getMessage(), e);
        }
        return AjaxResponse.success(null);
    }
    
	/**
	 * @Title: findDriverInfoByDriverId
	 * @Description: 查询司机信息
	 * @param driverId
	 * @return 
	 * @return AjaxResponse
	 * @throws
	 */
	@RequestMapping(value = "/findDriverInfoByDriverId")
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public AjaxResponse findDriverInfoByDriverId(@NotNull(message = "司机ID不能为空") Integer driverId) {

		CarBizDriverInfo carBizDriverInfo = carBizDriverInfoService.selectByPrimaryKey(driverId);
		if (carBizDriverInfo == null) {
			return AjaxResponse.failMsg(RestErrorCode.DRIVER_NOT_EXIST, "司机不存在");
		}
		BusDriverDetailInfoVO busDriverDetailInfoVO = BeanUtil.copyObject(carBizDriverInfo, BusDriverDetailInfoVO.class);
		// 查询司机银行卡信息
		CarBizDriverInfoDetailDTO carBizDriverInfoDetailDTO = carBizDriverInfoDetailService.selectByDriverId(driverId);
		if (carBizDriverInfoDetailDTO != null) {
			busDriverDetailInfoVO.setBankCardNumber(carBizDriverInfoDetailDTO.getBankCardNumber());
			busDriverDetailInfoVO.setBankCardBank(carBizDriverInfoDetailDTO.getBankCardBank());
		}
		// 查询城市名称，供应商名称，服务类型，加盟类型
		busCarBizDriverInfoService.getBaseStatis(busDriverDetailInfoVO);
		return AjaxResponse.success(busDriverDetailInfoVO);
	}
	
	
	/**
	 * @Title: exportDriverList
	 * @Description: 导出司机列表信息
	 * @param exportDTO
	 * @param request
	 * @param response 
	 * @return void
	 * @throws
	 */
	@SuppressWarnings("resource")
	@RequestMapping(value = "/exportDriverList")
	public void exportDriverList(BusDriverQueryDTO exportDTO, HttpServletRequest request,
			HttpServletResponse response) {
		long start = System.currentTimeMillis(); // 获取开始时间
		try {
			// 数据权限控制SSOLoginUser
			Set<Integer> permOfCity = WebSessionUtil.getCurrentLoginUser().getCityIds(); // 普通管理员可以管理的所有城市ID
			Set<Integer> permOfSupplier = WebSessionUtil.getCurrentLoginUser().getSupplierIds(); // 普通管理员可以管理的所有供应商ID
			Set<Integer> permOfTeam = WebSessionUtil.getCurrentLoginUser().getTeamIds(); // 普通管理员可以管理的所有车队ID
			exportDTO.setAuthOfCity(permOfCity);
			exportDTO.setAuthOfSupplier(permOfSupplier);
			exportDTO.setAuthOfTeam(permOfTeam);

			// 文件名
			LocalDateTime now = LocalDateTime.now();
			String suffix = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(now);
			String fileName = "司机信息" + suffix + ".csv";
			String agent = request.getHeader("User-Agent").toUpperCase(); // 获得浏览器信息并转换为大写
			if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO") > 0 && agent.indexOf("RV:11") > 0)) { // IE浏览器和Edge浏览器
				fileName = URLEncoder.encode(fileName, "UTF-8");
			} else { // 其他浏览器
				fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
			}

			CsvUtils utilEntity = new CsvUtils();
			// 表头
			List<String> csvHeaderList = new ArrayList<>();
			String headerStr = "城市,供应商,司机姓名,性别,车型类别,司机身份证号,司机手机号,出生日期,驾照类型,驾驶证号,驾照领证日期,道路运输从业资格证编号,司机评分";
			csvHeaderList.add(headerStr);

			// 查询车队、小组查询司机
			Set<Integer> driverIds = null;
			if (permOfTeam != null && !permOfTeam.isEmpty()) {
				driverIds = busCarDriverTeamService.selectDriverIdsByTeamIdOrGroupId(null, null, permOfTeam);
				if (driverIds == null || driverIds.isEmpty()) {
					logger.info("[ BusDriverInfoController-exportDriverList ] 当前用户拥有的车队权限teamIds={}没有司机信息", permOfTeam);
					// 数据区
					List<String> csvDataList = new ArrayList<>();
					csvDataList.add("没有查到符合条件的数据");
					utilEntity.exportCsvV2(response, csvDataList, csvHeaderList, fileName, true, true);
					return;
				}
			}
			// 添加查询条件
			exportDTO.setDriverIds(driverIds);
			
			/**导出逻辑*/
			int pageNum = 0;
			int pages = 1;
			boolean isFirst = true;
			boolean isLast = false;
			do {
				// 页码+1
				pageNum++;
				
				// 查询数据
				exportDTO.setPageNum(pageNum);
				exportDTO.setPageSize(CsvUtils.downPerSize);
				List<BusDriverInfoExportVO> list = busCarBizDriverInfoService.queryDriverExportList(exportDTO);
				Page<BusDriverInfoExportVO> page = (Page<BusDriverInfoExportVO>) list;
				// 总页数(以第一次查询结果为准)
				if (pageNum == 1 && page != null) {
					pages = page.getPages();
				}
				// 判断是否为第一页
				if (pageNum > 1) {
					isFirst = false;
				}
				// 判断是否为最后一页
				if (pages <= 1 || pageNum == pages) {
					isLast = true;
				}
				
				// 数据区
				// 如果查询结果为空
				if (list == null || list.isEmpty()) {
					logger.info("[ BusDriverInfoController-exportDriverList ] 导出条件params={}没有查询出对应的司机信息", JSON.toJSONString(exportDTO));
					if (isFirst) {
						List<String> csvDataList = new ArrayList<>();
						csvDataList.add("没有查到符合条件的数据");
						utilEntity.exportCsvV2(response, csvDataList, csvHeaderList, fileName, true, true);
					}
					break;
				}
				// 导出查询数据
				List<String> csvDataList = busCarBizDriverInfoService.completeDriverExportList(list);// 补充其它字段
				utilEntity.exportCsvV2(response, csvDataList, csvHeaderList, fileName, isFirst, isLast);
			} while (!isLast);// 不到最后一页则继续导出

			// 获取结束时间
			long end = System.currentTimeMillis();
			logger.info("司机导出成功，参数为：" + JSON.toJSONString(exportDTO) + ",耗时=" + (end - start) + "ms");
		} catch (Exception e) {
			// 获取结束时间
			long end = System.currentTimeMillis();
			logger.error("司机导出成功，参数为：" + JSON.toJSONString(exportDTO) + ",耗时=" + (end - start) + "ms", e);
		}
	}
	
	/**
	 * @Title: fileDownloadDriverInfo
	 * @Description: 下载司机导入模板
	 * @param request
	 * @param response 
	 * @return void
	 * @throws
	 */
	@RequestMapping(value = "/downloadDriverInfoImportTemplate")
	public void fileDownloadDriverInfo(HttpServletRequest request, HttpServletResponse response) throws Exception{
		//文件名
		String fileName = BusConstant.DriverContant.FILE_NAME;
		//性别
		String[] genders = {"男","女"};
		//车型类别
		List<Map<Object, Object>> maps = commonService.queryGroups();
		String[] groupNames = new String[maps.size()];
		for(int i=0;i<maps.size();i++){
			groupNames[i]=(String) maps.get(i).get("groupName");
		}
		//驾照类型
		String[] drivingLicenseTypes = BusConst.DRIVING_LICENSE_TYPES;

		List<String[]> downdata = new ArrayList<>();
		downdata.add(genders);
		downdata.add(groupNames);
		downdata.add(drivingLicenseTypes);
		//下拉框位置
		String[] downRows={"3","4","8"};
		String[] heads=null;
		List contentList=new ArrayList();
		BusDriverImportTemplateVO templateVO=new BusDriverImportTemplateVO();
		templateVO.setCityName("北京");
		templateVO.setSupplierName("测试集团01");
		templateVO.setName("例子");
		templateVO.setGender("男");
		templateVO.setGroupName("巴士49座");
		templateVO.setIdCardNo("130102199003079710");
		templateVO.setPhone("13712344567");
		SimpleDateFormat sf =new SimpleDateFormat("yyyy-MM-dd");
		templateVO.setBirthDay(sf.parse("1990-01-01"));
		templateVO.setDrivingLicenseType("A1");
		templateVO.setDriverLicenseNumber("1234567890");
		templateVO.setIssueDate(sf.parse("2010-01-01"));
		templateVO.setXyDriverNumber("1234567890");
		//判断是否是运营角色
		boolean roleBoolean = commonService.ifOperate();
		if(roleBoolean){
			heads=BusConstant.DriverContant.TEMPLATE_HEAD;
			contentList.add(templateVO);
		}else{
			//非运营角色下载的模板不包括城市和供应商
			heads=new String[BusConstant.DriverContant.TEMPLATE_HEAD.length-2];
			System.arraycopy(BusConstant.DriverContant.TEMPLATE_HEAD,2,heads,0, BusConstant.DriverContant.TEMPLATE_HEAD.length-2);
			downRows[0]="1";
			downRows[1]="2";
			downRows[2]="6";
			BusDriverBaseImportTemplateVO baseImportTemplateVO=new BusDriverBaseImportTemplateVO();
			BeanUtils.copyProperties(templateVO,baseImportTemplateVO);
			contentList.add(baseImportTemplateVO);
		}
		ExportExcelUtil.exportExcel(fileName, heads, downdata, downRows, contentList,request, response);
	}
	
    /**
     * @Title: batchInputDriverInfo
     * @Description: 导入司机信息
     * @param cityId
     * @param supplierId
     * @param file
     * @param request
     * @param response
     * @return 
     * @return AjaxResponse
     * @throws
     */
    @RequestMapping(value = "/importDriverInfo")
	public AjaxResponse batchInputDriverInfo(@NotNull(message = "请选择城市") Integer cityId,
			@NotNull(message = "请选择供应商") Integer supplierId, MultipartFile file, HttpServletRequest request,
			HttpServletResponse response) {
		if (file == null || file.isEmpty()) {
			logger.info("file is empty!");
			return AjaxResponse.failMsg(RestErrorCode.FILE_ERROR, "导入文件不能为空");
		}

        AjaxResponse result = busCarBizDriverInfoService.batchInputDriverInfo(cityId, supplierId, file, request, response);
        return result;
    }

	/**
	 * 解锁
	 * @return
	 */
    @RequestMapping("/unlock")
    public AjaxResponse unlock(@Verify(param="phone",rule="mobile")String phone) {
		Map<String, Object> param = new HashMap(2);
		param.put("phoneNumber", phone);
		try {
			JSONObject result = MpOkHttpUtil.okHttpPostBackJson(mp_rest_url + "/api/v1/driver/delete/busLockKey", param, 2000, "解除被锁定的司机");
			Integer code = result.getInteger("code");
			if (code == 0) {
				//发送短信
				SmsSendUtil.sendTemplate(phone, 207,new ArrayList());
				return AjaxResponse.success(null);
			} else if (code == 1102) {
				return AjaxResponse.fail(RestErrorCode.DRIVER_NOT_LOCKED);
			} else {
				logger.error("解除司机锁定异常：参数:phone=" + phone + " 结果：" + JSON.toJSONString(result));
				return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
			}
		} catch (Exception e) {
			logger.error("解除司机锁定异常：参数:phone=" + phone + " e：{}", e);
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}
	}
}
