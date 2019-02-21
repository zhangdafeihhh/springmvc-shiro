package com.zhuanche.serv.busManage;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constants.busManage.BusConstant;
import com.zhuanche.constants.busManage.EnumFuel;
import com.zhuanche.dto.busManage.BusCarSaveDTO;
import com.zhuanche.dto.busManage.BusInfoDTO;
import com.zhuanche.dto.rentcar.CarMongoDTO;
import com.zhuanche.entity.busManage.BusCarInfo;
import com.zhuanche.entity.rentcar.CarBizCarGroup;
import com.zhuanche.entity.rentcar.CarInfo;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.DateUtils;
import com.zhuanche.util.objcompare.CompareObjectUtils;
import com.zhuanche.util.objcompare.entity.BusCarCompareEntity;
import com.zhuanche.vo.busManage.BusDetailVO;
import com.zhuanche.vo.busManage.BusInfoVO;
import mapper.mdbcarmanage.ex.BusBizChangeLogExMapper;
import mapper.rentcar.CarBizCarGroupMapper;
import mapper.rentcar.ex.BusInfoExMapper;
import mapper.rentcar.ex.CarBizCityExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;
import java.util.List;


/**
 * @program: mp-manage
 * @description: 巴士车辆管理
 * @author: niuzilian
 * @create: 2018-11-22 16:06
 **/
@Service
public class BusInfoService {

    @Autowired
    private BusBizChangeLogService busBizChangeLogService;

    @Autowired
    private BusInfoExMapper busInfoExMapper;

    @Autowired
    private CarBizCarGroupMapper groupMapper;
    @Autowired
    private CarBizSupplierExMapper supplierExMapper;
    @Autowired
    private CarBizCityExMapper cityExMapper;
    @Resource(name = "carMongoTemplate")
    private MongoTemplate carMongoTemplate;
    //存入mongoDB的Collection名称
    private static String mongoCollectionName = "carInfoDTO";

    public PageInfo<BusInfoVO> queryList(BusInfoDTO infoDTO) {
        PageInfo<BusInfoVO> pageInfo = PageHelper.startPage(infoDTO.getPageNum(), infoDTO.getPageSize(), true).doSelectPageInfo(() -> busInfoExMapper.selectList(infoDTO));
        return pageInfo;
    }

