package com.zhuanche.entity.driver.punish;


import com.zhuanche.entity.common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * driver_punish
 *
 * @author
 */
@Setter
@Getter
public class ConfigPunishTypeBaseEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Integer configid;

    /**
     * 处罚类型的名称，会展现在司机端
     */
    private String punishName;

    /**
     * 发起该处罚的后台（服务）名称
     */
    private String relatedOperationSys;

    /**
     * 申诉时长（司机确定被处罚后可进行申诉的时间）
     */
    private Integer appealDuration;

    /**
     * 二次申诉时长（第一次申诉被驳回后可以再次申诉的时间）
     */
    private Integer appealDurationSecond;

    /**
     * 处理后台 0车管后台 1业务后台 （若为多项则以英文逗号分隔）
     */
    private String dealBackground;

    /**
     * 后台处理时长（若为多项则以英文逗号分隔）
     */
    private String dealDuration;

    /**
     * 超时处理 1自动通过 2 自动拒绝（若为多项则以英文逗号分隔）
     */
    private String dealOvertime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 状态：0禁用 1可用
     */
    private Byte status;

    //----------特殊处罚类型多的字段begin-----------------
    /**
     * 关联基础处罚类型id
     */
    private Integer relatedBasePunishId;

    /**
     * 合作类型 0首约自营 1加盟司机
     */
    private Byte cooperationType;

    /**
     * 服务城市（多个城市id间以英文逗号分隔）
     */
    private String servCitys;
    //----------特殊处罚类型多的字段end-----------------

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
        ConfigPunishTypeBaseEntity other = (ConfigPunishTypeBaseEntity) that;
        return (this.getConfigid() == null ? other.getConfigid() == null : this.getConfigid().equals(other.getConfigid()))
                && (this.getPunishName() == null ? other.getPunishName() == null : this.getPunishName().equals(other.getPunishName()))
                && (this.getRelatedOperationSys() == null ? other.getRelatedOperationSys() == null : this.getRelatedOperationSys().equals(other.getRelatedOperationSys()))
                && (this.getAppealDuration() == null ? other.getAppealDuration() == null : this.getAppealDuration().equals(other.getAppealDuration()))
                && (this.getAppealDurationSecond() == null ? other.getAppealDurationSecond() == null : this.getAppealDurationSecond().equals(other.getAppealDurationSecond()))
                && (this.getDealBackground() == null ? other.getDealBackground() == null : this.getDealBackground().equals(other.getDealBackground()))
                && (this.getDealDuration() == null ? other.getDealDuration() == null : this.getDealDuration().equals(other.getDealDuration()))
                && (this.getDealOvertime() == null ? other.getDealOvertime() == null : this.getDealOvertime().equals(other.getDealOvertime()))
                && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
                && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getConfigid() == null) ? 0 : getConfigid().hashCode());
        result = prime * result + ((getPunishName() == null) ? 0 : getPunishName().hashCode());
        result = prime * result + ((getRelatedOperationSys() == null) ? 0 : getRelatedOperationSys().hashCode());
        result = prime * result + ((getAppealDuration() == null) ? 0 : getAppealDuration().hashCode());
        result = prime * result + ((getAppealDurationSecond() == null) ? 0 : getAppealDurationSecond().hashCode());
        result = prime * result + ((getDealBackground() == null) ? 0 : getDealBackground().hashCode());
        result = prime * result + ((getDealDuration() == null) ? 0 : getDealDuration().hashCode());
        result = prime * result + ((getDealOvertime() == null) ? 0 : getDealOvertime().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", configid=").append(configid);
        sb.append(", punishName=").append(punishName);
        sb.append(", relatedOperationSys=").append(relatedOperationSys);
        sb.append(", appealDuration=").append(appealDuration);
        sb.append(", appealDurationSecond=").append(appealDurationSecond);
        sb.append(", dealBackground=").append(dealBackground);
        sb.append(", dealDuration=").append(dealDuration);
        sb.append(", dealOvertime=").append(dealOvertime);
        sb.append(", createTime=").append(createTime);
        sb.append(", status=").append(status);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}