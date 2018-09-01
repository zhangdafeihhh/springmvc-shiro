package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.CarBizCarInfoTemp;
import com.zhuanche.entity.mdbcarmanage.DriverVoEntity;

import java.util.List;
import java.util.Map;

/**
 * @author wzq
 */
public interface CarBizDriverInfoTempExMapper {

    /**
     * 根据车牌号查询
     * @param licensePlates
     * @return
     */
    DriverVoEntity getDriverByLincesePlates(String licensePlates);

    /**
     * 更新
     * @param driverVoEntity
     * @return
     */
    int update(DriverVoEntity driverVoEntity);

    /**
     * 根据车牌号查询
     * @param params 车牌号
     * @return
     */
    int checkLicensePlates(String params);
}