package com.zhuanche.controller.authsupplier;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.Verify;
import com.zhuanche.serv.authsupplier.ResetPasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author fanht
 * @Description 修改手机号
 * @Date 2020/6/16 下午6:18
 * @Version 1.0
 */
@RestController
public class ModifyPhoneController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ResetPasswordService passwordService;

    @RequestMapping("/modifyPhoneCode")
    public AjaxResponse modifyPhoneCode(@Verify(param = "phone",rule = "required|mobile") String phone){
        logger.info("修改手机发送验证码入参===" + phone);
        return passwordService.sendPhoneCode(phone,SendPhoneEnum.UPDATE_PHONE.getCode());
    }


    @RequestMapping("/modifyPhone")
    public AjaxResponse modifyPhone(@Verify(param = "phone",rule = "required|mobile")String phone,
                                    @Verify(param = "msgCode",rule = "required")String msgCode){
        logger.info("修改手机发送验证码入参===" + phone);
        return passwordService.modifyPhone(phone,msgCode);
    }
}
