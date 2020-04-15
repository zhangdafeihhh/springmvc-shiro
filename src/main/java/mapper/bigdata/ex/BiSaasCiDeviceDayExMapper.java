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

    List<CiOrderStatisticSection> getCiOrderNumStatistic(SAASIndexQuery saasIndexQuery,
                                                         @Param("minId")Integer minId,
                                                         @Param("maxId")Integer maxId);

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

    List<SAASAllCoreIndexPercentDto> getCiAllCoreIndexStatistic(@Param("startDate") String startDate,
                                                                @Param("endDate")String endDate,
                                                                @Param("minId")Integer minId,
                                                                @Param("maxId")Integer maxId);

    List<CiOrderStatisticSection> getCiServiceNegativeRate(SAASIndexQuery saasIndexQuery,
                                                           @Param("minId")Integer minId,
                                                           @Param("maxId")Integer maxId);
    CiServiceBadEvaluateAllStatisticSection getAllCiServiceNegativeRate(@Param("findDate")String findDate);

    /**查询指定日期最大和最小id值**/
    MaxAndMinId queryMaxAndMinId(@Param("startDate") String startDate,@Param("endDate")String endDate);


}
