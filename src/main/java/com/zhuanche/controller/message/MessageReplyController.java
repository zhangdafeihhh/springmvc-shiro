package com.zhuanche.controller.message;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.enums.MenuEnum;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.entity.mdbcarmanage.CarMessageReceiver;
import com.zhuanche.entity.mdbcarmanage.CarMessageReply;
import com.zhuanche.serv.message.MessageReplyService;
import com.zhuanche.serv.message.MessageService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import mapper.mdbcarmanage.ex.CarMessageReceiverExMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/reply")
public class MessageReplyController {

    private static final Logger logger = LoggerFactory.getLogger(MessageReplyController.class);

    @Autowired
    private MessageReplyService messageReplyService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private CarMessageReceiverExMapper carMessageReceiverExMapper;

    @RequestMapping(value = "/addReply")
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
//    @RequestFunction(menu = )
    public AjaxResponse addReply(
            @Verify(param = "messageId", rule = "required") Long messageId,
            @Verify(param = "replyContent", rule = "required") String replyContent
            ){

        //??????????????????500???
        if (replyContent.length() > 500){
            return AjaxResponse.fail(RestErrorCode.MESSAGE_CONTENT_ERROR);
        }

        try {
            SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();

            if (null == currentLoginUser){
                return AjaxResponse.fail(RestErrorCode.HTTP_INVALID_SESSION);
            }

            //????????????
            CarMessageReply reply = new CarMessageReply();
            //?????????id
            reply.setSenderId(currentLoginUser.getId());
            //???????????????
            reply.setSenderName(currentLoginUser.getName());
            //??????????????????
            reply.setReplyContent(replyContent);
            //???????????????id
            reply.setMessageId(messageId);
            Date currentDate = new Date();
            //????????????
            reply.setCreateTime(currentDate);
            //????????????
            reply.setUpdateTime(currentDate);

            Integer result = messageReplyService.addReply(reply);

            if (result > 0){
                //????????????????????????
                carMessageReceiverExMapper.replyMessage(messageId.intValue(),
                        currentLoginUser.getId(),
                        CarMessageReceiver.ReadStatus.reply.getValue());
                return AjaxResponse.success(null);
            }

        } catch (Exception e) {
            logger.error("??????????????????:",e);
        }
        return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
    }


    @RequestMapping(value = "/replyList")
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public AjaxResponse replyList(
            @Verify(param = "messageId", rule = "required|min(1)") Long messageId,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "30") Integer pageSize
            ){

        //????????????????????????
        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        if (null == currentLoginUser){
            return AjaxResponse.fail(RestErrorCode.HTTP_INVALID_SESSION);
        }
        Integer userId = currentLoginUser.getId();

        //???????????????????????????????????????????????????
        boolean isAuthor = messageService.isMessageAuthor(userId,messageId);

        Page p = PageHelper.startPage(pageNum, pageSize, true);
        List<CarMessageReply> list = null;
        int total = 0;
        try {
            if (isAuthor){
                //????????????????????????????????????,??????????????????????????????
                list = messageReplyService.findReplyListByMessageIdPage(messageId);
            }else {
                //???????????????????????????????????????,?????????????????????????????????
                list = messageReplyService.findReplyListByMessageIdAndSenderIdPage(messageId, userId);
            }
            total = (int) p.getTotal();
        } finally {
            PageHelper.clearPage();
        }

        if (null == list){
            list = Lists.newArrayList();
        }

        PageDTO pageDTO = new PageDTO(pageNum, pageSize, total, list);
        return AjaxResponse.success(pageDTO);
    }

    /**
     * ?????????????????????
     * @param messageId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/replyListSelf")
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public AjaxResponse replyListSelf(
            @Verify(param = "messageId", rule = "required|min(1)") Long messageId,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "30") Integer pageSize
    ) {

        //????????????????????????
        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        if (null == currentLoginUser) {
            return AjaxResponse.fail(RestErrorCode.HTTP_INVALID_SESSION);
        }
        Integer userId = currentLoginUser.getId();


        Page p = PageHelper.startPage(pageNum, pageSize, true);
        List<CarMessageReply> list = null;
        int total = 0;
        try {
            list = messageReplyService.findReplyListByMessageIdAndSenderIdPage(messageId, userId);
            total = (int) p.getTotal();
        } finally {
            PageHelper.clearPage();
        }

        if (null == list) {
            list = Lists.newArrayList();
        }

        PageDTO pageDTO = new PageDTO(pageNum, pageSize, total, list);
        return AjaxResponse.success(pageDTO);
    }

}
