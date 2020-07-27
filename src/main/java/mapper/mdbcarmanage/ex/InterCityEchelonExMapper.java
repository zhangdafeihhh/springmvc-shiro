package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.InterCityEchelon;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InterCityEchelonExMapper {

    /**根据单个车队和月份查询*/
    List<InterCityEchelon> queryTeamId(@Param("teamId") Integer teamId,
                                       @Param("echelonMonth") String echelonMonth);

    /**根据多个车队和月份查询*/
    List<InterCityEchelon> queryTeamIds(@Param("teamIdList") List<Integer> teamIdList,
                                        @Param("echelonMonth") String echelonMonth);

    /**查询某个月份的数据*/
    List<Integer> teamIdListByMonth(@Param("echelonMonth") String echelonMonth);


}