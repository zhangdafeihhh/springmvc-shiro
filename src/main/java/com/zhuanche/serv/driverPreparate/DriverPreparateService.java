package com.zhuanche.serv.driverPreparate;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.entity.DriverPreparate.DriverPreparate;
import com.zhuanche.serv.order.OrderService;
import com.zhuanche.util.MyRestTemplate;

@Component("driverPreparateService")
public class DriverPreparateService {
	
	private static Log log =  LogFactory.getLog(DriverPreparateService.class);
	
	private static String GET_REPORT_LIST = "/api/report/getReportListByPage";
	private static String GET_REPORT_DETAIL = "/api/report/getReportDetail";

	private static String AID = "104";
	private static String AK = "jibR98Ml";
	
	@Autowired
	@Qualifier("driverPreparateTemplate")
	private MyRestTemplate driverPreparateTemplate;
	@Autowired
	private OrderService orderService;

	/**
     * @param orderNo
     * @param driverPhone
     * @param licensePlates
     * @param page
     * @param pagesize
     * @return
     */
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
	})
	public Map selectList(String orderNo, String driverPhone, String licensePlates,Integer page, Integer pagesize) {
		Map<String, Object> map = new HashMap<>();
		try {
			List<DriverPreparate> list = new ArrayList<>();
			TreeMap<String, String> paramMap =  new TreeMap<String, String>();
			String url = GET_REPORT_LIST + "?aId="+AID;
			// 参数：订单号 、业务线id
			paramMap.put("aId", AID);
			if(StringUtils.isNotBlank(driverPhone)) {
				paramMap.put("driverPhone", driverPhone);
				url += "&driverPhone="+driverPhone;
			}
			if(StringUtils.isNotBlank(licensePlates)) {
				paramMap.put("licensePlates", licensePlates);
				url += "&licensePlates="+licensePlates;
			}
			if(StringUtils.isNotBlank(orderNo)) {
				paramMap.put("orderNo", orderNo);
				url += "&orderNo="+orderNo;
			}
			if(page!=null) {
				paramMap.put("pageNo", String.valueOf(page));
				url += "&pageNo="+page;
			}
			if(pagesize!=null) {
				paramMap.put("pageSize", String.valueOf(pagesize));
				url += "&pageSize="+pagesize;
			}
			String sign = getMD5ToKefuApi(paramMap, AK);
			url += "&sign="+sign;

			JSONObject result = driverPreparateTemplate.getForObject(url,JSONObject.class);
			int code = result.getIntValue("code");
			String msg = result.getString("msg");
			if (code == 1) {
				log.info("查询司机报备列表出错,错误码:" + code + ",错误原因:" + msg);
				return map;
			}
			if (code == 0) {
				JSONObject data = result.getJSONObject("data");
				int total = data.getIntValue("total");
				JSONArray jsonArray = data.getJSONArray("list");
				if (jsonArray==null || jsonArray.isEmpty()) {
					return map;
				}
				for (Object object : jsonArray) {
					JSONObject jsonObject = (JSONObject) object;
					DriverPreparate t = JSONObject.toJavaObject(jsonObject, DriverPreparate.class);
					if(t!=null&&StringUtils.isNotEmpty(t.getOrderNo())) {
						JSONObject orderInfoJson = orderService.getOrderInfo(null, t.getOrderNo() );
						if(orderInfoJson!=null) {
							String orderId = ""+orderInfoJson.getIntValue("orderId");
							t.setOrderId(orderId);
						}
						list.add(t);
					}
				}
				map.put("list",list);
				map.put("total",total);
				return map;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 *  通过订单号查询司机报备详情
	 * @param orderNo
	 * @return com.zhuanche.entity.DriverPreparate.DriverPreparate
	 */
	public DriverPreparate selectDriverPreparateDetail(String orderNo){
		// 签名
		TreeMap<String, String> paramMap =  new TreeMap<String, String>();
		// 参数：订单号 、业务线id
		paramMap.put("aId", AID);
		paramMap.put("orderNo", orderNo);
		// 参数：订单号 、业务线id
		String sign = getMD5ToKefuApi(paramMap, AK);
		DriverPreparate entity = new DriverPreparate();
		String url = GET_REPORT_DETAIL+"?aId="+AID+"&sign="+sign+"&orderNo="+orderNo;
		try {
			JSONObject result = driverPreparateTemplate.getForObject(url,JSONObject.class);
			int code = result.getIntValue("code");
			String msg = result.getString("msg");
			if (code == 1) {
				log.info("查询司机报备详情出错,错误码:" + code + ",错误原因:" + msg);
				return null;
			}
			if (code == 0) {
				JSONObject jsonObject = result.getJSONObject("data");
				entity = JSONObject.toJavaObject(jsonObject, DriverPreparate.class);
			}
		} catch (Exception e) {
			log.info("selectList error:" + e);
		}
		return entity;
	}

	/**
	 * 获取字符串的md5值（推荐使用）<br>
	 * 来自stackoverflow的MD5计算方法，调用了MessageDigest库函数，并把byte数组结果转换成16进制<br>
	 *
	 * @param data
	 * @param ak
	 * @return
	 */
	public static String getMD5ToKefuApi(TreeMap<String, String> data, String ak) {
		try {
			StringBuilder buffer = new StringBuilder();
			for (String k : data.keySet()) {
				if (data.get(k) != null) {
					String v = data.get(k).toString();
					if (StringUtils.isNotBlank(v)) {
						buffer.append(k + "=" + v + "&");
					}
				}
			}
			buffer.append("ak=" + ak);
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] array = md.digest(buffer.toString().getBytes());
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
}
  