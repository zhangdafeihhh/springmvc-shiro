package com.zhuanche.serv.busManage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.WriteResult;
import com.zhuanche.common.cache.RedisCacheUtil;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.rocketmq.CommonRocketProducer;
import com.zhuanche.common.rocketmq.CommonRocketProducerDouble;
import com.zhuanche.common.rocketmq.DriverWideRocketProducer;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constant.EnvUtils;
import com.zhuanche.constants.BusConst;
import com.zhuanche.constants.busManage.BusConstant;
import com.zhuanche.dto.busManage.BusBaseStatisDTO;
import com.zhuanche.dto.busManage.BusDriverQueryDTO;
import com.zhuanche.dto.busManage.BusDriverSaveDTO;
import com.zhuanche.dto.rentcar.CarBizCarInfoDTO;
import com.zhuanche.dto.rentcar.CarBizDriverInfoDetailDTO;
import com.zhuanche.entity.mdbcarmanage.CarAdmUser;
import com.zhuanche.entity.mdbcarmanage.CarBizAgreementCompany;
import com.zhuanche.entity.mdbcarmanage.CarRelateGroup;
import com.zhuanche.entity.mdbcarmanage.CarRelateTeam;
import com.zhuanche.entity.rentcar.*;
import com.zhuanche.http.HttpClientUtil;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.mongo.DriverMongo;
import com.zhuanche.mongo.busManage.BusDriverInfoAudit;
import com.zhuanche.mongo.busManage.BusInfoAudit;
import com.zhuanche.serv.CarBizChatUserService;
import com.zhuanche.serv.CarBizDriverInfoDetailService;
import com.zhuanche.serv.CarBizDriverInfoService;
import com.zhuanche.serv.mdbcarmanage.CarBizDriverUpdateService;
import com.zhuanche.serv.mongo.BusDriverMongoService;
import com.zhuanche.serv.mongo.DriverMongoService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.Common;
import com.zhuanche.util.SignUtils;
import com.zhuanche.util.ValidateUtils;
import com.zhuanche.util.excel.CsvUtils;
import com.zhuanche.util.objcompare.CompareObjectUtils;
import com.zhuanche.util.objcompare.entity.BusDriverCompareEntity;
import com.zhuanche.vo.busManage.BusDriverDetailInfoVO;
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
import mapper.rentcar.*;
import mapper.rentcar.ex.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.entity.ContentType;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: BusCarBizDriverInfoService
 * @Description:
 * @author: yanyunpeng
 * @date: 2018年12月17日 下午7:49:33
 */
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BusCarBizDriverInfoService implements BusConst {

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
    @Autowired
    private CarBizSupplierExMapper carBizSupplierExMapper;
    @Autowired
    private CarBizCityExMapper carBizCityExMapper;

    // ===========================巴士业务拓展mapper==================================
    @Autowired
    private BusCarBizDriverInfoExMapper busCarBizDriverInfoExMapper;
    @Autowired
    private BusCarBizCustomerAppraisalStatisticsExMapper appraisalStatisticsExMapper;

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
    @Autowired
    private BusCommonService commonService;

//    @Resource(name = "driverMongoTemplate")
//    private MongoTemplate driverMongoTemplate;
    @Resource(name = "busMongoTemplate")
    private MongoTemplate busMongoTemplate;

    @Value("${car.rest.url}")
    private String order_url ;

    @Value("${mp.restapi.url}")
    private String mpReatApiUrl;
    /**
     * @param queryDTO
     * @return List<BusDriverInfoVO>
     * @throws
     * @Title: queryDriverList
     * @Description: 分页查询司机信息列表
     * 数据量较大的表不做表关联查询，单表查询组装数据
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
            //司机的平均分
            Double evaScore = appraisalStatisticsExMapper.queryAvgAppraisal(driver.getDriverId());
            driver.setAverage(evaScore==null?StringUtils.EMPTY:String.valueOf(Math.round(evaScore)));
        });
        return driverList;
    }

    /**
     * @param exportDTO
     * @return List<BusDriverInfoVO>
     * @throws
     * @Title: queryDriverExportList
     * @Description: 查询导出数据
     */
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
    public List<BusDriverInfoExportVO> queryDriverExportList(BusDriverQueryDTO exportDTO) {
        logger.info("[ BusCarBizDriverInfoService-queryDriverExportList ] 导出司机列表params={}", JSON.toJSONString(exportDTO));
        List<BusDriverInfoExportVO> driverList = busCarBizDriverInfoExMapper.queryDriverExportList(exportDTO);
        return driverList;
    }

    /**
     * @param list
     * @return void
     * @throws
     * @Title: completeDriverExportList
     * @Description: 补充导出数据
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
            // 司机积分
            Double score = appraisalStatisticsExMapper.queryAvgAppraisal(driver.getDriverId());
            builder.append(CsvUtils.tab).append(score==null?StringUtils.EMPTY:Math.round(score));
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
    @MasterSlaveConfigs(configs = {@MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE),
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.SLAVE)})
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
                logger.error("保存需要上报交通委的信息异常,error={}", e.getMessage(), e);
            }

            return AjaxResponse.success("修改成功");
        } catch (Exception e) {
            logger.error("修改司机信息异常,error={}", e.getMessage(), e);
            return AjaxResponse.failMsg(RestErrorCode.HTTP_SYSTEM_ERROR, "修改司机信息异常");
        }
    }
    public  boolean isAuditStatus (Integer driverId){
        //判断审核表中，有没有待审核的数据
        Query query = new Query();
        query.addCriteria(Criteria.where("driverId").is(driverId));
        query.addCriteria(Criteria.where("auditStatus").is(0));
        List<BusDriverInfoAudit> busDriverInfoAudits = busMongoTemplate.find(query, BusDriverInfoAudit.class);
        if(busDriverInfoAudits!=null&&busDriverInfoAudits.size()>0){
            return true;
        }
        return false;
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
                driverMongoService.disableDriverMongo(saveDTO.getDriverId());

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
     * @param saveDTO void
     * @throws
     * @Title: driverUpdate
     * @Description: 交通委需要修改以下信息的记录
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
    @MasterSlaveConfigs(configs = {@MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE),
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.SLAVE)})
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
            //saveDTO.setStatus(1);
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

            // 保存操作记录
            busBizChangeLogService.insertLog(BusinessType.DRIVER, String.valueOf(saveDTO.getDriverId()),"新建司机", new Date());
            return AjaxResponse.success(null);
        } catch (Exception e) {
            logger.error("新增司机信息异常,error={}", e.getMessage(), e);
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
        logger.info("巴士司机入库操作开始-----------------");

        busCarBizDriverInfoExMapper.insertBusDriverInfo(saveDTO);
        Integer driverId = saveDTO.getDriverId();
        logger.info("巴士司机入库操作完成-----------------司机id--{}",driverId);

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
     * @return
     */
    @MasterSlaveConfigs(configs = {@MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE),
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.SLAVE)})
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
                baseStatisDTO.setGroupType(carBizCarGroup.getType());
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
     * @param saveDTO
     * @return AjaxResponse
     * @throws
     * @Title: completeInfo
     * @Description: 补充默认信息(用户认为没必要但基于已有的系统设计业务可能会需要的字段)
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
        if (!serviceCity.equals(carBizSupplier.getSupplierCity())) {
            return AjaxResponse.fail(RestErrorCode.CITY_SUPPLIER_DIFFER);
        }
        // 根据服务类型查找服务类型名称
        Integer groupId = saveDTO.getGroupId();
        CarBizCarGroup carBizCarGroup = carBizCarGroupMapper.selectByPrimaryKey(groupId);
        if (carBizCarGroup == null) {
            return AjaxResponse.fail(RestErrorCode.GROUP_NOT_EXIST);
        }

        LocalDate now = LocalDate.now();
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-M-d");
        /** ===========================需要校验的字段=========================== */
        // 出生日期
        String birthDay = saveDTO.getBirthDay();
		if (saveDTO.getAge() == null || saveDTO.getAge() == 0) {
			// 司机年龄
			long age = ChronoUnit.YEARS.between(LocalDate.parse(birthDay, pattern), now);
			saveDTO.setAge((int) age);
		}
		if (saveDTO.getAge() != null && (saveDTO.getAge() < 21 || saveDTO.getAge() > 60)) {
			return AjaxResponse.failMsg(RestErrorCode.HTTP_PARAM_INVALID, "司机年龄必须满足（21≤司机年龄≤60）");
		}
		// 驾照领证日期
        Date issueDate = saveDTO.getIssueDate();
		if (saveDTO.getDrivingYears() == null || saveDTO.getDrivingYears() == 0) {
			// 驾龄
			LocalDate issueLocalDate = LocalDateTime.ofInstant(issueDate.toInstant(), ZoneId.systemDefault()).toLocalDate();
			long drivingYears = ChronoUnit.YEARS.between(issueLocalDate, now);
			saveDTO.setDrivingYears((int) drivingYears);
		}
		if (saveDTO.getDrivingYears() != null && (saveDTO.getDrivingYears() < 3)) {
			return AjaxResponse.failMsg(RestErrorCode.HTTP_PARAM_INVALID, "司机驾龄必须满足（驾龄≥3）");
		}
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
     * @param method  tag: INSERT UPDATE DELETE
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
            
            Date createDate = saveDTO.getCreateDate();
            String create_date = "";
            if (createDate != null) {
            	create_date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.ofInstant(createDate.toInstant(), ZoneId.systemDefault()));
			}
			messageMap.put("create_date", create_date); // 司机创建时间
			messageMap.put("carType", saveDTO.getGroupName() == null ? "" : saveDTO.getGroupName()); // 司机服务类型
			messageMap.put("groupType", saveDTO.getGroupType() == null ? "" : saveDTO.getGroupType());
			messageMap.put("teamId", saveDTO.getTeamId() == null ? "" : saveDTO.getTeamId()); // 司机所属车队ID
			messageMap.put("teamName", saveDTO.getTeamName() == null ? "" : saveDTO.getTeamName()); // 司机所属车队名称
			messageMap.put("teamGroupId", saveDTO.getTeamGroupId() == null ? "" : saveDTO.getTeamGroupId()); // 司机所属小组ID
			messageMap.put("teamGroupName", saveDTO.getTeamGroupName() == null ? "" : saveDTO.getTeamGroupName()); // 司机所属小组名称
            logger.info("根据司机id查询司机信息driverId={}" , saveDTO.getDriverId());
            CarBizDriverInfo driverInfo = carBizDriverInfoMapper.selectByPrimaryKey(saveDTO.getDriverId());
            if (Objects.nonNull(driverInfo)) {
                logger.info("查询司机信息不为空添加车牌号到messageMap");
                messageMap.put("licensePlates" , driverInfo.getLicensePlates());
            }
            Integer driverId = saveDTO.getDriverId();
            logger.info("专车司机driverId={}，同步发送数据={}", driverId, JSON.toJSONString(messageMap));
            //TODO 20190619新增一组修改司机信息发送MQ
            DriverWideRocketProducer.publishMessage(DriverWideRocketProducer.TOPIC, method, String.valueOf(driverId), messageMap);
            CommonRocketProducer.publishMessage("driver_info", method, String.valueOf(driverId), messageMap);
            String envName = EnvUtils.ENVIMENT;
            if (Objects.nonNull(envName) && Arrays.asList(new String[]{"online","prod"}).contains(envName)){
                CommonRocketProducerDouble.publishMessage("driver_info",method,String.valueOf(driverId),messageMap);
            }
        } catch (Exception e) {
            logger.error("发送MQ异常,method={},error={}", method, e.getMessage(), e);
        }
    }

    @SuppressWarnings("resource")
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
    public AjaxResponse batchInputDriverInfo(Integer cityId, Integer supplierId, MultipartFile file,
                                             HttpServletRequest request, HttpServletResponse response) {

        String cityName = carBizCityExMapper.queryNameById(cityId);
        String supplierName = carBizSupplierExMapper.getSupplierNameById(supplierId);
        if (StringUtils.isBlank(supplierName) || StringUtils.isBlank(cityName)) {
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "城市或供应商不存在");
        }
        // 数据错误原因
        List<String> errorMsgs = new ArrayList<>();
        int count = 0;// 实际扫描导入的条数
        int successCount = 0;// 成功的条数
        int failedCount = 0;// 失败的条数

        // 导入文件校验
        String fileName = file.getOriginalFilename();
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
        logger.info("上传的文件名为:{},上传的后缀名为:{}", fileName, fileType);
        try (InputStream is = file.getInputStream()) {
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
            String[] heads=null;
            //判断是否是运营角色
            boolean roleBoolean = commonService.ifOperate();
            if(roleBoolean){
                heads=BusConstant.DriverContant.TEMPLATE_HEAD;
            }else{
                //非运营角色下载的模板不包括城市和供应商
                heads=new String[BusConstant.DriverContant.TEMPLATE_HEAD.length-2];
                System.arraycopy(BusConstant.DriverContant.TEMPLATE_HEAD,2,heads,0, BusConstant.DriverContant.TEMPLATE_HEAD.length-2);
            }
            short lastCellNum = headRow.getLastCellNum();
            if(lastCellNum!=heads.length){
                return AjaxResponse.failMsg(RestErrorCode.FILE_TRMPLATE_ERROR, "模板错误");
            }
            // 判断模板列是否缺少
            for (int colIndex = 0; colIndex < heads.length; colIndex++) {
                String field = heads[colIndex];
                Cell cell = headRow.getCell(colIndex);// 获取列对象
                String cellValue = getCellValue(cell, evaluator);// 获取单元格值
                if (cell == null || cellValue == null || !field.equals(cellValue)) {
                    return AjaxResponse.failMsg(RestErrorCode.FILE_TRMPLATE_ERROR, "模板缺少【" + field + "】列");
                }
            }
            // 处理导入数据
            int startIndex = 1;// 过滤掉标题，从第一行开始导入数据
            int endIndex = sheet.getLastRowNum(); // 要导入数据的总条数
            int driverNameIdx=0;
            for(int i=0;i<heads.length;i++){
                if(heads[i].equals("司机姓名(必填)")){
                    driverNameIdx=i;
                    break;
                }
            }
            Row dataRowFirst = sheet.getRow(1);
            Cell driverNameCell = dataRowFirst.getCell(driverNameIdx);
            String driverName = getCellValue(driverNameCell, evaluator);
            if("例子".equals(driverName)){
                startIndex=2;
            }
            if (endIndex > 10000) {
                return AjaxResponse.failMsg(RestErrorCode.FILE_ERROR, "导入数据过大（count>10000）,建议分批导入");
            }
            for (int rowIndex = startIndex; rowIndex <= endIndex; rowIndex++) {
                Row row = sheet.getRow(rowIndex); // 获取行对象
                if (row == null) {
                    continue;
                }
                count++;
                // 数据封装对象
                BusDriverSaveDTO saveDTO = new BusDriverSaveDTO();
                boolean isTrue = true;// 标识是否为有效数据
                StringBuffer rePhone = new StringBuffer();//手机号
                StringBuffer reIdCarNo = new StringBuffer(); //身份证号
                // 司机导入模板总共11列
                for (int colIndex = 0; colIndex < heads.length; colIndex++) {
                    // 错误信息前缀
                    String headerText = heads[colIndex];
                    String errorPrefix = "第" + (rowIndex + 1) + "行数据，第" + (colIndex + 1) + "列 【" + headerText + "】:";

                    // 获取某列单元格对象
                    Cell cell = row.getCell(colIndex);
                    String cellValue = null;// 单元格文本值
                    if (cell == null || StringUtils.isBlank(cellValue = getCellValue(cell, evaluator))) {
                        errorMsgs.add(errorPrefix + "内容非法或为空");
                        isTrue = false;
                        continue;
                    }
                    switch (headerText) {
                        case "城市(必填)":
                            String cname = StringUtils.deleteWhitespace(cellValue);
                            if (!cityName.equals(cname)) {
                                errorMsgs.add(errorPrefix + "城市名称和页面选择的不一致");
                                isTrue = false;
                                break;
                            }
                            break;
                        case "供应商(必填)":
                            String supName = StringUtils.deleteWhitespace(cellValue);
                            if (!supplierName.equals(supName)) {
                                errorMsgs.add(errorPrefix + "供应商名称和页面选择的不一致");
                                isTrue = false;
                                break;
                            }
                            break;
                        // 司机姓名
                        case "司机姓名(必填)":
                            String name = StringUtils.deleteWhitespace(cellValue);
                            if (name.length() > 20) {
                                errorMsgs.add(errorPrefix + "司机姓名长度不能超过20");
                                isTrue = false;
                            } else {
                                saveDTO.setName(name);
                            }
                            break;
                        // 性别
                        case "性别(必填)":
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
                        case "车型类别(必填)":
                            String groupName = StringUtils.deleteWhitespace(cellValue);
                            CarBizCarGroup carBizCarGroup = carBizCarGroupExMapper.queryGroupByGroupNameAndStatus(groupName,1);
                            if (carBizCarGroup == null) {
                                errorMsgs.add(errorPrefix + "系统暂时没有该车型类别——" + groupName);
                                isTrue = false;
                            } else {
                                saveDTO.setGroupId(carBizCarGroup.getGroupId());
                            }
                            break;
                        // 司机身份证号
                        case "司机身份证号(必填)":
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
                            Boolean hadIdCard = this.checkMongoIdCardMo(null, idCardNo, null);
                            reIdCarNo.append(idCardNo).append(",");
                            if (StringUtils.isEmpty(idCardNo) || !ValidateUtils.validateIdCarNo(idCardNo)) {
                                errorMsgs.add(errorPrefix + "不合法");
                                isTrue = false;
                            } else if (carBizDriverInfoService.checkIdCardNo(idCardNo, null) || hadIdCard) {
                                errorMsgs.add(errorPrefix + "已存在【" + idCardNo + "】的司机信息信息");
                                isTrue = false;
                            } else {
                                saveDTO.setIdCardNo(idCardNo);
                            }
                            break;
                        // 驾驶员手机
                        case "司机手机号(必填)":
                            String phone = StringUtils.deleteWhitespace(cellValue);
                            if (rePhone.indexOf(phone) > 0) {
                                errorMsgs.add(errorPrefix + "表中有重复的驾驶员手机号——" + phone);
                                isTrue = false;
                                break;
                            }
                            rePhone.append(phone);
                            Boolean hadPhone = checkMongoPhone(null, phone, null);
                            if (StringUtils.isEmpty(phone) || !ValidateUtils.validatePhone(phone)) {
                                errorMsgs.add(errorPrefix + "不合法");
                                isTrue = false;
                            } else if (carBizDriverInfoService.checkPhone(phone, null) || hadPhone) {
                                errorMsgs.add(errorPrefix + "已存在【" + phone + "】的司机信息信息");
                                isTrue = false;
                            } else {
                                saveDTO.setPhone(phone);
                            }
                            break;
                        // 出生日期
                        case "出生日期(必填 年龄：21-60)":
                            try {
                                String data = transfTime(cellValue);
                                LocalDate localDate = LocalDate.parse(data, DateTimeFormatter.ofPattern("yyyy-M-d"));
                                String format = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(localDate);
                                saveDTO.setBirthDay(format);
                                
                                // 司机年龄
                                long age = ChronoUnit.YEARS.between(localDate, LocalDate.now());
                                if (age < 21 || age > 60) {
                                	errorMsgs.add(errorPrefix + "司机年龄必须满足（21≤司机年龄≤60）");
                                	isTrue = false;
                                	break;
                                }
                    			saveDTO.setAge((int) age);
                            } catch (Exception e) {
                                errorMsgs.add(errorPrefix + "格式错误，正确格式为：yyyy-MM-dd");
                                isTrue = false;
                            }
                            break;
                        // 驾照类型
                        case "驾照类型(必填)":
                            String drivingLicenseType = StringUtils.deleteWhitespace(cellValue);
                            boolean isMatch = Arrays.stream(DRIVING_LICENSE_TYPES).anyMatch(type -> type.equals(drivingLicenseType));
                            String initDrivingLicenseType = isMatch ? drivingLicenseType : "C1";
                            saveDTO.setDrivingLicenseType(initDrivingLicenseType);
                            break;
                        // 驾驶证号
                        case "驾驶证号(必填)":
                            String driverLicenseNumber = StringUtils.deleteWhitespace(cellValue);
                            if (driverLicenseNumber.length() > 30) {
                                errorMsgs.add(errorPrefix + "驾驶证号长度不能超过30");
                                isTrue = false;
                            } else {
                                saveDTO.setDriverlicensenumber(driverLicenseNumber);
                            }
                            break;
                        // 驾照领证日期
                        case "驾照领证日期(必填 驾龄≥3)":
                            try {
                                LocalDate issueDate = LocalDate.parse(transfTime(cellValue), DateTimeFormatter.ofPattern("yyyy-M-d"));
                                saveDTO.setIssueDate(Date.from(issueDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                                
                                // 驾龄
                                long drivingYears = ChronoUnit.YEARS.between(issueDate, LocalDate.now());
                    			if (drivingYears < 3) {
                    				errorMsgs.add(errorPrefix + "司机驾龄必须满足（驾龄≥3）");
                                	isTrue = false;
                                	break;
                    			}
                    			saveDTO.setDrivingYears((int) drivingYears);
                            } catch (Exception e) {
                                errorMsgs.add(errorPrefix + "格式错误，正确格式为：yyyy-MM-dd");
                                isTrue = false;
                            }
                            break;
                        // 道路运输从业资格证编号
                        case "道路运输从业资格证编号(必填)":
                            String xyDriverNumber = StringUtils.deleteWhitespace(cellValue);
                            if (xyDriverNumber.length() > 20) {
                                errorMsgs.add(errorPrefix + "道路运输从业资格证编号长度不能超过20");
                                isTrue = false;
                            } else {
                                saveDTO.setXyDriverNumber(xyDriverNumber);
                            }
                            break;
                        default:
                            break;
                    }// switch end
                }// 循环列结束

                if (isTrue) {
                    // 补充其它信息
                    saveDTO.setServiceCity(cityId);
                    saveDTO.setSupplierId(supplierId);
                    AjaxResponse checkResult = this.completeInfo(saveDTO);
                    if (!checkResult.isSuccess()) {
                        errorMsgs.add("手机号=" + saveDTO.getPhone() + "数据有误:" + checkResult.getMsg());
                        failedCount++;// 失败条数+1
                        continue;
                    }
                    saveDTO.setStatus(1);
                    //保存司机信息,存入审核列表
                    AjaxResponse saveResult = this.saveAuditDriverToMongo(saveDTO);
                    if (!saveResult.isSuccess()) {
                        errorMsgs.add("手机号=" + saveDTO.getPhone() + "保存出错，错误=" + saveResult.getMsg());
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
        if(count==0){
            return AjaxResponse.failMsg(RestErrorCode.FILE_ERROR, "表中没有数据，请检查");
        }
        // 将错误列表导出
        Map<Object, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failedCount", failedCount);
        result.put("errorMsgs", errorMsgs);
        result.put("total",count);
        if (errorMsgs.size() > 0) {
            //将错误信息放到redis中
            String errMsgKey = BusConstant.ERROR_DRIVER_KEY + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            RedisCacheUtil.set(errMsgKey, errorMsgs, BusConstant.ERROR_IMPORT_KEY_EXPIRE);
            result.put("errorMsgKey", errMsgKey);
        }
        return AjaxResponse.success(result);
    }

    private String transfTime(String data) {
        if (data.contains("/")) {
            data = data.replace("/", "-");
        }
        return data;
    }

    /**
     * @param evaluator
     * @param cell
     * @return String
     * @throws
     * @Title: getCellValue
     * @Description: 获取单元格值
     */
    public String getCellValue(Cell cell, FormulaEvaluator evaluator) {
        if (cell == null) {
            return null;
        }
        String cellStringValue = "";
        // 以下是判断数据的类型
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC: // 数字
                short format = cell.getCellStyle().getDataFormat();
                SimpleDateFormat sdf = null;
                if (format == 14 || format == 31 || format == 57 || format == 58) {
                    //日期
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date val = cell.getDateCellValue();
                    cellStringValue = sdf.format(val);
                } else if (format == 20 || format == 32) {
                    //时间
                    sdf = new SimpleDateFormat("HH:mm");
                    Date val = cell.getDateCellValue();
                    cellStringValue = sdf.format(val);
                } else { // 纯数字 只保留整数部分
                    DecimalFormat df = new DecimalFormat("########");
                    cellStringValue = df.format(cell.getNumericCellValue());
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
     * @param driverId
     * @return void
     * @throws
     * @Title: resetIMEI
     * @Description: 重置imei
     */
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.MASTER))
    public int resetIMEI(Integer driverId) {
        // 创建操作记录
       // busBizChangeLogService.insertLog(BusinessType.DRIVER, String.valueOf(driverId), new Date());
        int succtss = carBizDriverInfoExMapper.resetIMEI(driverId);
        try {
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("driverId", driverId);
            String url = mpReatApiUrl + Common.DRIVER_WIDE_MQ;
            String jsonObjectStr = HttpClientUtil.buildPostRequest(url).addParams(paramMap).addHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED).execute();
            logger.info("driverId={}重置imei调用新增接口, result={}", driverId, jsonObjectStr);
        } catch (HttpException e) {
            logger.info("driverId={}重置imei调用接口, error={}", driverId, e);
        }
        return succtss;
    }

    /**
     * 根据巴士司机手机号查询巴士司机信息
     * @param busDriverPhone
     * @return
     */
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.SLAVE))
    public BusDriverDetailInfoVO selectByBusDriverPhone(String busDriverPhone){
        BusDriverDetailInfoVO carBizDriverInfo = busCarBizDriverInfoExMapper.queryBusDriverInfoByPhone(busDriverPhone);
        return carBizDriverInfo;
    }
    public AjaxResponse queryBusDriverAuditList(BusDriverQueryDTO busDriverDTO) {
        Query query = new Query().limit(busDriverDTO.getPageSize());
        //城市
        if(busDriverDTO.getCityId()!=null){
            query.addCriteria(Criteria.where("serviceCity").is(busDriverDTO.getCityId()));
        }
        //供应商权限
        Set<Integer> authOfSupplier = busDriverDTO.getAuthOfSupplier();
        if(authOfSupplier!=null&&authOfSupplier.size()>0){
            query.addCriteria(Criteria.where("supplierId").in(authOfSupplier));
        }
        //车型类别
        if(busDriverDTO.getGroupId()!=null){
            query.addCriteria(Criteria.where("groupId").is(busDriverDTO.getGroupId()));
        }
        //司机姓名
        if(StringUtils.isNotBlank(busDriverDTO.getName())){
            query.addCriteria(Criteria.where("name").is(busDriverDTO.getName()));
        }
        //手机号
        if(StringUtils.isNotBlank(busDriverDTO.getPhone())){
            query.addCriteria(Criteria.where("phone").is(busDriverDTO.getPhone()));
        }
        if(busDriverDTO.getStatus()!=null){
            query.addCriteria(Criteria.where("status").is(busDriverDTO.getStatus()));
        }
        query.addCriteria(Criteria.where("auditStatus").is(0));
        Integer pageNum = busDriverDTO.getPageNum();
        Integer pageSize = busDriverDTO.getPageSize();
        int start = (pageNum-1)*pageSize;
        query.skip(start-1<0?0:start);
        List<BusDriverInfoAudit> busDriverInfoAudits = busMongoTemplate.find(query, BusDriverInfoAudit.class);
        long count = busMongoTemplate.count(query, BusDriverInfoAudit.class);

        if(CollectionUtils.isEmpty(busDriverInfoAudits)){
            return AjaxResponse.success(new PageDTO(pageNum,pageSize,count,busDriverInfoAudits));
        }
        //补充展示字段
        //城市
        Set<Integer>cityIds=new HashSet<>();
        Set<Integer>supplierIds=new HashSet<>();
        Set<Integer> grouIds=new HashSet<>();
        busDriverInfoAudits.forEach(busDriver->{
            if(busDriver.getServiceCity()!=null){cityIds.add(busDriver.getServiceCity());}
            if(busDriver.getSupplierId()!=null){supplierIds.add(busDriver.getSupplierId());}
            if(busDriver.getGroupId()!=null){grouIds.add(busDriver.getGroupId());}
        });
        List<CarBizCity> carBizCities = carBizCityExMapper.queryNameByIds(cityIds);
        Map<Integer, CarBizCity> cityMap = carBizCities.stream().collect(Collectors.toMap(CarBizCity::getCityId, (o -> o)));
        List<CarBizSupplier> carBizSuppliers = carBizSupplierExMapper.findByIdSet(supplierIds);
        Map<Integer, CarBizSupplier> supplierMap = carBizSuppliers.stream().collect(Collectors.toMap(CarBizSupplier::getSupplierId, (sup -> sup)));

        List<CarBizCarGroup> groups = carBizCarGroupExMapper.queryCarGroupByIdSet(grouIds);
        Map<Integer, CarBizCarGroup> groupMap = groups.stream().collect(Collectors.toMap(CarBizCarGroup::getGroupId, (gop -> gop)));

        //补充字段
        List<BusDriverInfoPageVO> result=new ArrayList<>();
        busDriverInfoAudits.forEach(busDriver->{
            BusDriverInfoPageVO busDriverInfoVO = new BusDriverInfoPageVO();
            BeanUtils.copyProperties(busDriver,busDriverInfoVO);
            CarBizCity carBizCity = cityMap.get(busDriver.getServiceCity());
            if(carBizCity!=null){busDriverInfoVO.setCityName(carBizCity.getCityName());}
            CarBizSupplier carBizSupplier = supplierMap.get(busDriver.getSupplierId());
            if(carBizCities!=null){busDriverInfoVO.setSupplierName(carBizSupplier.getSupplierFullName());}
            CarBizCarGroup group = groupMap.get(busDriver.getGroupId());
            if(group!=null){busDriverInfoVO.setGroupName(group.getGroupName());}
            result.add(busDriverInfoVO);
        });
        return AjaxResponse.success(new PageDTO(pageNum,pageSize,count,result));
    }

    public AjaxResponse checkMongoInfo(BusDriverSaveDTO saveDTO) {
        String id = saveDTO.getId();
        Integer driverId = saveDTO.getDriverId();
        String phone = saveDTO.getPhone();
        String idCardNo = saveDTO.getIdCardNo();
        Boolean had =this.checkMongoPhone(driverId,phone,id);
        if (had) {
            return AjaxResponse.fail(RestErrorCode.DRIVER_PHONE_EXIST);
        }
        had = this.checkMongoIdCardMo(driverId,idCardNo,id);
        if (had) {
            return AjaxResponse.fail(RestErrorCode.DRIVER_IDCARNO_EXIST);
        }
        return AjaxResponse.success(true);
    }

    /**
     * 校验mongo中审核司机是否存在该身份证号司机
     * @param driverId
     * @param idCardNo
     * @return
     */
    private Boolean checkMongoIdCardMo(Integer driverId, String idCardNo,String id) {
        Query query = new Query();
        //未审核司机
        query.addCriteria(Criteria.where("auditStatus").is(0));
        //有效司机
        query.addCriteria(Criteria.where("status").is(1));
        query.addCriteria(Criteria.where("idCardNo").is(idCardNo));
        if(driverId != null){
            query.addCriteria(Criteria.where("driverId").nin(driverId));
        }
        if(StringUtils.isNotBlank(id)){
            query.addCriteria(Criteria.where("_id").nin(id));
        }
        List<BusDriverInfoAudit> busDriverInfoAudits = busMongoTemplate.find(query, BusDriverInfoAudit.class);
        if(CollectionUtils.isNotEmpty(busDriverInfoAudits)){
            return true;
        }
        return  false;
    }

    /**
     * 校验mongo中审核司机是否存在该手机号司机
     * @param driverId
     * @param phone
     * @return
     */
    private Boolean checkMongoPhone(Integer driverId, String phone,String id) {
        Query query = new Query();
        //未审核司机
        query.addCriteria(Criteria.where("auditStatus").is(0));
        //有效司机
        query.addCriteria(Criteria.where("status").is(1));
        query.addCriteria(Criteria.where("phone").is(phone));
        if(driverId != null){
            query.addCriteria(Criteria.where("driverId").nin(driverId));
        }
        if(StringUtils.isNotBlank(id)){
            query.addCriteria(Criteria.where("_id").nin(id));
        }
        List<BusDriverInfoAudit> busDriverInfoAudits = busMongoTemplate.find(query, BusDriverInfoAudit.class);
        if(CollectionUtils.isNotEmpty(busDriverInfoAudits)){
            return true;
        }
        return  false;
    }

    /**
     * 新增司机到审核列表
     * @param saveDTO
     * @return
     */
    public AjaxResponse saveAuditDriverToMongo(BusDriverSaveDTO saveDTO) {
        logger.info("操作方式：新建,审核Mongo数据:" + JSON.toJSONString(saveDTO));

        try {
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
            BusDriverInfoAudit busDriverInfoAudit = BeanUtil.copyObject(saveDTO, BusDriverInfoAudit.class);
            busDriverInfoAudit.setCreateDate(new Date());
            busDriverInfoAudit.setCreateBy(WebSessionUtil.getCurrentLoginUser().getId());
            busDriverInfoAudit.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
            busDriverInfoAudit.setUpdateDate(new Date());
            //审核状态 未审核
            busDriverInfoAudit.setAuditStatus(0);
            //来源 创建
            busDriverInfoAudit.setStemFrom(0);

            busMongoTemplate.insert(busDriverInfoAudit);

            return AjaxResponse.success(null);
        } catch (Exception e) {
            logger.error("新增审核司机信息异常,error={}", e.getMessage(), e);
            return AjaxResponse.failMsg(RestErrorCode.HTTP_SYSTEM_ERROR, "保存审核司机信息异常");
        }
    }

    /**
     * 新增修改司机到审核列表
     * @param saveDTO
     * @return
     */
    public AjaxResponse saveUpdateAuditDriverToMongo(BusDriverSaveDTO saveDTO) {
        logger.info("操作方式：编辑,审核Mongo新数据:" + JSON.toJSONString(saveDTO));
        try {
            String idCardNo = saveDTO.getIdCardNo();
            if ("X".equals(idCardNo.substring(idCardNo.length() - 1, idCardNo.length()))) {
                idCardNo = idCardNo.toLowerCase();
            }
            saveDTO.setIdCardNo(idCardNo);
            // 机动车驾驶证号
            if (StringUtils.isBlank(saveDTO.getDriverlicensenumber())) {
                saveDTO.setDriverlicensenumber(idCardNo);
            }
            BusDriverInfoAudit busDriverInfoAudit = BeanUtil.copyObject(saveDTO, BusDriverInfoAudit.class);
            busDriverInfoAudit.setCreateDate(new Date());
            busDriverInfoAudit.setCreateBy(WebSessionUtil.getCurrentLoginUser().getId());
            busDriverInfoAudit.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
            busDriverInfoAudit.setUpdateDate(new Date());
            //审核状态 未审核
            busDriverInfoAudit.setAuditStatus(0);
            //来源 修改
            busDriverInfoAudit.setStemFrom(1);

            busMongoTemplate.insert(busDriverInfoAudit);

            return AjaxResponse.success("保存成功，司机进入审核");
        }catch (Exception e) {
        logger.error("新增修改审核司机信息异常,error={}", e.getMessage(), e);
        return AjaxResponse.failMsg(RestErrorCode.HTTP_SYSTEM_ERROR, "保存审核司机修改信息异常");
        }


    }

    /**
     * 审核列表司机修改
     * @param saveDTO
     * @return
     */
    public AjaxResponse updateAuditDriverToMongo(BusDriverSaveDTO saveDTO) {
        logger.info("操作方式：编辑,审核Mongo修改数据:" + JSON.toJSONString(saveDTO));
        try {
            String idCardNo = saveDTO.getIdCardNo();
            if ("X".equals(idCardNo.substring(idCardNo.length() - 1, idCardNo.length()))) {
                idCardNo = idCardNo.toLowerCase();
            }
            saveDTO.setIdCardNo(idCardNo);
            // 机动车驾驶证号
            if (StringUtils.isBlank(saveDTO.getDriverlicensenumber())) {
                saveDTO.setDriverlicensenumber(idCardNo);
            }
            Query query = Query.query(Criteria.where("_id").is(saveDTO.getId()));
            BusDriverInfoAudit busDriverInfoAudit = busMongoTemplate.findById(saveDTO.getId(),BusDriverInfoAudit.class);
            if(busDriverInfoAudit != null){
                Update update = new Update();
                BeanUtils.copyProperties(saveDTO,busDriverInfoAudit);
                busDriverInfoAudit.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
                busDriverInfoAudit.setUpdateDate(new Date());

                BeanWrapper beanWrapper = new BeanWrapperImpl(busDriverInfoAudit);
                Field[] fields = busDriverInfoAudit.getClass().getDeclaredFields();
                String name = null;
                for (Field field : fields) {
                    name = field.getName();
                    if("serialVersionUID".equalsIgnoreCase(name)){
                        continue;
                    }
                    if (null != beanWrapper.getPropertyValue(name)) {
                        update.set(name, beanWrapper.getPropertyValue(name));
                    }

                }
                busMongoTemplate.updateFirst(query, update, BusDriverInfoAudit.class);
                return AjaxResponse.success(null);
            }else{
                return AjaxResponse.failMsg(RestErrorCode.HTTP_SYSTEM_ERROR, "审核列表司机修改信息异常");
            }
        }catch (Exception e) {
            logger.error("新增修改审核司机信息异常,error={}", e.getMessage(), e);
            return AjaxResponse.failMsg(RestErrorCode.HTTP_SYSTEM_ERROR, "保存审核列表司机修改信息异常");
        }
    }

    /**
     * 审核列表司机审核
     * @param ids
     * @return
     */
    public AjaxResponse auditDriver(String ids) {
        logger.info("操作方式：审核,审核司机:" + JSON.toJSONString(ids));
        try{
            String[] idArray = ids.split(",");
            List<String> idList = Arrays.asList(idArray);
            if(CollectionUtils.isEmpty(idList)){
                return AjaxResponse.failMsg(RestErrorCode.HTTP_PARAM_INVALID, "审核司机参数为空");
            }
            for (String id: idList) {
                Query query = new Query();
                query.addCriteria(Criteria.where("id").is(id));
                query.addCriteria(Criteria.where("auditStatus").is(0));
                BusDriverInfoAudit busDriverInfoAudit = busMongoTemplate.findOne(query,BusDriverInfoAudit.class);
                if(busDriverInfoAudit==null){
                    continue;
                }
                BusDriverSaveDTO saveDTO = BeanUtil.copyObject(busDriverInfoAudit, BusDriverSaveDTO.class);
                this.completeInfo(saveDTO);
                //修改审核表审核状态，审核人，审核时间
                Update update = new Update();
                update.set("auditStatus", 1);
                update.set("auditor", WebSessionUtil.getCurrentLoginUser().getId());
                update.set("auditDate", new Date());
                AjaxResponse saveResult = AjaxResponse.success(null);
                //写库操作
                if(busDriverInfoAudit.getStemFrom() == 0){
                    //新增司机审核通过审核
                    saveDTO.setCreateBy(busDriverInfoAudit.getCreateBy());
                    saveDTO.setCreateDate(busDriverInfoAudit.getCreateDate());
                    saveDTO.setUpdateBy(busDriverInfoAudit.getUpdateBy());
                    saveDTO.setUpdateDate(busDriverInfoAudit.getUpdateDate());
                    AjaxResponse response = this.saveDriver(saveDTO);
                    if(!response.isSuccess()){
                        return response;
                    }
                }else{
                    //修改司机审核通过
                    BusDriverDetailInfoVO data = this.findDriverInfoByDriverId(saveDTO.getDriverId());
                    saveDTO.setCreateBy(null);
                    saveDTO.setCreateDate(null);
                    saveDTO.setUpdateBy(busDriverInfoAudit.getUpdateBy());
                    saveDTO.setUpdateDate(busDriverInfoAudit.getUpdateDate());
                    this.updateDriver(saveDTO);
                    //插入修改记录
                    this.saveUpdateLog(data,saveDTO.getDriverId());
                }
                //修改审核状态
                WriteResult writeResult = busMongoTemplate.updateFirst(query, update, BusDriverInfoAudit.class);
                SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //插入审核记录
                StringBuffer log = new StringBuffer();
                log.append("司机审核通过：");
                log.append("审核人id："+WebSessionUtil.getCurrentLoginUser().getId()+",");
                log.append("审核人姓名：" +WebSessionUtil.getCurrentLoginUser().getLoginName()+",");
                log.append("审核日期：" + f.format(new Date()));
                busBizChangeLogService.insertLog(BusinessType.DRIVER, String.valueOf(saveDTO.getDriverId()),log.toString(), new Date());
            }
            return AjaxResponse.success(null);
        }catch (Exception e){
            logger.error("审核司机异常,error={}", e.getMessage(), e);
            return AjaxResponse.failMsg(RestErrorCode.HTTP_SYSTEM_ERROR, "审核司机异常");
        }
    }

    /**
     * 插入修改司机记录
     * @param driverInfo
     * @param driverId
     */
    public void saveUpdateLog(BusDriverDetailInfoVO driverInfo, Integer driverId) {
        try {
            BusDriverCompareEntity oldDriver = new BusDriverCompareEntity();
            BeanUtils.copyProperties(driverInfo,oldDriver);
            oldDriver.setStatus(driverInfo.getStatus()==1?"有效":"无效");
            oldDriver.setGender(driverInfo.getGender()==0?"女":"男");
            String oldLicenseType = this.getDrivingLicenseType(driverInfo.getDrivingLicenseType());
            oldDriver.setDrivingLicenseType(oldLicenseType);
            BusDriverCompareEntity newDriver=new BusDriverCompareEntity();
            //防止主从延迟，切换主库查询
            DynamicRoutingDataSource.setMasterSlave("rentcar-DataSource", DataSourceMode.MASTER);
            //查询最新的信息
            BusDriverDetailInfoVO carBizDriverInfo  = findDriverInfoByDriverId(driverId);
            BeanUtils.copyProperties(carBizDriverInfo,newDriver);
            newDriver.setStatus(carBizDriverInfo.getStatus()==1?"有效":"无效");
            newDriver.setGender(carBizDriverInfo.getGender()==0?"女":"男");
            String newLicenseType = this.getDrivingLicenseType(carBizDriverInfo.getDrivingLicenseType());
            newDriver.setDrivingLicenseType(newLicenseType);
            List<Object> objects = CompareObjectUtils.contrastObj(oldDriver, newDriver, null);
            if(objects.size()!=0){
                String join = StringUtils.join(objects, ",");
                logger.info("巴士司机入库操作开始，修改司机--{}，插入内容--{}",driverId,join);
                busBizChangeLogService.insertLog(BusinessType.DRIVER, String.valueOf(driverId),join, new Date());
                logger.info("巴士司机入库操作完成，修改司机--{}",driverId);
            }else {
                logger.info("巴士司机无信息进行修改，司机id--{}",driverId);
            }
        } catch (BeansException e) {
            logger.error("[ BusDriverInfoController-saveUpdateLog ] 保存操作日志异常", e);
        }
    }

    /**
     * 查询司机信息
     * @param driverId
     * @return
     */
    private BusDriverDetailInfoVO findDriverInfoByDriverId(Integer driverId) {
        CarBizDriverInfo carBizDriverInfo = carBizDriverInfoService.selectByPrimaryKey(driverId);
        if (carBizDriverInfo == null) {
            return null;
        }
        BusDriverDetailInfoVO busDriverDetailInfoVO = BeanUtil.copyObject(carBizDriverInfo, BusDriverDetailInfoVO.class);
        // 查询司机银行卡信息
        CarBizDriverInfoDetailDTO carBizDriverInfoDetailDTO = carBizDriverInfoDetailService.selectByDriverId(driverId);
        if (carBizDriverInfoDetailDTO != null) {
            busDriverDetailInfoVO.setBankCardNumber(carBizDriverInfoDetailDTO.getBankCardNumber());
            busDriverDetailInfoVO.setBankCardBank(carBizDriverInfoDetailDTO.getBankCardBank());
        }
        // 查询城市名称，供应商名称，服务类型，加盟类型
        getBaseStatis(busDriverDetailInfoVO);
        return busDriverDetailInfoVO;
    }

    /**
     * 根据物理主键查询审核司机详情
     * @param id
     * @return
     */
    public BusDriverInfoAudit findAuditDriverInfoById( String id) {

        BusDriverInfoAudit busDriverInfoAudit = busMongoTemplate.findById(id,BusDriverInfoAudit.class);
        return busDriverInfoAudit;
    }

    /**
     * 校验司机是否有服务中订单
     * @param driverId
     * @return
     */
    public boolean isInService(Integer driverId){
        //默认在服务中不能修改
        boolean result= true;
        Map<String,Object>param = new HashMap(2);
        param.put("driverId",driverId);
        param.put("businessId", Common.BUSINESSID);
        String sign = SignUtils.createMD5Sign(param, Common.KEY);
        param.put("sign",sign);
        String url = order_url+ BusConst.Order.GET_SERVICE_ORDER;
        String response = MpOkHttpUtil.okHttpPost(url, param, 3, "判断司机是否在服务中");
        logger.info("判断司机是否在服务中："+response);
        if(response!=null){
            JSONObject res = JSONObject.parseObject(response);
            if(res.getInteger("code")!=null&&res.getInteger("code")==0){
                JSONObject data = res.getJSONObject("data");
                Integer orderCount = data.getInteger("orderCount");
                if(orderCount>0){
                    result=true;
                }else{
                    result=false;
                }
            }
        }
        return result;
    }

    private String getDrivingLicenseType(String drivingLicenseType){
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
}
