package com.zhuanche.controller.driverReport;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.rocketmq.ExcelProducer;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.constant.Constants;
import com.zhuanche.constants.SaasConst;
import com.zhuanche.dto.CarDriverTeamDTO;
import com.zhuanche.dto.bigdata.BiDriverBusinessInfoDayReportDTO;
import com.zhuanche.dto.bigdata.BiDriverBusinessInfoMonthReportDTO;
import com.zhuanche.dto.bigdata.BiDriverBusinessInfoSummaryReportDTO;
import com.zhuanche.dto.bigdata.SaasReportParamDTO;
import com.zhuanche.entity.bigdata.BiDriverBusinessInfoDayReport;
import com.zhuanche.entity.bigdata.BiDriverBusinessInfoMonthReport;
import com.zhuanche.entity.bigdata.BiDriverBusinessInfoSummaryReport;
import com.zhuanche.entity.rentcar.CarBizCarGroup;
import com.zhuanche.entity.rentcar.CarBizModel;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.DateUtils;
import com.zhuanche.util.dateUtil.DateUtil;
import mapper.bigdata.ex.BiDriverBusinessInfoDayReportExMapper;
import mapper.bigdata.ex.BiDriverBusinessInfoMonthReportExMapper;
import mapper.bigdata.ex.BiDriverBusinessInfoSummaryReportExMapper;
import mapper.mdbcarmanage.ex.CarAdmUserExMapper;
import mapper.mdbcarmanage.ex.CarDriverTeamExMapper;
import mapper.rentcar.ex.CarBizCarGroupExMapper;
import mapper.rentcar.ex.CarBizModelExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import okhttp3.internal.http2.ErrorCode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @Author fanht
 * @Description
 * @Date 2019/4/18 下午2:19
 * @Version 1.0
 */
@Controller
@RequestMapping("driver-business/")
public class SaasDriverDailyReportController {


    private Logger logger = LoggerFactory.getLogger(this.getClass());



    @Autowired
    private BiDriverBusinessInfoDayReportExMapper dayReportExMapper;


    @Autowired
    private BiDriverBusinessInfoMonthReportExMapper monthReportExMapper;

    @Autowired
    private BiDriverBusinessInfoSummaryReportExMapper summaryReportExMapper;

    @Autowired
    private CarAdmUserExMapper carAdmUserExMapper;



    @Autowired
    private CarBizSupplierExMapper carBizSupplierExMapper;



    @Autowired
    private CarDriverTeamExMapper carDriverTeamExMapper;




    @Autowired
    private CarBizModelExMapper carBizModelExMapper;  //具体车型

    @Autowired
    private CarBizCarGroupExMapper carBizCarGroupExMapper;//司机服务类型


