package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.CarBizCarInfoTemp;

import java.util.List;
import java.util.Map;

public interface CarBizCarInfoTempExMapper {
    /**
     * 供应商车辆分页查询
     * @param params 查询条件
     * @return
     */
    List<CarBizCarInfoTemp> queryForPageObject(Map<String,Object> params);

    /**
     *供应商车辆查询详情
     * @param params
     * @return
     */
    CarBizCarInfoTemp queryForObject(Map<String,Object> params);
}