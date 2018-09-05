package mapper.mdbcarmanage.ex;

import java.util.List;

import com.zhuanche.entity.mdbcarmanage.DriverDailyReportParams;
import com.zhuanche.entity.mdbcarmanage.DriverDailyReport;

public interface DriverDailyReportExMapper {
    
	public List<DriverDailyReport> queryForListObject(DriverDailyReportParams params);
	
	public List<DriverDailyReport> queryWeekForListObject(DriverDailyReportParams params);

	public List<DriverDailyReport> queryDriverReportData(DriverDailyReportParams params);

}