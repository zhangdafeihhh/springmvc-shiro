package mapper.driver;

import com.zhuanche.entity.driver.SubscriptionReport;

public interface SubscriptionReportMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SubscriptionReport record);

    int insertSelective(SubscriptionReport record);

    SubscriptionReport selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SubscriptionReport record);

    int updateByPrimaryKey(SubscriptionReport record);
}