package com.zhuanche.serv.order.elasticsearch;

import java.util.List;

/**当前的分页信息**/
public class PageDTO{
	private int pageNo;  //当前页
	private int pageSize;//每页大小
	private int totalPage;//总页数
	private int total;       //总条数
	private List<OrderInfoDTO> data;//每页中的数据
	
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public List<OrderInfoDTO> getData() {
		return data;
	}
	public void setData(List<OrderInfoDTO> data) {
		this.data = data;
	}
}
