package com.zhuanche.serv;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhuanche.common.cache.RedisCacheDriverUtil;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.rocketmq.CommonRocketProducer;
import com.zhuanche.common.rocketmq.CommonRocketProducerDouble;
import com.zhuanche.common.rocketmq.DriverWideRocketProducer;
import com.zhuanche.common.sms.SmsSendUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constant.Constants;
import com.zhuanche.constant.EnvUtils;
import com.zhuanche.constants.SmsTempleConstants;
import com.zhuanche.dto.driver.TelescopeDriver;
import com.zhuanche.dto.rentcar.*;
import com.zhuanche.entity.mdbcarmanage.*;
import com.zhuanche.entity.rentcar.*;
import com.zhuanche.http.HttpClientUtil;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.mongo.DriverMongo;
import com.zhuanche.serv.driverteam.CarDriverTeamService;
import com.zhuanche.serv.mdbcarmanage.CarBizDriverUpdateService;
import com.zhuanche.serv.mongo.DriverMongoService;
import com.zhuanche.serv.order.OrderService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.Common;
import com.zhuanche.util.DateUtil;
import com.zhuanche.util.Md5Util;
import com.zhuanche.util.ValidateUtils;
import com.zhuanche.util.encrypt.MD5Utils;
import mapper.mdbcarmanage.CarAdmUserMapper;
import mapper.mdbcarmanage.CarDriverTeamMapper;
import mapper.mdbcarmanage.CarRelateGroupMapper;
import mapper.mdbcarmanage.CarRelateTeamMapper;
import mapper.mdbcarmanage.ex.*;
import mapper.rentcar.CarBizDriverAccountMapper;
import mapper.rentcar.CarBizDriverInfoMapper;
import mapper.rentcar.CarBizSupplierMapper;
import mapper.rentcar.ex.CarBizCarInfoExMapper;
import mapper.rentcar.ex.CarBizDriverInfoExMapper;
import mapper.rentcar.ex.CarBizModelExMapper;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.entity.ContentType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.FileInputStream;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CarBizDriverInfoService {

    private static final Logger logger = LoggerFactory.getLogger(CarBizDriverInfoService.class);
    private static final String LOGTAG = "[????????????]: ";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    //????????????????????????
    private static final String DRIVER_SERVICE_TRIPLIST_URL="/trip/driverHasServiceOrderOrNot";

    // ????????????redis??????
    public static final String DRIVER_FLASH_REDIS_URL = "/api/v2/driver/flash/driverInfo";

    @Value("${order.server.api.base.url}")
    String orderServiceApiBaseUrl;

    @Value("${driver.server.api.url}")
    String driverServiceApiUrl;

    @Value("${order.statistics.url}")
    String orderStatisticsUrl;

    //????????????id
    @Value("${order.bid}")
    String bId;

    @Value("${order.sign.key}")
    String signKey;

    @Autowired
    private CarBizDriverInfoService carBizDriverInfoService;

    @Autowired
    private DriverIncomeScoreService driverIncomeScoreService;

    @Autowired
    private CarBizDriverInfoMapper carBizDriverInfoMapper;

    @Autowired
    private CarBizDriverInfoExMapper carBizDriverInfoExMapper;

    @Autowired
    private CarBizCarInfoExMapper carBizCarInfoExMapper;

    @Autowired
    private CarBizDriverAccountMapper carBizDriverAccountMapper;

    @Autowired
    private CarBizDriverInfoDetailService carBizDriverInfoDetailService;

    @Autowired
    private CarBizDriverUpdateService carBizDriverUpdateService;

    @Autowired
    private CarBizChatUserService carBizChatUserService;

    @Autowired
    private CarBizCityService carBizCityService;

    @Autowired
    private CarBizCarInfoService carBizCarInfoService;

    @Autowired
    private CarBizSupplierService carBizSupplierService;

    @Autowired
    private CarBizCarGroupService carBizCarGroupService;

    @Autowired
    private CarBizCooperationTypeService carBizCooperationTypeService;

    @Autowired
    private CarDriverTeamMapper carDriverTeamMapper;

    @Autowired
    private CarDriverTeamExMapper carDriverTeamExMapper;

    @Autowired
    private CarRelateTeamMapper carRelateTeamMapper;

    @Autowired
    private CarRelateTeamExMapper carRelateTeamExMapper;

    @Autowired
    private CarRelateGroupMapper carRelateGroupMapper;

    @Autowired
    private CarRelateGroupExMapper carRelateGroupExMapper;

    @Autowired
    private CarAdmUserMapper carAdmUserMapper;

    @Autowired
    private CarBizAgreementCompanyExMapper carBizAgreementCompanyExMapper;

    @Autowired
    private DriverMongoService driverMongoService;

    @Autowired
    private CarDriverTeamService carDriverTeamService;

    @Autowired
    private CarBizModelExMapper carBizModelExMapper;
    @Autowired
    private CarAdmUserExMapper carAdmUserExMapper;
    @Value("${telescope.supplierId}")
    private Integer telescopeSupplierId ;

    @Autowired
    private DriverTelescopeUserExMapper driverTelescopeUserExMapper;

    @Autowired
    private CarBizSupplierMapper carBizSupplierMapper;

    @Autowired
    private OrderService orderService;

    @Value("${mp.restapi.url}")
    private String mpReatApiUrl;

    /**
     * ??????????????????????????????
     *
     * @param params
     * @return
     */
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
    } )
    public List<CarBizDriverInfoDTO> queryDriverList(CarBizDriverInfoDTO params) {
        return carBizDriverInfoExMapper.queryDriverList(params);
    }

    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
    } )
    public PageDTO  queryDriverIncomeScoreListData(int page,int pageSize,CarBizDriverInfoDTO carBizDriverInfoDTO) {
        Page p = PageHelper.startPage(page, pageSize, true);
        List<CarBizDriverInfoDTO> list = null;
        int total = 0;
        try {
            list = this.queryDriverList(carBizDriverInfoDTO);
            total = (int) p.getTotal();
        } finally {
            PageHelper.clearPage();
        }
        // ??????????????????????????????????????????????????????????????????
        for (CarBizDriverInfoDTO driver : list) {
            carBizDriverInfoService.getBaseStatis(driver);
        }
        fillIncomeScore(list);
        PageDTO pageDTO = new PageDTO(page, pageSize, total, list);
        return pageDTO;
    }


    private void fillIncomeScore(List<CarBizDriverInfoDTO> rows) {
        Map<Integer, DriverIncomeScoreDto> map = driverIncomeScoreService.incomeList(rows.stream().map(CarBizDriverInfoDTO::getDriverId).collect(Collectors.toList()));
        if (null == map) return;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:dd");
        DriverIncomeScoreDto dto;
        for (CarBizDriverInfoDTO info : rows) {
            dto = map.get(info.getDriverId());
            if (null != dto) {
                info.setIncomeScore(dto.getIncomeScore());
                info.setServiceScore(dto.getServiceScore());
                info.setTripScore(dto.getTripScore());
                info.setAppendScore(dto.getAppendScore());
                if (null != dto.getUpdateTime())
                    info.setUpdateTime(df.format(new Date(dto.getUpdateTime())));
            }
        }
    }

    /**
     * ????????????????????????????????????????????????????????????????????????
     *
     * @param params
     * @return
     */
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
    } )
    public List<CarBizDriverInfoDTO> queryDriverListForSaas(CarBizDriverInfoDTO params) {
        return carBizDriverInfoExMapper.queryDriverListForSaas(params);
    }





    public PageInfo<CarBizDriverInfoDTO> queryDriverPage(CarBizDriverInfoDTO params,int pageNo,int pageSize) {
        PageHelper.startPage(pageNo, pageSize, true);
        List<CarBizDriverInfoDTO> list = carBizDriverInfoExMapper.queryDriverList(params);
        PageInfo<CarBizDriverInfoDTO> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    /**
     * ???????????????????????????
     *
     * @param phone    ??????????????????
     * @param driverId ??????ID????????????
     * @return
     */
    public Boolean checkPhone(String phone, Integer driverId) {
        int count = carBizDriverInfoExMapper.checkPhone(phone, driverId);
        if (count > 0) {
            return true;
        }
        return false;
    }

    /**
     * ???????????????????????????
     *
     * @param idCardNo ????????????
     * @param driverId ??????ID????????????
     * @return
     */
    public Boolean checkIdCardNo(String idCardNo, Integer driverId) {
        int count = carBizDriverInfoExMapper.checkIdCardNo(idCardNo, driverId);
        if (count > 0) {
            return true;
        }
        return false;
    }

    /**
     * ????????????ID????????????
     *
     * @param driverId
     * @return
     */
    public CarBizDriverInfo selectByPrimaryKey(Integer driverId) {
        return carBizDriverInfoMapper.selectByPrimaryKey(driverId);
    }

    /**
     * ??????imei
     *
     * @param driverId
     * @return
     */
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.MASTER )
    } )
    public int resetIMEI(Integer driverId) {
        int i = carBizDriverInfoExMapper.resetIMEI(driverId);
        try {
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("driverId", driverId);

            String url = mpReatApiUrl + Common.DRIVER_WIDE_MQ;
            String jsonObjectStr = HttpClientUtil.buildPostRequest(url).addParams(paramMap).addHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED).execute();
            logger.info("driverId={}??????imei??????????????????, result={}", driverId, jsonObjectStr);
        } catch (HttpException e) {
            logger.info("driverId={}??????imei????????????, error={}", driverId, e);
        }

        return i;
    }

//    /**
//     * ??????????????????
//     *
//     * @param carBizDriverInfo
//     * @return
//     */
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE),
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.MASTER)
    })
//    public Map<String, Object> updateDriver(CarBizDriverInfoDTO carBizDriverInfo) {
//        Map<String, Object> resultMap = Maps.newHashMap();
//        try {
//            try {
//                logger.info(LOGTAG + "?????????????????????,?????????:" + JSON.toJSONString(carBizDriverInfo));
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//            // ??????????????????????????????????????????????????????????????????
//            carBizDriverInfo = this.getBaseStatis(carBizDriverInfo);
//            // ????????????????????????????????????????????????????????? ??????
//            if(carBizDriverInfo.getCooperationType()!=null && carBizDriverInfo.getCooperationType()==5){
//                CarBizAgreementCompany company = carBizAgreementCompanyExMapper.selectByName(carBizDriverInfo.getCorptype());
//                if(company==null){
//                    resultMap.put("result", 1);
//                    resultMap.put("msg", " ?????????????????????????????????????????????????????????????????????");
//                    return resultMap;
//                }
//            }
//
//            // ??????????????????Id
//            carBizDriverInfo.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
//            carBizDriverInfo.setUpdateDate(new Date());
//            //
//            String idCardNo = carBizDriverInfo.getIdCardNo();
//            if ("X".equals(idCardNo.substring(idCardNo.length() - 1, idCardNo.length()))) {
//                idCardNo = idCardNo.toLowerCase();
//            }
//            carBizDriverInfo.setIdCardNo(idCardNo);
//            carBizDriverInfo.setDriverlicensenumber(idCardNo);//?????????????????????
//
//            if (carBizDriverInfo.getPasswordReset()!=null && carBizDriverInfo.getPasswordReset() == 1) {//????????????
//                carBizDriverInfo.setPassword(getPassword(idCardNo));
//            }
//
//            //????????????ID????????????
//            CarBizDriverInfo orginDriverInfo = carBizDriverInfoMapper.selectByPrimaryKey(carBizDriverInfo.getDriverId());
//            if (orginDriverInfo != null) {
//                carBizDriverInfo.setOldPhone(orginDriverInfo.getPhone());//?????????
//                carBizDriverInfo.setOldIdCardNo(orginDriverInfo.getIdCardNo());//?????????
//                carBizDriverInfo.setOldDriverLicenseNumber(orginDriverInfo.getDriverlicensenumber());//?????????????????????
//                carBizDriverInfo.setOldDriverLicenseIssuingNumber(orginDriverInfo.getDriverlicenseissuingnumber());//?????????????????????????????????????????????
//                carBizDriverInfo.setOldLicensePlates(orginDriverInfo.getLicensePlates());//?????????
//                carBizDriverInfo.setOldCity(orginDriverInfo.getServiceCity());//??????
//                carBizDriverInfo.setOldSupplier(orginDriverInfo.getSupplierId());//?????????
//            }
//            try {
//                logger.info(LOGTAG + "?????????????????????,????????????:" + JSON.toJSONString(carBizDriverInfo));
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//
//            //??????????????????
//            DynamicRoutingDataSource.setMasterSlave("rentcar-DataSource", DataSourceMode.MASTER);
//            int n = this.updateDriverInfo(carBizDriverInfo);
//
//            // ?????????????????? ?????? ????????????????????? ?????????????????????????????????
//            if (n > 0) {
//                logger.info("****************?????? ????????????????????? ?????????????????????????????????");
////                if(carInfo!=null){
//                carBizCarInfoExMapper.updateCarLicensePlates(carBizDriverInfo.getLicensePlates(), carBizDriverInfo.getDriverId());
////                }
//            }
//
//            // ??????mongoDB
//            DriverMongo driverMongo = driverMongoService.findByDriverId(carBizDriverInfo.getDriverId());
//            if (driverMongo != null) {
//                driverMongoService.updateDriverMongo(carBizDriverInfo);
//            } else {
//                driverMongoService.saveDriverMongo(carBizDriverInfo);
//            }
//
//            String method = "UPDATE";
//            // ????????????????????????????????????????????????
//            if (carBizDriverInfo.getStatus().intValue() == 0) {
//                method = "DELETE";
//                this.updateDriverByXiao(carBizDriverInfo);
//            }
//
//            //?????????????????????????????????
//            if ((carBizDriverInfo.getOldCity() != null && !carBizDriverInfo.getOldCity().equals(carBizDriverInfo.getServiceCity()))
//                    || (carBizDriverInfo.getOldSupplier() != null && !carBizDriverInfo.getOldSupplier().equals(carBizDriverInfo.getSupplierId()))) {
//                logger.info("????????????driverId=" + carBizDriverInfo.getDriverId() + "?????????????????????????????????????????????????????????");
//                // ??????????????????????????????
//                carRelateTeamExMapper.deleteByDriverId(carBizDriverInfo.getDriverId());
//                carRelateGroupExMapper.deleteByDriverId(carBizDriverInfo.getDriverId());
//                carBizDriverInfo.setTeamId(null);
//                carBizDriverInfo.setTeamName("");
//                carBizDriverInfo.setTeamGroupId(null);
//                carBizDriverInfo.setTeamGroupName("");
//            }
//
//            carBizDriverInfo.setCreateDate(orginDriverInfo.getCreateDate());
//            //??????MQ
//            if (carBizDriverInfo.getStatus() == 0) {
//                sendDriverToMq(carBizDriverInfo, "DELETE");
//            } else {
//                sendDriverToMq(carBizDriverInfo, "UPDATE");
//            }
//
//            try {
//                // ???????????????????????????????????????
//                driverUpdate(carBizDriverInfo);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            resultMap.put("result", 0);
//            resultMap.put("msg", "??????");
//        } catch (Exception e) {
//            resultMap.put("result", 1);
//            resultMap.put("msg", e.getMessage());
//            e.printStackTrace();
//        }
//        return resultMap;
//    }

    /**
     * ??????????????????
     *
     * @param carBizDriverInfo
     * @return
     */
