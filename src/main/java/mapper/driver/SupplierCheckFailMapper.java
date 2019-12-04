package mapper.driver;

import com.zhuanche.entity.driver.SupplierCheckFail;

public interface SupplierCheckFailMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SupplierCheckFail record);

    int insertSelective(SupplierCheckFail record);

    SupplierCheckFail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SupplierCheckFail record);

    int updateByPrimaryKey(SupplierCheckFail record);
}