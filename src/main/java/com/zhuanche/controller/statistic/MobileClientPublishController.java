package com.zhuanche.controller.statistic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.entity.rentcar.CarSysMobileClientPublish;
import com.zhuanche.http.HttpClientUtil;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.IPv4Util2;
import com.zhuanche.util.MyRestTemplate;
import mapper.rentcar.ex.CarSysMobileClientPublishExMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller()
@RequestMapping(value = "/mobileClientPublish")
public class MobileClientPublishController {

	private static Log log =  LogFactory.getLog(MobileClientPublishController.class);

	@Autowired
	private CarSysMobileClientPublishExMapper carSysMobileClientPublishExMapper;

	@Autowired
	@Qualifier("wwwApiTemplate")
	private MyRestTemplate wwwApiTemplate;
	
	private  String VALIDATE_CODE = "phoneCode";

	/**
	 * 页面初始化信息，此时不能告知其下载地址
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryMobileClientPublish")
	public AjaxResponse queryMobileClientPublish() {
		log.info("手机端管理版本：queryMobileClientPublish");
		String suppliers = WebSessionUtil.getCurrentLoginUser().getSupplierIds().toString();
		suppliers = suppliers.substring(1, suppliers.length());
		String channelNum =  "shouqi";
		if(!"".equals(suppliers)&&"43".equals(suppliers)){
			channelNum = "xianglong";
		}
		CarSysMobileClientPublish carSysMobileClientPublishes = this.carSysMobileClientPublishExMapper.queryMobileClientPublishInfo(channelNum);
		carSysMobileClientPublishes.setDownloadUrl("");
		return AjaxResponse.success(carSysMobileClientPublishes);
	}

	@ResponseBody
	@RequestMapping(value = "/valiteCode")
	public AjaxResponse valiteCode(HttpServletRequest request,String phoneCode) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String,Object> result = new HashMap<String,Object>();
		//登陆名
		String userName = WebSessionUtil.getCurrentLoginUser().getLoginName();
		//手机号
		String mobile = (String) WebSessionUtil.getAttribute(userName+"_"+VALIDATE_CODE);
//		(String) request.getSession().getAttribute();
		if(StringUtils.isNotEmpty(mobile)){
			if(phoneCode.equals(mobile)){
				String suppliers = WebSessionUtil.getCurrentLoginUser().getSupplierIds().toString();
				suppliers = suppliers.substring(1, suppliers.length());
				String channelNum =  "shouqi";
				if(!"".equals(suppliers)&&"43".equals(suppliers)){
					channelNum = "xianglong";
				}
				CarSysMobileClientPublish params = this.carSysMobileClientPublishExMapper.queryMobileClientPublishInfo(channelNum);
				log.info("下载手机端管理版本userName:"+userName);
				log.info("验证码phoneCode:"+phoneCode);
				log.info("时间"+sdf.format(new Date()));
				log.info("downloadUrl:"+params.getDownloadUrl());
				result.put("downloadUrl", params.getDownloadUrl());
				result.put("result", 1);
			}else{
				result.put("result", 0);
			}
		}else{
			result.put("result", 0);
		}
		return AjaxResponse.success(result);
	}
	
	
	@ResponseBody
	@RequestMapping(value = "/phoneCode")
	public AjaxResponse sendPhoneCode(HttpServletRequest request) {
//		String userName = ToolUtils.getSecurityUser().getAccount();
		SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
		String userName = WebSessionUtil.getCurrentLoginUser().getLoginName();
		if (user!=null && StringUtils.isNotEmpty(userName)){
			String ip = IPv4Util2.getClientIpAddr(request);
			log.info("短信验证码发送用户userName:"+userName+"  手机号phone:"+user.getMobile()+"   ip:"+ip);
			String code = getCode();
			log.info("短信验证码发送用户userName:"+userName+"  手机号phone:"+user.getMobile()+"   验证码code:"+code);
			try {
				this.send(user.getMobile(), "验证码为："+code+" 验证码60秒内有效!");
			} catch (IOException e) {
				log.info("短信验证码发送userName:"+userName+"phone:"+user.getMobile()+",error:"+e);
			} catch (InterruptedException e) {
				log.info("短信验证码发送userName:"+userName+"phone:"+user.getMobile()+",error:"+e);
			}
			Map<String,Object> paramMap = new HashMap<String,Object>();
			String url = "/msg/sendMobileMsg?content={content}&Content_Type={Content_Type}&mobile={mobile}&AppId=2";
			paramMap.put("mobile", user.getMobile());
			paramMap.put("Content_Type", 1101);
			paramMap.put("content", "登录验证码为："+code+" 验证码60秒内有效!");
			wwwApiTemplate.getForObject(url, JSONObject.class, paramMap);
			log.info("session中验证码放的key为:"+userName+"_"+VALIDATE_CODE);
			WebSessionUtil.setAttribute(userName+"_"+VALIDATE_CODE, code);
			return AjaxResponse.success(0);
		}else{
			return AjaxResponse.fail(RestErrorCode.USER_NOT_EXIST);
		}
	}
	
	private String getCode(){
		double r = Math.random()*10000;
		int i = (int)r;
		if(i<1000){
			i+=1000;
		}
		return String.valueOf(i);
	}

	//发送验证码
	public void send(String mobile, String content) throws IOException, InterruptedException {
		String appkey = "C0BE8EEE8F49492CADF0C178B1194B35";
		String timestamp = String.valueOf(System.currentTimeMillis());
		String signContent = "appkey="+appkey+"&content="+content+"&mobile="+mobile+"&timestamp="+timestamp+"&appsecret=1D88D5B7B77547A8968C3B3669F69AA3";
		String url = "http://inside-mp.01zhuanche.com/api/v1/message/content/send";
		String sign = DigestUtils.md5Hex(signContent).toUpperCase();

		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("appkey", appkey);
		paramMap.put("content", content);
		paramMap.put("mobile", mobile);
		paramMap.put("timestamp", timestamp);
		paramMap.put("sign", sign);
		try {
			log.info("发短信 paramMap=" + paramMap);
			String resultData = HttpClientUtil.buildPostRequest(url).addParams(paramMap)
					.setConnectTimeOut(5000).execute();
			//response = driverMessageTemplate.postForObject(url,JSONObject.class,paramMap);
			//logger.info("发短信 返回结果response=" + response);
			JSONObject result = JSON.parseObject(resultData);
			int code = result.getIntValue("code");
			String msg = result.getString("msg");
			if (code == 1) {
				log.info("发短信出错,错误码:" + code + ",错误原因:" + msg);
			}
		} catch (Exception e) {
			log.info("发短信出错  error:" + e);
		}
		Thread.sleep(100L);
	}

}