package com.zhuanche.serv.punish;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.le.config.dict.Dicts;
import com.sq.common.okhttp.OkHttpUtil;
import com.sq.common.okhttp.result.OkResponseResult;
import com.zhuanche.common.exception.ServiceException;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author kjeakiry
 */
@Service
public class DriverPunishClientService extends DriverPunishService {
    private static final String DO_AUDIT = "/driverPunish/examineDriverPunish";
    private static final String PUNISH_DETAIL = "/driverPunish/findDriverPunishDetail";
    private static final String CODE = "code";


    @Override
    public void doAudit(Integer punishId, Integer status, String cgReason, String cgPictures) {
        Map<String, Object> paramMap = Maps.newHashMapWithExpectedSize(5);
        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        paramMap.put("punishId", punishId);
        paramMap.put("status", status);
        paramMap.put("cgReason", cgReason);
        paramMap.put("cgPictures", cgPictures);
        paramMap.put("updateUserId", currentLoginUser.getId());
        paramMap.put("updateUserName", currentLoginUser.getName());
        OkResponseResult result = OkHttpUtil.getIntance().doPost(Dicts.getString("mp.transport.url") + DO_AUDIT, null, paramMap, "司机申诉审核");
        if (!result.isSuccessful()) {
            throw new ServiceException(RestErrorCode.RECORD_DEAL_FAILURE, result.getErrorMsg());
        }
        JSONObject jsonObject = JSONObject.parseObject(result.getResponseBody());
        if (!Objects.equals(0, jsonObject.getInteger(CODE))) {
            throw new ServiceException(RestErrorCode.RECORD_DEAL_FAILURE, jsonObject.getString("msg"));
        }
    }


    /**
     * 查询申诉详情
     * @param punishId
     * @return
     */
    public Map<String, Object> getDriverPunishDetail(Integer punishId) {
        Map<String, Object> paramMap = Maps.newHashMapWithExpectedSize(1);
        paramMap.put("punishId", punishId);
        OkResponseResult result = OkHttpUtil.getIntance().doGet(Dicts.getString("mp.transport.url") + PUNISH_DETAIL, null, paramMap, "查询申诉详情");
        if (!result.isSuccessful()) {
            throw new ServiceException(RestErrorCode.HTTP_SYSTEM_ERROR, result.getErrorMsg());
        }
        JSONObject jsonObject = JSONObject.parseObject(result.getResponseBody());
        if (Objects.nonNull(jsonObject) && Objects.equals(0, jsonObject.getInteger(CODE))) {
            JSONObject data = jsonObject.getJSONObject("data");
            Map<String, Object> resultMap = new HashMap<>(4);
            resultMap.put("driverPunish", data.getJSONObject("driverPunish"));
            resultMap.put("rocordList", data.getJSONArray("rocordList"));
            resultMap.put("orderVideoVOList", data.getJSONArray("videoList"));
            return resultMap;
        }
        throw new ServiceException(RestErrorCode.HTTP_SYSTEM_ERROR, jsonObject.getString("msg"));
    }

}
