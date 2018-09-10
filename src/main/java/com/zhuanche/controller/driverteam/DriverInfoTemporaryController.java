package com.zhuanche.controller.driverteam;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.BaseController;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.mdbcarmanage.CarBizDriverInfoTempDTO;
import com.zhuanche.entity.mdbcarmanage.CarBizDriverInfoTemp;
import com.zhuanche.entity.rentcar.CarBizCarGroup;
import com.zhuanche.entity.rentcar.CarBizCooperationType;
import com.zhuanche.serv.CarBizCarGroupService;
import com.zhuanche.serv.CarBizCooperationTypeService;
import com.zhuanche.serv.mdbcarmanage.CarBizDriverInfoTempService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wzq
 */
@Controller
@RequestMapping(value = "/driverInfoTemporary")
public class DriverInfoTemporaryController extends BaseController {

    private static final Logger log =  LoggerFactory.getLogger(DriverInfoTemporaryController.class);

    @Autowired
    private CarBizDriverInfoTempService carBizDriverInfoTempService;

    @Autowired
    private CarBizCarGroupService carBizCarGroupService;

    @Autowired
    private CarBizCooperationTypeService carBizCooperationTypeService;
    /**
     * 加盟商司机查询
     * @param page 页数
     * @param pageSize 条数
     * @param name 司机姓名
     * @param phone 手机号
     * @param licensePlates 车牌号
     * @param groupid 车型类型Id
     * @param serviceCityId 城市Id
     * @param supplierId 供应商Id
     * @param teamIds 车队Id
     * @param groupIds 小组Id
     * @param createDateBegin 开始时间
     * @param createDateEnd 结束时间
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/driverListData", method =  RequestMethod.GET )
    public AjaxResponse driverListData(@RequestParam(value = "page",defaultValue="1") Integer page,
                                       @RequestParam(value = "pageSize",defaultValue="10") Integer pageSize,
                                       @RequestParam(value = "name",required = false) String name,
                                       @RequestParam(value = "phone",required = false) String phone,
                                       @RequestParam(value = "licensePlates",required = false) String licensePlates,
                                       @RequestParam(value = "groupid",required = false) Integer groupid,
                                       @RequestParam(value = "serviceCityId",required = false) Integer serviceCityId,
                                       @RequestParam(value = "supplierId",required = false) Integer supplierId,
                                       @RequestParam(value = "teamIds",required = false) Integer teamIds,
                                       @RequestParam(value = "groupIds",required = false) String groupIds,
                                       @RequestParam(value = "createDateBegin",required = false) String createDateBegin,
                                       @RequestParam(value = "createDateEnd",required = false) String createDateEnd) {
        log.info("查询司机信息列表");
        CarBizDriverInfoTemp driverEntity = new CarBizDriverInfoTemp();
        //权限  当前用户的权限
        SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
        String sessionCityIds = StringUtils.join(user.getCityIds().toArray(), ",");
        String sessionSuppliers = StringUtils.join(user.getSupplierIds().toArray(), ",");
        String sessionTeamIds = StringUtils.join(user.getTeamIds().toArray(), ",");
        if(StringUtils.isNotBlank(name)){
            driverEntity.setName(name);
        }
        if(StringUtils.isNotBlank(phone)){
            driverEntity.setPhone(phone);
        }
        if(StringUtils.isNotBlank(licensePlates)){
            driverEntity.setLicensePlates(licensePlates);
        }
        if(groupid != null){
            driverEntity.setGroupid(groupid);
        }
        if(serviceCityId != null){
            driverEntity.setCities(serviceCityId.toString());
        }else{
            driverEntity.setCities(sessionCityIds);
        }
        if(supplierId != null){
            driverEntity.setSuppliers(supplierId.toString());
        }else{
            driverEntity.setSuppliers(sessionSuppliers);
        }
        if(teamIds != null){
            driverEntity.setTeamIds(teamIds.toString());
        }else{
            driverEntity.setTeamIds(sessionTeamIds);
        }
        if(groupIds != null){
            driverEntity.setGroupId(groupIds);
        }
        if(StringUtils.isNotBlank(createDateBegin)){
            driverEntity.setCreateDateBegin(createDateBegin);
        }
        if(StringUtils.isNotBlank(createDateEnd)){
            driverEntity.setCreateDateEnd(createDateEnd);
        }
        long total=0;
        PageHelper.startPage(page, pageSize,true);
        List<CarBizDriverInfoTemp> carBizDriverInfoTemps = null;
        try{
            carBizDriverInfoTemps = carBizDriverInfoTempService.queryForPageObject(driverEntity);
            PageInfo<CarBizDriverInfoTemp> pageInfo = new PageInfo<>(carBizDriverInfoTemps);
            total = pageInfo.getTotal();
        }finally {
            PageHelper.clearPage();
        }
        if(carBizDriverInfoTemps == null || carBizDriverInfoTemps.size() == 0){
            PageDTO pageDto = new PageDTO(page,pageSize,0,new ArrayList());
            return AjaxResponse.success(pageDto);
        }else{
            for (CarBizDriverInfoTemp driverInfoTemp:carBizDriverInfoTemps) {
                Map<String, Object> result = super.querySupplierName(driverInfoTemp.getCityId(), driverInfoTemp.getSupplierId());
                driverInfoTemp.setCityName((String)result.get("cityName"));
                driverInfoTemp.setSupplierName((String)result.get("supplierName"));
                //车型，加盟类型
                if(driverInfoTemp.getGroupid()!=null&&!"".equals(driverInfoTemp.getGroupid())){
                    CarBizCarGroup groupEntity = new CarBizCarGroup();
                    groupEntity.setGroupId(driverInfoTemp.getGroupid());
                    CarBizCarGroup queryForObject = carBizCarGroupService.queryForObject(groupEntity);
                    if(queryForObject!=null){
                        driverInfoTemp.setCarGroupName(queryForObject.getGroupName());
                    }
                }
                if(driverInfoTemp.getCooperationType()!=0){
                    CarBizCooperationType queryForObject = carBizCooperationTypeService.selectByPrimaryKey(driverInfoTemp.getCooperationType());
                    if(queryForObject!=null){
                        driverInfoTemp.setCooperationName(queryForObject.getCooperationName());
                    }
                }
            }
        }
        List<CarBizDriverInfoTempDTO> carBizDriverInfoTempDTOList = BeanUtil.copyList(carBizDriverInfoTemps,CarBizDriverInfoTempDTO.class);
        PageDTO pageDto = new PageDTO(page,pageSize,(int)total,carBizDriverInfoTempDTOList);
        return AjaxResponse.success(pageDto);
    }

    /**
     * 司机删除
     * @param driverIds 司机Id,以逗号分隔
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/deleteDriverInfo", method = RequestMethod.POST)
    public AjaxResponse deleteDriverInfo(@Verify(param = "driverIds",rule="required") String driverIds) {
        log.info("司机删除:deleteDriverInfo,参数:"+driverIds);
        return carBizDriverInfoTempService.delete(driverIds);
    }


    /**
     * 下载司机导入模板
     * @param request
     * @param response
     */
    @RequestMapping(value = "/fileDownloadDriverInfo",method =  RequestMethod.GET)
    public void fileDownloadDriverInfo(HttpServletRequest request, HttpServletResponse response) {
        String path = request.getSession().getServletContext().getRealPath("/upload")+File.separator+"IMPORTDRIVERINFO.xlsx";
        super.fileDownload(request,response,path);
    }

