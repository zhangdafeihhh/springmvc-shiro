package com.zhuanche.controller.rentcar;

import com.google.common.collect.Maps;
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
import org.springframework.web.multipart.MultipartFile;

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


    @RequestMapping(value = "/queryDriverOutageData")
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
            PageDTO result = new PageDTO(params.getPage(), params.getPagesize(), 0, rows);
            return AjaxResponse.success(result);
        }
        //查数据
        rows = driverOutageService.queryAllForListObject(params);
        return AjaxResponse.success(new PageDTO(params.getPage(), params.getPagesize(), total, BeanUtil.copyList(rows, DriverOutageAllDTO.class)));
    }

    @RequestMapping(value="/saveDriverOutage")
    public AjaxResponse saveDriverOutage(
                                        @Verify(param = "driverName",rule = "required")String driverName,
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
            return getResponse(result);
        }
    }

    @RequestMapping(value="/updateDriverOutages")
    public AjaxResponse updateDriverOutages(@Verify(param = "outageIds",rule = "required")String outageIds,
                                            @Verify(param = "removeReason",rule = "required")String removeReason){
        DriverOutage params = new DriverOutage();
        params.setOutageIds(outageIds);
        params.setRemoveReason(removeReason);
        params.setRemoveStatus(2);//解除状态 1：启用 2：停用
        logger.info("【司机永久停运】永久停运批量解除数据=="+params.toString());
        Map<String,Object> result = new HashMap<String,Object>();
        result = this.driverOutageService.updateDriverOutagesAll(params);
        return getResponse(result);
    }

//    /**
//     * 永久停运导入
//     */
//    @RequestMapping(value = "/importDriverOutageInfo")
//    public AjaxResponse importDriverOutageInfo(DriverOutageVo params, HttpServletRequest request) {
//        logger.info("永久停运导入保存importDriverOutageInfo,参数" + params.toString());
//        Map<String, Object> result = new HashMap<String, Object>();
//        result = this.driverOutageService.importDriverOutageInfo(params, request);
//        return getResponse(result);
//    }

    /**
     * 永久停运导入
     */
    @RequestMapping(value = "/importDriverOutageInfo")
    public AjaxResponse importDriverOutageInfo(@RequestParam(value="filename") MultipartFile file,
                                               HttpServletRequest request,HttpServletResponse response) {

        if(file == null){
            return AjaxResponse.fail(400);
        }
        //获取文件名
        String name=file.getOriginalFilename();
        //进一步判断文件是否为空（即判断其大小是否为0或其名称是否为null）
        long size=file.getSize();
        if(name==null || ("").equals(name) && size==0)
            return AjaxResponse.fail(400);

        logger.info("永久停运导入保存importDriverOutageInfo,参数" + file.getName());
        Map<String, Object> result = new HashMap<String, Object>();
        result = this.driverOutageService.importDriverOutageInfo(name, file, request);
        return getResponse(result);
    }

    /**
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

    public AjaxResponse getResponse(Map<String,Object> result){
        try{
//            JSONObject jsonStr = (JSONObject)result.get("jsonStr");

            Integer result1 = Integer.valueOf( result.get("result").toString() );
            Map response = Maps.newHashMap();
            if( 0 == result1 ){
                String exception = result.get("exception").toString();
                return AjaxResponse.fail(996, exception);
            } else if(1 == result1){
                Object success = result.get("success");
                Object error = result.get("error");
                Object msg = result.get("msg");
                Object errorList = result.get("errorList");
                if(success != null)
                    response.put("success", success);
                if(error != null)
                    response.put("error", error);
                if(msg != null)
                    response.put("msg", msg);
                if(errorList != null)
                    response.put("errorList", msg);
                return AjaxResponse.success(response);
            }
            return AjaxResponse.fail(999);
        } catch (Exception e){
            return AjaxResponse.fail(999);
        }
    }

}
