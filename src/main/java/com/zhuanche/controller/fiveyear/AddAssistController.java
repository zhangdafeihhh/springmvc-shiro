package com.zhuanche.controller.fiveyear;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.sq.common.okhttp.OkHttpUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.Verify;
import com.zhuanche.constant.Constants;
import com.zhuanche.http.MpOkHttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Map;

/**
 * @Author fanht
 * @Description
 * @Date 2020/9/4 下午2:15
 * @Version 1.0
 */
@RestController
@RequestMapping("/addAssist")
public class AddAssistController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${domain.addr}")
    private String domainAddr;


    private static final String URL = "/otherViews/SQPartnerLikes/index.html?cityId=%d&cityName=%s&supplierName=%s&supplierId=%d&blessing=%s&currentTime=%d";


    @Value("${qr.short.addr}")
    private String qrShortAddr;

    @RequestMapping(value = "/qrShortAddress",method = RequestMethod.GET)
    public AjaxResponse qrShortAddress(@Verify(param = "cityId",rule = "required") Integer cityId,
                                       @Verify(param = "supplierId",rule = "required")Integer supplierId,
                                       @Verify(param = "cityName",rule = "required")String cityName,
                                       @Verify(param = "supplierName",rule = "required")String supplierName,
                                       @Verify(param = "blessing",rule = "required")String blessing){
        logger.info(MessageFormat.format("生成二维码短连接入参:cityId:{0},suppierId:{1},supplierName:{2},blessing:{3},cityName:{4}",cityId,
                supplierId,supplierName,blessing,cityName));
        String jsonResult = null;
        try {
            Map<String,Object> map = Maps.newHashMap();

            map.put("url", String.format(domainAddr+URL,cityId,URLEncoder.encode(cityName,"utf-8"), URLEncoder.encode(supplierName,"utf-8"),supplierId,URLEncoder.encode(blessing,"utf-8"),System.currentTimeMillis()));
            map.put("expired",0);

            logger.info("生成二维码短链入参" + JSONObject.toJSONString(map));

            jsonResult = MpOkHttpUtil.okHttpPost(qrShortAddr + "/admin/addShortLink", map,0,null);

            logger.info("=====生成二维码返回结果======" + jsonResult);

            if(StringUtils.isNotEmpty(jsonResult)){
                JSONObject jsonObject = JSONObject.parseObject(jsonResult);
                if(jsonObject.get(Constants.CODE) != null && jsonObject.getInteger(Constants.CODE) == 0){
                    return AjaxResponse.success(jsonObject.get(Constants.DATA));
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return AjaxResponse.success(jsonResult);


    }

}
