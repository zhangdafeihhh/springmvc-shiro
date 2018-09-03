package com.zhuanche.controller.rentcar;

import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.rentcar.DriverOutageAllDTO;
import com.zhuanche.entity.rentcar.DriverOutage;
import com.zhuanche.entity.rentcar.DriverOutageVo;
import com.zhuanche.serv.rentcar.DriverOutageService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@RestController
@RequestMapping("/driverOutageAll")
public class DriverOutageAllController {

    private static final Logger logger = LoggerFactory.getLogger(DriverOutageAllController.class);

    @Autowired
    DriverOutageService driverOutageService;


//    @RequestMapping(value = "/queryDriverOutage", method = { RequestMethod.GET })
//    public String queryDriverOutage(ModelMap model, DriverOutageEntity params){
//        try {
//            logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"【司机永久停运】司机永久停运列表");
//        } catch (Exception e) {
//        }
//        logger.info("【司机永久停运】queryDriverOutage:司机永久停运列表");
//        return "driverOutage/driverOutageAllList";
//    }
//
//    @RequestMapping(value = "/queryDriverOutageNo", method = { RequestMethod.GET })
//    public String queryDriverOutageNo(ModelMap model, DriverOutageEntity params){
//        try {
//            logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"【司机永久停运】司机永久停运列表");
//        } catch (Exception e) {
//        }
//        logger.info("【司机永久停运】queryDriverOutageNo:司机永久停运列表");
//        return "driverOutage/driverOutageAllListNo";
//    }

    @GetMapping(value = "/queryDriverOutageData.json")
    public AjaxResponse queryDriverOutageData(@Verify(param = "cityId",rule = "") Integer cityId,
                                              Integer supplierId,
                                              Integer carGroupId,
                                              Integer outageSource,
                                              String driverName,
                                              String driverPhone,
                                              @Verify(param = "startDateBegin",rule = "")String startDateBegin,
                                              String startDateEnd,
                                              Integer removeStatus,
                                              Integer page,
                                              Integer pageSize){
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
        if(null != page && page > 0)
            params.setPage(page);
        if(null != pageSize && pageSize > 0)
            params.setPagesize(pageSize);

        List<DriverOutage> rows = new ArrayList<>();
        int total = 0;
        //权限
        String cities = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getCityIds(), ",");
        String suppliers = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getSupplierIds(),",");
        params.setCities(cities);
        params.setSupplierIds(suppliers);
        //查数量
        total = driverOutageService.queryAllForInt(params);
        if(total==0){
            PageDTO result = new PageDTO(params.getPage(), params.getPagerSize(), 0, rows);
            return AjaxResponse.success(result);
        }
        //查数据
        rows = driverOutageService.queryAllForListObject(params);
        return AjaxResponse.success(new PageDTO(params.getPage(), params.getPagesize(), total, BeanUtil.copyList(rows, DriverOutageAllDTO.class)));
    }

//    @AuthPassport
//    @RequestMapping(value = "/driverOutageAddView")
//    public String driverOutageAddView() {
//        try {
//            logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"【司机永久停运】永久停运页面");
//        } catch (Exception e) {
//        }
//        logger.info("【司机永久停运】永久停运页面");
//        return "driverOutage/driverOutageAllAdd";
//    }

    @RequestMapping(value="/saveDriverOutage", method = { RequestMethod.POST })
    public AjaxResponse saveDriverOutage(
                                         String driverName,
                                         @Verify(param = "driverPhone",rule = "mobile")String driverPhone,
                                         @Verify(param = "outageReason",rule = "required")String outageReason,
                                         @Verify(param = "driverId",rule = "required")Integer driverId){
        if(outageReason.length() > 50)
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID,"请输入50字以内的停运说明");
        DriverOutage params = new DriverOutage();
        params.setDriverPhone(driverPhone);
        params.setDriverName(driverName);
        params.setOutageReason(outageReason);
        params.setDriverId(driverId);

        logger.info("【司机永久停运】永久停运新增=="+params.toString());
        Map<String,Object> result = new HashMap<String,Object>();
        if(params.getDriverId()==null){
            return AjaxResponse.fail(RestErrorCode.DRIVER_NOT_EXIST);
        }
        List<DriverOutage> list = this.driverOutageService.queryDriverOutageByDriverId(params);
        DriverOutage outage = this.driverOutageService.queryDriverOutageAllByDriverId(params);

        if(list!=null&&list.size()>0){
            return AjaxResponse.fail(RestErrorCode.DRIVER_OUTAGE_EXIST);

        }else if(outage!=null){
            return AjaxResponse.fail(RestErrorCode.DRIVER_OUTAGEALL_EXIST);
        }else{
            result = this.driverOutageService.saveDriverOutageAll(params);
            return AjaxResponse.success(result);
        }
    }

    @RequestMapping(value="/updateDriverOutages", method = { RequestMethod.POST })
    public AjaxResponse updateDriverOutages(@Verify(param = "outageIds",rule = "required")String outageIds,
                                            @Verify(param = "removeReason",rule = "required")String removeReason){
        DriverOutage params = new DriverOutage();
        params.setOutageIds(outageIds);
        params.setRemoveReason(removeReason);
        params.setRemoveStatus(2);//解除状态 1：启用 2：停用
        logger.info("【司机永久停运】永久停运批量解除数据=="+params.toString());
        Map<String,Object> result = new HashMap<String,Object>();
        result = this.driverOutageService.updateDriverOutagesAll(params);
        return AjaxResponse.success(result);
    }

    /**
     * 永久停运导入
     */
    @RequestMapping(value = "/importDriverOutageInfo")
    public AjaxResponse importDriverOutageInfo(DriverOutageVo params, HttpServletRequest request) {
        logger.info("永久停运导入保存importDriverOutageInfo,参数" + params.toString());
        Map<String, Object> result = new HashMap<String, Object>();
        result = this.driverOutageService.importDriverOutageInfo(params, request);
        return AjaxResponse.success(request);
    }

    /*
     * 下载永久停运导入模板
     */
    @RequestMapping(value = "/fileDownloadInfo")
    public void fileDownloadCarInfo(HttpServletRequest request,
                                    HttpServletResponse response) {
        String path = request.getRealPath("/") + File.separator + "upload"
                + File.separator + "IMPORTDRIVEROUTAGEALLINFO.xlsx";
        fileDownload(request,response,path);
    }

    public void fileDownload(HttpServletRequest request, HttpServletResponse response,String path) {

        File file = new File(path);// path是根据日志路径和文件名拼接出来的
        String filename = file.getName();// 获取日志文件名称
        try {
            InputStream fis = new BufferedInputStream(new FileInputStream(path));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            response.reset();
            // 先去掉文件名称中的空格,然后转换编码格式为utf-8,保证不出现乱码,这个文件名称用于浏览器的下载框中自动显示的文件名
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.replaceAll(" ", "").getBytes("utf-8"),"iso8859-1"));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream os = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            os.write(buffer);// 输出文件
            os.flush();
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