//    public Map<String, Object> saveDriver(CarBizDriverInfoDTO carBizDriverInfo) {
//        Map<String, Object> resultMap = Maps.newHashMap();
//
//        try {
//            logger.info(LOGTAG + "?????????????????????,??????:" + JSON.toJSONString(carBizDriverInfo));
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        try {
//            // ??????????????????????????????????????????????????????????????????
//            carBizDriverInfo = this.getBaseStatis(carBizDriverInfo);
//            // ????????????????????????????????????????????????????????? ??????
//            if(carBizDriverInfo.getCooperationType()!=null && carBizDriverInfo.getCooperationType()==5){
//                CarBizAgreementCompany company = carBizAgreementCompanyExMapper.selectByName(carBizDriverInfo.getCorptype());
//                if(company==null){
//                    resultMap.put("result", 1);
//                    resultMap.put("msg", " ?????????????????????????????????????????????????????????????????????");
//                    return resultMap;
//                }
//            }
//
//            // ??????????????????Id
//            carBizDriverInfo.setCreateBy(WebSessionUtil.getCurrentLoginUser().getId());
//            carBizDriverInfo.setCreateDate(new Date());
//            carBizDriverInfo.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
//            carBizDriverInfo.setUpdateDate(new Date());
//            carBizDriverInfo.setStatus(1);
//            //
//            String idCardNo = carBizDriverInfo.getIdCardNo();
//            if ("X".equals(idCardNo.substring(idCardNo.length() - 1, idCardNo.length()))) {
//                idCardNo = idCardNo.toLowerCase();
//            }
//            carBizDriverInfo.setIdCardNo(idCardNo);
//            carBizDriverInfo.setDriverlicensenumber(idCardNo);//?????????????????????
//            carBizDriverInfo.setPassword(getPassword(carBizDriverInfo.getIdCardNo()));
//
//            // ?????????????????????mysql???mongo
//            DynamicRoutingDataSource.setMasterSlave("rentcar-DataSource", DataSourceMode.MASTER);
//            int n = this.saveDriverInfo(carBizDriverInfo);
//            driverMongoService.saveDriverMongo(carBizDriverInfo);
//
//            if (n > 0) {
//                // ?????? ????????????????????? ?????????????????????????????????
////                if(carBizCarInfo!=null){
//                carBizCarInfoExMapper.updateCarLicensePlates(carBizDriverInfo.getLicensePlates(), carBizDriverInfo.getDriverId());
////                }
//            }
//            carBizChatUserService.insertChat(carBizDriverInfo.getDriverId());
//
//            // teamId teamGroupId ?????????????????????????????????????????????
//            if(carBizDriverInfo.getTeamId()!=null){//????????????
//                CarRelateTeam record = new CarRelateTeam();
//                record.setTeamId(carBizDriverInfo.getTeamId());
//                record.setDriverId(carBizDriverInfo.getDriverId());
//                carRelateTeamMapper.insertSelective(record);
//            }
//            if(carBizDriverInfo.getTeamGroupId()!=null){//????????????
//                CarRelateGroup record = new CarRelateGroup();
//                record.setGroupId(carBizDriverInfo.getTeamGroupId());
//                record.setDriverId(carBizDriverInfo.getDriverId());
//                carRelateGroupMapper.insertSelective(record);
//            }
//
//            //??????MQ
//            sendDriverToMq(carBizDriverInfo, "INSERT");
//
//            resultMap.put("result", 0);
//            resultMap.put("msg", "??????");
//        } catch (Exception e) {
//            resultMap.put("result", 1);
//            resultMap.put("msg", e.getMessage());
//            e.printStackTrace();
//        }
//        return resultMap;
//    }

    /**
     * ??????????????????
     *
     * @param carBizDriverInfoDTO
     * @return
     */
//    public int saveDriverInfo(CarBizDriverInfoDTO carBizDriverInfoDTO) {
//        if (carBizDriverInfoDTO.getExpireDate().equals("")) {
//            carBizDriverInfoDTO.setExpireDate(null);
//        }
//        if (carBizDriverInfoDTO.getIssueDate().equals("")) {
//            carBizDriverInfoDTO.setIssueDate(null);
//        }
//        carBizDriverInfoExMapper.insertCarBizDriverInfoDTO(carBizDriverInfoDTO);
//        int driverId = carBizDriverInfoDTO.getDriverId();
//
//        //??????????????????????????????????????????
//        CarBizDriverInfoDetail carBizDriverInfoDetail = new CarBizDriverInfoDetail();
//        carBizDriverInfoDetail.setBankCardBank(carBizDriverInfoDTO.getBankCardBank());
//        carBizDriverInfoDetail.setBankCardNumber(carBizDriverInfoDTO.getBankCardNumber());
//        carBizDriverInfoDetail.setDriverId(carBizDriverInfoDTO.getDriverId());
//        carBizDriverInfoDetail.setExt1(2);//??????????????????  1?????? 2??????  ????????????????????????2
//        carBizDriverInfoDetailService.insertSelective(carBizDriverInfoDetail);
//
//        // ??????????????????
//        CarBizDriverAccount accountPojo = new CarBizDriverAccount();
//        accountPojo.setAccountAmount(new BigDecimal(0.0));
//        accountPojo.setCreditBalance(new BigDecimal(0.0));
//        accountPojo.setDriverId(driverId);
//        accountPojo.setFrozenAmount(new BigDecimal(0.0));
//        accountPojo.setSettleAccount(new BigDecimal(0.0));
//        accountPojo.setWithdrawDeposit(new BigDecimal(0.0));
//        carBizDriverAccountMapper.insertSelective(accountPojo);
//        return driverId;
//    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param carBizDriverInfoDTO
     * @return
     */
//    @MasterSlaveConfigs(configs = {
//            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.MASTER)
//    })
//    public int updateDriverInfo(CarBizDriverInfoDTO carBizDriverInfoDTO) {
//        if (carBizDriverInfoDTO.getExpireDate() != null) {
//            if (carBizDriverInfoDTO.getExpireDate().equals("")) {
//                carBizDriverInfoDTO.setExpireDate(null);
//            }
//        }
//        if (carBizDriverInfoDTO.getIssueDate() != null) {
//            if (carBizDriverInfoDTO.getIssueDate().equals("")) {
//                carBizDriverInfoDTO.setIssueDate(null);
//            }
//        }
//        carBizDriverInfoExMapper.updateCarBizDriverInfoDTO(carBizDriverInfoDTO);
//        int id = carBizDriverInfoDTO.getDriverId();
//
//        //??????????????????????????????????????????
//        CarBizDriverInfoDetailDTO infoDetail = carBizDriverInfoDetailService.selectByDriverId(carBizDriverInfoDTO.getDriverId());
//        CarBizDriverInfoDetail carBizDriverInfoDetail = new CarBizDriverInfoDetail();
//        carBizDriverInfoDetail.setBankCardBank(carBizDriverInfoDTO.getBankCardBank());
//        carBizDriverInfoDetail.setBankCardNumber(carBizDriverInfoDTO.getBankCardNumber());
//        carBizDriverInfoDetail.setDriverId(carBizDriverInfoDTO.getDriverId());
//        if (infoDetail != null) {
//            carBizDriverInfoDetailService.updateByPrimaryKeySelective(carBizDriverInfoDetail);
//        } else {
//            carBizDriverInfoDetail.setExt1(2);//??????????????????  1?????? 2??????  ????????????????????????2
//            carBizDriverInfoDetailService.insertSelective(carBizDriverInfoDetail);
//        }
//        return id;
//    }

    /**
     * ?????????????????????????????????????????????????????????????????????
     *
     * @param carBizDriverInfoDTO
     * @return
     */
//    @MasterSlaveConfigs(configs = {
//            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.MASTER)
//    })
//    public int updateDriverByXiao(CarBizDriverInfoDTO carBizDriverInfoDTO) {
//        int rtn = 0;
//        try {
//            rtn = carBizDriverInfoExMapper.updateDriverByXiao(carBizDriverInfoDTO.getDriverId());
//            if (rtn > 0) {
//                // ?????????????????????
//                if (carBizDriverInfoDTO.getStatus().intValue() == 0) {
//                    //?????????????????????????????????
//                    carBizCarInfoExMapper.updateCarLicensePlates(carBizDriverInfoDTO.getLicensePlates(), 0);
//                }
//                //????????????mongo
//                driverMongoService.updateByDriverId(carBizDriverInfoDTO.getDriverId(), carBizDriverInfoDTO.getStatus());
//
//                try {
//                    carBizDriverUpdateService.insert(carBizDriverInfoDTO.getLicensePlates(), "", carBizDriverInfoDTO.getDriverId(), 1);
//                    carBizDriverUpdateService.insert(carBizDriverInfoDTO.getPhone(), "", carBizDriverInfoDTO.getDriverId(), 2);
//                } catch (Exception e) {
//                    logger.info("updateDriverByXiao error:" + e);
//                }
//            }
//        } catch (Exception e) {
//            logger.info("updateDriverByXiao error:" + e);
//        }
//        return rtn;
//    }

    /**
     * ??????MQ
     *
     * @param carBizDriverInfoDTO
     * @param method              tag: INSERT UPDATE DELETE
     */
    public void sendDriverToMq(CarBizDriverInfoDTO carBizDriverInfoDTO, String method) {
        //MQ????????????
        try {
            Map<String, Object> messageMap = new HashMap<String, Object>();
            messageMap.put("carNumber", carBizDriverInfoDTO.getLicensePlates());//?????????
            messageMap.put("city", carBizDriverInfoDTO.getCityName()); //????????????
            messageMap.put("cityId", carBizDriverInfoDTO.getServiceCity()); //??????ID
            messageMap.put("createBy", carBizDriverInfoDTO.getUpdateBy() == null ? "1" : carBizDriverInfoDTO.getUpdateBy()); //?????????
            messageMap.put("driverId", carBizDriverInfoDTO.getDriverId()); //??????ID
            messageMap.put("driverName", carBizDriverInfoDTO.getName()); //????????????
            messageMap.put("driverPhone", carBizDriverInfoDTO.getPhone() == null ? "" : carBizDriverInfoDTO.getPhone()); //???????????????
            messageMap.put("status", carBizDriverInfoDTO.getStatus()); //????????????
            messageMap.put("supplierFullName", carBizDriverInfoDTO.getSupplierName()); //?????????????????????
            messageMap.put("supplierId", carBizDriverInfoDTO.getSupplierId()); //???????????????
            messageMap.put("cooperationType", carBizDriverInfoDTO.getCooperationType()); //??????????????????
            messageMap.put("groupId", carBizDriverInfoDTO.getGroupId()); //??????????????????ID
            messageMap.put("create_date", carBizDriverInfoDTO.getCreateDate()); //??????????????????
            messageMap.put("carType", carBizDriverInfoDTO.getCarGroupName() == null ? "" : carBizDriverInfoDTO.getCarGroupName()); //??????????????????
            messageMap.put("teamId", carBizDriverInfoDTO.getTeamId() == null ? "" : carBizDriverInfoDTO.getTeamId()); //??????????????????ID
            messageMap.put("teamName", carBizDriverInfoDTO.getTeamName() == null ? "" : carBizDriverInfoDTO.getTeamName()); //????????????????????????
            messageMap.put("teamGroupId", carBizDriverInfoDTO.getTeamGroupId() == null ? "" : carBizDriverInfoDTO.getTeamName()); //??????????????????ID
            messageMap.put("teamGroupName", carBizDriverInfoDTO.getTeamGroupName() == null ? "" : carBizDriverInfoDTO.getTeamGroupName()); //????????????????????????
            logger.info("????????????id??????????????????driverId={}" , carBizDriverInfoDTO.getDriverId());
            CarBizDriverInfo driverInfo = carBizDriverInfoMapper.selectByPrimaryKey(carBizDriverInfoDTO.getDriverId());
            if (Objects.nonNull(driverInfo)) {
                logger.info("?????????????????????????????????????????????messageMap");
                messageMap.put("licensePlates" , driverInfo.getLicensePlates());
            }
            String messageStr = JSONObject.fromObject(messageMap).toString();
            logger.info("????????????driverId={}?????????????????????={}", carBizDriverInfoDTO.getDriverId(), messageStr);
            //TODO 20190619????????????????????????????????????MQ
            DriverWideRocketProducer.publishMessage(DriverWideRocketProducer.TOPIC, method, String.valueOf(carBizDriverInfoDTO.getDriverId()), messageMap);
            CommonRocketProducer.publishMessage("driver_info", method, String.valueOf(carBizDriverInfoDTO.getDriverId()), messageMap);
            String envName = EnvUtils.ENVIMENT;
            if (Objects.nonNull(envName) && Arrays.asList(new String[]{"online","prod"}).contains(envName)){
                CommonRocketProducerDouble.publishMessage("driver_info", method, String.valueOf(carBizDriverInfoDTO.getDriverId()), messageMap);
            }
        } catch (Exception e) {
            logger.error("send Driver mq error" ,e);
        }
    }

    /**
     * ???????????????????????????
     *
     * @param idCarNo
     * @return
     */
    public String getPassword(String idCarNo) {
        String parrword = "";
        try {
            //????????????
            if (idCarNo.length() == 18) {
                parrword = MD5Utils.getMD5DigestHex(idCarNo.substring(idCarNo.length() - 6, idCarNo.length()));
            } else if (idCarNo.length() == 10) {
                parrword = MD5Utils.getMD5DigestHex(idCarNo.substring(1, 7));
            } else {
                parrword = MD5Utils.getMD5DigestHex(idCarNo.substring(idCarNo.length() - 6, idCarNo.length()));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return parrword;
    }

    /**
     * ????????????????????????????????????
     * @param driverId ??????ID
     * @param phone ?????????
     * @param idCardNo ????????????
     * @param bankCardNumber ???????????????
     * @param bankCardBank ??????????????????
     * @return
     */
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE)
    })
    public AjaxResponse validateCarDriverInfo(Integer driverId, String phone, String idCardNo, String bankCardNumber, String bankCardBank) {
        //?????????????????????
        if (StringUtils.isEmpty(phone) || !ValidateUtils.validatePhone(phone)) {
            return AjaxResponse.fail(RestErrorCode.DRIVER_PHONE_NOT_LEGAL);
        }
        //??????????????????????????????X?????????????????????x
        if ("X".equals(idCardNo.substring(idCardNo.length() - 1, idCardNo.length()))) {
            idCardNo = idCardNo.toLowerCase();
        }
        if (StringUtils.isEmpty(idCardNo) || !ValidateUtils.validateIdCarNo(idCardNo)) {
            return AjaxResponse.fail(RestErrorCode.DRIVER_IDCARNO_NOT_LEGAL);
        }
        //???????????????16???18?????????????????????????????????????????????????????????
        if (((StringUtils.isNotEmpty(bankCardNumber) && StringUtils.isEmpty(bankCardBank))) || ((StringUtils.isEmpty(bankCardNumber) && StringUtils.isNotEmpty(bankCardBank)))) {
            return AjaxResponse.fail(RestErrorCode.DRIVER_BANK_CARD_NUMBER_NOT_COMPLETE);
        }
        if (StringUtils.isNotEmpty(bankCardNumber) && StringUtils.isNotEmpty(bankCardBank) && !ValidateUtils.isRegular(bankCardNumber, ValidateUtils.BANK_CARD_NUMBER)) {
            return AjaxResponse.fail(RestErrorCode.DRIVER_BANK_CARD_NUMBER_NOT_LEGAL);
        }
        //???????????????????????????
        Boolean had = this.checkPhone(phone, driverId);
        if (had) {
            return AjaxResponse.fail(RestErrorCode.DRIVER_PHONE_EXIST);
        }
        //??????????????????????????????
        had = this.checkIdCardNo(idCardNo, driverId);
        if (had) {
            return AjaxResponse.fail(RestErrorCode.DRIVER_IDCARNO_EXIST);
        }
        //??????????????????????????????
        if (StringUtils.isNotEmpty(bankCardNumber) && StringUtils.isNotEmpty(bankCardBank)){
            had = carBizDriverInfoDetailService.checkBankCardBank(bankCardNumber, driverId);
            if (had) {
                return AjaxResponse.fail(RestErrorCode.DRIVER_BANK_CARD_NUMBER_EXIST);
            }
        }
        return AjaxResponse.success(true);
    }

    public void driverUpdate(CarBizDriverInfoDTO carBizDriverInfo) {
        try {
            //?????? ????????????????????? ???????????? ?????? ?????????
            if (StringUtils.isNotEmpty(carBizDriverInfo.getOldLicensePlates()) && !carBizDriverInfo.getOldLicensePlates().equals(carBizDriverInfo.getLicensePlates())) {
                logger.info(LOGTAG + "****************??????????????? ????????????????????????");
                carBizCarInfoExMapper.updateCarLicensePlates(carBizDriverInfo.getOldLicensePlates(), 0);
                carBizDriverUpdateService.insert(carBizDriverInfo.getOldLicensePlates(), carBizDriverInfo.getLicensePlates(), carBizDriverInfo.getDriverId(), 1);
            }
            //?????? ????????????????????? ????????????  ??????????????????
            if (carBizDriverInfo.getOldPhone() != null && carBizDriverInfo.getOldPhone().length() >= 1 && !carBizDriverInfo.getOldPhone().equals(carBizDriverInfo.getPhone())) {
                logger.info("****************???????????????");
                carBizDriverUpdateService.insert(carBizDriverInfo.getOldPhone(), carBizDriverInfo.getPhone(), carBizDriverInfo.getDriverId(), 2);
            }
            //?????? ????????????????????? ???????????? ??????????????????
            if (carBizDriverInfo.getOldIdCardNo() != null && carBizDriverInfo.getOldIdCardNo().length() >= 1 && !carBizDriverInfo.getOldIdCardNo().equals(carBizDriverInfo.getIdCardNo())) {
                logger.info("****************???????????????");
                carBizDriverUpdateService.insert(carBizDriverInfo.getOldIdCardNo(), carBizDriverInfo.getIdCardNo(), carBizDriverInfo.getDriverId(), 3);
            }
            //?????? ????????????????????????????????? ????????????  ??????????????????
            if (carBizDriverInfo.getOldDriverLicenseNumber() != null && carBizDriverInfo.getOldDriverLicenseNumber().length() >= 1 && !carBizDriverInfo.getOldDriverLicenseNumber().equals(carBizDriverInfo.getDriverlicensenumber())) {
                logger.info("****************?????? ?????????????????????");
                carBizDriverUpdateService.insert(carBizDriverInfo.getOldDriverLicenseNumber(), carBizDriverInfo.getDriverlicensenumber(), carBizDriverInfo.getDriverId(), 4);
            }
            //?????? ????????????????????????????????????????????????????????? ????????????  ??????????????????
            if (carBizDriverInfo.getOldDriverLicenseIssuingNumber() != null && carBizDriverInfo.getOldDriverLicenseIssuingNumber().length() >= 1 && !carBizDriverInfo.getOldDriverLicenseIssuingNumber().equals(carBizDriverInfo.getDriverlicenseissuingnumber())) {
                logger.info("****************???????????????????????????????????????????????????");
                carBizDriverUpdateService.insert(carBizDriverInfo.getOldDriverLicenseIssuingNumber(), carBizDriverInfo.getDriverlicenseissuingnumber(), carBizDriverInfo.getDriverId(), 5);
            }
        } catch (Exception e) {
            logger.info("driverUpdateService error:" + e);
        }
    }

    /**
     * ????????????????????????????????????
     *
     * @param driverId
     * @param teamId
     * @param teamName
     * @param value
     */