    public BusDetailVO getDetail(Integer carId) {
        return busInfoExMapper.selectCarByCarId(carId);
    }

    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER))
    public AjaxResponse saveCar(BusCarSaveDTO saveDTO) {
        boolean b = this.licensePlatesIfExist(saveDTO.getLicensePlates());
        if (b) {
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "车牌号已经存在");
        }
        String fuelName = EnumFuel.getFuelNameByCode(saveDTO.getFueltype());
        if (fuelName == null) {
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "燃料类型不存在");
        }

        CarBizCarGroup group = groupMapper.selectByPrimaryKey(saveDTO.getGroupId());
        if (group == null) {
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "服务类型有误");
        }
        BusCarInfo carInfo = new BusCarInfo();
        BeanUtils.copyProperties(saveDTO, carInfo);

        //补充默认字段
        buidDefaultParam(carInfo);
        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        Integer userId = currentLoginUser.getId();
        carInfo.setCreateBy(userId);
        carInfo.setCreateDate(new Date());
        carInfo.setUpdateBy(userId);
        carInfo.setUpdateDate(new Date());

        //因为不填写车型所以默认为0
        carInfo.setCarModelId(0);
        //所属车主字段，默认供应商名称
        String supplierName = supplierExMapper.getSupplierNameById(carInfo.getSupplierId());
        carInfo.setVehicleOwner(supplierName);
        //保存到mysql
        int result = busInfoExMapper.insertCar(carInfo);
        if (result > 0) {
            //保存到mongDB中
            CarMongoDTO carMongo = new CarMongoDTO();
            carMongo.setStatus(carInfo.getStatus());
            carMongo.setCreateBy(carInfo.getCreateBy());
            carMongo.setUpdateBy(carInfo.getUpdateBy());
            carMongo.setCreateDate(carInfo.getCreateDate());
            carMongo.setUpdateDate(carInfo.getUpdateDate());
            carMongo.setCarId(String.valueOf(carInfo.getCarId()));
            carMongo.setBrand(carInfo.getBrand());
            carMongo.setCarModel(carInfo.getModelDetail());
            carMongo.setLicensePlates(carInfo.getLicensePlates());
            carMongo.setColor(carInfo.getColor());
            carMongo.setModelDetail(carInfo.getModelDetail());

            carMongo.setGroupId(String.valueOf(carInfo.getGroupId()));
            carMongo.setGroupName(group.getGroupName());
            carMongo.setSeatNum(group.getSeatNum());

            carMongo.setSupplierName(supplierName);


            String cityName = cityExMapper.queryNameById(carInfo.getCityId());
            carMongo.setCityId(String.valueOf(carInfo.getCityId()));
            carMongo.setCityName(cityName);

            carMongo.setCarModelId(carInfo.getCarModelId());
            carMongo.setEngineNo(carInfo.getEngineNo());
            carMongo.setFrameNo(carInfo.getFrameNo());
            carMongo.setNextInspectDate(carInfo.getNextInspectDate());
            carMongo.setRentalExpireDate(carInfo.getRentalExpireDate());
            carMongo.setMemo(carInfo.getMemo());
            carMongoTemplate.insert(carMongo, mongoCollectionName);
            //保存操作日志
            busBizChangeLogService.insertLog(BusBizChangeLogExMapper.BusinessType.CAR, String.valueOf(carInfo.getCarId()), "创建车辆", new Date());
            return AjaxResponse.success(null);
        }
        return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
    }

    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER))
    public AjaxResponse updateCarById(BusCarSaveDTO busCarSaveDTO) {
        BusDetailVO busDetail = this.getDetail(busCarSaveDTO.getCarId());
        if (busDetail == null) {
            AjaxResponse.failMsg(RestErrorCode.HTTP_PARAM_INVALID, "车辆ID传入错误");
        }
        boolean b = this.licensePlatesIfExist(busCarSaveDTO.getLicensePlates());
        //如果传入的车牌号存在且跟carId查出来的车牌号不相符，该车牌号已经存在
        if (b && !busCarSaveDTO.getLicensePlates().equals(busDetail.getLicensePlates())) {
            return AjaxResponse.failMsg(RestErrorCode.HTTP_PARAM_INVALID, "该车牌号已经存在,不能修改");
        }
        String fuelName = EnumFuel.getFuelNameByCode(busCarSaveDTO.getFueltype());
        if (fuelName == null) {
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "燃料类型不存在");
        }

        CarBizCarGroup group = groupMapper.selectByPrimaryKey(busCarSaveDTO.getGroupId());
        if (group == null) {
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "服务类型有误");
        }
        BusCarInfo carInfo = new BusCarInfo();
        BeanUtils.copyProperties(busCarSaveDTO, carInfo);

        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        Integer userId = currentLoginUser.getId();
        carInfo.setUpdateBy(userId);
        carInfo.setUpdateDate(new Date());
        //所属车主字段，默认供应商名称
        String supplierName = supplierExMapper.getSupplierNameById(carInfo.getSupplierId());
        carInfo.setVehicleOwner(supplierName);
        int result = busInfoExMapper.updateCarById(carInfo);
        if(result>0){
            Query query = new Query(Criteria.where("carId").is(String.valueOf(carInfo.getCarId())));

            Update update = new Update();
            update.set("cityId", String.valueOf(carInfo.getCityId()));
            //城市
            String cityName = cityExMapper.queryNameById(carInfo.getCityId());
            update.set("cityName", cityName);
            //供应商
            update.set("supplierId", carInfo.getSupplierId());
            update.set("supplierName", supplierName);

            update.set("licensePlates", carInfo.getLicensePlates());

            //车型类别
            update.set("groupId", String.valueOf(carInfo.getGroupId()));
            update.set("groupName", group.getGroupName());
            //页面没有
            //update.set("seatNum",group.getSeatNum());
            //车辆厂牌 mongo不存储
            //具体型号
            update.set("carModel", carInfo.getModelDetail());
            update.set("modelDetail", carInfo.getModelDetail());

            update.set("color", carInfo.getColor());
            //燃料类型不存储
            //运输证字号不存储
            update.set("status", carInfo.getStatus());
            update.set("updateBy", carInfo.getUpdateBy());
            update.set("updateDate", carInfo.getUpdateDate());
            carMongoTemplate.updateFirst(query, update, mongoCollectionName);
            //保存修改日志
            saveUpdateLog(busDetail,busCarSaveDTO.getCarId());
            return AjaxResponse.success(null);
        }
        return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
    }
    private void saveUpdateLog(BusDetailVO oldDetail, Integer carId) {
        BusCarCompareEntity oldRecord = new BusCarCompareEntity();
        BeanUtils.copyProperties(oldDetail, oldRecord);
        oldRecord.setFuelName(EnumFuel.getFuelNameByCode(oldDetail.getFueltype()));
        oldRecord.setStatus(oldDetail.getStatus() == 1 ? "有效" : "无效");
        //查询一下最新的信息
        BusDetailVO carInfoNew = this.getDetail(carId);
        BusCarCompareEntity newRecord = new BusCarCompareEntity();
        BeanUtils.copyProperties(carInfoNew, newRecord);
        newRecord.setStatus(carInfoNew.getStatus() == 1 ? "有效" : "无效");
        newRecord.setFuelName(EnumFuel.getFuelNameByCode(carInfoNew.getFueltype()));
        List<Object> objects = CompareObjectUtils.contrastObj(oldRecord, newRecord, null);
        if (objects.size() != 0) {
            String join = StringUtils.join(objects, ",");
            busBizChangeLogService.insertLog(BusBizChangeLogExMapper.BusinessType.CAR, String.valueOf(carId), join, new Date());
        }
    }
    /**
     * @Description: 构建默认值
     * @Param: [carInfo]
     * @return: void
     * @Date: 2018/11/26
     */
    private void buidDefaultParam(BusCarInfo carInfo) {
        //=====================将巴士不需要的字段设置为默认值 或者空=================
        /**车辆类型（以机动车行驶证为主）*/
        carInfo.setVehicleType(BusConstant.BusSaveDefaultProperty.vehicle_type);
        /**车辆注册日期  */
        carInfo.setVehicleRegistrationDate(BusConstant.BusSaveDefaultProperty.vehicle_registration_date);
        /**网络预约出租汽车运输证发证机构*/
        carInfo.setCertificationauthority(BusConstant.BusSaveDefaultProperty.certificationAuthority);
        /**经营区域*/
        carInfo.setOperatingregion(BusConstant.BusSaveDefaultProperty.operatingRegion);
        /**网络预约出租汽车运输证有效期起*/
        carInfo.setTransportnumberdatestart(BusConstant.BusSaveDefaultProperty.transportNumberDateStart);
        /**网络预约出租汽车运输证有效期止*/
        carInfo.setTransportnumberdateend(BusConstant.BusSaveDefaultProperty.transportNumberDateEnd);
        /**网约车初次登记日期*/
        carInfo.setFirstdate(BusConstant.BusSaveDefaultProperty.firstDate);
        /**车辆检修状态（合格）*/
        carInfo.setOverhaulstatus(BusConstant.BusSaveDefaultProperty.overhaulStatus);
        /**年度审验状态（合格）*/
        carInfo.setAuditingstatus(BusConstant.BusSaveDefaultProperty.auditingStatus);
        /**车辆年度审验日期*/
        carInfo.setAuditingDate(BusConstant.BusSaveDefaultProperty.auditing_date);
        /*网约车发票打印设备序列号**/
        carInfo.setEquipmentnumber(BusConstant.BusSaveDefaultProperty.equipmentNumber);
        /**卫星定位装置品牌*/
        carInfo.setGpsbrand(BusConstant.BusSaveDefaultProperty.gpsBrand);
        /**卫星定位装置型号*/
        carInfo.setGpstype(BusConstant.BusSaveDefaultProperty.gpsType);
        /**卫星定位装置IMEI号*/
        carInfo.setGpsImei(BusConstant.BusSaveDefaultProperty.gps_imei);
        /**卫星定位装置安装日期 */
        carInfo.setGpsdate(BusConstant.BusSaveDefaultProperty.gpsDate);
        /*发动机编号*/
        carInfo.setEngineNo(BusConstant.BusSaveDefaultProperty.engine_no);
        /*发动机排量*/
        carInfo.setVehicleenginedisplacement(BusConstant.BusSaveDefaultProperty.vehicleEngineDisplacement);
        /**发动机功率*/
        carInfo.setVehicleEnginePower(BusConstant.BusSaveDefaultProperty.vehicle_engine_power);
        /*车架号*/
        carInfo.setFrameNo(BusConstant.BusSaveDefaultProperty.frame_no);
        /*车辆轴距*/
        carInfo.setVehicleEngineWheelbase(BusConstant.BusSaveDefaultProperty.vehicle_engine_wheelbase);
        try {
        /*下次车检时间*/
            //  carInfo.setNextInspectDate(DateUtils.getDate1(BusSaveDefaultProperty.next_inspect_date));
        /*下次维保时间*/
            //  carInfo.setNextMaintenanceDate(DateUtils.getDate1(BusSaveDefaultProperty.next_maintenance_date));
        /*运营证检测时间   购买*/
            // carInfo.setNextOperationDate(DateUtils.getDate1(BusSaveDefaultProperty.next_operation_date));
        /*下次治安证检测时间*/
            carInfo.setNextSecurityDate(DateUtils.getDate1(BusConstant.BusSaveDefaultProperty.next_security_date));
        /*下次等级检测时间*/
            carInfo.setNextClassDate(DateUtils.getDate1(BusConstant.BusSaveDefaultProperty.next_class_date));
            /**二次维保时间*/
            carInfo.setTwoLevelMaintenanceDate(DateUtils.getDate1(BusConstant.BusSaveDefaultProperty.two_level_maintenance_date));
            /**租赁到期时间*/
            carInfo.setRentalExpireDate(DateUtils.getDate1(BusConstant.BusSaveDefaultProperty.rental_expire_date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        /*VIN码*/
        carInfo.setVehicleVinCode(BusConstant.BusSaveDefaultProperty.vehicle_VIN_code);
    }


    public boolean licensePlatesIfExist(String licensePlates) {
        int result = busInfoExMapper.countLicensePlates(licensePlates);
        if (result > 0) {
            return true;
        }
        return false;
    }

    public String getLicensePlatesByCarId(Integer carId) {
        return busInfoExMapper.getLicensePlatesByCarId(carId);
    }


}
