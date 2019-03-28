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
		/** 供应商 **/
		SUPPLIER(100, "supplierId"),
		/** 司机 **/
		DRIVER(200, "driverId"),
		/** 车辆 **/
		CAR(300, "carId"),
		/** 巴士违规处罚 **/
		BUS_PUNISH(400, "violatorId");

		/** 业务类型 **/
		private Integer businessType;
		/** 业务主键描述(备注) **/
		private String businessKeyNote;

		private BusinessType(Integer businessType, String businessKeyNote) {
			this.businessType = businessType;
			this.businessKeyNote = businessKeyNote;
		}
		public Integer businessType() {
			return businessType;
		}
		public String businessKeyNote() {
			return businessKeyNote;
		}
		/** 业务类型是否存在 **/
		public static boolean isExist(Integer businessType) {
			for (BusinessType type : values()) {
				if (type.businessType.equals(businessType)) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * @Title: queryRecnetlyChangeLogs
	 * @Description: 查询最近的操作记录
	 * @param param
	 * @return 
	 * @return List<BusBizChangeLog>
	 * @throws
	 */
	List<Map<Object, Object>> queryRecnetlyChangeLogs(Map<String, Object> param);

	/**
	 * @Title: insertLog
	 * @Description: 保存操作
	 * @param log
	 * @return 
	 * @return int
	 * @throws
	 */
	int insertLog(BusBizChangeLog log);
	
}