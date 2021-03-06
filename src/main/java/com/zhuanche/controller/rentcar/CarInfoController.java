package com.zhuanche.controller.rentcar;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.rentcar.CarInfoDTO;
import com.zhuanche.entity.mdbcarmanage.CarAdmUser;
import com.zhuanche.entity.rentcar.CarInfo;
import com.zhuanche.serv.authc.UserManagementService;
import com.zhuanche.serv.rentcar.CarInfoService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.Common;
import com.zhuanche.util.excel.CsvUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.zhuanche.common.enums.MenuEnum.*;

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
     * @param cities    ??????id?????????????????????
     * @param supplierIds   ?????????id?????????????????????
     * @param carModelIds   ??????id?????????????????????
     * @param licensePlates ?????????
     * @param createDateBegin   ????????????
     * @param createDateEnd     ????????????
     * @param status        ????????????
     * @param isFree        ????????????
     * @return
     */
    @RequestMapping(value = "/queryCarData")
	@RequiresPermissions(value = { "CarInfoManageSearch_look" } )
    @MasterSlaveConfigs(configs={ 
			@MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE )
	} )
    @RequestFunction(menu = CAR_INFO_MANAGE_LIST)
    public Object queryCarData( String cities, String supplierIds,
                               String carModelIds,
                              String licensePlates,
                              String createDateBegin,
                                String createDateEnd,
                               @Verify(param = "status",rule="") Integer status,
                               @Verify(param = "isFree",rule="") Integer isFree,
                               @Verify(param = "page",rule="") Integer page,
                               @Verify(param = "pageSize",rule = "max(50)") Integer pageSize) {

        CarInfo params = new CarInfo();
    try{
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
        logger.info("??????????????????????????????:"+JSON.toJSONString(params));
        PageInfo<CarInfo> pageInfo = carService.findPageByCarInfo(params,params.getPage(),params.getPagesize());
        carService.transContent(pageInfo.getList());
        List<CarInfo> rows =  pageInfo.getList();

        return AjaxResponse.success(new PageDTO(params.getPage(), params.getPagesize(), Integer.parseInt(pageInfo.getTotal()+""),rows));
    }catch (Exception e){
        e.printStackTrace();
        logger.error("???????????????????????????????????????"+JSON.toJSONString(params),e);
        return null;
    }
    }

    /**
     * ??????????????????
     * @param carId
     * @return
     */
    @RequestMapping(value = "/queryCarInfo")
    @MasterSlaveConfigs(configs={ 
			@MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE ),
			@MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE)
	} )
    @RequestFunction(menu = CAR_INFO_MANAGE_DETAIL)
    public AjaxResponse queryCarInfo(@Verify(param = "carId",rule = "required") Integer carId) {
        logger.info("queryCar:????????????????????????");
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
     * ????????????????????????????????????
     * @param licensePlates
     * @return
     */
//    @RequestMapping(value = "/checkLicensePlates")
//    public Object checkLicensePlates(@Verify(param = "licensePlates", rule = "required") String licensePlates) {
//        logger.info("????????????????????????????????????:checkLicensePlates");
//        CarInfo params = new CarInfo();
//        params.setLicensePlates(licensePlates);
//        return getResponse(carService.checkLicensePlates(params) );
//    }

    /**
     * ??????
     * @param carId ??????
     * @param licensePlates ?????????
     * @param status ????????????
     * @param cityId ??????
     * @param supplierId ?????????
     * @param carModelId ??????
     * @param imageUrl ??????????????????
     * @param vehicleDrivingLicense ??????????????????
     * @param modelDetail ????????????
     * @param color ??????
     * @param clicensePlatesColor ????????????
     * @param engineNo ????????????
     * @param vehicleEngineDisplacement ?????????????????????
     * @param vehicleEnginePower ???????????????
     * @param carryPassengers ???????????????
     * @param frameNo ?????????
     * @param vehicleBrand ????????????
     * @param vehicleEngineWheelbase ????????????
     * @param vehicleOwner ????????????
     * @param vehicleType ?????????????????????????????????????????????
     * @param fuelType ??????????????????
     * @param nextInspectDate ?????????????????? (??????:yyyy-MM-dd)
     * @param nextMaintenanceDate ?????????????????? (??????:yyyy-MM-dd)
     * @param nextOperationDate ??????????????????????????? (??????:yyyy-MM-dd)
     * @param nextSecurityDate ??????????????????????????? (??????:yyyy-MM-dd)
     * @param nextClassDate ???????????????????????? (??????:yyyy-MM-dd)
     * @param twoLevelMaintenanceDate ?????????????????? (??????:yyyy-MM-dd)
     * @param rentalExpireDate ?????????????????? (??????:yyyy-MM-dd)
     * @param purchaseDate ???????????? (??????:yyyy-MM-dd)
     * @param vehicleRegistrationDate ?????????????????? (??????:yyyy-MM-dd)
     * @param transportNumber ???????????????
         * @param certificationAuthority ???????????????????????????
     * @param operatingRegion ??????????????????
     * @param transportNumberDateStart ??????????????????????????? (??????:yyyy-MM-dd)
     * @param transportNumberDateEnd ??????????????????????????? (??????:yyyy-MM-dd)
     * @param firstDate ???????????????????????? (??????:yyyy-MM-dd)
     * @param overHaulStatus ??????????????????
        * @param auditingStatus ????????????????????????
     * @param auditingDate ???????????????????????? (??????:yyyy-MM-dd)
     * @param equipmentNumber ???????????????????????????
     * @param gpsBrand ????????????????????????
     * @param gpsType ????????????????????????
     * @param gpsImei ??????????????????IMEI???
     * @param gpsDate ??????????????????????????????(??????:yyyy-MM-dd)
     * @param licensePlates1 ???????????????
     * @param oldCity ?????????Id
     * @param oldSupplierId ???????????????Ids
     * @param memo ??????
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
        logger.info("????????????/??????:saveCarInfo");
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
                logger.info("*********?????????????????????");
                params.setCreateBy(userId);
            }else {
                logger.info("*********?????????????????????");
            }
            result = this.carService.saveCarInfo(params);
            logger.info("saveCar-result-result={}" + result);
        } catch (Exception e) {
            logger.error("save CarInfo error. ", e);
        }
        return getResponse(result);
    }


    /**
     * ??????????????????
     * @param carIds
     */
    @RequestMapping(value = "/deleteCarInfo")
    public Object deleteCarInfo(@Verify(param = "carIds", rule = "required") String carIds) {
        logger.info("????????????:deleteCarInfo");
        Map<String, Object> result = new HashMap<String, Object>();
        String message = "";
        try {
            if(StringUtils.isEmpty(carIds)){
                result.put(Common.RESULT_RESULT, 0);
                result.put(Common.RESULT_ERRORMSG, "????????????");
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
                    message += licensePlates + "????????????????????????????????????;";
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
        result.put(Common.RESULT_ERRORMSG, "??????");
        return getResponse(result);
    }


    /**
     * ????????????????????????
     */
    @RequestMapping(value = "/importDeleteCarInfo")
    public Object importDeleteCarInfo(CarInfo params, HttpServletRequest request) {
        logger.info("????????????????????????");
        Map<String, Object> result = new HashMap<String, Object>();
        result = this.carService.importDeleteCarInfo(params, request);
        return result;
    }

    /**
     * ??????????????????
     */
    @RequestMapping(value = "/importCarInfo")
    public AjaxResponse importCarInfo(@RequestParam("fileName") MultipartFile fileName, HttpServletRequest request) {
        logger.info("????????????????????????:importCarInfo,??????" + fileName);
        Map<String, Object> result = new HashMap<String, Object>();
        return carService.importCarInfo(fileName, request);
    }

    /**
     * ??????????????????
     * @param cities    ??????id?????????????????????
     * @param supplierIds   ?????????id?????????????????????
     * @param carModelIds   ??????id?????????????????????
     * @param licensePlates ?????????
     * @param createDateBegin   ????????????
     * @param createDateEnd     ????????????
     * @param status        ????????????
     * @param isFree        ????????????
     * @return
     */

    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE )
    } )
    @RequestMapping("/exportCarInfo")
	@RequiresPermissions(value = { "CarInfoManageSearch_export" } )
    @RequestFunction(menu = CAR_INFO_MANAGE_EXPORT)
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
            params.setPagesize(CsvUtils.downPerSize);
            params.setPage(1);

            logger.info("exportCarInfo:???????????????????????????????????????"+ JSON.toJSONString(params));

            /*@SuppressWarnings("deprecation")
            Workbook wb = this.carService.exportExcel(params,request.getRealPath("/")+File.separator+"template"+File.separator+"car_info.xlsx");
            exportExcelFromTemplet(request, response, wb, new String("??????????????????".getBytes("gb2312"), "iso8859-1"));*/
            List<String> header = new ArrayList<>();
            header.add("?????????,??????,??????,?????????,??????,????????????,????????????,??????,????????????,?????????,??????????????????,??????????????????,??????????????????,????????????????????????," +
                    "????????????????????????,????????????????????????,??????????????????,???????????????,????????????,????????????,??????VIN???,??????????????????,??????????????????,???????????????????????????," +
                    "???????????????????????????,????????????????????????,???????????????,???????????????????????????,??????????????????,???????????????????????????,???????????????????????????,????????????????????????," +
                    "??????????????????,????????????????????????,????????????????????????,???????????????????????????,????????????????????????,????????????????????????,??????????????????IMEI???,??????????????????????????????," +
                    "?????????,????????????,?????????,????????????,??????,????????????,????????????,????????????(???????????????????????????),?????????????????????????????????");
            String fileName = "????????????" + com.zhuanche.util.dateUtil.DateUtil.dateFormat(new Date(), com.zhuanche.util.dateUtil.DateUtil.intTimestampPattern)+".csv";
            String agent = request.getHeader("User-Agent").toUpperCase(); //???????????????????????????????????????
            if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO")>0 && agent.indexOf("RV:11")>0)) {  //IE????????????Edge?????????
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } else {  //???????????????
                fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            }

            PageInfo<CarInfo> pageInfos = carService.findPageByCarInfo(params,params.getPage(),params.getPagesize());


            CsvUtils entity = new CsvUtils();
            List<CarInfo> carInfoList  = pageInfos.getList();
            List<String> csvDataList = new ArrayList<>();
            if(carInfoList == null || carInfoList.size() == 0){
                csvDataList.add("?????????????????????????????????");

                entity.exportCsvV2(response,csvDataList,header,fileName,true,true);
                return "?????????????????????????????????";
            }else{
                int pages = pageInfos.getPages();//?????????????????????
                boolean isFirst = true;
                boolean isLast = false;
                if(pages == 1 ||pages == 0 ){
                    isLast = true;
                }

                carService.doTrans4Csv(csvDataList,carInfoList);
                entity.exportCsvV2(response,csvDataList,header,fileName,isFirst,isLast);
                isFirst = false;
                for(int pageNumber = 2;pageNumber <= pages ; pageNumber++){

                    params.setPage(pageNumber);

                    pageInfos =  carService.findPageByCarInfo(params,params.getPage(),params.getPagesize());
                    logger.info("???????????????????????????????????????"+ JSON.toJSONString(params)+",???"+pageNumber+"?????????????????????"+pages);
                    csvDataList = new ArrayList<>();
                    if(pageNumber == pages){
                        isLast = true;
                    }
                    carInfoList  = pageInfos.getList();
                    carService.doTrans4Csv(csvDataList,carInfoList);
                    entity.exportCsvV2(response,csvDataList,header,fileName,isFirst,isLast);
                }

                long  end = System.currentTimeMillis();

                logger.info("?????????????????????????????????????????????"+ JSON.toJSONString(params)+",?????????"+(end-start)+"??????");
            }

        } catch (IOException e) {
            long  end = System.currentTimeMillis();
            logger.error("?????????????????????????????????????????????"+ JSON.toJSONString(params)+",?????????"+(end-start)+"??????",e);
        } catch (Exception e) {
            long  end = System.currentTimeMillis();
            logger.error("?????????????????????????????????????????????"+ JSON.toJSONString(params)+",?????????"+(end-start)+"??????",e);
        }
        return "";
    }

    /**
     * ????????????????????????
     */
    @RequestMapping(value = "/fileDownloadCarInfo")
    public void fileDownloadCarInfo(HttpServletRequest request,
                                    HttpServletResponse response) {

        String path = request.getRealPath("/") + File.separator + "upload"
                + File.separator + "IMPORTCARINFO.xlsx";
        fileDownload(request,response,path);
    }

    /*
     * ??????
     */
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

    public void exportExcelFromTemplet(HttpServletRequest request, HttpServletResponse response, Workbook wb, String fileName) throws IOException {
        if(StringUtils.isEmpty(fileName)) {
            fileName = "exportExcel";
        }
        response.setHeader("Content-Disposition","attachment;filename="+fileName+".xlsx");//????????????????????????
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
