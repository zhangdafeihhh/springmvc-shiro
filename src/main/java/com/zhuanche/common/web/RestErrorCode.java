package com.zhuanche.common.web;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 定义错误码与错误提示
 * @author zhaoyali
 */
public final class RestErrorCode{
	//-----------------------------------------------系统参数
	@ResultMessage("成功")
	public static final int SUCCESS                                   = 0;
	@ResultMessage("获得互斥锁超时")
	public static final int GET_LOCK_TIMEOUT                = 1;
	@ResultMessage("参数错误")
	public static final int PARAMS_ERROR                 =400;
	@ResultMessage("缺少授权")
	public static final int HTTP_UNAUTHORIZED             = 401;
	@ResultMessage("禁止访问")
	public static final int HTTP_FORBIDDEN                     = 403;
	@ResultMessage("资源不存在")
	public static final int HTTP_NOT_FOUND                   = 404;
	@ResultMessage("系统内部发生错误")
	public static final int HTTP_SYSTEM_ERROR              = 500;
	@ResultMessage("服务错误：{0}")
	public static final int CAR_API_ERROR           = 996;
	@ResultMessage("会话已失效，请重新登录")
	public static final int HTTP_INVALID_SESSION           = 997;
	@ResultMessage("请求参数校验不通过{0}")
	public static final int HTTP_PARAM_INVALID              = 998;
	@ResultMessage("未知错误")
	public static final int UNKNOWN_ERROR                   = 999;

	@ResultMessage("记录操作失败")
	public static final int RECORD_DEAL_FAILURE                   = 501;

	//-----------------------------------------------用户
	@ResultMessage("获取验证码太频繁,请{0}分钟后重试")
	public static final int GET_MSGCODE_EXCEED           = 1000;
	@ResultMessage("用户名或密码错误")
	public static final int USER_NOT_EXIST                      = 1001;
	@ResultMessage("用户名或密码错误")
	public static final int USER_PASSWORD_WRONG      = 1002;
	@ResultMessage("用户已被禁用")
	public static final int USER_INVALID                          = 1003;
	@ResultMessage("登录失败")
	public static final int USER_LOGIN_FAILED                = 1004;
	@ResultMessage("短信验证码已经失效")
	public static final int MSG_CODE_INVALID                = 1005;
	@ResultMessage("短信验证码不正确")
	public static final int MSG_CODE_WRONG                = 1006;
	@ResultMessage("账号已经存在")
	public static final int ACCOUNT_EXIST                      = 1007;
	@ResultMessage("短信验证码发送失败")
	public static final int MSG_CODE_FAIL                      = 1011;
	@ResultMessage("短信验证码{0}秒内不能重复发送")
	public static final int MSG_CODE_REPEAT_SEND                      = 1012;
	@ResultMessage("登录太频繁，请{0}分钟后重新登录")
	public static final int DO_LOGIN_FREQUENTLY           = 1013;
	@ResultMessage("手机号已存在")
	public static final int PHONE_EXIST                          = 1014;

	//----------------------------------------------权限管理
	@ResultMessage("父权限不存在")
	public static final int PARENT_PERMISSION_NOT_EXIST           = 10001;
	@ResultMessage("权限代码已经存在")
	public static final int PERMISSION_CODE_EXIST                        = 10002;
	@ResultMessage("权限类型不合法")
	public static final int PERMISSION_TYPE_WRONG                     = 10003;
	@ResultMessage("权限不存在")
	public static final int PERMISSION_NOT_EXIST                          = 10004;
	@ResultMessage("存在已经生效的子权限，请先禁用子权限")
	public static final int PERMISSION_DISABLE_CANT                    = 10005;
	@ResultMessage("父权限已经被禁用，请先启用父权限")
	public static final int PERMISSION_ENABLE_CANT                     = 10006;
	@ResultMessage("{0}为系统预置权限，不能禁用、修改")
	public static final int SYSTEM_PERMISSION_CANOT_CHANGE  = 10007;

	//---------------------------------------------------------------------------------------------------------------------------------------------
	//----------------------------------------------角色管理
	@ResultMessage("角色不存在")
	public static final int ROLE_NOT_EXIST                                      = 10100;
	@ResultMessage("角色代码已经存在")
	public static final int ROLE_CODE_EXIST                                    = 10101;
	@ResultMessage("{0}为系统预置角色，不能禁用、修改")
	public static final int SYSTEM_ROLE_CANOT_CHANGE              = 10102;

