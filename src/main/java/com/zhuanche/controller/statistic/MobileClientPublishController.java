package com.zhuanche.controller.statistic;

import com.zhuanche.common.sms.SmsSendUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.rentcar.CarSysMobileClientPublishDTO;
import com.zhuanche.entity.rentcar.CarSysMobileClientPublish;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.IPv4Util2;
import mapper.rentcar.ex.CarSysMobileClientPublishExMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller()
@RequestMapping(value = "/mobileClientPublish")
public class MobileClientPublishController {

	private static Logger log =  LoggerFactory.getLogger(MobileClientPublishController.class);

	@Autowired
	private CarSysMobileClientPublishExMapper carSysMobileClientPublishExMapper;

	private  String VALIDATE_CODE = "phoneCode";

	private  String VALIDATE_CODE_TIME = "VALIDATE_CODE_TIME";

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
		CarSysMobileClientPublish carSysMobileClientPublishe = this.carSysMobileClientPublishExMapper.queryMobileClientPublishInfo(channelNum);
		CarSysMobileClientPublishDTO dto = BeanUtil.copyObject(carSysMobileClientPublishe, CarSysMobileClientPublishDTO.class);
		return AjaxResponse.success(dto);
	}

	@ResponseBody
	@RequestMapping(value = "/valiteCode")
	public AjaxResponse valiteCode(HttpServletRequest request, @Verify(param = "phoneCode",rule = "required") String phoneCode) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//登陆名
		String userName = WebSessionUtil.getCurrentLoginUser().getLoginName();
		//验证码
		String mobile = (String) WebSessionUtil.getAttribute(userName+"_"+VALIDATE_CODE);
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
				return AjaxResponse.success(params.getDownloadUrl());
			}else{
				return AjaxResponse.fail(RestErrorCode.MSG_CODE_WRONG);
			}
		}else{
			return AjaxResponse.fail(RestErrorCode.MSG_CODE_INVALID);
		}
	}

	/**
	 * 请求发送验证码
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/phoneCode")
	public AjaxResponse sendPhoneCode(HttpServletRequest request) {
		SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
		String userName = WebSessionUtil.getCurrentLoginUser().getLoginName();
		if (user!=null && StringUtils.isNotEmpty(userName)){
			String ip = IPv4Util2.getClientIpAddr(request);
			log.info("短信验证码发送用户userName:"+userName+"  手机号phone:"+user.getMobile()+"   ip:"+ip);
			String code = getCode();
			log.info("短信验证码发送用户userName:"+userName+"  手机号phone:"+user.getMobile()+"   验证码code:"+code);
			SmsSendUtil.send(user.getMobile(), "验证码为："+code+" 验证码60秒内有效!");
			log.info("session中验证码放的key为:"+userName+"_"+VALIDATE_CODE);
			WebSessionUtil.setAttribute(userName+"_"+VALIDATE_CODE, code);
			return AjaxResponse.success(0);
		}else{
			return AjaxResponse.fail(RestErrorCode.HTTP_INVALID_SESSION);
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

}