package com.zhuanche.serv.intercity;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.entity.mdbcarmanage.InterCityTeam;

import java.util.List;

/**
 * @Author fanht
 * @Description
 * @Date 2020/7/13 下午4:27
 * @Version 1.0
 */
public interface IntegerCityTeamService {


    /**添加编辑车队*/
    AjaxResponse saveOrupdateTeam(Integer id,Integer cityId,Integer supplierId,String teamName);


    InterCityTeam teamDetail(Integer id);
}
