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
     * 问题反馈分页列表
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
    @RequiresPermissions("ProblemFeedbacks_look")
    public AjaxResponse dataList(
            @RequestParam(value = "createTimeStart",required = false) String createTimeStart,
            @RequestParam(value = "createTimeEnd", required = false) String createTimeEnd,
            @RequestParam(value = "manageStatus",required = false) Integer manageStatus,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "30") Integer pageSize
            ){

        Page p = PageHelper.startPage(pageNum, pageSize, true);
        int total = 0;
        List<Feedback> feedbackList = null;
        try {
            feedbackList = feedBackService.findDataList(createTimeStart,createTimeEnd,manageStatus);
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
     * 问题反馈新增
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
    @RequiresPermissions("PROBLEM_FEED_BACK_ADD")
    public AjaxResponse addFeedBack(
            @Verify(param = "feedbackContent",rule = "required") String feedbackContent,
            @RequestParam(value = "files",required = false) MultipartFile[] multipartFiles
    ){

        //最大不能输入500字
        if (feedbackContent.length() >= 500){
            return AjaxResponse.fail(RestErrorCode.MESSAGE_CONTENT_ERROR);
        }

        //单个文件长度大小不能超过5m
        if (!FileUploadUtils.validateFilesSize(multipartFiles, FileSizeEnum.MByte.getSize() * 5)){
            return AjaxResponse.fail(RestErrorCode.FILE_SIZE_TOO_BIG);
        }

        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        if (null == currentLoginUser){
            return AjaxResponse.fail(RestErrorCode.HTTP_INVALID_SESSION);
        }

        try {
            Feedback feedback = new Feedback();
            //设置反馈内容
            feedback.setFeedbackContent(feedbackContent);
            //设置反馈人id
            feedback.setSenderId(currentLoginUser.getId());
            //设置反馈人姓名
            feedback.setSenderName(currentLoginUser.getName());

            //设置时间
            Date currentDate = new Date();
            feedback.setCreateTime(currentDate);
            feedback.setUpdateTime(currentDate);

            //设置状态为待受理
            feedback.setManageStatus(FeedBackManageStatusEnum.TO_ACCEPT.getCode());
            feedBackService.addFeedBack(feedback, multipartFiles);
            return AjaxResponse.success(null);
        } catch (Exception e) {
            logger.error("问题反馈异常:",e);
        }
        return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
    }

    /**
     * 附件下载
     * @param feedbackDocId
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/download")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    @RequestFunction(menu = MenuEnum.PROBLEM_FEED_BACK_DOWNLOAD)
    @RequiresPermissions("PROBLEM_FEED_BACK_DOWNLOAD")
    public ResponseEntity<byte[]> download(@Verify(param = "feedbackDocId",rule = "required") Integer feedbackDocId) throws Exception {

        FeedbackDoc feedbackDoc = feedBackDocService.selectFeedBackDocById(feedbackDocId);

        if (null == feedbackDoc){
            logger.error("问题反馈文件不存在, 传入文件id : [" + feedbackDocId + "]");
            return null;
        }

        String fileUrl = feedbackDoc.getDocUrl();
        String fileName = feedbackDoc.getDocName();

        if (null == fileUrl){
            logger.error("问题反馈文件路径不存在, 传入文件id : [" + feedbackDocId + "]");
            return null;
        }

        if(StringUtils.isEmpty(fileName)){
            logger.error("问题反馈文件名不存在, 传入文件id : [" + feedbackDocId + "]");
            fileName = UUID.randomUUID().toString().replace("-","");
        }

        try {
            String path = "";  //服务器
            File file = new File(path + File.separator + fileUrl);
            HttpHeaders headers = new HttpHeaders();
            //下载显示的文件名，解决中文名称乱码问题
            //通知浏览器以attachment（下载方式）打开图片
            headers.setContentDispositionFormData("attachment", fileName);
            //application/octet-stream ： 二进制流数据（最常见的文件下载）。
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file),
                    headers, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("问题反馈文件下载错误:" + e.getMessage());
        }
        return null;
    }

    /**
     * 按id查询问题
     * @param id
     * @return
     */
    @RequestMapping(value = "/feedBackDetail")
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    @RequestFunction(menu = MenuEnum.PROBLEM_FEED_BACK_QUERY)
    @RequiresPermissions("ProblemFeedbacks_look")
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
            logger.error("按id查询问题反馈异常:",e);
        }
        return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
    }


    /**
     * 问题反馈受理
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
    @RequiresPermissions("PROBLEM_FEED_BACK_MANAGE")
    public AjaxResponse updateFeedBack(
            @Verify(param = "feedbackId",rule = "required") Integer feedbackId,
            @Verify(param = "manageContent",rule = "required") String manageContent
            ){

        //最大不能输入500字
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
            //记录id
            feedback.setId(feedbackId);
            //更新时间
            Date currentDate = new Date();
            //更新时间
            feedback.setUpdateTime(currentDate);
            //受理时间
            feedback.setManageTime(currentDate);
            //受理人
            feedback.setManageId(userId);
            //受理状态变为已受理
            feedback.setManageStatus(FeedBackManageStatusEnum.ALREADY_ACCEPT.getCode());
            //处理结果
            feedback.setManageContent(manageContent);

            //更新意见反馈
            int result = feedBackService.updateFeedBack(feedback);

            if (result > 0){
                return AjaxResponse.success(null);
            }
        } catch (Exception e) {
            logger.error("问题反馈受理异常:", e);
        }

        return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
    }



}
