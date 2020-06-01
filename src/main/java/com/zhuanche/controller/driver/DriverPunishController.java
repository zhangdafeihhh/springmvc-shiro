package com.zhuanche.controller.driver;

import com.github.pagehelper.PageInfo;
import com.zhuanche.common.exception.ServiceException;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.BaseController;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constant.Constants;
import com.zhuanche.entity.bigdata.MaxAndMinId;
import com.zhuanche.entity.driver.DriverAppealRecord;
import com.zhuanche.entity.driver.DriverPunishDto;
import com.zhuanche.entity.rentcar.OrderVideoVO;
import com.zhuanche.serv.punish.DriverPunishService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    public AjaxResponse examineDriverPunish(Integer punishId, Integer status, String reason) {
        if (Objects.isNull(punishId) || Objects.isNull(status)) {
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }
        log.info("司机处罚审核操作 punishId:{},status:{},reason:{}", punishId, status, reason);
        try {
            driverPunishService.doAudit(punishId, status, reason);
            log.info("司机处罚审核操作成功");
            return AjaxResponse.success(null);
        } catch (ServiceException e) {
            log.info("司机处罚审核操作失败", e);
            return AjaxResponse.failMsg(e.getErrorCode(), e.getMessage());
        }
    }


    /**
     * 根据订单查询司机录音
     * @param params
     * @return
     */
    @GetMapping(value = "/initVideoData")
    public AjaxResponse initVideoData(DriverPunishDto params) {
        if (Objects.isNull(params.getOrderNo())) {
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }
        try {
            return AjaxResponse.success(driverPunishService.videoRecordQuery(params.getOrderNo()));
        } catch (ServiceException e) {
            log.error("司机处罚根据订单查询司机录音异常", e);
            return AjaxResponse.fail(e.getErrorCode());
        }
    }


    /**
     * 查询处罚详情
     * @param punishId punishId
     * @return
     */
    @RequestMapping("/getDriverPunishDetail")
    public AjaxResponse getDriverPunishDetail(Integer punishId) {
        if (Objects.isNull(punishId)) {
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }
        Map<String, Object> data = new HashMap<>(4);
        try {
            log.info("查询详情,punishId为--{}", punishId);
            DriverPunishDto driverPunish = driverPunishService.getDetail(punishId);
            List<DriverAppealRecord> recordList = driverPunishService.selectDriverAppealRocordByPunishId(punishId);

            String orderNo = driverPunish.getOrderNo();
            String sign = com.zhuanche.util.Common.MAIN_ORDER_KEY;
            String businessId = com.zhuanche.util.Common.BUSSINESSID;
            List<OrderVideoVO> orderVideoVOList = driverPunishService.getOrderVideoVOList(orderNo, businessId, sign);

            data.put("driverPunish", driverPunish);
            data.put("rocordList", Optional.ofNullable(recordList).orElse(Collections.emptyList()));
            data.put("orderVideoVOList", orderVideoVOList);
            return AjaxResponse.success(data);
        } catch (Exception e) {
            log.error("查询列表出现异常", e);
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

            String endDate = DateUtils.formatDate(new Date(),DateUtils.dateTimeFormat_parttern);
            Date startDate = DateUtils.afterMonth(new Date(),-3);
            String start = DateUtils.formatDate(startDate,DateUtils.dateTimeFormat_parttern);

            MaxAndMinId maxAndMinId = driverPunishService.queryMaxAndMin(start,endDate);
            if(maxAndMinId != null){
                params.setMaxId(maxAndMinId.getMaxId());
                params.setMinId(maxAndMinId.getMinId());
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
            Workbook wb = driverPunishService.exportExcel(rows,request.getSession().getServletContext().getRealPath("/")+ File.separator+"template"+File.separator+"driver_punish.xlsx");
            super.exportExcelFromTemplet(request, response, wb, new String("司机处罚列表".getBytes(StandardCharsets.UTF_8), "iso8859-1"));
        } catch (IOException e) {
            log.error("daochu error", e);
        } catch (Exception e) {
            log.error("daochu error", e);

        }
    }
}
