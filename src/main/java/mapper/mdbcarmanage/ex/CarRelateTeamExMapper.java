package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.CarRelateTeam;
import com.zhuanche.request.TeamGroupRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarRelateTeamExMapper {

    List<CarRelateTeam> queryDriverTeamRelationList(TeamGroupRequest teamGroupRequest);

    CarRelateTeam selectOneTeam(CarRelateTeam carRelateTeam);

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