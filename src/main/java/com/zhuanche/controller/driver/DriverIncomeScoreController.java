package com.zhuanche.controller.driver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.rocketmq.ExcelProducerDouble;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.common.web.Verify;
import com.zhuanche.constant.Constants;
import com.zhuanche.dto.driver.DriverVoEntity;
import com.zhuanche.dto.mdbcarmanage.ScoreDetailDTO;
import com.zhuanche.dto.rentcar.*;
import com.zhuanche.serv.CarBizDriverInfoService;
import com.zhuanche.serv.DriverDispatchScoreService;
import com.zhuanche.serv.DriverIncomeScoreService;
import com.zhuanche.serv.driverteam.CarDriverTeamService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.DateUtils;
import com.zhuanche.util.MobileOverlayUtil;
import mapper.mdbcarmanage.ex.CarAdmUserExMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;

import static com.zhuanche.common.enums.MenuEnum.DRIVER_INFO_LIST;

/**
 * 司机收入分
 */
@Controller
@RequestMapping("/driverIncomeScore")
public class DriverIncomeScoreController {
    private static final Logger logger = LoggerFactory.getLogger(DriverIncomeScoreController.class);
    private static final String LOGTAG = "[司机收入分信息]: ";

    @Autowired
    private CarBizDriverInfoService carBizDriverInfoService;

    @Autowired
    private CarDriverTeamService carDriverTeamService;

    @Autowired
    private DriverIncomeScoreService driverIncomeScoreService;

    @Autowired
    private CarAdmUserExMapper carAdmUserExMapper;

    @Resource
    private DriverDispatchScoreService driverDispatchScoreService;

    /**
     * 分页查询司机派单分概括<br>
     *     派单分2.0
     *
     * @param driverId      司机id
     * @param phone         司机手机号
     * @param licensePlates 车牌号
     * @param cityId        城市ID
     * @param supplierId    供应商ID
     * @param teamId        车队ID
     * @param page          起始页，默认0
     * @param pageSize      取N条，默认20
     * @return AjaxResponse
     */
    @ResponseBody
    @RequestMapping(value = "/queryPageDriverDispatchScore")
    @RequestFunction(menu = DRIVER_INFO_LIST)
    public AjaxResponse queryPageDriverDispatchScore(Integer driverId, String phone,
                                                     String licensePlates, Integer cityId,
                                                     Integer supplierId, Integer teamId,
                                                     @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                     @Verify(param = "pageSize",rule = "max(50)") @RequestParam(value = "pageSize",defaultValue = "20") Integer pageSize){

        CarBizDriverInfoDTO carBizDriverInfoDTO = new CarBizDriverInfoDTO();
        carBizDriverInfoDTO.setDriverId(driverId);
        carBizDriverInfoDTO.setPhone(phone);
        carBizDriverInfoDTO.setLicensePlates(licensePlates);
        carBizDriverInfoDTO.setServiceCity(cityId);
        carBizDriverInfoDTO.setSupplierId(supplierId);
        carBizDriverInfoDTO.setTeamId(teamId);
        PageDTO pageDTO = driverDispatchScoreService.queryPageDriverDispatchScore(page,pageSize,carBizDriverInfoDTO);
        return AjaxResponse.success(pageDTO);
    }

    /**
     * 查询司机派单分每日更新记录<br>
     *     注意，只能查询3个月内的数据
     *
     * @param driverId 司机ID
     * @param driverName 司机姓名
     * @param driverPhone 司机手机号
     * @param updateTime 开始日期,格式：YYYY-MM-DD
     * @param endUpdateTime 结束日期,格式：YYYY-MM-DD
     * @return AjaxResponse
     */
    @ResponseBody
    @RequestMapping(value = "/queryDispatchScoreDailyUpdateRecord")
    @RequestFunction(menu = DRIVER_INFO_LIST)
    public AjaxResponse queryDispatchScoreDailyUpdateRecord(Integer driverId,String driverName,String driverPhone,String updateTime,String endUpdateTime){
        DriverDispatchScoreDailyUpdateRecordQuery driverDispatchScoreDailyUpdateRecordQuery = new DriverDispatchScoreDailyUpdateRecordQuery();
        driverDispatchScoreDailyUpdateRecordQuery.setDriverId(driverId);
        driverDispatchScoreDailyUpdateRecordQuery.setDriverName(driverName);
        driverDispatchScoreDailyUpdateRecordQuery.setDriverPhone(driverPhone);
        driverDispatchScoreDailyUpdateRecordQuery.setUpdateTime(updateTime);
        driverDispatchScoreDailyUpdateRecordQuery.setEndUpdateTime(endUpdateTime);
        List<DriverDispatchScoreDailyUpdateRecord> list = driverDispatchScoreService.queryDispatchScoreDailyUpdateRecord(driverDispatchScoreDailyUpdateRecordQuery);
        return AjaxResponse.success(list);
    }

