package com.zhuanche.dto.rentcar;

/**
 * @Author fanht
 * @Description
 * @Date 2019/10/30 下午11:37
 * @Version 1.0
 */
public class CityDto {
    private Integer cityId;

    private String cityName;

    private String code;

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
