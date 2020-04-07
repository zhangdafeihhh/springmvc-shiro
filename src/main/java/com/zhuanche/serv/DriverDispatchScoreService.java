package com.zhuanche.serv;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.rpc.RPCResponse;
import com.zhuanche.dto.rentcar.CarBizDriverInfoDTO;
import com.zhuanche.dto.rentcar.DriverDispatchScoreGeneralize;
import com.zhuanche.dto.rentcar.DriverIntegralDispatchScoreGeneralize;
import com.zhuanche.entity.mdbcarmanage.CarRelateTeam;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.DateUtil;
import com.zhuanche.util.DateUtils;
import com.zhuanche.util.MobileOverlayUtil;
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

}
