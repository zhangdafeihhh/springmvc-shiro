package mapper.driver.ex;

import com.zhuanche.entity.driver.DriverActionDto;
import com.zhuanche.entity.driver.DriverActionVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DriverActionDtoExMapper {
    List<DriverActionDto> queryActionList(DriverActionVO driverActionVO, @Param("table_name") String table);
}
