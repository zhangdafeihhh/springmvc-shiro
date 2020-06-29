package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.CarMessageGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarMessageGroupExMapper {

    int insert(CarMessageGroup record);

    int insertSelective(CarMessageGroup record);

    CarMessageGroup selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CarMessageGroup record);

    int updateByPrimaryKey(CarMessageGroup record);

    int editStatusGroup(@Param("id") Long id,
                        @Param("status") Integer status);

    List<CarMessageGroup> searchGroup(CarMessageGroup group);

    Integer isRepeatGroupName(@Param("groupName") String groupName);

    List<CarMessageGroup> getGroupByIds(@Param("ids") List<Integer> ids);
}