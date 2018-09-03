package com.zhuanche.request;

import java.io.Serializable;

/**
  * @description: 分页信息封装基类
  *
  * <PRE>
  * <BR>	修改记录
  * <BR>-----------------------------------------------
  * <BR>	修改日期			修改人			修改内容
  * </PRE>
  *
  * @author lunan
  * @version 1.0
  * @since 1.0
  * @create: 2018-05-21 14:01
  *
*/

public class PageRequest implements Serializable {

    private static final long serialVersionUID = 4611865358509946424L;

    /** layui start*/
    private int page = 1;

    private  int limit = 10;
    /** layui end*/

    //当前页
    private int pageNo = 1;
    //每页的记录数
    private  int pageSize = 10;
    //分页查询的开始位置

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

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

}
