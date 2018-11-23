package com.zhuanche.controller.subscription;


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
import com.zhuanche.dto.driver.SubscriptionReportConfigureDTO;
import com.zhuanche.entity.driver.SubscriptionReport;
import com.zhuanche.entity.driver.SubscriptionReportConfigure;
import com.zhuanche.serv.subscription.SubscriptionReportConfigureService;
import com.zhuanche.shiro.session.WebSessionUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/subscription/report")
public class SubscriptionReportConfigureController {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionReportConfigureController.class);
    private static final String LOGTAG = "[数据报表订阅]: ";

    private String SUBSCRIPTION = "subscription";

    @Autowired
    private SubscriptionReportConfigureService subscriptionReportConfigureService;

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
                    return AjaxResponse.fail(RestErrorCode.CAR_API_ERROR, "操作太频繁，请稍后。");
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
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DataSourceMode.SLAVE)
    })
    public AjaxResponse querySubscriptionList(Integer reportId, Integer subscriptionCycle, Integer cityId,
                                       Integer supplierId, Integer teamId,
                                       @RequestParam(value="page", defaultValue="0")Integer page,
                                       @RequestParam(value="pageSize", defaultValue="20")Integer pageSize) {

        int total = 0;
        List<SubscriptionReport> list =  Lists.newArrayList();

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
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/querySubscriptionConfigure")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DataSourceMode.SLAVE)
    })
    public AjaxResponse querySubscriptionConfigure(
            @Verify(param = "subscriptionCycle",rule="required") Integer subscriptionCycle) {

        logger.info(LOGTAG + "/querySubscriptionConfigure,subscriptionCycle={}", subscriptionCycle);
        List<SubscriptionReportConfigureDTO> cycleList = subscriptionReportConfigureService.selectBySubscriptionCycle(subscriptionCycle);
        return AjaxResponse.success(cycleList);
    }


    /**
     * 数据报表下载地址保存
     *
     * @param bussinessNumber subscription_report_configure表中的主键
     * @param subscriptionTime 时间范围:年-月-日~年-月-日
     * @param url 报表链接
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveSubscriptionUrl", method = RequestMethod.POST)
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DataSourceMode.SLAVE)
    })
    public AjaxResponse saveSubscriptionUrl(
            @Verify(param = "bussinessNumber",rule="required") Integer bussinessNumber,
            @Verify(param = "subscriptionTime",rule="required") String subscriptionTime,
            @Verify(param = "url",rule="required") String url) {

        logger.info(LOGTAG + "数据报表下载地址保存,bussinessNumber={}, subscriptionTime={}, url={}", bussinessNumber, subscriptionTime, url);
        SubscriptionReportConfigure reportConfigure = subscriptionReportConfigureService.selectByPrimaryKey(new Long(bussinessNumber));
        if(reportConfigure==null){
            return AjaxResponse.fail(RestErrorCode.SUBSCRIPTION_NOT_EXIST);
        }
        SubscriptionReport subscriptionReport = new SubscriptionReport();
        subscriptionReport.setBussinessNumber(bussinessNumber);
        subscriptionReport.setSubscriptionTime(subscriptionTime);
        subscriptionReport.setUrl(url);
        subscriptionReport.setReportId(reportConfigure.getReportId());
        subscriptionReport.setReportName(reportConfigure.getReportName());
        subscriptionReport.setCityId(reportConfigure.getCityId());
        subscriptionReport.setCityName(reportConfigure.getCityName());
        subscriptionReport.setSupplierId(reportConfigure.getSupplierId());
        subscriptionReport.setSupplierName(reportConfigure.getSupplierName());
        subscriptionReport.setTeamId(reportConfigure.getTeamId());
        subscriptionReport.setTeamName(reportConfigure.getTeamName());
        subscriptionReport.setLevel(reportConfigure.getLevel());
        subscriptionReport.setSubscriptionCycle(reportConfigure.getSubscriptionCycle());
        subscriptionReport.setCreateId(reportConfigure.getCreateId());
        subscriptionReport.setCreateName(reportConfigure.getCreateName());

        int i = subscriptionReportConfigureService.saveSubscriptionUrl(subscriptionReport);
        if(i>0){
            return AjaxResponse.success(null);
        }else {
            return AjaxResponse.fail(RestErrorCode.CAR_API_ERROR, "保存失败");
        }
    }

    /**
     * 数据报表
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/querySubscriptionName")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DataSourceMode.SLAVE)
    })
    public AjaxResponse querySubscriptionName() {

        Map<Integer, Object> map = Maps.newHashMap();
        map.put( 1, "工资明细");
        map.put( 2, "完单详情");
        map.put( 3, "积分");
        map.put( 4, "数单奖");
        return AjaxResponse.success(map);
    }
}
