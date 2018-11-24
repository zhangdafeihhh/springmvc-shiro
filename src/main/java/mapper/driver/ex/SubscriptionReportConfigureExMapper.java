package mapper.driver.ex;

import com.zhuanche.dto.driver.SubscriptionReportConfigureDTO;
import com.zhuanche.entity.driver.SubscriptionReportConfigure;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SubscriptionReportConfigureExMapper {

    /**
     * 根据订阅周期查询数据报表订阅配置
     * @param subscriptionCycle 订阅周期,1-周;2-月;
     * @return
     */
    List<SubscriptionReportConfigureDTO> selectBySubscriptionCycle (@Param("subscriptionCycle") Integer subscriptionCycle);

    /**
     * 对已存在的配置做无效
     * @return
     */
    int updateConfigureStatus();

    /**
     * 保存
     * bussinessNumber存在更新状态status=1,不存在插入一条新数据
     * @param record
     * @return
     */
    int saveSubscriptionReportConfigure(SubscriptionReportConfigure record);



}