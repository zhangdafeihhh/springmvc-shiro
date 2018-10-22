package mapper.mdbcarmanage.ex;

import java.util.List;

import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.entity.mdbcarmanage.DriverDailyReportParams;
import com.zhuanche.entity.mdbcarmanage.DriverDailyReport;

public interface DriverDailyReportExMapper {

	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
	})
	public List<DriverDailyReport> queryForListObject(DriverDailyReportParams params);
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
	})
	public List<DriverDailyReport> queryWeekForListObject(DriverDailyReportParams params);
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
	})
	public List<DriverDailyReport> queryDriverReportData(DriverDailyReportParams params);

}