//    public void processingData(Integer driverId, Integer teamId, String teamName, Integer value) {
//        CarBizDriverInfo carBizDriverInfo = carBizDriverInfoMapper.selectByPrimaryKey(driverId);
//        if (carBizDriverInfo == null) {
//            return;
//        }
//        CarBizDriverInfoDTO carBizDriverInfoDTO = BeanUtil.copyObject(carBizDriverInfo, CarBizDriverInfoDTO.class);
//        // ?????????????????????????????????????????????????????????
//        carBizDriverInfoDTO = this.getBaseStatis(carBizDriverInfoDTO);
//        //????????????
//        carBizDriverInfoDTO.setTeamId(teamId);
//        carBizDriverInfoDTO.setTeamName(teamName);
//        carBizDriverInfoDTO.setTeamGroupId(null);
//        carBizDriverInfoDTO.setTeamGroupName("");
//        sendDriverToMq(carBizDriverInfoDTO, "UPDATE");
//    }

    /**
     * ??????????????????????????????????????????????????????????????????
     *
     * @param carBizDriverInfo
     * @return
     */
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE),
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.SLAVE)
    })
    public CarBizDriverInfoDTO getBaseStatis(CarBizDriverInfoDTO carBizDriverInfo) {
        if (carBizDriverInfo == null) {
            return carBizDriverInfo;
        }

        if(carBizDriverInfo.getExt2() == null){
            carBizDriverInfo.setExt2(0);
        }
        if(carBizDriverInfo.getExt3() == null){
            carBizDriverInfo.setExt3(4);
        }

        // ???????????????ID???????????????????????????????????????
        CarBizSupplier carBizSupplier = carBizSupplierService.selectByPrimaryKey(carBizDriverInfo.getSupplierId());
        if (carBizSupplier != null) {
            carBizDriverInfo.setSupplierName(carBizSupplier.getSupplierFullName());
            carBizDriverInfo.setCooperationType(carBizSupplier.getCooperationType());
            CarBizCooperationType carBizCooperationType = carBizCooperationTypeService.selectByPrimaryKey(carBizSupplier.getCooperationType());
            if (carBizCooperationType != null) {
                carBizDriverInfo.setCooperationTypeName(carBizCooperationType.getCooperationName());
            }
        }
        // ?????????????????????????????????
        CarBizCarInfoDTO carInfo = carBizCarInfoService.selectModelByLicensePlates(carBizDriverInfo.getLicensePlates());
        if (carInfo != null) {
            carBizDriverInfo.setModelId(carInfo.getCarModelId());
            carBizDriverInfo.setModelName(carInfo.getCarModelName());
        }
        // ????????????ID??????????????????
        CarBizCity carBizCity = carBizCityService.selectByPrimaryKey(carBizDriverInfo.getServiceCity());
        if (carBizCity != null) {
            carBizDriverInfo.setCityName(carBizCity.getCityName());
        }
        // ??????????????????????????????????????????
        CarBizCarGroup carBizCarGroup = carBizCarGroupService.selectByPrimaryKey(carBizDriverInfo.getGroupId());
        if (carBizCarGroup != null) {
            carBizDriverInfo.setCarGroupName(carBizCarGroup.getGroupName());
        }
        if (carBizDriverInfo.getDriverId() != null) {
            // ????????????ID????????????????????????
            Map<String, Object> stringObjectMap = carDriverTeamExMapper.queryTeamNameAndGroupNameByDriverId(carBizDriverInfo.getDriverId());
            if(stringObjectMap!=null){
                if(stringObjectMap.containsKey("teamId") && stringObjectMap.get("teamId")!=null ){
                    carBizDriverInfo.setTeamId(Integer.parseInt(stringObjectMap.get("teamId").toString()));
                }
                if(stringObjectMap.containsKey("teamName") && stringObjectMap.get("teamName")!=null ){
                    carBizDriverInfo.setTeamName(stringObjectMap.get("teamName").toString());
                }
                if(stringObjectMap.containsKey("teamGroupId") && stringObjectMap.get("teamGroupId")!=null ){
                    carBizDriverInfo.setTeamGroupId(Integer.parseInt(stringObjectMap.get("teamGroupId").toString()));
                }
                if(stringObjectMap.containsKey("teamGroupName") && stringObjectMap.get("teamGroupName")!=null ){
                    carBizDriverInfo.setTeamGroupName(stringObjectMap.get("teamGroupName").toString());
                }
            }

            // ?????????????????????
            if(carBizDriverInfo.getCreateBy()!=null){
                CarAdmUser carAdmUser = carAdmUserMapper.selectByPrimaryKey(carBizDriverInfo.getCreateBy());
                if(carAdmUser!=null){
                    carBizDriverInfo.setCreateName(carAdmUser.getUserName());
                }
            }
            if(carBizDriverInfo.getUpdateBy()!=null){
                CarAdmUser carAdmUser = carAdmUserMapper.selectByPrimaryKey(carBizDriverInfo.getUpdateBy());
                if(carAdmUser!=null){
                    carBizDriverInfo.setUpdateName(carAdmUser.getUserName());
                }
            }

            try {
                //????????????????????????
                Map<String, Object> params = new HashMap<>();
                params.put("driverId", carBizDriverInfo.getDriverId());
                com.alibaba.fastjson.JSONObject resultJson = MpOkHttpUtil.okHttpGetBackJson(orderStatisticsUrl, params, 1, Constants.DRIVER_INFO_TAG);
                if (resultJson != null && Constants.SUCCESS_CODE == resultJson.getInteger(Constants.CODE)) {
                    com.alibaba.fastjson.JSONObject data = resultJson.getJSONObject(Constants.DATA);
                    if (data != null) {
                        String orderNum = data.getJSONObject(Constants.DRIVER).getString(Constants.FIRST_ORDER_NO);
                        if (StringUtils.isNotBlank(orderNum)) {
                            com.alibaba.fastjson.JSONObject result = orderService.getOrderInfoByParams(orderNum, "fact_end_date", Constants.DRIVER_INFO_TAG);
                            if (result != null && Constants.SUCCESS_CODE == result.getInteger(Constants.CODE)) {
                                JSONArray jsonArray = result.getJSONArray(Constants.DATA);
                                if (jsonArray != null && !jsonArray.isEmpty()) {
                                    String updateDate = jsonArray.getJSONObject(0).getString("factEndDate");
                                    if (StringUtils.isNotBlank(updateDate)) {
                                        carBizDriverInfo.setActiveDate(DateUtil.getTimeString(updateDate));
                                    }
                                }
                            }
                        }
                    }
                }
            }catch (Exception e){
                logger.error("?????????????????????????????? driverId : {}", carBizDriverInfo.getDriverId());
                logger.error("???????????? ???", e);
            }
        }
        return carBizDriverInfo;
    }

