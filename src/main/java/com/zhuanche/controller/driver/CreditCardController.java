package com.zhuanche.controller.driver;

import com.bill99.mgw.dto.UnBindDTO;
import com.bill99.mgw.dto.UnBindResponse;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.entity.rentcar.CarBizDriverInfo;
import com.zhuanche.serv.CarBizDriverInfoService;
import com.zhuanche.serv.creditCard.CreditCardService;
import com.zhuanche.shiro.session.WebSessionUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/creditCard")
public class CreditCardController {

    private static final Logger logger = LoggerFactory.getLogger(Componment.class);
    private static final String LOGTAG = "[司机信用卡]: ";

    @Autowired
    private CreditCardService creditCarService;

    @Autowired
    private CarBizDriverInfoService carBizDriverInfoService;

    /**
     * 获取信用卡验证码及短卡号
     * @param driverId 司机ID
     * @param creditCardNo 卡号
     * @param expireDate 有效期
     * @param cvn2 cvn2
     * @param idCardNo 身份证
     * @param cardHolderName 姓名
     * @param driverPhone 司机手机号
     * @param bandCardphone 绑定银行卡手机号
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/addCreditInfo")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE)
    })
    public AjaxResponse savecreditCard(@Verify(param = "driverId", rule = "required") Integer driverId,
                                 @Verify(param = "creditCardNo", rule = "required") String creditCardNo,
                                 @Verify(param = "expireDate", rule = "required") String expireDate,
                                 @Verify(param = "cvn2", rule = "required") String cvn2,
                                 @Verify(param = "idCardNo", rule = "required") String idCardNo,
                                 @Verify(param = "cardHolderName", rule = "required") String cardHolderName,
                                 @Verify(param = "driverPhone", rule = "required") String driverPhone,
                                 @Verify(param = "bandCardphone", rule = "required") String bandCardphone) {
        try {
            logger.info(LOGTAG + "司机driverId={},司机获取验证码接口处理 creditCardNo={}, expireDate={}, cvn2={}, idCardNo={}, cardHolderName={}, bandCardphone={}",
                    driverId, creditCardNo, expireDate, cvn2, idCardNo, cardHolderName, bandCardphone);
            Map<String, Object> returnMap = creditCarService.updateValNumDriver("2", "", creditCardNo, expireDate, cvn2, cardHolderName, idCardNo, bandCardphone, driverPhone, driverId);
            if(returnMap.get("returnCode").equals("0")){
                return AjaxResponse.success(true);
            }else if(returnMap.get("returnCode").equals("1")){
                if(!(returnMap.get("errormsg")==null || returnMap.get("errormsg")=="" ||  returnMap.get("errormsg").toString().isEmpty())){
                    logger.info(LOGTAG + "司机driverId={},司机获取验证码接口处理 处理失败={}", driverId, returnMap.get("errormsg").toString());
                    return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR, returnMap.get("errormsg").toString());
                }
            }else {
                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR, "服务器异常!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(LOGTAG + "司机driverId={},司机获取验证码接口处理 处理失败={}", driverId, e.getMessage());
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR, "服务器异常!");
        }
        return AjaxResponse.success(true);
    }

    /**
     * 绑定信用卡
     * @param creditCardNo 卡号
     * @param expireDate 有效期
     * @param cvn2 cvn2
     * @param idCardNo 身份证
     * @param cardHolderName 姓名
     * @param driverPhone 司机手机号
     * @param bandCardphone 绑定银行卡手机号
     * @param driverId 司机ID
     * @param valditCode 验证码
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/bindcreditCard")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE)
    })
    public AjaxResponse bindcreditCard(@Verify(param = "creditCardNo", rule = "required") String creditCardNo,
                                 @Verify(param = "expireDate", rule = "required") String expireDate,
                                 @Verify(param = "cvn2", rule = "required") String cvn2,
                                 @Verify(param = "idCardNo", rule = "required") String idCardNo,
                                 @Verify(param = "cardHolderName", rule = "required") String cardHolderName,
                                 @Verify(param = "driverPhone", rule = "required") String driverPhone,
                                 @Verify(param = "bandCardphone", rule = "required") String bandCardphone,
                                 @Verify(param = "driverId", rule = "required") Integer driverId,
                                 @Verify(param = "valditCode", rule = "required") String valditCode) {
        logger.info(LOGTAG + "司机driverId={},绑定信用卡", driverId);
        try {
            Map<String, Object> returnMap = this.creditCarService.addDriverCreditCard(creditCardNo, expireDate, cvn2, cardHolderName, idCardNo, bandCardphone, valditCode, driverPhone, driverId);
            if(returnMap.get("returnCode").equals("0")){
                return AjaxResponse.success(true);
            }else{
                if(returnMap.get("returnMsg")!=null || returnMap.get("returnMsg")!=""){
                    return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR, returnMap.get("returnMsg").toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR, e.getMessage());
        }
        return AjaxResponse.success(true);
    }

    /**
     * 解绑信用卡（司机）
     * @param driverId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/relieveCreditCards")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE)
    })
    public AjaxResponse relieveCreditCards(@Verify(param = "driverId", rule = "required") Integer driverId) {

        Map<String, Object> resultMap = new HashMap<String, Object>();
        logger.info(LOGTAG + "司机driverId={},解绑信用卡（司机）", driverId);
        try {
            CarBizDriverInfo carBizDriverInfo = carBizDriverInfoService.selectByPrimaryKey(driverId);
            if(carBizDriverInfo==null){
                logger.info(LOGTAG + "司机driverId={},解绑司机信用卡失败，司机信息为空", driverId);
                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR, "司机driverId=" + driverId + ",解绑信用卡失败，司机信息为空");
            }

            if(StringUtils.isEmpty(carBizDriverInfo.getStorableCardNo()) || StringUtils.isEmpty(carBizDriverInfo.getCreditCardNo())
                    || StringUtils.isEmpty(carBizDriverInfo.getCreditOpenAccountBank()) || StringUtils.isEmpty(carBizDriverInfo.getBankid())){
                // 信用卡短卡号绑定至司机信息
                Map<String, Object> cardMap = new HashMap<String, Object>();
                cardMap.put("updateBy", WebSessionUtil.getCurrentLoginUser().getId());//
                cardMap.put("driverId", driverId);//
                cardMap.put("creditOpenAccountBank", "");//开户行
                cardMap.put("shortCardNo", "");//短卡号
                cardMap.put("isBindingCreditCard", 0);//是否绑卡（0不绑）
                cardMap.put("creditCardNo", "");//信用卡号
                cardMap.put("CVN2", "");//cvn2
                cardMap.put("phone", carBizDriverInfo.getPhone());//phone
                carBizDriverInfoService.updateDriverCardInfo(cardMap);
                logger.info(LOGTAG + "司机driverId={},解绑信用卡成功", driverId);
                return AjaxResponse.success(true);
            }

            UnBindDTO dto=new UnBindDTO();
            dto.setCustomerId(carBizDriverInfo.getQuickpayCustomerid());
            dto.setPan(carBizDriverInfo.getCreditCardNo());//信用卡号
            dto.setStorablePan(carBizDriverInfo.getStorableCardNo());//短卡号
            dto.setBankId(carBizDriverInfo.getBankid());
            UnBindResponse response  = creditCarService.relieveCreditCards(dto);
            if(response==null){
                logger.info(LOGTAG + "司机driverId={},解绑信用卡失败，调用接口失败", driverId);
                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR, "司机driverId=" + driverId + ",解绑信用卡失败，调用接口失败");
            }else{
                boolean isqzjb = true;
//				 如果块钱有过绑定     responseCode=00 操作成功
                Integer userId = WebSessionUtil.getCurrentLoginUser().getId();
                if("00".equals(response.getResponseCode())){
                    // 信用卡短卡号绑定至司机信息
                    Map<String, Object> cardMap = new HashMap<String, Object>();
                    cardMap.put("updateBy", userId);//
                    cardMap.put("driverId", carBizDriverInfo.getDriverId());//
//					 cardMap.put("quickpayCustomerid", "");//块钱标识
                    cardMap.put("creditOpenAccountBank", "");//开户行
                    cardMap.put("shortCardNo", "");//短卡号
                    cardMap.put("isBindingCreditCard", 0);//是否绑卡（0不绑）
                    cardMap.put("creditCardNo", "");//信用卡号
                    cardMap.put("CVN2", "");//cvn2
                    cardMap.put("phone", carBizDriverInfo.getPhone());//phone
                    carBizDriverInfoService.updateDriverCardInfo(cardMap);
                    logger.info(LOGTAG + "司机driverId={},解绑信用卡成功", driverId);
                    return AjaxResponse.success(true);
                }else if("T8".equals(response.getResponseCode()) && isqzjb){
                    // 信用卡短卡号绑定至司机信息
                    Map<String, Object> cardMap = new HashMap<String, Object>();
                    cardMap.put("updateBy", userId);//
                    cardMap.put("driverId", carBizDriverInfo.getDriverId());//
//					 cardMap.put("quickpayCustomerid", "");//块钱标识
                    cardMap.put("creditOpenAccountBank", "");//开户行
                    cardMap.put("shortCardNo", "");//短卡号
                    cardMap.put("isBindingCreditCard", 0);//是否绑卡（0不绑）
                    cardMap.put("creditCardNo", "");//信用卡号
                    cardMap.put("CVN2", "");//cvn2
                    cardMap.put("phone", carBizDriverInfo.getPhone());//phone
                    carBizDriverInfoService.updateDriverCardInfo(cardMap);
                    logger.info(LOGTAG + "司机driverId={},解绑信用卡成功", driverId);
                    return AjaxResponse.success(true);
                }else{
                    logger.info(LOGTAG + "司机driverId={},解绑信用卡失败={}", driverId, response.getReturnTextMessage());
                    // 信用卡短卡号绑定至司机信息
                    Map<String, Object> cardMap = new HashMap<String, Object>();
                    cardMap.put("updateBy", userId);//
                    cardMap.put("driverId", carBizDriverInfo.getDriverId());//
//					 cardMap.put("quickpayCustomerid", "");//块钱标识
                    cardMap.put("creditOpenAccountBank", "");//开户行
                    cardMap.put("shortCardNo", "");//短卡号
                    cardMap.put("isBindingCreditCard", 0);//是否绑卡（0不绑）
                    cardMap.put("creditCardNo", "");//信用卡号
                    cardMap.put("CVN2", "");//cvn2
                    cardMap.put("phone", carBizDriverInfo.getPhone());//phone
                    carBizDriverInfoService.updateDriverCardInfo(cardMap);
                    logger.info(LOGTAG + "司机driverId={},解绑信用卡成功", driverId);
                    return AjaxResponse.success(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(LOGTAG + "司机driverId={},司机解绑信用卡失败={}", driverId, e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR, "司机driverId=" + driverId + ",解绑信用卡失败" + e.getMessage());
        }
    }
}
