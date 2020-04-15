package mapper.bigdata.ex;

import com.zhuanche.entity.bigdata.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author yxp
 * @Description
 * @Date 2020.03.04
 * @Version 1.0
 */
public interface BiSaasCiDeviceDayExMapper {

    List<CiOrderStatisticSection> getCiOrderNumStatistic(SAASIndexQuery saasIndexQuery);

    CiOrderAllStatisticSection getAllCiOrderNumStatistic(@Param("findDate")String findDate);

    Integer getInstallCiDrierNum(SAASIndexQuery saasIndexQuery);

    List<SAASCoreIndexPercentDto> getCiCoreIndexStatistic(@Param("startDate") String startDate,
                                                 @Param("endDate")String endDate,
                                                 @Param("allianceId")String allianceId,
                                                 @Param("motorcadeId")String motorcadeId,
                                                 @Param("visibleAllianceIds")List<String> visibleAllianceIds,
                                                 @Param("visibleMotocadeIds")List<String> visibleMotocadeIds,
                                                 @Param("dateDiff")long dateDiff,
                                                 @Param("minId")Integer minId,
                                                 @Param("maxId")Integer maxId);

    List<SAASAllCoreIndexPercentDto> getCiAllCoreIndexStatistic(@Param("startDate") String startDate, @Param("endDate")String endDate);

    List<CiOrderStatisticSection> getCiServiceNegativeRate(SAASIndexQuery saasIndexQuery);
    CiServiceBadEvaluateAllStatisticSection getAllCiServiceNegativeRate(@Param("findDate")String findDate);

    /**查询指定日期最小值**/
    Integer queryMinId(@Param("startDate") String startDate);

    /**查询指定日期最大值**/
    Integer queryMaxId( @Param("endDate")String endDate);
}
