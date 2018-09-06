package com.zhuanche.serv.rentcar;

import com.zhuanche.entity.rentcar.DriverOutage;
import com.zhuanche.entity.rentcar.DriverOutageVo;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface DriverOutageService {
    /*
     * 查询列表，有分页
     */
     List<DriverOutage> queryForListObject(DriverOutage params);
    /*
     * 查询数量
     */
     int queryForInt(DriverOutage params);
    /*
     * 查询单个
     */
     DriverOutage queryForObject(DriverOutage params);
    /*
     * 查询列表，无分页
     */
     List<DriverOutage> queryForListObjectNoLimit(DriverOutage params);
    /*
     * 导出
     */
     Workbook exportExcelDriverOutage(List<DriverOutage> list , String path) throws Exception;
    /*
     * 保存
     */
     Map<String,Object> saveDriverOutage(DriverOutage params);
    /*
     * 检查给定时间是否可以设置
     */
     Map<String,Object> checkDriverOutageStartDate(DriverOutage params);
    /*
     * 根据手机号查询司机名字
     */
     DriverOutage queryDriverNameByPhone(DriverOutage params);
    /*
     * 解除
     */
     Map<String,Object> updateDriverOutage(DriverOutage params);
    /*
     * 批量解除
     */
     Map<String,Object> updateDriverOutages(DriverOutage params);

    //永久停运
    /*
     * 查询列表，有分页
     */
     List<DriverOutage> queryAllForListObject(DriverOutage params);
    /*
     * 查询数量
     */
     int queryAllForInt(DriverOutage params);
    //根据司机id，查询司机临时停运，正在执行或者待执行的数据
     List<DriverOutage> queryDriverOutageByDriverId(DriverOutage params);
    //根据司机id，查询司机永久停运，启用的数据
     DriverOutage queryDriverOutageAllByDriverId(DriverOutage params);
    /*
     * 保存
     */
     Map<String,Object> saveDriverOutageAll(DriverOutage params);

     Map<String,Object> updateDriverOutagesAll(DriverOutage params);

    /*
     * 导入
     */
     Map<String,Object> importDriverOutageInfo(String fileName, MultipartFile file, HttpServletRequest request);
}
