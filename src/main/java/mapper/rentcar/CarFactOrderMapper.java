package mapper.rentcar;

import com.zhuanche.entity.rentcar.CarFactOrder;

public interface CarFactOrderMapper {
    int deleteByPrimaryKey(Integer orderId);

    int insert(CarFactOrder record);

    int insertSelective(CarFactOrder record);

    CarFactOrder selectByPrimaryKey(Integer orderId);

    int updateByPrimaryKeySelective(CarFactOrder record);

    int updateByPrimaryKeyWithBLOBs(CarFactOrder record);

    int updateByPrimaryKey(CarFactOrder record);
}