package com.zhuanche.constant;

/**
 * @Author fanht
 */
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


	/******saas日报异步导出********/
	public static final Integer SAAS_DAILY_EXCEL = 5;

	/*******saas月报导出***********/
	public static final Integer SAAS_MONTH_EXCEL = 6;

	/*******saas营业报表汇总导出***********/
	public static final Integer SAAS_SUMMARY_EXCEL = 7;

	/*******司机派单信息导出***********/
	public static final Integer SAAS_DRIVER_DISPATCH = 14;



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
	public static final String ALL_RANGE_REVERSE = "2,1";
	public static final String TITLE = "1";
	public static final String ATTACHMENT = "2";
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String TILDE = "~";

	public static final int MAX_CONTENT_LENGTH = 2000;

	public static final String QUERY_DATE="2018-01-01";
	public static final Integer WEEK=1; //周报
	public static final Integer MONTH=2;//月报

	public static final String UPDATE = "UPDATE";
	public static final String CREATE = "CREATE";

	public static final String SAVE_SUPPLIER_ERROR = "保存供应商失败";
	public static final String SAVE_SUPPLIER_SUCCESS = "保存供应商成功";

	public static final String SUPPLIER_TOPIC = "vipSupplierTopic";
	public static final String SUPPLIER_MQ_SEND_FAILED = "专车供应商发送MQ失败";

	public static final int SUCCESS_CODE = 0;
	public static final String CODE = "code";
	public static final String DRIVER = "driver";
	public static final String DATA = "data";
	public static final String FIRST_ORDER_NO = "firstOrderNo";
	public static final String SUPPLIER_ID = "supplierId";
	public static final String GROUP_ID = "groupId";
	public static final String GROUP_NAME = "groupName";
	public static final String GROUP_INFO_TAG = "groupInfo";
	public static final String DRIVER_INFO_TAG = "driverInfo";
	public static final String ORDER_INFO_TAG = "orderInfo";
	public static final String TOTAL = "total";
	public static final String RECORD_LIST = "recordList";
	public static final String PAGE_NO = "pageNo";
	public static final String PAGE_NUM = "pageNum";
	public static final String LIST = "list";
	public static final int ZERO = 0;
	public static final String SUPPLIER_NAME_AVAILABLE = "供应商名称可以使用";
	public static final String SUPPLIER_NAME_EXIST = "供应商名称已存在";

	public static final String RIDER_PHONE = "riderPhone";
	public static final String BOOKING_USER_PHONE = "bookingUserPhone";

	public static final String PRORATE_ID = "prorateId";
	public static final String ACTIVE_START_DATE = "activeStartDate";
	public static final String ACTIVE_END_DATE = "activeEndDate";

	public static final String RESULT = "result";

	public static final String DRIVER_PHONE = "driverPhone";

	/**折扣金额*/
	public static final String COUPON_SETTLE_AMOUNT = "couponSettleAmout";

	/**是否使用折扣金额*/
	public static final String IS_COUPON = "isCoupon";

	public static final String ORDERNO = "orderNo";

	/**新城际拼车类型**/
	public static final Integer INTEGER_SERVICE_TYPE = 68;
	/**调用预估价标志**/
	public static final String SAAS_PRECE = "saas_pc";

	public static final String EROR_DATE = "NaN-NaN-NaN";

	/**降序排序**/
	public static final String BOOKING_DATE_SORT_DESC = "1";
	/**升序排序**/
	public static final String BOOKING_DATE_SORT_ASC = "2";
	/**不走管理费模式*/
	public static final Integer PAYFLAG = 13;

	public static final String PING_SETTLE_TYPE = "1";



	/**短横线*/
	public static final String SHORT_STOKE = "-";
	/**城际拼车类型*/
	public static final Integer INTER_CITY_SERVICE_TYPE = 1;
    /**包车类型*/
	public static final Integer INTER_CITY_CHARTER_TYPE = 2;

	/**========json解析常用变量===========**/

 	/**分销商id*/
	public static final String  DISTRIBUTORID = "distributorId";

	public static final String  DISTRIBUTORNAME = "distributorName";

	public static final String  LINEID = "lineId";

	public static final String SPLIT = ";";

	/**订单*/
	public static final Integer ORDER_RETURN_CODE = 249;


	public static final String MEMO = "memo";

	public static final String BOOKING_USER_NAME = "bookingUserName";

	public static final String ORDER_ADDRESS = "orderAddress";

	public static final Integer ORDER_STATUS = 13;

	public static final String MAIN_ORDER_NO = "mainOrderNo";

	public static final String ESTIMATED = "estimated";

	public static final  String ESTIMATED_KEY = "estimatedKey";

	public static final  String PING_SIGN = "pingSign";

	public static final String NEW_CROSS_SERVICE_TYPE = "offlineIntercityServiceType";

	public static final String STRATEGY_LIST = "strategyList";

	public static final String CITY_ID = "cityId";

	public static final Integer IS_REDUCT_DISCOUNT_FALSE = 0;

	public static final Integer IS_REDUCT_DISCOUNT_TRUE = 1;


	public static final Integer IS_COUPLE_SETTLE_AMOUNT = 1;

	public static final Integer NOT_COUPLE_SETTLE_AMOUNT = 0;

	/**包车乘车人数*/
	public static final Integer BAO_CHE_RIDER_COUNT = 99;

	public static final String AND = "&";

	public static final Integer VERIFY_HOUR = 2;

	/**验证司机的状态*/
	public static final String VERIFY_STATUS = "15,20,25,30";
}
