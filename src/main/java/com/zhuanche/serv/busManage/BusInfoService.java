package com.zhuanche.serv.busManage;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mongodb.WriteResult;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.constants.busManage.BusConstant;
import com.zhuanche.constants.busManage.EnumFuel;
import com.zhuanche.dto.busManage.BusCarSaveDTO;
import com.zhuanche.dto.busManage.BusInfoDTO;
import com.zhuanche.dto.rentcar.CarMongoDTO;
import com.zhuanche.entity.busManage.BusCarInfo;
import com.zhuanche.entity.rentcar.CarBizCarGroup;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.mongo.busManage.BusInfoAudit;
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
import mapper.rentcar.ex.CarBizCarGroupExMapper;
import mapper.rentcar.ex.CarBizCityExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


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
    @Autowired
    private CarBizCarGroupExMapper groupExMapper;
    @Resource(name = "carMongoTemplate")
    private MongoTemplate carMongoTemplate;
    //存入mongoDB的Collection名称
    //存入mongoDB的Collection名称
    private static String mongoCollectionName = "carInfoDTO";


    public PageInfo<BusInfoVO> queryList(BusInfoDTO infoDTO) {
        PageInfo<BusInfoVO> pageInfo = PageHelper.startPage(infoDTO.getPageNum(), infoDTO.getPageSize(), true).doSelectPageInfo(() -> busInfoExMapper.selectList(infoDTO));
        return pageInfo;
    }

    public BusDetailVO getDetail(Integer carId) {
        return busInfoExMapper.selectCarByCarId(carId);
    }

    /**
     * 保存车辆信息到审核表 （mongoDB）
     *
     * @param saveDTO
     * @return
     */
    public AjaxResponse saveCarToAuditCollect(BusCarSaveDTO saveDTO) {
        //创建mongDB 巴士审核colloection 对应的实体
        BusInfoAudit busInfo = new BusInfoAudit();
        BeanUtils.copyProperties(saveDTO, busInfo);
        //补充默认字段
        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        Integer userId = currentLoginUser.getId();
        Date now = new Date();
        busInfo.setCreateBy(userId);
        busInfo.setCreateDate(now);
        busInfo.setUpdateBy(userId);
        busInfo.setUpdateDate(now);
        busInfo.setUpdateName(currentLoginUser.getName());
        busInfo.setCreateName(currentLoginUser.getName());
        //默认未审核
        busInfo.setAuditStatus(0);
        //来源：0创建
        busInfo.setStemFrom(0);
        carMongoTemplate.insert(busInfo);
        return AjaxResponse.success(null);
    }


    public AjaxResponse queryAuditList(BusInfoDTO busDTO) {
        Query query = new Query().limit(busDTO.getPageSize());
        if (busDTO.getCityId() != null) {
            query.addCriteria(Criteria.where("cityId").is(busDTO.getCityId()));
        }
        Set<Integer> authOfSupplier = busDTO.getAuthOfSupplier();
        if (authOfSupplier != null && authOfSupplier.size() > 0) {
            query.addCriteria(Criteria.where("supplierId").in(authOfSupplier));
        }
        if (busDTO.getGroupId() != null) {
            query.addCriteria(Criteria.where("groupId").is(busDTO.getGroupId()));
        }
        if (busDTO.getStatus() != null) {
            query.addCriteria(Criteria.where("status").is(busDTO.getStatus()));
        }
        if (StringUtils.isNotBlank(busDTO.getLicensePlates())) {
            Pattern pattern = Pattern.compile("^.*" + busDTO.getLicensePlates() + ".*$", Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("licensePlates").regex(pattern));
        }
        //只查询未审核的数据
        query.addCriteria(Criteria.where("status").is(0));
        Integer pageNum = busDTO.getPageNum();
        Integer pageSize = busDTO.getPageSize();
        int start = (pageNum - 1) * pageSize;
        query.skip(start - 1 < 0 ? 0 : start);

        List<BusInfoAudit> busInfoAudits = carMongoTemplate.find(query, BusInfoAudit.class);
        long count = carMongoTemplate.count(query, BusInfoAudit.class);

        if (busInfoAudits == null || busInfoAudits.isEmpty()) {
            return AjaxResponse.success(new PageDTO(pageNum, pageSize, count, busInfoAudits));
        }
        //补充展示字段
        //城市
        Set<Integer> cityIds = new HashSet<>();
        Set<Integer> supplierIds = new HashSet<>();
        Set<Integer> grouIds = new HashSet<>();
        busInfoAudits.forEach(bus -> {
            if (bus.getCityId() != null) {
                cityIds.add(bus.getCityId());
            }
            if (bus.getSupplierId() != null) {
                supplierIds.add(bus.getSupplierId());
            }
            if (bus.getGroupId() != null) {
                grouIds.add(bus.getGroupId());
            }
        });
        List<CarBizCity> carBizCities = cityExMapper.queryNameByIds(cityIds);
        Map<Integer, CarBizCity> cityMap = carBizCities.stream().collect(Collectors.toMap(CarBizCity::getCityId, (o -> o)));
        List<CarBizSupplier> carBizSuppliers = supplierExMapper.findByIdSet(supplierIds);
        Map<Integer, CarBizSupplier> supplierMap = carBizSuppliers.stream().collect(Collectors.toMap(CarBizSupplier::getSupplierId, (sup -> sup)));

        List<CarBizCarGroup> groups = groupExMapper.queryCarGroupByIdSet(grouIds);
        Map<Integer, CarBizCarGroup> groupMap = groups.stream().collect(Collectors.toMap(CarBizCarGroup::getGroupId, (gop -> gop)));

        //补充字段
        List<BusInfoVO> result = new ArrayList<>();
        busInfoAudits.forEach(bus -> {
            BusInfoVO busInfoVO = new BusInfoVO();
            BeanUtils.copyProperties(bus, busInfoVO);
            CarBizCity carBizCity = cityMap.get(bus.getCityId());
            if (carBizCity != null) {
                busInfoVO.setCityName(carBizCity.getCityName());
            }
            CarBizSupplier carBizSupplier = supplierMap.get(bus.getSupplierId());
            if (carBizCities != null) {
                busInfoVO.setSupplierName(carBizSupplier.getSupplierFullName());
            }
            CarBizCarGroup group = groupMap.get(bus.getGroupId());
            if (group != null) {
                busInfoVO.setGroupName(group.getGroupName());
            }
            result.add(busInfoVO);
        });
        return AjaxResponse.success(new PageDTO(pageNum, pageSize, count, result));
    }


    @RequestMapping("/audit")
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER))
    public AjaxResponse audit(@Verify(param = "ids", rule = "required") String ids) {
        String[] split = ids.split(",");
        if (split.length == 0) {
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
        }
        int total = split.length;
        int success = 0;
        try {
            for (int i = 0; i < split.length; i++) {
                Query query = new Query(Criteria.where("id").is(split[i]));
                BusInfoAudit busAudit = carMongoTemplate.findOne(query, BusInfoAudit.class);
                Integer stemFrom = busAudit.getStemFrom();
                BusCarInfo carInfo = new BusCarInfo();
                BeanUtils.copyProperties(busAudit, carInfo);
                //审核新增数据
                if (stemFrom == 0) {
                    int carId = saveCar2DB(carInfo);
                    if (carId > 0) {
                        Update update = new Update();
                        update.set("status", 1);
                        update.set("auditor", WebSessionUtil.getCurrentLoginUser().getId());
                        update.set("auditDate", new Date());
                        update.set("auditorName",WebSessionUtil.getCurrentLoginUser().getName());
                        carMongoTemplate.updateFirst(query, update, BusInfoAudit.class);
                        //保存操作日志
                        busBizChangeLogService.insertLog(BusBizChangeLogExMapper.BusinessType.CAR, String.valueOf(carId), "创建车辆", busAudit.getCreateBy(), busAudit.getCreateName(), busAudit.getCreateDate());
                        busBizChangeLogService.insertLog(BusBizChangeLogExMapper.BusinessType.CAR, String.valueOf(carId), "审核通过", new Date());
                        success++;
                    }
                } else {
                    //审核修改数据
                    BusDetailVO detail = this.getDetail(carInfo.getCarId());
                    int result = updateCar2DB(carInfo);
                    if (result > 0) {
                        Update update = new Update();
                        update.set("status", 1);
                        update.set("auditor", WebSessionUtil.getCurrentLoginUser().getId());
                        update.set("auditDate", new Date());
                        update.set("auditorName",WebSessionUtil.getCurrentLoginUser().getName());
                        carMongoTemplate.updateFirst(query, update, BusInfoAudit.class);
                        this.saveUpdateLog(detail, carInfo.getCarId());
                        busBizChangeLogService.insertLog(BusBizChangeLogExMapper.BusinessType.CAR, String.valueOf(carInfo.getCarId()), "审核通过", new Date());
                        success++;
                    }
                }
            }
            Map<String, Object> result = new HashMap(4);
            result.put("total", total);
            result.put("success", success);
            return AjaxResponse.success(result);
        } catch (BeansException e) {
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    public AjaxResponse getAuditDetail(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        BusInfoAudit auditDetail = carMongoTemplate.findOne(query, BusInfoAudit.class);

        BusDetailVO detail = new BusDetailVO();
        BeanUtils.copyProperties(auditDetail, detail);

        //补充信息
        String cityName = cityExMapper.queryNameById(auditDetail.getCityId());
        detail.setCityName(cityName);
        String groupName = groupExMapper.getGroupNameByGroupId(auditDetail.getGroupId());
        detail.setGroupName(groupName);
        String supplierName = supplierExMapper.getSupplierNameById(auditDetail.getSupplierId());
        detail.setSupplierName(supplierName);
        String fuelName = EnumFuel.getFuelNameByCode(auditDetail.getFueltype());
        detail.setFuelName(fuelName);
        return AjaxResponse.success(detail);
    }

    public AjaxResponse updateAuditCar(BusCarSaveDTO saveDTO) {
        Query query = new Query(Criteria.where("id").is(saveDTO.getId()));
        BusInfoAudit detail = carMongoTemplate.findOne(query, BusInfoAudit.class);
        if (!detail.getLicensePlates().equals(saveDTO.getLicensePlates())) {
            //校验新车牌号是否存在
            boolean b = this.licensePlatesIfExist(saveDTO.getLicensePlates());
            if (b) {
                return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "车牌号已经存在");
            }
        }
        //以主键更新
        Update update = new Update();
        update.set("cityId", saveDTO.getCityId());
        update.set("supplierId", saveDTO.getSupplierId());
        update.set("licensePlates", saveDTO.getLicensePlates());
        update.set("groupId", saveDTO.getGroupId());
        update.set("vehicleBrand", saveDTO.getVehicleBrand());
        update.set("modelDetail", saveDTO.getModelDetail());
        update.set("color", saveDTO.getColor());
        update.set("fueltype", saveDTO.getFueltype());
        update.set("transportnumber", saveDTO.getTransportnumber());
        update.set("status", saveDTO.getStatus());
        update.set("nextInspectDate", saveDTO.getNextInspectDate());
        update.set("nextMaintenanceDate", saveDTO.getNextMaintenanceDate());
        update.set("nextOperationDate", saveDTO.getNextInspectDate());
        update.set("carPurchaseDate", saveDTO.getCarPurchaseDate());
        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        Integer id = currentLoginUser.getId();
        update.set("updateBy", id);
        update.set("updateDate", new Date());
        update.set("updateName", currentLoginUser.getName());
        WriteResult writeResult = carMongoTemplate.updateFirst(query, update, BusInfoAudit.class);
        return AjaxResponse.success(null);
    }

    public int saveCar2DB(BusCarInfo carInfo) {
        CarBizCarGroup group = groupMapper.selectByPrimaryKey(carInfo.getGroupId());
        //补充默认字段
        buidDefaultParam(carInfo);

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

            return carInfo.getCarId();
        }
        return 0;
    }

    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER))
    public AjaxResponse updateCarById(BusCarSaveDTO busCarSaveDTO) {
        BusDetailVO busDetail = this.getDetail(busCarSaveDTO.getCarId());
        if (busDetail == null) {
            return AjaxResponse.failMsg(RestErrorCode.HTTP_PARAM_INVALID, "车辆ID传入错误");
        }
        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        Integer userId = currentLoginUser.getId();
        if (busDetail.getLicensePlates().equals(busCarSaveDTO.getLicensePlates()) && busDetail.getTransportnumber().equals(busCarSaveDTO.getTransportnumber())) {
            // 没有修改车牌号和运输证字号，直接修改主表，不进入审核表
            BusCarInfo carInfo = new BusCarInfo();
            BeanUtils.copyProperties(busCarSaveDTO, carInfo);
            carInfo.setUpdateBy(userId);
            carInfo.setUpdateDate(new Date());
            int result = updateCar2DB(carInfo);
            if (result > 0) {
                //保存操作日志
                saveUpdateLog(busDetail, busCarSaveDTO.getCarId());
                return AjaxResponse.success(null);
            }
        } else {
            //校验新的车牌号是否已经存在
            boolean b = this.licensePlatesIfExist(busCarSaveDTO.getLicensePlates());
            if (b) {
                return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "车牌号已经存在");
            }
            //进入审核列表
            BusInfoAudit busInfoAudit = new BusInfoAudit();
            BeanUtils.copyProperties(busCarSaveDTO, busInfoAudit);
            busInfoAudit.setUpdateBy(userId);
            busInfoAudit.setUpdateDate(new Date());
            busInfoAudit.setUpdateName(currentLoginUser.getName());
            //来源
            busInfoAudit.setStemFrom(1);
            //默认未审核
            busInfoAudit.setAuditStatus(0);
            carMongoTemplate.insert(busInfoAudit);
            return AjaxResponse.success(null);
        }
        return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
    }


    private int updateCar2DB(BusCarInfo carInfo) {
        //所属车主字段，默认供应商名称
        String supplierName = supplierExMapper.getSupplierNameById(carInfo.getSupplierId());
        carInfo.setVehicleOwner(supplierName);
        int result = busInfoExMapper.updateCarById(carInfo);
        if (result > 0) {
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
            CarBizCarGroup group = groupMapper.selectByPrimaryKey(carInfo.getGroupId());
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
            return result;
        }
        return 0;
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
        //只要是审核表中存在就一定重复
        Query query = new Query();
        query.addCriteria(Criteria.where("licensePlates").is(licensePlates));
        query.addCriteria(Criteria.where("status").is(0));
        List<BusInfoAudit> busInfoAudits = carMongoTemplate.find(query, BusInfoAudit.class);
        if (result > 0 || (busInfoAudits != null && busInfoAudits.size() > 0)) {
            return true;
        }
        return false;
    }

    public String getLicensePlatesByCarId(Integer carId) {
        return busInfoExMapper.getLicensePlatesByCarId(carId);
    }


}