    @ResponseBody
    @RequestMapping("saasDriverDaily")
    public AjaxResponse response(@Verify(param = "cityId",rule ="required")String cityId,
                                 @Verify(param = "cityName",rule="required")String cityName,
                                 @Param("supplierId")String supplierId,
                                 @Param("supplierName")String supplierName,
                                 @Param("driverTeamId")String driverTeamId,
                                 @Param("driverTeamName")String driverTeamName,
                                 @Param("driverGroupId")String driverGroupId,
                                 @Param("driverGroupName")String driverGroupName,
                                 @Param("driverPhone")String driverPhone,
                                 @Param("licensePlates")String licensePlates,
                                 @Param("beginDate")String beginDate,
                                 @Param("endDate")String endDate,
                                 @Param("businessVolumeSort")String businessVolumeSort,
                                 @Param("finOrdCntSort")String finOrdCntSort,
                                 @Param("badCntSort")String badCntSort,
                                 @Verify(param="pageNum",rule="required|min(1)") Integer pageNum,
                                 @Verify(param="pageSize",rule="required|min(10)") Integer pageSize){


        boolean bl = DateUtil.isLess(beginDate,"2019-01-01");
        if (!bl){
            logger.info("请选择2019年后的日期");
            return AjaxResponse.fail(RestErrorCode.CHOOSE_BAD_DATE);
        }


        List<CarBizSupplier> carBizSupplierList = null;
        List<CarDriverTeamDTO> listTeam = null;
        Map<Integer,String> supplierMap = Maps.newHashMap();
        Map<Integer,String> teamMap = Maps.newHashMap();
        Map<Integer,String> modelMap= Maps.newHashMap();
        Map<Integer,String> carGroupMap  = Maps.newHashMap();
        if(StringUtils.isEmpty(supplierId)){
            //根据城市查询所有的供应商
            Set<Integer> set = new HashSet<>();
            set.add(Integer.valueOf(cityId));
            carBizSupplierList = carBizSupplierExMapper.querySupplierName(set);

           Set<String> suppliersSet = new HashSet<>();
           for(CarBizSupplier carBizSupplier : carBizSupplierList){
               suppliersSet.add(carBizSupplier.getSupplierId()== null ? null:carBizSupplier.getSupplierId().toString());
           }

           //根据供应商查询所有的车队
            listTeam = carDriverTeamExMapper.queryDriverTeamAndGroupAll(null,suppliersSet,null);

            Set<Integer> setGroup = new HashSet<>();
            for(CarDriverTeamDTO carDriverTeamDTO : listTeam){
                setGroup.add(carDriverTeamDTO.getId());
            }

        }else {
            //根据供应商查询所有的车队
            Set<String> suppliersSet = new HashSet<>();
            suppliersSet.add(supplierId);

            listTeam = carDriverTeamExMapper.queryDriverTeamAndGroupAll(null,suppliersSet,null);
            Set<Integer> setGroup = new HashSet<>();
            for(CarDriverTeamDTO carDriverTeamDTO : listTeam){
                setGroup.add(carDriverTeamDTO.getId());
            }
        }

        List<CarBizModel> carBizModelList = carBizModelExMapper.queryAllList();
        List<CarBizCarGroup> carGroupList = carBizCarGroupExMapper.queryGroupNameList();
        if(CollectionUtils.isNotEmpty(carBizSupplierList)){
            for(CarBizSupplier supplier : carBizSupplierList){
                supplierMap.put(supplier.getSupplierId(),supplier.getSupplierFullName());
            }
        }

        if(CollectionUtils.isNotEmpty(listTeam)){
            for(CarDriverTeamDTO team : listTeam){
                teamMap.put(team.getId(),team.getTeamName());
            }
        }

        if(CollectionUtils.isNotEmpty(carBizModelList)){
            for(CarBizModel type : carBizModelList){
                carGroupMap.put(type.getModelId(),type.getModelName());
            }
        }

        if(CollectionUtils.isNotEmpty(carGroupList)){
            for(CarBizCarGroup carGroup : carGroupList){
                modelMap.put(carGroup.getGroupId(),carGroup.getGroupName());
            }
        }

        //2019-04-15
        String year = beginDate.substring(0,4);
        String month = beginDate.substring(5,7);
        String day = beginDate.substring(8,10);

        if(StringUtils.isNotEmpty(day)){
            Integer intDay = Integer.valueOf(day);
            if(intDay >= 26 && intDay <= 31){
                month = String.valueOf(Integer.valueOf(month)+1);
                if(month.length()==1){
                    month = "0" + month;
                }
            }
        }

        String table = SaasConst.DAILYTABLE+ year + "_"+ month;

        String sort = "";


        if("1".equals(businessVolumeSort) || "1".equals(finOrdCntSort) || "1".equals(badCntSort)){
            sort = "desc";
        }else if ("2".equals(businessVolumeSort) || "2".equals(finOrdCntSort) || "2".equals(badCntSort)){
            sort = "asc";
        }

        long total = 0;
        List<BiDriverBusinessInfoDayReport> list = null;
        try {
            Page page =  PageHelper.startPage(pageNum,pageSize,true);

            Set<Integer> setSuppliers = new HashSet<>();
            Set<Integer> setTeamids = new HashSet<>();
            Set<Integer> setGroups = new HashSet<>();
            setSuppliers = this.getSupplierIds(supplierId,setSuppliers);
            setTeamids = this.getTeamIds(driverTeamId,setTeamids);
            setGroups = this.getGroupIds(driverGroupId,setGroups);

            list = dayReportExMapper.queryDayReport(Integer.valueOf(cityId), setSuppliers,
                    setTeamids,setGroups,driverPhone,licensePlates,beginDate,endDate,businessVolumeSort,
                    finOrdCntSort,badCntSort,sort,table);

            total = page.getTotal();
        } catch (NumberFormatException e) {
            logger.info("",e);
         }finally {
            PageHelper.clearPage();
        }

        List<BiDriverBusinessInfoDayReportDTO> dailyDtoList = new ArrayList<>();
        dailyDtoList = BeanUtil.copyList(list,BiDriverBusinessInfoDayReportDTO.class);
        for(BiDriverBusinessInfoDayReportDTO dto : dailyDtoList){
            dto.setCityName(cityName);
            dto.setSupplierName(StringUtils.isEmpty(supplierName)?supplierMap.get(dto.getSupplierId()):supplierName);
            dto.setDriverTeamName(StringUtils.isEmpty(driverTeamName)?teamMap.get(dto.getDriverTeamId()):driverTeamName);
            dto.setDriverGroupName(StringUtils.isEmpty(driverGroupName)?teamMap.get(dto.getDriverGroupId()):driverGroupName);
            dto.setCooperateName(modelMap.get(dto.getCooperationType()));
            dto.setCarGroupName(carGroupMap.get(dto.getCarGroupId()));
        }
        PageDTO pageDTO = new PageDTO(pageNum, pageSize, total, dailyDtoList);

       return AjaxResponse.success(pageDTO);

    }





