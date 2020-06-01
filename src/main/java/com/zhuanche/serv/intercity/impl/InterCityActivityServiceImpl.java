package com.zhuanche.serv.intercity.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.constant.Constants;
import com.zhuanche.entity.rentcar.CarBizCarGroup;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.serv.intercity.InterCityActivityService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import mapper.rentcar.ex.CarBizCarGroupExMapper;
import mapper.rentcar.ex.CarBizCarInfoExMapper;
import mapper.rentcar.ex.CarBizCityExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author fanht
 * @Description
 * @Date 2020/4/20 下午2:39
 * @Version 1.0
 */
@Service
public class InterCityActivityServiceImpl implements InterCityActivityService {

    @Value("${config.url}")
    private String configUrl;

    @Value("${ordercost.server.api.base.url}")
    private String orderCostUrl;

    @Autowired
    private CarBizSupplierExMapper carBizSupplierExMapper;


    @Autowired
    private CarBizCityExMapper cityExMapper;

    @Autowired
    private CarBizCarInfoExMapper carBizCarInfoExMapper;

    @Autowired
    private CarBizCarGroupExMapper  groupExMapper;


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public PageDTO queryList(Integer groupId, Integer discountStatus, Integer ruleId,Integer pageNo,Integer pageSize) {

        Map<String,Object> mapParam = Maps.newHashMap();
        Integer total= 0;
        String ruleIdListStr = "";
        if(ruleId == null){
            ruleIdListStr = this.getRuleId();
            if(ruleIdListStr == null){
                logger.info("===============折扣立减活动获取线路id为空========");
                return  null;
            }
            mapParam.put("ruleIdListStr",ruleIdListStr);

        } else {
            mapParam.put("ruleIdListStr",ruleId);
        }
        mapParam.put("serviceTypeId",Constants.INTEGER_SERVICE_TYPE);
        mapParam.put("groupId",groupId);
        mapParam.put("discountStatus",discountStatus);
        mapParam.put("pageNo",pageNo);
        mapParam.put("pageSize",pageSize);
        Set<Integer> setCitys = new HashSet<>();
        JSONArray resultArray = null;
        try {
            String result = MpOkHttpUtil.okHttpPost(orderCostUrl+"/interCity/strategy/discount/list",mapParam,0,null);
            logger.info("=========获取计费后台数据============" + JSONObject.toJSONString(result));
            if(StringUtils.isNotEmpty(result)){
                JSONObject jsonResult = JSONObject.parseObject(result);
                if(jsonResult.get(Constants.CODE) != null && jsonResult.getInteger(Constants.CODE) ==0 ){
                    String data = jsonResult.getString("data");
                    if(data != null  ){
                        JSONObject strategyJson = JSONObject.parseObject(data);
                        if(strategyJson.get(Constants.STRATEGY_LIST) != null ){
                            total = strategyJson.get("total") != null?strategyJson.getInteger("total"):0;
                            resultArray = JSONArray.parseArray(strategyJson.getString("strategyList"));
                            if(resultArray.size() > 0){
                                resultArray.forEach(array ->{
                                    JSONObject jsonObject = (JSONObject) array;
                                    setCitys.add(jsonObject.get("cityId") == null ? null : jsonObject.getInteger("cityId"));
                                });
                            }
                        }
                    }

                }
            }
        } catch (Exception e) {
            logger.error("解析异常" +e);
        }
        List<CarBizCity> carBizCityList = new ArrayList<>();
        Map<Integer,String> cityMap = Maps.newHashMap();
        Map<Integer,String> groupMap = Maps.newHashMap();
        List<CarBizCarGroup> listGroup = groupExMapper.queryAllGroup();
        listGroup.forEach(group ->{
            groupMap.put(group.getGroupId(),group.getGroupName());
        });
        if(CollectionUtils.isNotEmpty(setCitys)){
            carBizCityList = cityExMapper.queryNameByIds(setCitys);
            carBizCityList.forEach(citys ->{
                cityMap.put(citys.getCityId(),citys.getCityName());
            });
        }
        final JSONArray jsonArray = new JSONArray();
        if(resultArray != null && resultArray.size() > 0){
            resultArray.forEach(array ->{
                JSONObject jsonObject = (JSONObject) array;
                if(jsonObject.get(Constants.CITY_ID) != null && jsonObject.getInteger(Constants.CITY_ID) > 0){
                    jsonObject.put("cityName", jsonObject.get("cityId") == null ? "":cityMap.get(jsonObject.getInteger("cityId")));
                    jsonObject.put("groupName",jsonObject.get("groupId")== null ? "":groupMap.get(jsonObject.getInteger("groupId")));
                }else {
                    jsonObject.put("cityName","");
                }
                jsonArray.add(jsonObject);
            });
        }
        PageDTO pageDTO = new PageDTO(pageNo, pageSize, total, jsonArray);
        return pageDTO;
    }

