package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.InterCityEchelon;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InterCityEchelonExMapper {

    /**根据单个车队和月份查询*/
    List<InterCityEchelon> queryTeamId(@Param("teamId") Integer teamId,
                                       @Param("echeclonMonth") String echeclonMonth);

    /**根据多个车队和月份查询*/
    List<InterCityEchelon> queryTeamIds(List<Integer> teamIdList, String echeclonMonth);

}