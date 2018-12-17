package mapper.driver;

import com.zhuanche.entity.driver.SubscriptionReportConfigure;

public interface SubscriptionReportConfigureMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SubscriptionReportConfigure record);

    int insertSelective(SubscriptionReportConfigure record);

    SubscriptionReportConfigure selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SubscriptionReportConfigure record);

    int updateByPrimaryKey(SubscriptionReportConfigure record);
}