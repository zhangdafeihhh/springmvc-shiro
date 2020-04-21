package com.zhuanche.dto.rentcar;

import com.alibaba.fastjson.JSON;

/**
 * 司机派单分类型<br>
 *     来源于策略工具组提供的数据
 * @author wuqiang
 * @date 2020.03.16
 */
public class DriverIntegralDispatchScoreType {

    /**
     * 类型编码
     */
    private Integer code;

    /**
     * 类型名称
     */
    private String name;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
