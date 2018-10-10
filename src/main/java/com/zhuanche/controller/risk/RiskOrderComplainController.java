package com.zhuanche.controller.risk;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.entity.risk.RiskCarManagerOrderComplainEntity;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.FtpUtil;
import com.zhuanche.util.FtpUtils;
import com.zhuanche.util.MyRestTemplate;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.util.*;

@Controller()
@RequestMapping(value = "/risk/incontrolorder")
public class RiskOrderComplainController {

    private static final Logger logger = LoggerFactory.getLogger(RiskOrderComplainController.class);


    @Autowired
    @Qualifier("riskOrderCompalinTemplate")
    private MyRestTemplate riskOrderCompalinTemplate;

    @Autowired
    private FtpUtil ftpUtil;


    @ResponseBody
    @RequestMapping(value = "/dopage", method = { RequestMethod.POST,RequestMethod.GET })
    public AjaxResponse incontrolorderDopage(
            @RequestParam(value = "pageNum", required = false,defaultValue = "0")int pageNo,
            @Verify(param = "pageSize",rule = "max(50)")@RequestParam(value = "pageSize", required = false,defaultValue = "20")int pageSize,
            HttpServletRequest request, RiskCarManagerOrderComplainEntity params, ModelMap model) {
        logger.info("风控-运行中订单-执行分页查询-请求参数  RiskCarManagerOrderComplainEntity："
                + params);
        JSONObject pageData = new JSONObject();
        pageData.put("Rows",new JSONArray());
        pageData.put("Total",0);
        try {
//            SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("orderNo", params.getOrderNo());
            paramMap.put("driverId", params.getDriverId());
            paramMap.put("driverPhone", params.getDriverPhone());
            paramMap.put("orderRiskStatus", params.getOrderRiskStatus());
            paramMap.put("orderEndDate", params.getOrderEndDate());
            paramMap.put("appealProcessAt", params.getAppealProcessAt());
            paramMap.put("cityId", params.getCityId());
            paramMap.put("supplierId", params.getSupplierId());


            paramMap.put("createAtStart", params.getCreateAtStart());
            paramMap.put("createAtEnd", params.getCreateAtEnd());
            paramMap.put("orderEndDateStart", params.getOrderEndDateStart());
            paramMap.put("orderEndDateEnd", params.getOrderEndDateEnd());

            paramMap.put("pageNum", pageNo);
            paramMap.put("pageSize", pageSize);

            // 数据权限设置
            Set<Integer> cityIdsForAuth = new HashSet<Integer>();// 非超级管理员可以管理的所有城市ID
            Set<Integer> supplierIdsForAuth = new HashSet<Integer>();// 非超级管理员可以管理的所有供应商ID
            if (!WebSessionUtil.isSupperAdmin()) {// 非超级管理员
                // 获取当前登录用户信息
                SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
                if(null != currentLoginUser){
                    cityIdsForAuth = currentLoginUser.getCityIds();
                    supplierIdsForAuth = currentLoginUser.getSupplierIds();
                }
            }
            // 查询参数设置
            String citys = "";
            for(Integer cityId : cityIdsForAuth){
                if(StringUtils.isEmpty(citys)){
                    citys += cityId;
                }else {
                    citys += (","+cityId);
                }
            }
            String supplierIds = "";
            for(Integer supplierId : supplierIdsForAuth){
                if(StringUtils.isEmpty(supplierIds)){
                    supplierIds += supplierId;
                }else {
                    supplierIds += (","+supplierId);
                }
            }
            if(StringUtils.isNotEmpty(citys)){
                paramMap.put("citys", citys);
            }
            if(StringUtils.isNotEmpty(supplierIds)){
                paramMap.put("supplierIds", supplierIds);
            }


            String result = riskOrderCompalinTemplate.postForObject("/car/manager/order/page.do",
                    String.class, paramMap);

            if (StringUtils.isNotEmpty(result)) {
                JSONObject responseObject = JSONObject.parseObject(result);

                Integer code = responseObject.getInteger("code");

                if (code == 0) {
                    JSONObject retPageData = responseObject.getJSONObject("data");
                    int totalRecord = retPageData.getInteger("totalRecord");
                    JSONArray arrayList = retPageData.getJSONArray("dataList") ;
                    pageData.put("Rows",arrayList);
                    pageData.put("Total",totalRecord);
                }
            }

        } catch (Exception e) {
            logger.error("风控-风控订单管理-被风控的订单-执行分页查询-服务异常 e:{" + e.getMessage()
                    + "},RiskCarManagerOrderComplainEntity：" + (params==null?"null":JSON.toJSONString(params)));
            return AjaxResponse.fail(RestErrorCode.RISK_ORDER_DATA_FAIL,pageData);

        }

        return AjaxResponse.success(pageData);
    }

