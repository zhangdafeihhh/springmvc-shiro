package com.zhuanche.common.listen;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerOrderly;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.zhuanche.common.sms.SmsSendUtil;
import com.zhuanche.controller.driver.YueAoTongPhoneConfig;
import com.zhuanche.controller.interCity.InterCityUtils;
import mapper.driver.ex.YueAoTongPhoneConfigExMapper;
import mapper.mdbcarmanage.ex.DriverInfoInterCityExMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 监听下单发短信
 *
 * @author admin
 */
public class NewInterCityListener implements MessageListenerOrderly {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String STATUS = "13";


    @Autowired
    private DriverInfoInterCityExMapper infoInterCityExMapper;

    @Autowired
    private YueAoTongPhoneConfigExMapper yueAoTongPhoneConfigExMapper;


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

                Integer serviceTypeId = jsonObject.get("serviceTypeId") == null ? 0 : jsonObject.getInteger("serviceTypeId");
                Integer startCityId = jsonObject.get("startCityId") == null ? 0:jsonObject.getInteger("startCityId");
                Integer endCityId = jsonObject.get("endCityId") == null ? 0:jsonObject.getInteger("endCityId");

                String bookingStartPoint = jsonObject.get("bookingStartPoint") == null ? null:jsonObject.getString("bookingStartPoint");

                String bookingEndPoint = jsonObject.get("bookingEndPoint") == null ? null:jsonObject.getString("bookingEndPoint");


                if(StringUtils.isNotBlank(status)) {
                    if (68 == serviceTypeId) {
                        logger.info("===========mq监听发送短信开始===================");


                        InterCityUtils utils = new InterCityUtils();
                        String[] on = bookingStartPoint.split(",");
                        logger.info("=========获取坐标做的数据=====" + JSONObject.toJSONString(on));
                        if(on.length >0) {
                            String x = on[0];
                            String y = on[1];
                            String boardOn = utils.hasBoardRoutRights(startCityId, x, y);

                            if (StringUtils.isNotEmpty(boardOn)) {
                                String[] off = bookingEndPoint.split(",");

                                if(off.length > 0){
                                    String offX = off[0];
                                    String offY = off[1];
                                    String boardOff = utils.hasBoardOffRoutRights(endCityId, offX, offY);
                                    String route = utils.hasRoute(boardOn, boardOff);
                                    if (StringUtils.isNotEmpty(route)) {
                                        JSONObject jsonSupplier = JSONObject.parseObject(route);
                                        logger.info("==========路线不在范围内============");
                                        if (jsonSupplier.get("supplierId") != null) {
                                            String suppliers = jsonObject.getString("supplierId");
                                            List<YueAoTongPhoneConfig> opePhone = this.queryOpePhone(suppliers);
                                            if (CollectionUtils.isNotEmpty(opePhone)) {
                                                //TODO:调用发短信接口
                                                for (YueAoTongPhoneConfig config : opePhone) {
                                                    String phone = config.getPhone();
                                                    logger.info("=====发送短信开始======");
                                                    SmsSendUtil.send(phone, "您好，有一个跨城订单，请登录后台及时抢单");
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        }
                        logger.info("===========mq发送短信结束==============");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("NewInterCityListener exception:", e);
            return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
        }
        return ConsumeOrderlyStatus.SUCCESS;
    }


    /**
     * 根据供应商id获取手机号
     *
     * @param suppliers
     * @return
     */
    private List<YueAoTongPhoneConfig>  queryOpePhone(String suppliers ) {
            List<YueAoTongPhoneConfig> list = yueAoTongPhoneConfigExMapper.findBySupplierId(suppliers);
           return list;
    }
}
