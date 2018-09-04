package com.zhuanche.serv.statisticalAnalysis.impl;

import java.io.FileInputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.zhuanche.dto.rentcar.CancelOrderDTO;
import com.zhuanche.dto.rentcar.DriverEvaluateDTO;
import com.zhuanche.serv.statisticalAnalysis.DriverEvaluateService;
@Service
public class DriverEvaluateServiceImpl implements DriverEvaluateService {
	private static final Logger logger = LoggerFactory.getLogger(DriverEvaluateServiceImpl.class);
	
	
	@Override
	public Workbook exportExcelDriverEvaluate(List<DriverEvaluateDTO> list, String path) throws Exception {
		FileInputStream io = new FileInputStream(path);
        // 创建 excel
        Workbook wb = new XSSFWorkbook(io);	
        if (list != null && list.size() > 0) {
            Sheet sheet = null;
            try {
                sheet = wb.getSheetAt(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Cell cell = null;
            int i = 0;
            for (DriverEvaluateDTO s : list) {
                Row row = sheet.createRow(i + 1);
                // 司机ID
                cell = row.createCell(0);
                cell.setCellValue(s.getDriverId() != null ? ""
                        + s.getDriverId() + "" : "");
                // 司机姓名
                cell = row.createCell(1);
                cell.setCellValue(s.getDriverName() != null ? ""
                        + s.getDriverName() + "" : "");
                // 车牌号
                cell = row.createCell(2);
                cell.setCellValue(s.getVehiclePlateNo() != null ? ""
                        + s.getVehiclePlateNo() + "" : "");
                //司机评分
                cell = row.createCell(3);
                cell.setCellValue(s.getDriverScore() != null ? ""
                        + s.getDriverScore() + "" : "");
                // 司机所属城市名称
                cell = row.createCell(4);
                cell.setCellValue(s.getDriverCityName() != null ? ""
                        + s.getDriverCityName() + "" : "");
                // 评价时间
                cell = row.createCell(5);
                cell.setCellValue(s.getEvaluateTime() != null ? ""
                        + s.getEvaluateTime() + "" : "");
                // 司机类型
                cell = row.createCell(6);
                cell.setCellValue(s.getDriverTypeName() != null ? ""
                        + s.getDriverTypeName() + "" : "");
                // 加盟商名称
                cell = row.createCell(7);
                cell.setCellValue(s.getAllianceName() != null ? ""
                        + s.getAllianceName() + "" : "");
                // 车队名称
                cell = row.createCell(8);
                cell.setCellValue(s.getMotorcardName() != null ? ""
                        + s.getMotorcardName() + "" : "");
                // 订单号
                cell = row.createCell(9);
                cell.setCellValue(s.getOrderNo() != null ? ""
                        + s.getOrderNo() + "" : "");
                // 司机评价内容
                cell = row.createCell(10);
                cell.setCellValue(s.getDriverEvaluateText() != null ? ""
                        + s.getDriverEvaluateText() + "" : "");
                i++;
            }
        }
        return wb;
	}
}
