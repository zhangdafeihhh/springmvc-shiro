package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.InterCityEchelon;

import java.util.List;

public interface InterCityEchelonExMapper {

    List<InterCityEchelon> queryTeamId(Integer teamId, String echeclonMonth);

    
}