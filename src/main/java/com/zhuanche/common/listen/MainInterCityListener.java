package com.zhuanche.common.listen;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerOrderly;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.google.common.collect.Maps;
import com.zhuanche.controller.driver.YueAoTongPhoneConfig;
import com.zhuanche.entity.mdbcarmanage.DriverInfoInterCity;
import com.zhuanche.entity.mdbcarmanage.MainOrderInterCity;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.serv.interCity.MainOrderInterService;
import com.zhuanche.util.Common;
import com.zhuanche.util.SignatureUtils;
import com.zhuanche.util.encrypt.MD5Utils;
import mapper.driver.ex.YueAoTongPhoneConfigExMapper;
import mapper.mdbcarmanage.ex.DriverInfoInterCityExMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 监听主单状态更新
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


    @Autowired
    private DriverInfoInterCityExMapper driverInfoInterCityExMapper;

    @Value("${order.server.api.base.url}")
    private String orderServiceUrl;

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
                String mainOrderNo = jsonObject.get("mainOrderNo") == null ? null:jsonObject.getString("mainOrderNo");
                Integer serviceTypeId = jsonObject.get("serviceTypeId") == null ? null : jsonObject.getInteger("serviceTypeId");
                String driverId = jsonObject.get("driverId") == null ? "0" : jsonObject.getString("driverId");
                Integer status = jsonObject.get("status") == null ? null : jsonObject.getInteger("status");
                String firstOrderId = jsonObject.get("firstOrderId") == null ?  null : jsonObject.getString("firstOrderId");
                JSONObject jsonMemo = jsonObject.get("memo") == null ? null : jsonObject.getJSONObject("memo");

                JSONObject dispatcherPhone = null;
                /*if(jsonMemo != null){
                    dispatcherPhone = jsonMemo.get("dispatcherPhone") == null ? null : jsonMemo.getJSONObject("dispatcherPhone");

                }*/


                if(StringUtils.isNotBlank(mainOrderNo) ) {
                    if (StringUtils.isNotBlank(driverId) && serviceTypeId != null) {
                        logger.info("===========mq监听修改司机开始===================");
                        if (serviceTypeId==68) {

                            if(MAIN_STATUS == status){
                                //防止mq后消费
                                MainOrderInterCity queryMain = interService.queryMainOrder(mainOrderNo);
                                int code = 0;
                                if(queryMain != null && queryMain.getId()>0){
                                    code = interService.updateMainOrderState(mainOrderNo,1,null);
                                }else {
                                    String routeName = "";
                                    String  orderTime = "";
                                    if(jsonMemo != null){
                                        if(jsonMemo.get("routeName") != null){
                                            routeName = jsonMemo.getString("routeName");

                                            orderTime = jsonMemo.get("crossCityStartTime") == null ? "" : jsonMemo.getString("crossCityStartTime");
                                        }


                                        if(StringUtils.isEmpty(routeName)){
                                            String startName = "";
                                            if(jsonMemo.get("startCityName") != null){
                                                startName = jsonMemo.getString("startCityName");
                                            }
                                            String endName = "";
                                            if(jsonMemo.get("endCityName") != null){
                                                 endName = jsonMemo.getString("endCityName");
                                            }
                                            routeName = startName + "-" + endName;
                                        }

                                    }

                                    if(StringUtils.isEmpty(orderTime)){//获取线路时间
                                        Map<String,Object> map = Maps.newHashMap();
                                        List<String> strList = new ArrayList<>();
                                        map.put("bId", Common.BUSSINESSID);
                                        strList.add("bId="+Common.BUSSINESSID);
                                        map.put("orderId",firstOrderId);
                                        strList.add("orderId="+firstOrderId);
                                        map.put("columns","booking_date");
                                        strList.add("columns=booking_date");


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
                                        JSONObject orderJSON = MpOkHttpUtil.okHttpGetBackJson(orderServiceUrl + "/orderMain/getOrdersByOrderNo", map, 0, "查询订单详情");
                                        logger.info("===========获取订单返回数据====" + orderJSON.toString());
                                        if(orderJSON != null && orderJSON.get("code") !=null) {
                                            int orderCode = orderJSON.getIntValue("code");
                                            if (0 == orderCode) {
                                                JSONArray jsonArray =  orderJSON.getJSONArray("data");
                                                if(jsonArray != null && jsonArray.size() > 0){
                                                    JSONObject jsonData = (JSONObject) jsonArray.get(0);
                                                    orderTime= jsonData.get("bookingDate") == null ? "" : jsonData.getString("bookingDate");
                                                    if(orderTime!= null){
                                                        orderTime = DateUtils.format(Long.valueOf(orderTime),"yyyy-MM-dd HH:mm:ss");
                                                        logger.info("获取订单时间orderTime:" + orderTime);
                                                    }
                                                }

                                            }
                                        }
                                    }


                                    String opePhone = null;
                                    if(dispatcherPhone == null){
                                        //根据司机id获取供应商id
                                     DriverInfoInterCity city =  driverInfoInterCityExMapper.getByDriverId(Integer.valueOf(driverId));
                                     if(city != null && city.getSupplierId()>0){
                                         YueAoTongPhoneConfig config = this.queryOpePhone(city.getSupplierId().toString());
                                         if(config == null || StringUtils.isEmpty(config.getPhone())){
                                             opePhone = city.getDriverPhone();//如果车管配置的手机为空，则留当前司机的手机号
                                         }else {
                                             opePhone = config.getPhone();
                                         }
                                     }
                                    }

                                    MainOrderInterCity main = new MainOrderInterCity();
                                    main.setDriverId(Integer.valueOf(driverId));
                                    main.setCreateTime(new Date());
                                    main.setUpdateTime(new Date());
                                    main.setMainName(routeName);
                                    main.setStatus(MainOrderInterCity.orderState.NOTSETOUT.getCode());
                                    main.setMainOrderNo(mainOrderNo);
                                    main.setOpePhone(dispatcherPhone == null ?opePhone : dispatcherPhone.toString());
                                    main.setMainTime(orderTime);
                                    code = interService.addMainOrderNo(main);
                                }

                                if(code > 0){
                                    logger.info("=========子单绑定主单成功=======");
                                }
                            }

                            if(SERVICE_STATUS == status || CANCEL_STATUS == status){
                             int code =   interService.updateMainOrderState(mainOrderNo,2,null);
                             if(code > 0){
                                 logger.info("更新主单状态成功" );
                             }
                            }
                        }
                        logger.info("===========mq更新主单状态结束==============");
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
    private YueAoTongPhoneConfig queryOpePhone(String suppliers ) {
        List<YueAoTongPhoneConfig> list = yueAoTongPhoneConfigExMapper.findBySupplierId(suppliers);
        if(CollectionUtils.isNotEmpty(list)){
            return list.get(0);
        }
        return null;
    }

}
