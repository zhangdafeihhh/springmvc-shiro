package mapper.mdbcarmanage.ex;

import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.entity.mdbcarmanage.CarRelateTeam;
import com.zhuanche.request.TeamGroupRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CarRelateTeamExMapper {

    List<CarRelateTeam> queryDriverTeamRelationList(TeamGroupRequest teamGroupRequest);

    List<Map<String,Object>> queryDriverIdsByTeamIds(@Param("teamIds") Set<Integer> teamIds);

    CarRelateTeam selectOneTeam(CarRelateTeam carRelateTeam);

    /**
     * 根据司机ID删除
     * @param driverId
     * @return
     */
    int deleteByDriverId(@Param("driverId") Integer driverId);

    /**
     * 查询所给车队ID下的所有司机ID
     * @param teamId
     * @param teamIds
     * @return
     */
    List<Integer> queryDriverIdsByTeamId(@Param("teamId") Integer teamId, @Param("teamIds") Set<Integer> teamIds);
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