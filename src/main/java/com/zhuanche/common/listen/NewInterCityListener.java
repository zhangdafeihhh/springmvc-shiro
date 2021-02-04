package com.zhuanche.common.listen;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerOrderly;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.zhuanche.common.sms.SmsSendUtil;
import com.zhuanche.constants.SmsTempleConstants;
import com.zhuanche.controller.driver.YueAoTongPhoneConfig;
import com.zhuanche.controller.intercity.InterCityUtils;
import com.zhuanche.entity.mdbcarmanage.CarAdmUser;
import com.zhuanche.entity.mdbcarmanage.InterDriverLineRel;
import com.zhuanche.serv.supplier.SupplierRecordService;
import mapper.driver.ex.YueAoTongPhoneConfigExMapper;
import mapper.mdbcarmanage.ex.CarAdmUserExMapper;
import mapper.mdbcarmanage.ex.InterDriverLineRelExMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.text.html.Option;
import java.util.*;

/**
 * 监听下单发短信
 *
 * @author admin
 */
public class NewInterCityListener implements MessageListenerOrderly {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private YueAoTongPhoneConfigExMapper yueAoTongPhoneConfigExMapper;

    @Autowired
    private SupplierRecordService recordService;

    @Autowired
    private InterDriverLineRelExMapper lineRelExMapper;

    @Autowired
    private CarAdmUserExMapper admUserExMapper;

    private static final String LITTLE_PROGRAMME = "13";

    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        try {
            for (MessageExt msg : msgs) {
                logger.info("NewInterCityListener consumer order start...messageId:{}", msg.getMsgId());
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

                String orderType = jsonObject.get("orderType") ==  null ? null : jsonObject.getString("orderType");

                Integer startCityId =0;
                Integer endCityId =0;
                Integer ruleId = null;

                if(jsonMemo!= null){
                     startCityId = jsonMemo.get("startCityId") == null ? 0:jsonMemo.getInteger("startCityId");
                     endCityId = jsonMemo.get("endCityId") == null ? 0:jsonMemo.getInteger("endCityId");
                    ruleId = jsonMemo.get("ruleId") == null ? null : jsonMemo.getInteger("ruleId");
                }
                Optional<String> optional = Optional.ofNullable(status);
                Integer finalRuleId = ruleId;
                Integer finalStartCityId = startCityId;
                Integer finalEndCityId = endCityId;
                optional.ifPresent(n->{
                    if (68 == serviceTypeId) {
                        logger.info("===========mq监听发送短信开始===================bookingStartPoint:" + bookingStartPoint);
                        boolean bl = false;
                        if(StringUtils.isNotEmpty(orderType) &&  orderType.equals(LITTLE_PROGRAMME)){
                            logger.info("========小程序乘客下单============发送短信start======");
                            bl = smallProSendMessage(finalRuleId);
                            logger.info("========小程序乘客下单============发送短信end======");
                        }
                        if(!bl){
                            String[] on = bookingStartPoint.split(";");
                            logger.info("=========获取坐标做的数据=====" + JSONObject.toJSONString(on));
                            otherSendMessage(on, finalStartCityId, finalEndCityId, finalRuleId,bookingEndPoint);
                            logger.info("===========mq发送短信结束==============");
                        }
                    }
                });
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
        return yueAoTongPhoneConfigExMapper.findBySupplierId(suppliers);
    }

    /**
     * 根据供应商id获取手机号
     *
     * @param suppliers
     * @return
     */
    private List<String>  querySupplierPhone(String suppliers ) {
        return recordService.listSupplierExtDto(suppliers);
    }


    /**
     * 小程序发送短信
     * @param ruleId
     */
    private boolean smallProSendMessage(Integer ruleId){
        boolean bl = false;
        try {
            if(ruleId != null){
                List<InterDriverLineRel> driverLineLists = lineRelExMapper.queryDriversByLineId(ruleId);
                //优先取账号权限分级的  没有取后台的 都没设置则不发送
                if(CollectionUtils.isNotEmpty(driverLineLists)){
                    List<Integer> userIdLists = new ArrayList<>();
                    driverLineLists.forEach(i-> userIdLists.add(i.getUserId()));
                    //根据userId查询手机号
                    List<CarAdmUser> userList = admUserExMapper.queryUsers(userIdLists,null,null,null,200);
                    if(CollectionUtils.isNotEmpty(userList)){
                        userList.forEach(i-> SmsSendUtil.sendTemplate(i.getPhone(), SmsTempleConstants.interNoticeTemple,Arrays.asList("小程序城际拼车")));
                        bl = true;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("发送短信异常",e);
        }

        return bl;
    }

    /**
     * 1.小程序未发送短信 2.后台下单发送短信
     * @param on
     * @param startCityId
     * @param endCityId
     * @param ruleId
     * @param bookingEndPoint
     */
    private void otherSendMessage(String[] on,Integer startCityId,Integer endCityId,Integer ruleId,String bookingEndPoint){
        InterCityUtils utils = new InterCityUtils();
        if(on.length >0 && startCityId>0 && endCityId>0 ) {
            String boardFirstAdd = on[0];
            if(StringUtils.isEmpty(boardFirstAdd)){
                return;
            }
            String[] boardPoint = boardFirstAdd.split(",");
            if(boardPoint.length > 1){
                String x = boardPoint[0];
                String y = boardPoint[1];
                List<String> onList = utils.hasBoardRoutRights(startCityId, x, y);
                if (CollectionUtils.isNotEmpty(onList)) {
                    String[] off = bookingEndPoint.split(";");
                    Integer finalEndCityId = endCityId;
                    onList.forEach(boardOn ->{
                        if(off.length > 0){
                            String[] offPoint = off[0].split(",");
                            if(offPoint.length > 0){
                                String offX = offPoint[0];
                                String offY = offPoint[1];
                                List<String> offList= utils.hasBoardOffRoutRights(finalEndCityId, offX, offY);
                                if(CollectionUtils.isNotEmpty(offList)){
                                    offList.forEach(boardOff ->{
                                        String route = utils.hasRoute(boardOn, boardOff);
                                        if (StringUtils.isNotEmpty(route)) {
                                            logger.info("=======下单====发送短信start====");
                                            sendMessage(route);
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        }
    }
    /**
     * 发动短信
     * @param route
     */
    private void sendMessage(String route){
        JSONObject jsonSupplier = JSONObject.parseObject(route);
        List<String> sendMobile = new ArrayList<>();
        logger.info("==========路线在范围内============");
        if (jsonSupplier.get("supplierId") != null) {
            String suppliers = jsonSupplier.getString("supplierId");
            List<String> supplierPhone = this.querySupplierPhone(suppliers);
            logger.info("======获取手机号码==========" + JSONObject.toJSONString(supplierPhone));
            if(CollectionUtils.isNotEmpty(supplierPhone) && StringUtils.isNotEmpty(supplierPhone.get(0))){
                supplierPhone.forEach(str ->{
                    logger.info("=====获取到的供应商手机号======" + str + ",发送短信开始=====");
                    if(!sendMobile.contains(str)){
                        SmsSendUtil.sendTemplate(str,SmsTempleConstants.interNoticeTemple,Arrays.asList("后台发送短信"));
                        sendMobile.add(str);
                    }
                });
            }else {
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
                        if(!sendMobile.contains(phone)){
                            SmsSendUtil.sendTemplate(phone,SmsTempleConstants.interNoticeTemple,Arrays.asList("后台发送"));
                            sendMobile.add(phone);
                        }
                    }
                }
            }
        }
    }





}
