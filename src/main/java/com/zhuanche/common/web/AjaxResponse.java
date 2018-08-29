package com.zhuanche.common.web;

/** 统一的Ajax响应封装类
 * 用于当请求为Ajax类型时，返回通用的结果，以便于前端JS框架进行统一封装**/
public final class AjaxResponse{
	private int          code     = RestErrorCode.UNKNOWN_ERROR;//错误代码
	private boolean success = false; //是否成功
	private String      msg      = "";       //错误提示信息
	private Object     data     = null;    //业务数据
	
	private AjaxResponse() {}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	
	//-----------------------------------------------------------------------------------------------------------工厂方法
	public static AjaxResponse success( Object data ) {
		AjaxResponse resp = new AjaxResponse();
		resp.setCode(RestErrorCode.SUCCESS);
		resp.setSuccess(true);
		resp.setMsg(  "成功" );
		resp.setData(data);
		return resp;
	}
	public static AjaxResponse fail( int errorCode, Object... errArgs ) {
		AjaxResponse resp = new AjaxResponse();
		int code = errorCode==RestErrorCode.SUCCESS? RestErrorCode.UNKNOWN_ERROR : errorCode;
		resp.setCode( code );
		resp.setSuccess(false);
		resp.setMsg(  RestErrorCode.renderMsg(code, errArgs) );
		return resp;
	}
}