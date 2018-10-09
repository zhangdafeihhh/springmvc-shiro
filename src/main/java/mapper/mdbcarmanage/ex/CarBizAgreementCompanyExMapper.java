package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.CarBizAgreementCompany;
import org.apache.ibatis.annotations.Param;

public interface CarBizAgreementCompanyExMapper {

    CarBizAgreementCompany selectByName(@Param("name") String name);

}