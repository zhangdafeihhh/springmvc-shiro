package mapper.rentcar.ex;

import com.zhuanche.entity.rentcar.CarBizModel;

import java.util.List;
import java.util.Set;

/**
 * @author wzq
 */
public interface CarBizModelExMapper {

    /**
     * 根据车型查询
     * @param modelName
     * @return
     */
    Integer queryCarModelByCarModelName(String modelName);

    /**
     * 查询所有车型
     * @return
     */
    List<CarBizModel> queryAllList();

    List<CarBizModel> findByIdSet(Set<Integer> carModelIdSet);
}