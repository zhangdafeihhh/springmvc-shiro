package mapper.mdbcarmanage.ex;

import com.zhuanche.dto.driver.DriverTeamRelationEntity;
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

    int deleteDriverFromTeam(@Param("paramId") Integer paramId,@Param("driverId") Integer driverId);

    /**
     * 根据司机ID删除
     * @param driverId
     * @return
     */
    int deleteByDriverId(@Param("driverId") Integer driverId);

    /**
     * 查询所给车队ID下的所有司机ID
     * @param teamId
     * @return
     */
    List<Integer> queryDriverIdsByTeamId(@Param("teamId") Integer teamId);

    /**
     * 根据teamIds返回司机id
     * @param teamIds
     * @return
     */
    List<Integer> queryDriverIdsByTeamIdss(@Param("teamIds") Set<Integer> teamIds);
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


    List<DriverTeamRelationEntity> queryForListObjectNoLimit(DriverTeamRelationEntity params);

    public DriverTeamRelationEntity queryForObject(DriverTeamRelationEntity params);

    public DriverTeamRelationEntity queryForObjectGroup(DriverTeamRelationEntity params);

    List<DriverTeamRelationEntity> selectByDriverIdSet(@Param("driverIdSet")Set<String> driverIdSet);


    List<CarRelateTeam> queryByDriverIdList(@Param("driverIdSet")Set<Integer> driverIdSet);
}