package mapper.orderPlatform.ex;
import com.zhuanche.dto.rentcar.CarPoolMainOrderDTO;

import java.util.List;

public interface PoolMainOrderExMapper {

    /**
     * 查询列表
     * @param params
     * @return
     */
     List<CarPoolMainOrderDTO> queryCarpoolMainList(CarPoolMainOrderDTO params);
}