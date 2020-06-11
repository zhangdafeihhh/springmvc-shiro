package com.zhuanche.common.web.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.sq.common.okhttp.builder.OkHttpRequest;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.serv.authc.MyDataSourceService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.ProxyHttpUtil;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author yanxinping
 * @Date 2020/05/19 09 36
 */

public class ProxyContollerInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ProxyContollerInterceptor.class);


    private static final String HTTP_GET = "GET";

    private static final String EXPORT_FLAG_KEY = "exportFlag";

    private static final String EXPORT_FLAG_VALUE = "1";

    private static final String PROXY_PREFIX = "/enterprise";


    private static final String ATTACHMENT = "attachment";

    private static final String MULTIPART_FILE = "multipart/form-data;";

    private static final String APPLICATION_JSON = "application/json";

    private static final String JSON_PREFIX = ".json";

    private static final String COMPANY_INFO_PREFIX = "companyInfo";

    private Map<String, String> urlDomainMapping = new HashMap<>(16);

    public void setUrlDomainMapping(Map<String, String> urlDomainMapping) {
        this.urlDomainMapping = urlDomainMapping;
    }

    @Autowired
    private MyDataSourceService myDataSourceService;

    /**
     * 图片储存路径
     */
    private static final String FILEPATH = "mp_manage_proxy/img";

    static OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS).build();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        Integer exportFlag = 0;
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        String url = request.getServletPath();
        String method = request.getMethod();
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> params = new HashMap<>(map.size());
        url = url.replace(PROXY_PREFIX, "");
        String[] urlParts = url.split("/");
        String thirdHttpUrl = urlDomainMapping.get(urlParts[1]);
        if(COMPANY_INFO_PREFIX.equals(urlParts[1])){
            url = url.replace(urlParts[1],"").replace(JSON_PREFIX,".html");
        }else{
            url = url.replace(urlParts[1],"").replace(JSON_PREFIX,"");
        }
        if (Objects.nonNull(map)) {
            for (String key : map.keySet()) {
                if (key.equals(EXPORT_FLAG_KEY)) {
                    String value = map.get(EXPORT_FLAG_KEY) != null ? map.get(key)[0].toString() : "";
                    if (EXPORT_FLAG_VALUE.equals(value)) {
                        exportFlag = 1;
                    }
                } else {
                    params.put(key, map.get(key)[0]);
                }
            }
        }
        getUserDataAuthority(params);
        logger.info("转发代理策略工具请求url={} params={}", url, JSON.toJSONString(params));
        JSONObject result = new JSONObject();
        Map<String, Object> headerMap = new HashMap<>(2);
        String conentType = request.getHeader("Content-Type");
        if (StringUtils.isNotBlank(conentType)) {
            headerMap.put("Content-Type", conentType);
        }
        if (HTTP_GET.equals(method)) {
            OkHttpRequest req = new OkHttpRequest.Builder().url(thirdHttpUrl + url).getParams(params).retryTimes(0).tag("转发代理接口").build();
            logger.info("转发代理get请求url={}", req != null ? req.url() : "");
            Response res = ProxyHttpUtil.doGet(req);
            if (exportFlag == Integer.parseInt(EXPORT_FLAG_VALUE)) {
                Headers headers = res.headers();
                logger.info("headers" + headers.toString());
                String ontentDisposition = headers.get("Content-Disposition");
                if (ontentDisposition!=null && !ontentDisposition.contains(ATTACHMENT)) {
                    ontentDisposition = "attachment;  " + ontentDisposition;
                }
                response.setHeader("Content-Disposition", ontentDisposition);
                response.setContentType("application/octet-stream;charset=gbk");
            }
            byte[] buf = new byte[1024];
            int len = -1;
            InputStream inputStream = res.body().byteStream();
            while ((len = inputStream.read(buf)) != -1) {
                response.getOutputStream().write(buf, 0, len);
            }
            response.getOutputStream().flush();
            return false;
        } else {
             if (StringUtils.isNotBlank(conentType) && conentType.contains(MULTIPART_FILE)) {
                 logger.info("转发代理策略工具文件上传POST请求url={}",url);
                // 调用策略工具，支持文件上传 创建一个通用的多部分解析器
                CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
                // 判断 request 是否有文件上传,即多部分请求
                if (multipartResolver.isMultipart(request)) {
                    // 转换request，解析出request中的文件
                    MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
                    // 获取文件map集合
                    Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
                    MultipartFile myfile = fileMap.get("file");
                    result = singleFileUpload(myfile, thirdHttpUrl + url, params);
                }
            } else {
                if (APPLICATION_JSON.equals(conentType)) {
                    logger.info("转发代理策略工具APPLICATION/JSON--POST请求url={}",url);
                    ServletInputStream stream = request.getInputStream();
                    String strcont = strcontBuilding(stream);
                    JSONObject paramJson = JSONObject.parseObject(strcont);
                    String resultString = MpOkHttpUtil.okHttpPostToJson(thirdHttpUrl + url, paramJson.toString(), 0, "首约saas代理接口" + url, headerMap);
                    result = JSONObject.parseObject(resultString);
                } else {
                    logger.info("转发代理策略工具普通--POST请求url={}",url);
                    result = MpOkHttpUtil.okHttpPostBackJson(thirdHttpUrl + url, params, 0, "首约saas代理接口" + url, headerMap);
                }
            }
            logger.info("转发代理策略工具post请求url={} params={}, result={}", url, JSON.toJSONString(params), result);
            response.getWriter().print(result);
            return false;
        }
    }
    public String strcontBuilding( ServletInputStream stream) throws Exception{
        StringBuilder content = new StringBuilder();
        byte[] b = new byte[1024];
        int lens = -1;
        while ((lens = stream.read(b)) > 0) {
            content.append(new String(b, 0, lens));
        }
        return  content.toString();
    }


    public JSONObject singleFileUpload(MultipartFile uploadFile, String url, Map<String, Object> params) {
        JSONObject result = new JSONObject();
        File file = null;
        try {
            // 参数封装
            MediaType type = MediaType.parse("application/octet-stream");
            file = new File(uploadFile.getOriginalFilename());
            // 获取文件名
            String fileName = uploadFile.getOriginalFilename();
            // 获取文件后缀
            String prefix = fileName.substring(fileName.lastIndexOf("."));
            // 用uuid作为文件名，防止生成的临时文件重复
            file = File.createTempFile(UUID.randomUUID().toString(), prefix);
            String strFile = file.toString();
            String uuidFileName = strFile.substring(strFile.lastIndexOf(File.separator) + 1);
            File filex = new File(uuidFileName);
            // MultipartFile to File
            /*uploadFile.transferTo(file);*/
            uploadFile.transferTo(filex);
            RequestBody fileBody = RequestBody.create(type, file);
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            builder.addFormDataPart("filePath", FILEPATH);
            builder.addFormDataPart("file", uploadFile.getOriginalFilename(), fileBody);
            for (Map.Entry<String, Object> param : params.entrySet()) {
                builder.addFormDataPart(param.getKey(), String.valueOf(param.getValue()));
            }
            RequestBody multipartBody = builder.setType(MultipartBody.FORM)
                    .addFormDataPart("filePath", FILEPATH).addFormDataPart("type", "1")
                    .addFormDataPart("file", uploadFile.getOriginalFilename(), fileBody).build();
            Request request = new Request.Builder().url(url).post(multipartBody).build();
            Response response = client.newCall(request).execute();
            String responseStr = response.body().string();
            result = JSON.parseObject(responseStr);
            logger.info("sq_partner_manage文件首约合伙人代理接口返回结果 => {}", result.toJSONString());
            return result;
        } catch (Exception e) {
            logger.error("sq_partner_manage调用首约合伙人代理接口上传文件失败", e);
        } finally {
            File del = new File(file.toURI());
            del.delete();
        }
        return result;
    }

    public void getUserDataAuthority(Map<String, Object> params) {
        logger.info("封装权限信息 params=" + JSON.toJSONString(params));
        boolean authFlag = true;
        if (params != null) {
            for (String key : params.keySet()) {
                if ("cityId".equals(key) && params.get(key) != null && !"".equals(params.get(key)) && Integer.parseInt(params.get(key).toString()) != 0) {
                    authFlag = false;
                }
            }
        }
        /*if (true) {*/
            //暂时不关联页面是否选择城市
            logger.info("页面没有选择城市供应商则自动加上用户账号有的城市");
            SSOLoginUser ssoLoginUser = WebSessionUtil.getCurrentLoginUser();
            SSOLoginUser currentLoginUser = myDataSourceService.getSsoLoginUser(ssoLoginUser.getLoginName());
            logger.info("查询用户信息为" + JSON.toJSONString(currentLoginUser));
            Set<Integer> cityIdsSet = currentLoginUser.getCityIds();
            Set<Integer> supplierIdsSet = currentLoginUser.getSupplierIds();
            if (cityIdsSet != null && cityIdsSet.size() > 0) {
                String cidyIds = Joiner.on(",").join(cityIdsSet);
                params.put("cityIds", cidyIds);
            }
            if (supplierIdsSet != null && supplierIdsSet.size() > 0) {
                String supperlierIds = Joiner.on(",").join(supplierIdsSet);
                params.put("supplierIds", supperlierIds);
            }
       /* }*/

    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