    @ResponseBody
    @RequestMapping("saasDriverExportDaily")
    public AjaxResponse driverExportDaily(@Verify(param = "cityId",rule ="required")String cityId,
                                 @Verify(param = "cityName",rule="required")String cityName,
                                 @Param("supplierId")String supplierId,
                                 @Param("supplierName")String supplierName,
                                 @Param("driverTeamId")String driverTeamId,
                                 @Param("driverTeamName")String driverTeamName,
                                 @Param("driverGroupId")String driverGroupId,
                                 @Param("driverGroupName")String driverGroupName,
                                 @Param("driverPhone")String driverPhone,
                                 @Param("licensePlates")String licensePlates,
                                 @Param("beginDate")String beginDate,
                                 @Param("endDate")String endDate,
                                 @Param("businessVolumeSort")String businessVolumeSort,
                                 @Param("finOrdCntSort")String finOrdCntSort,
                                 @Param("badCntSort")String badCntSort,
                                 @Verify(param = "email",rule="required") @Param("email")String email){

        Map<String,String> map = Maps.newHashMap();
        try {
            logger.info("导出司机日报操作");
            boolean bl = DateUtil.isLess(beginDate,"2019-01-01");
            if (!bl){
                logger.info("请选择2019年后的日期");
                return AjaxResponse.fail(RestErrorCode.CHOOSE_BAD_DATE);
            }



           // logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"导出司机周/月列表2.0");
        } catch (Exception e) {
        }
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        JSONObject obj = new JSONObject();
        obj.put("auth_account",loginUser.getAccountType());
        obj.put("auth_cityIds",loginUser.getCityIds());
        obj.put("auth_suppliers",loginUser.getSupplierIds());
        obj.put("auth_teamIds",loginUser.getTeamIds());
        obj.put("auth_groups",loginUser.getGroupIds());
        obj.put("auth_userId",loginUser.getId());
        obj.put("auth_userName",loginUser.getLoginName());
        obj.put("send_email",StringUtils.isNotBlank(email)?email:loginUser.getEmail());
        obj.put("excel_export_type", Constants.SAAS_DAILY_EXCEL);
        obj.put("businessVolumeSort",businessVolumeSort);
        obj.put("finOrdCntSort",finOrdCntSort);
        obj.put("badCntSort",badCntSort);

        BiDriverBusinessInfoDayReportDTO dto = new BiDriverBusinessInfoDayReportDTO();
        dto.setCityId(Integer.valueOf(cityId));
        dto.setCityName(cityName);
        if(StringUtils.isNotBlank(supplierId)){
            dto.setSupplierId(Integer.valueOf(supplierId));
            dto.setSupplierName(supplierName);
        }if (StringUtils.isNotBlank(driverTeamId)){
            dto.setDriverTeamId(Integer.valueOf(driverTeamId));
            dto.setDriverTeamName(driverTeamName);
        }if(StringUtils.isNotBlank(driverGroupId)){
            dto.setDriverGroupId(Integer.valueOf(driverGroupId));
            dto.setDriverGroupName(driverGroupName);
        }
        dto.setDriverPhone(driverPhone);
        dto.setLicensePlates(licensePlates);

        //dto.setBusinessVolume(Integer.valueOf(badCntDesc));


        obj.put("buiness_params",dto);


        String year = beginDate.substring(0,4);
        String month = beginDate.substring(5,7);
        String day = beginDate.substring(8,10);

        if(StringUtils.isNotEmpty(day)){
            Integer intDay = Integer.valueOf(day);
            if(intDay >= 26 && intDay <= 31){
                month = String.valueOf(Integer.valueOf(month)+1);
                if(month.length()==1){
                    month = "0" + month;
                }
            }
        }

        String table = SaasConst.DAILYTABLE+ year + "_"+ month;

        SaasReportParamDTO saasDTO = new SaasReportParamDTO(beginDate,endDate,null,table,1);

        obj.put("saasReport",saasDTO);
        try{
            ExcelProducer.publishMessage("excel_export_producer","excel-mp-manage",null,obj);
            //ExcelProducer.sendMessage("excel_export_producer","excel-car-manager",obj);
            //维护用户的邮箱
            if(loginUser.getId() != null && loginUser.getAccountType() != null){

                carAdmUserExMapper.updateEmail(email!= null?email:loginUser.getEmail(),loginUser.getId());
            }
            map.put("code","200");
        }catch (Exception e){
            logger.error("saas日报发送错误:{}",e);
            map.put("code","500");
        }
        return AjaxResponse.success(null);
    }



