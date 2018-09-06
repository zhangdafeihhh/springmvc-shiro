package mapper.driver.ex;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zhuanche.entity.driver.DriverJoinRecord;

import mapper.driver.DriverJoinRecordMapper;

public interface DriverJoinRecordExMapper extends DriverJoinRecordMapper{

	List<DriverJoinRecord> queryJoinRecordByDriverId(@Param("driverId")Long driverId);
    
}