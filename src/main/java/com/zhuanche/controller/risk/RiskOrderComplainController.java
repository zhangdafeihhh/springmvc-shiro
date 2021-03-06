package com.zhuanche.controller.risk;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.entity.risk.RiskCarManagerOrderComplainEntity;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.FtpUtil;
import com.zhuanche.util.FtpUtils;
import com.zhuanche.util.MobileOverlayUtil;
import com.zhuanche.util.MyRestTemplate;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
import java.util.stream.Collectors;

import static com.zhuanche.common.enums.MenuEnum.*;

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
	@RequiresPermissions(value = { "RiskOrder_look" } )
    @RequestFunction(menu = RISK_ORDER_LIST)
    public AjaxResponse incontrolorderDopage(
            @RequestParam(value = "pageNum", required = false,defaultValue = "0")int pageNo,
            @Verify(param = "pageSize",rule = "max(50)")@RequestParam(value = "pageSize", required = false,defaultValue = "20")int pageSize,
            HttpServletRequest request, RiskCarManagerOrderComplainEntity params, ModelMap model) {
        logger.info("??????-???????????????-??????????????????-????????????  RiskCarManagerOrderComplainEntity???"
                + params);
        JSONObject pageData = new JSONObject();
        pageData.put("Rows",new JSONArray());
        pageData.put("Total",0);
        try {
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

            // ??????????????????
            Set<Integer> cityIdsForAuth = new HashSet<>();// ?????????????????????????????????????????????ID
            Set<Integer> supplierIdsForAuth = new HashSet<>();// ????????????????????????????????????????????????ID
            if (!WebSessionUtil.isSupperAdmin()) {// ??????????????????
                // ??????????????????????????????
                SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
                if(null != currentLoginUser){
                    cityIdsForAuth = currentLoginUser.getCityIds();
                    supplierIdsForAuth = currentLoginUser.getSupplierIds();
                }
            }
            // ??????????????????
            String citys = "";
            if (cityIdsForAuth != null && !cityIdsForAuth.isEmpty()){
                citys = cityIdsForAuth.stream().filter(Objects::nonNull).map(Objects::toString).collect(Collectors.joining(","));
            }
            String supplierIds = "";
            if (supplierIdsForAuth != null && !supplierIdsForAuth.isEmpty()){
                supplierIds = supplierIdsForAuth.stream().filter(Objects::nonNull).map(Objects::toString).collect(Collectors.joining(","));
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
                    //TODO ?????????????????????????????????
                    JSONObject retPageData = responseObject.getJSONObject("data");
                    int totalRecord = retPageData.getInteger("totalRecord");
                    JSONArray arrayList = retPageData.getJSONArray("dataList") ;
                    if(totalRecord>0){
                        arrayList.forEach(elem -> {
                            JSONObject jsonData = (JSONObject) elem;
                            jsonData.put("driverPhone", MobileOverlayUtil.doOverlayPhone(jsonData.getString("driverPhone")));
                        });
                    }
                    pageData.put("Rows",arrayList);
                    pageData.put("Total",totalRecord);
                }
            }

        } catch (Exception e) {
            logger.error("??????-??????????????????-??????????????????-??????????????????-???????????? e:{" + e.getMessage()
                    + "},RiskCarManagerOrderComplainEntity???" + (params==null?"null":JSON.toJSONString(params)));
            return AjaxResponse.fail(RestErrorCode.RISK_ORDER_DATA_FAIL,pageData);

        }

        return AjaxResponse.success(pageData);
    }

    @ResponseBody
    @RequestMapping(value = "/detail", method = { RequestMethod.GET })
    @RequestFunction(menu = RISK_ORDER_DETAIL)
    public AjaxResponse detail(@RequestParam("orderNo") String orderNo) {
        logger.info("??????-??????-??????????????????-??????????????????-????????????????????????  orderNo???{" + orderNo + "}");
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

            logger.info("??????-??????-??????????????????-??????????????????-????????????????????????  orderNo???{" + orderNo + "},????????????="+JSON.toJSONString(fieldList));
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
                        repData.put("appealStatus","?????????");
                    }else if(appealStatus == 2){
                        repData.put("appealStatus","?????????");
                    }else if (appealStatus == 3){
                        repData.put("appealStatus","????????????");
                    }else if (appealStatus == 4){
                        repData.put("appealStatus","????????????");
                    }

                    repData.put("appealReason",appealReason);
                    repData.put("remark",remark);
                    repData.put("appealProcessAt",appealProcessAt);
                }
            }
        } catch (Exception e) {
            logger.error("??????-??????????????????-??????????????????-??????,error:{" + e.getMessage()
                    + "},orderNo:{" + orderNo + "}");


            return AjaxResponse.fail(1,repData);
        }
        return AjaxResponse.success(repData);
    }


    /**
     * ????????????
     * @param request
     * @param complainReason
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/submitComplain")
    @ResponseBody
    @RequestFunction(menu = RISK_ORDER_APPEAL)
    public AjaxResponse summitComplain(HttpServletRequest request,
                                 @RequestParam("complainReason") String complainReason,
                                 @RequestParam("orderNo") String orderNo) {
        logger.info("??????-??????????????????-??????????????????-????????????  orderNo???{" + orderNo
                + "},complainReason:{" + complainReason + "}");
        try {
            String reason = URLEncoder.encode(request.getParameter("complainReason"),"UTF-8");
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("orderNo", orderNo);
            paramMap.put("complainReason", reason);
            SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
            paramMap.put("appealCommitBy", currentLoginUser.getName());
             paramMap.put("fileNameList", new ArrayList<>());//?????????????????????
            String result =riskOrderCompalinTemplate.postForObject("/car/manager/order/submitComplain.do",
                    String.class, paramMap);
            JSONObject resultJson = JSON.parseObject(result);
           Integer code = resultJson.getInteger("code");
           if(code != null && code.equals(0)){
               return AjaxResponse.success(null);
           }else{
               return AjaxResponse.fail(RestErrorCode.RISK_SUBMITCOMPLAIN_FAIL,result);
           }


        } catch (Exception e) {
            logger.error("??????-??????????????????-??????????????????,orderNo:{" + orderNo + "}",e);
            return AjaxResponse.fail(RestErrorCode.RISK_SUBMITCOMPLAIN_FAIL,"ERROR");
        }
    }



    /**
     * ????????????
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
                        logger.info("??????-??????????????????-????????????????????? url:"+"/attachment/manager/addAttachment.do");

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
            logger.error("??????-??????????????????-???????????? error:"+e.getMessage()+",orderNo="+orderNo,e);
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
        logger.info("??????-??????????????????-???????????? url:"+url);

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
            logger.info("??????-??????????????????-???????????? url:"+url);
            String result =riskOrderCompalinTemplate.postForObject(url,
                    String.class, paramMap);
            return AjaxResponse.success(null);
        } catch (Exception e) {

            logger.error("??????-??????????????????-???????????? error:"+e.getMessage()+",attachid="+attachid);
            return AjaxResponse.fail(1,"error");
        }
    }


    private String getRemoteFileDir() {
        StringBuilder sb = new StringBuilder();
        sb.append("/upload/").append("risk/order/complain/");
        return sb.toString();
    }
    /**
     * ???????????????
     * @param uploadFile
     */
    public Map<String,Object> upload(MultipartFile uploadFile) {
        Map<String,Object> resultMap = new HashMap<>();
        try {

            String extension = FilenameUtils.getExtension(uploadFile.getOriginalFilename());
            String uuid = UUID.randomUUID().toString();
            String timeStamp = System.currentTimeMillis() + "";
            String filePathDir = getRemoteFileDir();

            //????????????
            ftpUtil.connectServer();
            boolean uploadFlag = ftpUtil.upload(filePathDir, uuid + "_" + timeStamp + "." + extension, uploadFile.getInputStream());
            if (uploadFlag){
                Boolean  ok = true;
                String  absoluteUrl = FtpUtils.getFtpServerUrl() + filePathDir + uuid + "_" + timeStamp + "." + extension;
                String oppositeUrl = filePathDir + uuid + "_" + timeStamp + "." + extension;
                logger.info("??????-??????????????????-absoluteUrl???{" + absoluteUrl + "},oppositeUrl???{" + oppositeUrl + "}");
                resultMap.put("ok",ok);
                resultMap.put("absoluteUrl",absoluteUrl);;
                resultMap.put("oppositeUrl",oppositeUrl);;

            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("??????-??????????????????-?????? error:{"+e.getMessage() +"}");
        }finally {
            ftpUtil.closeConnect();
        }
        return  resultMap;
    }
}
