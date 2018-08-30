package com.zhuanche.common.rocketmq;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.MessageQueueSelector;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageQueue;

/**通用的RocketMQ生产者
 * @author zhaoyali
 **/
public class CommonRocketProducer{
	private static final Logger logger = LoggerFactory.getLogger(CommonRocketProducer.class);
	private static DefaultMQProducer producer; //静态的生产者
	
	private String namesrvAddr;//RocketMQ nameserverAddr
	public void setNamesrvAddr(String namesrvAddr) {
		this.namesrvAddr = namesrvAddr;
	}
	
	/**初始化生产者**/
	public void init() throws MQClientException{
		CommonRocketProducer.startProducer( this.namesrvAddr );
	}
	private synchronized static void startProducer(String namesrvAddr) throws MQClientException {
		producer = new DefaultMQProducer("mp-manage");
		producer.setNamesrvAddr(namesrvAddr);
		producer.setRetryAnotherBrokerWhenNotStoreOK(true);//消息没有存储成功是否发送到另外一个broker
		producer.setRetryTimesWhenSendFailed(4); //定义重试次数,默认是2
		producer.setSendMsgTimeout(6000); //定义超时时间,默认是3000
		producer.start();
		logger.info(">>>>>>>>>>>通用的RocketMQ生产者初始化成功！");
	}
	/**销毁生产者**/
	public void destroy() {
		CommonRocketProducer.stopProducer();
	}
	private synchronized static void stopProducer() {
		producer.shutdown();
		logger.info(">>>>>>>>>>>通用的RocketMQ生产者销毁成功！");
	}
	
	
	//-------------------------------------------------------------------------------------------------------------以下为通用的发送方法
	/**发送普通消息**/
	public static boolean publishMessage(String topic, String tags, String keys, Object message){
		if(topic==null) {
			return false;
		}
		if(tags==null) {
			tags = "default";
		}
		if(keys==null) {
			keys = "default";
		}
		if(message==null) {
			return false;
		}
		String msg = JSON.toJSONString(message);
		try{
			logger.info("[普通MQ: topic:"+topic+",tags:"+tags+",keys:"+keys+"]: " + msg );
			Message rocketMsg = new Message( topic, tags, keys,  msg.getBytes("UTF-8") );
			SendResult sendResult = producer.send(rocketMsg);

			if (sendResult == null || sendResult.getSendStatus() != SendStatus.SEND_OK) {
				logger.error("[普通MQ: topic:"+topic+",tags:"+tags+",keys:"+keys+"]: Send failed!" );
				return false;
			}
			logger.info("[普通MQ: topic:"+topic+",tags:"+tags+",keys:"+keys+"]: Send successful!" );
			return true;
		} catch (Exception e) {
			logger.error("[普通MQ: topic:"+topic+",tags:"+tags+",keys:"+keys+"]: 发送普通消息异常!",e );
			return false;
		}
	}

	/**
	 * 发送顺序消息
	 */
	public static boolean publishMessageOrderly(String topic, String tags, String keys, Object message){
		if(topic==null) {
			return false;
		}
		if(tags==null) {
			tags = "default";
		}
		if(keys==null) {
			keys = "default";
		}
		if(message==null) {
			return false;
		}
		String msg = JSON.toJSONString(message);
		try{
			logger.info("[顺序MQ: topic:"+topic+",tags:"+tags+",keys:"+keys+"]: " + msg );
			Message rocketMsg = new Message( topic, tags, keys,  msg.getBytes("UTF-8") );
			SendResult sendResult = producer.send(rocketMsg, new MessageQueueSelector() {
				@Override
				public MessageQueue select(List<MessageQueue> messageQueues, Message msg, Object obj) {
					if(obj==null) {
						return messageQueues.get(0);
					}
					int hashCode = Math.abs(obj.hashCode());
					int index = hashCode % messageQueues.size();
					return messageQueues.get(index);
				}
			}, keys);

			if (sendResult == null || sendResult.getSendStatus() != SendStatus.SEND_OK) {
				logger.error("[顺序MQ: topic:"+topic+",tags:"+tags+",keys:"+keys+"]: Send failed!" );
				return false;
			}
			logger.info("[顺序MQ: topic:"+topic+",tags:"+tags+",keys:"+keys+"]: Send successful!" );
			return true;
		}catch (Exception e) {
			logger.error("[顺序MQ: topic:"+topic+",tags:"+tags+",keys:"+keys+"]: 发送顺序消息异常!",e );
			return false;
		}
	}
	
}