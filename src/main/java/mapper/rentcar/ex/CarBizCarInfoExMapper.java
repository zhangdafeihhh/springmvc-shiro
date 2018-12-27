package mapper.rentcar.ex;


import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.dto.busManage.BusCarRicherDTO;
import com.zhuanche.dto.rentcar.CarBizCarInfoDTO;
import com.zhuanche.entity.rentcar.CarBizCarInfo;

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

    /**
     * 根据城市ID，供应商Id,车牌号 查询是否存在
     * @param cityId
     * @param supplierId
     * @param licensePlates
     * @return
     */
    Integer validateCityAndSupplier(@Param("cityId") Integer cityId, @Param("supplierId") Integer supplierId, @Param("licensePlates") String licensePlates);


    List<CarBizCarInfo> selectByLicensePlates(@Param("license_platess") Set<String> license_platesList);
	/**
	 * @Title: queryBusCarList
	 * @Description: 查询巴士订单指派/改派可用车辆
	 * @param richerDTO
	 * @return List<Map<String,Object>>
	 * @throws
	 */
	List<Map<String, Object>> queryBusCarList(BusCarRicherDTO richerDTO);

	/**
	 * @Title: selectCarCitySupplierInfoByCarId
	 * @Description: 根据车辆ID查询其车辆及关联的城市、供应商信息
	 * @param carId
	 * @return CarBizCarInfoDTO
	 * @throws
	 */
	CarBizCarInfoDTO selectCarCitySupplierInfoByCarId(Integer carId);

	CarBizCarInfoDTO  selectBasicCarInfo(Integer carId);
}