    @Override
    public JSONObject getDetail(Integer id) {

        Map<String,Object> mapDetail = Maps.newConcurrentMap();
        mapDetail.put("strategyId",id);
        String result = MpOkHttpUtil.okHttpGet(orderCostUrl+"/interCity/strategy/discount/getDetail",mapDetail,0,null);
        JSONObject jsonObject = new JSONObject();
        if(StringUtils.isNotEmpty(result)){
            JSONObject jsonResult = JSONObject.parseObject(result);
            if(jsonResult.get(Constants.CODE) != null && jsonResult.getInteger(Constants.CODE) ==0 ){
                String data = jsonResult.getString("data");
                if(data != null ){
                 jsonObject = jsonResult.getJSONObject("data");
                 if(jsonObject.get(Constants.CITY_ID) != null && jsonObject.getInteger(Constants.CITY_ID) > 0){
                     Integer cityId = jsonObject.getInteger("cityId");
                     String cityName = cityExMapper.queryNameById(cityId);
                     jsonObject.put("cityName",cityName);
                 }else {
                     jsonObject.put("cityName","");

                 }


                 if(jsonObject.get(Constants.GROUP_ID) != null && jsonObject.getInteger(Constants.GROUP_ID) > 0){
                        Integer groupId = jsonObject.getInteger("groupId");
                        String groupName = groupExMapper.getGroupNameByGroupId(groupId);
                        jsonObject.put("groupName",groupName);
                    }else {
                        jsonObject.put("groupName","");

                    }



                }
            }
        }
        return jsonObject;
    }

    @Override
    public Integer saveOrUpdate(Integer discountId,
                                Integer strategyId,
                                Integer discountType,
                                String discountAmount,
                                String discountStartTime,
                                String discountEndTime,
                                Integer discountStatus,
                                Integer allDiscountType,
                                String allDiscountAmount) {
        Map<String,Object> map = Maps.newHashMap();
        if(discountId != null){
            map.put("discountId",discountId);
        }
        map.put("strategyId",strategyId);
        map.put("discountType",discountType);
        map.put("discountAmount",discountAmount);
        map.put("discountStartTime",discountStartTime);
        map.put("discountEndTime",discountEndTime);
        map.put("discountStatus",discountStatus);
        map.put("updateBy",WebSessionUtil.getCurrentLoginUser().getLoginName());

        map.put("allDiscountType",allDiscountType);
        map.put("allDiscountAmount",allDiscountAmount);

        String result = MpOkHttpUtil.okHttpPost(orderCostUrl+"/interCity/strategy/discount/saveOrUpdate",map,0,null);

        if(StringUtils.isNotEmpty(result)){
            JSONObject jsonObject = JSONObject.parseObject(result);
            if(jsonObject.get(Constants.CODE) != null && jsonObject.getInteger(Constants.CODE) == 0){
                return jsonObject.getInteger("code");
            }
        }
        return 1;
    }


