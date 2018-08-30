package com.zhuanche.controller.rentcar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.dto.rentcar.DriverOutageDTO;
import com.zhuanche.entity.rentcar.DriverOutage;
import com.zhuanche.serv.rentcar.DriverOutageService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * ClassName: DriverOutageController 
 * date: 2017年5月4日 下午7:19:45 
 *
 * @author zhulingling
 * @version
 * @since JDK 1.6 
 */
@RestController
@RequestMapping("/driverOutage")
public class DriverOutageController {

    private static final Logger logger = LoggerFactory.getLogger(DriverOutageController.class);

    @Autowired
    private DriverOutageService driverOutageService;

//    @Autowired
//    private LogService logService;

//    @AuthPassport
//    @RequestMapping(value = "/queryDriverOutage", method = { RequestMethod.GET })
//    public String queryDriverOutage(ModelMap model, DriverOutage params){
//        try {
//            logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"【司机停运】司机停运列表");
//        } catch (Exception e) {
//        }
//        logger.info("【司机停运】queryDriverOutage:司机停运列表");
//        return "driverOutage/driverOutageList";
//    }
//
//    @AuthPassport
//    @RequestMapping(value = "/queryDriverOutageNo", method = { RequestMethod.GET })
//    public String queryDriverOutageNo(ModelMap model, DriverOutage params){
//        try {
//            logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"【司机停运】司机停运列表");
//        } catch (Exception e) {
//        }
//        logger.info("【司机停运】queryDriverOutageNo:司机停运列表");
//        return "driverOutage/driverOutageListNo";
//    }

    /**
     * 查询临时停运司机列表
     * @param cityId    城市
     * @param supplierId    供应商
     * @param carGroupId    服务类型
     * @param outageSource  停运来源
     * @param driverName    司机姓名
     * @param driverPhone   手机号
     * @param startDateBegin    停运开始日期
     * @param startDateEnd      停运结束日期
     * @param removeStatus      解除状态
     * @return
     */
    @GetMapping(value = "/queryDriverOutageData.json")
    public AjaxResponse queryDriverOutageData(Integer cityId,
                                              Integer supplierId,
                                              Integer carGroupId,
                                              Integer outageSource,
                                              String driverName,
                                              String driverPhone,
                                              String startDateBegin,
                                              String startDateEnd,
                                              Integer removeStatus,
                                              Integer page,
                                              Integer pageSize){
//        try {
//            logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"【司机停运】司机停运列表数据");
//        } catch (Exception e) {
//        }

        logger.info("【司机停运】司机停运列表数据:queryDriverOutageData.json");
        DriverOutage params = new DriverOutage();
        params.setCityId(cityId);
        params.setSupplierId(supplierId);
        params.setCarGroupId(carGroupId);
        params.setOutageSource(outageSource);
        params.setDriverName(driverName);
        params.setDriverPhone(driverPhone);
        params.setStartDateBegin(startDateBegin);
        params.setStartDateEnd(startDateEnd);
        params.setRemoveStatus(removeStatus);
        params.setPage(page);
        params.setPagesize(pageSize);

        List<DriverOutage> rows = new ArrayList<>();
        List<DriverOutageDTO> data = Lists.newArrayList();
        int total = 0;
        //权限
        String cities = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getCityIds(), ",");
        String suppliers = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getSupplierIds(),",");
        params.setCities(cities);
        params.setSupplierIds(suppliers);
        //查数量
        total = driverOutageService.queryForInt(params);
        if(total==0){
            PageDTO result = new PageDTO(params.getPage(), params.getPagerSize(), 0, data);
            return AjaxResponse.success(result);
        }
        //查数据
        rows = driverOutageService.queryForListObject(params);
        return AjaxResponse.success(new PageDTO(params.getPage(), params.getPagesize(), total, BeanUtil.copyList(rows, DriverOutageDTO.class)));
    }

