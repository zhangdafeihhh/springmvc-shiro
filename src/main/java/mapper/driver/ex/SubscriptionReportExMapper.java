package mapper.driver.ex;

import com.zhuanche.entity.driver.SubscriptionReport;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface SubscriptionReportExMapper {

    /**
     * 数据报表下载列表
     * @param reportId 报表ID,1-工资明细;2-完单详情;3-积分;4-数单奖;
     * @param subscriptionCycle 订阅周期,1-周;2-月;
     * @param cityId 城市ID
     * @param supplierId 供应商ID
     * @param teamId 车队ID
     * @return
     */
    List<SubscriptionReport> querySubscriptionList(@Param("reportId") Integer reportId,
                                                   @Param("subscriptionCycle") Integer subscriptionCycle,
                                                   @Param("cityId") Integer cityId,
                                                   @Param("supplierId") Integer supplierId,
                                                   @Param("teamId") Integer teamId,
                                                   @Param("cityIds") Set<Integer> cityIds,
                                                   @Param("supplierIds") Set<Integer> supplierIds,
                                                   @Param("teamIds") Set<Integer> teamIds);

}