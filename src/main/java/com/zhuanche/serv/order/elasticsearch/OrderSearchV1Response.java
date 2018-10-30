package com.zhuanche.serv.order.elasticsearch;

/**此类是针对订单ES搜索服务（ http://inside-order-search-api.01zhuanche.com/order/v1/search ） 响应结果的封装**/
public class OrderSearchV1Response {
	private int           code = -1;     //0成功，其余失败	
	private String       msg;     //错误信息	
	private boolean  success = false ;//是否成功
	private PageDTO  data;    //返回数据
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
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public PageDTO getData() {
		return data;
	}
	public void setData(PageDTO data) {
		this.data = data;
	}

}