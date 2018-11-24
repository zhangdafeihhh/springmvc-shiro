package com.zhuanche.controller.message;

import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.mdbcarmanage.CarMessageDetailDto;
import com.zhuanche.entity.mdbcarmanage.CarMessagePost;
import com.zhuanche.serv.message.MessageService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author fanht
 * @Description  消息中心
 * @Date 2018/11/22 下午3:57
 * @Version 1.0
 */
@Controller
@RequestMapping("/message")
public class MessageManagerController {


    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private MessageService messageService;


    /**
     * 发送消息接口
     * @param status 1 草稿 2 发送
     * @param creater 创建人
     * @param messageTitle 消息标题
     * @param messageContent 消息内容
     * @param level 级别 1；全国权限2；城市权限4；加盟商权限8；车队权限16；可以多个组合
     * @param cities 城市
     * @param suppliers 加盟商
     * @param teamId 车队
     * @param docName 文档名称
     * @param docUrl 文档地址
     * @return
     */
    @RequestMapping(value = "/postMessage",method = RequestMethod.POST)
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    public AjaxResponse postMessage(@RequestParam(value = "status",required = true) Integer status,
                                    //@Verify(param = "status",rule = "required") Integer status,
                                    @RequestParam(value = "creater",required = false) String creater,
                                    @RequestParam(value = "messageTitle",required = false) String messageTitle,
                                    @RequestParam(value = "messageContent",required = false) String messageContent,
                                    @RequestParam(value = "level",required = false) Integer level,
                                    @RequestParam(value = "cities",required = false) String cities,
                                    @RequestParam(value = "suppliers",required = false) String suppliers,
                                    @RequestParam(value = "teamId",required = false) String teamId,
                                    @RequestParam(value = "docName",required = false) String docName,
                                    @RequestParam(value = "docUrl",required = false) String docUrl,
                                    @RequestParam(value = "file",required = false) MultipartFile file,
                                    HttpServletRequest request){

        logger.info(MessageFormat.format("postMessage入参：status:{0},creater:{1},messageTitle:{2},messageContent:{3}," +
                "level:{4},cities:{5},suppliers:{6},teamId{7},docName:{8},docUrl:{9}",
                String.valueOf(status),creater,messageTitle,messageContent,String.valueOf(level),cities,
                suppliers,teamId,docName,docUrl));

        if(status.equals(CarMessagePost.Status.publish)){
          if (StringUtils.isBlank(creater) || StringUtils.isBlank(messageTitle) || StringUtils.isBlank(messageContent)
                  || StringUtils.isBlank(String.valueOf(level)) ){
              logger.info("消息为发布状态，必传参数为空");
              return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);

          }
        }

        AjaxResponse response = AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        int code = messageService.postMessage(null,status, Integer.valueOf(creater), messageTitle, messageContent, level, cities, suppliers, teamId, docName, docUrl,
                file,request);

