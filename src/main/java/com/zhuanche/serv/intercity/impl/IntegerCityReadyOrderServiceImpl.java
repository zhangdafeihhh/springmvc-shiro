package com.zhuanche.serv.intercity.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.constant.Constants;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.serv.intercity.IntegerCityReadyOrderService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author fanht
 * @Description
 * @Date 2020/8/26 上午10:31
 * @Version 1.0
 */
@Service
public class IntegerCityReadyOrderServiceImpl implements IntegerCityReadyOrderService{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${order.saas.es.url}")
    private String esOrderDataSaasUrl;

    @Autowired
    private CarBizSupplierExMapper carBizSupplierExMapper;


    @Value("${config.url}")
    private String configUrl;


    @Override
    public AjaxResponse orderWrestQuery(Integer pageNum, Integer pageSize) {
        Map<String, Object> map = Maps.newHashMap();

        /**1.根据当前用户的数据权限获取对应权限下的线路ids*/
        map = this.getRuleIdBatch(map);

        if (map == null) {
            return AjaxResponse.success(null);
        }
        /**2.调用订单接口分页查询所有的线路*/
        map = wrestQueryMap(map,pageNum,pageSize);

        String url = esOrderDataSaasUrl + "/order/v2/search";
        /**调用订单组接口查询*/
        String result = MpOkHttpUtil.okHttpGet(url, map, 0, null);
        if (StringUtils.isNotEmpty(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            int code = jsonObject.getIntValue(Constants.CODE);
            /**0表示有结果返回*/
            if (code == 0) {
                JSONObject jsonData = jsonObject.getJSONObject(Constants.DATA);
                if (jsonData != null && jsonData.get(Constants.DATA) != null) {
                    /**3.去重后返回所有的线路id和名称*/
                    return this.response(jsonData);
                }
            }
        }
        return AjaxResponse.success(null);
    }



    private Map<String, Object> wrestQueryMap(Map<String, Object> map, Integer pageNum,
                                              Integer pageSize) {
        map.put("pageNo", pageNum);
        map.put("pageSize", pageSize);
        map.put("status", 13);
        map.put("supplierIdBatch", "");
        /**添加排序字段*/
        JSONObject jsonSort = new JSONObject();
        jsonSort.put("field", "createDate");
        jsonSort.put("operator", "desc");
        JSONArray arraySort = new JSONArray();
        arraySort.add(jsonSort);
        map.put("sort", arraySort.toString());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSS");
        String transId = sdf.format(new Date());
        map.put("transId", transId);

        /**根据不同权限添加过滤条件*/
        map.put("serviceTypeIdBatch", String.valueOf(Constants.INTEGER_SERVICE_TYPE));

        return map;
    }


    private Map<String,Object> getRuleIdBatch(Map<String,Object> map){
        String supplierIdBatch = "";
        if (!WebSessionUtil.isSupperAdmin()) {
            SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
            Set<Integer> suppliersSet = loginUser.getSupplierIds();
            /**如果是供应商级别*/
            if (suppliersSet != null && suppliersSet.size() > 0) {
                StringBuilder supplierBuilder = new StringBuilder();
                for (Integer supplierId : suppliersSet) {
                    supplierBuilder.append(supplierId).append(Constants.SEPERATER);
                }
                if (StringUtils.isNotEmpty(supplierBuilder.toString())) {
                    supplierIdBatch = supplierBuilder.toString().substring(0, supplierBuilder.toString().length() - 1);
                }
                String lineIds = this.getLineIdBySupplierIds(supplierIdBatch);
                if (StringUtils.isEmpty(lineIds)) {
                    logger.info("=========该供应商未配置线路============");
                    return null;
                }
                if (StringUtils.isNotBlank(lineIds)) {
                    map.put("ruleIdBatch", lineIds);
                } else {
                    map.put("ruleIdBatch", "-1");
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
                    String lineIds = this.getLineIdBySupplierIds(allSupplier.substring(0, allSupplier.length() - 1));
                    if (StringUtils.isEmpty(lineIds)) {
                        logger.info("=========该城市未配置线路=============");
                        return null;
                    }

                    if (StringUtils.isNotBlank(lineIds)) {
                        map.put("ruleIdBatch", lineIds);
                    } else {
                        map.put("ruleIdBatch", "-1");
                    }
                } else {
                    logger.info("=========该城市未配置线路============");
                    return null;
                }
            }
        }
        return map;
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
            logger.info("配置中心供应商--{}查询路线ID集合返回结果集-- {}", supplierIdBatch, lineResult);
            if (StringUtils.isNotEmpty(lineResult)) {
                JSONObject jsonResult = JSONObject.parseObject(lineResult);
                if (jsonResult != null && jsonResult.getInteger(Constants.CODE) == 0) {
                    if (jsonResult.get(Constants.DATA) != null) {
                        /**如果长度超过500 传 -1 ,因为订单那边有参数长度限制*/
                        String ruleBatchResult = jsonResult.get(Constants.DATA).toString();

                        if (ruleBatchResult.split(Constants.SEPERATER).length > 500) {
                            return Constants.AllRULE;
                        }
                        return jsonResult.get(Constants.DATA).toString();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("查询配置路线ID异常" + e);
            return "";
        }
        return "";
    }



    private AjaxResponse response(JSONObject jsonData ){
        try {
            JSONArray jsonArray = jsonData.getJSONArray("data");

            JSONObject jsonResult = new JSONObject();

            Map<String,String> map = Maps.newHashMap();
            jsonArray.forEach(array -> {
                JSONObject disObject = (JSONObject) array;
                if(!map.containsKey(disObject.getString(Constants.RULEID))){
                    map.put(disObject.getString(Constants.RULEID),disObject.getString(Constants.LINENAME));
                }
            });

            jsonResult.put("data", map);

            return AjaxResponse.success(jsonResult);
        } catch (Exception e) {
            logger.error("查询异常", e);
        }
        return AjaxResponse.success(null);
    }

}
