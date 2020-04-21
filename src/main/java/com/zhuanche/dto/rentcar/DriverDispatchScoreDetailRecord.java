package com.zhuanche.dto.rentcar;

import com.zhuanche.util.DateUtil;

import java.math.BigDecimal;

/**
 * 司机派单分明细记录
 *
 * @author wuqiang
 * @date 2020.03.10
 */
public class DriverDispatchScoreDetailRecord {

    /**
     * 司机ID
     */
    private Integer driverId;

    /**
     * 司机姓名
     */
    private String driverName;

    /**
     * 司机手机号
     */
    private String driverPhone;

    /**
     * 类型名称<br>
     *     派单分分类
     */
    private String typeName;

    /**
     * 具体类型名称
     */
    private String detailTypeName;

    /**
     * 分值
     */
    private BigDecimal itemScore;

    /**
     * 关联单号
     */
    private String orderNo;

    /**
     * 备注
     */
    private String scoreDesc;

    /**
     * 更新时间戳
     */
    private long updateTime;

    /**
     * 更新时间<br>
     *     示例：2020-02-28
     */
    private String updateDate;

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDetailTypeName() {
        return detailTypeName;
    }

    public void setDetailTypeName(String detailTypeName) {
        this.detailTypeName = detailTypeName;
    }

    public BigDecimal getItemScore() {
        return itemScore;
    }

    public void setItemScore(BigDecimal itemScore) {
        this.itemScore = itemScore;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getScoreDesc() {
        return scoreDesc;
    }

    public void setScoreDesc(String scoreDesc) {
        this.scoreDesc = scoreDesc;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
        if(updateTime > 0){
            this.updateDate = DateUtil.getStringDate(updateTime);
        }
    }

    public String getUpdateDate() {
        return updateDate;
    }

}
