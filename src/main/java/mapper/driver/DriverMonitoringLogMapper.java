package mapper.driver;

import com.zhuanche.entity.driver.DriverMonitoringLog;

public interface DriverMonitoringLogMapper {
    int deleteByPrimaryKey(Long monitorId);

    int insert(DriverMonitoringLog record);

    int insertSelective(DriverMonitoringLog record);

    DriverMonitoringLog selectByPrimaryKey(Long monitorId);

    int updateByPrimaryKeySelective(DriverMonitoringLog record);

    int updateByPrimaryKey(DriverMonitoringLog record);
}