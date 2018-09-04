package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.CarRelateTeam;
import com.zhuanche.request.TeamGroupRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarRelateTeamExMapper {

    List<CarRelateTeam> queryDriverTeamRelationList(TeamGroupRequest teamGroupRequest);

    CarRelateTeam selectOneTeam(CarRelateTeam carRelateTeam);

    /**
     * 根据司机ID删除
     * @param driverId
     * @return
     */
    int deleteByDriverId(@Param("driverId") Integer driverId);

}