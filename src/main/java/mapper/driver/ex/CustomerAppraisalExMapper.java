package mapper.driver.ex;

import com.zhuanche.entity.driver.MpCarBizCustomerAppraisal;
import com.zhuanche.entity.driver.MpCustomerAppraisalParams;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface CustomerAppraisalExMapper {

    List<MpCarBizCustomerAppraisal> queryForListObject(MpCustomerAppraisalParams params);
    List<Integer> queryIds(MpCustomerAppraisalParams params);
    List<MpCarBizCustomerAppraisal> queryByIds(@Param("ids") List<Integer> ids);

}