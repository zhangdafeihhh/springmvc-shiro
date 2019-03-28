package com.zhuanche.util;

import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期处理类
 * @author will
 */
public class DateUtils {

	public static String dateTimeFormat_parttern = "yyyy-MM-dd HH:mm:ss";



	public static Date parseDateStr(String dateStr,String parttern){
		try{
			SimpleDateFormat format =  new SimpleDateFormat(parttern);
			return format.parse(dateStr);
		}catch (Exception e){
			return null;
		}
	}
	/**
	 * 得到N(N可以为负数)小时后的日期
	 */
	public static Date afterNHoursDate(Date theDate, int hous) {
		try {
			if (theDate == null) {
				return getCurrentDate();
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(theDate);
			cal.add(Calendar.HOUR_OF_DAY, hous);
			return cal.getTime();
		} catch (Exception e) {
			return getCurrentDate(); // 如果无法转化，则返回默认格式的时间。
		}
	}

	/**
	 * 返回当前日期 Date
	 */
	public static Date getCurrentDate() {
		return Calendar.getInstance().getTime();
	}
	/**
	* 格式化当前日期为 'yyyyMMddHHmmss'.
	*
	* @return String  格式化后日期. 
	* @exception 
	*/
	public static String getCurrTime() {
		Date now = new Date();
		SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String s = outFormat.format(now);
		return s;
	}
	/**
	* 按给定格式格式化给定日期.
	*
	* @author hansel.
	* @param  date 给定的日期,format 给定的格式.
	* @return String  格式化后日期. 
	* @exception 
	*/
	public static String formatDate_CN(Date date) {
		if(date == null)
			return "";
		SimpleDateFormat outFormat = new SimpleDateFormat("yyyy年MM月dd日");
		return outFormat.format(date);
	}
	
	public static String formatDateTime_CN(Date date) {
		SimpleDateFormat outFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		return outFormat.format(date);
	}
	
	/**
	* 按给定格式格式化给定日期.
	*
	* @author hansel.
	* @param  date 给定的日期,format 给定的格式.
	* @return String  格式化后日期. 
	* @exception 
	*/
	public static String formatDate(Date date, String format) {
		SimpleDateFormat outFormat = new SimpleDateFormat(format);
		return outFormat.format(date);
	}

	/**
	* 格式化给定日期为 'yyyy-MM-dd'格式.
	*
	* @author hansel.
	* @param  date 给定的日期.
	* @return String  格式化后日期. 
	* @exception 
	*/
	public static String formatDate(Date date) {
		SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd");
		return outFormat.format(date);
	}

	/**
	* 格式化给定日期为 'yyyy-MM-dd HH:mm:ss'格式.
	*
	* @author hansel.
	* @param  date 给定的日期.
	* @return String  格式化后日期. 
	* @exception 
	*/
	public static String formatDateTime(Date date) {
		SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return outFormat.format(date);
	}

	/**
	* 格式化给定日期为 'yyyy/MM/dd'格式.
	*
	* @author hansel.
	* @param  myDate 给定的日期.
	* @return String  格式化后日期. 
	* @exception 
	*/
	public static String formatDate2(Date myDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		String strDate = formatter.format(myDate);
		return strDate;
	}

	/**
	* 格式化给定日期为 'MM-dd HH:mm'格式.
	*
	* @author hansel.
	* @param  myDate 给定的日期.
	* @return String  格式化后日期. 
	* @exception 
	*/
	public static String formatDate3(Date myDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
		String strDate = formatter.format(myDate);
		return strDate;
	}

	/**
	* 格式化给定日期为 'yyyyMMdd'格式.
	*
	* @author hansel.
	* @param  myDate 给定的日期.
	* @return String  格式化后日期. 
	* @exception 
	*/
	public static String formatDate4(Date myDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String strDate = formatter.format(myDate);
		return strDate;
	}
	
	/**
	* 格式化给定日期为 'yyyy-MM-dd'格式.
	*
	* @author hansel.
	* @param  myDate 给定的日期.
	* @return String  格式化后日期. 
	* @exception 
	*/
	public static String formatDate5(Date myDate) {
		String strDate = getYear(myDate) + "-" + getMonth(myDate) + "-" + getDay(myDate);
		return strDate;
	}

	/**
	* 格式化给定日期为 'yyyy-MM-dd HH:mm'格式.
	*
	* @author hansel.
	* @param  myDate 给定的日期.
	* @return String  格式化后日期. 
	* @exception 
	*/
	public static String formatDate6(Date myDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String strDate = formatter.format(myDate);
		return strDate;
	}

	/**
	* 根据给定的参数设置日期.
	*
	* @author hansel.
	* @param  year 年,  month 月,  date 日.
	* @return long  设置后的日期(以毫秒为单位). 
	* @exception 
	*/
	public static long Date2Long(int year, int month, int date) {
		Calendar cld = Calendar.getInstance();
		month = month - 1;
		cld.set(year, month, date);
		return cld.getTime().getTime();
	}
	
	/**
	* 根据给定的参数设置日期时间.
	*
	* @author hansel.
	* @param  year 年,  month 月,  date 日,  hour 小时,  minute 分钟,  second 秒.
	* @return long  设置后日期时间(以毫秒为单位). 
	* @exception 
	*/
	public static long Time2Long(int year, int month, int date, int hour, int minute, int second) {
		Calendar cld = Calendar.getInstance();
		month = month - 1;
		cld.set(year, month, date, hour, minute, second);
		return cld.getTime().getTime();
	}

	/**
	* 获得给定日期的年份.
	*
	* @author hansel.
	* @param t 给定的以毫秒为单位的日期
	* @return int  给定日期的年份. 
	* @exception 
	*/
	public static int getYear(long t) {
		Calendar cld = Calendar.getInstance();
		if (t > 0) {
			cld.setTime(new Date(t));
		}
		return cld.get(Calendar.YEAR);
	}

	/**
	* 获得给定日期的月份.
	*
	* @author hansel.
	* @param t 给定的以毫秒为单位的日期
	* @return int  给定日期的月份.
	* @exception
	*/
	public static int getMonth(long t) {
		Calendar cld = Calendar.getInstance();
		if (t > 0) {
			cld.setTime(new Date(t));
		}
		return cld.get(Calendar.MONTH) + 1;
	}

	/**
	* 获得给定日期的天.
	*
	* @author hansel.
	* @param t 给定的以毫秒为单位的日期
	* @return int  给定日期的天.
	* @exception
	*/
	public static int getDay(long t) {
		Calendar cld = Calendar.getInstance();
		if (t > 0) {
			cld.setTime(new Date(t));
		}
		return cld.get(Calendar.DAY_OF_MONTH);
	}

	/**
	* 获得给定日期的小时.
	*
	* @author hansel.
	* @param t 给定的以毫秒为单位的日期
	* @return int  给定日期的小时.
	* @exception
	*/
	public static int getHour(long t) {
		Calendar cld = Calendar.getInstance();
		if (t > 0) {
			cld.setTime(new Date(t));
		}
		return cld.get(Calendar.HOUR_OF_DAY);
	}

	/**
	* 获得给定日期的分钟.
	*
	* @author hansel.
	* @param t 给定的以毫秒为单位的日期
	* @return int  给定日期的分钟.
	* @exception
	*/
	public static int getMinute(long t) {
		Calendar cld = Calendar.getInstance();
		if (t > 0) {
			cld.setTime(new Date(t));
		}
		return cld.get(Calendar.MINUTE);
	}

	/**
	* 获得给定日期的秒.
	*
	* @author hansel.
	* @param t 给定的以毫秒为单位的日期
	* @return int  给定日期的秒.
	* @exception
	*/
	public static int getSecond(long t) {
		Calendar cld = Calendar.getInstance();
		if (t > 0) {
			cld.setTime(new Date(t));
		}
		return cld.get(Calendar.SECOND);
	}

	/**
	* 获得给定日期的年份.
	*
	* @author hansel.
	* @param date 给定的日期
	* @return int  给定日期的年份.
	* @exception
	*/
	public static int getYear(Date date) {
		Calendar cld = Calendar.getInstance();
		cld.setTime(date);
		return cld.get(Calendar.YEAR);
	}

	/**
	* 获得给定日期的月份.
	*
	* @author hansel.
	* @param date 给定的日期
	* @return int  给定日期的月份.
	* @exception
	*/
	public static int getMonth(Date date) {
		Calendar cld = Calendar.getInstance();
		cld.setTime(date);
		return cld.get(Calendar.MONTH) + 1;
	}

	/**
	* 获得给定日期的天.
	*
	* @author hansel.
	* @param date 给定的日期
	* @return int  给定日期的天.
	* @exception
	*/
	public static int getDay(Date date) {
		Calendar cld = Calendar.getInstance();
		cld.setTime(date);
		return cld.get(Calendar.DAY_OF_MONTH);
	}

	/**
	* 获得给定日期的小时.
	*
	* @author hansel.
	* @param date 给定的日期
	* @return int  给定日期的小时.
	* @exception
	*/
	public static int getHour(Date date) {
		Calendar cld = Calendar.getInstance();
		cld.setTime(date);
		return cld.get(Calendar.HOUR_OF_DAY);
	}

	/**
	* 获得给定日期的分钟.
	*
	* @author hansel.
	* @param date 给定的日期
	* @return int  给定日期的分钟.
	* @exception
	*/
	public static int getMinute(Date date) {
		Calendar cld = Calendar.getInstance();
		cld.setTime(date);
		return cld.get(Calendar.MINUTE);
	}

	/**
	* 获得给定日期的秒.
	*
	* @author hansel.
	* @param date 给定的日期
	* @return int  给定日期的秒.
	* @exception
	*/
	public static int getSecond(Date date) {
		Calendar cld = Calendar.getInstance();
		cld.setTime(date);
		return cld.get(Calendar.SECOND);
	}

	/**
	* 获得岁数相对应的年数.
	*
	* @author hansel.
	* @param  age 年龄
	* @return String YYYYMMDD格式.
	* @exception
	*/
	public static String formatdate7(int age) {
		Calendar cld = Calendar.getInstance();
		cld.setTime(new Date());
		return (cld.get(Calendar.YEAR)-age)+"0101";
	}

	/**
	* 获得当前日期的月.
	*
	* @author hansel.
	* @param
	* @return int  当前日期的月.
	* @exception
	*/
	public static int getMonth() {
		Calendar cld = Calendar.getInstance();
		cld.setTime(new Date());
		return cld.get(Calendar.MONTH) + 1;
	}

	/**
	* 获得当前日期的天.
	*
	* @author hansel.
	* @param
	* @return int  当前日期的天.
	* @exception
	*/
	public static int getDay() {
		Calendar cld = Calendar.getInstance();
		cld.setTime(new Date());
		return cld.get(Calendar.DAY_OF_MONTH);
	}

	/**
	* 获得当前日期的年.
	*
	* @author hansel.
	* @param
	* @return int  当前日期的年.
	* @exception
	*/
	public static int  getYear() {
		Calendar cld = Calendar.getInstance();
		cld.setTime(new Date());
		return cld.get(Calendar.YEAR);
	}
	/**
	* 根据输入的年月日，获得DATE类型.
	*
	* @author hansel.
	* @param
	* @return Date  Date类型数据.
	 * @throws ParseException
	* @exception
	*/
	public static Date getDate(int year,int month,int day) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date= formatter.parse(year+"-"+month+"-"+day);
		return date;
	}

