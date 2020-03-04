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
                                                 @Param("dateDiff")long dateDiff);

    List<SAASAllCoreIndexPercentDto> getCiAllCoreIndexStatistic(@Param("startDate") String startDate, @Param("endDate")String endDate);


}
