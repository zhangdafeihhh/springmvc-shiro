package mapper.bigdata.ex;

import com.zhuanche.dto.bigdata.BiNewcityDriverReportDayDto;
import com.zhuanche.entity.bigdata.BiNewcityDriverReportDay;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BiNewcityDriverReportDayExMapper {

    /**分页查询*/
    List<BiNewcityDriverReportDay> queryFlowList(@Param("cityId") Integer cityId,
                                                 @Param("supplierId") Integer supplierId,
                                                 @Param("driverName") String driverName,
                                                 @Param("driverPhone") String driverPhone,
                                                 @Param("licensePlates") String licensePlates,
                                                 @Param("dateType") Integer dateType,
                                                 @Param("dataBeginDate") String dataBeginDate,
                                                 @Param("dataEndDate") String dataEndDate,
                                                 @Param("sort")Integer sort);

    /**汇总查询*/
    BiNewcityDriverReportDayDto queryFlowTotal(@Param("cityId") Integer cityId,
                                                    @Param("supplierId") Integer supplierId,
                                                    @Param("driverName") String driverName,
                                                    @Param("driverPhone") String driverPhone,
                                                    @Param("licensePlates") String licensePlates,
                                                    @Param("dateType") Integer dateType,
                                                    @Param("dataBeginDate") String dataBeginDate,
                                                    @Param("dataEndDate") String dataEndDate);

}