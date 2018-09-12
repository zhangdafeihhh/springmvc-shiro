package com.zhuanche.serv.rentcar;

import java.util.List;
import java.util.Map;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.dto.rentcar.CarFactOrderInfoDTO;
import com.zhuanche.entity.DriverOrderRecord.OrderTimeEntity;
import com.zhuanche.entity.rentcar.CarBizOrderSettleEntity;
import com.zhuanche.entity.rentcar.CarBizOrderWaitingPeriod;
import com.zhuanche.entity.rentcar.CarBizPlanEntity;
import com.zhuanche.entity.rentcar.CarFactOrderInfo;
import com.zhuanche.entity.rentcar.CarGroupEntity;


public interface CarFactOrderInfoService {
	/**
     *  （老车管）输入订单号返回订单明细
     * @param orderNo 订单号
     * @return java.lang.String
     */
    public CarFactOrderInfo selectByPrimaryKey(Long orderId);
    /**
     *  （老车管）根据子订单号查询主订单
     * @param orderNo 订单号
     * @return java.lang.String
     */
    public String getMainOrderBySubOrderNo(String orderNo);
    /**
	 * 查询LBS提供的轨迹坐标
	 * @param paramsStr
	 * @return
	 */
	public String queryDrivingRouteData(String paramsStr);
	/**
	 * 查询计费提供的计费明细
	 * @param paramsStr
	 * @return
	 */
	public String queryCostDetailData(String paramsStr);
    /**
     * ?
     * @param orderId
     * @return
     */
    public CarBizOrderSettleEntity selectDriverSettleByOrderId(Long orderId);
    /**
     * 
     */
    public CarGroupEntity selectCarGroupById(Integer id);
   /**
    * ?
    * @param orderId
    * @return
    */
	public List<CarFactOrderInfo> selectByListPrimaryKey(Long orderId);
    /**
     * 
     */
    public CarBizPlanEntity selectByOrderNo(Map<String, Object> map);
    /**
     * 
     */
    public List<OrderTimeEntity> queryDriverOrderRecord(Map<String,String> p);
    /**
     * 
     */
    public List<CarBizOrderWaitingPeriod> selectWaitingPeriodListSlave(String orderNo);
    /**
     * 
     * @param paramsStr
     * @return
     */
    public AjaxResponse  queryOrderDataList(Map<String, Object> paramMap);
}
