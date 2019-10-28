package com.zhuanche.dto.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.DriverInfoInterCity;

/**
 * @Author fanht
 * @Description
 * @Date 2019/10/24 下午2:12
 * @Version 1.0
 */
public class MainOrderDetailDTO extends DriverInfoInterCity{

    private String mainOrder;

    private String mainOrderName;

    private Integer remainSeats;

    private String mainOrderTime;

    private String subRouteName;//子单路线名称

    private String driverIds;

    public String getMainOrder() {
        return mainOrder;
    }

    public void setMainOrder(String mainOrder) {
        this.mainOrder = mainOrder;
    }

    public String getMainOrderName() {
        return mainOrderName;
    }

    public void setMainOrderName(String mainOrderName) {
        this.mainOrderName = mainOrderName;
    }

    public Integer getRemainSeats() {
        return remainSeats;
    }

    public void setRemainSeats(Integer remainSeats) {
        this.remainSeats = remainSeats;
    }

    public String getMainOrderTime() {
        return mainOrderTime;
    }

    public void setMainOrderTime(String mainOrderTime) {
        this.mainOrderTime = mainOrderTime;
    }

    public String getSubRouteName() {
        return subRouteName;
    }

    public void setSubRouteName(String subRouteName) {
        this.subRouteName = subRouteName;
    }

    public String getDriverIds() {
        return driverIds;
    }

    public void setDriverIds(String driverIds) {
        this.driverIds = driverIds;
    }
}
