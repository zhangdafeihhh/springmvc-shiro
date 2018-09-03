package mapper.rentcar;

import com.zhuanche.entity.rentcar.CarBizUserAction;

public interface CarBizUserActionMapper {
    int deleteByPrimaryKey(Integer chatUserId);

    int insert(CarBizUserAction record);

    int insertSelective(CarBizUserAction record);

    CarBizUserAction selectByPrimaryKey(Integer chatUserId);

    int updateByPrimaryKeySelective(CarBizUserAction record);

    int updateByPrimaryKey(CarBizUserAction record);
}