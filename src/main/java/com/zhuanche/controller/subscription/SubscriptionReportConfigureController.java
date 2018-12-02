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
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.driver.SubscriptionReportConfigureDTO;
import com.zhuanche.entity.driver.SubscriptionReport;
import com.zhuanche.serv.subscription.SubscriptionReportConfigureService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BigDataFtpUtil;
import mapper.mdbcarmanage.ex.CarAdmUserExMapper;
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
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/subscription/report")
public class SubscriptionReportConfigureController {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionReportConfigureController.class);
    private static final String LOGTAG = "[数据报表订阅]: ";

    private String SUBSCRIPTION = "subscription";

    @Autowired
    private SubscriptionReportConfigureService subscriptionReportConfigureService;

    @Autowired
    private BigDataFtpUtil bigDataFtpUtil;

    @Autowired
    private CarAdmUserExMapper carAdmUserExMapper;

    /**
     * 数据报表订阅
     * @param reportId 报表ID,1-工资明细;2-完单详情;3-积分;4-数单奖;
     * @param reportName 报表名称:1-工资明细,2-完单详情,3-积分,4-数单奖
     * @param subscriptionCycle 订阅周期,1-周;2-月;
     * @param level 级别,1-全国;2-城市;4-加盟商;8-车队;16-班组（多个ID用英文逗号分隔）
     * @param cities 城市ID（多个ID用英文逗号分隔）
     * @param supplierIds 供应商ID（多个ID用英文逗号分隔）
     * @param teamIds 车队ID（多个ID用英文逗号分隔）
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveSubscription", method = RequestMethod.POST)
    @RequiresPermissions(value = "SubscribeStatement_look")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DataSourceMode.MASTER)
    })
    public AjaxResponse saveSubscription(
            @Verify(param = "reportId",rule="required") Integer reportId,
            @Verify(param = "reportName",rule="required") String reportName,
            @Verify(param = "subscriptionCycle",rule="required") Integer subscriptionCycle,
            @Verify(param = "level",rule="required") String level,
            String cities, String supplierIds, String teamIds) {

        long start = System.currentTimeMillis();

        Integer currentLevel = WebSessionUtil.getCurrentLoginUser().getLevel();//获取用户的级别,1-全国;2-城市;4-加盟商;8-车队;16-班组
        if(currentLevel!=null && currentLevel==16){
            return AjaxResponse.fail(RestErrorCode.SUBSCRIPTION_INVALID, "当前账户没权限");
        }
        logger.info(LOGTAG + "数据报表订阅, reportName={}, subscriptionCycle={}, level={}, cities={}, supplierIds={}, teamIds={}",
                reportName, subscriptionCycle, level, cities, supplierIds, teamIds);
        //解析等级
        String[] levels = level.split(",");
        Boolean isWhole = false;
        Boolean isCity = false;
        Boolean isSupplier = false;
        Boolean isTeam = false;
        for (int i = 0; i < levels.length; i++) {
            Integer levelGrade = Integer.parseInt(levels[i]);
            if(levelGrade==1) { //全国
                isWhole = true;
            }
            if(levelGrade==2) { //城市
                if(StringUtils.isEmpty(cities)){
                    return AjaxResponse.fail(RestErrorCode.SUBSCRIPTION_INVALID, "选择城市级别需要传城市ID");
                }
                isCity = true;
            }
            if(levelGrade==4) { //供应商
                if(StringUtils.isEmpty(supplierIds)){
                    return AjaxResponse.fail(RestErrorCode.SUBSCRIPTION_INVALID, "选择供应商级别需要传供应商ID");
                }
                isSupplier = true;
            }
            if(levelGrade==8) { //车队
                if(StringUtils.isEmpty(teamIds)){
                    return AjaxResponse.fail(RestErrorCode.SUBSCRIPTION_INVALID, "选择车队级别需要传车队ID");
                }
                isTeam = true;
            }
        }

        AjaxResponse ajaxResponse = null;
        try {
            //验证码
            String isLock = (String) WebSessionUtil.getAttribute(SUBSCRIPTION);
            logger.info(LOGTAG + "数据报表订阅, isLock={}", isLock);
            if(StringUtils.isNotEmpty(isLock)){
                if(Integer.parseInt(isLock)==1){
                    return AjaxResponse.fail(RestErrorCode.CAR_API_ERROR, "报表正在被订阅，请稍后处理。");
                }
            }
            WebSessionUtil.setAttribute(SUBSCRIPTION, 1);

            ajaxResponse = subscriptionReportConfigureService.saveSubscription(reportId, reportName, subscriptionCycle,
                    level, cities, supplierIds, teamIds, isWhole, isCity, isSupplier, isTeam);
        } finally {
            logger.info(LOGTAG + "数据报表订阅,删除锁");
            WebSessionUtil.removeAttribute(SUBSCRIPTION);
        }
        logger.info(LOGTAG + "数据报表订阅,耗时"+(System.currentTimeMillis() -start)+"毫秒");
        return ajaxResponse;
    }

    /**
     * 数据报表下载列表
     * @param reportId 报表ID,1-工资明细;2-完单详情;3-积分;4-数单奖;
     * @param subscriptionCycle 订阅周期,1-周;2-月;
     * @param cityId 城市ID
     * @param supplierId 供应商ID
     * @param teamId 车队ID
     * @param page 起始页，默认0
     * @param pageSize 取N条，默认20
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/querySubscriptionList")
    @RequiresPermissions(value = "DownloadStatement_look")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DataSourceMode.SLAVE)
    })
    public AjaxResponse querySubscriptionList(Integer reportId, Integer subscriptionCycle, Integer cityId,
                                       Integer supplierId, Integer teamId,
                                       @RequestParam(value="page", defaultValue="0")Integer page,
                                       @RequestParam(value="pageSize", defaultValue="20")Integer pageSize) {

        int total = 0;
        List<SubscriptionReport> list =  Lists.newArrayList();

        Integer currentLevel = WebSessionUtil.getCurrentLoginUser().getLevel();//获取用户的级别,1-全国;2-城市;4-加盟商;8-车队;16-班组
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
     * 根据订阅周期查询数据报表订阅配置
     * @param subscriptionCycle 订阅周期,1-周;2-月;
     * @param reportId 报表ID,1-工资明细;2-完单详情;3-积分;4-数单奖
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/querySubscriptionConfigureList")
    @RequiresPermissions(value = "SubscribeStatement_look")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DataSourceMode.SLAVE)
    })
    public AjaxResponse querySubscriptionConfigureList(
            @Verify(param = "subscriptionCycle",rule="required") Integer subscriptionCycle,
            @Verify(param = "reportId",rule="required") Integer reportId) {

        logger.info(LOGTAG + "/querySubscriptionConfigureList,subscriptionCycle={}, reportId={}", subscriptionCycle, reportId);
        List<SubscriptionReportConfigureDTO> cycleList = subscriptionReportConfigureService.selectBySubscriptionCycle(subscriptionCycle, reportId);
        return AjaxResponse.success(cycleList);
    }

    /**
     * 数据报表
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/querySubscriptionName")
    @RequiresPermissions(value = "SubscribeStatement_look")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DataSourceMode.SLAVE)
    })
    public AjaxResponse querySubscriptionName() {
        JSONObject map = new JSONObject();
        map.put( "1", "工资明细");
        map.put( "2", "完单详情");
        map.put( "3", "积分");
        map.put( "4", "数单奖");
        return AjaxResponse.success(map);
    }

    @ResponseBody
    @RequestMapping(value = "/exportSubscriptionUrl")
    @RequiresPermissions(value = "DownloadStatement_look")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DataSourceMode.SLAVE)
    })
    public void exportSubscriptionUrl(@Verify(param = "id",rule="required") Long id,
                                      HttpServletRequest request, HttpServletResponse response) {

        SubscriptionReport report = subscriptionReportConfigureService.selectSubscriptionReportByPrimaryKey(id);
        if (report == null) {
            return;
        }
        String url = report.getUrl();
        logger.info(LOGTAG + "下载地址,url={}", url);
        if (StringUtils.isEmpty(url)) {
            return;
        }
        String operation = url.split("/")[0] +"/"+ url.split("/")[1]
                + "/" + url.split("/")[2] + "/" + url.split("/")[3];
        String file = url.split("/")[4];
        //名称
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
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("utf-8"), "iso8859-1"));//指定下载的文件名
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
     * 根据订阅周期查询数据报表订阅配置
     * @param subscriptionCycle 订阅周期,1-周;2-月;
     * @param reportId 报表ID,1-工资明细;2-完单详情;3-积分;4-数单奖
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/querySubscriptionConfigure")
    @RequiresPermissions(value = "SubscribeStatement_look")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DataSourceMode.SLAVE)
    })
    public AjaxResponse querySubscriptionConfigure(
            @Verify(param = "subscriptionCycle",rule="required") Integer subscriptionCycle,
            @Verify(param = "reportId",rule="required") Integer reportId) {

        long start = System.currentTimeMillis();
        logger.info(LOGTAG + "/querySubscriptionConfigure,subscriptionCycle={}, reportId={}", subscriptionCycle, reportId);
        String level = "";//级别,1-全国;2-城市;4-加盟商;8-车队;16-班组（多个ID用英文逗号分隔）
        Set citiesSet = Sets.newHashSet();
        Set supplierIdsSet = Sets.newHashSet();
        Set teamIdsSet = Sets.newHashSet();

        Integer currentLevel = WebSessionUtil.getCurrentLoginUser().getLevel();//获取用户的级别,1-全国;2-城市;4-加盟商;8-车队;16-班组
        if(currentLevel!=null && currentLevel==16){
            JSONObject map = new JSONObject();
            map.put( "level", level);
            map.put( "cities", citiesSet);
            map.put( "supplierIds", supplierIdsSet);
            map.put( "teamIds", teamIdsSet);
            logger.info(LOGTAG + "/querySubscriptionConfigure班组权限无权查询");
            return AjaxResponse.success(map);
        }
        //查询全国
        List<SubscriptionReportConfigureDTO> allList = subscriptionReportConfigureService.querySubscriptionConfigure(subscriptionCycle, reportId, 1);
        if(allList!=null && allList.size()>0){
            level += ",1";
        }
        //查询城市
        List<SubscriptionReportConfigureDTO> cityList = subscriptionReportConfigureService.querySubscriptionConfigure(subscriptionCycle, reportId, 2);
        if(cityList!=null && cityList.size()>0){
            level += ",2";
            for (SubscriptionReportConfigureDTO sub : cityList) {
                citiesSet.add(sub.getCityId());
            }
        }
        //查询供应商
        List<SubscriptionReportConfigureDTO> supplerList = subscriptionReportConfigureService.querySubscriptionConfigure(subscriptionCycle, reportId, 4);
        if(supplerList!=null && supplerList.size()>0){
            level += ",4";
            for (SubscriptionReportConfigureDTO sub : supplerList) {
                citiesSet.add(sub.getCityId());
                supplierIdsSet.add(sub.getSupplierId());

            }
        }
        //查询车队
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
        logger.info(LOGTAG + "/querySubscriptionConfigure,subscriptionCycle={}, reportId={}，map={}, 耗时={}",
                subscriptionCycle, reportId, ToStringBuilder.reflectionToString(map), (end-start));
        return AjaxResponse.success(map);
    }

    /**
     * 数据报表
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/querySubscriptionLevel")
    @RequiresPermissions(value = "SubscribeStatement_look")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DataSourceMode.SLAVE)
    })
    public AjaxResponse querySubscriptionLevel() {
        Integer level = WebSessionUtil.getCurrentLoginUser().getLevel();//获取用户的级别,1-全国;2-城市;4-加盟商;8-车队;16-班组
        JSONObject map = new JSONObject();
        if (level != null) {
            switch (level) {
                case 1:
                    map.put( "1", "全国");
                    map.put( "2", "城市");
                    map.put( "4", "供应商");
                    map.put( "8", "车队");
                    break;
                case 2:
                    map.put( "2", "城市");
                    map.put( "4", "供应商");
                    map.put( "8", "车队");
                    break;
                case 4:
                    map.put( "4", "供应商");
                    map.put( "8", "车队");
                    break;
                case 8:
                    map.put( "8", "车队");
                    break;
                case 16:
                    map.put( "16", "小组");
                    break;
            }
        }
        return AjaxResponse.success(map);
    }
}
