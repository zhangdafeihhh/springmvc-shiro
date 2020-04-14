package mapper.driver;

import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalDTO;
import com.zhuanche.entity.driver.CustomerAppraisal;
import com.zhuanche.entity.rentcar.CarBizCustomerAppraisal;
import com.zhuanche.entity.rentcar.CarBizCustomerAppraisalParams;

import java.util.List;

public interface CustomerAppraisalMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CustomerAppraisal record);

    int insertSelective(CustomerAppraisal record);

    CustomerAppraisal selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CustomerAppraisal record);

    int updateByPrimaryKey(CustomerAppraisal record);

    List<CustomerAppraisal> queryCustomerAppraisalList(CarBizCustomerAppraisalDTO dto);

    List<CarBizCustomerAppraisalDTO> queryDriverAppraisalDetail(CarBizCustomerAppraisalDTO carBizCustomerAppraisalDTO);

    List<CarBizCustomerAppraisal> queryForListObject(CarBizCustomerAppraisalParams params);
}