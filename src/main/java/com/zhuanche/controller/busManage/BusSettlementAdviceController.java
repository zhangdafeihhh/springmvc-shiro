package com.zhuanche.controller.busManage;

import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.entity.mdbcarmanage.BusBizSupplierDetail;
import com.zhuanche.serv.busManage.BusSupplierService;
import com.zhuanche.util.DateUtil;
import com.zhuanche.vo.busManage.BusSupplierInfoVO;
import com.zhuanche.vo.busManage.BusSupplierSettleDetailVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.dto.busManage.BusSupplierSettleListDTO;
import com.zhuanche.serv.busManage.BusCommonService;
import com.zhuanche.serv.busManage.BusSettlementAdviceService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;

/**
 * @ClassName: BusSettlementAdviceController
 * @Description: 巴士结算单管理
 * @author: yanyunpeng
 * @date: 2018年12月7日 上午11:28:50
 */
@RestController
@RequestMapping("/bus/settlement")
@Validated
public class BusSettlementAdviceController {

    private static final Logger logger = LoggerFactory.getLogger(BusSettlementAdviceController.class);

    @Autowired
    private BusCommonService busCommonService;

    @Autowired
    private BusSettlementAdviceService busSettlementAdviceService;

    @Autowired
    private BusSupplierService busSupplierService;


    /**
     * 查询供应商分佣订单明细 TODO 等计费完成接口完善
     *
     * @param dto
     * @return
     */
    @RequestMapping(value = "/pageList")
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
    public AjaxResponse querySettleDetailList(BusSupplierSettleListDTO dto) {
        logger.info("巴士供应商查询账单列表参数=" + JSON.toJSONString(dto));
        getAuth(dto);
        Integer cityId = dto.getCityId();
        Integer supplierId = dto.getSupplierId();
        if (supplierId != null) {
            dto.setSupplierIds(String.valueOf(supplierId));
        } else {
            if (cityId == null) {
                Set<Integer> authOfSupplier = dto.getAuthOfSupplier();
                String join = StringUtils.join(authOfSupplier, ",");
                dto.setSupplierIds(join);
            } else {
                List<Map<Object, Object>> maps = busCommonService.querySuppliers(cityId);
                if (maps.isEmpty()) {
                    return AjaxResponse.success(new ArrayList<>());
                }
                StringBuffer sb = new StringBuffer();
                for (Map<Object, Object> map : maps) {
                    sb.append(String.valueOf(map.get("supplierId"))).append(",");
                }
                dto.setSupplierIds(sb.substring(0, sb.length() - 1));
            }
        }
        JSONObject result = busSettlementAdviceService.querySettleDetailList(dto);
        Integer code = result.getInteger("code");
        if (0 != code) {
            return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "查询失败，请联系管理员");
        }
        JSONArray data = result.getJSONArray("data");
        if (data == null || data.isEmpty()) {
            return AjaxResponse.success(new ArrayList<>());
        }
        Set<Integer> queryparam = data.stream().map(O -> (JSONObject) O).map(O -> O.getInteger("supplierId")).collect(Collectors.toSet());
        //查询供应商的基本信息
        List<BusSupplierInfoVO> supplierInfo = busSupplierService.queryBasicInfoByIds(queryparam);
        Map<Integer, BusSupplierInfoVO> supplierInfoMap = new HashMap<>(16);
        supplierInfo.forEach(o -> {
            supplierInfoMap.put(o.getSupplierId(), o);
        });
        List<BusSupplierSettleDetailVO> collect = data.stream().map(o -> (JSONObject) o).map(o -> {
            BusSupplierSettleDetailVO detail = JSONObject.toJavaObject(o, BusSupplierSettleDetailVO.class);
            Date startTime = new Date(o.getLong("startTime"));
            Date endTime = new Date(o.getLong("endTime"));
            detail.setStartTime(DateUtil.getTimeString(startTime));
            detail.setEndTime(DateUtil.getTimeString(endTime));
            return detail;
        }).map(o -> {
            return buidSettleDetailVO(o, supplierInfoMap);
        }).collect(Collectors.toList());
        return AjaxResponse.success(collect);
    }

    private BusSupplierSettleDetailVO buidSettleDetailVO(BusSupplierSettleDetailVO settleDetail, Map<Integer, BusSupplierInfoVO> infoMap) {
        BusSupplierInfoVO info = infoMap.get(settleDetail.getSupplierId());
        if (info != null) {
            settleDetail.setCityName(info.getCityName());
            settleDetail.setSupplierName(info.getSupplierName());
        }
        return settleDetail;
    }

    private void getAuth(BusSupplierSettleListDTO dto) {
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        Set<Integer> authSupper = loginUser.getSupplierIds();
        Set<Integer> authCity = loginUser.getCityIds();
        if (authSupper.isEmpty()) {
            Map<String, Set<Integer>> param = new HashMap<>(2);
            param.put("cityIds", authCity);
            List<Integer> integers = busSupplierService.querySupplierIdByCitys(param);
            authSupper = new HashSet<>(integers);
        }
        dto.setAuthOfCity(authCity);
        dto.setAuthOfSupplier(authSupper);
    }
}
