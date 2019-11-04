package com.zhuanche.common.listen;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerOrderly;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.google.common.collect.Maps;
import com.zhuanche.common.sms.SmsSendUtil;
import com.zhuanche.controller.driver.YueAoTongPhoneConfig;
import com.zhuanche.controller.interCity.InterCityUtils;
import com.zhuanche.entity.mdbcarmanage.CarAdmUser;
import mapper.driver.ex.YueAoTongPhoneConfigExMapper;
import mapper.mdbcarmanage.ex.CarAdmUserExMapper;
import mapper.mdbcarmanage.ex.DriverInfoInterCityExMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private CarAdmUserExMapper exMapper;


    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        try {
            for (MessageExt msg : msgs) {
                logger.info("consumer order start...messageId:{}", msg.getMsgId());
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

                String bookingStartPoint = jsonObject.get("bookingStartPoint") == null ? null:jsonObject.getString("bookingStartPoint");

                String bookingEndPoint = jsonObject.get("bookingEndPoint") == null ? null:jsonObject.getString("bookingEndPoint");

                JSONObject jsonMemo = jsonObject.get("memo") == null ? null : jsonObject.getJSONObject("memo");

                Integer startCityId =0;
                Integer endCityId =0;

                if(jsonMemo!= null){
                     startCityId = jsonMemo.get("startCityId") == null ? 0:jsonMemo.getInteger("startCityId");
                     endCityId = jsonMemo.get("endCityId") == null ? 0:jsonMemo.getInteger("endCityId");

                }
                if(StringUtils.isNotBlank(status)) {
                    if (68 == serviceTypeId) {
                        logger.info("===========mq监听发送短信开始===================bookingStartPoint:" + bookingStartPoint);


                        InterCityUtils utils = new InterCityUtils();
                        String[] on = bookingStartPoint.split(";");

                        logger.info("=========获取坐标做的数据=====" + JSONObject.toJSONString(on));
                        if(on.length >0 && startCityId>0 && endCityId>0 ) {
                            String boardFirstAdd = on[0];
                            String[] boardPoint = boardFirstAdd.split(",");
                            if(boardPoint.length > 0){
                                String x = boardPoint[0];
                                String y = boardPoint[1];
                                String boardOn = utils.hasBoardRoutRights(startCityId, x, y);

                                if (StringUtils.isNotEmpty(boardOn)) {
                                    String[] off = bookingEndPoint.split(";");

                                    if(off.length > 0){
                                        String[] offPoint = off[0].split(",");
                                        if(offPoint.length > 0){
                                            String offX = offPoint[0];
                                            String offY = offPoint[1];
                                            String boardOff = utils.hasBoardOffRoutRights(endCityId, offX, offY);
                                            String route = utils.hasRoute(boardOn, boardOff);
                                            if (StringUtils.isNotEmpty(route)) {
                                                JSONObject jsonSupplier = JSONObject.parseObject(route);
                                                logger.info("==========路线在范围内============");
                                                if (jsonSupplier.get("supplierId") != null) {
                                                    String suppliers = jsonSupplier.getString("supplierId");
                                                    List<YueAoTongPhoneConfig> opePhone = this.queryOpePhone(suppliers);
                                                    if (CollectionUtils.isNotEmpty(opePhone)) {
                                                        //TODO:调用发短信接口
                                                        for (YueAoTongPhoneConfig config : opePhone) {

                                                            String phone = config.getPhone();
                                                            if(phone.contains(",")){
                                                                String arr[] = phone.split(",");
                                                                phone = arr[0];
                                                            }
                                                            logger.info("=====发送短信开始======" + phone);
                                                            SmsSendUtil.send(phone, "您好，有一个城际订单，请登录后台及时抢单");
                                                        }
                                                    }
                                                }
                                                /*if (jsonSupplier.get("supplierId") != null) {
                                                    String suppliers = jsonSupplier.getString("supplierId");
                                                    String[] supplierArr = suppliers.split(",");
                                                    List<Integer> mapSupplier = new ArrayList<>();

                                                    for(int i =0 ;i<supplierArr.length;i++){
                                                        mapSupplier.add(Integer.valueOf(supplierArr[i]));
                                                    }

                                                    List<CarAdmUser> listAdm = exMapper.selectUsersByLevel(4);//查询所有的供应商
                                                    for(CarAdmUser user : listAdm){
                                                        String saasSupplier = user.getSuppliers();
                                                        String[] saasArr = saasSupplier.split(",");

                                                        List<Integer> saasList = new ArrayList<>();
                                                        for(int k = 0;k<saasArr.length;k++){
                                                            if(StringUtils.isNotEmpty(saasArr[k])){
                                                                saasList.add(Integer.valueOf(saasArr[k]));
                                                            }
                                                        }
                                                        if(saasList.retainAll(mapSupplier)){
                                                            String phone = user.getPhone();
                                                            logger.info("=====发送短信开始======");
                                                            SmsSendUtil.send(phone, "您好，有一个跨城订单，请登录后台及时抢单");
                                                        }
                                                    }

                                                }*/
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
