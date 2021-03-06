package com.zhuanche.controller.driverteam;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.*;
import com.zhuanche.dto.mdbcarmanage.CarBizDriverInfoTempDTO;
import com.zhuanche.entity.mdbcarmanage.CarBizCarInfoTemp;
import com.zhuanche.entity.mdbcarmanage.CarBizDriverInfoTemp;
import com.zhuanche.entity.rentcar.CarBizCarGroup;
import com.zhuanche.entity.rentcar.CarBizCooperationType;
import com.zhuanche.serv.CarBizCarGroupService;
import com.zhuanche.serv.CarBizCooperationTypeService;
import com.zhuanche.serv.mdbcarmanage.CarBizDriverInfoTempService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.MobileOverlayUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
import java.util.*;

import static com.zhuanche.common.enums.MenuEnum.*;

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
     * ?????????????????????
     * @param page ??????
     * @param pageSize ??????
     * @param name ????????????
     * @param phone ?????????
     * @param licensePlates ?????????
     * @param groupid ????????????Id
     * @param serviceCityId ??????Id
     * @param supplierId ?????????Id
     * @param teamIds ??????Id
     * @param groupIds ??????Id
     * @param createDateBegin ????????????
     * @param createDateEnd ????????????
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/driverListData", method =  RequestMethod.GET )
	@RequiresPermissions(value = { "SupplierDriverEntry_look" } )
    @MasterSlaveConfigs(configs={ 
			@MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE ),
			@MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE)
	} )
    @RequestFunction(menu = DRIVER_JOIN_APPLY_LIST)
    public AjaxResponse driverListData(@RequestParam(value = "page",defaultValue="1") Integer page,
                                       @Verify(param = "pageSize",rule = "max(50)")
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
        log.info("????????????????????????");
        CarBizDriverInfoTemp driverEntity = new CarBizDriverInfoTemp();
        //??????  ?????????????????????
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
                //?????????????????????
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
        overLayPhone(carBizDriverInfoTempDTOList);
        return AjaxResponse.success(pageDto);
    }

    private void overLayPhone(List<CarBizDriverInfoTempDTO> result) {
        if (Objects.nonNull(result)){
            for (CarBizDriverInfoTempDTO carBizDriverInfoTempDTO : result) {
                carBizDriverInfoTempDTO.setPhone(MobileOverlayUtil.doOverlayPhone(carBizDriverInfoTempDTO.getPhone()));
            }
        }
    }

    /**
     * ????????????
     * @param driverIds ??????Id,???????????????
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/deleteDriverInfo", method = RequestMethod.POST)
    @RequestFunction(menu = DRIVER_JOIN_APPLY_DELETE)
    public AjaxResponse deleteDriverInfo(@Verify(param = "driverIds",rule="required") String driverIds) {
        log.info("????????????:deleteDriverInfo,??????:"+driverIds);
        return carBizDriverInfoTempService.delete(driverIds);
    }


    /**
     * ????????????????????????
     * @param request
     * @param response
     */
    @RequestMapping(value = "/fileDownloadDriverInfo",method =  RequestMethod.GET)
	@RequiresPermissions(value = { "SupplierDriverEntry_download" } )
    @RequestFunction(menu = DRIVER_JOIN_APPLY_TEMPLATE_DOWNLOAD)
    public void fileDownloadDriverInfo(HttpServletRequest request, HttpServletResponse response) {
        String path = request.getSession().getServletContext().getRealPath("/upload")+File.separator+"IMPORTDRIVERINFO.xlsx";
        super.fileDownload(request,response,path);
    }

    /**
     * ????????????
     * @param file
     * @param cityId ??????Id
     * @param supplierId ?????????Id
     * @param teamId ??????Id
     * @param groupId ??????Id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/importDriverInfo",method =  RequestMethod.POST)
	@RequiresPermissions(value = { "SupplierDriverEntry_import" } )
    @RequestFunction(menu = DRIVER_JOIN_APPLY_IMPORT)
    public AjaxResponse importDriverInfo(@RequestParam(value="fileName") MultipartFile file,
                                         @Verify(param = "cityId",rule="required") Integer cityId,
                                         @Verify(param = "supplierId",rule="required") Integer supplierId,
                                         @RequestParam(value = "teamId",required = false) Integer teamId,
                                         @RequestParam(value = "groupId",required = false) Integer groupId,
                                         HttpServletResponse response){
        try {
            log.info("????????????");
            // ??????????????????????????????
            String filename = file.getOriginalFilename();
            //????????????
            String prefix=filename.substring(filename.lastIndexOf(".")+1);
            if (!"xls".equals(prefix) && !"xlsx".equals(prefix)) {
                return AjaxResponse.fail(RestErrorCode.FILE_TRMPLATE_ERROR);
            }
            return carBizDriverInfoTempService.importDriverInfo(file.getInputStream(),prefix,cityId,supplierId,teamId,groupId,response);
        } catch (IOException e) {
            log.info("????????????",e);
             return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    /**
     * ????????????????????????(??????)
     * @param request
     * @param response
     */
    @RequestMapping(value = "/fileDownloadDriverInfo4Bus",method =  RequestMethod.GET)
    public void fileDownloadDriverInfo4Bus(HttpServletRequest request, HttpServletResponse response) {
        String path = request.getSession().getServletContext().getRealPath("/upload")+File.separator+"IMPORTDRIVERINFO4BUS.xlsx";
        super.fileDownload(request,response,path);
    }

    /**
     * ????????????(????????????)
     * @param file
     * @param cityId ??????Id
     * @param supplierId ?????????Id
     * @param teamId ??????Id
     * @param groupId ??????Id
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
            log.info("??????????????????");
            // ??????????????????????????????
            String filename = file.getOriginalFilename();
            //????????????
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
     * ????????????Id????????????????????????
     * @param driverId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/driverInfo",method =  RequestMethod.GET)
    @MasterSlaveConfigs(configs={ 
			@MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE ),
			@MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE)
	} )
    @RequestFunction(menu = DRIVER_JOIN_APPLY_UPDATE_INFO)
    public AjaxResponse driverInfo(@Verify(param = "driverId",rule="required") String driverId) {
        log.info("????????????????????????:driverInfo,??????Id:"+driverId);
        CarBizDriverInfoTemp entity = new CarBizDriverInfoTemp();
        entity.setDriverId(driverId);
        CarBizDriverInfoTemp rows = carBizDriverInfoTempService.queryForObject(entity);
        if (null!=rows && rows.getDrivingLicenseType() != null) {
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
            //?????????????????????
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
     * ??????????????????
     * @param name ????????????
     * @param nationAlity ??????
     * @param nation ??????
     * @param houseHoldRegisterPermanent ??????????????????
     * @param houseHoldRegister ???????????????
     * @param idCardNo ????????????
     * @param birthDay ????????????(?????? yyyy-MM-dd)
     * @param gender ??????
     * @param phone ?????????
     * @param phoneType ??????????????????
     * @param phoneCorp ?????????????????????
     * @param age ??????
     * @param marriage ????????????
     * @param education ??????
     * @param foreignLanguage ????????????
     * @param currentAddress ?????????
     * @param emergencyContactPerson ???????????????
     * @param emergencyContactNumber ??????????????????
     * @param emergencyContactAddr ??????????????????
     * @param drivingTypeString ????????????
     * @param issueDate ???????????? (?????? yyyy-MM-dd)
     * @param expireDate ???????????? (?????? yyyy-MM-dd)
     * @param superintendNo ???????????????
     * @param superintendUrl ?????????????????? (????????????)
     * @param archivesNo ????????????
     * @param drivingYears ??????
     * @param driverLicenseNumber ?????????????????????
     * @param firstDrivingLicenseDate ?????????????????? (?????? yyyy-MM-dd)
     * @param firstMeshworkDrivingLicenseDate ???????????????????????????????????????????????? (?????? yyyy-MM-dd)
     * @param corpType ????????????????????????????????????????????????
     * @param signDate ???????????? (?????? yyyy-MM-dd)
     * @param signDateEnd ??????(?????????) ???????????? (?????? yyyy-MM-dd)
     * @param contractDate ??????????????????  (?????? yyyy-MM-dd)
     * @param isXyDriver ????????????????????????????????? (1:???;0:???)
     * @param xyDriverNumber ???????????????????????????????????????
     * @param partTimeJobDri ?????????????????? (??? ???)
     * @param mapType ????????????(?????? ??????	??????)
     * @param assessment ??????
     * @param driverLicenseIssuingDateStart ??????????????????????????? (?????? yyyy-MM-dd)
     * @param driverLicenseIssuingDateEnd ??????????????????????????? (?????? yyyy-MM-dd)
     * @param driverLicenseIssuingCorp ????????????????????????????????????????????????
     * @param driverLicenseIssuingNumber ?????????????????????????????????????????????
     * @param driverLicenseIssuingRegisterDate ???????????? (?????? yyyy-MM-dd)
     * @param driverLicenseIssuingFirstDate ??????????????????????????? (?????? yyyy-MM-dd)
     * @param driverLicenseIssuingGrantDate ????????????????????? (?????? yyyy-MM-dd)
     * @param cityId ???????????? Integer
     * @param supplierId ????????? Integer
     * @param licensePlates ?????????
     * @param groupId ???????????? Integer
     * @param bankCardNumber ????????????
     * @param bankCardBank ??????????????????
     * @param memo ??????
     * @param photoSrct ????????????
     * @param drivingLicenseImg ??????????????????
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/addSave", method =  RequestMethod.POST)
    @RequestFunction(menu = DRIVER_JOIN_APPLY_ADD)
    public AjaxResponse addSave(@Verify(param = "name",rule="required") String name,
                                    @Verify(param = "nationAlity",rule="required") String nationAlity,
                                    @Verify(param = "nation",rule="required") String nation,
                                    @Verify(param = "houseHoldRegisterPermanent",rule="required") String houseHoldRegisterPermanent,
                                    @Verify(param = "houseHoldRegister",rule="required") String houseHoldRegister,
                                    @Verify(param = "idCardNo",rule="required") String idCardNo,
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
                                    @Verify(param = "emergencyContactPerson",rule="required|maxLength(100)") String emergencyContactPerson,
                                    @Verify(param = "emergencyContactNumber",rule="required|maxLength(11)") String emergencyContactNumber,
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
                                    @Verify(param = "bankCardNumber",rule="maxLength(20)") String bankCardNumber,
                                    @RequestParam(value = "bankCardBank",required = false) String bankCardBank,
                                    @RequestParam(value = "memo",required = false) String memo,
                                    @RequestParam(value = "photoSrct",required = false) String photoSrct,
                                    @RequestParam(value = "drivingLicenseImg",required = false) String drivingLicenseImg) {
        log.info("????????????????????????");
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
        entity.setDrivingLicenseType(drivingTypeString);
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
        entity.setAssessment(StringUtils.isNotBlank(assessment)?assessment:null);
        entity.setDriverLicenseIssuingDateStart(driverLicenseIssuingDateStart);
        entity.setDriverLicenseIssuingDateEnd(driverLicenseIssuingDateEnd);
        entity.setDriverLicenseIssuingCorp(driverLicenseIssuingCorp);
        entity.setDriverLicenseIssuingNumber(driverLicenseIssuingNumber);
        entity.setDriverLicenseIssuingRegisterDate(driverLicenseIssuingRegisterDate);
        entity.setDriverLicenseIssuingFirstDate(driverLicenseIssuingFirstDate);
        entity.setDriverLicenseIssuingGrantDate(driverLicenseIssuingGrantDate);
        entity.setServiceCity(String.valueOf(cityId));
        entity.setSupplierId(supplierId);
        entity.setLicensePlates(licensePlates);
       // entity.setGroupId(String.valueOf(groupId));
        entity.setGroupId("43"); //??????????????????????????????????????????????????????????????????????????????????????? fht 2019-09-01
        entity.setBankCardNumber(StringUtils.isNotBlank(bankCardNumber)?bankCardNumber:null);
        entity.setBankCardBank(StringUtils.isNotBlank(bankCardBank)?bankCardBank:null);
        entity.setMemo(StringUtils.isNotBlank(memo)?memo:null);
        entity.setAddress(currentAddress);
        entity.setPhotoSrct(StringUtils.isNotBlank(photoSrct)?photoSrct:null);
        entity.setDrivingLicenseImg(StringUtils.isNotBlank(drivingLicenseImg)?drivingLicenseImg:null);
        return carBizDriverInfoTempService.addSave(entity);
    }

    /**
     * ??????????????????
     * @param driverId ??????Id
     * @param name ????????????
     * @param nationAlity ??????
     * @param nation ??????
     * @param houseHoldRegisterPermanent ??????????????????
     * @param houseHoldRegister ???????????????
     * @param idCardNo ????????????
     * @param birthDay ????????????(?????? yyyy-MM-dd)
     * @param gender ??????
     * @param phone ?????????
     * @param phoneType ??????????????????
     * @param phoneCorp ?????????????????????
     * @param age ??????
     * @param marriage ????????????
     * @param education ??????
     * @param foreignLanguage ???????????? Integer
     * @param currentAddress ?????????
     * @param emergencyContactPerson ???????????????
     * @param emergencyContactNumber ??????????????????
     * @param emergencyContactAddr ??????????????????
     * @param drivingTypeString ????????????
     * @param issueDate ???????????? (?????? yyyy-MM-dd)
     * @param expireDate ???????????? (?????? yyyy-MM-dd)
     * @param superintendNo ???????????????
     * @param superintendUrl ?????????????????? (????????????)
     * @param archivesNo ????????????
     * @param drivingYears ??????
     * @param driverLicenseNumber ?????????????????????
     * @param firstDrivingLicenseDate ?????????????????? (?????? yyyy-MM-dd)
     * @param firstMeshworkDrivingLicenseDate ???????????????????????????????????????????????? (?????? yyyy-MM-dd)
     * @param corpType ????????????????????????????????????????????????
     * @param signDate ???????????? (?????? yyyy-MM-dd)
     * @param signDateEnd ??????(?????????) ???????????? (?????? yyyy-MM-dd)
     * @param contractDate ??????????????????  (?????? yyyy-MM-dd)
     * @param isXyDriver ????????????????????????????????? (1:???;0:???)
     * @param xyDriverNumber ???????????????????????????????????????
     * @param partTimeJobDri ?????????????????? (??? ???)
     * @param mapType ????????????(?????? ??????	??????)
     * @param assessment ??????
     * @param driverLicenseIssuingDateStart ??????????????????????????? (?????? yyyy-MM-dd)
     * @param driverLicenseIssuingDateEnd ??????????????????????????? (?????? yyyy-MM-dd)
     * @param driverLicenseIssuingCorp ????????????????????????????????????????????????
     * @param driverLicenseIssuingNumber ?????????????????????????????????????????????
     * @param driverLicenseIssuingRegisterDate ???????????? (?????? yyyy-MM-dd)
     * @param driverLicenseIssuingFirstDate ??????????????????????????? (?????? yyyy-MM-dd)
     * @param driverLicenseIssuingGrantDate ????????????????????? (?????? yyyy-MM-dd)
     * @param cityId ???????????? Integer
     * @param supplierId ????????? Integer
     * @param licensePlates ?????????
     * @param groupId ???????????? Integer
     * @param bankCardNumber ????????????
     * @param bankCardBank ??????????????????
     * @param memo ??????
     * @param oldCityId ????????????Id
     * @param oldSupplierId ???????????????
     * @param oldPhone ???????????????
     * @param oldLicensePlates ???????????????
     * @param photoSrct ????????????
     * @param drivingLicenseImg ??????????????????
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updateSave", method =  RequestMethod.POST)
    @RequestFunction(menu = DRIVER_JOIN_APPLY_UPDATE)
    public AjaxResponse updateSave(@Verify(param = "driverId",rule="required") String driverId,
                                @Verify(param = "name",rule="required") String name,
                                @Verify(param = "nationAlity",rule="required") String nationAlity,
                                @Verify(param = "nation",rule="required") String nation,
                                @Verify(param = "houseHoldRegisterPermanent",rule="required") String houseHoldRegisterPermanent,
                                @Verify(param = "houseHoldRegister",rule="required") String houseHoldRegister,
                                @Verify(param = "idCardNo",rule="required") String idCardNo,
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
                                @Verify(param = "emergencyContactPerson",rule="required|maxLength(100)") String emergencyContactPerson,
                                @Verify(param = "emergencyContactNumber",rule="required|maxLength(11)") String emergencyContactNumber,
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
                                @RequestParam(value = "mapType",required = false) String mapType,
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
                                @Verify(param = "bankCardNumber",rule="maxLength(20)") String bankCardNumber,
                                @RequestParam(value = "bankCardBank",required = false) String bankCardBank,
                                @RequestParam(value = "memo",required = false) String memo,
                                @Verify(param = "oldCityId",rule="required") Integer oldCityId,
                                @Verify(param = "oldSupplierId",rule="required") Integer oldSupplierId,
                                @Verify(param = "oldPhone",rule="required") String oldPhone,
                                @Verify(param = "oldLicensePlates",rule="required") String oldLicensePlates,
                                @RequestParam(value = "photoSrct",required = false) String photoSrct,
                                @RequestParam(value = "drivingLicenseImg",required = false) String drivingLicenseImg) {
        log.info("????????????????????????,??????Id:"+driverId);
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
        entity.setDrivingLicenseType(drivingTypeString);
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
        entity.setAssessment(StringUtils.isNotBlank(assessment)?assessment:null);
        entity.setDriverLicenseIssuingDateStart(driverLicenseIssuingDateStart);
        entity.setDriverLicenseIssuingDateEnd(driverLicenseIssuingDateEnd);
        entity.setDriverLicenseIssuingCorp(driverLicenseIssuingCorp);
        entity.setDriverLicenseIssuingNumber(driverLicenseIssuingNumber);
        entity.setDriverLicenseIssuingRegisterDate(driverLicenseIssuingRegisterDate);
        entity.setDriverLicenseIssuingFirstDate(driverLicenseIssuingFirstDate);
        entity.setDriverLicenseIssuingGrantDate(driverLicenseIssuingGrantDate);
        entity.setServiceCity(String.valueOf(cityId));
        entity.setSupplierId(supplierId);
        entity.setLicensePlates(licensePlates);
        entity.setGroupId(String.valueOf(groupId));
        entity.setBankCardNumber(StringUtils.isNotBlank(bankCardNumber)?bankCardNumber:null);
        entity.setBankCardBank(StringUtils.isNotBlank(bankCardBank)?bankCardBank:null);
        entity.setMemo(StringUtils.isNotBlank(memo)?memo:null);
        entity.setOldCityId(oldCityId);
        entity.setOldSupplierId(oldSupplierId);
        entity.setOldPhone(oldPhone);
        entity.setOldLicensePlates(oldLicensePlates);
        entity.setAddress(currentAddress);
        entity.setPhotoSrct(StringUtils.isNotBlank(photoSrct)?photoSrct:null);
        entity.setDrivingLicenseImg(StringUtils.isNotBlank(drivingLicenseImg)?drivingLicenseImg:null);
        return carBizDriverInfoTempService.updateSave(entity);
    }


    /**
     * ????????????????????????
     * @param cityId ??????Id
     * @param supplierId ?????????Id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/licensePlatesTempList")
    @RequestFunction(menu = DRIVER_JOIN_APPLY_LICENSE_PLATES)
    public Object licensePlatesList(@Verify(param = "cityId", rule = "required") Integer cityId,
                                    @Verify(param = "supplierId", rule = "required") Integer supplierId) {
        Map<String, Object> map=new HashMap<String, Object>();
        map.put("cityId",cityId);
        map.put("supplierId",supplierId);
        List<CarBizCarInfoTemp> carList = carBizDriverInfoTempService.licensePlatesNotDriverIdList(map);
        return AjaxResponse.success(carList);
    }
}