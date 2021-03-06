package com.zhuanche.serv.busManage;

import com.alibaba.fastjson.JSONObject;
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
import com.zhuanche.constants.BusConst;
import com.zhuanche.constants.busManage.BusConstant;
import com.zhuanche.constants.busManage.EnumFuel;
import com.zhuanche.dto.busManage.BusCarSaveDTO;
import com.zhuanche.dto.busManage.BusInfoDTO;
import com.zhuanche.dto.rentcar.CarMongoDTO;
import com.zhuanche.entity.busManage.BusCarInfo;
import com.zhuanche.entity.rentcar.CarBizCarGroup;
import com.zhuanche.entity.rentcar.CarBizCarInfo;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.mongo.busManage.BusInfoAudit;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.Common;
import com.zhuanche.util.DateUtils;
import com.zhuanche.util.SignUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
 * @description: ??????????????????
 * @author: niuzilian
 * @create: 2018-11-22 16:06
 **/
@Service
public class BusInfoService {


    private static Logger logger = LoggerFactory.getLogger(BusInfoService.class);

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

    @Resource(name = "busMongoTemplate")
    private MongoTemplate busMongoTemplage;

    @Value("${car.rest.url}")
    private String order_url ;

    //??????mongoDB???Collection??????
    //??????mongoDB???Collection??????
    private static String mongoCollectionName = "carInfoDTO";




    public PageInfo<BusInfoVO> queryList(BusInfoDTO infoDTO) {
        logger.info("===??????????????????====" + JSONObject.toJSONString(infoDTO));
        List<Integer> groupIdList = groupExMapper.queryBusGroup();
        logger.info("=====groupIdList====" + JSONObject.toJSONString(groupIdList));
        if(groupIdList != null){
            infoDTO.setGroupIdList(groupIdList);
        }
        PageInfo<BusInfoVO> pageInfo = PageHelper.startPage(infoDTO.getPageNum(), infoDTO.getPageSize(), true).doSelectPageInfo(() -> busInfoExMapper.selectList(infoDTO));
        logger.info("=======??????????????????===" + JSONObject.toJSONString(pageInfo));
        return pageInfo;
    }

    public BusDetailVO getDetail(Integer carId) {
        return busInfoExMapper.selectCarByCarId(carId);
    }

