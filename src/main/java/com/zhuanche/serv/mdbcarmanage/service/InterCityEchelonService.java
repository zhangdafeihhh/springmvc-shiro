package com.zhuanche.serv.mdbcarmanage.service;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.entity.mdbcarmanage.InterCityEchelon;

/**
 * @Author fanht
 * @Description
 * @Date 2020/7/14 下午5:01
 * @Version 1.0
 */
public interface InterCityEchelonService {

    AjaxResponse insertSelective(Integer cityId,
                                 Integer supplierId,
                                 Integer teamId,
                                 String echelonDate,
                                 Integer sort,
                                 String echelonMonth);

    int updateByPrimaryKey(InterCityEchelon record);

}
