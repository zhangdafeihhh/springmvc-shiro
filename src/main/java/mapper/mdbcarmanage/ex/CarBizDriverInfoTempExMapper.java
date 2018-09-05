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

    /**
     * 根据司机Id查询
     * @param driverVoEntity
     * @return
     */
    CarBizDriverInfoTemp queryForObject(CarBizDriverInfoTemp driverVoEntity);

    /**
     * 删除
     * @param driverVoEntity
     * @return
     */
    int delete(CarBizDriverInfoTemp driverVoEntity);

    /**
     * 验证车牌号
     * @param driverVoEntity
     * @return
     */
    Integer validateLicensePlates(CarBizDriverInfoTemp driverVoEntity);

    /**
     * 查询车牌号所在城市，厂商=============（验证车牌号之后在执行此方法）
     * @param driverVoEntity
     * @return
     */
    Integer validateCityAndSupplier(CarBizDriverInfoTemp driverVoEntity);

    /**
     * 根据身份证号检查司机是否存在
     * @param carBizDriverInfoTemp
     * @return
     */
    Integer checkIdCardNo(CarBizDriverInfoTemp carBizDriverInfoTemp);

    /**
     *根据手机号查询
     * @param carBizDriverInfoTemp
     * @return
     */
    Integer selectCountForPhone(CarBizDriverInfoTemp carBizDriverInfoTemp);

    /**
     * 新增
     * @param entity
     * @return
     */
    Integer save(CarBizDriverInfoTemp entity);

    /**
     * 验证银行卡卡号
     * @param carBizDriverInfoTemp
     * @return
     */
    int validateBankCardNumber(CarBizDriverInfoTemp carBizDriverInfoTemp);
}