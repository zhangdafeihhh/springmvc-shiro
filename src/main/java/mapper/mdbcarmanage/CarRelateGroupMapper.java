package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.CarRelateGroup;

public interface CarRelateGroupMapper {
    int deleteByPrimaryKey(Integer relationId);

    int insert(CarRelateGroup record);

    int insertSelective(CarRelateGroup record);

    CarRelateGroup selectByPrimaryKey(Integer relationId);

    int updateByPrimaryKeySelective(CarRelateGroup record);

    int updateByPrimaryKey(CarRelateGroup record);
}