package mapper.driverOrderRecord;
import java.util.List;
import java.util.Map;

import com.zhuanche.entity.driverOrderRecord.OrderTimeEntity;

public interface DriverOrderRecordMapper {
	public List<OrderTimeEntity> queryDriverOrderRecord(Map<String,String> p);
}