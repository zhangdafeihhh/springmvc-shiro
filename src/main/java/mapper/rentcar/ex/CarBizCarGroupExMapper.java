package mapper.rentcar.ex;

import com.zhuanche.entity.rentcar.CarBizCarGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarBizCarGroupExMapper {
    CarBizCarGroup queryGroupByGroupName (@Param("groupName") String groupName);
    /**
     * 根据groupId查询
     * @param carBizCarGroup
     * @return
     */
    CarBizCarGroup queryForObject(CarBizCarGroup carBizCarGroup);

    CarBizCarGroup queryForObjectByGroupName(CarBizCarGroup carBizCarGroup);

    /**
     * 查询服务类型
     * @param type 1:专车、2:大巴车
     * @return
     */
    List<CarBizCarGroup> queryCarGroupList(@Param("type") Integer type);
    /**
     */
    public String getGroupNameByGroupId(Integer groupId);
}