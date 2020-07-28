package com.zhuanche.serv.punish;

import cn.hutool.core.io.IoUtil;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Maps;
import com.le.config.dict.Dicts;
import com.sq.common.okhttp.OkHttpUtil;
import com.sq.common.okhttp.result.OkResponseResult;
import com.zhuanche.common.exception.ServiceException;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.serv.punish.query.DriverPunishQuery;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author kjeakiry
 */
@Slf4j
@Service
public class DriverPunishClientService extends DriverPunishService {
    private static final String DO_AUDIT = "/driverPunish/examineDriverPunish";
    private static final String PUNISH_DETAIL = "/driverPunish/findDriverPunishDetail";
    private static final String PUNISH_EXPORT = "/driverPunish/export";
    private static final String CODE = "code";
    private static final String CHAR_NULL = "";
    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient() {{
        new Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }};


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
     *
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

    public void exportExcel(DriverPunishQuery params, HttpServletResponse response) {
        log.info("处罚列表导出，参数: {}", params.toString());
        Map<String, Object> paramMap = JSONObject.parseObject(JSONObject.toJSONString(params), new TypeReference<HashMap<String, Object>>() {
        });
        String url = Dicts.getString("mp.transport.url") + PUNISH_EXPORT;
        Request okHttpRequest = new Request.Builder().url(buildGetUrl(url, paramMap)).build();
        Call call = OK_HTTP_CLIENT.newCall(okHttpRequest);
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            Response okHttpResponse = call.execute();
            if (okHttpResponse != null && okHttpResponse.isSuccessful() && Objects.nonNull(okHttpResponse.body())) {
                inputStream = okHttpResponse.body().byteStream();
                outputStream = getOutputStream("司机处罚列表", response);
                IoUtil.copy(inputStream, outputStream);
                outputStream.flush();
            }
        } catch (Exception e) {
            log.error("司机申诉列表下载失败", e);
        } finally {
            IoUtil.close(inputStream);
            IoUtil.close(outputStream);
        }
    }

    private static OutputStream getOutputStream(String fileName, HttpServletResponse response) throws Exception {
        if (!fileName.contains(ExcelTypeEnum.XLSX.getValue())) {
            fileName = fileName + ExcelTypeEnum.XLSX.getValue();
        }
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.setContentType("multipart/form-data");
        response.setCharacterEncoding("utf8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        return response.getOutputStream();
    }


    private static String buildGetUrl(String url, Map<String, ?> params) {
        StringBuilder builder = null;
        if (params != null) {
            for (String key : params.keySet()) {
                if (key != null && params.get(key) != null) {
                    if (builder == null) {
                        builder = new StringBuilder(url);
                        builder.append((!url.contains("?")) ? "?" : "&");
                    } else {
                        builder.append("&");
                    }
                    builder.append(key).append("=");
                    if (params.get(key) != null) {
                        builder.append(encodeParams(params.get(key).toString()));
                    } else {
                        builder.append(CHAR_NULL);
                    }
                }
            }
        }
        builder = (builder != null) ? builder : new StringBuilder(url);

        return builder.toString();
    }

    private static String encodeParams(String params) {
        try {
            params = URLEncoder.encode(params, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
        }
        return params;
    }


}
