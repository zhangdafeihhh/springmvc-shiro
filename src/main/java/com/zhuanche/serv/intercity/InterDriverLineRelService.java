package com.zhuanche.serv.intercity;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.entity.mdbcarmanage.InterDriverLineRel;

/**
 * @Author fanht
 * @Description
 * @Date 2020/11/3 上午11:30
 * @Version 1.0
 */
public interface InterDriverLineRelService {

    /**
     * 根据用户id查询线路和司机关系
     * @param userId
     * @return
     */
    AjaxResponse queryDetail(Integer userId);


    /**
     * 添加or更新
     * @param id
     * @param driverIds
     * @param lineIds
     * @return
     */
    int addOrUpdateDriverLineRel(Integer id,String driverIds,String lineIds);
}
