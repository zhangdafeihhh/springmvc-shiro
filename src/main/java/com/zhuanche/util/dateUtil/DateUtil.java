package com.zhuanche.util.dateUtil;



import com.zhuanche.util.BigDecimalUtil;
import com.zhuanche.util.Check;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>日期工具类</p>
 * 
 * <PRE>
 * <BR>	修改记录
 * <BR>-----------------------------------------------
 * <BR>	修改日期			修改人			修改内容
 * <PRE>
 * 
 * @author lunan
 * @since 1.0
 * @version 1.0
 */
public class DateUtil {

	public enum IntervalUnit {
		MILLISECOND, SECOND, MINUTE, HOUR, DAY, WEEK, MONTH, YEAR
	}

	private static final Map<String, ThreadLocal<SimpleDateFormat>> timestampFormatPool = new HashMap<String, ThreadLocal<SimpleDateFormat>>();

	private static final Map<String, ThreadLocal<SimpleDateFormat>> dateFormatPool = new HashMap<String, ThreadLocal<SimpleDateFormat>>();

	private static final Object timestampFormatLock = new Object();

	private static final Object dateFormatLock = new Object();

	public static String dateFormatPattern = "yyyy-MM-dd";

	public static String intFormatPattern = "yyyyMMdd";

	public static String timestampPattern = "yyyy-MM-dd HH:mm:ss";

	public static String intTimestampPattern = "yyyyMMddHHmmss";



	private static SimpleDateFormat getDateFormat() {
		ThreadLocal<SimpleDateFormat> tl = dateFormatPool.get(dateFormatPattern);
		if (null == tl) {
			synchronized (dateFormatLock) {
				tl = dateFormatPool.get(dateFormatPattern);
				if (null == tl) {
					tl = new ThreadLocal<SimpleDateFormat>() {
						@Override
						protected synchronized SimpleDateFormat initialValue() {
							return new SimpleDateFormat(dateFormatPattern);
						}
					};
					dateFormatPool.put(dateFormatPattern, tl);
				}
			}
		}
		return tl.get();
	}
	
	private static SimpleDateFormat getDateFormat(final String dateFormatPattern) {
		ThreadLocal<SimpleDateFormat> tl = dateFormatPool.get(dateFormatPattern);
		if (null == tl) {
			synchronized (dateFormatLock) {
				tl = dateFormatPool.get(dateFormatPattern);
				if (null == tl) {
					tl = new ThreadLocal<SimpleDateFormat>() {
						@Override
						protected synchronized SimpleDateFormat initialValue() {
							return new SimpleDateFormat(dateFormatPattern);
						}
					};
					dateFormatPool.put(dateFormatPattern, tl);
				}
			}
		}
		return tl.get();
	}

	private static SimpleDateFormat getTimestampFormat() {
		ThreadLocal<SimpleDateFormat> tl = timestampFormatPool.get(timestampPattern);
		if (null == tl) {
			synchronized (timestampFormatLock) {
				tl = timestampFormatPool.get(timestampPattern);
				if (null == tl) {
					tl = new ThreadLocal<SimpleDateFormat>() {
						@Override
						protected synchronized SimpleDateFormat initialValue() {
							return new SimpleDateFormat(timestampPattern);
						}
					};
					timestampFormatPool.put(timestampPattern, tl);
				}
			}
		}
		return tl.get();
	}


	
	/**
	 * 根据日期格式解析成对应的date
	 * @author lunan
	 * @param str
	 * @param dateFormatPattern
	 * @return
	 * @throws ParseException
	 */
	 public static Date parseDate(String str,String dateFormatPattern){
	 	try {
			SimpleDateFormat sdf = getDateFormat(dateFormatPattern);
			Date d = sdf.parse(str);
			return d;
		}catch (Exception e){
	 		throw new BusinessException(e);
		}
	 }
	 
