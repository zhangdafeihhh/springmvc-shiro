package com.zhuanche.vo.busManage;

import com.zhuanche.constants.busManage.BusConstant.SupplierMaidConstant.EnumStatus;
import com.zhuanche.util.DateUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: mp-manage
 * @description: 巴士订单列表对应的实体
 * @author: niuzilian
 * @create: 2018-12-17 20:28
 **/
public class BusSupplierSettleDetailVO {
    /**
     * 供应商id
     */
    private Integer supplierId;
    /**
     * 供应商名称
     */
    private String supplierName;
    /**
     * 城市名称
     */
    private String cityName;
    /**
     * 类型
     */
    private Integer type;
    /**
     * 类型名称
     */
    private String typeName;
    /**
     * 账单主键（结算编号）
     */
    private String supplierBillId;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 到期付款日期
     */
    private Date settleTime;
    /**
     * 结算金额
     */
    private BigDecimal billAmount;

    /**
     * 结算类型
     */
    private Integer settleType;
    /**
     * 分佣方式
     */
    private Integer shareWay;
    /**
     * 分佣方式名称
     */
    private String shareWayName;
    /**
     * 分佣类型
     */
    private Integer shareType;
    /**
     * 分佣类型名称
     */
    private String shareTypeName;
    /**
     * 账单状态
     */
    private Integer status;

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

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getSupplierBillId() {
        return supplierBillId;
    }

    public void setSupplierBillId(String supplierBillId) {
        this.supplierBillId = supplierBillId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getSettleTime() {
        return settleTime;
    }

    public void setSettleTime(Date settleTime) {
        this.settleTime = settleTime;
    }

    public BigDecimal getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(BigDecimal billAmount) {
        this.billAmount = billAmount;
    }


    public Integer getSettleType() {
        return settleType;
    }

    public void setSettleType(Integer settleType) {
        this.settleType = settleType;
    }

    public Integer getShareWay() {
        return shareWay;
    }

    public void setShareWay(Integer shareWay) {
        this.shareWay = shareWay;
    }

    public Integer getShareType() {
        return shareType;
    }

    public void setShareType(Integer shareType) {
        this.shareType = shareType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getShareWayName() {
        return shareWayName;
    }

    public void setShareWayName(String shareWayName) {
        this.shareWayName = shareWayName;
    }

    public String getShareTypeName() {
        return shareTypeName;
    }

    public void setShareTypeName(String shareTypeName) {
        this.shareTypeName = shareTypeName;
    }
    //String BILL_EXPORT_HEAD="供应商编号,供应商名称,城市,业务类型,账单编号,账单开始日期,账单结束日期,到期付款日期,账单金额,结算方式,分佣方式,分佣类型,账单状态";

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        String split = ",";
        sb.append(supplierId).append(split).append(supplierName).append(split).append(cityName).append(split)
                .append(typeName).append(split).append(supplierBillId).append(split)
                .append("\t").append(DateUtils.formatDate(startTime)).append(split)
                .append("\t").append(DateUtils.formatDate(endTime)).append(split)
                .append("\t").append(DateUtils.formatDate(settleTime)).append(split)
                .append(billAmount).append(split).append(settleType == 0 ? "自动" : "手动").append(split)
                .append(shareWayName).append(split).append(shareTypeName).append(split)
                .append(EnumStatus.getDescByCode(status));
        return sb.toString();
    }
}
