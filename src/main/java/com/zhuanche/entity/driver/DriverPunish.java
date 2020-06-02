package com.zhuanche.entity.driver;



import com.zhuanche.common.enums.EnumCooperationType;
import com.zhuanche.entity.common.BaseEntity;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * driver_punish
 * @author 
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverPunish extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Integer punishId;

    /**
     * 业务处罚id与其它系统统一
     */
    private String businessId;

    /**
     * 订单Id
     */
    private Long orderId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 处罚类型 (与处理类型表一致mp_config.config_punish_type_base)
     */
    private Integer punishType;

    /**
     * 处罚类型名称
     */
    private String punishTypeName;

    /**
     * 处罚原因
     */
    private String punishReason;

    /**
     * 停运天数
     */
    private BigDecimal stopDay;

    /**
     * 处罚金额
     */
    private BigDecimal punishPrice;

    /**
     * 扣除积分
     */
    private BigDecimal punishIntegral;

    /**
     * 扣除流水
     */
    private BigDecimal punishFlow;

    /**
     * 申诉时间
     */
    private Date appealDate;

    /**
     * 司机ID
     */
    private Integer driverId;

    /**
     * 司机手机号
     */
    private String phone;

    /**
     * 司机姓名
     */
    private String name;

    /**
     * 车牌号
     */
    private String licensePlates;

    /**
     * 合作类型 car_biz_cooperation_type
     */
    private Integer cooperationType;

    private String cooperationTypeName;

    /**
     * 城市ID
     */
    private Integer cityId;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 合作商ID
     */
    private Integer supplierId;

    /**
     * 合作商名称
     */
    private String supplierName;

    /**
     * 车队ID
     */
    private Integer teamId;

    /**
     * 车队名称
     */
    private String teamName;

    /**
     * 当前审核节点(1-车管后台,2-业务平台)车管优先审核
     */
    private Byte currentAuditNode;

    /**
     * 审核节点
     */
    private String auditNode;

    /**
     * 状态 1-待申诉,2-待审核,3-审核通过,4-审核拒绝,5-已驳回,6-已过期
     */
    private Byte status;

    /**
     * 处罚时间
     */
    private Date createDate;

    /**
     * 修改时间
     */
    private Date updateDate;

    /**
     * 停运关联ID
     */
    private String stopId;

    /**
     * 过期时间
     */
    private Date expireDate;

    private String createDateStr;
    private String appealDateStr;

    @Setter@Getter
    private String orderOrigin;

    private BigDecimal dispatchPoints;

    @Setter@Getter
    private Integer channelAppealResult;
    @Setter@Getter
    private Integer channelAppealState;

    /**
     * 限制听单描述
     */
    private String notListening;

    public String getCreateDateStr() {
        return createDateStr;
    }

    public void setCreateDateStr(String createDateStr) {
        this.createDateStr = createDateStr;
    }

    public String getAppealDateStr() {
        return appealDateStr;
    }

    public void setAppealDateStr(String appealDateStr) {
        this.appealDateStr = appealDateStr;
    }

    public Integer getPunishId() {
        return punishId;
    }

    public void setPunishId(Integer punishId) {
        this.punishId = punishId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getPunishType() {
        return punishType;
    }

    public void setPunishType(Integer punishType) {
        this.punishType = punishType;
    }

    public String getPunishTypeName() {
        return punishTypeName;
    }

    public void setPunishTypeName(String punishTypeName) {
        this.punishTypeName = punishTypeName;
    }

    public String getPunishReason() {
        return punishReason;
    }

    public void setPunishReason(String punishReason) {
        this.punishReason = punishReason;
    }

    public BigDecimal getStopDay() {
        return stopDay;
    }

    public void setStopDay(BigDecimal stopDay) {
        this.stopDay = stopDay;
    }

    public BigDecimal getPunishPrice() {
        return punishPrice;
    }

    public void setPunishPrice(BigDecimal punishPrice) {
        this.punishPrice = punishPrice;
    }

    public BigDecimal getPunishIntegral() {
        return punishIntegral;
    }

    public void setPunishIntegral(BigDecimal punishIntegral) {
        this.punishIntegral = punishIntegral;
    }

    public BigDecimal getPunishFlow() {
        return punishFlow;
    }

    public void setPunishFlow(BigDecimal punishFlow) {
        this.punishFlow = punishFlow;
    }

    public Date getAppealDate() {
        return appealDate;
    }

    public void setAppealDate(Date appealDate) {
        this.appealDate = appealDate;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates;
    }

    public Integer getCooperationType() {
        return cooperationType;
    }

    public void setCooperationType(Integer cooperationType) {
        this.cooperationType = cooperationType;
        if(cooperationType!=null){
            this.setCooperationTypeName(EnumCooperationType.getEnumByKey(cooperationType));
        }
    }

    public String getCooperationTypeName() {
        return cooperationTypeName;
    }

    public void setCooperationTypeName(String cooperationTypeName) {
        this.cooperationTypeName = cooperationTypeName;
    }

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

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getAuditNode() {
        return auditNode;
    }

    public void setAuditNode(String auditNode) {
        this.auditNode = auditNode;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Byte getCurrentAuditNode() {
        return currentAuditNode;
    }

    public void setCurrentAuditNode(Byte currentAuditNode) {
        this.currentAuditNode = currentAuditNode;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public BigDecimal getDispatchPoints() {
        return dispatchPoints;
    }

    public void setDispatchPoints(BigDecimal dispatchPoints) {
        this.dispatchPoints = dispatchPoints;
    }

    public String getNotListening() {
        return notListening;
    }

    public void setNotListening(String notListening) {
        this.notListening = notListening;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        DriverPunish other = (DriverPunish) that;
        return (this.getPunishId() == null ? other.getPunishId() == null : this.getPunishId().equals(other.getPunishId()))
                && (this.getBusinessId() == null ? other.getBusinessId() == null : this.getBusinessId().equals(other.getBusinessId()))
                && (this.getOrderId() == null ? other.getOrderId() == null : this.getOrderId().equals(other.getOrderId()))
                && (this.getPunishType() == null ? other.getPunishType() == null : this.getPunishType().equals(other.getPunishType()))
                && (this.getPunishTypeName() == null ? other.getPunishTypeName() == null : this.getPunishTypeName().equals(other.getPunishTypeName()))
                && (this.getPunishReason() == null ? other.getPunishReason() == null : this.getPunishReason().equals(other.getPunishReason()))
                && (this.getStopDay() == null ? other.getStopDay() == null : this.getStopDay().equals(other.getStopDay()))
                && (this.getPunishPrice() == null ? other.getPunishPrice() == null : this.getPunishPrice().equals(other.getPunishPrice()))
                && (this.getPunishIntegral() == null ? other.getPunishIntegral() == null : this.getPunishIntegral().equals(other.getPunishIntegral()))
                && (this.getPunishFlow() == null ? other.getPunishFlow() == null : this.getPunishFlow().equals(other.getPunishFlow()))
                && (this.getAppealDate() == null ? other.getAppealDate() == null : this.getAppealDate().equals(other.getAppealDate()))
                && (this.getDriverId() == null ? other.getDriverId() == null : this.getDriverId().equals(other.getDriverId()))
                && (this.getPhone() == null ? other.getPhone() == null : this.getPhone().equals(other.getPhone()))
                && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
                && (this.getLicensePlates() == null ? other.getLicensePlates() == null : this.getLicensePlates().equals(other.getLicensePlates()))
                && (this.getCooperationType() == null ? other.getCooperationType() == null : this.getCooperationType().equals(other.getCooperationType()))
                && (this.getCityId() == null ? other.getCityId() == null : this.getCityId().equals(other.getCityId()))
                && (this.getCityName() == null ? other.getCityName() == null : this.getCityName().equals(other.getCityName()))
                && (this.getSupplierId() == null ? other.getSupplierId() == null : this.getSupplierId().equals(other.getSupplierId()))
                && (this.getSupplierName() == null ? other.getSupplierName() == null : this.getSupplierName().equals(other.getSupplierName()))
                && (this.getTeamId() == null ? other.getTeamId() == null : this.getTeamId().equals(other.getTeamId()))
                && (this.getTeamName() == null ? other.getTeamName() == null : this.getTeamName().equals(other.getTeamName()))
                && (this.getAuditNode() == null ? other.getAuditNode() == null : this.getAuditNode().equals(other.getAuditNode()))
                && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
                && (this.getCreateDate() == null ? other.getCreateDate() == null : this.getCreateDate().equals(other.getCreateDate()))
                && (this.getUpdateDate() == null ? other.getUpdateDate() == null : this.getUpdateDate().equals(other.getUpdateDate()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getPunishId() == null) ? 0 : getPunishId().hashCode());
        result = prime * result + ((getBusinessId() == null) ? 0 : getBusinessId().hashCode());
        result = prime * result + ((getOrderId() == null) ? 0 : getOrderId().hashCode());
        result = prime * result + ((getPunishType() == null) ? 0 : getPunishType().hashCode());
        result = prime * result + ((getPunishTypeName() == null) ? 0 : getPunishTypeName().hashCode());
        result = prime * result + ((getPunishReason() == null) ? 0 : getPunishReason().hashCode());
        result = prime * result + ((getStopDay() == null) ? 0 : getStopDay().hashCode());
        result = prime * result + ((getPunishPrice() == null) ? 0 : getPunishPrice().hashCode());
        result = prime * result + ((getPunishIntegral() == null) ? 0 : getPunishIntegral().hashCode());
        result = prime * result + ((getPunishFlow() == null) ? 0 : getPunishFlow().hashCode());
        result = prime * result + ((getAppealDate() == null) ? 0 : getAppealDate().hashCode());
        result = prime * result + ((getDriverId() == null) ? 0 : getDriverId().hashCode());
        result = prime * result + ((getPhone() == null) ? 0 : getPhone().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getLicensePlates() == null) ? 0 : getLicensePlates().hashCode());
        result = prime * result + ((getCooperationType() == null) ? 0 : getCooperationType().hashCode());
        result = prime * result + ((getCityId() == null) ? 0 : getCityId().hashCode());
        result = prime * result + ((getCityName() == null) ? 0 : getCityName().hashCode());
        result = prime * result + ((getSupplierId() == null) ? 0 : getSupplierId().hashCode());
        result = prime * result + ((getSupplierName() == null) ? 0 : getSupplierName().hashCode());
        result = prime * result + ((getTeamId() == null) ? 0 : getTeamId().hashCode());
        result = prime * result + ((getTeamName() == null) ? 0 : getTeamName().hashCode());
        result = prime * result + ((getAuditNode() == null) ? 0 : getAuditNode().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getCreateDate() == null) ? 0 : getCreateDate().hashCode());
        result = prime * result + ((getUpdateDate() == null) ? 0 : getUpdateDate().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", punishId=").append(punishId);
        sb.append(", businessId=").append(businessId);
        sb.append(", orderId=").append(orderId);
        sb.append(", punishType=").append(punishType);
        sb.append(", punishTypeName=").append(punishTypeName);
        sb.append(", punishReason=").append(punishReason);
        sb.append(", stopDay=").append(stopDay);
        sb.append(", punishPrice=").append(punishPrice);
        sb.append(", punishIntegral=").append(punishIntegral);
        sb.append(", punishFlow=").append(punishFlow);
        sb.append(", appealDate=").append(appealDate);
        sb.append(", driverId=").append(driverId);
        sb.append(", phone=").append(phone);
        sb.append(", name=").append(name);
        sb.append(", licensePlates=").append(licensePlates);
        sb.append(", cooperationType=").append(cooperationType);
        sb.append(", cityId=").append(cityId);
        sb.append(", cityName=").append(cityName);
        sb.append(", supplierId=").append(supplierId);
        sb.append(", supplierName=").append(supplierName);
        sb.append(", teamId=").append(teamId);
        sb.append(", teamName=").append(teamName);
        sb.append(", auditNode=").append(auditNode);
        sb.append(", status=").append(status);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}