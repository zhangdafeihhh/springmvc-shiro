package mapper.rentcar.ex;

import com.zhuanche.entity.rentcar.CarBizCustomerAppraisal;

import java.util.List;

public interface BusCarBizCustomerAppraisalExMapper {

	/**
	 * @Title: queryAppraisal
	 * @Description: 查询订单评分
	 * @return 
	 * @return CarBizCustomerAppraisal
	 * @throws
	 */
	CarBizCustomerAppraisal queryAppraisal(String orderNo);

	List<CarBizCustomerAppraisal>queryBatchAppraisal(List<String> orderNos);

}