package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.InterCityTeam;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface InterCityTeamExMapper {

    /**根据城市和供应商id查询*/
    List<InterCityTeam> queryTeam(@Param("cityId") Integer cityId,
                            @Param("supplierId") Integer supplierId);

    /**验证是否有重复的*/
    List<InterCityTeam> verifyTeam(@Param("cityId") Integer cityId,
                                  @Param("supplierId") Integer supplierId,
                                   @Param("teamName")String teamName);

    List<InterCityTeam> listTeamByIds(@Param("ids") List<Integer> ids);



    /**查询小组*/
    List<InterCityTeam> queryTeamsByParam(@Param("cityIdParam") Integer cityIdParam,
                                   @Param("supplierId") Integer supplierId,
                                   @Param("teamId")Integer teamId,
                                   @Param("teamIdList")List<Integer> teamIdList,
                                          @Param("cityIds") Set<Integer> cityIds,
                                          @Param("supplierIds") Set<Integer> supplierIds);

}