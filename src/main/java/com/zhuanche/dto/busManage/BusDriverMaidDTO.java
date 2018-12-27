package com.zhuanche.dto.busManage;

import com.zhuanche.dto.BaseDTO;

/**
 * @program: mp-manage
 * @description: 巴士司机分佣扩展类
 * @author: niuzilian
 * @create: 2018-12-01 16:14
 **/
public class BusDriverMaidDTO extends BaseDTO{
    /**
     * 司机手机号
     */
    private String phone;
    /**
     * 城市ID
     */
    private Integer cityId;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 开始时间  yyyy-MM-dd HH:mm:ss
     */
    private String startDate;
    /**
     * 结束时间 yyyy-MM-dd HH:mm:ss
     */
    private String endDate;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
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
}
