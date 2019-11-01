package com.zhuanche.common.listen;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerOrderly;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.google.common.collect.Maps;
import com.zhuanche.common.sms.SmsSendUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.controller.interCity.InterCityUtils;
import com.zhuanche.entity.mdbcarmanage.MainOrderInterCity;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.serv.interCity.MainOrderInterService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.Common;
import com.zhuanche.util.SignatureUtils;
import com.zhuanche.util.encrypt.MD5Utils;
import mapper.driver.ex.YueAoTongPhoneConfigExMapper;
import mapper.mdbcarmanage.ex.DriverInfoInterCityExMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 监听下单发短信
 *
 * @author admin
 */
public class MainInterCityListener implements MessageListenerOrderly {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final Integer MAIN_STATUS = 15;

    private static final Integer SERVICE_STATUS = 30;

    private static final Integer CANCEL_STATUS = 60;

    @Autowired
    private MainOrderInterService interService;

    @Value("${order.server.api.base.url}")
    private String orderServiceUrl;



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
                String mainOrderNo = jsonObject.get("mainOrderNo") == null ? null:jsonObject.getString("mainOrderNo");
                Integer serviceTypeId = jsonObject.get("serviceTypeId") == null ? null : jsonObject.getInteger("serviceTypeId");
                String driverId = jsonObject.get("driverId") == null ? "0" : jsonObject.getString("driverId");
                Integer status = jsonObject.get("status") == null ? null : jsonObject.getInteger("status");
                String firstOrderId = jsonObject.get("firstOrderId") == null ?  null : jsonObject.getString("firstOrderId");
                JSONObject jsonMemo = jsonObject.get("memo") == null ? null : jsonObject.getJSONObject("memo");
                JSONObject dispatcherPhone = jsonObject.get("dispatcherPhone") == null ? null : jsonObject.getJSONObject("dispatcherPhone");


                if(StringUtils.isNotBlank(mainOrderNo)) {
                    if (StringUtils.isNotBlank(driverId)) {
                        logger.info("===========mq监听发送短信开始===================");
                        if (serviceTypeId==68) {

                            if(MAIN_STATUS == status){
                                //防止mq后消费
                                MainOrderInterCity queryMain = interService.queryMainOrder(mainOrderNo);
                                int code = 0;
                                if(queryMain != null && queryMain.getId()>0){
                                    code = interService.updateMainOrderState(mainOrderNo,1);
                                }else {
                                    String routeName = "";
                                    if(jsonMemo != null){
                                        if(jsonMemo.get("startCityName") != null){
                                            String startCityName = jsonMemo.getString("startCityName");
                                            routeName = startCityName;
                                        }
                                        if(jsonMemo.get("endCityName") != null){
                                            String endCityName = jsonMemo.getString("endCityName");
                                            routeName = "-" +endCityName;
                                        }
                                    }




                                    String orderTime = "";
                                    Map<String,Object> map = Maps.newHashMap();
                                    List<String> strList = new ArrayList<>();
                                    map.put("bId", Common.BUSSINESSID);
                                    strList.add("bId="+Common.BUSSINESSID);
                                    map.put("orderNo",firstOrderId);
                                    strList.add("orderNo="+firstOrderId);

                                    Collections.sort(strList);
                                    strList.add("key="+Common.MAIN_ORDER_KEY);

                                    String sign = null;
                                    try {
                                        sign = MD5Utils.getMD5DigestBase64(SignatureUtils.getMD5Sign(map, Common.MAIN_ORDER_KEY));
                                    } catch (NoSuchAlgorithmException e) {
                                        e.printStackTrace();
                                    }
                                    map.put("sign",sign);
                                    logger.info("==================获取订单详情入参：" + JSONObject.toJSONString(map));
                                    JSONObject orderJSON = MpOkHttpUtil.okHttpGetBackJson(orderServiceUrl + "/orderMain/getOrderByOrderNo", map, 0, "查询订单详情");

                                    if(orderJSON != null && orderJSON.get("code") !=null) {
                                        Integer orderCode = orderJSON.getIntValue("code");
                                        if (0 == orderCode) {
                                            JSONObject jsonData =  orderJSON.getJSONObject("data");
                                            orderTime= jsonData.get("bookingDate") == null ? "" : jsonData.getString("bookingDate");
                                        }
                                    }

                                    MainOrderInterCity main = new MainOrderInterCity();
                                    main.setDriverId(Integer.valueOf(driverId));
                                    main.setCreateTime(new Date());
                                    main.setUpdateTime(new Date());
                                    main.setMainName(routeName);
                                    main.setStatus(MainOrderInterCity.orderState.NOTSETOUT.getCode());
                                    main.setMainOrderNo(mainOrderNo);
                                    main.setOpePhone(dispatcherPhone.toString());
                                    main.setMainTime(orderTime);
                                    code = interService.addMainOrderNo(main);
                                }

                                if(code > 0){
                                    logger.info("=========子单绑定主单成功=======");
                                }
                            }

                            if(SERVICE_STATUS == status || CANCEL_STATUS == status){
                             int code =   interService.updateMainOrderState(mainOrderNo,2);
                             if(code > 0){
                                 logger.info("更新主单状态成功" );
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
}
