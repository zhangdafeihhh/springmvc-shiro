package mapper.rentcar;

import com.zhuanche.entity.rentcar.CarBizCustomer;

public interface CarBizCustomerMapper {
    int deleteByPrimaryKey(Integer customerId);

    int insert(CarBizCustomer record);

    int insertSelective(CarBizCustomer record);

    CarBizCustomer selectByPrimaryKey(Integer customerId);

    int updateByPrimaryKeySelective(CarBizCustomer record);

    int updateByPrimaryKeyWithBLOBs(CarBizCustomer record);

    int updateByPrimaryKey(CarBizCustomer record);
}