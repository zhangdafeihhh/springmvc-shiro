package com.zhuanche.controller.busManage;

import java.io.File;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.datavalidate.custom.InArray;
import com.zhuanche.dto.busManage.BusDriverQueryDTO;
import com.zhuanche.dto.busManage.BusDriverSaveDTO;
import com.zhuanche.dto.rentcar.CarBizDriverInfoDetailDTO;
import com.zhuanche.entity.rentcar.CarBizDriverInfo;
import com.zhuanche.serv.CarBizDriverInfoDetailService;
import com.zhuanche.serv.CarBizDriverInfoService;
import com.zhuanche.serv.busManage.BusCarBizDriverInfoService;
import com.zhuanche.serv.busManage.BusCarDriverTeamService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.excel.CsvUtils;
import com.zhuanche.vo.busManage.BusDriverDetailInfoVO;
import com.zhuanche.vo.busManage.BusDriverInfoExportVO;
import com.zhuanche.vo.busManage.BusDriverInfoPageVO;

@RestController
@RequestMapping("/bus/driverInfo")
public class BusDriverInfoController implements BusFileDownload {

	private static final Logger logger = LoggerFactory.getLogger(BusDriverInfoController.class);

	// ===========================专车业务拓展service==================================
	@Autowired
	private CarBizDriverInfoService carBizDriverInfoService;

	@Autowired
	private CarBizDriverInfoDetailService carBizDriverInfoDetailService;

	// ===========================巴士业务拓展service==================================
	@Autowired
	private BusCarBizDriverInfoService busCarBizDriverInfoService;

	@Autowired
	private BusCarDriverTeamService busCarDriverTeamService;

