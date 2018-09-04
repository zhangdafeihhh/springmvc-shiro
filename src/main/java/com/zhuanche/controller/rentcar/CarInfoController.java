package com.zhuanche.controller.rentcar;

import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.rentcar.CarInfoDTO;
import com.zhuanche.entity.mdbcarmanage.CarAdmUser;
import com.zhuanche.entity.rentcar.CarInfo;
import com.zhuanche.serv.authc.UserManagementService;
import com.zhuanche.serv.rentcar.CarInfoService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.Common;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController("CarInfoController")
@RequestMapping("carInfo")
public class CarInfoController {
    private static Logger logger = LoggerFactory.getLogger(CarInfoController.class);

    @Autowired
    private CarInfoService carService;

    @Autowired
    private UserManagementService userManagementService;

    /**
     *
     * @param cities    城市id，多个逗号分割
     * @param supplierIds   供应商id，多个逗号分割
     * @param carModelIds   车型id，多个逗号分割
     * @param licensePlates 车牌号
     * @param createDateBegin   创建时间
     * @param createDateEnd     截止时间
     * @param status        是否有效
     * @param isFree        车辆状态
     * @return
     */
    @RequestMapping(value = "/queryCarData.json", method = { RequestMethod.POST })
    public Object queryCarData(String cities,
                               String supplierIds,
                               String carModelIds,
                               String licensePlates,
                               String createDateBegin,
                               String createDateEnd,
                               Integer status,
                               Integer isFree,
                               Integer page,
                               Integer pageSize) {
        logger.info("车辆列表数据:queryCarData");

        CarInfo params = new CarInfo();
        params.setCities(cities);
        params.setSupplierIds(supplierIds);
        params.setCarModelIds(carModelIds);
        params.setLicensePlates(licensePlates);
        params.setCreateDateBegin(createDateBegin);
        params.setCreateDateEnd(createDateEnd);
        params.setStatus(status);
        params.setIsFree(isFree);
        if(null != page && page > 0)
            params.setPage(page);
        if(null != pageSize && pageSize > 0)
            params.setPagesize(pageSize);

        String _cities = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getCityIds(), ",");
        String _suppliers = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getSupplierIds(), ",");
        String _teamId = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getTeamIds(), ",");
        String _carModelIds = "";
        if(params.getCities()!=null && !"".equals(params.getCities()) ){
            _cities = params.getCities().replace(";", ",");
        }
        if(!"".equals(params.getSupplierIds())&&params.getSupplierIds()!=null){
            _suppliers = params.getSupplierIds().replace(";", ",");
        }
        if(!"".equals(params.getCarModelIds())&&params.getCarModelIds()!=null){
            _carModelIds = params.getCarModelIds().replace(";", ",");
        }
        params.setCarModelIds(_carModelIds);
        params.setSupplierIds(_suppliers);
        params.setCities(_cities);
        params.setTeamIds(_teamId);
        List<CarInfo> rows = new ArrayList<CarInfo>();
        rows = carService.selectList(params);
        int total = carService.selectListCount(params);

        return AjaxResponse.success(new PageDTO(params.getPage(), params.getPagesize(), total, BeanUtil.copyList(rows, CarInfoDTO.class)));
    }

    @RequestMapping(value = "/queryCarInfo.json", method = { RequestMethod.GET })
    public AjaxResponse queryCarInfo(@Verify(param = "carId",rule = "required") Integer carId) {
        logger.info("queryCar:查看车辆详情列表");
        CarInfo params = new CarInfo();
        params.setCarId(carId);
        params = this.carService.selectCarInfoByCarId(params);
        if(params!=null){
            if(params.getCreateBy()!=null && !params.getCreateBy().equals("")){
                CarAdmUser user = new CarAdmUser();
                user.setUserId(params.getCreateBy());
                CarAdmUser us = userManagementService.getUserById(params.getCreateBy());
                if(us!=null){
                    params.setCreateName(us.getUserName());
                }
            }
            if(params.getUpdateBy()!=null && !params.getUpdateBy().equals("")){
                CarAdmUser user = new CarAdmUser();
                user.setUserId(params.getUpdateBy());
                CarAdmUser us = userManagementService.getUserById(params.getUpdateBy());
                if(us!=null){
                    params.setUpdateName(us.getUserName());
                }
            }
        }
        return AjaxResponse.success( BeanUtil.copyObject(params, CarInfoDTO.class) );
    }


    /**
     * 根据车牌号查询是否已存在
     * @param licensePlates
     * @return
     */
    @RequestMapping(value = "/checkLicensePlates.json", method = { RequestMethod.POST })
    public Object checkLicensePlates(@Verify(param = "licensePlates", rule = "required") String licensePlates) {
        logger.info("根据车牌号查询是否已存在:checkLicensePlates");
        CarInfo params = new CarInfo();
        params.setLicensePlates(licensePlates);
        return carService.checkLicensePlates(params);
    }

    /**
     * 保存或修改车辆 TODO 待完成
     * @param params
     * @param request
     * @return
     */
    @RequestMapping(value = "/saveCarInfo.json", method = { RequestMethod.POST })
    public Object saveCarInfo(CarInfo params, HttpServletRequest request) {
        logger.info("车辆保存/修改:saveCarInfo");
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Integer userId = WebSessionUtil.getCurrentLoginUser().getId();
            params.setUpdateBy(userId);
            if (params.getCarId() == null) {
                logger.info("*********操作类型：新建");
                params.setCreateBy(userId);
            }
            result = this.carService.saveCarInfo(params);
        } catch (Exception e) {
            logger.error("save CarInfo error. ", e);
        }
        return result;
    }


