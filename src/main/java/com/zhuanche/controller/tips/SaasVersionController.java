package com.zhuanche.controller.tips;

import com.alibaba.fastjson.JSON;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.sms.SmsSendUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.entity.busManage.BusOrderDetail;
import com.zhuanche.entity.mdbcarmanage.CarBizSaasVersion;
import com.zhuanche.entity.mdbcarmanage.CarBizSaasVersionDetail;
import com.zhuanche.serv.mdbcarmanage.service.CarBizSaasVersionService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.DateUtil;
import com.zhuanche.util.DateUtils;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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


    /**
     * 创建版本记录及上传附件接口
     * @param version
     * @param versionSummary
     * @param versionDetail
     * @param cityIds
     * @param versionTakeEffectDate
     * @param file
     * @param request
     * @return
     */
    @RequestMapping(value = "/createVersionRecord",method = RequestMethod.POST)
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    public AjaxResponse createVersionRecord(String version,String versionSummary,String versionDetail,String cityIds,Date versionTakeEffectDate){

        LOGGER.info("创建版本更细记录createVersionRecord入参:version={},versionSummary={},versionDetail={},cityIds={},versionTakeEffectDate={}",version,versionSummary,versionDetail,cityIds,versionTakeEffectDate);
        if(StringUtils.isBlank(version) || StringUtils.isBlank(versionSummary)|| StringUtils.isBlank(versionDetail) || StringUtils.isBlank(cityIds) || versionTakeEffectDate == null){
            LOGGER.info("创建版本更新记录参数错误 version={}",version);
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }

        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        Integer userId = loginUser.getId();
        CarBizSaasVersion record = new CarBizSaasVersion();
        record.setVersion(version);
        record.setVersionSummary(versionSummary);
        record.setVersionDetail(versionDetail);
        record.setVersionTakeEffectDate(versionTakeEffectDate);
        record.setCityId(cityIds);
        record.setCreateUserid(userId);
        //调用创建版本记录及附件接口
        try {
            Boolean flag = carBizSaasVersionService.saveOrUpdateVersion(record);
            if (flag) {
                //发送短信   根据城市id集合获取下属所有加盟商联系人  发送版本更新短信提醒
                LOGGER.info("发送短信");

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
     * @param file
     * @param request
     * @param versionId
     * @param detailIds
     * @return
     */
    @RequestMapping(value = "/editVersion",method = RequestMethod.POST)
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    public AjaxResponse editVersion(String version,String versionSummary,String versionDetail,String cityIds,Date versionTakeEffectDate,Integer versionId){

        LOGGER.info("创建版本更细记录createVersionRecord入参:version={},versionSummary={},versionDetail={},cityIds={}," +
                "versionTakeEffectDate={},versionId={},detailIdList={}",version,versionSummary,versionDetail,cityIds,versionTakeEffectDate,versionId);

        if(StringUtils.isBlank(version) || StringUtils.isBlank(versionSummary)|| StringUtils.isBlank(versionDetail)
                || StringUtils.isBlank(cityIds) || versionTakeEffectDate == null || versionId == null){
            LOGGER.info("更新版本更新记录参数错误 version={}",version);
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }

        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        Integer userId = loginUser.getId();
        CarBizSaasVersion record = new CarBizSaasVersion();
        record.setId(versionId);
        record.setVersion(version);
        record.setVersionSummary(versionSummary);
        record.setVersionDetail(versionDetail);
        record.setVersionTakeEffectDate(versionTakeEffectDate);
        record.setCityId(cityIds);
        record.setCreateUserid(userId);
        record.setUpdateDate(new Date());

        try {
            Boolean flag = carBizSaasVersionService.saveOrUpdateVersion(record);
            if (flag) {
                //发送短信   根据城市id集合获取下属所有加盟商联系人  发送版本更新短信提醒
                LOGGER.info("更新成功");
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















    @RequestMapping(value = "/deletetest",method = RequestMethod.POST)
    @RequiresPermissions(value = {"SupplierTipsCreate"})
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    public void delete(){




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
            String fileName = request.getScheme()+"://"+ request.getServerName()+"/saasVersion/getImg?fileName="+fileUrl;
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









    private Map<Object, Object> sendMessage(BusOrderDetail beforeBusOrder, BusOrderDetail afterBusOrder) {
        Map<Object, Object> result = new HashMap<Object, Object>();
        try {
            // 预订上车时间
            Date bookDate = beforeBusOrder.getBookingDate();
            String bookingDate = DateUtils.formatDate(bookDate, DateUtil.LOCAL_FORMAT);

            Date date = new Date();
            long difference = bookDate.getTime() - date.getTime();
            double subResult = difference * 1.0 / (1000 * 60 * 60);
            if (subResult <= 24) {
                // 预订人手机
                String riderPhone = beforeBusOrder.getRiderPhone();
                // 取消的司机手机号
                String beforeDriverPhone = beforeBusOrder.getDriverPhone();
                // 改派后司机姓名
                String driverName = afterBusOrder.getDriverName();
                // 改派后司机手机号
                String afterDriverPhone = afterBusOrder.getDriverPhone();
                // 改派后车牌号
                String licensePlates = afterBusOrder.getLicensePlates();
                // 预订上车地点
                String bookingStartAddr = beforeBusOrder.getBookingStartAddr();
                // 预订下车地点
                String bookingEndAddr = beforeBusOrder.getBookingEndAddr();

                String driverContext = "订单，" + bookingDate + "有乘客从" + bookingStartAddr + "到" + bookingEndAddr;
                String beforeDriverContext = "尊敬的师傅您好，您的巴士指派" + driverContext + "，已被改派取消。";
                String afterDriverContext = "尊敬的师傅您好，接到巴士服务" + driverContext + "，请您按时接送。";
                String riderContext = "尊敬的用户您好，您预订的" + bookingDate + "的巴士服务订单已被改派成功，司机" + driverName + "，" + afterDriverPhone + "，车牌号" + licensePlates + "，将竭诚为您服务。";

                // 乘客
                SmsSendUtil.send(riderPhone, riderContext);
                // 取消司机
                SmsSendUtil.send(beforeDriverPhone, beforeDriverContext);
                // 改派司机
                SmsSendUtil.send(afterDriverPhone, afterDriverContext);
            } else {
                LOGGER.info("巴士改派司机-大于等于24小时，无需发送短信: orderNo = " + beforeBusOrder.getOrderNo() + ", bookingDate = " + bookingDate);
            }
        } catch (Exception e) {
            LOGGER.error("巴士改派发送短信异常.", e);
        }
        return result;
    }

    private String imgFileDir() {
        StringBuilder sb = new StringBuilder();
        sb.append(File.separator).append("u01").append(File.separator).append("upload").append(File.separator).append("saasVersion")
                .append(File.separator);
        return sb.toString();
    }




}
