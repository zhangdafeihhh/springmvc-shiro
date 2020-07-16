package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.InterCityTeam;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface InterCityTeamExMapper {

    /**根据城市和供应商id查询*/
    List<InterCityTeam> queryTeam(@Param("cityId") Integer cityId,
                            @Param("supplierId") Integer supplierId);

    /**验证是否有重复的*/
    List<InterCityTeam> verifyTeam(@Param("cityId") Integer cityId,
                                  @Param("supplierId") Integer supplierId,
                                   @Param("teamName")String teamName);

    List<InterCityTeam> listTeamByIds(@Param("ids") List<Integer> ids);


}