    /**
     * ?????????????????????????????? ???mongoDB???
     *
     * @param saveDTO
     * @return
     */
    public AjaxResponse saveCarToAuditCollect(BusCarSaveDTO saveDTO) {
        //??????mongDB ????????????colloection ???????????????
        BusInfoAudit busInfo = new BusInfoAudit();
        BeanUtils.copyProperties(saveDTO, busInfo);
        //??????????????????
        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        Integer userId = currentLoginUser.getId();
        Date now = new Date();
        busInfo.setCreateBy(userId);
        busInfo.setCreateDate(now);
        busInfo.setUpdateBy(userId);
        busInfo.setUpdateDate(now);
        busInfo.setUpdateName(currentLoginUser.getName());
        busInfo.setCreateName(currentLoginUser.getName());
        //???????????????
        busInfo.setAuditStatus(0);
        //?????????0??????
        busInfo.setStemFrom(0);
        busMongoTemplage.insert(busInfo);
        return AjaxResponse.success("????????????????????????????????????");
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
        //???????????????????????????
        query.addCriteria(Criteria.where("auditStatus").is(0));
        Integer pageNum = busDTO.getPageNum();
        Integer pageSize = busDTO.getPageSize();
        int start = (pageNum - 1) * pageSize;
        query.skip(start - 1 < 0 ? 0 : start);

        List<BusInfoAudit> busInfoAudits = busMongoTemplage.find(query, BusInfoAudit.class);
        long count = busMongoTemplage.count(query, BusInfoAudit.class);

        if (busInfoAudits == null || busInfoAudits.isEmpty()) {
            return AjaxResponse.success(new PageDTO(pageNum, pageSize, count, busInfoAudits));
        }
        //??????????????????
        //??????
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

        //????????????
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
                Query query = new Query();
                query.addCriteria(Criteria.where("id").is(split[i]));
                query.addCriteria(Criteria.where("auditStatus").is(0));
                BusInfoAudit busAudit = busMongoTemplage.findOne(query, BusInfoAudit.class);
                if(busAudit==null){
                    continue;
                }
                Integer stemFrom = busAudit.getStemFrom();
                BusCarInfo carInfo = new BusCarInfo();
                BeanUtils.copyProperties(busAudit, carInfo);
                //??????????????????
                if (stemFrom == 0) {
                    int carId = saveCar2DB(carInfo);
                    if (carId > 0) {
                        Update update = new Update();
                        update.set("auditStatus", 1);
                        update.set("auditor", WebSessionUtil.getCurrentLoginUser().getId());
                        update.set("auditDate", new Date());
                        update.set("auditorName",WebSessionUtil.getCurrentLoginUser().getName());
                        busMongoTemplage.updateFirst(query, update, BusInfoAudit.class);
                        //??????????????????
                        busBizChangeLogService.insertLog(BusBizChangeLogExMapper.BusinessType.CAR, String.valueOf(carId), "????????????", busAudit.getCreateBy(), busAudit.getCreateName(), busAudit.getCreateDate());
                        busBizChangeLogService.insertLog(BusBizChangeLogExMapper.BusinessType.CAR, String.valueOf(carId), "????????????", new Date());
                        success++;
                    }
                } else {
                    //??????????????????
                    BusDetailVO detail = this.getDetail(carInfo.getCarId());
                    int result = updateCar2DB(carInfo);
                    if (result > 0) {
                        Update update = new Update();
                        update.set("auditStatus", 1);
                        update.set("auditor", WebSessionUtil.getCurrentLoginUser().getId());
                        update.set("auditDate", new Date());
                        update.set("auditorName",WebSessionUtil.getCurrentLoginUser().getName());
                        busMongoTemplage.updateFirst(query, update, BusInfoAudit.class);
                        String diff = this.saveUpdateLog(detail, carInfo.getCarId());
                        if(!"".equals(diff)) {
                            busBizChangeLogService.insertLog(BusBizChangeLogExMapper.BusinessType.CAR, String.valueOf(carInfo.getCarId()), diff, busAudit.getUpdateBy(), busAudit.getUpdateName(), busAudit.getUpdateDate());
                            busBizChangeLogService.insertLog(BusBizChangeLogExMapper.BusinessType.CAR, String.valueOf(carInfo.getCarId()), "????????????", new Date());
                        }
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
        BusInfoAudit auditDetail = busMongoTemplage.findOne(query, BusInfoAudit.class);

        BusDetailVO detail = new BusDetailVO();
        BeanUtils.copyProperties(auditDetail, detail);

        //????????????
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
        BusInfoAudit detail = busMongoTemplage.findOne(query, BusInfoAudit.class);
        if (!detail.getLicensePlates().equals(saveDTO.getLicensePlates())) {
            //??????????????????????????????
            boolean b = this.licensePlatesIfExist(saveDTO.getLicensePlates());
            if (b) {
                return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "?????????????????????");
            }
        }
        //???????????????
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
        WriteResult writeResult = busMongoTemplage.updateFirst(query, update, BusInfoAudit.class);
        return AjaxResponse.success(null);
    }

    public int saveCar2DB(BusCarInfo carInfo) {
        CarBizCarGroup group = groupMapper.selectByPrimaryKey(carInfo.getGroupId());
        //??????????????????
        buidDefaultParam(carInfo);

        //????????????????????????????????????0
        carInfo.setCarModelId(0);
        //??????????????????????????????????????????
        String supplierName = supplierExMapper.getSupplierNameById(carInfo.getSupplierId());
        carInfo.setVehicleOwner(supplierName);
        //?????????mysql
        int result = busInfoExMapper.insertCar(carInfo);
        if (result > 0) {
            //?????????mongDB???
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
            return AjaxResponse.failMsg(RestErrorCode.HTTP_PARAM_INVALID, "??????ID????????????");
        }
        boolean auditStatus = this.isAuditStatus(busCarSaveDTO.getCarId());
        if(auditStatus){
            return AjaxResponse.fail(RestErrorCode.INT_AUDIT_STATUS);
        }
        boolean inService = this.isInService(busDetail.getLicensePlates());
        if(inService){
            return AjaxResponse.fail(RestErrorCode.INT_SERVICE);
        }
        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        Integer userId = currentLoginUser.getId();
        if (busDetail.getLicensePlates().equals(busCarSaveDTO.getLicensePlates()) && busDetail.getTransportnumber().equals(busCarSaveDTO.getTransportnumber())) {
            // ?????????????????????????????????????????????????????????????????????????????????
            BusCarInfo carInfo = new BusCarInfo();
            BeanUtils.copyProperties(busCarSaveDTO, carInfo);
            carInfo.setUpdateBy(userId);
            carInfo.setUpdateDate(new Date());
            //????????????????????????modelId ??????0
            carInfo.setCarModelId(0);
            int result = updateCar2DB(carInfo);
            if (result > 0) {
                //??????????????????
                String diff = saveUpdateLog(busDetail, busCarSaveDTO.getCarId());
                if(!"".equals(diff)){
                    busBizChangeLogService.insertLog(BusBizChangeLogExMapper.BusinessType.CAR, String.valueOf(busCarSaveDTO.getCarId()), diff, new Date());
                }
                return AjaxResponse.success("????????????");
            }
        } else {
            //????????????????????????????????????????????????????????????
            if(!busDetail.getLicensePlates().equals(busCarSaveDTO.getLicensePlates())){
                boolean b = this.licensePlatesIfExist(busCarSaveDTO.getLicensePlates());
                if (b) {
                    return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "?????????????????????");
                }
            }
            //??????????????????
            BusInfoAudit busInfoAudit = new BusInfoAudit();
            BeanUtils.copyProperties(busCarSaveDTO, busInfoAudit);
            busInfoAudit.setUpdateBy(userId);
            busInfoAudit.setUpdateDate(new Date());
            busInfoAudit.setUpdateName(currentLoginUser.getName());
            //??????
            busInfoAudit.setStemFrom(1);
            //???????????????
            busInfoAudit.setAuditStatus(0);
            busMongoTemplage.insert(busInfoAudit);
            return AjaxResponse.success("????????????????????????????????????");
        }
        return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
    }

