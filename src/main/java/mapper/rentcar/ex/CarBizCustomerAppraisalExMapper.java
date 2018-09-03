package mapper.rentcar.ex;

import com.zhuanche.entity.rentcar.CarBizCustomerAppraisal;
import com.zhuanche.entity.rentcar.CarBizCustomerAppraisalParams;

import java.util.List;

public interface CarBizCustomerAppraisalExMapper {


    public List<CarBizCustomerAppraisal> queryForListObject(CarBizCustomerAppraisalParams params);

}