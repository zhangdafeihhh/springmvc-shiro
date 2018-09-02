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
import com.zhuanche.util.BeanUtil;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CarBizDriverInfoService {

    private static final Logger logger = LoggerFactory.getLogger(CarBizDriverInfoService.class);
    private static final String LOGTAG = "[司机信息]: ";

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
     * @param params
     * @return
     */
    public List<CarBizDriverInfoDTO> queryDriverList(CarBizDriverInfoDTO params){
        return carBizDriverInfoExMapper.queryDriverList(params);
    }

    /**
     * 查询司机信息列表展示(无分页)
     * @param params
     * @return
     */
    public List<CarBizDriverInfoDTO> queryDriverListNoLimit(CarBizDriverInfoDTO params){
        return carBizDriverInfoExMapper.queryDriverListNoLimit(params);
    }

    /**
     * 查询手机号是否存在
     * @param phone 手机号，必传
     * @param driverId 司机ID，可不传
     * @return
     */
    public Boolean checkPhone(String phone, Integer driverId){
        int count = carBizDriverInfoExMapper.checkPhone(phone, driverId);
        if(count>0){
            return false;
        }
        return true;
    }

    /**
     * 查询身份证是否存在
     * @param idCardNo 身份证号
     * @param driverId 司机ID，可不传
     * @return
     */
    public Boolean checkIdCardNo(String idCardNo, Integer driverId){
        int count = carBizDriverInfoExMapper.checkIdCardNo(idCardNo, driverId);
        if(count>0){
            return false;
        }
        return true;
    }

    /**
     * 根据司机ID查询信息
     * @param driverId
     * @return
     */
    public CarBizDriverInfo selectByPrimaryKey(Integer driverId){
        return carBizDriverInfoMapper.selectByPrimaryKey(driverId);
    }

    /**
     * 重置imei
     * @param driverId
     * @return
     */
    public int resetIMEI(Integer driverId){
        return carBizDriverInfoExMapper.resetIMEI(driverId);
    }

    /**
     * 修改司机信息
     * @param carBizDriverInfo
     * @return
     */
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE ),
            @MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode=DataSourceMode.MASTER )
    } )
    public Map<String, Object> updateDriver(CarBizDriverInfoDTO carBizDriverInfo){
        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            try {
                logger.info(LOGTAG + "操作方式：编辑,新数据:" + JSON.toJSONString(carBizDriverInfo));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            String idCardNo = carBizDriverInfo.getIdCardNo();
            if("X".equals(idCardNo.substring(idCardNo.length()-1,idCardNo.length()))){
                idCardNo = idCardNo.toLowerCase();
            }
            carBizDriverInfo.setIdCardNo(idCardNo);
            carBizDriverInfo.setDriverlicensenumber(idCardNo);//机动车驾驶证号

            if(carBizDriverInfo.getPasswordReset()==1){//重置密码
                carBizDriverInfo.setPassword(getPassword(idCardNo));
            }

            //根据司机ID查询数据
            CarBizDriverInfo orginDriverInfo = carBizDriverInfoMapper.selectByPrimaryKey(carBizDriverInfo.getDriverId());
            if(orginDriverInfo!=null){
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
            if(n>0){
                logger.info("****************根据 车牌号更新车辆 信息（更换车辆所属人）");
//                if(carInfo!=null){
                    carBizCarInfoExMapper.updateCarLicensePlates(carBizDriverInfo.getLicensePlates(),carBizDriverInfo.getDriverId());
//                }
            }

            // 更新mongoDB
            DriverMongo driverMongo = driverMongoService.findByDriverId(carBizDriverInfo.getDriverId());
            if(driverMongo!=null){
                driverMongoService.updateDriverMongo(carBizDriverInfo);
            }else{
                driverMongoService.saveDriverMongo(carBizDriverInfo);
            }

            String method = "UPDATE";
            // 将司机置为无效状态需释放车辆资源
            if(carBizDriverInfo.getStatus().intValue() == 0) {
                method = "DELETE";
                this.updateDriverByXiao(carBizDriverInfo);
            }

            //城市或者供应商是否更换
            if((carBizDriverInfo.getOldCity()!= null && !carBizDriverInfo.getOldCity().equals(carBizDriverInfo.getServiceCity()))
                    || (carBizDriverInfo.getOldSupplier()!= null && !carBizDriverInfo.getOldSupplier().equals(carBizDriverInfo.getSupplierId()))){
                logger.info("修改司机driverId="+carBizDriverInfo.getDriverId()+"的城市或者供应商，需将司机移除车队小组");
                //TODO 移除司机车队小组信息
//                DriverTeamRelationEntity relation = new DriverTeamRelationEntity();
//                relation.setDriverId(driver.getDriverId());
//                driverTeamRelationService.deleteTeamAndGroupByDriverId(relation);
                carBizDriverInfo.setTeamId(null);
                carBizDriverInfo.setTeamName("");
                carBizDriverInfo.setTeamGroupId(null);
                carBizDriverInfo.setTeamGroupName("");
            }

            carBizDriverInfo.setCreateDate(orginDriverInfo.getCreateDate());
            //发送MQ
            if(carBizDriverInfo.getStatus()==0){
                sendDriverToMq(carBizDriverInfo, "DELETE");
            }else{
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
     * @param carBizDriverInfo
     * @return
     */
    public Map<String, Object> saveDriver(CarBizDriverInfoDTO carBizDriverInfo){
        Map<String, Object> resultMap = Maps.newHashMap();

        try {
            logger.info(LOGTAG + "操作方式：新建,数据:" + JSON.toJSONString(carBizDriverInfo));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            String idCardNo = carBizDriverInfo.getIdCardNo();
            if("X".equals(idCardNo.substring(idCardNo.length()-1,idCardNo.length()))){
                idCardNo = idCardNo.toLowerCase();
            }
            carBizDriverInfo.setIdCardNo(idCardNo);
            carBizDriverInfo.setDriverlicensenumber(idCardNo);//机动车驾驶证号
            carBizDriverInfo.setPassword(getPassword(carBizDriverInfo.getIdCardNo()));

            // 插入司机信息到mysql，mongo
            int n = this.saveDriverInfo(carBizDriverInfo);
            driverMongoService.saveDriverMongo(carBizDriverInfo);

            if(n > 0){
                // 根据 车牌号更新车辆 信息（更换车辆所属人）
//                if(carBizCarInfo!=null){
                    carBizCarInfoExMapper.updateCarLicensePlates(carBizDriverInfo.getLicensePlates(),carBizDriverInfo.getDriverId());
//                }
            }
            carBizChatUserService.insertChat(carBizDriverInfo.getDriverId());

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
     * @param driver
     * @return
     */
    public int saveDriverInfo(CarBizDriverInfoDTO driver) {
        if (driver.getExpireDate().equals("")) {
            driver.setExpireDate(null);
        }
        if (driver.getIssueDate().equals("")) {
            driver.setIssueDate(null);
        }
        carBizDriverInfoExMapper.insertCarBizDriverInfoDTO(driver);
        int driverId = driver.getDriverId();

        //司机信息扩展表，司机银行卡号
        CarBizDriverInfoDetail infoDetail = carBizDriverInfoDetailService.selectByPrimaryKey(driver.getDriverId());
        CarBizDriverInfoDetail carBizDriverInfoDetail = new CarBizDriverInfoDetail();
        carBizDriverInfoDetail.setBankCardBank(driver.getBankCardBank());
        carBizDriverInfoDetail.setBankCardNumber(driver.getBankCardNumber());
        carBizDriverInfoDetail.setDriverId(driver.getDriverId());
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
     * @param driver
     * @return
     */
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.MASTER )
    } )
    public int updateDriverInfo(CarBizDriverInfoDTO driver) {
        if(driver.getExpireDate()!=null){
            if (driver.getExpireDate().equals("")) {
                driver.setExpireDate(null);
            }
        }
        if(driver.getIssueDate()!=null){
            if (driver.getIssueDate().equals("")) {
                driver.setIssueDate(null);
            }
        }
        carBizDriverInfoExMapper.updateCarBizDriverInfoDTO(driver);
        int id = driver.getDriverId();

        //司机信息扩展表，司机银行卡号
        CarBizDriverInfoDetail infoDetail = carBizDriverInfoDetailService.selectByPrimaryKey(driver.getDriverId());
        CarBizDriverInfoDetail carBizDriverInfoDetail = new CarBizDriverInfoDetail();
        carBizDriverInfoDetail.setBankCardBank(driver.getBankCardBank());
        carBizDriverInfoDetail.setBankCardNumber(driver.getBankCardNumber());
        carBizDriverInfoDetail.setDriverId(driver.getDriverId());
        if(infoDetail!=null){
            carBizDriverInfoDetailService.updateByPrimaryKeySelective(carBizDriverInfoDetail);
        }else{
            carBizDriverInfoDetail.setExt1(2);//司机停运状态  1停运 2正常  司机新建是默认为2
            carBizDriverInfoDetailService.insertSelective(carBizDriverInfoDetail);
        }
        return id;
    }

    /**
     * 更新司机状态，如果将司机置为无效则释放车辆资源
     * @param driver
     * @return
     */
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.MASTER )
    } )
    public int updateDriverByXiao(CarBizDriverInfoDTO driver) {
        int rtn = carBizDriverInfoExMapper.updateDriverByXiao(driver.getDriverId());
        if (rtn > 0) {
            // 将司机置为无效
            if (driver.getStatus().intValue() == 0) {
                //根据车牌号更新车辆信息
                carBizCarInfoExMapper.updateCarLicensePlates(driver.getLicensePlates(), 0);
            }
            //更新司机mongo
            driverMongoService.updateByDriverId(driver.getDriverId(), driver.getStatus());

            try {
                carBizDriverUpdateService.insert(driver.getLicensePlates(), "", driver.getDriverId(), 1);
                carBizDriverUpdateService.insert(driver.getPhone(), "", driver.getDriverId(), 2);
            } catch (Exception e) {
                logger.info("updateDriverByXiao error:" + e);
            }
        }
        return rtn;
    }

    /**
     * 发送MQ
     * @param driver
     * @param method tag: INSERT UPDATE DELETE
     */
    public void sendDriverToMq(CarBizDriverInfoDTO driver, String method){
        //MQ消息写入
        try {
            Map<String, Object> messageMap = new HashMap<String, Object>();
            messageMap.put("carNumber", driver.getLicensePlates());//车牌号
            messageMap.put("city", driver.getCityName()); //城市名称
            messageMap.put("cityId",driver.getServiceCity()); //城市ID
            messageMap.put("createBy", driver.getUpdateBy()==null?"1":driver.getUpdateBy()); //操作人
            messageMap.put("driverId", driver.getDriverId()); //司机ID
            messageMap.put("driverName",driver.getName()); //司机姓名
            messageMap.put("driverPhone", driver.getPhone()==null?"":driver.getPhone()); //司机手机号
            messageMap.put("status",driver.getStatus()); //司机状态
            messageMap.put("supplierFullName", driver.getSupplierName()); //司机供应商名称
            messageMap.put("supplierId", driver.getSupplierId()); //司机供应商
            messageMap.put("cooperationType", driver.getCooperationType()); //司机加盟类型
            messageMap.put("groupId",driver.getGroupId()); //司机服务类型ID
            messageMap.put("create_date",driver.getCreateDate()); //司机创建时间
            messageMap.put("carType",driver.getCarGroupName()==null?"":driver.getCarGroupName()); //司机服务类型
            messageMap.put("teamId", driver.getTeamId()==null?"":driver.getTeamId()); //司机所属车队ID
            messageMap.put("teamName", driver.getTeamName()==null?"":driver.getTeamName()); //司机所属车队名称
            messageMap.put("teamGroupId", driver.getTeamGroupId()==null?"":driver.getTeamName()); //司机所属小组ID
            messageMap.put("teamGroupName", driver.getTeamGroupName()==null?"":driver.getTeamGroupName()); //司机所属小组名称

            String messageStr = JSONObject.fromObject(messageMap).toString();
            logger.info("专车司机driverId={}，同步发送数据={}", driver.getDriverId(), messageStr);
            CommonRocketProducer.publishMessage("driver_info", method, String.valueOf(driver.getDriverId()),messageMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据身份证获取密码
     * @param idCarNo
     * @return
     */
    public String getPassword(String idCarNo) {
        String parrword = "";
        try {
            //司机密码
            if(idCarNo.length()==18){
                parrword = MD5Utils.getMD5DigestHex(idCarNo.substring(idCarNo.length()-6, idCarNo.length()));
            }else if(idCarNo.length()==10){
                parrword = MD5Utils.getMD5DigestHex(idCarNo.substring(1, 7));
            }else{
                parrword = MD5Utils.getMD5DigestHex(idCarNo.substring(idCarNo.length()-6, idCarNo.length()));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return parrword;
    }

    /**
     * 判断一些基础信息是否正确
     * @param carBizDriverInfo
     * @return
     */
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE )
    } )
    public AjaxResponse validateCarDriverInfo (CarBizDriverInfoDTO carBizDriverInfo) {
        //手机号是否合法
        String phone = carBizDriverInfo.getPhone();
        if(StringUtils.isEmpty(phone) || !ValidateUtils.validatePhone(phone)){
            return AjaxResponse.fail(RestErrorCode.DRIVER_PHONE_NOT_LEGAL);
        }
        //身份证是否合法，大写X一律改为小写的x
        String idCardNo = carBizDriverInfo.getIdCardNo();
        if("X".equals(idCardNo.substring(idCardNo.length()-1,idCardNo.length()))){
            idCardNo = idCardNo.toLowerCase();
        }
        if(StringUtils.isEmpty(idCardNo) || !ValidateUtils.validateIdCarNo(idCardNo)){
            return AjaxResponse.fail(RestErrorCode.DRIVER_IDCARNO_NOT_LEGAL);
        }
        //银行卡号位16到18位数字，银行开户行，二者都填，或都不填
        String bankCardNumber = carBizDriverInfo.getBankCardNumber();
        String bankCardBank = carBizDriverInfo.getBankCardBank();
        if((StringUtils.isNotEmpty(bankCardNumber)||StringUtils.isEmpty(bankCardBank))||(StringUtils.isEmpty(bankCardNumber)||StringUtils.isNotEmpty(bankCardBank))){
            return AjaxResponse.fail(RestErrorCode.DRIVER_BANK_CARD_NUMBER_NOT_COMPLETE);
        }
        if(StringUtils.isNotEmpty(bankCardNumber)&&StringUtils.isNotEmpty(bankCardBank)&&!ValidateUtils.isRegular(bankCardNumber, ValidateUtils.BANK_CARD_NUMBER)){
            return AjaxResponse.fail(RestErrorCode.DRIVER_BANK_CARD_NUMBER_NOT_LEGAL);
        }
        //查询手机号是否存在
        Integer driverId = carBizDriverInfo.getDriverId();
        Boolean had = this.checkPhone(phone, driverId);
        if(had){
            return AjaxResponse.fail(RestErrorCode.DRIVER_PHONE_EXIST);
        }
        //查询身份证号是否存在
        had = this.checkIdCardNo(idCardNo, driverId);
        if(had){
            return AjaxResponse.fail(RestErrorCode.DRIVER_IDCARNO_EXIST);
        }
        //查询银行卡号是否存在
        had = carBizDriverInfoDetailService.checkBankCardBank(bankCardNumber, driverId);
        if(had){
            return AjaxResponse.fail(RestErrorCode.DRIVER_BANK_CARD_NUMBER_EXIST);
        }
        return AjaxResponse.success(true);
    }

    public void driverUpdate(CarBizDriverInfoDTO carBizDriverInfo){
        try {
            //判断 车牌号是否修改 如果修改 释放 车牌号
            if(StringUtils.isNotEmpty(carBizDriverInfo.getOldLicensePlates()) && !carBizDriverInfo.getOldLicensePlates().equals(carBizDriverInfo.getLicensePlates())){
                logger.info(LOGTAG + "****************修改车牌号 释放以前的车牌号");
                carBizCarInfoExMapper.updateCarLicensePlates(carBizDriverInfo.getOldLicensePlates(), 0);
                carBizDriverUpdateService.insert(carBizDriverInfo.getOldLicensePlates(), carBizDriverInfo.getLicensePlates(), carBizDriverInfo.getDriverId(), 1);
            }
            //判断 手机号是否修改 如果修改  添加司机事件
            if(carBizDriverInfo.getOldPhone()!= null && carBizDriverInfo.getOldPhone().length()>=1 && !carBizDriverInfo.getOldPhone().equals(carBizDriverInfo.getPhone())){
                logger.info("****************修改手机号");
                carBizDriverUpdateService.insert(carBizDriverInfo.getOldPhone(), carBizDriverInfo.getPhone(), carBizDriverInfo.getDriverId(), 2);
            }
            //判断 身份证是否修改 如果修改 添加司机事件
            if(carBizDriverInfo.getOldIdCardNo()!= null && carBizDriverInfo.getOldIdCardNo().length()>=1 && !carBizDriverInfo.getOldIdCardNo().equals(carBizDriverInfo.getIdCardNo())){
                logger.info("****************修改身份证");
                carBizDriverUpdateService.insert(carBizDriverInfo.getOldIdCardNo(), carBizDriverInfo.getIdCardNo(), carBizDriverInfo.getDriverId(), 3);
            }
            //判断 机动车驾驶证号是否修改 如果修改  添加司机事件
            if(carBizDriverInfo.getOldDriverLicenseNumber()!= null && carBizDriverInfo.getOldDriverLicenseNumber().length()>=1 && !carBizDriverInfo.getOldDriverLicenseNumber().equals(carBizDriverInfo.getDriverlicensenumber())){
                logger.info("****************修改 机动车驾驶证号");
                carBizDriverUpdateService.insert(carBizDriverInfo.getOldDriverLicenseNumber(), carBizDriverInfo.getDriverlicensenumber(), carBizDriverInfo.getDriverId(), 4);
            }
            //判断 网络预约出租汽车驾驶员资格证号是否修改 如果修改  添加司机事件
            if(carBizDriverInfo.getOldDriverLicenseIssuingNumber()!= null && carBizDriverInfo.getOldDriverLicenseIssuingNumber().length()>=1 && !carBizDriverInfo.getOldDriverLicenseIssuingNumber().equals(carBizDriverInfo.getDriverlicenseissuingnumber())){
                logger.info("****************修改网络预约出租汽车驾驶员资格证号");
                carBizDriverUpdateService.insert(carBizDriverInfo.getOldDriverLicenseIssuingNumber(), carBizDriverInfo.getDriverlicenseissuingnumber(), carBizDriverInfo.getDriverId(), 5);
            }
        } catch (Exception e) {
            logger.info("driverUpdateService error:"+e);
        }
    }

    /**
     * 仅供车队新增移除司机使用
     * @param driverId
     * @param teamId
     * @param teamName
     * @param value
     */
    public void processingData(Integer driverId, Integer teamId, String teamName, Integer value) {
        CarBizDriverInfo carBizDriverInfo = carBizDriverInfoMapper.selectByPrimaryKey(driverId);
        if(carBizDriverInfo==null){
            return ;
        }
        CarBizDriverInfoDTO carBizDriverInfoDTO = BeanUtil.copyObject(carBizDriverInfo, CarBizDriverInfoDTO.class);
        //TODO 查询城市名称，供应商名称，服务类型名称
        //车队信息
        carBizDriverInfoDTO.setTeamId(teamId);
        carBizDriverInfoDTO.setTeamName(teamName);
        carBizDriverInfoDTO.setTeamGroupId(null);
        carBizDriverInfoDTO.setTeamGroupName("");
        sendDriverToMq(carBizDriverInfoDTO, "UPDATE");
    }

    /**
     * 查询城市名称，供应商名称，服务类型，加盟类型
     * @param carBizDriverInfo
     * @return
     */
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE ),
            @MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode=DataSourceMode.SLAVE )
    } )
    public CarBizDriverInfoDTO getBaseStatis(CarBizDriverInfoDTO carBizDriverInfo){
        if(carBizDriverInfo==null){
            return carBizDriverInfo;
        }
        // 根据供应商ID查询供应商名称以及加盟类型
        CarBizSupplier carBizSupplier = carBizSupplierService.selectByPrimaryKey(carBizDriverInfo.getSupplierId());
        if(carBizSupplier!=null){
            carBizDriverInfo.setSupplierName(carBizSupplier.getSupplierFullName());
            carBizDriverInfo.setCooperationType(carBizSupplier.getCooperationType());
            CarBizCooperationType carBizCooperationType = carBizCooperationTypeService.selectByPrimaryKey(carBizSupplier.getCooperationType());
            if(carBizCooperationType!=null){
                carBizDriverInfo.setCooperationTypeName(carBizCooperationType.getCooperationName());
            }
        }
        // 根据车牌号查找对应车型
        CarBizCarInfoDTO carInfo = carBizCarInfoService.selectModelByLicensePlates(carBizDriverInfo.getLicensePlates());
        if(carInfo!=null){
            carBizDriverInfo.setModelId(carInfo.getCarModelId());
            carBizDriverInfo.setModelName(carInfo.getCarModelName());
        }
        // 根据城市ID查找城市名称
        CarBizCity carBizCity = carBizCityService.selectByPrimaryKey(carBizDriverInfo.getServiceCity());
        if(carBizCity!=null){
            carBizDriverInfo.setCityName(carBizCity.getCityName());
        }
        // 根据服务类型查找服务类型名称
        CarBizCarGroup carBizCarGroup = carBizCarGroupService.selectByPrimaryKey(carBizDriverInfo.getGroupId());
        if(carBizCarGroup!=null){
            carBizDriverInfo.setCarGroupName(carBizCarGroup.getGroupName());
        }
        if(carBizDriverInfo.getDriverId()!=null){
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
}
