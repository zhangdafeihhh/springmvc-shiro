package com.zhuanche.controller.subscription;


import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.driver.SubscriptionReportConfigureDTO;
import com.zhuanche.entity.driver.SubscriptionReport;
import com.zhuanche.entity.driver.SubscriptionReportType;
import com.zhuanche.entity.mdbcarmanage.CarDriverTeam;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.serv.common.CitySupplierTeamCommonService;
import com.zhuanche.serv.subscription.SubscriptionReportConfigureService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BigDataFtpUtil;
import mapper.driver.ex.SubscriptionReportTypeExMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.zhuanche.common.enums.MenuEnum.*;

@Controller
@RequestMapping("/subscription/report")
public class SubscriptionReportConfigureController {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionReportConfigureController.class);
    private static final String LOGTAG = "[??????????????????]: ";

    private String SUBSCRIPTION = "subscription";

    @Autowired
    private SubscriptionReportConfigureService subscriptionReportConfigureService;

    @Autowired
    private BigDataFtpUtil bigDataFtpUtil;

    @Autowired
    private CitySupplierTeamCommonService citySupplierTeamCommonService;

    @Autowired
    private SubscriptionReportTypeExMapper subscriptionReportTypeExMapper;

    /**
     * ??????????????????
     * @param reportId ??????ID,1-????????????;2-????????????;3-??????;4-?????????;
     * @param reportName ????????????:1-????????????,2-????????????,3-??????,4-?????????
     * @param subscriptionCycle ????????????,1-???;2-???;
     * @param level ??????,1-??????;2-??????;4-?????????;8-??????;16-???????????????ID????????????????????????
     * @param cities ??????ID?????????ID????????????????????????
     * @param supplierIds ?????????ID?????????ID????????????????????????
     * @param teamIds ??????ID?????????ID????????????????????????
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveSubscription", method = RequestMethod.POST)
    @RequiresPermissions(value = "SubscribeStatement_look")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DataSourceMode.MASTER)
    })
    @RequestFunction(menu = SUBSCRIPTION_REPORT)
    public AjaxResponse saveSubscription(
            @Verify(param = "reportId",rule="required") Integer reportId,
            @Verify(param = "reportName",rule="required") String reportName,
            @Verify(param = "subscriptionCycle",rule="required") Integer subscriptionCycle,
            @Verify(param = "level",rule="required") String level,
            String cities, String supplierIds, String teamIds) {

        long start = System.currentTimeMillis();

        Integer currentLevel = WebSessionUtil.getCurrentLoginUser().getLevel();//?????????????????????,1-??????;2-??????;4-?????????;8-??????;16-??????
        if(currentLevel!=null && currentLevel==16){
            return AjaxResponse.fail(RestErrorCode.SUBSCRIPTION_INVALID, "?????????????????????");
        }
        logger.info(LOGTAG + "??????????????????, reportName={}, subscriptionCycle={}, level={}, cities={}, supplierIds={}, teamIds={}",
                reportName, subscriptionCycle, level, cities, supplierIds, teamIds);
        //????????????
        String[] levels = level.split(",");
        Boolean isWhole = false;
        Boolean isCity = false;
        Boolean isSupplier = false;
        Boolean isTeam = false;
        for (int i = 0; i < levels.length; i++) {
            Integer levelGrade = Integer.parseInt(levels[i]);
            if(levelGrade==1) { //??????
                isWhole = true;
            }
            if(levelGrade==2) { //??????
                if(StringUtils.isEmpty(cities)){
                    return AjaxResponse.fail(RestErrorCode.SUBSCRIPTION_INVALID, "?????????????????????????????????ID");
                }
                isCity = true;
            }
            if(levelGrade==4) { //?????????
                if(StringUtils.isEmpty(supplierIds)){
                    return AjaxResponse.fail(RestErrorCode.SUBSCRIPTION_INVALID, "???????????????????????????????????????ID");
                }
                isSupplier = true;
            }
            if(levelGrade==8) { //??????
                if(StringUtils.isEmpty(teamIds)){
                    return AjaxResponse.fail(RestErrorCode.SUBSCRIPTION_INVALID, "?????????????????????????????????ID");
                }
                isTeam = true;
            }
        }

        AjaxResponse ajaxResponse = null;
        try {
            //?????????
            String isLock = (String) WebSessionUtil.getAttribute(SUBSCRIPTION);
            logger.info(LOGTAG + "??????????????????, isLock={}", isLock);
            if(StringUtils.isNotEmpty(isLock)){
                if(Integer.parseInt(isLock)==1){
                    return AjaxResponse.fail(RestErrorCode.CAR_API_ERROR, "??????????????????????????????????????????");
                }
            }
            WebSessionUtil.setAttribute(SUBSCRIPTION, 1);

            ajaxResponse = subscriptionReportConfigureService.saveSubscription(reportId, reportName, subscriptionCycle,
                    level, cities, supplierIds, teamIds, isWhole, isCity, isSupplier, isTeam);
        } finally {
            logger.info(LOGTAG + "??????????????????,?????????");
            WebSessionUtil.removeAttribute(SUBSCRIPTION);
        }
        logger.info(LOGTAG + "??????????????????,??????"+(System.currentTimeMillis() -start)+"??????");
        return ajaxResponse;
    }

    /**
     * ????????????????????????
     * @param reportId ??????ID,1-????????????;2-????????????;3-??????;4-?????????;
     * @param subscriptionCycle ????????????,1-???;2-???;
     * @param cityId ??????ID
     * @param supplierId ?????????ID
     * @param teamId ??????ID
     * @param page ??????????????????0
     * @param pageSize ???N????????????20
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/querySubscriptionList")
    @RequiresPermissions(value = "DownloadStatement_look")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DataSourceMode.SLAVE)
    })
    @RequestFunction(menu = REPORT_DOWNLOAD_LIST)
    public AjaxResponse querySubscriptionList(Integer reportId, Integer subscriptionCycle, Integer cityId,
                                       Integer supplierId, Integer teamId,
                                       @RequestParam(value="page", defaultValue="0")Integer page,
                                       @RequestParam(value="pageSize", defaultValue="20")Integer pageSize) {

        int total = 0;
        List<SubscriptionReport> list =  Lists.newArrayList();

        Integer currentLevel = WebSessionUtil.getCurrentLoginUser().getLevel();//?????????????????????,1-??????;2-??????;4-?????????;8-??????;16-??????
        if(currentLevel!=null && currentLevel==16){
            PageDTO pageDTO = new PageDTO(page, pageSize, total, list);
            return AjaxResponse.success(pageDTO);
        }
        Page p = PageHelper.startPage(page, pageSize, true);
        try {
            list = subscriptionReportConfigureService.
                    querySubscriptionList(reportId, subscriptionCycle, cityId, supplierId, teamId);
            total = (int)p.getTotal();
        } finally {
            PageHelper.clearPage();
        }
        PageDTO pageDTO = new PageDTO(page, pageSize, total, list);
        return AjaxResponse.success(pageDTO);
    }

    /**
     * ????????????????????????????????????????????????
     * @param subscriptionCycle ????????????,1-???;2-???;
     * @param reportId ??????ID,1-????????????;2-????????????;3-??????;4-?????????
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/querySubscriptionConfigureList")
    @RequiresPermissions(value = "SubscribeStatement_look")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DataSourceMode.SLAVE)
    })
    @RequestFunction(menu = REPORT_CONFIGURE_LIST)
    public AjaxResponse querySubscriptionConfigureList(
            @Verify(param = "subscriptionCycle",rule="required") Integer subscriptionCycle,
            @Verify(param = "reportId",rule="required") Integer reportId) {

        logger.info(LOGTAG + "/querySubscriptionConfigureList,subscriptionCycle={}, reportId={}", subscriptionCycle, reportId);
        List<SubscriptionReportConfigureDTO> cycleList = subscriptionReportConfigureService.selectBySubscriptionCycle(subscriptionCycle, reportId);
        return AjaxResponse.success(cycleList);
    }

    /**
     * ????????????
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/querySubscriptionName")
    @RequestFunction(menu = REPORT_TYPE_LIST)
    public AjaxResponse querySubscriptionName() {
        JSONObject map = new JSONObject();
        List<SubscriptionReportType> list = subscriptionReportTypeExMapper.findList();
        list.forEach(ele -> {
            map.put(ele.getReportId().toString(), ele.getReportName());
        });
        return AjaxResponse.success(map);
    }

    @ResponseBody
    @RequestMapping(value = "/exportSubscriptionUrl")
    @RequiresPermissions(value = "DownloadStatement_look")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DataSourceMode.SLAVE)
    })
    @RequestFunction(menu = REPORT_DOWNLOAD)
    public void exportSubscriptionUrl(@Verify(param = "id",rule="required") Long id,
                                      HttpServletRequest request, HttpServletResponse response) {

        SubscriptionReport report = subscriptionReportConfigureService.selectSubscriptionReportByPrimaryKey(id);
        if (report == null) {
            return;
        }
        String url = report.getUrl();
        logger.info(LOGTAG + "????????????,url={}", url);
        if (StringUtils.isEmpty(url)) {
            return;
        }
        String operation = url.split("/")[0] +"/"+ url.split("/")[1]
                + "/" + url.split("/")[2] + "/" + url.split("/")[3];
        String file = url.split("/")[4];
        //??????
        String fileName = report.getReportName();
        if (StringUtils.isNotEmpty(report.getCityName())) {
            fileName += "_" + report.getCityName();
        }
        if (StringUtils.isNotEmpty(report.getSupplierName())) {
            fileName += "_" + report.getSupplierName();
        }
        if (StringUtils.isNotEmpty(report.getTeamName())) {
            fileName += "_" + report.getTeamName();
        }
        fileName += "_" + report.getSubscriptionTime()+".csv";
        try {
            InputStream in = bigDataFtpUtil.downloadFile(operation, file);
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("utf-8"), "iso8859-1"));//????????????????????????
            response.setContentType("application/binary;charset=ISO8859_1");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            ServletOutputStream os = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = in.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            in.close();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ;
    }

    /**
     * ????????????????????????????????????????????????
     * @param subscriptionCycle ????????????,1-???;2-???;
     * @param reportId ??????ID,1-????????????;2-????????????;3-??????;4-?????????
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/querySubscriptionConfigure")
    @RequiresPermissions(value = "SubscribeStatement_look")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DataSourceMode.SLAVE)
    })
    @RequestFunction(menu = REPORT_CONFIGURE)
    public AjaxResponse querySubscriptionConfigure(
            @Verify(param = "subscriptionCycle",rule="required") Integer subscriptionCycle,
            @Verify(param = "reportId",rule="required") Integer reportId) {

        long start = System.currentTimeMillis();
        logger.info(LOGTAG + "/querySubscriptionConfigure,subscriptionCycle={}, reportId={}", subscriptionCycle, reportId);
        String level = "";//??????,1-??????;2-??????;4-?????????;8-??????;16-???????????????ID????????????????????????
        Set citiesSet = Sets.newHashSet();
        Set supplierIdsSet = Sets.newHashSet();
        Set teamIdsSet = Sets.newHashSet();

        Integer currentLevel = WebSessionUtil.getCurrentLoginUser().getLevel();//?????????????????????,1-??????;2-??????;4-?????????;8-??????;16-??????
        if(currentLevel!=null && currentLevel==16){
            JSONObject map = new JSONObject();
            map.put( "level", level);
            map.put( "cities", citiesSet);
            map.put( "supplierIds", supplierIdsSet);
            map.put( "teamIds", teamIdsSet);
            logger.info(LOGTAG + "/querySubscriptionConfigure????????????????????????");
            return AjaxResponse.success(map);
        }
        //????????????
        List<SubscriptionReportConfigureDTO> allList = subscriptionReportConfigureService.querySubscriptionConfigure(subscriptionCycle, reportId, 1);
        if(allList!=null && allList.size()>0){
            level += ",1";
        }
        //????????????
        List<SubscriptionReportConfigureDTO> cityList = subscriptionReportConfigureService.querySubscriptionConfigure(subscriptionCycle, reportId, 2);
        if(cityList!=null && cityList.size()>0){
            level += ",2";
            for (SubscriptionReportConfigureDTO sub : cityList) {
                citiesSet.add(sub.getCityId());
            }
        }
        //???????????????
        List<SubscriptionReportConfigureDTO> supplerList = subscriptionReportConfigureService.querySubscriptionConfigure(subscriptionCycle, reportId, 4);
        if(supplerList!=null && supplerList.size()>0){
            level += ",4";
            for (SubscriptionReportConfigureDTO sub : supplerList) {
                citiesSet.add(sub.getCityId());
                supplierIdsSet.add(sub.getSupplierId());

            }
        }
        //????????????
        List<SubscriptionReportConfigureDTO> teamList = subscriptionReportConfigureService.querySubscriptionConfigure(subscriptionCycle, reportId, 8);
        if(teamList!=null && teamList.size()>0){
            level += ",8";
            for (SubscriptionReportConfigureDTO sub : teamList) {
                citiesSet.add(sub.getCityId());
                supplierIdsSet.add(sub.getSupplierId());
                teamIdsSet.add(sub.getTeamId());
            }
        }
        if(level.length()>1){
            level = level.substring(1,level.length());
        }
        JSONObject map = new JSONObject();
        map.put( "level", level);
        map.put( "cities", citiesSet);
        map.put( "supplierIds", supplierIdsSet);
        map.put( "teamIds", teamIdsSet);
        long end = System.currentTimeMillis();
        logger.info(LOGTAG + "/querySubscriptionConfigure,subscriptionCycle={}, reportId={}???map={}, ??????={}",
                subscriptionCycle, reportId, ToStringBuilder.reflectionToString(map), (end-start));
        return AjaxResponse.success(map);
    }

    /**
     * ????????????
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/querySubscriptionLevel")
    public AjaxResponse querySubscriptionLevel(Integer authority) {
        Integer level = WebSessionUtil.getCurrentLoginUser().getLevel();//?????????????????????,1-??????;2-??????;4-?????????;8-??????;16-??????
        JSONObject map = new JSONObject();
        if (level != null) {
            if(authority!=null && authority==1){
                map.put( "1", "??????");
                map.put( "2", "??????");
                map.put( "4", "?????????");
                map.put( "8", "??????");
                return AjaxResponse.success(map);
            }
            switch (level) {
                case 1:
                    map.put( "1", "??????");
                    map.put( "2", "??????");
                    map.put( "4", "?????????");
                    map.put( "8", "??????");
                    break;
                case 2:
                    map.put( "2", "??????");
                    map.put( "4", "?????????");
                    map.put( "8", "??????");
                    break;
                case 4:
                    map.put( "4", "?????????");
                    map.put( "8", "??????");
                    break;
                case 8:
                    map.put( "8", "??????");
                    break;
                case 16:
//                    map.put( "16", "??????");
                    break;
            }
        }
        return AjaxResponse.success(map);
    }



    /**
     * @Desc:  ??????????????????(??????????????????)
     */
    @RequestMapping("/getCities")
    @ResponseBody
    public AjaxResponse getCities(){
        List<CarBizCity> carBizCities = citySupplierTeamCommonService.getCities();
        return AjaxResponse.success(carBizCities);
    }

    /**
     * @Desc: ????????????????????????(??????????????????)
     */
    @RequestMapping("/getSuppliers")
    @ResponseBody
    public AjaxResponse getSuppliers(@Verify(param = "cityId", rule = "required") Integer cityId, String cityIds ){

        Set<Integer> cityIdset = new HashSet<Integer>();
        cityIdset.add(cityId);
        if(org.apache.commons.lang.StringUtils.isNotEmpty(cityIds)) {//???????????????cityid???
            Set<Integer> cityids = Stream.of(cityIds.split(",")).mapToInt(s -> {
                if(org.apache.commons.lang.StringUtils.isNotEmpty(s)) {
                    return Integer.valueOf(s);
                }else {
                    return -1;
                }
            }).boxed().collect(Collectors.toSet());
            cityIdset.addAll(cityids);
        }

        List<CarBizSupplier> carBizSuppliers = citySupplierTeamCommonService.getSuppliers( cityIdset );
        return AjaxResponse.success(carBizSuppliers);
    }

    /**
     * @Desc: ??????????????????(??????????????????)
     */
    @RequestMapping("/getTeams")
    @ResponseBody
    public AjaxResponse getTeams(@Verify(param = "supplierId", rule = "required") Integer supplierId,
                                 Integer cityId, String supplierIds ){
        //??????ID
        Set<String> cityIdset = new HashSet<String>();
        if(cityId!=null && cityId.intValue()>0) {
            cityIdset.add(cityId.toString());
        }
        //?????????ID
        Set<String> supplieridSet = new HashSet<String>();
        supplieridSet.add(supplierId.toString());
        if(org.apache.commons.lang.StringUtils.isNotEmpty(supplierIds)) {//???????????????supplierId???
            Set<String> supplierids = Stream.of(supplierIds.split(",")).collect(Collectors.toSet());
            supplieridSet.addAll(supplierids);
        }
        List<CarDriverTeam> carDriverTeams = citySupplierTeamCommonService.getTeams(cityIdset, supplieridSet);
        return AjaxResponse.success(carDriverTeams);
    }
}