    @ResponseBody
    @RequestMapping(value = "/detail", method = { RequestMethod.GET })
    public AjaxResponse detail(@RequestParam("orderNo") String orderNo) {
        logger.info("风控-风控-风控订单管理-被风控的订单-获取订单详情信息  orderNo：{" + orderNo + "}");
        JSONObject repData = new JSONObject();
        try {
            repData.put("appealReason","");
            repData.put("appealProcessAt"," ");
            repData.put("remark"," ");
            repData.put("appealStatus"," ");
            repData.put("orderNo", orderNo);

            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("buinessId", orderNo);

            String fieldResult = riskOrderCompalinTemplate.postForObject("/attachment/manager/findAttachemntList.do",
                    String.class, paramMap);
            JSONArray fieldList = null;
            if (StringUtils.isNotEmpty(fieldResult)) {
                JSONObject responseObject = JSONObject.parseObject(fieldResult);
                Integer code = responseObject.getInteger("code");

                if (code == 0) {
                    fieldList = responseObject.getJSONArray("data");
                }
            }

            logger.info("风控-风控-风控订单管理-被风控的订单-获取订单详情信息  orderNo：{" + orderNo + "},附件列表="+JSON.toJSONString(fieldList));
            repData.put("fileList", fieldList);

            Map<String, Object> param = new HashMap<String, Object>();
            param.put("orderNo", orderNo);
            String resultDetail =riskOrderCompalinTemplate.postForObject("/risk/in/running/getDetailByOrderNo.do",
                    String.class, param);

            if (StringUtils.isNotEmpty(resultDetail)) {
                JSONObject responseObject = JSONObject.parseObject(resultDetail);
                Integer code = responseObject.getInteger("code");

                if (code == 0) {
                    JSONObject repDetailData = responseObject.getJSONObject("data");

                    String appealReason = repDetailData.getString("appealReason");
                    Integer appealStatus = repDetailData.getInteger("appealStatus");
                    String remark = repDetailData.getString("remark");
                    String appealProcessAt = repDetailData.getString("appealProcessAt");
                    if(StringUtils.isNotEmpty(appealProcessAt)){
                        appealProcessAt = appealProcessAt.substring(0,appealProcessAt.indexOf("."));
                    }

                    if (appealStatus == 1){
                        repData.put("appealStatus","未申诉");
                    }else if(appealStatus == 2){
                        repData.put("appealStatus","待处理");
                    }else if (appealStatus == 3){
                        repData.put("appealStatus","申诉成功");
                    }else if (appealStatus == 4){
                        repData.put("appealStatus","申诉驳回");
                    }

                    repData.put("appealReason",appealReason);
                    repData.put("remark",remark);
                    repData.put("appealProcessAt",appealProcessAt);
                }
            }
        } catch (Exception e) {
            logger.error("风控-风控订单管理-获取订单详情-异常,error:{" + e.getMessage()
                    + "},orderNo:{" + orderNo + "}");


            return AjaxResponse.fail(1,repData);
        }
        return AjaxResponse.success(repData);
    }


