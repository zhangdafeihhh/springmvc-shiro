package mapper.rentcar.ex;

import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.dto.CarDriverInfoDTO;
import com.zhuanche.dto.driver.DriverVoEntity;
import com.zhuanche.entity.mdbcarmanage.CarBizDriverInfoTemp;
import com.zhuanche.entity.rentcar.CarBizDriverInfo;
import com.zhuanche.request.DriverMonthDutyRequest;
import com.zhuanche.request.DriverTeamRequest;
import com.zhuanche.request.DutyParamRequest;
import com.zhuanche.dto.rentcar.CarBizDriverInfoDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CarBizDriverInfoExMapper {

    List<CarDriverInfoDTO> selectDriverList(DriverTeamRequest driverTeamRequest);

    List<CarDriverInfoDTO> queryListByLimits(DriverTeamRequest driverTeamRequest);

    CarDriverInfoDTO queryOneDriver(DutyParamRequest dutyParamRequest);

    List<CarDriverInfoDTO> queryListDriverByDriverIds(@Param("set") Set<Integer> driverIds);

    /** 更改车队信息查询司机信息*/
    CarDriverInfoDTO selectDriverInfoByDriverId(Integer driverId);

    /** 月排班查询司机详情*/
    CarDriverInfoDTO selectDriverDetail(String driverId);

    /** 司机月排班查询司机信息列表*/
    List<CarDriverInfoDTO> queryDriverListForMonthDuty(DriverMonthDutyRequest param);
    /**
     * 查询司机信息列表展示
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

    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.MASTER)
    })
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
     * 根据身份证号检查司机是否存在
     * @param carBizDriverInfoTemp
     * @return
     */
    Integer checkIdCardNoNew(CarBizDriverInfoTemp carBizDriverInfoTemp);

    /**
     * 检测司机手机号是否存在
     * @param carBizDriverInfo
     * @return
     */
    Integer selectCountForPhone(CarBizDriverInfo carBizDriverInfo);

    /**
     * 验证银行卡卡号
     * @param carBizDriverInfoTemp
     * @return
     */
    Integer validateBankCardNumber(CarBizDriverInfoTemp carBizDriverInfoTemp);

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
	/**
	 * 
	 * @param params
	 * @return
	 */
	public CarBizDriverInfoDTO querySupplierIdAndNameByDriverId(Integer params);

    /**
     * 根据车牌号查询司机信息
     * @param license_plates
     * @return
     */
    public List<CarBizDriverInfoDTO> queryDriverByLicensePlates(String license_plates);


    /**
     * 查看司机列表
     * @param driverInfoDTO
     * @return
     */
    List<CarBizDriverInfoDTO> queryCarBizDriverList(CarBizDriverInfoDTO driverInfoDTO);

    int selectDriverByKeyCountAddCooperation(DriverVoEntity params);

    public List<DriverVoEntity> selectDriverByKeyAddCooperation(DriverVoEntity params);


    CarBizDriverInfoDTO selectByPhone(@Param("phone") String phone);

	/**
	 * @Title: updateDriverCooperationTypeBySupplierId
	 * @Description: 更新司机的加盟类型
	 * @param map 
	 * @return void
	 * @throws
	 */
	void updateDriverCooperationTypeBySupplierId(Map<String, Object> map);


}