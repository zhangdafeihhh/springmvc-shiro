package com.zhuanche.serv.busManage;

import java.util.HashMap;
import java.util.Map;

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
 * @Description: 巴士公用接口服务类
 * @author: yanyunpeng
 * @date: 2018年12月6日 上午10:25:09
 * 
 */
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BusSettlementAdviceService implements BusConst {

	private static final Logger logger = LoggerFactory.getLogger(BusSettlementAdviceService.class);

	@Value("${order.pay.url}")
	private String orderPayUrl;

	public JSONArray querySettleDetailList(BusSupplierSettleListDTO dto) {
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
		params.put("type",0);
		params.put("pageNum",dto.getPageNum());
		params.put("pageSize",dto.getPageSize());
		try {
			logger.info("[ BusSupplierService-querySettleDetailList ] 查供应商分佣账单列表,params={}", params);
			JSONObject result = MpOkHttpUtil.okHttpPostBackJson(orderPayUrl + Pay.SETTLE_DETAIL_LIST, params , 2000, "查供应商分佣账单列表");
			if (result.getIntValue("code") == 0) {
				JSONArray jsonArray = result.getJSONArray("data");
				return jsonArray;
			} else {
				logger.info("[ BusSupplierService-getProrateList ] 查供应商分佣账单列表 调用接口出错,params={},errorMsg={}", params, result.getString("msg"));
			}
		} catch (Exception e) {
			logger.error("[ BusSupplierService-getProrateList ] 查供应商分佣账单列表 异常,params={},errorMsg={}", params, e.getMessage(), e);
		}
		return null;
	}

}