    private boolean isInService(String licensePlates){
        //??????????????????????????????
        boolean result= true;
        Map<String,Object>param = new HashMap(2);
        param.put("licensePlates",licensePlates);
        param.put("businessId", Common.BUSINESSID);
        String sign = SignUtils.createMD5Sign(param, Common.KEY);
        param.put("sign",sign);
        String url = order_url+ BusConst.Order.GET_SERVICE_ORDER;
        String response = MpOkHttpUtil.okHttpPost(url, param, 3, "??????????????????????????????");
        logger.info("?????????????????????????????????"+response);
        if(response!=null){
            JSONObject res = JSONObject.parseObject(response);
            if(res.getInteger("code")!=null&&res.getInteger("code")==0){
                JSONObject data = res.getJSONObject("data");
                Integer orderCount = data.getInteger("orderCount");
                if(orderCount>0){
                    result=true;
                }else{
                    result=false;
                }
            }
        }
        return result;
    }

    private int updateCar2DB(BusCarInfo carInfo) {
        //??????????????????????????????????????????
        String supplierName = supplierExMapper.getSupplierNameById(carInfo.getSupplierId());
        carInfo.setVehicleOwner(supplierName);
        int result = busInfoExMapper.updateCarById(carInfo);
        if (result > 0) {
            Query query = new Query(Criteria.where("carId").is(String.valueOf(carInfo.getCarId())));

            Update update = new Update();
            update.set("cityId", String.valueOf(carInfo.getCityId()));
            //??????
            String cityName = cityExMapper.queryNameById(carInfo.getCityId());
            update.set("cityName", cityName);
            //?????????
            update.set("supplierId", carInfo.getSupplierId());
            update.set("supplierName", supplierName);

            update.set("licensePlates", carInfo.getLicensePlates());

            //????????????
            update.set("groupId", String.valueOf(carInfo.getGroupId()));
            CarBizCarGroup group = groupMapper.selectByPrimaryKey(carInfo.getGroupId());
            update.set("groupName", group.getGroupName());
            //????????????
            //update.set("seatNum",group.getSeatNum());
            //???????????? mongo?????????
            //????????????
            update.set("carModel", carInfo.getModelDetail());
            update.set("modelDetail", carInfo.getModelDetail());

            update.set("color", carInfo.getColor());
            //?????????????????????
            //????????????????????????
            update.set("status", carInfo.getStatus());
            update.set("updateBy", carInfo.getUpdateBy());
            update.set("updateDate", carInfo.getUpdateDate());
            carMongoTemplate.updateFirst(query, update, mongoCollectionName);
            return result;
        }
        return 0;
    }

