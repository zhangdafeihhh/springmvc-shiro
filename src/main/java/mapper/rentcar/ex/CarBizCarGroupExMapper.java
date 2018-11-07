package mapper.rentcar.ex;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zhuanche.entity.rentcar.CarBizCarGroup;

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

    List<CarBizCarGroup> queryGroupNameList();
    
	/**
	 * @Title: getSeatNumByGroupId
	 * @Description: 根据groupId查询车型类别座位数
	 * @param groupId
	 * @return int
	 * @throws
	 */
	int getSeatNumByGroupId(Integer groupId);
}