	/**
	 * 格式化成时间戳格式
	 * @author lunan
	 * @param date	要格式化的日期
	 * @return	"年年年年-月月-日日 时时:分分:秒秒"格式的日期字符串
	 */
	public static String timestampFormat(final Date date) {
		if (date == null) {
			return "";
		}
		return getTimestampFormat().format(date);
	}

	/**
	 * 格式化成时间戳格式
	 * @author lunan
	 * @param datetime	要格式化的日期
	 * @return	"年年年年-月月-日日 时时:分分:秒秒"格式的日期字符串
	 */
	public static String timestampFormat(final long datetime) {
		return getTimestampFormat().format(new Date(datetime));
	}
	

	/**
	 * 将"年年年年-月月-日日 时时:分分:秒秒"格式的日期字符串转换成Long型日期
	 * @author lunan
	 * @param timestampStr	年年年年-月月-日日 时时:分分:秒秒"格式的日期字符串
	 * @return	Long型日期
	 */
	public static long formatTimestampToLong(final String timestampStr) {
		Date date;
		try {
			//			date = timestampFormat.parse(timestampStr);
			date = getTimestampFormat().parse(timestampStr);
		} catch (final ParseException e) {
			return 0L;
		}
		return date.getTime();
	}

	/**
	 * 格式化成日期格式
	 * @author lunan
	 * @param date	要格式化的日期
	 * @return	"年年年年-月月-日日"格式的日期字符串
	 */
	public static String dateFormat(final Date date) {
		if (date == null) {
			return "";
		}
		return getDateFormat().format(date);
	}




	/**
	 * 格式化成日期格式, 根据传过来的日志格式
	 * @author lunan
	 * @param date
	 * @return
	 */
	public static int  intFormat(final Date date) {
		if (date == null) {
			return 0;
		}
		String time =  getDateFormat(intFormatPattern).format(date);
		return Integer.parseInt(time);
	}



	/**
	 * 格式化成日期格式, 根据传过来的日志格式
	 * @author lunan
	 * @param date
	 * @param dateFormatPattern
	 * @return
	 */
	public static String dateFormat(final Date date, String dateFormatPattern) {
		if (date == null) {
			return "";
		}
		return getDateFormat(dateFormatPattern).format(date);
	}

	/**
	 * 格式化成日期格式
	 * @author lunan
	 * @param datetime	要格式化的日期
	 * @return	"年年年年-月月-日日"格式的日期字符串
	 */
	public static String dateFormat(final long datetime) {
		return getDateFormat().format(new Date(datetime));
	}

	/**
	 * 将"年年年年-月月-日日"格式的日期字符串转换成Long型日期
	 * @author lunan
	 * @param dateStr	"年年年年-月月-日日"格式的日期字符串
	 * @return	Long型日期
	 * @throws BusinessException	日期格式化异常
	 */
	public static long formatDateToLong(final String dateStr) throws BusinessException {
		Date date;
		try {
			date = getDateFormat().parse(dateStr);
		} catch (final ParseException e) {
			throw new BusinessException("将输入内容格式化成日期类型时出错。", e);
		}
		return date.getTime();
	}


	/**
	 * 得到本月的第一天
	 * @author lunan
	 * @return	以"年年年年-月月-日日"格式返回当前月第一天的日期
	 */
	public static String getFirstDayOfCurrentMonth() {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		return getDateFormat().format(calendar.getTime());
	}



