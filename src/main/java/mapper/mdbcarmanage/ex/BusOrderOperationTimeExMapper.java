package mapper.mdbcarmanage.ex;

import java.util.List;

import com.zhuanche.entity.mdbcarmanage.BusOrderOperationTime;

public interface BusOrderOperationTimeExMapper {

	List<BusOrderOperationTime> queryOperationByOrderId(Integer orderId);
	
}