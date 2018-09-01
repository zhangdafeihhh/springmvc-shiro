package mapper.driver.ex;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zhuanche.entity.driver.DriverBigdataDistrict;

public interface DriverBigdataDistrictExMapper {
    List<DriverBigdataDistrict> findListByCityId(@Param("cityId") Integer cityId, @Param("date") Date date , @Param("time") String time);

}