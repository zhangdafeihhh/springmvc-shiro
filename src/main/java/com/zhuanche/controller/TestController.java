package com.zhuanche.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.syslog.SysLogAnn;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.entity.driver.SysLog;
import com.zhuanche.serv.syslog.SysLogService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;

/**  
 * ClassName:TestController <br/>  
 * Date:     2019年4月18日 下午3:50:40 <br/>  
 * @author   baiyunlong
 * @version  1.0.0      
 */
@Controller
@RequestMapping("/test")
public class TestController {
    @Autowired
    private SysLogService sysLogService;
    
    @RequestMapping("/updateSysLog")
    /*@SysLogAnn(module="系统日志",methods="系统日志更新",
    serviceClass="sysLogService",queryMethod="querySysLog",parameterType="Integer",parameterKey="sysLogId")*/
    @ResponseBody
    public AjaxResponse updateSysLog(Integer sysLogId,
    		String username,
    		String module,
    		String method/*,
    		@RequestParam(value = "file",required = false) MultipartFile file*/
    		){
    	SysLog sysLog=new SysLog();
    	sysLog.setSysLogId(sysLogId);
    	sysLog.setUsername(username);
    	sysLog.setModule(module);
    	sysLog.setMethod(method);
    	sysLogService.updateSysLog(sysLog);
    	AjaxResponse respJson = AjaxResponse.success(true);
        return respJson;
    }
    
	@RequestMapping(value = "/querySysLogList")
    @ResponseBody
	public AjaxResponse querySysLogList(@Verify(param = "page", rule = "required|min(1)") Integer page,
			@Verify(param = "pageSize", rule = "required|min(10)") Integer pageSize) {
		try {
			PageDTO pageDTO = sysLogService.querySysLogList(page, pageSize);
			return AjaxResponse.success(pageDTO);
		} catch (Exception e) {
			return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
		}
	}
    
}
  
