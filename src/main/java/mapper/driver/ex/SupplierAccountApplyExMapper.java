package mapper.driver.ex;

import com.zhuanche.entity.driver.SupplierAccountApply;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface SupplierAccountApplyExMapper {

    List<SupplierAccountApply> selectApplyAllBySupplierId(@Param("supplierId") Integer supplierId);

    SupplierAccountApply selectApplyBySupplierId(@Param("supplierId") Integer supplierId);

    List<SupplierAccountApply> queryApplyList(@Param("cityId") Integer cityId, @Param("supplierId") Integer supplierId,
                                              @Param("status") Integer status, @Param("cityIds") Set<Integer> cityIds,
                                              @Param("supplierIds") Set<Integer> supplierIds);

    SupplierAccountApply selectApplyStatusBySupplierId(@Param("supplierId") Integer supplierId);

    int updateByPrimaryKey(SupplierAccountApply record);

    int updateBySupplier(SupplierAccountApply record);

    int insert(SupplierAccountApply record);

}