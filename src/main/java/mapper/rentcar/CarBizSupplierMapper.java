package mapper.rentcar;

import com.zhuanche.entity.rentcar.CarBizSupplier;

public interface CarBizSupplierMapper {
    int deleteByPrimaryKey(Integer supplierId);

    int insert(CarBizSupplier record);

    int insertSelective(CarBizSupplier record);

    CarBizSupplier selectByPrimaryKey(Integer supplierId);

    int updateByPrimaryKeySelective(CarBizSupplier record);

    int updateByPrimaryKeyWithBLOBs(CarBizSupplier record);

    int updateByPrimaryKey(CarBizSupplier record);
}