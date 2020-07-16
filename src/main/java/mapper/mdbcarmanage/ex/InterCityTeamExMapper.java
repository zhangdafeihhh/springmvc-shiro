package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.InterCityTeam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InterCityTeamExMapper {

    /**根据城市和供应商id查询*/
    List<InterCityTeam> queryTeam(@Param("cityId") Integer cityId,
                            @Param("supplierId") Integer supplierId);
}