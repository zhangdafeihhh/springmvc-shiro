package com.zhuanche.controller.rentcar;

import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.dto.rentcar.CarInfoDTO;
import com.zhuanche.entity.rentcar.CarInfo;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("carInfo")
public class CarInfoController {
    private static Logger logger = LoggerFactory.getLogger(CarInfoController.class);

    @Autowired
    private CarInfoService carService;

    //TODO 登录用户暂无
//    @Autowired
//    private UserService userService;

//    @RequestMapping(value = "/queryCar", method = { RequestMethod.GET })
//    public String queryCar() {
//        try {
//            logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"车辆列表");
//        } catch (Exception e) {
//        }
//        logger.info("queryCar:车辆列表");
//        return "car/carList";
//    }

    @RequestMapping(value = "/queryCarData.json", method = { RequestMethod.POST })
    public Object queryCarData(CarInfo params) {
        logger.info("车辆列表数据:queryCarData");

        String cities = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getCityIds(), ",");
        String suppliers = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getSupplierIds(), ",");
        String teamId = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getTeamIds(), ",");
        String carModelIds = "";
        if(!"".equals(params.getCities())&&params.getCities()!=null){
            cities = params.getCities().replace(";", ",");
        }
        if(!"".equals(params.getSupplierIds())&&params.getSupplierIds()!=null){
            suppliers = params.getSupplierIds().replace(";", ",");
        }
        if(!"".equals(params.getCarModelIds())&&params.getCarModelIds()!=null){
            carModelIds = params.getCarModelIds().replace(";", ",");
        }
        params.setCarModelIds(carModelIds);
        params.setSupplierIds(suppliers);
        params.setCities(cities);
        params.setTeamIds(teamId);
        List<CarInfo> rows = new ArrayList<CarInfo>();
        rows = carService.selectList(params);
        int total = carService.selectListCount(params);

        return AjaxResponse.success(new PageDTO(params.getPage(), params.getPagesize(), total, BeanUtil.copyList(rows, CarInfoDTO.class)));
    }

//    @AuthPassport
//    @RequestMapping(value = "/queryCarNew", method = { RequestMethod.GET })
//    public String queryCarNew() {
//        try {
//            logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"车辆列表");
//        } catch (Exception e) {
//        }
//        logger.info("queryCarNew:车辆列表");
//        return "car/carListNew";
//    }

//    @RequestMapping(value = "/queryCarInfo", method = { RequestMethod.GET })
//    public String queryCarInfo(ModelMap model, CarInfo params) {
//        logger.info("queryCar:查看车辆详情列表");
//        params = this.carService.selectCarInfoByCarId(params);
//        if(params!=null){
//            if(params.getCreateBy()!=null && !params.getCreateBy().equals("")){
//                User user = new User();
//                user.setUserId(params.getCreateBy());
//                User us = userService.getUserInfo(user);
//                if(us!=null){
//                    params.setCreateName(us.getUserName());
//                }
//            }
//            if(params.getUpdateBy()!=null && !params.getUpdateBy().equals("")){
//                User user = new User();
//                user.setUserId(params.getUpdateBy());
//                User us = userService.getUserInfo(user);
//                if(us!=null){
//                    params.setUpdateName(us.getUserName());
//                }
//            }
//        }
//        model.put("carInfoEntity", params);
//        return "car/carInfo";
//    }

