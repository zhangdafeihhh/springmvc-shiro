package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.DriverDutyTimeInfo;

public interface DriverDutyTimeInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DriverDutyTimeInfo record);

    int insertSelective(DriverDutyTimeInfo record);

    DriverDutyTimeInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DriverDutyTimeInfo record);

    int updateByPrimaryKey(DriverDutyTimeInfo record);
}