package com.zhuanche.util;

import java.io.Serializable;
import java.util.List;

public class Page<T> implements Serializable {

    private static final long serialVersionUID = -8485349521817371036L;

    private Integer pageNo;

    private Integer pageSize;

    private Integer total;

    private Integer totalPages;

    private List<T> content;

    public Page(Integer pageNo,Integer pageSize,Integer total,List<T> content){
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.total = total;
        this.content = content;
        totalPages = (total  +  pageSize  - 1) / pageSize;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Boolean hasPrevious(){
        return getPageNo() > 1;
    }

    public Boolean hasNext(){
        return getPageNo() < getTotalPages();
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }
}
