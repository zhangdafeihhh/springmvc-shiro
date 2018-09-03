package mapper.rentcar.ex;

import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalDTO;

import java.util.List;

public interface CarBizCustomerAppraisalExMapper {

    List<CarBizCustomerAppraisalDTO> queryCustomerAppraisalList(Integer appraisalId);

}