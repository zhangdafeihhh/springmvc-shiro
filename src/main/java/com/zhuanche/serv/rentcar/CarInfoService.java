package com.zhuanche.serv.rentcar;

import com.zhuanche.entity.rentcar.CarInfo;
import com.zhuanche.entity.rentcar.CarInfoVo;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface CarInfoService {
    //查询车辆车牌号
    List<CarInfoVo> selectcarnum(Map<String, Object> map);


    //查询车辆列表  有分页
    List<CarInfo> selectList(CarInfo params);

    //查询车辆列表中的司机数量
    int selectListCount(CarInfo params);

    //查询车辆详情信息
    CarInfo selectCarInfoByLicensePlates(CarInfo params);

    CarInfo selectCarInfoByCarId(CarInfo params);

    String selectCarByCarId(Integer params);

    /*
     * 车辆导入
     */
    Map<String,Object> importCarInfo(String fileName, HttpServletRequest request);

    //根据车牌号查询是否已存在
    Map<String, Object> checkLicensePlates(CarInfo params);

    /*
     * 车辆新增/修改
     */
    Map<String,Object> saveCarInfo(CarInfo params);

    /*
     * 导出车辆信息操作
     */
    Workbook exportExcel(CarInfo params, String path) throws Exception;

    /*
     * 车辆导入删除
     */
    Map<String,Object> importDeleteCarInfo(CarInfo params,HttpServletRequest request);

    String selectModelNameByLicensePlates(String params);
}