    @ResponseBody
    @RequestMapping("saasMonthReport")
    public AjaxResponse saasMonthReport(@Verify(param = "cityId",rule ="required")String cityId,
                                 @Verify(param = "cityName",rule="required")String cityName,
                                 @Param("supplierId")String supplierId,
                                 @Param("supplierName")String supplierName,
                                 @Param("driverTeamId")String driverTeamId,
                                 @Param("driverTeamName")String driverTeamName,
                                 @Param("driverGroupId")String driverGroupId,
                                 @Param("driverGroupName")String driverGroupName,
                                 @Param("driverPhone")String driverPhone,
                                 @Param("licensePlates")String licensePlates,
                                 @Param("month")String month,
                                 @Param("businessVolumeSort")String businessVolumeSort,
                                 @Param("finOrdCntSort")String finOrdCntSort,
                                 @Param("badCntSort")String badCntSort,
                                 @Verify(param="pageNum",rule="required|min(1)") Integer pageNum,
                                 @Verify(param="pageSize",rule="required|min(10)") Integer pageSize){
        //如果月份大于当前月份 直接返回错误信息
        String requestMonth = month+"-01";

        boolean bl = DateUtil.isBig(requestMonth);
        if (!bl){
            logger.info("月份大于当前日期");
            return AjaxResponse.fail(RestErrorCode.MONTH_IS_BIG);
        }

        boolean isLess = DateUtil.isLess(requestMonth,"2019-01-01");
        if (!isLess){
            logger.info("请选择2019年后的月份");
            return AjaxResponse.fail(RestErrorCode.CHOOSE_BAD_MONTH);
        }

        List<CarBizSupplier> carBizSupplierList = null;
        List<CarDriverTeamDTO> listTeam = null;
        Map<Integer,String> supplierMap = Maps.newHashMap();
        Map<Integer,String> teamMap = Maps.newHashMap();
        Map<Integer,String> carGroupMap = Maps.newHashMap();
        Map<Integer,String>  cooperMap= Maps.newHashMap();
        if(StringUtils.isEmpty(supplierId)){
            //根据城市查询所有的供应商
            Set<Integer> set = new HashSet<>();
            set.add(Integer.valueOf(cityId));
            carBizSupplierList = carBizSupplierExMapper.querySupplierName(set);

            Set<String> suppliersSet = new HashSet<>();
            for(CarBizSupplier carBizSupplier : carBizSupplierList){
                suppliersSet.add(carBizSupplier.getSupplierId()== null ? null:carBizSupplier.getSupplierId().toString());
            }

            //根据供应商查询所有的车队
            listTeam = carDriverTeamExMapper.queryDriverTeamAndGroupAll(null,suppliersSet,null);

            Set<Integer> setGroup = new HashSet<>();
            for(CarDriverTeamDTO carDriverTeamDTO : listTeam){
                setGroup.add(carDriverTeamDTO.getId());
            }

        }else {
            //根据供应商查询所有的车队
            Set<String> suppliersSet = new HashSet<>();
            suppliersSet.add(supplierId);

            listTeam = carDriverTeamExMapper.queryDriverTeamAndGroupAll(null,suppliersSet,null);
            Set<Integer> setGroup = new HashSet<>();
            for(CarDriverTeamDTO carDriverTeamDTO : listTeam){
                setGroup.add(carDriverTeamDTO.getId());
            }
        }

        List<CarBizModel> modelList = carBizModelExMapper.queryAllList();
        List<CarBizCarGroup> carGroupList = carBizCarGroupExMapper.queryGroupNameList();
        if(CollectionUtils.isNotEmpty(carBizSupplierList)){
            for(CarBizSupplier supplier : carBizSupplierList){
                supplierMap.put(supplier.getSupplierId(),supplier.getSupplierFullName());
            }
        }

        if(CollectionUtils.isNotEmpty(listTeam)){
            for(CarDriverTeamDTO team : listTeam){
                teamMap.put(team.getId(),team.getTeamName());
            }
        }

        if(CollectionUtils.isNotEmpty(modelList)){
            for(CarBizModel type : modelList){
                carGroupMap.put(type.getModelId(),type.getModelName());
            }
        }

        if(CollectionUtils.isNotEmpty(carGroupList)){
            for(CarBizCarGroup carGroup : carGroupList){
                cooperMap.put(carGroup.getGroupId(),carGroup.getGroupName());
            }
        }


        String table = this.getTable( cityId, month);

        String sort = "";
        if("1".equals(businessVolumeSort) || "1".equals(finOrdCntSort) || "1".equals(badCntSort)){
            sort = "desc";
        }else if ("2".equals(businessVolumeSort) || "2".equals(finOrdCntSort) || "2".equals(badCntSort)){
            sort = "asc";
        }

        long total = 0;
        List<BiDriverBusinessInfoMonthReport> monthReportlist = null;
        try {
            Page page =  PageHelper.startPage(pageNum,pageSize,true);
            Set<Integer> setSuppliers = new HashSet<>();
            Set<Integer> setTeamids = new HashSet<>();
            Set<Integer> setGroups = new HashSet<>();
            setSuppliers = this.getSupplierIds(supplierId,setSuppliers);
            setTeamids = this.getTeamIds(driverTeamId,setTeamids);
            setGroups = this.getGroupIds(driverGroupId,setGroups);

            monthReportlist = monthReportExMapper.queryMonthReport(Integer.valueOf(cityId),setSuppliers,
                    setTeamids,setGroups, driverPhone,licensePlates,month,businessVolumeSort,
                    finOrdCntSort,badCntSort,sort,table);

            total = page.getTotal();
        } catch (NumberFormatException e) {
            logger.info("",e);
        }finally {
            PageHelper.clearPage();
        }

        List<BiDriverBusinessInfoMonthReportDTO> monthDtoList = new ArrayList<>();
        monthDtoList = BeanUtil.copyList(monthReportlist,BiDriverBusinessInfoMonthReportDTO.class);
        for(BiDriverBusinessInfoMonthReportDTO dto : monthDtoList){
            dto.setCityName(cityName);
            dto.setSupplierName(StringUtils.isEmpty(supplierName)?supplierMap.get(dto.getSupplierId()):supplierName);
            dto.setDriverTeamName(StringUtils.isEmpty(driverTeamName)?teamMap.get(dto.getDriverTeamId()):driverTeamName);
            dto.setDriverGroupName(StringUtils.isEmpty(driverGroupName)?teamMap.get(dto.getDriverGroupId()):driverGroupName);
            dto.setCooperateName(cooperMap.get(dto.getCooperationType()));
            dto.setCarGroupName(carGroupMap.get(dto.getCarGroupId()));
        }
        PageDTO pageDTO = new PageDTO(pageNum, pageSize, total, monthDtoList);

        return AjaxResponse.success(pageDTO);

    }