    /**
     * 提交申诉
     * @param request
     * @param complainReason
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/submitComplain")
    @ResponseBody
    public AjaxResponse summitComplain(HttpServletRequest request,
                                 @RequestParam("complainReason") String complainReason,
                                 @RequestParam("orderNo") String orderNo) {
        logger.info("风控-风控订单管理-执行提交申诉-请求参数  orderNo：{" + orderNo
                + "},complainReason:{" + complainReason + "}");
        try {
            String reason = URLEncoder.encode(request.getParameter("complainReason"),"UTF-8");
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("orderNo", orderNo);
            paramMap.put("complainReason", reason);
            SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
            paramMap.put("appealCommitBy", currentLoginUser.getName());
             paramMap.put("fileNameList", new ArrayList<>());//必须保留的参数
            String result =riskOrderCompalinTemplate.postForObject("/car/manager/order/submitComplain.do",
                    String.class, paramMap);
            JSONObject resultJson = JSON.parseObject(result);
           Integer code = resultJson.getInteger("code");
           if(code != null && code.intValue() == 0){
               return AjaxResponse.success(null);
           }else{
               return AjaxResponse.fail(RestErrorCode.RISK_SUBMITCOMPLAIN_FAIL,result);
           }


        } catch (Exception e) {
            logger.error("风控-风控订单管理-执行提交申诉,orderNo:{" + orderNo + "}",e);
            return AjaxResponse.fail(RestErrorCode.RISK_SUBMITCOMPLAIN_FAIL,"ERROR");
        }
    }



    /**
     * 上传附件
     * @param request
     * @param file
     * @return
     */
    @RequestMapping(value = "/douplaod",method = {RequestMethod.POST})
    @ResponseBody
    public AjaxResponse douplaod(HttpServletRequest request, MultipartFile file) {

        JSONObject jsonObject = new JSONObject();
        String orderNo = request.getParameter("orderNo");
        String fileId = request.getParameter("id");
        try {
            jsonObject.put("id",fileId);
            if (null != file && !file.isEmpty()) {

                MultipartHttpServletRequest req = (MultipartHttpServletRequest) request;
                Map<String, MultipartFile> map = new HashMap<String, MultipartFile>();
                Collection<MultipartFile> files = req.getFileMap().values();
                for (MultipartFile multipartFile : files) {
                    map.put(multipartFile.getName(), multipartFile);
                }
                MultipartFile onemultipartFile;
                for (Map.Entry<String, MultipartFile> entry : map.entrySet()) {
                    onemultipartFile = entry.getValue();
                    Map<String,Object>  result = this.upload(onemultipartFile);
                    Boolean ok = (Boolean) result.get("ok");
                    if (ok== null || !ok){
                        logger.error("风控-上传订单附件-未调用调用风控 url:"+"/attachment/manager/addAttachment.do");

                    }else {
                        String relationship =  this.addAttachmentRelationship(onemultipartFile.getOriginalFilename(),orderNo, (String) result.get("oppositeUrl"));

                        JSONObject responseObject = JSONObject.parseObject(relationship);
                        Integer code = responseObject.getInteger("code");

                        if (code == 0) {
                            Integer relationshipId = responseObject.getInteger("data");
                            jsonObject.put("attachid",relationshipId);
                        }

                    }
                }
            }
            return AjaxResponse.success(jsonObject);
        } catch (Exception e) {
            logger.error("风控-上传订单附件-上传异常 error:"+e.getMessage()+",orderNo="+orderNo,e);
            return AjaxResponse.fail(RestErrorCode.RISK_UPLOAD_FILE_FAIL,"ERROR");
        }
    }
    private String addAttachmentRelationship(String fileName,String orderNo,String oppositeUrl){
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("fileName", fileName);
        paramMap.put("filePath", oppositeUrl);
        paramMap.put("buinessId", orderNo);
        paramMap.put("buinessType", 1);
        paramMap.put("fileSize", 10);
        paramMap.put("fileStatus", 0);
        paramMap.put("status", 1);
        paramMap.put("createEmp", 1);

        String url = "/attachment/manager/addAttachment.do";
        logger.error("风控-上传订单附件-调用风控 url:"+url);

        String result =riskOrderCompalinTemplate.postForObject(url,
                String.class, paramMap);
        return result;
    }


    @RequestMapping(value = "/deleteAttachmentById")
    @ResponseBody
    public AjaxResponse deleteAttachmentById(HttpServletRequest request) {
        String attachid = request.getParameter("attachid");

        try {
            Integer attachmentId = Integer.parseInt(attachid);
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("id", attachmentId);
            String url = "/attachment/manager/deleteAttachmentById.do";
            logger.error("风控-删除订单附件-调用风控 url:"+url);
            String result =riskOrderCompalinTemplate.postForObject(url,
                    String.class, paramMap);
            return AjaxResponse.success(null);
        } catch (Exception e) {

            logger.error("风控-删除订单附件-删除异常 error:"+e.getMessage()+",attachid="+attachid);
            return AjaxResponse.fail(1,"error");
        }
    }


    private String getRemoteFileDir() {
        StringBuilder sb = new StringBuilder();
        sb.append("/upload/").append("risk/order/complain/");
        return sb.toString();
    }
    /**
     * 上传文件类
     * @param uploadFile
     */
    public Map<String,Object> upload(MultipartFile uploadFile) {
//        FtpUtil ftpUtil = FtpUtil.getInstance();
        Map<String,Object> resultMap = new HashMap<>();
        try {

            String extension = FilenameUtils.getExtension(uploadFile.getOriginalFilename());
            String uuid = UUID.randomUUID().toString();
            String timeStamp = System.currentTimeMillis() + "";
            String filePathDir = getRemoteFileDir();

            //开始上传
            ftpUtil.connectServer();
            boolean uploadFlag = ftpUtil.upload(filePathDir, uuid + "_" + timeStamp + "." + extension, uploadFile.getInputStream());
            if (uploadFlag){
                Boolean  ok = true;
                String  absoluteUrl = FtpUtils.getFtpServerUrl() + filePathDir + uuid + "_" + timeStamp + "." + extension;
                String oppositeUrl = filePathDir + uuid + "_" + timeStamp + "." + extension;
                logger.info("风控-上传订单附件-absoluteUrl：{" + absoluteUrl + "},oppositeUrl：{" + oppositeUrl + "}");
                resultMap.put("ok",ok);
                resultMap.put("absoluteUrl",absoluteUrl);;
                resultMap.put("oppositeUrl",oppositeUrl);;

            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("风控-上传订单附件-异常 error:{"+e.getMessage() +"}");
        }finally {
            ftpUtil.closeConnect();
        }
        return  resultMap;
    }
}
