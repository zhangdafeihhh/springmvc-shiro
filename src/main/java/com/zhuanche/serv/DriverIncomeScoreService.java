package com.zhuanche.serv;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.rpc.DriverIncomeScoreResponse;
import com.zhuanche.common.rpc.RPCAPI;
import com.zhuanche.common.rpc.RPCResponse;
import com.zhuanche.dto.driver.DriverVoEntity;
import com.zhuanche.dto.rentcar.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 司机收入分
 */
@Service
public class DriverIncomeScoreService {

    @Value("${driver.integral.url}")
    private String DRIVER_INTEGRAL;

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
        map.put("startDate", dto.getStartUpdateTime());
        map.put("endDate", dto.getEndUpdateTime());
        String body = new RPCAPI().requestWithRetry(RPCAPI.HttpMethod.POST, DRIVER_INTEGRAL + "/incomeScore/incomeRecordList", map, null, "UTF-8");
        if (StringUtils.isBlank(body)) return null;
        RPCResponse response = RPCResponse.parse(body);
        if (null == response || response.getCode() != 0 || response.getData() == null) return null;
        List<DriverIncomeScoreRecordDto> list = JSON.parseArray(JSON.toJSONString(response.getData()), DriverIncomeScoreRecordDto.class);
        if (null == list || list.size() == 0) return null;
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
                map.put("data", JSON.parseArray(JSON.toJSONString(response.getData()), DriverIncomeScoreDetailDto.class));//List<DriverIncomeScoreDetailDto>
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
}
