package com.zhuanche.controller.busManage;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.busManage.EnumFuel;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.rentcar.BusInfoDTO;
import com.zhuanche.entity.rentcar.CarBizCarInfo;
import com.zhuanche.serv.CarBizCarGroupService;
import com.zhuanche.serv.busManage.BusInfoService;
import com.zhuanche.shiro.constants.BusConstant;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.vo.rentcar.BusDetailVO;
import com.zhuanche.vo.rentcar.BusInfoVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: mp-manage
 * @description: 巴士车辆管理
 * @author: niuzilian
 * @create: 2018-11-22 14:22
 **/
@RestController
@RequestMapping("/bus/busInfo")
public class BusInfoController {
    private static Logger logger = LoggerFactory.getLogger(BusInfoController.class);
    private static String LOG_PRE = "【巴士】";
    @Autowired
    private BusInfoService busInfoService;
    @Autowired
    private CarBizCarGroupService groupService;

    /**
     * @Description:查询巴士车辆列表
     * @Param: [busDTO]
     * @return: com.zhuanche.common.web.AjaxResponse
     * @Date: 2018/11/23
     */
    @RequestMapping(value = "/queryList", method = RequestMethod.POST)
    public AjaxResponse queryList(BusInfoDTO busDTO) {
        //从session中获取权限
        busDTO.setCityIds(WebSessionUtil.getCurrentLoginUser().getCityIds());
        busDTO.setSupplierIds(WebSessionUtil.getCurrentLoginUser().getSupplierIds());
        logger.info(LOG_PRE + "查询车辆列表参数=" + JSON.toJSONString(busDTO));
        PageInfo<BusInfoVO> pageInfo = busInfoService.queryList(busDTO);
        return AjaxResponse.success(new PageDTO(busDTO.getPageNum(), busDTO.getPageSize(), Integer.parseInt(pageInfo.getTotal() + ""), pageInfo.getList()));
    }

    /**
     * @Description: 查询车辆详情
     * @Param: [carId]
     * @return: com.zhuanche.common.web.AjaxResponse
     * @Date: 2018/11/23
     */
    @RequestMapping(value = "/getDetail", method = RequestMethod.GET)
    public AjaxResponse getDetail(@Verify(param = "carId", rule = "required") Integer carId) {
        logger.info(LOG_PRE + "查询车辆详情参数carId=" + carId);
        BusDetailVO detail = busInfoService.getDetail(carId);
        if (detail == null) {
            return AjaxResponse.failMsg(RestErrorCode.BUS_NOT_EXIST, "车辆信息不存在");
        }
        String fuelName = EnumFuel.getFuelNameByCode(detail.getFuelType());
        detail.setFuelName(fuelName);
        logger.info(LOG_PRE + "查询车辆详情结果" + JSON.toJSONString(detail));
        return AjaxResponse.success(detail);
    }

    @RequestMapping(value = "/saveCar", method = RequestMethod.POST)
    public AjaxResponse saveCar(@Verify(param = "carId", rule = "") Integer carId, @Verify(param = "cityId", rule = "") Integer cityId,
                                @Verify(param = "supplierId", rule = "") Integer supplierId,
                                @Verify(param = "licensePlates", rule = "request") String licensePlates,
                                @Verify(param = "groupId", rule = "request") Integer groupId,
                                @Verify(param = "vehicleBrand", rule = "request") String vehicleBrand,
                                @Verify(param = "modelDetail", rule = "request") String modelDetail,
                                @Verify(param = "color", rule = "request") String color,
                                @Verify(param = "fuelType", rule = "request") String fuelType,
                                @Verify(param = "transportNumber", rule = "request") String transportNumber,
                                @Verify(param = "status", rule = "request|min(0)|max(1)") Integer status) {

        boolean checkResult = busInfoService.licensePlatesIfExist(licensePlates);
        if (checkResult && carId == null) {
            return AjaxResponse.fail(RestErrorCode.LICENSE_PLATES_EXIST);
        }
        if (checkResult && carId != null) {
            String licensePlatesOld = busInfoService.getLicensePlatesByCarId(carId);
            if(!licensePlatesOld.equals(licensePlates)){
                return AjaxResponse.fail(RestErrorCode.LICENSE_PLATES_EXIST);
            }
        }
        if(EnumFuel.getFuelNameByCode(fuelType)==null){
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID,"燃料类型不存在");
        }
        //判断服务类型是否存在
        boolean checkoutGroup = groupService.groupIfExist(groupId);
        if(!checkoutGroup) {
            return AjaxResponse.success("服务类型有误");
        }
        //封装保存的参数
        CarBizCarInfo carInfo = new CarBizCarInfo();
        carInfo.setCarId(carId);
        carInfo.setLicensePlates(licensePlates);
        carInfo.setCityId(cityId);
        carInfo.setSupplierId(supplierId);
        carInfo.setGroupId(groupId);
        carInfo.setVehicleBrand(vehicleBrand);
        carInfo.setModelDetail(modelDetail);
        carInfo.setColor(color);
        //有问题和数据库不匹配
        carInfo.setFuelType(Integer.parseInt(fuelType));
        carInfo.setTransportNumber(transportNumber);
        carInfo.setStatus(status);
        Integer userId = WebSessionUtil.getCurrentLoginUser().getId();
        carInfo.setCreateBy(userId);
        carInfo.setUpdateBy(userId);
        //=====================将巴士不需要的字段设置为默认值 或者空=================
        carInfo.setVehicleType(BusConstant.);

        /**
         * 车辆类型（以机动车行驶证为主）
         */
        String vehicle_type = "car54856656";
        /**
         * 车辆注册日期
         */
        String vehicle_registration_date="2012-5-10";
        /**
         * 网络预约出租汽车运输证发证机构
         */
        String certificationAuthority = "道路运输管理局";
        /**
         * 经营区域
         */
        String operatingRegion = "全国";
        /**
         * 网络预约出租汽车运输证有效期起
         */
        String transportNumberDateStart ="2016-6-1";
        /**
         * 网络预约出租汽车运输证有效期止
         */
        String transportNumberDateEnd = "2026-6-1";
        /**
         * 网约车初次登记日期
         */
        String firstDate = "2016-6-1";
        /**
         * 车辆检修状态（合格）
         */
        int overhaulStatus = 1;
        /**
         * 年度审验状态（合格）
         */
        int auditingStatus = 1;
        /**
         * 车辆年度审验日期
         */
        String auditing_date = "2016-6-1";
        /**
         * 网约车发票打印设备序列号
         */
        String equipmentNumber = "asf54asd8564688";
        /**
         * 卫星定位装置品牌
         */
        String gpsBrand = "brand";
        /**
         * 卫星定位装置型号
         */
        String gpsType = "vasmd";
        /**
         * 卫星定位装置IMEI号
         */
        String gps_imei ="imeiv2015";
        /**
         * 卫星定位装置安装日期
         */
        String gpsDate = "2016-6-1";




        return AjaxResponse.success(null);
    }
}
