package mapper.mdbcarmanage.ex;

import java.util.List;

import com.zhuanche.entity.common.DriverDailyReportBean;
import com.zhuanche.entity.mdbcarmanage.DriverDailyReport;

public interface DriverDailyReportExMapper {
    
	public List<DriverDailyReport> queryForListObject(DriverDailyReportBean driverDailyReportBean);
	
	public List<DriverDailyReport> queryWeekForListObject(DriverDailyReportBean driverDailyReportBean);
	
}