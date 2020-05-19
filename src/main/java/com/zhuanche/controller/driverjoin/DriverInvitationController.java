package com.zhuanche.controller.driverjoin;

import com.google.common.collect.Maps;
import com.zhuanche.common.web.RequestFunction;
import org.apache.http.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.http.HttpClientUtil;

import java.util.Map;

import static com.zhuanche.common.enums.MenuEnum.DRIVER_JOIN_PROMOTE_INVITE;

/** 
 * 司机注册邀请
 * ClassName: DriverInvitationController.java 
 * Date: 2018年8月29日 
 * @author xinchun
 * @version 1.0
 * @since JDK 1.8.0_161
 */

@Controller
@RequestMapping("/driverInvitation")
public class DriverInvitationController {
	
	private static final Logger logger = LoggerFactory.getLogger(DriverInvitationController.class);

	/**司机加盟注册链接**/
	private static final String DRIVER_JOIN_URL = "https://monline.01zhuanche.com/driverRegister/register.html?source=supplier-invite%26supplier=";
	/**司机加盟**/
	private static final String DRIVER_JOIN_URL_LONG = "https://monline.01zhuanche.com/driverRegister/register.html?source=supplier-invite&supplier=";

	/**SINA提供短链接生成服务**/
	private static final String SINA_API = "http://api.t.sina.com.cn/short_url/shorten.json?source=1681459862&url_long=";
	
	/**生成短链接*/
	@RequestMapping(value = "/makeShortUrl")
	@ResponseBody
	@RequestFunction(menu = DRIVER_JOIN_PROMOTE_INVITE)
	public AjaxResponse makeShortUrl(@Verify(param="supplierId", rule = "required")String supplierId){
		logger.info("供应商短链接生成,supplierId="+supplierId);
		JSONArray parseArray = null;
		try {
			String url = DRIVER_JOIN_URL+supplierId;
			String shortUrl  = null;
			try {
                shortUrl = HttpClientUtil.buildGetRequest(SINA_API + url).setLimitResult(1).execute();
                logger.info("供应商短链接生成,supplierId={},result={}",supplierId,shortUrl);
            } catch (HttpException e) {
				logger.info("供应商连接地址生成失败，使用长连接" + e);
				shortUrl = url;
            }
			parseArray = JSON.parseArray((shortUrl));
		} catch (Exception e) {
			logger.info("地址生成失败");
			parseArray = new JSONArray();
			Map<String,Object> map = Maps.newHashMap();
			map.put("url_long",DRIVER_JOIN_URL_LONG+supplierId);
			map.put("type",0);
			parseArray.add(map);
			logger.info("连接地址生成失败，重新生成：" + parseArray);
 		}
		return AjaxResponse.success(parseArray);
	}
}
