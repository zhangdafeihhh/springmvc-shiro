package com.zhuanche.serv;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.rocketmq.CommonRocketProducer;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.dto.rentcar.CarBizCarInfoDTO;
import com.zhuanche.dto.rentcar.CarBizDriverInfoDTO;
import com.zhuanche.entity.rentcar.*;
import com.zhuanche.mongo.DriverMongo;
import com.zhuanche.serv.mdbcaranage.CarBizDriverUpdateService;
import com.zhuanche.serv.mongo.DriverMongoService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.Common;
import com.zhuanche.util.ValidateUtils;
import com.zhuanche.util.encrypt.MD5Utils;
import mapper.rentcar.CarBizDriverAccountMapper;
import mapper.rentcar.CarBizDriverInfoMapper;
import mapper.rentcar.ex.CarBizCarInfoExMapper;
import mapper.rentcar.ex.CarBizDriverInfoExMapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CarBizDriverInfoService {

    private static final Logger logger = LoggerFactory.getLogger(CarBizDriverInfoService.class);
    private static final String LOGTAG = "[司机信息]: ";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

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
    private DriverMongoService driverMongoService;

    /**
     * 查询司机信息列表展示(有分页)
     *
     * @param params
     * @return
     */
    public List<CarBizDriverInfoDTO> queryDriverList(CarBizDriverInfoDTO params) {
        return carBizDriverInfoExMapper.queryDriverList(params);
    }

    /**
     * 查询司机信息列表展示(无分页)
     *
     * @param params
     * @return
     */
    public List<CarBizDriverInfoDTO> queryDriverListNoLimit(CarBizDriverInfoDTO params) {
        return carBizDriverInfoExMapper.queryDriverListNoLimit(params);
    }

    /**
     * 查询手机号是否存在
     *
     * @param phone    手机号，必传
     * @param driverId 司机ID，可不传
     * @return
     */
    public Boolean checkPhone(String phone, Integer driverId) {
        int count = carBizDriverInfoExMapper.checkPhone(phone, driverId);
        if (count > 0) {
            return false;
        }
        return true;
    }

    /**
     * 查询身份证是否存在
     *
     * @param idCardNo 身份证号
     * @param driverId 司机ID，可不传
     * @return
     */
    public Boolean checkIdCardNo(String idCardNo, Integer driverId) {
        int count = carBizDriverInfoExMapper.checkIdCardNo(idCardNo, driverId);
        if (count > 0) {
            return false;
        }
        return true;
    }

    /**
     * 根据司机ID查询信息
     *
     * @param driverId
     * @return
     */
    public CarBizDriverInfo selectByPrimaryKey(Integer driverId) {
        return carBizDriverInfoMapper.selectByPrimaryKey(driverId);
    }

    /**
     * 重置imei
     *
     * @param driverId
     * @return
     */
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.MASTER )
    } )
    public int resetIMEI(Integer driverId) {
        return carBizDriverInfoExMapper.resetIMEI(driverId);
    }

    /**
     * 修改司机信息
     *
     * @param carBizDriverInfo
     * @return
     */
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE),
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.MASTER)
    })
    public Map<String, Object> updateDriver(CarBizDriverInfoDTO carBizDriverInfo) {
        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            try {
                logger.info(LOGTAG + "操作方式：编辑,新数据:" + JSON.toJSONString(carBizDriverInfo));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            // 查询城市名称，供应商名称，服务类型，加盟类型
            carBizDriverInfo = this.getBaseStatis(carBizDriverInfo);
            //TODO 驾驶员合同（或协议）签署公司在协议公司 验证
//            if(cooperationType!=null&&cooperationType==5){
//                int count = this.queryAgreementCompanyByName(driver.getCorpType());
//                if(count==0){
//                    result.put("result", 1);
//                    result.put("msg", " 驾驶员合同（或协议）签署公司在协议公司中不存在");
//                    return result;
//                }
//            }

            // 获取当前用户Id
            carBizDriverInfo.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
            carBizDriverInfo.setUpdateDate(new Date());
            //
            String idCardNo = carBizDriverInfo.getIdCardNo();
            if ("X".equals(idCardNo.substring(idCardNo.length() - 1, idCardNo.length()))) {
                idCardNo = idCardNo.toLowerCase();
            }
            carBizDriverInfo.setIdCardNo(idCardNo);
            carBizDriverInfo.setDriverlicensenumber(idCardNo);//机动车驾驶证号

            if (carBizDriverInfo.getPasswordReset() == 1) {//重置密码
                carBizDriverInfo.setPassword(getPassword(idCardNo));
            }

            //根据司机ID查询数据
            CarBizDriverInfo orginDriverInfo = carBizDriverInfoMapper.selectByPrimaryKey(carBizDriverInfo.getDriverId());
            if (orginDriverInfo != null) {
                carBizDriverInfo.setOldPhone(orginDriverInfo.getPhone());//手机号
                carBizDriverInfo.setOldIdCardNo(orginDriverInfo.getIdCardNo());//身份证
                carBizDriverInfo.setOldDriverLicenseNumber(orginDriverInfo.getDriverlicensenumber());//机动车驾驶证号
                carBizDriverInfo.setOldDriverLicenseIssuingNumber(orginDriverInfo.getDriverlicenseissuingnumber());//网络预约出租汽车驾驶员资格证号
                carBizDriverInfo.setOldLicensePlates(orginDriverInfo.getLicensePlates());//车牌号
                carBizDriverInfo.setOldCity(orginDriverInfo.getServiceCity());//城市
                carBizDriverInfo.setOldSupplier(orginDriverInfo.getSupplierId());//供应商
            }
            try {
                logger.info(LOGTAG + "操作方式：编辑,原始数据:" + JSON.toJSONString(carBizDriverInfo));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            carBizDriverInfo.setUpdateDate(new Date());
            //更新司机信息
            int n = this.updateDriverInfo(carBizDriverInfo);

            // 更新车辆信息 根据 车牌号更新车辆 信息（更换车辆所属人）
            if (n > 0) {
                logger.info("****************根据 车牌号更新车辆 信息（更换车辆所属人）");
//                if(carInfo!=null){
                carBizCarInfoExMapper.updateCarLicensePlates(carBizDriverInfo.getLicensePlates(), carBizDriverInfo.getDriverId());
//                }
            }

            // 更新mongoDB
            DriverMongo driverMongo = driverMongoService.findByDriverId(carBizDriverInfo.getDriverId());
            if (driverMongo != null) {
                driverMongoService.updateDriverMongo(carBizDriverInfo);
            } else {
                driverMongoService.saveDriverMongo(carBizDriverInfo);
            }

            String method = "UPDATE";
            // 将司机置为无效状态需释放车辆资源
            if (carBizDriverInfo.getStatus().intValue() == 0) {
                method = "DELETE";
                this.updateDriverByXiao(carBizDriverInfo);
            }

            //城市或者供应商是否更换
            if ((carBizDriverInfo.getOldCity() != null && !carBizDriverInfo.getOldCity().equals(carBizDriverInfo.getServiceCity()))
                    || (carBizDriverInfo.getOldSupplier() != null && !carBizDriverInfo.getOldSupplier().equals(carBizDriverInfo.getSupplierId()))) {
                logger.info("修改司机driverId=" + carBizDriverInfo.getDriverId() + "的城市或者供应商，需将司机移除车队小组");
                //TODO 移除司机车队小组信息
//                DriverTeamRelationEntity relation = new DriverTeamRelationEntity();
//                relation.setDriverId(carBizDriverInfoDTO.getDriverId());
//                driverTeamRelationService.deleteTeamAndGroupByDriverId(relation);
                carBizDriverInfo.setTeamId(null);
                carBizDriverInfo.setTeamName("");
                carBizDriverInfo.setTeamGroupId(null);
                carBizDriverInfo.setTeamGroupName("");
            }

            carBizDriverInfo.setCreateDate(orginDriverInfo.getCreateDate());
            //发送MQ
            if (carBizDriverInfo.getStatus() == 0) {
                sendDriverToMq(carBizDriverInfo, "DELETE");
            } else {
                sendDriverToMq(carBizDriverInfo, "UPDATE");
            }

            try {
                // 司机变更部分信息，需要记录
                driverUpdate(carBizDriverInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
            resultMap.put("result", 0);
            resultMap.put("msg", "成功");
        } catch (Exception e) {
            resultMap.put("result", 1);
            resultMap.put("msg", e.getMessage());
            e.printStackTrace();
        }
        return resultMap;
    }

    /**
     * 保存司机信息
     *
     * @param carBizDriverInfo
     * @return
     */
    public Map<String, Object> saveDriver(CarBizDriverInfoDTO carBizDriverInfo) {
        Map<String, Object> resultMap = Maps.newHashMap();

        try {
            logger.info(LOGTAG + "操作方式：新建,数据:" + JSON.toJSONString(carBizDriverInfo));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            // 查询城市名称，供应商名称，服务类型，加盟类型
            carBizDriverInfo = this.getBaseStatis(carBizDriverInfo);
            //TODO 驾驶员合同（或协议）签署公司在协议公司 验证
//            if(cooperationType!=null&&cooperationType==5){
//                int count = this.queryAgreementCompanyByName(driver.getCorpType());
//                if(count==0){
//                    result.put("result", 1);
//                    result.put("msg", " 驾驶员合同（或协议）签署公司在协议公司中不存在");
//                    return result;
//                }
//            }

            // 获取当前用户Id
            carBizDriverInfo.setCreateBy(WebSessionUtil.getCurrentLoginUser().getId());
            carBizDriverInfo.setCreateDate(new Date());
            carBizDriverInfo.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
            carBizDriverInfo.setUpdateDate(new Date());
            carBizDriverInfo.setStatus(1);
            //
            String idCardNo = carBizDriverInfo.getIdCardNo();
            if ("X".equals(idCardNo.substring(idCardNo.length() - 1, idCardNo.length()))) {
                idCardNo = idCardNo.toLowerCase();
            }
            carBizDriverInfo.setIdCardNo(idCardNo);
            carBizDriverInfo.setDriverlicensenumber(idCardNo);//机动车驾驶证号
            carBizDriverInfo.setPassword(getPassword(carBizDriverInfo.getIdCardNo()));

            // 插入司机信息到mysql，mongo
            int n = this.saveDriverInfo(carBizDriverInfo);
            driverMongoService.saveDriverMongo(carBizDriverInfo);

            if (n > 0) {
                // 根据 车牌号更新车辆 信息（更换车辆所属人）
//                if(carBizCarInfo!=null){
                carBizCarInfoExMapper.updateCarLicensePlates(carBizDriverInfo.getLicensePlates(), carBizDriverInfo.getDriverId());
//                }
            }
            carBizChatUserService.insertChat(carBizDriverInfo.getDriverId());

            //TODO teamId teamGroupId 存在，则新增车队与司机的关联表


            //发送MQ
            sendDriverToMq(carBizDriverInfo, "INSERT");

            resultMap.put("result", 0);
            resultMap.put("msg", "成功");
        } catch (Exception e) {
            resultMap.put("result", 1);
            resultMap.put("msg", e.getMessage());
            e.printStackTrace();
        }
        return resultMap;
    }

    /**
     * 新增司机信息
     *
     * @param carBizDriverInfoDTO
     * @return
     */
    public int saveDriverInfo(CarBizDriverInfoDTO carBizDriverInfoDTO) {
        if (carBizDriverInfoDTO.getExpireDate().equals("")) {
            carBizDriverInfoDTO.setExpireDate(null);
        }
        if (carBizDriverInfoDTO.getIssueDate().equals("")) {
            carBizDriverInfoDTO.setIssueDate(null);
        }
        carBizDriverInfoExMapper.insertCarBizDriverInfoDTO(carBizDriverInfoDTO);
        int driverId = carBizDriverInfoDTO.getDriverId();

        //司机信息扩展表，司机银行卡号
        CarBizDriverInfoDetail carBizDriverInfoDetail = new CarBizDriverInfoDetail();
        carBizDriverInfoDetail.setBankCardBank(carBizDriverInfoDTO.getBankCardBank());
        carBizDriverInfoDetail.setBankCardNumber(carBizDriverInfoDTO.getBankCardNumber());
        carBizDriverInfoDetail.setDriverId(carBizDriverInfoDTO.getDriverId());
        carBizDriverInfoDetail.setExt1(2);//司机停运状态  1停运 2正常  司机新建是默认为2
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
     * 修改司机信息，操作司机信息扩展表
     *
     * @param carBizDriverInfoDTO
     * @return
     */
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.MASTER)
    })
    public int updateDriverInfo(CarBizDriverInfoDTO carBizDriverInfoDTO) {
        if (carBizDriverInfoDTO.getExpireDate() != null) {
            if (carBizDriverInfoDTO.getExpireDate().equals("")) {
                carBizDriverInfoDTO.setExpireDate(null);
            }
        }
        if (carBizDriverInfoDTO.getIssueDate() != null) {
            if (carBizDriverInfoDTO.getIssueDate().equals("")) {
                carBizDriverInfoDTO.setIssueDate(null);
            }
        }
        carBizDriverInfoExMapper.updateCarBizDriverInfoDTO(carBizDriverInfoDTO);
        int id = carBizDriverInfoDTO.getDriverId();

        //司机信息扩展表，司机银行卡号
        CarBizDriverInfoDetail infoDetail = carBizDriverInfoDetailService.selectByPrimaryKey(carBizDriverInfoDTO.getDriverId());
        CarBizDriverInfoDetail carBizDriverInfoDetail = new CarBizDriverInfoDetail();
        carBizDriverInfoDetail.setBankCardBank(carBizDriverInfoDTO.getBankCardBank());
        carBizDriverInfoDetail.setBankCardNumber(carBizDriverInfoDTO.getBankCardNumber());
        carBizDriverInfoDetail.setDriverId(carBizDriverInfoDTO.getDriverId());
        if (infoDetail != null) {
            carBizDriverInfoDetailService.updateByPrimaryKeySelective(carBizDriverInfoDetail);
        } else {
            carBizDriverInfoDetail.setExt1(2);//司机停运状态  1停运 2正常  司机新建是默认为2
            carBizDriverInfoDetailService.insertSelective(carBizDriverInfoDetail);
        }
        return id;
    }

    /**
     * 更新司机状态，如果将司机置为无效则释放车辆资源
     *
     * @param carBizDriverInfoDTO
     * @return
     */
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.MASTER)
    })
    public int updateDriverByXiao(CarBizDriverInfoDTO carBizDriverInfoDTO) {
        int rtn = 0;
        try {
            rtn = carBizDriverInfoExMapper.updateDriverByXiao(carBizDriverInfoDTO.getDriverId());
            if (rtn > 0) {
                // 将司机置为无效
                if (carBizDriverInfoDTO.getStatus().intValue() == 0) {
                    //根据车牌号更新车辆信息
                    carBizCarInfoExMapper.updateCarLicensePlates(carBizDriverInfoDTO.getLicensePlates(), 0);
                }
                //更新司机mongo
                driverMongoService.updateByDriverId(carBizDriverInfoDTO.getDriverId(), carBizDriverInfoDTO.getStatus());

                try {
                    carBizDriverUpdateService.insert(carBizDriverInfoDTO.getLicensePlates(), "", carBizDriverInfoDTO.getDriverId(), 1);
                    carBizDriverUpdateService.insert(carBizDriverInfoDTO.getPhone(), "", carBizDriverInfoDTO.getDriverId(), 2);
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
     * 发送MQ
     *
     * @param carBizDriverInfoDTO
     * @param method              tag: INSERT UPDATE DELETE
     */
    public void sendDriverToMq(CarBizDriverInfoDTO carBizDriverInfoDTO, String method) {
        //MQ消息写入
        try {
            Map<String, Object> messageMap = new HashMap<String, Object>();
            messageMap.put("carNumber", carBizDriverInfoDTO.getLicensePlates());//车牌号
            messageMap.put("city", carBizDriverInfoDTO.getCityName()); //城市名称
            messageMap.put("cityId", carBizDriverInfoDTO.getServiceCity()); //城市ID
            messageMap.put("createBy", carBizDriverInfoDTO.getUpdateBy() == null ? "1" : carBizDriverInfoDTO.getUpdateBy()); //操作人
            messageMap.put("driverId", carBizDriverInfoDTO.getDriverId()); //司机ID
            messageMap.put("driverName", carBizDriverInfoDTO.getName()); //司机姓名
            messageMap.put("driverPhone", carBizDriverInfoDTO.getPhone() == null ? "" : carBizDriverInfoDTO.getPhone()); //司机手机号
            messageMap.put("status", carBizDriverInfoDTO.getStatus()); //司机状态
            messageMap.put("supplierFullName", carBizDriverInfoDTO.getSupplierName()); //司机供应商名称
            messageMap.put("supplierId", carBizDriverInfoDTO.getSupplierId()); //司机供应商
            messageMap.put("cooperationType", carBizDriverInfoDTO.getCooperationType()); //司机加盟类型
            messageMap.put("groupId", carBizDriverInfoDTO.getGroupId()); //司机服务类型ID
            messageMap.put("create_date", carBizDriverInfoDTO.getCreateDate()); //司机创建时间
            messageMap.put("carType", carBizDriverInfoDTO.getCarGroupName() == null ? "" : carBizDriverInfoDTO.getCarGroupName()); //司机服务类型
            messageMap.put("teamId", carBizDriverInfoDTO.getTeamId() == null ? "" : carBizDriverInfoDTO.getTeamId()); //司机所属车队ID
            messageMap.put("teamName", carBizDriverInfoDTO.getTeamName() == null ? "" : carBizDriverInfoDTO.getTeamName()); //司机所属车队名称
            messageMap.put("teamGroupId", carBizDriverInfoDTO.getTeamGroupId() == null ? "" : carBizDriverInfoDTO.getTeamName()); //司机所属小组ID
            messageMap.put("teamGroupName", carBizDriverInfoDTO.getTeamGroupName() == null ? "" : carBizDriverInfoDTO.getTeamGroupName()); //司机所属小组名称

            String messageStr = JSONObject.fromObject(messageMap).toString();
            logger.info("专车司机driverId={}，同步发送数据={}", carBizDriverInfoDTO.getDriverId(), messageStr);
            CommonRocketProducer.publishMessage("driver_info", method, String.valueOf(carBizDriverInfoDTO.getDriverId()), messageMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据身份证获取密码
     *
     * @param idCarNo
     * @return
     */
    public String getPassword(String idCarNo) {
        String parrword = "";
        try {
            //司机密码
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
     * 判断一些基础信息是否正确
     *
     * @param carBizDriverInfo
     * @return
     */
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE)
    })
    public AjaxResponse validateCarDriverInfo(CarBizDriverInfoDTO carBizDriverInfo) {
        //手机号是否合法
        String phone = carBizDriverInfo.getPhone();
        if (StringUtils.isEmpty(phone) || !ValidateUtils.validatePhone(phone)) {
            return AjaxResponse.fail(RestErrorCode.DRIVER_PHONE_NOT_LEGAL);
        }
        //身份证是否合法，大写X一律改为小写的x
        String idCardNo = carBizDriverInfo.getIdCardNo();
        if ("X".equals(idCardNo.substring(idCardNo.length() - 1, idCardNo.length()))) {
            idCardNo = idCardNo.toLowerCase();
        }
        if (StringUtils.isEmpty(idCardNo) || !ValidateUtils.validateIdCarNo(idCardNo)) {
            return AjaxResponse.fail(RestErrorCode.DRIVER_IDCARNO_NOT_LEGAL);
        }
        //银行卡号位16到18位数字，银行开户行，二者都填，或都不填
        String bankCardNumber = carBizDriverInfo.getBankCardNumber();
        String bankCardBank = carBizDriverInfo.getBankCardBank();
        if ((StringUtils.isNotEmpty(bankCardNumber) || StringUtils.isEmpty(bankCardBank)) || (StringUtils.isEmpty(bankCardNumber) || StringUtils.isNotEmpty(bankCardBank))) {
            return AjaxResponse.fail(RestErrorCode.DRIVER_BANK_CARD_NUMBER_NOT_COMPLETE);
        }
        if (StringUtils.isNotEmpty(bankCardNumber) && StringUtils.isNotEmpty(bankCardBank) && !ValidateUtils.isRegular(bankCardNumber, ValidateUtils.BANK_CARD_NUMBER)) {
            return AjaxResponse.fail(RestErrorCode.DRIVER_BANK_CARD_NUMBER_NOT_LEGAL);
        }
        //查询手机号是否存在
        Integer driverId = carBizDriverInfo.getDriverId();
        Boolean had = this.checkPhone(phone, driverId);
        if (had) {
            return AjaxResponse.fail(RestErrorCode.DRIVER_PHONE_EXIST);
        }
        //查询身份证号是否存在
        had = this.checkIdCardNo(idCardNo, driverId);
        if (had) {
            return AjaxResponse.fail(RestErrorCode.DRIVER_IDCARNO_EXIST);
        }
        //查询银行卡号是否存在
        had = carBizDriverInfoDetailService.checkBankCardBank(bankCardNumber, driverId);
        if (had) {
            return AjaxResponse.fail(RestErrorCode.DRIVER_BANK_CARD_NUMBER_EXIST);
        }
        return AjaxResponse.success(true);
    }

    public void driverUpdate(CarBizDriverInfoDTO carBizDriverInfo) {
        try {
            //判断 车牌号是否修改 如果修改 释放 车牌号
            if (StringUtils.isNotEmpty(carBizDriverInfo.getOldLicensePlates()) && !carBizDriverInfo.getOldLicensePlates().equals(carBizDriverInfo.getLicensePlates())) {
                logger.info(LOGTAG + "****************修改车牌号 释放以前的车牌号");
                carBizCarInfoExMapper.updateCarLicensePlates(carBizDriverInfo.getOldLicensePlates(), 0);
                carBizDriverUpdateService.insert(carBizDriverInfo.getOldLicensePlates(), carBizDriverInfo.getLicensePlates(), carBizDriverInfo.getDriverId(), 1);
            }
            //判断 手机号是否修改 如果修改  添加司机事件
            if (carBizDriverInfo.getOldPhone() != null && carBizDriverInfo.getOldPhone().length() >= 1 && !carBizDriverInfo.getOldPhone().equals(carBizDriverInfo.getPhone())) {
                logger.info("****************修改手机号");
                carBizDriverUpdateService.insert(carBizDriverInfo.getOldPhone(), carBizDriverInfo.getPhone(), carBizDriverInfo.getDriverId(), 2);
            }
            //判断 身份证是否修改 如果修改 添加司机事件
            if (carBizDriverInfo.getOldIdCardNo() != null && carBizDriverInfo.getOldIdCardNo().length() >= 1 && !carBizDriverInfo.getOldIdCardNo().equals(carBizDriverInfo.getIdCardNo())) {
                logger.info("****************修改身份证");
                carBizDriverUpdateService.insert(carBizDriverInfo.getOldIdCardNo(), carBizDriverInfo.getIdCardNo(), carBizDriverInfo.getDriverId(), 3);
            }
            //判断 机动车驾驶证号是否修改 如果修改  添加司机事件
            if (carBizDriverInfo.getOldDriverLicenseNumber() != null && carBizDriverInfo.getOldDriverLicenseNumber().length() >= 1 && !carBizDriverInfo.getOldDriverLicenseNumber().equals(carBizDriverInfo.getDriverlicensenumber())) {
                logger.info("****************修改 机动车驾驶证号");
                carBizDriverUpdateService.insert(carBizDriverInfo.getOldDriverLicenseNumber(), carBizDriverInfo.getDriverlicensenumber(), carBizDriverInfo.getDriverId(), 4);
            }
            //判断 网络预约出租汽车驾驶员资格证号是否修改 如果修改  添加司机事件
            if (carBizDriverInfo.getOldDriverLicenseIssuingNumber() != null && carBizDriverInfo.getOldDriverLicenseIssuingNumber().length() >= 1 && !carBizDriverInfo.getOldDriverLicenseIssuingNumber().equals(carBizDriverInfo.getDriverlicenseissuingnumber())) {
                logger.info("****************修改网络预约出租汽车驾驶员资格证号");
                carBizDriverUpdateService.insert(carBizDriverInfo.getOldDriverLicenseIssuingNumber(), carBizDriverInfo.getDriverlicenseissuingnumber(), carBizDriverInfo.getDriverId(), 5);
            }
        } catch (Exception e) {
            logger.info("driverUpdateService error:" + e);
        }
    }

    /**
     * 仅供车队新增移除司机使用
     *
     * @param driverId
     * @param teamId
     * @param teamName
     * @param value
     */
    public void processingData(Integer driverId, Integer teamId, String teamName, Integer value) {
        CarBizDriverInfo carBizDriverInfo = carBizDriverInfoMapper.selectByPrimaryKey(driverId);
        if (carBizDriverInfo == null) {
            return;
        }
        CarBizDriverInfoDTO carBizDriverInfoDTO = BeanUtil.copyObject(carBizDriverInfo, CarBizDriverInfoDTO.class);
        // 查询城市名称，供应商名称，服务类型名称
        carBizDriverInfoDTO = this.getBaseStatis(carBizDriverInfoDTO);
        //车队信息
        carBizDriverInfoDTO.setTeamId(teamId);
        carBizDriverInfoDTO.setTeamName(teamName);
        carBizDriverInfoDTO.setTeamGroupId(null);
        carBizDriverInfoDTO.setTeamGroupName("");
        sendDriverToMq(carBizDriverInfoDTO, "UPDATE");
    }

    /**
     * 查询城市名称，供应商名称，服务类型，加盟类型
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
        // 根据供应商ID查询供应商名称以及加盟类型
        CarBizSupplier carBizSupplier = carBizSupplierService.selectByPrimaryKey(carBizDriverInfo.getSupplierId());
        if (carBizSupplier != null) {
            carBizDriverInfo.setSupplierName(carBizSupplier.getSupplierFullName());
            carBizDriverInfo.setCooperationType(carBizSupplier.getCooperationType());
            CarBizCooperationType carBizCooperationType = carBizCooperationTypeService.selectByPrimaryKey(carBizSupplier.getCooperationType());
            if (carBizCooperationType != null) {
                carBizDriverInfo.setCooperationTypeName(carBizCooperationType.getCooperationName());
            }
        }
        // 根据车牌号查找对应车型
        CarBizCarInfoDTO carInfo = carBizCarInfoService.selectModelByLicensePlates(carBizDriverInfo.getLicensePlates());
        if (carInfo != null) {
            carBizDriverInfo.setModelId(carInfo.getCarModelId());
            carBizDriverInfo.setModelName(carInfo.getCarModelName());
        }
        // 根据城市ID查找城市名称
        CarBizCity carBizCity = carBizCityService.selectByPrimaryKey(carBizDriverInfo.getServiceCity());
        if (carBizCity != null) {
            carBizDriverInfo.setCityName(carBizCity.getCityName());
        }
        // 根据服务类型查找服务类型名称
        CarBizCarGroup carBizCarGroup = carBizCarGroupService.selectByPrimaryKey(carBizDriverInfo.getGroupId());
        if (carBizCarGroup != null) {
            carBizDriverInfo.setCarGroupName(carBizCarGroup.getGroupName());
        }
        if (carBizDriverInfo.getDriverId() != null) {
            //TODO 根据司机ID查询车队小组信息
            Integer teamId = 0;
            String teamName = "";
            Integer teamGroupId = 0;
            String teamGroupName = "";
            carBizDriverInfo.setTeamId(teamId);
            carBizDriverInfo.setTeamName(teamName);
            carBizDriverInfo.setTeamGroupId(teamGroupId);
            carBizDriverInfo.setTeamGroupName(teamGroupName);

            //TODO 查询用户的名称
            carBizDriverInfo.setCreateName("");
            carBizDriverInfo.setUpdateName("");
        }
        return carBizDriverInfo;
    }

    public Map<String, Object> batchInputDriverInfo(Integer cityId, Integer supplierId, Integer teamId,
                                                    Integer teamGroupId, MultipartFile file, HttpServletRequest request) {

        Map<String, Object> resultMap = Maps.newHashMap();

        String resultError1 = "-1";//模板错误
        String resultErrorMag1 = "导入模板格式错误!";
        List<CarImportExceptionEntity> listException = Lists.newArrayList(); // 数据错误原因

        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        logger.info("上传的文件名为:{},上传的后缀名为:{}", fileName, suffixName);
        InputStream is;
        try {
            is = file.getInputStream();

            Workbook workbook = null;
            String fileType = fileName.split("\\.")[1];
            if (fileType.equals("xls")) {
                workbook = new HSSFWorkbook(is);
            } else if (fileType.equals("xlsx")) {
                workbook = new XSSFWorkbook(is);
            }
            Sheet sheet = workbook.getSheetAt(0);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            // 检查模板是否正确
            Row row1 = sheet.getRow(0);
            if (row1 == null) {
                resultMap.put("result", resultError1);
                resultMap.put("msg", resultErrorMag1);
                return resultMap;
            }
            for (int colIx = 0; colIx < 50; colIx++) {
                Cell cell = row1.getCell(colIx); // 获取列对象
                CellValue cellValue = evaluator.evaluate(cell); // 获取列属性
                if (cell == null || cellValue == null) {
                    resultMap.put("result", resultError1);
                    resultMap.put("msg", resultErrorMag1);
                    return resultMap;
                } else {
                    switch ((colIx + 1)) {
                        case 1:
                            if (!cellValue.getStringValue().contains("车牌号")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 2:
                            if (!cellValue.getStringValue().contains("机动车驾驶员姓名")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 3:
                            if (!cellValue.getStringValue().contains("驾驶员身份证号")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 4:
                            if (!cellValue.getStringValue().contains("驾驶员手机号")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 5:
                            if (!cellValue.getStringValue().contains("司机手机型号")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 6:
                            if (!cellValue.getStringValue().contains("司机手机运营商")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;

                        case 7:
                            if (!cellValue.getStringValue().contains("驾驶员性别")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 8:
                            if (!cellValue.getStringValue().contains("出生日期")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 9:
                            if (!cellValue.getStringValue().contains("年龄")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 10:
                            if (!cellValue.getStringValue().contains("服务监督号码")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 11:
                            if (!cellValue.getStringValue().contains("服务监督链接")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 12:
                            if (!cellValue.getStringValue().contains("车型类别")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 13:
                            if (!cellValue.getStringValue().contains("驾照类型")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 14:
                            if (!cellValue.getStringValue().contains("驾照领证日期")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 15:
                            if (!cellValue.getStringValue().contains("驾龄")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 16:
                            if (!cellValue.getStringValue().contains("驾照到期时间")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 17:
                            if (!cellValue.getStringValue().contains("档案编号")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 18:
                            if (!cellValue.getStringValue().contains("国籍")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 19:
                            if (!cellValue.getStringValue().contains("驾驶员民族")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 20:
                            if (!cellValue.getStringValue().contains("驾驶员婚姻状况")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 21:
                            if (!cellValue.getStringValue().contains("驾驶员外语能力")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 22:
                            if (!cellValue.getStringValue().contains("驾驶员学历")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 23:
                            if (!cellValue.getStringValue().contains("户口登记机关名称")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 24:
                            if (!cellValue.getStringValue().contains("户口住址或长住地址")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 25:
                            if (!cellValue.getStringValue().contains("驾驶员通信地址")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 26:
                            if (!cellValue.getStringValue().contains("驾驶员照片文件编号")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 27:
                            if (!cellValue.getStringValue().contains("机动车驾驶证号")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 28:
                            if (!cellValue.getStringValue().contains("机动车驾驶证扫描件文件编号")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 29:
                            if (!cellValue.getStringValue().contains("初次领取驾驶证日期")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 30:
                            if (!cellValue.getStringValue().contains("是否巡游出租汽车驾驶员")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 31:
                            if (!cellValue.getStringValue().contains("网络预约出租汽车驾驶员资格证号")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 32:
                            if (!cellValue.getStringValue().contains("网络预约出租汽车驾驶员证初领日期")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 33:
                            if (!cellValue.getStringValue().contains("巡游出租汽车驾驶员资格证号")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 34:
                            if (!cellValue.getStringValue().contains("网络预约出租汽车驾驶员证发证机构")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 35:
                            if (!cellValue.getStringValue().contains("资格证发证日期")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 36:
                            if (!cellValue.getStringValue().contains("初次领取资格证日期")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 37:
                            if (!cellValue.getStringValue().contains("资格证有效起始日期")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 38:
                            if (!cellValue.getStringValue().contains("资格证有效截止日期")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 39:
                            if (!cellValue.getStringValue().contains("注册日期")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 40:
                            if (!cellValue.getStringValue().contains("是否专职驾驶员")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 41:
                            if (!cellValue.getStringValue().contains("是否在驾驶员黑名单内")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 42:
                            if (!cellValue.getStringValue().contains("驾驶员合同（或协议）签署公司")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 43:
                            if (!cellValue.getStringValue().contains("有效合同时间")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 44:
                            if (!cellValue.getStringValue().contains("合同（或协议）有效期起")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 45:
                            if (!cellValue.getStringValue().contains("合同（或协议）有效期止")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 46:
                            if (!cellValue.getStringValue().contains("紧急情况联系人")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 47:
                            if (!cellValue.getStringValue().contains("紧急情况联系人电话")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 48:
                            if (!cellValue.getStringValue().contains("紧急情况联系人通讯地址")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 49:
                            if (!cellValue.getStringValue().contains("银行卡卡号")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                        case 50:
                            if (!cellValue.getStringValue().contains("银行卡开户行")) {
                                resultMap.put("result", resultError1);
                                resultMap.put("msg", resultErrorMag1);
                                return resultMap;
                            }
                            break;
                    }
                }
            }

            int minRowIx = 1;// 过滤掉标题，从第一行开始导入数据
            int maxRowIx = sheet.getLastRowNum(); // 要导入数据的总条数
            int successCount = 0;// 成功导入条数

            for (int rowIx = minRowIx; rowIx <= maxRowIx; rowIx++) {
                Row row = sheet.getRow(rowIx); // 获取行对象
                if (row == null) {
                    continue;
                }
                CarBizDriverInfoDTO carBizDriverInfoDTO = new CarBizDriverInfoDTO();
//                //TODO 获取当前用户Id
//                carBizDriverInfoDTO.setCreateBy(1);
//                carBizDriverInfoDTO.setCreateDate(new Date());
//                carBizDriverInfoDTO.setUpdateBy(1);
//                carBizDriverInfoDTO.setUpdateDate(new Date());
//                carBizDriverInfoDTO.setStatus(1);

                // 根据供应商ID查询供应商名称以及加盟类型
                CarBizSupplier carBizSupplier = carBizSupplierService.selectByPrimaryKey(supplierId);
                if (carBizSupplier != null) {
                    carBizDriverInfoDTO.setSupplierName(carBizSupplier.getSupplierFullName());
                    carBizDriverInfoDTO.setCooperationType(carBizSupplier.getCooperationType());
                }

                boolean isTrue = true;// 标识是否为有效数据
                String bankCardNumber = "";
                StringBuffer rePhone = new StringBuffer();//手机号
                StringBuffer reLicensePlates = new StringBuffer(); //车牌号
                StringBuffer reIdCarNo = new StringBuffer(); //身份证号

                // 司机导入模板总共50列
                for (int colIx = 0; colIx < 50; colIx++) {
                    Cell cell = row.getCell(colIx); // 获取列对象
                    CellValue cellValue = evaluator.evaluate(cell); // 获取列属性
                    switch ((colIx + 1)) {
                        // 车牌号
                        case 1:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【车牌号】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                // 去除车牌号空格，并且全部大写
                                String licensePlates = Common.replaceBlank(cellValue.getStringValue());
                                if (reLicensePlates.indexOf(licensePlates) > 0) {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第" + (rowIx + 1) + "行数据，第"
                                            + (colIx + 1) + "列 【车牌号】:" + licensePlates + "有重复车牌号");
                                    listException.add(returnVO);
                                    isTrue = false;
                                    colIx = 100;// 结束本行数据
                                } else {
                                    reLicensePlates.append(licensePlates);
                                    Integer carCount = carBizCarInfoExMapper.checkLicensePlates(licensePlates);
                                    if (carCount == null || carCount == 0) {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setReson( "第" + (rowIx + 1) + "行数据，第"
                                                + (colIx + 1) + "列 无效的【车牌号】");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    } else {
                                        Integer driverCount = carBizDriverInfoExMapper.checkLicensePlates(licensePlates);
                                        if (driverCount == null || driverCount > 0) {
                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                            returnVO.setReson( "第" + (rowIx + 1) + "行数据，第"
                                                    + (colIx + 1) + "列 【车牌号】:" + licensePlates + "已被绑定");
                                            listException.add(returnVO);
                                            isTrue = false;
                                        }
                                    }
                                    Integer cityCount = carBizCarInfoExMapper.validateCityAndSupplier(cityId, supplierId, licensePlates);
                                    if (cityCount == null || cityCount > 0) {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setReson( "第" + (rowIx + 1) + "行数据，第"
                                                + (colIx + 1) + "列 【车牌号】:" + licensePlates + "不在所选的城市或厂商");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    } else {
                                        carBizDriverInfoDTO.setLicensePlates(licensePlates);
                                    }
                                }
                            }
                            break;
                        // 机动车驾驶员姓名
                        case 2:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【机动车驾驶员姓名】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String name = Common.replaceBlank(cellValue.getStringValue());
                                carBizDriverInfoDTO.setName(name);
                            }
                            break;
                        // 驾驶员身份证号
                        case 3:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【驾驶员身份证号】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String idCardNo = Common.replaceBlankNoUpper(cellValue.getStringValue());
                                if ("X".equals(idCardNo.substring(idCardNo.length() - 1, idCardNo.length()))) {
                                    idCardNo = idCardNo.toLowerCase();
                                }
                                if (reIdCarNo.indexOf(idCardNo) > 0) {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第" + (rowIx + 1) + "行数据，第"
                                            + (colIx + 1) + "列 【驾驶员身份证号】:" + idCardNo + "有重复身份证号");
                                    listException.add(returnVO);
                                    isTrue = false;
                                    colIx = 100;// 结束本行数据
                                } else {
                                    reIdCarNo.append(idCardNo);
                                    if (StringUtils.isEmpty(idCardNo) || !ValidateUtils.validateIdCarNo(idCardNo)) {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                                + "列 【驾驶员身份证号】不合法");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    } else if (this.checkIdCardNo(idCardNo, null)) {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setReson( "第" + (rowIx + 1) + "行数据，第"
                                                + (colIx + 1) + "列 已存在【驾驶员身份证号为："
                                                + idCardNo
                                                + "】的信息");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    } else {
                                        carBizDriverInfoDTO.setIdCardNo(idCardNo);
                                    }
                                }
                            }
                            break;
                        // 驾驶员手机
                        case 4:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【驾驶员手机】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String phone = Common.replaceBlank(cellValue.getStringValue());
                                if (rePhone.indexOf(phone) > 0) {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第" + (rowIx + 1) + "行数据，第"
                                            + (colIx + 1) + "列 【驾驶员手机】:" + phone + "有重复手机号");
                                    listException.add(returnVO);
                                    isTrue = false;
                                    colIx = 100;// 结束本行数据
                                } else {
                                    rePhone.append(phone);
                                    if (StringUtils.isEmpty(phone) || !ValidateUtils.validatePhone(phone)) {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                                + "列 【驾驶员手机】不合法");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    } else if (this.checkPhone(phone, null)) {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setReson( "第" + (rowIx + 1) + "行数据，第"
                                                + (colIx + 1) + "列 已存在【手机号为："
                                                + phone
                                                + "】的信息");
                                        listException.add(returnVO);
                                        colIx = 100;// 结束本行数据
                                        isTrue = false;
                                    } else {
                                        carBizDriverInfoDTO.setPhone(phone);
                                    }
                                }
                            }
                            break;
                        // 司机手机型号
                        case 5:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【司机手机型号】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String phoneType = Common.replaceBlank(cellValue.getStringValue());
                                carBizDriverInfoDTO.setPhonetype(phoneType);
                            }
                            break;
                        // 司机手机运营商
                        case 6:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【司机手机运营商】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String phoneCorp = Common.replaceBlank(cellValue.getStringValue());
                                if ("中国移动".equals(phoneCorp) ||
                                        "中国联通".equals(phoneCorp) ||
                                        "中国电信".equals(phoneCorp) ||
                                        "其他".equals(phoneCorp)) {
                                    carBizDriverInfoDTO.setPhonecorp(phoneCorp);
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第" + (rowIx + 1)
                                            + "行数据，第" + (colIx + 1)
                                            + "列 请输入正确的【司机手机运营商】；中国移动、中国联通 或者 中国电信、其他");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 性别
                        case 7:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【性别】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String gender = Common.replaceBlank(cellValue.getStringValue());
                                if ("男".equals(gender)) {
                                    carBizDriverInfoDTO.setGender(1);
                                } else if ("女".equals(gender)) {
                                    carBizDriverInfoDTO.setGender(0);
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第" + (rowIx + 1)
                                            + "行数据，第" + (colIx + 1)
                                            + "列 请输入正确的【性别】；男 或者 女");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 出生日期
                        case 8:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【出生日期】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-")
                                        .replace("月", "-")
                                        .replace("日", "-")
                                        .replace(".", "-").trim();
                                if (StringUtils.isNotEmpty(d) && d.length() >= 1) {
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                }
                                if (ValidateUtils.isValidDate(d)) {
                                    d = dateFormat.format(dateFormat.parse(d));
                                    carBizDriverInfoDTO.setBirthDay(d);
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第"
                                            + (rowIx + 1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【出生日期】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 年龄
                        case 9:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【年龄】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String age = Common.replaceBlank(cellValue.getStringValue());
                                if (ValidateUtils.isRegular(age, ValidateUtils.NUMBER_PATTERN)) {
                                    carBizDriverInfoDTO.setAge(Integer.valueOf(age));
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第" + (rowIx + 1) + "行数据，第"
                                            + (colIx + 1) + "列 【年龄】只能有数字");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 服务监督号码
                        case 10:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【服务监督号码】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String superintendNo = Common.replaceBlank(cellValue.getStringValue());
                                carBizDriverInfoDTO.setSuperintendNo(superintendNo);
                            }
                            break;
                        // 服务监督链接
                        case 11:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【服务监督链接】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String superintendUrl = Common.replaceBlank(cellValue.getStringValue());
                                carBizDriverInfoDTO.setSuperintendUrl(superintendUrl);
                            }
                            break;
                        // 车型类别
                        case 12:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【车型类别】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String groupName = Common.replaceBlank(cellValue.getStringValue());
                                CarBizCarGroup carBizCarGroup = carBizCarGroupService.queryGroupByGroupName(groupName);
                                if (carBizCarGroup == null) {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                            + "列 【车型类别】没有" + groupName);
                                    listException.add(returnVO);
                                    isTrue = false;
                                } else {
                                    carBizDriverInfoDTO.setGroupId(carBizCarGroup.getGroupId());
                                }
                            }
                            break;
                        // 驾照类型
                        case 13:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【驾照类型】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String driverType = "A1、A2、A3、B1、B2、C1、C2、N、P";
                                String drivingLicenseType = Common.replaceBlank(cellValue.getStringValue());
                                String yuandrivingTypeString = "C1";
                                if (driverType.indexOf(drivingLicenseType) > 0) {
                                    yuandrivingTypeString = drivingLicenseType;
                                }
                                carBizDriverInfoDTO.setDrivingLicenseType(yuandrivingTypeString);
                            }
                            break;
                        // 驾照领证日期
                        case 14:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【驾照领证时间】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-")
                                        .replace("月", "-")
                                        .replace("日", "-")
                                        .replace(".", "-").trim();
                                if (StringUtils.isNotEmpty(d) && d.length() >= 1) {
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                }
                                if (ValidateUtils.isValidDate(d)) {
                                    Date issueDate = dateFormat.parse(d);
                                    carBizDriverInfoDTO.setIssueDate(issueDate);
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第"
                                            + (rowIx + 1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【驾照领证时间】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 驾龄
                        case 15:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【驾龄】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String drivingYears = Common.replaceBlank(cellValue.getStringValue());
                                if (ValidateUtils.isRegular(drivingYears, ValidateUtils.NUMBER_PATTERN)){
                                    carBizDriverInfoDTO.setDrivingYears(Integer.valueOf(drivingYears));
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第" + (rowIx + 1) + "行数据，第"
                                            + (colIx + 1) + "列 【驾龄】只能有数字");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 驾照到期时间
                        case 16:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【驾照到期时间】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-")
                                        .replace("月", "-")
                                        .replace("日", "-")
                                        .replace(".", "-").trim();
                                if (StringUtils.isNotEmpty(d) && d.length() >= 1) {
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                }
                                if (ValidateUtils.isValidDate(d)) {
                                    String datetime = dateFormat.format(new Date());
                                    if (dateFormat.parse(d).getTime() < dateFormat.parse(datetime).getTime()) {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setReson( "第" + (rowIx + 1) + "行数据，第"
                                                + (colIx + 1) + "列 【驾照到期时间】应该大于当前时间");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    } else {
                                        Date expireDate = dateFormat.parse(d);
                                        carBizDriverInfoDTO.setExpireDate(expireDate);
                                    }
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第"
                                            + (rowIx + 1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【驾照到期时间】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 档案编号
                        case 17:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【档案编号】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String archivesNo = Common.replaceBlank(cellValue.getStringValue());
                                carBizDriverInfoDTO.setArchivesNo(archivesNo);
                            }
                            break;
                        // 国籍
                        case 18:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【国籍】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String nationAlity = Common.replaceBlank(cellValue.getStringValue());
                                carBizDriverInfoDTO.setNationality(nationAlity);
                            }
                            break;
                        // 驾驶员民族
                        case 19:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【驾驶员民族】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String nation = Common.replaceBlank(cellValue.getStringValue());
                                carBizDriverInfoDTO.setNation(nation);
                            }
                            break;
                        // 驾驶员婚姻状况
                        case 20:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【驾驶员婚姻状况】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String marriage = Common.replaceBlank(cellValue.getStringValue());
                                if ("已婚".equals(marriage) || "未婚".equals(marriage) || "离异".equals(marriage)) {
                                    carBizDriverInfoDTO.setMarriage(marriage);
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                            + "列 【驾驶员婚姻状况】，请输入已婚、未婚、离异");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 驾驶员外语能力
                        case 21:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【驾驶员外语能力】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String foreignLanguage = Common.replaceBlank(cellValue.getStringValue());
                                if ("英语".equals(foreignLanguage)) {
                                    carBizDriverInfoDTO.setForeignlanguage("1");
                                } else if ("德语".equals(foreignLanguage)) {
                                    carBizDriverInfoDTO.setForeignlanguage("2");
                                } else if ("法语".equals(foreignLanguage)) {
                                    carBizDriverInfoDTO.setForeignlanguage("3");
                                } else if ("其他".equals(foreignLanguage)) {
                                    carBizDriverInfoDTO.setForeignlanguage("4");
                                } else if ("无".equals(foreignLanguage)) {
                                    carBizDriverInfoDTO.setForeignlanguage("0");
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                            + "列 【驾驶员外语能力】，请填写英语、德语、法语、其他或者无");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 驾驶员学历
                        case 22:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【驾驶员学历】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String education = Common.replaceBlank(cellValue.getStringValue());
                                if ("研究生".equals(education) || "本科".equals(education) || "大专".equals(education) ||
                                        "中专".equals(education) || "高中".equals(education) || "初中".equals(education) ||
                                        "小学".equals(education) || "其他".equals(education)) {
                                    carBizDriverInfoDTO.setEducation(education);
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                            + "列 【驾驶员学历】，请填写研究生、本科、大专、中专、高中、 初中、 中学、其他");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 户口登记机关名称
                        case 23:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【户口登记机关名称】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String houseHoldRegisterPermanent = Common.replaceBlank(cellValue.getStringValue());
                                carBizDriverInfoDTO.setHouseHoldRegisterPermanent(houseHoldRegisterPermanent);
                            }
                            break;
                        // 户口住址或长住地址
                        case 24:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【户口住址或长住地址】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String houseHoldRegister = Common.replaceBlank(cellValue.getStringValue());
                                carBizDriverInfoDTO.setHouseholdregister(houseHoldRegister);
                            }
                            break;
                        // 驾驶员通信地址
                        case 25:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【驾驶员通信地址】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String currentAddress = Common.replaceBlank(cellValue.getStringValue());
                                carBizDriverInfoDTO.setCurrentAddress(currentAddress);
                            }
                            break;
                        // 驾驶员照片文件编号
                        case 26:
                            if (cellValue != null && !StringUtils.isEmpty(cellValue.getStringValue())) {
                                String photosrct = Common.replaceBlank(cellValue.getStringValue());
                                if (photosrct.contains("http://pupload.01zhuanche.com/")) {
                                    carBizDriverInfoDTO.setPhotosrct(photosrct);
                                }
                            }
                            break;
                        // 机动车驾驶证号
                        case 27:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【机动车驾驶证号】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String driverLicenseNumber = Common.replaceBlank(cellValue.getStringValue());
                                carBizDriverInfoDTO.setDriverlicensenumber(driverLicenseNumber);
                            }
                            break;
                        // 机动车驾驶证扫描件文件编号
                        case 28:
                            if (cellValue != null && !StringUtils.isEmpty(cellValue.getStringValue())) {
                                String drivingLicenseImg = Common.replaceBlank(cellValue.getStringValue());
                                if (drivingLicenseImg.contains("http://pupload.01zhuanche.com/")) {
                                    carBizDriverInfoDTO.setDrivinglicenseimg(drivingLicenseImg);
                                }
                            }
                            break;
                        //初次领取驾驶证日期
                        case 29:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【初次领取驾驶证日期】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-")
                                        .replace("月", "-")
                                        .replace("日", "-")
                                        .replace(".", "-").trim();
                                if (StringUtils.isNotEmpty(d) && d.length() >= 1) {
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                }
                                if (ValidateUtils.isValidDate(d)) {
                                    String datetime = dateFormat.format(new Date());
                                    if (dateFormat.parse(d).getTime() > dateFormat.parse(datetime).getTime()) {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setReson( "第" + (rowIx + 1) + "行数据，第"
                                                + (colIx + 1) + "列 【初次领取驾驶证日期】应该小于当前时间");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    } else {
                                        d = dateFormat.format(dateFormat.parse(d));
                                        carBizDriverInfoDTO.setFirstmeshworkdrivinglicensedate(d);
                                    }
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第"
                                            + (rowIx + 1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【初次领取驾驶证日期】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //是否巡游出租汽车驾驶员
                        case 30:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【是否巡游出租汽车驾驶员】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String isXyDriver = Common.replaceBlank(cellValue.getStringValue());
                                if ("是".equals(isXyDriver)) {
                                    carBizDriverInfoDTO.setIsxydriver(1);
                                } else if ("否".equals(isXyDriver)) {
                                    carBizDriverInfoDTO.setIsxydriver(0);
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第" + (rowIx + 1)
                                            + "行数据，第" + (colIx + 1)
                                            + "列 请输入正确的【是否巡游出租汽车驾驶员】；是 或者 否");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //网络预约出租汽车驾驶员资格证号
                        case 31:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【网络预约出租汽车驾驶员资格证号】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String driverLicenseIssuingNumber = Common.replaceBlank(cellValue.getStringValue());
                                carBizDriverInfoDTO.setDriverlicenseissuingnumber(driverLicenseIssuingNumber);
                            }
                            break;
                        //网络预约出租汽车驾驶员证初领日期
                        case 32:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【网络预约出租汽车驾驶员证初领日期】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-")
                                        .replace("月", "-")
                                        .replace("日", "-")
                                        .replace(".", "-").trim();
                                if (StringUtils.isNotEmpty(d) && d.length() >= 1) {
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                }
                                if (ValidateUtils.isValidDate(d)) {
                                    String datetime = dateFormat.format(new Date());
                                    if (dateFormat.parse(d).getTime() > dateFormat.parse(datetime).getTime()) {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setReson( "第" + (rowIx + 1) + "行数据，第"
                                                + (colIx + 1) + "列 【网络预约出租汽车驾驶员证初领日期】应该小于当前时间");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    } else {
                                        d = dateFormat.format(dateFormat.parse(d));
                                        carBizDriverInfoDTO.setFirstmeshworkdrivinglicensedate(d);
                                    }
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第"
                                            + (rowIx + 1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【网络预约出租汽车驾驶员证初领日期】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //巡游出租汽车驾驶员资格证号
                        case 33:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【巡游出租汽车驾驶员资格证号】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String xyDriverNumber = Common.replaceBlank(cellValue.getStringValue());
                                carBizDriverInfoDTO.setXyDriverNumber(xyDriverNumber);
                            }
                            break;
                        //网络预约出租汽车驾驶员证发证机构
                        case 34:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【网络预约出租汽车驾驶员证发证机构】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String driverLicenseIssuingCorp = Common.replaceBlank(cellValue.getStringValue());
                                carBizDriverInfoDTO.setDriverlicenseissuingcorp(driverLicenseIssuingCorp);
                            }
                            break;
                        //资格证发证日期
                        case 35:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【资格证发证日期】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-")
                                        .replace("月", "-")
                                        .replace("日", "-")
                                        .replace(".", "-").trim();
                                if (StringUtils.isNotEmpty(d) && d.length() >= 1) {
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                }
                                if (ValidateUtils.isValidDate(d)) {
                                    d = dateFormat.format(dateFormat.parse(d));
                                    carBizDriverInfoDTO.setDriverLicenseIssuingGrantDate(d);
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第"
                                            + (rowIx + 1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【资格证发证日期】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //初次领取资格证日期
                        case 36:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【初次领取资格证日期】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-")
                                        .replace("月", "-")
                                        .replace("日", "-")
                                        .replace(".", "-").trim();
                                if (StringUtils.isNotEmpty(d) && d.length() >= 1) {
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                }
                                if (ValidateUtils.isValidDate(d)) {
                                    d = dateFormat.format(dateFormat.parse(d));
                                    carBizDriverInfoDTO.setDriverLicenseIssuingFirstDate(d);
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第"
                                            + (rowIx + 1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【初次领取资格证日期】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //资格证有效起始日期
                        case 37:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【资格证有效起始日期】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-")
                                        .replace("月", "-")
                                        .replace("日", "-")
                                        .replace(".", "-").trim();
                                if (StringUtils.isNotEmpty(d) && d.length() >= 1) {
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                }
                                if (ValidateUtils.isValidDate(d)) {
                                    d = dateFormat.format(dateFormat.parse(d));
                                    carBizDriverInfoDTO.setDriverlicenseissuingdatestart(d);
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第"
                                            + (rowIx + 1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【资格证有效起始日期】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //资格证有效截止日期
                        case 38:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【资格证有效截止日期】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-")
                                        .replace("月", "-")
                                        .replace("日", "-")
                                        .replace(".", "-").trim();
                                if (StringUtils.isNotEmpty(d) && d.length() >= 1) {
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                }
                                if (ValidateUtils.isValidDate(d)) {
                                    d = dateFormat.format(dateFormat.parse(d));
                                    carBizDriverInfoDTO.setDriverlicenseissuingdateend(d);
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第"
                                            + (rowIx + 1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【资格证有效截止日期】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //注册日期
                        case 39:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【注册日期】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-")
                                        .replace("月", "-")
                                        .replace("日", "-")
                                        .replace(".", "-").trim();
                                if (StringUtils.isNotEmpty(d) && d.length() >= 1) {
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                }
                                if (ValidateUtils.isValidDate(d)) {
                                    String datetime = dateFormat.format(new Date());
                                    if (dateFormat.parse(d).getTime() > dateFormat.parse(datetime).getTime()) {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setReson( "第" + (rowIx + 1) + "行数据，第"
                                                + (colIx + 1) + "列 【注册日期】应该小于当前时间");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    } else {
                                        d = dateFormat.format(dateFormat.parse(d));
                                        carBizDriverInfoDTO.setDriverLicenseIssuingRegisterDate(d);
                                    }
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第"
                                            + (rowIx + 1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【注册日期】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //是否专职驾驶员
                        case 40:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【是否专职驾驶员】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String partTimeJobDri = Common.replaceBlank(cellValue.getStringValue());
                                if ("是".equals(partTimeJobDri) ||
                                        "否".equals(partTimeJobDri)) {
                                    carBizDriverInfoDTO.setParttimejobdri(partTimeJobDri);
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第" + (rowIx + 1)
                                            + "行数据，第" + (colIx + 1)
                                            + "列 请输入正确的【是否专职驾驶员】；是 或者 否");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //是否在驾驶员黑名单内
                        case 41:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【是否在驾驶员黑名单内】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String isDriverBlack = Common.replaceBlank(cellValue.getStringValue());
                                if ("是".equals(isDriverBlack) || "否".equals(isDriverBlack)) {
//                                    carBizDriverInfoDTO.setIsDriverBlack(isDriverBlack);
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第" + (rowIx + 1)
                                            + "行数据，第" + (colIx + 1)
                                            + "列 请输入正确的【是否在驾驶员黑名单内】；是 或者 否");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //驾驶员合同（或协议）签署公司
                        case 42:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【驾驶员合同（或协议）签署公司】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String corpType = Common.replaceBlank(cellValue.getStringValue());
                                carBizDriverInfoDTO.setCorptype(corpType);
                            }
                            break;
                        //有效合同时间
                        case 43:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【有效合同时间】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-")
                                        .replace("月", "-")
                                        .replace("日", "-")
                                        .replace(".", "-").trim();
                                if (StringUtils.isNotEmpty(d) && d.length() >= 1) {
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                }
                                if (ValidateUtils.isValidDate(d)) {
                                    String datetime = dateFormat.format(new Date());
                                    if (dateFormat.parse(d).getTime() < dateFormat.parse(datetime).getTime()) {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setReson( "第" + (rowIx + 1) + "行数据，第"
                                                + (colIx + 1) + "列 【有效合同时间】应该大于当前时间");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    } else {
                                        d = dateFormat.format(dateFormat.parse(d));
                                        carBizDriverInfoDTO.setContractdate(d);
                                    }
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第"
                                            + (rowIx + 1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【有效合同时间】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //合同（或协议）有效期起
                        case 44:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【合同（或协议）有效期起】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-")
                                        .replace("月", "-")
                                        .replace("日", "-")
                                        .replace(".", "-").trim();
                                if (StringUtils.isNotEmpty(d) && d.length() >= 1) {
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                }
                                if (ValidateUtils.isValidDate(d)) {
                                    String datetime = dateFormat.format(new Date());
                                    d = dateFormat.format(dateFormat.parse(d));
                                    carBizDriverInfoDTO.setSigndate(d);
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第"
                                            + (rowIx + 1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【合同（或协议）有效期起】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //合同（或协议）有效期止
                        case 45:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【合同（或协议）有效期止】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-")
                                        .replace("月", "-")
                                        .replace("日", "-")
                                        .replace(".", "-").trim();
                                if (StringUtils.isNotEmpty(d) && d.length() >= 1) {
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                }
                                if (ValidateUtils.isValidDate(d)) {
                                    d = dateFormat.format(dateFormat.parse(d));
                                    carBizDriverInfoDTO.setSigndateend(d);
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第"
                                            + (rowIx + 1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【合同（或协议）有效期止】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 紧急联系人
                        case 46:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【紧急联系人】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String emergencyContactPerson = Common.replaceBlank(cellValue.getStringValue());
                                carBizDriverInfoDTO.setEmergencyContactPerson(emergencyContactPerson);
                            }
                            break;
                        // 紧急联系方式
                        case 47:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【紧急联系方式】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String emergencyContactNumber = Common.replaceBlank(cellValue.getStringValue()).trim();
                                if (emergencyContactNumber.length() > 11) {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                            + "列 【紧急联系方式】最多11位");
                                    listException.add(returnVO);
                                    isTrue = false;
                                } else {
                                    carBizDriverInfoDTO.setEmergencyContactNumber(emergencyContactNumber);
                                }
                            }
                            break;
                        // 紧急情况联系人通讯地址
                        case 48:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【紧急情况联系人通讯地址】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String emergencyContactAddr = Common.replaceBlank(cellValue.getStringValue());
                                carBizDriverInfoDTO.setEmergencycontactaddr(emergencyContactAddr);
                            }
                            break;
                        // 银行卡卡号
                        case 49:
                            if (cellValue != null && !StringUtils.isEmpty(cellValue.getStringValue())) {
                                bankCardNumber = Common.replaceBlank(cellValue.getStringValue());
                                if (StringUtils.isNotEmpty(bankCardNumber) && !ValidateUtils.isRegular(bankCardNumber, ValidateUtils.BANK_CARD_NUMBER)) {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                            + "列 【银行卡卡号】为16~19位数字组合");
                                    listException.add(returnVO);
                                    isTrue = false;
                                } else if (carBizDriverInfoDetailService.checkBankCardBank(bankCardNumber, null)) {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第" + (rowIx + 1) + "行数据，第"
                                            + (colIx + 1) + "列 【银行卡卡号】:" + cellValue.getStringValue() + "已被绑定");
                                    listException.add(returnVO);
                                    isTrue = false;
                                } else {
                                    carBizDriverInfoDTO.setBankCardNumber(bankCardNumber);
                                }
                            }
                            break;
                        // 银行卡开户行
                        case 50:
                            if (cellValue != null && !StringUtils.isEmpty(cellValue.getStringValue())) {
                                String bankCardBank = Common.replaceBlank(cellValue.getStringValue());
                                if (bankCardBank.length() > 100) {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第" + (rowIx + 1) + "行数据，第"
                                            + (colIx + 1) + "列 【银行卡卡号】:银行卡开户行不能超过100字，请检查后重新输入");
                                    listException.add(returnVO);
                                    isTrue = false;
                                } else if (StringUtils.isNotEmpty(bankCardBank) && StringUtils.isEmpty(bankCardNumber)) {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第" + (rowIx + 1) + "行数据，第"
                                            + (colIx + 1) + "列 【银行卡卡号】:请填写银行卡信息");
                                    listException.add(returnVO);
                                    isTrue = false;
                                } else {
                                    carBizDriverInfoDTO.setBankCardBank(bankCardBank);
                                }
                            } else {
                                if (StringUtils.isNotEmpty(bankCardNumber)) {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson( "第" + (rowIx + 1) + "行数据，第"
                                            + (colIx + 1) + "列 【银行卡开户行】:请填写银行卡开户行信息");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                    }// switch end
                }// 循环列结束
                if (isTrue && carBizDriverInfoDTO != null) {
                    carBizDriverInfoDTO.setServiceCity(cityId);
                    carBizDriverInfoDTO.setSupplierId(supplierId);
                    carBizDriverInfoDTO.setTeamId(teamId);
                    carBizDriverInfoDTO.setTeamGroupId(teamGroupId);
                    //TODO 车队名称
                    carBizDriverInfoDTO.setTeamName("");
                    carBizDriverInfoDTO.setTeamGroupName("");

                    //TODO 保存司机信息
                    Map<String, Object> stringObjectMap = this.saveDriver(carBizDriverInfoDTO);
                    if (stringObjectMap != null && "1".equals(stringObjectMap.get("result").toString())) {
                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                        returnVO.setReson( "手机号=" + carBizDriverInfoDTO.getPhone() + "保存出错，错误=" + stringObjectMap.get("msg").toString());
                        logger.info(LOGTAG + returnVO.getReson());
                        listException.add(returnVO);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String download = "";
        try {
            // 将错误列表导出
            if(listException.size() > 0) {
                Workbook wb = Common.exportExcel(request.getServletContext().getRealPath("/")+ "template" + File.separator + "car_exception.xlsx", listException);
                download = Common.exportExcelFromTempletToLoacl(request, wb,new String("ERROR".getBytes("utf-8"), "iso8859-1") );
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        if (!"".equals(download) && download != null) {
            resultMap.put("result", 1);
            resultMap.put("msg", "有错误信息");
            resultMap.put("download", download);
        } else {
            resultMap.put("result", 1);
            resultMap.put("msg", "成功");
            resultMap.put("download", "");
        }
        return resultMap;
    }
}
