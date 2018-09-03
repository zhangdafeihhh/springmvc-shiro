package com.zhuanche.common.web;
import java.io.File;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	@ResultMessage("缺少授权")
	public static final int HTTP_UNAUTHORIZED             = 401;
	@ResultMessage("禁止访问")
	public static final int HTTP_FORBIDDEN                     = 403;
	@ResultMessage("资源不存在")
	public static final int HTTP_NOT_FOUND                   = 404;
	@ResultMessage("系统内部发生错误")
	public static final int HTTP_SYSTEM_ERROR              = 500;
	@ResultMessage("请求参数校验不通过")
	public static final int HTTP_PARAM_INVALID              = 998;
	@ResultMessage("未知错误")
	public static final int UNKNOWN_ERROR                   = 999;
	
	
	//-----------------------------------------------用户
	@ResultMessage("获取验证码太频繁")
	public static final int GET_MSGCODE_EXCEED           = 1000;
	@ResultMessage("用户不存在")
	public static final int USER_NOT_EXIST                      = 1001;
	@ResultMessage("用户密码不正确")
	public static final int USER_PASSWORD_WRONG      = 1002;
	@ResultMessage("用户已被禁用")
	public static final int USER_INVALID                          = 1003;
	@ResultMessage("登录失败")
	public static final int USER_LOGIN_FAILED                = 1004;
	@ResultMessage("短信验证码已经失效")
	public static final int MSG_CODE_INVALID                = 1005;
	@ResultMessage("短信验证码不正确")
	public static final int MSG_CODE_WRONG                = 1006;
	
	
	
	
	//-----------------------------------------------业务参数：司机
	@ResultMessage("司机不存在，请仔细核对！")
	public static final int DRIVER_NOT_EXIST = 2000;
	@ResultMessage("该司机已存在启用的永久停运！")
	public static final int DRIVER_OUTAGEALL_EXIST = 2001;
//	@ResultMessage("司机状态不是正常启用状态")
//	public static final int DRIVER_STATUS_NOT_ENABLED                     = 1001;
//	@ResultMessage("司机手机号码与身份证号码不符合")
//	public static final int DRIVER_PHONE_IDCARD_NOT_MATCHED      = 1002;
//	@ResultMessage("司机手机号码或司机ID两者必须传入一个")
//	public static final int DRIVER_PHONE_ID_MUST_HAVE_ONE            = 1003;
//	@ResultMessage("登录密码不正确")
//	public static final int DRIVER_LOGIN_PASSWORD_WRONG             = 1004;
//	@ResultMessage("超过每天换车最大次数（{0}次）")
//	public static final int DRIVER_EXCEED_BINDBUS_LIMIT_PERDAY     = 1005;
//	@ResultMessage("无法退出并解绑车辆（您目前有待服务、服务中的任务）")
//	public static final int DRIVER_CAN_NOT_UNBIND_BUS                    = 1006;
//	@ResultMessage("无法选取车辆（您目前有服务中的任务）")
//	public static final int DRIVER_CAN_NOT_BIND_BUS                         = 1007;

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
	@ResultMessage("银行卡号和银行开户行不能只填写一个")
	public static final int DRIVER_BANK_CARD_NUMBER_EXIST                    = 3007;

	//-----------------------------------------------业务参数：导入文件
	@ResultMessage("文件异常")
	public static final int FILE_ERROR                    = 4001;
	@ResultMessage("导入模板格式错误")
	public static final int FILE_TRMPLATE_ERROR                    = 4002;
	//-----------------------------------------------业务参数：车辆
	@ResultMessage("车辆信息不存在")
	public static final int BUS_NOT_EXIST                                               = 1100;
//	@ResultMessage("车辆状态不是正常启用状态，被停止运营")
//	public static final int BUS_STATUS_NOT_ENABLED                           = 1101;
//	@ResultMessage("此车辆已被其他司机选取，请选择其它车辆")
//	public static final int BUS_HAVE_BINDED_DRIVER                             = 1102;
	
	
	//-----------------------------------------------业务参数：公共基础服务
//	@ResultMessage("城市不存在")
//	public static final int CITY_INFO_NOT_EXIST                                      = 1400;
	
	//-----------------------------------------------业务参数：订单
//	@ResultMessage("订单不存在")
//	public static final int ORDER_INFO_NOT_EXIST                                      = 2000;
//	@ResultMessage("预定人手机号或乘客手机号与此订单不相符合")
//	public static final int CUSTOMER_PHONE_NOT_MATCH_ORDER           = 2001;
//	@ResultMessage("订单已经评价")
//	public static final int ORDER_HAD_APPRAISAL                                        = 2002;

	//-----------------------------------------------业务参数：投诉评分
	@ResultMessage("请选择一个车队或输入司机手机号")
	public static final int TEAMID_OR_DRIVERID_ISNULL = 5201;
	@ResultMessage("文件导出失败")
	public static final int FILE_EXPORT_FAIL = 5202;

	
	//---------------------------------------------------------------------------------------------------------------------------------------------
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
					log.error(text);
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