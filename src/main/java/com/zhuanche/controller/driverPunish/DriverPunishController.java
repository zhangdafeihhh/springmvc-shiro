package com.zhuanche.controller.driverPunish;

import com.github.pagehelper.PageInfo;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.BaseController;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constant.Constants;
import com.zhuanche.entity.driver.DriverAppealRecord;
import com.zhuanche.entity.driver.DriverPunishDto;
import com.zhuanche.serv.driverPunish.DriverPunishService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @Author:qxx
 * @Date:2020/4/9
 * @Description:
 */


@RestController
@RequestMapping("/driverPunish")
public class DriverPunishController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(DriverPunishController.class);

    @Autowired
    private DriverPunishService driverPunishService;


    @RequestMapping("/getDriverPunishList")
    public AjaxResponse getDriverPunishList(DriverPunishDto params){


        if(params.getCityId() == null){
            log.info("请选择城市");
            return AjaxResponse.fail(RestErrorCode.CHOOSE_CITY);

        }
        try {
            log.info("查询列表,参数为--{}", params.toString());

            SSOLoginUser ssoLoginUser = WebSessionUtil.getCurrentLoginUser();
            if(ssoLoginUser.getSupplierIds() != null && ssoLoginUser.getSupplierIds().size()>0 ){
                Set<Integer> set = ssoLoginUser.getSupplierIds();
                String supplierIds = StringUtils.join(set.toArray(), Constants.SEPERATER);
                params.setSupplierIds(supplierIds);
            }

            PageInfo<DriverPunishDto> page = driverPunishService.selectList(params);
            PageDTO pageDTO = new PageDTO(params.getPage(), params.getPagesize(), page.getTotal(), page.getList());
            return AjaxResponse.success(pageDTO);
        }catch (Exception e){
            log.error("查询列表出现异常-{}", e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    @RequestMapping("/getDriverPunishDetail")
    public AjaxResponse getDriverPunishDetail(Integer punishId){
        try {
            Map<String, Object> data = new HashMap<>();

            log.info("查询详情,punishId为--{}", punishId);
            DriverPunishDto driverPunish = driverPunishService.getDetail(punishId);
            data.put("driverPunish", driverPunish);
            List<DriverAppealRecord> rocordList = driverPunishService.selectDriverAppealRocordByPunishId(punishId);
            if(rocordList==null){
                rocordList = new ArrayList<DriverAppealRecord>();
            }
            data.put("rocordList", rocordList);
            return AjaxResponse.success(data);
        }catch (Exception e){
            log.error("查询列表出现异常-{}", e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    @RequestMapping("/exportDriverPunishList")
    public void daochu(DriverPunishDto params, HttpServletRequest request, HttpServletResponse response){

        try {
            log.info("导出列表,参数为--{}", params.toString());
            //数据层权限
            if(params.getCityId() == null){
                SSOLoginUser ssoLoginUser = WebSessionUtil.getCurrentLoginUser();
                if(CollectionUtils.isNotEmpty(ssoLoginUser.getSupplierIds())){
                    Set<Integer> set = ssoLoginUser.getSupplierIds();
                    String supplierIds = StringUtils.join(set.toArray(), Constants.SEPERATER);
                    params.setSupplierIds(supplierIds);
                }else if(CollectionUtils.isNotEmpty(ssoLoginUser.getCityIds())){
                    Set<Integer> set = ssoLoginUser.getCityIds();
                    String cityIds = StringUtils.join(set.toArray(),Constants.SEPERATER);
                    params.setCities(cityIds);
                }else  {
                    log.info("======数据权限过大,无法导出=======");
                    return;
                }
            }
            
            Integer pageIndex =1;
            params.setPagesize(200);
            params.setPage(pageIndex);
            List<DriverPunishDto> rows = new ArrayList<>();
            PageInfo<DriverPunishDto> page = driverPunishService.selectList(params, false);
            while (page.getList() != null && page.getList().size() != 0){
                rows.addAll(page.getList());
                pageIndex++;
                params.setPage(pageIndex);

                page = driverPunishService.selectList(params);
            }
            Workbook wb = driverPunishService.exportExcel(rows,request.getRealPath("/")+ File.separator+"template"+File.separator+"driver_punish.xlsx");
            super.exportExcelFromTemplet(request, response, wb, new String("司机处罚列表".getBytes("utf-8"), "iso8859-1"));
        } catch (IOException e) {
            log.error("daochu error", e);
        } catch (Exception e) {
            log.error("daochu error", e);

        }
    }
}
