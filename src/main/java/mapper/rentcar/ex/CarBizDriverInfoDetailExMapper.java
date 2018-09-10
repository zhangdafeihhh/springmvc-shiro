package mapper.rentcar.ex;

import com.zhuanche.dto.rentcar.CarBizDriverInfoDetailDTO;
import org.apache.ibatis.annotations.Param;

public interface CarBizDriverInfoDetailExMapper {

    /**
     * 检查银行卡号是否存在
     * @param bankCardNumber
     * @param driverId
     * @return
     */
    int checkBankCardBank(@Param("bankCardNumber") String bankCardNumber, @Param("driverId") Integer driverId);

    /**
     * 根据司机ID查询司机扩展表
     * @param driverId
     * @return
     */
    CarBizDriverInfoDetailDTO selectByDriverId(Integer driverId);
}