	/**
	 * @Title: findDriverList
	 * @Description: 查询司机列表
	 * @param queryDTO
	 * @return AjaxResponse
	 * @throws
	 */
	@SuppressWarnings("resource")
	@RequestMapping(value = "/findDriverList")
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE) })
	public AjaxResponse findDriverList(BusDriverQueryDTO queryDTO) {

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

		// TODO 司机订单量、司机平均评分
		list.forEach(driver -> {
			driver.setFinishedOrderCount(null);
			driver.setAverage(null);
		});

		Page<BusDriverInfoPageVO> page = (Page<BusDriverInfoPageVO>) list;
		pageDTO = new PageDTO(page.getPageNum(), page.getPageSize(), page.getTotal(), list);
		return AjaxResponse.success(pageDTO);
	}

	@RequestMapping(value = "/saveDriver")
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE) })
	@Validated
	public AjaxResponse saveDriver(BusDriverSaveDTO saveDTO) {
		
		/** 补充默认信息(用户不想填但业务需要的字段)*/
		AjaxResponse checkResult = busCarBizDriverInfoService.completeInfo(saveDTO);
		if (!checkResult.isSuccess()) {
			return checkResult;
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

		// TODO 创建操作记录
		
		if (driverId != null) {
			logger.info("[ BusDriverInfoController-saveDriver ] 操作方式：编辑");
			// 司机获取派单的接口，是否可以修改
			Map<String, Object> updateDriverMap = carBizDriverInfoService.isUpdateDriver(driverId, phone);
			if (updateDriverMap != null && "2".equals(updateDriverMap.get("result").toString())) {
				return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR, updateDriverMap.get("msg").toString());
			}
			try {
				// 调用接口清除，key
				carBizDriverInfoService.flashDriverInfo(driverId);
			} catch (Exception e) {
				logger.error("[ BusDriverInfoController-saveDriver ] 司机driverId={},修改调用清除接口异常={}", driverId, e.getMessage(), e);
			}
			return busCarBizDriverInfoService.updateDriver(saveDTO);
		} else {
			logger.info("[ BusDriverInfoController-saveDriver ] 操作方式：新建");
			return busCarBizDriverInfoService.saveDriver(saveDTO);
		}
	}

	/**
	 * 重置IMEI
	 * 
	 * @param driverId
	 * @return
	 */
	@RequestMapping(value = "/resetIMEI")
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE) })
	@Validated
	public AjaxResponse resetIMEI(@NotNull(message = "司机ID不能为空") Integer driverId) {

		logger.info("[ BusDriverInfoController-resetIMEI ] 司机driverId={} 重置imei", driverId);
		CarBizDriverInfo carBizDriverInfo = carBizDriverInfoService.selectByPrimaryKey(driverId);
		if (carBizDriverInfo == null) {
			return AjaxResponse.fail(RestErrorCode.DRIVER_NOT_EXIST);
		}
		carBizDriverInfoService.resetIMEI(driverId);
		return AjaxResponse.success(null);
	}
	
	/**
     * 修改司机状态信息 ,司机设置为无效后释放其绑定的车辆
     * @param driverId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updateDriverStatus")
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE )
    } )
    @Validated
	public AjaxResponse updateDriverStatus(@NotNull(message = "司机ID不能为空") Integer driverId,
			@NotNull(message = "司机状态不能为空") @InArray(values = { "0", "1" }, message = "司机状态不在有效范围内") Integer status) {

        logger.info("[ BusDriverInfoController-updateDriverStatus ] 司机driverId={} 状态置为status={}", driverId, status);
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
            logger.error("[ BusDriverInfoController-updateDriverStatus ] 司机driverId={} 状态置为status={},调用清除接口异常={}", driverId, status, e.getMessage(), e);
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
           logger.error("[ BusDriverInfoController-updateDriverStatus ] 司机driverId={},状态置为status={},释放车辆资源error={}", driverId, status, e.getMessage(), e);
        }
        if (rtn == 0) {
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }
        try {
            // 查询城市名称，供应商名称，服务类型，加盟类型
        	busCarBizDriverInfoService.getBaseStatis(driverSaveDTO);
        	busCarBizDriverInfoService.sendDriverToMq(driverSaveDTO, "DELETE");
        } catch (Exception e) {
            logger.error("[ BusDriverInfoController-updateDriverStatus ] 司机driverId={},状态置为status={},发MQ出现error={}", driverId, status, e.getMessage(), e);
        }
        return AjaxResponse.success(null);
    }
    
	/**
	 * 司机信息
	 * 
	 * @param driverId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/findDriverInfoByDriverId")
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE) })
	@Validated
	public AjaxResponse findDriverInfoByDriverId(@NotNull(message = "司机ID不能为空") Integer driverId) {

		CarBizDriverInfo carBizDriverInfo = carBizDriverInfoService.selectByPrimaryKey(driverId);
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
	
	
	@SuppressWarnings("resource")
	@ResponseBody
	@RequestMapping(value = "/exportDriverList")
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE) })
	@Validated(BusDriverQueryDTO.Export.class)
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
			String headerStr = "城市,供应商,司机姓名,性别,车型类别,司机身份证号,司机手机号,出生日期,驾照类型,驾驶证号,驾照领证日期,道路运输从业资格证编号";
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
	public void fileDownloadDriverInfo(HttpServletRequest request, HttpServletResponse response) {
		String path = request.getSession().getServletContext().getRealPath("/upload") + File.separator
				+ "IMPORT_BUS_DRIVER_INFO.xlsx";
		fileDownload(request, response, path);
	}
	
	/**
     * 导入司机信息
     * @param cityId
     * @param supplierId
     * @param teamId
     * @param teamGroupId
     * @param fileName
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/importDriverInfo")
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE )
    } )
    @Validated
	public AjaxResponse batchInputDriverInfo(@NotNull(message = "请选择城市") Integer cityId,
			@NotNull(message = "请选择供应商") Integer supplierId, MultipartFile file, HttpServletRequest request,
			HttpServletResponse response) {

        if (file.isEmpty()) {
            logger.info("file is empty!");
            return AjaxResponse.fail(RestErrorCode.FILE_ERROR);
        }

        AjaxResponse result = busCarBizDriverInfoService.batchInputDriverInfo(cityId, supplierId, file, request, response);
        return result;
    }

}
