package mapper.rentcar.ex;

import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.dto.CarDriverInfoDTO;
import com.zhuanche.request.DriverTeamRequest;
import com.zhuanche.request.DutyParamRequest;
import com.zhuanche.dto.rentcar.CarBizDriverInfoDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CarBizDriverInfoExMapper {

    List<CarDriverInfoDTO> selectDriverList(DriverTeamRequest driverTeamRequest);

    List<CarDriverInfoDTO> queryListByLimits(DriverTeamRequest driverTeamRequest);

    CarDriverInfoDTO queryOneDriver(DutyParamRequest dutyParamRequest);
    /**
     * 查询司机信息列表展示(有分页)
     * @param params
     * @return
     */
    List<CarBizDriverInfoDTO> queryDriverList(CarBizDriverInfoDTO params);

    /**
     * 查询手机号是否存在
     * @param phone 手机号，必传
     * @param driverId 司机ID，可不传
     * @return
     */
    int checkPhone(@Param("phone") String phone, @Param("driverId") Integer driverId);

    /**
     * 查询身份证是否存在
     * @param idCardNo 身份证号
     * @param driverId 司机ID，可不传
     * @return
     */
    int checkIdCardNo(@Param("idCardNo") String idCardNo, @Param("driverId") Integer driverId);

    int insertCarBizDriverInfoDTO(CarBizDriverInfoDTO record);

    int updateCarBizDriverInfoDTO(CarBizDriverInfoDTO record);

    /**
     * 司机置为无效
     * @param driverId
     * @return
     */
    int updateDriverByXiao(@Param("driverId") Integer driverId);

    /**
     * 重置imei
     * @param driverId
     * @return
     */
    int resetIMEI(@Param("driverId") Integer driverId);

    /**
     * 根据车牌号查询
     * @param licensePlates
     * @return
     */
    Integer checkLicensePlates(@Param("licensePlates") String licensePlates);

    /**
     * 解绑司机信用卡，更新
     * @param map
     */
    int updateDriverCardInfo(Map<String, Object> map);

    /**
     * 更新uickpay_customerid
     * @param map
     */
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.MASTER)
    })
    void updateDriverCustomerId(Map<String, Object> map);
}