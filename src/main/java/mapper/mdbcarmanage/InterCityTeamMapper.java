package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.InterCityTeam;

public interface InterCityTeamMapper {
    int insert(InterCityTeam record);

    int insertSelective(InterCityTeam record);

    InterCityTeam selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(InterCityTeam record);

    int updateByPrimaryKey(InterCityTeam record);
}