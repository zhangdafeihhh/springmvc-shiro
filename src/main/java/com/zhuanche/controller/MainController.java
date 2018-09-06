package com.zhuanche.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;

@Controller
public class MainController{
	private static final Logger logger = Logger.getLogger(MainController.class);	
    @Value(value="${loginpage.url}")
    private String loginpageUrl;  //前端UI登录页面
	@Value("${homepage.url}")
	private String homepageUrl; //前端UI首页页面
	
    
    /**运维监控心跳检测 **/
    @RequestMapping("/nginx")
    public String nginx(HttpServletResponse response ) throws IOException{
    	response.getWriter().append("true");
    	response.getWriter().close();
        return null;
    }
	/**显示首页**/
    @RequestMapping("/index")
    public String index(HttpServletRequest request , HttpServletResponse response,Model model) throws Exception {
    	logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>log4j桥接至logback测试成功！");
//		response.sendRedirect(homepageUrl);
//		return null;
        return "index";
    }
    
    /**显示登录页面 **/
	@RequestMapping("/login")
    public String login(HttpServletRequest request , HttpServletResponse response) throws Exception{
		Boolean isAjax = (Boolean) request.getAttribute("X_IS_AJAX");
		if(  isAjax  ) {
			AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_INVALID_SESSION);
			this.outJson(response, ajaxResponse);
		}else {
			response.sendRedirect(loginpageUrl);
		}
		return null;
	}
    
    /**显示无权限页面**/
	@RequestMapping("/unauthorized")
    public String unauthorized(HttpServletRequest request , HttpServletResponse response) throws Exception{
		Boolean isAjax = (Boolean) request.getAttribute("X_IS_AJAX");
		if(  isAjax  ) {
			AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_FORBIDDEN);
			this.outJson(response, ajaxResponse);
		}else {
			response.sendRedirect(homepageUrl);
			//return "unauthorized_content";//------------返回禁止访问的错误页
		}
		return null;
    }
    
    /**自定义实现404（利用SPRING的匹配优先级，最后是模糊匹配）**/
	@RequestMapping("/**")
    public String process404( HttpServletRequest request , HttpServletResponse response ) throws IOException{
		Boolean isAjax = (Boolean) request.getAttribute("X_IS_AJAX");
		if(  isAjax  ) {
			AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_NOT_FOUND);
			this.outJson(response, ajaxResponse);
		}else {
			response.sendRedirect(homepageUrl);
			//return "notFound";//------------返回页面不存在的错误页
		}
		return null;
    }
	
	
	/**响应JSON数据**/
	private void outJson( HttpServletResponse response , AjaxResponse ajaxResponse ) {
		PrintWriter out = null;
		try{
			response.setStatus(HttpStatus.SC_OK);
			out = response.getWriter();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json; charset=UTF-8");
			response.setHeader("pragma", "no-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			out.write( JSON.toJSONString(ajaxResponse, true) );
			out.close();
		} catch (Exception e) {
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}
}