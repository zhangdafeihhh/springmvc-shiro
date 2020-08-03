package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.InterCityEchelon;

public interface InterCityEchelonMapper {
    int insert(InterCityEchelon record);

    int insertSelective(InterCityEchelon record);

    InterCityEchelon selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(InterCityEchelon record);

    int updateByPrimaryKey(InterCityEchelon record);



}