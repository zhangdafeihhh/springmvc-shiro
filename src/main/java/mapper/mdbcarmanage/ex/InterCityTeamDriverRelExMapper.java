package mapper.mdbcarmanage.ex;


import com.zhuanche.entity.mdbcarmanage.IntercityTeamDriverRel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InterCityTeamDriverRelExMapper {
    /**批量插入数据库*/
    int insertDriversBatch(@Param("teamDriverRel") List<IntercityTeamDriverRel> teamDriverRel);

    /**删除司机*/
    int deleteDriver(@Param("driverId")Integer driverId,
                     @Param("teamId")Integer teamId);

    List<IntercityTeamDriverRel> teamRelList(@Param("driverIds") List driverIds);

    /**查询司机Id*/
    List<Integer> queryDriverIds(@Param("teamId") Integer teamId);


    List<Integer> queryRelTeamIds(@Param("cityId")Integer cityId,
                          @Param("supplierId")Integer supplierId,
                          @Param("teamId")Integer teamId);
    /**删除车队的司机*/
    int delByTeamId(Integer teamId);

    int batchDelete(@Param("driverIdList") List<Integer> driverIdList);
}