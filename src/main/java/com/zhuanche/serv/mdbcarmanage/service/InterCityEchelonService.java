package com.zhuanche.serv.mdbcarmanage.service;

import com.alibaba.fastjson.JSONArray;
import com.zhuanche.common.jsonobject.EchelonJsonData;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.dto.mdbcarmanage.InterCityEchelonDto;
import com.zhuanche.dto.mdbcarmanage.InterCityTeamDto;
import com.zhuanche.entity.mdbcarmanage.DriverInfoInterCity;
import com.zhuanche.entity.mdbcarmanage.InterCityEchelon;
import com.zhuanche.entity.mdbcarmanage.InterCityTeam;

import java.util.List;

/**
 * @Author fanht
 * @Description
 * @Date 2020/7/14 下午5:01
 * @Version 1.0
 */
public interface InterCityEchelonService {

    AjaxResponse addOrEdit(EchelonJsonData echelon);

    int updateByPrimaryKey(InterCityEchelon record);


    List<InterCityEchelon> detailList(Integer teamId,String echelonMonth);

    AjaxResponse queryEchelonList(DriverInfoInterCity driverInfoInterCity,
                                  String  echelonMonth,
                                  Integer pageNo,
                                  Integer pageSize);

    List<InterCityTeamDto> queryTeam(Integer cityId,
                                     Integer supplierId);
}
