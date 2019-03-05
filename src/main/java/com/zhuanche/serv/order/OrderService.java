package com.zhuanche.serv.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.rpc.HttpParamSignGenerator;
import com.zhuanche.common.rpc.RPCAPI;
import com.zhuanche.common.rpc.RPCResponse;
import com.zhuanche.constant.Constants;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.serv.order.elasticsearch.OrderSearchOrderBy;
import com.zhuanche.serv.order.elasticsearch.OrderSearchV1Response;
import com.zhuanche.util.Common;
import com.zhuanche.util.SignatureUtils;
import com.zhuanche.util.encrypt.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;

/**  调用订单的接口，集中在这个类中封装
 * 
 *   注意：
 *           1)订单组提供了两套接口：一个是order-service-api接口，另一个是order-api接口。这两个域名是不一样的！不要混淆！
 *           2)订单组还提供了ES搜索接口：order-search-api
 * **/
@Service
public class OrderService{
	private static final Logger log  = LoggerFactory.getLogger(OrderService.class);
	//----------------------------------以下是order-service-api接口配置
	@Value("${order.server.api.base.url}")
	private String ORDER_SERVICE_API_BASE_URL      = "http://inside-orderapi.01zhuanche.com";//方便于自测，直接初始化一下
	private String ORDER_SERVICE_API_BUSSINESSID = Common.BUSSINESSID;
	private String ORDER_SERVICE_API_SIGNKEY       = Common.MAIN_ORDER_KEY;
	public static final String ORDER_INFO_BY_COLUMNS = "/orderMain/getOrdersByOrderNo";
	//----------------------------------以下是order-api接口配置
	@Value("${car.rest.url}")
	private String ORDER_API_URL                           = "http://inside-order.01zhuanche.com";//方便于自测，直接初始化一下
	private String ORDER_API_BUSINESS_ID              = "5";//方便于自测，直接初始化一下
	private String ORDER_API_BUSINESS_SIGNKEY    = "cbf7b1141f964ddca8a8580d4594fed9";//方便于自测，直接初始化一下
	//----------------------------------以下是order-search-api接口配置
	@Value("${order.saas.es.url}")
	private String ORDER_SEARCH_API_V1                = "http://inside-order-search-api.01zhuanche.com";//方便于自测，直接初始化一下
	private String ORDER_SEARCH_API_V1_transId     = "transId";  //请求唯一标识的HTTP参数名
	
	/**生成请求唯一标识**/
	private static final String SEED_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final Random rnd = new  SecureRandom();
	private static String genTransId() {
		int length = 8;//生成8位随机字符
		StringBuffer sb = new StringBuffer( length  );
		for(int i=0;i<length;i++) {
			int index = rnd.nextInt( SEED_CHARS.length()  );
			char cha = SEED_CHARS.charAt(index);
			sb.append(cha);
		}
		return sb.toString()+"-SAAS";
	}
	
	/**调用order-search-api接口：通用的请求订单搜索ES服务的方法**/
	public OrderSearchV1Response startOrderSearchV1( Map<String,Object> httpParams , List<OrderSearchOrderBy> orderby) {
		if(httpParams==null) {
			httpParams = new HashMap<String,Object>();
		}
		if(httpParams.containsKey(ORDER_SEARCH_API_V1_transId)) {
			log.error("严重错误：请求参数中不能含有系统参数名称：" + ORDER_SEARCH_API_V1_transId);
			return null;
		}
		//开始请求
		String transId = OrderService.genTransId();
		httpParams.put( ORDER_SEARCH_API_V1_transId, transId  );
		//预处理一下排序参数
		if(orderby!=null && orderby.size()>0) {
			List<OrderSearchOrderBy> orderbynewer = new ArrayList<OrderSearchOrderBy>(orderby.size());
			for(OrderSearchOrderBy item:orderby) {
				if( StringUtils.isNotEmpty(item.getField()) && StringUtils.isNotEmpty(item.getOperator()) ) {
					orderbynewer.add(item);
				}
			}
			if(orderbynewer.size()>0) {
				String sort = JSON.toJSONString(orderbynewer);
				httpParams.put("sort", sort);
			}
		}
		String responseText = new RPCAPI().requestWithRetry(RPCAPI.HttpMethod.POST, ORDER_SEARCH_API_V1+"/order/v1/search", httpParams, null, "UTF-8");
		httpParams.remove(ORDER_SEARCH_API_V1_transId);//清理掉
		//接收响应，反序列化
		if(responseText==null) {
			return null;
		}
		OrderSearchV1Response orderSearchV1Response = JSON.parseObject(responseText, OrderSearchV1Response.class);
		return orderSearchV1Response;
	}
	
	/**调用order-service-api接口：查询司机是否有待服务订单**/
	public boolean driverHasServiceOrder(String driverId) {
		boolean flag = true;
		Map<String,Object> httpParams = new HashMap<String,Object>(16);
		httpParams.put("driverId", driverId ); 
		httpParams.put("limit",  1);
		httpParams.put("page",  1); 
		httpParams.put("bId", ORDER_SERVICE_API_BUSSINESSID );
		String sign = HttpParamSignGenerator.genSignForOrderAPI (httpParams, ORDER_SERVICE_API_SIGNKEY );
		httpParams.put("sign", sign);
		String body = new RPCAPI().requestWithRetry(RPCAPI.HttpMethod.POST, ORDER_SERVICE_API_BASE_URL+"/trip/driverHasServiceOrderOrNot", httpParams, null, "UTF-8");
		if(body==null ) {
			return flag;
		}
		RPCResponse orderResponse = RPCResponse.parse(body);
		if(orderResponse.getCode()!=0 || orderResponse.getData()==null) {
			return flag;
		}
		JSONObject order = (JSONObject)orderResponse.getData();
		flag = order.getBooleanValue("hasServiceOrder");
		return flag;
	}
	
