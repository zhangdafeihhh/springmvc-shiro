package mapper.driver.ex;

import com.zhuanche.entity.driver.CustomerAppraisal;
import com.zhuanche.entity.driver.MpCarBizCustomerAppraisal;
import com.zhuanche.entity.driver.MpCustomerAppraisalParams;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomerAppraisalExMapper {

    List<MpCarBizCustomerAppraisal> queryForListObject(MpCustomerAppraisalParams params);
    List<Integer> queryIds(MpCustomerAppraisalParams params);
    List<MpCarBizCustomerAppraisal> queryByIds(@Param("ids") List<Integer> ids);

    List<CustomerAppraisal>queryBatchAppraisal(List<String> orderNos);

    CustomerAppraisal queryAppraisal(String orderNo);

}