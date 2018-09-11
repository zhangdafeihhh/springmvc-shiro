package com.zhuanche.controller.rentcar;

import com.google.common.collect.Maps;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController("CarInfoController")
@RequestMapping("carInfo")
public class CarInfoController {
    private static Logger logger = LoggerFactory.getLogger(CarInfoController.class);

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

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
    @RequestMapping(value = "/queryCarData")
    public Object queryCarData(@Verify(param = "cities",rule="") String cities,
                               @Verify(param = "supplierIds",rule="") String supplierIds,
                               @Verify(param = "carModelIds",rule="") String carModelIds,
                               @Verify(param = "licensePlates",rule="") String licensePlates,
                               @Verify(param = "createDateBegin",rule="") String createDateBegin,
                               @Verify(param = "createDateEnd",rule="") String createDateEnd,
                               @Verify(param = "status",rule="") Integer status,
                               @Verify(param = "isFree",rule="") Integer isFree,
                               @Verify(param = "page",rule="") Integer page,
                               @Verify(param = "pageSize",rule="") Integer pageSize) {
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

    /**
     * 查询车辆详情
     * @param carId
     * @return
     */
    @RequestMapping(value = "/queryCarInfo")
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
        params.setLicensePlates1(params.getLicensePlates());
        params.setOldCity(params.getCityId());
        params.setOldSupplierId(params.getSupplierId());

        return AjaxResponse.success( BeanUtil.copyObject(params, CarInfoDTO.class) );
    }


    /**
     * 根据车牌号查询是否已存在
     * @param licensePlates
     * @return
     */
//    @RequestMapping(value = "/checkLicensePlates")
//    public Object checkLicensePlates(@Verify(param = "licensePlates", rule = "required") String licensePlates) {
//        logger.info("根据车牌号查询是否已存在:checkLicensePlates");
//        CarInfo params = new CarInfo();
//        params.setLicensePlates(licensePlates);
//        return getResponse(carService.checkLicensePlates(params) );
//    }