	//-----------------------------------------------业务参数：司机
	@ResultMessage("司机不存在，请仔细核对！")
	public static final int DRIVER_NOT_EXIST = 2000;
	@ResultMessage("该司机已存在启用的永久停运！")
	public static final int DRIVER_OUTAGEALL_EXIST = 2001;
	@ResultMessage("该司机已存在启用的临时停运！")
	public static final int DRIVER_OUTAGE_EXIST = 2002;
	@ResultMessage("司机手机号已存在")
	public static final int DRIVER_PHONE_EXIST                     = 3001;
	@ResultMessage("司机身份证已存在")
	public static final int DRIVER_IDCARNO_EXIST                     = 3002;
	@ResultMessage("司机手机号不合法")
	public static final int DRIVER_PHONE_NOT_LEGAL                     = 3003;
	@ResultMessage("司机身份证不合法")
	public static final int DRIVER_IDCARNO_NOT_LEGAL                     = 3004;
	@ResultMessage("银行卡号不合法")
	public static final int DRIVER_BANK_CARD_NUMBER_NOT_LEGAL                     = 3005;
	@ResultMessage("银行卡号和银行开户行不能只填写一个")
	public static final int DRIVER_BANK_CARD_NUMBER_NOT_COMPLETE                     = 3006;
	@ResultMessage("银行卡号已存在")
	public static final int DRIVER_BANK_CARD_NUMBER_EXIST                    = 3007;
	@ResultMessage("车型不存在")
	public static final int MODEL_NOT_EXIST                    = 3008;
	@ResultMessage("信息不全，请补全")
	public static final int INFORMATION_NOT_COMPLETE                    = 3009;
	@ResultMessage("供应商ID={0}的供应商不存在")
	public static final int SUPPLIER_NOT_EXIST                    = 3010;
	@ResultMessage("所选城市和供应商城市不一致")
	public static final int CITY_SUPPLIER_DIFFER                    = 3011;
	@ResultMessage("车辆已经被绑定")
	public static final int CAR_HAS_BIND                    = 3012;
	@ResultMessage("所选城市和供应商、车辆信息不一致")
	public static final int CITY_SUPPLIER_CAR_DIFFER                    = 3013;
	@ResultMessage("服务类型不存在")
	public static final int GROUP_NOT_EXIST                    = 3014;
	@ResultMessage("修改手机号与原手机号不可以相同")
	public static final int PHONE_NEW_SAME                    = 3015;



	@ResultMessage("周报查询时间段只能查询一个星期的时间")
	public static final int ONLY_QUERY_WEEK                    = 3101;
	@ResultMessage("月报查询时间段只能查询一个月份的时间")
	public static final int ONLY_QUERY_ONE_MONTH                    = 3102;
	@ResultMessage("文件导出失败")
	public static final int FILE_EXCEL_REPORT_FAIL                    = 3103;
	@ResultMessage("查询时间范围结束时间不能为空")
	public static final int ENDTIME_IS_NULL                    = 3104;
	@ResultMessage("查询时间范围开始时间不能大于结束时间")
	public static final int STARTTIME_GREATE_ENDTIME                    = 3105;
	@ResultMessage("该司机没有被锁定")
	public static final int DRIVER_NOT_LOCKED                           =3106;

	@ResultMessage("月份大于当前月份")
	public static final int MONTH_IS_BIG                           =3300;
	@ResultMessage("请选择2019年后的日期")
	public static final int CHOOSE_BAD_DATE                           =3301;
	@ResultMessage("请选择2019年后的日期")
	public static final int CHOOSE_BAD_MONTH                           =3302;

	//-----------------------------------------------业务参数：导入文件
	@ResultMessage("文件异常")
	public static final int FILE_ERROR                    = 4001;
	@ResultMessage("导入模板格式错误")
	public static final int FILE_TRMPLATE_ERROR                    = 4002;
    @ResultMessage("导入错误:{0}")
    public static final int FILE_IMPORT_ERROR                    = 4003;
	//-----------------------------------------------业务参数：车辆
	@ResultMessage("车辆信息不存在")
	public static final int BUS_NOT_EXIST                                               = 1100;


	@ResultMessage("结果不存在")
	public static final int NOT_FOUND_RESULT                                               = 1101;
    @ResultMessage("车牌号已存在")
    public static final int LICENSE_PLATES_EXIST                                               = 1102;
    @ResultMessage("车牌号不存在")
    public static final int LICENSE_PLATES_NOT_EXIST                                                = 1103;
	@ResultMessage("请求风控资源失败")
	public static final int RISK_ORDER_DATA_FAIL                                        = 5002;
	@ResultMessage("上传风控文件失败")
	public static final int RISK_UPLOAD_FILE_FAIL                                        = 5003;
	@ResultMessage("提交申诉失败")
	public static final int RISK_SUBMITCOMPLAIN_FAIL                                        = 5004;

