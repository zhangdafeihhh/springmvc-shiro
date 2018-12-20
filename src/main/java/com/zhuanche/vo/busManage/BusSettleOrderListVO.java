package com.zhuanche.vo.busManage;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: mp-manage
 * @description: 巴士供应商 分佣账单流水 列表返回的VO
 * @author: niuzilian
 * @create: 2018-12-19 18:26
 **/
public class BusSettleOrderListVO {
    private Integer id;
    private String orderNo;
    private BigDecimal settleAmount;
    private String createDate;
    private Integer accountType;
    private String orderDetail;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public BigDecimal getSettleAmount() {
        return settleAmount;
    }

    public void setSettleAmount(BigDecimal settleAmount) {
        this.settleAmount = settleAmount;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    public String getOrderDetail() {
        return orderDetail;
    }

    public void setOrderDetail(String orderDetail) {
        this.orderDetail = orderDetail;
    }

    @Override
    public String toString() {
        //http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=28755726
        String typeName = "";
        if (accountType == 5) {
            typeName = "巴士分佣收入";
        } else if (accountType == 6) {
            typeName = "修正订单";
        } else if (accountType == 7) {
            typeName = "修正账单";
        }else{
            typeName="未知类型";
        }
        return id + "," + settleAmount + ",\t" + createDate +","+typeName;
    }
}
