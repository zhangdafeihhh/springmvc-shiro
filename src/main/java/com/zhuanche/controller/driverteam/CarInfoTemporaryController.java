package com.zhuanche.controller.driverteam;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.exception.ServiceException;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.*;
import com.zhuanche.dto.mdbcarmanage.CarBizCarInfoTempDTO;
import com.zhuanche.entity.driver.DriverBrand;
import com.zhuanche.entity.driver.DriverVehicle;
import com.zhuanche.entity.mdbcarmanage.CarBizCarInfoTemp;
import com.zhuanche.entity.rentcar.CarBizModel;
import com.zhuanche.serv.deiver.CarBizCarInfoTempService;
import com.zhuanche.serv.financial.DriverBrandService;
import com.zhuanche.serv.financial.DriverVehicleService;
import com.zhuanche.serv.rentcar.CarBizModelService;
import com.zhuanche.shiro.constants.BusConstant;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.excel.ExportExcelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.zhuanche.common.enums.MenuEnum.*;

/**
 * @author wzq
 */
@Controller
@RequestMapping(value = "/carInfoTemporary")
public class CarInfoTemporaryController extends BaseController {

    private static final Logger log =  LoggerFactory.getLogger(CarInfoTemporaryController.class);

	@Autowired
	private CarBizCarInfoTempService carBizCarInfoTempService;

	@Autowired
    private CarBizModelService carBizModelService;

    @Autowired
    private DriverVehicleService driverVehicleService;

    @Autowired
    private DriverBrandService driverBrandService;

