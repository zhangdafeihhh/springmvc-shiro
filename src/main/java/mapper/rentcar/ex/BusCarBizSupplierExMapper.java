package mapper.rentcar.ex;

import java.util.List;
import java.util.Map;

import com.zhuanche.entity.rentcar.CarBizSupplier;

public interface BusCarBizSupplierExMapper {

	/**
	 * @Title: querySuppliers
	 * @Description: 查询供应商
	 * @param param
	 * @return 
	 * @return List<CarBizSupplier>
	 * @throws
	 */
	List<CarBizSupplier> querySuppliers(Map<String, Object> param);
	
}