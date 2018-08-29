package com.zhuanche.controller.user;

import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zhuanche.common.cache.RedisCacheUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.shiro.token.UsernamePasswordMsgcodeToken;

import mapper.mdbcarmanage.ex.CarAdmUserExMapper;
/**用户登录相关的功能**/
@Controller
public class LoginController{
	private static final Logger log =  LoggerFactory.getLogger(LoginController.class);
	private static final String CACHE_PREFIX_MSGCODE_CONTROL = "mp_login_cache_msgcode_control_";
	private static final String CACHE_PREFIX_MSGCODE = "mp_login_cache_msgcode_";
	@Autowired
	private CarAdmUserExMapper carAdmUserExMapper;
	
	
	
	
	/**通过用户名、密码，获取短信验证码**/
	@RequestMapping("/getMsgCode")
	@ResponseBody
    public AjaxResponse getMsgCode(String username, String password ){
		//A: 频率检查
		String flag = RedisCacheUtil.get(CACHE_PREFIX_MSGCODE_CONTROL+username, String.class);
		if(flag!=null ) {
			//TODO
			return AjaxResponse.fail(RestErrorCode.GET_LOCK_TIMEOUT);
		}
		
		//查询用户信息
		//用户不存在
		//密码不正确
		
		//发达验证码短信
		
		//成功信息，秒数
		
		return null;
		
	}
	
	/**执行登录**/
	@RequestMapping("/dologin")
	@ResponseBody
    public AjaxResponse dologin( String username, String password, String msgcode ){
		//表单验证
		//频率检查？？？
		
		//查询用户信息
		//用户不存在
		//密码不正确
		
		//短信验证码不正确
		
		
		//shiro登录
		
		//返回登录成功
		
		
		Subject currentuser = SecurityUtils.getSubject();
		
		
		UsernamePasswordMsgcodeToken token = new UsernamePasswordMsgcodeToken("admin", new char[] {'1','1','1','1','1','1'} , "800890");
		
		if(currentuser.isAuthenticated()) {
			//TODO  页面》重定向到首页，ajax >json???
		}else {
			currentuser.login(token);
			//TODO  页面》重定向到首页，ajax >json???
		}
		
		//DEMO
		currentuser = SecurityUtils.getSubject();
		
		Map m = new HashMap();
		m.put("a", "v");
		m.put("b", "10086");
		
		return AjaxResponse.success(  m );
		
		
//		//1.判断是否为AJAX请求
//		boolean isAjax = false;
//		String XMLHttpRequest = request.getHeader("X-Requested-With");
//		if(XMLHttpRequest!=null && XMLHttpRequest.trim().length()>0){
//			isAjax = true;
//		}
//		//2.根据请求不同类型，返回相应的结果
//		if( isAjax ) {//-------------------------返回AJAX 对象
//			PrintWriter out = null;
//			try{
//				response.setStatus(HttpStatus.SC_FORBIDDEN, "HTTP_STATUS_403：禁止访问！");
//				out = response.getWriter();
//				response.setCharacterEncoding("UTF-8");
//				response.setContentType("application/json; charset=UTF-8");
//				response.setHeader("pragma", "no-cache");
//				response.setHeader("Cache-Control", "no-cache");
//				response.setDateHeader("Expires", 0);
//				AjaxResponse ajaxResponse = AjaxResponse.fail("HTTP_STATUS_403：禁止访问！",   null);
//				out.write( JSON.toJSONString(ajaxResponse, true) );
//				out.close();
//			} catch (Exception e) {
//			} finally {
//				if (out != null) {
//					out.close();
//				}
//			}
//			return null;
//		}else {
//			return "unauthorized_content";//------------返回禁止访问的错误页
//		}
    }
	
	/**执行登出**/
	@RequestMapping("/dologout")
	@ResponseBody
    public AjaxResponse dologout( ){
		
		SecurityUtils.getSubject().logout();
		
		//TODO  页面》重定向到首页，ajax >json???
		
		
		return null;
	}
  
}