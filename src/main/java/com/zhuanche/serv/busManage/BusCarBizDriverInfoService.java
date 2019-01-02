package com.zhuanche.serv.busManage;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.rocketmq.CommonRocketProducer;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constants.BusConst;
import com.zhuanche.dto.busManage.BusBaseStatisDTO;
import com.zhuanche.dto.busManage.BusDriverQueryDTO;
import com.zhuanche.dto.busManage.BusDriverSaveDTO;
import com.zhuanche.dto.rentcar.CarBizCarInfoDTO;
import com.zhuanche.dto.rentcar.CarBizDriverInfoDetailDTO;
import com.zhuanche.entity.mdbcarmanage.CarAdmUser;
import com.zhuanche.entity.mdbcarmanage.CarBizAgreementCompany;
import com.zhuanche.entity.mdbcarmanage.CarRelateGroup;
import com.zhuanche.entity.mdbcarmanage.CarRelateTeam;
import com.zhuanche.entity.rentcar.CarBizCarGroup;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.entity.rentcar.CarBizCooperationType;
import com.zhuanche.entity.rentcar.CarBizDriverAccount;
import com.zhuanche.entity.rentcar.CarBizDriverInfo;
import com.zhuanche.entity.rentcar.CarBizDriverInfoDetail;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.mongo.DriverMongo;
import com.zhuanche.serv.CarBizChatUserService;
import com.zhuanche.serv.CarBizDriverInfoDetailService;
import com.zhuanche.serv.CarBizDriverInfoService;
import com.zhuanche.serv.mdbcarmanage.CarBizDriverUpdateService;
import com.zhuanche.serv.mongo.BusDriverMongoService;
import com.zhuanche.serv.mongo.DriverMongoService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.ValidateUtils;
import com.zhuanche.util.excel.CsvUtils;
import com.zhuanche.vo.busManage.BusDriverInfoExportVO;
import com.zhuanche.vo.busManage.BusDriverInfoPageVO;

import mapper.mdbcarmanage.CarAdmUserMapper;
import mapper.mdbcarmanage.CarRelateGroupMapper;
import mapper.mdbcarmanage.CarRelateTeamMapper;
import mapper.mdbcarmanage.ex.BusBizChangeLogExMapper.BusinessType;
import mapper.mdbcarmanage.ex.CarBizAgreementCompanyExMapper;
import mapper.mdbcarmanage.ex.CarDriverTeamExMapper;
import mapper.mdbcarmanage.ex.CarRelateGroupExMapper;
import mapper.mdbcarmanage.ex.CarRelateTeamExMapper;
import mapper.rentcar.CarBizCarGroupMapper;
import mapper.rentcar.CarBizCityMapper;
import mapper.rentcar.CarBizCooperationTypeMapper;
import mapper.rentcar.CarBizDriverAccountMapper;
import mapper.rentcar.CarBizDriverInfoMapper;
import mapper.rentcar.CarBizSupplierMapper;
import mapper.rentcar.ex.BusCarBizDriverInfoExMapper;
import mapper.rentcar.ex.CarBizCarGroupExMapper;
import mapper.rentcar.ex.CarBizCarInfoExMapper;
import mapper.rentcar.ex.CarBizDriverInfoExMapper;

