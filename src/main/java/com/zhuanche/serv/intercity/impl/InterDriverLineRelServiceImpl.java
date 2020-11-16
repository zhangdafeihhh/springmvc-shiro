package com.zhuanche.serv.intercity.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.zhuanche.common.enums.PermissionLevelEnum;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constant.Constants;
import com.zhuanche.dto.mdbcarmanage.InterDriverLineRelDto;
import com.zhuanche.entity.mdbcarmanage.CarAdmUser;
import com.zhuanche.entity.mdbcarmanage.DriverInfoInterCity;
import com.zhuanche.entity.mdbcarmanage.InterDriverLineRel;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.serv.intercity.DriverInfoInterCityService;
import com.zhuanche.serv.intercity.InterDriverLineRelService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.ValidateUtils;
import lombok.extern.log4j.Log4j;
import mapper.mdbcarmanage.ex.CarAdmUserExMapper;
import mapper.mdbcarmanage.ex.InterDriverLineRelExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author fanht
 * @Description
 * @Date 2020/11/3 上午11:30
 * @Version 1.0
 */
@Service
public class InterDriverLineRelServiceImpl implements InterDriverLineRelService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InterDriverLineRelExMapper exMapper;

    @Autowired
    private DriverInfoInterCityService driverInfoInterCityService;

    @Autowired
    private CarAdmUserExMapper carAdmUserExMapper;

    @Autowired
    private CarBizSupplierExMapper supplierExMapper;


    @Value("${config.url}")
    private String configUrl;

    @Override
    public AjaxResponse queryDetail(Integer userId) {
        try {
            InterDriverLineRel lineRel = exMapper.queryDriverLineRelByUserId(userId);
            if (lineRel == null) {
                return AjaxResponse.success(null);
            }

            return AjaxResponse.success(lineRel);
        } catch (Exception e) {
            log.error("查询异常",e);
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }
    }


    @Override
    public InterDriverLineRelDto queryAllLineAndDriver(Integer userId) {
        InterDriverLineRelDto dto = new InterDriverLineRelDto();
        dto.setUserId(userId);
        //根据用户id查询当前用户的权限
        CarAdmUser carAdmUser = carAdmUserExMapper.queryUserPermissionInfo(userId);
        if (carAdmUser == null) {
            return null;
        }
        Set<Integer> setCities = null;
        Set<Integer> setSupplierIds = null;
        try {
            JSONArray jsonLines = new JSONArray();
            StringBuffer stringBuffer = new StringBuffer();
            setCities = null;
            setSupplierIds = null;
            if (ValidateUtils.isAdmin(carAdmUser.getAccountType()) || carAdmUser.getLevel().equals(PermissionLevelEnum.ALL.getCode())) {
                List<CarBizSupplier> listAll = supplierExMapper.queryByCityOrSupplierName(null, null);

                if (CollectionUtils.isNotEmpty(listAll)) {
                    listAll.forEach(i -> stringBuffer.append(i.getSupplierId()).append(Constants.SEPERATER));
                    jsonLines = getLineNames(stringBuffer.substring(0, stringBuffer.length() - 1), jsonLines);
                }
            } else if (carAdmUser.getLevel().equals(PermissionLevelEnum.CITY.getCode())) {
                setCities = Arrays.stream(carAdmUser.getCities().split(Constants.SEPERATER)).map(s -> Integer.valueOf(s.trim()))
                        .collect(Collectors.toSet());
                List<CarBizSupplier> listAll = supplierExMapper.queryByCityOrSupplierName(setCities, null);
                if (CollectionUtils.isNotEmpty(listAll)) {
                    listAll.forEach(i -> stringBuffer.append(i.getSupplierId()).append(Constants.SEPERATER));
                    jsonLines = getLineNames(stringBuffer.substring(0, stringBuffer.length() - 1), jsonLines);
                }
            } else if (carAdmUser.getLevel().equals(PermissionLevelEnum.SUPPLIER.getCode())) {
                setSupplierIds = Arrays.stream(carAdmUser.getSuppliers().split(Constants.SEPERATER)).map(s -> Integer.valueOf(s.trim()))
                        .collect(Collectors.toSet());
                jsonLines = getLineNames(carAdmUser.getSuppliers(), jsonLines);
            }

            dto.setJsonLine(jsonLines);
        } catch (Exception e) {
            log.error("查询线路异常",e);
        }

        try {
            List<DriverInfoInterCity> driverLists = driverInfoInterCityService.queryDrivers(setCities, setSupplierIds);
            JSONArray jsonDriver = new JSONArray();
            if (CollectionUtils.isNotEmpty(driverLists)) {
                driverLists.forEach(i -> {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("driverId", i.getDriverId());
                    jsonObject.put("driverNameLicense", i.getDriverName() + "/" + i.getLicensePlates());
                    jsonDriver.add(jsonObject);
                });
                dto.setJsonDriver(jsonDriver);
            }

        } catch (Exception e) {
            log.error("查询异常", e);
        }
        return dto;
    }


    private JSONArray getLineNames(String supplierIds, JSONArray jsonLines) {

        Map<String, Object> jsonRouteMap = Maps.newHashMap();
        jsonRouteMap.put("supplierIds", supplierIds);
        //wiki http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=43163628
        String lineResult = MpOkHttpUtil.okHttpPost(configUrl + "/intercityCarUse/getLineIdBySupplierIds", jsonRouteMap, 0, null);
        if (StringUtils.isNotEmpty(lineResult)) {
            JSONObject jsonData = JSONObject.parseObject(lineResult);
            String lineIds = jsonData.get(Constants.DATA) == null ? null : jsonData.getString(Constants.DATA);
            if (StringUtils.isNotEmpty(lineIds)) {
                Map<String, Object> jsonNames = Maps.newHashMap();
                jsonNames.put("lineIds", lineIds);
                //wiki http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=43174850
                String jsonLineName = MpOkHttpUtil.okHttpPost(configUrl + "/intercityCarUse/getLineNameByIds", jsonNames, 0, null);
                if (StringUtils.isNotEmpty(jsonLineName) ) {
                    JSONObject linObj = JSONObject.parseObject(jsonLineName);
                    if(linObj != null && linObj.get(Constants.DATA) != null){
                        JSONArray jsonArray = JSONArray.parseArray(linObj.get(Constants.DATA).toString());
                        jsonArray.forEach(json -> {
                            JSONObject obj = (JSONObject) json;
                            JSONObject jsonObjLine = new JSONObject();
                            jsonObjLine.put("lineId", obj.get("id") == null ? null : obj.getIntValue("id"));
                            jsonObjLine.put("lineName", obj.getString("lineName"));
                            jsonLines.add(jsonObjLine);
                        });
                    }

                }
            }
        }
        return jsonLines;
    }

    @Override
    public int addOrUpdateDriverLineRel(Integer id, String driverIds, String lineIds, Integer userId) {
        InterDriverLineRel rel = new InterDriverLineRel();
        if (id != null && id > 0) {
            rel = updateDriverLineRel(driverIds, lineIds, userId);
            rel.setId(id);
            return exMapper.updateByPrimaryKeySelective(rel);
        } else {
            rel = addDriverLineRel(driverIds, lineIds, userId);
            return exMapper.insertSelective(rel);
        }
    }

    public static InterDriverLineRel addDriverLineRel(String driverIds, String lineIds, Integer userId) {
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();

        return InterDriverLineRel.builder().createId(loginUser.getId()).createName(loginUser.getLoginName()).userId(userId)
                .createTime(new Date()).updateId(loginUser.getId()).updateName(loginUser.getLoginName()).updateTime(new Date())
                .driverIds(driverIds).lineIds(lineIds).build();
    }

    public static InterDriverLineRel updateDriverLineRel(String driverIds, String lineIds, Integer userId) {
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();

        return InterDriverLineRel.builder().updateId(loginUser.getId()).userId(userId).updateName(loginUser.getLoginName())
                .updateTime(new Date()).driverIds(driverIds).lineIds(lineIds).build();
    }
}
