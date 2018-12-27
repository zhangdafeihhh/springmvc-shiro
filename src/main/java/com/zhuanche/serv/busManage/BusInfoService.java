package com.zhuanche.serv.busManage;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.constants.busManage.BusConstant;
import com.zhuanche.dto.busManage.BusInfoDTO;
import com.zhuanche.dto.rentcar.CarMongoDTO;
import com.zhuanche.entity.busManage.BusCarInfo;
import com.zhuanche.entity.rentcar.CarBizCarGroup;
import com.zhuanche.util.DateUtils;
import com.zhuanche.vo.busManage.BusDetailVO;
import com.zhuanche.vo.busManage.BusInfoVO;
import mapper.rentcar.CarBizCarGroupMapper;
import mapper.rentcar.ex.BusInfoExMapper;
import mapper.rentcar.ex.CarBizCityExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;


/**
 * @program: mp-manage
 * @description: 巴士车辆管理
 * @author: niuzilian
 * @create: 2018-11-22 16:06
 **/
@Service
public class BusInfoService {

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
    public int saveCar(BusCarInfo carInfo) {
        buidDefaultParam(carInfo);
        //保存到mysql
        int result = busInfoExMapper.insertCar(carInfo);
        return result;
    }

	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER))
    public int updateCarById(BusCarInfo carInfo) {
        int result = busInfoExMapper.updateCarById(carInfo);
        return result;
    }
    public void saveCar2MongoDB(BusCarInfo carInfo) {
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
        //查询groupName
        Integer groupId = carInfo.getGroupId();
        CarBizCarGroup group = groupMapper.selectByPrimaryKey(groupId);
        carMongo.setGroupId(String.valueOf(groupId));
        carMongo.setGroupName(group.getGroupName());
        carMongo.setSeatNum(group.getSeatNum());
        //查询供应商姓名
        String supplierName = supplierExMapper.getSupplierNameById(carInfo.getSupplierId());
        carMongo.setSupplierName(supplierName);
        //城市
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
    }

    public void update2mongoDB(BusCarInfo info) {
        Query query = new Query(Criteria.where("carId").is(String.valueOf(info.getCarId())));

        Update update = new Update();
        update.set("cityId",String.valueOf(info.getCityId()));
        //城市
        String cityName = cityExMapper.queryNameById(info.getCityId());
        update.set("cityName",cityName);
        //供应商
        String supplierName = supplierExMapper.getSupplierNameById(info.getSupplierId());
        update.set("supplierId",info.getSupplierId());
        update.set("supplierName",supplierName);

        update.set("licensePlates", info.getLicensePlates());

        //车型类别
        update.set("groupId", String.valueOf(info.getGroupId()));
        CarBizCarGroup group = groupMapper.selectByPrimaryKey(info.getGroupId());
        update.set("groupName",group.getGroupName());
        //页面没有
        //update.set("seatNum",group.getSeatNum());
        //车辆厂牌 mongo不存储
        //具体型号
        update.set("carModel", info.getModelDetail());
        update.set("modelDetail",info.getModelDetail());

        update.set("color", info.getColor());
        //燃料类型不存储
        //运输证字号不存储
        update.set("status", info.getStatus());
        update.set("updateBy",info.getUpdateBy());
        update.set("updateDate",info.getUpdateDate());
        carMongoTemplate.updateFirst(query, update, mongoCollectionName);
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
