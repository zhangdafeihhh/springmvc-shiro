package mapper.rentcar;

import com.zhuanche.entity.rentcar.CarBizDriverAccount;

public interface CarBizDriverAccountMapper {
    int deleteByPrimaryKey(Integer accountId);

    int insert(CarBizDriverAccount record);

    int insertSelective(CarBizDriverAccount record);

    CarBizDriverAccount selectByPrimaryKey(Integer accountId);

    int updateByPrimaryKeySelective(CarBizDriverAccount record);

    int updateByPrimaryKey(CarBizDriverAccount record);
}