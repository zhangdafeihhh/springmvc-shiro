package mapper.mdbcarmanage.ex;

import com.zhuanche.dto.mdbcarmanage.SupplierFeeManageDto;
import com.zhuanche.entity.mdbcarmanage.SupplierFeeManage;

import java.util.List;

public interface SupplierFeeManageExMapper {

    List<SupplierFeeManage> feeManageList(SupplierFeeManageDto supplierFeeManageDto);


    SupplierFeeManage feeOrderDetail(String feeOrderNo);
}