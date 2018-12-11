package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.CarBizOrderMessageTask;

public interface CarBizOrderMessageTaskMapper {
    int insert(CarBizOrderMessageTask record);

    int insertSelective(CarBizOrderMessageTask record);

    CarBizOrderMessageTask selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CarBizOrderMessageTask record);

    int updateByPrimaryKey(CarBizOrderMessageTask record);
}