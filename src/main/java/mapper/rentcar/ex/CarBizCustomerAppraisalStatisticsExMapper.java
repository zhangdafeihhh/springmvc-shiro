package mapper.rentcar.ex;

import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalStatisticsDTO;

import java.util.List;

public interface CarBizCustomerAppraisalStatisticsExMapper {

    List<CarBizCustomerAppraisalStatisticsDTO> queryCustomerAppraisalStatisticsList(CarBizCustomerAppraisalStatisticsDTO carBizCustomerAppraisalStatisticsDTO);

}