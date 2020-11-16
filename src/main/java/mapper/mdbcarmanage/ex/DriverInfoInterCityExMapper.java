package mapper.mdbcarmanage.ex;

import com.zhuanche.dto.mdbcarmanage.IntegerDriverInfoDto;
import com.zhuanche.dto.mdbcarmanage.InterDriverTeamRelDto;
import com.zhuanche.dto.mdbcarmanage.MainOrderDetailDTO;
import com.zhuanche.entity.mdbcarmanage.DriverInfoInterCity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface DriverInfoInterCityExMapper {


    List<MainOrderDetailDTO> queryDriver(@Param("cityId")Integer cityId,
                                         @Param("supplierId")Integer supplierId,
                                         @Param("driverName")String driverName,
                                         @Param("driverPhone")String driverPhone,
                                         @Param("licensePlates")String licensePlates,
                                         @Param("cityIds")Set<Integer> cityIds,
                                         @Param("supplierIds")Set<Integer> supplierIds,
                                         @Param("teamId")Integer teamId);

    DriverInfoInterCity getByDriverId(@Param("driverId") Integer driverId);

    List<DriverInfoInterCity> queryMainOrderDrivers(@Param("cityId")Integer cityId,
                                                    @Param("supplierId")Integer supplierId,
                                                    @Param("driverName")String driverName,
                                                    @Param("driverPhone")String driverPhone,
                                                    @Param("licensePlates")String licensePlates,
                                                    @Param("driverIds")String driverIds);
    /**查询城际拼车司机车队列表*/
    List<InterDriverTeamRelDto> queryDriverRelTeam(@Param("cityId")Integer cityId,
                                                   @Param("supplierId")Integer supplierId,
                                                   @Param("driverName")String driverName,
                                                   @Param("driverPhone")String driverPhone,
                                                   @Param("licensePlates")String licensePlates,
                                                   @Param("teamId")Integer teamId);

    /**根据司机的查询司机信息*/
    List<IntegerDriverInfoDto> driverDtoList(@Param("driverIds") List<Integer> driverIds);

    List<DriverInfoInterCity> queryDriverByParam(@Param("queryParam") String  queryParam,
                                                 @Param("supplierId") Integer  supplierId);

    List<Integer> queryTeamIds(@Param("cityId")Integer cityId,
                               @Param("supplierId")Integer supplierId,
                               @Param("driverName")String driverName,
                               @Param("driverPhone")String driverPhone,
                               @Param("licensePlates")String licensePlates);

    List<DriverInfoInterCity> phoneQueryMainOrderDrivers(@Param("queryParam")String queryParam,
                                                         @Param("cityIds")Set<Integer> cityIds,
                                                         @Param("supplierIds")Set<Integer> supplierIds,
                                                         @Param("teamId")Set<Integer> teamId);

    List<DriverInfoInterCity> queryDrivers(@Param("cities")Set<Integer> cities,
                                           @Param("supplierIds") Set<Integer> supplierIds);

}