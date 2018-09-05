package mapper.rentcar.ex;

import com.zhuanche.dto.rentcar.CarBizDriverAccountDTO;
import org.apache.ibatis.annotations.Param;

public interface CarBizDriverAccountExMapper {

    CarBizDriverAccountDTO selectDriverAcctount(@Param("driverId") Integer driverId);

    /*
     * 更新司机账户信息
     */
    void updateDrivetAccount(CarBizDriverAccountDTO carBizDriverAccountDTO);

    /*
     * 增加司机提成流水
     */
    void insertDriverAccountDetil(CarBizDriverAccountDTO carBizDriverAccountDTO);
}