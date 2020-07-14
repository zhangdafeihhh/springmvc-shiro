package com.zhuanche.serv.intercity;

/**
 * @Author fanht
 * @Description
 * @Date 2020/7/13 下午4:27
 * @Version 1.0
 */
public interface IntegerCityTeamService {

    /**添加车队*/
    int addTeam(Integer cityId,Integer supplierId,String teamName);

    /**编辑车队*/
    int updateTeam(Integer id,Integer cityId,Integer supplierId,String teamName);

}
