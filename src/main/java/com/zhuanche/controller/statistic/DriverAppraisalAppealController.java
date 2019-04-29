package com.zhuanche.controller.statistic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.rpc.RPCAPI;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.entity.driver.CustomerAppraisal;
import com.zhuanche.entity.driver.DriverAppraisalAppeal;
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
    //上传图片路径
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
        //默认已申诉（待审核）
        appeal.setAppealStatus(1);
        appeal.setCreateBy(WebSessionUtil.getCurrentLoginUser().getId());
        appeal.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
        appeal.setCreateName(WebSessionUtil.getCurrentLoginUser().getName());
        appeal.setUpdateName(WebSessionUtil.getCurrentLoginUser().getName());
        appeal.setCreateTime(new Date());
        appeal.setUpdateTime(new Date());

        //查询是否是撤销状态，是改变状态，否重新添加
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
                return AjaxResponse.failMsg(RestErrorCode.HTTP_PARAM_INVALID,"appraisalId="+appraisalId+" 不是已撤销状态");
            }
        }
        if(result>0) {
            Integer updateResult = this.updateAppraisal(appraisalId);
            if (updateResult > 0) {
                return AjaxResponse.success(null);
            } else {
                logger.error("司机保存申诉记录接口，修改评分表isApeal状态失败 appraisalId=" + appraisalId);
                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
            }
        }else{
            logger.error("司机保存申诉记录接口，修改申诉记录表失败 appraisalId=" + appraisalId);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }
    private int updateAppraisal(Integer appraisalId){
        CustomerAppraisal updateIsAppeal=new CustomerAppraisal();
        updateIsAppeal.setId(appraisalId);
        //表示改数据已申诉
        updateIsAppeal.setIsAlreadyAppeal(1);
        updateIsAppeal.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
        updateIsAppeal.setUpdateAt(new Date());
        return mpDriverCustomerAppraisalService.updateBySelective(updateIsAppeal);
    }

    @RequestMapping(value = "uploadPicture", method = RequestMethod.POST)
    public AjaxResponse uploadPicture(MultipartFile file) {
        if (file == null || file.getSize() == 0) {
            return AjaxResponse.failMsg(RestErrorCode.HTTP_PARAM_INVALID, "文件不能为空");
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
                logger.error("传入司机申诉附件错误,未获取到响应值");
                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
            }
            JSONObject response = JSONObject.parseObject(result);
            if (response.getIntValue("code") == 1) {
                return AjaxResponse.success(response.getJSONArray("data").getJSONObject(0));
            }
            logger.error("传入司机申诉附件错误，msg=" + response.getString("msg"));
        } catch (Exception e) {
            logger.error("传入司机申诉附件，接口调用异常e:{}", e);
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
        String request = rpcapi.request(RPCAPI.HttpMethod.GET, url, param, null, "UTF-8");
        logger.info("rest 查询司机申诉详情查询结果 msg=" + request);

        if (request == null) {
            logger.error("rest 查询司机申诉详情异常");
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
        return JSON.parseObject(request, AjaxResponse.class);
    }

    @RequestMapping(value = "revokeAppeal")
    public AjaxResponse revokeAppeal(@Verify(param = "appealId", rule = "required") Integer appealId) {
        //查一下审核状态
        //如果不是待审核不能撤销
        Integer appealStatus = driverAppraisalAppealService.getAppealStatus(appealId);
        if(appealStatus==null){
            logger.error("撤销申诉，查询申诉状态错误");
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
        if(appealStatus!=1){
            logger.error("撤销申诉，申诉状态不是待审核，appealId="+appealId+" appealStatus="+appealStatus);
            return AjaxResponse.failMsg(RestErrorCode.HTTP_PARAM_INVALID,"只有待审核状态才能撤销");
        }
        int result = driverAppraisalAppealService.revokeAppeal(appealId);
        if (result > 0) {
            return AjaxResponse.success(null);
        } else {
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }
}

