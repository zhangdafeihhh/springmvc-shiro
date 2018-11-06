package com.zhuanche.dto.rentcar;

import java.util.Set;

public class CarBizCustomerAppraisalExtDTO extends  CarBizCustomerAppraisalDTO {

    private static final long serialVersionUID = 1346427662939958799L;
    private String startTime;

    private String endTime;

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
}
