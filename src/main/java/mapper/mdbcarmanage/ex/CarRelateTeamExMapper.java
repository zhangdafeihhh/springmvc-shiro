package mapper.mdbcarmanage.ex;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zhuanche.entity.mdbcarmanage.CarRelateTeam;

public interface CarRelateTeamExMapper {

	/**  
	 * <p>Title: selectDriverIdsGroupNoLimit</p>  
	 * <p>Description: 查询司机组</p>  
	 * @param relationId
	 * @param groupIds
	 * @param driverId  
	 * return: void
	 */  
//	List<CarRelateTeam> selectDriverIdsGroupNoLimit(String relationId, String groupIds, String driverId);
	
	/**
	 * 
	 * <p>Title: queryForListObject</p>  
	 * <p>Description: </p>  
	 * @param relationId
	 * @param teamIds    for exce('111','123')
	 * @param driverId
	 * @return  
	 * return: List<CarRelateTeam>
	 */
//	List<CarRelateTeam> queryForListObject(@Param("relationId")String relationId, @Param("teamIds")List teamIds, @Param("driverId")String driverId);
    
	/**
	 * <p>Title: queryListByTeamId</p>  
	 * <p>Description: 通过车队id查询关联关系，参数格式要求('','','')</p>  
	 * @param teamIds
	 * @return  
	 * return: List<CarRelateTeam>
	 */
	List<CarRelateTeam> queryListByTeamIds(@Param("teamIds") String teamIds);

	/**  
	 * <p>Title: queryListByGroupId</p>  
	 * <p>Description: 通过车组id查询关联关系，参数格式要求('','','')</p>  
	 * @param groupIds 
	 * @return  
	 * return: List<CarRelateTeam>
	 */  
	List<CarRelateTeam> queryListByGroupIds(@Param("groupIds") String groupIds);
	
}