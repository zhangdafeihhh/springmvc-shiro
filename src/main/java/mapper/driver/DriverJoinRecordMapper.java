package mapper.driver;

import com.zhuanche.entity.driver.DriverJoinRecord;

public interface DriverJoinRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DriverJoinRecord record);

    int insertSelective(DriverJoinRecord record);

    DriverJoinRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DriverJoinRecord record);

    int updateByPrimaryKey(DriverJoinRecord record);
}