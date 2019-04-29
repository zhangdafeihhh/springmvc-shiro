package mapper.driver.ex;

import com.zhuanche.entity.driver.SupplierCooperationAgreementUrl;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SupplierCooperationAgreementUrlExMapper {

    /**
     * 根据合作协议ID查询附件
     * @param agreementId
     * @return
     */
    List<SupplierCooperationAgreementUrl> selectUrlByAgreementId(@Param("agreementId") Long agreementId);


    int deleteBySupplierId(@Param("supplierId") Integer supplierId);

}