	/**
	 * 得到月份第一天.以当前月份为基准
	 * @author lunan
	 * @param offset
	 * @return Date
	 */
	public static Date getFirstDayOfMonth(int offset) {
		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH,offset);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MILLISECOND,0);
		return calendar.getTime();
	}


	/**
	 * 得到下个月的第一秒
	 * @return
	 */
	public static Long getFirstMillSecondOfNextMonth(){
		Date d = getFirstDayOfMonth(1);
		return d.getTime();
	}

	/**
	 * 得到下个月的最后一秒
	 * @return
	 */
	public static Long getLastMillSecondOfNextMonth(){
		Date d = getLastDayOfMonth(1);
		return d.getTime()+24*3600*1000L-1000L;
	}

	/**
	 * 得到月份最后一天.以当前月份为基准
	 * @author lunan
	 * @return	以"年年年年-月月-日日"格式返回当前月最后一天的日期
	 */
	public static Date getLastDayOfMonth(int offset) {
		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH,offset);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MILLISECOND,0);
		return calendar.getTime();
	}

	/**
	 * 得到本月的最后一天
	 * @author lunan
	 * @return	以"年年年年-月月-日日"格式返回当前月最后一天的日期
	 */
	public static String getLastDayOfCurrentMonth() {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return getDateFormat().format(calendar.getTime());
	}

	/**
	 * 获取指定日期所在月的第一天
	 * @author lunan
	 * @param date	日期
	 * @return	以"年年年年-月月-日日"格式返回当指定月第一天的日期
	 */
	public static String getFirstDayOfMonth(final Date date) {
		final Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		ca.set(Calendar.DAY_OF_MONTH, 1);
		return getDateFormat().format(ca.getTime());
	}

	/**
	 * 获取指定日期所在月的最后一天
	 * @author lunan
	 * @param date
	 * @return	以"年年年年-月月-日日"格式返回当指定月最后一天的日期
	 */
	public static String getLastDayOfMonth(final Date date) {
		final Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		ca.set(Calendar.DAY_OF_MONTH, 1);
		ca.roll(Calendar.DAY_OF_MONTH, -1);
		return getDateFormat().format(ca.getTime());
	}

	/**
	 * 指定的 日期 yyyy-MM-dd 和当前日期作比较
	 * @param str
	 * @return
	 */
	public static boolean  isBig(String str){
		String lastDay = getLastDayOfMonth(parseDate(str,dateFormatPattern));
		String nowDate =getLastDayOfMonth(new Date());
		Long last = formatDateToLong(lastDay);
		Long now = formatDateToLong(nowDate);
		if(last - now <= 0){
			return  true;
		}
		return false;

	}
	/**
	 * 获取指定日期所在月的最后一天
	 * @author lunan
	 * @param date
	 * @return	以"年年年年-月月-日日"格式返回当指定月最后一天的日期
	 */
	public static Long getLastDayTimeOfMonth(final Date date) {
		final Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		ca.set(Calendar.DAY_OF_MONTH, 1);
		ca.roll(Calendar.DAY_OF_MONTH, -1);
		return ca.getTime().getTime();
	}

	/**
	 * 获取指定日期所在周的第一天
	 * @author lunan
	 * @param date	日期
	 * @return	以"年年年年-月月-日日"格式返回当指定周第一天的日期
	 */
	public static String getFirstDayOfWeek(final Date date) {
		return getDateFormat().format(getFirstDayOfWeekDay(date));
	}


	/**
	 * 获取指定日期所在周的第一天
	 * @author lunan
	 * @param date	日期
	 * @return	以"年年年年-月月-日日"格式返回当指定周第一天的日期
	 */
	public static Date getFirstDayOfWeekDay(final Date date) {
		final Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		ca.set(Calendar.DAY_OF_WEEK, 2);
		return ca.getTime();
	}

	/**
	 * 获取当前日期所在周的周末
	 * @param date
	 * @return
	 */
	private static Calendar lastDayOfWeek(final Date date){
		final Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		//日期减去1防止是周日（国外周日为一周的第一天）
		ca.add(Calendar.DATE,-1);
		//设置为本周的周六,这里不能直接设置为周日，中国本周日和国外本周日不同
		ca.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY);
		//日期往前推移一天
		ca.add(Calendar.DATE,1);
		return ca;
	}

	/**
	 * 获取当前日期所在周的周末
	 * @author lunan
	 * @param date
	 * @return	以"年年年年-月月-日日"
	 */
	public static String getLastDayOfWeek(final Date date) {
		final Calendar ca = lastDayOfWeek(date);
		return getDateFormat().format(ca.getTime());
	}
	
	/**
	 * 获取当前日期所在的周六    第一秒
	 * @param date
	 * @return
	 */
	public static Long getSaturdayOfWeek(final Date date) {
		//周日零点零分零秒
		final Calendar ca = lastDayOfWeek(date);
		ca.set(Calendar.HOUR_OF_DAY,0);
		ca.set(Calendar.MINUTE,0);
		ca.set(Calendar.SECOND,0);
		ca.set(Calendar.MILLISECOND,0);
		//日期减去1天 变为周六零时零分零秒
		ca.add(Calendar.DATE,-1);
		return ca.getTime().getTime();
	}

	/**
	 * 获取当前日期所在的周日    最后一秒
	 * @param date
	 * @return
	 */
	public static Long getLastSecondOfWeek(final Date date) {
		final Calendar ca = lastDayOfWeek(date);
		ca.set(Calendar.HOUR_OF_DAY, 0);
		ca.set(Calendar.MINUTE,0);
		ca.set(Calendar.SECOND,0);
		ca.set(Calendar.MILLISECOND,0);
		ca.add(Calendar.DATE,1);
		return ca.getTime().getTime()-1;
	}

	/**
	 * 获取当前日期的前一天
	 * @author lunan
	 * @return	以"年年年年-月月-日日"格式返回当前日期的前一天的日期
	 */
	public static String getDayBeforeCurrentDate() {
		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		return getDateFormat().format(calendar.getTime());
	}

	/**
	 * 获取指定日期的前一天
	 * @author lunan
	 * @param date
	 * @return	以"年年年年-月月-日日"格式返回指定日期的前一天的日期
	 */
	public static String getDayBeforeDate(final Date date) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, -1);
		return getDateFormat().format(calendar.getTime());
	}

	/**
	 * 获取当前日期的后一天
	 * @author lunan
	 * @return	以"年年年年-月月-日日"格式返回当前日期的后一天的日期
	 */
	public static String getDayAfterCurrentDate() {
		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		return getDateFormat().format(calendar.getTime());
	}

	/**
	 * 获取当前日期的后一天
	 * @author lunan
	 * @return	以"年年年年-月月-日日"格式返回指定日期的后一天的日期
	 */
	public static String getDayAfterDate(final Date date) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, 1);
		return getDateFormat().format(calendar.getTime());
	}

	/**
	 * 功能：判断是否是工作日
	 * @author lunan
	 * @param date
	 * @return
	 */
	public static boolean isWorkDay(Date date) {
		boolean flag = true;
		if (Check.NuNObj(date)){
			throw new BusinessException("异常的时间,判断是否是工作日");
		}
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if(calendar.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY||
				calendar.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY){
			flag = false;
		}
		return flag;
	}


	/**
	 * 
	 * 获取当前时间，精确到秒
	 * @author lunan
	 * @return	精确到秒的当前时间
	 */
	public static int currentTimeSecond() {
		return Long.valueOf(System.currentTimeMillis() / 1000).intValue();
	}

	/**
	 * 替换掉日期格式中所有分隔符（含“-”、“:”及空格）
	 * @author lunan
	 * @param target	字符型目标日期
	 * @return	替换后的结果
	 */
	public static String replaceAllSeparator(final String target) {
		return target.replace("-", "").replace(":", "").replace(" ", "");
	}

	/**
	 * 替换掉日志格式中指定的分隔符
	 * @author lunan
	 * @param target	字符型目标日期
	 * @param separator	要被替换掉的分割符
	 * @return	替换后的结果
	 */
	public static String replaceSeparator(final String target, final String... separator) {
		String temp = target;
		for (final String sep : separator) {
			temp = temp.replace(sep, "");
		}
		return temp;
	}

	/**
	 * 根据步长获取时间
	 * @author lunan
	 * @param interval 步长 ，正数获取将来时间， 负数获取以前的时间
	 * @param unit 步长单位, 年月周日时分秒
	 * @return
	 */
	public static Date intervalDate(final int interval, final IntervalUnit unit) {
		final Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.setLenient(true);
		c.add(translate(unit), interval);
		return c.getTime();
	}

	private static int translate(final IntervalUnit unit) {
		switch (unit) {
		case DAY:
			return Calendar.DAY_OF_YEAR;
		case HOUR:
			return Calendar.HOUR_OF_DAY;
		case MINUTE:
			return Calendar.MINUTE;
		case MONTH:
			return Calendar.MONTH;
		case SECOND:
			return Calendar.SECOND;
		case MILLISECOND:
			return Calendar.MILLISECOND;
		case WEEK:
			return Calendar.WEEK_OF_YEAR;
		case YEAR:
			return Calendar.YEAR;
		default:
			throw new IllegalArgumentException("Unknown IntervalUnit");
		}
	}

	/**
	 * 获取几天前或几天后的日期
	 * @param day
	 *            可为负数,为负数时代表获取之前的日期.为正数,代表获取之后的日期
	 * @return
	 */
	public static Date getTime(final int day) {
		return getTime(new Date(), day);
	}

	/**
	 * 获取指定日期几天前或几天后的日期
	 * @param date
	 *            指定的日期
	 * @param day
	 *            可为负数, 为负数时代表获取之前的日志.为正数,代表获取之后的日期
	 * @return
	 */
	public static Date getTime(final Date date, final int day) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + day);
		return calendar.getTime();
	}




	/**
	 * 功能：判断字符串是否为日期格式
	 * @param strDate
	 * @return
	 */
	public static boolean isDate(String strDate) {
		Pattern pattern = Pattern.compile(
				"^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
		Matcher m = pattern.matcher(strDate);
		if (m.matches()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 获取小时之前的时间串
	 * @author lunan
	 * @param hour 小时
	 * @return
	 */
	public static String getTimeStringOfHourBefore(int hour) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.HOUR, hour*-1);
		return getTimestampFormat().format(calendar.getTime());
	}
	
	/**
	 * 获取两个日期之间的天数
	 * 例如：startDate=2016-04-03   endDate=2016-04-05
	 *     返回：2
	 * @author lunan
	 *
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static int getDatebetweenOfDayNum(Date startDate, Date endDate) {
		Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);  
        long time1 = cal.getTimeInMillis();               
        cal.setTime(endDate);  
        long time2 = cal.getTimeInMillis();       
        long between_days=(time2-time1)/(1000*3600*24);  
          
       return Integer.parseInt(String.valueOf(between_days));  
	}
	
	/**
	 * 获取两个日期间的区间日期
	 * 例如：startDate=2016-04-03   endDate=2016-04-05
	 *     返回：2016-04-03  2016-04-04  2016-04-05  集合
	 * @author lunan
	 *
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static List<Date> getDatebetweenOfDays(Date startDate, Date endDate) {
		List<Date> list = new ArrayList<Date>();
        GregorianCalendar gc1=new GregorianCalendar(),gc2=new GregorianCalendar();   
        gc1.setTime(startDate);   
        gc2.setTime(endDate);   
        do{   
            GregorianCalendar gc3=(GregorianCalendar)gc1.clone();   
            list.add(gc3.getTime());
            gc1.add(Calendar.DAY_OF_MONTH, 1);
         }while(!gc1.after(gc2));   
        return null;   
	}

	/**
	 * 获取跳跃之后的时间：按月
	 * @param current
	 * @param jump
	 * @return
	 */
	public static Date jumpMonth(Date current,int jump) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(current);
		cal.add(Calendar.MONTH, jump);    //跳跃
		return cal.getTime();
	}


	/**
	 * 获取跳跃之后的时间：按天
	 * @param current
	 * @param jump
	 * @return
	 */
	public static Date jumpDate(Date current,int jump) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(current);
		cal.add(Calendar.DATE, jump);    //跳跃
		return cal.getTime();
	}

	/**
	 * 获取跳跃后的时间：按小时
	 * @author lishaochuan
	 * @create 2016年5月12日上午11:14:50
	 * @param current
	 * @param jump
	 * @return
	 */
	public static Date jumpHours(Date current,int jump) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(current);
		cal.add(Calendar.HOUR_OF_DAY, jump);    //跳跃
		return cal.getTime();
	}

	/**
	 * 获取跳跃后的时间：按秒
	 * @author lunan
	 * @create 2016年10月31日
	 * @param current
	 * @param jump
	 * @return
	 */
	public static Date jumpSecond(Date current,int jump) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(current);
		cal.add(Calendar.SECOND, jump);    //跳跃
		return cal.getTime();
	}

	/**
	 * 获取跳跃后的时间：按分钟
	 * @author lishaochuan
	 * @create 2016年5月12日下午1:06:25
	 * @param current
	 * @param jump
	 * @return
	 */
	public static Date jumpMinute(Date current,int jump) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(current);
		cal.add(Calendar.MINUTE, jump);    //跳跃
		return cal.getTime();
	}


	/**
	 * 获取时间的开始时间
	 * @author lunan
	 * @param day
	 * @return
	 */
	public static Date getDayStart(Date day){
		if(Check.NuNObj(day)){
			return null;
		}
		String str = DateUtil.dateFormat(day) + " 00:00:00";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date rst = null;
		try {
			rst =  sdf.parse(str);
		}catch (Exception e){
			throw new BusinessException(e);
		}
		return rst;
	}

	/**
	 * 获取时间的开始时间
	 * @author lunan
	 * @param day
	 * @return
	 */
	public static Date getDayEnd(Date day){
		if(Check.NuNObj(day)){
			return null;
		}
		String str = DateUtil.dateFormat(day) + " 23:59:59";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date rst = null;
		try {
			rst =  sdf.parse(str);
		}catch (Exception e){
			throw new BusinessException(e);
		}
		return rst;
	}



	/**
	 * 将时间拼接在一起
	 * @author lunan
	 * @param day
	 * @param timeStr
	 * @return
	 */
	public static Date connectDate(Date day,String timeStr){
		if(Check.NuNObj(day)){
			return null;
		}
		if(Check.NuNStr(timeStr)){
			return null;
		}
		String str = DateUtil.dateFormat(day) + " " + timeStr.trim();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date rst = null;
		try {
			rst =  sdf.parse(str);
		}catch (Exception e){
			throw new BusinessException(e);
		}
		return rst;
	}



	/**
	 * 两个时间之间相差距离多少天
	 * @param start 时间参数 1：
	 * @param end 时间参数 2：
	 * @return 相差天数
	 */
	public static long getDistanceHours(Date start, Date end){

		if (Check.NuNObjs(start,end)){
			return 0L;
		}

		long hour=0;
		try {
			long time1 = start.getTime();
			long time2 = end.getTime();
			long diff ;
			if(time1<time2) {
				diff = time2 - time1;
			} else {
				diff = time1 - time2;
			}
			double value = BigDecimalUtil.div(diff,1000 * 60 * 60 );
			double flag = Math.ceil(value);       //向上取整计算
			hour = (int)flag;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hour;
	}

	/**
	 * 两个时间之间相差距离多少秒
	 * @param start 时间参数 1：
	 * @param end 时间参数 2：
	 * @return 相差秒数
	 */
	public static long getDistanceSeconds(Date start, Date end) {
		if (Check.NuNObjs(start,end)){
			return 0L;
		}

		long seconds=0;
		try {
			long time1 = start.getTime();
			long time2 = end.getTime();
			long diff ;
			if(time1<time2) {
				diff = time2 - time1;
			} else {
				diff = time1 - time2;
			}
			double value = BigDecimalUtil.div(diff,1000);
			seconds = (long)value;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return seconds;

	}
	
	/**
	 * @Description 获取两个时间相隔月数
	 * @param date1
	 * @param date2
	 * @return
	 * @throws ParseException
	 */
	public static int getMonthSpace(String date1, String date2) {
		int result = 0;
		try {
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        Calendar c1 = Calendar.getInstance();
	        Calendar c2 = Calendar.getInstance();
	        c1.setTime(sdf.parse(date1));
	        c2.setTime(sdf.parse(date2));
	        result = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return result == 0 ? 1 : Math.abs(result);
    }

	public static void main(String[] args) throws Exception{
		System.out.println(isBig("2019-01-29"));
		System.out.println(System.currentTimeMillis());
		//System.out.println(getMonthSpace("2017-08-23","2017-12-12"));
	}


	/**
	 * 获取时间差，返回字符串,返回粗略时间状态
	 * @author lunan
	 * @param begin
	 * @param end
	 * @return
	 */
	public static String getDiffTimeStr(Date begin,Date end){
		if(Check.NuNObj(begin)){
			return null;
		}
		if(Check.NuNObj(end)){
			return null;
		}
		// 除以1000是为了转换成秒
		long between = (end.getTime() - begin.getTime()) / 1000;
		long day = between / (24 * 3600);
		long hour = between % (24 * 3600) / 3600;
		long minute = between % 3600 / 60;
		long second = between % 60 ;
		StringBuilder dateStr = new StringBuilder();
		if(day > 0){
			dateStr.append(day);
			return dateStr.append("天").toString();
		}
		if( hour > 0){
			dateStr.append(hour);
			return dateStr.append("小时").toString();
		}
		if(minute > 0){
			dateStr.append(minute);
			return dateStr.append("分").toString();
		}
		dateStr.append("1分钟");
		return dateStr.toString();
	}



	/**
	 * 获取时间差，返回字符串
	 * @author lunan
	 * @param begin
	 * @param end
	 * @return
	 */
	public static String getDiffTimeFull(Date begin,Date end){
		if(Check.NuNObj(begin)){
			return null;
		}
		if(Check.NuNObj(end)){
			return null;
		}
		long between = (end.getTime() - begin.getTime()) / 1000;// 除以1000是为了转换成秒
		long day = between / (24 * 3600);
		long hour = between % (24 * 3600) / 3600;
		long minute = between % 3600 / 60;
		long second = between % 60 ;
		StringBuilder dateStr = new StringBuilder();
		if(day != 0L){
			dateStr.append(day);
			dateStr.append("天");
		}
		if(day != 0L || hour != 0L){
			dateStr.append(hour);
			dateStr.append("小时");
		}
		if(day != 0L || hour != 0L || minute != 0L){
			dateStr.append(minute);
			dateStr.append("分");
		}
		if(day != 0L || hour != 0L || minute != 0L || second != 0L){
			dateStr.append(second);
			dateStr.append("秒");
		}
		return dateStr.toString();
	}
	
	public static String formatDateToStr(Date day){
		try{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			return format.format(day);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static Date[] getDayStartAndEndTimePoint(Date day){
		try {
			Date[] dayArr = new Date[2];
			String dayStr = formatDateToStr(day);
			String startTimePoint = new StringBuffer().append(dayStr).append(" ").append("00:00:00").toString();
			String endTimePoint = new StringBuffer().append(dayStr).append(" ").append("23:59:59").toString();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timestampPattern);
			dayArr[0] = simpleDateFormat.parse(startTimePoint);
			dayArr[1] = simpleDateFormat.parse(endTimePoint);
			return dayArr;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	public static long  calDateDiff(String startDate, String endDate){
		long days=0;
		try{
			//解析日期
			DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate start = LocalDate.parse(startDate, f);
			LocalDate end = LocalDate.parse(endDate, f);
			LocalDate now = LocalDate.now();
			long toE = end.toEpochDay()-now.toEpochDay();
			if(toE>=0){
				days = now.toEpochDay()-start.toEpochDay();
			}else{
				days = end.toEpochDay()-start.toEpochDay();
				days++;
			}

		}catch (Exception e){
			//logger.error("解析日期异常",e);
		}
		return days;
	}

}
