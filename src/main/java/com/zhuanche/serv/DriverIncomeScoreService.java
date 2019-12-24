package com.zhuanche.serv;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.rpc.DriverIncomeScoreResponse;
import com.zhuanche.common.rpc.RPCAPI;
import com.zhuanche.common.rpc.RPCResponse;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.dto.driver.DriverVoEntity;
import com.zhuanche.dto.mdbcarmanage.ScoreDetailDTO;
import com.zhuanche.dto.rentcar.*;
import com.zhuanche.http.MpOkHttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 司机收入分
 */
@Service
public class DriverIncomeScoreService {


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${driver.integral.url}")
    private String DRIVER_INTEGRAL;

    @Autowired
    private CarBizDriverInfoService carBizDriverInfoService;

    /**
     * 车管批量查询司机当前出行分信息
     * wiki http://inside-yapi.01zhuanche.com/project/187/interface/api/13959
     *
     * @param driverIds
     * @return
     */
    public Map<Integer, DriverIncomeScoreDto> incomeList(List<Integer> driverIds) {
        if (driverIds == null || driverIds.size() == 0) return null;
        List<String> driverIdsStr = driverIds.stream().map(x -> x + "").collect(Collectors.toList());
        String body = new RPCAPI().requestWithRetry(RPCAPI.HttpMethod.POST, String.format(DRIVER_INTEGRAL + "/incomeScore/incomeList?driverIds=%s", String.join(",", driverIdsStr)), null, null, "UTF-8");
        if (StringUtils.isBlank(body)) return null;
        RPCResponse response = RPCResponse.parse(body);
        if (null == response || response.getCode() != 0 || response.getData() == null) return null;
        List<DriverIncomeScoreDto> list = JSON.parseArray(JSON.toJSONString(response.getData()), DriverIncomeScoreDto.class);
        if (null == list || list.size() == 0) return null;
        else return list.stream().collect(Collectors.toMap(DriverIncomeScoreDto::getDriverId, a -> a, (k1, k2) -> k1));
    }

    /**
     * 车管收入分类型
     * wiki http://inside-yapi.01zhuanche.com/project/187/interface/api/14095
     *
     * @return
     */
    public List<DriverIncomeScoreTypeVo> incomeTypeList() {
        String body = new RPCAPI().requestWithRetry(RPCAPI.HttpMethod.POST, DRIVER_INTEGRAL + "/incomeScore/incomeTypeList", null, null, "UTF-8");
        if (StringUtils.isBlank(body)) return null;
        RPCResponse response = RPCResponse.parse(body);
        if (null == response || response.getCode() != 0 || response.getData() == null) return null;
        return JSON.parseArray(JSON.toJSONString(response.getData()), DriverIncomeScoreTypeVo.class);
    }

    /**
     * 车管收入分具体类型列表
     * wiki http://inside-yapi.01zhuanche.com/project/187/interface/api/14103
     *
     * @return
     */
    public List<DriverIncomeScoreTypeVo> typeList(String incomeType) {
        String body = new RPCAPI().requestWithRetry(RPCAPI.HttpMethod.POST, DRIVER_INTEGRAL + "/incomeScore/typeList?incomeType=" + incomeType, null, null, "UTF-8");
        if (StringUtils.isBlank(body)) return null;
        RPCResponse response = RPCResponse.parse(body);
        if (null == response || response.getCode() != 0 || response.getData() == null) return null;
        return JSON.parseArray(JSON.toJSONString(response.getData()), DriverIncomeScoreTypeVo.class);
    }

    /**
     * 收入分更新记录列表接口
     * wiki http://inside-yapi.01zhuanche.com/project/187/interface/api/13887
     *
     * @param dto
     * @return
     */
    public List<DriverIncomeScoreRecordDto> getIncomeScoreRecord(DriverVoEntity dto) {
        Map<String, Object> map = new HashMap(3);
        map.put("driverId", dto.getDriverId());
        map.put("startDate", dto.getStartDate());
        map.put("endDate", dto.getEndDate());
        String body = new RPCAPI().requestWithRetry(RPCAPI.HttpMethod.POST, DRIVER_INTEGRAL + "/incomeScore/incomeRecordList", map, null, "UTF-8");
        if (StringUtils.isBlank(body)) {return null;}
        RPCResponse response = RPCResponse.parse(body);
        if (null == response || response.getCode() != 0 || response.getData() == null) {return null;}
        List<DriverIncomeScoreRecordDto> list = JSON.parseArray(JSON.toJSONString(response.getData()), DriverIncomeScoreRecordDto.class);
        if (null == list || list.size() == 0) {return null;}
        else {
            fillNamePhone(list, dto.getName(), dto.getPhone());
        }
        return list;
    }

