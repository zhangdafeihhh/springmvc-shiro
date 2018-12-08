package mapper.rentcar;

import com.zhuanche.entity.rentcar.CarBizService;

public interface CarBizServiceMapper {
    int insert(CarBizService record);

    int insertSelective(CarBizService record);

    CarBizService selectByPrimaryKey(Integer serviceId);

    int updateByPrimaryKeySelective(CarBizService record);

    int updateByPrimaryKeyWithBLOBs(CarBizService record);

    int updateByPrimaryKey(CarBizService record);
}