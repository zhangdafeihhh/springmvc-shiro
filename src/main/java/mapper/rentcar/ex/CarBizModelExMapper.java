package mapper.rentcar.ex;

import com.zhuanche.entity.rentcar.CarBizModel;

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
}