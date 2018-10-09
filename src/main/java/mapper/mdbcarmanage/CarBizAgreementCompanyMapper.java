package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.CarBizAgreementCompany;

public interface CarBizAgreementCompanyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CarBizAgreementCompany record);

    int insertSelective(CarBizAgreementCompany record);

    CarBizAgreementCompany selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CarBizAgreementCompany record);

    int updateByPrimaryKey(CarBizAgreementCompany record);
}