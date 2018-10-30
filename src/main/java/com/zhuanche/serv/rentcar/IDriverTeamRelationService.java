package com.zhuanche.serv.rentcar;


import java.util.List;
import java.util.Set;

@SuppressWarnings("hiding")
public interface IDriverTeamRelationService<DriverTeamRelationEntity> {


	/*
	 * 查询该车队下的司机ids  无分页
	 */
	public List<DriverTeamRelationEntity> selectDriverIdsNoLimit(DriverTeamRelationEntity params);

	public String pingDriverIds(List<DriverTeamRelationEntity> params);

	public DriverTeamRelationEntity selectDriverInfo(DriverTeamRelationEntity params);

	public DriverTeamRelationEntity queryForObjectGroup(DriverTeamRelationEntity params);

	public List<DriverTeamRelationEntity> selectByDriverIdSet(Set<String> driverIdSet);

}
