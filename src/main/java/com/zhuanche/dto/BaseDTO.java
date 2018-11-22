package com.zhuanche.dto;

import java.io.Serializable;

public class BaseDTO implements Serializable {

	private static final long serialVersionUID = -4735370780290630864L;

	/** 页码 **/
	protected Integer pageNum;

	/** 每页条数 **/
	protected Integer pageSize;
	

	public BaseDTO() {
		super();
		this.setPageNum(null);
		this.setPageSize(null);
	}

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum == null ? 0 : pageNum;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize == null ? 30 : pageSize;
	}

}
