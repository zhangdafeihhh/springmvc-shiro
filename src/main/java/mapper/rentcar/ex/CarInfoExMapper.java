package mapper.rentcar.ex;

import com.zhuanche.entity.rentcar.CarInfo;
import com.zhuanche.entity.rentcar.CarInfoVo;

import java.util.List;
import java.util.Map;

public interface CarInfoExMapper {
     List<CarInfoVo> selectcarnum(Map<String, Object> params);

    //通过车型名称 查询车型ID
     Integer getGroupId(CarInfo carInfoEntity);
    //根据车牌号更新
     int updateByLicence(CarInfo carInfoEntity);
    //查所有车辆信息  有分页
     List<CarInfo> selectList(CarInfo  params);
    //查所有车辆信息  无分页
//     List<CarInfo> selectListNoPage(CarInfo  params);
    //查所有车辆信息的数量
//     int selectListCount(CarInfo carInfoEntity);
    //查询车辆详情信息----车牌号
     CarInfo selectCarInfoByLicensePlates(CarInfo carInfoEntity);
    //查询车辆详情信息----车辆id
     CarInfo selectCarInfoByCarId(CarInfo carInfoEntity);
    //根据车牌号查询是否已存在
     int checkLicensePlates(String params);

    /**
     * 司机置为无效后，根据车牌号更新车辆信息
     * @param params
     * @return
     */
     int updatedriver(Map<String, Object> params);

     String selectCarByCarId(Integer params);

     String selectModelNameByLicensePlates(String params);
}
