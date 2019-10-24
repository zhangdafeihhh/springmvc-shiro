package mapper.driver;

import com.zhuanche.entity.driver.SubscriptionReportType;

public interface SubscriptionReportTypeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SubscriptionReportType record);

    int insertSelective(SubscriptionReportType record);

    SubscriptionReportType selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SubscriptionReportType record);

    int updateByPrimaryKey(SubscriptionReportType record);
}