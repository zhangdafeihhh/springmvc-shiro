package mapper.rentcar.ex;

import com.zhuanche.entity.rentcar.CarBizCooperationType;

import java.util.List;

public interface CarBizCooperationTypeExMapper {
    CarBizCooperationType queryForObject(CarBizCooperationType carBizCarGroup);

    /**
     * 查询所有加盟类型
     * @return
     */
    List<CarBizCooperationType> queryCarBizCooperationTypeList();
}