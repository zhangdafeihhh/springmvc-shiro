package mapper.rentcar.ex;

import org.apache.ibatis.annotations.Param;

public interface CarBizDriverInfoDetailExMapper {

    /**
     * 检查银行卡号是否存在
     * @param bankCardBank
     * @param driverId
     * @return
     */
    int checkBankCardBank(@Param("bankCardBank") String bankCardBank, @Param("driverId") Integer driverId);

}