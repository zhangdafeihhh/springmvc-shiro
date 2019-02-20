package mapper.driverOrderRecord.ex;

import com.zhuanche.entity.driver.DriverActionDto;

import java.util.List;
import java.util.Map;

public interface DriverActionDtoExMapper {
    List<DriverActionDto> queryActionList(Map<String, Object> map);
}