    /**
     * 分页查询司机派单分明细记录
     *
     * @param driverId 司机ID
     * @param driverName 司机姓名
     * @param driverPhone 司机手机号
     * @param parentDispatchScoreType 父派单分分类
     * @param type 具体类型
     * @param orderNo 关联单号
     * @param startDate 开始日期,格式：YYYY-MM-DD
     * @param endDate 结束日期,格式：YYYY-MM-DD
     * @param page 页码
     * @param pageSize 每页条数
     * @return AjaxResponse
     */
    @ResponseBody
    @RequestMapping(value = "/queryPageDriverDispatchScoreDetailRecord")
    @RequestFunction(menu = DRIVER_INFO_LIST)
    public AjaxResponse queryPageDriverDispatchScoreDetailRecord(Integer driverId,String driverName,String driverPhone,
                                                                 String parentDispatchScoreType,String type,
                                                                 String orderNo, String startDate,String endDate,
                                                                 @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                 @RequestParam(value = "pageSize",defaultValue = "20") Integer pageSize){
        DriverDispatchScoreDetailRecordQuery queryCondition = new DriverDispatchScoreDetailRecordQuery();
        queryCondition.setDriverId(driverId);
        queryCondition.setDriverName(driverName);
        queryCondition.setDriverPhone(driverPhone);
        queryCondition.setParentDispatchScoreType(parentDispatchScoreType);
        queryCondition.setType(type);
        queryCondition.setOrderNo(orderNo);
        queryCondition.setStartDate(startDate);
        queryCondition.setEndDate(endDate);
        queryCondition.setPage(page);
        queryCondition.setPageSize(pageSize);
        PageDTO pageDTO = driverDispatchScoreService.queryPageDriverDispatchScoreDetailRecord(queryCondition);
        return AjaxResponse.success(pageDTO);
    }

    /**
     * 查询司机服务分计算明细
     *
     * @param driverId 司机ID
     * @param driverName 司机姓名
     * @param driverPhone 司机手机号
     * @param ownershipDate 所属日期，示例：2020-02-28
     * @param serviceScore 当前ownershipDate总服务分
     * @return AjaxResponse
     */
    @ResponseBody
    @RequestMapping(value = "/queryDriverServiceScoreCalculateDetail")
    @RequestFunction(menu = DRIVER_INFO_LIST)
    public AjaxResponse queryDriverServiceScoreCalculateDetail(Integer driverId,String driverName,String driverPhone,
                                                                                        String ownershipDate,String serviceScore){
        DriverServiceScoreCalculateGeneralize generalize = driverDispatchScoreService.queryDriverServiceScoreCalculateDetail(driverId,driverName,driverPhone,ownershipDate,serviceScore);
        return AjaxResponse.success(generalize);
    }

    /**
     * 查询司机时长分计算明细
     *
     * @param driverId 司机ID
     * @param driverName 司机姓名
     * @param driverPhone 司机手机号
     * @param day 所属日期，示例：2020-02-28
     * @param timeLengthScore 当前day的时长分
     * @return AjaxResponse
     */
    @ResponseBody
    @RequestMapping(value = "/queryDriverTimeLengthScoreCalculateDetail")
    @RequestFunction(menu = DRIVER_INFO_LIST)
    public AjaxResponse queryDriverTimeLengthScoreCalculateDetail(Integer driverId, String driverName, String driverPhone,
                                                                                              String day,String timeLengthScore){
        DriverTimeLengthScoreCalculateGeneralize generalize = driverDispatchScoreService.queryDriverTimeLengthScoreCalculateDetail(driverId,driverName,driverPhone,day,timeLengthScore);
        return AjaxResponse.success(generalize);
    }

    /**
     * 查询父派单分类型
     *
     * @return AjaxResponse
     */
    @ResponseBody
    @RequestMapping(value = "/queryParentDispatchScoreType")
    @RequestFunction(menu = DRIVER_INFO_LIST)
    public AjaxResponse queryParentDispatchScoreType(){
        List<DriverIntegralDispatchScoreType> list = driverDispatchScoreService.queryParentDispatchScoreType();
        return AjaxResponse.success(list);
    }

