package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.CarRelateTeam;

public interface CarRelateTeamMapper {
    int deleteByPrimaryKey(Integer relationId);

    int insert(CarRelateTeam record);

    int insertSelective(CarRelateTeam record);

    CarRelateTeam selectByPrimaryKey(Integer relationId);

    int updateByPrimaryKeySelective(CarRelateTeam record);

    int updateByPrimaryKey(CarRelateTeam record);
}