	/**调用order-api接口：根据orderId或orderNo查询订单信息( 入参orderId和orderNo必传其一)**/
	public JSONObject getOrderInfo(String orderId, String orderNo ) {
		Map<String,Object> httpParams = new HashMap<String,Object>(4);
		if( StringUtils.isNotEmpty(orderId) ) {
			httpParams.put("orderId", orderId ); 
		}else if( StringUtils.isNotEmpty(orderNo) ) {
			httpParams.put("orderNo", orderNo ); 
		}else {
			return null;
		}
		
		httpParams.put("businessId", ORDER_API_BUSINESS_ID );
		String sign = HttpParamSignGenerator.genSignForOrderAPI (httpParams, ORDER_API_BUSINESS_SIGNKEY );
		httpParams.put("sign", sign);
		String orderInfo = new RPCAPI().requestWithRetry(RPCAPI.HttpMethod.POST, ORDER_API_URL.trim()+"/order/getOrder", httpParams, null, "UTF-8");
		if(orderInfo==null ) {
			log.info("查询订单失败。" );
			return null;
		}
		RPCResponse orderResponse = RPCResponse.parse(orderInfo);
		if(orderResponse.getCode()!=0 || orderResponse.getData()==null) {
			log.info( "订单不存在。" );
			return null;
		}
		JSONObject order = (JSONObject)orderResponse.getData();
		return order;
	}
	/**调用order-api接口：根据orderId查询订单取消原因信息**/
	public JSONObject getOrderCancelInfo(String orderId) {
		Map<String,Object> httpParams = new HashMap<String,Object>(4);
		httpParams.put("orderId", orderId ); 
		httpParams.put("businessId", ORDER_API_BUSINESS_ID );
		String sign = HttpParamSignGenerator.genSignForOrderAPI (httpParams, ORDER_API_BUSINESS_SIGNKEY );
		httpParams.put("sign", sign);
		String orderCancelInfo = new RPCAPI().requestWithRetry(RPCAPI.HttpMethod.POST, ORDER_API_URL.trim()+"/order/getOrderCancelInfo", httpParams, null, "UTF-8");
		if(orderCancelInfo==null ) {
			log.info("查询订单取消原因失败。" );
			return null;
		}
		RPCResponse orderResponse = RPCResponse.parse(orderCancelInfo);
		if(orderResponse.getCode()!=0 || orderResponse.getData()==null) {
			log.info( "订单取消原因不存在。" );
			return null;
		}
		JSONObject order = (JSONObject)orderResponse.getData();
		return order;
	}

	public JSONObject getOrderInfoByParams(String orderNo, String columns, String tag){
		Map<String, Object> params = new HashMap<>();
		String url = ORDER_SERVICE_API_BASE_URL + ORDER_INFO_BY_COLUMNS;
		params.put("orderNo", orderNo);
		params.put("bId", ORDER_SERVICE_API_BUSSINESSID);
		params.put("columns", columns);
		params.put("needHistory", 1);
		try{
			params.put("sign", MD5Utils.getMD5DigestBase64(SignatureUtils.getMD5Sign(params, ORDER_SERVICE_API_SIGNKEY)));
		}catch (Exception e){
			log.error("签名错误");
			return null;
		}
		JSONObject result = MpOkHttpUtil.okHttpGetBackJson(url, params, 1, tag);
		if (result != null && Constants.SUCCESS_CODE == result.getInteger(Constants.CODE)){
			return result;
		}
		return null;
	}
	
	

	//--------------------------------------------------------------------------------------for debug
	public static void main(String[] args) {
		OrderService orderService = new OrderService();
		
		//调试
		String orderNo = "P181009065517513255";
		Map<String,Object> httpParams = new HashMap<String,Object>(4);
		httpParams.put("orderNo", orderNo );
		
		//排序
		List<OrderSearchOrderBy> orderby = new ArrayList<OrderSearchOrderBy>();
		orderby.add(new OrderSearchOrderBy("orderId","asc") );
		
		orderService.startOrderSearchV1(httpParams , orderby );
		
		
//		B1533543657536056
//		P201808062897262575
//		BS201808069936313885
//		B1533543489594251
//		P1533543398706161
//		B1533543378951197
//		BS201808065394717391
//		B1533543352140503
//		B1533543344271715
//		P1533543300474335
//		Map<String,Object> httpParams = new HashMap<String,Object>();
//		httpParams.put("orderNo", "BS201808065394717391"); 
//		httpParams.put("username", "赵紫阳测试中文参数"); 
//		httpParams.put("businessId", "5");
//		String sign = HttpParamSignGenerator.genSignForOrderAPI (httpParams,"cbf7b1141f964ddca8a8580d4594fed9");
//		httpParams.put("sign", sign);
//		new RPCAPI().requestWithRetry(RPCAPI.HttpMethod.POST, "http://test-inside-order.01zhuanche.com/order/getOrder", httpParams, null, "UTF-8");
	}

}
