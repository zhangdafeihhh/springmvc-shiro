package com.zhuanche.serv.punish;

import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.le.config.dict.Dicts;
import com.sq.common.okhttp.OkHttpUtil;
import com.sq.common.okhttp.result.OkResponseResult;
import com.zhuanche.common.exception.ServiceException;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constant.Constants;
import com.zhuanche.entity.driver.PunishRecordVoiceDTO;
import com.zhuanche.serv.punish.query.DriverPunishQuery;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.OkHttpStreamUtil;
import com.zhuanche.util.excel.ExportExcelUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author kjeakiry
 */
@Slf4j
@Service
public class DriverPunishClientService extends DriverPunishService {

    private static final String RENDER_URL = "/driverPunish/renderVideo.json?filePath=";
    private static final String ALI_YUN_CS_COM = "aliyuncs.com";
    private static final String DO_AUDIT = "/driverPunish/examineDriverPunish";
    private static final String PUNISH_DETAIL = "/driverPunish/findDriverPunishDetail";
    private static final String PUNISH_EXPORT = "/driverPunish/export";
    private static final String INIT_VIDEO = "/driverPunish/initVideoData";
    private static final String PUNISH_TYPE_LIST = "/driverPunish/punishTypeList";
    private static final String CODE = "code";
    private static final String HTTP = "http:";
    private static final Set<String> IGNORE_MSG = new HashSet<String>(){{
        add("?????????????????????????????????");
    }};
    public static final String MSG = "msg";


    /**
     * ??????????????????
     * @param punishId
     * @param status
     * @param cgReason
     * @param cgPictures
     */
    public void doAudit(Integer punishId, Integer status, String cgReason, String cgPictures) {
        Map<String, Object> paramMap = Maps.newHashMapWithExpectedSize(5);
        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        paramMap.put("punishId", punishId);
        paramMap.put("status", status);
        paramMap.put("reason", cgReason);
        paramMap.put("cgPictures", cgPictures);
        paramMap.put("updateUserId", currentLoginUser.getId());
        paramMap.put("updateUserName", currentLoginUser.getName());
        OkResponseResult result = OkHttpUtil.getIntance().doPost(Dicts.getString("mp.transport.url") + DO_AUDIT, null, paramMap, "??????????????????");
        getDataString(result);
        log.info("????????????????????????{}", result);
    }


    /**
     * ??????????????????
     * @param punishId
     * @return
     */
    public Map<String, Object> getDriverPunishDetail(Integer punishId, HttpServletRequest request) {
        Map<String, Object> paramMap = Maps.newHashMapWithExpectedSize(1);
        paramMap.put("punishId", punishId);
        OkResponseResult result = OkHttpUtil.getIntance().doGet(Dicts.getString("mp.transport.url") + PUNISH_DETAIL, null, paramMap, "??????????????????");
        JSONObject data = JSONObject.parseObject(getDataString(result));
        //????????????????????????
        List<JSONObject> videoList = JSONObject.parseArray(data.getString("videoList"), JSONObject.class);
        String hostUrl = getRequestHost(request);
        Optional.ofNullable(videoList).ifPresent(e-> e.forEach(jsonObject -> jsonObject.put("soundPath", getRenderFilePath(hostUrl, jsonObject.getString("soundPath")))));

        Map<String, Object> resultMap = new HashMap<>(4);
        resultMap.put("driverPunish", data.getJSONObject("driverPunish"));
        resultMap.put("rocordList", data.getJSONArray("rocordList"));
        resultMap.put("orderVideoVOList", videoList);
        log.info("????????????????????????:{}", JSONObject.toJSONString(resultMap));
        return resultMap;
    }

    private String getRequestHost(HttpServletRequest request) {
        StringBuffer url = request.getRequestURL();
        String s = url.delete(url.length() - request.getRequestURI().length(), url.length()).toString();
        if (s.startsWith(HTTP)) {
            s = "https" + s.substring(4);
        }
        return s;
    }