//    public Map<String, Object> batchInputDriverInfo(Integer cityId, Integer supplierId, Integer teamId,
//                                                    Integer teamGroupId, MultipartFile file,
//                                                    HttpServletRequest request,
//                                                    HttpServletResponse response) {
//
//        Map<String, Object> resultMap = Maps.newHashMap();
//
//        String resultError1 = "-1";//????????????
//        String resultErrorMag1 = "????????????????????????!";
//        List<CarImportExceptionEntity> listException = Lists.newArrayList(); // ??????????????????
//        int count = 0;
//
//        String fileName = file.getOriginalFilename();
//        String suffixName = fileName.substring(fileName.lastIndexOf("."));
//        logger.info("?????????????????????:{},?????????????????????:{}", fileName, suffixName);
//        InputStream is = null;
//        try {
//            is = file.getInputStream();
//
//            Workbook workbook = null;
//            String fileType = fileName.split("\\.")[1];
//            if (fileType.equals("xls")) {
//                workbook = new HSSFWorkbook(is);
//            } else if (fileType.equals("xlsx")) {
//                workbook = new XSSFWorkbook(is);
//            }
//            Sheet sheet = workbook.getSheetAt(0);
//            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
//            // ????????????????????????
//            Row row1 = sheet.getRow(0);
//            if (row1 == null) {
//                resultMap.put("result", resultError1);
//                resultMap.put("msg", resultErrorMag1);
//                return resultMap;
//            }
//            for (int colIx = 0; colIx < 50; colIx++) {
//                Cell cell = row1.getCell(colIx); // ???????????????
//                CellValue cellValue = evaluator.evaluate(cell); // ???????????????
//                if (cell == null || cellValue == null) {
//                    resultMap.put("result", resultError1);
//                    resultMap.put("msg", resultErrorMag1);
//                    return resultMap;
//                } else {
//                    switch ((colIx + 1)) {
//                        case 1:
//                            if (!cellValue.getStringValue().contains("?????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 2:
//                            if (!cellValue.getStringValue().contains("????????????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 3:
//                            if (!cellValue.getStringValue().contains("?????????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 4:
//                            if (!cellValue.getStringValue().contains("??????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 5:
//                            if (!cellValue.getStringValue().contains("??????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 6:
//                            if (!cellValue.getStringValue().contains("?????????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//
//                        case 7:
//                            if (!cellValue.getStringValue().contains("???????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 8:
//                            if (!cellValue.getStringValue().contains("????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 9:
//                            if (!cellValue.getStringValue().contains("??????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 10:
//                            if (!cellValue.getStringValue().contains("??????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 11:
//                            if (!cellValue.getStringValue().contains("??????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 12:
//                            if (!cellValue.getStringValue().contains("????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 13:
//                            if (!cellValue.getStringValue().contains("????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 14:
//                            if (!cellValue.getStringValue().contains("??????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 15:
//                            if (!cellValue.getStringValue().contains("??????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 16:
//                            if (!cellValue.getStringValue().contains("??????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 17:
//                            if (!cellValue.getStringValue().contains("????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 18:
//                            if (!cellValue.getStringValue().contains("??????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 19:
//                            if (!cellValue.getStringValue().contains("???????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 20:
//                            if (!cellValue.getStringValue().contains("?????????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 21:
//                            if (!cellValue.getStringValue().contains("?????????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 22:
//                            if (!cellValue.getStringValue().contains("???????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 23:
//                            if (!cellValue.getStringValue().contains("????????????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 24:
//                            if (!cellValue.getStringValue().contains("???????????????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 25:
//                            if (!cellValue.getStringValue().contains("?????????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 26:
//                            if (!cellValue.getStringValue().contains("???????????????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 27:
//                            if (!cellValue.getStringValue().contains("?????????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 28:
//                            if (!cellValue.getStringValue().contains("???????????????????????????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 29:
//                            if (!cellValue.getStringValue().contains("???????????????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 30:
//                            if (!cellValue.getStringValue().contains("?????????????????????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 31:
//                            if (!cellValue.getStringValue().contains("?????????????????????????????????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 32:
//                            if (!cellValue.getStringValue().contains("????????????????????????????????????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 33:
//                            if (!cellValue.getStringValue().contains("???????????????????????????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 34:
//                            if (!cellValue.getStringValue().contains("????????????????????????????????????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 35:
//                            if (!cellValue.getStringValue().contains("?????????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 36:
//                            if (!cellValue.getStringValue().contains("???????????????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 37:
//                            if (!cellValue.getStringValue().contains("???????????????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 38:
//                            if (!cellValue.getStringValue().contains("???????????????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 39:
//                            if (!cellValue.getStringValue().contains("????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 40:
//                            if (!cellValue.getStringValue().contains("?????????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 41:
//                            if (!cellValue.getStringValue().contains("??????????????????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 42:
//                            if (!cellValue.getStringValue().contains("??????????????????????????????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 43:
//                            if (!cellValue.getStringValue().contains("??????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 44:
//                            if (!cellValue.getStringValue().contains("?????????????????????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 45:
//                            if (!cellValue.getStringValue().contains("?????????????????????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 46:
//                            if (!cellValue.getStringValue().contains("?????????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 47:
//                            if (!cellValue.getStringValue().contains("???????????????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 48:
//                            if (!cellValue.getStringValue().contains("?????????????????????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 49:
//                            if (!cellValue.getStringValue().contains("???????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                        case 50:
//                            if (!cellValue.getStringValue().contains("??????????????????")) {
//                                resultMap.put("result", resultError1);
//                                resultMap.put("msg", resultErrorMag1);
//                                return resultMap;
//                            }
//                            break;
//                    }
//                }
//            }
//
//            int minRowIx = 1;// ????????????????????????????????????????????????
//            int maxRowIx = sheet.getLastRowNum(); // ???????????????????????????
//
//            for (int rowIx = minRowIx; rowIx <= maxRowIx; rowIx++) {
//                count ++;
//                Row row = sheet.getRow(rowIx); // ???????????????
//                if (row == null) {
//                    continue;
//                }
//                CarBizDriverInfoDTO carBizDriverInfoDTO = new CarBizDriverInfoDTO();
//
//                // ???????????????ID???????????????????????????????????????
//                CarBizSupplier carBizSupplier = carBizSupplierService.selectByPrimaryKey(supplierId);
//                if (carBizSupplier != null) {
//                    carBizDriverInfoDTO.setSupplierName(carBizSupplier.getSupplierFullName());
//                    carBizDriverInfoDTO.setCooperationType(carBizSupplier.getCooperationType());
//                }
//
//                boolean isTrue = true;// ???????????????????????????
//                String bankCardNumber = "";
//                StringBuffer rePhone = new StringBuffer();//?????????
//                StringBuffer reLicensePlates = new StringBuffer(); //?????????
//                StringBuffer reIdCarNo = new StringBuffer(); //????????????
//
//                // ????????????????????????50???
//                for (int colIx = 0; colIx < 50; colIx++) {
//                    Cell cell = row.getCell(colIx); // ???????????????
//                    CellValue cellValue = evaluator.evaluate(cell); // ???????????????
//                    switch ((colIx + 1)) {
//                        // ?????????
//                        case 1:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                // ??????????????????????????????????????????
//                                String licensePlates = Common.replaceBlank(cellValue.getStringValue());
//                                if (reLicensePlates.indexOf(licensePlates) > 0) {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???" + (rowIx + 1) + "???????????????"
//                                            + (colIx + 1) + "??? ???????????????:" + licensePlates + "??????????????????");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                    colIx = 100;// ??????????????????
//                                } else {
//                                    reLicensePlates.append(licensePlates);
//                                    Integer carCount = carBizCarInfoExMapper.checkLicensePlates(licensePlates);
//                                    if (carCount == null || carCount == 0) {
//                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                        returnVO.setReson( "???" + (rowIx + 1) + "???????????????"
//                                                + (colIx + 1) + "??? ????????????????????????");
//                                        listException.add(returnVO);
//                                        isTrue = false;
//                                    } else {
//                                        Integer driverCount = carBizDriverInfoExMapper.checkLicensePlates(licensePlates);
//                                        if (driverCount == null || driverCount > 0) {
//                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                            returnVO.setReson( "???" + (rowIx + 1) + "???????????????"
//                                                    + (colIx + 1) + "??? ???????????????:" + licensePlates + "????????????");
//                                            listException.add(returnVO);
//                                            isTrue = false;
//                                        }
//                                    }
//                                    Integer cityCount = carBizCarInfoExMapper.validateCityAndSupplier(cityId, supplierId, licensePlates);
//                                    if (cityCount == null || cityCount == 0) {
//                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                        returnVO.setReson( "???" + (rowIx + 1) + "???????????????"
//                                                + (colIx + 1) + "??? ???????????????:" + licensePlates + "??????????????????????????????");
//                                        listException.add(returnVO);
//                                        isTrue = false;
//                                    } else {
//                                        carBizDriverInfoDTO.setLicensePlates(licensePlates);
//                                    }
//                                }
//                            }
//                            break;
//                        // ????????????????????????
//                        case 2:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ???????????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String name = Common.replaceBlank(cellValue.getStringValue());
//                                carBizDriverInfoDTO.setName(name);
//                            }
//                            break;
//                        // ?????????????????????
//                        case 3:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ????????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String idCardNo = Common.replaceBlankNoUpper(cellValue.getStringValue());
//                                if ("X".equals(idCardNo.substring(idCardNo.length() - 1, idCardNo.length()))) {
//                                    idCardNo = idCardNo.toLowerCase();
//                                }
//                                if (reIdCarNo.indexOf(idCardNo) > 0) {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???" + (rowIx + 1) + "???????????????"
//                                            + (colIx + 1) + "??? ???????????????????????????:" + idCardNo + "?????????????????????");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                    colIx = 100;// ??????????????????
//                                } else {
//                                    reIdCarNo.append(idCardNo);
//                                    if (StringUtils.isEmpty(idCardNo) || !ValidateUtils.validateIdCarNo(idCardNo)) {
//                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                        returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                                + "??? ????????????????????????????????????");
//                                        listException.add(returnVO);
//                                        isTrue = false;
//                                    } else if (this.checkIdCardNo(idCardNo, null)) {
//                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                        returnVO.setReson( "???" + (rowIx + 1) + "???????????????"
//                                                + (colIx + 1) + "??? ???????????????????????????????????????"
//                                                + idCardNo
//                                                + "????????????");
//                                        listException.add(returnVO);
//                                        isTrue = false;
//                                    } else {
//                                        carBizDriverInfoDTO.setIdCardNo(idCardNo);
//                                    }
//                                }
//                            }
//                            break;
//                        // ???????????????
//                        case 4:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ??????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String phone = Common.replaceBlank(cellValue.getStringValue());
//                                if (rePhone.indexOf(phone) > 0) {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???" + (rowIx + 1) + "???????????????"
//                                            + (colIx + 1) + "??? ?????????????????????:" + phone + "??????????????????");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                    colIx = 100;// ??????????????????
//                                } else {
//                                    rePhone.append(phone);
//                                    if (StringUtils.isEmpty(phone) || !ValidateUtils.validatePhone(phone)) {
//                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                        returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                                + "??? ??????????????????????????????");
//                                        listException.add(returnVO);
//                                        isTrue = false;
//                                    } else if (this.checkPhone(phone, null)) {
//                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                        returnVO.setReson( "???" + (rowIx + 1) + "???????????????"
//                                                + (colIx + 1) + "??? ???????????????????????????"
//                                                + phone
//                                                + "????????????");
//                                        listException.add(returnVO);
//                                        colIx = 100;// ??????????????????
//                                        isTrue = false;
//                                    } else {
//                                        carBizDriverInfoDTO.setPhone(phone);
//                                    }
//                                }
//                            }
//                            break;
//                        // ??????????????????
//                        case 5:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ?????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String phoneType = Common.replaceBlank(cellValue.getStringValue());
//                                carBizDriverInfoDTO.setPhonetype(phoneType);
//                            }
//                            break;
//                        // ?????????????????????
//                        case 6:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ????????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String phoneCorp = Common.replaceBlank(cellValue.getStringValue());
//                                if ("????????????".equals(phoneCorp) ||
//                                        "????????????".equals(phoneCorp) ||
//                                        "????????????".equals(phoneCorp) ||
//                                        "??????".equals(phoneCorp)) {
//                                    carBizDriverInfoDTO.setPhonecorp(phoneCorp);
//                                } else {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???" + (rowIx + 1)
//                                            + "???????????????" + (colIx + 1)
//                                            + "??? ??????????????????????????????????????????????????????????????????????????? ?????? ?????????????????????");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                }
//                            }
//                            break;
//                        // ??????
//                        case 7:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ?????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String gender = Common.replaceBlank(cellValue.getStringValue());
//                                if ("???".equals(gender)) {
//                                    carBizDriverInfoDTO.setGender(1);
//                                } else if ("???".equals(gender)) {
//                                    carBizDriverInfoDTO.setGender(0);
//                                } else {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???" + (rowIx + 1)
//                                            + "???????????????" + (colIx + 1)
//                                            + "??? ???????????????????????????????????? ?????? ???");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                }
//                            }
//                            break;
//                        // ????????????
//                        case 8:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ???????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String d = cellValue.getStringValue()
//                                        .replace("???", "-")
//                                        .replace("???", "-")
//                                        .replace("???", "-")
//                                        .replace(".", "-").trim();
//                                if (StringUtils.isNotEmpty(d) && d.length() >= 1) {
//                                    if (d.substring(d.length() - 1).equals("-")) {
//                                        d = d.substring(0, d.length() - 1);
//                                    }
//                                }
//                                if (ValidateUtils.isValidDate(d)) {
//                                    d = DATE_FORMAT.format(DATE_FORMAT.parse(d));
//                                    carBizDriverInfoDTO.setBirthDay(d);
//                                } else {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???"
//                                            + (rowIx + 1)
//                                            + "???????????????"
//                                            + (colIx + 1)
//                                            + "??? ???????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                }
//                            }
//                            break;
//                        // ??????
//                        case 9:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ?????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String age = Common.replaceBlank(cellValue.getStringValue());
//                                if (ValidateUtils.isRegular(age, ValidateUtils.NUMBER_PATTERN)) {
//                                    carBizDriverInfoDTO.setAge(Integer.valueOf(age));
//                                } else {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???" + (rowIx + 1) + "???????????????"
//                                            + (colIx + 1) + "??? ???????????????????????????");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                }
//                            }
//                            break;
//                        // ??????????????????
//                        case 10:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ?????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String superintendNo = Common.replaceBlank(cellValue.getStringValue());
//                                carBizDriverInfoDTO.setSuperintendNo(superintendNo);
//                            }
//                            break;
//                        // ??????????????????
//                        case 11:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ?????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String superintendUrl = Common.replaceBlank(cellValue.getStringValue());
//                                carBizDriverInfoDTO.setSuperintendUrl(superintendUrl);
//                            }
//                            break;
//                        // ????????????
//                        case 12:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ???????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String groupName = Common.replaceBlank(cellValue.getStringValue());
//                                CarBizCarGroup carBizCarGroup = carBizCarGroupService.queryGroupByGroupName(groupName);
//                                if (carBizCarGroup == null) {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                            + "??? ????????????????????????" + groupName);
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                } else {
//                                    carBizDriverInfoDTO.setGroupId(carBizCarGroup.getGroupId());
//                                }
//                            }
//                            break;
//                        // ????????????
//                        case 13:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ???????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String driverType = "A1???A2???A3???B1???B2???C1???C2???N???P";
//                                String drivingLicenseType = Common.replaceBlank(cellValue.getStringValue());
//                                String yuandrivingTypeString = "C1";
//                                if (driverType.indexOf(drivingLicenseType) > 0) {
//                                    yuandrivingTypeString = drivingLicenseType;
//                                }
//                                carBizDriverInfoDTO.setDrivingLicenseType(yuandrivingTypeString);
//                            }
//                            break;
//                        // ??????????????????
//                        case 14:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ?????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String d = cellValue.getStringValue()
//                                        .replace("???", "-")
//                                        .replace("???", "-")
//                                        .replace("???", "-")
//                                        .replace(".", "-").trim();
//                                if (StringUtils.isNotEmpty(d) && d.length() >= 1) {
//                                    if (d.substring(d.length() - 1).equals("-")) {
//                                        d = d.substring(0, d.length() - 1);
//                                    }
//                                }
//                                if (ValidateUtils.isValidDate(d)) {
//                                    Date issueDate = DATE_FORMAT.parse(d);
//                                    carBizDriverInfoDTO.setIssueDate(issueDate);
//                                } else {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???"
//                                            + (rowIx + 1)
//                                            + "???????????????"
//                                            + (colIx + 1)
//                                            + "??? ?????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                }
//                            }
//                            break;
//                        // ??????
//                        case 15:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ?????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String drivingYears = Common.replaceBlank(cellValue.getStringValue());
//                                if (ValidateUtils.isRegular(drivingYears, ValidateUtils.NUMBER_PATTERN)){
//                                    carBizDriverInfoDTO.setDrivingYears(Integer.valueOf(drivingYears));
//                                } else {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???" + (rowIx + 1) + "???????????????"
//                                            + (colIx + 1) + "??? ???????????????????????????");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                }
//                            }
//                            break;
//                        // ??????????????????
//                        case 16:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ?????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String d = cellValue.getStringValue()
//                                        .replace("???", "-")
//                                        .replace("???", "-")
//                                        .replace("???", "-")
//                                        .replace(".", "-").trim();
//                                if (StringUtils.isNotEmpty(d) && d.length() >= 1) {
//                                    if (d.substring(d.length() - 1).equals("-")) {
//                                        d = d.substring(0, d.length() - 1);
//                                    }
//                                }
//                                if (ValidateUtils.isValidDate(d)) {
//                                    String datetime = DATE_FORMAT.format(new Date());
//                                    if (DATE_FORMAT.parse(d).getTime() < DATE_FORMAT.parse(datetime).getTime()) {
//                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                        returnVO.setReson( "???" + (rowIx + 1) + "???????????????"
//                                                + (colIx + 1) + "??? ????????????????????????????????????????????????");
//                                        listException.add(returnVO);
//                                        isTrue = false;
//                                    } else {
//                                        Date expireDate = DATE_FORMAT.parse(d);
//                                        carBizDriverInfoDTO.setExpireDate(expireDate);
//                                    }
//                                } else {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???"
//                                            + (rowIx + 1)
//                                            + "???????????????"
//                                            + (colIx + 1)
//                                            + "??? ?????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                }
//                            }
//                            break;
//                        // ????????????
//                        case 17:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ???????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String archivesNo = Common.replaceBlank(cellValue.getStringValue());
//                                carBizDriverInfoDTO.setArchivesNo(archivesNo);
//                            }
//                            break;
//                        // ??????
//                        case 18:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ?????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String nationAlity = Common.replaceBlank(cellValue.getStringValue());
//                                carBizDriverInfoDTO.setNationality(nationAlity);
//                            }
//                            break;
//                        // ???????????????
//                        case 19:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ??????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String nation = Common.replaceBlank(cellValue.getStringValue());
//                                carBizDriverInfoDTO.setNation(nation);
//                            }
//                            break;
//                        // ?????????????????????
//                        case 20:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ????????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String marriage = Common.replaceBlank(cellValue.getStringValue());
//                                if ("??????".equals(marriage) || "??????".equals(marriage) || "??????".equals(marriage)) {
//                                    carBizDriverInfoDTO.setMarriage(marriage);
//                                } else {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                            + "??? ???????????????????????????????????????????????????????????????");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                }
//                            }
//                            break;
//                        // ?????????????????????
//                        case 21:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ????????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String foreignLanguage = Common.replaceBlank(cellValue.getStringValue());
//                                if ("??????".equals(foreignLanguage)) {
//                                    carBizDriverInfoDTO.setForeignlanguage("1");
//                                } else if ("??????".equals(foreignLanguage)) {
//                                    carBizDriverInfoDTO.setForeignlanguage("2");
//                                } else if ("??????".equals(foreignLanguage)) {
//                                    carBizDriverInfoDTO.setForeignlanguage("3");
//                                } else if ("??????".equals(foreignLanguage)) {
//                                    carBizDriverInfoDTO.setForeignlanguage("4");
//                                } else if ("???".equals(foreignLanguage)) {
//                                    carBizDriverInfoDTO.setForeignlanguage("0");
//                                } else {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                            + "??? ?????????????????????????????????????????????????????????????????????????????????");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                }
//                            }
//                            break;
//                        // ???????????????
//                        case 22:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ??????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String education = Common.replaceBlank(cellValue.getStringValue());
//                                if ("?????????".equals(education) || "??????".equals(education) || "??????".equals(education) ||
//                                        "??????".equals(education) || "??????".equals(education) || "??????".equals(education) ||
//                                        "??????".equals(education) || "??????".equals(education)) {
//                                    carBizDriverInfoDTO.setEducation(education);
//                                } else {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                            + "??? ????????????????????????????????????????????????????????????????????????????????? ????????? ???????????????");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                }
//                            }
//                            break;
//                        // ????????????????????????
//                        case 23:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ???????????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String houseHoldRegisterPermanent = Common.replaceBlank(cellValue.getStringValue());
//                                carBizDriverInfoDTO.setHouseHoldRegisterPermanent(houseHoldRegisterPermanent);
//                            }
//                            break;
//                        // ???????????????????????????
//                        case 24:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ??????????????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String houseHoldRegister = Common.replaceBlank(cellValue.getStringValue());
//                                carBizDriverInfoDTO.setHouseholdregister(houseHoldRegister);
//                            }
//                            break;
//                        // ?????????????????????
//                        case 25:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ????????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String currentAddress = Common.replaceBlank(cellValue.getStringValue());
//                                carBizDriverInfoDTO.setCurrentAddress(currentAddress);
//                            }
//                            break;
//                        // ???????????????????????????
//                        case 26:
//                            if (cellValue != null && !StringUtils.isEmpty(cellValue.getStringValue())) {
//                                String photosrct = Common.replaceBlank(cellValue.getStringValue());
//                                if (photosrct.contains("http://pupload.01zhuanche.com/")) {
//                                    carBizDriverInfoDTO.setPhotosrct(photosrct);
//                                }
//                            }
//                            break;
//                        // ?????????????????????
//                        case 27:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ????????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String driverLicenseNumber = Common.replaceBlank(cellValue.getStringValue());
//                                carBizDriverInfoDTO.setDriverlicensenumber(driverLicenseNumber);
//                            }
//                            break;
//                        // ???????????????????????????????????????
//                        case 28:
//                            if (cellValue != null && !StringUtils.isEmpty(cellValue.getStringValue())) {
//                                String drivingLicenseImg = Common.replaceBlank(cellValue.getStringValue());
//                                if (drivingLicenseImg.contains("http://pupload.01zhuanche.com/")) {
//                                    carBizDriverInfoDTO.setDrivinglicenseimg(drivingLicenseImg);
//                                }
//                            }
//                            break;
//                        //???????????????????????????
//                        case 29:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ??????????????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String d = cellValue.getStringValue()
//                                        .replace("???", "-")
//                                        .replace("???", "-")
//                                        .replace("???", "-")
//                                        .replace(".", "-").trim();
//                                if (StringUtils.isNotEmpty(d) && d.length() >= 1) {
//                                    if (d.substring(d.length() - 1).equals("-")) {
//                                        d = d.substring(0, d.length() - 1);
//                                    }
//                                }
//                                if (ValidateUtils.isValidDate(d)) {
//                                    String datetime = DATE_FORMAT.format(new Date());
//                                    if (DATE_FORMAT.parse(d).getTime() > DATE_FORMAT.parse(datetime).getTime()) {
//                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                        returnVO.setReson( "???" + (rowIx + 1) + "???????????????"
//                                                + (colIx + 1) + "??? ?????????????????????????????????????????????????????????");
//                                        listException.add(returnVO);
//                                        isTrue = false;
//                                    } else {
//                                        d = DATE_FORMAT.format(DATE_FORMAT.parse(d));
//                                        carBizDriverInfoDTO.setFirstdrivinglicensedate(d);
//                                    }
//                                } else {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???"
//                                            + (rowIx + 1)
//                                            + "???????????????"
//                                            + (colIx + 1)
//                                            + "??? ??????????????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                }
//                            }
//                            break;
//                        //?????????????????????????????????
//                        case 30:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ????????????????????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String isXyDriver = Common.replaceBlank(cellValue.getStringValue());
//                                if ("???".equals(isXyDriver)) {
//                                    carBizDriverInfoDTO.setIsxydriver(1);
//                                } else if ("???".equals(isXyDriver)) {
//                                    carBizDriverInfoDTO.setIsxydriver(0);
//                                } else {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???" + (rowIx + 1)
//                                            + "???????????????" + (colIx + 1)
//                                            + "??? ??????????????????????????????????????????????????????????????? ?????? ???");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                }
//                            }
//                            break;
//                        //?????????????????????????????????????????????
//                        case 31:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ????????????????????????????????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String driverLicenseIssuingNumber = Common.replaceBlank(cellValue.getStringValue());
//                                carBizDriverInfoDTO.setDriverlicenseissuingnumber(driverLicenseIssuingNumber);
//                            }
//                            break;
//                        //????????????????????????????????????????????????
//                        case 32:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ???????????????????????????????????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String d = cellValue.getStringValue()
//                                        .replace("???", "-")
//                                        .replace("???", "-")
//                                        .replace("???", "-")
//                                        .replace(".", "-").trim();
//                                if (StringUtils.isNotEmpty(d) && d.length() >= 1) {
//                                    if (d.substring(d.length() - 1).equals("-")) {
//                                        d = d.substring(0, d.length() - 1);
//                                    }
//                                }
//                                if (ValidateUtils.isValidDate(d)) {
//                                    String datetime = DATE_FORMAT.format(new Date());
//                                    if (DATE_FORMAT.parse(d).getTime() > DATE_FORMAT.parse(datetime).getTime()) {
//                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                        returnVO.setReson( "???" + (rowIx + 1) + "???????????????"
//                                                + (colIx + 1) + "??? ??????????????????????????????????????????????????????????????????????????????");
//                                        listException.add(returnVO);
//                                        isTrue = false;
//                                    } else {
//                                        d = DATE_FORMAT.format(DATE_FORMAT.parse(d));
//                                        carBizDriverInfoDTO.setFirstmeshworkdrivinglicensedate(d);
//                                    }
//                                } else {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???"
//                                            + (rowIx + 1)
//                                            + "???????????????"
//                                            + (colIx + 1)
//                                            + "??? ???????????????????????????????????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                }
//                            }
//                            break;
//                        //???????????????????????????????????????
//                        case 33:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ??????????????????????????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String xyDriverNumber = Common.replaceBlank(cellValue.getStringValue());
//                                carBizDriverInfoDTO.setXyDriverNumber(xyDriverNumber);
//                            }
//                            break;
//                        //????????????????????????????????????????????????
//                        case 34:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ???????????????????????????????????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String driverLicenseIssuingCorp = Common.replaceBlank(cellValue.getStringValue());
//                                carBizDriverInfoDTO.setDriverlicenseissuingcorp(driverLicenseIssuingCorp);
//                            }
//                            break;
//                        //?????????????????????
//                        case 35:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ????????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String d = cellValue.getStringValue()
//                                        .replace("???", "-")
//                                        .replace("???", "-")
//                                        .replace("???", "-")
//                                        .replace(".", "-").trim();
//                                if (StringUtils.isNotEmpty(d) && d.length() >= 1) {
//                                    if (d.substring(d.length() - 1).equals("-")) {
//                                        d = d.substring(0, d.length() - 1);
//                                    }
//                                }
//                                if (ValidateUtils.isValidDate(d)) {
//                                    d = DATE_FORMAT.format(DATE_FORMAT.parse(d));
//                                    carBizDriverInfoDTO.setDriverLicenseIssuingGrantDate(d);
//                                } else {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???"
//                                            + (rowIx + 1)
//                                            + "???????????????"
//                                            + (colIx + 1)
//                                            + "??? ????????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                }
//                            }
//                            break;
//                        //???????????????????????????
//                        case 36:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ??????????????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String d = cellValue.getStringValue()
//                                        .replace("???", "-")
//                                        .replace("???", "-")
//                                        .replace("???", "-")
//                                        .replace(".", "-").trim();
//                                if (StringUtils.isNotEmpty(d) && d.length() >= 1) {
//                                    if (d.substring(d.length() - 1).equals("-")) {
//                                        d = d.substring(0, d.length() - 1);
//                                    }
//                                }
//                                if (ValidateUtils.isValidDate(d)) {
//                                    d = DATE_FORMAT.format(DATE_FORMAT.parse(d));
//                                    carBizDriverInfoDTO.setDriverLicenseIssuingFirstDate(d);
//                                } else {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???"
//                                            + (rowIx + 1)
//                                            + "???????????????"
//                                            + (colIx + 1)
//                                            + "??? ??????????????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                }
//                            }
//                            break;
//                        //???????????????????????????
//                        case 37:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ??????????????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String d = cellValue.getStringValue()
//                                        .replace("???", "-")
//                                        .replace("???", "-")
//                                        .replace("???", "-")
//                                        .replace(".", "-").trim();
//                                if (StringUtils.isNotEmpty(d) && d.length() >= 1) {
//                                    if (d.substring(d.length() - 1).equals("-")) {
//                                        d = d.substring(0, d.length() - 1);
//                                    }
//                                }
//                                if (ValidateUtils.isValidDate(d)) {
//                                    d = DATE_FORMAT.format(DATE_FORMAT.parse(d));
//                                    carBizDriverInfoDTO.setDriverlicenseissuingdatestart(d);
//                                } else {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???"
//                                            + (rowIx + 1)
//                                            + "???????????????"
//                                            + (colIx + 1)
//                                            + "??? ??????????????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                }
//                            }
//                            break;
//                        //???????????????????????????
//                        case 38:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ??????????????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String d = cellValue.getStringValue()
//                                        .replace("???", "-")
//                                        .replace("???", "-")
//                                        .replace("???", "-")
//                                        .replace(".", "-").trim();
//                                if (StringUtils.isNotEmpty(d) && d.length() >= 1) {
//                                    if (d.substring(d.length() - 1).equals("-")) {
//                                        d = d.substring(0, d.length() - 1);
//                                    }
//                                }
//                                if (ValidateUtils.isValidDate(d)) {
//                                    d = DATE_FORMAT.format(DATE_FORMAT.parse(d));
//                                    carBizDriverInfoDTO.setDriverlicenseissuingdateend(d);
//                                } else {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???"
//                                            + (rowIx + 1)
//                                            + "???????????????"
//                                            + (colIx + 1)
//                                            + "??? ??????????????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                }
//                            }
//                            break;
//                        //????????????
//                        case 39:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ???????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String d = cellValue.getStringValue()
//                                        .replace("???", "-")
//                                        .replace("???", "-")
//                                        .replace("???", "-")
//                                        .replace(".", "-").trim();
//                                if (StringUtils.isNotEmpty(d) && d.length() >= 1) {
//                                    if (d.substring(d.length() - 1).equals("-")) {
//                                        d = d.substring(0, d.length() - 1);
//                                    }
//                                }
//                                if (ValidateUtils.isValidDate(d)) {
//                                    String datetime = DATE_FORMAT.format(new Date());
//                                    if (DATE_FORMAT.parse(d).getTime() > DATE_FORMAT.parse(datetime).getTime()) {
//                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                        returnVO.setReson( "???" + (rowIx + 1) + "???????????????"
//                                                + (colIx + 1) + "??? ??????????????????????????????????????????");
//                                        listException.add(returnVO);
//                                        isTrue = false;
//                                    } else {
//                                        d = DATE_FORMAT.format(DATE_FORMAT.parse(d));
//                                        carBizDriverInfoDTO.setDriverLicenseIssuingRegisterDate(d);
//                                    }
//                                } else {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???"
//                                            + (rowIx + 1)
//                                            + "???????????????"
//                                            + (colIx + 1)
//                                            + "??? ???????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                }
//                            }
//                            break;
//                        //?????????????????????
//                        case 40:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ????????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String partTimeJobDri = Common.replaceBlank(cellValue.getStringValue());
//                                if ("???".equals(partTimeJobDri) ||
//                                        "???".equals(partTimeJobDri)) {
//                                    carBizDriverInfoDTO.setParttimejobdri(partTimeJobDri);
//                                } else {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???" + (rowIx + 1)
//                                            + "???????????????" + (colIx + 1)
//                                            + "??? ??????????????????????????????????????????????????? ?????? ???");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                }
//                            }
//                            break;
//                        //??????????????????????????????
//                        case 41:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ?????????????????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String isDriverBlack = Common.replaceBlank(cellValue.getStringValue());
//                                if ("???".equals(isDriverBlack) || "???".equals(isDriverBlack)) {
////                                    carBizDriverInfoDTO.setIsDriverBlack(isDriverBlack);
//                                } else {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???" + (rowIx + 1)
//                                            + "???????????????" + (colIx + 1)
//                                            + "??? ???????????????????????????????????????????????????????????? ?????? ???");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                }
//                            }
//                            break;
//                        //??????????????????????????????????????????
//                        case 42:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ?????????????????????????????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String corpType = Common.replaceBlank(cellValue.getStringValue());
//                                carBizDriverInfoDTO.setCorptype(corpType);
//                            }
//                            break;
//                        //??????????????????
//                        case 43:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ?????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String d = cellValue.getStringValue()
//                                        .replace("???", "-")
//                                        .replace("???", "-")
//                                        .replace("???", "-")
//                                        .replace(".", "-").trim();
//                                if (StringUtils.isNotEmpty(d) && d.length() >= 1) {
//                                    if (d.substring(d.length() - 1).equals("-")) {
//                                        d = d.substring(0, d.length() - 1);
//                                    }
//                                }
//                                if (ValidateUtils.isValidDate(d)) {
//                                    String datetime = DATE_FORMAT.format(new Date());
//                                    if (DATE_FORMAT.parse(d).getTime() < DATE_FORMAT.parse(datetime).getTime()) {
//                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                        returnVO.setReson( "???" + (rowIx + 1) + "???????????????"
//                                                + (colIx + 1) + "??? ????????????????????????????????????????????????");
//                                        listException.add(returnVO);
//                                        isTrue = false;
//                                    } else {
//                                        d = DATE_FORMAT.format(DATE_FORMAT.parse(d));
//                                        carBizDriverInfoDTO.setContractdate(d);
//                                    }
//                                } else {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???"
//                                            + (rowIx + 1)
//                                            + "???????????????"
//                                            + (colIx + 1)
//                                            + "??? ?????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                }
//                            }
//                            break;
//                        //?????????????????????????????????
//                        case 44:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ????????????????????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String d = cellValue.getStringValue()
//                                        .replace("???", "-")
//                                        .replace("???", "-")
//                                        .replace("???", "-")
//                                        .replace(".", "-").trim();
//                                if (StringUtils.isNotEmpty(d) && d.length() >= 1) {
//                                    if (d.substring(d.length() - 1).equals("-")) {
//                                        d = d.substring(0, d.length() - 1);
//                                    }
//                                }
//                                if (ValidateUtils.isValidDate(d)) {
//                                    String datetime = DATE_FORMAT.format(new Date());
//                                    d = DATE_FORMAT.format(DATE_FORMAT.parse(d));
//                                    carBizDriverInfoDTO.setSigndate(d);
//                                } else {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???"
//                                            + (rowIx + 1)
//                                            + "???????????????"
//                                            + (colIx + 1)
//                                            + "??? ????????????????????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                }
//                            }
//                            break;
//                        //?????????????????????????????????
//                        case 45:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ????????????????????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String d = cellValue.getStringValue()
//                                        .replace("???", "-")
//                                        .replace("???", "-")
//                                        .replace("???", "-")
//                                        .replace(".", "-").trim();
//                                if (StringUtils.isNotEmpty(d) && d.length() >= 1) {
//                                    if (d.substring(d.length() - 1).equals("-")) {
//                                        d = d.substring(0, d.length() - 1);
//                                    }
//                                }
//                                if (ValidateUtils.isValidDate(d)) {
//                                    d = DATE_FORMAT.format(DATE_FORMAT.parse(d));
//                                    carBizDriverInfoDTO.setSigndateend(d);
//                                } else {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???"
//                                            + (rowIx + 1)
//                                            + "???????????????"
//                                            + (colIx + 1)
//                                            + "??? ????????????????????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                }
//                            }
//                            break;
//                        // ???????????????
//                        case 46:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ??????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String emergencyContactPerson = Common.replaceBlank(cellValue.getStringValue());
//                                carBizDriverInfoDTO.setEmergencyContactPerson(emergencyContactPerson);
//                            }
//                            break;
//                        // ??????????????????
//                        case 47:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ?????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String emergencyContactNumber = Common.replaceBlank(cellValue.getStringValue()).trim();
//                                if (emergencyContactNumber.length() > 11) {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                            + "??? ??????????????????????????????11???");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                } else {
//                                    carBizDriverInfoDTO.setEmergencyContactNumber(emergencyContactNumber);
//                                }
//                            }
//                            break;
//                        // ?????????????????????????????????
//                        case 48:
//                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
//                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                        + "??? ????????????????????????????????????????????????????????????????????????????????????");
//                                listException.add(returnVO);
//                                isTrue = false;
//                            } else {
//                                String emergencyContactAddr = Common.replaceBlank(cellValue.getStringValue());
//                                carBizDriverInfoDTO.setEmergencycontactaddr(emergencyContactAddr);
//                            }
//                            break;
//                        // ???????????????
//                        case 49:
//                            if (cellValue != null && !StringUtils.isEmpty(cellValue.getStringValue())) {
//                                bankCardNumber = Common.replaceBlank(cellValue.getStringValue());
//                                if (StringUtils.isNotEmpty(bankCardNumber) && !ValidateUtils.isRegular(bankCardNumber, ValidateUtils.BANK_CARD_NUMBER)) {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???" + (rowIx + 1) + "???????????????" + (colIx + 1)
//                                            + "??? ????????????????????????16~19???????????????");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                } else if (carBizDriverInfoDetailService.checkBankCardBank(bankCardNumber, null)) {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???" + (rowIx + 1) + "???????????????"
//                                            + (colIx + 1) + "??? ?????????????????????:" + cellValue.getStringValue() + "????????????");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                } else {
//                                    carBizDriverInfoDTO.setBankCardNumber(bankCardNumber);
//                                }
//                            }
//                            break;
//                        // ??????????????????
//                        case 50:
//                            if (cellValue != null && !StringUtils.isEmpty(cellValue.getStringValue())) {
//                                String bankCardBank = Common.replaceBlank(cellValue.getStringValue());
//                                if (bankCardBank.length() > 100) {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???" + (rowIx + 1) + "???????????????"
//                                            + (colIx + 1) + "??? ?????????????????????:??????????????????????????????100??????????????????????????????");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                } else if (StringUtils.isNotEmpty(bankCardBank) && StringUtils.isEmpty(bankCardNumber)) {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???" + (rowIx + 1) + "???????????????"
//                                            + (colIx + 1) + "??? ?????????????????????:????????????????????????");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                } else {
//                                    carBizDriverInfoDTO.setBankCardBank(bankCardBank);
//                                }
//                            } else {
//                                if (StringUtils.isNotEmpty(bankCardNumber)) {
//                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                                    returnVO.setReson( "???" + (rowIx + 1) + "???????????????"
//                                            + (colIx + 1) + "??? ????????????????????????:?????????????????????????????????");
//                                    listException.add(returnVO);
//                                    isTrue = false;
//                                }
//                            }
//                            break;
//                    }// switch end
//                }// ???????????????
//                if (isTrue && carBizDriverInfoDTO != null) {
//                    carBizDriverInfoDTO.setServiceCity(cityId);
//                    carBizDriverInfoDTO.setSupplierId(supplierId);
//                    carBizDriverInfoDTO.setTeamId(teamId);
//                    carBizDriverInfoDTO.setTeamGroupId(teamGroupId);
//
//                    if(teamId!=null){//????????????
//                        CarDriverTeam carDriverTeam = carDriverTeamMapper.selectByPrimaryKey(teamId);
//                        if(carDriverTeam!=null){
//                            carBizDriverInfoDTO.setTeamName(carDriverTeam.getTeamName());
//                        }
//                    }
//                    if(teamGroupId!=null){//????????????
//                        CarDriverTeam carDriverTeam = carDriverTeamMapper.selectByPrimaryKey(teamGroupId);
//                        if(carDriverTeam!=null){
//                            carBizDriverInfoDTO.setTeamGroupName(carDriverTeam.getTeamName());
//                        }
//                    }
//
//                    //??????????????????
//                    Map<String, Object> stringObjectMap = this.saveDriver(carBizDriverInfoDTO);
//                    if (stringObjectMap != null && stringObjectMap.containsKey("result") && (int)stringObjectMap.get("result")==1) {
//                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//                        returnVO.setReson( "?????????=" + carBizDriverInfoDTO.getPhone() + "?????????????????????=" + stringObjectMap.get("msg").toString());
//                        logger.info(LOGTAG + returnVO.getReson());
//                        listException.add(returnVO);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }finally {
//            if (is != null) {
//                try {
//                    is.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        try {
//            // ?????????????????????
//            if(listException.size() > 0) {
//                StringBuilder errorMsg = new StringBuilder();
//                for (CarImportExceptionEntity entity:listException){
//                    errorMsg.append(entity.getReson()).append(";");
//                }
//                resultMap.put("result", "0");
//                resultMap.put("msg", errorMsg);
//                return resultMap;
//            }
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        if(count==0){
//            resultMap.put("result", "0");
//            resultMap.put("msg", "??????????????????????????????");
//        }else {
//            resultMap.put("result", "1");
//            resultMap.put("msg", "??????");
//        }
//        return resultMap;
//    }

    /*
     * ????????????????????????
     */
//    public Workbook exportExcel(List<CarBizDriverInfoDTO> list, Integer cityId,Integer supplierId, String path) throws Exception{
//
//        long start=System.currentTimeMillis(); //??????????????????
//
//        FileInputStream io = new FileInputStream(path);
//        // ?????? excel
//        Workbook wb = new XSSFWorkbook(io);
//        if(list != null && list.size()>0){
//            Sheet sheet = null;
//            try {
//                sheet = wb.getSheetAt(0);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            Cell cell = null;
//            int i=0;
//
//            // ???????????????ID???????????????????????????????????????
//            CarBizSupplier carBizSupplier = carBizSupplierService.selectByPrimaryKey(supplierId);
//            String supplierName = "";
//            String cityName = "";
//            if (carBizSupplier != null) {
//                supplierName = carBizSupplier.getSupplierFullName();
//            }
//            // ????????????ID??????????????????
//            CarBizCity carBizCity = carBizCityService.selectByPrimaryKey(cityId);
//            if (carBizCity != null) {
//                cityName = carBizCity.getCityName();
//            }
//            Map<Integer, String> groupMap = null;
//            try {
//                groupMap = carBizCarGroupService.queryGroupNameMap();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            Map<Integer, String> teamMap = null;
//            Map<Integer, String> teamGroupMap = null;
//            try {
////                teamMap = carDriverTeamService.queryDriverTeamList(cityId, supplierId);
//                String driverIds = this.pingDriverIds(list);
//                teamMap = carDriverTeamService.queryDriverTeamListByDriverId(driverIds);
//                teamGroupMap = carDriverTeamService.queryDriverTeamGroupListByDriverId(driverIds);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            for(CarBizDriverInfoDTO s:list){
//
//                // ??????????????????????????????????????????????????????????????????
////                this.getBaseStatis(s);
//
//                Row row = sheet.createRow(i + 1);
//                // ?????????
//                cell = row.createCell(0);
//                cell.setCellValue(s.getLicensePlates()!=null?""+s.getLicensePlates()+"":"");
//                // ????????????????????????
//                cell = row.createCell(1);
//                cell.setCellValue(s.getName()!=null?""+s.getName()+"":"");
//                // ?????????????????????
//                cell = row.createCell(2);
//                cell.setCellValue(s.getIdCardNo()!=null?""+s.getIdCardNo()+"":"");
//                // ???????????????
//                cell = row.createCell(3);
//                cell.setCellValue(s.getPhone()!=null?""+s.getPhone()+"":"");
//                // ??????????????????
//                cell = row.createCell(4);
//                cell.setCellValue(s.getPhonetype()!=null?""+s.getPhonetype()+"":"");
//                // ?????????????????????
//                cell = row.createCell(5);
//                cell.setCellValue(s.getPhonecorp()!=null?""+s.getPhonecorp()+"":"");
//                // ??????
//                cell = row.createCell(6);
//                cell.setCellValue(s.getGender()!=null?""+(s.getGender()==1?"???":"???"+""):"");
//                // ????????????
//                cell = row.createCell(7);
//                cell.setCellValue(s.getBirthDay()!=null?""+s.getBirthDay()+"":"");
//                // ??????
//                cell = row.createCell(8);
//                cell.setCellValue(s.getAge()!=null?""+s.getAge()+"":"");
//                // ??????????????????
//                cell = row.createCell(9);
//                cell.setCellValue(s.getSuperintendNo()!=null?""+s.getSuperintendNo()+"":"");
//                // ??????????????????
//                cell = row.createCell(10);
//                cell.setCellValue(s.getSuperintendUrl()!=null?""+s.getSuperintendUrl()+"":"");
//                // ????????????
//                String groupName = "";
//                if(groupMap!=null){
//                    groupName = groupMap.get(s.getGroupId());
//                }
//                cell = row.createCell(11);
//                cell.setCellValue(groupName);
//                // ????????????
//                cell = row.createCell(12);
//                cell.setCellValue(s.getDrivingLicenseType()!=null?""+s.getDrivingLicenseType()+"":"");
//                // ??????????????????
//                cell = row.createCell(13);
//                cell.setCellValue(DateUtil.getTimeString(s.getIssueDate()));
//                // ??????
//                cell = row.createCell(14);
//                cell.setCellValue(s.getDrivingYears()!=null?""+s.getDrivingYears()+"":"");
//                // ??????????????????
//                cell = row.createCell(15);
//                cell.setCellValue(DateUtil.getTimeString(s.getExpireDate()));
//                // ????????????
//                cell = row.createCell(16);
//                cell.setCellValue(s.getArchivesNo()!=null?""+s.getArchivesNo()+"":"");
//                // ??????
//                cell = row.createCell(17);
//                cell.setCellValue(s.getNationality()!=null?""+s.getNationality()+"":"");
//                // ???????????????
//                cell = row.createCell(18);
//                cell.setCellValue(s.getNation()!=null?""+s.getNation()+"":"");
//                // ?????????????????????
//                cell = row.createCell(19);
//                cell.setCellValue(s.getMarriage()!=null?""+s.getMarriage()+"":"");
//                // ?????????????????????
//                String foreignlanguageName= "???";
//                String foreignLanguage = s.getForeignlanguage();
//                if(StringUtils.isNotEmpty(foreignLanguage)){
//                    if("1".equals(foreignLanguage)){
//                        foreignlanguageName = "??????";
//                    }else if("2".equals(foreignLanguage)){
//                        foreignlanguageName = "??????";
//                    }else if("3".equals(foreignLanguage)){
//                        foreignlanguageName = "??????";
//                    }else if("4".equals(foreignLanguage)){
//                        foreignlanguageName = "??????";
//                    }
//                }
//                cell = row.createCell(20);
//                cell.setCellValue(foreignlanguageName);
//                // ???????????????
//                cell = row.createCell(21);
//                cell.setCellValue(s.getEducation()!=null?""+s.getEducation()+"":"");
//                // ????????????????????????
//                cell = row.createCell(22);
//                cell.setCellValue(s.getHouseHoldRegisterPermanent()!=null?""+s.getHouseHoldRegisterPermanent()+"":"");
//                // ???????????????????????????
//                cell = row.createCell(23);
//                cell.setCellValue(s.getHouseholdregister()!=null?""+s.getHouseholdregister()+"":"");
//                // ?????????????????????
//                cell = row.createCell(24);
//                cell.setCellValue(s.getCurrentAddress()!=null?""+s.getCurrentAddress()+"":"");
//                // ???????????????????????????
//                cell = row.createCell(25);
//                cell.setCellValue(s.getPhotosrct()!=null?""+s.getPhotosrct()+"":"");
//                // ?????????????????????
//                cell = row.createCell(26);
//                cell.setCellValue(s.getDriverlicensenumber()!=null?""+s.getDriverlicensenumber()+"":"");
//                // ???????????????????????????????????????
//                cell = row.createCell(27);
//                cell.setCellValue(s.getDrivinglicenseimg()!=null?""+s.getDrivinglicenseimg()+"":"");
//                //???????????????????????????
//                cell = row.createCell(28);
//                cell.setCellValue(s.getFirstdrivinglicensedate()!=null?""+s.getFirstdrivinglicensedate()+"":"");
//                //?????????????????????????????????
//                cell = row.createCell(29);
//                cell.setCellValue(s.getIsxydriver()!=null?""+(s.getIsxydriver()==1?"???":"???"+""):"");
//                //?????????????????????????????????????????????
//                cell = row.createCell(30);
//                cell.setCellValue(s.getDriverlicenseissuingnumber()!=null?""+s.getDriverlicenseissuingnumber()+"":"");
//                //????????????????????????????????????????????????
//                cell = row.createCell(31);
//                cell.setCellValue(s.getFirstmeshworkdrivinglicensedate()!=null?""+s.getFirstmeshworkdrivinglicensedate()+"":"");
//                //???????????????????????????????????????
//                cell = row.createCell(32);
//                cell.setCellValue(s.getXyDriverNumber()!=null?""+s.getXyDriverNumber()+"":"");
//                //????????????????????????????????????????????????
//                cell = row.createCell(33);
//                cell.setCellValue(s.getDriverlicenseissuingcorp()!=null?""+s.getDriverlicenseissuingcorp()+"":"");
//                //?????????????????????
//                cell = row.createCell(34);
//                cell.setCellValue(s.getDriverLicenseIssuingGrantDate()!=null?""+s.getDriverLicenseIssuingGrantDate()+"":"");
//                //???????????????????????????
//                cell = row.createCell(35);
//                cell.setCellValue(s.getDriverLicenseIssuingFirstDate()!=null?""+s.getDriverLicenseIssuingFirstDate()+"":"");
//                //???????????????????????????
//                cell = row.createCell(36);
//                cell.setCellValue(s.getDriverlicenseissuingdatestart()!=null?""+s.getDriverlicenseissuingdatestart()+"":"");
//                //???????????????????????????
//                cell = row.createCell(37);
//                cell.setCellValue(s.getDriverlicenseissuingdateend()!=null?""+s.getDriverlicenseissuingdateend()+"":"");
//                //????????????
//                cell = row.createCell(38);
//                cell.setCellValue(s.getDriverLicenseIssuingRegisterDate()!=null?""+s.getDriverLicenseIssuingRegisterDate()+"":"");
//                //?????????????????????
//                cell = row.createCell(39);
//                cell.setCellValue(s.getParttimejobdri()!=null?""+s.getParttimejobdri()+"":"");
//                //??????????????????????????????????????????
//                cell = row.createCell(40);
//                cell.setCellValue(s.getCorptype()!=null?""+s.getCorptype()+"":"");
//                //??????????????????
//                cell = row.createCell(41);
//                cell.setCellValue(s.getContractdate()!=null?""+s.getContractdate()+"":"");
//                //?????????????????????????????????
//                cell = row.createCell(42);
//                cell.setCellValue(s.getSigndate()!=null?""+s.getSigndate()+"":"");
//                //?????????????????????????????????
//                cell = row.createCell(43);
//                cell.setCellValue(s.getSigndateend()!=null?""+s.getSigndateend()+"":"");
//                // ???????????????
//                cell = row.createCell(44);
//                cell.setCellValue(s.getEmergencyContactPerson()!=null?""+s.getEmergencyContactPerson()+"":"");
//                // ??????????????????
//                cell = row.createCell(45);
//                cell.setCellValue(s.getEmergencyContactNumber()!=null?""+s.getEmergencyContactNumber()+"":"");
//                // ?????????????????????????????????
//                cell = row.createCell(46);
//                cell.setCellValue(s.getEmergencycontactaddr()!=null?""+s.getEmergencycontactaddr()+"":"");
//                //?????????
//                cell = row.createCell(47);
//                cell.setCellValue(supplierName);
//                //????????????
//                cell = row.createCell(48);
//                cell.setCellValue(cityName);
//                //??????
//                String teamName = "";
//                if(teamMap!=null){
//                    teamName = teamMap.get(s.getDriverId());
//                }
//                cell = row.createCell(49);
//                cell.setCellValue(teamName);
//                //??????
//                String teamGroupName = "";
//                if(teamGroupMap!=null){
//                    teamGroupName = teamGroupMap.get(s.getDriverId());
//                }
//                cell = row.createCell(50);
//                cell.setCellValue(teamGroupName);
//                //??????id
//                cell = row.createCell(51);
//                cell.setCellValue(s.getDriverId()!=null?""+s.getDriverId()+"":"");
//                //????????????
//                cell = row.createCell(52);
//                cell.setCellValue(DateUtil.getTimeString(s.getCreateDate()));
//
//                i++;
//            }
//        }
//        long end=System.currentTimeMillis(); //??????????????????
//        logger.info(LOGTAG + "????????????cityId={},supplierId={}?????????????????????={}ms", cityId, supplierId, (end-start));
//        return wb;
//    }
    public Workbook exportExcel(List<CarBizDriverInfoDTO> list, Integer cityId,Integer supplierId, String path) throws Exception{

        long start=System.currentTimeMillis(); //??????????????????

        FileInputStream io = new FileInputStream(path);
        // ?????? excel
        Workbook wb = new XSSFWorkbook(io);
        if(list != null && list.size()>0){
            Sheet sheet = null;
            try {
                sheet = wb.getSheetAt(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Cell cell = null;
            int i=0;

            // ???????????????ID???????????????????????????????????????
            CarBizSupplier carBizSupplier = carBizSupplierService.selectByPrimaryKey(supplierId);
            String supplierName = "";
            String cityName = "";
            if (carBizSupplier != null) {
                supplierName = carBizSupplier.getSupplierFullName();
            }
            // ????????????ID??????????????????
            CarBizCity carBizCity = carBizCityService.selectByPrimaryKey(cityId);
            if (carBizCity != null) {
                cityName = carBizCity.getCityName();
            }
            Map<Integer, String> groupMap = null;
            try {
                groupMap = carBizCarGroupService.queryGroupNameMap();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Map<Integer, String> teamMap = null;
            Map<Integer, String> teamGroupMap = null;
            try {
//                teamMap = carDriverTeamService.queryDriverTeamList(cityId, supplierId);
                String driverIds = this.pingDriverIds(list);
                teamMap = carDriverTeamService.queryDriverTeamListByDriverId(driverIds);
                teamGroupMap = carDriverTeamService.queryDriverTeamGroupListByDriverId(driverIds);
            } catch (Exception e) {
                e.printStackTrace();
            }

            for(CarBizDriverInfoDTO s:list){

                // ??????????????????????????????????????????????????????????????????
//                this.getBaseStatis(s);

                Row row = sheet.createRow(i + 1);
                // ?????????
                cell = row.createCell(0);
                cell.setCellValue(s.getLicensePlates()!=null?""+s.getLicensePlates()+"":"");
                // ????????????????????????
                cell = row.createCell(1);
                cell.setCellValue(s.getName()!=null?""+s.getName()+"":"");
                // ?????????????????????
                cell = row.createCell(2);
                cell.setCellValue(s.getIdCardNo()!=null?""+s.getIdCardNo()+"":"");
                // ???????????????
                cell = row.createCell(3);
                cell.setCellValue(s.getPhone()!=null?""+s.getPhone()+"":"");
                // ??????????????????
                cell = row.createCell(4);
                cell.setCellValue(s.getPhonetype()!=null?""+s.getPhonetype()+"":"");
                // ?????????????????????
                cell = row.createCell(5);
                cell.setCellValue(s.getPhonecorp()!=null?""+s.getPhonecorp()+"":"");
                // ??????
                cell = row.createCell(6);
                cell.setCellValue(s.getGender()!=null?""+(s.getGender()==1?"???":"???"+""):"");
                // ????????????
                cell = row.createCell(7);
                cell.setCellValue(s.getBirthDay()!=null?""+s.getBirthDay()+"":"");
                // ??????
                cell = row.createCell(8);
                cell.setCellValue(s.getAge()!=null?""+s.getAge()+"":"");
                // ??????????????????
                cell = row.createCell(9);
                cell.setCellValue(s.getSuperintendNo()!=null?""+s.getSuperintendNo()+"":"");
                // ??????????????????
                cell = row.createCell(10);
                cell.setCellValue(s.getSuperintendUrl()!=null?""+s.getSuperintendUrl()+"":"");
                // ????????????
                String groupName = "";
                if(groupMap!=null){
                    groupName = groupMap.get(s.getGroupId());
                }
                cell = row.createCell(11);
                cell.setCellValue(groupName);
                // ????????????
                cell = row.createCell(12);
                cell.setCellValue(s.getDrivingLicenseType()!=null?""+s.getDrivingLicenseType()+"":"");
                // ??????????????????
                cell = row.createCell(13);
                cell.setCellValue(DateUtil.getTimeString(s.getIssueDate()));
                // ??????
                cell = row.createCell(14);
                cell.setCellValue(s.getDrivingYears()!=null?""+s.getDrivingYears()+"":"");
                // ??????????????????
                cell = row.createCell(15);
                cell.setCellValue(DateUtil.getTimeString(s.getExpireDate()));
                // ????????????
                cell = row.createCell(16);
                cell.setCellValue(s.getArchivesNo()!=null?""+s.getArchivesNo()+"":"");
                // ??????
                cell = row.createCell(17);
                cell.setCellValue(s.getNationality()!=null?""+s.getNationality()+"":"");
                // ???????????????
                cell = row.createCell(18);
                cell.setCellValue(s.getNation()!=null?""+s.getNation()+"":"");
                // ?????????????????????
                cell = row.createCell(19);
                cell.setCellValue(s.getMarriage()!=null?""+s.getMarriage()+"":"");
                // ?????????????????????
                String foreignlanguageName= "???";
                String foreignLanguage = s.getForeignlanguage();
                if(StringUtils.isNotEmpty(foreignLanguage)){
                    if("1".equals(foreignLanguage)){
                        foreignlanguageName = "??????";
                    }else if("2".equals(foreignLanguage)){
                        foreignlanguageName = "??????";
                    }else if("3".equals(foreignLanguage)){
                        foreignlanguageName = "??????";
                    }else if("4".equals(foreignLanguage)){
                        foreignlanguageName = "??????";
                    }
                }
                cell = row.createCell(20);
                cell.setCellValue(foreignlanguageName);
                // ???????????????
                cell = row.createCell(21);
                // ????????????????????????
                cell = row.createCell(22);
                cell.setCellValue(s.getEducation()!=null?""+s.getEducation()+"":"");
                cell.setCellValue(s.getHouseHoldRegisterPermanent()!=null?""+s.getHouseHoldRegisterPermanent()+"":"");
                // ???????????????????????????
                cell = row.createCell(23);
                cell.setCellValue(s.getHouseholdregister()!=null?""+s.getHouseholdregister()+"":"");
                // ?????????????????????
                cell = row.createCell(24);
                cell.setCellValue(s.getCurrentAddress()!=null?""+s.getCurrentAddress()+"":"");
                // ???????????????????????????
                cell = row.createCell(25);
                cell.setCellValue(s.getPhotosrct()!=null?""+s.getPhotosrct()+"":"");
                // ?????????????????????
                cell = row.createCell(26);
                cell.setCellValue(s.getDriverlicensenumber()!=null?""+s.getDriverlicensenumber()+"":"");
                // ???????????????????????????????????????
                cell = row.createCell(27);
                cell.setCellValue(s.getDrivinglicenseimg()!=null?""+s.getDrivinglicenseimg()+"":"");
                //???????????????????????????
                cell = row.createCell(28);
                cell.setCellValue(s.getFirstdrivinglicensedate()!=null?""+s.getFirstdrivinglicensedate()+"":"");
                //?????????????????????????????????
                cell = row.createCell(29);
                cell.setCellValue(s.getIsxydriver()!=null?""+(s.getIsxydriver()==1?"???":"???"+""):"");
                //?????????????????????????????????????????????
                cell = row.createCell(30);
                cell.setCellValue(s.getDriverlicenseissuingnumber()!=null?""+s.getDriverlicenseissuingnumber()+"":"");
                //????????????????????????????????????????????????
                cell = row.createCell(31);
                cell.setCellValue(s.getFirstmeshworkdrivinglicensedate()!=null?""+s.getFirstmeshworkdrivinglicensedate()+"":"");
                //???????????????????????????????????????
                cell = row.createCell(32);
                cell.setCellValue(s.getXyDriverNumber()!=null?""+s.getXyDriverNumber()+"":"");
                //????????????????????????????????????????????????
                cell = row.createCell(33);
                cell.setCellValue(s.getDriverlicenseissuingcorp()!=null?""+s.getDriverlicenseissuingcorp()+"":"");
                //?????????????????????
                cell = row.createCell(34);
                cell.setCellValue(s.getDriverLicenseIssuingGrantDate()!=null?""+s.getDriverLicenseIssuingGrantDate()+"":"");
                //???????????????????????????
                cell = row.createCell(35);
                cell.setCellValue(s.getDriverLicenseIssuingFirstDate()!=null?""+s.getDriverLicenseIssuingFirstDate()+"":"");
                //???????????????????????????
                cell = row.createCell(36);
                cell.setCellValue(s.getDriverlicenseissuingdatestart()!=null?""+s.getDriverlicenseissuingdatestart()+"":"");
                //???????????????????????????
                cell = row.createCell(37);
                cell.setCellValue(s.getDriverlicenseissuingdateend()!=null?""+s.getDriverlicenseissuingdateend()+"":"");
                //????????????
                cell = row.createCell(38);
                cell.setCellValue(s.getDriverLicenseIssuingRegisterDate()!=null?""+s.getDriverLicenseIssuingRegisterDate()+"":"");
                //?????????????????????
                cell = row.createCell(39);
                cell.setCellValue(s.getParttimejobdri()!=null?""+s.getParttimejobdri()+"":"");
                //??????????????????????????????????????????
                cell = row.createCell(40);
                cell.setCellValue(s.getCorptype()!=null?""+s.getCorptype()+"":"");
                //??????????????????
                cell = row.createCell(41);
                cell.setCellValue(s.getContractdate()!=null?""+s.getContractdate()+"":"");
                //?????????????????????????????????
                cell = row.createCell(42);
                cell.setCellValue(s.getSigndate()!=null?""+s.getSigndate()+"":"");
                //?????????????????????????????????
                cell = row.createCell(43);
                cell.setCellValue(s.getSigndateend()!=null?""+s.getSigndateend()+"":"");
                // ???????????????
                cell = row.createCell(44);
                cell.setCellValue(s.getEmergencyContactPerson()!=null?""+s.getEmergencyContactPerson()+"":"");
                // ??????????????????
                cell = row.createCell(45);
                cell.setCellValue(s.getEmergencyContactNumber()!=null?""+s.getEmergencyContactNumber()+"":"");
                // ?????????????????????????????????
                cell = row.createCell(46);
                cell.setCellValue(s.getEmergencycontactaddr()!=null?""+s.getEmergencycontactaddr()+"":"");
                //?????????
                cell = row.createCell(47);
                cell.setCellValue(supplierName);
                //????????????
                cell = row.createCell(48);
                cell.setCellValue(cityName);
                //??????
                String teamName = "";
                if(teamMap!=null){
                    teamName = teamMap.get(s.getDriverId());
                }
                cell = row.createCell(49);
                cell.setCellValue(teamName);
                //??????
                String teamGroupName = "";
                if(teamGroupMap!=null){
                    teamGroupName = teamGroupMap.get(s.getDriverId());
                }
                cell = row.createCell(50);
                cell.setCellValue(teamGroupName);
                //??????id
                cell = row.createCell(51);
                cell.setCellValue(s.getDriverId()!=null?""+s.getDriverId()+"":"");
                //????????????
                cell = row.createCell(52);
                cell.setCellValue(DateUtil.getTimeString(s.getCreateDate()));

                i++;
            }
        }
        long end=System.currentTimeMillis(); //??????????????????
        logger.info(LOGTAG + "????????????cityId={},supplierId={}?????????????????????={}ms", cityId, supplierId, (end-start));
        return wb;
    }


    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * ???????????????????????????????????????????????????????????????????????????
     * @param driverId
     * @param phone
     * @return
     */
    public Map<String, Object> isUpdateDriver(Integer driverId, String phone){
        Map<String, Object> result = Maps.newHashMap();
        try {
            if(!this.getLock(phone)){
                // ???????????????-???????????????????????????
                logger.info(LOGTAG + "??????????????????-driverId="+driverId+",?????????????????????,?????????????????????...");
                result.put("result", 2);
                result.put("msg", "?????????????????????...??????????????????");
                return result;
            }
        } catch (Exception e) {
            logger.error(LOGTAG + "??????????????????-driverId="+driverId+",?????????????????????",e);
            result.put("result", 2);
            result.put("msg", "?????????????????????...??????????????????");
            return result;
        }
        try {
            // ???????????????????????????-?????????????????????
            boolean ok = getDriverServiceTripList(driverId);
            if(ok){
                // ????????????????????????-???????????????????????????
                result.put("result", 2);
                result.put("msg", "???????????????????????????????????????????????????????????????");
                logger.info(LOGTAG + "??????????????????-driverId="+driverId+",??????????????????,???????????????????????????");
                return result;
            }else{
                logger.info(LOGTAG + "??????????????????-driverId="+driverId+",??????????????????,????????????????????????");
            }
        } catch (Exception e) {
            logger.error(LOGTAG + "??????????????????-driverId:"+driverId+"??????",e);
        }finally {
            // ?????????
            unLock(phone);
        }
        result.put("result", 1);
        result.put("msg", "?????????????????????");
        return result;
    }

    // ???????????????-???????????????
    private Boolean getLock(String phone){
        boolean lock = false;
        int expireTime = 20;
        String key = "D" + phone + "_lock";
        String value = RedisCacheDriverUtil.get(key, String.class);
        if(StringUtils.isBlank(value)){
//            long expire = System.currentTimeMillis() + expireTime * 1000 + 1;
            long expire = System.currentTimeMillis() + expireTime * 1000 + 1;
//            String result = RedisCacheDriverUtil.getSet(key, String.valueOf(expire), String.class);
            String result = RedisCacheDriverUtil.set(key, String.valueOf(expire), expireTime);
//            logger.info(LOGTAG + "?????????-??????KEY[" + key + "] " + result);
            if(result != null){
                lock = true;
            }
        }
        return lock;
    }

    // ?????????????????????
    private void unLock(String phone){
        String key = "D" + phone + "_lock";
        RedisCacheDriverUtil.delete(key);
        logger.info(LOGTAG + "?????????-??????KEY[={}]", key);
    }

    /**
     * ???????????????????????????
     * @param driverId
     * @return
     */
    private Boolean getDriverServiceTripList(Integer driverId) {
        boolean flag = true;
        TreeMap<String, Object> params = new TreeMap<String, Object>();
        params.put("driverId",  driverId);
        params.put("limit",  1);
        params.put("page",  1);
        getOrderSignMap(params);
        try {
            logger.info(LOGTAG + "????????????????????????????????????????????????????????????...driverId="+driverId);
            com.alibaba.fastjson.JSONObject jsonObj = MpOkHttpUtil.okHttpPostBackJson(orderServiceApiBaseUrl + DRIVER_SERVICE_TRIPLIST_URL, params, 3000, "??????????????????????????????????????????????????????");
            logger.info(LOGTAG + "????????????????????????????????????????????????????????????...driverId="+driverId+",??????:"+jsonObj.toString());
            if(jsonObj==null || !jsonObj.containsKey("code")){
                logger.info(LOGTAG + "??????????????????????????????????????????????????????,??????");

            }else{
                int code = jsonObj.getIntValue("code");
                if(code == 0){
                    com.alibaba.fastjson.JSONObject data = jsonObj.getJSONObject("data");
                    if(null != data){
                        // ??????????????????
                        boolean hasServiceOrder = data.getBoolean("hasServiceOrder");
                        flag = hasServiceOrder;
                        logger.info(LOGTAG + "??????????????????????????????????????????????????????:hasServiceOrder="+hasServiceOrder);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(LOGTAG + "????????????????????????????????????????????????????????????",e);
        }
        return flag;
    }

    /**
     * ????????????
     * @param params
     */
    private void getOrderSignMap(TreeMap<String, Object> params) {
        params.put("bId", Common.BUSSINESSID );
        // ?????????????????????????????????null?????????""???????????????????????????sign ?????????????????????????????????????????????????????????key
        StringBuilder _sb = new StringBuilder();
        for (Map.Entry<String, Object> e : params.entrySet()) {
            if (e == null || e.getValue() == null || "".equals(e.getValue())) {
                continue;
            }
            _sb.append(e.getKey()).append("=").append(e.getValue()).append("&");
        }
        _sb.append("key=").append(Common.MAIN_ORDER_KEY);// ??????key??????????????????
        params.put("sign", new String(Base64.encodeBase64((getMD5ForByte(_sb.toString())))));
    }

    /**
     * ??????????????????md5???
     *
     * @param str
     * @return
     */
    private byte[] getMD5ForByte(String str) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(str.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return array;
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void flashDriverInfo(Integer driverId){
        // ????????????????????????,???????????????????????????
        try {
            String url = driverServiceApiUrl + DRIVER_FLASH_REDIS_URL+"?driverId="+driverId;
            String result = HttpClientUtil.buildGetRequest(url).execute();
            logger.info(LOGTAG + "????????????????????????,???????????????????????????,??????????????????={}", result);
        } catch (HttpException e) {
            logger.info(LOGTAG + "??????driverId={},??????,????????????????????????={}", driverId, e.getMessage());
        }
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * ??????????????????????????????
     * @param map
     */
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.MASTER)
    })
    public void updateDriverCardInfo(Map<String, Object> map) {
        Map<String,Object> result = new HashMap<String,Object>();
        try {
            // ???????????????????????????????????????
            int rtn = carBizDriverInfoExMapper.updateDriverCardInfo(map);
            driverMongoService.updateDriverCardInfo(map);
            if (rtn > 0) {
                result.put("result", 1);
                if(map != null && map.containsKey("driverId")){
                    Integer driverId = Integer.valueOf(map.get("driverId").toString());
                    try {
                        Map<String, Object> paramMap = new HashMap<String, Object>();
                        paramMap.put("driverId", driverId);
                        String url = mpReatApiUrl + Common.DRIVER_WIDE_MQ;
                        String jsonObjectStr = HttpClientUtil.buildPostRequest(url).addParams(paramMap).addHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED).execute();
                        logger.info("driverId={}?????????????????????????????????MQ??????, result={}", driverId, jsonObjectStr);
                    } catch (HttpException e) {
                        logger.info("driverId={}?????????????????????????????????MQ??????, error={}", driverId, e);
                    }
                }
            } else {
                result.put("result", 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CarBizDriverInfoDTO selectByPhone(String phone){
        CarBizDriverInfoDTO carBizDriverInfo = carBizDriverInfoExMapper.selectByPhone(phone);
        return carBizDriverInfo;
    }

    /**
     * ????????????????????????????????????
     * @param licensePlates
     * @return
     */
    public Boolean checkLicensePlates(String licensePlates){
        int count = carBizDriverInfoExMapper.checkLicensePlates(licensePlates);
        if(count>0){
            return true;
        }
        return false;
    }

    /**
     * ?????????????????????????????????
     * @param license_plates
     * @return
     */
    public List<CarBizDriverInfoDTO> queryDriverByLicensePlates(String license_plates) {
        return carBizDriverInfoExMapper.queryDriverByLicensePlates(license_plates);
    }

    public String pingDriverIds(List<CarBizDriverInfoDTO> list) {
        String driverId = "";
        if(list!=null&&list.size()>0){
            int j=0;
            for(int i=0;i<list.size();i++){
                if(!"".equals(list.get(i))&&list.get(i)!=null&&!"".equals(list.get(i).getDriverId())&&list.get(i).getDriverId()!=null){
                    if(j==0){
                        driverId = "'"+list.get(i).getDriverId()+"'";
                    }else{
                        driverId +=",'"+list.get(i).getDriverId()+"'";
                    }
                    j++	;
                }
            }
        }
        return driverId;
    }


    /**
     * ?????????????????????????????????
     * @param carBizDriverInfoList
     * @param exportStringList
     */
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE),
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.SLAVE)
    })
    public void batchGetBaseStatis(List<CarBizDriverInfoDTO> carBizDriverInfoList,List<String> exportStringList) {
        if(carBizDriverInfoList == null || carBizDriverInfoList.isEmpty()){
            return ;
        }
        logger.info("????????????????????????????????????"+carBizDriverInfoList.size());
        Set<Integer> createBySet = new HashSet<>();
        Set<Integer> updateBySet = new HashSet<>();

        Set<Integer> supplierIdSet = new HashSet<>();
        Set<Integer> cooperationTypeIdSet = new HashSet<>();
        Set<String> licensePlatesSet = new HashSet<>();
        Set<Integer> cityIdSet = new HashSet<>();
        Set<Integer> driveridSet = new HashSet<>();
        Set<Integer> carBizCarGroupSet = new HashSet<>();


        //??????????????????
        for(CarBizDriverInfoDTO item:carBizDriverInfoList){
            if(item.getCreateBy() != null){
                createBySet.add(item.getCreateBy());
            }
            if(item.getUpdateBy() != null){
                updateBySet.add(item.getUpdateBy());
            }
            if(item.getSupplierId() != null){
                supplierIdSet.add(item.getSupplierId());
            }
            if(item.getCooperationType() != null){
                cooperationTypeIdSet.add(item.getCooperationType());
            }
            if(StringUtils.isNotEmpty(item.getLicensePlates())){
                licensePlatesSet.add(item.getLicensePlates());
            }
            if(item.getServiceCity() != null){
                cityIdSet.add(item.getServiceCity());
            }
            if(item.getGroupId() != null){
                carBizCarGroupSet.add(item.getGroupId());
            }

            driveridSet.add(item.getDriverId());

        }
        //??????createBySet?????????????????????
        List<CarAdmUser> createUserList = null;
        if(!(createBySet.isEmpty())){
            createUserList = carAdmUserExMapper.selectUsersByIdList(new ArrayList<>(createBySet));
        }
        //??????createBySet?????????????????????
        List<CarAdmUser> updateUserList = null;
        if(!(updateBySet.isEmpty())){
            updateUserList = carAdmUserExMapper.selectUsersByIdList(new ArrayList<>(updateBySet));
        }
        //??????supplierIdSet?????????????????????
       List<CarBizSupplier> supplierList =  null;
        if(!supplierIdSet.isEmpty()){
            supplierList =  carBizSupplierService.findByIdSet(supplierIdSet);
        }
        //???????????????????????????
        List<CarBizCooperationType> carBizCooperationTypeList = carBizCooperationTypeService.queryCarBizCooperationTypeList();

        //??????cityIdSet??????????????????
        Map<Integer, CarBizCity> cityMap =  null;
        if(!cityIdSet.isEmpty()){
            cityMap =  carBizCityService.queryCity(cityIdSet);
        }
        //??????carBizCarGroupSet?????????????????????
        List<CarBizCarGroup> carBizCarGroupList =  null;
        if(carBizCarGroupSet != null){
            carBizCarGroupList =  carBizCarGroupService.queryCarGroupByIdSet(carBizCarGroupSet);
        }
        //??????????????????????????????
        List<CarBizCarInfo> carBizCarInfolist = null;
        //???????????????????????????
        Map<String,CarBizCarInfo> carBizCarInfoMap = new HashMap<>();
        Set<Integer> carModelIdSet = new HashSet<>();
        if(!licensePlatesSet.isEmpty()){
            carBizCarInfolist =   carBizCarInfoExMapper.selectByLicensePlates(licensePlatesSet);
            if(carBizCarInfolist != null){
                for(CarBizCarInfo item :carBizCarInfolist){
                    carBizCarInfoMap.put(item.getLicensePlates(),item);
                    if(item.getCarModelId() != null){
                        carModelIdSet.add(item.getCarModelId());
                    }

                }
            }
        }
        Map<String,CarBizModel> carBizModelMap = new HashMap<>();
        if(!carModelIdSet.isEmpty()){
            List<CarBizModel>  carBizModels = carBizModelExMapper.findByIdSet(carModelIdSet);
            if(carBizModels != null){
                for(CarBizModel item : carBizModels){
                    carBizModelMap.put("t_"+item.getModelId(),item);
                }
            }
        }

        //??????????????????
        List<Integer>  teamIdList = null;
        List<Integer>  teamGroupIdList = null;
        List<CarDriverTeam>  carDriverTeamList = null; //????????????

        List<CarRelateGroup>  driverTeamGroupIdList = null;//???????????????
        List<CarDriverTeam>  carGroupDriverTeamList = null; //??????????????????
        List<CarRelateTeam> driverTeamRelationEntityList = null;

        List<DriverComplianceDTO> driverComplianceDTOList = null;

        Map<String,Integer> driverTeamMap = new HashMap<>();
        Map<String,Integer> driverGroupMap = new HashMap<>();
        if(!driveridSet.isEmpty()){
            //????????????????????????
              driverTeamRelationEntityList = carRelateTeamExMapper.queryByDriverIdList(driveridSet);
              if(driverTeamRelationEntityList != null){
                  teamIdList = new ArrayList<>();
                  for(CarRelateTeam item : driverTeamRelationEntityList){
                      if(item.getTeamId() != null){
                          teamIdList.add(item.getTeamId());
                      }
                      driverTeamMap.put("t_"+item.getDriverId(),item.getTeamId());

                  }
              }
              //??????????????????
              if(teamIdList != null && teamIdList.size() >= 1){
                    carDriverTeamList =  carDriverTeamExMapper.queryTeamListByTemIdList(teamIdList);
              }
                //????????????????????????
            List<Integer> driverList = new ArrayList(driveridSet);
            //??????id??????
            driverTeamGroupIdList = carRelateGroupExMapper.queryDriverGroupRelationListByDriverIdSet(driverList);
            if(driverTeamGroupIdList != null){
                if(driverTeamGroupIdList != null){
                    teamGroupIdList = new ArrayList<>();
                    for(CarRelateGroup item : driverTeamGroupIdList){
                        if( item.getGroupId() != null){
                            teamGroupIdList.add(item.getGroupId());
                        }
                        driverGroupMap.put("t_"+item.getDriverId(),item.getGroupId());

                    }
                }
                if(teamGroupIdList != null && teamGroupIdList.size() >= 1) {
                    carGroupDriverTeamList = carDriverTeamExMapper.queryTeamListByTemIdList(teamGroupIdList);
                }
            }

            /**
             * ????????????driverid??????????????????????????????
             * ext3 ???????????? 1:???????????? 2:???????????? 3:???????????? 4:?????????
             * ext2 ???????????? 0????????? 1 ??????
             */
            driverComplianceDTOList = carBizDriverInfoExMapper.queryDriverComplianceDTOListByDriverIdSet(driveridSet);
        }

        //??????list??????Map
        Map<String,CarAdmUser> createByMap = new HashMap<>();
        Map<String,CarAdmUser> updateByMap = new HashMap<>();
        Map<String,CarBizSupplier> supplierMap = new HashMap<>();
        Map<String,CarBizCooperationType> cooperationTypeMap = new HashMap<>();
        Map<String,CarDriverTeam> teamMap = new HashMap<>();
        Map<String,CarDriverTeam> teamGroupMap = new HashMap<>();
        Map<String,CarBizCarGroup> groupMap = new HashMap<>();

        //??????map
        Map<String,DriverComplianceDTO> driverComplianceDTOMap = new HashMap<>();

        if(driverComplianceDTOList != null && !driverComplianceDTOList.isEmpty()){
            for (DriverComplianceDTO item : driverComplianceDTOList){
                driverComplianceDTOMap.put("t_" + item.getDriverId(),item);
            }
        }


        if(createUserList != null){
            for(CarAdmUser item : createUserList){
                createByMap.put("t_"+item.getUserId(),item);
            }
        }
        if(updateUserList != null){
            for(CarAdmUser item : updateUserList){
                updateByMap.put("t_"+item.getUserId(),item);
            }
        }
        if(supplierList != null){
            for(CarBizSupplier item : supplierList){
                supplierMap.put("t_"+item.getSupplierId(),item);
            }
        }
        if(carBizCooperationTypeList != null){
            for(CarBizCooperationType item : carBizCooperationTypeList){
                cooperationTypeMap.put("t_"+item.getId(),item);
            }
        }
        if(carDriverTeamList != null){
            for(CarDriverTeam item : carDriverTeamList){
                teamMap.put("t_"+item.getId(),item);
            }
        }
        if(carGroupDriverTeamList != null){
            for(CarDriverTeam item : carGroupDriverTeamList){
                teamGroupMap.put("t_"+item.getId(),item);
            }
        }

        if(carBizCarGroupList != null){
            for(CarBizCarGroup item : carBizCarGroupList){
                groupMap.put("t_"+item.getGroupId(),item);
            }
        }

        CarBizSupplier carBizSupplier = null;
        CarBizCity carBizCity = null;
        CarAdmUser carAdmUser = null;
        Integer teamId = null;
        Integer driverId = null;
        Integer groupId = null;
        CarDriverTeam carDriverTeam = null;
        CarDriverTeam driverTeamGroup = null;
        CarBizCarGroup carBizCarGroup = null;
        CarBizCarInfo carBizCarInfo = null;
        CarBizModel carBizModel = null;
        CarBizDriverInfoDTO dto = null;
        DriverComplianceDTO driverComplianceDTO = null;
        for(CarBizDriverInfoDTO carBizDriverInfo:carBizDriverInfoList){

            driverId = carBizDriverInfo.getDriverId();

            driverComplianceDTO = driverComplianceDTOMap.get("t_" + driverId);
            if(driverComplianceDTO != null){
                carBizDriverInfo.setExt2(driverComplianceDTO.getComplianceStatus());
                carBizDriverInfo.setExt3(driverComplianceDTO.getComplianceKind());
            }


            carBizSupplier = supplierMap.get("t_"+carBizDriverInfo.getSupplierId());

            if (carBizSupplier != null) {
                carBizDriverInfo.setSupplierName(carBizSupplier.getSupplierFullName());
                carBizDriverInfo.setCooperationType(carBizSupplier.getCooperationType());
                if(carBizDriverInfo.getCooperationType() != null){
                    if(cooperationTypeMap.get("t_"+carBizDriverInfo.getCooperationType()) != null){
                        carBizDriverInfo.setCooperationTypeName(cooperationTypeMap.get("t_"+carBizDriverInfo.getCooperationType()).getCooperationName());
                    }
                }
            }
            //??????????????????
            carBizCity = cityMap.get(carBizDriverInfo.getServiceCity());
            if (carBizCity != null) {
                carBizDriverInfo.setCityName(carBizCity.getCityName());
            }
            //?????????????????????
            if(carBizDriverInfo.getCreateBy()!=null){
                  carAdmUser = createByMap.get("t_"+carBizDriverInfo.getCreateBy());
                if(carAdmUser!=null){
                    carBizDriverInfo.setCreateName(carAdmUser.getUserName());
                }
            }
            //?????????????????????
            if(carBizDriverInfo.getUpdateBy()!=null){
                  carAdmUser = updateByMap.get("t_"+carBizDriverInfo.getUpdateBy());
                if(carAdmUser!=null){
                    carBizDriverInfo.setUpdateName(carAdmUser.getUserName());
                }
            }

            //???????????????????????????id
            teamId = driverTeamMap.get("t_"+driverId);
            if( teamId != null){
                carBizDriverInfo.setTeamId(teamId);
                carDriverTeam =  teamMap.get("t_"+teamId);
                if(carDriverTeam != null){
                    carBizDriverInfo.setTeamName(carDriverTeam.getTeamName());
                }
            }

            //????????????????????????????????????id
            groupId = driverGroupMap.get("t_"+driverId);
            if( groupId != null){
                carBizDriverInfo.setTeamGroupId(groupId);
                driverTeamGroup =  teamGroupMap.get("t_"+groupId);
                if(driverTeamGroup != null){
                    carBizDriverInfo.setTeamGroupName(driverTeamGroup.getTeamName());
                }
            }
            //????????????
            carBizCarGroup = groupMap.get("t_"+carBizDriverInfo.getGroupId());
            if(carBizCarGroup != null){
                carBizDriverInfo.setCarGroupName(carBizCarGroup.getGroupName());
            }

            if(StringUtils.isNotEmpty(carBizDriverInfo.getLicensePlates())){
                carBizCarInfo = carBizCarInfoMap.get(carBizDriverInfo.getLicensePlates());
                if(carBizCarInfo != null){
                    carBizModel = carBizModelMap.get("t_"+carBizCarInfo.getCarModelId());
                    if(carBizModel != null){
                        carBizDriverInfo.setModelId(carBizModel.getModelId());
                        carBizDriverInfo.setModelName(carBizModel.getModelName());
                    }

                }
            }



            if(exportStringList != null){
                dto = carBizDriverInfo;

                StringBuilder builder = new StringBuilder();
                builder.append(dto.getLicensePlates()!=null?""+dto.getLicensePlates()+"":"").append(",");
                builder.append(dto.getName()!=null?""+dto.getName()+"":"").append(",");
                builder.append(dto.getIdCardNo()!=null?"\t"+dto.getIdCardNo()+"":"").append(",");
                builder.append(dto.getPhone()!=null?"\t"+dto.getPhone()+"":"").append(",");
                builder.append(dto.getPhonetype()!=null?""+dto.getPhonetype()+"":"").append(",");
                builder.append(dto.getPhonecorp()!=null?""+dto.getPhonecorp()+"":"").append(",");
                builder.append(dto.getGender()!=null?""+(dto.getGender()==1?"???":"???"+""):"").append(",");
                builder.append(dto.getBirthDay()!=null?"\t"+dto.getBirthDay()+"":"").append(",");
                builder.append(dto.getAge()!=null?""+dto.getAge()+"":"").append(",");
                builder.append(dto.getSuperintendNo()!=null?"\t"+dto.getSuperintendNo()+"":"").append(",");
                builder.append(dto.getSuperintendUrl()!=null?""+dto.getSuperintendUrl()+"":"").append(",");

                builder.append(StringUtils.isEmpty(dto.getCarGroupName())?"":dto.getCarGroupName()).append(",");
                // ????????????
                builder.append(dto.getDrivingLicenseType()!=null?""+dto.getDrivingLicenseType()+"":"").append(",");
                builder.append("\t").append(DateUtil.getTimeString(dto.getIssueDate())).append(",");
                builder.append(dto.getDrivingYears()!=null?""+dto.getDrivingYears()+"":"").append(",");
                builder.append("\t").append(DateUtil.getTimeString(dto.getExpireDate())).append(",");
                builder.append("\t").append(dto.getArchivesNo()!=null?""+dto.getArchivesNo()+"":"").append(",");
                builder.append(dto.getNationality()!=null?""+dto.getNationality()+"":"").append(",");
                builder.append(dto.getNation()!=null?""+dto.getNation()+"":"").append(",");
                builder.append(dto.getMarriage()!=null?""+dto.getMarriage()+"":"").append(",");
                // ?????????????????????
                String foreignlanguageName= "???";
                String foreignLanguage = dto.getForeignlanguage();
                if(StringUtils.isNotEmpty(foreignLanguage)){
                    if("1".equals(foreignLanguage)){
                        foreignlanguageName = "??????";
                    }else if("2".equals(foreignLanguage)){
                        foreignlanguageName = "??????";
                    }else if("3".equals(foreignLanguage)){
                        foreignlanguageName = "??????";
                    }else if("4".equals(foreignLanguage)){
                        foreignlanguageName = "??????";
                    }
                }
                builder.append(foreignlanguageName).append(",");
                builder.append(dto.getEducation()!=null?""+dto.getEducation()+"":"").append(",");
                builder.append(dto.getHouseHoldRegisterPermanent()!=null?""+dto.getHouseHoldRegisterPermanent()+"":"").append(",");
                builder.append(dto.getHouseholdregister()!=null?""+dto.getHouseholdregister()+"":"").append(",");
                builder.append(dto.getCurrentAddress()!=null?""+dto.getCurrentAddress()+"":"").append(",");
                builder.append(dto.getPhotosrct()!=null?""+dto.getPhotosrct()+"":"").append(",");//???????????????????????????
                builder.append(dto.getDriverlicensenumber()!=null?"\t"+dto.getDriverlicensenumber()+"":"").append(",");
                builder.append(dto.getDrivinglicenseimg()!=null?""+dto.getDrivinglicenseimg()+"":"").append(",");//???????????????????????????????????????
                builder.append(dto.getFirstdrivinglicensedate()!=null?"\t"+dto.getFirstdrivinglicensedate()+"":"").append(",");//????????????????????????
                builder.append(dto.getIsxydriver()!=null?""+(dto.getIsxydriver()==1?"???":"???"+""):"").append(",");
                builder.append(dto.getDriverlicenseissuingnumber()!=null?"\t"+dto.getDriverlicenseissuingnumber()+"":"").append(",");
                builder.append(dto.getFirstmeshworkdrivinglicensedate()!=null?"\t"+dto.getFirstmeshworkdrivinglicensedate()+"":"").append(",");
                builder.append(dto.getXyDriverNumber()!=null?"\t"+dto.getXyDriverNumber()+"":"").append(",");
                builder.append(dto.getDriverlicenseissuingcorp()!=null?""+dto.getDriverlicenseissuingcorp()+"":"").append(",");
                builder.append(dto.getDriverLicenseIssuingGrantDate()!=null?"\t"+dto.getDriverLicenseIssuingGrantDate()+"":"").append(",");
                builder.append(dto.getDriverLicenseIssuingFirstDate()!=null?"\t"+dto.getDriverLicenseIssuingFirstDate()+"":"").append(",");
                builder.append(dto.getDriverlicenseissuingdatestart()!=null?"\t"+dto.getDriverlicenseissuingdatestart()+"":"").append(",");
                builder.append(dto.getDriverlicenseissuingdateend()!=null?"\t"+dto.getDriverlicenseissuingdateend()+"":"").append(",");
                builder.append(dto.getDriverLicenseIssuingRegisterDate()!=null?"\t"+dto.getDriverLicenseIssuingRegisterDate()+"":"").append(",");
                builder.append(dto.getParttimejobdri()!=null?""+dto.getParttimejobdri()+"":"").append(",");
                builder.append(dto.getCorptype()!=null?""+dto.getCorptype()+"":"").append(",");
                builder.append(dto.getContractdate()!=null?"\t"+dto.getContractdate()+"":"").append(",");
                builder.append(dto.getSigndate()!=null?"\t"+dto.getSigndate()+"":"").append(",");
                builder.append(dto.getSigndateend()!=null?"\t"+dto.getSigndateend()+"":"").append(",");
                builder.append(dto.getEmergencyContactPerson()!=null?""+dto.getEmergencyContactPerson()+"":"").append(",");
                builder.append(dto.getEmergencyContactNumber()!=null?"\t"+dto.getEmergencyContactNumber()+"":"").append(",");
                builder.append(dto.getEmergencycontactaddr()!=null?""+dto.getEmergencycontactaddr()+"":"").append(",");
                builder.append(dto.getSupplierName() == null?"":dto.getSupplierName()).append(",");
                builder.append(dto.getCityName() == null?"":dto.getCityName()).append(",");
                //??????

                builder.append(dto.getTeamName()==null?"":dto.getTeamName()).append(",");
                //??????
                builder.append(dto.getTeamGroupName()==null?"":dto.getTeamGroupName()).append(",");
                builder.append(dto.getDriverId()!=null?"\t"+dto.getDriverId()+"":"").append(",");
                builder.append("\t").append(DateUtil.getTimeString(dto.getCreateDate())).append(",");

                //??????????????????
                String complianceKind = "";//???????????????  ??????ext3  ????????????
                String complianceStatus = ""; // ??????ext2  ????????????
                Integer ext2 = dto.getExt2();
                Integer ext3 = dto.getExt3();
                //??????????????????1234???????????????==??????  1:???????????? 2:???????????? 3:???????????? 4:?????????
                if(ext2 != null){
                    if(1 == ext2){
                        complianceStatus = "??????";
                    }else if(0 == ext2){
                        complianceStatus = "?????????";
                    }
                }

                if(ext3 != null){
                    if(1 == ext3){
                        complianceKind = "????????????";
                    }else if(2 == ext3){
                        complianceKind = "????????????";
                    }else if(3 == ext3){
                        complianceKind = "????????????";
                    }else if(4 == ext3){
                        complianceKind = "?????????";
                    }
                }
                builder.append(complianceStatus).append(",");
                builder.append(complianceKind);


                exportStringList.add(builder.toString());
            }

        }

    }
    
	/**
	 * @Title: updateDriverCooperationTypeBySupplierId
	 * @Description: ???????????????????????????
	 * @param supplierId
	 * @param cooperationType
	 * @return void
	 * @throws
	 */
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.MASTER))
	public void updateDriverCooperationTypeBySupplierId(Integer supplierId, Integer cooperationType){
		// ??????mongo
		driverMongoService.updateDriverCooperationTypeBySupplierId(supplierId, cooperationType);

		// ???????????????
		Map<String, Object> map = new HashMap<>();
		map.put("supplierId", supplierId);
		map.put("cooperationType", cooperationType);
		carBizDriverInfoExMapper.updateDriverCooperationTypeBySupplierId(map);
	}


    public CarBizDriverInfoDTO addTelescopeDriver(TelescopeDriver telescopeDriver){
        boolean result = false;
        String initPwd = String.valueOf((int)((Math.random()*9+1)*100000));
        CarBizDriverInfoDTO carBizDriverInfoDTO = carBizDriverInfoExMapper.selectByPhone(telescopeDriver.getPhone());
        if(null != carBizDriverInfoDTO){
            return carBizDriverInfoDTO;
        }
        CarBizDriverInfo carBizDriverInfo = new CarBizDriverInfo();
        CarBizSupplier param = new CarBizSupplier();
        param.setSupplierCity(telescopeDriver.getCityId());
        CarBizCity carBizCity = carBizCityService.selectByPrimaryKey(telescopeDriver.getCityId());
        CarBizSupplier carBizSupplier = carBizSupplierService.queryQianLiYanSupplierByCityId(param);
        if(null == carBizSupplier){
            carBizSupplier = new CarBizSupplier();
            carBizSupplier.setSupplierCity(telescopeDriver.getCityId());
            carBizSupplier.setSupplierNum("qianliyan");
            carBizSupplier.setType(1);
            carBizSupplier.setAddress("");
            carBizSupplier.setContacts("?????????");
            carBizSupplier.setContactsPhone("18611165319");
            carBizSupplier.setCooperationType(5);
            carBizSupplier.setCreateBy(WebSessionUtil.getCurrentLoginUser().getId());
            carBizSupplier.setCreateDate(new Date());
            carBizSupplier.setEnterpriseType(2);
            carBizSupplier.setIscommission(2);
            carBizSupplier.setIstest(0);
            carBizSupplier.setPospayflag(0);
            carBizSupplier.setStatus(1);
            carBizSupplier.setSupplierFullName("????????????????????????"+carBizCity.getCityName()+")");
            carBizSupplierMapper.insertSelective(carBizSupplier);
            //MQ???????????? ?????????
            try {
                String method = "CREATE";
                Map<String, Object> messageMap = new HashMap<String, Object>();
                messageMap.put("method",method);
                JSONObject json = JSONObject.fromObject(carBizSupplier);
                messageMap.put("data", json);
                String messageStr = JSONObject.fromObject(messageMap).toString();
                logger.info("???????????????????????????????????????" + messageStr);
                CommonRocketProducer.publishMessage("vipSupplierTopic", method, String.valueOf(carBizSupplier.getSupplierId()), messageMap);
                String envName = EnvUtils.ENVIMENT;
                if (Objects.nonNull(envName) && Arrays.asList(new String[]{"online","prod"}).contains(envName)){
                    CommonRocketProducerDouble.publishMessage("vipSupplierTopic", method, String.valueOf(carBizSupplier.getSupplierId()), messageMap);
                }
            } catch (Exception e) {
                logger.error("??????????????????????????????"+carBizSupplier.getSupplierFullName()+"??????????????????????????????",e);
            }
        }
        carBizDriverInfo.setServiceCity(carBizSupplier.getSupplierCity());
        carBizDriverInfo.setSupplierId(carBizSupplier.getSupplierId());
        carBizDriverInfo.setCooperationType(Byte.valueOf(carBizSupplier.getCooperationType().toString()));
        carBizDriverInfo.setPhone(telescopeDriver.getPhone());
        carBizDriverInfo.setName(telescopeDriver.getName());
        carBizDriverInfo.setStatus(1);
        carBizDriverInfo.setPassword(Md5Util.md5(initPwd));
        carBizDriverInfo.setGroupId(34);
        carBizDriverInfo.setCreateBy(WebSessionUtil.getCurrentLoginUser().getId());
        carBizDriverInfo.setCreateDate(new Date());
        carBizDriverInfo.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
        carBizDriverInfo.setUpdateDate(new Date());
        int insertResult = carBizDriverInfoMapper.insertSelective(carBizDriverInfo);
        if(insertResult>0){
            CarBizDriverInfoDTO driverInfoDTO = new CarBizDriverInfoDTO();
            driverInfoDTO.setDriverId(carBizDriverInfo.getDriverId());
            driverInfoDTO.setServiceCity(carBizSupplier.getSupplierCity());
            driverInfoDTO.setSupplierId(carBizSupplier.getSupplierId());
            driverInfoDTO.setPhone(telescopeDriver.getPhone());
            driverInfoDTO.setName(telescopeDriver.getName());
            driverInfoDTO.setStatus(1);
            driverInfoDTO.setPassword(Md5Util.md5(initPwd));
            driverInfoDTO.setCreateBy(WebSessionUtil.getCurrentLoginUser().getId());
            driverInfoDTO.setCreateDate(new Date());
            driverInfoDTO.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
            driverInfoDTO.setUpdateDate(new Date());
            driverInfoDTO.setCooperationType(carBizSupplier.getCooperationType());
            driverInfoDTO.setGroupId(34);
            //??????MQ
            sendDriverToMq(driverInfoDTO, "INSERT");
            // ??????mongoDB
            DriverMongo driverMongo = driverMongoService.findByDriverId(carBizDriverInfo.getDriverId());
            if (driverMongo != null) {
                driverMongoService.updateDriverMongo(driverInfoDTO);
            } else {
                driverMongoService.saveDriverMongo(driverInfoDTO);
            }
            try {
                carBizChatUserService.insertChat(driverInfoDTO.getDriverId());
            } catch (Exception e) {
                logger.error("?????????????????????carBizChatUserService.insertChat?????????",e);
            }
            try{
                //????????????

                List list = new ArrayList();
                list.add(telescopeDriver.getName());
                list.add(telescopeDriver.getPhone());
                list.add(initPwd);
                logger.info("?????????????????????:" + com.alibaba.fastjson.JSONObject.toJSON(list));
                SmsSendUtil.sendTemplate(telescopeDriver.getPhone(), SmsTempleConstants.eyeRegisterTemple,list);
            }catch (Exception e){
                logger.error("??????????????????????????????????????????",e);
            }
            return driverInfoDTO;
        }
        return null;
    }

    public CarBizDriverInfoDTO querySupplierIdAndNameByDriverId(Integer driverId){
	    return carBizDriverInfoExMapper.querySupplierIdAndNameByDriverId(driverId);
    }

    public List<CarBizDriverInfoDTO> queryCarBizDriverListBySupplierId(Integer supplierId){
        return carBizDriverInfoExMapper.queryCarBizDriverListBySupplierId(supplierId);
    }

}
