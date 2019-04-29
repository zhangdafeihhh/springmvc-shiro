package mapper.driver;

import com.zhuanche.entity.driver.SupplierCooperationAgreementUrl;

public interface SupplierCooperationAgreementUrlMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SupplierCooperationAgreementUrl record);

    int insertSelective(SupplierCooperationAgreementUrl record);

    SupplierCooperationAgreementUrl selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SupplierCooperationAgreementUrl record);

    int updateByPrimaryKey(SupplierCooperationAgreementUrl record);
}