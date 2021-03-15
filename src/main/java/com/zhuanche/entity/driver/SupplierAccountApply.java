package com.zhuanche.entity.driver;

import com.zhuanche.util.AliyunImgUtils;

import java.util.Date;
import java.util.List;

public class SupplierAccountApply {
    private Long id;

    private Integer cityId;

    private Integer supplierId;

    private Byte status;

    private String settlementAccount;

    private String bankAccount;

    private String bankName;

    private String bankIdentify;

    private String settlementFullName;

    private Integer createBy;

    private String createName;

    private Date createDate;

    private Integer updateBy;

    private String updateName;

    private Date updateDate;

    private String bankPicUrl;

    private String officalSealUrl;

    private List<SupplierCheckFail> list;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getSettlementAccount() {
        return settlementAccount;
    }

    public void setSettlementAccount(String settlementAccount) {
        this.settlementAccount = settlementAccount == null ? null : settlementAccount.trim();
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount == null ? null : bankAccount.trim();
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName == null ? null : bankName.trim();
    }

    public String getBankIdentify() {
        return bankIdentify;
    }

    public void setBankIdentify(String bankIdentify) {
        this.bankIdentify = bankIdentify == null ? null : bankIdentify.trim();
    }

    public String getSettlementFullName() {
        return settlementFullName;
    }

    public void setSettlementFullName(String settlementFullName) {
        this.settlementFullName = settlementFullName == null ? null : settlementFullName.trim();
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName == null ? null : createName.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }

    public String getUpdateName() {
        return updateName;
    }

    public void setUpdateName(String updateName) {
        this.updateName = updateName == null ? null : updateName.trim();
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getBankPicUrl() {
        return AliyunImgUtils.transUrl(bankPicUrl);
    }

    public void setBankPicUrl(String bankPicUrl) {
        this.bankPicUrl = bankPicUrl == null ? null : bankPicUrl.trim();
    }

    public String getOfficalSealUrl() {
        return AliyunImgUtils.transUrl(officalSealUrl);
    }

    public void setOfficalSealUrl(String officalSealUrl) {
        this.officalSealUrl = officalSealUrl == null ? null : officalSealUrl.trim();
    }

    public List<SupplierCheckFail> getList() {
        return list;
    }

    public void setList(List<SupplierCheckFail> list) {
        this.list = list;
    }
}