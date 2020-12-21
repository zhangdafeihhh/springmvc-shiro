package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.SupplierFeeExt;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SupplierFeeExtExMapper {

    List<SupplierFeeExt> queryBySupplierFeeId(@Param("supplierFeeId") Integer supplierFeeId);


}