package com.zhuanche.controller.authsupplier;

import com.zhuanche.common.sms.SmsSendUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.Verify;
import com.zhuanche.serv.authsupplier.ResetPasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author fanht
 * @Description
 * @Date 2020/6/11 下午7:38
 * @Version 1.0
 */
@RequestMapping("/resetPassword")
public class ResetPasswordController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private ResetPasswordService passwordService;

    /**发送手机验证码*/
    @RequestMapping("/sendPhoneCode")
    public AjaxResponse sendPhoneCode(@Verify(param = "phone",rule = "required|mobile") String phone){

        return passwordService.sendPhoneCode(phone);

    }


    /**确认验证码*/
    @RequestMapping("/verifyPhoneCode")
    public AjaxResponse verifyPhoneCode(String phone,String msgCode){
        return passwordService.verifyPhoneCode(phone,msgCode);
    }


    /**修改密码*/
    @RequestMapping("/resetPasswordByPhone")
    public AjaxResponse resetPasswordByPhone(@Verify(param = "phone",rule = "required|mobile") String phone,
                                             @Verify(param = "newPassword",rule = "required") String newPassword,
                                             @Verify(param = "msgCode",rule = "required") String msgCode){
        return passwordService.resetPasswordByPhone(phone,newPassword,msgCode);
    }


}
