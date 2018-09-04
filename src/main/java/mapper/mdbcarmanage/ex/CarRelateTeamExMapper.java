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

}