    /**
     * 车管收入分明细列表接口
     * http://inside-yapi.01zhuanche.com/project/187/interface/api/13967
     *
     * @param params
     * @return
     */
    public Map<String, Object> incomeDetailList(DriverIncomeRecordParams params) {
        Map<String, Object> map = new HashMap<>(2);
        Map<String, Object> paramMap = new HashMap<>(8);
        paramMap.put("driverId", params.getDriverId());
        paramMap.put("incomeType", params.getIncomeType());
        paramMap.put("type", params.getType());
        paramMap.put("orderNo", params.getOrderNo());
        paramMap.put("startDate", params.getStartDate());
        paramMap.put("endDate", params.getEndDate());
        paramMap.put("pageNo", params.getPage());
        paramMap.put("pageSize", params.getPagesize());
        String body = new RPCAPI().requestWithRetry(RPCAPI.HttpMethod.POST, DRIVER_INTEGRAL + "/incomeScore/incomeDetailList", paramMap, null, "UTF-8");
        if (StringUtils.isNotBlank(body)) {
            DriverIncomeScoreResponse response = DriverIncomeScoreResponse.parse(body);
            if (null != response && response.getCode() == 0 && response.getData() != null) {
                map.put("data", JSON.parseArray(JSON.toJSONString(response.getData()), DriverIncomeScoreDetailDto.class));
                map.put("page", JSONObject.parseObject(JSON.toJSONString(response.getPage()), DriverIncomeScorePage.class));
            }
        }
        return map;
    }

    private void fillNamePhone(List<DriverIncomeScoreRecordDto> list, String name, String phone) {
        for (DriverIncomeScoreRecordDto dto : list) {
            dto.setPhone(phone);
            dto.setName(name);
        }
    }



