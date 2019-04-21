package mapper.bigdata.ex;

import com.zhuanche.entity.bigdata.BiDriverBusinessInfoSummaryReport;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BiDriverBusinessInfoSummaryReportExMapper {
    /**
     *
     * @param cityId 城市id
     * @param supplierId 加盟商
     * @param driverTeamId  车队
     * @param driverGroupId 班组
     * @param driverPhone 司机手机号
     * @param licensePlates 车牌号
     * @param currentDate 当前日期
     * @param businessVolumeSort 营业额排序
     * @param finOrdCntSort 完单量排序
     * @param badCntSort 差评排序
     * @return
     */
    List<BiDriverBusinessInfoSummaryReport> querySummeryReport(@Param("cityId")Integer cityId,
                                                           @Param("supplierId")Integer supplierId,
                                                           @Param("driverTeamId")Integer driverTeamId,
                                                           @Param("driverGroupId")Integer driverGroupId,
                                                           @Param("driverPhone")String driverPhone,
                                                           @Param("licensePlates")String licensePlates,
                                                           @Param("currentDate")String currentDate,
                                                           @Param("businessVolumeSort")String businessVolumeSort,
                                                           @Param("finOrdCntSort")String finOrdCntSort,
                                                           @Param("badCntSort")String badCntSort,
                                                           @Param("sort")String sort);
}