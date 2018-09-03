package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.CarRelateGroup;
import com.zhuanche.request.TeamGroupRequest;

import java.util.List;

public interface CarRelateGroupExMapper {

    List<CarRelateGroup> queryDriverGroupRelationList(TeamGroupRequest teamGroupRequest);

    CarRelateGroup selectOneGroup(CarRelateGroup group);

}