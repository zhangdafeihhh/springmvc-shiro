package com.zhuanche.controller.driver;


import com.alibaba.fastjson.JSON;
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
import com.zhuanche.entity.rentcar.*;
import com.zhuanche.serv.*;
import com.zhuanche.serv.driverteam.CarDriverTeamService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
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
import java.io.File;
import java.util.*;

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
    public AjaxResponse exportDriverList(String name, String phone, String licensePlates, Integer status, Integer cityId, Integer supplierId,
                                         Integer teamId, Integer teamGroupId, Integer groupId, Integer cooperationType,
                                         String imei, String idCardNo, Integer isImage,
                                         HttpServletRequest request) {

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
                driver = carBizDriverInfoService.getBaseStatis(driver);
            }
        }
        try {
            //TODO 待完善
            Workbook wb = carBizDriverInfoService.exportExcel(list,request.getRealPath("/")+File.separator+"template"+File.separator+"driver_info.xlsx");
//        super.exportExcelFromTemplet(request, response, wb, new String("司机信息列表".getBytes("utf-8"), "iso8859-1"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return AjaxResponse.success(list);
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
     * @param driverInfo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveDriver")
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE )
    } )
    public AjaxResponse saveDriver(@Verify(param = "driverInfo", rule = "required") String driverInfo) {

        CarBizDriverInfoDTO carBizDriverInfo = JSON.parseObject(driverInfo, CarBizDriverInfoDTO.class);
        if(carBizDriverInfo==null){
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
        }
        Integer driverId = carBizDriverInfo.getDriverId();
        //判断一些基础信息是否正确
        AjaxResponse ajaxResponse = carBizDriverInfoService.validateCarDriverInfo(carBizDriverInfo);
        if(ajaxResponse.getCode()!=0){
            return ajaxResponse;
        }
        Map<String, Object> resultMap = Maps.newHashMap();
        if (driverId == null) {
            logger.info(LOGTAG + "操作方式：编辑");
            // 司机获取派单的接口，是否可以修改
            Map<String, Object> updateDriverMap = carBizDriverInfoService.isUpdateDriver(driverId, carBizDriverInfo.getPhone());
            if(updateDriverMap!=null && "2".equals(updateDriverMap.get("result").toString())){
                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR, updateDriverMap.get("msg").toString());
            }
            // 调用接口清除，key
            carBizDriverInfoService.flashDriverInfo(driverId);
            resultMap = carBizDriverInfoService.updateDriver(carBizDriverInfo);
        }else{
            logger.info(LOGTAG + "操作方式：新建");
            resultMap = carBizDriverInfoService.saveDriver(carBizDriverInfo);
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
        if(updateDriverMap!=null && "2".equals(updateDriverMap.get("result").toString())){
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR, updateDriverMap.get("msg").toString());
        }
        // 调用接口清除，key
        carBizDriverInfoService.flashDriverInfo(driverId);
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
        return AjaxResponse.success(true);
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
        return AjaxResponse.success(true);
    }

    /**
     * 导入司机信息
     * @param cityId
     * @param supplierId
     * @param teamId
     * @param teamGroupId
     * @param file
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
                                             @Verify(param = "file", rule = "required") MultipartFile file,
                                             HttpServletRequest request) {

        if (file.isEmpty()) {
            logger.info("file is empty!");
            return AjaxResponse.fail(RestErrorCode.FILE_ERROR);
        }

        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap = carBizDriverInfoService.batchInputDriverInfo(cityId, supplierId, teamId, teamGroupId, file, request);
        //模板错误
        if(resultMap!=null && "-1".equals(resultMap.get("result").toString())){
            return AjaxResponse.fail(RestErrorCode.FILE_TRMPLATE_ERROR);
        }
        //
        return AjaxResponse.success(true);
    }
}
