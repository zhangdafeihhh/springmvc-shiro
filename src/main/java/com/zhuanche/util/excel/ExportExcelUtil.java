package com.zhuanche.util.excel;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.zhuanche.util.Check;
import com.zhuanche.util.dateUtil.DateUtil;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: excel 导出工具类
 * @Param:
 * @return:
 * @Author: lunan
 * @Date: 2018/7/9
 */
public class ExportExcelUtil<T> {

    public void exportExcel(String sheet, Collection<T> dataset, OutputStream out) {
        exportExcel(sheet, null, dataset, out, "yyyy-MM-dd");
    }

    public void exportExcel(String sheet, String[] headers, Collection<T> dataset,
                            OutputStream out) {
        exportExcel(sheet, headers, dataset, out, "yyyy-MM-dd");
    }

    public void exportExcel(String[] headers, Collection<T> dataset,
                            OutputStream out, String pattern, String sheet) {
        exportExcel(sheet, headers, dataset, out, pattern);
    }

    /** 多个sheet*/
    public <T> HSSFWorkbook exportExcelSheet(HSSFWorkbook workbook, String title, String[] headers, Collection<T> dataset) {
        if(Check.NuNObj(workbook)){
            workbook = new HSSFWorkbook();
        }
        // 生成一个表格
        HSSFSheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth((short) 15);
        // 生成一个样式
        HSSFCellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 生成一个字体
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.VIOLET.index);
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 把字体应用到当前的样式
        style.setFont(font);
        // 生成并设置另一个样式
        HSSFCellStyle style2 = workbook.createCellStyle();
        style2.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
        style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        // 生成另一个字体
        HSSFFont font2 = workbook.createFont();
        font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        style2.setFont(font2);
        // 声明一个画图的顶级管理器
        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
        // 定义注释的大小和位置,详见文档
        HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));
        // 设置注释内容
        comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));
        // 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
        comment.setAuthor("leno");
        //产生表格标题行
        HSSFRow row = sheet.createRow(0);
        for (short i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(style);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
        }
        if (dataset != null) {
            //遍历集合数据，产生数据行
            Iterator<T> it = dataset.iterator();
            int index = 0;
            HSSFFont font3 = workbook.createFont();
            font3.setColor(HSSFColor.BLUE.index);

            HSSFCellStyle styleDate = workbook.createCellStyle();
            styleDate.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
            styleDate.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            styleDate.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            HSSFDataFormat format = workbook.createDataFormat();
            styleDate.setDataFormat(format.getFormat("yyyy-mm-dd hh:mm:ss"));
            while (it.hasNext()) {
                index++;
                row = sheet.createRow(index);
                T t = (T) it.next();
                //利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
                Field[] fields = t.getClass().getDeclaredFields();
                for (short i = 0; i < fields.length; i++) {
                    HSSFCell cell = row.createCell(i);
                    cell.setCellStyle(style2);
                    Field field = fields[i];
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

                            cell.setCellStyle(styleDate);
                            cell.setCellValue((Date) value);

                        } else if (value instanceof byte[]) {
                            // 有图片时，设置行高为60px;
                            row.setHeightInPoints(60);
                            // 设置图片所在列宽度为80px,注意这里单位的一个换算
                            sheet.setColumnWidth(i, (short) (35.7 * 80));
                            // sheet.autoSizeColumn(i);
                            byte[] bsValue = (byte[]) value;
                            HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0,
                                    1023, 255, (short) 6, index, (short) 6, index);
                            anchor.setAnchorType(2);
                            patriarch.createPicture(anchor, workbook.addPicture(
                                    bsValue, HSSFWorkbook.PICTURE_TYPE_JPEG));
                        } else {
                            //其它数据类型都当作字符串简单处理
                            if (!Check.NuNObjs(value)) {
                                textValue = value.toString();
                            }
                        }
                        //如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
                        if (textValue != null) {
                            Pattern p = Pattern.compile("^//d+(//.//d+)?$");
                            Matcher matcher = p.matcher(textValue);
                            if (matcher.matches()) {
                                //是数字当作double处理
                                cell.setCellValue(Double.parseDouble(textValue));
                            } else {
                                HSSFRichTextString richString = new HSSFRichTextString(textValue);

                                richString.applyFont(font3);
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
        return workbook;
    }

    /**
     * 只导出表头信息
     * @param sheet
     * @param headers
     * @param out
     */
    public void exportExcel(String sheet, String[] headers,
                            OutputStream out) {
        exportExcel(sheet, headers, null, out, "yyyy-MM-dd");
    }
    /**
     * 这是一个通用的方法，利用了JAVA的反射机制，可以将放置在JAVA集合中并且符号一定条件的数据以EXCEL 的形式输出到指定IO设备上
     *
     * @param title
     * @param headers 表格属性列名数组表格标题名
     * @param dataset 需要显示的数据集合,集合中一定要放置符合javabean风格的类的对象。此方法支持的
     *                javabean属性的数据类型有基本数据类型及String,Date,byte[](图片数据)
     * @param out     与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
     * @param pattern 如果有时间数据，设定输出格式。默认为"yyy-MM-dd"
     */
    @SuppressWarnings("unchecked")
    public <T> void exportExcel(String title, String[] headers,
                            Collection<T> dataset, OutputStream out, String pattern) {
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        workbook = exportExcelSheet(workbook,title,headers,dataset);
        try {
            workbook.write(out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*// 生成一个表格
        HSSFSheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth((short) 15);
        // 生成一个样式
        HSSFCellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 生成一个字体
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.VIOLET.index);
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 把字体应用到当前的样式
        style.setFont(font);
        // 生成并设置另一个样式
        HSSFCellStyle style2 = workbook.createCellStyle();
        style2.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
        style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        // 生成另一个字体
        HSSFFont font2 = workbook.createFont();
        font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        style2.setFont(font2);
        // 声明一个画图的顶级管理器
        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
        // 定义注释的大小和位置,详见文档
        HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));
        // 设置注释内容
        comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));
        // 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
        comment.setAuthor("leno");
        //产生表格标题行
        HSSFRow row = sheet.createRow(0);
        for (short i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(style);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
        }
        if (dataset != null) {
            //遍历集合数据，产生数据行
            Iterator<T> it = dataset.iterator();
            int index = 0;
            HSSFFont font3 = workbook.createFont();
            font3.setColor(HSSFColor.BLUE.index);

            HSSFCellStyle styleDate = workbook.createCellStyle();
            styleDate.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
            styleDate.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            styleDate.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            HSSFDataFormat format = workbook.createDataFormat();
            styleDate.setDataFormat(format.getFormat("yyyy-mm-dd hh:mm:ss"));
            while (it.hasNext()) {
                index++;
                row = sheet.createRow(index);
                T t = (T) it.next();
                //利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
                Field[] fields = t.getClass().getDeclaredFields();
                for (short i = 0; i < fields.length; i++) {
                    HSSFCell cell = row.createCell(i);
                    cell.setCellStyle(style2);
                    Field field = fields[i];
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

                            cell.setCellStyle(styleDate);
                            cell.setCellValue((Date) value);

                        } else if (value instanceof byte[]) {
                            // 有图片时，设置行高为60px;
                            row.setHeightInPoints(60);
                            // 设置图片所在列宽度为80px,注意这里单位的一个换算
                            sheet.setColumnWidth(i, (short) (35.7 * 80));
                            // sheet.autoSizeColumn(i);
                            byte[] bsValue = (byte[]) value;
                            HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0,
                                    1023, 255, (short) 6, index, (short) 6, index);
                            anchor.setAnchorType(2);
                            patriarch.createPicture(anchor, workbook.addPicture(
                                    bsValue, HSSFWorkbook.PICTURE_TYPE_JPEG));
                        } else {
                            //其它数据类型都当作字符串简单处理
                            if (!Check.NuNObjs(value)) {
                                textValue = value.toString();
                            }
                        }
                        //如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
                        if (textValue != null) {
                            Pattern p = Pattern.compile("^//d+(//.//d+)?$");
                            Matcher matcher = p.matcher(textValue);
                            if (matcher.matches()) {
                                //是数字当作double处理
                                cell.setCellValue(Double.parseDouble(textValue));
                            } else {
                                HSSFRichTextString richString = new HSSFRichTextString(textValue);

                                richString.applyFont(font3);
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
        }*/

    }


//    public <T> SXSSFWorkbook exportExcelSheetV2(SXSSFWorkbook workbook, String title, String[] headers, Collection<T> dataset) {
//        if(Check.NuNObj(workbook)){
//            workbook = new SXSSFWorkbook();
//        }
//
//        // 生成一个表格
//        Sheet sheet = workbook.createSheet(title);
//        // 设置表格默认列宽度为15个字节
//        sheet.setDefaultColumnWidth((short) 15);
//        // 生成一个样式
//        CellStyle style = workbook.createCellStyle();
//        // 设置这些样式
//        style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
//        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
//        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
//        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
//        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
//        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//        // 生成一个字体
//        Font font = workbook.createFont();
//        font.setColor(HSSFColor.VIOLET.index);
//        font.setFontHeightInPoints((short) 12);
//        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
//        // 把字体应用到当前的样式
//        style.setFont(font);
//        // 生成并设置另一个样式
//        CellStyle style2 = workbook.createCellStyle();
//        style2.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
//        style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//        style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
//        style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
//        style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
//        style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
//        style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
//        // 生成另一个字体
//        Font font2 = workbook.createFont();
//        font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
//        // 把字体应用到当前的样式
//        style2.setFont(font2);
//        // 声明一个画图的顶级管理器
//        Drawing patriarch = sheet.createDrawingPatriarch();
//        // 定义注释的大小和位置,详见文档
//
////        HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));
////        // 设置注释内容
////        comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));
////        // 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
////        comment.setAuthor("leno");
//        //产生表格标题行
//        Row row = sheet.createRow(0);
//        for (short i = 0; i < headers.length; i++) {
//            Cell cell = row.createCell(i);
//            cell.setCellStyle(style);
//            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
//            cell.setCellValue(text);
//        }
//        if (dataset != null) {
//            //遍历集合数据，产生数据行
//            Iterator<T> it = dataset.iterator();
//            int index = 0;
//            Font font3 = workbook.createFont();
//            font3.setColor(HSSFColor.BLUE.index);
//
//            CellStyle style3 = workbook.createCellStyle();
//            style3.setFont(font3);
//
//            CellStyle styleDate = workbook.createCellStyle();
//            styleDate.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
//            styleDate.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//            styleDate.setBorderBottom(HSSFCellStyle.BORDER_THIN);
//            DataFormat format = workbook.createDataFormat();
//            styleDate.setDataFormat(format.getFormat("yyyy-mm-dd hh:mm:ss"));
//            while (it.hasNext()) {
//                index++;
//                row = sheet.createRow(index);
//                T t = (T) it.next();
//                //利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
//                Field[] fields = t.getClass().getDeclaredFields();
//                for (short i = 0; i < fields.length; i++) {
//                    Cell cell = row.createCell(i);
//                    cell.setCellStyle(style2);
//                    Field field = fields[i];
//                    String fieldName = field.getName();
//                    String getMethodName = "get"
//                            + fieldName.substring(0, 1).toUpperCase()
//                            + fieldName.substring(1);
//                    if("getSerialVersionUID".equals(getMethodName)){
//                        continue;
//                    }
//                    try {
//                        Class tCls = t.getClass();
//                        Method getMethod = tCls.getMethod(getMethodName,
//                                new Class[]{});
//                        Object value = getMethod.invoke(t, new Object[]{});
//                        //判断值的类型后进行强制类型转换
//                        String textValue = null;
//
//                        if (value instanceof Date) {
//
//                            cell.setCellStyle(styleDate);
//                            cell.setCellValue((Date) value);
//
//                        } else if (value instanceof byte[]) {
//                            // 有图片时，设置行高为60px;
//                            row.setHeightInPoints(60);
//                            // 设置图片所在列宽度为80px,注意这里单位的一个换算
//                            sheet.setColumnWidth(i, (short) (35.7 * 80));
//                            // sheet.autoSizeColumn(i);
//                            byte[] bsValue = (byte[]) value;
//                            HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0,
//                                    1023, 255, (short) 6, index, (short) 6, index);
//                            anchor.setAnchorType(2);
//                            patriarch.createPicture(anchor, workbook.addPicture(
//                                    bsValue, HSSFWorkbook.PICTURE_TYPE_JPEG));
//                        } else {
//                            //其它数据类型都当作字符串简单处理
//                            if (!Check.NuNObjs(value)) {
//                                textValue = value.toString();
//                            }
//                        }
//                        //如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
//                        if (textValue != null) {
//                            Pattern p = Pattern.compile("^//d+(//.//d+)?$");
//                            Matcher matcher = p.matcher(textValue);
//                            if (matcher.matches()) {
//                                //是数字当作double处理
//                                cell.setCellValue(Double.parseDouble(textValue));
//                            } else {
//                                HSSFRichTextString richString = new HSSFRichTextString(textValue);
//                                cell.setCellStyle(style3);
////                                richString.applyFont(font3);
//                                cell.setCellValue(richString);
//                            }
//                        }
//                    } catch (SecurityException e) {
//                        e.printStackTrace();
//                    } catch (NoSuchMethodException e) {
//                        e.printStackTrace();
//                    } catch (IllegalArgumentException e) {
//                        e.printStackTrace();
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    } catch (InvocationTargetException e) {
//                        e.printStackTrace();
//                    } finally {
//                    }
//                }
//            }
//        }
//        return workbook;
//    }

    /**
     *  用于生成导入模板，可支持自定义下拉列表
     *
     * @param filename 文件名称
     * @param handers  表格titel
     * @param downData 下拉框内容
     * @param downRows 下拉框所在位置
     * @param request
     * @param response
     * @throws Exception
     */
    public static  void exportExcel(String filename, String[] handers,
                                    List<String[]> downData, String[] downRows, List contentList,HttpServletRequest request, HttpServletResponse response) throws Exception {
        Workbook workbook = ExportTemplateUtil.createExcelTemplate(handers, downData, downRows,contentList);
        filename = filename + DateUtil.dateFormat(new Date(), DateUtil.intTimestampPattern);
        //获得浏览器信息并转换为大写
        String agent = request.getHeader("User-Agent").toUpperCase();
        //IE浏览器和Edge浏览器
        if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO") > 0 && agent.indexOf("RV:11") > 0)) {
            filename = URLEncoder.encode(filename, "UTF-8");
        } else {  //其他浏览器
            filename = new String(filename.getBytes("UTF-8"), "iso-8859-1");
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename="+filename + ".xlsx");
        response.addHeader("Pargam", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        ServletOutputStream out = response.getOutputStream();
        workbook.write(out);
        out.flush();
        out.close();
    }

    public static OutputStream getOutputStream(String fileName, HttpServletResponse response) throws Exception {
        if (!fileName.contains(ExcelTypeEnum.XLSX.getValue())) {
            fileName = fileName + ExcelTypeEnum.XLSX.getValue();
        }
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        return response.getOutputStream();
    }

}
