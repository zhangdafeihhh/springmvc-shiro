package mapper.driver;

import com.zhuanche.entity.driver.SupplierExt;

public interface SupplierExtMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SupplierExt record);

    int insertSelective(SupplierExt record);

    SupplierExt selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SupplierExt record);

    int updateByPrimaryKey(SupplierExt record);
}