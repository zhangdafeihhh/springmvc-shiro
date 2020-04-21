package com.zhuanche.serv.interCity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.paging.PageDTO;

/**
 * @Author fanht
 * @Description
 * @Date 2020/4/20 下午2:38
 * @Version 1.0
 */
public interface InterCityActivityService {

    /**
     * 查询折扣列表
     * @param discountStatus
     * @param groupId
     * @param ruleId
     * @return
     */
    PageDTO queryList(Integer groupId, Integer discountStatus, Integer ruleId, Integer pageNo, Integer pageSize);

    JSONObject getDetail(Integer id);

    Integer saveOrUpdate(Integer discountId,
                        Integer strategyId,
                        Integer discountType,
                        String discountAmount,
                        String discountStartTime,
                        String discountEndTime,
                        Integer discountStatus);
    JSONArray queryRule();
}