	@ResultMessage("查询GPS数据失败")
	public static final int MONITOR_GPS_FAIL                                        = 6001;
	@ResultMessage("查询GPS数据失败,司机信息不存在")
	public static final int MONITOR_GPS_DRIVER_NOT_EXIST                                        = 6002;
	@ResultMessage("查询大数据司机订单信息失败")
	public static final int MONITOR_DRIVERO_ORDER_FAIL                                        = 7001;
	@ResultMessage("查询大数据车辆分析指标趋势信息失败")
	public static final int MONITOR_CARINDEX_FAIL                                        = 7002;
    //-----------------------------------------------业务参数：投诉评分
    @ResultMessage("请选择一个车队或输入司机手机号")
    public static final int TEAMID_OR_DRIVERID_ISNULL = 5201;
    @ResultMessage("文件导出失败")
    public static final int FILE_EXPORT_FAIL = 5202;

	@ResultMessage("存在未审核的申请记录")
	public static final int UPDATE_APPLY_EXIST = 8001;

	//-----------------------------------------------业务参数：数据报表
	@ResultMessage("未查询到相应数据报表任务")
	public static final int SUBSCRIPTION_NOT_EXIST = 7010;
	@ResultMessage("数据报表：{0}")
	public static final int SUBSCRIPTION_INVALID              = 7011;

	@ResultMessage("通知消息内容超过最大字数限制")
	public static final int MESSAGE_CONTENT_ERROR = 8002;
	@ResultMessage("通知消息样式过于复杂")
	public static final int MESSAGE_CONTENT_CSS_TOO_MANY = 8003;
	//---------------------------------------------------------------------------------------------------------------------------------------------
	@ResultMessage("获取分佣信息失败")
	public static final int GET_SUPPLIER_COMMISSION_INFO_FAILED = 9001;
	@ResultMessage("供应商简称无效")
	public static final int GET_SUPPLIER_SHORT_NAME_INVALID = 9002;

	@ResultMessage("文件上传大小超过限制")
	public static final int FILE_SIZE_TOO_BIG = 20001;


	@ResultMessage("小贴士内容超过最大字数限制")
	public static final int TIPS_CONTENT_MAX_LIMIT = 8100;
	@ResultMessage("通知消息样式过于复杂")
	public static final int TIPS_CONTENT_COMPEX = 8101;
	@ResultMessage("标题含有emoji表情或者特殊符号")
	public static final int TIPS_TITLE_EMOJI = 8102;
	@ResultMessage("内容含有emoji表情或者特殊符号")
	public static final int TIPS_CONTENT_EMOJI = 8103;

	@ResultMessage("【评价时间】范围或【完成日期】范围至少限定一个，支持跨度31天")
	public static final int PARAMS_NOT = 8210;

	//----------------------------------------------巴士业务
	@ResultMessage("巴士服务城市权限校验不通过")
	public static final int BUS_CITY_AUTH_FORBIDDEN =801;

	@ResultMessage("巴士服务供应商权限校验不通过")
	public static final int BUS_SUPPLIER_AUTH_FORBIDDEN =802;
	@ResultMessage("正在服务中")
	public static final int INT_SERVICE =803;
	@ResultMessage("该数据正在审核中，暂时不能进行修改")
	public static final int INT_AUDIT_STATUS=804;
	@ResultMessage("{0}")
	public static final int BUS_COMMON_ERROR_CODE=805;

	//----------------------------------------------供应商业务
	@ResultMessage("有申请中修改记录，暂时不能再次申请")
	public static final int SUPPLIER_ACCOUNT_APPLY_EXIST=3110;
	@ResultMessage("该条记录不存在或已更新")
	public static final int SUPPLIER_ACCOUNT_APPLY_UPDATE=3111;

	//----------------------------------------------供应商业务


	//----------------------------------------------千里眼业务
	@ResultMessage("该司机已绑定千里眼，不允许重复绑定！")
	public static final int DRIVER_ACCOUNT_APPLY_EXIST=40001;
	//----------------------------------------------千里眼业务

	//------------------------------------------------新城际拼车业务
	@ResultMessage("该地点不含有城际拼车的线路")
	public static final int UNDEFINED_LINE = 50000;

	@ResultMessage("获取上车点地址失败")
	public static final int GET_ON_ADDRESS_FAILED = 50010;

	@ResultMessage("获取下车点地址失败")
	public static final int GET_OFF_ADDRESS_FAILED = 50011;

	@ResultMessage("预订人注册失败")
	public static final int REGISTER_BY_PHONE = 50012;

	@ResultMessage("上下车点不再围栏区域")
	public static final int ADD_NOT_RIGHT = 50013;

	@ResultMessage("未匹配到线路")
	public static final int UNFINDED_LINE = 50014;

	@ResultMessage("未获取到预估价")
	public static final int UNGET_PRICE = 50015;

	@ResultMessage("创建子单失败")
	public static final int FAILED_CREATE_SUB_ORDER = 50020;

