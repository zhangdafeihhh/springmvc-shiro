package com.zhuanche.controller.feedback;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.enums.FeedBackManageStatusEnum;
import com.zhuanche.common.enums.FileSizeEnum;
import com.zhuanche.common.enums.MenuEnum;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.mdbcarmanage.FeedBackDetailDto;
import com.zhuanche.entity.mdbcarmanage.Feedback;
import com.zhuanche.entity.mdbcarmanage.FeedbackDoc;
import com.zhuanche.serv.feedback.FeedBackDocService;
import com.zhuanche.serv.feedback.FeedBackService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.FileUploadUtils;
import org.apache.commons.collections.CollectionUtils;
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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Controller
@RequestMapping("/feedback")
public class FeedBackController {

    private static final Logger logger = LoggerFactory.getLogger(FeedBackController.class);

    @Autowired
    FeedBackService feedBackService;

    @Autowired
    FeedBackDocService feedBackDocService;



    /**
     * ????????????????????????
     * @param createTimeStart
     * @param createTimeEnd
     * @param manageStatus
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/dataList")
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    @RequestFunction(menu = MenuEnum.PROBLEM_FEED_BACK_QUERY)
    //@RequiresPermissions("ProblemFeedbacks_look")
    public AjaxResponse dataList(
            @RequestParam(value = "createTimeStart",required = false) String createTimeStart,
            @RequestParam(value = "createTimeEnd", required = false) String createTimeEnd,
            @RequestParam(value = "manageStatus",required = false) Integer manageStatus,
            @RequestParam(value = "feedbackType",required = false) Integer feedbackType,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "30") Integer pageSize
            ){

        Page p = PageHelper.startPage(pageNum, pageSize, true);
        int total = 0;
        List<Feedback> feedbackList = null;
        try {
            feedbackList = feedBackService.findDataList(createTimeStart,createTimeEnd,manageStatus,feedbackType);
            if (null == feedbackList){
                feedbackList = Lists.newArrayList();
            }
            feedbackList.forEach(value -> {
                if (FeedBackManageStatusEnum.TO_ACCEPT.getCode() == value.getManageStatus()){
                    value.setManageTime(null);
                }
            });
            total = (int) p.getTotal();
        } finally {
            PageHelper.clearPage();
        }
        PageDTO pageDTO = new PageDTO(pageNum, pageSize, total, feedbackList);
        return AjaxResponse.success(pageDTO);
    }

    /**
     * ??????????????????????????????
     * @param createTimeStart
     * @param createTimeEnd
     * @param manageStatus
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/dataListSelf")
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    @RequestFunction(menu = MenuEnum.PROBLEM_FEED_BACK_QUERY)
    //@RequiresPermissions("ProblemFeedbacks_look")
    public AjaxResponse dataListSelf(
            @RequestParam(value = "createTimeStart",required = false) String createTimeStart,
            @RequestParam(value = "feedbackType",required = false) Integer feedbackType,
            @RequestParam(value = "createTimeEnd", required = false) String createTimeEnd,
            @RequestParam(value = "manageStatus",required = false) Integer manageStatus,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "30") Integer pageSize
    ){

        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        if (null == currentLoginUser){
            return AjaxResponse.fail(RestErrorCode.HTTP_INVALID_SESSION);
        }

        Page p = PageHelper.startPage(pageNum, pageSize, true);
        int total = 0;
        List<Feedback> feedbackList = null;
        try {
            Integer userId = currentLoginUser.getId();
            feedbackList = feedBackService.findDataListSelf(createTimeStart,createTimeEnd,manageStatus, userId,feedbackType);
            if (CollectionUtils.isEmpty(feedbackList)){
                feedbackList = Lists.newArrayList();
            }
            feedbackList.stream().forEach(value -> {
                if (FeedBackManageStatusEnum.TO_ACCEPT.getCode() == value.getManageStatus()){
                    value.setManageTime(null);
                }
            });
            total = (int) p.getTotal();
        } finally {
            PageHelper.clearPage();
        }
        if (CollectionUtils.isEmpty(feedbackList)){
            feedbackList = Lists.newArrayList();
        }
        PageDTO pageDTO = new PageDTO(pageNum, pageSize, total, feedbackList);
        return AjaxResponse.success(pageDTO);
    }

    /**
     * ??????????????????
     * @param feedbackContent
     * @param multipartFiles
     * @return
     */
    @RequestMapping(value = "/addFeedBack")
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    @RequestFunction(menu = MenuEnum.PROBLEM_FEED_BACK_ADD)
    //@RequiresPermissions("PROBLEM_FEED_BACK_ADD")
    public AjaxResponse addFeedBack(
            @Verify(param = "feedbackType",rule = "required") Integer feedbackType,
            @Verify(param = "feedbackContent",rule = "required") String feedbackContent,
            @RequestParam(value = "files",required = false) MultipartFile[] multipartFiles
    ){

        //??????????????????500???
        if (feedbackContent.length() > 500){
            return AjaxResponse.fail(RestErrorCode.MESSAGE_CONTENT_ERROR);
        }

        //????????????????????????????????????5m
        if (!FileUploadUtils.validateFilesSize(multipartFiles, FileSizeEnum.MByte.getSize() * 5)){
            return AjaxResponse.fail(RestErrorCode.FILE_SIZE_TOO_BIG);
        }

        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        if (null == currentLoginUser){
            return AjaxResponse.fail(RestErrorCode.HTTP_INVALID_SESSION);
        }

        try {
            Feedback feedback = new Feedback();
            //??????????????????
            feedback.setFeedbackContent(feedbackContent);
            //???????????????id
            feedback.setSenderId(currentLoginUser.getId());
            //?????????????????????
            feedback.setSenderName(currentLoginUser.getName());
            //??????????????????
            feedback.setFeedbackType(feedbackType);

            //????????????
            Date currentDate = new Date();
            feedback.setCreateTime(currentDate);
            feedback.setUpdateTime(currentDate);

            //????????????????????????
            feedback.setManageStatus(FeedBackManageStatusEnum.TO_ACCEPT.getCode());
            feedBackService.addFeedBack(feedback, multipartFiles);
            return AjaxResponse.success(null);
        } catch (Exception e) {
            logger.error("??????????????????:",e);
        }
        return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
    }

