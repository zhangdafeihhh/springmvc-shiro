package mapper.rentcar.ex;

import java.util.List;
import java.util.Map;

public interface BusCarBizSupplierExMapper {

	/**
	 * @Title: querySuppliers
	 * @Description: 查询供应商
	 * @param param
	 * @return 
	 * @return List<CarBizSupplier>
	 * @throws
	 */
	List<Map<Object, Object>> querySuppliers(Map<String, Object> param);
	
}