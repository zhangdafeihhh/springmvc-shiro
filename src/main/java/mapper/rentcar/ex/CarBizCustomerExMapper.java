package mapper.rentcar.ex;

import com.zhuanche.entity.rentcar.CarBizCustomer;

import java.util.List;

public interface CarBizCustomerExMapper {

	/**
	 * @Title: selectCustomerNameById
	 * @Description: 根据ID查询乘客名称 
	 * @param cancelCreateBy
	 * @return String
	 * @throws
	 */
	String selectCustomerNameById(Integer customerId);

	List<CarBizCustomer> selectBatchCusName(List<Integer> ids);
	
}