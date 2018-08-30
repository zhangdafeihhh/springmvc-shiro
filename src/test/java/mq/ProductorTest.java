package mq;

import com.zhuanche.common.rocketmq.CommonRocketProducer;

public class ProductorTest {
	
	public static void main(String[] args ) throws Exception {
		CommonRocketProducer prod = new CommonRocketProducer();
		prod.setNamesrvAddr("dev-rq01-a.mq.01zhuanche.com:9876");
		prod.init();
		
//		for(int i=0; i<5;i++) {
//			CommonRocketProducer.publishMessage("test-20180817", "tagA", "pr"+i,  "jrd"+i  );
////			CommonRocketProducer.publishMessageOrderly("test-20180723", "yali", "pr",  "jrd"+i  );
//		}
		
//		CommonRocketProducer.publishMessage("test-20180817", "tagA", "pr",  "jrdjrd"  );
//		CommonRocketProducer.publishMessage("test-20180817", "tagB", "pr",  "jrdjrd"  );
//		CommonRocketProducer.publishMessage("test-20180817", "tagA tagB", "pr",  "jrdjrd"  );
		
		CommonRocketProducer.publishMessage("topic-name-demo1", "tagA", "keyname",  "This is a message, hello world!"  );
		
		prod.destroy();
	}

}
