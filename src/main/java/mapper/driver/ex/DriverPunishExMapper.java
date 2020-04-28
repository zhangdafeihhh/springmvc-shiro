package mapper.driver.ex;

import com.zhuanche.entity.bigdata.MaxAndMinId;
import com.zhuanche.entity.driver.DriverPunishDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DriverPunishExMapper {

    List<DriverPunishDto> selectList(DriverPunishDto params);

    DriverPunishDto getDetail(@Param("punishId") Integer punishId);

    MaxAndMinId queryMaxAndMin(@Param("startDate") String startDate,
                               @Param("endDate") String endDate);
}