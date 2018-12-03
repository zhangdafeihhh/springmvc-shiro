package com.zhuanche.controller.message;

import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.constant.Constants;
import com.zhuanche.dto.mdbcarmanage.CarMessageDetailDto;
import com.zhuanche.entity.mdbcarmanage.CarMessagePost;
import com.zhuanche.exception.MessageException;
import com.zhuanche.serv.message.MessageService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.HtmlFilterUtil;
import mapper.mdbcarmanage.ex.CarAdmUserExMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
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

    @Autowired
    private CarAdmUserExMapper carAdmUserExMapper;


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
    @RequiresPermissions(value = {"PublishMessage"})
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    public AjaxResponse postMessage(@RequestParam(value = "messageId",required = false)Integer messageId,
                                    @RequestParam(value = "status",required = true) Integer status,
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
                "level:{4},cities:{5},suppliers:{6},teamId{7},docName:{8},docUrl:{9},messageId:{10}",
                String.valueOf(status),creater,messageTitle,messageContent,String.valueOf(level),cities,
                suppliers,teamId,docName,docUrl,messageId));

        if (StringUtils.isBlank(creater) || StringUtils.isBlank(String.valueOf(status)) ){
            logger.info("消息为发布状态，必传参数为空");
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
        }

        if (StringUtils.isNotEmpty(messageContent)){
           String  clearCss = HtmlFilterUtil.HTMLTagSpirit(messageContent);
           if (clearCss.length() > 2000){
               logger.info("字体长度大于2000");
               return AjaxResponse.fail(RestErrorCode.MESSAGE_CONTENT_ERROR);
           }
           if (messageContent.length() - clearCss.length() > 4000-clearCss.length()){
               logger.info("样式长度过于复杂");
               return AjaxResponse.fail(RestErrorCode.MESSAGE_CONTENT_CSS_TOO_MANY);
           }

        }

        if(status.equals(CarMessagePost.Status.publish)){
          if (StringUtils.isBlank(creater) || StringUtils.isBlank(messageTitle) || StringUtils.isBlank(messageContent)
                  || StringUtils.isBlank(String.valueOf(level)) ){
              logger.info("消息为发布状态，必传参数为空");
              return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
          }
        }

        AjaxResponse response = AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        try {
            int code = messageService.postMessage(messageId,status, Integer.valueOf(creater), messageTitle, messageContent, level, cities, suppliers, teamId, docName, docUrl,
                    file,request);

            if (code > 0){
                logger.info("消息发送成功");
                return AjaxResponse.success(null);
            }
        } catch (MessageException e) {
           response = AjaxResponse.fail(e.getCode());
        }
        return  response;
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


        if (StringUtils.isBlank(creater) || StringUtils.isBlank(String.valueOf(status)) ){
            logger.info("消息为发布状态，必传参数为空");
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
        }


        if(status.equals(CarMessagePost.Status.publish)){
            if (StringUtils.isBlank(creater) || StringUtils.isBlank(messageTitle) || StringUtils.isBlank(messageContent)
                    || StringUtils.isBlank(String.valueOf(level)) ){
                logger.info("消息为发布状态，必传参数为空");
                return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);

            }
        }
        AjaxResponse response = AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        try {
            int code = messageService.postMessage(Integer.valueOf(messageId),status, Integer.valueOf(creater), messageTitle, messageContent, level, cities, suppliers, teamId, docName, docUrl,
                    file,request);
            if (code > 0){
                logger.info("消息发送成功");
                return AjaxResponse.success(null);
            }
        } catch (MessageException e) {
            response = AjaxResponse.fail(e.getCode());
        }

        return  response;


    }


    /**
     * 撤回操作
     * @param messageId
     * @return
     */
    @RequestMapping(value = "/messageWithDraw")
    @ResponseBody
    @RequiresPermissions(value = {"PublishMessage"})
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
        try {
            int code = messageService.withDraw(messageId);
            if (code > 0){
                return AjaxResponse.success(null);
            }else {
                return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
            }
        } catch (MessageException e) {
            return AjaxResponse.fail(e.getCode());
        }
    }



    /**
     * 撤回操作
     * @param messageId
     * @return
     */
    @RequestMapping(value = "/messageDeleteDraw")
    @RequiresPermissions(value = {"PublishMessage"})
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    public AjaxResponse messageDeleteDraw(@RequestParam(value = "messageId") Integer messageId){

        logger.info(MessageFormat.format("messageDeleteDraw入参:{0}",messageId));
        if (StringUtils.isBlank(String.valueOf(messageId))){
            logger.info("messageWithDraw入参 messageId为空");
            return  AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isBlank(String.valueOf(messageId))){
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
        }
        try {
            int code = messageService.messageDeleteDraw(messageId);
            if (code > 0){
                return AjaxResponse.success(null);
            }else {
                return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
            }
        } catch (MessageException e) {
            return AjaxResponse.fail(e.getCode());
        }
    }


    /**
     * 获取列表
     * @param userId
     * @param status
     * @param pageSize
     * @param pageNum
     * @return
     */
    @RequestMapping(value = "/messageLisByStatus")
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public AjaxResponse messageLisByStatus(@RequestParam(value = "userId",defaultValue = "0")Integer userId,
                                           @RequestParam(value = "status",defaultValue = "1")Integer status,
                                           @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                           @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum){

        logger.info(MessageFormat.format("messageLisByStatus入参:userId:{0},status:{1},pageSize:{2},pageNum:{3}",
                userId,status,pageSize,pageNum));
        if(StringUtils.isBlank(String.valueOf(status)) || StringUtils.isBlank(String.valueOf(userId))){
            logger.info("查询条件缺失");
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
        }
        try {
            PageDTO pageDTO = messageService.messageLisByStatus(userId,status,pageNum,pageSize);
            return AjaxResponse.success(pageDTO);
        } catch (MessageException e) {
            return AjaxResponse.fail(e.getCode());
        }

    }

    @RequestMapping(value = "/messageDetail")
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    public AjaxResponse messageDetail(@RequestParam(value = "messageId",defaultValue = "0")Integer messageId,
                                      @RequestParam(value = "userId",defaultValue = "0")Integer userId){

        logger.info(MessageFormat.format("messageDetail入参:messageId{0},userId:{1}",messageId,userId));
        if (StringUtils.isBlank(String.valueOf(messageId)) || StringUtils.isBlank(String.valueOf(userId))){
            logger.info("messageDetail 入参确实");
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }
        try {
            CarMessageDetailDto detailDto = messageService.messageDetail(messageId,userId);
            return AjaxResponse.success(detailDto);
        } catch (MessageException e) {
            return AjaxResponse.fail(e.getCode());
        }
    }

    @RequestMapping(value = "/messageUnreadCount")
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public AjaxResponse messageUnreadCount(@RequestParam(value = "userId",defaultValue = "")Integer userId){
        logger.info(MessageFormat.format("messageUnreadCount入参:userId:{0}",userId));
        if (StringUtils.isEmpty(String.valueOf(userId))) {
            logger.info("messageUnreadCount参数为空");
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
        }
        try {
            Integer count = messageService.unReadCount(userId);
            Map<String,Object> map = new HashMap<>();
            map.put("userId",userId);
            if (count != null && count>0){
             map.put("count",count);
            }else {
                map.put("count",0);
            }
            return AjaxResponse.success(map);
        } catch (MessageException e) {
            return AjaxResponse.fail(e.getCode());
        }
    }

    @RequestMapping(value = "/messageSearch",method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public AjaxResponse messageSearch(String range, String keyword,
                                      String timeRange, String createUser,
                                      @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
                                      @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum) {
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        Integer userId = loginUser.getId();
        if (StringUtils.isEmpty(range)){
            range = Constants.ALL_RANGE;
        }else {
            if (!range.equals(Constants.TITLE) && !range.equals(Constants.ATTACHMENT) && !range.equals(Constants.ALL_RANGE)){
                return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR, "range is invalid");
            }
        }
        String startDate = null;
        String endDate = null;
        if (StringUtils.isNotEmpty(timeRange)){
            String[] split = timeRange.split(Constants.TILDE);
            startDate = split[0];
            endDate = split[1];
        }
        List<Integer> idList = null;
        if (StringUtils.isNotBlank(createUser)){
            idList = carAdmUserExMapper.queryIdListByName(createUser);
        }
        PageDTO pageDTO = messageService.messageSearch(range, keyword,
                startDate, endDate, idList, pageSize, pageNum, userId);
        return AjaxResponse.success(pageDTO);
    }


    @RequestMapping(value="/download")
    public ResponseEntity<byte[]> download(@RequestParam("fileUrl") String fileUrl)
            throws Exception {
        //下载文件路径
        try {
            String path = "";  //服务器
            //String path = "/Users/fan/upload";  //本地
            logger.info("path:" + path);
            File file = new File(path + File.separator + fileUrl);
            HttpHeaders headers = new HttpHeaders();
            //下载显示的文件名，解决中文名称乱码问题
            String downloadFielName = new String(fileUrl.getBytes("UTF-8"),"iso-8859-1");
            String fileName = downloadFielName.substring(downloadFielName.lastIndexOf(File.separator)+1);
            System.out.println(fileName);
            //通知浏览器以attachment（下载方式）打开图片
            headers.setContentDispositionFormData("attachment", fileName);
            //application/octet-stream ： 二进制流数据（最常见的文件下载）。
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file),
                    headers, HttpStatus.CREATED);
        } catch (IOException e) {
            logger.info("下载错误：" + e.getMessage());
        }
        return null;
    }


}
