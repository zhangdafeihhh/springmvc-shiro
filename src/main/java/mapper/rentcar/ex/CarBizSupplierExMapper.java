package mapper.rentcar.ex;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.zhuanche.dto.rentcar.CarBizSupplierDTO;
import com.zhuanche.entity.rentcar.CarBizSupplier;

public interface CarBizSupplierExMapper{
	/**查询一个城市的所有供应商**/
	List<CarBizSupplier> querySuppliers( @Param("cityIds") Set<Integer> cityIds , @Param("supplierIds") Set<Integer> supplierIds);

    CarBizSupplier queryForObject(CarBizSupplier carBizSupplier);

	/** 根据供应商名称查询供应商id **/
	Integer querySupplierBySupplierName( @Param("supplierName") String supplierName);

	List<CarBizSupplier> queryNamesByIds(@Param("supplierIds") Set<Integer> supplierIds);

	List<CarBizSupplier> findByIdSet(@Param("supplierIdSet") Set<Integer> supplierIdSet);
	/**根据ID查询供应商名称*/
	String getSupplierNameById(Integer supplierId);

	List<CarBizSupplierDTO> queryNameBySupplierIds(@Param("supplierIds")  String supplierIds );
	
	List<Map<String, Object>> getSupplierList(@Param("supplierIds") Set<String> supplierIds);
	/** 根据供应商ID查询其调度员电话 **/
	String queryDispatcherPhoneBySupplierId(@Param("supplierId") Integer supplierId);
}