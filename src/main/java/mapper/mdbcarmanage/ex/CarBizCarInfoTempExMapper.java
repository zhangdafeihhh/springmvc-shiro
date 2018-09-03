package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.CarBizCarInfoTemp;

import java.util.List;
import java.util.Map;

public interface CarBizCarInfoTempExMapper {
    /**
     * 分页查询
     * @param params 查询条件
     * @return
     */
    List<CarBizCarInfoTemp> queryForPageObject(Map<String,Object> params);

    /**
     *查询详情
     * @param params
     * @return
     */
    CarBizCarInfoTemp queryForObject(Map<String,Object> params);
}