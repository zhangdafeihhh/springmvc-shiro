package com.zhuanche.serv.feedback;

import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.entity.mdbcarmanage.FeedbackDoc;
import mapper.mdbcarmanage.FeedbackDocMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedBackDocService {

    @Autowired
    FeedbackDocMapper feedbackDocMapper;

    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public FeedbackDoc selectFeedBackDocById(Integer docId){
        return feedbackDocMapper.selectByPrimaryKey(docId);
    }

}
