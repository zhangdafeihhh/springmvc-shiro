package com.zhuanche.dto.busManage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zhuanche.dto.BaseDTO;

import java.math.BigDecimal;

/**
 * @program: car-manage
 * @description:自营司机提现记录
 * @author: niuzilian
 * @create: 2018-10-09 18:42
 **/
@JsonIgnoreProperties(ignoreUnknown = true)
public class withdrawalsRecordDTO extends BaseDTO {
    private String phone;
    private String startDate;
    private String endDate;
    private String name;
    private Integer cityId;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }
}
