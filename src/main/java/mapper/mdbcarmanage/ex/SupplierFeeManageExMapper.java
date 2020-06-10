package mapper.mdbcarmanage.ex;

import com.zhuanche.dto.mdbcarmanage.SupplierFeeManageDto;
import com.zhuanche.entity.mdbcarmanage.SupplierFeeManage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SupplierFeeManageExMapper {

    List<SupplierFeeManage> feeManageList(SupplierFeeManageDto supplierFeeManageDto);


    SupplierFeeManage feeOrderDetail(String feeOrderNo);

    int updateStatusByFeeOrderNo(@Param("feeOrderNo") String feeOrderNo,@Param("amountStatus") int amountStatus);

    int updateStatusAndAmount(@Param("feeOrderNo") String feeOrderNo,@Param("amountStatus") int amountStatus,@Param("status")int status);

    int updateStatus(@Param("feeOrderNo") String feeOrderNo,
                     @Param("status")Integer status);
}