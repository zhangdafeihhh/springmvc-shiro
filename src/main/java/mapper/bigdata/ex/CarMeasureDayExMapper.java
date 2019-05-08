package mapper.bigdata.ex;

import com.zhuanche.entity.bigdata.SAASCoreIndexDto;
import com.zhuanche.entity.bigdata.SAASIndexQuery;
import com.zhuanche.entity.bigdata.StatisticSection;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author fanht
 * @Description
 * @Date 2019/3/29 下午5:43
 * @Version 1.0
 */
public interface CarMeasureDayExMapper {

    Integer getCarOnlineDurationMaxId(SAASIndexQuery saasIndexQuery);

    List<StatisticSection> getCarOnlineDuration(SAASIndexQuery saasIndexQuery);

    List<StatisticSection> getCarOperateStatistics(SAASIndexQuery saasIndexQuery);

    List<StatisticSection> getOrderNumStatistic(SAASIndexQuery saasIndexQuery);

    List<StatisticSection> getServiceNegativeRate(SAASIndexQuery saasIndexQuery);

    /**
     * 获取首页指标
     * @param startDate
     * @param endDate
     * @param allianceId
     * @param motorcadeId
     * @param visibleAllianceIds
     * @param visibleMotocadeIds
     * @return
     */
    List<SAASCoreIndexDto> getCoreIndexStatistic(@Param("startDate") String startDate,
                                                 @Param("endDate")String endDate,
                                                 @Param("allianceId")String allianceId,
                                                 @Param("motorcadeId")String motorcadeId,
                                                 @Param("visibleAllianceIds")List<String> visibleAllianceIds,
                                                 @Param("visibleMotocadeIds")List<String> visibleMotocadeIds,
                                                 @Param("dateDiff")long dateDiff);

}
