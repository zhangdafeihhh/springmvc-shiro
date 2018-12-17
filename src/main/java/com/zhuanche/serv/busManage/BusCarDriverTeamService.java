package com.zhuanche.serv.busManage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;

import mapper.mdbcarmanage.ex.CarRelateGroupExMapper;
import mapper.mdbcarmanage.ex.CarRelateTeamExMapper;

/**
 * @ClassName: CarDriverTeamService
 * @Description: 巴士车队管理
 * @author: yanyunpeng
 * @date: 2018年11月22日 下午6:27:22
 * 
 */
@Service
public class BusCarDriverTeamService {

	// ===========================表基础mapper==================================

	// ===========================专车业务拓展mapper==================================
	@Autowired
	private CarRelateTeamExMapper carRelateTeamExMapper;

	@Autowired
	private CarRelateGroupExMapper carRelateGroupExMapper;

	// ===========================巴士业务拓展mapper==================================

	/**
	 * 查询所给小组ID下的所有司机ID, 查询所给车队ID下的所有司机ID
	 * 
	 * @param groupId
	 * @param teamId
	 * @param teamIds
	 * @return
	 */
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.SLAVE))
	public Set<Integer> selectDriverIdsByTeamIdOrGroupId(Integer groupId, Integer teamId, Set<Integer> teamIds) {
		Set<Integer> result = new HashSet<Integer>();
		if (groupId != null) {// 有车队下小组ID传入，故以此ID为主
			List<Integer> driverIds = carRelateGroupExMapper.queryDriverIdsByGroupId(groupId);
			if (driverIds != null && driverIds.size() > 0) {
				result = new HashSet<Integer>(driverIds);
			}
			return result;
		} else if (teamId != null) {// 没有车队下小组ID传入，以传入车队ID以及当前用户的数据权限下车队ID查询sijiID
			List<Integer> driverIds = carRelateTeamExMapper.queryDriverIdsByTeamId(teamId);
			if (driverIds != null && driverIds.size() > 0) {
				result = new HashSet<Integer>(driverIds);
			}
			return result;
		} else if (teamIds != null && teamIds.size() > 0) {
			List<Integer> driverIds = carRelateTeamExMapper.queryDriverIdsByTeamIdss(teamIds);
			if (driverIds != null && driverIds.size() > 0) {
				result = new HashSet<Integer>(driverIds);
			}
			return result;
		}
		return result;
	}

}