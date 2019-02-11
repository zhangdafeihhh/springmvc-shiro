package com.zhuanche.controller.user;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.enums.PermissionLevelEnum;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.entity.mdbcarmanage.CarAdmUser;
import com.zhuanche.entity.mdbcarmanage.CarDriverTeam;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.serv.authc.RoleManagementService;
import com.zhuanche.serv.common.CitySupplierTeamCommonService;
import mapper.mdbcarmanage.CarAdmUserMapper;
import mapper.mdbcarmanage.ex.CarAdmUserExMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@Controller
@RequestMapping("/permission")
public class PermissionController {


    private static final Logger logger = LoggerFactory.getLogger(PermissionController.class);

    @Autowired
    private CitySupplierTeamCommonService citySupplierTeamCommonService;

    @Autowired
    private CarAdmUserExMapper carAdmUserExMapper;

    @Autowired
    private CarAdmUserMapper carAdmUserMapper;

    @Autowired
    private RoleManagementService roleManagementService;

    @RequestMapping("/findByUid")
    @ResponseBody
    public AjaxResponse getPermissionInfo(Integer userId){
        CarAdmUser user = carAdmUserExMapper.queryUserPermissionInfo(userId);
        if (user == null){
            return AjaxResponse.fail(RestErrorCode.USER_NOT_EXIST);
        }
        JSONObject data = new JSONObject();
        data.put("uid", user.getUserId());
        data.put("username", user.getUserName());
        data.put("account", user.getAccount());
        data.put("phone", user.getPhone());
        PermissionLevelEnum enumByCode = PermissionLevelEnum.getEnumByCode(user.getLevel());
        data.put("level", enumByCode == null ? "暂无" : enumByCode.getName());
        List<String> roleName = roleManagementService.getAllRoleName(user.getUserId());
        data.put("roleNames", roleName);
        List<String> levelRange = null;
        if (enumByCode != null) {
            switch (enumByCode) {
                case ALL:
                    break;
                case CITY:
                    String cities = user.getCities();
                    Set<String> cityId = new HashSet<>();
                    stringToSet(cities, cityId, false);
                    levelRange = citySupplierTeamCommonService.getCityList(cityId);
                    break;
                case SUPPLIER:
                    String suppliers = user.getSuppliers();
                    Set<String> supplierId = new HashSet<>();
                    stringToSet(suppliers, supplierId, false);
                    levelRange = citySupplierTeamCommonService.getSupplierList(supplierId);
                    break;
                case TEAM:
                    String teamId = user.getTeamId();
                    Set<String> teamIds = new HashSet<>();
                    stringToSet(teamId, teamIds, false);
                    levelRange = citySupplierTeamCommonService.getTeamList(teamIds);
                    break;
                case GROUP:
                    String groupId = user.getGroupIds();
                    Set<String> groupIds = new HashSet<>();
                    stringToSet(groupId, groupIds, false);
                    levelRange = citySupplierTeamCommonService.getGroupList(groupIds);
                    break;
            }
        }
        data.put("levelInfos", levelRange);
        return AjaxResponse.success(data);
    }

    @RequestMapping("/levelList")
    @ResponseBody
    public AjaxResponse getPermissionLevelList() {
        PermissionLevelEnum[] values = PermissionLevelEnum.values();
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (PermissionLevelEnum levelEnum : values) {
            builder.append(levelEnum).append(",");
        }
        builder.replace(builder.length() - 1, builder.length(), "").append("]");
        return AjaxResponse.success(builder.toString());
    }