    @ResponseBody
    @RequestMapping("saasDriverExportMonth")
    public AjaxResponse driverExportMonth(@Verify(param = "cityId",rule ="required")String cityId,
                                          @Verify(param = "cityName",rule="required")String cityName,
                                          @Param("supplierId")String supplierId,
                                          @Param("supplierName")String supplierName,
                                          @Param("driverTeamId")String driverTeamId,
                                          @Param("driverTeamName")String driverTeamName,
                                          @Param("driverGroupId")String driverGroupId,
                                          @Param("driverGroupName")String driverGroupName,
                                          @Param("driverPhone")String driverPhone,
                                          @Param("licensePlates")String licensePlates,
                                          @Param("month")String month,
                                          @Param("businessVolumeSort")String businessVolumeSort,
                                          @Param("finOrdCntSort")String finOrdCntSort,
                                          @Param("badCntSort")String badCntSort,
                                          @Verify(param = "email",rule="required") @Param("email")String email){

        Map<String,String> map = Maps.newHashMap();
        try {
            logger.info("导出司机月报操作");
            String requestMonth = month+"-01";

            boolean bl = DateUtil.isBig(requestMonth);
            if (!bl){
                logger.info("月份大于当前日期");
                return AjaxResponse.success(null);
            }

            boolean isLess = DateUtil.isLess(requestMonth,"2019-01-01");
            if (!isLess){
                logger.info("请选择2019年后的月份");
                return AjaxResponse.fail(RestErrorCode.CHOOSE_BAD_MONTH);
            }


            // logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"导出司机周/月列表2.0");
        } catch (Exception e) {
            map.put("code","200");
        }
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        JSONObject obj = new JSONObject();
        obj.put("auth_account",loginUser.getAccountType());
        obj.put("auth_cityIds",loginUser.getCityIds());
        obj.put("auth_suppliers",loginUser.getSupplierIds());
        obj.put("auth_teamIds",loginUser.getTeamIds());
        obj.put("auth_groups",loginUser.getGroupIds());
        obj.put("auth_userId",loginUser.getId());
        obj.put("auth_userName",loginUser.getLoginName());
        obj.put("send_email",StringUtils.isNotBlank(email)?email:loginUser.getEmail());
        obj.put("excel_export_type", Constants.SAAS_MONTH_EXCEL);
        obj.put("businessVolumeSort",businessVolumeSort);
        obj.put("finOrdCntSort",finOrdCntSort);
        obj.put("badCntSort",badCntSort);

        BiDriverBusinessInfoMonthReportDTO dto = new BiDriverBusinessInfoMonthReportDTO();
        dto.setCityId(Integer.valueOf(cityId));
        dto.setCityName(cityName);
        if(StringUtils.isNotBlank(supplierId)){
            dto.setSupplierId(Integer.valueOf(supplierId));
            dto.setSupplierName(supplierName);
        }if (StringUtils.isNotBlank(driverTeamId)){
            dto.setDriverTeamId(Integer.valueOf(driverTeamId));
            dto.setDriverTeamName(driverTeamName);
        }if(StringUtils.isNotBlank(driverGroupId)){
            dto.setDriverGroupId(Integer.valueOf(driverGroupId));
            dto.setDriverGroupName(driverGroupName);
        }
        dto.setDriverPhone(driverPhone);
        dto.setLicensePlates(licensePlates);


        obj.put("buiness_params",dto);


        String table = this.getTable( cityId, month);

        SaasReportParamDTO saasDTO = new SaasReportParamDTO(null,null,month,table,2);

        obj.put("saasReport",saasDTO);
        try{
            ExcelProducer.publishMessage("excel_export_producer","excel-mp-manage",null,obj);
            //ExcelProducer.sendMessage("excel_export_producer","excel-car-manager",obj);
            //维护用户的邮箱
            if(loginUser.getId() != null && loginUser.getAccountType() != null){

                carAdmUserExMapper.updateEmail(email != null?email:loginUser.getEmail(),loginUser.getId());
            }
            map.put("code","200");
        }catch (Exception e){
            logger.error("saas月报发送错误:{}",e);
            map.put("code","500");
        }
        return AjaxResponse.success(null);
    }