    /**
     * ??????????????????
     *
     * @param orderNo ??????
     * @return
     */
    public List<PunishRecordVoiceDTO> videoRecordQuery(String orderNo, HttpServletRequest request) {
        Map<String, Object> paramMap = Maps.newHashMapWithExpectedSize(1);
        paramMap.put("orderNo", orderNo);
        OkResponseResult result = OkHttpUtil.getIntance().doGet(Dicts.getString("mp.transport.url") + INIT_VIDEO, null, paramMap, "??????????????????");
        String data = getDataString(result);
        String hostUrl = getRequestHost(request);
        List<PunishRecordVoiceDTO> list = JSON.parseArray(data, PunishRecordVoiceDTO.class);
        return Objects.isNull(list) ? Collections.emptyList() : list
                .stream().filter(e -> StringUtils.isNotBlank(e.getFilePath()))
                .peek(e -> e.setFilePath(getRenderFilePath(hostUrl, e.getFilePath())))
                .collect(Collectors.toList());
    }

    private static String getRenderFilePath(String hostUrl, String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return filePath;
        }
        if(filePath.contains(ALI_YUN_CS_COM)){
            return filePath;
        }
        return hostUrl + RENDER_URL + filePath;
    }

    /**
     * ??????????????????????????????excel
     * @param params
     * @param response
     */
    public void exportExcel(DriverPunishQuery params, HttpServletResponse response) throws Exception {
        log.info("???????????????????????????: {}", params.toString());
        String url = Dicts.getString("mp.transport.url") + PUNISH_EXPORT;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = OkHttpStreamUtil.postJson(url, JSONObject.toJSONString(params));
            if (Objects.nonNull(inputStream)) {
                outputStream = ExportExcelUtil.getOutputStream("??????????????????", response);
                IoUtil.copy(inputStream, outputStream);
                IoUtil.flush(outputStream);
            }
        } catch (Exception e) {
            log.error("??????????????????????????????", e);
            throw e;
        } finally {
            IoUtil.close(inputStream);
            IoUtil.close(outputStream);
        }
    }

    /**
     * ??????????????????
     * @param filePath
     * @param response
     * @throws IOException
     */
    public void renderVideo(String filePath,HttpServletResponse response) throws IOException {
        log.info("====threadName==== ????????????????????????service??????===");
        OutputStream outputStream = null;
        try {
            byte[] fileBytes = OkHttpStreamUtil.executeForBytes(filePath);
            if (Objects.nonNull(fileBytes)) {
                //??????????????????
                int fileLength = fileBytes.length;
                response.addHeader("Accept-Ranges", "bytes");
                response.addHeader("Content-Length", "" + fileLength);
                response.addHeader("Content-Range", "bytes 0-" + fileLength);
                response.addHeader("Content-Type", "audio/mpeg");
                response.addHeader("Content-Length", "" + fileLength);
                outputStream = new BufferedOutputStream(response.getOutputStream());
                outputStream.write(fileBytes);
                outputStream.flush();
            }
        } finally {
            IoUtil.close(outputStream);
        }
    }


    private String getDataString(OkResponseResult result) {
        if (!result.isSuccessful()) {
            throw new ServiceException(RestErrorCode.HTTP_SYSTEM_ERROR, result.getErrorMsg());
        }
        JSONObject responseBody = JSONObject.parseObject(result.getResponseBody());
        if (Objects.isNull(responseBody) || responseBody.getIntValue(CODE) != Constants.SUCCESS_CODE) {
            log.warn("rest result business exception, {}", JSONObject.toJSONString(responseBody));
            throw new ServiceException(RestErrorCode.REST_FAIL_MP_MANAGE_API, Optional.ofNullable(responseBody).map(e -> e.getString(MSG)).orElse("No result"));
        }
        log.info("request result :{}", JSONObject.toJSONString(responseBody));
        return responseBody.getString("data");
    }

    /**
     * ??????????????????
     * @return
     */
    public List<Map<String, Object>> getPunishTypeList() {
        OkResponseResult result = OkHttpUtil.getIntance().doGet(Dicts.getString("mp.transport.url") + PUNISH_TYPE_LIST, null, "??????????????????");
        String data = getDataString(result);
        JSONArray jsonArray = JSONArray.fromObject(data);
        List<Map<String, Object>> list = (List)jsonArray;
        return list;
    }
}
