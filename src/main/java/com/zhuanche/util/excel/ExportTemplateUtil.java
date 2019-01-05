package com.zhuanche.util.excel;


import com.alibaba.fastjson.JSON;
import com.zhuanche.util.dateUtil.DateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * @program: mp-manage
 * @description: 生成导出模板，包含下拉框
 *
 *
 * @author: niuzilian
 * @create: 2019-01-04 17:11
 **/
public class ExportTemplateUtil {
    /**
     * @param @param filePath  Excel文件路径
     * @param @param handers   Excel列标题(数组)
     * @param @param downData  下拉框数据(数组)
     * @param @param downRows  下拉列的序号(数组,序号从0开始)
     * @return void
     * @throws
     * @Title: createExcelTemplate
     * @Description: 生成Excel导入模板
     */
    public static Workbook createExcelTemplate(String[] handers,
                                                   List<String[]> downData, String[] downRows) {
       //创建工作薄
        XSSFWorkbook wb = new XSSFWorkbook();

        //表头样式
        XSSFCellStyle style = wb.createCellStyle();
            //背景色
        style.setFillPattern((IndexedColors.SKY_BLUE.getIndex()));
       // style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            // 创建一个居中格式
        style.setAlignment(HorizontalAlignment.CENTER);
            // 竖向居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);
             // 边框
        /*style.setBorderBottom(BorderStyle.THICK);
        style.setBorderLeft(BorderStyle.THICK);
        style.setBorderRight(BorderStyle.THICK);
        style.setBorderTop(BorderStyle.THICK);*/

        //字体样式
        XSSFFont fontStyle = wb.createFont();
        fontStyle.setFontName("微软雅黑");
        fontStyle.setFontHeightInPoints((short) 12);
        fontStyle.setColor(IndexedColors.VIOLET.getIndex());
        fontStyle.setBold(true);
        style.setFont(fontStyle);


        //新建sheet
        XSSFSheet sheet1 = wb.createSheet("Sheet1");
        sheet1.autoSizeColumn((short)0);
        XSSFSheet sheet2 = wb.createSheet("Sheet2");
        XSSFSheet sheet3 = wb.createSheet("Sheet3");



        //生成sheet1内容
        XSSFRow rowFirst = sheet1.createRow(0);//第一个sheet的第一行为标题
        //写标题
        for (int i = 0; i < handers.length; i++) {
            XSSFCell cell = rowFirst.createCell(i); //获取第一行的每个单元格
            sheet1.setColumnWidth(i, 4000); //设置每列的列宽
            cell.setCellStyle(style); //加样式
            cell.setCellValue(handers[i]); //往单元格里写数据
        }

        //设置下拉框数据
        String[] arr = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        int index = 0;
        XSSFRow row = null;
        for (int r = 0; r < downRows.length; r++) {
            String[] dlData = downData.get(r);//获取下拉对象
            int rownum = Integer.parseInt(downRows[r]);

            if (dlData.length < 50) { //50以内的下拉
                //50以内的下拉,参数分别是：作用的sheet、下拉内容数组、起始行、终止行、起始列、终止列
                sheet1.addValidationData(setDataValidation(sheet1, dlData, 1, 50000, rownum, rownum)); //超过255个报错
            } else { //50以上的下拉，即下拉列表元素很多的情况

                //1、设置有效性
                //String strFormula = "Sheet2!$A$1:$A$5000" ; //Sheet2第A1到A5000作为下拉列表来源数据
                String strFormula = "Sheet2!$" + arr[index] + "$1:$" + arr[index] + "$5000"; //Sheet2第A1到A5000作为下拉列表来源数据
                sheet2.setColumnWidth(r, 4000); //设置每列的列宽
                //设置数据有效性加载在哪个单元格上,参数分别是：从sheet2获取A1到A5000作为一个下拉的数据、起始行、终止行、起始列、终止列
                sheet1.addValidationData(SetDataValidation(sheet2,strFormula, 1, 5000, rownum, rownum)); //下拉列表元素很多的情况

                //2、生成sheet2内容
                for (int j = 0; j < dlData.length; j++) {
                    if (index == 0) { //第1个下拉选项，直接创建行、列
                        row = sheet2.createRow(j); //创建数据行
                        sheet2.setColumnWidth(j, 4000); //设置每列的列宽
                        row.createCell(0).setCellValue(dlData[j]); //设置对应单元格的值

                    } else { //非第1个下拉选项

                        int rowCount = sheet2.getLastRowNum();
                        //System.out.println("========== LastRowNum =========" + rowCount);
                        if (j <= rowCount) { //前面创建过的行，直接获取行，创建列
                            //获取行，创建列
                            sheet2.getRow(j).createCell(index).setCellValue(dlData[j]); //设置对应单元格的值

                        } else { //未创建过的行，直接创建行、创建列
                            sheet2.setColumnWidth(j, 4000); //设置每列的列宽
                            //创建行、创建列
                            sheet2.createRow(j).createCell(index).setCellValue(dlData[j]); //设置对应单元格的值
                        }
                    }
                }
                index++;
                 }
            }

            return wb;
        }


    /**
     * @param @param  strFormula
     * @param @param  firstRow   起始行
     * @param @param  endRow     终止行
     * @param @param  firstCol   起始列
     * @param @param  endCol     终止列
     * @param @return
     * @return HSSFDataValidation
     * @throws
     * @Title: SetDataValidation
     * @Description: 下拉列表元素很多的情况 (255以上的下拉)
     */
    private static DataValidation SetDataValidation(XSSFSheet sheet,String strFormula,
                                                        int firstRow, int endRow, int firstCol, int endCol) {
        DataValidationHelper helper = new XSSFDataValidationHelper(sheet);
        DataValidationConstraint constraint = helper.createFormulaListConstraint(strFormula);
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        DataValidation dataValidation = helper.createValidation(constraint, regions);
        dataValidation.createErrorBox("Error", "Error");
        dataValidation.createPromptBox("", null);
        return dataValidation;
    }


    /**
     * @param @param  sheet
     * @param @param  textList
     * @param @param  firstRow
     * @param @param  endRow
     * @param @param  firstCol
     * @param @param  endCol
     * @param @return
     * @return DataValidation
     * @throws
     * @Title: setDataValidation
     * @Description: 下拉列表元素不多的情况(255以内的下拉)
     */
    private static DataValidation setDataValidation(Sheet sheet, String[] textList, int firstRow, int endRow, int firstCol, int endCol) {

        DataValidationHelper helper = sheet.getDataValidationHelper();
        //加载下拉列表内容
        DataValidationConstraint constraint = helper.createExplicitListConstraint(textList);
        //DVConstraint constraint = new DVConstraint();
        constraint.setExplicitListValues(textList);

        //设置数据有效性加载在哪个单元格上。四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList( firstRow, endRow, firstCol, endCol);

        //数据有效性对象
        DataValidation data_validation = helper.createValidation(constraint, regions);
        //DataValidation data_validation = new DataValidation(regions, constraint);

        return data_validation;
    }



}
