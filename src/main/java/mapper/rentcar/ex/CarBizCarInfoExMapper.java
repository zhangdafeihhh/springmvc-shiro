package mapper.rentcar.ex;


import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.dto.rentcar.CarBizCarInfoDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author wzq
 */
public interface CarBizCarInfoExMapper {
    /**
     * 根据车牌号查询
     * @param licensePlates
     * @return
     */
    Integer checkLicensePlates(String licensePlates);

    /**
     * 根据车牌号查询车型信息
     * @param licensePlates
     * @return
     */
    CarBizCarInfoDTO selectModelByLicensePlates(@Param("licensePlates")  String licensePlates);

    /**
     * 更新 车辆信息
     * @param licensePlates
     * @return
     */
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.MASTER ),
    } )
    int updateCarLicensePlates(@Param("licensePlates") String licensePlates, @Param("driverId") Integer driverId);

    /**
     * 查询未绑定车牌号
     * @param map
     * @return
     */
    List<CarBizCarInfoDTO> licensePlatesNotDriverIdList(Map<String, Object> map);
}