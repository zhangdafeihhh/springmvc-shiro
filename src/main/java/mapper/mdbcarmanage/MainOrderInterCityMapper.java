package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.MainOrderInterCity;

public interface MainOrderInterCityMapper {
    int insert(MainOrderInterCity record);

    int insertSelective(MainOrderInterCity record);

    MainOrderInterCity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MainOrderInterCity record);

    int updateByPrimaryKey(MainOrderInterCity record);
}