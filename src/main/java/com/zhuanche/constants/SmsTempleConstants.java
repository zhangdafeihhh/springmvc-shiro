package com.zhuanche.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author fanht
 * @Description
 * @Date 2021/1/21 下午2:41
 * @Version 1.0
 */
@Component
public class SmsTempleConstants {

    /**登录模板id*/
    public static Integer loginTemple;

    /**重置密码模板id*/
    public static Integer resetTemple;

    /**版本更新模板id*/
    public static Integer versionUpdateTemple;

    /**加盟商服务平台开通模板id*/
    public static Integer registerTemple;

    /**城际拼车指派模板*/
    public static Integer interNoticeTemple;

    /**城际拼车改派模板*/
    public static Integer interLoginTemple;

    /**千里眼注册成功模板*/
    public static Integer eyeRegisterTemple;

    /**巴士模板*/
    public static Integer bashiTemple;


    /**管理员重置密码*/
    public static Integer manageResetTemple;

    /**司机发送模板*/
    public static Integer driverTemple;
    /**巴士取消*/
    public static Integer bashiCancleTemple;
    /**巴士发送短信*/
    public static Integer bashiNoticeTemple;


    @Value("${login.temple}")
    public  void setLoginTemple(Integer loginTemple) {
        SmsTempleConstants.loginTemple = loginTemple;
    }
    @Value("${reset.temple}")
    public  void setResetTemple(Integer resetTemple) {
        SmsTempleConstants.resetTemple = resetTemple;
    }
    @Value("${version.update.temple}")
    public  void setVersionUpdateTemple(Integer versionUpdateTemple) {
        SmsTempleConstants.versionUpdateTemple = versionUpdateTemple;
    }
    @Value("${register.temple}")
    public  void setRegisterTemple(Integer registerTemple) {
        SmsTempleConstants.registerTemple = registerTemple;
    }
    @Value("${inter.notice.temple}")
    public  void setInterNoticeTemple(Integer interNoticeTemple) {
        SmsTempleConstants.interNoticeTemple = interNoticeTemple;
    }
    @Value("${inter.login.temple}")
    public  void setInterLoginTemple(Integer interLoginTemple) {
        SmsTempleConstants.interLoginTemple = interLoginTemple;
    }
    @Value("${eye.register.temple}")
    public  void setEyeRegisterTemple(Integer eyeRegisterTemple) {
        SmsTempleConstants.eyeRegisterTemple = eyeRegisterTemple;
    }
    @Value("${bashi.temple}")
    public  void setBashiTemple(Integer bashiTemple) {
        SmsTempleConstants.bashiTemple = bashiTemple;
    }

    @Value("${manage.reset.temple}")
    public  void setManageResetTemple(Integer manageResetTemple) {
        SmsTempleConstants.manageResetTemple = manageResetTemple;
    }
    @Value("${driver.temple}")
    public  void setDriverTemple(Integer driverTemple) {
        SmsTempleConstants.driverTemple = driverTemple;
    }
    @Value("${bashi.cancle.temple}")
    public  void setBashiCancleTemple(Integer bashiCancleTemple) {
        SmsTempleConstants.bashiCancleTemple = bashiCancleTemple;
    }
    @Value("${bashi.notice.temple}")
    public  void setBashiNoticeTemple(Integer bashiNoticeTemple) {
        SmsTempleConstants.bashiNoticeTemple = bashiNoticeTemple;
    }
}
