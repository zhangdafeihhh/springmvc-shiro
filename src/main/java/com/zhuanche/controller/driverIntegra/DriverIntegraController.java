package com.zhuanche.controller.driverIntegra;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.dto.driver.DriverTeamEntity;
import com.zhuanche.dto.driver.DriverTeamRelationEntity;
import com.zhuanche.dto.driver.DriverVoEntity;
import com.zhuanche.serv.CarBizDriverInfoService;
import com.zhuanche.serv.IDriverService;
import com.zhuanche.serv.IDriverTeamRelationService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.Common;
import com.zhuanche.util.MyRestTemplate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping(value = "/driverIntegra")
public class DriverIntegraController {

    private static Logger logger =  LoggerFactory.getLogger(DriverIntegraController.class);

    @Autowired
    private IDriverTeamRelationService<DriverTeamRelationEntity> driverTeamRelationService;


    @Autowired
    private IDriverService driverService;

    @Autowired
    @Qualifier("driverIntegralApiTemplate")
    private MyRestTemplate driverIntegralApiTemplate;


    protected Map<String, Object> gridJsonFormate(List<?> rows, int total) {
        rows = null == rows ? new ArrayList<>() : rows;
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(Common.RESULT_ROWS, rows);
        result.put(Common.RESULT_TOTAL, total);
        return result;
    }
    @ResponseBody
    @RequestMapping("/queryDriverIntegralListData")
    public Object queryDriverIntegralListData(DriverVoEntity driverEntity) {

        try{
            logger.info("queryDriverAppraisalListData:司机积分数据列表,参数为："+(driverEntity==null?"null": JSON.toJSONString(driverEntity)));
            // 权限
            SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息
            Set<Integer> citieSet = currentLoginUser.getCityIds();
            Set<Integer> supplierSet = currentLoginUser.getSupplierIds();
            Set<Integer> teamIdSet = currentLoginUser.getTeamIds();
            String cities = "";
            String suppliers = "";
            String teamIds = "";
            if(citieSet != null){
                for(Integer id : citieSet){
                    if(StringUtils.isNotEmpty(cities)){
                        cities += ",";
                    }
                    cities += id;
                }
            }
            if(supplierSet != null){
                for(Integer id : supplierSet){
                    if(StringUtils.isNotEmpty(suppliers)){
                        suppliers += ",";
                    }
                    suppliers += id;
                }
            }
            if(teamIdSet != null){
                for(Integer id : teamIdSet){
                    if(StringUtils.isNotEmpty(teamIds)){
                        teamIds += ",";
                    }
                    teamIds += id;
                }
            }
            if (driverEntity.getServiceCityId() != null && !"".equals(driverEntity.getServiceCityId())) {
                cities = String.valueOf(driverEntity.getServiceCityId());
            }
            if (driverEntity.getSupplierId() != null && !"".equals(driverEntity.getSupplierId())) {
                suppliers = String.valueOf(driverEntity.getSupplierId());
            }
            if (!"".equals(driverEntity.getTeamIds()) && driverEntity.getTeamIds() != null) {
                teamIds = driverEntity.getTeamIds();
            }
            List<DriverVoEntity> rows = new ArrayList<DriverVoEntity>();
            int total = 0;
            driverEntity.setCities(cities);
            driverEntity.setSuppliers(suppliers);
            String driverIdTeam = "";
            if (StringUtils.isNotBlank(teamIds)) {
                String[] teamId = teamIds.split(",");
                DriverTeamRelationEntity params = new DriverTeamRelationEntity();
                params.setTeamId(teamIds);
                DriverTeamEntity paramsTeam = new DriverTeamEntity();
                int j = 0;
                for (int i = 0; i < teamId.length; i++) {
                    paramsTeam.setTeamId(teamId[i]);
                    //不分页查询司机-车队关系
                    List<DriverTeamRelationEntity> driverIdList = this.driverTeamRelationService
                            .selectDriverIdsNoLimit(params);
                    String driverIds = this.driverTeamRelationService.pingDriverIds(driverIdList);
                    if (j == 0) {
                        driverIdTeam = driverIds;
                    } else {
                        driverIdTeam += "," + driverIds;
                    }
                    j++;
                }
                if (driverIdTeam == null || "".equals(driverIdTeam)) {
                    return this.gridJsonFormate(rows, total);
                }
            }else{
                driverIdTeam = driverEntity.getDriverId();
            }
            driverEntity.setDriverId(driverIdTeam);
            total = this.driverService.selectDriverByKeyCountAddCooperation(driverEntity);
            if (total == 0) {
                return this.gridJsonFormate(rows, total);
            }
            //查询司机信息
            rows = this.driverService.selectDriverByKeyAddCooperation(driverEntity);
            List<JSONObject> driverInfoList = new ArrayList<>();
            for (DriverVoEntity driverVoEntity : rows) {
                JSONObject item = new JSONObject();
                item.put("driverId",StringUtils.isNotEmpty(driverVoEntity.getDriverId())?Integer.parseInt(driverVoEntity.getDriverId()):0);
                item.put("cityId",driverVoEntity.getServiceCityId());
                driverInfoList.add(item);
            }
            JSONArray driverIntegralInfoArray = getDriverIntegralInfoList(driverInfoList);
            if(driverIntegralInfoArray != null){
                Map<String,JSONObject> itemMap = new HashMap<>();
                int size = driverIntegralInfoArray.size();
                for(int i=0;i<size;i++ ){
                    JSONObject item = driverIntegralInfoArray.getJSONObject(i);
//				itemMap.put(item.getString("driverId"),item);
                    itemMap.put("flag_"+item.getInteger("driverId"),item);
                }
                String driverId = null;
                for (DriverVoEntity driverVoEntity : rows) {

                    driverId = driverVoEntity.getDriverId() ;
                    JSONObject driverIntegraInfo = itemMap.get("flag_"+driverId);
//				logger.info("调用策略平台批量查询积分-driverId："+driverId+";driverIntegraInfo="+(driverIntegraInfo==null?"null":driverIntegraInfo.toJSONString()));
                    if(driverIntegraInfo != null){
                        //设置司机当月积分
                        driverVoEntity.setMonthIntegral(driverIntegraInfo.getString("monthIntegral"));
                        //设置司机当日积分
                        driverVoEntity.setDayIntegral(driverIntegraInfo.getString("todayIntegral"));
                        //设置司机司机等级
                        driverVoEntity.setMembershipName(driverIntegraInfo.getString("membershipName"));
                    }
                }
            }
            return this.gridJsonFormate(rows, total);
        }catch (Exception e){
            logger.error("司机积分数据列表异常",e);
            throw  e;
        }
    }

