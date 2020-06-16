package com.zhuanche.serv.authsupplier;

import com.zhuanche.common.cache.RedisCacheUtil;
import com.zhuanche.common.sms.SmsSendUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constant.Constants;
import com.zhuanche.entity.mdbcarmanage.CarAdmUser;
import com.zhuanche.shiro.cache.RedisCache;
import com.zhuanche.shiro.session.RedisSessionDAO;
import com.zhuanche.util.NumberUtil;
import com.zhuanche.util.PasswordUtil;
import mapper.mdbcarmanage.CarAdmUserMapper;
import mapper.mdbcarmanage.ex.CarAdmUserExMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author fanht
 * @Description
 * @Date 2020/6/15 下午5:08
 * @Version 1.0
 */
@Service
public class ResetPasswordService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CarAdmUserExMapper userExMapper;


    @Autowired
    private CarAdmUserMapper userMapper;


    @Autowired
    private RedisSessionDAO redisSessionDAO;

    /**根据手机号发送验证码*/
    public AjaxResponse sendPhoneCode(String phone){
        /**用户不存在*/
        CarAdmUser admUser = userExMapper.queryByPhone(phone);
        if( admUser == null ) {
            return AjaxResponse.fail(RestErrorCode.USER_NOT_EXIST );
        }

        String key = Constants.RESET_PASSWORD_KEY + phone;


        String msgCode =  RedisCacheUtil.get(key,String.class);

        if(msgCode == null){
             msgCode = NumberUtil.genRandomCode(6);
        }

        RedisCacheUtil.set(key,msgCode,2*60);

        String content = "尊敬的用户，您重置密码验证码为" + msgCode + ",有效期120秒";

        SmsSendUtil.send(phone,content);

        return AjaxResponse.success(null);
     }


     public AjaxResponse verifyPhoneCode(String phone,String msgCode){
         logger.info("====判断验证码是否正确=======入参：phone:" + phone +",msgCode:" +msgCode);


         String key = Constants.RESET_PASSWORD_KEY + phone;

         if(!RedisCacheUtil.exist(key)){
             logger.info("======验证码已过期=====");
             return AjaxResponse.fail(RestErrorCode.MSG_CODE_INVALID);
         }

         String  cacheMsgCode = RedisCacheUtil.get(key,String.class) == null ? "" : RedisCacheUtil.get(key,String.class).trim();

         logger.info("=======获取msgCode=====" + cacheMsgCode);
         if(msgCode.equals(cacheMsgCode)){
             logger.info("=======手机验证码正常========");
             return AjaxResponse.success(null);
         }else {
             return AjaxResponse.fail(RestErrorCode.MSG_CODE_WRONG);
         }
     }


    /**
     * 根据手机号重置密码
     * @return
     */
    public AjaxResponse resetPasswordByPhone(String phone,String newPassword,String msgCode){
        logger.info("====手机号码重置密码start=======入参：newPassword:" + newPassword +",phone:" +phone);
        CarAdmUser carAdmUser = userExMapper.queryByPhone(phone);
        if(carAdmUser == null){
            logger.info("======手机号码不存在=====");
            return AjaxResponse.fail(RestErrorCode.USER_NOT_EXIST);
        }

        String phoneCode =RedisCacheUtil.get(Constants.RESET_PASSWORD_KEY + phone,String.class);


        if(!RedisCacheUtil.exist(Constants.RESET_PASSWORD_KEY + phone)){
            logger.info("======验证码已过期=====");
            return AjaxResponse.fail(RestErrorCode.MSG_CODE_INVALID);
        }

        if(!msgCode.equals(phoneCode)){
            logger.info("======手机验证码不匹配=====");
            return AjaxResponse.fail(RestErrorCode.MSG_CODE_WRONG);
        }


        carAdmUser.setPassword(PasswordUtil.md5(newPassword , carAdmUser.getAccount()) );


        int upCode = userMapper.updateByPrimaryKeySelective(carAdmUser);

        if(upCode > 0){
            RedisCacheUtil.delete(Constants.RESET_PASSWORD_KEY + phone);
             logger.info("=======更改密码成功end========");
            //调用监听用户退出登录
            redisSessionDAO.clearRelativeSession(null,null, carAdmUser.getUserId());

            return AjaxResponse.success(null);
        }else {
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }

    }
}
