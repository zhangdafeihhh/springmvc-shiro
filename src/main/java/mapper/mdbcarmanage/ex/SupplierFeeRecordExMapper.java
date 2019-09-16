package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.SupplierFeeRecord;

import java.util.List;

public interface SupplierFeeRecordExMapper {
    int insertFeeRecord(SupplierFeeRecord record);

    List<SupplierFeeRecord> listRecord(String feeOrderNo);

    int insertSelective(SupplierFeeRecord record);

    SupplierFeeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SupplierFeeRecord record);

    int updateByPrimaryKey(SupplierFeeRecord record);
}