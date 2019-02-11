package mapper.rentcar.ex;

public interface CarBizCustomerExMapper {

	/**
	 * @Title: selectCustomerNameById
	 * @Description: 根据ID查询乘客名称 
	 * @param customerId
	 * @return String
	 * @throws
	 */
	String selectCustomerNameById(Integer customerId);
	
}