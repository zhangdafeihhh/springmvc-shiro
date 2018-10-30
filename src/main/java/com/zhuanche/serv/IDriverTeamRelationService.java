package com.zhuanche.web.service.driver;

import com.zhuanche.web.entity.driver.DriverTeamEntity;

import java.util.List;
import java.util.Map;

@SuppressWarnings("hiding")
public interface IDriverTeamRelationService<DriverTeamRelationEntity> {

	/*
	 * 查询该车队下的司机ids
	 */
	public List<DriverTeamRelationEntity> selectDriverIds(DriverTeamRelationEntity params);
	
	
	public DriverTeamRelationEntity selectDriverInfo(DriverTeamRelationEntity params);
	/*
	 * 保存
	 */
	public int insert(DriverTeamRelationEntity params);
	
	/*
	 * 移除
	 */
	public int delete(DriverTeamRelationEntity params);
	
	public int update(DriverTeamRelationEntity params);
	/*
	 * 查询该车队下的司机数
	 */
	public int selectDriverIdsCount(DriverTeamRelationEntity params);
	
	public List<DriverTeamRelationEntity> queryForList(DriverTeamRelationEntity params);

	public String pingDriverIds(List<DriverTeamRelationEntity> params);
	
	public String pingTeamIds(List<DriverTeamEntity> params);
	/*
	 * 查询该车队下的司机ids  无分页
	 */
	public List<DriverTeamRelationEntity> selectDriverIdsNoLimit(DriverTeamRelationEntity params);
	//================================================================================================
	/*
	 * 保存小组
	 */
	public void insertGroup(DriverTeamRelationEntity params);
	/*
	 * 移除小组
	 */
	public void deleteGroup(DriverTeamRelationEntity params);
	/*
	 * 查询小组司机数
	 */
	public int queryForIntGroup(DriverTeamRelationEntity params);
	/*
	 * 查询该小组下的司机
	 */
	public List<DriverTeamRelationEntity> queryForListObjectGroup(DriverTeamRelationEntity params);
	/*
	 * 查询该车队下的司机ids  无分页
	 */
	public List<DriverTeamRelationEntity> selectDriverIdsGroupNoLimit(DriverTeamRelationEntity params);
	
	public DriverTeamRelationEntity queryForObjectGroup(DriverTeamRelationEntity params);
	
	public DriverTeamRelationEntity queryForObjectTeam(DriverTeamRelationEntity params);
	
	public void deleteTeamAndGroupByDriverId(DriverTeamRelationEntity params);

	public void updateGroup(DriverTeamRelationEntity driverTeamRelationEntity);

	public void updateTeam(DriverTeamRelationEntity driverTeamRelationEntity);

	/**
	 * 根据teamId查询底下司机ID
	 */
	List<Integer> queryDriverIdsByTeamId(Integer teamId);

	List<DriverTeamRelationEntity> queryTeamListInfo(List<String> driverIds);

	List<DriverTeamRelationEntity> queryGroupListInfo(List<String> driverIds);
}
