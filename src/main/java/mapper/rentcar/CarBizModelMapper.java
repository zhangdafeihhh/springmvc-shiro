package mapper.rentcar;

import com.zhuanche.entity.rentcar.CarBizModel;

public interface CarBizModelMapper {
    int deleteByPrimaryKey(Integer modelId);

    int insert(CarBizModel record);

    int insertSelective(CarBizModel record);

    CarBizModel selectByPrimaryKey(Integer modelId);

    int updateByPrimaryKeySelective(CarBizModel record);

    int updateByPrimaryKeyWithBLOBs(CarBizModel record);

    int updateByPrimaryKey(CarBizModel record);
}