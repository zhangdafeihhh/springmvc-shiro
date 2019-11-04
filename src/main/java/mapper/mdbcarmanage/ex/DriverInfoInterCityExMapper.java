package mapper.mdbcarmanage.ex;

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
                                         @Param("supplierIds")Set<Integer> supplierIds);

    DriverInfoInterCity getByDriverId(@Param("driverId") Integer driverId);

    List<DriverInfoInterCity> queryMainOrderDrivers(@Param("cityId")Integer cityId,
                                                    @Param("supplierId")Integer supplierId,
                                                    @Param("driverName")String driverName,
                                                    @Param("driverPhone")String driverPhone,
                                                    @Param("licensePlates")String licensePlates,
                                                    @Param("driverIds")String driverIds);
}