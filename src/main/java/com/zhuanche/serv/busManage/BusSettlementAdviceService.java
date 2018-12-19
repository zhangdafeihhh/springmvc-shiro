package com.zhuanche.serv.busManage;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.zhuanche.controller.busManage.BusSettlementAdviceController;
import com.zhuanche.dto.busManage.BusSettlementOrderChangeDTO;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import mapper.rentcar.CarBizSupplierMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private CarBizSupplierMapper supplierMapper;

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

    public JSONObject updateSupplierBill(BusSettlementOrderChangeDTO dto){
        CarBizSupplier supplier = supplierMapper.selectByPrimaryKey(dto.getSupplierId());
        Map<String,Object> param = new HashMap(16);
        param.put("supplierBillId",dto.getSupplierBillId());
        param.put("addMoney",dto.getAddMoney());
        param.put("type",6);
        param.put("orderNo",dto.getOrderNo());
        param.put("phone",supplier.getContactsPhone());
        param.put("cityCode",supplier.getSupplierCity());
        JSONObject otherInfo = new JSONObject();
        otherInfo.put("reasonCode",dto.getReasonCode());
        otherInfo.put("reason", EnumUpdateReason.getReason(dto.getReasonCode()));
        if(dto.getSettleWay()!=null){
            otherInfo.put("settleWay",dto.getSettleWay());
        }
        otherInfo.put("desc",dto.getDesc()==null?"":dto.getDesc());
        param.put("otherInfo",otherInfo);
        JSONObject result = MpOkHttpUtil.okHttpPostBackJson(orderPayUrl + Pay.SETTLT_SUPPLIER_BILL_UPDATE, param, 2000, "查供应商分佣账单列表");
        return result;
    }

    public enum EnumUpdateReason{
        CHANGE_ORDER(0,"订单金额修改"),
        ORDER_PROBLEM(1,"订单问题"),
        SUPPLEMENT_COST(2,"补录费用"),
        OTHER(3,"其他原因");
        EnumUpdateReason(int code, String reason) {
            this.code = code;
            this.reason = reason;
        }

        private int code;
        private String reason;

        public static String getReason(int code){
            for (EnumUpdateReason r:EnumUpdateReason.values()) {
                if(r.code==code){
                    return r.reason;
                }
            }
            return StringUtils.EMPTY;
        }
    }

}
