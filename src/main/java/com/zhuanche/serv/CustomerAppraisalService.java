package com.zhuanche.serv;

import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalDTO;
import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalStatisticsDTO;
import com.zhuanche.serv.driverteam.CarDriverTeamService;
import mapper.rentcar.ex.CarBizCustomerAppraisalExMapper;
import mapper.rentcar.ex.CarBizCustomerAppraisalStatisticsExMapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

@Service
public class CustomerAppraisalService {

    @Autowired
    private CarBizCustomerAppraisalExMapper carBizCustomerAppraisalExMapper;

    @Autowired
    private CarBizCustomerAppraisalStatisticsExMapper carBizCustomerAppraisalStatisticsExMapper;

    @Autowired
    private CarDriverTeamService carDriverTeamService;

    /**
     * 查询订单评分信息
     * @param carBizCustomerAppraisalDTO
     * @return
     */
    public List<CarBizCustomerAppraisalDTO> queryCustomerAppraisalList(CarBizCustomerAppraisalDTO carBizCustomerAppraisalDTO) {
        return carBizCustomerAppraisalExMapper.queryCustomerAppraisalList(carBizCustomerAppraisalDTO);
    }

    /**
     * 查询司机评分
     * @param carBizCustomerAppraisalStatisticsDTO
     * @return
     */
    public List<CarBizCustomerAppraisalStatisticsDTO> queryCustomerAppraisalStatisticsList(CarBizCustomerAppraisalStatisticsDTO carBizCustomerAppraisalStatisticsDTO) {
        return carBizCustomerAppraisalStatisticsExMapper.queryCustomerAppraisalStatisticsList(carBizCustomerAppraisalStatisticsDTO);
    }

    /**
     * 查询订单评分信息
     * @param carBizCustomerAppraisalDTO
     * @return
     */
    public List<CarBizCustomerAppraisalDTO> queryDriverAppraisalDetail(CarBizCustomerAppraisalDTO carBizCustomerAppraisalDTO) {
        return carBizCustomerAppraisalExMapper.queryDriverAppraisalDetail(carBizCustomerAppraisalDTO);
    }

    /**
     * 导出司机评分
     * @param list
     * @param path
     * @return
     * @throws Exception
     */
    public Workbook exportExcelDriverAppraisal(List<CarBizCustomerAppraisalStatisticsDTO> list, String path) throws Exception {
        FileInputStream io = new FileInputStream(path);
        // 内存缓存最大行数
        int rowMaxCache = 100;
        // 使用SXSSFWorkbook解决OOM问题
        SXSSFWorkbook wb = new SXSSFWorkbook(new XSSFWorkbook(io),rowMaxCache);
        if(list != null && list.size()>0){
            Sheet sheet = wb.getSheetAt(0);
            Cell cell = null;
            int i=0;

            Map<Integer, String> teamMap = null;
            try {
                String driverIds = this.pingDriverIds(list);
                teamMap = carDriverTeamService.queryDriverTeamListByDriverId(driverIds);
            } catch (Exception e) {
                e.printStackTrace();
            }

            for(CarBizCustomerAppraisalStatisticsDTO s:list){
                Row row = sheet.createRow(i + 1);
                cell = row.createCell(0);
                cell.setCellValue(s.getDriverName());

                cell = row.createCell(1);
                cell.setCellValue(s.getDriverPhone());

                cell = row.createCell(2);
                cell.setCellValue(s.getCreateDate());

                cell = row.createCell(3);
                cell.setCellValue(s.getEvaluateScore());

                cell = row.createCell(4);
                cell.setCellValue(s.getIdCardNo());

                cell = row.createCell(5);
                String teamName = "";
                if(teamMap!=null){
                    teamName = teamMap.get(s.getDriverId());
                }
                cell.setCellValue(teamName);

                i++;
            }
        }
        return wb;
    }

    public static String pingDriverIds(List<CarBizCustomerAppraisalStatisticsDTO> list) {
        String driverId = "";
        if(list!=null&&list.size()>0){
            int j=0;
            for(int i=0;i<list.size();i++){
                if(!"".equals(list.get(i))&&list.get(i)!=null&&!"".equals(list.get(i).getDriverId())&&list.get(i).getDriverId()!=null){
                    if(j==0){
                        driverId = "'"+list.get(i).getDriverId()+"'";
                    }else{
                        driverId +=",'"+list.get(i).getDriverId()+"'";
                    }
                    j++	;
                }
            }
        }
        return driverId;
    }
}
