package com.zhuanche.common.web;

import com.alibaba.fastjson.JSON;

/** 统一的Ajax响应封装类
 * 用于当请求为Ajax类型时，返回通用的结果，以便于前端JS框架进行统一封装**/
public final class AjaxResponse{
	private boolean success = false; //是否成功
	private String      errMsg = "";       //错误提示信息
	private Object     data     = null;    //业务数据
	
	private AjaxResponse() {}
	public AjaxResponse(boolean success, String errMsg, Object data) {
		super();
		this.success = success;
		this.errMsg = errMsg;
		this.data = data;
	}
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	
	//-----------------------------------------------------------------------------------------------------------工厂方法
	public static AjaxResponse success( String errMsg, Object data ) {
		AjaxResponse resp = new AjaxResponse( true, errMsg, data);
		return resp;
	}
	public static AjaxResponse fail(String errMsg, Object data ) {
		AjaxResponse resp = new AjaxResponse( false, errMsg, data);
		return resp;
	}
	

	//------------------------------------------------------------------------------------------------------------debug
	public static void main(String[] args) {
		AjaxResponse AjaxResponse1 = AjaxResponse.success("成功了",   "DATA23432");
		System.out.println(  JSON.toJSONString(AjaxResponse1, true)  );

		AjaxResponse AjaxResponse2 = AjaxResponse.fail("失败了",   "DATA11111111");
		System.out.println(  JSON.toJSONString(AjaxResponse2, true)  );
	}
}