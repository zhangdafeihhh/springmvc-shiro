package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.SupplierFeeRecord;

public interface SupplierFeeRecordMapper {
    int insert(SupplierFeeRecord record);

    int insertSelective(SupplierFeeRecord record);

    SupplierFeeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SupplierFeeRecord record);

    int updateByPrimaryKey(SupplierFeeRecord record);
}