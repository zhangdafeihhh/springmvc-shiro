package mapper.rentcar;

import com.zhuanche.entity.rentcar.CarBizCustomerAppraisalStatistics;

public interface CarBizCustomerAppraisalStatisticsMapper {
    int deleteByPrimaryKey(Integer appraisalStatisticsId);

    int insert(CarBizCustomerAppraisalStatistics record);

    int insertSelective(CarBizCustomerAppraisalStatistics record);

    CarBizCustomerAppraisalStatistics selectByPrimaryKey(Integer appraisalStatisticsId);

    int updateByPrimaryKeySelective(CarBizCustomerAppraisalStatistics record);

    int updateByPrimaryKey(CarBizCustomerAppraisalStatistics record);
}