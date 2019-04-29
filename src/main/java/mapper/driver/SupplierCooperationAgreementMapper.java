package mapper.driver;

import com.zhuanche.entity.driver.SupplierCooperationAgreement;

public interface SupplierCooperationAgreementMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SupplierCooperationAgreement record);

    int insertSelective(SupplierCooperationAgreement record);

    SupplierCooperationAgreement selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SupplierCooperationAgreement record);

    int updateByPrimaryKey(SupplierCooperationAgreement record);
}