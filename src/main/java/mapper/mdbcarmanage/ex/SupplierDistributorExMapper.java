package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.SupplierDistributor;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface SupplierDistributorExMapper {

    int insertSelective(SupplierDistributor record);

    SupplierDistributor selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SupplierDistributor record);

    List<SupplierDistributor> distributorList(SupplierDistributor distributor);

    List<SupplierDistributor> getDistributorNames(@Param("ids") Set<Integer> ids);
}