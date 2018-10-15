package com.zhuanche.constant;

public final class Constants {

	private Constants() {
	}

	public static final int STATUS_100 = 100;
	public static final int STATUS_200 = 200;
	public static final int STATUS_ALL = -1;

	public static final int FEED_STATE_ON = 1;
	public static final int FEED_STATE_OFF = 0;

	public static final int LOG_TYPE_LOGIN = 1;
	public static final int LOG_TYPE_OPTION = 2;
	
	public static final int LOG_TYPE_EXIT = 6;//编辑
	public static final int LOG_TYPE_QUERY = 7;//查询
	public static final int LOG_TYPE_AUTH = 8;//司机证件验证
	
	public static final int SETTING_DUTY = 3;//设置班制
	public static final int ARRANGE_DUTY = 4;//司机排班
	public static final int IMPORT_DRIVER_INFO = 5;//司机信息导入
	public static final int DRIVER_SALARY_PERFORMANCE = 10;//司机薪酬绩效

	/** 司机排班相关redis key前缀*/
	public static final String REDISKEYPREFIX_ISINDUTY = "redis_duty_is_induty";
	public static final String REDISKEYPREFIX_ISDUTYTIME = "redis_duty_is_duty_time";
	public static final String REDISKEYPREFIX_DRIVERDUTYINFO = "redis_duty_driver_duty_info";
	
	

	public static final String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
}
