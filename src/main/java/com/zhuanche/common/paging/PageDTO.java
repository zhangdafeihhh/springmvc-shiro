package com.zhuanche.common.paging;

import java.util.List;

/**通用的分页包装类**/
public final class PageDTO{
	/**当前页号**/
    private int page     = 1;
	/**页面大小**/
    private int pageSize = 20;
	/**查询出来的总记录条数**/
    private long total    = 0;
	/**查询出来的结果**/
    @SuppressWarnings("rawtypes")
	private List result;
    
	public PageDTO(){}
	@SuppressWarnings("rawtypes")
	public PageDTO(int page, int pageSize, long total, List result){
		super();
		this.page = page;
		this.pageSize = pageSize;
		this.total = total;
		this.result = result;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
        this.total = (total < 0) ? 0 : total;
        if (this.page > this.getPages()) {
			this.page = this.getPages();
		}        
	}
	@SuppressWarnings("rawtypes")
	public List getResult() {
		return result;
	}
	@SuppressWarnings("rawtypes")
	public void setResult(List result) {
		this.result = result;
	}
	
	//----------------------------------------------------------分页相关BEGIN
    public int getPages() {
    	if (getPageSize() == 0) {
			return 0;
		} else {
			return (int) ((total + getPageSize() - 1) / getPageSize());
		}
    }
    public int getStartNo() {
		int page = Math.max(getPage() - 1, 0);
		return page == 0 ? 0 : page * getPageSize() + 1;
    }
    public long getEndNo() {
		int page = getPage() == 0 ? 1 : getPage();
        return Math.min(page * getPageSize(), total);
    }
    public int getPrePageNo() {
        return Math.max(getPage() - 1, 0);
    }
    public int getNextPageNo() {
		return Math.min(getPage() + 1, getPages());
    }
	//----------------------------------------------------------分页相关END
}