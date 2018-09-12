package com.zhuanche.controller.driver;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.rentcar.CarBizDriverInfoDTO;
import com.zhuanche.dto.rentcar.CarBizDriverInfoDetailDTO;
import com.zhuanche.entity.rentcar.CarBizCarGroup;
import com.zhuanche.entity.rentcar.CarBizDriverInfo;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.serv.*;
import com.zhuanche.serv.driverteam.CarDriverTeamService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/driverInfo")
public class DriverInfoController {

    private static final Logger logger = LoggerFactory.getLogger(DriverInfoController.class);
    private static final String LOGTAG = "[司机信息]: ";

    @Autowired
    private CarBizDriverInfoService carBizDriverInfoService;

    @Autowired
    private CarBizDriverInfoDetailService carBizDriverInfoDetailService;

    @Autowired
    private CarDriverTeamService carDriverTeamService;

    @Autowired
    private CarBizSupplierService carBizSupplierService;

    @Autowired
    private CarBizCarInfoService carBizCarInfoService;

    @Autowired
    private CarBizCarGroupService carBizCarGroupService;


    /**
     * 司机信息列表（有分页）
     * @param name 司机姓名
     * @param phone 司机手机号
     * @param licensePlates 车牌号
     * @param status 状态
     * @param cityId 城市ID
     * @param supplierId 供应商ID
     * @param teamId 车队ID
     * @param teamGroupId 车队下小组ID
     * @param groupId 服务类型ID
     * @param cooperationType 加盟类型
     * @param imei imei
     * @param idCardNo 身份证号
     * @param isImage 是否维护形象
     * @param page 起始页，默认0
     * @param pageSize 取N条，默认20
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/findDriverList")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE)
    })
    public AjaxResponse findDriverList(String name, String phone, String licensePlates, Integer status, Integer cityId, Integer supplierId,
            Integer teamId, Integer teamGroupId, Integer groupId, Integer cooperationType, String imei, String idCardNo, Integer isImage,
            @RequestParam(value="page", defaultValue="0")Integer page,
            @RequestParam(value="pageSize", defaultValue="20")Integer pageSize) {

        // 数据权限控制SSOLoginUser
        Set<Integer> permOfCity        = WebSessionUtil.getCurrentLoginUser().getCityIds(); //普通管理员可以管理的所有城市ID
        Set<Integer> permOfSupplier    = WebSessionUtil.getCurrentLoginUser().getSupplierIds(); //普通管理员可以管理的所有供应商ID
        Set<Integer> permOfTeam        = WebSessionUtil.getCurrentLoginUser().getTeamIds(); //普通管理员可以管理的所有车队ID

        int total = 0;
        List<CarBizDriverInfoDTO> list =  Lists.newArrayList();
        Set<Integer> driverIds = null;
        Boolean had = false;
        if(teamGroupId!=null || teamId!=null || (permOfTeam!=null && permOfTeam.size()>0)){
            had = true;
            driverIds = carDriverTeamService.selectDriverIdsByTeamIdAndGroupId(teamGroupId, teamId, permOfTeam);
        }
        if(had && (driverIds==null || driverIds.size()==0)){
            logger.info(LOGTAG + "查询teamId={},groupId={},permOfTeam={}没有司机信息", teamId, teamGroupId, permOfTeam);
            PageDTO pageDTO = new PageDTO(page, pageSize, total, list);
            return AjaxResponse.success(pageDTO);
        }

        CarBizDriverInfoDTO carBizDriverInfoDTO = new CarBizDriverInfoDTO();
        carBizDriverInfoDTO.setName(name);
        carBizDriverInfoDTO.setPhone(phone);
        carBizDriverInfoDTO.setLicensePlates(licensePlates);
        carBizDriverInfoDTO.setStatus(status);
        carBizDriverInfoDTO.setServiceCity(cityId);
        carBizDriverInfoDTO.setSupplierId(supplierId);
        carBizDriverInfoDTO.setTeamId(teamId);
        carBizDriverInfoDTO.setTeamGroupId(teamGroupId);
        carBizDriverInfoDTO.setGroupId(groupId);
        carBizDriverInfoDTO.setCooperationType(cooperationType);
        carBizDriverInfoDTO.setImei(imei);
        carBizDriverInfoDTO.setIdCardNo(idCardNo);
        carBizDriverInfoDTO.setIsImage(isImage);
        //数据权限
        carBizDriverInfoDTO.setCityIds(permOfCity);
        carBizDriverInfoDTO.setSupplierIds(permOfSupplier);
        carBizDriverInfoDTO.setTeamIds(permOfTeam);
        carBizDriverInfoDTO.setDriverIds(driverIds);

        Page p = PageHelper.startPage(page, pageSize, true);
        try {
            list = carBizDriverInfoService.queryDriverList(carBizDriverInfoDTO);
            total = (int)p.getTotal();
        } finally {
            PageHelper.clearPage();
        }
        // 查询城市名称，供应商名称，服务类型，加盟类型
        for (CarBizDriverInfoDTO driver : list) {
            driver = carBizDriverInfoService.getBaseStatis(driver);
        }
        PageDTO pageDTO = new PageDTO(page, pageSize, total, list);
        return AjaxResponse.success(pageDTO);
    }

    /**
     * 司机信息列表查询（无分页）
     * @param name 司机姓名
     * @param phone 司机手机号
     * @param licensePlates 车牌号
     * @param status 状态
     * @param cityId 城市ID
     * @param supplierId 供应商ID
     * @param teamId 车队ID
     * @param teamGroupId 车队下小组ID
     * @param groupId 服务类型ID
     * @param cooperationType 加盟类型
     * @param imei imei
     * @param idCardNo 身份证号
     * @param isImage 是否维护形象
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/findDriverAllList")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE)
    })
    public AjaxResponse findDriverAllList(String name, String phone, String licensePlates, Integer status, Integer cityId, Integer supplierId,
         Integer teamId, Integer teamGroupId, Integer groupId, Integer cooperationType, String imei, String idCardNo, Integer isImage) {

        // 数据权限控制SSOLoginUser
        Set<Integer> permOfCity        = WebSessionUtil.getCurrentLoginUser().getCityIds(); //普通管理员可以管理的所有城市ID
        Set<Integer> permOfSupplier    = WebSessionUtil.getCurrentLoginUser().getSupplierIds(); //普通管理员可以管理的所有供应商ID
        Set<Integer> permOfTeam        = WebSessionUtil.getCurrentLoginUser().getTeamIds(); //普通管理员可以管理的所有车队ID

        List<CarBizDriverInfoDTO> list =  Lists.newArrayList();
        Set<Integer> driverIds = null;
        Boolean had = false;
        if(teamGroupId!=null || teamId!=null || (permOfTeam!=null && permOfTeam.size()>0)){
            had = true;
            driverIds = carDriverTeamService.selectDriverIdsByTeamIdAndGroupId(teamGroupId, teamId, permOfTeam);
        }
        if(had && (driverIds==null || driverIds.size()==0)){
            logger.info(LOGTAG + "查询teamId={},groupId={},permOfTeam={}没有司机信息", teamId, teamGroupId, permOfTeam);
            return AjaxResponse.success(list);
        }
        CarBizDriverInfoDTO carBizDriverInfoDTO = new CarBizDriverInfoDTO();
        carBizDriverInfoDTO.setName(name);
        carBizDriverInfoDTO.setPhone(phone);
        carBizDriverInfoDTO.setLicensePlates(licensePlates);
        carBizDriverInfoDTO.setStatus(status);
        carBizDriverInfoDTO.setServiceCity(cityId);
        carBizDriverInfoDTO.setSupplierId(supplierId);
        carBizDriverInfoDTO.setTeamId(teamId);
        carBizDriverInfoDTO.setTeamGroupId(teamGroupId);
        carBizDriverInfoDTO.setGroupId(groupId);
        carBizDriverInfoDTO.setCooperationType(cooperationType);
        carBizDriverInfoDTO.setImei(imei);
        carBizDriverInfoDTO.setIdCardNo(idCardNo);
        carBizDriverInfoDTO.setIsImage(isImage);
        //数据权限
        carBizDriverInfoDTO.setCityIds(permOfCity);
        carBizDriverInfoDTO.setSupplierIds(permOfSupplier);
        carBizDriverInfoDTO.setTeamIds(permOfTeam);
        carBizDriverInfoDTO.setDriverIds(driverIds);

        list = carBizDriverInfoService.queryDriverList(carBizDriverInfoDTO);
        // 查询城市名称，供应商名称，服务类型，加盟类型
        for (CarBizDriverInfoDTO driver : list) {
            driver = carBizDriverInfoService.getBaseStatis(driver);
        }
        return AjaxResponse.success(list);
    }

    /**
     * 司机信息列表查询导出
     * @param name 司机姓名
     * @param phone 司机手机号
     * @param licensePlates 车牌号
     * @param status 状态
     * @param cityId 城市ID
     * @param supplierId 供应商ID
     * @param teamId 车队ID
     * @param teamGroupId 车队下小组ID
     * @param groupId 服务类型ID
     * @param cooperationType 加盟类型
     * @param imei imei
     * @param idCardNo 身份证号
     * @param isImage 是否维护形象
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/exportDriverList")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE)
    })
    public void exportDriverList(String name, String phone, String licensePlates, Integer status, Integer cityId, Integer supplierId,
                                         Integer teamId, Integer teamGroupId, Integer groupId, Integer cooperationType,
                                         String imei, String idCardNo, Integer isImage,
                                         HttpServletRequest request, HttpServletResponse response) {

        // 数据权限控制SSOLoginUser
        Set<Integer> permOfCity        = WebSessionUtil.getCurrentLoginUser().getCityIds(); //普通管理员可以管理的所有城市ID
        Set<Integer> permOfSupplier    = WebSessionUtil.getCurrentLoginUser().getSupplierIds(); //普通管理员可以管理的所有供应商ID
        Set<Integer> permOfTeam        = WebSessionUtil.getCurrentLoginUser().getTeamIds(); //普通管理员可以管理的所有车队ID

        List<CarBizDriverInfoDTO> list =  Lists.newArrayList();
        Set<Integer> driverIds = null;
        Boolean had = false;
        if(teamGroupId!=null || teamId!=null || (permOfTeam!=null && permOfTeam.size()>0)){
            had = true;
            driverIds = carDriverTeamService.selectDriverIdsByTeamIdAndGroupId(teamGroupId, teamId, permOfTeam);
        }
        if(had && (driverIds==null || driverIds.size()==0)){
            logger.info(LOGTAG + "查询teamId={},groupId={},permOfTeam={}没有司机信息", teamId, teamGroupId, permOfTeam);
            list =  Lists.newArrayList();
        }else{
            CarBizDriverInfoDTO carBizDriverInfoDTO = new CarBizDriverInfoDTO();
            carBizDriverInfoDTO.setName(name);
            carBizDriverInfoDTO.setPhone(phone);
            carBizDriverInfoDTO.setLicensePlates(licensePlates);
            carBizDriverInfoDTO.setStatus(status);
            carBizDriverInfoDTO.setServiceCity(cityId);
            carBizDriverInfoDTO.setSupplierId(supplierId);
            carBizDriverInfoDTO.setTeamId(teamId);
            carBizDriverInfoDTO.setTeamGroupId(teamGroupId);
            carBizDriverInfoDTO.setGroupId(groupId);
            carBizDriverInfoDTO.setCooperationType(cooperationType);
            carBizDriverInfoDTO.setImei(imei);
            carBizDriverInfoDTO.setIdCardNo(idCardNo);
            carBizDriverInfoDTO.setIsImage(isImage);
            //数据权限
            carBizDriverInfoDTO.setCityIds(permOfCity);
            carBizDriverInfoDTO.setSupplierIds(permOfSupplier);
            carBizDriverInfoDTO.setTeamIds(permOfTeam);
            carBizDriverInfoDTO.setDriverIds(driverIds);

            list = carBizDriverInfoService.queryDriverList(carBizDriverInfoDTO);
            // 查询城市名称，供应商名称，服务类型，加盟类型
            for (CarBizDriverInfoDTO driver : list) {
                carBizDriverInfoService.getBaseStatis(driver);
            }
        }
        try {
            Workbook wb = carBizDriverInfoService.exportExcel(list,request.getRealPath("/")+File.separator+"template"+File.separator+"driver_info.xlsx");
            Componment.fileDownload(response, wb, new String("司机信息".getBytes("utf-8"), "iso8859-1"));
        } catch (Exception e) {
           logger.error("司机信息列表查询导出error",e);
        }
    }

    /**
     * 司机信息
     * @param driverId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/findDriverInfoByDriverId")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE)
    })
    public AjaxResponse findDriverInfoByDriverId(@Verify(param = "driverId", rule = "required") Integer driverId) {

        CarBizDriverInfo carBizDriverInfo = carBizDriverInfoService.selectByPrimaryKey(driverId);
        CarBizDriverInfoDTO carBizDriverInfoDTO = BeanUtil.copyObject(carBizDriverInfo, CarBizDriverInfoDTO.class);
        //查询司机银行卡信息
        CarBizDriverInfoDetailDTO carBizDriverInfoDetailDTO = carBizDriverInfoDetailService.selectByDriverId(driverId);
        if(carBizDriverInfoDetailDTO!=null){
            carBizDriverInfoDTO.setBankCardNumber(carBizDriverInfoDetailDTO.getBankCardNumber());
            carBizDriverInfoDTO.setBankCardBank(carBizDriverInfoDetailDTO.getBankCardBank());
        }
        // 查询城市名称，供应商名称，服务类型，加盟类型
        carBizDriverInfoDTO = carBizDriverInfoService.getBaseStatis(carBizDriverInfoDTO);
        return AjaxResponse.success(carBizDriverInfoDTO);
    }

    /**
     * 保存/修改司机信息
     * @param driverId //司机ID，修改需要传
     * @param passwordReset//密码重置 修改时传值[1.是 2.否]
     * @param phone //司机手机号，不可为空
     * @param gender //司机性别，[1.男0.女]
     * @param name //司机姓名，不可为空
     * @param supplierId //供应商ID
     * @param age //司机姓名
     * @param currentAddress //现住址
     * @param emergencyContactPerson //紧急联系人
     * @param emergencyContactNumber //紧急联系方式
     * @param idCardNo //身份证号
     * @param superintendNo //服务监督码
     * @param superintendUrl //服务监督码链接
     * @param drivingLicenseType //驾照类型,例：A1,B1,C1
     * @param drivingYears //驾龄
     * @param archivesNo //档案编号
     * @param issueDateStr //领证日期 yyyy-MM-dd
     * @param expireDateStr //驾照到期时间 yyyy-MM-dd
     * @param serviceCity //服务城市ID
     * @param licensePlates //车牌号
     * @param status //司机状态
     * @param photosrct //司机头像,图片url
     * @param driverlicensenumber //机动车驾驶证号
     * @param drivinglicenseimg //驾驶证扫描件URL
     * @param nationality //国籍
     * @param householdregister //户籍
     * @param nation //驾驶员民族
     * @param marriage //驾驶员婚姻状况  [填写:已婚、未婚、离异]
     * @param foreignlanguage //外语能力 [0无（默认） 1 英语 2 德语  3 法语 4 其他]
     * @param education //驾驶员学历 [填写:研究生、本科、大专、中专、高中、 初中、 中学、其他]
     * @param firstdrivinglicensedate //初次领取驾驶证日期 yyyy-MM-dd
     * @param firstmeshworkdrivinglicensedate //网络预约出租汽车驾驶员证初领日期 yyyy-MM-dd
     * @param corptype //驾驶员合同（或协议）签署公司标识
     * @param signdate //签署日期 yyyy-MM-dd
     * @param signdateend //合同（或协议）到期时间 yyyy-MM-dd
     * @param contractdate //有效合同时间 yyyy-MM-dd
     * @param isxydriver //是否巡游出租汽车驾驶员  [填写: 1.是、0.否]
     * @param xyDriverNumber //巡游出租汽车驾驶员资格证号
     * @param parttimejobdri //专职或兼职司机  [填写: 是、否]
     * @param phonetype //司机手机型号
     * @param phonecorp //司机手机运营商 [填写: 中国移动、中国联通 或者 中国电信、其他]
     * @param maptype //使用地图类型 [填写: 高德、百度]
     * @param emergencycontactaddr //紧急情况联系人通讯地址
     * @param assessment //评估，驾驶员服务质量信誉考核结果
     * @param driverlicenseissuingdatestart //资格证有效起始日期，驾驶员证发证日期 yyyy-MM-dd
     * @param driverlicenseissuingdateend //资格证有效截止日期，驾驶员证有效期止 yyyy-MM-dd
     * @param driverlicenseissuingcorp //网络预约出租汽车驾驶员证发证机构,驾驶员证发证机构
     * @param driverlicenseissuingnumber //网络预约出租汽车驾驶员资格证号,网络预约出租汽车驾驶员证编号
     * @param driverLicenseIssuingRegisterDate //注册日期 yyyy-MM-dd
     * @param driverLicenseIssuingFirstDate //初次领取资格证日期 yyyy-MM-dd
     * @param driverLicenseIssuingGrantDate //资格证发证日期 yyyy-MM-dd
     * @param birthDay //出生日期 yyyy-MM-dd
     * @param houseHoldRegisterPermanent //户口登记机关名称
     * @param groupId //服务类型ID
     * @param memo //备注
     * @param bankCardBank //银行卡开户行
     * @param bankCardNumber //银行卡卡号
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveDriver")
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE )
    } )
    public AjaxResponse saveDriver(Integer driverId, Integer passwordReset,
                                   @Verify(param = "phone",rule="required") String phone,
                                   @Verify(param = "gender",rule="required") Integer gender,
                                   @Verify(param = "name",rule="required") String name,
                                   @Verify(param = "idCardNo",rule="required") String idCardNo,
                                   @Verify(param = "serviceCity",rule="required") Integer serviceCity,
                                   @Verify(param = "supplierId",rule="required") Integer supplierId,
                                   @Verify(param = "licensePlates",rule="required") String licensePlates,
                                   @Verify(param = "status",rule="required") Integer status,
                                   @Verify(param = "groupId",rule="required") Integer groupId,
                                   @Verify(param = "age",rule="required|min(18)") Integer age,
                                   @Verify(param = "emergencyContactNumber",rule="maxlength(11)") String emergencyContactNumber,
                                   String currentAddress, String emergencyContactPerson,
                                   String superintendNo, String superintendUrl, String drivingLicenseType, Integer drivingYears,
                                   String archivesNo, String issueDateStr, String expireDateStr, String photosrct, String driverlicensenumber,
                                   String drivinglicenseimg, String nationality, String householdregister, String nation, String marriage,
                                   String foreignlanguage, String education, String firstdrivinglicensedate,
                                   String firstmeshworkdrivinglicensedate, String corptype,  String signdate, String signdateend,
                                   String contractdate, Integer isxydriver, String xyDriverNumber, String parttimejobdri, String phonetype,
                                   String phonecorp, String maptype, String emergencycontactaddr, String assessment,
                                   String driverlicenseissuingdatestart, String driverlicenseissuingdateend, String driverlicenseissuingcorp,
                                   String driverlicenseissuingnumber, String driverLicenseIssuingRegisterDate, String driverLicenseIssuingFirstDate,
                                   String driverLicenseIssuingGrantDate, String birthDay, String houseHoldRegisterPermanent,
                                   String memo, String bankCardBank,String bankCardNumber) {

        //判断一些基础信息是否正确
        AjaxResponse ajaxResponse = carBizDriverInfoService.validateCarDriverInfo(driverId, phone, idCardNo, bankCardNumber, bankCardBank);
        if(ajaxResponse.getCode()!=0){
            return ajaxResponse;
        }
        // 根据供应商ID查询供应商名称以及加盟类型
        CarBizSupplier carBizSupplier = carBizSupplierService.selectByPrimaryKey(supplierId);
        if (carBizSupplier == null) {
            return AjaxResponse.fail(RestErrorCode.SUPPLIER_NOT_EXIST, supplierId);
        }
        if(serviceCity!=carBizSupplier.getSupplierCity()){
            return AjaxResponse.fail(RestErrorCode.CITY_SUPPLIER_DIFFER);
        }
        Boolean had = carBizCarInfoService.checkLicensePlates(licensePlates);
        if (!had) {//查看车牌号是否在车辆表存在
            return AjaxResponse.fail(RestErrorCode.BUS_NOT_EXIST);
        } else {
            if(driverId == null){
                had = carBizDriverInfoService.checkLicensePlates(licensePlates);
                if (had) {//查看车辆是否已被绑定
                    return AjaxResponse.fail(RestErrorCode.CAR_HAS_BIND);
                }
            }
        }
        had = carBizCarInfoService.validateCityAndSupplier(serviceCity, supplierId, licensePlates);
        if (!had) {
            return AjaxResponse.fail(RestErrorCode.CITY_SUPPLIER_CAR_DIFFER);
        }
        // 根据服务类型查找服务类型名称
        CarBizCarGroup carBizCarGroup = carBizCarGroupService.selectByPrimaryKey(groupId);
        if (carBizCarGroup == null) {
            return AjaxResponse.fail(RestErrorCode.GROUP_NOT_EXIST);
        }
        Integer cooperationType = carBizSupplier.getCooperationType();
        if(cooperationType!=null && cooperationType==5){
            if(age==null || StringUtils.isEmpty(currentAddress) || StringUtils.isEmpty(emergencyContactPerson)
                    || StringUtils.isEmpty(emergencyContactNumber) || StringUtils.isEmpty(superintendNo)
                    || StringUtils.isEmpty(drivingLicenseType) || drivingYears==null || StringUtils.isEmpty(archivesNo)
                    || StringUtils.isEmpty(issueDateStr) || StringUtils.isEmpty(expireDateStr) || StringUtils.isEmpty(driverlicensenumber) || StringUtils.isEmpty(nationality)
                    || StringUtils.isEmpty(householdregister) || StringUtils.isEmpty(nation) || StringUtils.isEmpty(marriage) || StringUtils.isEmpty(foreignlanguage)
                    || StringUtils.isEmpty(education) || StringUtils.isEmpty(firstdrivinglicensedate) || StringUtils.isEmpty(firstmeshworkdrivinglicensedate)
                    || StringUtils.isEmpty(corptype) || StringUtils.isEmpty(signdate) || StringUtils.isEmpty(signdateend) || StringUtils.isEmpty(contractdate)
                    || isxydriver==null || StringUtils.isEmpty(xyDriverNumber) || StringUtils.isEmpty(parttimejobdri) || StringUtils.isEmpty(phonetype)
                    || StringUtils.isEmpty(phonecorp) || StringUtils.isEmpty(maptype) || StringUtils.isEmpty(emergencycontactaddr) || StringUtils.isEmpty(driverlicenseissuingdatestart)
                    || StringUtils.isEmpty(driverlicenseissuingdateend) || StringUtils.isEmpty(driverlicenseissuingcorp) || StringUtils.isEmpty(driverlicenseissuingnumber)
                    || StringUtils.isEmpty(driverLicenseIssuingRegisterDate) || StringUtils.isEmpty(driverLicenseIssuingFirstDate) || StringUtils.isEmpty(driverLicenseIssuingGrantDate)
                    || StringUtils.isEmpty(birthDay) || StringUtils.isEmpty(houseHoldRegisterPermanent) || groupId == null
                    || StringUtils.isEmpty(memo) || StringUtils.isEmpty(bankCardBank) || StringUtils.isEmpty(bankCardNumber)){
                return AjaxResponse.fail(RestErrorCode.INFORMATION_NOT_COMPLETE);
            }

        }
        CarBizDriverInfoDTO carBizDriverInfoDTO = new CarBizDriverInfoDTO();
        carBizDriverInfoDTO.setDriverId(driverId);
        carBizDriverInfoDTO.setPasswordReset(passwordReset);
        carBizDriverInfoDTO.setPhone(phone);
        carBizDriverInfoDTO.setGender(gender);
        carBizDriverInfoDTO.setName(name);
        carBizDriverInfoDTO.setIdCardNo(idCardNo);
        carBizDriverInfoDTO.setServiceCity(serviceCity);
        carBizDriverInfoDTO.setSupplierId(supplierId);
        carBizDriverInfoDTO.setLicensePlates(licensePlates);
        carBizDriverInfoDTO.setStatus(status);
        carBizDriverInfoDTO.setAge(age);
        carBizDriverInfoDTO.setCurrentAddress(currentAddress);
        carBizDriverInfoDTO.setEmergencyContactPerson(emergencyContactPerson);
        carBizDriverInfoDTO.setEmergencyContactNumber(emergencyContactNumber);
        carBizDriverInfoDTO.setSuperintendNo(superintendNo);
        carBizDriverInfoDTO.setSuperintendUrl(superintendUrl);
        carBizDriverInfoDTO.setDrivingLicenseType(drivingLicenseType);
        carBizDriverInfoDTO.setDrivingYears(drivingYears);
        carBizDriverInfoDTO.setArchivesNo(archivesNo);
        try {
            if(StringUtils.isNotEmpty(issueDateStr)){
                carBizDriverInfoDTO.setIssueDate(new SimpleDateFormat("yyyy-MM-dd").parse(issueDateStr));
            }
            if(StringUtils.isNotEmpty(expireDateStr)){
                carBizDriverInfoDTO.setExpireDate(new SimpleDateFormat("yyyy-MM-dd").parse(expireDateStr));
            }
        } catch (ParseException e) {
            logger.info(LOGTAG + "司机driverId={},修改,时间转换异常={}", driverId, e.getMessage());
        }
//        carBizDriverInfoDTO.setIssueDate(issueDate);
//        carBizDriverInfoDTO.setExpireDate(expireDate);
        carBizDriverInfoDTO.setPhotosrct(photosrct);
        carBizDriverInfoDTO.setDriverlicensenumber(driverlicensenumber);
        carBizDriverInfoDTO.setDrivinglicenseimg(drivinglicenseimg);
        carBizDriverInfoDTO.setNationality(nationality);
        carBizDriverInfoDTO.setHouseholdregister(householdregister);
        carBizDriverInfoDTO.setNation(nation);
        carBizDriverInfoDTO.setMarriage(marriage);
        carBizDriverInfoDTO.setForeignlanguage(foreignlanguage);
        carBizDriverInfoDTO.setEducation(education);
        carBizDriverInfoDTO.setFirstdrivinglicensedate(firstdrivinglicensedate);
        carBizDriverInfoDTO.setFirstmeshworkdrivinglicensedate(firstmeshworkdrivinglicensedate);
        carBizDriverInfoDTO.setCorptype(corptype);
        carBizDriverInfoDTO.setSigndate(signdate);
        carBizDriverInfoDTO.setSigndateend(signdateend);
        carBizDriverInfoDTO.setContractdate(contractdate);
        carBizDriverInfoDTO.setIsxydriver(isxydriver);
        carBizDriverInfoDTO.setXyDriverNumber(xyDriverNumber);
        carBizDriverInfoDTO.setParttimejobdri(parttimejobdri);
        carBizDriverInfoDTO.setPhonetype(phonetype);
        carBizDriverInfoDTO.setPhonecorp(phonecorp);
        carBizDriverInfoDTO.setMaptype(maptype);
        carBizDriverInfoDTO.setEmergencycontactaddr(emergencycontactaddr);
        carBizDriverInfoDTO.setAssessment(assessment);
        carBizDriverInfoDTO.setDriverlicenseissuingdatestart(driverlicenseissuingdatestart);
        carBizDriverInfoDTO.setDriverlicenseissuingdateend(driverlicenseissuingdateend);
        carBizDriverInfoDTO.setDriverlicenseissuingcorp(driverlicenseissuingcorp);
        carBizDriverInfoDTO.setDriverlicenseissuingnumber(driverlicenseissuingnumber);
        carBizDriverInfoDTO.setDriverLicenseIssuingRegisterDate(driverLicenseIssuingRegisterDate);
        carBizDriverInfoDTO.setDriverLicenseIssuingFirstDate(driverLicenseIssuingFirstDate);
        carBizDriverInfoDTO.setDriverlicenseissuingnumber(driverlicenseissuingnumber);
        carBizDriverInfoDTO.setDriverLicenseIssuingRegisterDate(driverLicenseIssuingRegisterDate);
        carBizDriverInfoDTO.setDriverLicenseIssuingFirstDate(driverLicenseIssuingFirstDate);
        carBizDriverInfoDTO.setDriverLicenseIssuingGrantDate(driverLicenseIssuingGrantDate);
        carBizDriverInfoDTO.setBirthDay(birthDay);
        carBizDriverInfoDTO.setHouseHoldRegisterPermanent(houseHoldRegisterPermanent);
        carBizDriverInfoDTO.setGroupId(groupId);
        carBizDriverInfoDTO.setMemo(memo);
        carBizDriverInfoDTO.setBankCardBank(bankCardBank);
        carBizDriverInfoDTO.setBankCardNumber(bankCardNumber);

        Map<String, Object> resultMap = Maps.newHashMap();
        if (driverId != null) {
            logger.info(LOGTAG + "操作方式：编辑");
            // 司机获取派单的接口，是否可以修改
            Map<String, Object> updateDriverMap = carBizDriverInfoService.isUpdateDriver(driverId, phone);
//            if(updateDriverMap!=null && "2".equals(updateDriverMap.get("result").toString())){
//                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR, updateDriverMap.get("msg").toString());
//            }
            try {
                // 调用接口清除，key
                carBizDriverInfoService.flashDriverInfo(driverId);
            } catch (Exception e) {
                logger.info(LOGTAG + "司机driverId={},修改,调用清除接口异常={}", driverId, e.getMessage());
            }
            resultMap = carBizDriverInfoService.updateDriver(carBizDriverInfoDTO);
        }else{
            logger.info(LOGTAG + "操作方式：新建");
            resultMap = carBizDriverInfoService.saveDriver(carBizDriverInfoDTO);
        }
        if(resultMap!=null && "1".equals(resultMap.get("result").toString())){
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR, resultMap.get("msg").toString());
        }
        return AjaxResponse.success(resultMap);
    }

    /**
     * 修改司机状态信息 ,司机设置为无效后释放其绑定的车辆
     * @param driverId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/setInvalidDriver")
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE )
    } )
    public AjaxResponse setInvalidDriver(@Verify(param = "driverId", rule = "required") Integer driverId) {

        logger.info(LOGTAG + "司机driverId={},置为无效", driverId);
        CarBizDriverInfo carBizDriverInfo = carBizDriverInfoService.selectByPrimaryKey(driverId);
        if(carBizDriverInfo==null){
            return AjaxResponse.fail(RestErrorCode.DRIVER_NOT_EXIST);
        }
        // 司机获取派单的接口，是否可以修改
        Map<String, Object> updateDriverMap = carBizDriverInfoService.isUpdateDriver(driverId, carBizDriverInfo.getPhone());
//        if(updateDriverMap!=null && (int)updateDriverMap.get("result")==2){
//            return AjaxResponse.fail(RestErrorCode.CAR_API_ERROR, updateDriverMap.get("msg").toString());
//        }
        try {
            // 调用接口清除，key
            carBizDriverInfoService.flashDriverInfo(driverId);
        } catch (Exception e) {
            logger.info(LOGTAG + "司机driverId={},置为无效,调用清除接口异常={}", driverId, e.getMessage());
        }
        //允许修改
        // 获取当前用户Id
        carBizDriverInfo.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
        carBizDriverInfo.setStatus(0);
        CarBizDriverInfoDTO carBizDriverInfoDTO = BeanUtil.copyObject(carBizDriverInfo, CarBizDriverInfoDTO.class);
        int rtn = 0;
        try {
            rtn = carBizDriverInfoService.updateDriverByXiao(carBizDriverInfoDTO);
        } catch (Exception e) {
           logger.info(LOGTAG + "司机driverId={},置为无效error={}", driverId, e.getMessage());
        }
        if (rtn == 0) {
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }
        try {
            // 查询城市名称，供应商名称，服务类型，加盟类型
            carBizDriverInfoDTO = carBizDriverInfoService.getBaseStatis(carBizDriverInfoDTO);
            carBizDriverInfoService.sendDriverToMq(carBizDriverInfoDTO, "DELETE");
        } catch (Exception e) {
            logger.info(LOGTAG + "司机driverId={},置为无效error={}", driverId, e.getMessage());
        }
        return AjaxResponse.success(null);
    }

    /**
     * 重置IMEI
     * @param driverId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/resetIMEI")
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE )
    } )
    public AjaxResponse resetIMEI(@Verify(param = "driverId", rule = "required") Integer driverId) {

        logger.info(LOGTAG + "司机driverId={},重置imei", driverId);
        CarBizDriverInfo carBizDriverInfo = carBizDriverInfoService.selectByPrimaryKey(driverId);
        if(carBizDriverInfo==null){
            return AjaxResponse.fail(RestErrorCode.DRIVER_NOT_EXIST);
        }
        carBizDriverInfoService.resetIMEI(driverId);
        return AjaxResponse.success(null);
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
    @RequestMapping(value = "/batchInputDriverInfo")
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE )
    } )
    public AjaxResponse batchInputDriverInfo(@Verify(param = "cityId", rule = "required") Integer cityId,
                                             @Verify(param = "supplierId", rule = "required") Integer supplierId,
                                             Integer teamId, Integer teamGroupId,
                                             MultipartFile fileName,
                                             HttpServletRequest request, HttpServletResponse response) {

        if (fileName.isEmpty()) {
            logger.info("file is empty!");
            return AjaxResponse.fail(RestErrorCode.FILE_ERROR);
        }

        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap = carBizDriverInfoService.batchInputDriverInfo(cityId, supplierId, teamId, teamGroupId, fileName, request, response);
        //模板错误
        if(resultMap!=null && "-1".equals(resultMap.get("result").toString())){
            return AjaxResponse.fail(RestErrorCode.FILE_TRMPLATE_ERROR);
        }
        if(resultMap!=null && "0".equals(resultMap.get("result").toString())){
            return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, resultMap.get("download").toString());
        }
        return AjaxResponse.success(resultMap);
    }

    /**
     * 下载司机导入错误的信息
     * @param fileName
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/exportException")
    public AjaxResponse exportException(@Verify(param = "fileName", rule = "required") String fileName,
                                        HttpServletRequest request,HttpServletResponse response) {

        String path = request.getServletContext().getRealPath("/") + fileName;
        this.fileDownload(response,path);
        return AjaxResponse.success(null);
    }

    public void fileDownload(HttpServletResponse response,String path) {

        File file = new File(path);// path是根据日志路径和文件名拼接出来的
        String filename = file.getName();// 获取日志文件名称
        try {
            InputStream fis = new BufferedInputStream(new FileInputStream(path));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            response.reset();
            // 先去掉文件名称中的空格,然后转换编码格式为utf-8,保证不出现乱码,这个文件名称用于浏览器的下载框中自动显示的文件名
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.replaceAll(" ", "").getBytes("utf-8"),"iso8859-1"));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream os = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            os.write(buffer);// 输出文件
            os.flush();
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @ResponseBody
    @RequestMapping(value = "/selectByPhone")
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE )
    } )
    public AjaxResponse selectByPhone(  @Verify(param = "phone",rule="required") String phone) {
        logger.info(LOGTAG + "根据手机号查询司机信息phone={}", phone);
        try{
            CarBizDriverInfo carBizDriverInfo = carBizDriverInfoService.selectByPhone(phone);
            if(carBizDriverInfo==null){
                return AjaxResponse.fail(RestErrorCode.DRIVER_NOT_EXIST);
            }
            return AjaxResponse.success(carBizDriverInfo);
        }catch (Exception e){
            logger.error(LOGTAG + "根据手机号查询司机信息phone="+phone,e );
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }
    }
}
