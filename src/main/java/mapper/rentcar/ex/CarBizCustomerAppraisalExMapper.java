package mapper.rentcar.ex;

import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalDTO;
import com.zhuanche.entity.rentcar.CarBizCustomerAppraisal;
import com.zhuanche.entity.rentcar.CarBizCustomerAppraisalParams;

import java.util.List;

public interface CarBizCustomerAppraisalExMapper {

    List<CarBizCustomerAppraisalDTO> queryCustomerAppraisalList(Integer appraisalId);

    public List<CarBizCustomerAppraisal> queryForListObject(CarBizCustomerAppraisalParams params);

}