    private JSONArray getDriverIntegralInfoList(List<JSONObject> driverInfoList){

        JSONObject jsonObj = null;
        JSONArray  retArray = null;
        long start = System.currentTimeMillis();
        try{
            String url = "/integral/batchGetDriverInfo";
            Map<String,Object> paramMap = new HashMap<>();
            paramMap.put("driverInfo", driverInfoList);

            jsonObj = driverIntegralApiTemplate.postForObject(url,driverInfoList,JSONObject.class);
            logger.info("调用策略平台批量查询积分正常,耗时+"+(System.currentTimeMillis() - start)+";参数为"+(driverInfoList==null?"": driverInfoList.toString())
                    +";返回结果为："+ (jsonObj == null ?"null":JSON.toJSONString(jsonObj)));
            if (jsonObj != null) {
                int code = jsonObj.getInteger("code");
                if(code == 0) {
                    JSONObject dataJSON = jsonObj.getJSONObject("data");
                    if(dataJSON != null){
                        retArray = dataJSON.getJSONArray("data");
                    }

                }
            }
        }catch (Exception e) {
            logger.error("调用策略平台批量查询积分异常,耗时"+(System.currentTimeMillis() - start)+";参数为"+(driverInfoList==null?"": driverInfoList.toString())
                    +";返回结果为："+ (jsonObj == null ?"null":jsonObj.toJSONString()),e);
        }
//		logger.info("调用策略平台批量查询积分-返回结果："+(retArray==null?"nul":retArray.toJSONString()));
        return retArray;

    }
}
