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


	public static final int  CONTRY  = 1; //全国
	public static final int  CITY  = 2;   //城市
	public static final int  CONTRYANDCITY  = 3;   //全国+城市
	public static final int  SUPPY  = 4;  //加盟商
	public static final int  CONTRYANDSUPPY  = 5;  //全国+加盟商
	public static final int  CITYANDSUPPY  = 6; //城市+加盟商
	public static final int  CONTRYANDCITYANDSUPPY  = 7; //全国+城市+加盟商
	public static final int  TEAM  = 8;  //车队
	public static final int  CONTRYANDTEAM  = 9; //全国+城市+加盟商
	public static final int  CITYANDTEAM  = 10; //城市+车队
	public static final int  CONTRYANDCITYANDTEAM  = 11; //全国+城市+车队
	public static final int  SUPPYANDTEAM  = 12; //城市+车队
	public static final int  CONTRYANDSUPPYANDTEAM  = 13; //全国+城市+车队
	public static final int  CITYANDSUPPYANDTEAM  = 14;//城市+加盟商+车队
	public static final int  CONTRYANDCITYANDSUPPYANDTEAM  = 15;//全国+城市+加盟商+车队

	public static final String  SEPERATER = ",";

	public static final String ALL_RANGE = "1,2";
	public static final String TITLE = "1";
	public static final String ATTACHMENT = "2";
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String TILDE = "~";

}
