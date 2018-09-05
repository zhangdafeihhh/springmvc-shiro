package mapper.rentcar;

import com.zhuanche.entity.rentcar.CarSysDictionary;

public interface CarSysDictionaryMapper {
    int deleteByPrimaryKey(Integer dataId);

    int insert(CarSysDictionary record);

    int insertSelective(CarSysDictionary record);

    CarSysDictionary selectByPrimaryKey(Integer dataId);

    int updateByPrimaryKeySelective(CarSysDictionary record);

    int updateByPrimaryKeyWithBLOBs(CarSysDictionary record);

    int updateByPrimaryKey(CarSysDictionary record);
}