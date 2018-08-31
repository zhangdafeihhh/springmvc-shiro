package com.zhuanche.controller.user;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zhuanche.common.cache.RedisCacheUtil;
import com.zhuanche.common.sms.SmsSendUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.entity.mdbcarmanage.CarAdmUser;
import com.zhuanche.util.NumberUtil;
import com.zhuanche.util.PasswordUtil;

import mapper.mdbcarmanage.ex.CarAdmUserExMapper;
/**用户登录相关的功能**/
@Controller
public class LoginController{
	private static final Logger log =  LoggerFactory.getLogger(LoginController.class);
	private static final String CACHE_PREFIX_MSGCODE_CONTROL = "mp_login_cache_msgcode_control_";
	private static final String CACHE_PREFIX_MSGCODE                   = "mp_login_cache_msgcode_";
	
	@Value("${homepage.url}")
	private String homepageUrl;
	
	private int msgcodeTimeoutMinutes = 1;
	
	@Autowired
	private CarAdmUserExMapper carAdmUserExMapper;
	
	/**通过用户名、密码，获取短信验证码**/
	@RequestMapping("/getMsgCode")
	@ResponseBody
    public AjaxResponse getMsgCode(String username, String password ){
		//A: 频率检查
		String flag = RedisCacheUtil.get(CACHE_PREFIX_MSGCODE_CONTROL+username, String.class);
		if(flag!=null ) {
			return AjaxResponse.fail(RestErrorCode.GET_MSGCODE_EXCEED);
		}
		//B:查询用户信息
		CarAdmUser user = carAdmUserExMapper.queryByAccount(username);
		if(user==null){
			return AjaxResponse.fail(RestErrorCode.USER_NOT_EXIST) ;
		}
		//C:密码不正确
		String enc_pwd = PasswordUtil.md5(password, user.getAccount());
		if(!enc_pwd.equalsIgnoreCase(user.getPassword())) {
			return AjaxResponse.fail(RestErrorCode.USER_PASSWORD_WRONG) ;
		}
		//D: 查询验证码，或新生成验证码，而后发送验证码短信
		String  msgcode = RedisCacheUtil.get(CACHE_PREFIX_MSGCODE+username, String.class);
		if(msgcode==null) {
			msgcode = NumberUtil.genRandomCode(6);
		}
		String mobile   = user.getPhone();
		String content  = "登录验证码为："+msgcode+"，请在"+msgcodeTimeoutMinutes+"分钟内进行登录。";
		SmsSendUtil.send(mobile, content);
		//E: 写入缓存
		RedisCacheUtil.set(CACHE_PREFIX_MSGCODE_CONTROL+username, "Y",  60 );
		RedisCacheUtil.set(CACHE_PREFIX_MSGCODE+username, msgcode,  msgcodeTimeoutMinutes * 60 );
		//返回结果
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("timeout", msgcodeTimeoutMinutes * 60 );//验证码有效的秒数
		result.put("tipText", "短信验证码已成功发送至尾号为"+mobile.substring(7)+"的手机上。" );//成功信息
		return AjaxResponse.success( result );
	}
	
	/**执行登录**/
	@RequestMapping("/dologin")
	@ResponseBody
    public AjaxResponse dologin(HttpServletRequest request , HttpServletResponse response, String username, String password, String msgcode ) throws IOException{
		//1.判断是否为AJAX请求
		boolean isAjax = false;
		String XMLHttpRequest = request.getHeader("X-Requested-With");
		if(XMLHttpRequest!=null && XMLHttpRequest.trim().length()>0){
			isAjax = true;
		}
		
		Subject currentLoginUser = SecurityUtils.getSubject();
		//A:是否已经登录
		if(currentLoginUser.isAuthenticated()) {
			if(isAjax) {
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
		String enc_pwd = PasswordUtil.md5(password, user.getAccount());
		if(!enc_pwd.equalsIgnoreCase(user.getPassword())) {
			return AjaxResponse.fail(RestErrorCode.USER_PASSWORD_WRONG) ;
		}
		//D: 查询验证码，并判断是否正确
//		String  msgcodeInCache = RedisCacheUtil.get(CACHE_PREFIX_MSGCODE+username, String.class);
//		if(msgcodeInCache==null) {
//			return AjaxResponse.fail(RestErrorCode.MSG_CODE_INVALID) ;
//		}
//		if(!msgcodeInCache.equals(msgcode)) {
//			return AjaxResponse.fail(RestErrorCode.MSG_CODE_WRONG) ;
//		}
		//E: 用户状态
		if(user.getStatus()!=null && user.getStatus().intValue()==100 ){
			return AjaxResponse.fail(RestErrorCode.USER_INVALID) ;
		}
		//F: shiro登录
		try {
			UsernamePasswordToken token = new UsernamePasswordToken( username, password.toCharArray() );
			currentLoginUser.login(token);
		}catch(AuthenticationException aex) {
			return AjaxResponse.fail(RestErrorCode.USER_LOGIN_FAILED) ;
		}
		//返回登录成功
		if(isAjax) {
			return AjaxResponse.success( null );
		}else {
			response.sendRedirect(homepageUrl);
			return null;
		}
    }
	
	/**执行登出**/
	@RequestMapping("/dologout")
	@ResponseBody
    public AjaxResponse dologout( ){
		Subject subject = SecurityUtils.getSubject();
		if(subject.isAuthenticated()) {
			subject.logout();
		}
		return AjaxResponse.success( null );
	}
 
	//-------------------------------------------------------------------------------------------------------------------------------------当前登录用户信息BEGIN
	@RequestMapping("/currentUserinfo")
	@ResponseBody
    public AjaxResponse currentUserinfo( ){
		//缓存机制（//TODO 后期加入）
		
		//用户基本信息
		//用户的菜单信息
		//用户的权限信息
		//用户的数据权限
		
//		Subject subject = SecurityUtils.getSubject();
//		if(subject.isAuthenticated()) {
//			subject.logout();
//		}
		return AjaxResponse.success( null );
	}
	//-------------------------------------------------------------------------------------------------------------------------------------当前登录用户信息END
	
}