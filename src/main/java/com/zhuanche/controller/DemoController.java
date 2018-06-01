package com.zhuanche.controller;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;

@Controller
@RequestMapping("/demo")
public class DemoController{
	//---------------------------------------------------------------------请求页面时
	/**有权限**/
    //@RequiresRoles(value= {"strategy_manage","dispatcher"},logical=Logical.OR )
    @RequestMapping("/hasPerm")
    public String hasPerm(){
        return "demo";
    }
	/**无权限**/
	@RequiresPermissions(value = { "user:viewaaaaaaaaaa", "user:create-bbbbbbbbbb" }, logical = Logical.AND )
    @RequestMapping("/noPerm")
    public String noPerm(){
        return "demo";
    }
	/**出现异常**/
    @RequestMapping("/whenException")
    public String whenException(){
    	int abc = 899/0;
    	abc = abc+1;
        return "demo";
    }

	//---------------------------------------------------------------------请求AJAX时
    /**AJAX响应正常**/
    @RequestMapping("/ajaxOK")
    @ResponseBody
    public AjaxResponse ajaxOK(){
    	SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
    	AjaxResponse respJson = AjaxResponse.success("业务受理成功", loginUser);
        return respJson;
    }
    
    /**AJAX响应无权限**/
	@RequiresPermissions(value = { "user:viewaaaaaaaaaa", "user:create-bbbbbbbbbb" }, logical = Logical.AND )
    @RequestMapping("/ajaxNoPerm")
    @ResponseBody
    public AjaxResponse ajaxNoPerm(){
    	SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
    	AjaxResponse respJson = AjaxResponse.success("业务受理成功", loginUser);
        return respJson;
    }
    
    /**AJAX响应异常**/
    @RequestMapping("/ajaxException")
    @ResponseBody
    public AjaxResponse ajaxException(){
    	SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
    	AjaxResponse respJson = AjaxResponse.success("业务受理成功", loginUser);
    	Integer.parseInt("0000ajaxException");
        return respJson;
    }

}