package com.zhuanche.controller.rentcar;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.rentcar.CarInfoDTO;
import com.zhuanche.entity.mdbcarmanage.CarAdmUser;
import com.zhuanche.entity.mdbcarmanage.DriverDailyReport;
import com.zhuanche.entity.rentcar.CarInfo;
import com.zhuanche.serv.authc.UserManagementService;
import com.zhuanche.serv.rentcar.CarInfoService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.Common;
import com.zhuanche.util.excel.CsvUtils;
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
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

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
    @MasterSlaveConfigs(configs={ 
			@MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE )
	} )
    public Object queryCarData(@Verify(param = "cities",rule="") String cities,
                               @Verify(param = "supplierIds",rule="") String supplierIds,
                               @Verify(param = "carModelIds",rule="") String carModelIds,
                               @Verify(param = "licensePlates",rule="") String licensePlates,
                               @Verify(param = "createDateBegin",rule="") String createDateBegin,
                               @Verify(param = "createDateEnd",rule="") String createDateEnd,
                               @Verify(param = "status",rule="") Integer status,
                               @Verify(param = "isFree",rule="") Integer isFree,
                               @Verify(param = "page",rule="") Integer page,
                               @Verify(param = "pageSize",rule = "max(50)") Integer pageSize) {
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

        PageInfo<CarInfo> pageInfo = carService.findPageByCarInfo(params,params.getPage(),params.getPagesize());
        List<CarInfo> rows =  pageInfo.getList();

        return AjaxResponse.success(new PageDTO(params.getPage(), params.getPagesize(), Integer.parseInt(pageInfo.getTotal()+""),rows));
    }

    /**
     * 查询车辆详情
     * @param carId
     * @return
     */
    @RequestMapping(value = "/queryCarInfo")
    @MasterSlaveConfigs(configs={ 
			@MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE ),
			@MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE)
	} )
    public AjaxResponse queryCarInfo(@Verify(param = "carId",rule = "required") Integer carId) {
        logger.info("queryCar:查看车辆详情列表");
        CarInfo params = new CarInfo();
        params.setCarId(carId);
        params = this.carService.selectCarInfoByCarId(params);
        if(params!=null){
            params.setCarPhotograph(params.getImageUrl());
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
                              @RequestParam(value = "carPhotograph", required = false) String imageUrl,
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
                              @RequestParam(value = "memo",required = false) String memo,
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
            if(!licensePlates.equals(licensePlates1)){
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
    public AjaxResponse importCarInfo(@RequestParam("fileName") MultipartFile fileName, HttpServletRequest request) {
        logger.info("车辆信息导入保存:importCarInfo,参数" + fileName);
        Map<String, Object> result = new HashMap<String, Object>();
        return carService.importCarInfo(fileName, request);
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
    public String exportCarInfo(@Verify(param = "cities",rule="required")String cities,
                              @Verify(param = "supplierIds",rule="required")String supplierIds,
                              String carModelIds,
                              String licensePlates,
                              String createDateBegin,
                              String createDateEnd,
                              Integer status,
                              Integer isFree,
                              HttpServletRequest request, HttpServletResponse response){

        long  start = System.currentTimeMillis();
        CarInfo params = new CarInfo();
        try {
            params.setCities(cities);
            params.setSupplierIds(supplierIds);
            params.setCarModelIds(carModelIds);
            params.setLicensePlates(licensePlates);
            params.setCreateDateBegin(createDateBegin);
            params.setCreateDateEnd(createDateEnd);
            params.setStatus(status);
            params.setIsFree(isFree);

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
            params.setPagerSize(CsvUtils.downPerSize);



            logger.info("exportCarInfo:车辆信息导出，请求参数为："+ JSON.toJSONString(params));

            /*@SuppressWarnings("deprecation")
            Workbook wb = this.carService.exportExcel(params,request.getRealPath("/")+File.separator+"template"+File.separator+"car_info.xlsx");
            exportExcelFromTemplet(request, response, wb, new String("车辆信息列表".getBytes("gb2312"), "iso8859-1"));*/
            List<String> header = new ArrayList<>();
            header.add("车牌号,城市,状态,供应商,车型,具体车型,购买日期,颜色,发动机号,车架号,下次车检时间,下次维保时间,租赁到期时间,下次等级鉴定时间," +
                    "下次检营运证时间,下次检治安证时间,二级维户时间,核定载客位,车辆厂牌,车牌颜色,车辆VIN码,车辆注册日期,车辆燃料类型,发动机排量（毫升）," +
                    "发动机功率（千瓦）,车辆轴距（毫米）,运输证字号,车辆运输证发证机构,车辆经营区域,车辆运输证有效期起,车辆运输证有效期止,车辆初次登记日期," +
                    "车辆检修状态,车辆年度审验状态,车辆年度审验日期,发票打印设备序列号,卫星定位装置品牌,卫星定位装置型号,卫星定位装置IMEI号,卫星定位设备安装日期," +
                    "创建人,创建时间,修改人,修改时间,备注,司机姓名,所属车主,车辆类型(以机动车行驶证为主)");
            String fileName = "车辆信息" + com.zhuanche.util.dateUtil.DateUtil.dateFormat(new Date(), com.zhuanche.util.dateUtil.DateUtil.intTimestampPattern)+".csv";
            String agent = request.getHeader("User-Agent").toUpperCase(); //获得浏览器信息并转换为大写
            if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO")>0 && agent.indexOf("RV:11")>0)) {  //IE浏览器和Edge浏览器
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } else {  //其他浏览器
                fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            }

            PageInfo<CarInfo> pageInfos = carService.findPageByCarInfo(params,params.getPage(),params.getPagesize());


            CsvUtils entity = new CsvUtils();
            List<CarInfo> carInfoList  = pageInfos.getList();
            List<String> csvDataList = new ArrayList<>();
            if(carInfoList == null || carInfoList.size() == 0){
                csvDataList.add("没有查到符合条件的数据");

                CsvUtils.exportCsvV2(response,csvDataList,header,fileName,true,true,entity);
                return "没有查到符合条件的数据";
            }else{
                int pages = pageInfos.getPages();//临时计算总页数
                boolean isFirst = true;
                boolean isLast = false;
                if(pages == 1 ||pages == 0 ){
                    isLast = true;
                }
                carService.doTrans4Csv(csvDataList,carInfoList);
                CsvUtils.exportCsvV2(response,csvDataList,header,fileName,isFirst,isLast,entity);

                for(int pageNumber = 2;pageNumber < pageInfos.getPages() ; pageNumber++){
                    params.setPage(pageNumber);
                    pageInfos =  carService.findPageByCarInfo(params,params.getPage(),params.getPagesize());
                    csvDataList = new ArrayList<>();
                    if(pageNumber == pages){
                        isLast = true;
                    }
                    carInfoList  = pageInfos.getList();
                    carService.doTrans4Csv(csvDataList,carInfoList);
                    CsvUtils.exportCsvV2(response,csvDataList,header,fileName,isFirst,isLast,entity);
                }

                long  end = System.currentTimeMillis();

                logger.info("车辆信息导出成功，请求参数为："+ JSON.toJSONString(params)+",耗时："+(end-start)+"毫秒");
            }

        } catch (IOException e) {
            long  end = System.currentTimeMillis();
            logger.error("车辆信息导出异常，请求参数为："+ JSON.toJSONString(params)+",耗时："+(end-start)+"毫秒",e);
        } catch (Exception e) {
            long  end = System.currentTimeMillis();
            logger.error("车辆信息导出异常，请求参数为："+ JSON.toJSONString(params)+",耗时："+(end-start)+"毫秒",e);
        }
        return "";
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
