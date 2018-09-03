package com.zhuanche.serv.statisticalAnalysis.impl;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.github.pagehelper.util.StringUtil;
import com.zhuanche.dto.rentcar.CompleteOrderDTO;
import com.zhuanche.serv.statisticalAnalysis.CompleteOrderService;
import com.zhuanche.shiro.session.WebSessionUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CompleteOrderServiceImpl implements CompleteOrderService {
	private static final Logger logger = LoggerFactory.getLogger(CompleteOrderServiceImpl.class);
	 
	/**
	 * 待修改 大数据URL
	 */
    @Autowired
    @Qualifier("saasBigdataApiTemplate")
    private MyRestTemplate saasBigdataApiTemplate;
    
	@Override
	public Map<String,Object> queryForPageObject(Map<String, Object> paramMap) {
		Map<String,Object> resutlMap = new HashMap<String,Object>();
		List<CompleteOrderDTO> list = new ArrayList<CompleteOrderDTO>();
		paramMap.put("removeBy", WebSessionUtil.getCurrentLoginUser().getId());
		paramMap.put("removeName", WebSessionUtil.getCurrentLoginUser().getLoginName());
        String url = "/CompleteOrderDTODetail/queryList";
        //String reqUrl = crowdDataUrl+API_CROWD_COUPON + "?mobile=" + mobile + "&crowdIds=" + crowdIds;
        // 调接口大数据接口
        String jsonResultStr = executeMassDataSystemService(url,paramMap);
    	if(!StringUtil.isEmpty(jsonResultStr)){
    		Map map = JsonUtil.jsonToMap(jsonResultStr);
			JSONArray jsonRecordList = JSONArray.fromObject(map.get("recordList"));  
        	list = (List<CompleteOrderDTO>)JSONArray.toCollection(jsonRecordList, CompleteOrderDTO.class);  
        	Integer total = (Integer)map.get("total");
        	resutlMap.put("list", list);
        	resutlMap.put("total", total);
        	return resutlMap;
    	}
        return resutlMap;
	}

	/**
	 * 调用大数据API
	 * 
	 * @param url
	 *            请求地址
	 * @param paramMap
	 *            请求参数
	 * @return
	 */
	private String executeMassDataSystemService(String url, Map<String, Object> paramMap) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = saasBigdataApiTemplate.postForObject(url,JSONObject.class, paramMap);
			if (result!=null) {
				Integer code = (Integer)result.get("code");
				if(code == 0){
					return String.valueOf(result.get("result"));
				}else if(code == -1){
					String message  = String.valueOf(result.get("message"));
					logger.error("请求大数据接口异常，url:{" + url + "},message:{"+message+"},paramMap:{" + paramMap
							+ "}");
				}else{
					logger.error("请求大数据接口异常,url:{" + url+ "},paramMap:{" + paramMap + "}");
				}
			}
			return null;
		} catch (Exception e) {
			logger.error("请求大数据接口异常，e:{" + e.getMessage() + "},url:{" + url
					+ "},paramMap:{" + paramMap + "}");
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	@Override
	public Workbook exportExcelCompleteOrder(List<CompleteOrderDTO> list, String path) throws Exception {
		FileInputStream io = new FileInputStream(path);
        // 创建 excel
        Workbook wb = new XSSFWorkbook(io);
        if (list != null && list.size() > 0) {
            Sheet sheet = null;
            try {
                sheet = wb.getSheetAt(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Cell cell = null;
            int i = 0;
            for (CompleteOrderDTO s : list) {
                Row row = sheet.createRow(i + 1);
                // 订单号
                cell = row.createCell(0);
                cell.setCellValue(s.getOrderNo() != null ? ""
                        + s.getOrderNo() + "" : "");
                // 下单城市
                cell = row.createCell(1);
                cell.setCellValue(s.getOrderCityName() != null ? ""
                        + s.getOrderCityName() + "" : "");
                // 订单完成时间
                cell = row.createCell(2);
                cell.setCellValue(s.getCompleteTime() != null ? ""
                        + s.getCompleteTime() + "" : "");
                // 产品类型
                cell = row.createCell(3);
                cell.setCellValue(s.getProductTypeName() != null ? ""
                        + s.getProductTypeName() + "" : "");
                // 预定车型
                cell = row.createCell(4);
                cell.setCellValue(s.getOrderVehicleTypeName() != null ? ""
                        + s.getOrderVehicleTypeName() + "" : "");
                // 总流水
                cell = row.createCell(5);
                cell.setCellValue(s.getTotalPrice() != null ? ""
                        + s.getTotalPrice() + "" : "");

                // 折扣后金额
                cell = row.createCell(6);
                cell.setCellValue(s.getPriceAfterDiscount() != null ? ""
                        + s.getPriceAfterDiscount() + "" : "");
                // 司机端金额
                cell = row.createCell(7);
                cell.setCellValue(s.getPriceForDriver() != null ? ""
                        + s.getPriceForDriver() + "" : "");
                // 优惠金额
                cell = row.createCell(8);
                cell.setCellValue(s.getDiscountAmount() != null ? ""
                        + s.getDiscountAmount() + "" : "");
                // 优惠抵扣
                cell = row.createCell(9);
                cell.setCellValue(s.getCouponDiscount() != null ? ""
                        + s.getCouponDiscount() + "" : "");
                // 退现结算
                cell = row.createCell(10);
                cell.setCellValue(s.getReturnCash() != null ? ""
                        + s.getReturnCash() + "" : "");
                // 价外费用
                cell = row.createCell(11);
                cell.setCellValue(s.getSpecialFee() != null ? ""
                        + s.getSpecialFee() + "" : "");
                // 订单类别
                cell = row.createCell(12);
                cell.setCellValue(s.getOrderTypeName() != null ? ""
                        + s.getOrderTypeName() + "" : "");
                // 订车人ID
                cell = row.createCell(13);
                cell.setCellValue(s.getCustomerId() != null ? ""
                        + s.getCustomerId() + "" : "");
                // 创建时间
                cell = row.createCell(14);
                cell.setCellValue(s.getCreateTime() != null ? ""
                        + s.getCreateTime() + "" : "");
                // 是否代人叫车
                cell = row.createCell(15);
                cell.setCellValue(s.getIsReplace() != null ? ""
                        + s.getIsReplace() + "" : "");
                // 司机ID
                cell = row.createCell(16);
                cell.setCellValue(s.getDriverId() != null ? ""
                        + s.getDriverId() + "" : "");
                // 司机名称
                cell = row.createCell(17);
                cell.setCellValue(s.getDriverName() != null ? ""
                        + s.getDriverId() + "" : "");
                i++;
            }
        }
        return wb;
	}

	
/*	public static void main(String[] args) {
		//String json="{'code':'0','message':'成功','result':{'total':'38291','pageNo':'22','recordList':[]}}";
		 String json="{'code':'0','message':'成功','result':{'total':'38291','pageNo':'22','recordList':[{'orderNo':'B67868957565','orderCityName':'北京','completeTime':'2018-09-01 15:00:01','productTypeName':'即时用车','orderVehicleTypeName':'豪华型','totalPrice':'100.0','priceAfterDiscount':'100.0','priceForDriver':'100.0','discountAmount':'0','couponDiscount':'0','returnCash':'0','specialFee':'0','orderTypeName':'个人','customerId':'123459','createTime':'2018-09-01 14:55:22','isReplace':'否','driverId':'18264','driverName':'八两金','vehiclePlateNo':'京B2902','serviceVehicleTypeName':'畅享型','allianceName':'首汽租赁(杭州)','beforeChargeMiles':'0','loadedMiles':'12.8','chargeMiles':'12.8','beforeChargeDuration':'3.6','loadedDuration':'9','afterChargeDuration':'0','actualAbordLocation':'银河soho','actualDebusLocation':'银河soho','orgnizationName':'测试机构','hotelName':'速八','channelName':'AppStore','waitingMinutes':'3'},{'orderNo':'B67868957565','orderCityName':'北京','completeTime':'2018-09-01 15:00:01','productTypeName':'即时用车','orderVehicleTypeName':'豪华型','totalPrice':'100.0','priceAfterDiscount':'100.0','priceForDriver':'100.0','discountAmount':'0','couponDiscount':'0','returnCash':'0','specialFee':'0','orderTypeName':'个人','customerId':'123459','createTime':'2018-09-01 14:55:22','isReplace':'否','driverId':'18264','driverName':'八两金','vehiclePlateNo':'京B2902','serviceVehicleTypeName':'畅享型','allianceName':'首汽租赁(杭州)','beforeChargeMiles':'0','loadedMiles':'12.8','chargeMiles':'12.8','beforeChargeDuration':'3.6','loadedDuration':'9','afterChargeDuration':'0','actualAbordLocation':'银河soho','actualDebusLocation':'银河soho','orgnizationName':'测试机构','hotelName':'速八','channelName':'AppStore','waitingMinutes':'3'}]}}";  
		 Map map = JsonUtil.jsonToMap(json);
		 System.out.println(map.get("code"));  
		 Map mapresult = JsonUtil.jsonToMap(map.get("result"));
		 System.out.println(mapresult.get("total")); 
		 
		 JSONArray jsonarray = JSONArray.fromObject(mapresult.get("recordList"));  
		 List<CompleteOrderDTODetails> list = (List<CompleteOrderDTODetails>)JSONArray.toCollection(jsonarray, CompleteOrderDTODetails.class);  
		 System.out.println(list.size()); 
		 //JSONArray jsonarray = JSONArray.fromObject(json);  
		 Iterator it = list.iterator();  
	        while(it.hasNext()){  
	        	CompleteOrderDTODetails p = (CompleteOrderDTODetails)it.next();  
	            System.out.println(p.getActualAbordLocation());  
	        }    	
	}*/
}
