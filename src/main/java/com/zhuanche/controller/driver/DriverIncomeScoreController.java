package com.zhuanche.controller.driver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.rocketmq.ExcelProducer;
import com.zhuanche.common.rocketmq.ExcelProducerDouble;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.constant.Constants;
import com.zhuanche.constant.EnvUtils;
import com.zhuanche.dto.driver.DriverVoEntity;
import com.zhuanche.dto.mdbcarmanage.ScoreDetailDTO;
import com.zhuanche.dto.rentcar.*;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.serv.*;
import com.zhuanche.serv.driverteam.CarDriverTeamService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.DateUtils;
import com.zhuanche.util.MobileOverlayUtil;
import mapper.mdbcarmanage.ex.CarAdmUserExMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.rest.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
        if (null != driverIds)
            carBizDriverInfoDTO.setDriverIds(driverIds);
        else if (null != driverId) {
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
            ExcelProducer.publishMessage("excel_export_producer","excel-mp-manage",null,obj);
            //双发
            this.sendDoubleMq(obj);
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
    private void sendDoubleMq(JSONObject obj){
        String envName = EnvUtils.ENVIMENT;
        if (Objects.nonNull(envName) && Arrays.asList(new String[]{"online","prod"}).contains(envName)){
            ExcelProducerDouble.publishMessage("excel_export_producer","excel-mp-manage",null,obj);
        }
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