//    /**
//     *
//     *导出司机停运操作
//     * @param request
//     * @param response
//     * @return
//     */
//
//    @RequestMapping("/exportDriverOutage")
//    public void exportDriverOutage(DriverOutage params, HttpServletRequest request,HttpServletResponse response){
//        try {
//            logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"【司机停运】导出司机停运列表");
//        } catch (Exception e) {
//        }
//        logger.info("【司机停运】导出司机停运操作");
//        try {
//            //权限
//            String cities = ToolUtils.getSecurityUser().getCities();
//            String suppliers = ToolUtils.getSecurityUser().getSuppliers();
//            params.setCities(cities);
//            params.setSupplierIds(suppliers);
//            List<DriverOutage> rows = this.driverOutageService.queryForListObjectNoLimit(params);
//            @SuppressWarnings("deprecation")
//            Workbook wb = driverOutageService.exportExcelDriverOutage(rows,request.getRealPath("/")+File.separator+"template"+File.separator+"driverOutage_info.xlsx");
//            super.exportExcelFromTemplet(request, response, wb, new String("临时停运司机".getBytes("gb2312"), "iso8859-1"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    @ResponseBody
//    @RequestMapping(value = "/queryDriverNameByPhone", method = { RequestMethod.POST })
//    public Object queryDriverNameByPhone(DriverOutage params){
//        try {
//            logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"【司机停运】查询手机号"+params.getDriverPhone()+"所对应的司机姓名");
//        } catch (Exception e) {
//        }
//        logger.info("【司机停运】查询手机号"+params.getDriverPhone()+"所对应的司机姓名");
//        Map<String,Object> result = new HashMap<String,Object>();
//        //查数据
//        DriverOutage outage = this.driverOutageService.queryDriverNameByPhone(params);
//        if(outage==null){
//            result.put("result", 0);
//            result.put("driverPhone", params.getDriverPhone());
//            result.put("driverName", "");
//            result.put("msg","没有该手机号的司机，请仔细核对！");
//        }else{
//            //返回为1 ==========成功
//            result.put("result", 1);
//            result.put("driverPhone", params.getDriverPhone());
//            result.put("driverName", outage.getDriverName());
//            result.put("driverId", outage.getDriverId());
//            result.put("msg","成功！");
//        }
//        return result;
//    }
//
//    @RequestMapping(value = "/driverOutageAddView")
//    public String driverOutageAddView() {
//        try {
//            logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"【司机停运】临时停运页面");
//        } catch (Exception e) {
//        }
//        logger.info("【司机停运】临时停运页面");
//        return "driverOutage/driverOutageAdd";
//    }
//
//    @ResponseBody
//    @RequestMapping(value="/saveDriverOutage", method = { RequestMethod.POST })
//    public Object saveDriverOutage(DriverOutage params){
//        try {
//            logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"【司机停运】临时停运新增数据=="+params.toString());
//        } catch (Exception e) {
//        }
//        logger.info("【司机停运】临时停运新增=="+params.toString());
//        Map<String,Object> result = new HashMap<String,Object>();
//        if(params.getDriverId()==null){
//            result.put("result", 0);
//            result.put("exception", "请查看司机姓名是否出现，如没有，请稍等！");
//            return result;
//        }
//        DriverOutage outage = this.driverOutageService.queryDriverOutageAllByDriverId(params);
//        if(outage!=null){
//            result.put("result", 0);
//            result.put("exception", "该司机已存在启用的永久停运！");
//        }else{
//            result = this.driverOutageService.saveDriverOutage(params);
//        }
//        return result;
//    }
//
//    @ResponseBody
//    @RequestMapping(value="/updateDriverOutage", method = { RequestMethod.POST })
//    public Object updateDriverOutage(DriverOutage params){
//        try {
//            logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"【司机停运】临时停运解除数据=="+params.toString());
//        } catch (Exception e) {
//        }
//        logger.info("【司机停运】临时停运解除数据=="+params.toString());
//        Map<String,Object> result = new HashMap<String,Object>();
//        result = this.driverOutageService.updateDriverOutage(params);
//        return result;
//    }
//
//    @ResponseBody
//    @RequestMapping(value="/updateDriverOutages", method = { RequestMethod.POST })
//    public Object updateDriverOutages(DriverOutage params){
//        try {
//            logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"【司机停运】临时停运批量解除数据=="+params.toString());
//        } catch (Exception e) {
//        }
//        logger.info("【司机停运】临时停运批量解除数据=="+params.toString());
//        Map<String,Object> result = new HashMap<String,Object>();
//        result = this.driverOutageService.updateDriverOutages(params);
//        return result;
//    }

}
  