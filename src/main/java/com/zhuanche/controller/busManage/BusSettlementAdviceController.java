package com.zhuanche.controller.busManage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.dto.busManage.BusSettlementAmendmentDTO;
import com.zhuanche.dto.busManage.BusSettlementInvoiceDTO;
import com.zhuanche.dto.busManage.BusSettlementPaymentDTO;
import com.zhuanche.dto.busManage.BusSupplierSettleListDTO;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.serv.CarBizSupplierService;
import com.zhuanche.serv.busManage.BusCommonService;
import com.zhuanche.serv.busManage.BusSettlementAdviceService;
import com.zhuanche.serv.busManage.BusSupplierService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.DateUtil;
import com.zhuanche.vo.busManage.BusSettlementInvoiceVO;
import com.zhuanche.vo.busManage.BusSettlementPaymentVO;
import com.zhuanche.vo.busManage.BusSupplierInfoVO;
import com.zhuanche.vo.busManage.BusSupplierSettleDetailVO;

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
    private static final String LOG_PRE="【供应商分佣结算单管理】";

    @Autowired
    private BusCommonService busCommonService;

    @Autowired
    private BusSettlementAdviceService busSettlementAdviceService;

    @Autowired
    private BusSupplierService busSupplierService;

    @Autowired
    private CarBizSupplierService supplierService;


    /**
     * 查询供应商账单列表 TODO 等计费完成接口完善
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
        if (code ==null||0 != code) {
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
            Date settleTime = new Date(o.getLong("settleTime"));
            detail.setStartTime(DateUtil.getTimeString(startTime));
            detail.setEndTime(DateUtil.getTimeString(endTime));
            detail.setSettleTime(DateUtil.getTimeString(settleTime));
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

    @RequestMapping(value = "/transactions/save",method = RequestMethod.POST)
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
    public AjaxResponse orderRevision(BusSettlementAmendmentDTO dto){
        logger.info(LOG_PRE+"修改订单记录,参数="+JSON.toJSONString(dto));
        CarBizSupplier supplier = supplierService.selectByPrimaryKey(dto.getSupplierId());
        Map<String,Object> param = new HashMap(16);
        param.put("supplierBillId",dto.getSupplierBillId());
        param.put("addMoney",dto.getAddMoney());
        param.put("type",6);
        param.put("orderNo",dto.getOrderNo());
        param.put("phone",supplier.getContactsPhone());
        param.put("cityCode",supplier.getSupplierCity());
        JSONObject otherInfo = new JSONObject();
        otherInfo.put("reasonCode",dto.getReasonCode());
        otherInfo.put("reason",EnumUpdateReason.getReason(dto.getReasonCode()));
        otherInfo.put("settleWay",dto.getSettleWay());
        otherInfo.put("desc",dto.getDesc()==null?"":dto.getDesc());
        param.put("otherInfo",otherInfo);
        JSONObject result = busSettlementAdviceService.updateSupplierBill(param);
        Integer code = result.getInteger("code");
        if (code==null||0 != code) {
            logger.error(LOG_PRE+"修改账单失败="+result.getString("msg"));
            return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "修改账单失败");
        }
        return AjaxResponse.success(new ArrayList());
    }



    public enum EnumUpdateReason{
        ORDER_PROBLEM(0,"订单问题"),
        SUPPLEMENT_COST(1,"补录费用");
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
    
	/**
	 * @Title: confirm
	 * @Description: 结算单确认
	 * @param supplierBillId
	 * @return AjaxResponse
	 * @throws
	 */
	@RequestMapping(value = "/confirm")
	public AjaxResponse confirm(@NotBlank(message = "账单编号不能为空") String supplierBillId) {
		return busSettlementAdviceService.confirm(supplierBillId);
	}

    /**
     * @Title: invoiceInit
     * @Description: 结算单确认收票窗口查询
     * @return AjaxResponse
     * @throws
     */
	@RequestMapping(value = "/invoice/init")
	public AjaxResponse invoiceInit(@NotBlank(message = "账单编号不能为空") String supplierBillId) {
		BusSettlementInvoiceVO invoice = busSettlementAdviceService.queryInvoiceInfo(supplierBillId);
		return AjaxResponse.success(invoice);
	}
	
	/**
	 * @Title: invoiceSave
	 * @Description: 结算单确认收票窗口保存
	 * @param invoiceDTO
	 * @return AjaxResponse
	 * @throws
	 */
	@RequestMapping(value = "/invoice/save")
	public AjaxResponse invoiceSave(@Validated BusSettlementInvoiceDTO invoiceDTO) {
		return busSettlementAdviceService.saveInvoiceInfo(invoiceDTO);
	}
	
	/**
	 * @Title: paymentInit
	 * @Description: 结算单确认收款窗口查询
	 * @param supplierBillId
	 * @return AjaxResponse
	 * @throws
	 */
	@RequestMapping(value = "/payment/init")
	public AjaxResponse paymentInit(@NotBlank(message = "账单编号不能为空") String supplierBillId) {
		BusSettlementPaymentVO payment = busSettlementAdviceService.queryPaymentInfo(supplierBillId);
		return AjaxResponse.success(payment);
	}
	
	/**
	 * @Title: paymentSave
	 * @Description: 结算单确认收款窗口保存
	 * @param invoiceDTO
	 * @return AjaxResponse
	 * @throws
	 */
	@RequestMapping(value = "/payment/save")
	public AjaxResponse paymentSave(@Validated BusSettlementPaymentDTO paymentDTO) {
		return busSettlementAdviceService.savePaymentInfo(paymentDTO);
	}

}