    public Map<String,Object> getScoreDetailDTO(Integer driverId,String day,Long scoreDate,String tripScore){
        Map<String,Object> resultMap = Maps.newHashMap();
        List<ScoreDetailDTO> list = new ArrayList<>();

        Map<String,Object> map = Maps.newHashMap();
        map.put("driverId",driverId);
        map.put("day",day);
        map.put("sortState",0);
        String result = MpOkHttpUtil.okHttpPost(DRIVER_INTEGRAL+"/incomeScore/tripScoreDetails",map,0,null);
        if(StringUtils.isEmpty(result)){
            logger.info("调用代理层返回结果为空");
            return resultMap;
        }

        CarBizDriverInfoDTO info = carBizDriverInfoService.querySupplierIdAndNameByDriverId(driverId);

        JSONObject jsonRes = JSONObject.parseObject(result);
        if(jsonRes.get("code") != null && jsonRes.getInteger("code")==0){
            String data = jsonRes.getString("data");
            JSONObject jsonObject = JSONObject.parseObject(data);
            if(jsonObject != null && jsonObject.get("dayDetailArray") != null) {
                JSONArray jsonArray = JSONArray.parseArray(jsonObject.get("dayDetailArray").toString());
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonResult = (JSONObject) jsonArray.get(i);
                    ScoreDetailDTO scoreDetailDTO = new ScoreDetailDTO();
                    if (jsonResult.get("day") != null && jsonResult.get("dayServiceTimeScore") != null && jsonResult.get("isCollect") != null) {
                        String scoreDetailDate = jsonResult.getString("day");
                        String hourScore = jsonResult.getBigDecimal("dayServiceTimeScore").toString();
                        Boolean isTotal = jsonResult.getBoolean("isCollect");
                        scoreDetailDTO.setDriverId(driverId);
                        scoreDetailDTO.setHourScore(hourScore);
                        scoreDetailDTO.setScoreDetailDate(scoreDetailDate);
                        scoreDetailDTO.setIsTotal(isTotal == true ? 1 : 0);
                        scoreDetailDTO.setName(info.getName());
                        scoreDetailDTO.setPhone(info.getPhone());
                        scoreDetailDTO.setDayOfFuWuSC(jsonResult.getBigDecimal("dayOfFuWuSC"));
                        scoreDetailDTO.setDayOfKongXianSC(jsonResult.getBigDecimal("dayOfKongXianSC"));
                        scoreDetailDTO.setDayOfTeShuJL(jsonResult.getBigDecimal("dayOfTeShuJL"));
                        scoreDetailDTO.setDayOfXinRenSC(jsonResult.getBigDecimal("dayOfXinRenSC"));
                        scoreDetailDTO.setDayOfDiaoDu(jsonResult.getBigDecimal("dayOfDiaoDu"));
                        scoreDetailDTO.setDayOfYingDa(jsonResult.getBigDecimal("dayOfYingDa"));
                        list.add(scoreDetailDTO);
                    }
                }
                resultMap.put("scoreDetailDTO",list);
            }

            if(jsonObject.get("finishOrderDay") != null){
                String finishOrderDay = jsonObject.getBigDecimal("finishOrderDay").toString();
                resultMap.put("finishOrderDay",finishOrderDay);
            }if(jsonObject.get("calScoreDay") != null){
                String calScoreDay = jsonObject.getBigDecimal("calScoreDay").toString();
                resultMap.put("calScoreDay",calScoreDay);
            }if(jsonObject.get("baseTripScore") != null){
                String baseTripScore = jsonObject.getBigDecimal("baseTripScore").toString();
                resultMap.put("baseTripScore",baseTripScore);
            }if(jsonObject.get("dispatchRollDay") != null){
                String dispatchRollDay = jsonObject.getBigDecimal("dispatchRollDay").toString();
                resultMap.put("dispatchRollDay",dispatchRollDay);
            }if(jsonObject.get("sumOfDispatchScore") != null){
                String sumOfDispatchScore = jsonObject.getBigDecimal("sumOfDispatchScore").toString();
                resultMap.put("sumOfDispatchScore",sumOfDispatchScore);
            }if(jsonObject.get("sumOfTripScore") != null){
                String sumOfTripScore = jsonObject.getBigDecimal("sumOfTripScore").toString();
                resultMap.put("sumOfTripScore",sumOfTripScore);
            }if(jsonObject.get("collectScore") != null){
                resultMap.put("tripScore",jsonObject.get("collectScore").toString());
            }
            if (jsonObject.get("dayTripScore") != null){
                resultMap.put("dayTripScore",jsonObject.get("dayTripScore").toString());
            }
            if (jsonObject.get("sumOfFuWuSC") != null){
                resultMap.put("sumOfFuWuSC",jsonObject.get("sumOfFuWuSC").toString());
            }
            if (jsonObject.get("sumOfKongXianSC") != null){
                resultMap.put("sumOfKongXianSC",jsonObject.get("sumOfKongXianSC").toString());
            }
            if (jsonObject.get("sumOfTeShuJL") != null){
                resultMap.put("sumOfTeShuJL",jsonObject.get("sumOfTeShuJL").toString());
            }
            if (jsonObject.get("sumOfXinRenSC") != null){
                resultMap.put("sumOfXinRenSC",jsonObject.get("sumOfXinRenSC").toString());
            }
            if (jsonObject.get("sumOfDiaoDu") != null){
                resultMap.put("sumOfDiaoDu",jsonObject.get("sumOfDiaoDu").toString());
            }
            if (jsonObject.get("sumOfYingDa") != null){
                resultMap.put("sumOfYingDa",jsonObject.get("sumOfYingDa").toString());
            }
            if (jsonObject.get("rollbackDay") != null){
                resultMap.put("rollbackDay",jsonObject.get("rollbackDay").toString());
            }
            if (jsonObject.get("topValDay") != null){
                resultMap.put("topValDay",jsonObject.get("topValDay").toString());
            }

            resultMap.put("driverId",driverId);
            resultMap.put("dispatchTime",scoreDate);
            resultMap.put("scoreDate",day);

        }else {
            logger.info("调用代理层接口返回结果数据异常",jsonRes);
        }
        return resultMap;
    }
}