    /**
     * 查询子派单分类型
     *
     * @param parentType 父派单分类型编码
     * @return AjaxResponse
     */
    @ResponseBody
    @RequestMapping(value = "/queryChildDispatchScoreType")
    @RequestFunction(menu = DRIVER_INFO_LIST)
    public AjaxResponse queryChildDispatchScoreType(Integer parentType){
        List<DriverIntegralDispatchScoreType> list = driverDispatchScoreService.queryChildDispatchScoreType(parentType);
        return AjaxResponse.success(list);
    }

    /**
     * 司机收入分信息列表（有分页）
     *
     * @param driverId      司机id
     * @param phone         司机手机号
     * @param licensePlates 车牌号
     * @param cityId        城市ID
     * @param supplierId    供应商ID
     * @param teamId        车队ID
     * @param page          起始页，默认0
     * @param pageSize      取N条，默认20
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/driverIncomeScoreListData")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    @RequestFunction(menu = DRIVER_INFO_LIST)
    public AjaxResponse incomeRecordDetailData(Integer driverId, String phone, String licensePlates, Integer cityId, Integer supplierId,
                                               Integer teamId, @RequestParam(value = "page", defaultValue = "0") Integer page,
                                               @Verify(param = "pageSize", rule = "max(50)") @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {

        long startTime = System.currentTimeMillis();
        // 数据权限控制SSOLoginUser
        Set<Integer> permOfCity = WebSessionUtil.getCurrentLoginUser().getCityIds(); //普通管理员可以管理的所有城市ID
        Set<Integer> permOfSupplier = WebSessionUtil.getCurrentLoginUser().getSupplierIds(); //普通管理员可以管理的所有供应商ID
        Set<Integer> permOfTeam = WebSessionUtil.getCurrentLoginUser().getTeamIds(); //普通管理员可以管理的所有车队ID
        int total = 0;
        List<CarBizDriverInfoDTO> list = Lists.newArrayList();
        Set<Integer> driverIds = null;
        Boolean had = false;
        if (teamId != null || (permOfTeam != null && permOfTeam.size() > 0)) {
            had = true;
            driverIds = carDriverTeamService.selectDriverIdsByTeamIdAndGroupId(null, teamId, permOfTeam);
        }
        if (had && (driverIds == null || driverIds.size() == 0)) {
            logger.info(LOGTAG + "查询teamId={},permOfTeam={}没有司机信息", teamId, permOfTeam);
            PageDTO pageDTO = new PageDTO(page, pageSize, total, list);
            return AjaxResponse.success(pageDTO);
        }
        CarBizDriverInfoDTO carBizDriverInfoDTO = new CarBizDriverInfoDTO();
        carBizDriverInfoDTO.setPhone(phone);
        carBizDriverInfoDTO.setLicensePlates(licensePlates);
        carBizDriverInfoDTO.setServiceCity(cityId);
        carBizDriverInfoDTO.setSupplierId(supplierId);
        carBizDriverInfoDTO.setTeamId(teamId);
        //数据权限
        carBizDriverInfoDTO.setCityIds(permOfCity);
        carBizDriverInfoDTO.setSupplierIds(permOfSupplier);
        carBizDriverInfoDTO.setTeamIds(permOfTeam);
        if (driverId != null && null != driverIds && driverIds.size() > 0) {
            if (driverIds.contains(driverId)) {
                driverIds.clear();
                driverIds.add(driverId);
            } else {
                PageDTO pageDTO = new PageDTO(page, pageSize, total, list);
                return AjaxResponse.success(pageDTO);
            }
        }
        if (null != driverIds)
            carBizDriverInfoDTO.setDriverIds(driverIds);
        else if (null != driverId) {
            driverIds = new HashSet<>();
            driverIds.add(driverId);
            carBizDriverInfoDTO.setDriverIds(driverIds);
        }
        PageDTO pageDTO = carBizDriverInfoService.queryDriverIncomeScoreListData(page,pageSize,carBizDriverInfoDTO);
        overLayPhone(pageDTO.getResult());
        logger.info("time cost : " + (System.currentTimeMillis() - startTime));
        return AjaxResponse.success(pageDTO);
    }

    private void overLayPhone(List<CarBizDriverInfoDTO> result) {
        if (Objects.nonNull(result)){
            for (CarBizDriverInfoDTO driverInfoDTO : result) {
                driverInfoDTO.setPhone(MobileOverlayUtil.doOverlayPhone(driverInfoDTO.getPhone()));
            }
        }
    }

    @ResponseBody
    @RequestMapping(value = "/downdriverIncomeScoreListData")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    @RequestFunction(menu = DRIVER_INFO_LIST)
    public AjaxResponse downdriverIncomeScoreListData( @Verify(param = "email", rule = "required")String email,Integer driverId, String phone, String licensePlates, Integer cityId, Integer supplierId,
                                                       Integer teamId, @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                       @Verify(param = "pageSize", rule = "max(50)") @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {

        long startTime = System.currentTimeMillis();
        // 数据权限控制SSOLoginUser
        Set<Integer> permOfCity = WebSessionUtil.getCurrentLoginUser().getCityIds(); //普通管理员可以管理的所有城市ID
        Set<Integer> permOfSupplier = WebSessionUtil.getCurrentLoginUser().getSupplierIds(); //普通管理员可以管理的所有供应商ID
        Set<Integer> permOfTeam = WebSessionUtil.getCurrentLoginUser().getTeamIds(); //普通管理员可以管理的所有车队ID
        int total = 0;
        List<CarBizDriverInfoDTO> list = Lists.newArrayList();
        Set<Integer> driverIds = null;
        Boolean had = false;
        if (teamId != null || (permOfTeam != null && permOfTeam.size() > 0)) {
            had = true;
            driverIds = carDriverTeamService.selectDriverIdsByTeamIdAndGroupId(null, teamId, permOfTeam);
        }
        if (had && (driverIds == null || driverIds.size() == 0)) {
            logger.info(LOGTAG + "查询teamId={},permOfTeam={}没有司机信息", teamId, permOfTeam);
            PageDTO pageDTO = new PageDTO(page, pageSize, total, list);
            return AjaxResponse.success(pageDTO);
        }

        CarBizDriverInfoDTO carBizDriverInfoDTO = new CarBizDriverInfoDTO();
        carBizDriverInfoDTO.setPhone(phone);
        carBizDriverInfoDTO.setLicensePlates(licensePlates);
        carBizDriverInfoDTO.setServiceCity(cityId);
        carBizDriverInfoDTO.setSupplierId(supplierId);
        carBizDriverInfoDTO.setTeamId(teamId);
        //数据权限
        carBizDriverInfoDTO.setCityIds(permOfCity);
        carBizDriverInfoDTO.setSupplierIds(permOfSupplier);
        carBizDriverInfoDTO.setTeamIds(permOfTeam);
        if (driverId != null && null != driverIds && driverIds.size() > 0) {
            if (driverIds.contains(driverId)) {
                driverIds.clear();
                driverIds.add(driverId);
            } else {
                return AjaxResponse.failMsg(-1,"您无权查看该司机的信息");
            }
        }
        if (null != driverIds){
            carBizDriverInfoDTO.setDriverIds(driverIds);
        }else if (null != driverId) {
            driverIds = new HashSet<>();
            driverIds.add(driverId);
            carBizDriverInfoDTO.setDriverIds(driverIds);
        }

        JSONObject obj = new JSONObject();
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        obj.put("auth_account",loginUser.getAccountType());//必填
        obj.put("excel_export_type", Constants.SAAS_DRIVER_DISPATCH);//必填
        obj.put("send_email",  StringUtils.isNotEmpty(email)?email:loginUser.getEmail());//必填
        obj.put("auth_cityIds",loginUser.getCityIds());
        obj.put("auth_suppliers",loginUser.getSupplierIds());
        obj.put("auth_teamIds",loginUser.getTeamIds());
        obj.put("auth_groups",loginUser.getGroupIds());
        obj.put("auth_userId",loginUser.getId());
        obj.put("auth_userName",loginUser.getLoginName());
        obj.put("buiness_params", JSON.toJSONString(carBizDriverInfoDTO));


        try{

            //删除发送03组的mq,改成发送08组的mq
            this.sendDoubleMq(obj,loginUser.getId());

            //维护用户的邮箱
            if(loginUser.getId() != null && loginUser.getAccountType() != null){

                carAdmUserExMapper.updateEmail(email!= null?email:loginUser.getEmail(),loginUser.getId());
            }
            return AjaxResponse.success("请到邮箱中查询导出信息");
        }catch (Exception e){
            logger.error("导出司机派单信息失败:{}",e);

            return AjaxResponse.failMsg(500,"导出司机派单信息失败");
        }

    }
    private void sendDoubleMq(JSONObject obj,Integer userId){
        ExcelProducerDouble.publishMessage("excel_export_producer","excel-mp-manage",userId != null ? String.valueOf(userId) : "default",obj);
    }

    /**
     * 更新记录列表
     *
     * @param driver
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updateRecordData", method = {RequestMethod.POST})
    public Object updateRecordData(DriverVoEntity driver) {
        int total = 0;
        List<DriverIncomeScoreRecordDto> list = new ArrayList<>();
        PageDTO pageDTO;
        if (null == driver || StringUtils.isBlank(driver.getDriverId())) {
            pageDTO = new PageDTO(driver.getPage(), driver.getPagesize(), total, list);
            return AjaxResponse.success(pageDTO);
        }
        list = driverIncomeScoreService.getIncomeScoreRecord(driver);
        pageDTO = new PageDTO(driver.getPage(), driver.getPagesize(), total, list);
        return AjaxResponse.success(pageDTO);
    }

    /**
     * 收入分明细记录查询
     *
     * @param model
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/incomeRecordDetailData", method = {RequestMethod.POST})
    public Object incomeRecordDetailData(ModelMap model, DriverIncomeRecordParams params) {
        Map<String, Object> map = driverIncomeScoreService.incomeDetailList(params);
        List<DriverIncomeScoreDetailDto> rows = (List<DriverIncomeScoreDetailDto>) map.get("data");
        DriverIncomeScorePage page = (DriverIncomeScorePage) map.get("page");
        model.addAttribute("driverId", params.getDriverId());
        fillDriverInfo(rows, params.getDriverId());
        PageDTO pageDTO = new PageDTO(page.getPageNo(), page.getPageSize(), page.getTotal(), rows);
        return AjaxResponse.success(pageDTO);
    }

    /**
     * 收入分类型
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/incomeTypeList")
    public Object incomeTypeList() {
        return AjaxResponse.success(driverIncomeScoreService.incomeTypeList());
    }

    /**
     * 收入具体类型
     *
     * @param incomeType 收入分类型
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/typeList")
    public Object typeList(String incomeType) {
        return AjaxResponse.success(driverIncomeScoreService.typeList(incomeType));
    }



    private void fillDriverInfo(List<DriverIncomeScoreDetailDto> rows, String driverId) {
        if (null == rows || rows.size() == 0) return;
        CarBizDriverInfoDTO info = carBizDriverInfoService.querySupplierIdAndNameByDriverId(Integer.parseInt(driverId));
        for (DriverIncomeScoreDetailDto dto : rows) {
            dto.setDriverId(driverId);
            if (null != info) {
                dto.setName(info.getName());
                dto.setPhone(info.getPhone());
            }
        }
    }


    /**
     * 收入分详情
     * @param driverId
     * @param scoreDate
     * @return
     */
    @RequestMapping("/scoreDetail")
    @ResponseBody
    public AjaxResponse scoreDetail(@Verify(param = "driverId",rule = "required") Integer driverId,
                                    @Verify(param = "scoreDate",rule = "required") Long scoreDate,
                                    @Verify(param = "tripScore",rule = "required")String tripScore,
                                    @RequestParam(value = "pageSize",required = false,defaultValue = "100")Integer pageSize,
                                    @RequestParam(value = "pageNum",required = false,defaultValue = "1")Integer pageNum){
        logger.info(MessageFormat.format("时长分详情入参：driverId:{0},scoreDate:{1},tripScore:{2}",driverId,scoreDate,tripScore));

        String day = DateUtils.convertLongToString(scoreDate,DateUtils.date_format);

        PageDTO pageDTO = new PageDTO(pageNum,pageSize,0,null);
        Map<String,Object> map = Maps.newHashMap();


        try {
            map = driverIncomeScoreService.getScoreDetailDTO(driverId,day,scoreDate,tripScore);

            if(map.get("scoreDetailDTO") == null){
                return AjaxResponse.success(map);
            }else {
                List<ScoreDetailDTO> list = (List<ScoreDetailDTO>) map.get("scoreDetailDTO");
                pageDTO = new PageDTO(pageNum,pageSize,list.size(),list);
                map.put("result",pageDTO);
                map.remove("scoreDetailDTO");
            }
        } catch (Exception e) {
            logger.error("获取时长分详情异常",e);
        }
        return AjaxResponse.success(map);

    }
}
