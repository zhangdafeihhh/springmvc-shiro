package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.CarBizDriverUpdate;

public interface CarBizDriverUpdateMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CarBizDriverUpdate record);

    int insertSelective(CarBizDriverUpdate record);

    CarBizDriverUpdate selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CarBizDriverUpdate record);

    int updateByPrimaryKey(CarBizDriverUpdate record);
}