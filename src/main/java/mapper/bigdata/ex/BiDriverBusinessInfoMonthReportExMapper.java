package mapper.bigdata.ex;

import com.zhuanche.entity.bigdata.BiDriverBusinessInfoMonthReport;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface BiDriverBusinessInfoMonthReportExMapper {
    /**
     *
     * @param cityId 城市id
     * @param supplierId 加盟商
     * @param driverTeamId  车队
     * @param driverGroupId 班组
     * @param driverPhone 司机手机号
     * @param licensePlates 车牌号
     * @param month 月份
     * @param businessVolumeSort 营业额排序
     * @param finOrdCntSort 完单量排序
     * @param badCntSort 差评排序
     * @return
     */
    List<BiDriverBusinessInfoMonthReport> queryMonthReport(@Param("cityId")Integer cityId,
                                                           @Param("supplierIds") Set<Integer> supplierIds,
                                                           @Param("driverTeamIds") Set<Integer> driverTeamIds,
                                                           @Param("driverGroupIds") Set<Integer> driverGroupIds,
                                                       @Param("driverPhone")String driverPhone,
                                                       @Param("licensePlates")String licensePlates,
                                                       @Param("month")String month,
                                                       @Param("businessVolumeSort")String businessVolumeSort,
                                                       @Param("finOrdCntSort")String finOrdCntSort,
                                                       @Param("badCntSort")String badCntSort,
                                                       @Param("sort")String sort,
                                                       @Param("table")String table);
}