/**
 * @ClassName:  BusCarBizDriverInfoService
 * @Description: 
 * @author: yanyunpeng
 * @date:   2018年12月17日 下午7:49:33
 * 
 */
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BusCarBizDriverInfoService implements BusConst{

	private static final Logger logger = LoggerFactory.getLogger(BusCarBizDriverInfoService.class);

	// ===========================表基础mapper==================================
	@Autowired
	private CarBizCityMapper carBizCityMapper;

	@Autowired
	private CarBizSupplierMapper carBizSupplierMapper;

	@Autowired
	private CarBizCarGroupMapper carBizCarGroupMapper;

	@Autowired
	private CarBizCooperationTypeMapper carBizCooperationTypeMapper;

	@Autowired
	private CarAdmUserMapper carAdmUserMapper;

	@Autowired
	private CarBizDriverInfoMapper carBizDriverInfoMapper;

	@Autowired
	private CarBizDriverAccountMapper carBizDriverAccountMapper;

	@Autowired
	private CarRelateTeamMapper carRelateTeamMapper;

	@Autowired
	private CarRelateGroupMapper carRelateGroupMapper;

	// ===========================专车业务拓展mapper==================================
	@Autowired
	private CarBizCarInfoExMapper carBizCarInfoExMapper;

	@Autowired
	private CarBizDriverInfoExMapper carBizDriverInfoExMapper;

	@Autowired
	private CarDriverTeamExMapper carDriverTeamExMapper;

	@Autowired
	private CarRelateTeamExMapper carRelateTeamExMapper;

	@Autowired
	private CarRelateGroupExMapper carRelateGroupExMapper;

	@Autowired
	private CarBizCarGroupExMapper carBizCarGroupExMapper;

	@Autowired
	private CarBizAgreementCompanyExMapper carBizAgreementCompanyExMapper;

	// ===========================巴士业务拓展mapper==================================
	@Autowired
	private BusCarBizDriverInfoExMapper busCarBizDriverInfoExMapper;
	
	// ===========================专车业务拓展service==================================
	@Autowired
	private CarBizDriverInfoService carBizDriverInfoService;

	@Autowired
	private CarBizDriverInfoDetailService carBizDriverInfoDetailService;

	@Autowired
	private CarBizDriverUpdateService carBizDriverUpdateService;

	@Autowired
	private CarBizChatUserService carBizChatUserService;

	// ===========================巴士业务拓展service==================================
	@Autowired
	private BusBizChangeLogService busBizChangeLogService;

	// ===============================专车其它服务===================================
	@Autowired
	private DriverMongoService driverMongoService;

	// ===============================巴士其它服务===================================
	@Autowired
	private BusDriverMongoService busDriverMongoService;

	/**
	 * @Title: queryDriverList 
	 * @Description: 分页查询司机信息列表
	 * 数据量较大的表不做表关联查询，单表查询组装数据
	 * @param queryDTO 
	 * @return List<BusDriverInfoVO> 
	 * @throws
	 */
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public List<BusDriverInfoPageVO> queryDriverPageList(BusDriverQueryDTO queryDTO) {

		logger.info("[ BusCarBizDriverInfoService-queryDriverPageList ] 查询司机列表params={}", JSON.toJSONString(queryDTO));
		List<BusDriverInfoPageVO> driverList = busCarBizDriverInfoExMapper.queryDriverPageList(queryDTO);

		if (driverList == null) {
			return new ArrayList<>();
		}

		driverList.forEach(driver -> {
			// 根据城市ID查找城市名称
			if (driver.getCityId() != null) {
				CarBizCity city = carBizCityMapper.selectByPrimaryKey(driver.getCityId());
				if (city != null) {
					driver.setCityName(city.getCityName());
				}
			}
			// 根据供应商ID查询供应商名称以及加盟类型
			if (driver.getSupplierId() != null) {
				CarBizSupplier supplier = carBizSupplierMapper.selectByPrimaryKey(driver.getSupplierId());
				if (supplier != null) {
					driver.setSupplierName(supplier.getSupplierFullName());
				}
			}
		});

		return driverList;
	}
	
	/**
	 * @Title: queryDriverExportList
	 * @Description: 查询导出数据
	 * @param exportDTO
	 * @return List<BusDriverInfoVO>
	 * @throws
	 */
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public List<BusDriverInfoExportVO> queryDriverExportList(BusDriverQueryDTO exportDTO) {
		logger.info("[ BusCarBizDriverInfoService-queryDriverExportList ] 导出司机列表params={}", JSON.toJSONString(exportDTO));
		List<BusDriverInfoExportVO> driverList = busCarBizDriverInfoExMapper.queryDriverExportList(exportDTO);
		return driverList;
	}
	
	/**
	 * @return 
	 * @Title: completeDriverExportList
	 * @Description: 补充导出数据
	 * @param list
	 * @param csvDataList 
	 * @return void
	 * @throws
	 */
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public List<String> completeDriverExportList(List<BusDriverInfoExportVO> list) {

		// 返回结果
		List<String> csvDataList = new ArrayList<>();
		if (list == null || list.isEmpty()) {
			return csvDataList;
		}

		list.forEach(driver -> {
			/** 行数据 */
			StringBuilder builder = new StringBuilder();

			// 一、城市名称
			String ciytName = "";
			if (driver.getCityId() != null) {
				CarBizCity city = carBizCityMapper.selectByPrimaryKey(driver.getCityId());
				if (city != null) {
					ciytName = city.getCityName();
				}
			}
			builder.append(CsvUtils.tab).append(StringUtils.defaultIfBlank(ciytName, "")).append(",");

			// 二、供应商名称
			String supplierName = "";
			if (driver.getSupplierId() != null) {
				CarBizSupplier supplier = carBizSupplierMapper.selectByPrimaryKey(driver.getSupplierId());
				if (supplier != null) {
					supplierName = supplier.getSupplierFullName();
				}
			}
			builder.append(CsvUtils.tab).append(StringUtils.defaultIfBlank(supplierName, "")).append(",");

			// 三、司机姓名
			builder.append(CsvUtils.tab).append(StringUtils.defaultIfBlank(driver.getName(), "")).append(",");

			// 四、司机性别
			String gender = "";
			if (driver.getGender() == 1) {
				gender = "男";
			} else {
				gender = "女";
			}
			builder.append(CsvUtils.tab).append(gender).append(",");

			// 五、车型类别
			builder.append(CsvUtils.tab).append(StringUtils.defaultIfBlank(driver.getGroupName(), "")).append(",");

			// 六、身份证号
			builder.append(CsvUtils.tab).append(StringUtils.defaultIfBlank(driver.getIdCardNo(), "")).append(",");

			// 七、手机号
			builder.append(CsvUtils.tab).append(StringUtils.defaultIfBlank(driver.getPhone(), "")).append(",");

			// 八、出生日期
			builder.append(CsvUtils.tab).append(StringUtils.defaultIfBlank(driver.getBirthDay(), "")).append(",");

			// 九、驾照类型
			String drivingLicenseType = "";
			switch (driver.getDrivingLicenseType()) {
			case "1":
				drivingLicenseType = "A1";
				break;
			case "2":
				drivingLicenseType = "A2";
				break;
			case "3":
				drivingLicenseType = "B1";
				break;
			case "4":
				drivingLicenseType = "B2";
				break;
			case "5":
				drivingLicenseType = "C1";
				break;
			case "6":
				drivingLicenseType = "C2";
				break;
			default:
				drivingLicenseType = driver.getDrivingLicenseType();
				break;
			}
			builder.append(CsvUtils.tab).append(StringUtils.defaultIfBlank(drivingLicenseType, "")).append(",");

			// 十、机动车驾驶证号
			builder.append(CsvUtils.tab).append(StringUtils.defaultIfBlank(driver.getDriverlicensenumber(), "")).append(",");

			// 十一、驾照领证日期
			String issueDate = "";
			if (driver.getIssueDate() != null) {
				issueDate = formatDate(FORMATTER_DATE_BY_HYPHEN, driver.getIssueDate());
			}
			builder.append(CsvUtils.tab).append(StringUtils.defaultIfBlank(issueDate, "")).append(",");

			// 十二、道路运输从业资格证编号
			builder.append(CsvUtils.tab).append(StringUtils.defaultIfBlank(driver.getXyDriverNumber(), "")).append(",");

			csvDataList.add(builder.toString());
		});

		return csvDataList;
	}

	/**
	 * 修改司机信息
	 *
	 * @param saveDTO
	 * @return
	 */
	@MasterSlaveConfigs(configs = { @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE),
			@MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.SLAVE) })
	public AjaxResponse updateDriver(BusDriverSaveDTO saveDTO) {
		try {
			logger.info("操作方式：编辑,新数据:" + JSON.toJSONString(saveDTO));

			/** 查询城市名称，供应商名称，服务类型，加盟类型 */
			this.getBaseStatis(saveDTO);

			// 驾驶员合同（或协议）签署公司在协议公司 验证
			if (saveDTO.getCooperationType() != null && saveDTO.getCooperationType() == 5) {
				CarBizAgreementCompany company = carBizAgreementCompanyExMapper.selectByName(saveDTO.getCorptype());
				if (company == null) {
					return AjaxResponse.failMsg(RestErrorCode.HTTP_PARAM_INVALID, "驾驶员合同（或协议）签署公司在协议公司中不存在");
				}
			}

			// 获取当前用户Id
			saveDTO.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
			saveDTO.setUpdateDate(new Date());
			// 身份证号
			String idCardNo = saveDTO.getIdCardNo();
			if ("X".equals(idCardNo.substring(idCardNo.length() - 1, idCardNo.length()))) {
				idCardNo = idCardNo.toLowerCase();
			}
			saveDTO.setIdCardNo(idCardNo);
			// 机动车驾驶证号
			if (StringUtils.isBlank(saveDTO.getDriverlicensenumber())) {
				saveDTO.setDriverlicensenumber(idCardNo);
			}
			// 重置密码
			if (saveDTO.getPasswordReset() != null && saveDTO.getPasswordReset() == 1) {
				saveDTO.setPassword(carBizDriverInfoService.getPassword(idCardNo));
			}

			// 根据司机ID查询数据
			CarBizDriverInfo orginDriverInfo = carBizDriverInfoMapper.selectByPrimaryKey(saveDTO.getDriverId());
			if (orginDriverInfo != null) {
				saveDTO.setOldPhone(orginDriverInfo.getPhone());// 手机号
				saveDTO.setOldIdCardNo(orginDriverInfo.getIdCardNo());// 身份证
				saveDTO.setOldDriverLicenseNumber(orginDriverInfo.getDriverlicensenumber());// 机动车驾驶证号
				saveDTO.setOldDriverLicenseIssuingNumber(orginDriverInfo.getDriverlicenseissuingnumber());// 网络预约出租汽车驾驶员资格证号
				saveDTO.setOldLicensePlates(orginDriverInfo.getLicensePlates());// 车牌号
				saveDTO.setOldCity(orginDriverInfo.getServiceCity());// 城市
				saveDTO.setOldSupplier(orginDriverInfo.getSupplierId());// 供应商
				saveDTO.setCreateDate(orginDriverInfo.getCreateDate());
			}
			logger.info("操作方式：编辑,原始数据:" + JSON.toJSONString(saveDTO));

			// 更新司机信息
			DynamicRoutingDataSource.setMasterSlave("rentcar-DataSource", DataSourceMode.MASTER);
			int n = this.updateDriverInfo(saveDTO);

			// 更新车辆信息 根据 车牌号更新车辆 信息（更换车辆所属人）
			if (n > 0) {
				logger.info("****************根据 车牌号更新车辆 信息（更换车辆所属人）");
				 if (saveDTO.getLicensePlates() != null) {
					carBizCarInfoExMapper.updateCarLicensePlates(saveDTO.getLicensePlates(), saveDTO.getDriverId());
				}
			}

			// 更新mongoDB
			DriverMongo driverMongo = busDriverMongoService.findByDriverId(saveDTO.getDriverId());
			if (driverMongo != null) {
				busDriverMongoService.updateDriverMongo(saveDTO);
			} else {
				busDriverMongoService.saveDriverMongo(saveDTO);
			}

			// 将司机置为无效状态需释放车辆资源
			if (saveDTO.getStatus().intValue() == 0) {
				this.updateDriverByXiao(saveDTO);
			}

			// 城市或者供应商是否更换
			if ((saveDTO.getOldCity() != null && !saveDTO.getOldCity().equals(saveDTO.getServiceCity()))
					|| (saveDTO.getOldSupplier() != null
							&& !saveDTO.getOldSupplier().equals(saveDTO.getSupplierId()))) {
				logger.info("修改司机driverId=" + saveDTO.getDriverId() + "的城市或者供应商，需将司机移除车队小组");
				// 移除司机车队小组信息
				DynamicRoutingDataSource.setMasterSlave("mdbcarmanage-DataSource", DataSourceMode.MASTER);
				carRelateTeamExMapper.deleteByDriverId(saveDTO.getDriverId());
				carRelateGroupExMapper.deleteByDriverId(saveDTO.getDriverId());
				saveDTO.setTeamId(null);
				saveDTO.setTeamName("");
				saveDTO.setTeamGroupId(null);
				saveDTO.setTeamGroupName("");
			}

			// 发送MQ
			if (saveDTO.getStatus() == 0) {
				this.sendDriverToMq(saveDTO, "DELETE");
			} else {
				this.sendDriverToMq(saveDTO, "UPDATE");
			}

			try {
				// 司机变更部分信息，需要记录
				this.driverUpdate(saveDTO);
			} catch (Exception e) {
				logger.error("保存需要上报交通委的信息异常,error={}", e.getMessage(),e);
			}
			
			return AjaxResponse.success(null);
		} catch (Exception e) {
			logger.error("修改司机信息异常,error={}", e.getMessage(),e);
			return AjaxResponse.failMsg(RestErrorCode.HTTP_SYSTEM_ERROR, "修改司机信息异常");
		}
	}

	/**
	 * 修改司机信息，操作司机信息扩展表
	 *
	 * @param saveDTO
	 * @return
	 */
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.MASTER))
	public int updateDriverInfo(BusDriverSaveDTO saveDTO) {
		// 司机基础信息表
		busCarBizDriverInfoExMapper.updateBusDriverInfo(saveDTO);
		int id = saveDTO.getDriverId();

		// 创建操作记录
		busBizChangeLogService.insertLog(BusinessType.DRIVER, String.valueOf(id), saveDTO.getUpdateDate());

		// 司机信息扩展表，司机银行卡号
		CarBizDriverInfoDetailDTO infoDetail = carBizDriverInfoDetailService.selectByDriverId(saveDTO.getDriverId());
		CarBizDriverInfoDetail carBizDriverInfoDetail = new CarBizDriverInfoDetail();
		carBizDriverInfoDetail.setBankCardBank(saveDTO.getBankCardBank());
		carBizDriverInfoDetail.setBankCardNumber(saveDTO.getBankCardNumber());
		carBizDriverInfoDetail.setDriverId(saveDTO.getDriverId());
		if (infoDetail != null) {
			carBizDriverInfoDetailService.updateByPrimaryKeySelective(carBizDriverInfoDetail);
		} else {
			carBizDriverInfoDetail.setExt1(2);// 司机停运状态 1停运 2正常 司机新建是默认为2
			carBizDriverInfoDetailService.insertSelective(carBizDriverInfoDetail);
		}
		return id;
	}

	/**
	 * 更新司机状态，如果将司机置为无效则释放车辆资源
	 *
	 * @param saveDTO
	 * @return
	 */
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.MASTER))
	public int updateDriverByXiao(BusDriverSaveDTO saveDTO) {
		int rtn = 0;
		try {
			rtn = carBizDriverInfoExMapper.updateDriverByXiao(saveDTO.getDriverId());
			if (rtn > 0) {
				// 将司机置为无效
				if (saveDTO.getStatus().intValue() == 0 && saveDTO.getLicensePlates() != null) {
					// 根据车牌号更新车辆信息
					carBizCarInfoExMapper.updateCarLicensePlates(saveDTO.getLicensePlates(), 0);
				}
				// 更新司机mongo
				driverMongoService.updateByDriverId(saveDTO.getDriverId(), saveDTO.getStatus());

				try {
					carBizDriverUpdateService.insert(saveDTO.getLicensePlates(), "", saveDTO.getDriverId(), 1);
					carBizDriverUpdateService.insert(saveDTO.getPhone(), "", saveDTO.getDriverId(), 2);
				} catch (Exception e) {
					logger.info("updateDriverByXiao error:" + e);
				}
			}
		} catch (Exception e) {
			logger.info("updateDriverByXiao error:" + e);
		}
		return rtn;
	}

	/**
	 * @Title: driverUpdate
	 * @Description: 交通委需要修改以下信息的记录
	 * @param saveDTO void
	 * @throws
	 */
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.MASTER))
	public void driverUpdate(BusDriverSaveDTO saveDTO) {
		try {
			// 判断 车牌号是否修改 如果修改 释放 车牌号
			if (StringUtils.isNotEmpty(saveDTO.getOldLicensePlates())
					&& !saveDTO.getOldLicensePlates().equals(saveDTO.getLicensePlates())) {
				logger.info("****************修改车牌号 释放以前的车牌号");
				carBizCarInfoExMapper.updateCarLicensePlates(saveDTO.getOldLicensePlates(), 0);
				carBizDriverUpdateService.insert(saveDTO.getOldLicensePlates(), saveDTO.getLicensePlates(), saveDTO.getDriverId(), 1);
			}
			// 判断 手机号是否修改 如果修改 添加司机事件
			if (saveDTO.getOldPhone() != null && saveDTO.getOldPhone().length() >= 1
					&& !saveDTO.getOldPhone().equals(saveDTO.getPhone())) {
				logger.info("****************修改手机号");
				carBizDriverUpdateService.insert(saveDTO.getOldPhone(), saveDTO.getPhone(), saveDTO.getDriverId(), 2);
			}
			// 判断 身份证是否修改 如果修改 添加司机事件
			if (saveDTO.getOldIdCardNo() != null && saveDTO.getOldIdCardNo().length() >= 1
					&& !saveDTO.getOldIdCardNo().equals(saveDTO.getIdCardNo())) {
				logger.info("****************修改身份证");
				carBizDriverUpdateService.insert(saveDTO.getOldIdCardNo(), saveDTO.getIdCardNo(), saveDTO.getDriverId(), 3);
			}
			// 判断 机动车驾驶证号是否修改 如果修改 添加司机事件
			if (saveDTO.getOldDriverLicenseNumber() != null && saveDTO.getOldDriverLicenseNumber().length() >= 1
					&& !saveDTO.getOldDriverLicenseNumber().equals(saveDTO.getDriverlicensenumber())) {
				logger.info("****************修改 机动车驾驶证号");
				carBizDriverUpdateService.insert(saveDTO.getOldDriverLicenseNumber(), saveDTO.getDriverlicensenumber(), saveDTO.getDriverId(), 4);
			}
			// 判断 网络预约出租汽车驾驶员资格证号是否修改 如果修改 添加司机事件
			if (saveDTO.getOldDriverLicenseIssuingNumber() != null
					&& saveDTO.getOldDriverLicenseIssuingNumber().length() >= 1
					&& !saveDTO.getOldDriverLicenseIssuingNumber().equals(saveDTO.getDriverlicenseissuingnumber())) {
				logger.info("****************修改网络预约出租汽车驾驶员资格证号");
				carBizDriverUpdateService.insert(saveDTO.getOldDriverLicenseIssuingNumber(), saveDTO.getDriverlicenseissuingnumber(), saveDTO.getDriverId(), 5);
			}
		} catch (Exception e) {
			logger.info("driverUpdateService error:" + e);
		}
	}

	/**
	 * 保存司机信息
	 *
	 * @param saveDTO
	 * @return
	 */
	@MasterSlaveConfigs(configs = { @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE),
			@MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.SLAVE) })
	public AjaxResponse saveDriver(BusDriverSaveDTO saveDTO) {

		logger.info("操作方式：新建,数据:" + JSON.toJSONString(saveDTO));

		try {
			// 查询城市名称，供应商名称，服务类型，加盟类型
			this.getBaseStatis(saveDTO);
			// 驾驶员合同（或协议）签署公司在协议公司 验证
			if (saveDTO.getCooperationType() != null && saveDTO.getCooperationType() == 5) {
				CarBizAgreementCompany company = carBizAgreementCompanyExMapper.selectByName(saveDTO.getCorptype());
				if (company == null) {
					return AjaxResponse.failMsg(RestErrorCode.HTTP_PARAM_INVALID, "驾驶员合同（或协议）签署公司在协议公司中不存在");
				}
			}

			// 获取当前用户Id
			saveDTO.setCreateBy(WebSessionUtil.getCurrentLoginUser().getId());
			saveDTO.setCreateDate(new Date());
			saveDTO.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
			saveDTO.setUpdateDate(new Date());
			saveDTO.setStatus(1);
			// 身份证号
			String idCardNo = saveDTO.getIdCardNo();
			if ("X".equals(idCardNo.substring(idCardNo.length() - 1, idCardNo.length()))) {
				idCardNo = idCardNo.toLowerCase();
			}
			saveDTO.setIdCardNo(idCardNo);
			// 机动车驾驶证号
			if (StringUtils.isBlank(saveDTO.getDriverlicensenumber())) {
				saveDTO.setDriverlicensenumber(idCardNo);
			}
			// 密码
			saveDTO.setPassword(carBizDriverInfoService.getPassword(saveDTO.getIdCardNo()));

			// 插入司机信息到mysql，mongo
			DynamicRoutingDataSource.setMasterSlave("rentcar-DataSource", DataSourceMode.MASTER);
			int n = this.saveDriverInfo(saveDTO);
			busDriverMongoService.saveDriverMongo(saveDTO);

			if (n > 0) {
				// 根据 车牌号更新车辆 信息（更换车辆所属人）
				if (saveDTO.getLicensePlates() != null) {
					carBizCarInfoExMapper.updateCarLicensePlates(saveDTO.getLicensePlates(), saveDTO.getDriverId());
				}
			}
			
			// 保存用户关系及注册时间
			carBizChatUserService.insertChat(saveDTO.getDriverId());

			// teamId teamGroupId 存在，则新增车队与司机的关联表
			if (saveDTO.getTeamId() != null) {// 新增车队
				DynamicRoutingDataSource.setMasterSlave("mdbcarmanage-DataSource", DataSourceMode.MASTER);
				CarRelateTeam record = new CarRelateTeam();
				record.setTeamId(saveDTO.getTeamId());
				record.setDriverId(saveDTO.getDriverId());
				carRelateTeamMapper.insertSelective(record);
			}
			if (saveDTO.getTeamGroupId() != null) {// 新增小组
				DynamicRoutingDataSource.setMasterSlave("mdbcarmanage-DataSource", DataSourceMode.MASTER);
				CarRelateGroup record = new CarRelateGroup();
				record.setGroupId(saveDTO.getTeamGroupId());
				record.setDriverId(saveDTO.getDriverId());
				carRelateGroupMapper.insertSelective(record);
			}

			// 发送MQ
			sendDriverToMq(saveDTO, "INSERT");

			return AjaxResponse.success(null);
		} catch (Exception e) {
			logger.error("新增司机信息异常,error={}", e.getMessage(),e);
			return AjaxResponse.failMsg(RestErrorCode.HTTP_SYSTEM_ERROR, "保存司机信息异常");
		}
	}

	/**
	 * 新增司机信息
	 *
	 * @param saveDTO
	 * @return
	 */
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.MASTER))
	public int saveDriverInfo(BusDriverSaveDTO saveDTO) {
		
		busCarBizDriverInfoExMapper.insertBusDriverInfo(saveDTO);
		Integer driverId = saveDTO.getDriverId();

		// 创建操作记录
		busBizChangeLogService.insertLog(BusinessType.DRIVER, String.valueOf(driverId), saveDTO.getUpdateDate());

		// 司机信息扩展表，司机银行卡号
		CarBizDriverInfoDetail carBizDriverInfoDetail = new CarBizDriverInfoDetail();
		carBizDriverInfoDetail.setBankCardBank(saveDTO.getBankCardBank());
		carBizDriverInfoDetail.setBankCardNumber(saveDTO.getBankCardNumber());
		carBizDriverInfoDetail.setDriverId(saveDTO.getDriverId());
		carBizDriverInfoDetail.setExt1(2);// 司机停运状态 1停运 2正常 司机新建是默认为2
		carBizDriverInfoDetailService.insertSelective(carBizDriverInfoDetail);

		// 新增司机帐号
		CarBizDriverAccount accountPojo = new CarBizDriverAccount();
		accountPojo.setAccountAmount(new BigDecimal(0.0));
		accountPojo.setCreditBalance(new BigDecimal(0.0));
		accountPojo.setDriverId(driverId);
		accountPojo.setFrozenAmount(new BigDecimal(0.0));
		accountPojo.setSettleAccount(new BigDecimal(0.0));
		accountPojo.setWithdrawDeposit(new BigDecimal(0.0));
		carBizDriverAccountMapper.insertSelective(accountPojo);
		return driverId;
	}

	/**
	 * 查询城市名称，供应商名称，服务类型，加盟类型
	 *
	 * @param saveDTO
	 * @return
	 */
	@MasterSlaveConfigs(configs = { @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE),
			@MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.SLAVE) })
	public <T> void getBaseStatis(T t) {
		if (t == null) {
			return;
		}
		BusBaseStatisDTO baseStatisDTO = new BusBaseStatisDTO();
		BeanUtils.copyProperties(t, baseStatisDTO);

		// 根据城市ID查找城市名称
		Integer cityId = null;
		if ((cityId = baseStatisDTO.getServiceCity()) != null || (cityId = baseStatisDTO.getCityId()) != null) {
			CarBizCity carBizCity = carBizCityMapper.selectByPrimaryKey(cityId);
			if (carBizCity != null) {
				baseStatisDTO.setCityName(carBizCity.getCityName());
			}
		}
		// 根据供应商ID查询供应商名称以及加盟类型
		if (baseStatisDTO.getSupplierId() != null) {
			CarBizSupplier carBizSupplier = carBizSupplierMapper.selectByPrimaryKey(baseStatisDTO.getSupplierId());
			if (carBizSupplier != null) {
				baseStatisDTO.setSupplierName(carBizSupplier.getSupplierFullName());
				baseStatisDTO.setCooperationType(carBizSupplier.getCooperationType());
				CarBizCooperationType carBizCooperationType = carBizCooperationTypeMapper
						.selectByPrimaryKey(carBizSupplier.getCooperationType());
				if (carBizCooperationType != null) {
					baseStatisDTO.setCooperationTypeName(carBizCooperationType.getCooperationName());
				}
			}
		}
		// 根据服务类型查找服务类型名称
		if (baseStatisDTO.getGroupId() != null) {
			CarBizCarGroup carBizCarGroup = carBizCarGroupMapper.selectByPrimaryKey(baseStatisDTO.getGroupId());
			if (carBizCarGroup != null) {
				baseStatisDTO.setGroupName(carBizCarGroup.getGroupName());
			}
		}
		if (baseStatisDTO.getDriverId() != null) {
			// 根据司机ID查询车队小组信息
			Map<String, Object> stringObjectMap = carDriverTeamExMapper
					.queryTeamNameAndGroupNameByDriverId(baseStatisDTO.getDriverId());
			if (stringObjectMap != null) {
				if (stringObjectMap.containsKey("teamId") && stringObjectMap.get("teamId") != null) {
					baseStatisDTO.setTeamId(Integer.parseInt(stringObjectMap.get("teamId").toString()));
				}
				if (stringObjectMap.containsKey("teamName") && stringObjectMap.get("teamName") != null) {
					baseStatisDTO.setTeamName(stringObjectMap.get("teamName").toString());
				}
				if (stringObjectMap.containsKey("teamGroupId") && stringObjectMap.get("teamGroupId") != null) {
					baseStatisDTO.setTeamGroupId(Integer.parseInt(stringObjectMap.get("teamGroupId").toString()));
				}
				if (stringObjectMap.containsKey("teamGroupName") && stringObjectMap.get("teamGroupName") != null) {
					baseStatisDTO.setTeamGroupName(stringObjectMap.get("teamGroupName").toString());
				}
			}

			// 查询用户的名称
			if (baseStatisDTO.getCreateBy() != null) {
				CarAdmUser carAdmUser = carAdmUserMapper.selectByPrimaryKey(baseStatisDTO.getCreateBy());
				if (carAdmUser != null) {
					baseStatisDTO.setCreateName(carAdmUser.getUserName());
				}
			}
			if (baseStatisDTO.getUpdateBy() != null) {
				CarAdmUser carAdmUser = carAdmUserMapper.selectByPrimaryKey(baseStatisDTO.getUpdateBy());
				if (carAdmUser != null) {
					baseStatisDTO.setUpdateName(carAdmUser.getUserName());
				}
			}
		}
		// 根据车牌号查找对应车型
		if (baseStatisDTO.getLicensePlates() != null) {
			CarBizCarInfoDTO carInfo = carBizCarInfoExMapper.selectModelByLicensePlates(baseStatisDTO.getLicensePlates());
			if (carInfo != null) {
				baseStatisDTO.setModelId(carInfo.getCarModelId());
				baseStatisDTO.setModelName(carInfo.getCarModelName());
			}
		}

		// 返回补充 的信息
		BeanUtils.copyProperties(baseStatisDTO, t);
	}
	
	/**
	 * @Title: completeInfo
	 * @Description: 补充默认信息(用户认为没必要但基于已有的系统设计业务可能会需要的字段)
	 * @param saveDTO
	 * @return 
	 * @return AjaxResponse
	 * @throws
	 */
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public AjaxResponse completeInfo(BusDriverSaveDTO saveDTO) {
		
		// 判断一些基础信息是否正确
		Integer driverId = saveDTO.getDriverId();
		String phone = saveDTO.getPhone();
		String idCardNo = saveDTO.getIdCardNo();
		String bankCardNumber = saveDTO.getBankCardNumber();
		String bankCardBank = saveDTO.getBankCardBank();
		AjaxResponse ajaxResponse = carBizDriverInfoService.validateCarDriverInfo(driverId, phone, idCardNo,
				bankCardNumber, bankCardBank);
		if (ajaxResponse.getCode() != 0) {
			return ajaxResponse;
		}
		// 根据供应商ID查询供应商名称以及加盟类型
		Integer supplierId = saveDTO.getSupplierId();
		CarBizSupplier carBizSupplier = carBizSupplierMapper.selectByPrimaryKey(supplierId);
		if (carBizSupplier == null) {
			return AjaxResponse.fail(RestErrorCode.SUPPLIER_NOT_EXIST, supplierId);
		}
		// 驾驶员合同（或协议）签署公司标识：与供应商相同
		saveDTO.setCorptype(carBizSupplier.getSupplierFullName());
		// 网络预约出租汽车驾驶员证发证机构：与供应商相同
		saveDTO.setDriverlicenseissuingcorp(carBizSupplier.getSupplierFullName());
		// 服务城市
		Integer serviceCity = saveDTO.getServiceCity();
		if (serviceCity != carBizSupplier.getSupplierCity()) {
			return AjaxResponse.fail(RestErrorCode.CITY_SUPPLIER_DIFFER);
		}
		// 根据服务类型查找服务类型名称
		Integer groupId = saveDTO.getGroupId();
		CarBizCarGroup carBizCarGroup = carBizCarGroupMapper.selectByPrimaryKey(groupId);
		if (carBizCarGroup == null) {
			return AjaxResponse.fail(RestErrorCode.GROUP_NOT_EXIST);
		}
		
		LocalDate now = LocalDate.now();
		DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		/** ===========================需要校验的字段=========================== */
		// 出生日期
		String birthDay = saveDTO.getBirthDay();
		long age = ChronoUnit.YEARS.between(LocalDate.parse(birthDay, pattern), now);
		if (age < 21 || age > 60) {
			return AjaxResponse.failMsg(RestErrorCode.HTTP_PARAM_INVALID, "司机年龄必须满足（21≤司机年龄≤60）");
		}
		// 司机年龄
		saveDTO.setAge((int) age);
		// 驾照领证日期
		Date issueDate = saveDTO.getIssueDate();
		LocalDate issueLocalDate = LocalDateTime.ofInstant(issueDate.toInstant(), ZoneId.systemDefault()).toLocalDate();
		long drivingYears = ChronoUnit.YEARS.between(issueLocalDate, now);
		if (drivingYears < 3) {
			return AjaxResponse.failMsg(RestErrorCode.HTTP_PARAM_INVALID, "司机驾龄必须满足（驾龄≥3）");
		}
		// 驾龄
		saveDTO.setDrivingYears((int) drivingYears);

		// ======================以下值属于默认值==============================
		// 国籍
		saveDTO.setNationality("中国");
		// 民族：汉
		saveDTO.setNation("汉");
		// 户口登记机关：北京市
		saveDTO.setHouseHoldRegisterPermanent("北京市");
		// 户籍所在地：北京市西城区开阳里小区
		saveDTO.setHouseholdregister("北京市西城区开阳里小区");
		// 婚姻状况：已婚
		saveDTO.setMarriage("已婚");
		// 司机手机型号：安卓
		saveDTO.setPhonetype("安卓");
		// 司机手机运营商：中国联通
		saveDTO.setPhonecorp("中国联通");
		// 学历：高中
		saveDTO.setEducation("高中");
		// 现住址：北京市西城区开阳里小区
		saveDTO.setCurrentAddress("北京市西城区开阳里小区");
		// 初次领证日期：2010-01-01
		saveDTO.setFirstdrivinglicensedate("2010-01-01");
		// 服务监督码 ：110108196310174233
		saveDTO.setSuperintendNo("110108196310174233");
		// 服务监督链接：http://m.01zhuanche.com/touch/
		saveDTO.setSuperintendUrl("http://m.01zhuanche.com/touch/");
		// 档案编号：330325504955
		saveDTO.setArchivesNo("330325504955");
		// 网络预约出租汽车驾驶员证编号：500108198912094021
		saveDTO.setDriverlicenseissuingnumber("500108198912094021");
		// 网络预约出租汽车驾驶员证初领日期 ：2017-10-01
		saveDTO.setFirstmeshworkdrivinglicensedate("2017-10-01");
		// 签署日期：2017-10-01
		saveDTO.setSigndate("2017-10-01");
		// 合同（或协议）到期时间：2020-05-12
		saveDTO.setSigndateend("2020-05-12");
		// 有效合同时间：2020-05-12
		saveDTO.setContractdate("2020-05-12");

		// 是否专职司机：是
		saveDTO.setParttimejobdri("是");
		// 地图类型：高德
		saveDTO.setMaptype("高德");
		// 资格证有效起始日期：2017-10-01
		saveDTO.setDriverlicenseissuingdatestart("2017-10-01");
		// 资格证有效截止日期：2020-05-12
		saveDTO.setDriverlicenseissuingdateend("2020-05-12");
		// 初次领取资格证日期：2017-10-01
		saveDTO.setDriverLicenseIssuingFirstDate("2017-10-01");
		// 资格证发证日期：2017-10-01
		saveDTO.setDriverLicenseIssuingGrantDate("2017-10-01");

		return AjaxResponse.success(null);
	}

	/**
	 * 发送MQ
	 *
	 * @param saveDTO
	 * @param method
	 *            tag: INSERT UPDATE DELETE
	 */
	public void sendDriverToMq(BusDriverSaveDTO saveDTO, String method) {
		// MQ消息写入
		try {
			Map<String, Object> messageMap = new HashMap<String, Object>();
			messageMap.put("carNumber", saveDTO.getLicensePlates());// 车牌号
			messageMap.put("city", saveDTO.getCityName()); // 城市名称
			messageMap.put("cityId", saveDTO.getServiceCity()); // 城市ID
			messageMap.put("createBy", saveDTO.getUpdateBy() == null ? "1" : saveDTO.getUpdateBy()); // 操作人
			messageMap.put("driverId", saveDTO.getDriverId()); // 司机ID
			messageMap.put("driverName", saveDTO.getName()); // 司机姓名
			messageMap.put("driverPhone", saveDTO.getPhone() == null ? "" : saveDTO.getPhone()); // 司机手机号
			messageMap.put("status", saveDTO.getStatus()); // 司机状态
			messageMap.put("supplierFullName", saveDTO.getSupplierName()); // 司机供应商名称
			messageMap.put("supplierId", saveDTO.getSupplierId()); // 司机供应商
			messageMap.put("cooperationType", saveDTO.getCooperationType()); // 司机加盟类型
			messageMap.put("groupId", saveDTO.getGroupId()); // 司机服务类型ID
			messageMap.put("create_date", saveDTO.getCreateDate()); // 司机创建时间
			messageMap.put("carType", saveDTO.getGroupName() == null ? "" : saveDTO.getGroupName()); // 司机服务类型
			messageMap.put("teamId", saveDTO.getTeamId() == null ? "" : saveDTO.getTeamId()); // 司机所属车队ID
			messageMap.put("teamName", saveDTO.getTeamName() == null ? "" : saveDTO.getTeamName()); // 司机所属车队名称
			messageMap.put("teamGroupId", saveDTO.getTeamGroupId() == null ? "" : saveDTO.getTeamGroupId()); // 司机所属小组ID
			messageMap.put("teamGroupName", saveDTO.getTeamGroupName() == null ? "" : saveDTO.getTeamGroupName()); // 司机所属小组名称

			logger.info("专车司机driverId={}，同步发送数据={}", saveDTO.getDriverId(), JSON.toJSONString(messageMap));
			CommonRocketProducer.publishMessage("driver_info", method, String.valueOf(saveDTO.getDriverId()), messageMap);
		} catch (Exception e) {
			logger.error("发送MQ异常,method={},error={}", method, e.getMessage(), e);
		}
	}

	@SuppressWarnings("resource")
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public AjaxResponse batchInputDriverInfo(Integer cityId, Integer supplierId, MultipartFile file,
			HttpServletRequest request, HttpServletResponse response) {

		// 数据错误原因
        List<String> errorMsgs = new ArrayList<>();
        int count = 0;// 实际扫描导入的条数
        int successCount = 0;// 成功的条数
        int failedCount = 0;// 失败的条数

        // 导入文件校验
        String fileName = file.getOriginalFilename();
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
        logger.info("上传的文件名为:{},上传的后缀名为:{}", fileName, fileType);
		try (InputStream is = file.getInputStream()){
            Workbook workbook = null;
            
            if (fileType.equals("xls")) {
                workbook = new HSSFWorkbook(is);
            } else if (fileType.equals("xlsx")) {
                workbook = new XSSFWorkbook(is);
            } else {
            	return AjaxResponse.failMsg(RestErrorCode.FILE_TRMPLATE_ERROR, "不支持的文件类型");
			}
            Sheet sheet = workbook.getSheetAt(0);
            // 公式求值器
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            // 检查模板是否正确
            Row headRow = sheet.getRow(0);// 模板列表头字段
            if (headRow == null) {
            	return AjaxResponse.failMsg(RestErrorCode.FILE_TRMPLATE_ERROR, "导入文件列表缺少表头字段");
            }

            // 判断模板列是否缺少
            List<String> templetFields = new ArrayList<>();
			templetFields.add("司机姓名");
			templetFields.add("性别");
			templetFields.add("车型类别（巴士xx座）");
			templetFields.add("司机身份证号");
			templetFields.add("司机手机号（11位）");
			templetFields.add("出生日期（21≤司机年龄≤60，格式：yyyy-MM-dd）");
			templetFields.add("驾照类型");
			templetFields.add("驾驶证号");
			templetFields.add("驾照领证日期（驾龄≥3，格式：yyyy-MM-dd）");
			templetFields.add("道路运输从业资格证编号");
            for (int colIndex = 0; colIndex < templetFields.size(); colIndex++) {
            	String field = templetFields.get(colIndex);
            	
                Cell cell = headRow.getCell(colIndex);// 获取列对象
                String cellValue = getCellValue(cell, evaluator);// 获取单元格值
                if (cell == null || cellValue == null || !field.contains(cellValue)) {
                    return AjaxResponse.failMsg(RestErrorCode.FILE_TRMPLATE_ERROR, "模板缺少【" + field + "】列");
                }
            }
            
            // 处理导入数据
            int startIndex = 1;// 过滤掉标题，从第一行开始导入数据
            int endIndex = sheet.getLastRowNum(); // 要导入数据的总条数
            
            if (endIndex > 10000) {
            	return AjaxResponse.failMsg(RestErrorCode.FILE_ERROR, "导入数据过大（count>10000）,建议分批导入");
			}
			for (int rowIndex = startIndex; rowIndex <= endIndex; rowIndex++) {
                Row row = sheet.getRow(rowIndex); // 获取行对象
                if (row == null) {
                    continue;
                }
                count ++;
                
                // 数据封装对象
                BusDriverSaveDTO saveDTO = new BusDriverSaveDTO();
                boolean isTrue = true;// 标识是否为有效数据
                StringBuffer rePhone = new StringBuffer();//手机号
                StringBuffer reIdCarNo = new StringBuffer(); //身份证号

                // 司机导入模板总共10列
				for (int colIndex = 0; colIndex < templetFields.size(); colIndex++) {
					// 错误信息前缀
					String headerText = templetFields.get(colIndex);
					String errorPrefix = "第" + (rowIndex + 1) + "行数据，第" + (colIndex + 1) + "列 【" + headerText + "】:";
                    
					// 获取某列单元格对象
					Cell cell = row.getCell(colIndex);
					String cellValue = null;// 单元格文本值
					if (cell == null || StringUtils.isBlank(cellValue = getCellValue(cell, evaluator))) {
						errorMsgs.add(errorPrefix + "内容非法或为空");
						isTrue = false;
						continue;
					}
                    switch (colIndex) {
                        // 司机姓名
                        case 0:
                            String name = StringUtils.deleteWhitespace(cellValue);
                            if (name.length() > 20) {
                            	errorMsgs.add(errorPrefix + "司机姓名长度不能超过20");
                            	isTrue = false;
							} else {
								saveDTO.setName(name);
							}
                            break;
                         // 性别
                        case 1:
                            String gender = StringUtils.deleteWhitespace(cellValue);
                            if ("男".equals(gender)) {
                            	saveDTO.setGender(1);
                            } else if ("女".equals(gender)) {
                            	saveDTO.setGender(0);
                            } else {
                            	errorMsgs.add(errorPrefix + "请输入正确的选项(男/女)");
                            	isTrue = false;
                            }
                            break;
                        // 车型类别（巴士xx座）
                        case 2:
							String groupName = StringUtils.deleteWhitespace(cellValue);
							CarBizCarGroup carBizCarGroup = carBizCarGroupExMapper.queryGroupByGroupName(groupName);
							if (carBizCarGroup == null) {
								errorMsgs.add(errorPrefix + "系统暂时没有该车型类别——" + groupName);
								isTrue = false;
							} else {
								saveDTO.setGroupId(carBizCarGroup.getGroupId());
							}
                            break;
                        // 司机身份证号
                        case 3:
							String idCardNo = StringUtils.deleteWhitespace(cellValue);
							if (idCardNo.length() > 18) {
								errorMsgs.add(errorPrefix + "身份证号长度不能超过18");
								isTrue = false;
								break;
							}
							if ("X".equals(idCardNo.substring(idCardNo.length() - 1, idCardNo.length()))) {
								idCardNo = idCardNo.toLowerCase();
							}
							
							if (reIdCarNo.indexOf(idCardNo) > 0) {
								errorMsgs.add(errorPrefix + "有重复身份证号");
								isTrue = false;
								break;
							}
							
							reIdCarNo.append(idCardNo).append(",");
							if (StringUtils.isEmpty(idCardNo) || !ValidateUtils.validateIdCarNo(idCardNo)) {
								errorMsgs.add(errorPrefix + "不合法");
								isTrue = false;
							} else if (carBizDriverInfoService.checkIdCardNo(idCardNo, null)) {
								errorMsgs.add(errorPrefix + "已存在【" + idCardNo + "】的司机信息信息");
								isTrue = false;
							} else {
								saveDTO.setIdCardNo(idCardNo);
							}
                            break;
                        // 驾驶员手机
                        case 4:
                            String phone = StringUtils.deleteWhitespace(cellValue);
                            if (rePhone.indexOf(phone) > 0) {
                                errorMsgs.add(errorPrefix + "表中有重复的驾驶员手机号——" + phone);
                                isTrue = false;
                                break;
                            }
                            
                            rePhone.append(phone);
                            if (StringUtils.isEmpty(phone) || !ValidateUtils.validatePhone(phone)) {
                                errorMsgs.add(errorPrefix + "不合法");
                                isTrue = false;
                            } else if (carBizDriverInfoService.checkPhone(phone, null)) {
                                errorMsgs.add(errorPrefix + "已存在【" + phone + "】的司机信息信息");
                                isTrue = false;
                            } else {
                                saveDTO.setPhone(phone);
                            }
                            break;
                        // 出生日期
                        case 5:
							try {
								LocalDate.parse(cellValue, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
								saveDTO.setBirthDay(cellValue);
							} catch (Exception e) {
								errorMsgs.add(errorPrefix + "格式错误，正确格式为：yyyy-MM-dd");
								isTrue = false;
							}
                            break;
                        // 驾照类型
                        case 6:
                            String drivingLicenseType = StringUtils.deleteWhitespace(cellValue);
                            boolean isMatch = Arrays.stream(DRIVING_LICENSE_TYPES).anyMatch(type -> type.equals(drivingLicenseType));
                            String initDrivingLicenseType = isMatch ? drivingLicenseType : "C1";
                            saveDTO.setDrivingLicenseType(initDrivingLicenseType);
                            break;
                        // 驾驶证号
                        case 7:
                        	 String driverLicenseNumber = StringUtils.deleteWhitespace(cellValue);
                             if (driverLicenseNumber.length() > 30) {
                             	errorMsgs.add(errorPrefix + "驾驶证号长度不能超过30");
                             	isTrue = false;
 							} else {
 								saveDTO.setDriverlicensenumber(driverLicenseNumber);
 							}
                             break;
                         // 驾照领证日期
                        case 8:
                        	try {
								LocalDate issueDate = LocalDate.parse(cellValue, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
								saveDTO.setIssueDate(Date.from(issueDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
							} catch (Exception e) {
                                errorMsgs.add(errorPrefix + "格式错误，正确格式为：yyyy-MM-dd");
                                isTrue = false;
                            }
                            break;
                        // 道路运输从业资格证编号
                        case 9:
                            String xyDriverNumber = StringUtils.deleteWhitespace(cellValue);
                            if (xyDriverNumber.length() > 20) {
                            	errorMsgs.add(errorPrefix + "道路运输从业资格证编号长度不能超过20");
                            	isTrue = false;
							} else {
								saveDTO.setXyDriverNumber(xyDriverNumber);
							}
                            break;
                    }// switch end
                }// 循环列结束
                
                if (isTrue) {
                    saveDTO.setServiceCity(cityId);
                    saveDTO.setSupplierId(supplierId);
                    
                    // 补充其它信息
                    AjaxResponse checkResult = this.completeInfo(saveDTO);
                    if (!checkResult.isSuccess()) {
                    	errorMsgs.add( "手机号=" + saveDTO.getPhone() + "数据有误:" + checkResult.getMsg());
                    	failedCount++;// 失败条数+1
                    	continue;
					}
                    //保存司机信息
                    AjaxResponse saveResult = this.saveDriver(saveDTO);
					if (!saveResult.isSuccess()) {
                        errorMsgs.add( "手机号=" + saveDTO.getPhone() + "保存出错，错误=" + saveResult.getMsg());
                        failedCount++;// 失败条数+1
                        continue;
                    }
					successCount++;// 成功条数+1
                } else {
					failedCount++;// 失败条数+1
				}
            }
        } catch (Exception e) {
			logger.error("[ BusCarBizDriverInfoService-batchInputDriverInfo ] 导入司机信息异常, error={}", e.getMessage(), e);
			return AjaxResponse.failMsg(RestErrorCode.FILE_IMPORT_ERROR, "导入文件错误");
        }
		
		 // 将错误列表导出
        if(errorMsgs.size() > 0) {
        	Map<Object, Object> result = new HashMap<>();
        	result.put("successCount", successCount);
        	result.put("failedCount", failedCount);
        	result.put("errorMsgs", errorMsgs);
            return AjaxResponse.success(result);
        }
        if(count==0){
        	return AjaxResponse.failMsg(RestErrorCode.FILE_ERROR, "表中没有数据，请检查");
        }else {
        	return AjaxResponse.success(null);
        }
	}
	
	/**
	 * @param evaluator 
	 * @Title: getCellValue
	 * @Description: 获取单元格值
	 * @param cell
	 * @return 
	 * @return String
	 * @throws
	 */
	public String getCellValue(Cell cell, FormulaEvaluator evaluator) {
		if(cell==null){
			return null;
		}
		String cellStringValue = "";
		// 以下是判断数据的类型
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC: // 数字
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				cellStringValue = sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue())).toString();
			} else {
				DataFormatter dataFormatter = new DataFormatter();
				cellStringValue = dataFormatter.formatCellValue(cell);
			}
			break;
		case Cell.CELL_TYPE_STRING: // 字符串
			cellStringValue = cell.getStringCellValue();
			break;
		case Cell.CELL_TYPE_BOOLEAN: // Boolean
			cellStringValue = Boolean.toString(cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_FORMULA: // 公式
			cellStringValue = String.valueOf(evaluator.evaluate(cell).getNumberValue());// 算出公式的值
			break;
		case Cell.CELL_TYPE_BLANK: // 空值
		case Cell.CELL_TYPE_ERROR: // 故障
		default:
			cellStringValue = "";
			break;
		}
		return cellStringValue;
	}
	
	/**
	 * @Title: resetIMEI
	 * @Description: 重置imei
	 * @param driverId 
	 * @return void
	 * @throws
	 */
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.MASTER))
	public int resetIMEI(Integer driverId) {
		// 创建操作记录
		busBizChangeLogService.insertLog(BusinessType.DRIVER, String.valueOf(driverId), new Date());
		return carBizDriverInfoExMapper.resetIMEI(driverId);
	}

}
