package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.SupplierFeeRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SupplierFeeRecordExMapper {

    int insertFeeRecord(SupplierFeeRecord record);

    List<SupplierFeeRecord> listRecord(@Param("feeOrderNo") String feeOrderNo);

}