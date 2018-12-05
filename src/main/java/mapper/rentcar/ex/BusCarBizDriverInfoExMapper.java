package mapper.rentcar.ex;

import java.util.List;

import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.dto.busManage.BusDriverQueryDTO;
import com.zhuanche.dto.busManage.BusDriverSaveDTO;
import com.zhuanche.vo.busManage.BusDriverInfoExportVO;
import com.zhuanche.vo.busManage.BusDriverInfoPageVO;

public interface BusCarBizDriverInfoExMapper {

	/**
	 * @Title: queryDriverPageList
	 * @Description: 查询司机信息列表展示
	 * @param queryDTO
	 * @return 
	 * @return List<BusDriverInfoVO>
	 * @throws
	 */
	List<BusDriverInfoPageVO> queryDriverPageList(BusDriverQueryDTO queryDTO);

	/**
	 * @Title: queryDriverExportList
	 * @Description: 查询司机信息导出列表
	 * @param exportDTO
	 * @return 
	 * @return List<BusDriverInfoVO>
	 * @throws
	 */
	List<BusDriverInfoExportVO> queryDriverExportList(BusDriverQueryDTO exportDTO);
	
	/**
	 * @Title: updateBusDriverInfo
	 * @Description: 修改司机信息
	 * @param saveDTO
	 * @return int
	 * @throws
	 */
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.MASTER) })
	int updateBusDriverInfo(BusDriverSaveDTO saveDTO);

	/**
	 * @Title: insertBusDriverInfo
	 * @Description: 新增司机信息
	 * @param record
	 * @return int
	 * @throws
	 */
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.MASTER) })
	int insertBusDriverInfo(BusDriverSaveDTO record);


}