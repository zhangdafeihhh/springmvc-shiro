package mapper.driver.ex;

import com.zhuanche.entity.driver.SupplierCooperationAgreement;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SupplierCooperationAgreementExMapper {

    /**
     * 根据供应商ID查询最新一条合作协议
     * @param supplierId
     * @return
     */
    SupplierCooperationAgreement selectLastAgreementBySupplierId(@Param("supplierId") Integer supplierId);


    List<SupplierCooperationAgreement> selectAllBySupplierId(@Param("supplierId") Integer supplierId);

    int deleteBySupplierId(@Param("supplierId") Integer supplierId);

}