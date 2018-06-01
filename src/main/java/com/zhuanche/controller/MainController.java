package com.zhuanche.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.zhuanche.common.web.AjaxResponse;

@Controller
public class MainController{
    /**运维监控心跳检测 **/
    @RequestMapping("/nginx")
    public String nginx(HttpServletResponse response ) throws IOException{
    	response.getWriter().append("true");
    	response.getWriter().close();
        return null;
    }
	/**显示首页**/
    @RequestMapping("/index")
    public String index(Model model){
        return "index";
    }
    
    /**显示无权限页面**/
	@RequestMapping("/unauthorized")
    @SuppressWarnings("deprecation")
    public String unauthorized(HttpServletRequest request , HttpServletResponse response, Model model){
		//1.判断是否为AJAX请求
		boolean isAjax = false;
		String XMLHttpRequest = request.getHeader("X-Requested-With");
		if(XMLHttpRequest!=null && XMLHttpRequest.trim().length()>0){
			isAjax = true;
		}
		//2.根据请求不同类型，返回相应的结果
		if( isAjax ) {//-------------------------返回AJAX 对象
			PrintWriter out = null;
			try{
				response.setStatus(HttpStatus.SC_FORBIDDEN, "HTTP_STATUS_403：禁止访问！");
				out = response.getWriter();
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/json; charset=UTF-8");
				response.setHeader("pragma", "no-cache");
				response.setHeader("Cache-Control", "no-cache");
				response.setDateHeader("Expires", 0);
				AjaxResponse ajaxResponse = AjaxResponse.fail("HTTP_STATUS_403：禁止访问！",   null);
				out.write( JSON.toJSONString(ajaxResponse, true) );
				out.close();
			} catch (Exception e) {
			} finally {
				if (out != null) {
					out.close();
				}
			}
			return null;
		}else {
			return "unauthorized_content";//------------返回禁止访问的错误页
		}
    }
    
    /**自定义实现404（利用SPRING的匹配优先级，最后是模糊匹配）**/
	@RequestMapping("/**")
    @SuppressWarnings("deprecation")
    public String process404( HttpServletRequest request , HttpServletResponse response ) throws IOException{
		//1.判断是否为AJAX请求
		boolean isAjax = false;
		String XMLHttpRequest = request.getHeader("X-Requested-With");
		if(XMLHttpRequest!=null && XMLHttpRequest.trim().length()>0){
			isAjax = true;
		}
		//2.根据请求不同类型，返回相应的结果
		if( isAjax ) {//-------------------------返回AJAX 对象
			PrintWriter out = null;
			try{
				response.setStatus(HttpStatus.SC_NOT_FOUND, "HTTP_STATUS_404：请求资源不存在！");
				out = response.getWriter();
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/json; charset=UTF-8");
				response.setHeader("pragma", "no-cache");
				response.setHeader("Cache-Control", "no-cache");
				response.setDateHeader("Expires", 0);
				AjaxResponse ajaxResponse = AjaxResponse.fail("HTTP_STATUS_404：请求资源不存在！",   null);
				out.write( JSON.toJSONString(ajaxResponse, true) );
				out.close();
			} catch (Exception e) {
			} finally {
				if (out != null) {
					out.close();
				}
			}
			return null;
		}else {
			return "notFound";//------------返回页面不存在的错误页
		}
    }
    
}