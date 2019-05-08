package mapper.bigdata.ex;

import com.zhuanche.entity.bigdata.BiDriverBusinessInfoDayReport;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @Author fanht
 * @Description
 * @Date 2019/4/19 下午5:14
 * @Version 1.0
 */
public interface BiDriverBusinessInfoDayReportExMapper {

    /**
     *
     * @param cityId 城市id
     * @param supplierIds 加盟商
     * @param driverTeamIds  车队
     * @param driverGroupIds 班组
     * @param driverPhone 司机手机号
     * @param licensePlates 车牌号
     * @param beginDate 开始日期
     * @param endDate 结束日期
     * @param businessVolumeSort 营业额排序
     * @param finOrdCntSort 完单量排序
     * @param badCntSort 差评排序
     * @return
     */
    List<BiDriverBusinessInfoDayReport> queryDayReport(@Param("cityId")Integer cityId,
                                                       @Param("supplierIds") Set<Integer> supplierIds,
                                                       @Param("driverTeamIds") Set<Integer> driverTeamIds,
                                                       @Param("driverGroupIds") Set<Integer> driverGroupIds,
                                                       @Param("driverPhone")String driverPhone,
                                                       @Param("licensePlates")String licensePlates,
                                                       @Param("beginDate")String beginDate,
                                                       @Param("endDate")String endDate,
                                                       @Param("businessVolumeSort")String businessVolumeSort,
                                                       @Param("finOrdCntSort")String finOrdCntSort,
                                                       @Param("badCntSort")String badCntSort,
                                                       @Param("sort")String sort,
                                                       @Param("table")String table);






}
