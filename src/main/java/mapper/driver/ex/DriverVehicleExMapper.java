package mapper.driver.ex;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zhuanche.entity.driver.DriverVehicle;

public interface DriverVehicleExMapper {

	List<DriverVehicle> queryDriverVehicleList(@Param(value = "brandId") Integer brandId);

}