    /**
     * 加盟商车辆查询
     * @param page 页数
     * @param pageSize 条数
     * @param licensePlates 车牌号
     * @param carModelIds 车型
     * @param cities 城市
     * @param supplierIds 供应商
     * @param createDateBegin 开始时间
     * @param createDateEnd 结束时间
     * @return
     */
	@ResponseBody
	@RequestMapping(value = "/queryCarData", method =  RequestMethod.GET )
	@RequiresPermissions(value = { "SupplierCarEntry_look" } )
	@MasterSlaveConfigs(configs={ 
			@MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE ),
			@MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE)
	} )
    @RequestFunction(menu = CAR_JOIN_APPLY_LIST)
	public AjaxResponse queryCarData(@RequestParam(value = "page",defaultValue="1") Integer page,
                                     @RequestParam(value = "pageSize",defaultValue="10") Integer pageSize,
                                     @RequestParam(value = "licensePlates",required = false,defaultValue = "") String licensePlates,
                                     @RequestParam(value = "carModelIds",required = false,defaultValue = "") String carModelIds,
                                     @RequestParam(value = "cities",required = false,defaultValue = "") String cities,
                                     @RequestParam(value = "supplierIds",required = false,defaultValue = "") String supplierIds,
                                     @RequestParam(value = "createDateBegin",required = false,defaultValue = "") String createDateBegin,
                                     @RequestParam(value = "createDateEnd",required = false,defaultValue = "") String createDateEnd) {
	    long total = 0;
        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        Map<String,Object> params = Maps.newHashMap();
        String sessionCities = StringUtils.join(currentLoginUser.getCityIds().toArray(), ",");
        String sessionSupplierIds = StringUtils.join(currentLoginUser.getSupplierIds().toArray(), ",");
        params.put("licensePlates",licensePlates);
        params.put("createDateBegin",createDateBegin);
        params.put("createDateEnd",createDateEnd);
        if(StringUtils.isNotBlank(carModelIds)){
            params.put("carModelIds",carModelIds);
        }
        if(StringUtils.isNotBlank(cities)){
            params.put("cities",cities);
        }else{
            params.put("cities",sessionCities);
        }
        if(StringUtils.isNotBlank(supplierIds)){
            params.put("supplierIds",supplierIds);
        }else{
            params.put("supplierIds",sessionSupplierIds);
        }
        PageHelper.startPage(page, pageSize,true);
        List<CarBizCarInfoTemp> carBizCarInfoTempList = null;
        try{
            log.info("查询车辆临时表数据,请求参数:{}", JSON.toJSONString(params));
            carBizCarInfoTempList = carBizCarInfoTempService.queryForPageObject(params);
            PageInfo<CarBizCarInfoTemp> pageInfo = new PageInfo<>(carBizCarInfoTempList);
            total = pageInfo.getTotal();
        }finally {
            PageHelper.clearPage();
        }
        if(carBizCarInfoTempList == null || carBizCarInfoTempList.size() == 0){
            PageDTO pageDto = new PageDTO(page,pageSize,0,new ArrayList());
            return AjaxResponse.success(pageDto);
        }else{
            for (CarBizCarInfoTemp carBizCarInfoTemp:carBizCarInfoTempList) {
                /*
                 * add by mingku.jia
                 * 1.查询城市名字-car_biz_car_info_temp中cityId直接获取名字
                 * 2.供应商名字->car_biz_car_info_temp中supplierId直接获取名字
                 * 3.车辆的模型名字->car_biz_car_info_temp(car_model_id)->在rentcar.car_biz_model;
                 * 4.获取品牌的名字->car_biz_car_info_temp(car_model_id)->在去获取mp-driver.driver_vehicle(品牌id)->mp-driver.driver_brand.
                 */
                Map<String, Object> result = super.querySupplierName(carBizCarInfoTemp.getCityId(), carBizCarInfoTemp.getSupplierId());
                carBizCarInfoTemp.setCityName((String)result.get("cityName"));
                carBizCarInfoTemp.setSupplierName((String)result.get("supplierName"));
                if(carBizCarInfoTemp.getCarModelId() != null){
                    CarBizModel carBizModel = carBizModelService.selectByPrimaryKey(carBizCarInfoTemp.getCarModelId());
                    if(carBizModel!=null){
                        carBizCarInfoTemp.setModeName(carBizModel.getModelName());
                    }
                    DriverVehicle driverVehicle = driverVehicleService.queryByModelId(carBizCarInfoTemp.getCarModelId());
                    if(driverVehicle != null){
                        Long brandId =   driverVehicle.getBrandId();
                        carBizCarInfoTemp.setNewBrandId(brandId);
                        if(brandId != null){
                            DriverBrand driverBrand = driverBrandService.getDriverBrandByPrimaryKey(brandId);
                            if(driverBrand != null){
                                carBizCarInfoTemp.setNewBrandName(driverBrand.getBrandName());
                            }
                        }
                    }
                }

            }
        }
        List<CarBizCarInfoTempDTO> carBizCarInfoTempDTOList = BeanUtil.copyList(carBizCarInfoTempList,CarBizCarInfoTempDTO.class);

        if (CollectionUtils.isNotEmpty(carBizCarInfoTempDTOList)) {
            carBizCarInfoTempService.buildAuditStatusInfo(carBizCarInfoTempDTOList);
        }
        PageDTO pageDto = new PageDTO(page,pageSize,(int)total,carBizCarInfoTempDTOList);
        return AjaxResponse.success(pageDto);
	}


    /**
     * 车辆删除
     * @param carIds
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/deleteCarInfo", method =  RequestMethod.POST )
    @RequestFunction(menu = CAR_JOIN_APPLY_DELETE)
    public AjaxResponse deleteCarInfo(@Verify(param = "carIds",rule="required") String carIds) {
        log.info("车辆删除:deleteCarInfo,请求参数:"+carIds);
        int code = carBizCarInfoTempService.delete(carIds);
        if(code > 0 ){
            return AjaxResponse.success(RestErrorCode.SUCCESS);
        }else{
            return AjaxResponse.success(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    /**
     * 下载车辆导入模板
     * @param request
     * @param response
     */
    @RequestMapping(value = "/fileDownloadCarInfo",method =  RequestMethod.GET)
	@RequiresPermissions(value = { "SupplierCarEntry_download" } )
    @RequestFunction(menu = CAR_JOIN_APPLY_TEMPLATE_DOWNLOAD)
    public void fileDownloadCarInfo(HttpServletRequest request, HttpServletResponse response) {
        String path = request.getRealPath("/") + File.separator + "upload" + File.separator + "IMPORTCARINFO.xlsx";
        super.fileDownload(request,response,path);
    }

    /**
     * 下载巴士模板
     * @param response
     * @throws IOException
     */
    @RequestMapping(value="/exportBusExcel",method =  RequestMethod.GET)
    public void exportExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream;charset=ISO8859-1");
        response.setHeader("Content-Disposition", "attachment;filename=" + new String(BusConstant.BusExcel.SHEET_NAME.getBytes("GB2312"), "ISO8859-1") + ".xls");
        response.addHeader("Pargam", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        new ExportExcelUtil<>().exportExcel(BusConstant.BusExcel.SHEET_NAME, BusConstant.BusExcel.BUS_EXCLE_TITLE, response.getOutputStream());
    }

    /**
     * 查询详情
     * @param carId 主键Id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryCarInfo", method = RequestMethod.GET )
    @MasterSlaveConfigs(configs={ 
			@MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE ),
			@MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE)
	} )
    @RequestFunction(menu = CAR_JOIN_APPLY_DETAIL)
    public AjaxResponse queryCarInfo(@Verify(param = "carId",rule="required") Integer carId) {
        log.info("queryCarInfo:查看车辆详情Id:"+carId);
        Map<String,Object> params = Maps.newHashMap();
        params.put("carId",carId);
        CarBizCarInfoTemp carBizCarInfoTemp = carBizCarInfoTempService.queryForObject(params);
        if(carBizCarInfoTemp != null){
            Map<String, Object> result = super.querySupplierName(carBizCarInfoTemp.getCityId(), carBizCarInfoTemp.getSupplierId());
            carBizCarInfoTemp.setCityName((String)result.get("cityName"));
            carBizCarInfoTemp.setSupplierName((String)result.get("supplierName"));
            Integer carModelId = carBizCarInfoTemp.getCarModelId();
            if(carModelId != null){
                CarBizModel carBizModel = carBizModelService.selectByPrimaryKey(carBizCarInfoTemp.getCarModelId());
                if(carBizModel!=null){
                    carBizCarInfoTemp.setModeName(carBizModel.getModelName());
                }
                DriverVehicle driverVehicle = driverVehicleService.queryByModelId(carBizCarInfoTemp.getCarModelId());
                if(driverVehicle != null){
                    Long brandId =   driverVehicle.getBrandId();
                    carBizCarInfoTemp.setNewBrandId(brandId);
                    if(brandId != null){
                        DriverBrand driverBrand = driverBrandService.getDriverBrandByPrimaryKey(brandId);
                        if(driverBrand != null){
                            carBizCarInfoTemp.setNewBrandName(driverBrand.getBrandName());
                        }
                    }
                }
            }
        }else{
            return AjaxResponse.fail(RestErrorCode.BUS_NOT_EXIST);
        }
        CarBizCarInfoTempDTO carBizCarInfoTempDTO = BeanUtil.copyObject(carBizCarInfoTemp, CarBizCarInfoTempDTO.class);
        carBizCarInfoTempService.buildCarAuditStatusInfoListAndOpsList(carBizCarInfoTempDTO);
        return AjaxResponse.success(carBizCarInfoTempDTO);
    }

    /**
     * 新增
     * @param licensePlates 车牌号
     * @param cityId 城市
     * @param supplierId 供应商
     * @param carModelId 车型
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
     * @param vehicleDrivingLicense 车辆图片
     * @param carPhotograph 行驶证扫描件
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/add", method =  RequestMethod.POST )
    @RequestFunction(menu = CAR_JOIN_APPLY_ADD)
    public AjaxResponse saveCarInfo(@Verify(param = "licensePlates",rule="required") String licensePlates,
                                    @Verify(param = "cityId",rule="required") Integer cityId,
                                    @Verify(param = "supplierId",rule="required") Integer supplierId,
                                    @Verify(param = "carModelId",rule="required") Integer carModelId,
                                    @Verify(param = "modelDetail",rule="required|maxLength(24)") String modelDetail,
                                    @Verify(param = "color",rule="required") String color,
                                    @Verify(param = "clicensePlatesColor",rule="required") String clicensePlatesColor,
                                    @Verify(param = "engineNo",rule="required") String engineNo,
                                    @Verify(param = "vehicleEngineDisplacement",rule="required") String vehicleEngineDisplacement,
                                    @Verify(param = "vehicleEnginePower",rule="required") String vehicleEnginePower,
                                    @Verify(param = "carryPassengers",rule="required") String carryPassengers,
                                    @Verify(param = "vehicleBrand",rule="required") String vehicleBrand,
                                    @Verify(param = "vehicleEngineWheelbase",rule="required") String vehicleEngineWheelbase,
                                    @Verify(param = "vehicleOwner",rule="required") String vehicleOwner,
                                    @Verify(param = "frameNo",rule="required") String frameNo,
                                    @Verify(param = "vehicleType",rule="required") String vehicleType,
                                    @Verify(param = "fuelType",rule="required") Integer fuelType,
                                    @RequestParam(value = "nextInspectDate",required = false) String nextInspectDate,
                                    @RequestParam(value = "nextMaintenanceDate",required = false) String nextMaintenanceDate,
                                    @RequestParam(value = "nextOperationDate",required = false) String nextOperationDate,
                                    @RequestParam(value = "nextSecurityDate",required = false) String nextSecurityDate,
                                    @RequestParam(value = "nextClassDate",required = false) String nextClassDate,
                                    @RequestParam(value = "twoLevelMaintenanceDate",required = false) String twoLevelMaintenanceDate,
                                    @RequestParam(value = "rentalExpireDate",required = false) String rentalExpireDate,
                                    @RequestParam(value = "purchaseDate",required = false) String purchaseDate,
                                    @Verify(param = "vehicleRegistrationDate",rule="required") String vehicleRegistrationDate,
                                    @Verify(param = "transportNumber",rule="required") String transportNumber,
                                    @Verify(param = "certificationAuthority",rule="required") String certificationAuthority,
                                    @Verify(param = "operatingRegion",rule="required") String operatingRegion,
                                    @Verify(param = "transportNumberDateStart",rule="required") String transportNumberDateStart,
                                    @Verify(param = "transportNumberDateEnd",rule="required") String transportNumberDateEnd,
                                    @Verify(param = "firstDate",rule="required") String firstDate,
                                    @Verify(param = "overHaulStatus",rule="required") Integer overHaulStatus,
                                    @Verify(param = "auditingStatus",rule="required") Integer auditingStatus,
                                    @Verify(param = "auditingDate",rule="required") String auditingDate,
                                    @Verify(param = "equipmentNumber",rule="required") String equipmentNumber,
                                    @Verify(param = "gpsBrand",rule="required") String gpsBrand,
                                    @Verify(param = "gpsType",rule="required") String gpsType,
                                    @Verify(param = "gpsImei",rule="required") String gpsImei,
                                    @Verify(param = "gpsDate",rule="required") String gpsDate,
                                    @RequestParam(value = "memo",required = false) String memo,
                                    @RequestParam(value = "vehicleDrivingLicense",required = false) String vehicleDrivingLicense,
                                    @RequestParam(value = "carPhotograph",required = false) String carPhotograph) {
        CarBizCarInfoTemp carBizCarInfoTemp = new CarBizCarInfoTemp();
        carBizCarInfoTemp.setLicensePlates(licensePlates);
        carBizCarInfoTemp.setCityId(cityId);
        carBizCarInfoTemp.setSupplierId(supplierId);
        carBizCarInfoTemp.setCarModelId(carModelId);
        carBizCarInfoTemp.setModelDetail(modelDetail);
        carBizCarInfoTemp.setColor(color);
        carBizCarInfoTemp.setClicensePlatesColor(clicensePlatesColor);
        carBizCarInfoTemp.setEngineNo(engineNo);
        carBizCarInfoTemp.setVehicleEngineDisplacement(vehicleEngineDisplacement);
        carBizCarInfoTemp.setVehicleEnginePower(vehicleEnginePower);
        carBizCarInfoTemp.setCarryPassengers(carryPassengers);
        carBizCarInfoTemp.setVehicleBrand(vehicleBrand);
        carBizCarInfoTemp.setVehicleEngineWheelbase(vehicleEngineWheelbase);
        carBizCarInfoTemp.setVehicleOwner(vehicleOwner);
        carBizCarInfoTemp.setFrameNo(frameNo);
        carBizCarInfoTemp.setVehicleType(vehicleType);
        carBizCarInfoTemp.setFuelType(fuelType);
        carBizCarInfoTemp.setNextInspectDate(StringUtils.isBlank(nextInspectDate)?null:nextInspectDate);
        carBizCarInfoTemp.setNextMaintenanceDate(StringUtils.isBlank(nextMaintenanceDate)?null:nextMaintenanceDate);
        carBizCarInfoTemp.setNextOperationDate(StringUtils.isBlank(nextOperationDate)?null:nextOperationDate);
        carBizCarInfoTemp.setNextSecurityDate(StringUtils.isBlank(nextSecurityDate)?null:nextSecurityDate);
        carBizCarInfoTemp.setNextClassDate(StringUtils.isBlank(nextClassDate)?null:nextClassDate);
        carBizCarInfoTemp.setTwoLevelMaintenanceDate(StringUtils.isBlank(twoLevelMaintenanceDate)?null:twoLevelMaintenanceDate);
        carBizCarInfoTemp.setRentalExpireDate(StringUtils.isBlank(rentalExpireDate)?null:rentalExpireDate);
        carBizCarInfoTemp.setPurchaseDate(StringUtils.isBlank(purchaseDate)?null:purchaseDate);
        carBizCarInfoTemp.setVehicleRegistrationDate(vehicleRegistrationDate);
        carBizCarInfoTemp.setTransportNumber(transportNumber);
        carBizCarInfoTemp.setCertificationAuthority(certificationAuthority);
        carBizCarInfoTemp.setOperatingRegion(operatingRegion);
        carBizCarInfoTemp.setTransportNumberDateStart(transportNumberDateStart);
        carBizCarInfoTemp.setTransportNumberDateEnd(transportNumberDateEnd);
        carBizCarInfoTemp.setFirstDate(firstDate);
        carBizCarInfoTemp.setOverHaulStatus(overHaulStatus);
        carBizCarInfoTemp.setAuditingStatus(auditingStatus);
        carBizCarInfoTemp.setAuditingDate(auditingDate);
        carBizCarInfoTemp.setEquipmentNumber(equipmentNumber);
        carBizCarInfoTemp.setGpsBrand(gpsBrand);
        carBizCarInfoTemp.setGpsType(gpsType);
        carBizCarInfoTemp.setGpsImei(gpsImei);
        carBizCarInfoTemp.setGpsDate(gpsDate);
        carBizCarInfoTemp.setMemo(StringUtils.isBlank(memo)?null:memo);
        SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
        Integer userId = user.getId();
        carBizCarInfoTemp.setUpdateBy(userId);
        carBizCarInfoTemp.setCreateBy(userId);

        if (StringUtils.isBlank(vehicleDrivingLicense)) {
            return AjaxResponse.failMsg(500, "行驶证图片url地址必须传递");
        }
        if (StringUtils.isBlank(carPhotograph)) {
            return AjaxResponse.failMsg(500, "车辆图片url地址必须传递");
        }

        carBizCarInfoTemp.setVehicleDrivingLicense(StringUtils.isBlank(vehicleDrivingLicense)?null:vehicleDrivingLicense);
        carBizCarInfoTemp.setCarPhotograph(StringUtils.isBlank(carPhotograph)?null:carPhotograph);

        return carBizCarInfoTempService.add(carBizCarInfoTemp);
    }

    /**
     * 修改
     * @param carId 主键
     * @param licensePlates 车牌号
     * @param cityId 城市
     * @param supplierId 供应商
     * @param carModelId 车型
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
     * @param oldLicensePlates 旧的车牌号
     * @param oldCity 旧城市Id
     * @param oldSupplierId 旧的供应商Id
     * @param vehicleDrivingLicense 车辆图片
     * @param carPhotograph 行驶证扫描件
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.GET} )
    @RequestFunction(menu = CAR_JOIN_APPLY_UPDATE)
    public AjaxResponse updateCarInfo(@Verify(param = "carId",rule="required") Integer carId,
                                    @Verify(param = "licensePlates",rule="required") String licensePlates,
                                    @Verify(param = "cityId",rule="required") Integer cityId,
                                    @Verify(param = "supplierId",rule="required") Integer supplierId,
                                    @Verify(param = "carModelId",rule="required") Integer carModelId,
                                    @Verify(param = "modelDetail",rule="required|maxLength(24)") String modelDetail,
                                    @Verify(param = "color",rule="required") String color,
                                    @Verify(param = "clicensePlatesColor",rule="required") String clicensePlatesColor,
                                    @Verify(param = "engineNo",rule="required") String engineNo,
                                    @Verify(param = "vehicleEngineDisplacement",rule="required") String vehicleEngineDisplacement,
                                    @Verify(param = "vehicleEnginePower",rule="required") String vehicleEnginePower,
                                    @Verify(param = "carryPassengers",rule="required") String carryPassengers,
                                    @Verify(param = "vehicleBrand",rule="required") String vehicleBrand,
                                    @Verify(param = "vehicleEngineWheelbase",rule="required") String vehicleEngineWheelbase,
                                    @Verify(param = "vehicleOwner",rule="required") String vehicleOwner,
                                    @Verify(param = "frameNo",rule="required") String frameNo,
                                    @Verify(param = "vehicleType",rule="required") String vehicleType,
                                    @Verify(param = "fuelType",rule="required") Integer fuelType,
                                    @RequestParam(value = "nextInspectDate",required = false) String nextInspectDate,
                                    @RequestParam(value = "nextMaintenanceDate",required = false) String nextMaintenanceDate,
                                    @RequestParam(value = "nextOperationDate",required = false) String nextOperationDate,
                                    @RequestParam(value = "nextSecurityDate",required = false) String nextSecurityDate,
                                    @RequestParam(value = "nextClassDate",required = false) String nextClassDate,
                                    @RequestParam(value = "twoLevelMaintenanceDate",required = false) String twoLevelMaintenanceDate,
                                    @RequestParam(value = "rentalExpireDate",required = false) String rentalExpireDate,
                                    @RequestParam(value = "purchaseDate",required = false) String purchaseDate,
                                    @Verify(param = "vehicleRegistrationDate",rule="required") String vehicleRegistrationDate,
                                    @Verify(param = "transportNumber",rule="required") String transportNumber,
                                    @Verify(param = "certificationAuthority",rule="required") String certificationAuthority,
                                    @Verify(param = "operatingRegion",rule="required") String operatingRegion,
                                    @Verify(param = "transportNumberDateStart",rule="required") String transportNumberDateStart,
                                    @Verify(param = "transportNumberDateEnd",rule="required") String transportNumberDateEnd,
                                    @Verify(param = "firstDate",rule="required") String firstDate,
                                    @Verify(param = "overHaulStatus",rule="required") Integer overHaulStatus,
                                    @Verify(param = "auditingStatus",rule="required") Integer auditingStatus,
                                    @Verify(param = "auditingDate",rule="required") String auditingDate,
                                    @Verify(param = "equipmentNumber",rule="required") String equipmentNumber,
                                    @Verify(param = "gpsBrand",rule="required") String gpsBrand,
                                    @Verify(param = "gpsType",rule="required") String gpsType,
                                    @Verify(param = "gpsImei",rule="required") String gpsImei,
                                    @Verify(param = "gpsDate",rule="required") String gpsDate,
                                    @RequestParam(value = "memo",required = false) String memo,
                                    @Verify(param = "oldLicensePlates",rule="required") String oldLicensePlates,
                                    @Verify(param = "oldCity",rule="required") Integer oldCity,
                                    @Verify(param = "oldSupplierId",rule="required") Integer oldSupplierId,
                                    @RequestParam(value = "vehicleDrivingLicense",required = false) String vehicleDrivingLicense,
                                    @RequestParam(value = "carPhotograph",required = false) String carPhotograph, String opType) {
        log.info("修改Id:"+carId +"当前提交类型:"+ opType);
        CarBizCarInfoTemp carBizCarInfoTemp = new CarBizCarInfoTemp();
        carBizCarInfoTemp.setCarId(carId);
        carBizCarInfoTemp.setLicensePlates(licensePlates);
        carBizCarInfoTemp.setCityId(cityId);
        carBizCarInfoTemp.setSupplierId(supplierId);
        carBizCarInfoTemp.setCarModelId(carModelId);
        carBizCarInfoTemp.setModelDetail(modelDetail);
        carBizCarInfoTemp.setColor(color);
        carBizCarInfoTemp.setClicensePlatesColor(clicensePlatesColor);
        carBizCarInfoTemp.setEngineNo(engineNo);
        carBizCarInfoTemp.setVehicleEngineDisplacement(vehicleEngineDisplacement);
        carBizCarInfoTemp.setVehicleEnginePower(vehicleEnginePower);
        carBizCarInfoTemp.setCarryPassengers(carryPassengers);
        carBizCarInfoTemp.setVehicleBrand(vehicleBrand);
        carBizCarInfoTemp.setVehicleEngineWheelbase(vehicleEngineWheelbase);
        carBizCarInfoTemp.setVehicleOwner(vehicleOwner);
        carBizCarInfoTemp.setFrameNo(frameNo);
        carBizCarInfoTemp.setVehicleType(vehicleType);
        carBizCarInfoTemp.setFuelType(fuelType);
        carBizCarInfoTemp.setNextInspectDate(StringUtils.isBlank(nextInspectDate)?null:nextInspectDate);
        carBizCarInfoTemp.setNextMaintenanceDate(StringUtils.isBlank(nextMaintenanceDate)?null:nextMaintenanceDate);
        carBizCarInfoTemp.setNextOperationDate(StringUtils.isBlank(nextOperationDate)?null:nextOperationDate);
        carBizCarInfoTemp.setNextSecurityDate(StringUtils.isBlank(nextSecurityDate)?null:nextSecurityDate);
        carBizCarInfoTemp.setNextClassDate(StringUtils.isBlank(nextClassDate)?null:nextClassDate);
        carBizCarInfoTemp.setTwoLevelMaintenanceDate(StringUtils.isBlank(twoLevelMaintenanceDate)?null:twoLevelMaintenanceDate);
        carBizCarInfoTemp.setRentalExpireDate(StringUtils.isBlank(rentalExpireDate)?null:rentalExpireDate);
        carBizCarInfoTemp.setPurchaseDate(StringUtils.isBlank(purchaseDate)?null:purchaseDate);
        carBizCarInfoTemp.setVehicleRegistrationDate(vehicleRegistrationDate);
        carBizCarInfoTemp.setTransportNumber(transportNumber);
        carBizCarInfoTemp.setCertificationAuthority(certificationAuthority);
        carBizCarInfoTemp.setOperatingRegion(operatingRegion);
        carBizCarInfoTemp.setTransportNumberDateStart(transportNumberDateStart);
        carBizCarInfoTemp.setTransportNumberDateEnd(transportNumberDateEnd);
        carBizCarInfoTemp.setFirstDate(firstDate);
        carBizCarInfoTemp.setOverHaulStatus(overHaulStatus);
        carBizCarInfoTemp.setAuditingStatus(auditingStatus);
        carBizCarInfoTemp.setAuditingDate(auditingDate);
        carBizCarInfoTemp.setEquipmentNumber(equipmentNumber);
        carBizCarInfoTemp.setGpsBrand(gpsBrand);
        carBizCarInfoTemp.setGpsType(gpsType);
        carBizCarInfoTemp.setGpsImei(gpsImei);
        carBizCarInfoTemp.setGpsDate(gpsDate);
        carBizCarInfoTemp.setMemo(StringUtils.isBlank(memo)?null:memo);
        carBizCarInfoTemp.setOldLicensePlates(oldLicensePlates);
        carBizCarInfoTemp.setOldCity(oldCity);
        carBizCarInfoTemp.setOldSupplierId(oldSupplierId);
        SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
        Integer userId = user.getId();
        carBizCarInfoTemp.setUpdateBy(userId);
        //---驾驶证图片

        if (StringUtils.isBlank(vehicleDrivingLicense)) {
            return AjaxResponse.failMsg(500, "行驶证图片url地址必须传递");
        }
        if (StringUtils.isBlank(carPhotograph)) {
            return AjaxResponse.failMsg(500, "车辆图片url地址必须传递");
        }
        carBizCarInfoTemp.setVehicleDrivingLicense(StringUtils.isBlank(vehicleDrivingLicense)?null:vehicleDrivingLicense);
        carBizCarInfoTemp.setCarPhotograph(StringUtils.isBlank(carPhotograph)?null:carPhotograph);
        return carBizCarInfoTempService.update(carBizCarInfoTemp, opType);
    }

    /**
     * 车辆信息导入
     * @param file 上传文件
     * @param cityId 城市Id
     * @param supplierId 供应商Id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/importCarInfo",method = RequestMethod.POST)
	@RequiresPermissions(value = { "SupplierCarEntry_import" } )
    @RequestFunction(menu = CAR_JOIN_APPLY_IMPORT)
    public AjaxResponse importCarInfo(@RequestParam(value="fileName") MultipartFile file,
                                      @Verify(param = "cityId",rule="required") Integer cityId,
                                      @Verify(param = "supplierId",rule="required") Integer supplierId) {
        log.info("车辆信息导入保存:importCarInfo");
        try {
            // 获取上传的文件的名称
            String filename = file.getOriginalFilename();
            //获取后缀
            String prefix=filename.substring(filename.lastIndexOf(".")+1);
            if (!"xls".equals(prefix) && !"xlsx".equals(prefix)) {
                return AjaxResponse.fail(RestErrorCode.FILE_TRMPLATE_ERROR);
            }
            return carBizCarInfoTempService.importCarInfo(file.getInputStream(), prefix,cityId,supplierId);
        } catch (IOException e) {
            e.printStackTrace();
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    /**
     * 巴士导入
     * @param file 上传文件
     * @param cityId 城市Id
     * @param supplierId 供应商Id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/importCarInfo4Bus",method = RequestMethod.POST)
    public AjaxResponse importCarInfo4Bus(@RequestParam(value="fileName") MultipartFile file,
                                          @Verify(param = "cityId",rule="required") Integer cityId,
                                          @Verify(param = "supplierId",rule="required") Integer supplierId) {
        log.info("车辆信息（巴士）导入保存:importCarInfo4Bus");
        try {
            // 获取上传的文件的名称
            String filename = file.getOriginalFilename();
            //获取后缀
            String prefix=filename.substring(filename.lastIndexOf(".")+1);
            if (!"xls".equals(prefix) && !"xlsx".equals(prefix)) {
                return AjaxResponse.fail(RestErrorCode.FILE_TRMPLATE_ERROR);
            }
            return carBizCarInfoTempService.importCarInfo4Bus(file.getInputStream(),prefix,cityId,supplierId);
        } catch (IOException e) {
            e.printStackTrace();
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    /**
     * 刷新数据
     */
    @ResponseBody
    @RequestMapping(value = "/flushData",method = {RequestMethod.POST, RequestMethod.GET})
    public AjaxResponse flushData(String string) {
        log.info("刷新数据-将临时表中不存在审核状态记录的数据刷新成为初始化状态");
        try {
            return AjaxResponse.success(carBizCarInfoTempService.flushData());
        } catch (Exception e) {
            AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
            log.info("刷新数据响应结果:" + JSON.toJSONString(ajaxResponse));
            return ajaxResponse;
        }
    }

    /**
     * 上传车辆图片,回写临时表的地址
     *
     * @param request       请求对象
     * @param multipartFile 文件对象
     * @return  包含图片路径
     */
    @ResponseBody
    @RequestMapping(value = "/uploadImage",method = RequestMethod.POST)
    public AjaxResponse uploadImage(HttpServletRequest request, MultipartFile multipartFile) {
        // uploadType = car / drivingLicense
        log.info("上传图片类型[{}], 车辆id[{}]", request.getParameter("uploadType"), request.getParameter("carId"));
        try {
            return AjaxResponse.success(carBizCarInfoTempService.uploadImage(request.getParameter("uploadType"), request.getParameter("carId"), multipartFile));
        } catch (Exception e) {
            log.error("uploadImage exception :"+ e.getMessage(), e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }
}