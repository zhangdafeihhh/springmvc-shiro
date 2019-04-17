package mapper.driver.ex;

import org.apache.ibatis.annotations.Param;

import mapper.driver.DriverCertMapper;

public interface DriverCertExMapper extends DriverCertMapper{

	/**查询司机证件照片通过司机ID和证件照片类型**/
	String queryImageByDriverIdAndType(@Param("driverId")Long driverId, @Param("type")Integer type);
	String queryImageByDriverIdAndTypeNew(@Param("driverId")Long driverId, @Param("type")Integer type);

}