    @ResponseBody
    @RequestMapping("saasSummaryReport")
    public AjaxResponse saasSummaryReport(@Verify(param = "cityId",rule ="required")String cityId,
                                        @Verify(param = "cityName",rule="required")String cityName,
                                        @Param("supplierId")String supplierId,
                                        @Param("supplierName")String supplierName,
                                        @Param("driverTeamId")String driverTeamId,
                                        @Param("driverTeamName")String driverTeamName,
                                        @Param("driverGroupId")String driverGroupId,
                                        @Param("driverGroupName")String driverGroupName,
                                        @Param("driverPhone")String driverPhone,
                                        @Param("licensePlates")String licensePlates,
                                        @Param("businessVolumeSort")String businessVolumeSort,
                                        @Param("finOrdCntSort")String finOrdCntSort,
                                        @Param("badCntSort")String badCntSort,
                                        @Verify(param="pageNum",rule="required|min(1)") Integer pageNum,
                                        @Verify(param="pageSize",rule="required|min(10)") Integer pageSize){
        List<CarBizSupplier> carBizSupplierList = null;
        List<CarDriverTeamDTO> listTeam = null;
        Map<Integer,String> supplierMap = Maps.newHashMap();
        Map<Integer,String> teamMap = Maps.newHashMap();
        Map<Integer,String> cooperMap = Maps.newHashMap();
        Map<Integer,String> carGroupMap = Maps.newHashMap();
        if(StringUtils.isEmpty(supplierId)){
            //根据城市查询所有的供应商
            Set<Integer> set = new HashSet<>();
            set.add(Integer.valueOf(cityId));
            carBizSupplierList = carBizSupplierExMapper.querySupplierName(set);

            Set<String> suppliersSet = new HashSet<>();
            for(CarBizSupplier carBizSupplier : carBizSupplierList){
                suppliersSet.add(carBizSupplier.getSupplierId()== null ? null:carBizSupplier.getSupplierId().toString());
            }

            //根据供应商查询所有的车队
            listTeam = carDriverTeamExMapper.queryDriverTeamAndGroupAll(null,suppliersSet,null);

            Set<Integer> setGroup = new HashSet<>();
            for(CarDriverTeamDTO carDriverTeamDTO : listTeam){
                setGroup.add(carDriverTeamDTO.getId());
            }

        }else {
            //根据供应商查询所有的车队
            Set<String> suppliersSet = new HashSet<>();
            suppliersSet.add(supplierId);

            listTeam = carDriverTeamExMapper.queryDriverTeamAndGroupAll(null,suppliersSet,null);
            Set<Integer> setGroup = new HashSet<>();
            for(CarDriverTeamDTO carDriverTeamDTO : listTeam){
                setGroup.add(carDriverTeamDTO.getId());
            }
        }

        List<CarBizModel> carBizModelList = carBizModelExMapper.queryAllList();
        List<CarBizCarGroup> carGroupList = carBizCarGroupExMapper.queryGroupNameList();
        if(CollectionUtils.isNotEmpty(carBizSupplierList)){
            for(CarBizSupplier supplier : carBizSupplierList){
                supplierMap.put(supplier.getSupplierId(),supplier.getSupplierFullName());
            }
        }

        if(CollectionUtils.isNotEmpty(listTeam)){
            for(CarDriverTeamDTO team : listTeam){
                teamMap.put(team.getId(),team.getTeamName());
            }
        }

        if(CollectionUtils.isNotEmpty(carBizModelList)){
            for(CarBizModel type : carBizModelList){
                carGroupMap.put(type.getModelId(),type.getModelName());
            }
        }

        if(CollectionUtils.isNotEmpty(carGroupList)){
            for(CarBizCarGroup carGroup : carGroupList){
                cooperMap.put(carGroup.getGroupId(),carGroup.getGroupName());
            }
        }

        //String table = SaasConst.MONTHTABLE + month;

        String sort = "";
        if("1".equals(businessVolumeSort) || "1".equals(finOrdCntSort) || "1".equals(badCntSort)){
            sort = "desc";
        }else if ("2".equals(businessVolumeSort) || "2".equals(finOrdCntSort) || "2".equals(badCntSort)){
            sort = "asc";
        }

        String currentDate = DateUtils.formatDate(new Date(),"yyyy-MM-dd");

        long total = 0;
        List<BiDriverBusinessInfoSummaryReport> summaryReportList = null;
        try {
            Page page =  PageHelper.startPage(pageNum,pageSize,true);
            Set<Integer> setSuppliers = new HashSet<>();
            Set<Integer> setTeamids = new HashSet<>();
            Set<Integer> setGroups = new HashSet<>();
            setSuppliers = this.getSupplierIds(supplierId,setSuppliers);
            setTeamids = this.getTeamIds(driverTeamId,setTeamids);
            setGroups = this.getGroupIds(driverGroupId,setGroups);

            summaryReportList = summaryReportExMapper.querySummeryReport(Integer.valueOf(cityId),setSuppliers,
                    setTeamids,setGroups,driverPhone,licensePlates,currentDate,
                    businessVolumeSort,finOrdCntSort,badCntSort,sort);

            total = page.getTotal();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }finally {
            PageHelper.clearPage();
        }

        List<BiDriverBusinessInfoSummaryReportDTO> summaryReportDTOList = new ArrayList<>();
        summaryReportDTOList = BeanUtil.copyList(summaryReportList,BiDriverBusinessInfoSummaryReportDTO.class);
        for(BiDriverBusinessInfoSummaryReportDTO dto : summaryReportDTOList){
            dto.setCityName(cityName);
            dto.setSupplierName(StringUtils.isEmpty(supplierName)?supplierMap.get(dto.getSupplierId()):supplierName);
            dto.setDriverTeamName(StringUtils.isEmpty(driverTeamName)?teamMap.get(dto.getDriverTeamId()):driverTeamName);
            dto.setDriverGroupName(StringUtils.isEmpty(driverGroupName)?teamMap.get(dto.getDriverGroupId()):driverGroupName);
            dto.setCooperateName(cooperMap.get(dto.getCooperationType()));
            dto.setCarGroupName(carGroupMap.get(dto.getCarGroupId()));
        }
        PageDTO pageDTO = new PageDTO(pageNum, pageSize, total, summaryReportDTOList);

        return AjaxResponse.success(pageDTO);
    }



