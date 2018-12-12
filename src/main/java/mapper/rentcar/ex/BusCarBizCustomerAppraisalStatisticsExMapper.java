package mapper.rentcar.ex;

import java.util.Map;

import com.zhuanche.entity.rentcar.CarBizCustomerAppraisalStatistics;

public interface BusCarBizCustomerAppraisalStatisticsExMapper {

	/**
	 * @Title: queryAppraisal
	 * @Description: 获取司机某月评分
	 * @param param
	 * @return 
	 * @return CarBizCustomerAppraisalStatistics
	 * @throws
	 */
	CarBizCustomerAppraisalStatistics queryAppraisal(Map<Object, Object> param);

}