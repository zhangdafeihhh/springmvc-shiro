package mapper.driver.ex;

import com.zhuanche.entity.driver.CarBizDriverInfo;
import com.zhuanche.entity.mdbcarmanage.CarBizDriverInfoTemp;

public interface CarBizDriverInfoExMapper {
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
}