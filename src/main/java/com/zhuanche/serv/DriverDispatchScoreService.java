package com.zhuanche.serv;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zhuanche.common.cache.RedisCacheUtil;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.rpc.RPCResponse;
import com.zhuanche.dto.rentcar.*;
import com.zhuanche.entity.mdbcarmanage.CarRelateTeam;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.*;
import mapper.mdbcarmanage.ex.CarDriverTeamExMapper;
import mapper.mdbcarmanage.ex.CarRelateTeamExMapper;
import mapper.rentcar.ex.CarBizCityExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 司机派单分服务
 *
 * @author wuqiang
 * @date 2020/4/7 11:11
 */
@Service
public class DriverDispatchScoreService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 父派单分类型缓存key
     */
    private static final String PARENT_DISPATCH_SCORE_TYPE = "mp_manage_parent_dispatch_score_type";

    /**
     * 子派单分类型缓存key
     */
    private static final String CHILD_DISPATCH_SCORE_TYPE = "mp_manage_child_dispatch_score_type:";

    @Value("${driver.integral.url}")
    private String driverIntegral;

    @Resource
    private CarBizDriverInfoService carBizDriverInfoService;

    @Resource
    private CarDriverTeamExMapper carDriverTeamExMapper;

    @Resource
    private CarBizCityExMapper carBizCityExMapper;

    @Resource
    private CarBizSupplierExMapper carBizSupplierExMapper;

    @Resource
    private CarRelateTeamExMapper carRelateTeamExMapper;

    /**
     * 分页查询司机派单分概括
     *
     * @param page 页码
     * @param pageSize 每页数量
     * @param carBizDriverInfoDTO 司机查询条件DTOS
     * @return PageDTO 分页结果
     */
    @MasterSlaveConfigs(configs={@MasterSlaveConfig(databaseTag="rentcar-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )} )
    public PageDTO queryPageDriverDispatchScore(int page, int pageSize, CarBizDriverInfoDTO carBizDriverInfoDTO){

        //数据权限
        PageDTO pageDTO = dataPermissions(page,pageSize,carBizDriverInfoDTO);
        if(null != pageDTO){
            return pageDTO;
        }

        Page p = PageHelper.startPage(page, pageSize, true);
        List<CarBizDriverInfoDTO> list;
        int total;
        try {
            list = carBizDriverInfoService.queryDriverList(carBizDriverInfoDTO);
            total = (int) p.getTotal();
        } finally {
            PageHelper.clearPage();
        }

        if(CollectionUtils.isEmpty(list)){
            return new PageDTO(page, pageSize, total, list);
        }

        List<DriverDispatchScoreGeneralize> dispatchScoreGeneralizeList = Lists.newArrayListWithCapacity(list.size());

        list.forEach(x -> {
            DriverDispatchScoreGeneralize dispatchScoreGeneralize = new DriverDispatchScoreGeneralize();
            dispatchScoreGeneralize.setCityId(x.getServiceCity());
            dispatchScoreGeneralize.setDriverId(x.getDriverId());
            dispatchScoreGeneralize.setDriverName(x.getName());
            dispatchScoreGeneralize.setDriverPhone(MobileOverlayUtil.doOverlayPhone(x.getPhone()));
            dispatchScoreGeneralize.setSupplierId(x.getSupplierId());

            dispatchScoreGeneralize.setLicensePlates(x.getLicensePlates());

            dispatchScoreGeneralizeList.add(dispatchScoreGeneralize);
        });

        //组装城市名称
        buildCityName(dispatchScoreGeneralizeList);

        //组装合作商名称
        buildSupplierName(dispatchScoreGeneralizeList);

        //组装车队信息
        buildTeamMessage(dispatchScoreGeneralizeList);

        //组装派单分概括
        buildDispatchScoreGeneralize(dispatchScoreGeneralizeList);

        return new PageDTO(page, pageSize, total, dispatchScoreGeneralizeList);
    }

    /**
     * 查询司机派单分每日更新记录<br>
     *     注意，只能查询3个月内的数据
     *
     * @param driverDispatchScoreDailyUpdateRecordQuery 司机派单分每日更新记录查询条件
     * @return List<DriverDispatchScoreDailyUpdateRecord> 司机派单分每日更新记录集合
     */
    public List<DriverDispatchScoreDailyUpdateRecord> queryDispatchScoreDailyUpdateRecord(DriverDispatchScoreDailyUpdateRecordQuery driverDispatchScoreDailyUpdateRecordQuery){

        Map<String, Object> paramMap = new HashMap<>(4);
        paramMap.put("driverId",driverDispatchScoreDailyUpdateRecordQuery.getDriverId());
        paramMap.put("startDay",driverDispatchScoreDailyUpdateRecordQuery.getUpdateTime().replace("-",""));
        paramMap.put("endDay",driverDispatchScoreDailyUpdateRecordQuery.getEndUpdateTime().replace("-",""));

        String result = queryDataByIntegralApi("/paidanfen2/getDayByDriverId",paramMap);
        if(StringUtils.isBlank(result)){
            return null;
        }

        List<DriverIntegralDispatchScoreDailyUpdateRecord> list = JSONArray.parseArray(result,DriverIntegralDispatchScoreDailyUpdateRecord.class);
        if(CollectionUtils.isEmpty(list)){
            return null;
        }

        List<DriverDispatchScoreDailyUpdateRecord> recordList = Lists.newArrayListWithCapacity(list.size());
        list.forEach(x -> {
            DriverDispatchScoreDailyUpdateRecord record = new DriverDispatchScoreDailyUpdateRecord();
            record.buildDriverId(x.getDriverId())
                    .buildDriverName(driverDispatchScoreDailyUpdateRecordQuery.getDriverName())
                    .buildDriverPhone(driverDispatchScoreDailyUpdateRecordQuery.getDriverPhone())
                    .buildCurrentDispatchScore(String.valueOf(x.getTotalScore()))
                    .buildServiceBaseScore(String.valueOf(x.getServiceScore()))
                    .buildServiceAccelerateScore(String.valueOf(x.getServGrowthScore()))
                    .buildTimeLengthBaseScore(String.valueOf(x.getDurationScore()))
                    .buildTimeLengthAccelerateScore(String.valueOf(x.getDuraGrowthScore()))
                    .buildBadBehaviorDeductionScore(String.valueOf(x.getBadScore()))
                    .buildChangeScore(String.valueOf(x.getChangeScore()))
                    .buildUpdateDate(DateUtil.getStringDate(x.getUpdateTime()));

            recordList.add(record);
        });

        return recordList;
    }

    /**
     * 分页查询司机派单分明细记录<br>
     *     调用策略工具组提供的接口获得，接口wiki：http://inside-yapi.01zhuanche.com/project/187/interface/api/23808
     *
     * @param queryCondition 司机派单分明细记录查询条件
     * @return PageDTO 司机派单分明细记录分页结果
     */
    public PageDTO queryPageDriverDispatchScoreDetailRecord(DriverDispatchScoreDetailRecordQuery queryCondition){

        Map<String, Object> paramMap = new HashMap<>(8);
        paramMap.put("driverId",queryCondition.getDriverId());
        paramMap.put("pageNo",queryCondition.getPage());
        paramMap.put("pageSize",queryCondition.getPageSize());

        if(StringUtils.isNotBlank(queryCondition.getOrderNo())){
            paramMap.put("orderNo",queryCondition.getOrderNo());
        }
        if(StringUtils.isNotBlank(queryCondition.getStartDate())){
            paramMap.put("startDate",queryCondition.getStartDate());
        }
        if(StringUtils.isNotBlank(queryCondition.getEndDate())){
            paramMap.put("endDate",queryCondition.getEndDate());
        }
        if(StringUtils.isNotBlank(queryCondition.getType())){
            paramMap.put("type",queryCondition.getType());
        }else{
            if(StringUtils.isNotBlank(queryCondition.getParentDispatchScoreType())){
                List<DriverIntegralDispatchScoreType> childList = queryChildDispatchScoreType(Integer.valueOf(queryCondition.getParentDispatchScoreType()));
                if(CollectionUtils.isNotEmpty(childList)){
                    List<Integer> childCodeList = childList.stream().map(DriverIntegralDispatchScoreType::getCode).collect(Collectors.toList());
                    paramMap.put("typeList",StringUtils.join(childCodeList,","));
                }
            }
        }

        Map<String,String> resultMap = queryPageByIntegralApi("/paidanfen2/getDetailByType",paramMap);
        if(MapUtils.isEmpty(resultMap)){
            return new PageDTO(queryCondition.getPage(), queryCondition.getPageSize(), 0, null);
        }

        List<DriverDispatchScoreDetailRecord> list = JSONArray.parseArray(resultMap.get("data"),DriverDispatchScoreDetailRecord.class);
        if(CollectionUtils.isEmpty(list)){
            return new PageDTO(queryCondition.getPage(), queryCondition.getPageSize(), 0, null);
        }

        list.forEach(x -> {
            x.setDriverName(queryCondition.getDriverName());
            x.setDriverPhone(queryCondition.getDriverPhone());
        });

        return new PageDTO(queryCondition.getPage(), queryCondition.getPageSize(), Integer.valueOf(resultMap.get("total")), list);
    }

    /**
     * 查询司机服务分计算明细
     *
     * @param driverId 司机ID
     * @param driverName 司机姓名
     * @param driverPhone 司机手机号
     * @param ownershipDate 所属日期，示例：2020-02-28
     * @param serviceScore 当前ownershipDate总服务分
     * @return List<DriverServiceScoreCalculateDetail> 司机服务分计算明细
     */
    public DriverServiceScoreCalculateGeneralize queryDriverServiceScoreCalculateDetail(Integer driverId,String driverName,String driverPhone,String ownershipDate,String serviceScore) {

        DriverServiceScoreCalculateGeneralize generalize = new DriverServiceScoreCalculateGeneralize();
        generalize.setServiceScore(new BigDecimal(serviceScore));
        //基础服务分固定为50
        generalize.setTotalBaseServiceScore(new BigDecimal("50"));
        generalize.setOwnershipDate(ownershipDate);

        DriverServiceScoreCalculateDetail defaultServiceScoreCalculateDetail = new DriverServiceScoreCalculateDetail();
        defaultServiceScoreCalculateDetail.setDriverId(driverId);
        defaultServiceScoreCalculateDetail.setDriverName(driverName);
        defaultServiceScoreCalculateDetail.setDriverPhone(driverPhone);
        defaultServiceScoreCalculateDetail.setDay(ownershipDate);

        Map<String, Object> paramMap = new HashMap<>(2);
        paramMap.put("driverId",driverId);
        paramMap.put("date",ownershipDate);

        String data = queryDataByIntegralApi("/paidanfen2/serviceDetail",paramMap);
        if(StringUtils.isBlank(data)){
            generalize.setCalculateDetailList(Lists.newArrayList(defaultServiceScoreCalculateDetail));
            return generalize;
        }

        JSONObject obj = JSONObject.parseObject(data);
        if(null == obj.get("serviceScoreHistoryList")){
            generalize.setCalculateDetailList(Lists.newArrayList(defaultServiceScoreCalculateDetail));
            return generalize;
        }

        String serviceScoreHistoryList = String.valueOf(obj.get("serviceScoreHistoryList"));
        List<DriverServiceScoreCalculateDetail> list = JSONArray.parseArray(serviceScoreHistoryList,DriverServiceScoreCalculateDetail.class);
        if(CollectionUtils.isEmpty(list)){
            generalize.setCalculateDetailList(Lists.newArrayList(defaultServiceScoreCalculateDetail));
            return generalize;
        }

        //按照日期倒序
        list = list.stream().sorted(Comparator.comparing(DriverServiceScoreCalculateDetail::getDay).reversed()).collect(Collectors.toList());

        for(DriverServiceScoreCalculateDetail detail : list){
            detail.setDriverId(driverId);
            detail.setDriverName(driverName);
            detail.setDriverPhone(driverPhone);
        }

        generalize.setCalculateDetailList(list);
        return generalize;
    }

    /**
     * 查询司机时长分计算明细
     *
     * @param driverId 司机ID
     * @param driverName 司机姓名
     * @param driverPhone 司机手机号
     * @param day 所属日期，示例：2020-02-28
     * @param timeLengthScore 当前day的时长分
     * @return List<DriverTimeLengthScoreCalculateDetail> 司机时长分计算明细
     */
    public DriverTimeLengthScoreCalculateGeneralize queryDriverTimeLengthScoreCalculateDetail(Integer driverId, String driverName, String driverPhone,
                                                                                              String day,String timeLengthScore) {

        Map<String, Object> paramMap = new HashMap<>(2);
        paramMap.put("driverId",driverId);
        paramMap.put("date",day);

        String data = queryDataByIntegralApi("/paidanfen2/durationDetail",paramMap);
        if(StringUtils.isBlank(data)){
            return null;
        }

        JSONObject obj = JSONObject.parseObject(data);
        if(null == obj.get("durationScoreHistoryList")){
            return null;
        }

        String durationScoreHistoryList = String.valueOf(obj.get("durationScoreHistoryList"));
        List<DriverTimeLengthScoreCalculateDetail> list = JSONArray.parseArray(durationScoreHistoryList,DriverTimeLengthScoreCalculateDetail.class);
        if(CollectionUtils.isEmpty(list)){
            return null;
        }

        //按照日期倒序
        list = list.stream().sorted(Comparator.comparing(DriverTimeLengthScoreCalculateDetail::getDay).reversed()).collect(Collectors.toList());

        for(DriverTimeLengthScoreCalculateDetail detail : list){
            detail.setDriverId(driverId);
            detail.setDriverName(driverName);
            detail.setDriverPhone(driverPhone);
        }

        DriverTimeLengthScoreCalculateDetail currentDayCalculateDetail = list.get(0);

        DriverTimeLengthScoreCalculateGeneralize generalize = new DriverTimeLengthScoreCalculateGeneralize();
        generalize.setDurationScore(new BigDecimal(timeLengthScore));
        //基础时长分固定为50
        generalize.setTotalBaseDurationScore(new BigDecimal("50"));
        generalize.setDay(day);
        generalize.setCalculateDetailList(list);

        return generalize;
    }

    /**
     * 查询父派单分类型
     *
     * @return List<DriverIntegralDispatchScoreType> 父派单分类型
     */
    public List<DriverIntegralDispatchScoreType> queryParentDispatchScoreType(){
        List<DriverIntegralDispatchScoreType> list;

        String value = RedisCacheUtil.get(PARENT_DISPATCH_SCORE_TYPE,String.class);
        if(StringUtils.isNotBlank(value)){
            list = JSON.parseArray(value,DriverIntegralDispatchScoreType.class);
            if(CollectionUtils.isNotEmpty(list)){
                return list;
            }
        }

        String data = queryDataByIntegralApi("/paidanfen2/typeList",null);
        if(StringUtils.isBlank(data)){
            return Lists.newArrayList();
        }


        list = JSON.parseArray(data,DriverIntegralDispatchScoreType.class);
        if(CollectionUtils.isEmpty(list)){
            return Lists.newArrayList();
        }

        RedisCacheUtil.set(PARENT_DISPATCH_SCORE_TYPE,data,600);

        return list;
    }

    /**
     * 查询子派单分类型
     *
     * @param parentType 父派单分类型编码
     * @return List<DriverIntegralDispatchScoreType> 子派单分类型
     */
    public List<DriverIntegralDispatchScoreType> queryChildDispatchScoreType(Integer parentType) {

        if(null == parentType){
            return Lists.newArrayList();
        }

        List<DriverIntegralDispatchScoreType> list;

        String value = RedisCacheUtil.get(CHILD_DISPATCH_SCORE_TYPE + parentType,String.class);
        if(StringUtils.isNotBlank(value)){
            list = JSON.parseArray(value,DriverIntegralDispatchScoreType.class);
            if(CollectionUtils.isNotEmpty(list)){
                return list;
            }
        }

        Map<String, Object> paramMap = new HashMap<>(2);
        paramMap.put("type",parentType);
        String data = queryDataByIntegralApi("/paidanfen2/detailTypeList",paramMap);
        if(StringUtils.isBlank(data)){
            return Lists.newArrayList();
        }

        list = JSON.parseArray(data,DriverIntegralDispatchScoreType.class);
        if(CollectionUtils.isEmpty(list)){
            return Lists.newArrayList();
        }

        RedisCacheUtil.set(CHILD_DISPATCH_SCORE_TYPE + parentType,data,600);

        return list;
    }

    /**
     * 数据权限
     *
     * @param page 页码
     * @param pageSize 每页数量
     * @param carBizDriverInfoDTO 司机查询条件DTOS
     * @return PageDTO 分页结果
     */
    private PageDTO dataPermissions(int page, int pageSize,CarBizDriverInfoDTO carBizDriverInfoDTO){

        //普通管理员可以管理的所有城市ID
        Set<Integer> permOfCitySet = WebSessionUtil.getCurrentLoginUser().getCityIds();
        //普通管理员可以管理的所有供应商ID
        Set<Integer> permOfSupplierIdSet = WebSessionUtil.getCurrentLoginUser().getSupplierIds();
        //普通管理员可以管理的所有车队ID
        Set<Integer> permOfTeamIdSet = WebSessionUtil.getCurrentLoginUser().getTeamIds();

        Set<Integer> driverIdSet = null;

        Integer teamId = carBizDriverInfoDTO.getTeamId();
        if(CollectionUtils.isNotEmpty(permOfTeamIdSet)){
            if(null != teamId){
                if(!permOfTeamIdSet.contains(teamId)){
                    return new PageDTO(page, pageSize, 0, Lists.newArrayList());
                }
                driverIdSet = queryDriverIdSetForTeamIdSet(Sets.newHashSet(teamId));
                if(CollectionUtils.isEmpty(driverIdSet)){
                    return new PageDTO(page, pageSize, 0, Lists.newArrayList());
                }
            }else{
                driverIdSet = queryDriverIdSetForTeamIdSet(permOfTeamIdSet);
                if(CollectionUtils.isEmpty(driverIdSet)){
                    return new PageDTO(page, pageSize, 0, Lists.newArrayList());
                }
            }
        }else{
            if(null != teamId){
                driverIdSet = queryDriverIdSetForTeamIdSet(Sets.newHashSet(teamId));
                if(CollectionUtils.isEmpty(driverIdSet)){
                    return new PageDTO(page, pageSize, 0, Lists.newArrayList());
                }
            }
        }

        if(null != carBizDriverInfoDTO.getDriverId()){
            if(CollectionUtils.isEmpty(driverIdSet)){
                driverIdSet = Sets.newHashSet(carBizDriverInfoDTO.getDriverId());
            }else{
                driverIdSet.add(carBizDriverInfoDTO.getDriverId());
            }
        }

        carBizDriverInfoDTO.setDriverIds(driverIdSet);

        //数据权限
        carBizDriverInfoDTO.setCityIds(permOfCitySet);
        carBizDriverInfoDTO.setSupplierIds(permOfSupplierIdSet);
        carBizDriverInfoDTO.setTeamIds(permOfTeamIdSet);

        return null;
    }

    /**
     * 通过车队ID查询司机
     *
     * @param teamIdSet 车队ID集合
     * @return Set<Integer> 司机ID集合
     */
    private Set<Integer> queryDriverIdSetForTeamIdSet(Set<Integer> teamIdSet){
        List<Integer> driverIdList = carRelateTeamExMapper.queryDriverIdsByTeamIdss(teamIdSet);
        return CollectionUtils.isEmpty(driverIdList) ? null : new HashSet<>(driverIdList);
    }

    /**
     * 组装城市名称
     *
     * @param dispatchScoreGeneralizeList 司机派单分概括集合
     */
    private void  buildCityName(List<DriverDispatchScoreGeneralize> dispatchScoreGeneralizeList){

        Set<Integer> cityIds = dispatchScoreGeneralizeList.stream().filter(x -> null != x.getCityId()).map(DriverDispatchScoreGeneralize::getCityId).collect(Collectors.toSet());
        List<CarBizCity> cityList = carBizCityExMapper.queryNameByIds(cityIds);
        if(CollectionUtils.isEmpty(cityList)){
            return;
        }

        for(DriverDispatchScoreGeneralize dispatchScoreGeneralize : dispatchScoreGeneralizeList){
            for(CarBizCity city : cityList){
                if(dispatchScoreGeneralize.getCityId().equals(city.getCityId())){
                    dispatchScoreGeneralize.setCityName(city.getCityName());
                    break;
                }
            }
        }
    }

    /**
     * 组装合作商名称
     *
     * @param dispatchScoreGeneralizeList 司机派单分概括集合
     */
    private void  buildSupplierName(List<DriverDispatchScoreGeneralize> dispatchScoreGeneralizeList){

        Set<Integer> supplierIdSet = dispatchScoreGeneralizeList.stream().filter(x -> null != x.getSupplierId()).map(DriverDispatchScoreGeneralize::getSupplierId).collect(Collectors.toSet());
        List<CarBizSupplier> supplierList = carBizSupplierExMapper.findByIdSet(supplierIdSet);
        if(CollectionUtils.isEmpty(supplierList)){
            return;
        }

        for(DriverDispatchScoreGeneralize dispatchScoreGeneralize : dispatchScoreGeneralizeList){
            for(CarBizSupplier supplier : supplierList){
                if(dispatchScoreGeneralize.getSupplierId().equals(supplier.getSupplierId())){
                    dispatchScoreGeneralize.setSupplierName(supplier.getSupplierFullName());
                    break;
                }
            }
        }
    }

    /**
     * 组装车队信息
     *
     * @param dispatchScoreGeneralizeList 司机派单分概括集合
     */
    private void buildTeamMessage(List<DriverDispatchScoreGeneralize> dispatchScoreGeneralizeList){

        List<String> driverIdList = dispatchScoreGeneralizeList.stream().filter(x -> null != x.getDriverId()).map(y -> String.valueOf(y.getDriverId())).collect(Collectors.toList());

        List<CarRelateTeam> teamList = carDriverTeamExMapper.queryDriverTeamListByDriverIdList(driverIdList);
        if(CollectionUtils.isEmpty(teamList)){
            return;
        }

        for(DriverDispatchScoreGeneralize dispatchScoreGeneralize : dispatchScoreGeneralizeList){
            for(CarRelateTeam team : teamList){
                if(dispatchScoreGeneralize.getDriverId().equals(team.getDriverId())){
                    dispatchScoreGeneralize.setTeamId(team.getTeamId());
                    dispatchScoreGeneralize.setTeamName(team.getTeamName());
                    break;
                }
            }
        }
    }

    /**
     * 组装派单分概括
     *
     * @param dispatchScoreGeneralizeList 司机派单分概括集合
     */
    private void buildDispatchScoreGeneralize(List<DriverDispatchScoreGeneralize> dispatchScoreGeneralizeList){

        List<Integer> driverIdList = dispatchScoreGeneralizeList.stream().filter(x -> null != x.getDriverId()).map(DriverDispatchScoreGeneralize::getDriverId).collect(Collectors.toList());

        //默认查询昨天(为什么派单分概括查询昨天？原因在于，策略工具组那边是每天凌晨统计昨天的数据，所以，当天的数据是查不到的)
        String day = DateUtils.formatDate(DateUtils.addDays(new Date(),-1), DateUtil.DATE_FORMAT).replace("-","");
        Map<Integer, DriverIntegralDispatchScoreGeneralize> generalizeMap = queryDispatchScoreGeneralize(driverIdList,day);
        if(MapUtils.isEmpty(generalizeMap)){
            return;
        }

        dispatchScoreGeneralizeList.forEach(x -> {
            DriverIntegralDispatchScoreGeneralize generalize = generalizeMap.get(x.getDriverId());
            if(null != generalize){
                x.setCurrentDispatchScore(generalize.getTotalScore());
                x.setServiceBaseScore(generalize.getServiceScore());
                x.setServiceAccelerateScore(generalize.getServGrowthScore());
                x.setTimeLengthBaseScore(generalize.getDurationScore());
                x.setTimeLengthAccelerateScore(generalize.getDuraGrowthScore());
                x.setBadBehaviorDeductionScore(generalize.getBadScore());
                x.setUpdateDate(DateUtil.getStringDate(generalize.getUpdateTime()));
            }
        });
    }

    /**
     * 查询司机派单分概括<br>
     *     调用策略工具组接口获得
     *
     * @param driverIdList 司机ID集合
     * @param day 有查询日期，格式：YYYYMMDD，示例：20200228
     * @return Map<Integer, DriverIntegralDispatchScoreGeneralize> 司机派单分概括Map,key为司机ID，valeu为该司机派单分概括
     */
    private Map<Integer, DriverIntegralDispatchScoreGeneralize> queryDispatchScoreGeneralize(List<Integer> driverIdList, String day) {

        Map<String, Object> paramMap = new HashMap<>(2);
        paramMap.put("driverIds", StringUtils.join(driverIdList,","));
        paramMap.put("day",day);

        String result = queryDataByIntegralApi("/paidanfen2/getDayCollectList",paramMap);
        if(StringUtils.isBlank(result)){
            return null;
        }

        List<DriverIntegralDispatchScoreGeneralize> list = JSONArray.parseArray(result,DriverIntegralDispatchScoreGeneralize.class);
        if(CollectionUtils.isEmpty(list)){
            return null;
        }

        return list.stream().collect(Collectors.toMap(DriverIntegralDispatchScoreGeneralize::getDriverId, v -> v));
    }

    private String queryDataByIntegralApi(String path, Map<String, Object> paramMap){

        String url = driverIntegral + path;
        String param = JSON.toJSONString(paramMap);

        logger.info("DriverIncomeScoreService queryMessageByIntegralApi: url:{},paramMap:{}",url,param);
        String result = MpOkHttpUtil.okHttpGet(url, paramMap,2,path);
        logger.info("DriverIncomeScoreService queryMessageByIntegralApi: url:{},paramMap:{},result:{}",url,param,result);

        if(StringUtils.isBlank(result)){
            return null;
        }

        RPCResponse apiResponse = JSON.parseObject(result,RPCResponse.class);
        if(null == apiResponse){
            return null;
        }

        if(apiResponse.getCode() != 0){
            logger.info("DriverIncomeScoreService queryMessageByIntegralApi: request fail,url:{},paramMap:{},result:{}",url,param,result);
            return null;
        }

        if (null == apiResponse.getData()){
            return null;
        }

        return String.valueOf(apiResponse.getData());
    }

    private Map<String,String> queryPageByIntegralApi(String path, Map<String, Object> paramMap){

        String url = driverIntegral + path;
        String param = JSON.toJSONString(paramMap);

        logger.info("DriverDispatchScoreServiceImpl queryPageByIntegralApi: url:{},paramMap:{}",url,param);
        String result = MpOkHttpUtil.okHttpGet(url, paramMap,2,path);
        logger.info("DriverDispatchScoreServiceImpl queryPageByIntegralApi: url:{},paramMap:{},result:{}",url,param,result);

        if(StringUtils.isBlank(result)){
            return null;
        }

        JSONObject obj = JSONObject.parseObject(result);
        if(null == obj){
            return null;
        }

        if(!obj.getInteger("code").equals(0)){
            logger.info("DriverDispatchScoreServiceImpl queryPageByIntegralApi: request fail,url:{},paramMap:{},result:{}",url,param,result);
            return null;
        }

        Map<String,String> map = new HashMap<>(2);
        map.put("data",String.valueOf(obj.get("data")));
        map.put("total","0");

        JSONObject page = obj.getJSONObject("page");
        if(null != page){
            map.put("total",String.valueOf(null != page.get("total") ? page.get("total") : 0));
        }

        return map;
    }

}
