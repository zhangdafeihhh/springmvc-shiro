package com.zhuanche.util;

import com.google.common.collect.Maps;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;

/**
 * 一些公用的日期方法
 * @author jifeng
 */
public class DateUtil {
	public static final String LOCAL_FORMAT = "yyyy年MM月dd日 HH时mm分ss秒";
	public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String MONTH_FORMAT = "yyyy_MM";
	public static final String CONCISE_TIME_FORMAT="yyyyMMddHHmmss";
	public static final DateTimeFormatter TIME_SIMPLE_FORMAT = DateTimeFormatter.ofPattern(TIME_FORMAT);
	public static final DateTimeFormatter DATE_SIMPLE_FORMAT = DateTimeFormatter.ofPattern(DATE_FORMAT);
	public static final DateTimeFormatter DATE_MONTH_FORMAT = DateTimeFormatter.ofPattern(MONTH_FORMAT);
	public static final DateTimeFormatter CONCISE_SIMPLE_TIME_FORMAT = DateTimeFormatter.ofPattern(CONCISE_TIME_FORMAT);
	private static Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = Maps.newHashMapWithExpectedSize(2);
    private static final Object lockObj = new Object();
	/**返回yyyy-MM-dd HH:mm:ss格式的字符串时间*/
	public static String createTimeString(){
		return TIME_SIMPLE_FORMAT.format(LocalDateTime.now());
	}
	
	/**返回yyyy-MM-dd格式的字符串时间*/
	public static String createDateString(){
		return DATE_SIMPLE_FORMAT.format(LocalDate.now());
	}
	/**返回yyyy-MM-dd格式的字符串时间*/
	public static String createMonthString(){
		return DATE_MONTH_FORMAT.format(LocalDate.now());
	}
	/**返回yyyyMMddHHmmss格式的字符串时间*/
	public static String creatConciseTimeString(){return CONCISE_SIMPLE_TIME_FORMAT.format(LocalDateTime.now());}

	/**根据传入的参数返回yyyy-MM-dd格式的字符串时间*/
	public static String getDateString(Date date){
		return date==null?"":DATE_SIMPLE_FORMAT.format(date.toInstant());
	}
	
	/**根据传入的参数返回yyyy-MM-dd HH:mm:ss格式的字符串时间*/
	public static String getTimeString(Date date){
		return date==null?"":TIME_SIMPLE_FORMAT.format(dateToLocalDateTime(date));
	}
	
	
	/**
	 * 获取一段时间段内的所有具体时间
	 * @author xiao
	 * 
	 * @param dBegin
	 * @param dEnd
	 * @return
	 */
	public static List<Date> findDates(Date dBegin, Date dEnd) {  
        List<Date> dates = new ArrayList<Date>();  
        dates.add(dBegin);  
        Calendar calBegin = Calendar.getInstance();  
        // 使用给定的 Date 设置此 Calendar 的时间    
        calBegin.setTime(dBegin);  
        Calendar calEnd = Calendar.getInstance();  
        // 使用给定的 Date 设置此 Calendar 的时间    
        calEnd.setTime(dEnd);  
        // 测试此日期是否在指定日期之后    
        while (dEnd.after(calBegin.getTime())) {  
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量    
            calBegin.add(Calendar.DAY_OF_MONTH, 1);  
            dates.add(calBegin.getTime());  
        }  
        return dates;  
    }  
	
	
	/**
	 * 获取一段时间段内的所有具体时间<String>
	 * @author xiao
	 * 
	 * @param dBegin
	 * @param dEnd
	 * @return
	 */
	public static List<String> findDatesAsString(Date dBegin, Date dEnd) {  
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<String> dates = new ArrayList<String>();  
        dates.add(sdf.format(dBegin));  
        Calendar calBegin = Calendar.getInstance();  
        // 使用给定的 Date 设置此 Calendar 的时间    
        calBegin.setTime(dBegin);  
        Calendar calEnd = Calendar.getInstance();  
        // 使用给定的 Date 设置此 Calendar 的时间    
        calEnd.setTime(dEnd);  
        // 测试此日期是否在指定日期之后    
        while (dEnd.after(calBegin.getTime())) {  
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量    
            calBegin.add(Calendar.DAY_OF_MONTH, 1);  
            dates.add(sdf.format(calBegin.getTime()));  
        }  
        return dates;  
    }  
	/***
	 * n分钟前时间
	 * @return
	 */
	public static String beforeHalfHour(Date date,int n,String formate){
		if(null==formate){
			formate = "yyyy-MM-dd HH:mm:ss";
		}
		if(date!=null){
			Long time = date.getTime();
			time -= n*60*1000;
			SimpleDateFormat sdf = new SimpleDateFormat(formate);
			return sdf.format(time);
		}
		return "";
	}
	
