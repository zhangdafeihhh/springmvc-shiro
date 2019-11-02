package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.MainOrderInterCity;
import org.apache.ibatis.annotations.Param;

public interface MainOrderInterCityExMapper {

    int updateMainTime(@Param("mainOrderNo") String mainOrderNo,
                       @Param("mainTime") String mainTime);

    int addMainOrderNo(MainOrderInterCity record);

    MainOrderInterCity queryMainOrder(@Param("mainOrderNo") String mainOrderNo);

    int updateMainOrderState(@Param("mainOrderNo") String mainOrderNo,
                             @Param("status") Integer status,
                             @Param("phone") Integer phone);

}