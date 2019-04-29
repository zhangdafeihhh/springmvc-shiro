package mapper.driver.ex;

import com.zhuanche.entity.driver.DriverAppraisalAppeal;
import com.zhuanche.vo.driver.DriverAppealDetailVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface DriverAppraisalAppealExMapper {
    /**
     * 根据评分表ID 批量查询申诉信息
     * @param appraisalIds
     * @return
     */
    List<DriverAppraisalAppeal> queryBaseInfoByAppraisalIds(@Param(value = "appraisalIds") List<Integer> appraisalIds);

    DriverAppealDetailVO getDetail(@Param(value = "appealId") Integer appealId);

    Integer getAppealStatus(@Param(value = "appealId") Integer appealId);

    DriverAppraisalAppeal getAppealStatusByAppraisalId(@Param(value = "appraisalId") Integer appraisalId);

    Set<Integer> getAppraissalIdsByAppealStatus(@Param("appealStatus") Integer appealStatus);


}