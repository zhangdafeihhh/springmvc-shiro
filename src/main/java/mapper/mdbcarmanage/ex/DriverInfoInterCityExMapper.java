package mapper.mdbcarmanage.ex;

import com.zhuanche.dto.mdbcarmanage.MainOrderDetailDTO;
import com.zhuanche.entity.mdbcarmanage.DriverInfoInterCity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DriverInfoInterCityExMapper {


    List<MainOrderDetailDTO> queryDriver(@Param("supplierId")Integer supplierId,
                                         @Param("driverName")String driverName,
                                         @Param("driverPhone")String driverPhone,
                                         @Param("licensePlates")String licensePlates);

    DriverInfoInterCity getByDriverId(@Param("driverId") Integer driverId);

    List<DriverInfoInterCity> queryMainOrderDrivers(@Param("cityId")Integer cityId,
                                                    @Param("supplierId")Integer supplierId,
                                                    @Param("driverName")String driverName,
                                                    @Param("driverPhone")String driverPhone,
                                                    @Param("licensePlates")String licensePlates,
                                                    @Param("driverIds")String driverIds);
}