    private String getLineIdBySupplierIds(String supplierIdBatch) {
        if (StringUtils.isBlank(supplierIdBatch)) {
            return "";
        }
        try {
            String linesUrl = configUrl + "/intercityCarUse/getLineIdBySupplierIds";
            Map<String, Object> params = Maps.newHashMap();
            params.put("supplierIds", supplierIdBatch);
            String lineResult = MpOkHttpUtil.okHttpPost(linesUrl, params, 1, null);
            logger.info("配置中心供应商--{}查询路线ID集合返回结果集--{}", supplierIdBatch, lineResult);
            if (StringUtils.isNotEmpty(lineResult)) {
                JSONObject jsonResult = JSONObject.parseObject(lineResult);
                if (jsonResult != null && jsonResult.getInteger(Constants.CODE) == 0) {
                    if (jsonResult.get(Constants.CODE) != null) {
                        return jsonResult.get("data").toString();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("查询配置路线ID异常" + e);
            return "";
        }
        return "";
    }


    private String getRuleId(){
        String ruleIdListStr = "";

        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        if (!WebSessionUtil.isSupperAdmin()) {
            Set<Integer> suppliersSet = loginUser.getSupplierIds();
            String supplierIdBatch = "";
            if (suppliersSet != null && suppliersSet.size() > 0) {
                StringBuilder supplierBuilder = new StringBuilder();
                for (Integer supplierId : suppliersSet) {
                    supplierBuilder.append(supplierId).append(Constants.SEPERATER);
                }
                if (StringUtils.isNotEmpty(supplierBuilder.toString())) {
                    supplierIdBatch = supplierBuilder.toString().substring(0, supplierBuilder.toString().length() - 1);
                }

                ruleIdListStr = this.getLineIdBySupplierIds(supplierIdBatch);

                if (StringUtils.isEmpty(ruleIdListStr)) {
                    logger.info("=========用户未配置线路============");
                    return null;
                }


            } else if (CollectionUtils.isNotEmpty(loginUser.getCityIds())) {

                List<CarBizSupplier> querySupplierAllList = carBizSupplierExMapper.querySupplierAllList(loginUser.getCityIds(), null);

                StringBuilder supplierBuilder = new StringBuilder();
                querySupplierAllList.forEach(list -> {
                    supplierBuilder.append(list.getSupplierId()).append(Constants.SEPERATER);
                });

                if (supplierBuilder.toString().length() > 0) {
                    String allSupplier = supplierBuilder.toString();
                    logger.info("获取所有的合作商id:" + allSupplier);
                    ruleIdListStr = this.getLineIdBySupplierIds(allSupplier.substring(0, allSupplier.length() - 1));
                    if (StringUtils.isEmpty(ruleIdListStr)) {
                        logger.info("=========折扣立减活动该城市未配置线路============");
                        return null;
                    }

                } else {
                    logger.info("=========该城市无供应商============");
                    return null;
                }

            }
        }else {
            //如果是管理员 可以查看所有的线路

        }
        return ruleIdListStr;
    }



    @Override
    public JSONArray queryRule(){
        String ruleIdListStr  = this.getRuleId();
        //如果是管理员查询所有的线路
        if(WebSessionUtil.isSupperAdmin()){
            //
            JSONArray jsonAdmin = new JSONArray();
            Map<String,Object> map = Maps.newHashMap();
            map.put("lineModel",2);
            map.put("status",1);
            String result = MpOkHttpUtil.okHttpGet(configUrl + "/intercityCarUse/getIntercityCarSharingList",map,0,null);
            if(StringUtils.isNotEmpty(result)){
                JSONObject jsonObject = JSONObject.parseObject(result);
                if(jsonObject.get(Constants.CODE) != null && jsonObject.getInteger(Constants.CODE) == 0){
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    jsonArray.forEach(array ->{
                        JSONObject json = (JSONObject) array;
                        JSONObject lineJson =new JSONObject();
                        lineJson.put("id",json.get("lineId") == null ? null : json.getIntValue("lineId"));
                        lineJson.put("lineName",json.get("lineName") == null ? null : json.getString("lineName"));
                        jsonAdmin.add(lineJson);
                    });
                    return jsonAdmin;
                }
            }

        }else {
            if(StringUtils.isNotEmpty(ruleIdListStr)){
                Map<String,Object> map = Maps.newHashMap();
                map.put("lineIds",ruleIdListStr);
                String result = MpOkHttpUtil.okHttpGet(configUrl + "/intercityCarUse/getLineNameByIds",map,0,null);
                if(StringUtils.isNotEmpty(result)){
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if(jsonObject.get(Constants.CODE) != null && jsonObject.getInteger(Constants.CODE) == 0){
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        return jsonArray;
                    }
                }
            }
        }


        return new JSONArray();
    }

}
