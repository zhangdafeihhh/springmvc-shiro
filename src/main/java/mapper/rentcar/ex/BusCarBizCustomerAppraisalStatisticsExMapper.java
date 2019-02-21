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

	/**
	 * 查询某个司机的平均值
	 * @param driverId
	 * @return
	 */
	Double queryAvgAppraisal (Integer driverId);


}