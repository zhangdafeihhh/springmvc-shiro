package com.zhuanche.controller.message;

import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.exception.MessageException;
import com.zhuanche.serv.message.MessageReceiveService;
import com.zhuanche.shiro.realm.SSOLoginUser;
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

import java.text.MessageFormat;
import java.util.List;

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


    @RequestMapping(value = "/replyQueryList",method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public AjaxResponse replyQueryList(String senderName,Integer status,
                                         String noticeStartTime,String noticeEndTime,
                                         String createStartTime,String createEndTime,
                                         String replyStartTime,String replyEndTime,
                                         @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
                                         @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum) {
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        /*Integer userId = loginUser.getId();
        List<Integer> idList = null;
        if (StringUtils.isNotBlank(createUser)){
            idList = carAdmUserExMapper.queryIdListByName(createUser);
        }
        PageDTO pageDTO = messageService.newMessageSearch(status,keyword,
                startDate, endDate, idList, pageSize, pageNum, userId);*/
        return AjaxResponse.success(null);
    }

}
