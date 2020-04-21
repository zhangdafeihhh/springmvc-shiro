package com.zhuanche.dto.rentcar;

import java.math.BigDecimal;
import java.util.List;

/**
 * 司机服务分计算概括
 *
 * @author wuqiang
 * @date 2020.03.12
 */
public class DriverServiceScoreCalculateGeneralize {

    /**
     * 总服务分
     */
    private BigDecimal serviceScore = BigDecimal.ZERO;

    /**
     * 总基础服务分
     */
    private BigDecimal totalBaseServiceScore = BigDecimal.ZERO;

    /**
     * 所属日期
     */
    private String ownershipDate;

    /**
     * 服务分计算明细集合
     */
    private List<DriverServiceScoreCalculateDetail> calculateDetailList;

    public BigDecimal getServiceScore() {
        return serviceScore;
    }

    public void setServiceScore(BigDecimal serviceScore) {
        this.serviceScore = serviceScore;
    }

    public BigDecimal getTotalBaseServiceScore() {
        return totalBaseServiceScore;
    }

    public void setTotalBaseServiceScore(BigDecimal totalBaseServiceScore) {
        this.totalBaseServiceScore = totalBaseServiceScore;
    }

    public String getOwnershipDate() {
        return ownershipDate;
    }

    public void setOwnershipDate(String ownershipDate) {
        this.ownershipDate = ownershipDate;
    }

    public List<DriverServiceScoreCalculateDetail> getCalculateDetailList() {
        return calculateDetailList;
    }

    public void setCalculateDetailList(List<DriverServiceScoreCalculateDetail> calculateDetailList) {
        this.calculateDetailList = calculateDetailList;
    }
}
