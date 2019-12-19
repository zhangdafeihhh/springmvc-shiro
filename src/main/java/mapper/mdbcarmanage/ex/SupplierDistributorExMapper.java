package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.SupplierDistributor;

import java.util.List;

public interface SupplierDistributorExMapper {

    int insertSelective(SupplierDistributor record);

    SupplierDistributor selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SupplierDistributor record);

    List<SupplierDistributor> distributorList(SupplierDistributor distributor);
}