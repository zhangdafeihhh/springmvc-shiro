package com.zhuanche.controller.statistic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.rpc.RPCAPI;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.entity.driver.CustomerAppraisal;
import com.zhuanche.entity.driver.DriverAppraisalAppeal;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.serv.deiver.DriverAppraisalAppealService;
import com.zhuanche.serv.deiver.MpDriverCustomerAppraisalService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.MyRestTemplate;
import org.apache.commons.collections.map.HashedMap;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @program: mp-manage
 * @description:
 * @author: niuzilian
 * @create: 2019-04-18 16:05
 **/
@RestController
@RequestMapping("driverAppeal")
public class DriverAppraisalAppealController {
    private static Logger logger = LoggerFactory.getLogger(DriverAppraisalAppealController.class);

    @Autowired
    private DriverAppraisalAppealService driverAppraisalAppealService;

    @Autowired
    private MpDriverCustomerAppraisalService mpDriverCustomerAppraisalService;

    @Autowired
    @Qualifier("wwwApiTemplate")
    private MyRestTemplate wwwApiTemplate;
    //??????????????????
    private final String picturePath = "/upload/public";

    private RPCAPI rpcapi = new RPCAPI();
    @Value("${mp.restapi.url}")
    private String restApiUri;

    @RequestMapping(value = "saveAppealRecord", method = RequestMethod.POST)
    public AjaxResponse saveAppealRecord(@Verify(param = "appraisalId", rule = "required") Integer appraisalId,
                                         @Verify(param = "appealContent", rule = "required") String appealContent,
                                         @Verify(param = "fileURL", rule = "required") String fileURL) {


        List<JSONObject> fileRecord = JSON.parseArray(fileURL, JSONObject.class);
        StringBuilder fileNameSb=new StringBuilder();
        StringBuilder urlSb=new StringBuilder();
        for (JSONObject file:fileRecord) {
            fileNameSb.append(file.getString("fileName")).append(",");
            urlSb.append(file.getString("filePath")).append(",");
        }
        DriverAppraisalAppeal appeal = new DriverAppraisalAppeal();
        appeal.setAppraisalId(appraisalId);
        appeal.setUrl(urlSb.substring(0,urlSb.length()-1));
        appeal.setFileName(fileNameSb.substring(0,fileNameSb.length()-1));
        appeal.setAppealContent(appealContent);
        //??????????????????????????????
        appeal.setAppealStatus(1);
        appeal.setCreateBy(WebSessionUtil.getCurrentLoginUser().getId());
        appeal.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
        appeal.setCreateName(WebSessionUtil.getCurrentLoginUser().getName());
        appeal.setUpdateName(WebSessionUtil.getCurrentLoginUser().getName());
        appeal.setCreateTime(new Date());
        appeal.setUpdateTime(new Date());

        //???????????????????????????????????????????????????????????????
        DriverAppraisalAppeal appealByAppraisalId = driverAppraisalAppealService.getAppealStatusByAppraisalId(appraisalId);
        int result =0;
        if(appealByAppraisalId==null){
            CustomerAppraisal appraisal = mpDriverCustomerAppraisalService.selectByPrimaryKey(appraisalId);
            BeanUtils.copyProperties(appraisal, appeal);
            result= driverAppraisalAppealService.saveSelective(appeal);
        }else{
            if(appealByAppraisalId.getAppealStatus()==4){
                appeal.setId(appealByAppraisalId.getId());
                result = driverAppraisalAppealService.updateSelective(appeal);
            }else{
                return AjaxResponse.failMsg(RestErrorCode.HTTP_PARAM_INVALID,"appraisalId="+appraisalId+" ?????????????????????");
            }
        }
        if(result>0) {
            Integer updateResult = this.updateAppraisal(appraisalId);
            if (updateResult > 0) {
                return AjaxResponse.success(null);
            } else {
                logger.info("????????????????????????????????????????????????isApeal???????????? appraisalId=" + appraisalId);
                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
            }
        }else{
            logger.info("???????????????????????????????????????????????????????????? appraisalId=" + appraisalId);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }
    private int updateAppraisal(Integer appraisalId){
        CustomerAppraisal updateIsAppeal=new CustomerAppraisal();
        updateIsAppeal.setId(appraisalId);
        //????????????????????????
        updateIsAppeal.setIsAlreadyAppeal(1);
        updateIsAppeal.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
        updateIsAppeal.setUpdateAt(new Date());
        return mpDriverCustomerAppraisalService.updateBySelective(updateIsAppeal);
    }

    @RequestMapping(value = "uploadPicture", method = RequestMethod.POST)
    public AjaxResponse uploadPicture(MultipartFile file) {
        if (file == null || file.getSize() == 0) {
            return AjaxResponse.failMsg(RestErrorCode.HTTP_PARAM_INVALID, "??????????????????");
        }
        InputStream in = null;
        try {
            in = file.getInputStream();
            ContentBody contentBody = new InputStreamBody(in, file.getOriginalFilename());
            Map<String, Object> param = new HashMap();
            param.put(file.getOriginalFilename(), contentBody);
            param.put("create_uid", WebSessionUtil.getCurrentLoginUser().getId());
            String result = wwwApiTemplate.postMultipartData(picturePath, String.class, param);
            if (result == null) {
                logger.error("??????????????????????????????,?????????????????????");
                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
            }
            JSONObject response = JSONObject.parseObject(result);
            if (response.getIntValue("code") == 1) {
                return AjaxResponse.success(response.getJSONArray("data").getJSONObject(0));
            }
            logger.info("?????????????????????????????????msg=" + response.getString("msg"));
        } catch (Exception e) {
            logger.error("?????????????????????????????????????????????e:{}", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
    }

    @RequestMapping(value = "getAppealRecordDetail", method = RequestMethod.GET)
    public AjaxResponse getAppealDetail(@Verify(param = "appealId", rule = "required") Integer appealId) {
        Map<String, Object> param = new HashedMap(1);
        String url = restApiUri + "/driverAppeal/getAppealRecordDetail";
        param.put("appealId", appealId);
        String request = MpOkHttpUtil.okHttpGet(url,param,0,null);
       // String request = rpcapi.request(RPCAPI.HttpMethod.GET, url, param, null, "UTF-8");
        logger.info("rest ???????????????????????????????????? msg=" + request);

        if (request == null) {
            logger.error("rest ??????????????????????????????");
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
        return JSON.parseObject(request, AjaxResponse.class);
    }

    @RequestMapping(value = "revokeAppeal")
    public AjaxResponse revokeAppeal(@Verify(param = "appealId", rule = "required") Integer appealId) {
        //?????????????????????
        //?????????????????????????????????
        Integer appealStatus = driverAppraisalAppealService.getAppealStatus(appealId);
        if(appealStatus==null){
            logger.error("???????????????????????????????????????");
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
        if(appealStatus!=1){
            logger.error("?????????????????????????????????????????????appealId="+appealId+" appealStatus="+appealStatus);
            return AjaxResponse.failMsg(RestErrorCode.HTTP_PARAM_INVALID,"?????????????????????????????????");
        }
        int result = driverAppraisalAppealService.revokeAppeal(appealId);
        if (result > 0) {
            return AjaxResponse.success(null);
        } else {
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }
}

