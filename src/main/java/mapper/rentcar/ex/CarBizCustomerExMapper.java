package mapper.rentcar.ex;

import com.zhuanche.entity.rentcar.CarBizCustomer;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface CarBizCustomerExMapper {

	/**
	 * @Title: selectCustomerNameById
	 * @Description: 根据ID查询乘客名称 
	 * @param customerId
	 * @return String
	 * @throws
	 */
	String selectCustomerNameById(Integer customerId);

	List<CarBizCustomer> selectBatchCusName(@Param("customerIds") Set<Integer> customerIds);
	
}