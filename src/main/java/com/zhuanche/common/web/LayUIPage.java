package com.zhuanche.common.web;

import java.util.ArrayList;
import java.util.List;

/** 分页数据的封装类
 * 即对应于 LayUI （一个WEB前端框架）中的动态TABLE控件，是对分页的json结果进行封装
 * 关于LayUI中的TABLE，详细参数请参考：http://www.layui.com/doc/modules/table.html#async
 **/
 public final class  LayUIPage{
	 /**数据状态**/
	 private int     code    = 0;
	 /**状态信息**/
	 private String msg     = "";
	 /**数据总数**/
	 private int    count    = 0;
	 /**数据列表**/
	 private List data = new ArrayList(1);
	
	private LayUIPage() {}
	private LayUIPage(int code, String msg, int count, List data) {
		super();
		this.code = code;
		this.msg = msg;
		this.count = count;
		this.data = data;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public List getData() {
		return data;
	}
	public void setData(List data) {
		this.data = data;
	}
	
	//-----------------------------------------------------------工厂方法
	public static LayUIPage build( String msg, int total, List data ) {
		if( msg ==null  ) {
			msg = "";
		}
		if( total < 0) {
			total = 0;
		}
		if( data == null ) {
			data = new ArrayList(1);
		}
		LayUIPage resp = new LayUIPage( 0, msg, total ,data );
		return resp;
	}
}