package com.zhuanche.serv.rentcar;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.dto.rentcar.CarBizDriverInfoDTO;
import com.zhuanche.dto.rentcar.CarPoolMainOrderDTO;
import com.zhuanche.dto.rentcar.ServiceTypeDTO;
import com.zhuanche.entity.driverOrderRecord.OrderTimeEntity;
import com.zhuanche.entity.rentcar.*;

import java.util.List;
import java.util.Map;


public interface CarFactOrderInfoService {
    /**
     *  （老车管）根据子订单号查询主订单
     * @param orderNo 订单号
     * @return java.lang.String
     */
    public String getMainOrderBySubOrderNo(String orderNo);
    /**
     *  （老车管）根据主订单查询子订单信息
     * @param mainOrderNo 订单号
     * @return java.lang.String
     */
    public List<CarFactOrderInfo> getMainOrderByMainOrderNo(String mainOrderNo);
    /**
     *  （老车管）all订单信息
     * @param orderNo 订单号
     * @return java.lang.String
     */
//    public List<CarFactOrderInfoDTO>  queryAllOrderDataList(Map<String, Object> paramMap);
    /**
	 * 查询LBS提供的轨迹坐标
	 * @param paramMap
	 * @return
	 */
	public String queryDrivingRouteData(Map<String, Object> paramMap);
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
     * @param paramMap
     * @return
     */
    public AjaxResponse  queryOrderDataList(Map<String, Object> paramMap);
    /**
     * 根据拼车订单号，查询主订单信息
     * @param params
     * @return
     */
	public CarPoolMainOrderDTO queryCarpoolMainForObject(CarPoolMainOrderDTO params);
	
	/**
	 * 
	 * @param params
	 * @return
	 */
	public CarBizDriverInfoDTO querySupplierIdAndNameByDriverId(Integer params);
	/**
	 * 
	 */
	public CarBizSupplier queryCarBizSupplier(CarBizSupplier params);
	/**
	 * 
	 */
	public CarBizCity queryCarBizCityById(CarBizCity params);
    /**
     * 查询服务类型
     */
    public String serviceTypeName(Integer serviceId);
    /**
     * 
     */
    public String getGroupNameByGroupId(Integer groupId);
    /**
     * 
     */
    public String selectModelNameByLicensePlates(String licensePlates);
    /**
     * 订单服务类型
     */
    public List<ServiceTypeDTO> selectServiceEntityList(ServiceEntity serviceEntity);

//    Workbook exportExceleOrderList(List<CarFactOrderInfoDTO> list, String path) throws Exception;

}