    @ResponseBody
    @RequestMapping("saasDriverExportSummary")
    public AjaxResponse driverExportSummary(@Verify(param = "cityId",rule ="required")String cityId,
                                          @Verify(param = "cityName",rule="required")String cityName,
                                          @Param("supplierId")String supplierId,
                                          @Param("supplierName")String supplierName,
                                          @Param("driverTeamId")String driverTeamId,
                                          @Param("driverTeamName")String driverTeamName,
                                          @Param("driverGroupId")String driverGroupId,
                                          @Param("driverGroupName")String driverGroupName,
                                          @Param("driverPhone")String driverPhone,
                                          @Param("licensePlates")String licensePlates,
                                            @Param("businessVolumeSort")String businessVolumeSort,
                                            @Param("finOrdCntSort")String finOrdCntSort,
                                            @Param("badCntSort")String badCntSort,
                                          @Verify(param = "email",rule="required") @Param("email")String email){

        Map<String,String> map = Maps.newHashMap();
        try {
            logger.info("导出司机营业汇总操作");
            return AjaxResponse.success(null);
            // logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"导出司机周/月列表2.0");
        } catch (Exception e) {

        }
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        JSONObject obj = new JSONObject();
        obj.put("auth_account",loginUser.getAccountType());
        obj.put("auth_cityIds",loginUser.getCityIds());
        obj.put("auth_suppliers",loginUser.getSupplierIds());
        obj.put("auth_teamIds",loginUser.getTeamIds());
        obj.put("auth_groups",loginUser.getGroupIds());
        obj.put("auth_userId",loginUser.getId());
        obj.put("auth_userName",loginUser.getLoginName());
        obj.put("send_email",StringUtils.isNotBlank(email)?email:loginUser.getEmail());
        obj.put("excel_export_type", Constants.SAAS_SUMMARY_EXCEL);
        obj.put("businessVolumeSort",businessVolumeSort);
        obj.put("finOrdCntSort",finOrdCntSort);
        obj.put("badCntSort",badCntSort);

        BiDriverBusinessInfoSummaryReportDTO dto = new BiDriverBusinessInfoSummaryReportDTO();
        dto.setCityId(Integer.valueOf(cityId));
        dto.setCityName(cityName);
        if(StringUtils.isNotBlank(supplierId)){
            dto.setSupplierId(Integer.valueOf(supplierId));
            dto.setSupplierName(supplierName);
        }if (StringUtils.isNotBlank(driverTeamId)){
            dto.setDriverTeamId(Integer.valueOf(driverTeamId));
            dto.setDriverTeamName(driverTeamName);
        }if(StringUtils.isNotBlank(driverGroupId)){
            dto.setDriverGroupId(Integer.valueOf(driverGroupId));
            dto.setDriverGroupName(driverGroupName);
        }
        dto.setDriverPhone(driverPhone);
        dto.setLicensePlates(licensePlates);


        obj.put("buiness_params",dto);


        //SaasReportParamDTO saasDTO = new SaasReportParamDTO(null,null,month,table,3);

        //obj.put("saasReport",saasDTO);
        try{
            ExcelProducer.publishMessage("excel_export_producer","excel-mp-manage",null,obj);
            //ExcelProducer.sendMessage("excel_export_producer","excel-car-manager",obj);
            //维护用户的邮箱
            if(loginUser.getId() != null && loginUser.getAccountType() != null){

                carAdmUserExMapper.updateEmail(email != null?email:loginUser.getEmail(),loginUser.getId());
            }
            map.put("code","200");
        }catch (Exception e){
            logger.error("saas营业汇总报表发送错误:{}",e);
            map.put("code","500");
        }
        return AjaxResponse.success(null);
    }

