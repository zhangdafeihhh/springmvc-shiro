package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.MainOrderInterCity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MainOrderInterCityExMapper {

    int updateMainTime(@Param("mainOrderNo") String mainOrderNo,
                       @Param("mainTime") String mainTime);

    int addMainOrderNo(MainOrderInterCity record);

    MainOrderInterCity queryMainOrder(@Param("mainOrderNo") String mainOrderNo);

    int updateMainOrderState(@Param("mainOrderNo") String mainOrderNo,
                             @Param("status") Integer status,
                             @Param("phone") String phone);

    /**
     * 根据司机id查询对应的主单信息
     * @param driverId
     * @return
     */
    List<MainOrderInterCity> phoneQueryByDriverId(@Param("driverId")Integer driverId);

}