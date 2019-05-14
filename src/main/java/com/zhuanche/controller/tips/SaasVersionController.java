package com.zhuanche.controller.tips;

import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.entity.mdbcarmanage.CarBizSaasVersion;
import com.zhuanche.serv.mdbcarmanage.service.CarBizSaasVersionService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

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




    @RequestMapping(value = "/createVersionRecord",method = RequestMethod.POST)
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    public AjaxResponse createVersionRecord(String version,String versionSummary,String versionDetail,String cityIds,Date versionTakeEffectDate,
                                            @RequestParam(value = "file",required = false) MultipartFile file,
                                            HttpServletRequest request){

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
            Boolean flag = carBizSaasVersionService.createSaasVersionRecord(record, file, null, request);
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



    @RequestMapping(value = "/editVersion",method = RequestMethod.POST)
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    public AjaxResponse editVersion(String version,String versionSummary,String versionDetail,String cityIds,Date versionTakeEffectDate,
                                    @RequestParam(value = "file",required = false) MultipartFile file,
                                    HttpServletRequest request,Integer versionId, String detailIds){

        LOGGER.info("创建版本更细记录createVersionRecord入参:version={},versionSummary={},versionDetail={},cityIds={}," +
                "versionTakeEffectDate={},versionId={},detailIdList={}",version,versionSummary,versionDetail,cityIds,versionTakeEffectDate,versionId,detailIds);

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
            Boolean flag = carBizSaasVersionService.createSaasVersionRecord(record, file, detailIds, request);
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






}
