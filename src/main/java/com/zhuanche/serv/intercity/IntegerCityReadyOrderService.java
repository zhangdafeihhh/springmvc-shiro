package com.zhuanche.serv.intercity;

import com.zhuanche.common.web.AjaxResponse;

/**
 * @Author fanht
 * @Description
 * @Date 2020/8/26 上午10:32
 * @Version 1.0
 */
public interface IntegerCityReadyOrderService {
    /**
     * 分页查询所有的线路
     * @param pageNum
     * @param pageSize
     * @return
     */
    AjaxResponse orderWrestQuery(Integer pageNum, Integer pageSize);
}