	@ResultMessage("绑定主单失败")
	public static final int BIND_MAIN_FAILED = 50100;
	@ResultMessage("改派订单失败")
	public static final int CHANGE_MAIN_FAILED = 50101;

	@ResultMessage("获取主单失败")
	public static final int FAILED_GET_MAIN_ORDER = 50102;

	@ResultMessage("更新子订单失败")
	public static final int UPDATE_SUB_ORDER_FAILED = 50301;

	@ResultMessage("未获取到订单数据")
	public static final int ORDER_DETAIL_UNDEFINED = 50302;

	@ResultMessage("取消订单失败")
	public static final int CANCEL_FAILED = 50200;

	@ResultMessage("关键字不能为空")
	public static final int KEYWORDS_IS_NOT_NULL = 50501;



	//------------------------------------------------

	//-------------------金融商品--start-------------------------

	@ResultMessage("查询车型库信息失败")
	public static final int QUERY_BASICSVEHICLE_ERROR=80001;

	@ResultMessage("修改商品信息ID参数不能为空")
	public static final int GOODSIDISNULL=80002;

	@ResultMessage("您好,请上传正确格式的文件")
	public static final int CORRECT_FORMAT=80003;

	@ResultMessage("车型车款已经存在")
	public static final int BASICSVEHICLE_EXISTS=80004;

	@ResultMessage("商品已经存在")
	public static final int GOODS_EXISTS=80005;

	@ResultMessage("很抱歉{0}车型车款已经停用！")
	public static final int ENABLE_STATUS=80006;
	//-------------------金融商品--end---------------------------
	private static final Logger log = LoggerFactory.getLogger(RestErrorCode.class);
	private static Map<Integer,String> codeMsgMappings  = new HashMap<Integer,String>();//错误码与错误文字的映射关系
	static{
		try {
			Field[] fields = RestErrorCode.class.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				ResultMessage annotation = field.getAnnotation(ResultMessage.class);
				if(annotation==null) {
					continue;
				}
				int resultCode = field.getInt(null);
				if (codeMsgMappings.containsKey(resultCode)) {//错误码定义发生冲突
					String text = "["+RestErrorCode.class.getName()+"]错误码定义发生冲突，应用进程已经退出，请解决冲突并重启服务！";
					log.info(text);
					System.exit(-1);
				}
				String resultMsg = annotation.value();
				if (null != resultMsg && !"".equals(resultMsg.trim())) {
					codeMsgMappings.put(resultCode, resultMsg.trim());
				}
			}
		} catch (Exception e) {
			log.error("初始化错误码异常！", e);
		}
	}
	/**生成错误信息的字符串**/
	public static String renderMsg( int errorCode , Object...  args) {
		String rawErrorMsg = codeMsgMappings.get(errorCode);
		if(rawErrorMsg==null) {
			return "未知错误";
		}
		return MessageFormat.format(rawErrorMsg, args);
	}

	/**生成一个HTML文件，方便生成技术文档**/
	public static void main(String[] args) throws Exception{
		//1.生成表格
		String path = "D:/errcode.html";
		StringBuffer html = new StringBuffer("<table border=1 style='border-collapse:collapse;'>\r\n<thead style='background-color:#ddd;'><tr><th>错误码</th><th>错误描述</th></tr></thead>\r\n");
		RestErrorCode ec = new RestErrorCode();
		Field[] fields = ec.getClass().getDeclaredFields();
		for(Field field:fields){
			field.setAccessible(true);
			ResultMessage emAnnotation =  field.getAnnotation(ResultMessage.class);
			if(emAnnotation==null) {
				continue;
			}
			Object errcode = field.get(ec);
			String errmsg = emAnnotation.value();
			html.append("<tr><td style='border-color:#bbb;'>"+errcode+"</td><td style='border-color:#bbb;'>"+errmsg+"</td></tr>\r\n");
		}
		html.append("</table>");
		FileUtils.writeStringToFile(new File(path), html.toString(),"GBK");
		System.out.println("Write html file to ["+path+"] successfully.");

		//2.生成国际化属性文件
		String propPath   = "D:/globalMessages.properties";
		StringBuffer text = new StringBuffer("#Error Codes\r\n");
		for(Field field:fields){
			field.setAccessible(true);
			ResultMessage emAnnotation =  field.getAnnotation(ResultMessage.class);
			if(emAnnotation==null) {
				continue;
			}
			Object errcode = field.get(ec);
			String errmsg = emAnnotation.value();
			text.append("errorcode."+errcode+" = "+errmsg+"\r\n");
		}
		FileUtils.writeStringToFile(new File(propPath), text.toString(),"UTF-8");
		System.out.println("Write property file to ["+propPath+"] successfully.");
	}
}