//    @AuthPassport
//    @RequestMapping(value = "/changeCarInfo", method = { RequestMethod.GET }, produces="text/html;charset=UTF-8")
//    public String changeCarInfo(ModelMap model, CarInfo params) {
//        try {
//            logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"新增车辆页");
//        } catch (Exception e) {
//        }
//        if (params != null && params.getCarId() != null) {
//            params = this.carService.selectCarInfoByCarId(params);
//            model.put("carInfoEntity", params);
//            if(params.getCooperationType()!=0 && params.getCooperationType()!=5){
//                //加盟车辆页面，部分字段不限制
//                return "car/addCarJoin";
//            }
//        }
//        return "car/addCar";
//    }

    /**
     * 根据车牌号查询是否已存在
     * @param params
     * @return
     */
    @RequestMapping(value = "/checkLicensePlates", method = { RequestMethod.POST })
    public Object checkLicensePlates(CarInfo params) {
        logger.info("根据车牌号查询是否已存在:checkLicensePlates");
        return carService.checkLicensePlates(params);
    }

    /**
     * 保存或修改车辆
     * @param params
     * @param request
     * @return
     */
    @RequestMapping(value = "/saveCarInfo", method = { RequestMethod.POST })
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


    /**
     * 车辆信息删除
     * @param params
     * @param request
     * @return
     */
    @RequestMapping(value = "/deleteCarInfo", method = { RequestMethod.POST })
    public Object deleteCarInfo(CarInfo params, HttpServletRequest request) {
        logger.info("车辆删除:deleteCarInfo");
        Map<String, Object> result = new HashMap<String, Object>();
        String message = "";
        try {
            String carIds = params.getCarIds();
            if(StringUtils.isEmpty(carIds)){
                result.put(Common.RESULT_RESULT, 0);
                result.put(Common.RESULT_ERRORMSG, "参数不全");
                return result;
            }
            String[] carIdStr = carIds.split(",");
            for (int i = 0; i < carIdStr.length; i++) {
                String carId = carIdStr[i];
                if(StringUtils.isEmpty(carId)){
                    continue;
                }
                String licensePlates = this.carService.selectCarByCarId(Integer.parseInt(carId));
                if(StringUtils.isEmpty(licensePlates)){
                    continue;
                }
                CarInfo carInfoEntity = new CarInfo();
                carInfoEntity.setCarId(Integer.parseInt(carId));
                carInfoEntity.setLicensePlates(licensePlates);
                carInfoEntity = this.carService.selectCarInfoByCarId(carInfoEntity);
                if(carInfoEntity.getIsFree()==1){
                    message += licensePlates + "该车正在运营中，不可删除;";
                    continue;
                }
                carInfoEntity.setStatus(2);
                carInfoEntity.setLicensePlates1(licensePlates);
                params.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
                result = this.carService.saveCarInfo(carInfoEntity);
            }
        } catch (Exception e) {
            logger.error("delete CarInfo error. ", e);
        }
        if(StringUtils.isNotEmpty(message)){
            result.put(Common.RESULT_RESULT, 0);
            result.put(Common.RESULT_ERRORMSG, message);
            return result;
        }
        result.put(Common.RESULT_RESULT, 1);
        result.put(Common.RESULT_ERRORMSG, "成功");
        return result;
    }

//    @AuthPassport
//    @RequestMapping(value = "/importDeleteCarInfoView", method = { RequestMethod.GET }, produces="text/html;charset=UTF-8")
//    public String importDeleteCarInfoView() {
//        try {
//            logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"车辆信息导入删除页");
//        } catch (Exception e) {
//        }
//        return "car/importDeleteCarInfo";
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
     * 车辆信息导入
     */
    @RequestMapping(value = "/importCarInfo")
    public Object importCarInfo(CarInfo params, HttpServletRequest request) {
        logger.info("车辆信息导入保存:importCarInfo,参数" + params.toString());
        Map<String, Object> result = new HashMap<String, Object>();
        result = this.carService.importCarInfo(params, request);
        return result;
    }

//    /**
//     *
//     *车辆信息导出
//     */
//    @RequestMapping("/exportCarInfo")
//    public void exportCarInfo(CarInfo params, HttpServletRequest request, HttpServletResponse response){
//        logger.info("exportCarInfo:车辆信息导出");
//        try {
//            @SuppressWarnings("deprecation")
//            Workbook wb = this.carService.exportExcel(params,request.getRealPath("/")+File.separator+"template"+File.separator+"car_info.xlsx");
//            super.exportExcelFromTemplet(request, response, wb, new String("车辆信息列表".getBytes("gb2312"), "iso8859-1"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 下载车辆导入模板
//     */
//    @RequestMapping(value = "/fileDownloadCarInfo")
//    public void fileDownloadCarInfo(HttpServletRequest request,
//                                    HttpServletResponse response) {
//
//        String path = request.getRealPath("/") + File.separator + "upload"
//                + File.separator + "IMPORTCARINFO.xlsx";
//        super.fileDownload(request,response,path);
//    }
}
