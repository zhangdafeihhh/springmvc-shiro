package mapper.rentcar;

import com.zhuanche.entity.rentcar.CarBizDriverInfoDetail;

public interface CarBizDriverInfoDetailMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CarBizDriverInfoDetail record);

    int insertSelective(CarBizDriverInfoDetail record);

    CarBizDriverInfoDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CarBizDriverInfoDetail record);

    int updateByPrimaryKey(CarBizDriverInfoDetail record);
}