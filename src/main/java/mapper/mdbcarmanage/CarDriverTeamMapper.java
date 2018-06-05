package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.CarDriverTeam;

public interface CarDriverTeamMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CarDriverTeam record);

    int insertSelective(CarDriverTeam record);

    CarDriverTeam selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CarDriverTeam record);

    int updateByPrimaryKey(CarDriverTeam record);
}