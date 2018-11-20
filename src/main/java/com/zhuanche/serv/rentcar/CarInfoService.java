package com.zhuanche.serv.rentcar;

import com.github.pagehelper.PageInfo;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.entity.rentcar.CarInfo;
import com.zhuanche.entity.rentcar.CarInfoVo;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface CarInfoService {
    //查询车辆车牌号
    List<CarInfoVo> selectcarnum(Map<String, Object> map);


    //查询车辆列表  有分页
    public PageInfo<CarInfo> findPageByCarInfo(CarInfo params,int pageNo,int pageSize);




    //查询车辆详情信息
    CarInfo selectCarInfoByLicensePlates(CarInfo params);

    CarInfo selectCarInfoByCarId(CarInfo params);

    String selectCarByCarId(Integer params);

    /*
     * 车辆导入
     */
    AjaxResponse importCarInfo(MultipartFile fileName, HttpServletRequest request);

    //根据车牌号查询是否已存在
    boolean checkLicensePlates(CarInfo params);

    /*
     * 车辆新增/修改
     */
    Map<String,Object> saveCarInfo(CarInfo params);

    /*
     * 导出车辆信息操作
     */
//    Workbook exportExcel(CarInfo params, String path) throws Exception;

    /*
     * 车辆导入删除
     */
    Map<String,Object> importDeleteCarInfo(CarInfo params,HttpServletRequest request);

    String selectModelNameByLicensePlates(String params);

//    void getExportExcel(CarInfo params, List<String> datas);


//    public PageInfo<CarInfo> findCarInfo(CarInfo params);

    public void doTrans4Csv(List<String> csvList,List<CarInfo> carInfoList);

    void transContent(List<CarInfo> list);
}