    private String saveUpdateLog(BusDetailVO oldDetail, Integer carId) {
        BusCarCompareEntity oldRecord = new BusCarCompareEntity();
        BeanUtils.copyProperties(oldDetail, oldRecord);
        oldRecord.setFuelName(EnumFuel.getFuelNameByCode(oldDetail.getFueltype()));
        oldRecord.setStatus(oldDetail.getStatus() == 1 ? "??????" : "??????");
        //???????????????????????????
        BusDetailVO carInfoNew = this.getDetail(carId);
        BusCarCompareEntity newRecord = new BusCarCompareEntity();
        BeanUtils.copyProperties(carInfoNew, newRecord);
        newRecord.setStatus(carInfoNew.getStatus() == 1 ? "??????" : "??????");
        newRecord.setFuelName(EnumFuel.getFuelNameByCode(carInfoNew.getFueltype()));
        List<Object> objects = CompareObjectUtils.contrastObj(oldRecord, newRecord, null);

        if (objects.size() != 0) {
            String join = StringUtils.join(objects, ",");
            return join;
           // busBizChangeLogService.insertLog(BusBizChangeLogExMapper.BusinessType.CAR, String.valueOf(carId), join, new Date());
        }
        return "";
    }

    /**
     * @Description: ???????????????
     * @Param: [carInfo]
     * @return: void
     * @Date: 2018/11/26
     */
    private void buidDefaultParam(BusCarInfo carInfo) {
        //=====================????????????????????????????????????????????? ?????????=================
        /**?????????????????????????????????????????????*/
        carInfo.setVehicleType(BusConstant.BusSaveDefaultProperty.vehicle_type);
        /**??????????????????  */
        carInfo.setVehicleRegistrationDate(BusConstant.BusSaveDefaultProperty.vehicle_registration_date);
        /**?????????????????????????????????????????????*/
        carInfo.setCertificationauthority(BusConstant.BusSaveDefaultProperty.certificationAuthority);
        /**????????????*/
        carInfo.setOperatingregion(BusConstant.BusSaveDefaultProperty.operatingRegion);
        /**?????????????????????????????????????????????*/
        carInfo.setTransportnumberdatestart(BusConstant.BusSaveDefaultProperty.transportNumberDateStart);
        /**?????????????????????????????????????????????*/
        carInfo.setTransportnumberdateend(BusConstant.BusSaveDefaultProperty.transportNumberDateEnd);
        /**???????????????????????????*/
        carInfo.setFirstdate(BusConstant.BusSaveDefaultProperty.firstDate);
        /**??????????????????????????????*/
        carInfo.setOverhaulstatus(BusConstant.BusSaveDefaultProperty.overhaulStatus);
        /**??????????????????????????????*/
        carInfo.setAuditingstatus(BusConstant.BusSaveDefaultProperty.auditingStatus);
        /**????????????????????????*/
        carInfo.setAuditingDate(BusConstant.BusSaveDefaultProperty.auditing_date);
        /*????????????????????????????????????**/
        carInfo.setEquipmentnumber(BusConstant.BusSaveDefaultProperty.equipmentNumber);
        /**????????????????????????*/
        carInfo.setGpsbrand(BusConstant.BusSaveDefaultProperty.gpsBrand);
        /**????????????????????????*/
        carInfo.setGpstype(BusConstant.BusSaveDefaultProperty.gpsType);
        /**??????????????????IMEI???*/
        carInfo.setGpsImei(BusConstant.BusSaveDefaultProperty.gps_imei);
        /**?????????????????????????????? */
        carInfo.setGpsdate(BusConstant.BusSaveDefaultProperty.gpsDate);
        /*???????????????*/
        carInfo.setEngineNo(BusConstant.BusSaveDefaultProperty.engine_no);
        /*???????????????*/
        carInfo.setVehicleenginedisplacement(BusConstant.BusSaveDefaultProperty.vehicleEngineDisplacement);
        /**???????????????*/
        carInfo.setVehicleEnginePower(BusConstant.BusSaveDefaultProperty.vehicle_engine_power);
        /*?????????*/
        carInfo.setFrameNo(BusConstant.BusSaveDefaultProperty.frame_no);
        /*????????????*/
        carInfo.setVehicleEngineWheelbase(BusConstant.BusSaveDefaultProperty.vehicle_engine_wheelbase);
        try {
        /*??????????????????*/
            //  carInfo.setNextInspectDate(DateUtils.getDate1(BusSaveDefaultProperty.next_inspect_date));
        /*??????????????????*/
            //  carInfo.setNextMaintenanceDate(DateUtils.getDate1(BusSaveDefaultProperty.next_maintenance_date));
        /*?????????????????????   ??????*/
            // carInfo.setNextOperationDate(DateUtils.getDate1(BusSaveDefaultProperty.next_operation_date));
        /*???????????????????????????*/
            carInfo.setNextSecurityDate(DateUtils.getDate1(BusConstant.BusSaveDefaultProperty.next_security_date));
        /*????????????????????????*/
            carInfo.setNextClassDate(DateUtils.getDate1(BusConstant.BusSaveDefaultProperty.next_class_date));
            /**??????????????????*/
            carInfo.setTwoLevelMaintenanceDate(DateUtils.getDate1(BusConstant.BusSaveDefaultProperty.two_level_maintenance_date));
            /**??????????????????*/
            carInfo.setRentalExpireDate(DateUtils.getDate1(BusConstant.BusSaveDefaultProperty.rental_expire_date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        /*VIN???*/
        carInfo.setVehicleVinCode(BusConstant.BusSaveDefaultProperty.vehicle_VIN_code);
    }


    public boolean licensePlatesIfExist(String licensePlates) {
        int result = busInfoExMapper.countLicensePlates(licensePlates);
        //??????????????????????????????????????????
        Query query = new Query();
        query.addCriteria(Criteria.where("licensePlates").is(licensePlates));
        query.addCriteria(Criteria.where("auditStatus").is(0));
        List<BusInfoAudit> busInfoAudits = busMongoTemplage.find(query, BusInfoAudit.class);
        if (result > 0 || (busInfoAudits != null && busInfoAudits.size() > 0)) {
            return true;
        }
        return false;
    }
    public boolean isAuditStatus (Integer carId){
        //????????????????????????????????????????????????
        Query query = new Query();
        query.addCriteria(Criteria.where("carId").is(carId));
        query.addCriteria(Criteria.where("auditStatus").is(0));
        List<BusInfoAudit> busInfoAudits = busMongoTemplage.find(query, BusInfoAudit.class);
        if(busInfoAudits!=null&&busInfoAudits.size()>0){
            return true;
        }
        return false;
    }

    public String getLicensePlatesByCarId(Integer carId) {
        return busInfoExMapper.getLicensePlatesByCarId(carId);
    }

}
