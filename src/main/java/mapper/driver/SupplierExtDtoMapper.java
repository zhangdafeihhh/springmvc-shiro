package mapper.driver;

import com.zhuanche.entity.driver.SupplierExtDto;

public interface SupplierExtDtoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SupplierExtDto record);

    int insertSelective(SupplierExtDto record);

    SupplierExtDto selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SupplierExtDto record);

    int updateByPrimaryKey(SupplierExtDto record);
}