    /**
     * 司机导入
     * @param file
     * @param cityId 城市Id
     * @param supplierId 供应商Id
     * @param teamId 车队Id
     * @param groupId 小组Id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/importDriverInfo",method =  RequestMethod.POST)
    public AjaxResponse importDriverInfo(@RequestParam(value="fileName") MultipartFile file,
                                         @Verify(param = "cityId",rule="required") Integer cityId,
                                         @Verify(param = "supplierId",rule="required") Integer supplierId,
                                         @Verify(param = "teamId",rule="required") Integer teamId,
                                         @Verify(param = "groupId",rule="required") Integer groupId,
                                         HttpServletResponse response){
        try {
            log.info("司机导入");
            // 获取上传的文件的名称
            String filename = file.getOriginalFilename();
            //获取后缀
            String prefix=filename.substring(filename.lastIndexOf(".")+1);
            if (!"xls".equals(prefix) && !"xlsx".equals(prefix)) {
                return AjaxResponse.fail(RestErrorCode.FILE_TRMPLATE_ERROR);
            }
            return carBizDriverInfoTempService.importDriverInfo(file.getInputStream(),prefix,cityId,supplierId,teamId,groupId,response);
        } catch (IOException e) {
            e.printStackTrace();
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    /**
     * 下载司机导入模板(巴士)
     * @param request
     * @param response
     */
    @RequestMapping(value = "/fileDownloadDriverInfo4Bus",method =  RequestMethod.GET)
    public void fileDownloadDriverInfo4Bus(HttpServletRequest request, HttpServletResponse response) {
        String path = request.getSession().getServletContext().getRealPath("/upload")+File.separator+"IMPORTDRIVERINFO4BUS.xlsx";
        super.fileDownload(request,response,path);
    }

