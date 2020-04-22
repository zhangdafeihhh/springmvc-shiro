package mapper.rentcar.ex;

import java.util.List;
import java.util.Map;

import com.zhuanche.dto.rentcar.ServiceTypeDTO;
import com.zhuanche.entity.rentcar.CarBizOrderSettleEntity;
import com.zhuanche.entity.rentcar.CarBizOrderWaitingPeriod;
import com.zhuanche.entity.rentcar.CarBizPlanEntity;
import com.zhuanche.entity.rentcar.CarFactOrderInfo;
import com.zhuanche.entity.rentcar.CarGroupEntity;
import com.zhuanche.entity.rentcar.ServiceEntity;

public interface CarFactOrderExMapper {
    /**
     * 
     * @param orderId
     * @return
     */
//    public CarBizOrderSettleEntity selectDriverSettleByOrderId(Long orderId);
    /**
     * 
     */
    public CarGroupEntity selectCarGroupById(Integer id);
    /**
     * 
     */
    public List<CarFactOrderInfo> selectByListPrimaryKey(Long orderId);
    /**
     * 
     */
    public CarBizPlanEntity selectByOrderNo(Map<String, Object> map);
    /**
     * 
     */
    public List<CarBizOrderWaitingPeriod> selectWaitingPeriodListSlave(String orderNo);
    /**
     * 查询车行类别
     */
    public ServiceEntity selectServiceEntityById(Integer serviceId);
    /**
     * 查询车型
     */
    public String selectModelNameByLicensePlates(String licensePlates);
    /**
     * 订单服务类型
     */
    public List<ServiceTypeDTO> selectServiceEntityList(ServiceEntity serviceEntity);

	//查询order_cost_detail
//	public CarFactOrderInfo selectOrderCostDetailByOrderId(Long orderId);
	//查询car_biz_order_ cost_detail_extension
//	public CarFactOrderInfo selectOrderCostExtension(Long orderId);
	//查询PaymentCustomer
	public Double selectPaymentCustomer( String orderNo);
	//查询PaymentDriver
	public Double selectPaymentDriver( String orderNo);
	//查询car_biz_partner_pay_detail
	public Double selectPartnerPayAmount( String orderNo);
	//查询dissent
	public CarFactOrderInfo selectDissent(Long orderId);
	//查询car_biz_order_ settle_detail_extension
//	public CarFactOrderInfo selectOrderSettleDetail(Long orderId);
}