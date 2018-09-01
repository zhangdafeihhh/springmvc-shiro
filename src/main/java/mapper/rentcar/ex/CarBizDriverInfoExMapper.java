package mapper.rentcar.ex;

import com.zhuanche.dto.rentcar.CarBizDriverInfoDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarBizDriverInfoExMapper {

    /**
     * 查询司机信息列表展示(有分页)
     * @param params
     * @return
     */
    List<CarBizDriverInfoDTO> queryDriverList(CarBizDriverInfoDTO params);

    /**
     * 查询司机信息列表展示(无分页)
     * @param params
     * @return
     */
    List<CarBizDriverInfoDTO> queryDriverListNoLimit(CarBizDriverInfoDTO params);

    /**
     * 查询手机号是否存在
     * @param phone 手机号，必传
     * @param driverId 司机ID，可不传
     * @return
     */
    int checkPhone(@Param("phone") String phone, @Param("driverId") Integer driverId);

    /**
     * 查询身份证是否存在
     * @param idCardNo 身份证号
     * @param driverId 司机ID，可不传
     * @return
     */
    int checkIdCardNo(@Param("idCardNo") String idCardNo, @Param("driverId") Integer driverId);

    int insertCarBizDriverInfoDTO(CarBizDriverInfoDTO record);

    int updateCarBizDriverInfoDTO(CarBizDriverInfoDTO record);

    /**
     * 司机置为无效
     * @param driverId
     * @return
     */
    int updateDriverByXiao(@Param("driverId") Integer driverId);

    /**
     * 重置imei
     * @param driverId
     * @return
     */
    public int resetIMEI(Integer driverId);
}