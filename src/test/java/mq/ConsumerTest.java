package mq;

import java.util.List;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.zhuanche.common.rocketmq.CommonRocketConsumer;

public class ConsumerTest {
	public static void main(String[] args ) throws Exception {
		
		CommonRocketConsumer consumer = new CommonRocketConsumer();
		consumer.setGroupName("order-statistics-test27");
		consumer.setNamesrvAddr("dev-rq01-a.mq.01zhuanche.com:9876");
		consumer.setTopic("test-20180817");
		
//		consumer.setConsumeFromWhere("CONSUME_FROM_LAST_OFFSET");
//		consumer.setConsumeFromWhere("CONSUME_FROM_FIRST_OFFSET");
//		consumer.setConsumeFromWhere("CONSUME_FROM_TIMESTAMP");
		
		consumer.setTags("tagC || tagD");
		consumer.setMessageListener(new MessageListenerConcurrently() {
			@Override
			public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
				try {
					System.out.println(  Thread.currentThread().getName() +"  :  " + msgs.size() );
					for( MessageExt msgExt : msgs) {
						System.out.println( msgExt.toString()  );
						System.out.println( msgExt.getTags()  );
					}
//					Thread.sleep(500);
				} catch (Exception e) {
				}
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		}});
		consumer.init();
		Thread.sleep(5000);
		consumer.destroy();
		
	}
}
