package com.zhuanche.entity.busManage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * @program: car-manage
 * @description: 企业费用信息
 * @author: niuzilian
 * @create: 2018-11-07 17:12
 **/
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrgCostInfo implements Serializable {
    /**
     * 机构名称
     */
    private String businessName;
    /**
     *  费用类型：1 预付费；-1 后付费
     */
    private Integer type;
    /**
     * 折扣
     */
    private Double percent;
    /**
     * 企业id
     */
    private Integer businessId;

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Double getPercent() {
        return percent;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }
}