	/**
	* 根据输入的年月日，获得DATE类型.
	*
	* @author hansel.
	* @param
	* @return Date  Date类型数据.
	 * @throws ParseException
	* @exception
	*/
	public static Date getDate1(String birthday) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date= formatter.parse(birthday);
		return date;
	}

	 public static Date getDate(String dateStr) {
		  DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		  Date d = null;
		try {
			d = df.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		  return d;
	 }


	 public static Date getDate(String dateStr, String dateFormat) {
		  DateFormat   df=new SimpleDateFormat(dateFormat);
		  Date d = null;
		try {
			d = df.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		  return d;
	 }

		/** 计算两个日期相差多少天 */
		public static int getIntervalDays(Date begin,Date end){
			long in = end.getTime() - begin.getTime();
			int days = (int)(in / 86400000);
			return days;
		}


		public static String dateOpt(String dateStr,int skip){
				String result = "";
		        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		        try {
		            Date d1 = df.parse(dateStr);
		            Calendar  g = Calendar.getInstance();
		            g.setTime(d1);
		            g.add(Calendar.SECOND,skip);
		            Date d2 = g.getTime();
		            result = df.format(d2);
		       } catch (ParseException e) {
		            e.printStackTrace();
		       }
		       return result;
		}

		/**
		 * 是否为当天
		 * @param date
		 * @return
		 */
		@SuppressWarnings("deprecation")
		public static boolean isToday(Date date){
			Date now = new Date();
			if(now.getDate() != date.getDate()){
				return false;
			}
			if(now.getMonth() != date.getMonth()){
				return false;
			}
			if(now.getYear() != date.getYear()){
				return false;
			}
			return true;
		}

		/**
		 * 将时间转换成milliseconds
		 * @param time
		 * @return
		 */
		public static String formatTimestap(Timestamp time) {
			if (time == null) {
				return "";
			}
			return String.valueOf(time.getTime());
		}


		public static Timestamp getCurrentTime() {
			return new Timestamp(new Date().getTime());
		}

		@SuppressWarnings("unchecked")
		public static <T extends Date> T parse(String dateString,String dateFormat,Class<T> targetResultType) {
			if(StringUtils.isEmpty(dateString))
				return null;
			DateFormat df = new SimpleDateFormat(dateFormat);
			try {
				long time = df.parse(dateString).getTime();
				Date t = targetResultType.getConstructor(long.class).newInstance(time);
				return (T)t;
			} catch (ParseException e) {
				String errorInfo = "cannot use dateformat:"+dateFormat+" parse datestring:"+dateString;
				throw new IllegalArgumentException(errorInfo,e);
			} catch (Exception e) {
				throw new IllegalArgumentException("error targetResultType:"+targetResultType.getName(),e);
			}
		}
		
		
		/**
		 * 获取每个月第一个星期日的日期
		 * 
		 * @param year 年份
		 * @param month 月份
		 * @return Date 日期
		 */
		public static Date getFirstSundayOfMonth(int year, int month)
		{
		    Calendar cal = Calendar.getInstance();
		 
		    cal.set(Calendar.YEAR, year);
		    cal.set(Calendar.MONTH, month - 1);
		    cal.set(Calendar.DATE, 1); // 设为第一天
		 
		    while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
		    {
		        cal.add(Calendar.DATE, 1);
		    }
		 
		    return cal.getTime();
		}

	    /**
	    * 获取上周星期日
	    * @return
	    */
	    public static Date getLastSundayOfWeek(){
			Calendar date=Calendar.getInstance();

			date.setFirstDayOfWeek(Calendar.MONDAY);//将每周第一天设为星期一，默认是星期天

			date.add(Calendar.WEEK_OF_MONTH,-1);//周数减一，即上周

			date.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);//日子设为星期天

			return date.getTime();
	    }

	/**
	 * 获取当前日期所在月的第几周
	 * @param date
	 * @return
	 */
	public static int getCurrentWeekOfDate(Date date){
			Calendar calendar=Calendar.getInstance();
			calendar.setFirstDayOfWeek(Calendar.MONDAY);//将每周第一天设为星期一，默认是星期天
			calendar.setTime(date);
			return calendar.get(Calendar.WEEK_OF_MONTH);
		}
		
		/**
		 * 为原日期添加指定的天数并返回添加后的日期，如果天数为负数则在原日期的基础上减去指定的天数
		 * 
		 * @param source
		 * @param days
		 * @return
		 */
		public static Date addDays(Date source, int days){
			try{
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
				format.parse(format.format(source));
				Calendar calendar = format.getCalendar();
				calendar.add(Calendar.DAY_OF_YEAR, days);
				return calendar.getTime();
			} catch (Exception e){
				throw new RuntimeException("add days is error.");
			}
		}


}