	/***
	 * n天前时间
	 * @return
	 */
	public static String beforeNDay(Date date,int n,String formate){
		if(null==formate){
			formate = "yyyy-MM-dd HH:mm:ss";
		}
		if(date!=null){
			Long time = date.getTime();
			time -= ((long)n)*60*1000*24*60;
			SimpleDateFormat sdf = new SimpleDateFormat(formate);
			return sdf.format(time);
		}
		return "";
	}
	
	/***
	 * n天前时间
	 * @return
	 */
	public static Date beforeNDayDate(Date date,int n){
		if(date!=null){
			Long time = date.getTime();
			time -= n*60*1000*24*60;
			return new Date(time);
		}
		return new Date();
	}
	
	/***
	 * n分钟前时间
	 * @return
	 */
	public static Date beforeNminDate(Date date,int n){
		if(date!=null){
			Long time = date.getTime();
			time -= n*60*1000;
			Date _time = new Date(time);
			return _time;
		}
		return null;
	}
	/**
	 * 获取两个时间之间的相差的时间
	 * @param endDate
	 * @param nowDate
	 * @return
	 */
	public static String getDatePoor(Date endDate, Date nowDate) {

		long nd = 1000 * 24 * 60 * 60;
		long nh = 1000 * 60 * 60;
		long nm = 1000 * 60;
		// long ns = 1000;
		// 获得两个时间的毫秒时间差异
		long diff = endDate.getTime() - nowDate.getTime();
		// 计算差多少天
		long day = diff / nd;
		// 计算差多少小时
		long hour = diff % nd / nh;
		// 计算差多少分钟
		long min = diff % nd % nh / nm;
		// 计算差多少秒//输出结果
		// long sec = diff % nd % nh % nm / ns;
		return day + "天" + hour + "小时" + min + "分钟";
	}


