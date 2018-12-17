package com.zhuanche.dto.busManage;

import com.zhuanche.dto.BaseDTO;

/**
 * @program: mp-manage
 * @description: 巴士供应商结算单列表DTO
 * @author: niuzilian
 * @create: 2018-12-12 15:19
 **/
public class BusSupplierSettleListDTO extends BaseDTO{
    private String supplierIds;
    private Integer cityId;
    private Integer status;
    private Integer type;
    private String startTime;
    private String endTime;

    public String getSupplierIds() {
        return supplierIds;
    }

    public void setSupplierIds(String supplierIds) {
        this.supplierIds = supplierIds;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }
}
