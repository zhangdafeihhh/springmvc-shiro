package mapper.driver.ex;

import com.zhuanche.dto.driver.SubscriptionReportConfigureDTO;
import com.zhuanche.entity.driver.SubscriptionReportConfigure;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SubscriptionReportConfigureExMapper {

    /**
     * 根据订阅周期查询数据报表订阅配置
     * @param subscriptionCycle 订阅周期,1-周;2-月;
     * @reportId 报表ID,1-工资明细;2-完单详情;3-积分;4-数单奖
     * @return
     */
    List<SubscriptionReportConfigureDTO> selectBySubscriptionCycle (@Param("subscriptionCycle") Integer subscriptionCycle, @Param("reportId") Integer reportId);

    /**
     * 根据订阅周期查询数据报表订阅配置
     * @param subscriptionCycle 订阅周期,1-周;2-月;
     * @param reportId 报表ID,1-工资明细;2-完单详情;3-积分;4-数单奖
     * @param level 级别,1-全国;2-城市;4-加盟商;8-车队;16-班组
     * @return
     */
    List<SubscriptionReportConfigureDTO> querySubscriptionConfigure (@Param("subscriptionCycle") Integer subscriptionCycle,
                                                                    @Param("reportId") Integer reportId,
                                                                    @Param("level") Integer level);

    /**
     * 对已存在的配置做无效
     * @param subscriptionCycle 订阅周期,1-周;2-月;
     * @return
     */
    int updateConfigureStatus(@Param("reportId") Integer reportId, @Param("subscriptionCycle") Integer subscriptionCycle);

    /**
     * 保存
     * bussinessNumber存在更新状态status=1,不存在插入一条新数据
     * @param record
     * @return
     */
    int saveSubscriptionReportConfigure(SubscriptionReportConfigure record);
}