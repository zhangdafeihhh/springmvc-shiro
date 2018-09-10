package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.DriverDailyReport;

public interface DriverDailyReportMapper {
	
    int deleteByPrimaryKey(Integer id);

    int insert(DriverDailyReport record);

    int insertSelective(DriverDailyReport record);

    DriverDailyReport selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DriverDailyReport record);

    int updateByPrimaryKey(DriverDailyReport record);
}