package com.zhuanche.common.rocketmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.MessageListener;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;

/**通用的RocketMQ消费者
 * @author zhaoyali
 **/
public class CommonRocketConsumer{
	private static final Logger logger = LoggerFactory.getLogger(CommonRocketConsumer.class);
	private DefaultMQPushConsumer consumer; //消费者
	
	private String groupName;                  //消费组名称（注意：相同的组名在同一个JVM中只能配置为一个）
	private String namesrvAddr;                //RocketMQ命名服务地址
	private String consumeFromWhere;      //消费起点
	private String messageModel;             //消费模式
	private String topic;                            //RocketMQ TOPIC
	private String tags;                             //RocketMQ TAG
	private int messageBatchMaxSize = 1; //每次消费消息的最大数量
	private int threads =1;                        //并发消费线程数
	private MessageListener messageListener;  //业务处理实现类
	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public void setNamesrvAddr(String namesrvAddr) {
		this.namesrvAddr = namesrvAddr;
	}
	public void setConsumeFromWhere(String consumeFromWhere) {
		this.consumeFromWhere = consumeFromWhere;
	}
	public void setMessageModel(String messageModel) {
		this.messageModel = messageModel;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public void setMessageBatchMaxSize(int messageBatchMaxSize) {
		this.messageBatchMaxSize = messageBatchMaxSize;
	}
	public void setThreads(int threads) {
		this.threads = threads;
	}
	public void setMessageListener(MessageListener messageListener) {
		this.messageListener = messageListener;
	}
	
	/**初始化消费者**/
	public void init() throws MQClientException{
		this.startConsumer(this.groupName, this.namesrvAddr, this.consumeFromWhere, this.messageModel,this.topic, this.tags, this.messageBatchMaxSize, this.threads, this.messageListener );
	}
	private synchronized void startConsumer(String groupName, String namesrvAddr,String consumeFromWhere,String messageModel,String topic, String tags, int messageBatchMaxSize, int threads,MessageListener messageListener) throws MQClientException{
		if(topic==null) {
			logger.error("param [topic]  is required! ");
			return;
		}
		if(messageListener==null) {
			logger.error("param [messageListener]  is required! ");
			return;
		}
		//init
		consumer = new DefaultMQPushConsumer(groupName);
		consumer.setNamesrvAddr(namesrvAddr);
		if(consumeFromWhere==null) {
			consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
		}else if("CONSUME_FROM_LAST_OFFSET".equalsIgnoreCase(consumeFromWhere)) {
			consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
		}else if("CONSUME_FROM_FIRST_OFFSET".equalsIgnoreCase(consumeFromWhere)) {
			consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
		}else if("CONSUME_FROM_TIMESTAMP".equalsIgnoreCase(consumeFromWhere)) {
			consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_TIMESTAMP);
		}
		if(messageModel==null) {
			consumer.setMessageModel(MessageModel.CLUSTERING);
		}else if("CLUSTERING".equalsIgnoreCase(messageModel)) {
			consumer.setMessageModel(MessageModel.CLUSTERING);
		}else if("BROADCASTING".equalsIgnoreCase(messageModel)) {
			consumer.setMessageModel(MessageModel.BROADCASTING);
		}
		if(tags==null || "".equalsIgnoreCase(tags.trim())) {
			consumer.subscribe(topic, "*");
		}else {
			consumer.subscribe(topic, tags.trim() );
		}
		consumer.setConsumeMessageBatchMaxSize(messageBatchMaxSize);  
		consumer.setConsumeThreadMin(threads);
		consumer.setMessageListener(messageListener);
		/**todo 注意要设置不同的实例名字，如果不设置 会出现消费组都跑到第一个实例上面的情况*/
		consumer.setInstanceName(System.currentTimeMillis()+"**");
		consumer.start();
		logger.info(">>>>>>>>>>>通用的RocketMQ消费者初始化成功！["+ this.topic +" : " +this.tags +"]" );
	}
	/**销毁消费者**/
	public void destroy() {
		this.stopConsumer();
	}
	private synchronized void stopConsumer() {
		consumer.shutdown();
		logger.info(">>>>>>>>>>>通用的RocketMQ消费者销毁成功！");
	}
}