        if (code > 0){
            logger.info("消息发送成功");
            return AjaxResponse.success(null);
        }
        return  response;
        //return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
    }


    /**
     * 更新草稿状态
     * @param status 1 草稿 2 发送
     * @param creater 创建人
     * @param messageTitle 消息标题
     * @param messageContent 消息内容
     * @param level 级别 1；全国权限2；城市权限4；加盟商权限8；车队权限16；可以多个组合
     * @param cities 城市
     * @param suppliers 加盟商
     * @param teamId 车队
     * @param docName 文档名称
     * @param docUrl 文档地址
     * @return
     */
    @RequestMapping(value = "/updateMessage",method = RequestMethod.POST)
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    public AjaxResponse updateDraftMessage(@RequestParam(value = "messageId",required = true)String messageId,
                                           @Verify(param = "status",rule = "required") Integer status,
                                           @RequestParam(value = "creater",required = false) String creater,
                                           @RequestParam(value = "messageTitle",required = false) String messageTitle,
                                           @RequestParam(value = "messageContent",required = false) String messageContent,
                                           @RequestParam(value = "level",required = false) Integer level,
                                           @RequestParam(value = "cities",required = false) String cities,
                                           @RequestParam(value = "suppliers",required = false) String suppliers,
                                           @RequestParam(value = "teamId",required = false) String teamId,
                                           @RequestParam(value = "docName",required = false) String docName,
                                           @RequestParam(value = "docUrl",required = false) String docUrl,
                                           @RequestParam(value = "file",required = false) MultipartFile file,
                                           HttpServletRequest request){
        logger.info(MessageFormat.format("updateDraftMessage入参：status:{0},creater:{1},messageTitle:{2},messageContent:{3}," +
                        "level:{4},cities:{5},suppliers:{6},teamId{7},docName:{8},docUrl:{9}",
                String.valueOf(status),creater,messageTitle,messageContent,String.valueOf(level),cities,
                suppliers,teamId,docName,docUrl));

        if(status.equals(CarMessagePost.Status.publish)){
            if (StringUtils.isBlank(creater) || StringUtils.isBlank(messageTitle) || StringUtils.isBlank(messageContent)
                    || StringUtils.isBlank(String.valueOf(level)) ){
                logger.info("消息为发布状态，必传参数为空");
                return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);

            }
        }
        AjaxResponse response = AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        int code = messageService.postMessage(Integer.valueOf(messageId),status, Integer.valueOf(creater), messageTitle, messageContent, level, cities, suppliers, teamId, docName, docUrl,
                file,request);

        if (code > 0){
            logger.info("消息发送成功");
            return AjaxResponse.success(null);
        }
        return  response;

    }


    /**
     * 撤回操作
     * @param messageId
     * @return
     */
    @RequestMapping(value = "/messageWithDraw",method = RequestMethod.POST)
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    public AjaxResponse messageWithDraw(@RequestParam(value = "messageId") Integer messageId){

        logger.info(MessageFormat.format("messageWithDraw入参:{0}",messageId));
        if (StringUtils.isBlank(String.valueOf(messageId))){
            logger.info("messageWithDraw入参 messageId为空");
            return  AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isBlank(String.valueOf(messageId))){
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
        }
        int code = messageService.withDraw(messageId);
        if (code > 0){
            return AjaxResponse.success(null);
        }else {
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }
    }


    /**
     * 获取列表
     * @param messageId
     * @param userId
     * @param status
     * @param pageSize
     * @param pageNum
     * @return
     */
    @RequestMapping(value = "/messageLisByStatus",method = RequestMethod.GET)
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public AjaxResponse messageLisByStatus(@RequestParam(value = "messageId",defaultValue = "0")Integer messageId,
                                           @RequestParam(value = "userId",defaultValue = "0")Integer userId,
                                           @RequestParam(value = "status",defaultValue = "1")Integer status,
                                           @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                           @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum){

        logger.info(MessageFormat.format("messageLisByStatus入参:messageId:{0},userId:{1},status:{3}",
                messageId,userId,status));
        if(StringUtils.isBlank(String.valueOf(messageId)) || StringUtils.isBlank(String.valueOf(userId))){
            logger.info("查询条件缺失");
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
        }
        PageDTO pageDTO = messageService.messageLisByStatus(userId,status,pageSize,pageNum);
        return AjaxResponse.success(pageDTO);

    }

    @RequestMapping(value = "/messageDetail",method = RequestMethod.POST)
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public AjaxResponse messageDetail(@RequestParam(value = "messageId",defaultValue = "0")Integer messageId,
                                      @RequestParam(value = "userId",defaultValue = "0")Integer userId){

        logger.info(MessageFormat.format("messageDetail入参:messageId{0},userId:{1}",messageId,userId));
        if (StringUtils.isBlank(String.valueOf(messageId)) || StringUtils.isBlank(String.valueOf(userId))){
            logger.info("messageDetail 入参确实");
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }
        CarMessageDetailDto detailDto = messageService.messageDetail(messageId,userId);
        return AjaxResponse.success(detailDto);
    }

    @RequestMapping(value = "/messageUnreadCount",method = RequestMethod.POST)
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public AjaxResponse messageUnreadCount(@RequestParam(value = "userId",defaultValue = "")Integer userId){
        logger.info(MessageFormat.format("messageUnreadCount入参:userId:{0}",userId));
        if (StringUtils.isBlank(String.valueOf(userId))) {
            logger.info("messageUnreadCount参数为空");
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
        }

        Integer count = messageService.unReadCount(userId);
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        if (count != null && count>0){
         map.put("count",count);
        }else {
            map.put("count",0);
        }
        return AjaxResponse.success(map);
    }


}
