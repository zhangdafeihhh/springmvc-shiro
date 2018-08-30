package com.zhuanche.controller.driverjoin;

import org.apache.http.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.http.HttpClientUtil;

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

	// 生成短链接
	@RequestMapping(value = "/makeShortUrl")
	@ResponseBody
	public AjaxResponse makeShortUrl(@Verify(param="supplierId", rule = "required")String supplierId){
		logger.info("供应商短链接生成,supplierId="+supplierId);
        String url = "https://monline.01zhuanche.com/driverRegister/register.html?source=supplier-invite%26supplier="+supplierId;
		String shortUrl  = null;
		try {
			shortUrl = HttpClientUtil.buildGetRequest("http://api.t.sina.com.cn/short_url/shorten.json?source=1681459862&url_long=" + url).setLimitResult(1).execute();
			logger.info("供应商短链接生成,supplierId={},shortUrl={}",supplierId,shortUrl);
		} catch (HttpException e) {
			logger.error("供应商短链接生成异常",e);
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}
		return AjaxResponse.success(shortUrl);
	}
}
