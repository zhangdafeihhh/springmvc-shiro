package mapper.rentcar.ex;


import com.zhuanche.dto.busManage.BusInfoDTO;
import com.zhuanche.entity.busManage.BusCarInfo;
import com.zhuanche.vo.busManage.BusDetailVO;
import com.zhuanche.vo.busManage.BusInfoVO;

import java.util.List;

/**
 * @author wzq
 */
public interface BusInfoExMapper {
    /*查询车辆列表*/
    List<BusInfoVO> selectList(BusInfoDTO busInfoDTO);
    /*查询车辆详情*/
    BusDetailVO selectCarByCarId(Integer carId);
    /*新增车辆信息*/
    int insertCar(BusCarInfo carInfo);
    /*修改车辆信息*/
    int updateCarById(BusCarInfo carInfo);
    /*判断车牌号是否存在*/
    int countLicensePlates(String licensePlates);
    /*通过车辆ID获取车牌号*/
    String getLicensePlatesByCarId(Integer carId);


}