package com.zhuanche.dto.rentcar;

import java.io.Serializable;

/**
 * @Author: nysspring@163.com
 * @Description: 避免导出多次查询
 * @Date: 18:15 2019/5/20
 */
public class DriverComplianceDTO implements Serializable{
    /**
     * 司机id
     */
    private Integer driverId;

    /**
     * 合规类型
     * ext3 合规类型 1:人证合规 2:车证合规 3:双证合规 4:不合规
     */
    private Integer complianceKind;

    /**
     * 合规状态
     * ext2 合规状态 0不合规 1 合规
     */
    private Integer complianceStatus;

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public Integer getComplianceKind() {
        return complianceKind;
    }

    public void setComplianceKind(Integer complianceKind) {
        this.complianceKind = complianceKind;
    }

    public Integer getComplianceStatus() {
        return complianceStatus;
    }

    public void setComplianceStatus(Integer complianceStatus) {
        this.complianceStatus = complianceStatus;
    }
}
