package mapper.rentcar.ex;


import com.zhuanche.dto.rentcar.BusInfoDTO;
import com.zhuanche.entity.rentcar.CarBizCarInfo;
import com.zhuanche.vo.rentcar.BusDetailVO;
import com.zhuanche.vo.rentcar.BusInfoVO;

import java.util.List;

/**
 * @author wzq
 */
public interface BusInfoExMapper {
    List<BusInfoVO> selectList(BusInfoDTO busInfoDTO);
    BusDetailVO selectCarByCarId(Integer carId);
    Integer insertCar(CarBizCarInfo carInfo);
    int countLicensePlates(String licensePlates);
    String getLicensePlatesByCarId(Integer carId);
}