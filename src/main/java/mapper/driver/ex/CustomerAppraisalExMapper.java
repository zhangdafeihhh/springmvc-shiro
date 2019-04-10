package mapper.driver.ex;

import com.zhuanche.entity.driver.MpCarBizCustomerAppraisal;
import com.zhuanche.entity.driver.MpCustomerAppraisalParams;

import java.util.List;

public interface CustomerAppraisalExMapper {

    List<MpCarBizCustomerAppraisal> queryForListObject(MpCustomerAppraisalParams params);

}