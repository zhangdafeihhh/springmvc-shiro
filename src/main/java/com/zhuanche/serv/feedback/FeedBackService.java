package com.zhuanche.serv.feedback;
import com.google.common.collect.Lists;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.dto.mdbcarmanage.FeedBackDetailDto;
import com.zhuanche.entity.mdbcarmanage.Feedback;
import com.zhuanche.entity.mdbcarmanage.FeedbackDoc;
import com.zhuanche.util.FileUploadUtils;
import mapper.mdbcarmanage.FeedbackDocMapper;
import mapper.mdbcarmanage.FeedbackMapper;
import mapper.mdbcarmanage.ex.FeedbackDocExMapper;
import mapper.mdbcarmanage.ex.FeedbackExMapper;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
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
    public List<Feedback> findDataList(String createTimeStart, String createTimeEnd, Integer manageStatus) {
        return feedbackExMapper.findDataList(createTimeStart, createTimeEnd, manageStatus);
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
            throw new RuntimeException("问题反馈插入异常,插入后未反回记录id");
        }

        //没有附件
        if (ArrayUtils.isEmpty(multipartFiles)){
            return;
        }

        //上传附件
        for (MultipartFile multipartFile : multipartFiles) {
            if (null == multipartFile || multipartFile.isEmpty()) {
                continue;
            }
            Map<String, Object> uploadMap = FileUploadUtils.fileUpload(multipartFile);
            if (null == uploadMap){
                throw new RuntimeException("问题反馈附件为空");
            }
            FeedbackDoc feedbackDoc = new FeedbackDoc();

            //时间
            Date currentDate = new Date();
            feedbackDoc.setCreateTime(currentDate);
            feedbackDoc.setUpdateTime(currentDate);
            //文件名
            feedbackDoc.setDocName(MapUtils.getString(uploadMap, "fileName",""));
            //文件地址
            feedbackDoc.setDocUrl(MapUtils.getString(uploadMap,"fileUrl",""));
            //问题反馈id
            feedbackDoc.setFeedbackId(feedbackId);
            int i = feedbackDocMapper.insertSelective(feedbackDoc);
            if(!( i > 0 )){

                throw new RuntimeException("问题反馈文件上传异常");
            }
        }
    }




}
