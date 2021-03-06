package com.zhuanche.controller.rentcar;

import com.google.common.collect.Maps;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.rentcar.DriverOutageAllDTO;
import com.zhuanche.entity.rentcar.DriverOutage;
import com.zhuanche.serv.rentcar.DriverOutageService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.MobileOverlayUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

import static com.zhuanche.common.enums.MenuEnum.DRIVER_FOREVER_STOP_MANAGE;

@RestController
@RequestMapping("/driverOutageAll")
public class DriverOutageAllController {

    private static final Logger logger = LoggerFactory.getLogger(DriverOutageAllController.class);

    @Autowired
    DriverOutageService driverOutageService;


    @RequestMapping(value = "/queryDriverOutageData")
	@RequiresPermissions(value = { "ForeverCarry_look" } )
    @MasterSlaveConfigs(configs={ 
			@MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE )
	} )
    @RequestFunction(menu = DRIVER_FOREVER_STOP_MANAGE)
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
        logger.info("??????????????????????????????????????????:queryDriverOutageData.json");
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
        //??????
        String cities = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getCityIds(), ",");
        String suppliers = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getSupplierIds(),",");
        params.setCities(cities);
        params.setSupplierIds(suppliers);
        //?????????
        total = driverOutageService.queryAllForInt(params);
        if(total==0){
            PageDTO result = new PageDTO(params.getPage(), params.getPagesize(), 0, rows);
            return AjaxResponse.success(result);
        }
        //?????????
        rows = driverOutageService.queryAllForListObject(params);
        overLayPhone(rows);
        return AjaxResponse.success(new PageDTO(params.getPage(), params.getPagesize(), total, BeanUtil.copyList(rows, DriverOutageAllDTO.class)));
    }

    private void overLayPhone(List<DriverOutage> rows) {
        if (Objects.nonNull(rows)){
            for (DriverOutage row : rows) {
                row.setDriverPhone(MobileOverlayUtil.doOverlayPhone(row.getDriverPhone()));
            }
        }
    }

    @RequestMapping(value="/saveDriverOutage")
    public AjaxResponse saveDriverOutage(
                                        @Verify(param = "driverName",rule = "required")String driverName,
                                         @Verify(param = "driverPhone",rule = "mobile")String driverPhone,
                                         @Verify(param = "outageReason",rule = "required")String outageReason,
                                         @Verify(param = "driverId",rule = "required")Integer driverId){
        if(outageReason.length() > 50)
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID,"?????????50????????????????????????");
        DriverOutage params = new DriverOutage();
        params.setDriverPhone(driverPhone);
        params.setDriverName(driverName);
        params.setOutageReason(outageReason);
        params.setDriverId(driverId);

        logger.info("??????????????????????????????????????????=="+params.toString());
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
        params.setRemoveStatus(2);//???????????? 1????????? 2?????????
        logger.info("??????????????????????????????????????????????????????=="+params.toString());
        Map<String,Object> result = new HashMap<String,Object>();
        result = this.driverOutageService.updateDriverOutagesAll(params);
        return getResponse(result);
    }

    /**
     * ??????????????????
     */
    @RequestMapping(value = "/importDriverOutageInfo")
    public AjaxResponse importDriverOutageInfo(@RequestParam(value="filename") MultipartFile file,
                                               HttpServletRequest request,HttpServletResponse response) {

        if(file == null){
            return AjaxResponse.fail(400);
        }
        //???????????????
        String name=file.getOriginalFilename();
        //???????????????????????????????????????????????????????????????0?????????????????????null???
        long size=file.getSize();
        if(name==null || ("").equals(name) && size==0)
            return AjaxResponse.fail(400);

        logger.info("????????????????????????importDriverOutageInfo,??????" + file.getName());
        Map<String, Object> result = new HashMap<String, Object>();
        return driverOutageService.importDriverOutageInfo(name, file, request);
    }

    /**
     * ??????????????????????????????
     */
    @RequestMapping(value = "/fileDownloadInfo")
    public void fileDownloadCarInfo(HttpServletRequest request,
                                    HttpServletResponse response) {
        String path = request.getRealPath("/") + File.separator + "upload"
                + File.separator + "IMPORTDRIVEROUTAGEALLINFO.xlsx";
        fileDownload(request,response,path);
    }

    public void fileDownload(HttpServletRequest request, HttpServletResponse response,String path) {

        File file = new File(path);// path????????????????????????????????????????????????
        String filename = file.getName();// ????????????????????????
        try {
            InputStream fis = new BufferedInputStream(new FileInputStream(path));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            response.reset();
            // ?????????????????????????????????,???????????????????????????utf-8,?????????????????????,????????????????????????????????????????????????????????????????????????
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.replaceAll(" ", "").getBytes("utf-8"),"iso8859-1"));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream os = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            os.write(buffer);// ????????????
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
