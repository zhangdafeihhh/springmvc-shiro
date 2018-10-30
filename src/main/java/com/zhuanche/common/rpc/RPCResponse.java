package com.zhuanche.common.rpc;

import java.util.Date;

import com.alibaba.fastjson.JSON;

/**RPC返回数据的封装**/
public final class RPCResponse {
	private int code;//错误码  0为成功
	private int status;//错误码  0为成功
	private String msg;//错误信息
	private Date time; //响应时间
	private Object data;//具体的业务数据

	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	
	
	
	
	//----------------------------------------------------------------------方便的转换方法
	/**当返回的属性比较少时，用这个比较方便（data是JSONObject）**/
	public static  RPCResponse parse( String jsonString ) {
		RPCResponse  resp = JSON.parseObject(jsonString, RPCResponse.class );
		return resp;
	}
	/**当返回的属性非常多时，建议将属性封装成普通JAVA对象（data是自定义的dataClz类的实例），此时用这个比较方便**/
	public static <T> RPCResponse parseObject( String jsonString , Class<T> dataClz ) {
		RPCResponse resp = parse(jsonString);
		if(resp!=null && resp.getData()!=null) {
			T  data	 = 	JSON.parseObject( JSON.toJSONString(resp.getData()),  dataClz  );
			resp.setData( data );
		}
		return resp;
	}
	/**当返回的是分页时，用这个**/
	public static <T> RPCResponse parsePage( String jsonString , Class<T> dataClz ) {
		return null;//待完成，以后要是有分页的响应，再封装吧
	}
}