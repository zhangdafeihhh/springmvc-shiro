package mapper.rentcar.ex;

import java.util.List;
import java.util.Map;

import com.zhuanche.dto.rentcar.CarPoolMainOrderDTO;
import com.zhuanche.dto.rentcar.ServiceTypeDTO;
import com.zhuanche.entity.rentcar.CarBizOrderSettleEntity;
import com.zhuanche.entity.rentcar.CarBizOrderWaitingPeriod;
import com.zhuanche.entity.rentcar.CarBizPlanEntity;
import com.zhuanche.entity.rentcar.CarFactOrderInfo;
import com.zhuanche.entity.rentcar.CarGroupEntity;
import com.zhuanche.entity.rentcar.ServiceEntity;

public interface CarFactOrderExMapper {

    /**
     *  输入订单号返回订单ID
     * @param orderNo 订单号
     * @return java.lang.String
     */
    public String selectorderIdByOrderNo(String orderNo);
    
    
    /**
     *  （老车管）输入订单号返回订单明细
     * @param orderNo 订单号
     * @return java.lang.String
     */
    public CarFactOrderInfo selectByPrimaryKey(Long orderId);
    /**
     * 
     * @param orderId
     * @return
     */
    public CarBizOrderSettleEntity selectDriverSettleByOrderId(Long orderId);
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
     * 
     */
    public CarPoolMainOrderDTO queryCarpoolMainForObject(CarPoolMainOrderDTO params);
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
    
}