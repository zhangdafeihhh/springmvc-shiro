package com.zhuanche.serv.busManage;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.constants.BusConst;
import com.zhuanche.dto.busManage.BusSupplierSettleListDTO;
import com.zhuanche.http.MpOkHttpUtil;

/**
 * @ClassName: BusCommonService
 * @Description: 供应商分佣结算单服务
 * @author: yanyunpeng
 * @date: 2018年12月6日 上午10:25:09
 */
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BusSettlementAdviceService implements BusConst {

    private static final Logger logger = LoggerFactory.getLogger(BusSettlementAdviceService.class);
    private final static String LOG_PRE="【供应商分佣结算单】";

    @Value("${order.pay.url}")
    private String orderPayUrl;

    public JSONObject querySettleDetailList(BusSupplierSettleListDTO dto) {
        Map<String, Object> params = new HashMap<>(16);
        if (StringUtils.isNotBlank(dto.getSupplierIds())) {
            params.put("supplierIds", dto.getSupplierIds());
        }
        if (dto.getStatus() == null) {
            params.put("status", dto.getStatus());
        }
        if (StringUtils.isNotBlank(dto.getStartTime())) {
            params.put("startTime", dto.getStartTime() + " 00:00:00");
        }
        if (StringUtils.isNotBlank(dto.getEndTime())) {
            params.put("endTime", dto.getEndTime() + " 23:59:59");
        }
        params.put("type", 0);
        params.put("pageNo", dto.getPageNum());
        params.put("pageSize", dto.getPageSize());
        logger.info("[ BusSupplierService-querySettleDetailList ] 查供应商分佣账单列表,params={}", params);
        JSONObject result = MpOkHttpUtil.okHttpPostBackJson(orderPayUrl + Pay.SETTLE_SUPPLIER_BILL_LIST, params, 2000, "查供应商分佣账单列表");
        logger.info(LOG_PRE+"查询分佣账单列表参数，param={},查询结果={}",params,JSON.toJSONString(result));
        return result;
    }

    public JSONObject updateSupplierBill(Map<String,Object> param){
        JSONObject result = MpOkHttpUtil.okHttpPostBackJson(orderPayUrl + Pay.SETTLT_SUPPLIER_BILL_LISET, param, 2000, "查供应商分佣账单列表");
        return result;
    }

}
