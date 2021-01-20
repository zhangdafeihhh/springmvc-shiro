package com.zhuanche.controller.driver;

import com.github.pagehelper.PageInfo;
import com.zhuanche.common.exception.ServiceException;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.BaseController;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constant.Constants;
import com.zhuanche.entity.driver.DriverPunishDto;
import com.zhuanche.serv.punish.DriverPunishClientService;
import com.zhuanche.serv.punish.query.DriverPunishQuery;
import com.zhuanche.shiro.cache.RedisCacheManager;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 * @Author:qxx
 * @Date:2020/4/9
 * @Description:
 */


@Controller
@RequestMapping("/driverPunish")
public class DriverPunishController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(DriverPunishController.class);

    @Resource
    private DriverPunishClientService driverPunishService;


    @RequestMapping("/getDriverPunishList")
    @ResponseBody
    public AjaxResponse getDriverPunishList(DriverPunishDto params){
        if (Objects.isNull(params.getOrderNo()) && StringUtils.isBlank(params.getPhone()) && StringUtils.isBlank(params.getLicensePlates())) {
            if(params.getCityId() == null){
                log.info("请选择城市");
                return AjaxResponse.fail(RestErrorCode.CHOOSE_CITY);
            }
        }
        try {
            log.info("查询列表,参数为--{}", params.toString());

            SSOLoginUser ssoLoginUser = WebSessionUtil.getCurrentLoginUser();
            if (ssoLoginUser.getSupplierIds() != null && ssoLoginUser.getSupplierIds().size() > 0) {
                Set<Integer> set = ssoLoginUser.getSupplierIds();
                String supplierIds = StringUtils.join(set.toArray(), Constants.SEPERATER);
                params.setSupplierIds(supplierIds);
            }

            PageInfo<DriverPunishDto> page = driverPunishService.selectList(params);
            PageDTO pageDTO = new PageDTO(params.getPage(), params.getPagesize(), page.getTotal(), page.getList());
            return AjaxResponse.success(pageDTO);
        }catch (Exception e){
            log.error("查询列表出现异常", e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    /**
     * 处罚审核操作
     * @param punishId 处罚记录Id
     * @param status 3 - 通过 4 - 拒绝, 5 - 驳回
     * @param reason 原因
     * @return
     */
    @PostMapping(value = "/examineDriverPunish")
    @ResponseBody
    public AjaxResponse examineDriverPunish(Integer punishId, Integer status, String reason,String cgPictures) {
        if (Objects.isNull(punishId) || Objects.isNull(status)) {
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }
        log.info("司机处罚审核操作 punishId:{},status:{},reason:{},cgPictures:{}", punishId, status, reason, cgPictures);
        try {
            driverPunishService.doAudit(punishId, status, reason, cgPictures);
            log.info("司机处罚审核操作成功");
            return AjaxResponse.success(null);
        }  catch (ServiceException e) {
            log.warn("司机处罚审核操作失败", e);
            return AjaxResponse.failMsg(e.getErrorCode(), e.getMessage());
        }
    }


    /**
     * 根据订单查询司机录音
     * @param params
     * @return
     */
    @RequiresPermissions(value = {"punishTripVideo"})
    @GetMapping(value = "/initVideoData")
    @ResponseBody
    public AjaxResponse initVideoData(DriverPunishDto params, HttpServletRequest request) {
        if (Objects.isNull(params.getOrderNo())) {
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }
        try {
            return AjaxResponse.success(driverPunishService.videoRecordQuery(params.getOrderNo(), request));
        } catch (ServiceException e) {
            log.error("司机处罚根据订单查询司机录音异常", e);
            return AjaxResponse.fail(e.getErrorCode());
        }
    }

    /**
     * 渲染行程录音
     * @param filePath
     * @param response
     * @return
     */
    @RequiresPermissions(value = {"punishTripVideo"})
    @RequestMapping("/renderVideo")
    @ResponseBody
    public void render(@RequestParam(name = "filePath") String filePath, HttpServletResponse response) {
        if (StringUtils.isBlank(filePath)) {
            return;
        }
        log.info("渲染录音文件, filePath:{}", filePath);
        try {
            driverPunishService.renderVideo(filePath, response);
        } catch (Exception e) {
            log.error("行程录音渲染失败",e);
        }
    }



    /**
     * 查询处罚详情
     * @param punishId punishId
     * @return
     */
    @RequestMapping("/getDriverPunishDetail")
    @ResponseBody
    public AjaxResponse getDriverPunishDetail(Integer punishId, HttpServletRequest request) {
        if (Objects.isNull(punishId)) {
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }
        try {
            return AjaxResponse.success(driverPunishService.getDriverPunishDetail(punishId, request));
        }  catch (ServiceException e) {
            log.error("司机处罚审核查询失败", e);
            return AjaxResponse.failMsg(e.getErrorCode(), e.getMessage());
        }
    }


    @RequestMapping("/exportDriverPunishList")
    public AjaxResponse export(DriverPunishQuery params, HttpServletResponse response){
        if(params.getCityId() == null){
            log.error("城市为必传项");
            return AjaxResponse.failMsg(RestErrorCode.PARAMS_ERROR, "城市为必传项");
        }
        try {
            //数据层权限
            SSOLoginUser ssoLoginUser = WebSessionUtil.getCurrentLoginUser();
            if (Objects.nonNull(ssoLoginUser) && CollectionUtils.isNotEmpty(ssoLoginUser.getSupplierIds())) {
                Set<Integer> set = ssoLoginUser.getSupplierIds();
                params.setSupplierIds(set);
            }
            String endDate = DateUtils.formatDate(new Date(),DateUtils.dateTimeFormat_parttern);
            Date startDate = DateUtils.afterMonth(new Date(),-3);
            String start = DateUtils.formatDate(startDate,DateUtils.dateTimeFormat_parttern);
            params.setCreateDateStart(start);
            params.setCreateDateEnd(endDate);
            driverPunishService.exportExcel(params, response);
            return AjaxResponse.success(null);
        } catch (Exception e) {
            log.error("driverPunish export error", e);
            return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, e.getMessage());
        }

    }

    /**
     * 司机处罚类型
     * @return
     */
    @RequestMapping("/getPunishTypeList")
    @ResponseBody
    public AjaxResponse getPunishTypeList() {
        try {
            return AjaxResponse.success(driverPunishService.getPunishTypeList());
        }  catch (ServiceException e) {
            log.error("司机处罚类型查询失败", e);
            return AjaxResponse.failMsg(e.getErrorCode(), e.getMessage());
        }
    }
}
