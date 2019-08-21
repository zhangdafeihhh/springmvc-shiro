package mapper.driver;

import com.zhuanche.entity.driver.SupplierLevelAdditional;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierLevelAdditionalMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SupplierLevelAdditional record);

    int insertSelective(SupplierLevelAdditional record);

    SupplierLevelAdditional selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SupplierLevelAdditional record);

    int updateByPrimaryKey(SupplierLevelAdditional record);

    SupplierLevelAdditional findBySupplierLevelIdAndSupplierLevelAdditionalName(Integer supplierLevelId, String supplierLevelAdditionalName);
}