    /**
     * 司机导入(巴士司机)
     * @param file
     * @param cityId 城市Id
     * @param supplierId 供应商Id
     * @param teamId 车队Id
     * @param groupId 小组Id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/importDriverInfo4Bus",method =  RequestMethod.POST)
    public AjaxResponse importDriverInfo4Bus(@RequestParam(value="fileName") MultipartFile file,
                                             @Verify(param = "cityId",rule="required") Integer cityId,
                                             @Verify(param = "supplierId",rule="required") Integer supplierId,
                                             @Verify(param = "teamId",rule="required") Integer teamId,
                                             @Verify(param = "groupId",rule="required") Integer groupId){
        try {
            log.info("导入巴士司机");
            // 获取上传的文件的名称
            String filename = file.getOriginalFilename();
            //获取后缀
            String prefix=filename.substring(filename.lastIndexOf(".")+1);
            if (!"xls".equals(prefix) && !"xlsx".equals(prefix)) {
                return AjaxResponse.fail(RestErrorCode.FILE_TRMPLATE_ERROR);
            }
            return carBizDriverInfoTempService.importDriverInfo4Bus(file.getInputStream(),prefix,cityId,supplierId,teamId,groupId);
        } catch (IOException e) {
            e.printStackTrace();
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }

    }

    /**
     * 根据司机Id查询司机个人信息
     * @param driverId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/driverInfo",method =  RequestMethod.GET)
    public AjaxResponse driverInfo(@Verify(param = "driverId",rule="required") String driverId) {
        log.info("查询司机个人信息:driverInfo,司机Id:"+driverId);
        CarBizDriverInfoTemp entity = new CarBizDriverInfoTemp();
        entity.setDriverId(driverId);
        CarBizDriverInfoTemp rows = carBizDriverInfoTempService.queryForObject(entity);
        if (rows.getDrivingLicenseType() != null) {
            if (rows.getDrivingLicenseType().equals("1")) {
                rows.setDrivingTypeString("A1");
            } else if (rows.getDrivingLicenseType().equals("2")) {
                rows.setDrivingTypeString("A2");
            } else if (rows.getDrivingLicenseType().equals("3")) {
                rows.setDrivingTypeString("B1");
            } else if (rows.getDrivingLicenseType().equals("4")) {
                rows.setDrivingTypeString("B2");
            } else if (rows.getDrivingLicenseType().equals("5")) {
                rows.setDrivingTypeString("C1");
            } else if (rows.getDrivingLicenseType().equals("6")) {
                rows.setDrivingTypeString("C2");
            } else {
                rows.setDrivingTypeString(rows.getDrivingLicenseType());
            }
        }
        if(rows!=null){
            Map<String, Object> result = new HashMap<String, Object>();
            result = super.querySupplierName(rows.getCityId(), rows.getSupplierId());
            rows.setCityName((String)result.get("cityName"));
            rows.setServiceCity((String)result.get("cityName"));
            rows.setSupplierName((String)result.get("supplierName"));
            //车型，加盟类型
            if(rows.getGroupid()!=null&&!"".equals(rows.getGroupid())){
                CarBizCarGroup groupEntity = new CarBizCarGroup();
                groupEntity.setGroupId(rows.getGroupid());
                CarBizCarGroup queryForObject = carBizCarGroupService.queryForObject(groupEntity);
                if(queryForObject!=null){
                    rows.setCarGroupName(queryForObject.getGroupName());
                }
            }
            if(rows.getCooperationType()!=0){
                CarBizCooperationType queryForObject = carBizCooperationTypeService.selectByPrimaryKey(rows.getCooperationType());
                if(queryForObject!=null){
                    rows.setCooperationName(queryForObject.getCooperationName());
                }
            }
            return AjaxResponse.success(rows);
        }else{
            return AjaxResponse.fail(RestErrorCode.NOT_FOUND_RESULT);
        }
    }

    /**
     * 新建司机保存
     * @param name 司机名称
     * @param nationAlity 国籍
     * @param nation 民族
     * @param houseHoldRegisterPermanent 户口登记机关
     * @param houseHoldRegister 户籍所在地
     * @param idCardNo 身份证号
     * @param birthDay 出生日期(格式 yyyy-MM-dd)
     * @param gender 性别
     * @param phone 手机号
     * @param phoneType 司机手机型号
     * @param phoneCorp 司机手机运营商
     * @param age 年龄
     * @param marriage 婚姻状况
     * @param education 学历
     * @param foreignLanguage 外语能力
     * @param currentAddress 现住址
     * @param emergencyContactPerson 紧急联系人
     * @param emergencyContactNumber 紧急联系方式
     * @param emergencyContactAddr 紧急联系地址
     * @param drivingTypeString 驾照类型
     * @param issueDate 领证日期 (格式 yyyy-MM-dd)
     * @param expireDate 到期时间 (格式 yyyy-MM-dd)
     * @param superintendNo 服务监督码
     * @param superintendUrl 服务监督链接 (非必填项)
     * @param archivesNo 档案编号
     * @param drivingYears 驾龄
     * @param driverLicenseNumber 机动车驾驶证号
     * @param firstDrivingLicenseDate 初次领证日期 (格式 yyyy-MM-dd)
     * @param firstMeshworkDrivingLicenseDate 网络预约出租汽车驾驶员证初领日期 (格式 yyyy-MM-dd)
     * @param corpType 驾驶员合同（或协议）签署公司标识
     * @param signDate 签署日期 (格式 yyyy-MM-dd)
     * @param signDateEnd 合同(或协议) 到期时间 (格式 yyyy-MM-dd)
     * @param contractDate 有效合同时间  (格式 yyyy-MM-dd)
     * @param isXyDriver 是否巡游出租汽车驾驶员 (1:是;0:否)
     * @param xyDriverNumber 巡游出租汽车驾驶员资格证号
     * @param partTimeJobDri 是否专职司机 (是 否)
     * @param mapType 地图类型(高德 百度	其他)
     * @param assessment 评估
     * @param driverLicenseIssuingDateStart 资格证有效起始日期 (格式 yyyy-MM-dd)
     * @param driverLicenseIssuingDateEnd 资格证有效截止日期 (格式 yyyy-MM-dd)
     * @param driverLicenseIssuingCorp 网络预约出租汽车驾驶员证发证机构
     * @param driverLicenseIssuingNumber 网络预约出租汽车驾驶员资格证号
     * @param driverLicenseIssuingRegisterDate 注册日期 (格式 yyyy-MM-dd)
     * @param driverLicenseIssuingFirstDate 初次领取资格证日期 (格式 yyyy-MM-dd)
     * @param driverLicenseIssuingGrantDate 资格证发证日期 (格式 yyyy-MM-dd)
     * @param cityId 服务城市 Integer
     * @param supplierId 供应商 Integer
     * @param licensePlates 车牌号
     * @param groupId 服务类型 Integer
     * @param bankCardNumber 银行卡号
     * @param bankCardBank 银行卡开户行
     * @param memo 备注
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/addSave", method =  RequestMethod.POST)
    public AjaxResponse addSave(@Verify(param = "name",rule="required") String name,
                                    @Verify(param = "nationAlity",rule="required") String nationAlity,
                                    @Verify(param = "nation",rule="required") String nation,
                                    @Verify(param = "houseHoldRegisterPermanent",rule="required") String houseHoldRegisterPermanent,
                                    @Verify(param = "houseHoldRegister",rule="required") String houseHoldRegister,
                                    @Verify(param = "idCardNo",rule="required|idcard") String idCardNo,
                                    @Verify(param = "birthDay",rule="required") String birthDay,
                                    @Verify(param = "gender",rule="required") Integer gender,
                                    @Verify(param = "phone",rule="required|mobile") String phone,
                                    @Verify(param = "phoneType",rule="required") String phoneType,
                                    @Verify(param = "phoneCorp",rule="required") String phoneCorp,
                                    @Verify(param = "age",rule="required") Integer age,
                                    @Verify(param = "marriage",rule="required") String marriage,
                                    @Verify(param = "education",rule="required") String education,
                                    @Verify(param = "foreignLanguage",rule="required") String foreignLanguage,
                                    @Verify(param = "currentAddress",rule="required") String currentAddress,
                                    @Verify(param = "emergencyContactPerson",rule="required") String emergencyContactPerson,
                                    @Verify(param = "emergencyContactNumber",rule="required") String emergencyContactNumber,
                                    @Verify(param = "emergencyContactAddr",rule="required") String emergencyContactAddr,
                                    @Verify(param = "drivingTypeString",rule="required") String drivingTypeString,
                                    @Verify(param = "issueDate",rule="required") String issueDate,
                                    @Verify(param = "expireDate",rule="required") String expireDate,
                                    @Verify(param = "superintendNo",rule="required") String superintendNo,
                                    @RequestParam(value = "superintendUrl",required = false) String superintendUrl,
                                    @Verify(param = "archivesNo",rule="required") String archivesNo,
                                    @Verify(param = "drivingYears",rule="required") Integer drivingYears,
                                    @Verify(param = "driverLicenseNumber",rule="required") String driverLicenseNumber,
                                    @Verify(param = "firstDrivingLicenseDate",rule="required") String firstDrivingLicenseDate,
                                    @Verify(param = "firstMeshworkDrivingLicenseDate",rule="required") String firstMeshworkDrivingLicenseDate,
                                    @Verify(param = "corpType",rule="required") String corpType,
                                    @Verify(param = "signDate",rule="required") String signDate,
                                    @Verify(param = "signDateEnd",rule="required") String signDateEnd,
                                    @Verify(param = "contractDate",rule="required") String contractDate,
                                    @Verify(param = "isXyDriver",rule="required") Integer isXyDriver,
                                    @Verify(param = "xyDriverNumber",rule="required") String xyDriverNumber,
                                    @Verify(param = "partTimeJobDri",rule="required") String partTimeJobDri,
                                    @Verify(param = "mapType",rule="required") String mapType,
                                    @RequestParam(value = "assessment",required = false) String assessment,
                                    @Verify(param = "driverLicenseIssuingDateStart",rule="required") String driverLicenseIssuingDateStart,
                                    @Verify(param = "driverLicenseIssuingDateEnd",rule="required") String driverLicenseIssuingDateEnd,
                                    @Verify(param = "driverLicenseIssuingCorp",rule="required") String driverLicenseIssuingCorp,
                                    @Verify(param = "driverLicenseIssuingNumber",rule="required") String driverLicenseIssuingNumber,
                                    @Verify(param = "driverLicenseIssuingRegisterDate",rule="required") String driverLicenseIssuingRegisterDate,
                                    @Verify(param = "driverLicenseIssuingFirstDate",rule="required") String driverLicenseIssuingFirstDate,
                                    @Verify(param = "driverLicenseIssuingGrantDate",rule="required") String driverLicenseIssuingGrantDate,
                                    @Verify(param = "cityId",rule="required") Integer cityId,
                                    @Verify(param = "supplierId",rule="required") Integer supplierId,
                                    @Verify(param = "licensePlates",rule="required") String licensePlates,
                                    @Verify(param = "groupId",rule="required") Integer groupId,
                                    @RequestParam(value = "bankCardNumber",required = false) String bankCardNumber,
                                    @RequestParam(value = "bankCardBank",required = false) String bankCardBank,
                                    @RequestParam(value = "memo",required = false) String memo) {
        log.info("新建司机信息保存");
        CarBizDriverInfoTemp entity = new CarBizDriverInfoTemp();
        entity.setName(name);
        entity.setNationAlity(nationAlity);
        entity.setNation(nation);
        entity.setHouseHoldRegisterPermanent(houseHoldRegisterPermanent);
        entity.setHouseHoldRegister(houseHoldRegister);
        entity.setIdCardNo(idCardNo);
        entity.setBirthDay(birthDay);
        entity.setGender(gender);
        entity.setPhone(phone);
        entity.setPhoneType(phoneType);
        entity.setPhoneCorp(phoneCorp);
        entity.setAge(age);
        entity.setMarriage(marriage);
        entity.setEducation(education);
        entity.setForeignLanguage(foreignLanguage);
        entity.setCurrentAddress(currentAddress);
        entity.setEmergencyContactPerson(emergencyContactPerson);
        entity.setEmergencyContactNumber(emergencyContactNumber);
        entity.setEmergencyContactAddr(emergencyContactAddr);
        entity.setDrivingTypeString(drivingTypeString);
        entity.setIssueDate(issueDate);
        entity.setExpireDate(expireDate);
        entity.setSuperintendNo(superintendNo);
        entity.setSuperintendUrl(StringUtils.isNotBlank(superintendUrl)?superintendUrl:null);
        entity.setArchivesNo(archivesNo);
        entity.setDrivingYears(drivingYears);
        entity.setDriverLicenseNumber(driverLicenseNumber);
        entity.setFirstDrivingLicenseDate(firstDrivingLicenseDate);
        entity.setFirstMeshworkDrivingLicenseDate(firstMeshworkDrivingLicenseDate);
        entity.setCorpType(corpType);
        entity.setSignDate(signDate);
        entity.setSignDateEnd(signDateEnd);
        entity.setContractDate(contractDate);
        entity.setIsXyDriver(isXyDriver);
        entity.setXyDriverNumber(xyDriverNumber);
        entity.setPartTimeJobDri(partTimeJobDri);
        entity.setMapType(mapType);
        entity.setAssessment(StringUtils.isNotBlank(assessment)?superintendUrl:null);
        entity.setDriverLicenseIssuingDateStart(driverLicenseIssuingDateStart);
        entity.setDriverLicenseIssuingDateEnd(driverLicenseIssuingDateEnd);
        entity.setDriverLicenseIssuingCorp(driverLicenseIssuingCorp);
        entity.setDriverLicenseIssuingNumber(driverLicenseIssuingNumber);
        entity.setDriverLicenseIssuingRegisterDate(driverLicenseIssuingRegisterDate);
        entity.setDriverLicenseIssuingFirstDate(driverLicenseIssuingFirstDate);
        entity.setDriverLicenseIssuingGrantDate(driverLicenseIssuingGrantDate);
        entity.setCityId(cityId);
        entity.setSupplierId(supplierId);
        entity.setLicensePlates(licensePlates);
        entity.setGroupid(groupId);
        entity.setBankCardNumber(StringUtils.isNotBlank(bankCardNumber)?superintendUrl:null);
        entity.setBankCardBank(StringUtils.isNotBlank(bankCardBank)?superintendUrl:null);
        entity.setMemo(StringUtils.isNotBlank(memo)?superintendUrl:null);
        return carBizDriverInfoTempService.addSave(entity);
    }

    /**
     * 修改司机保存
     * @param driverId 司机Id
     * @param name 司机名称
     * @param nationAlity 国籍
     * @param nation 民族
     * @param houseHoldRegisterPermanent 户口登记机关
     * @param houseHoldRegister 户籍所在地
     * @param idCardNo 身份证号
     * @param birthDay 出生日期(格式 yyyy-MM-dd)
     * @param gender 性别
     * @param phone 手机号
     * @param phoneType 司机手机型号
     * @param phoneCorp 司机手机运营商
     * @param age 年龄
     * @param marriage 婚姻状况
     * @param education 学历
     * @param foreignLanguage 外语能力 Integer
     * @param currentAddress 现住址
     * @param emergencyContactPerson 紧急联系人
     * @param emergencyContactNumber 紧急联系方式
     * @param emergencyContactAddr 紧急联系地址
     * @param drivingTypeString 驾照类型
     * @param issueDate 领证日期 (格式 yyyy-MM-dd)
     * @param expireDate 到期时间 (格式 yyyy-MM-dd)
     * @param superintendNo 服务监督码
     * @param superintendUrl 服务监督链接 (非必填项)
     * @param archivesNo 档案编号
     * @param drivingYears 驾龄
     * @param driverLicenseNumber 机动车驾驶证号
     * @param firstDrivingLicenseDate 初次领证日期 (格式 yyyy-MM-dd)
     * @param firstMeshworkDrivingLicenseDate 网络预约出租汽车驾驶员证初领日期 (格式 yyyy-MM-dd)
     * @param corpType 驾驶员合同（或协议）签署公司标识
     * @param signDate 签署日期 (格式 yyyy-MM-dd)
     * @param signDateEnd 合同(或协议) 到期时间 (格式 yyyy-MM-dd)
     * @param contractDate 有效合同时间  (格式 yyyy-MM-dd)
     * @param isXyDriver 是否巡游出租汽车驾驶员 (1:是;0:否)
     * @param xyDriverNumber 巡游出租汽车驾驶员资格证号
     * @param partTimeJobDri 是否专职司机 (是 否)
     * @param mapType 地图类型(高德 百度	其他)
     * @param assessment 评估
     * @param driverLicenseIssuingDateStart 资格证有效起始日期 (格式 yyyy-MM-dd)
     * @param driverLicenseIssuingDateEnd 资格证有效截止日期 (格式 yyyy-MM-dd)
     * @param driverLicenseIssuingCorp 网络预约出租汽车驾驶员证发证机构
     * @param driverLicenseIssuingNumber 网络预约出租汽车驾驶员资格证号
     * @param driverLicenseIssuingRegisterDate 注册日期 (格式 yyyy-MM-dd)
     * @param driverLicenseIssuingFirstDate 初次领取资格证日期 (格式 yyyy-MM-dd)
     * @param driverLicenseIssuingGrantDate 资格证发证日期 (格式 yyyy-MM-dd)
     * @param cityId 服务城市 Integer
     * @param supplierId 供应商 Integer
     * @param licensePlates 车牌号
     * @param groupId 服务类型 Integer
     * @param bankCardNumber 银行卡号
     * @param bankCardBank 银行卡开户行
     * @param memo 备注
     * @param oldCityId 旧的城市Id
     * @param oldSupplierId 旧的供应商
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updateSave", method =  RequestMethod.POST)
    public AjaxResponse updateSave(@Verify(param = "driverId",rule="required") String driverId,
                                @Verify(param = "name",rule="required") String name,
                                @Verify(param = "nationAlity",rule="required") String nationAlity,
                                @Verify(param = "nation",rule="required") String nation,
                                @Verify(param = "houseHoldRegisterPermanent",rule="required") String houseHoldRegisterPermanent,
                                @Verify(param = "houseHoldRegister",rule="required") String houseHoldRegister,
                                @Verify(param = "idCardNo",rule="required|idcard") String idCardNo,
                                @Verify(param = "birthDay",rule="required") String birthDay,
                                @Verify(param = "gender",rule="required") Integer gender,
                                @Verify(param = "phone",rule="required|mobile") String phone,
                                @Verify(param = "phoneType",rule="required") String phoneType,
                                @Verify(param = "phoneCorp",rule="required") String phoneCorp,
                                @Verify(param = "age",rule="required") Integer age,
                                @Verify(param = "marriage",rule="required") String marriage,
                                @Verify(param = "education",rule="required") String education,
                                @Verify(param = "foreignLanguage",rule="required") String foreignLanguage,
                                @Verify(param = "currentAddress",rule="required") String currentAddress,
                                @Verify(param = "emergencyContactPerson",rule="required") String emergencyContactPerson,
                                @Verify(param = "emergencyContactNumber",rule="required") String emergencyContactNumber,
                                @Verify(param = "emergencyContactAddr",rule="required") String emergencyContactAddr,
                                @Verify(param = "drivingTypeString",rule="required") String drivingTypeString,
                                @Verify(param = "issueDate",rule="required") String issueDate,
                                @Verify(param = "expireDate",rule="required") String expireDate,
                                @Verify(param = "superintendNo",rule="required") String superintendNo,
                                @RequestParam(value = "superintendUrl",required = false) String superintendUrl,
                                @Verify(param = "archivesNo",rule="required") String archivesNo,
                                @Verify(param = "drivingYears",rule="required") Integer drivingYears,
                                @Verify(param = "driverLicenseNumber",rule="required") String driverLicenseNumber,
                                @Verify(param = "firstDrivingLicenseDate",rule="required") String firstDrivingLicenseDate,
                                @Verify(param = "firstMeshworkDrivingLicenseDate",rule="required") String firstMeshworkDrivingLicenseDate,
                                @Verify(param = "corpType",rule="required") String corpType,
                                @Verify(param = "signDate",rule="required") String signDate,
                                @Verify(param = "signDateEnd",rule="required") String signDateEnd,
                                @Verify(param = "contractDate",rule="required") String contractDate,
                                @Verify(param = "isXyDriver",rule="required") Integer isXyDriver,
                                @Verify(param = "xyDriverNumber",rule="required") String xyDriverNumber,
                                @Verify(param = "partTimeJobDri",rule="required") String partTimeJobDri,
                                @Verify(param = "mapType",rule="required") String mapType,
                                @RequestParam(value = "assessment",required = false) String assessment,
                                @Verify(param = "driverLicenseIssuingDateStart",rule="required") String driverLicenseIssuingDateStart,
                                @Verify(param = "driverLicenseIssuingDateEnd",rule="required") String driverLicenseIssuingDateEnd,
                                @Verify(param = "driverLicenseIssuingCorp",rule="required") String driverLicenseIssuingCorp,
                                @Verify(param = "driverLicenseIssuingNumber",rule="required") String driverLicenseIssuingNumber,
                                @Verify(param = "driverLicenseIssuingRegisterDate",rule="required") String driverLicenseIssuingRegisterDate,
                                @Verify(param = "driverLicenseIssuingFirstDate",rule="required") String driverLicenseIssuingFirstDate,
                                @Verify(param = "driverLicenseIssuingGrantDate",rule="required") String driverLicenseIssuingGrantDate,
                                @Verify(param = "cityId",rule="required") Integer cityId,
                                @Verify(param = "supplierId",rule="required") Integer supplierId,
                                @Verify(param = "licensePlates",rule="required") String licensePlates,
                                @Verify(param = "groupId",rule="required") Integer groupId,
                                @RequestParam(value = "bankCardNumber",required = false) String bankCardNumber,
                                @RequestParam(value = "bankCardBank",required = false) String bankCardBank,
                                @RequestParam(value = "memo",required = false) String memo,
                                @Verify(param = "oldCityId",rule="required") Integer oldCityId,
                                @Verify(param = "oldSupplierId",rule="required") Integer oldSupplierId) {
        log.info("修改司机信息保存,司机Id:"+driverId);
        CarBizDriverInfoTemp entity = new CarBizDriverInfoTemp();
        entity.setDriverId(driverId);
        entity.setName(name);
        entity.setNationAlity(nationAlity);
        entity.setNation(nation);
        entity.setHouseHoldRegisterPermanent(houseHoldRegisterPermanent);
        entity.setHouseHoldRegister(houseHoldRegister);
        entity.setIdCardNo(idCardNo);
        entity.setBirthDay(birthDay);
        entity.setGender(gender);
        entity.setPhone(phone);
        entity.setPhoneType(phoneType);
        entity.setPhoneCorp(phoneCorp);
        entity.setAge(age);
        entity.setMarriage(marriage);
        entity.setEducation(education);
        entity.setForeignLanguage(foreignLanguage);
        entity.setCurrentAddress(currentAddress);
        entity.setEmergencyContactPerson(emergencyContactPerson);
        entity.setEmergencyContactNumber(emergencyContactNumber);
        entity.setEmergencyContactAddr(emergencyContactAddr);
        entity.setDrivingTypeString(drivingTypeString);
        entity.setIssueDate(issueDate);
        entity.setExpireDate(expireDate);
        entity.setSuperintendNo(superintendNo);
        entity.setSuperintendUrl(StringUtils.isNotBlank(superintendUrl)?superintendUrl:null);
        entity.setArchivesNo(archivesNo);
        entity.setDrivingYears(drivingYears);
        entity.setDriverLicenseNumber(driverLicenseNumber);
        entity.setFirstDrivingLicenseDate(firstDrivingLicenseDate);
        entity.setFirstMeshworkDrivingLicenseDate(firstMeshworkDrivingLicenseDate);
        entity.setCorpType(corpType);
        entity.setSignDate(signDate);
        entity.setSignDateEnd(signDateEnd);
        entity.setContractDate(contractDate);
        entity.setIsXyDriver(isXyDriver);
        entity.setXyDriverNumber(xyDriverNumber);
        entity.setPartTimeJobDri(partTimeJobDri);
        entity.setMapType(mapType);
        entity.setAssessment(StringUtils.isNotBlank(assessment)?superintendUrl:null);
        entity.setDriverLicenseIssuingDateStart(driverLicenseIssuingDateStart);
        entity.setDriverLicenseIssuingDateEnd(driverLicenseIssuingDateEnd);
        entity.setDriverLicenseIssuingCorp(driverLicenseIssuingCorp);
        entity.setDriverLicenseIssuingNumber(driverLicenseIssuingNumber);
        entity.setDriverLicenseIssuingRegisterDate(driverLicenseIssuingRegisterDate);
        entity.setDriverLicenseIssuingFirstDate(driverLicenseIssuingFirstDate);
        entity.setDriverLicenseIssuingGrantDate(driverLicenseIssuingGrantDate);
        entity.setCityId(cityId);
        entity.setSupplierId(supplierId);
        entity.setLicensePlates(licensePlates);
        entity.setGroupid(groupId);
        entity.setBankCardNumber(StringUtils.isNotBlank(bankCardNumber)?superintendUrl:null);
        entity.setBankCardBank(StringUtils.isNotBlank(bankCardBank)?superintendUrl:null);
        entity.setMemo(StringUtils.isNotBlank(memo)?superintendUrl:null);
        entity.setOldCityId(oldCityId);
        entity.setOldSupplierId(oldSupplierId);
        return carBizDriverInfoTempService.updateSave(entity);
    }
}