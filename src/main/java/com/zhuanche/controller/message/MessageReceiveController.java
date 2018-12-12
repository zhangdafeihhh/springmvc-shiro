package com.zhuanche.controller.message;

import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.exception.MessageException;
import com.zhuanche.serv.message.MessageReceiveService;
import okhttp3.internal.http2.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;

/**
 * @Author fanht
 * @Description  向字表发消息
 * @Date 2018/11/22 下午7:55
 * @Version 1.0
 */
@Controller
@RequestMapping("/messageReceive")
public class MessageReceiveController {


    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private MessageReceiveService receiveService;

    @RequestMapping(value = "/sendMessage",method = RequestMethod.POST)
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    public AjaxResponse sendMessage(Integer messageId,
                                    Integer level,
                                    String cities,
                                    String suppliers,
                                    String teamId){

        logger.info(MessageFormat.format("sendMessage入参:messageId:{0}," +
                "level:{1},cities:{2},suppliers:{3},teamId:{4}",messageId,level,
                cities,suppliers,teamId));
        if (StringUtils.isBlank(String.valueOf(messageId)) ||
                 StringUtils.isBlank(String.valueOf(level))){
            logger.info("sendMessage 参数错误");
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
        }

        AjaxResponse response = AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);

        try {
            int code = receiveService.sendMessage(messageId,level,cities,suppliers,teamId);

            if (code > 0){
                return AjaxResponse.success(null);
            }
        } catch (MessageException e) {

            response = AjaxResponse.fail(e.getCode());
        }

        return response;

    }
}