    /**
     * 根据数据权限作为查询条件
     * @param supplierId
     * @param setSupplierIds
     */
    private Set<Integer> getSupplierIds(String supplierId,Set<Integer> setSupplierIds){
        if(StringUtils.isNotEmpty(supplierId)){
            setSupplierIds.add(Integer.valueOf(supplierId));
        }else {
            if(WebSessionUtil.getCurrentLoginUser().getAccountType()!= null && WebSessionUtil.getCurrentLoginUser().getAccountType() == 900){

            }else {
                setSupplierIds = WebSessionUtil.getCurrentLoginUser().getSupplierIds();

            }
        }
        return setSupplierIds;
    }

    private Set<Integer> getTeamIds(String teamId,
                                    Set<Integer> setTeamIds){


        if(StringUtils.isNotEmpty(teamId)){
            setTeamIds.add(Integer.valueOf(teamId));
        }else {

            if(WebSessionUtil.getCurrentLoginUser().getAccountType()!= null && WebSessionUtil.getCurrentLoginUser().getAccountType() == 900){

            }else {
                setTeamIds = WebSessionUtil.getCurrentLoginUser().getTeamIds();
            }
        }
        return setTeamIds;
    }

    private Set<Integer> getGroupIds(String groupId,Set<Integer> setGroupIds){

        if(StringUtils.isNotEmpty(groupId)){
            setGroupIds.add(Integer.valueOf(groupId));
        }else {

            if(WebSessionUtil.getCurrentLoginUser().getAccountType()!= null && WebSessionUtil.getCurrentLoginUser().getAccountType() == 900){

            }else {
                setGroupIds = WebSessionUtil.getCurrentLoginUser().getGroupIds();
            }
        }

        return setGroupIds;
    }

    private String getTable (String cityId, String month){
        String table = SaasConst.MONTHTABLE + month.replace("-","_");

        if(StringUtils.isNotBlank(cityId) && "44".equals(cityId)) {
            table = SaasConst.MONTHTABLEBEIJING + month.replace("-","_");
        }
        return table;
    }
}
