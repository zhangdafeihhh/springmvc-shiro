package com.zhuanche.controller.driverteam;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.BaseController;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.mdbcarmanage.CarBizCarInfoTempDTO;
import com.zhuanche.dto.mdbcarmanage.CarBizDriverInfoTempDTO;
import com.zhuanche.entity.mdbcarmanage.CarBizCarInfoTemp;
import com.zhuanche.entity.mdbcarmanage.CarBizDriverInfoTemp;
import com.zhuanche.entity.rentcar.CarBizCarGroup;
import com.zhuanche.entity.rentcar.CarBizCooperationType;
import com.zhuanche.entity.rentcar.CarBizModel;
import com.zhuanche.serv.deiver.CarBizCarInfoTempService;
import com.zhuanche.serv.rentcar.CarBizModelService;
import com.zhuanche.shiro.constants.BusConstant;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.excel.ExportExcelUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wzq
 */
@Controller
@RequestMapping(value = "/ç")
public class CarInfoTemporaryController extends BaseController {

    private static Log log =  LogFactory.getLog(CarInfoTemporaryController.class);

	@Autowired
	private CarBizCarInfoTempService carBizCarInfoTempService;

	@Autowired
    private CarBizModelService carBizModelService;

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
	public AjaxResponse queryCarData(@RequestParam(value = "page",defaultValue="1") Integer page,
                                     @RequestParam(value = "pageNum",defaultValue="10") Integer pageSize,
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
                Map<String, Object> result = super.querySupplierName(carBizCarInfoTemp.getCityId(), carBizCarInfoTemp.getSupplierId());
                carBizCarInfoTemp.setCityName((String)result.get("cityName"));
                carBizCarInfoTemp.setSupplierName((String)result.get("supplierName"));
                CarBizModel carBizModel = carBizModelService.selectByPrimaryKey(carBizCarInfoTemp.getCarModelId());
                if(carBizModel!=null){
                    carBizCarInfoTemp.setModeName(carBizModel.getModelName());
                }
            }
        }
        List<CarBizCarInfoTempDTO> carBizCarInfoTempDTOList = BeanUtil.copyList(carBizCarInfoTempList,CarBizCarInfoTempDTO.class);
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
    public void fileDownloadCarInfo(HttpServletRequest request,
                                    HttpServletResponse response) {
        String path = request.getRealPath("/") + File.separator + "upload"
                + File.separator + "IMPORTCARINFO.xlsx";
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
    public AjaxResponse queryCarInfo(@Verify(param = "carId",rule="required") Integer carId) {
        log.info("queryCarInfo:查看车辆详情Id:"+carId);
        Map<String,Object> params = Maps.newHashMap();
        params.put("carId",carId);
        CarBizCarInfoTemp carBizCarInfoTemp = carBizCarInfoTempService.queryForObject(params);
        if(carBizCarInfoTemp != null){
            Map<String, Object> result = super.querySupplierName(carBizCarInfoTemp.getCityId(), carBizCarInfoTemp.getSupplierId());
            carBizCarInfoTemp.setCityName((String)result.get("cityName"));
            carBizCarInfoTemp.setSupplierName((String)result.get("supplierName"));
            CarBizModel carBizModel = carBizModelService.selectByPrimaryKey(carBizCarInfoTemp.getCarModelId());
            if(carBizModel!=null){
                carBizCarInfoTemp.setModeName(carBizModel.getModelName());
            }
        }else{
            return AjaxResponse.fail(RestErrorCode.BUS_NOT_EXIST);
        }
        CarBizCarInfoTempDTO carBizCarInfoTempDTO = BeanUtil.copyObject(carBizCarInfoTemp, CarBizCarInfoTempDTO.class);
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
     * @param memo 备注
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/add", method =  RequestMethod.POST )
    public AjaxResponse saveCarInfo(@Verify(param = "licensePlates",rule="required") String licensePlates,
                                    @Verify(param = "cityId",rule="required") Integer cityId,
                                    @Verify(param = "supplierId",rule="required") Integer supplierId,
                                    @Verify(param = "carModelId",rule="required") Integer carModelId,
                                    @Verify(param = "modelDetail",rule="required") String modelDetail,
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
                                    @RequestParam(value = "purchaseDate",required = false) String memo
                                    ) {
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
        carBizCarInfoTemp.setNextInspectDate(nextInspectDate);
        carBizCarInfoTemp.setNextMaintenanceDate(nextMaintenanceDate);
        carBizCarInfoTemp.setNextOperationDate(nextOperationDate);
        carBizCarInfoTemp.setNextSecurityDate(nextSecurityDate);
        carBizCarInfoTemp.setNextClassDate(nextClassDate);
        carBizCarInfoTemp.setTwoLevelMaintenanceDate(twoLevelMaintenanceDate);
        carBizCarInfoTemp.setRentalExpireDate(rentalExpireDate);
        carBizCarInfoTemp.setPurchaseDate(purchaseDate);
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
        carBizCarInfoTemp.setMemo(memo);
        SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
        Integer userId = user.getId();
        carBizCarInfoTemp.setUpdateBy(userId);
        carBizCarInfoTemp.setCreateBy(userId);
        int code  = carBizCarInfoTempService.add(carBizCarInfoTemp);
        if(code > 0 ){
            return AjaxResponse.success(RestErrorCode.SUCCESS);
        }else{
            return AjaxResponse.success(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
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
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/update", method =  RequestMethod.POST )
    public AjaxResponse updateCarInfo(@Verify(param = "carId",rule="required") Integer carId,
                                    @Verify(param = "licensePlates",rule="required") String licensePlates,
                                    @Verify(param = "cityId",rule="required") Integer cityId,
                                    @Verify(param = "supplierId",rule="required") Integer supplierId,
                                    @Verify(param = "carModelId",rule="required") Integer carModelId,
                                    @Verify(param = "modelDetail",rule="required") String modelDetail,
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
                                    @Verify(param = "oldSupplierId",rule="required") Integer oldSupplierId) {
        log.error("修改Id:"+carId);
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
        carBizCarInfoTemp.setNextInspectDate(nextInspectDate);
        carBizCarInfoTemp.setNextMaintenanceDate(nextMaintenanceDate);
        carBizCarInfoTemp.setNextOperationDate(nextOperationDate);
        carBizCarInfoTemp.setNextSecurityDate(nextSecurityDate);
        carBizCarInfoTemp.setNextClassDate(nextClassDate);
        carBizCarInfoTemp.setTwoLevelMaintenanceDate(twoLevelMaintenanceDate);
        carBizCarInfoTemp.setRentalExpireDate(rentalExpireDate);
        carBizCarInfoTemp.setPurchaseDate(purchaseDate);
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
        carBizCarInfoTemp.setMemo(memo);
        carBizCarInfoTemp.setOldLicensePlates(oldLicensePlates);
        carBizCarInfoTemp.setOldCity(oldCity);
        carBizCarInfoTemp.setOldSupplierId(oldSupplierId);
        SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
        Integer userId = user.getId();
        carBizCarInfoTemp.setUpdateBy(userId);
        return carBizCarInfoTempService.update(carBizCarInfoTemp);
    }

    /**
     * 车辆信息导入
     * @param params
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/importCarInfo",method = RequestMethod.POST)
    public AjaxResponse importCarInfo(CarBizCarInfoTemp params, HttpServletRequest request) {
        log.info("车辆信息导入保存:importCarInfo,参数" + params.toString());
        return carBizCarInfoTempService.importCarInfo(params, request);
    }

    /**
     * 巴士导入
     * @param params
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/importCarInfo4Bus",method = RequestMethod.POST)
    public AjaxResponse importCarInfo4Bus(CarBizCarInfoTemp params, HttpServletRequest request) {
        log.info("车辆信息（巴士）导入保存:importCarInfo4Bus,参数" + params.toString());
        return carBizCarInfoTempService.importCarInfo4Bus(params, request);
    }


}