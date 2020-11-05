package com.zhuanche.serv.intercity.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.constant.Constants;
import com.zhuanche.dto.mdbcarmanage.InterDriverLineRelDto;
import com.zhuanche.entity.mdbcarmanage.DriverInfoInterCity;
import com.zhuanche.entity.mdbcarmanage.InterDriverLineRel;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.serv.intercity.DriverInfoInterCityService;
import com.zhuanche.serv.intercity.InterDriverLineRelService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import lombok.extern.log4j.Log4j;
import mapper.mdbcarmanage.ex.InterDriverLineRelExMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author fanht
 * @Description
 * @Date 2020/11/3 上午11:30
 * @Version 1.0
 */
@Log4j
@Service
public class InterDriverLineRelServiceImpl implements InterDriverLineRelService {


    @Autowired
    private InterDriverLineRelExMapper exMapper;

    @Autowired
    private DriverInfoInterCityService driverInfoInterCityService;


    @Value("${config.url}")
    private String configUrl;

    @Override
    public AjaxResponse queryDetail(Integer userId) {

        InterDriverLineRel lineRel = exMapper.queryDriverLineRelByUserId(userId);


        if (lineRel == null) {
            return AjaxResponse.success(null);
        }

        InterDriverLineRelDto dto = new InterDriverLineRelDto();
        dto.setId(lineRel.getId());
        dto.setUserId(userId);
        Set<Integer> setCityIds = WebSessionUtil.isSupperAdmin() ? null : WebSessionUtil.getCurrentLoginUser().getCityIds();
        Set<Integer> setSupplierIds = WebSessionUtil.isSupperAdmin() ? null : WebSessionUtil.getCurrentLoginUser().getSupplierIds();


        try {
            List<DriverInfoInterCity> driverLists = driverInfoInterCityService.queryDrivers(setCityIds, setSupplierIds);
            JSONArray jsonDriver = new JSONArray();
            if (CollectionUtils.isNotEmpty(driverLists)) {
                driverLists.forEach(i -> {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("driverId", i.getDriverId());
                    jsonObject.put("driverNameLicense", i.getDriverName() + "/" + i.getLicensePlates());
                    jsonDriver.add(jsonObject);
                });
            }
            dto.setJsonDriver(jsonDriver);
        } catch (Exception e) {
            log.error("查询异常",e);
        }

        //调用大后台接口
        Map<String, Object> jsonRouteMap = Maps.newHashMap();
        jsonRouteMap.put("cityIds", setToString(setCityIds));
        jsonRouteMap.put("supplierIds", setToString(setSupplierIds));

        try {
            String routeResult = MpOkHttpUtil.okHttpGet(configUrl + "/intercityCarUse/getLineNameByIds", jsonRouteMap, 0, null);
            log.info("==============调用配置后台获取线路结果=========" + JSONObject.toJSONString(routeResult));
            JSONArray jsonLine = new JSONArray();

            if (StringUtils.isNotEmpty(routeResult)) {
                JSONArray jsonArray = JSONArray.parseArray(routeResult);

                jsonArray.forEach(json -> {
                    JSONObject obj = (JSONObject) json;
                    JSONObject jsonObjLine = new JSONObject();
                    jsonObjLine.put("lineId", obj.get(""));
                    jsonObjLine.put("lineName", obj.getString("lineName"));
                    jsonLine.add(jsonObjLine);
                });
            }
            dto.setJsonLine(jsonLine);
        } catch (Exception e) {
            log.error("调用大后台接口异常",e);
        }
        return AjaxResponse.success(dto);
    }

    private String setToString(Set<Integer> setInteger) {
        if (CollectionUtils.isEmpty(setInteger)) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        setInteger.forEach(i -> {
            stringBuilder.append(i + Constants.SEPERATER);
        });
        return stringBuilder.toString().substring(0, stringBuilder.length() - 1);
    }

    @Override
    public int addOrUpdateDriverLineRel(Integer id,String driverIds,String lineIds,Integer userId) {
        InterDriverLineRel rel = new InterDriverLineRel();
        if (id!= null && id > 0) {
            rel.setId(id);
            rel = updateDriverLineRel(driverIds,lineIds,userId);
            return exMapper.updateByPrimaryKeySelective(rel);
        }else {
            rel = addDriverLineRel(driverIds,lineIds,userId);
            return exMapper.insertSelective(rel);
        }
    }

    public static InterDriverLineRel addDriverLineRel(String driverIds,String lineIds,Integer userId) {
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();

        return  InterDriverLineRel.builder().createId(loginUser.getId()).createName(loginUser.getLoginName()).userId(userId)
                .createTime(new Date()).updateId(loginUser.getId()).updateName(loginUser.getLoginName()).updateTime(new Date())
                .driverIds(driverIds).lineIds(lineIds).build();
    }

    public static InterDriverLineRel updateDriverLineRel(String driverIds,String lineIds,Integer userId) {
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();

        return InterDriverLineRel.builder().updateId(loginUser.getId()).userId(userId).updateName(loginUser.getLoginName())
                .updateTime(new Date()).driverIds(driverIds).lineIds(lineIds).build();
    }
}
