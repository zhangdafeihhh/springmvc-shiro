package com.zhuanche.controller.user;

import com.google.common.collect.Maps;
import com.zhuanche.common.cache.RedisCacheUtil;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.sms.SmsSendUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.constants.SmsTempleConstants;
import com.zhuanche.entity.mdbcarmanage.CarAdmUser;
import com.zhuanche.shiro.session.RedisSessionDAO;
import com.zhuanche.util.NumberUtil;
import mapper.mdbcarmanage.ex.CarAdmUserExMapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author fanht
 * @Description  手机端登录
 * @Date 2020/10/19 下午4:00
 * @Version 1.0
 */
@Controller
@RequestMapping("/phone")
public class PhoneLoginController {

    private static final Logger log        =  LoggerFactory.getLogger(LoginController.class);
    private static final String CACHE_PREFIX_MSGCODE_CONTROL_PHONE = "phone_mp_login_cache_msgcode_control_";
    private static final String CACHE_PREFIX_MSGCODE_PHONE         = "phone_mp_login_cache_msgcode_";
    @Value(value="${loginpage.url}")
    private String loginpageUrl;  //前端UI登录页面
    @Value("${homepage.url}")
    private String homepageUrl; //前端UI首页页面
    @Value("${login.checkMsgCode.switch}")
    private String loginCheckMsgCodeSwitch = "ON";//登录时是否进行短信验证的开关

    private int msgcodeTimeoutMinutes = 2;

    @Autowired
    private CarAdmUserExMapper carAdmUserExMapper;

