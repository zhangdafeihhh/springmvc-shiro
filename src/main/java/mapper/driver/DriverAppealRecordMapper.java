package mapper.driver;

import com.zhuanche.entity.driver.DriverAppealRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DriverAppealRecordMapper {
    int deleteByPrimaryKey(Integer appealId);

    int insert(DriverAppealRecord record);

    int insertSelective(DriverAppealRecord record);

    DriverAppealRecord selectByPrimaryKey(Integer appealId);

    int updateByPrimaryKeySelective(DriverAppealRecord record);

    int updateNotRejectedByPunishId(DriverAppealRecord record);

    int updateByPrimaryKey(DriverAppealRecord record);

    List<DriverAppealRecord> selectDriverAppealRocordByPunishId(@Param("punishId") Integer punishId);
}