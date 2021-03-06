package com.zhuanche.controller.tips;

import com.alibaba.fastjson.JSON;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.sms.SmsSendUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constant.Constants;
import com.zhuanche.constants.SmsTempleConstants;
import com.zhuanche.entity.mdbcarmanage.CarBizSaasVersion;
import com.zhuanche.entity.mdbcarmanage.CarBizSaasVersionDetail;
import com.zhuanche.serv.CarBizSupplierService;
import com.zhuanche.serv.mdbcarmanage.service.CarBizSaasVersionService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: nysspring@163.com
 * @Description: saas 系统版本更新记录
 * @Date: 19:08 2019/5/13
 */
@Controller
@RequestMapping("/saasVersion")
public class SaasVersionController {

    private Logger LOGGER = LoggerFactory.getLogger(SaasVersionController.class);

    @Autowired
    private CarBizSaasVersionService carBizSaasVersionService;

    @Autowired
    private CarBizSupplierService carBizSupplierService;


    /**
     * 创建版本记录及上传附件接口
     * @param version
     * @param versionSummary
     * @param versionDetail
     * @param cityIds
     * @param versionTakeEffectDate
     * @return
     */
    @RequestMapping(value = "/createVersionRecord",method = RequestMethod.POST)
    @RequiresPermissions(value = {"VersionUpdateRecordCreate"})
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    public AjaxResponse createVersionRecord(String version,String versionSummary,String versionDetail,String cityIds,String versionTakeEffectDate){

        LOGGER.info("创建版本更细记录createVersionRecord入参:version={},versionSummary={},versionDetail={},cityIds={},versionTakeEffectDate={}",version,versionSummary,versionDetail,cityIds,versionTakeEffectDate);
        if(StringUtils.isBlank(version) || StringUtils.isBlank(versionSummary)|| StringUtils.isBlank(versionDetail) || StringUtils.isBlank(cityIds) || StringUtils.isBlank(versionTakeEffectDate)){
            LOGGER.info("创建版本更新记录参数错误 version={}",version);
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }
        if(versionTakeEffectDate.length() != 10){
//            String substring = versionTakeEffectDate.substring(4, 5);
//            String substring1 = versionTakeEffectDate.substring(7, 8);
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date takeEffectDate = null;
        try {
            takeEffectDate = sdf.parse(versionTakeEffectDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }
        if(takeEffectDate == null){
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }

        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        Integer userId = loginUser.getId();
        CarBizSaasVersion record = new CarBizSaasVersion();
        record.setVersion(version);
        record.setVersionSummary(versionSummary);
        record.setVersionDetail(versionDetail);
        record.setVersionTakeEffectDate(takeEffectDate);
        record.setCityId(cityIds);
        record.setCreateUserid(userId);
        //调用创建版本记录及附件接口
        try {
            Boolean flag = carBizSaasVersionService.saveOrUpdateVersion(record);
            if (flag) {
//                发送短信   根据城市id集合获取下属所有加盟商联系人  发送版本更新短信提醒
                /**
                 * 【首汽约车】（联系人姓）老板您好，首约加盟商服务平台刚于2019-05-06进行了V_1.7.0版本更新，本次更新主要包括：订单查询、司机查询等功能优化以及问题修复，访问版本记录可以查看完整更新说明，如有任何问题欢迎反馈。您的满意，我们的动力！
                 */
                String[] split = cityIds.split(Constants.SEPERATER);
                List<Integer> list = new ArrayList<>();
                for(int i = 0;i<split.length;i++){
                    list.add(Integer.parseInt(split[i]));
                }
                LOGGER.info("发送短信list={}",list);

                Map<String,String> msgMap = new HashMap<>();
                if(list != null && !list.isEmpty()){
                    for (Integer cityId : list){
                        List<Map<String, String>> mapList = carBizSaasVersionService.selectContactsSendMsg(cityId);
                        if(mapList != null && !mapList.isEmpty()){
                            for(Map<String,String> map : mapList){
                                String cities = map.get("cities");
                                String[] split1 = cities.split(Constants.SEPERATER);
                                List<String> list1 = Arrays.asList(split1);
                                if(list1.contains(String.valueOf(cityId))){
                                    String name = map.get("name");
                                    String phone = map.get("phone");
                                    msgMap.put(phone,name);
                                }
                            }
                        }
                    }

                    if(msgMap == null || msgMap.isEmpty()){
                        LOGGER.info("当前城市id没有对应联系人cityidList={}",list);
                        return AjaxResponse.success(null);
                    }

                    for (Map.Entry<String,String> entry : msgMap.entrySet()){
                        String contactPhone = entry.getKey();
                        String contactName = entry.getValue();
                        if(StringUtils.isNotBlank(contactName) && StringUtils.isNotBlank(contactPhone) && contactPhone.length() == 11){
                            List listTemple = new ArrayList();
                            listTemple.add(versionTakeEffectDate);
                            listTemple.add(version);
                            listTemple.add(versionSummary);
                            SmsSendUtil.sendTemplate(contactPhone, SmsTempleConstants.versionUpdateTemple,listTemple);
                            LOGGER.info("发送短信成功contactName={},contactPhone={}",contactName,contactPhone);
                        }
                    }
                }

            }
        }catch (Exception e){
            LOGGER.error("创建版本记录异常 e={}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
        return AjaxResponse.success(null);
    }


    /**
     * 编辑版本记录及附件接口
     * @param version
     * @param versionSummary
     * @param versionDetail
     * @param cityIds
     * @param versionTakeEffectDate
     * @param versionId
     * @return
     */
    @RequestMapping(value = "/editVersion",method = RequestMethod.POST)
    @RequiresPermissions(value = {"VersionUpdateRecordEdit"})
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    public AjaxResponse editVersion(String version,String versionSummary,String versionDetail,String cityIds,String versionTakeEffectDate,Integer versionId){

        LOGGER.info("创建版本更细记录createVersionRecord入参:version={},versionSummary={},versionDetail={},cityIds={}," +
                "versionTakeEffectDate={},versionId={},detailIdList={}",version,versionSummary,versionDetail,cityIds,versionTakeEffectDate,versionId);

        if(StringUtils.isBlank(version) || StringUtils.isBlank(versionSummary)|| StringUtils.isBlank(versionDetail)
                || StringUtils.isBlank(cityIds) || StringUtils.isBlank(versionTakeEffectDate) || versionId == null){
            LOGGER.info("更新版本更新记录参数错误 version={}",version);
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date takeEffectDate = null;
        try {
            takeEffectDate = sdf.parse(versionTakeEffectDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }
        if(takeEffectDate == null){
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }

        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        Integer userId = loginUser.getId();
        CarBizSaasVersion record = new CarBizSaasVersion();
        record.setId(versionId);
        record.setVersion(version);
        record.setVersionSummary(versionSummary);
        record.setVersionDetail(versionDetail);
        record.setVersionTakeEffectDate(takeEffectDate);
        record.setCityId(cityIds);
        record.setCreateUserid(userId);
        record.setUpdateDate(new Date());

        try {
            Boolean flag = carBizSaasVersionService.saveOrUpdateVersion(record);
            if (flag) {
                //发送短信   根据城市id集合获取下属所有加盟商联系人  发送版本更新短信提醒
                LOGGER.info("更新成功 versionId={}",versionId);
            }
        }catch (Exception e){
            LOGGER.error("创建版本记录异常 e={}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
        return  AjaxResponse.success(null);
    }


    /**
     * 版本记录列表
     * @param pageSize
     * @param pageNum
     * @return
     */
    @RequestMapping(value = "/listVersion")
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public AjaxResponse listVersion(@RequestParam(value = "pageSize",required = false,defaultValue = "10")Integer pageSize,
                                 @RequestParam(value = "pageNum",required = false,defaultValue = "1")Integer pageNum){
        LOGGER.info("listVersion入参：pageSize:{},pageNum:{}",pageSize,pageNum);

        try {
            PageDTO pageDTO = carBizSaasVersionService.listVersion(pageNum,pageSize);
            return AjaxResponse.success(pageDTO);
        } catch (Exception e) {
            LOGGER.error("查询版本更新记录列表异常e={}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }


    /**
     * 版本记录详情
     * @return
     */
    @RequestMapping(value = "/versionDetail")
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public AjaxResponse versionDetail(Integer versionId){
        LOGGER.info("versionDetail入参：versionId:{}",versionId);

        try {
            CarBizSaasVersion carBizSaasVersion = carBizSaasVersionService.selectVersionById(versionId);
            return AjaxResponse.success(carBizSaasVersion);
        } catch (Exception e) {
            LOGGER.error("查询版本更新记录列表异常e={}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }



    @RequestMapping(value="/downDetail")
    public ResponseEntity<byte[]> downDetail(@RequestParam("detailId") Integer detailId)
            throws Exception {
        //下载文件路径
        try {
            CarBizSaasVersionDetail carBizSaasVersionDetail = carBizSaasVersionService.selectDetailById(detailId);
            if(carBizSaasVersionDetail != null){
                String fileUrl = carBizSaasVersionDetail.getDetailUrl();
                String fileName = carBizSaasVersionDetail.getDetailName();
//                String path = "";  //服务器

//                File file = new File(path + File.separator + fileUrl);
                File file = new File("D:" + File.separator + fileUrl);
                HttpHeaders headers = new HttpHeaders();
                String downloadFielName = new String(fileName.getBytes("UTF-8"));
                String docName = downloadFielName.substring(downloadFielName.lastIndexOf(File.separator)+1);
                headers.add("Content-Disposition", "attchement;filename="+ URLEncoder.encode(fileName,"UTF-8"));
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file),
                        headers, HttpStatus.CREATED);

            }

        } catch (IOException e) {
            LOGGER.info("下载错误：" + e.getMessage());
        }
        return null;
    }







    /**
     * 删除版本记录及附件
     * @param versionId
     * @return
     */
    @RequestMapping(value = "/deleteVersion",method = RequestMethod.POST)
    @RequiresPermissions(value = {"VersionUpdateRecordDelete"})
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    public AjaxResponse deleteVersion(Integer versionId){
        LOGGER.info("删除版本更新记录  versionId={}",versionId);

        if(versionId == null){
            LOGGER.info("deleteVersion参数null");
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }

        try {
            Boolean aBoolean = carBizSaasVersionService.deleteVersion(versionId);
            LOGGER.info("删除结果 versionId={},aBoolean={}",versionId,aBoolean);
            return AjaxResponse.success(null);
        } catch (Exception e) {
            LOGGER.error("删除版本更新记录异常 e={}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    @RequestMapping(value = "/uploadImg",method = RequestMethod.POST)
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    public AjaxResponse uploadImg(MultipartFile file, HttpServletRequest request){

        if(file == null){
            LOGGER.info("上传的图片为null date={}",new Date());
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }
        //调用创建版本记录及附件接口
        try {
            String fileUrl = carBizSaasVersionService.uploadImg(file,request,imgFileDir());
            String fileName = "https://"+ request.getServerName()+"/saasVersion/getImg.json?fileName="+fileUrl;
            return AjaxResponse.success(fileName);
        }catch (Exception e){
            LOGGER.error("上传的图片异常 e={}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    @RequestMapping(value = "/getImg",method = RequestMethod.GET)
    public String getImg(HttpServletResponse response, String fileName){
        Map<String,Object> resultMap = new HashMap<>();
        try {
                String path = "";  //服务器
//                File file = new File(path + File.separator + fileUrl);
//windows            File file = new File("D:" + File.separator + imgFileDir() + fileName);
            File file = new File(path + imgFileDir() + fileName);
            byte[] bytes = FileUtils.readFileToByteArray(file);

            response.setContentType("image/jpg");
            OutputStream output = response.getOutputStream();
            InputStream in = new ByteArrayInputStream(bytes);
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf)) != -1){
                output.write(buf,0,len);
            }
            output.flush();
            output.close();
            resultMap.put("SUCCESS",true);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return JSON.toJSONString(resultMap);
    }


    private String imgFileDir() {
        StringBuilder sb = new StringBuilder();
        sb.append(File.separator).append("u01").append(File.separator).append("upload").append(File.separator).append("saasVersion")
                .append(File.separator);
        return sb.toString();
    }




}