    /**
     * 修改
     * @param carId 主键
     * @param licensePlates 车牌号
     * @param status 是否有效
     * @param cityId 城市
     * @param supplierId 供应商
     * @param carModelId 车型
     * @param imageUrl 车辆图片上传
     * @param vehicleDrivingLicense 行驶证扫描件
     * @param modelDetail 具体车型
     * @param color 颜色
     * @param clicensePlatesColor 车牌颜色
     * @param engineNo 发动机号
     * @param vehicleEngineDisplacement 车辆发动机排量
     * @param vehicleEnginePower 发动机功率
     * @param carryPassengers 核定载客位
     * @param frameNo 车架号
     * @param vehicleBrand 车辆厂牌
     * @param vehicleEngineWheelbase 车辆轴距
     * @param vehicleOwner 所属车主
     * @param vehicleType 车辆类型（以机动车行驶证为主）
     * @param fuelType 车辆燃料类型
     * @param nextInspectDate 下次车检时间 (格式:yyyy-MM-dd)
     * @param nextMaintenanceDate 下次维保时间 (格式:yyyy-MM-dd)
     * @param nextOperationDate 下次运营证检验时间 (格式:yyyy-MM-dd)
     * @param nextSecurityDate 下次治安证检测时间 (格式:yyyy-MM-dd)
     * @param nextClassDate 下次等级验证时间 (格式:yyyy-MM-dd)
     * @param twoLevelMaintenanceDate 二级维护时间 (格式:yyyy-MM-dd)
     * @param rentalExpireDate 租赁到期时间 (格式:yyyy-MM-dd)
     * @param purchaseDate 购买时间 (格式:yyyy-MM-dd)
     * @param vehicleRegistrationDate 车辆注册日期 (格式:yyyy-MM-dd)
     * @param transportNumber 运输证字号
     * @param certificationAuthority 车辆运输证发证机构
     * @param operatingRegion 车辆经营区域
     * @param transportNumberDateStart 车辆运输证有效期起 (格式:yyyy-MM-dd)
     * @param transportNumberDateEnd 车辆运输证有效期止 (格式:yyyy-MM-dd)
     * @param firstDate 车辆初次登记日期 (格式:yyyy-MM-dd)
     * @param overHaulStatus 车辆检修状态
     * @param auditingStatus 车辆年度审验状态
     * @param auditingDate 车辆年度审验日期 (格式:yyyy-MM-dd)
     * @param equipmentNumber 发票打印设备序列号
     * @param gpsBrand 卫星定位装置品牌
     * @param gpsType 卫星定位装置型号
     * @param gpsImei 卫星定位装置IMEI号
     * @param gpsDate 卫星定位设备安装日期(格式:yyyy-MM-dd)
     * @param licensePlates1 旧的车牌号
     * @param oldCity 旧城市Id
     * @param oldSupplierId 旧的供应商Ids
     * @param memo 备注
     * @return
     */
    @RequestMapping(value = "/saveCarInfo")
    public Object saveCarInfo(@Verify(param = "carId",rule="") Integer carId,
                                  @Verify(param = "licensePlates",rule="required") String licensePlates,
                                  @Verify(param = "status",rule="required") Integer status,
                                  @Verify(param = "cityId",rule="required") Integer cityId,
                                  @Verify(param = "supplierId",rule="required") Integer supplierId,
                                  @Verify(param = "carModelId",rule="required") Integer carModelId,
                              @Verify(param = "imageUrl",rule="") String imageUrl,
                              @Verify(param = "vehicleDrivingLicense",rule="") String vehicleDrivingLicense,
                                  @Verify(param = "modelDetail",rule="required") String modelDetail,
                                  @Verify(param = "color",rule="required") String color,
                              @Verify(param = "clicensePlatesColor",rule="") String clicensePlatesColor,
                              @Verify(param = "engineNo",rule="") String engineNo,
                              @Verify(param = "vehicleEngineDisplacement",rule="") String vehicleEngineDisplacement,
                              @Verify(param = "vehicleEnginePower",rule="") String vehicleEnginePower,
                              @Verify(param = "carryPassengers",rule="") String carryPassengers,
                              @Verify(param = "vehicleBrand",rule="") String vehicleBrand,
                              @Verify(param = "vehicleEngineWheelbase",rule="") String vehicleEngineWheelbase,
                                    @Verify(param = "vehicleOwner",rule="required") String vehicleOwner,
                              @Verify(param = "frameNo",rule="") String frameNo,
                              @Verify(param = "vehicleType",rule="") String vehicleType,
                              @Verify(param = "fuelType",rule="") Integer fuelType,
                              @RequestParam(value = "nextInspectDate",required = false) String nextInspectDate,
                              @RequestParam(value = "nextMaintenanceDate",required = false) String nextMaintenanceDate,
                              @RequestParam(value = "nextOperationDate",required = false) String nextOperationDate,
                              @RequestParam(value = "nextSecurityDate",required = false) String nextSecurityDate,
                              @RequestParam(value = "nextClassDate",required = false) String nextClassDate,
                              @RequestParam(value = "twoLevelMaintenanceDate",required = false) String twoLevelMaintenanceDate,
                              @RequestParam(value = "rentalExpireDate",required = false) String rentalExpireDate,
                              @RequestParam(value = "purchaseDate",required = false) String purchaseDate,
                                    @Verify(param = "vehicleRegistrationDate",rule="required") String vehicleRegistrationDate,
                              @Verify(param = "transportNumber",rule="") String transportNumber,
                              @Verify(param = "certificationAuthority",rule="") String certificationAuthority,
                              @Verify(param = "operatingRegion",rule="") String operatingRegion,
                              @Verify(param = "transportNumberDateStart",rule="") String transportNumberDateStart,
                              @Verify(param = "transportNumberDateEnd",rule="") String transportNumberDateEnd,
                              @Verify(param = "firstDate",rule="") String firstDate,
                              @Verify(param = "overHaulStatus",rule="") Integer overHaulStatus,
                              @Verify(param = "auditingStatus",rule="") Integer auditingStatus,
                              @Verify(param = "auditingDate",rule="") String auditingDate,
                              @Verify(param = "equipmentNumber",rule="") String equipmentNumber,
                              @Verify(param = "gpsBrand",rule="") String gpsBrand,
                              @Verify(param = "gpsType",rule="") String gpsType,
                              @Verify(param = "gpsImei",rule="") String gpsImei,
                              @Verify(param = "gpsDate",rule="") String gpsDate,
                              @RequestParam(value = "purchaseDate",required = false) String memo,
                                  @Verify(param = "licensePlates1",rule="") String licensePlates1,
                                  @Verify(param = "oldCity",rule="") Integer oldCity,
                                  @Verify(param = "oldSupplierId",rule="") Integer oldSupplierId) {
        logger.info("车辆保存/修改:saveCarInfo");
        CarInfo params = new CarInfo();
        params.setLicensePlates(licensePlates);

        if(null != carId){
            if(StringUtils.isBlank(licensePlates1))
                return AjaxResponse.fail(998, "licensePlates1");
            if(oldCity == null)
                return AjaxResponse.fail(998, "oldCity");
            if(oldSupplierId == null)
                return AjaxResponse.fail(998, "oldSupplierId");
            if(licensePlates.equals(licensePlates1)){
                if(!carService.checkLicensePlates(params))
                    return AjaxResponse.fail(1102);
            }

        } else {
            if(!carService.checkLicensePlates(params))
                return AjaxResponse.fail(1102);
        }



        params.setCarId(carId);

        params.setStatus(status);
        params.setCityId(cityId);
        params.setSupplierId(supplierId);
        params.setCarModelId(carModelId);
        params.setImageUrl(imageUrl);
        params.setVehicleDrivingLicense(vehicleDrivingLicense);

        params.setModelDetail(modelDetail);
        params.setColor(color);
        params.setClicensePlatesColor(clicensePlatesColor);
        params.setEngineNo(engineNo);
        params.setVehicleEngineDisplacement(vehicleEngineDisplacement);
        params.setVehicleEnginePower(vehicleEnginePower);
        params.setCarryPassengers(carryPassengers);
        params.setVehicleBrand(vehicleBrand);
        params.setVehicleEngineWheelbase(vehicleEngineWheelbase);
        params.setVehicleOwner(vehicleOwner);
        params.setFrameNo(frameNo);
        params.setVehicleType(vehicleType);
        params.setFuelType(fuelType);
        params.setNextInspectDate(nextInspectDate);
        params.setNextMaintenanceDate(nextMaintenanceDate);

        params.setRentalExpireDate(rentalExpireDate);
        params.setPurchaseDate(purchaseDate);
        params.setVehicleRegistrationDate(vehicleRegistrationDate);
        params.setTransportNumber(transportNumber);
        params.setCertificationAuthority(certificationAuthority);
        params.setOperatingRegion(operatingRegion);
        params.setTransportNumberDateStart(transportNumberDateStart);
        params.setTransportNumberDateEnd(transportNumberDateEnd);
        params.setFirstDate(firstDate);
        params.setOverHaulStatus(overHaulStatus);
        params.setAuditingStatus(auditingStatus);
        params.setAuditingDate(auditingDate);
        params.setEquipmentNumber(equipmentNumber);
        params.setGpsBrand(gpsBrand);
        params.setGpsType(gpsType);
        params.setGpsImei(gpsImei);
        params.setGpsDate(gpsDate);
        params.setMemo(memo);
        params.setLicensePlates1(licensePlates1);
        params.setOldCity(oldCity);
        params.setOldSupplierId(oldSupplierId);

        params.setNextOperationDate( nextOperationDate );
        params.setNextSecurityDate( nextSecurityDate);
        params.setNextClassDate( nextClassDate);
        params.setTwoLevelMaintenanceDate( twoLevelMaintenanceDate);

        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Integer userId = WebSessionUtil.getCurrentLoginUser().getId();
            params.setUpdateBy(userId);
            if (params.getCarId() == null) {
                logger.info("*********操作类型：新建");
                params.setCreateBy(userId);
            }else {
                logger.info("*********操作类型：修改");
            }
            result = this.carService.saveCarInfo(params);
            logger.info("saveCar-result-result={}" + result);
        } catch (Exception e) {
            logger.error("save CarInfo error. ", e);
        }
        return getResponse(result);
    }


    /**
     * 车辆信息删除
     * @param carIds
     */
    @RequestMapping(value = "/deleteCarInfo")
    public Object deleteCarInfo(@Verify(param = "carIds", rule = "required") String carIds) {
        logger.info("车辆删除:deleteCarInfo");
        Map<String, Object> result = new HashMap<String, Object>();
        String message = "";
        try {
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
//                params.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
                result = this.carService.saveCarInfo(carInfoEntity);
            }
        } catch (Exception e) {
            logger.error("delete CarInfo error. ", e);
        }
        if(StringUtils.isNotEmpty(message)){
            result.put(Common.RESULT_RESULT, 0);
            result.put("exception", message);
            return getResponse(result);
        }
        result.put(Common.RESULT_RESULT, 1);
        result.put(Common.RESULT_ERRORMSG, "成功");
        return getResponse(result);
    }


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
    public Object importCarInfo(@RequestParam("fileName") MultipartFile fileName, HttpServletRequest request) {
        logger.info("车辆信息导入保存:importCarInfo,参数" + fileName);
        Map<String, Object> result = new HashMap<String, Object>();
        result = this.carService.importCarInfo(fileName, request);
        return result;
    }

    /**
     * 车辆信息导出
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
    @RequestMapping("/exportCarInfo")
    public void exportCarInfo(String cities,
                              String supplierIds,
                              String carModelIds,
                              String licensePlates,
                              String createDateBegin,
                              String createDateEnd,
                              Integer status,
                              Integer isFree,
                              HttpServletRequest request, HttpServletResponse response){
        logger.info("exportCarInfo:车辆信息导出");
        try {
            CarInfo params = new CarInfo();
            params.setCities(cities);
            params.setSupplierIds(supplierIds);
            params.setCarModelIds(carModelIds);
            params.setLicensePlates(licensePlates);
            params.setCreateDateBegin(createDateBegin);
            params.setCreateDateEnd(createDateEnd);
            params.setStatus(status);
            params.setIsFree(isFree);

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
    @RequestMapping(value = "/fileDownloadCarInfo")
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
                if(success != null)
                    response.put("success", success);
                if(error != null)
                    response.put("error", error);
                return AjaxResponse.success(response);
            }
            return AjaxResponse.fail(999);
        } catch (Exception e){
            return AjaxResponse.fail(999);
        }
    }
}