	/***
	 * n分钟前时间
	 * @return
	 */
	public static Date _beforeHalfHour(Date date,int n,String formate){
		if(null==formate){
			formate = "yyyy-MM-dd HH:mm:ss";
		}
		if(date!=null){
			Long time = date.getTime();
			time -= n*60*1000;
			Date _time = new Date(time);
			return _time;
		}
		return null;
	}
	public static String getNowTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}
	
	/***
	 * 两个时间之间的分钟数
	 * @param time1
	 * @param time2
	 * @param formate
	 * @return
	 */
	public static double getMinuteBetweenTime(String time1,String time2,String formate){
		if(null==formate){
			formate = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(formate);
		Double result = 0.0;
		try {
			Date date1 = sdf.parse(time1);
			Date date2 = sdf.parse(time2);
			result = (double)((date2.getTime()-date1.getTime())/1000/60);
			if(result<0){
				result = (double)(((date2.getTime()+24*60*60*1000)-date1.getTime())/1000/60);
			}
		} catch (Exception e) {
		}
		return result;
	}


	/***
	 * 两个时间之间的分钟数 可正可负
	 * @param time1
	 * @param time2
	 * @param formate
	 * @return
	 */
	public static double getResBetweenTime(String time1,String time2,String formate){
		if(null==formate){
			formate = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(formate);
		Double result = 0.0;
		try {
			Date date1 = sdf.parse(time1);
			Date date2 = sdf.parse(time2);
			result = (double)((date2.getTime()-date1.getTime())/1000/60);
		} catch (Exception e) {
		}
		return result;
	}



	/***
	 * 计算两个时间段之间的日期数组
	 * @param start_time
	 * @param end_time
	 * @return
	 * @throws ParseException
	 */
    public static List<String> findDates(String start_time, String end_time){   
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        List<String> result = new ArrayList<String>();
		try {
			Date date1 = df.parse(start_time);
	        Date date2= df.parse(end_time);
	        double s = (double) ((date2.getTime() - date1.getTime())/ (24 * 60 * 60 * 1000));
	        for(long i = 0;i<=s;i++){
	        	long todayDate = date1.getTime() + i * 24 * 60 * 60 * 1000;
	        	Date tmDate = new Date(todayDate);
	        	result.add(new SimpleDateFormat("yyyy-MM-dd").format(tmDate));
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
        return result;
    }
	
	public static List<Date> dateTimeArr(){
		List<Date> result = new ArrayList<Date>();
		try {
			String date = "2015-8-20 00:30:00";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date _date = sdf.parse(date);//毫秒
			
			Long jDate = new Date().getTime()-_date.getTime();
			int n = (int)(jDate/1000/1800);
			result.add(_date);
			for(int i=0;i<n;i++){
				Date __date = _beforeHalfHour(_date,-30,null);
				_date = __date;
				result.add(__date);
			}
		} catch (Exception e) {
			e.printStackTrace(); 
		}
		return result;
	}
	
	public static String getBeforeYearMonth(){
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH );
		if (0 == month){
			year = year - 1;
			month = 12;
		}
		String monthStr = month+"";
		if(month < 10){
			monthStr = "0"+month;
		}
		return year +"-"+ monthStr;
	}
	
   	public static String getYearMonth(int month){
   		int yearNow = DateUtils.getYear();
   		int mTemp = DateUtils.getMonth()+month;
   		String rTemp = mTemp+"";
   		if(mTemp > 12){
   			yearNow = yearNow +1;
   			mTemp = mTemp - 12;
   		}
   		if(mTemp <= 9){
   			rTemp = "0"+mTemp;
   		}
   		rTemp = yearNow+"-"+rTemp;
   		return rTemp;
   	}


	/***
	 * n天后的日期
	 * @return
	 */
	public static String afterNDay(String date,int n,String formate){
		if(null==formate){
			formate = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(formate);
		try {
			Date date1 = sdf.parse(date);

			if(date!=null){
				Long time = date1.getTime();
				time += ((long)n)*60*1000*24*60;
				return sdf.format(time);
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return "";
		}
		return "";
	}

	/**
	 * M1.0 判断两个时间相差多少天
	 *
	 * @param smdate
	 * @param bdate
	 * @return
	 * @throws ParseException
	 */
	public static int daysBetween(Date smdate, Date bdate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		smdate = sdf.parse(sdf.format(smdate));
		bdate = sdf.parse(sdf.format(bdate));
		Calendar cal = Calendar.getInstance();
		cal.setTime(smdate);
		long time1 = cal.getTimeInMillis();
		cal.setTime(bdate);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);
		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * 计算两个日期之间的间隔天数
	 * case 2019-01-11 ~ 2019-01-11 返回1
	 * case 2019-01-11 ~ 2019-01-12 返回2
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static int getDays(Date startDate, Date endDate) throws ParseException {
		if (endDate.compareTo(startDate) < 0){
			throw new IllegalArgumentException("结束时间小于开始时间");
		}
		return daysBetween(startDate, endDate) + 1;

	}


    /**
     *
     * @param date
     * @return
     * @throws Exception
     */
	public static int yearsBetween(Date date) throws Exception {
		return yearsBetween(date, new Date());
	}
	
	/**
	 * 日期相关年数
	 * @param before
	 * @param next
	 * @return
	 * @throws Exception
	 */
	public static int yearsBetween(Date before, Date next) throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.setTime(next);
		
		if (cal.before(before)) {
			throw new IllegalArgumentException("The beforeDate is before nextDate.It's unbelievable!");
		}
		int yearNext = cal.get(Calendar.YEAR);
		int monthNext = cal.get(Calendar.MONTH);
		int dayOfMonthNext = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTime(before);

		int yearBefore = cal.get(Calendar.YEAR);
		int monthBefore = cal.get(Calendar.MONTH);
		int dayOfMonthBefore = cal.get(Calendar.DAY_OF_MONTH);

		int years = yearNext - yearBefore;

		if (monthNext <= monthBefore) {
			if (monthNext == monthBefore) {
				if (dayOfMonthNext < dayOfMonthBefore) {
					years--;
				}
			} else {
				years--;
			}
		}
		return years;
	}


	/***
	 * n个月之后的日期   2017-11-01  3个月之后  2017-11-01
	 * @return
	 */
	public static String afterNMonth(String date,int n,String formate){
		if(null==formate){
			formate = "yyyy-MM-dd";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(formate);
		try {
			Date now = sdf.parse(date);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(now);
			calendar.add(Calendar.MONTH, n);
			return sdf.format(calendar.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 得到指定月的天数
	 * */
	public static int getMonthLastDay(int year, int month)
	{
		Calendar a = Calendar.getInstance();
		a.set(Calendar.YEAR, year);
		a.set(Calendar.MONTH, month - 1);
		a.set(Calendar.DATE, 1);//把日期设置为当月第一天
		a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
		int maxDate = a.get(Calendar.DATE);
		return maxDate;
	}

	/**
	 * 判断两个时间是否在同一周
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isWeekSame(String date1, String date2){
		Date d1 = null;
		Date d2 = null;
		try{
			ZoneId zoneId = ZoneId.systemDefault();
			d1 =  Date.from(LocalDate.parse(date1, DATE_SIMPLE_FORMAT).atStartOfDay(zoneId).toInstant());
			d2 =  Date.from(LocalDate.parse(date2, DATE_SIMPLE_FORMAT).atStartOfDay(zoneId).toInstant());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setFirstDayOfWeek(Calendar.MONDAY);//西方周日为一周的第一天，咱得将周一设为一周第一天
		cal2.setFirstDayOfWeek(Calendar.MONDAY);
		cal1.setTime(d1);
		cal2.setTime(d2);
		int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);// subYear==0,说明是同一年
		if (subYear == 0){
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
				return true;
		}
		return false;
	}

	public static String getTimeString(String time) {
		Calendar instance = Calendar.getInstance();
		instance.setTimeInMillis(Long.valueOf(time));
		return TIME_SIMPLE_FORMAT.format(dateToLocalDateTime(instance.getTime()));
	}

	private static LocalDate dateToLocalDate(Date date){
		Instant instant = date.toInstant();
		ZoneId zoneId = ZoneId.systemDefault();
		return LocalDateTime.ofInstant(instant, zoneId).toLocalDate();
	}

	private static LocalDateTime dateToLocalDateTime(Date date){
		Instant instant = date.toInstant();
		ZoneId zoneId = ZoneId.systemDefault();
		return LocalDateTime.ofInstant(instant, zoneId);
	}

	public static long strDateParseLong(String str){
		TemporalAccessor accessor = DATE_SIMPLE_FORMAT.parse(str);
		ZoneId zoneId = ZoneId.systemDefault();
		return LocalDate.from(accessor).atStartOfDay(zoneId).toInstant().toEpochMilli();
	}
    public static SimpleDateFormat getSdf(final String pattern) {
        ThreadLocal<SimpleDateFormat> tl = sdfMap.get(pattern);
        // 此处的双重判断和同步是为了防止sdfMap这个单例被多次put重复的sdf
        if (tl == null) {
            synchronized (lockObj) {
                tl = sdfMap.get(pattern);
                if (tl == null) {
                    // 只有Map中还没有这个pattern的sdf才会生成新的sdf并放入map
                    // 这里是关键,使用ThreadLocal<SimpleDateFormat>替代原来直接new SimpleDateFormat
                    tl = new ThreadLocal<SimpleDateFormat>() {

                        @Override
                        protected SimpleDateFormat initialValue() {
                            // pattern: " + pattern);
                            return new SimpleDateFormat(pattern);
                        }
                    };
                    // 把这个线程安全的对象放到对应key 为[pattern] 中去
                    sdfMap.put(pattern, tl);
                }
            }
        }
        return tl.get();
    }

	public static String getStringDate(Long longTime){
		SimpleDateFormat sdf= new SimpleDateFormat(DATE_FORMAT);
		Date date = new Date(longTime);
		return sdf.format(date);
	}
}