    /**
     * ????????????
     * @param feedbackDocId
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/download")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    @RequestFunction(menu = MenuEnum.PROBLEM_FEED_BACK_DOWNLOAD)
    //@RequiresPermissions("PROBLEM_FEED_BACK_DOWNLOAD")
    public ResponseEntity<byte[]> download(@Verify(param = "feedbackDocId",rule = "required") Integer feedbackDocId) throws Exception {

        FeedbackDoc feedbackDoc = feedBackDocService.selectFeedBackDocById(feedbackDocId);

        if (null == feedbackDoc){
            logger.error("???????????????????????????, ????????????id : [" + feedbackDocId + "]");
            return null;
        }

        String fileUrl = feedbackDoc.getDocUrl();
        String fileName = feedbackDoc.getDocName();

        if (null == fileUrl){
            logger.error("?????????????????????????????????, ????????????id : [" + feedbackDocId + "]");
            return null;
        }

        if(StringUtils.isEmpty(fileName)){
            logger.error("??????????????????????????????, ????????????id : [" + feedbackDocId + "]");
            fileName = UUID.randomUUID().toString().replace("-","");
        }

        try {
            String path = "";  //?????????
             File file = new File(path + fileUrl);
            HttpHeaders headers = new HttpHeaders();
            //?????????????????????????????????????????????????????????
            //??????????????????attachment??????????????????????????????
            headers.add("Content-Disposition", "attchement;filename="+ URLEncoder.encode(fileName,"UTF-8"));
            //application/octet-stream ??? ???????????????????????????????????????????????????
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file),
                    headers, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("??????????????????????????????:" + e.getMessage());
        }
        return null;
    }

    /**
     * ???id????????????
     * @param id
     * @return
     */
    @RequestMapping(value = "/feedBackDetail")
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    @RequestFunction(menu = MenuEnum.PROBLEM_FEED_BACK_QUERY)
    //@RequiresPermissions("ProblemFeedbacks_look")
    public AjaxResponse feedBackDetail(@Verify(param = "id",rule = "required") Integer id){

        try {
            FeedBackDetailDto feedBackDetailDto = feedBackService.queryFeedBackDetailByFeedBackId(id);
            if (null == feedBackDetailDto){
                return AjaxResponse.fail(RestErrorCode.NOT_FOUND_RESULT);
            }
            if (feedBackDetailDto.getManageStatus() == FeedBackManageStatusEnum.TO_ACCEPT.getCode()){
                feedBackDetailDto.setManageTime(null);
            }
            return AjaxResponse.success(feedBackDetailDto);
        } catch (Exception e) {
            logger.error("???id????????????????????????:",e);
        }
        return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
    }


    /**
     * ??????????????????
     * @param feedbackId
     * @param manageContent
     * @return
     */
    @RequestMapping(value = "updateFeedBack")
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    @RequestFunction(menu = MenuEnum.PROBLEM_FEED_BACK_MANAGE)
    //@RequiresPermissions("PROBLEM_FEED_BACK_MANAGE")
    public AjaxResponse updateFeedBack(
            @Verify(param = "feedbackId",rule = "required") Integer feedbackId,
            @Verify(param = "manageContent",rule = "required") String manageContent
            ){

        //??????????????????500???
        if (manageContent.length() > 500){
            return AjaxResponse.fail(RestErrorCode.MESSAGE_CONTENT_ERROR);
        }

        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        if (null == currentLoginUser){
            return AjaxResponse.fail(RestErrorCode.HTTP_INVALID_SESSION);
        }

        try {
            Integer userId = currentLoginUser.getId();

            Feedback feedback = new Feedback();
            //??????id
            feedback.setId(feedbackId);
            //????????????
            Date currentDate = new Date();
            //????????????
            feedback.setUpdateTime(currentDate);
            //????????????
            feedback.setManageTime(currentDate);
            //?????????
            feedback.setManageId(userId);
            //???????????????????????????
            feedback.setManageStatus(FeedBackManageStatusEnum.ALREADY_ACCEPT.getCode());
            //????????????
            feedback.setManageContent(manageContent);

            //??????????????????
            int result = feedBackService.updateFeedBack(feedback);

            if (result > 0){
                return AjaxResponse.success(null);
            }
        } catch (Exception e) {
            logger.error("????????????????????????:", e);
        }

        return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
    }



}
