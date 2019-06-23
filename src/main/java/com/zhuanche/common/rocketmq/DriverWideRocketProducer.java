package com.zhuanche.common.rocketmq;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.MessageQueueSelector;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.alibaba.rocketmq.remoting.common.RemotingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author (yangbo)
 * @Date: 2019/6/19 14:15
 * @Description:(司机宽表发送MQ)
 */
public class DriverWideRocketProducer {

    private static final Logger logger = LoggerFactory.getLogger(DriverWideRocketProducer.class);

    private static DefaultMQProducer producer;

    /***
     * 发送MQ地址
     */
    private String namesrvAddr;

    private static final String PRODUCERGROUP = "mp-driver-wide-info";

    /***
     * 发送MQ消息的topic
     */
    public static final String TOPIC = "mp_driver_info";

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    /**
     * 初始化生产者
     **/
    public void init() throws MQClientException {
        DriverWideRocketProducer.startProducer(this.namesrvAddr);
    }

    private synchronized static void startProducer(String namesrvAddr) throws MQClientException {
        producer = new DefaultMQProducer(PRODUCERGROUP);
        producer.setInstanceName(RemotingUtil.getLocalAddress() + "@" + System.nanoTime());
        producer.setNamesrvAddr(namesrvAddr);
        //消息没有存储成功是否发送到另外一个broker
        producer.setRetryAnotherBrokerWhenNotStoreOK(true);
        //定义重试次数,默认是2
        producer.setRetryTimesWhenSendFailed(5);
        //定义超时时间,默认是3000
        producer.setSendMsgTimeout(5000);
        producer.start();
        logger.info(">>>>>>>>司机宽表RocketMQ生产者初始化成功<<<<<<<<<<<");
    }

    /**
     * 销毁生产者
     **/
    public void destroy() {
        DriverWideRocketProducer.stopProducer();
    }

    private synchronized static void stopProducer() {
        producer.shutdown();
        logger.info(">>>>>>>>>>>>司机宽表RocketMQ生产者销毁成功<<<<<<<<<<<");
    }

    /**
     * 发送普通消息
     **/
    public static boolean publishMessage(String topic, String tags, String keys, Object message) {
        if (topic == null) {
            return false;
        }
        if (tags == null) {
            tags = "default";
        }
        if (keys == null) {
            keys = "default";
        }
        if (message == null) {
            return false;
        }
        String msg = JSON.toJSONString(message, SerializerFeature.WriteMapNullValue);
        try {
            logger.info("[司机宽表发送普通MQ,topic:{},tags:{},keys:{},msg:{}]", topic, tags, keys, msg);
            Message rocketMsg = new Message(topic, tags, keys, msg.getBytes("UTF-8"));
            SendResult sendResult = producer.send(rocketMsg);

            if (sendResult == null || sendResult.getSendStatus() != SendStatus.SEND_OK) {
                logger.error("[司机宽表发送普通MQ,topic:{},tags:{},keys:{},msg:{},Send failed]", topic, tags, keys, msg);
                return false;
            }
            logger.info("[司机宽表普通MQ状态,sendRestult:{}]", sendResult.getSendStatus());
            return true;
        } catch (Exception e) {
            logger.error("[司机宽表发送普通MQ,topic:{},tags:{},keys:{},msg:{},发送异常:{}]", topic, tags, keys, msg, e);
            return false;
        }
    }

    /**
     * 发送顺序消息
     */
    public static boolean publishMessageOrderly(String topic, String tags, String keys, Object message) {
        if (topic == null) {
            return false;
        }
        if (tags == null) {
            tags = "default";
        }
        if (keys == null) {
            keys = "default";
        }
        if (message == null) {
            return false;
        }
        String msg = JSON.toJSONString(message, SerializerFeature.WriteMapNullValue);
        try {
            logger.info("[司机宽表发送顺序MQ,topic:{},tags:{},keys:{},msg:{}]", topic, tags, keys, msg);
            Message rocketMsg = new Message(topic, tags, keys, msg.getBytes("UTF-8"));
            SendResult sendResult = producer.send(rocketMsg, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> messageQueues, Message msg, Object obj) {
                    if (obj == null) {
                        return messageQueues.get(0);
                    }
                    int hashCode = Math.abs(obj.hashCode());
                    int index = hashCode % messageQueues.size();
                    return messageQueues.get(index);
                }
            }, keys);

            if (sendResult == null || sendResult.getSendStatus() != SendStatus.SEND_OK) {
                logger.error("[司机宽表发送顺序MQ,topic:{},tags:{},keys:{},msg:{},Send failed]", topic, tags, keys, msg);
                return false;
            }
            logger.info("[司机宽表顺序MQ状态,sendRestult:{}]", sendResult.getSendStatus());
            return true;
        } catch (Exception e) {
            logger.error("[司机宽表发送顺序MQ,topic:{},tags:{},keys:{},msg:{},发送异常:{}]", topic, tags, keys, msg, e);
            return false;
        }
    }
}
