package mapper.mdbcarmanage.ex;

import java.util.List;
import java.util.Map;

import com.zhuanche.entity.mdbcarmanage.BusBizChangeLog;

/**
 * @ClassName: BusBizChangeLogExMapper
 * @Description: 操作日志
 * @author: yanyunpeng
 * @date: 2018年12月6日 下午3:39:55
 * 
 */
public interface BusBizChangeLogExMapper {
	
	/**
	 * @ClassName: BusinessType
	 * @Description: 业务类型
	 * @author: yanyunpeng
	 * @date: 2018年12月6日 下午3:39:53
	 * 
	 */
	enum BusinessType {
		SUPPLIER(), DRIVER(), CAR();
		
	}

	/**
	 * @Title: queryRecnetlyChangeLogs
	 * @Description: 查询最近的操作记录
	 * @param param
	 * @return 
	 * @return List<BusBizChangeLog>
	 * @throws
	 */
	List<BusBizChangeLog> queryRecnetlyChangeLogs(Map<String, Object> param);
	
}