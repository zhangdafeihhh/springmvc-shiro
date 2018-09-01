package mapper.rentcar;

import com.zhuanche.entity.rentcar.CarBizCooperationType;

public interface CarBizCooperationTypeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CarBizCooperationType record);

    int insertSelective(CarBizCooperationType record);

    CarBizCooperationType selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CarBizCooperationType record);

    int updateByPrimaryKey(CarBizCooperationType record);
}