package mapper.rentcar.ex;

import com.zhuanche.entity.rentcar.CarBizCarGroup;
import org.apache.ibatis.annotations.Param;

public interface CarBizCarGroupExMapper {

    CarBizCarGroup queryGroupByGroupName (@Param("groupName") String groupName);

}