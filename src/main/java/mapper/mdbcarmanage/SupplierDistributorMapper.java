package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.SupplierDistributor;

public interface SupplierDistributorMapper {
    int insert(SupplierDistributor record);

    int insertSelective(SupplierDistributor record);

    SupplierDistributor selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SupplierDistributor record);

    int updateByPrimaryKey(SupplierDistributor record);
}