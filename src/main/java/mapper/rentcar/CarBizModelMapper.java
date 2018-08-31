package mapper.rentcar;

import com.zhuanche.entity.rentcar.CarBizModel;

/**
 * @author wzq
 */
public interface CarBizModelMapper {
    int deleteByPrimaryKey(Integer modelId);

    int insert(CarBizModel record);

    int insertSelective(CarBizModel record);

    CarBizModel selectByPrimaryKey(Integer modelId);

    int updateByPrimaryKeySelective(CarBizModel record);
}