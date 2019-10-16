package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.DriverInfoInterCity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DriverInfoInterCityExMapper {


    List<DriverInfoInterCity> queryDriver(@Param("supplierId")Integer supplierId,
                                          @Param("driverName")String driverName,
                                          @Param("driverPhone")String driverPhone,
                                          @Param("licensePlates")String licensePlates);

}