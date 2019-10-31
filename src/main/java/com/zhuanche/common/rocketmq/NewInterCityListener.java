package com.zhuanche.common.rocketmq;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerOrderly;
import com.alibaba.rocketmq.common.message.MessageExt;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 监听下单发短信
 *
 * @author admin
 */
public class NewInterCityListener implements MessageListenerOrderly {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String STATUS = "13";

    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        try {
            for (MessageExt msg : msgs) {
                logger.info("consumer order start...messageId:{}; body:{}", msg.getMsgId(), msg.getBody());
                String topic = msg.getTopic();
                if(StringUtils.isBlank(topic)){
                    logger.info("topic is null");
                    continue;
                }
                String content = new String(msg.getBody());
                if(StringUtils.isBlank(content)){
                    logger.info("content is null");
                    continue;
                }
                JSONObject jsonObject = JSONObject.parseObject(content);
                String status = jsonObject.get("status") == null ? null:jsonObject.getString("status");
                String orderNo = jsonObject.get("orderNo") == null ? null : jsonObject.getString("orderNo");
                String driverId = jsonObject.get("driverId") == null ? null : jsonObject.getString("driverId");
                if(StringUtils.isNotBlank(status)) {
                    if (StringUtils.isNotBlank(driverId)) {
                        if (STATUS.equals(status)) {
                            //TODO:调用鸿涛写的发短信接口
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
