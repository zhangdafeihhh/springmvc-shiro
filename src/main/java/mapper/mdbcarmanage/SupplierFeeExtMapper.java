package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.SupplierFeeExt;

public interface SupplierFeeExtMapper {
    int insert(SupplierFeeExt record);

    int insertSelective(SupplierFeeExt record);

    SupplierFeeExt selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SupplierFeeExt record);

    int updateByPrimaryKey(SupplierFeeExt record);
}