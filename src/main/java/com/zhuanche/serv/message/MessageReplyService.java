package com.zhuanche.serv.message;

import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.entity.mdbcarmanage.CarMessageReply;
import mapper.mdbcarmanage.CarMessageReplyMapper;
import mapper.mdbcarmanage.ex.CarMessageReplyExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageReplyService {

    @Autowired
    CarMessageReplyMapper carMessageReplyMapper;

    @Autowired
    CarMessageReplyExMapper carMessageReplyExMapper;

    /**
     * 回复消息
     * @param carMessageReply
     * @return
     */
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    public Integer addReply(CarMessageReply carMessageReply){
        return carMessageReplyMapper.insertSelective(carMessageReply);
    }

    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public List<CarMessageReply> findReplyListByMessageIdPage(Long messageId) {
        return carMessageReplyExMapper.findReplyListByMessageIdPage(messageId);
    }

    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public List<CarMessageReply> findReplyListByMessageIdAndSenderIdPage(Long messageId,Integer userId) {
        return carMessageReplyExMapper.findReplyListByMessageIdAndSenderIdPage(messageId, userId);
    }
}
