package mapper.driverOrderRecord.ex;

import com.zhuanche.entity.driver.DriverActionDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DriverActionDtoExMapper {
    List<DriverActionDto> queryActionList(Map<String, Object> map);

    List<DriverActionDto> queryActionTimeLine(@Param("tableName") String tableName, @Param("driverId")Integer driverId, @Param("time")String time);
}
