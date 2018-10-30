package com.zhuanche.serv.order.elasticsearch;

/**此类是针对订单ES搜索服务（ http://inside-order-search-api.01zhuanche.com/order/v1/search ） 入参排序的封装**/
public final class OrderSearchOrderBy {
	private String       field;            //排序字段名称
	private String       operator;     //排序顺序（只能是asc或desc）	
	
	public OrderSearchOrderBy(String field, String operator) {
		super();
		this.field = field;
		this.operator = operator;
	}
	
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
}