package com.zhuanche.util.excel;


import com.alibaba.fastjson.JSON;
import com.zhuanche.util.Check;
import com.zhuanche.util.dateUtil.DateUtil;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: mp-manage
 * @description: 生成导出模板，包含下拉框
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
                                               List<String[]> downData, String[] downRows, List contentList) {
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
        //将表头设置为不可编辑
        style.setLocked(true);


        //新建sheet
        XSSFSheet sheet1 = wb.createSheet("Sheet1");
        sheet1.autoSizeColumn((short) 0);
        XSSFSheet listSheet = wb.createSheet("listSheet");
        // XSSFSheet sheet3 = wb.createSheet("Sheet3");


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
                //String strFormula = "listSheet!$A$1:$A$5000" ; //listSheet第A1到A5000作为下拉列表来源数据
                String strFormula = "listSheet!$" + arr[index] + "$1:$" + arr[index] + "$5000"; //listSheet第A1到A5000作为下拉列表来源数据
                listSheet.setColumnWidth(r, 4000); //设置每列的列宽
                //设置数据有效性加载在哪个单元格上,参数分别是：从listSheet获取A1到A5000作为一个下拉的数据、起始行、终止行、起始列、终止列
                sheet1.addValidationData(SetDataValidation(listSheet, strFormula, 1, 5000, rownum, rownum)); //下拉列表元素很多的情况

                //2、生成listSheet内容
                for (int j = 0; j < dlData.length; j++) {
                    if (index == 0) { //第1个下拉选项，直接创建行、列
                        row = listSheet.createRow(j); //创建数据行
                        listSheet.setColumnWidth(j, 4000); //设置每列的列宽
                        row.createCell(0).setCellValue(dlData[j]); //设置对应单元格的值

                    } else { //非第1个下拉选项

                        int rowCount = listSheet.getLastRowNum();
                        //System.out.println("========== LastRowNum =========" + rowCount);
                        if (j <= rowCount) { //前面创建过的行，直接获取行，创建列
                            //获取行，创建列
                            listSheet.getRow(j).createCell(index).setCellValue(dlData[j]); //设置对应单元格的值

                        } else { //未创建过的行，直接创建行、创建列
                            listSheet.setColumnWidth(j, 4000); //设置每列的列宽
                            //创建行、创建列
                            listSheet.createRow(j).createCell(index).setCellValue(dlData[j]); //设置对应单元格的值
                        }
                    }
                }
                index++;
            }
        }
        sheet1.createFreezePane(0, 1, 0, 1);
        //隐藏掉listSheet
        int sheetIndex = wb.getSheetIndex(listSheet);
        wb.setSheetHidden(sheetIndex, true);


        if (contentList != null&&contentList.size()>0) {
            CellStyle styleDate = wb.createCellStyle();
         //   styleDate.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
         //   styleDate.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
           // styleDate.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            // 14 代表 yyyy-MM-dd
            styleDate.setDataFormat((short) 14);

            //遍历集合数据，产生数据行
            //   Iterator<T> it = contentList.iterator();
            // int index = 0;
          //  XSSFFont font3 = wb.createFont();
           // font3.setColor(HSSFColor.BLUE.index);
            //  while (it.hasNext()) {
            //    index++;
            for (int i = 0; i < contentList.size(); i++) {
                row = sheet1.createRow(i+1);
                //T t = (T) it.next();
                Object t = contentList.get(i);
                //利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
                Field[] fields = t.getClass().getDeclaredFields();
                for (short j = 0; j < fields.length; j++) {
                    XSSFCell cell = row.createCell(j);
                    //cell.setCellStyle(style2);
                    Field field = fields[j];
                    String fieldName = field.getName();
                    String getMethodName = "get"
                            + fieldName.substring(0, 1).toUpperCase()
                            + fieldName.substring(1);
                    try {
                        Class tCls = t.getClass();
                        Method getMethod = tCls.getMethod(getMethodName,
                                new Class[]{});
                        Object value = getMethod.invoke(t, new Object[]{});
                        //判断值的类型后进行强制类型转换
                        String textValue = null;

                        if (value instanceof Date) {
                            cell.setCellValue((Date) value);
                            cell.setCellStyle(styleDate);
                        } else {
                            //其它数据类型都当作字符串简单处理
                            if (!Check.NuNObjs(value)) {
                                textValue = value.toString();
                            }
                        }
                        //利用正则表达式判断textValue是否全部由数字组成
                        if (textValue != null) {
                            Pattern p = Pattern.compile("^//d+(//.//d+)?$");
                            Matcher matcher = p.matcher(textValue);
                            if (matcher.matches()) {
                                //是数字当作double处理
                                cell.setCellValue(Double.parseDouble(textValue));
                            } else {
                                //HSSFRichTextString
                                XSSFRichTextString richString = new XSSFRichTextString(textValue);
                               // HSSFRichTextString richString = new HSSFRichTextString(textValue);
                              //  richString.applyFont(font3);
                                cell.setCellValue(richString);
                            }
                        }
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } finally {
                    }
                }
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
    private static DataValidation SetDataValidation(XSSFSheet sheet, String strFormula,
                                                    int firstRow, int endRow, int firstCol, int endCol) {
        DataValidationHelper helper = new XSSFDataValidationHelper(sheet);
        DataValidationConstraint constraint = helper.createFormulaListConstraint(strFormula);
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        DataValidation dataValidation = helper.createValidation(constraint, regions);
        dataValidation.createPromptBox("", null);
        if (dataValidation instanceof XSSFDataValidation) {
            //数据校验
            dataValidation.setSuppressDropDownArrow(true);
            dataValidation.setShowErrorBox(true);
        } else {
            dataValidation.setSuppressDropDownArrow(false);
        }
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
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);

        //数据有效性对象
        DataValidation data_validation = helper.createValidation(constraint, regions);
        //DataValidation data_validation = new DataValidation(regions, constraint);
        //处理Excel兼容性问题
        if (data_validation instanceof XSSFDataValidation) {
            //数据校验
            data_validation.setSuppressDropDownArrow(true);
            data_validation.setShowErrorBox(true);
        } else {
            data_validation.setSuppressDropDownArrow(false);
        }
        return data_validation;
    }


}
