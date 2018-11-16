package mapper.rentcar;

import com.zhuanche.entity.rentcar.CarBizSupplier;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface CarBizSupplierMapper {
    int deleteByPrimaryKey(Integer supplierId);

    int insert(CarBizSupplier record);

    int insertSelective(CarBizSupplier record);

    CarBizSupplier selectByPrimaryKey(Integer supplierId);

    int updateByPrimaryKeySelective(CarBizSupplier record);

    int updateByPrimaryKeyWithBLOBs(CarBizSupplier record);

    int updateByPrimaryKey(CarBizSupplier record);

    List<CarBizSupplier> findByIdSet(@Param("supplierIdSet") Set<Integer> supplierIdSet);
}