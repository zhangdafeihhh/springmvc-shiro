package mapper.rentcar;

import com.zhuanche.entity.rentcar.CarBizCustomerAppraisal;

public interface CarBizCustomerAppraisalMapper {
    int deleteByPrimaryKey(Integer appraisalId);

    int insert(CarBizCustomerAppraisal record);

    int insertSelective(CarBizCustomerAppraisal record);

    CarBizCustomerAppraisal selectByPrimaryKey(Integer appraisalId);

    int updateByPrimaryKeySelective(CarBizCustomerAppraisal record);

    int updateByPrimaryKey(CarBizCustomerAppraisal record);
}