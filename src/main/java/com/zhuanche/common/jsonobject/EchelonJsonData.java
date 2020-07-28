package com.zhuanche.common.jsonobject;

import com.alibaba.fastjson.JSONArray;
import com.zhuanche.entity.mdbcarmanage.InterCityEchelon;
import lombok.Data;

import java.util.List;

/**
 * @Author fanht
 * @Description
 * @Date 2020/7/28 下午1:09
 * @Version 1.0
 */
@Data
public class EchelonJsonData {

    private Integer cityId;

    private Integer supplierId;

    private Integer teamId;

    private Integer sort;

    private String echelonMonth;

    private List<InterCityEchelon> echelonList;


    private String jsonArray;
}
