package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.CarBizDriverInfoTemp;

import java.util.List;

/**
 * @author wzq
 */
public interface CarBizDriverInfoTempExMapper {

    /**
     * 根据车牌号查询
     * @param licensePlates
     * @return
     */
    CarBizDriverInfoTemp getDriverByLincesePlates(String licensePlates);

    /**
     * 更新
     * @param driverVoEntity
     * @return
     */
    int update(CarBizDriverInfoTemp driverVoEntity);

    /**
     * 根据车牌号查询
     * @param params 车牌号
     * @return
     */
    int checkLicensePlates(String params);

    /**
     * 根据条件分页查询
     * @param driverVoEntity
     * @return
     */
    List<CarBizDriverInfoTemp> queryForPageObject(CarBizDriverInfoTemp driverVoEntity);
}