    @Autowired
    private RedisSessionDAO redisSessionDAO;

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    /**通过用户名、密码，获取短信验证码**/
    @RequestMapping(value = "/getMsgCode" )
    @ResponseBody
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
    } )
    public AjaxResponse getMsgCode(@Verify(param="username",rule="required") String username ){
        //A: 频率检查 ,
        String redis_getmsgcode_key = "mp_manager_getmsgcode_key_"+username;
        long score = System.currentTimeMillis();
        //zset内部是按分数来排序的，这里用当前时间做分数
        redisTemplate.opsForZSet().add(redis_getmsgcode_key, String.valueOf(score), score);
        //统计30分钟内获取验证码次数
        int statistics = 30;
        redisTemplate.expire(redis_getmsgcode_key, statistics, TimeUnit.MINUTES);

        //统计用户30分钟内获取验证码次数
        long max = score;
        long min = max - (statistics * 60 * 1000);
        long count = redisTemplate.opsForZSet().count(redis_getmsgcode_key, min, max);

        int countLimit = 5;
        log.info("获取验证码-用户"+username+"在"+statistics+"分钟内第"+count+"次进行获取验证码操作");
        if(count  > countLimit) {
            log.info("获取验证码-用户"+username+"在"+statistics+"分钟内进行获取验证码"+count+"次,超过限制"+countLimit+",需要等待"+statistics+"分钟");
            return AjaxResponse.fail(RestErrorCode.GET_MSGCODE_EXCEED,statistics);
        }

        //B:查询用户信息
        CarAdmUser user = carAdmUserExMapper.queryByAccount(username);
        if(user==null){
            return AjaxResponse.fail(RestErrorCode.USER_NOT_EXIST) ;
        }
        //C:密码不正确

        //D: 查询验证码，或新生成验证码，而后发送验证码短信
        String  msgcode = RedisCacheUtil.get(CACHE_PREFIX_MSGCODE_PHONE+username, String.class);
        if(msgcode==null) {
            msgcode = NumberUtil.genRandomCode(6);
        }
        List list = new ArrayList();
        list.add(msgcode);
        SmsSendUtil.sendTemplate(user.getPhone(), SmsTempleConstants.loginTemple,list);
        //E: 写入缓存
        RedisCacheUtil.set(CACHE_PREFIX_MSGCODE_PHONE+username, msgcode,  msgcodeTimeoutMinutes * 60 );
        //返回结果
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("timeout",  60 );//验证码有效的秒数
        result.put("tipText", "短信验证码已成功发送至尾号为"+user.getPhone().substring(7)+"的手机上。" );//成功信息
        return AjaxResponse.success( result );
    }

    /**执行登录**/
    @RequestMapping(value = "/dologin" )
    @ResponseBody
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
    } )
    public AjaxResponse dologin(HttpServletRequest request , HttpServletResponse response,
                                @Verify(param="username",rule="required") String username,
                                @Verify(param="msgcode",rule="required") String msgcode ) throws IOException {

        String redis_login_key = "phone_mp_manager_login_key_"+username;
        String redis_getmsgcode_key = "phone_mp_manager_getmsgcode_key_"+username;

        Subject currentLoginUser = SecurityUtils.getSubject();
        //A:是否已经登录
        if(currentLoginUser.isAuthenticated()) {
            Boolean isAjax = (Boolean) request.getAttribute("X_IS_AJAX");
            if(  isAjax  ) {
                return AjaxResponse.success( null );
            }else {
                response.sendRedirect(homepageUrl);
                return null;
            }
        }
        //B:查询用户信息
        CarAdmUser user = carAdmUserExMapper.queryByAccount(username);
        if(user==null){
            return AjaxResponse.fail(RestErrorCode.USER_NOT_EXIST) ;
        }
        //C:密码不正确

        //D: 查询验证码，并判断是否正确
        if("ON".equalsIgnoreCase(loginCheckMsgCodeSwitch)) {


            long score = System.currentTimeMillis();
            //zset内部是按分数来排序的，这里用当前时间做分数
            redisTemplate.opsForZSet().add(redis_login_key, String.valueOf(score), score);
            //统计30分钟内用户登录次数
            int statistics = 30;
            redisTemplate.expire(redis_login_key, statistics, TimeUnit.MINUTES);

            //统计用户30分钟内登录的次数
            long max = score;
            long min = max - (statistics * 60 * 1000);
            long count = redisTemplate.opsForZSet().count(redis_login_key, min, max);

            int countLimit = 5;
            log.info("登录-用户"+username+"在"+statistics+"分钟内第"+count+"次登录");
            if(count  > countLimit) {
                log.info("登录-用户"+username+"在"+statistics+"分钟内登录"+count+"次,超过限制"+countLimit+",需要等待"+statistics+"分钟");
                return AjaxResponse.fail(RestErrorCode.DO_LOGIN_FREQUENTLY,statistics);
            }
            //验证验证码是否正确
            String  msgcodeInCache = RedisCacheUtil.get(CACHE_PREFIX_MSGCODE_PHONE+username, String.class);
            if(msgcodeInCache==null) {
                return AjaxResponse.fail(RestErrorCode.MSG_CODE_INVALID) ;
            }
            if(!msgcodeInCache.equals(msgcode)) {
                return AjaxResponse.fail(RestErrorCode.MSG_CODE_WRONG) ;
            }

        }
        //E: 用户状态
        if(user.getStatus()!=null && user.getStatus().intValue()==100 ){
            return AjaxResponse.fail(RestErrorCode.USER_INVALID) ;
        }
        //F: 执行登录
        Map<String,Object> map = Maps.newHashMap();
        try {
            //shiro登录  TODO 此处直接用查询出来的密码作为密码
            UsernamePasswordToken token = new UsernamePasswordToken( username, user.getPassword().toCharArray() );
            currentLoginUser.login(token);
            //记录登录用户的所有会话ID，以支持“系统管理”功能中的自动会话清理
            String sessionId =  (String)currentLoginUser.getSession().getId() ;
            redisSessionDAO.phoneSaveSessionIdOfLoginUser(username, sessionId);
            map.put("sesssionId",sessionId);
            redisTemplate.delete(redis_login_key);
            redisTemplate.delete(redis_getmsgcode_key);

        }catch(AuthenticationException aex) {
            return AjaxResponse.fail(RestErrorCode.USER_LOGIN_FAILED) ;
        }
        //返回登录成功
        Boolean isAjax = (Boolean) request.getAttribute("X_IS_AJAX");
        if(  isAjax  ) {
            return AjaxResponse.success( map );
        }else {
            response.sendRedirect(homepageUrl);
            return AjaxResponse.success(map);
        }
    }

    /**执行登出 **/
    @RequestMapping("/dologout")
    @ResponseBody
    public AjaxResponse dologout( HttpServletRequest request , HttpServletResponse response ) throws Exception{
        Subject subject = SecurityUtils.getSubject();
        if(subject.isAuthenticated()) {
            subject.logout();
        }
        Boolean isAjax = (Boolean) request.getAttribute("X_IS_AJAX");
        if(  isAjax  ) {
            return AjaxResponse.success( null );
        }else {
            response.sendRedirect(loginpageUrl);
            return null;
        }
    }
}
