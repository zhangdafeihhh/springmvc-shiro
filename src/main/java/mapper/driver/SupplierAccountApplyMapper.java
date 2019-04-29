package mapper.driver;

import com.zhuanche.entity.driver.SupplierAccountApply;

public interface SupplierAccountApplyMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SupplierAccountApply record);

    int insertSelective(SupplierAccountApply record);

    SupplierAccountApply selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SupplierAccountApply record);

    int updateByPrimaryKey(SupplierAccountApply record);
}