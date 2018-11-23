package mapper.rentcar.ex;

import com.zhuanche.dto.rentcar.CarBizSupplierDTO;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CarBizSupplierExMapper{
	/**查询一个城市的所有供应商**/
	List<CarBizSupplier> querySuppliers( @Param("cityIds") Set<Integer> cityIds , @Param("supplierIds") Set<Integer> supplierIds);

    CarBizSupplier queryForObject(CarBizSupplier carBizSupplier);

	/** 根据供应商名称查询供应商id **/
	Integer querySupplierBySupplierName( @Param("supplierName") String supplierName);

	List<CarBizSupplier> queryNamesByIds(@Param("supplierIds") Set<Integer> supplierIds);

	List<CarBizSupplier> findByIdSet(@Param("supplierIdSet") Set<Integer> supplierIdSet);

	List<CarBizSupplierDTO> queryNameBySupplierIds(@Param("supplierIds")  String supplierIds );
}