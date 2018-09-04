package com.zhuanche.serv.statisticalAnalysis.impl;

import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.zhuanche.dto.rentcar.CancelOrderDTO;
import com.zhuanche.serv.statisticalAnalysis.CancelOrderService;
@Service
public class CancelOrderServiceImpl implements CancelOrderService {
	private static final Logger logger = LoggerFactory.getLogger(CancelOrderServiceImpl.class);
	
	
	@Override
	public Workbook exportExcelCancelOrder(List<CancelOrderDTO> list, String path) throws Exception {
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
            for (CancelOrderDTO s : list) {
                Row row = sheet.createRow(i + 1);
                // 下单城市
                cell = row.createCell(0);
                cell.setCellValue(s.getOrderCityName() != null ? ""
                        + s.getOrderCityName() + "" : "");
                // 创建时间
                cell = row.createCell(1);
                cell.setCellValue(s.getCreateTime() != null ? ""
                        + s.getCreateTime() + "" : "");
                // 预计乘车时间
                cell = row.createCell(2);
                cell.setCellValue(s.getPlanOnboardTime() != null ? ""
                        + s.getPlanOnboardTime() + "" : "");
                // 司机出发时间
                cell = row.createCell(3);
                cell.setCellValue(s.getDriverStartTime() != null ? ""
                        + s.getDriverStartTime() + "" : "");
                // 取消时间
                cell = row.createCell(4);
                cell.setCellValue(s.getCancelTime() != null ? ""
                        + s.getCancelTime() + "" : "");
                // 取消时长
                cell = row.createCell(5);
                cell.setCellValue(s.getCancelDuration() != null ? ""
                        + s.getCancelDuration() + "" : "");

                // 订单号
                cell = row.createCell(6);
                cell.setCellValue(s.getOrderNo() != null ? ""
                        + s.getOrderNo() + "" : "");
                // 司机ID
                cell = row.createCell(7);
                cell.setCellValue(s.getDriverId() != null ? ""
                        + s.getDriverId() + "" : "");
                // 司机类型
                cell = row.createCell(8);
                cell.setCellValue(s.getDriverTypeName() != null ? ""
                        + s.getDriverTypeName() + "" : "");
                // 加盟商名称
                cell = row.createCell(9);
                cell.setCellValue(s.getAllianceName() != null ? ""
                        + s.getAllianceName() + "" : "");
                // 司机姓名
                cell = row.createCell(10);
                cell.setCellValue(s.getDriverName() != null ? ""
                        + s.getDriverName() + "" : "");
                // 订车人ID
                cell = row.createCell(11);
                cell.setCellValue(s.getCustomerId() != null ? ""
                        + s.getCustomerId() + "" : "");
                // 预估金额
                cell = row.createCell(12);
                cell.setCellValue(s.getPredictPrice() != null ? ""
                        + s.getPredictPrice() + "" : "");
                // 下单渠道名称
                cell = row.createCell(13);
                cell.setCellValue(s.getChannelName() != null ? ""
                        + s.getChannelName() + "" : "");
                // 产品类型名称
                cell = row.createCell(14);
                cell.setCellValue(s.getProductTypeName() != null ? ""
                        + s.getProductTypeName() + "" : "");
                // 预定车型名称
                cell = row.createCell(15);
                cell.setCellValue(s.getOrderVehicleTypeName() != null ? ""
                        + s.getOrderVehicleTypeName() + "" : "");
                i++;
            }
        }
        return wb;
	}
}
