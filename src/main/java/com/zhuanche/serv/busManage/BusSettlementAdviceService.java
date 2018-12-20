package com.zhuanche.serv.busManage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constants.BusConst;
import com.zhuanche.dto.busManage.*;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.DateUtil;
import com.zhuanche.vo.busManage.BusSettlementInvoiceVO;
import com.zhuanche.vo.busManage.BusSettlementPaymentVO;
import mapper.rentcar.CarBizSupplierMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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
	private final static String LOG_PRE = "【供应商分佣结算单】";

	@Autowired
	private CarBizSupplierMapper carBizSupplierMapper;

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
        otherInfo.put("addMoney",dto.getAddMoney());
        otherInfo.put("updateTime", DateUtil.creatConciseTimeString());
		SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
		otherInfo.put("updateName",user.getLoginName());
        if(dto.getSettleWay()!=null){
            otherInfo.put("settleWay",dto.getSettleWay());
        }
        otherInfo.put("desc",dto.getDesc()==null?"":dto.getDesc());
        param.put("otherInfo",otherInfo);
        JSONObject result = MpOkHttpUtil.okHttpPostBackJson(orderPayUrl + Pay.SETTLT_SUPPLIER_BILL_UPDATE, param, 2000, "查供应商分佣账单列表");
        return result;
    }

    public JSONObject queryOrderList(BusSettleOrderListDTO dto){
		JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(dto));
		Map<String, Object> innerMap = jsonObject.getInnerMap();
		JSONObject result = MpOkHttpUtil.okHttpPostBackJson(orderPayUrl + Pay.SETTLT_DETAIL_LIST, innerMap, 2000, "查供应商分佣账单流水列表");
		return result;
	}

    /**
     * @Title: confirmSettle
     * @Description: 结算单确认
     * @param supplierBillId
     * @return AjaxResponse
     * @throws
     */
    public AjaxResponse confirm(String supplierBillId) {
    	String errorMsg = confirmSettle(supplierBillId);
		if (StringUtils.isNotBlank(errorMsg)) {
			return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, errorMsg);
		}
		return AjaxResponse.success(null);
	}

	/**
	 * @Title: queryInvoiceInfo
	 * @Description: 结算单确认收票窗口查询
	 * @param supplierBillId
	 * @return BusSettlementInvoiceVO
	 * @throws
	 */
	public BusSettlementInvoiceVO queryInvoiceInfo(String supplierBillId) {
		// 查询账单信息
		JSONObject billDetail = getBillDetail(supplierBillId);
		if (billDetail == null) {
			return null;
		}
		BusSettlementInvoiceVO invoiceVO = JSON.toJavaObject(billDetail, BusSettlementInvoiceVO.class);
		
		// 补充信息
		// 供应商名称
		if (invoiceVO != null) {
			Integer supplierId = billDetail.getInteger("supplierId");
			if (supplierId != null) {
				CarBizSupplier supplier = carBizSupplierMapper.selectByPrimaryKey(supplierId);
				if (supplier != null) {
					invoiceVO.setSupplierName(supplier.getSupplierFullName());
				}
			}
		}
		
		return invoiceVO;
	}
	
	/**
	 * @Title: saveInvoiceInfo
	 * @Description: 结算单确认收票窗口保存
	 * @param invoiceDTO
	 * @return AjaxResponse
	 * @throws
	 */
	public AjaxResponse saveInvoiceInfo(BusSettlementInvoiceDTO invoiceDTO) {
		String errorMsg = confirmInvoice(invoiceDTO);
		if (StringUtils.isNotBlank(errorMsg)) {
			return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, errorMsg);
		}
		return AjaxResponse.success(null);
	}
	
	/**
	 * @Title: queryPaymentInfo
	 * @Description: 结算单确认收款窗口查询
	 * @param supplierBillId
	 * @return BusSettlementPaymentVO
	 * @throws
	 */
	public BusSettlementPaymentVO queryPaymentInfo(String supplierBillId) {
		// 查询账单信息
		JSONObject billDetail = getBillDetail(supplierBillId);
		if (billDetail == null) {
			return null;
		}
		BusSettlementPaymentVO paymentVO = JSON.toJavaObject(billDetail, BusSettlementPaymentVO.class);
		
		// 补充信息
		// 供应商名称
		if (paymentVO != null) {
			Integer supplierId = billDetail.getInteger("supplierId");
			if (supplierId != null) {
				CarBizSupplier supplier = carBizSupplierMapper.selectByPrimaryKey(supplierId);
				if (supplier != null) {
					paymentVO.setSupplierName(supplier.getSupplierFullName());
				}
			}
		}
		
		return paymentVO;
	}
	
	/**
	 * @Title: savePaymentInfo
	 * @Description: 结算单确认收款窗口保存
	 * @param paymentDTO
	 * @return AjaxResponse
	 * @throws
	 */
	public AjaxResponse savePaymentInfo(BusSettlementPaymentDTO paymentDTO) {
		String errorMsg = confirmPay(paymentDTO);
		if (StringUtils.isNotBlank(errorMsg)) {
			return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, errorMsg);
		}
		return AjaxResponse.success(null);
	}

	/**
	 * @Title: confirmSettle
	 * @Description: 供应商账单确认结算
	 * @param paymentDTO
	 * @return String
	 * @throws
	 */
	public String confirmSettle(String supplierBillId) {
		if (StringUtils.isNotBlank(supplierBillId)) {
			// 请求参数
			Map<String,Object> params = new HashMap<>();
			params.put("supplierBillId", supplierBillId);
			params.put("settleName", WebSessionUtil.getCurrentLoginUser().getName());// 结算人
			
			try {
				logger.info("[ BusSettlementAdviceService-confirmSettle ] 供应商账单确认结算,params={}", params);
				JSONObject result = MpOkHttpUtil.okHttpPostBackJson(orderPayUrl + Pay.SETTLE_SUPPLIER_BILL_CONFIRM_SETTLE, params , 2000, "供应商账单确认结算");
				if (result.getIntValue("code") != 0) {
					String errorMsg = result.getString("msg");
					logger.info("[ BusSettlementAdviceService-confirmSettle ] 供应商账单确认结算调用接口出错,params={},errorMsg={}", params, errorMsg);
					return errorMsg;
				}
			} catch (Exception e) {
				logger.error("[ BusSettlementAdviceService-confirmSettle ] 供应商账单确认结算异常,params={},errorMsg={}", params, e.getMessage(), e);
				return "供应商账单确认开票异常";
			}
		}
		return null;
	}

	/**
	 * @Title: getBillDetail
	 * @Description: 供应商账单查询根据账单id
	 * @param supplierBillId
	 * @return JSONObject
	 * @throws
	 */
	public JSONObject getBillDetail(String supplierBillId) {
		if (StringUtils.isNotBlank(supplierBillId)) {
			Map<String, Object> params = new HashMap<>();
			params.put("supplierBillId", supplierBillId);
			try {
				logger.info("[ BusSettlementAdviceService-getBillDetail ] 供应商账单查询根据账单id,params={}", params);
				JSONObject result = MpOkHttpUtil.okHttpPostBackJson(orderPayUrl + Pay.SETTLE_SUPPLIER_BILL_DETAIL, params , 2000, "供应商账单查询根据账单id");
				if (result.getIntValue("code") == 0) {
					JSONObject jsonArray = result.getJSONObject("data");
					return jsonArray;
				} else {
					logger.info("[ BusSettlementAdviceService-getBillDetail ] 供应商账单查询根据账单id调用接口出错,params={},errorMsg={}", params, result.getString("msg"));
				}
			} catch (Exception e) {
				logger.error("[ BusSettlementAdviceService-getBillDetail ] 供应商账单查询根据账单id异常,params={},errorMsg={}", params, e.getMessage(), e);
			}
		}
		return null;
	}
	
	/**
	 * @Title: getBillDetail
	 * @Description: 供应商账单确认开票
	 * @param supplierBillId
	 * @return JSONObject
	 * @throws
	 */
	public String confirmInvoice(BusSettlementInvoiceDTO invoiceDTO) {
		if (invoiceDTO != null) {
			// 开票人
			invoiceDTO.setInvoiceName(WebSessionUtil.getCurrentLoginUser().getName());
			
			// 请求参数
			String jsonString = JSON.toJSONStringWithDateFormat(invoiceDTO, JSON.DEFFAULT_DATE_FORMAT, new SerializerFeature[0]);
			JSONObject json = (JSONObject) JSONObject.parse(jsonString);
			Map<String, Object> params = json.getInnerMap();
			
			try {
				logger.info("[ BusSettlementAdviceService-confirmInvoice ] 供应商账单确认开票,params={}", params);
				JSONObject result = MpOkHttpUtil.okHttpPostBackJson(orderPayUrl + Pay.SETTLE_SUPPLIER_BILL_CONFIRM_INVOICE, params , 2000, "供应商账单确认开票");
				if (result.getIntValue("code") != 0) {
					String errorMsg = result.getString("msg");
					logger.info("[ BusSettlementAdviceService-confirmInvoice ] 供应商账单确认开票调用接口出错,params={},errorMsg={}", params, errorMsg);
					return errorMsg;
				}
			} catch (Exception e) {
				logger.error("[ BusSettlementAdviceService-confirmInvoice ] 供应商账单确认开票异常,params={},errorMsg={}", params, e.getMessage(), e);
				return "供应商账单确认开票异常";
			}
		}
		return null;
	}
	
	/**
	 * @Title: confirmPay
	 * @Description: 供应商账单确认收款
	 * @param invoiceDTO
	 * @return String
	 * @throws
	 */
	public String confirmPay(BusSettlementPaymentDTO paymentDTO) {
		if (paymentDTO != null) {
			// 开票人
			paymentDTO.setPayName(WebSessionUtil.getCurrentLoginUser().getName());
			
			// 请求参数
			String jsonString = JSON.toJSONStringWithDateFormat(paymentDTO, JSON.DEFFAULT_DATE_FORMAT, new SerializerFeature[0]);
			JSONObject json = (JSONObject) JSONObject.parse(jsonString);
			Map<String, Object> params = json.getInnerMap();
			
			try {
				logger.info("[ BusSettlementAdviceService-confirmPay ] 供应商账单确认打款,params={}", params);
				JSONObject result = MpOkHttpUtil.okHttpPostBackJson(orderPayUrl + Pay.SETTLE_SUPPLIER_BILL_CONFIRM_PAY, params , 2000, "供应商账单确认打款");
				if (result.getIntValue("code") != 0) {
					String errorMsg = result.getString("msg");
					logger.info("[ BusSettlementAdviceService-confirmPay ] 供应商账单确认打款调用接口出错,params={},errorMsg={}", params, errorMsg);
					return errorMsg;
				}
			} catch (Exception e) {
				logger.error("[ BusSettlementAdviceService-confirmPay ] 供应商账单确认打款异常,params={},errorMsg={}", params, e.getMessage(), e);
				return "供应商账单确认开票异常";
			}
		}
		return null;
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
