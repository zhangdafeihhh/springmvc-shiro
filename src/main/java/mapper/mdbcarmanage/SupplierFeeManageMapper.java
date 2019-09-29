package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.SupplierFeeManage;

public interface SupplierFeeManageMapper {
    int insert(SupplierFeeManage record);

    int insertSelective(SupplierFeeManage record);

    SupplierFeeManage selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SupplierFeeManage record);

    int updateByPrimaryKey(SupplierFeeManage record);
}