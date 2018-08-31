package com.zhuanche.util;

import com.zhuanche.dto.CarImportExceptionEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Common {
	
	//中国地图边界.
	public static final Double ChinaEastEdge = 137.0417;
	public static final Double ChinaWestEdge = 73.6667;
	public static final Double ChinaNorthEdge = 53.55;
	public static final Double ChinaSouthEdge = 3.8667;

	public static Pattern COMPILE = Pattern.compile("[0-9]*");

    public static Pattern COMPILE1 = Pattern.compile("\\s*|\t|\r|\n");

    public static Pattern COMPILE2 = Pattern.compile("\\s*|\t|\r|\n");

	public static String getPath(HttpServletRequest request) {
		String uploadDir =request.getSession().getServletContext().getRealPath("/");  
		File f1 = new File(uploadDir);
		String path  = f1.getParentFile().getParentFile().getParent()+"/upload/";
		return path;
	}
	
	public static Workbook exportExcel(String path,
			List<CarImportExceptionEntity> exportList) throws Exception {
		FileInputStream io = new FileInputStream(path);
		// 创建 excel
		Workbook wb = new XSSFWorkbook(io);
		List<CarImportExceptionEntity> list = exportList;
		if (list != null && list.size() > 0) {
			Sheet sheet = wb.getSheetAt(0);
			Cell cell = null;
			int i = 0;
			for (CarImportExceptionEntity s : list) {
				Row row = sheet.createRow(i + 1);
				cell = row.createCell(0);
				cell.setCellValue(s.getReson());
				i++;
			}
		}
		return wb;
	}
	
	/**
	 * 
	 * @param request
	 * @param wb
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static String exportExcelFromTempletToLoacl(HttpServletRequest request,
			Workbook wb, String fileName) throws Exception {
		if (StringUtils.isEmpty(fileName)) {
			fileName = "exportExcel";
		}
		// 获取本地绝对路径
		String webPath = request.getServletContext().getRealPath("/");

		// 获取配置文件上下文地址
		// 生成文件相对路径
		String fileURI = "/template/error/" + fileName + buildRandom(2) + "_"
				+ System.currentTimeMillis() + ".xlsx";

		FileOutputStream fos = new FileOutputStream(webPath + fileURI);
		wb.write(fos);
		fos.close();
		return fileURI;
	}
	
	public static int buildRandom(int length) {
		int num = 1;
		double random = Math.random();
		if (random < 0.1) {
			random = random + 0.1;
		}
		for (int i = 0; i < length; i++) {
			num = num * 10;
		}
		return (int) ((random * num));
	}
	
	public static boolean isValidDate(String str) {
		boolean convertSuccess = true;
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			format.setLenient(false);
			format.parse(str);
		} catch (Exception e) {
			convertSuccess = false;
		}
		return convertSuccess;
	}
	public static boolean isRegular(String str,String regular){ 
	   Pattern pattern = Pattern.compile(regular); 
	   Matcher isNum = pattern.matcher(str);
	   if( !isNum.matches() ){
	       return false; 
	   } 
	   return true; 
	}
	
	//数字
	public static boolean isNumeric(String str){
	   Matcher isNum = COMPILE.matcher(str);
	   if( !isNum.matches() ){
	       return false; 
	   } 
	   return true; 
	}

	public static String replaceBlank(String str) {
		String dest = "";
		if (str!=null) {
			Matcher m = COMPILE1.matcher(str);
			dest = m.replaceAll("").toUpperCase();
		}
		return dest.replaceAll("\\s*", "");
	}

	public static String replaceBlankNoUpper(String str) {
		String dest = "";
		if (str!=null) {
			Matcher m = COMPILE2.matcher(str);
			dest = m.replaceAll("");
		}
		return dest.replaceAll("\\s*", "");
	}
	
	/**
	 * 检查坐标信息组是否有效.
	 * 
	 * @param pointGroupStr
	 * @return
	 */
	public static boolean isValidPoints(String pointGroupStr) {
		String[] pointGroupArrays = pointGroupStr.split("/");
		for (int k = 0; k < pointGroupArrays.length; k++) {
			String[] pointGroupArray = pointGroupArrays[k].split(";");
			for (int i = 0; i < pointGroupArray.length; i++) {
				if (!isValidPoint(pointGroupArray[i])) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 检查坐标点有效性
	 * 
	 * @param point
	 * @return
	 */
	public static boolean isValidPoint(String point) {
		if (StringUtils.isBlank(point)) {
			return false;
		}
		String[] pointGroup = point.split(",");
		if (pointGroup.length != 2) {
			return false;
		}
		String lngStr = pointGroup[0];
		String latStr = pointGroup[1];
		try {
			Double lng = Double.parseDouble(lngStr);
			Double lat = Double.parseDouble(latStr);

			if (lng > ChinaEastEdge || lng < ChinaWestEdge || lat > ChinaNorthEdge
					|| lat < ChinaSouthEdge) {
				return false;
			}

		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static String removeSuffix(String cityName) {
		if ("市".equals(cityName.substring(cityName.length() - 1))) {
			return cityName.substring(0, cityName.length() - 1);
		}
		return cityName;

	}

	/**
	 * Java将Unix时间戳转换成指定格式日期字符串
	 * @param timestampString 时间戳 如："1473048265";
	 * @param formats 要格式化的格式 默认："yyyy-MM-dd HH:mm:ss";
	 *
	 * @return 返回结果 如："2016-09-05 16:06:42";
	 */
	public static String timeStamp2Date(String timestampString, String formats) {
		if (TextUtils.isEmpty(formats)){
            formats = "yyyy-MM-dd HH:mm:ss";
        }
		Long timestamp = Long.parseLong(timestampString);
		String date = new SimpleDateFormat(formats, Locale.CHINA).format(new Date(timestamp));
		return date;
	}
}
