package com.zhuanche.serv.third;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.zhuanche.common.exception.ServiceException;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constant.Constants;
import com.zhuanche.entity.driver.PunishRecordVoiceDTO;
import com.zhuanche.http.MpOkHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author kjeakiry
 */
@Slf4j
@Component
public class MpManageRestClient {

    public static final String DRIVER_PUNISH_INIT_VIDEO_DATA = "/driverPunish/initVideoData";
    public static final String CODE = "code";

    @Value("${mp-manage-rest.url}")
    String mpManageRestUrl;


    /**
     * 查询司机录音
     * @param orderNo 订单号
     * @return
     */
    public List<PunishRecordVoiceDTO> driverPunishVideoQuery(String orderNo) {
        Map<String, Object> params = Maps.newHashMapWithExpectedSize(1);
        params.put("orderNo", orderNo);
        JSONObject result = MpOkHttpUtil.okHttpGetBackJson(mpManageRestUrl + DRIVER_PUNISH_INIT_VIDEO_DATA, params, 1, "录音查询");
        String data = getDataString(result);
        List<PunishRecordVoiceDTO> list = JSON.parseArray(data, PunishRecordVoiceDTO.class);
        return Objects.isNull(list) ? Collections.emptyList() : list;
    }

    private String getDataString(JSONObject result) {
        if (Objects.isNull(result) ||result.getIntValue(CODE) != Constants.SUCCESS_CODE) {
            throw new ServiceException(RestErrorCode.REST_FAIL_MP_MANAGE_API);
        }
        return result.getString("data");
    }

}
