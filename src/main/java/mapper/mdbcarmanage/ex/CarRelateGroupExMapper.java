package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.CarRelateGroup;
import com.zhuanche.request.TeamGroupRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarRelateGroupExMapper {

    List<CarRelateGroup> queryDriverGroupRelationList(TeamGroupRequest teamGroupRequest);

    CarRelateGroup selectOneGroup(CarRelateGroup group);

	List<CarRelateGroup> queryByParams(CarRelateGroup group);

	List<Integer> queryDriversByParams(CarRelateGroup group);

    /**
     * 根据司机ID删除
     * @param driverId
     * @return
     */
    int deleteByDriverId(@Param("driverId") Integer driverId);

    /**
     * 查询所给小组ID下的所有司机ID
     * @param groupId
     * @return
     */
    List<Integer> queryDriverIdsByGroupId(@Param("groupId") Integer groupId);

}