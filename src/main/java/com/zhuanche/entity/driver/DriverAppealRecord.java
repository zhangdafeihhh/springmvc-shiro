package com.zhuanche.entity.driver;

import com.zhuanche.util.DateUtil;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DriverAppealRecord {
    private static final long serialVersionUID = 1L;

    private Integer appealId;

    /**
     * 订单处罚id与处罚表关联
     */
    private Integer punishId;

    /**
     * 申诉人ID
     */
    private Integer driverId;

    /**
     * 申诉人姓名
     */
    private String appealName;

    /**
     * 处罚类型 (与处理类型表一致mp_config.config_punish_type_base)
     */
    private Integer punishType;

    /**
     * 申诉原因
     */
    private String appealReason;

    /**
     * 状态 2-待处理,3-审核通过,4-审核拒绝,5-已驳回',
     */
    private Byte status;

    /**
     * 车管_处理人
     */
    private String cgOperator;

    /**
     * 车管_处理时间
     */
    private Date cgOperateDate;

    /**
     * 车管_处理原因
     */
    private String cgReason;

    /**
     * 车管_状态 2-待处理,3-审核通过,4-审核拒绝,5-已驳回',
     */
    private Byte cgStatus;

    /**
     * 车管_处罚时长
     * cgOperateDate与createDate之间的差
     */
    private String cgPunishTime;

    /**
     * 业务_处理人
     */
    private String ywOperator;

    /**
     * 业务_处理时间
     */
    private Date ywOperateDate;

    /**
     * 业务_处理原因(展示给司机)
     */
    private String ywShowReason;

    /**
     * 业务_处理原因(内部显示)
     */
    private String ywReason;

    /**
     * 业务_状态 2-待处理,3-审核通过,4-审核拒绝,5-已驳回',
     */
    private Byte ywStatus;

    /**
     * 业务_处罚时长
     * ywOperateDate与createDate之间的差
     */
    private String ywPunishTime;

    /**
     * 申诉时间
     */
    private Date createDate;

    /**
     * 修改时间
     */
    private Date updateDate;

    /**
     * 申诉图片(多图片分割符,)
     */
    private String appealPictureUrl;

    private List<String> appealPictureUrlList;

    private String cgOperateDateStr;//车管_处理时间
    private String ywOperateDateStr;//业务_处理时间
    private String createDateStr;//创建时间
    private String updateDateStr;//修改时间


    public Integer getAppealId() {
        return appealId;
    }

    public void setAppealId(Integer appealId) {
        this.appealId = appealId;
    }

    public Integer getPunishId() {
        return punishId;
    }

    public void setPunishId(Integer punishId) {
        this.punishId = punishId;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getAppealName() {
        return appealName;
    }

    public void setAppealName(String appealName) {
        this.appealName = appealName;
    }

    public Integer getPunishType() {
        return punishType;
    }

    public void setPunishType(Integer punishType) {
        this.punishType = punishType;
    }

    public String getAppealReason() {
        return appealReason;
    }

    public void setAppealReason(String appealReason) {
        this.appealReason = appealReason;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getCgOperator() {
        return cgOperator;
    }

    public void setCgOperator(String cgOperator) {
        this.cgOperator = cgOperator;
    }

    public Date getCgOperateDate() {
        return cgOperateDate;
    }

    public void setCgOperateDate(Date cgOperateDate) {
        this.cgOperateDate = cgOperateDate;
    }

    public String getCgReason() {
        return cgReason;
    }

    public void setCgReason(String cgReason) {
        this.cgReason = cgReason;
    }

    public String getYwOperator() {
        return ywOperator;
    }

    public void setYwOperator(String ywOperator) {
        this.ywOperator = ywOperator;
    }

    public Date getYwOperateDate() {
        return ywOperateDate;
    }

    public void setYwOperateDate(Date ywOperateDate) {
        this.ywOperateDate = ywOperateDate;
    }

    public String getYwShowReason() {
        return ywShowReason;
    }

    public void setYwShowReason(String ywShowReason) {
        this.ywShowReason = ywShowReason;
    }

    public String getYwReason() {
        return ywReason;
    }

    public void setYwReason(String ywReason) {
        this.ywReason = ywReason;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
        if(this.getCgOperateDate()!=null&&createDate!=null){
            this.setCgPunishTime(DateUtil.getDatePoor(this.getCgOperateDate(), createDate));
        }
        if(this.getYwOperateDate()!=null&&createDate!=null){
            this.setYwPunishTime(DateUtil.getDatePoor(this.getYwOperateDate(), createDate));
        }
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getAppealPictureUrl() {
        return appealPictureUrl;
    }

    public void setAppealPictureUrl(String appealPictureUrl) {
        this.appealPictureUrl = appealPictureUrl;
        if(StringUtils.isNotEmpty(appealPictureUrl)){
            String[] urls = appealPictureUrl.split(",");
            List<String> list = new ArrayList<String>();
            for (int i = 0; i < urls.length; i++) {
                list.add(urls[i]);
            }
            this.appealPictureUrlList = list;
        }
    }

    public String getCgOperateDateStr() {
        return cgOperateDateStr;
    }

    public void setCgOperateDateStr(String cgOperateDateStr) {
        this.cgOperateDateStr = cgOperateDateStr;
    }

    public String getYwOperateDateStr() {
        return ywOperateDateStr;
    }

    public void setYwOperateDateStr(String ywOperateDateStr) {
        this.ywOperateDateStr = ywOperateDateStr;
    }

    public String getCreateDateStr() {
        return createDateStr;
    }

    public void setCreateDateStr(String createDateStr) {
        this.createDateStr = createDateStr;
    }

    public String getUpdateDateStr() {
        return updateDateStr;
    }

    public void setUpdateDateStr(String updateDateStr) {
        this.updateDateStr = updateDateStr;
    }

    public Byte getCgStatus() {
        return cgStatus;
    }

    public void setCgStatus(Byte cgStatus) {
        this.cgStatus = cgStatus;
    }

    public String getCgPunishTime() {
        return cgPunishTime;
    }

    public void setCgPunishTime(String cgPunishTime) {
        this.cgPunishTime = cgPunishTime;
    }

    public Byte getYwStatus() {
        return ywStatus;
    }

    public void setYwStatus(Byte ywStatus) {
        this.ywStatus = ywStatus;
    }

    public String getYwPunishTime() {
        return ywPunishTime;
    }

    public void setYwPunishTime(String ywPunishTime) {
        this.ywPunishTime = ywPunishTime;
    }

    public List<String> getAppealPictureUrlList() {
        return appealPictureUrlList;
    }

    public void setAppealPictureUrlList(List<String> appealPictureUrlList) {
        this.appealPictureUrlList = appealPictureUrlList;
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
        DriverAppealRecord other = (DriverAppealRecord) that;
        return (this.getAppealId() == null ? other.getAppealId() == null : this.getAppealId().equals(other.getAppealId()))
                && (this.getPunishId() == null ? other.getPunishId() == null : this.getPunishId().equals(other.getPunishId()))
                && (this.getDriverId() == null ? other.getDriverId() == null : this.getDriverId().equals(other.getDriverId()))
                && (this.getAppealName() == null ? other.getAppealName() == null : this.getAppealName().equals(other.getAppealName()))
                && (this.getPunishType() == null ? other.getPunishType() == null : this.getPunishType().equals(other.getPunishType()))
                && (this.getAppealReason() == null ? other.getAppealReason() == null : this.getAppealReason().equals(other.getAppealReason()))
                && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
                && (this.getCgOperator() == null ? other.getCgOperator() == null : this.getCgOperator().equals(other.getCgOperator()))
                && (this.getCgOperateDate() == null ? other.getCgOperateDate() == null : this.getCgOperateDate().equals(other.getCgOperateDate()))
                && (this.getCgReason() == null ? other.getCgReason() == null : this.getCgReason().equals(other.getCgReason()))
                && (this.getYwOperator() == null ? other.getYwOperator() == null : this.getYwOperator().equals(other.getYwOperator()))
                && (this.getYwOperateDate() == null ? other.getYwOperateDate() == null : this.getYwOperateDate().equals(other.getYwOperateDate()))
                && (this.getYwShowReason() == null ? other.getYwShowReason() == null : this.getYwShowReason().equals(other.getYwShowReason()))
                && (this.getYwReason() == null ? other.getYwReason() == null : this.getYwReason().equals(other.getYwReason()))
                && (this.getCreateDate() == null ? other.getCreateDate() == null : this.getCreateDate().equals(other.getCreateDate()))
                && (this.getUpdateDate() == null ? other.getUpdateDate() == null : this.getUpdateDate().equals(other.getUpdateDate()))
                && (this.getAppealPictureUrl() == null ? other.getAppealPictureUrl() == null : this.getAppealPictureUrl().equals(other.getAppealPictureUrl()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getAppealId() == null) ? 0 : getAppealId().hashCode());
        result = prime * result + ((getPunishId() == null) ? 0 : getPunishId().hashCode());
        result = prime * result + ((getDriverId() == null) ? 0 : getDriverId().hashCode());
        result = prime * result + ((getAppealName() == null) ? 0 : getAppealName().hashCode());
        result = prime * result + ((getPunishType() == null) ? 0 : getPunishType().hashCode());
        result = prime * result + ((getAppealReason() == null) ? 0 : getAppealReason().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getCgOperator() == null) ? 0 : getCgOperator().hashCode());
        result = prime * result + ((getCgOperateDate() == null) ? 0 : getCgOperateDate().hashCode());
        result = prime * result + ((getCgReason() == null) ? 0 : getCgReason().hashCode());
        result = prime * result + ((getYwOperator() == null) ? 0 : getYwOperator().hashCode());
        result = prime * result + ((getYwOperateDate() == null) ? 0 : getYwOperateDate().hashCode());
        result = prime * result + ((getYwShowReason() == null) ? 0 : getYwShowReason().hashCode());
        result = prime * result + ((getYwReason() == null) ? 0 : getYwReason().hashCode());
        result = prime * result + ((getCreateDate() == null) ? 0 : getCreateDate().hashCode());
        result = prime * result + ((getUpdateDate() == null) ? 0 : getUpdateDate().hashCode());
        result = prime * result + ((getAppealPictureUrl() == null) ? 0 : getAppealPictureUrl().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", appealId=").append(appealId);
        sb.append(", punishId=").append(punishId);
        sb.append(", driverId=").append(driverId);
        sb.append(", appealName=").append(appealName);
        sb.append(", punishType=").append(punishType);
        sb.append(", appealReason=").append(appealReason);
        sb.append(", status=").append(status);
        sb.append(", cgOperator=").append(cgOperator);
        sb.append(", cgOperateDate=").append(cgOperateDate);
        sb.append(", cgReason=").append(cgReason);
        sb.append(", ywOperator=").append(ywOperator);
        sb.append(", ywOperateDate=").append(ywOperateDate);
        sb.append(", ywShowReason=").append(ywShowReason);
        sb.append(", ywReason=").append(ywReason);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", appealPictureUrl=").append(appealPictureUrl);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}