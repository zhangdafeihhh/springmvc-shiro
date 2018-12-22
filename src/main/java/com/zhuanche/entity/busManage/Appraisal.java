package com.zhuanche.entity.busManage;


import java.io.Serializable;

/**
 * @program: car-manage
 * @description: 司机评价信息
 * @author: niuzilian
 * @create: 2018-11-05 10:29
 **/
public class Appraisal implements Serializable {
    /**
     * 评价记录ID
     */
    private Integer appraisalId;
    /**
     * 订单ID
     */
    private Integer orderId;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 	评价时间
     */
    private String createDate;
    /**
     * 评价司机的标签
     */
    private String evaluate;
    /**
     * 评价司机的分数（1分至5分）
     */
    private String evaluateScore;
    /**
     * 评价司机的内容
     */
    private String memo;
    /**
     * 评价平台的标签
     */
    private String platformEvaluate;
    /**
     * 评价平台的分数（1分至5分）
     */
    private String platformEvaluateScore;
    /**
     * 评价平台的内容
     */
    private String platformMemo;

    public Integer getAppraisalId() {
        return appraisalId;
    }

    public void setAppraisalId(Integer appraisalId) {
        this.appraisalId = appraisalId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getEvaluate() {
        return evaluate;
    }

    public void setEvaluate(String evaluate) {
        this.evaluate = evaluate;
    }

    public String getEvaluateScore() {
        return evaluateScore;
    }

    public void setEvaluateScore(String evaluateScore) {
        this.evaluateScore = evaluateScore;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getPlatformEvaluate() {
        return platformEvaluate;
    }

    public void setPlatformEvaluate(String platformEvaluate) {
        this.platformEvaluate = platformEvaluate;
    }

    public String getPlatformEvaluateScore() {
        return platformEvaluateScore;
    }

    public void setPlatformEvaluateScore(String platformEvaluateScore) {
        this.platformEvaluateScore = platformEvaluateScore;
    }

    public String getPlatformMemo() {
        return platformMemo;
    }

    public void setPlatformMemo(String platformMemo) {
        this.platformMemo = platformMemo;
    }
}
