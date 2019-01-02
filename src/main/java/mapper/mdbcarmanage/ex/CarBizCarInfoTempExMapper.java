package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.CarBizCarInfoTemp;
import com.zhuanche.entity.mdbcarmanage.CarBizDriverInfoTemp;

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

    /**
     * 根据车牌查询车辆
     * @param licensePlates
     * @return
     */
    int checkLicensePlates(String licensePlates);

    /**
     *根据车牌号更新司机Id
     * @param car
     * @return
     */
    int updateByLicensePlates(CarBizCarInfoTemp car);

    /**
     * 根据车牌号查询
     * @param params
     * @return
     */
    CarBizCarInfoTemp selectBylicensePlates(Map<String,Object> params);

    /**
     * 查询未绑定车牌号
     * @param map
     * @return
     */
    List<CarBizCarInfoTemp> licensePlatesNotDriverIdList(Map<String,Object> map);

    void updateDriverCooperationTypeBySupplierId(Integer supplierId, Integer cooperationType);
}