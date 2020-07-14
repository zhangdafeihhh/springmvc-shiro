package mapper.mdbcarmanage.ex;


import com.zhuanche.entity.mdbcarmanage.IntercityTeamDriverRel;

import java.util.List;

public interface InterCityTeamDriverRelExMapper {
    /**批量插入数据库*/
    int insertBatch(List<IntercityTeamDriverRel> teamDriverRel);

}