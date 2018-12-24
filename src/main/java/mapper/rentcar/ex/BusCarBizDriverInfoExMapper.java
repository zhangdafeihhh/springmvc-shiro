package mapper.rentcar.ex;

import com.zhuanche.entity.rentcar.CarBizDriverInfo;

public interface BusCarBizDriverInfoExMapper {

	/**
	 * @Title: queryDriverSimpleInfoById
	 * @Description: 根据司机ID查询简单司机信息
	 * @param orderId
	 * @return 
	 * @return CarBizDriverInfo
	 * @throws
	 */
	CarBizDriverInfo queryDriverSimpleInfoById(Integer driverId);


}