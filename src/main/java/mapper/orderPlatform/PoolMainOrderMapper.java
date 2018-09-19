package mapper.orderPlatform;
import com.zhuanche.dto.rentcar.CarPoolMainOrderDTO;

public interface PoolMainOrderMapper {
	   /**
     * 
     */
    public CarPoolMainOrderDTO queryCarpoolMainForObject(CarPoolMainOrderDTO params);
}