    @RequestMapping("/rangeList")
    @ResponseBody
    public AjaxResponse getRangeList(Integer level, String cityIds, Integer cityId,
                                     String supplierIds,
                                     String teamIds) {

        PermissionLevelEnum levelEnum = PermissionLevelEnum.getEnumByCode(level);
        if (levelEnum == null) {
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR, "param level is invalid");
        }
        JSONObject result = new JSONObject();
        JSONArray data = new JSONArray();
        switch (levelEnum) {
            case ALL:
                result.put("level", PermissionLevelEnum.ALL.getCode());
                result.put("list", data);
                return AjaxResponse.success(result);
            case CITY:
                result.put("level", PermissionLevelEnum.CITY.getCode());
                List<CarBizCity> carBizCities = citySupplierTeamCommonService.queryCityList();
                carBizCities.forEach(carBizCity -> {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", carBizCity.getCityName());
                    jsonObject.put("id", carBizCity.getCityId());
                    data.add(jsonObject);
                });
                result.put("list", data);
                return AjaxResponse.success(result);
            case SUPPLIER:
                result.put("level", PermissionLevelEnum.SUPPLIER.getCode());
                Set<Integer> citySet = new HashSet<>();
                if (cityId != null && cityId > 0){
                    citySet.add(cityId);
                }
                stringToSet(cityIds, citySet, true);
                List<CarBizSupplier> carBizSuppliers = citySupplierTeamCommonService.querySupplierList(citySet);

                carBizSuppliers.forEach(carBizSupplier -> {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", carBizSupplier.getSupplierFullName());
                    jsonObject.put("id", carBizSupplier.getSupplierId());
                    data.add(jsonObject);
                });
                result.put("list", data);
                return AjaxResponse.success(result);
            case TEAM:
                result.put("level", PermissionLevelEnum.TEAM.getCode());
                Set<String> cityIdSet = new HashSet<>();
                if (cityId != null && cityId > 0) {
                    cityIdSet.add(cityId.toString());
                }
                Set<String> supplierSet = new HashSet<>();
                stringToSet(supplierIds, supplierSet, false);
                List<CarDriverTeam> carDriverTeams = citySupplierTeamCommonService.queryDriverTeamList(cityIdSet, supplierSet);
                carDriverTeams.forEach(driverTeam -> {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", driverTeam.getTeamName());
                    jsonObject.put("id", driverTeam.getId());
                    data.add(jsonObject);
                });
                result.put("list", data);
                return AjaxResponse.success(result);
            case GROUP:
                result.put("level", PermissionLevelEnum.GROUP.getCode());
                Set<String> teamSet = new HashSet<>();
                stringToSet(teamIds, teamSet, false);
                List<Map<String, Object>> groupList = citySupplierTeamCommonService.getTeamsByPids(teamSet);
                groupList.forEach(map -> {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", map.get("groupName"));
                    jsonObject.put("id", map.get("id"));
                    data.add(jsonObject);
                });
                result.put("list", data);
                return AjaxResponse.success(result);
            default:
                return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR, "param level is invalid");
        }
    }

    @RequestMapping("/upsert")
    @ResponseBody
    public AjaxResponse savePermissionInfo(Integer level, String cities, String suppliers, String teams, String groups, Integer userId){
        PermissionLevelEnum enumByCode = PermissionLevelEnum.getEnumByCode(level);
        if (enumByCode == null){
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR, "param level is invalid");
        }
        CarAdmUser user = new CarAdmUser();
        user.setUserId(userId);
        switch (enumByCode){
            case ALL:
                user.setLevel(PermissionLevelEnum.ALL.getCode());
                break;
            case CITY:
                user.setCities(cities);
                user.setLevel(PermissionLevelEnum.CITY.getCode());
                break;
            case SUPPLIER:
                user.setCities(cities);
                user.setSuppliers(suppliers);
                user.setLevel(PermissionLevelEnum.SUPPLIER.getCode());
                break;
            case TEAM:
                user.setCities(cities);
                user.setSuppliers(suppliers);
                user.setTeamId(teams);
                user.setLevel(PermissionLevelEnum.TEAM.getCode());
                break;
            case GROUP:
                user.setCities(cities);
                user.setSuppliers(suppliers);
                user.setTeamId(teams);
                user.setGroupIds(groups);
                user.setLevel(PermissionLevelEnum.GROUP.getCode());
                break;
        }
        carAdmUserMapper.updateByPrimaryKey(user);
        return AjaxResponse.success("success");
    }

    private void stringToSet(String str, Set set, boolean isNum) {
        if (StringUtils.isNotEmpty(str)) {
            Stream.of(str.split(",")).forEach(elem -> {
                Object obj;
                if (isNum){
                    obj = Integer.parseInt(elem);
                }else {
                    obj = elem;
                }
                set.add(obj);
            }
            );
        }
    }
}
