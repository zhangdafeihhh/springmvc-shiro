package mapper.mdbcarmanage.ex;

import java.util.List;

import com.zhuanche.entity.mdbcarmanage.DriverDailyReportParams;
import com.zhuanche.entity.mdbcarmanage.DriverDailyReport;

public interface DriverDailyReportExMapper {

	List<DriverDailyReport> queryForListObject(DriverDailyReportParams params);

	List<DriverDailyReport> queryWeekForListObject(DriverDailyReportParams params);

	List<DriverDailyReport> queryDriverReportData(DriverDailyReportParams params);

	List<Integer> queryDriverIds(DriverDailyReportParams params);

}