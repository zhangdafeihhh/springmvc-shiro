package com.zhuanche.serv.punish.query;

import com.zhuanche.entity.query.BaseQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author kjeakiry
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DriverPunishQuery extends BaseQuery {

    /**
     * "主键ID"
     */
    private Integer punishId;

    /**
     * "业务处罚id与其它系统统一"
     */
    private String businessId;

    /**
     * "订单Id"
     */
    private Long orderId;

    /**
     * "订单号"
     */
    private String orderNo;

    /**
     * "城市ID"
     */
    private Integer cityId;

    /**
     * "合作商ID"
     */
    private Integer supplierId;

    /**
     * "车队ID"
     */
    private Integer teamId;

    /**
     * "司机ID"
     */
    private Integer driverId;

    /**
     * "司机手机号"
     */
    private String phone;

    /**
     * "司机姓名"
     */
    private String name;

    /**
     * "车牌号"
     */
    private String licensePlates;

    /**
     * "合作类型"
     */
    private Integer cooperationType;

    /**
     * "合作商名称"
     */
    private String cooperationTypeName;

    /**
     * "处罚类型 (与处理类型表一致mp_config.config_punish_type_base)"
     */
    private Integer punishType;

    /**
     * "状态 1-待申诉,2-待审核,3-审核通过,4-审核拒绝,5-已驳回,6-已过期"
     */
    private Integer status;

    /**
     * "当前审核节点(1-车管后台,2-业务平台)车管优先审核"
     */
    private Integer currentAuditNode;

    /**
     * "查询条件: 处罚时间开始 yyyy-MM-dd HH:mm:ss "
     */
    private String createDateStart;

    /**
     * "查询条件: 处罚时间结束 yyyy-MM-dd HH:mm:ss"
     */
    private String createDateEnd;

    /**
     * "渠道申诉发起结果"
     */
    private Integer channelAppealState;

    /**
     * "渠道申诉审核结果"
     */
    private Integer channelAppealResult;

    /**
     * "订单来源"
     */
    private String orderOrigin;

    /**
     * "排序规则 1 车管审核时间 其他：处罚创建时间"
     */
    private Integer sortByField;

    /**
     * "申诉开始时间 yyyy-MM-dd HH:mm:ss"
     */
    private String appealDateStart;
    /**
     * "申诉结束时间 yyyy-MM-dd HH:mm:ss"
     */
    private String appealDateEnd;

    /**
     * "车管审核开始时间 yyyy-MM-dd HH:mm:ss"
     */
    private String cgOperateDateStart;
    /**
     * "车管审核结束时间 yyyy-MM-dd HH:mm:ss"
     */
    private String cgOperateDateEnd;

    /**
     * "业务审核开始时间 yyyy-MM-dd HH:mm:ss"
     */
    private String ywOperateDateStart;
    /**
     * "业务审核结束时间 yyyy-MM-dd HH:mm:ss"
     */
    private String ywOperateDateEnd;

    /**
     * "处罚开始时间 yyyy-MM-dd HH:mm:ss"
     */
    private String punishDateStart;
    /**
     * "处罚结束时间 yyyy-MM-dd HH:mm:ss"
     */
    private String punishDateEnd;

    /**
     * "业务过期开始时间 yyyy-MM-dd HH:mm:ss"
     */
    private String ywExpireDateStart;
    /**
     * "业务过期结束时间 yyyy-MM-dd HH:mm:ss"
     */
    private String ywExpireDateEnd;

    /**
     * "限定条件（1.待客服处理；2.全部)"
     */
    private Integer value;

    /**
     * "未坐车产生费用"
     */
    private Integer punishTypeNoByCarFee;

    /**
     * "审核节点名称"
     */
    private String auditNode;

}
