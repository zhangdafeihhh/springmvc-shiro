package mapper.mdbcarmanage.ex;


import com.zhuanche.entity.mdbcarmanage.IntercityTeamDriverRel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InterCityTeamDriverRelExMapper {
    /**批量插入数据库*/
    int insertDriversBatch(List<IntercityTeamDriverRel> teamDriverRel);

    /**删除司机*/
    int deleteDriver(@Param("DriverId")Integer driverId,
                     @Param("teamId")Integer teamId);
}