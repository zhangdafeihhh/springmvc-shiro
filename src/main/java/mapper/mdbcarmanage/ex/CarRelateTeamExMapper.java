package mapper.mdbcarmanage.ex;

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
}