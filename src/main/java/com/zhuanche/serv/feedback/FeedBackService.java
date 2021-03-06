package com.zhuanche.serv.feedback;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.controller.feedback.FeedBackController;
import com.zhuanche.dto.mdbcarmanage.FeedBackDetailDto;
import com.zhuanche.entity.mdbcarmanage.Feedback;
import com.zhuanche.entity.mdbcarmanage.FeedbackDoc;
import com.zhuanche.exception.MessageException;
import mapper.mdbcarmanage.FeedbackDocMapper;
import mapper.mdbcarmanage.FeedbackMapper;
import mapper.mdbcarmanage.ex.FeedbackDocExMapper;
import mapper.mdbcarmanage.ex.FeedbackExMapper;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

@Service
public class FeedBackService {

    private static final Logger logger = LoggerFactory.getLogger(FeedBackService.class);


    @Autowired
    FeedbackExMapper feedbackExMapper;

    @Autowired
    FeedbackMapper feedbackMapper;

    @Autowired
    FeedbackDocExMapper feedbackDocExMapper;

    @Autowired
    FeedbackDocMapper feedbackDocMapper;

    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public List<Feedback> findDataList(String createTimeStart, String createTimeEnd, Integer manageStatus,Integer feedbackType) {
        return feedbackExMapper.findDataList(createTimeStart, createTimeEnd, manageStatus,feedbackType);
    }

    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    public int updateFeedBack(Feedback feedback) {
        return feedbackMapper.updateByPrimaryKeySelective(feedback);
    }

    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public FeedBackDetailDto queryFeedBackDetailByFeedBackId(Integer id) {
        Feedback feedback = feedbackMapper.selectByPrimaryKey(id);
        if (null == feedback) {
            return null;
        }
        List<FeedbackDoc> feedbackDocs = feedbackDocExMapper.selectFeedBackDocListByFeedBackId(id);
        if (null == feedbackDocs) {
            feedbackDocs = Lists.newArrayList();
        }
        return FeedBackDetailDto.build(feedback, feedbackDocs);
    }

    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    @Transactional(rollbackFor = Exception.class)
    public void addFeedBack(Feedback feedback, MultipartFile[] multipartFiles) throws Exception{

        int feedbackInsertResult = feedbackMapper.insertSelective(feedback);

        Integer feedbackId = feedback.getId();
        if (null == feedbackId || !(feedbackInsertResult > 0)){
            throw new RuntimeException("????????????????????????,????????????????????????id");
        }

        //????????????
        if (ArrayUtils.isEmpty(multipartFiles)){
            return;
        }

        //????????????
        for (MultipartFile multipartFile : multipartFiles) {
            if (null == multipartFile || multipartFile.isEmpty()) {
                continue;
            }
            Map<String, Object> uploadMap = fileUpload(multipartFile);
            if (null == uploadMap){
                throw new RuntimeException("????????????");
            }
            FeedbackDoc feedbackDoc = new FeedbackDoc();

            //??????
            Date currentDate = new Date();
            feedbackDoc.setCreateTime(currentDate);
            feedbackDoc.setUpdateTime(currentDate);
            //?????????
            feedbackDoc.setDocName(MapUtils.getString(uploadMap, "fileName",""));
            //????????????
            feedbackDoc.setDocUrl(MapUtils.getString(uploadMap,"fileUrl",""));
            //????????????id
            feedbackDoc.setFeedbackId(feedbackId);
            int i = feedbackDocMapper.insertSelective(feedbackDoc);
            if(!( i > 0 )){
                throw new RuntimeException("??????????????????????????????");
            }
        }
    }

    private Map<String, Object> fileUpload(MultipartFile file) {
        Map<String, Object> map = Maps.newHashMap();

        if (null == file || file.isEmpty()) {
            return null;
        }

        try {
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            String uuid = UUID.randomUUID().toString();
            String timeStamp = System.currentTimeMillis() + "";
            // ??????????????????????????????
            String rootPath = this.getRemoteFileDir();
            File filePath = new File(rootPath);

            logger.info("????????????????????????:" + rootPath);
            if (!filePath.exists())
                filePath.mkdirs();
            // ?????????????????????
            String absoluteUrl = rootPath + uuid + "_" + timeStamp + "." + extension;
            File serverFile = new File(absoluteUrl);
            file.transferTo(serverFile);
            logger.info("?????????????????????????????????" + absoluteUrl);
            map.put("ok", true);
            map.put("fileUrl", absoluteUrl);
            map.put("fileName", file.getOriginalFilename());
        } catch (Exception e) {
            logger.error("??????????????????????????????,e");
            return null;
        }
        return map;
    }

    /**
     * ?????????????????????????????????
     * @param createTimeStart
     * @param createTimeEnd
     * @param manageStatus
     * @param userId
     * @return
     */
    public List<Feedback> findDataListSelf(String createTimeStart, String createTimeEnd, Integer manageStatus, Integer userId,Integer feedbackType) {
        return feedbackExMapper.findDataListSelf(createTimeStart, createTimeEnd, manageStatus, userId, feedbackType);
    }

    private String getRemoteFileDir() {
        Calendar now = Calendar.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append(File.separator)
                .append("u01")
                .append(File.separator)
                .append("upload")
                .append(File.separator)
                .append("message")
                .append(File.separator)
                .append(now.get(Calendar.YEAR))
                .append(File.separator)
                //now.get(Calendar.MONTH)????????????????????????????????????
                .append(now.get(Calendar.MONTH)+1)
                .append(File.separator);
        return sb.toString();
    }
}
