package com.zhuanche.entity.bigdata;

/**
 * @Author yxp
 * @Date 2020.03.04
 * @Version 1.0
 */
public class SAASAllCoreIndexPercentDto {

    private String ciFactEndNum;//安装ci完单总量
    private String ciDriverNum;//安装ci司机数
    private String ciFactOverAmount;//安装ci的司机端完单流水
    private String ciOrderNum;//安装ci的下单量
    private String ciBadEvaluateAllNum;//安装ci差评总数
    private String ciBadEvaluateNum;//安装ci有效差评
    private String ciOrderCntNotChannel;//安装ci非渠道单数量
    public String getCiFactEndNum() {
        return ciFactEndNum;
    }

    public void setCiFactEndNum(String ciFactEndNum) {
        this.ciFactEndNum = ciFactEndNum;
    }

    public String getCiDriverNum() {
        return ciDriverNum;
    }

    public void setCiDriverNum(String ciDriverNum) {
        this.ciDriverNum = ciDriverNum;
    }

    public String getCiFactOverAmount() {
        return ciFactOverAmount;
    }

    public void setCiFactOverAmount(String ciFactOverAmount) {
        this.ciFactOverAmount = ciFactOverAmount;
    }

    public String getCiOrderNum() {
        return ciOrderNum;
    }

    public void setCiOrderNum(String ciOrderNum) {
        this.ciOrderNum = ciOrderNum;
    }

    public String getCiBadEvaluateAllNum() {
        return ciBadEvaluateAllNum;
    }

    public void setCiBadEvaluateAllNum(String ciBadEvaluateAllNum) {
        this.ciBadEvaluateAllNum = ciBadEvaluateAllNum;
    }

    public String getCiBadEvaluateNum() {
        return ciBadEvaluateNum;
    }

    public void setCiBadEvaluateNum(String ciBadEvaluateNum) {
        this.ciBadEvaluateNum = ciBadEvaluateNum;
    }

    public String getCiOrderCntNotChannel() {
        return ciOrderCntNotChannel;
    }

    public void setCiOrderCntNotChannel(String ciOrderCntNotChannel) {
        this.ciOrderCntNotChannel = ciOrderCntNotChannel;
    }
}
