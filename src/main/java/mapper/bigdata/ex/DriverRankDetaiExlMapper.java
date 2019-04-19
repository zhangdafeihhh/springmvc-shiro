package mapper.bigdata.ex;

import com.zhuanche.entity.bigdata.SAASDriverRankingDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DriverRankDetaiExlMapper {
    List<SAASDriverRankingDto> getDriverRanking(
                                                @Param("allianceId")String allianceId,
                                                @Param("motorcadeId")String motorcadeId,
                                                @Param("orderByColumnCode")String orderByColumnCode,
                                                @Param("orderByTypeCode")String orderByTypeCode,
                                                @Param("topNum")Integer topNum,
                                                @Param("visibleAllianceIds")List<String> visibleAllianceIds,
                                                @Param("visibleMotocadeIds")List<String> visibleMotocadeIds,
                                                @Param("date")String date
                                               );
}