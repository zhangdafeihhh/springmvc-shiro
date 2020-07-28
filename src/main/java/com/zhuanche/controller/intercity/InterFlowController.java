package com.zhuanche.controller.intercity;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.bigdata.BiNewcityDriverReportDayDto;
import com.zhuanche.entity.bigdata.BiNewcityDriverReportDay;
import com.zhuanche.serv.bigdata.BiNewcityDriverReportDayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.MessageFormat;
import java.util.Map;

/**
 * @Author fanht
 * @Description
 * @Date 2020/7/20 下午1:42
 * @Version 1.0
 */
@Controller
@RequestMapping("/interFlow")
public class InterFlowController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BiNewcityDriverReportDayService  service;

    @RequestMapping("/queryFlowList")
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "bigdata-DataSource",mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public AjaxResponse queryFlowList(BiNewcityDriverReportDay reportDay,
                                      @Verify(param = "pageNum",rule = "required") Integer pageNum,
                                      @Verify(param = "pageSize",rule = "required") Integer pageSize,
                                      Integer dataType,
                                      String dataBeginDate,
                                      String dataEndDate,
                                      @Verify(param = "sort",rule = "max(2) ")@RequestParam(value = "pageNum",defaultValue = "1")Integer sort){
        logger.info(MessageFormat.format("=====查询司机流水列表=======入参", JSONObject.toJSON(reportDay),pageNum,pageSize,dataType,dataBeginDate,dataEndDate));

        Map<String,Object> map = Maps.newHashMap();
        PageDTO pageDTO = null;
        try {
            pageDTO = service.pageInfoList(reportDay,pageNum,pageSize,dataType,dataBeginDate,dataEndDate,sort);

            BiNewcityDriverReportDayDto dto = service.flowTotal(reportDay,dataType,dataBeginDate,dataEndDate);

            

            map.put("pageDTO",pageDTO);

            map.put("flowData",dto);

        } catch (Exception e) {
            logger.error("查询城际拼车流水统计异常",e);
        }

        return AjaxResponse.success(map);
    }
}
