package mapper.orderPlatform;
import com.zhuanche.dto.rentcar.CarPoolMainOrderDTO;

public interface PoolMainOrderMapper {
	   /**
     * 
     */
    CarPoolMainOrderDTO queryCarpoolMainForObject(CarPoolMainOrderDTO params);
}