//    /**
//     * 车辆信息删除
//     * @param params
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/deleteCarInfo.json", method = { RequestMethod.POST })
//    public Object deleteCarInfo(CarInfo params, HttpServletRequest request) {
//        logger.info("车辆删除:deleteCarInfo");
//        Map<String, Object> result = new HashMap<String, Object>();
//        String message = "";
//        try {
//            String carIds = params.getCarIds();
//            if(StringUtils.isEmpty(carIds)){
//                result.put(Common.RESULT_RESULT, 0);
//                result.put(Common.RESULT_ERRORMSG, "参数不全");
//                return result;
//            }
//            String[] carIdStr = carIds.split(",");
//            for (int i = 0; i < carIdStr.length; i++) {
//                String carId = carIdStr[i];
//                if(StringUtils.isEmpty(carId)){
//                    continue;
//                }
//                String licensePlates = this.carService.selectCarByCarId(Integer.parseInt(carId));
//                if(StringUtils.isEmpty(licensePlates)){
//                    continue;
//                }
//                CarInfo carInfoEntity = new CarInfo();
//                carInfoEntity.setCarId(Integer.parseInt(carId));
//                carInfoEntity.setLicensePlates(licensePlates);
//                carInfoEntity = this.carService.selectCarInfoByCarId(carInfoEntity);
//                if(carInfoEntity.getIsFree()==1){
//                    message += licensePlates + "该车正在运营中，不可删除;";
//                    continue;
//                }
//                carInfoEntity.setStatus(2);
//                carInfoEntity.setLicensePlates1(licensePlates);
//                params.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
//                result = this.carService.saveCarInfo(carInfoEntity);
//            }
//        } catch (Exception e) {
//            logger.error("delete CarInfo error. ", e);
//        }
//        if(StringUtils.isNotEmpty(message)){
//            result.put(Common.RESULT_RESULT, 0);
//            result.put(Common.RESULT_ERRORMSG, message);
//            return result;
//        }
//        result.put(Common.RESULT_RESULT, 1);
//        result.put(Common.RESULT_ERRORMSG, "成功");
//        return result;
//    }


    /**
     * 车辆信息导入删除
     */
    @RequestMapping(value = "/importDeleteCarInfo")
    public Object importDeleteCarInfo(CarInfo params, HttpServletRequest request) {
        logger.info("车辆信息导入删除");
        Map<String, Object> result = new HashMap<String, Object>();
        result = this.carService.importDeleteCarInfo(params, request);
        return result;
    }

    /**
     * 车辆信息导入 TODO 待完成
     */
    @RequestMapping(value = "/importCarInfo")
    public Object importCarInfo(CarInfo params, HttpServletRequest request) {
        logger.info("车辆信息导入保存:importCarInfo,参数" + params.toString());
        Map<String, Object> result = new HashMap<String, Object>();
        result = this.carService.importCarInfo(params, request);
        return result;
    }

    /**
     *
     *车辆信息导出
     */
    @RequestMapping("/exportCarInfo.json")
    public void exportCarInfo(CarInfo params, HttpServletRequest request, HttpServletResponse response){
        logger.info("exportCarInfo:车辆信息导出");
        try {
            @SuppressWarnings("deprecation")
            Workbook wb = this.carService.exportExcel(params,request.getRealPath("/")+File.separator+"template"+File.separator+"car_info.xlsx");
            exportExcelFromTemplet(request, response, wb, new String("车辆信息列表".getBytes("gb2312"), "iso8859-1"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载车辆导入模板
     */
    @RequestMapping(value = "/fileDownloadCarInfo.json")
    public void fileDownloadCarInfo(HttpServletRequest request,
                                    HttpServletResponse response) {

        String path = request.getRealPath("/") + File.separator + "upload"
                + File.separator + "IMPORTCARINFO.xlsx";
        fileDownload(request,response,path);
    }

    /*
     * 下载
     */
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

    public void exportExcelFromTemplet(HttpServletRequest request, HttpServletResponse response, Workbook wb, String fileName) throws IOException {
        if(StringUtils.isEmpty(fileName)) {
            fileName = "exportExcel";
        }
        response.setHeader("Content-Disposition","attachment;filename="+fileName+".xlsx");//指定下载的文件名
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        ServletOutputStream os =  response.getOutputStream();
        wb.write(os);
        os.close();
    }

}
