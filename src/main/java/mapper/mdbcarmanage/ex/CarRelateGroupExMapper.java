package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.CarRelateGroup;
import com.zhuanche.request.TeamGroupRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarRelateGroupExMapper {

    List<CarRelateGroup> queryDriverGroupRelationList(TeamGroupRequest teamGroupRequest);

    CarRelateGroup selectOneGroup(CarRelateGroup group);

    /**
     * 根据司机ID删除
     * @param driverId
     * @return
     */
    int deleteByDriverId(@Param("driverId") Integer driverId);

}