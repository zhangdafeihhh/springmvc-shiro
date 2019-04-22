package com.zhuanche.controller.driverReport;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.rocketmq.ExcelProducer;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.Verify;
import com.zhuanche.constant.Constants;
import com.zhuanche.constants.SaasConst;
import com.zhuanche.dto.CarDriverTeamDTO;
import com.zhuanche.dto.bigdata.BiDriverBusinessInfoDayReportDTO;
import com.zhuanche.dto.bigdata.BiDriverBusinessInfoMonthReportDTO;
import com.zhuanche.dto.bigdata.BiDriverBusinessInfoSummaryReportDTO;
import com.zhuanche.entity.bigdata.BiDriverBusinessInfoDayReport;
import com.zhuanche.entity.bigdata.BiDriverBusinessInfoMonthReport;
import com.zhuanche.entity.bigdata.BiDriverBusinessInfoSummaryReport;
import com.zhuanche.entity.rentcar.CarBizCarGroup;
import com.zhuanche.entity.rentcar.CarBizCooperationType;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.DateUtil;
import com.zhuanche.util.DateUtils;
import mapper.bigdata.ex.BiDriverBusinessInfoDayReportExMapper;
import mapper.bigdata.ex.BiDriverBusinessInfoMonthReportExMapper;
import mapper.bigdata.ex.BiDriverBusinessInfoSummaryReportExMapper;
import mapper.mdbcarmanage.ex.CarAdmUserExMapper;
import mapper.mdbcarmanage.ex.CarDriverTeamExMapper;
import mapper.rentcar.ex.CarBizCarGroupExMapper;
import mapper.rentcar.ex.CarBizCooperationTypeExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
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
    private CarBizCooperationTypeExMapper cooperationTypeExMapper;

    @Autowired
    private CarBizCarGroupExMapper carBizCarGroupExMapper;


    @ResponseBody
    @RequestMapping("saasDriverDaily")
    public AjaxResponse response(@Verify(param = "cityId",rule ="required")String cityId,
                                 @Verify(param = "cityName",rule="required")String cityName,
                                 @Param("supplierId")String supplierId,
                                 @Param("supplierName")String supplierName,
                                 @Param("teamId")String teamId,
                                 @Param("teamName")String teamName,
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
            listTeam = carDriverTeamExMapper.queryDriverTeamAndGroup(null,suppliersSet,null);

            Set<Integer> setGroup = new HashSet<>();
            for(CarDriverTeamDTO carDriverTeamDTO : listTeam){
                setGroup.add(carDriverTeamDTO.getId());
            }
            //根据车队查询班组
             //listGroup =  carBizCarGroupExMapper.queryCarGroupByIdSet(setGroup);

        }else {
            //根据供应商查询所有的车队
            Set<String> suppliersSet = new HashSet<>();
            suppliersSet.add(supplierId);

            listTeam = carDriverTeamExMapper.queryDriverTeamAndGroup(null,suppliersSet,null);
            Set<Integer> setGroup = new HashSet<>();
            for(CarDriverTeamDTO carDriverTeamDTO : listTeam){
                setGroup.add(carDriverTeamDTO.getId());
            }
            //根据车队查询班组
           // listGroup =  carBizCarGroupExMapper.queryCarGroupByIdSet(setGroup);
        }

        List<CarBizCooperationType> cooperationTypeList = cooperationTypeExMapper.queryCarBizCooperationTypeList();
        List<CarBizCarGroup> carGroupList = carBizCarGroupExMapper.queryGroupNameList();
        for(CarBizSupplier supplier : carBizSupplierList){
            supplierMap.put(supplier.getSupplierId(),supplier.getSupplierFullName());
        }
        for(CarDriverTeamDTO teamDTO : listTeam){
            supplierMap.put(teamDTO.getId(),teamDTO.getTeamName());
        }
        /*for(CarBizCarGroup carGroup : listGroup){
            supplierMap.put(carGroup.getGroupId(),carGroup.getGroupName());
        }*/
        for(CarBizCooperationType type : cooperationTypeList){
            cooperMap.put(type.getId(),type.getCooperationName());
        }
        for(CarBizCarGroup carGroup : carGroupList){
            carGroupMap.put(carGroup.getGroupId(),carGroup.getGroupName());
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
        if(businessVolumeSort.equals("1") || finOrdCntSort.equals("1") || badCntSort.equals("1")){
            sort = "desc";
        }else if (businessVolumeSort.equals("2") || finOrdCntSort.equals("2") || badCntSort.equals("2")){
            sort = "asc";
        }

        long total = 0;
        List<BiDriverBusinessInfoDayReport> list = null;
        try {
            Page page =  PageHelper.startPage(pageNum,pageSize,true);
            list = dayReportExMapper.queryDayReport(Integer.valueOf(cityId), StringUtils.isEmpty(supplierId)?null:Integer.valueOf(supplierId),
                    StringUtils.isEmpty(teamId)?null:Integer.valueOf(teamId),StringUtils.isEmpty(driverGroupId)?null:Integer.valueOf(driverGroupId),
                    driverPhone,licensePlates,beginDate,endDate,businessVolumeSort,finOrdCntSort,badCntSort,sort,table);

            total = page.getTotal();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }finally {
            PageHelper.clearPage();
        }

        List<BiDriverBusinessInfoDayReportDTO> dailyDtoList = new ArrayList<>();
        dailyDtoList = BeanUtil.copyList(list,BiDriverBusinessInfoDayReportDTO.class);
        for(BiDriverBusinessInfoDayReportDTO dto : dailyDtoList){
            dto.setCityName(cityName);
            dto.setSupplierName(StringUtils.isEmpty(supplierName)?supplierMap.get(dto.getSupplierId()):supplierName);
            dto.setDriverTeamName(StringUtils.isEmpty(teamName)?teamMap.get(dto.getDriverTeamId()):teamName);
            dto.setDriverGroupName(StringUtils.isEmpty(driverGroupName)?teamMap.get(dto.getDriverGroupId()):driverGroupName);
            dto.setCooperateName(cooperMap.get(dto.getCooperationType()));
            dto.setCarGroupName(carGroupMap.get(dto.getCarGroupId()));
        }
        PageDTO pageDTO = new PageDTO(pageNum, pageSize, total, dailyDtoList);

       return AjaxResponse.success(pageDTO);

    }





    @ResponseBody
    @RequestMapping("saasDriverExportDaily")
    public AjaxResponse driverExportDaily(@Verify(param = "cityId",rule ="required")String cityId,
                                 @Verify(param = "cityName",rule="cityName")String cityName,
                                 @Param("supplierId")String supplierId,
                                 @Param("supplierName")String supplierName,
                                 @Param("teamId")String teamId,
                                 @Param("teamName")String teamName,
                                 @Param("driverGroupId")String driverGroupId,
                                 @Param("driverGroupName")String driverGroupName,
                                 @Param("driverPhone")String driverPhone,
                                 @Param("licensePlates")String licensePlates,
                                 @Param("beginDate")String beginDate,
                                 @Param("endDate")String endDate,
                                 @Param("actualPayDesc")String actualPayDesc,
                                 @Param("oddNumberDesc")String oddNumberDesc,
                                 @Param("badNumDesc")String badNumDesc){

        Map<String,String> map = Maps.newHashMap();
        try {
            logger.info("导出司机周/月操作");
           // logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"导出司机周/月列表2.0");
        } catch (Exception e) {
        }
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        JSONObject obj = new JSONObject();
        obj.put("auth_account",loginUser.getAccountType());
        obj.put("auth_cityIds",loginUser.getCityIds());
        obj.put("auth_suppliers",loginUser.getSupplierIds());
        obj.put("auth_teamIds",loginUser.getTeamIds());
        obj.put("auth_userId",loginUser.getId());
        obj.put("auth_userName",loginUser.getLoginName());
        obj.put("send_email",loginUser.getEmail());
        obj.put("excel_export_type", Constants.SAAS_DAILY_EXCEL);

        BiDriverBusinessInfoDayReportDTO dto = new BiDriverBusinessInfoDayReportDTO();
        dto.setCityId(Integer.valueOf(cityId));
        dto.setCityName(cityName);
        dto.setSupplierId(Integer.valueOf(supplierId));
        dto.setSupplierName(supplierName);
        dto.setDriverTeamId(Integer.valueOf(teamId));
        dto.setDriverTeamName(teamName);
        dto.setDriverGroupId(Integer.valueOf(driverGroupId));

        obj.put("buiness_params",dto);
        try{
            ExcelProducer.publishMessage("excel_export_producer","excel-mp-manage",null,obj);
            //ExcelProducer.sendMessage("excel_export_producer","excel-car-manager",obj);
            //维护用户的邮箱
            if(loginUser.getId() != null && loginUser.getAccountType() != null){
                carAdmUserExMapper.updateEmail(loginUser.getEmail(),loginUser.getId());
            }
            map.put("code","200");
        }catch (Exception e){
            logger.error("saas日报发送错误:{}",e);
            map.put("code","500");
        }
        return null;

        /*List<CarBizSupplier> carBizSupplierList = null;
        List<CarDriverTeamDTO> listTeam = null;
        List<CarBizCarGroup> listGroup = null;
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
            listTeam = carDriverTeamExMapper.queryDriverTeamAndGroup(null,suppliersSet,null);

            Set<Integer> setGroup = new HashSet<>();
            for(CarDriverTeamDTO carDriverTeamDTO : listTeam){
                setGroup.add(carDriverTeamDTO.getId());
            }
            //根据车队查询班组
            listGroup =  carBizCarGroupExMapper.queryCarGroupByIdSet(setGroup);
        }else {
            //根据供应商查询所有的车队
            Set<String> suppliersSet = new HashSet<>();
            suppliersSet.add(supplierId);

            listTeam = carDriverTeamExMapper.queryDriverTeamAndGroup(null,suppliersSet,null);
            Set<Integer> setGroup = new HashSet<>();
            for(CarDriverTeamDTO carDriverTeamDTO : listTeam){
                setGroup.add(carDriverTeamDTO.getId());
            }
            //根据车队查询班组
            listGroup =  carBizCarGroupExMapper.queryCarGroupByIdSet(setGroup);
        }




        return null;*/
    }



    @ResponseBody
    @RequestMapping("saasMonthReport")
    public AjaxResponse saasMonthReport(@Verify(param = "cityId",rule ="required")String cityId,
                                 @Verify(param = "cityName",rule="required")String cityName,
                                 @Param("supplierId")String supplierId,
                                 @Param("supplierName")String supplierName,
                                 @Param("teamId")String teamId,
                                 @Param("teamName")String teamName,
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
            listTeam = carDriverTeamExMapper.queryDriverTeamAndGroup(null,suppliersSet,null);

            Set<Integer> setGroup = new HashSet<>();
            for(CarDriverTeamDTO carDriverTeamDTO : listTeam){
                setGroup.add(carDriverTeamDTO.getId());
            }

        }else {
            //根据供应商查询所有的车队
            Set<String> suppliersSet = new HashSet<>();
            suppliersSet.add(supplierId);

            listTeam = carDriverTeamExMapper.queryDriverTeamAndGroup(null,suppliersSet,null);
            Set<Integer> setGroup = new HashSet<>();
            for(CarDriverTeamDTO carDriverTeamDTO : listTeam){
                setGroup.add(carDriverTeamDTO.getId());
            }
        }

        List<CarBizCooperationType> cooperationTypeList = cooperationTypeExMapper.queryCarBizCooperationTypeList();
        List<CarBizCarGroup> carGroupList = carBizCarGroupExMapper.queryGroupNameList();
        for(CarBizSupplier supplier : carBizSupplierList){
            supplierMap.put(supplier.getSupplierId(),supplier.getSupplierFullName());
        }
        for(CarDriverTeamDTO teamDTO : listTeam){
            supplierMap.put(teamDTO.getId(),teamDTO.getTeamName());
        }
        for(CarBizCooperationType type : cooperationTypeList){
            cooperMap.put(type.getId(),type.getCooperationName());
        }
        for(CarBizCarGroup carGroup : carGroupList){
            carGroupMap.put(carGroup.getGroupId(),carGroup.getGroupName());
        }

        String table = SaasConst.MONTHTABLE + month.replace("-","_");

        String sort = "";
        if(businessVolumeSort.equals("1") || finOrdCntSort.equals("1") || badCntSort.equals("1")){
            sort = "desc";
        }else if (businessVolumeSort.equals("2") || finOrdCntSort.equals("2") || badCntSort.equals("2")){
            sort = "asc";
        }

        long total = 0;
        List<BiDriverBusinessInfoMonthReport> monthReportlist = null;
        try {
            Page page =  PageHelper.startPage(pageNum,pageSize,true);
            monthReportlist = monthReportExMapper.queryMonthReport(Integer.valueOf(cityId), StringUtils.isEmpty(supplierId)?null:Integer.valueOf(supplierId),
                    StringUtils.isEmpty(teamId)?null:Integer.valueOf(teamId),StringUtils.isEmpty(driverGroupId)?null:Integer.valueOf(driverGroupId),
                    driverPhone,licensePlates,month,businessVolumeSort,finOrdCntSort,badCntSort,sort,table);

            total = page.getTotal();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }finally {
            PageHelper.clearPage();
        }

        List<BiDriverBusinessInfoMonthReportDTO> monthDtoList = new ArrayList<>();
        monthDtoList = BeanUtil.copyList(monthReportlist,BiDriverBusinessInfoMonthReportDTO.class);
        for(BiDriverBusinessInfoMonthReportDTO dto : monthDtoList){
            dto.setCityName(cityName);
            dto.setSupplierName(StringUtils.isEmpty(supplierName)?supplierMap.get(dto.getSupplierId()):supplierName);
            dto.setDriverTeamName(StringUtils.isEmpty(teamName)?teamMap.get(dto.getDriverTeamId()):teamName);
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
                                          @Verify(param = "cityName",rule="cityName")String cityName,
                                          @Param("supplierId")String supplierId,
                                          @Param("supplierName")String supplierName,
                                          @Param("teamId")String teamId,
                                          @Param("teamName")String teamName,
                                          @Param("driverGroupId")String driverGroupId,
                                          @Param("driverGroupName")String driverGroupName,
                                          @Param("driverPhone")String driverPhone,
                                          @Param("licensePlates")String licensePlates,
                                          @Param("beginDate")String beginDate,
                                          @Param("endDate")String endDate,
                                          @Param("actualPayDesc")String actualPayDesc,
                                          @Param("oddNumberDesc")String oddNumberDesc,
                                          @Param("badNumDesc")String badNumDesc,BiDriverBusinessInfoDayReportDTO dto){

        Map<String,String> map = Maps.newHashMap();
        try {
            logger.info("导出司机周/月操作");
            // logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"导出司机周/月列表2.0");
        } catch (Exception e) {
        }
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        JSONObject obj = new JSONObject();
        obj.put("auth_account",loginUser.getAccountType());
        obj.put("auth_cityIds",loginUser.getCityIds());
        obj.put("auth_suppliers",loginUser.getSupplierIds());
        obj.put("auth_teamIds",loginUser.getTeamIds());
        obj.put("auth_userId",loginUser.getId());
        obj.put("auth_userName",loginUser.getLoginName());
        obj.put("send_email",loginUser.getEmail());
        obj.put("excel_export_type", Constants.SAAS_DAILY_EXCEL);
        obj.put("buiness_params",dto);
        try{
            ExcelProducer.publishMessage("excel_export_producer","excel-mp-manage",null,obj);
            //ExcelProducer.sendMessage("excel_export_producer","excel-car-manager",obj);
            //维护用户的邮箱
            if(loginUser.getId() != null && loginUser.getAccountType() != null){
                carAdmUserExMapper.updateEmail(loginUser.getEmail(),loginUser.getId());
            }
            map.put("code","200");
        }catch (Exception e){
            logger.error("saas日报发送错误:{}",e);
            map.put("code","500");
        }
        return null;
    }




    @ResponseBody
    @RequestMapping("saasSummaryReport")
    public AjaxResponse saasSummaryReport(@Verify(param = "cityId",rule ="required")String cityId,
                                        @Verify(param = "cityName",rule="required")String cityName,
                                        @Param("supplierId")String supplierId,
                                        @Param("supplierName")String supplierName,
                                        @Param("teamId")String teamId,
                                        @Param("teamName")String teamName,
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
            listTeam = carDriverTeamExMapper.queryDriverTeamAndGroup(null,suppliersSet,null);

            Set<Integer> setGroup = new HashSet<>();
            for(CarDriverTeamDTO carDriverTeamDTO : listTeam){
                setGroup.add(carDriverTeamDTO.getId());
            }

        }else {
            //根据供应商查询所有的车队
            Set<String> suppliersSet = new HashSet<>();
            suppliersSet.add(supplierId);

            listTeam = carDriverTeamExMapper.queryDriverTeamAndGroup(null,suppliersSet,null);
            Set<Integer> setGroup = new HashSet<>();
            for(CarDriverTeamDTO carDriverTeamDTO : listTeam){
                setGroup.add(carDriverTeamDTO.getId());
            }
        }

        List<CarBizCooperationType> cooperationTypeList = cooperationTypeExMapper.queryCarBizCooperationTypeList();
        List<CarBizCarGroup> carGroupList = carBizCarGroupExMapper.queryGroupNameList();
        for(CarBizSupplier supplier : carBizSupplierList){
            supplierMap.put(supplier.getSupplierId(),supplier.getSupplierFullName());
        }
        for(CarDriverTeamDTO teamDTO : listTeam){
            supplierMap.put(teamDTO.getId(),teamDTO.getTeamName());
        }
        for(CarBizCooperationType type : cooperationTypeList){
            cooperMap.put(type.getId(),type.getCooperationName());
        }
        for(CarBizCarGroup carGroup : carGroupList){
            carGroupMap.put(carGroup.getGroupId(),carGroup.getGroupName());
        }

        //String table = SaasConst.MONTHTABLE + month;

        String sort = "";
        if(businessVolumeSort.equals("1") || finOrdCntSort.equals("1") || badCntSort.equals("1")){
            sort = "desc";
        }else if (businessVolumeSort.equals("2") || finOrdCntSort.equals("2") || badCntSort.equals("2")){
            sort = "asc";
        }

        String currentDate = DateUtils.formatDate(new Date(),"yyyy-MM-dd");

        long total = 0;
        List<BiDriverBusinessInfoSummaryReport> summaryReportList = null;
        try {
            Page page =  PageHelper.startPage(pageNum,pageSize,true);
            summaryReportList = summaryReportExMapper.querySummeryReport(Integer.valueOf(cityId), StringUtils.isEmpty(supplierId)?null:Integer.valueOf(supplierId),
                    StringUtils.isEmpty(teamId)?null:Integer.valueOf(teamId),StringUtils.isEmpty(driverGroupId)?null:Integer.valueOf(driverGroupId),
                    driverPhone,licensePlates,currentDate,businessVolumeSort,finOrdCntSort,badCntSort,sort);

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
            dto.setDriverTeamName(StringUtils.isEmpty(teamName)?teamMap.get(dto.getDriverTeamId()):teamName);
            dto.setDriverGroupName(StringUtils.isEmpty(driverGroupName)?teamMap.get(dto.getDriverGroupId()):driverGroupName);
            dto.setCooperateName(cooperMap.get(dto.getCooperationType()));
            dto.setCarGroupName(carGroupMap.get(dto.getCarGroupId()));
        }
        PageDTO pageDTO = new PageDTO(pageNum, pageSize, total, summaryReportDTOList);

        return AjaxResponse.success(pageDTO);

    }
}
