package com.zhuanche.entity.driver;

import com.zhuanche.util.AliyunImgUtils;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * supplier_ext
 * @author
 */
public class SupplierExtDto {
    private Long id;

    /**
     * 供应商id
     */
    private Integer supplierId;

    /**
     * 状态 1启用 0禁用
     */
    private Byte status;

    /**
     * 供应商简称
     */
    private String supplierShortName;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 修改时间
     */
    private Date updateDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 二级加盟类型id
     */
    private Integer twoLevelCooperation;

    /**
     * 结算类型 0 无效类型 1自主结算 2 对公结算 3其他
     */
    private Integer settlementType;

    /**
     * 结算周期 0 无效周期 1月结 2 半月结 3 周结
     */
    private Integer settlementCycle;

    /**
     * 结算日 0为无效结算日
     */
    private Integer settlementDay;

    /**
     * 打款账户名称
     */
    private String settlementAccount;

    /**
     * 打款银行账号
     */
    private String bankAccount;

    /**
     * 开户行名称
     */
    private String bankName;

    /**
     * 联行号
     */
    private String bankIdentify;

    /**
     * 结算供应商全称
     */
    private String settlementFullName;

    /**
     * 首次签约时间
     */
    private Date firstSignTime;

    /**
     * 税率 3%、6%、9%、10%、13%、16%
     */
    private String taxRate;

    /**
     * 发票类型 0.无效发票类型 1.专票、2.普票、3.电子发票（普票）
     */
    private Integer invoiceType;


    /**
     *
     */
    private Integer cityId;

    /**
     *
     */
    private Integer mainCityId;

    /**
     *大区
     */
    private String largeArea;

    /**
     *合作模式 1 自主、2 非自主；
     */
    private Integer cooperationMode;

    /**
     * 花园计划等级 1 LV1、2 LV2、3 LV3、4 LV4、5 LV5、6 金牌；
     */
    private String gardenPlanLevel;

    /**
     * 签约发起人
     */
    private String signSponsor;

    /**
     * 签约时间
     */
    private String contractTime;

    /**
     * 90天加车数
     */
    private Integer carNumber;

    /**
     * 90天加车数
     */
    private String monthWater;

    /**
     * 保证金金额
     */
    private String amountDeposit;

    /**
     * 收款账户
     */
    private String receiveAccount;

    /**
     * 打款户名
     */
    private String accountUser;

    /**
     * 收款账户
     */
    private String depositReceipt;

    /**
     * 解约日期
     */
    private Date cancellationDate;

    /**
     * 退款情况
     */
    private String refundStatus;

    /**
     * 公司成立日期
     */
    private Date companyDate;

    /**
     * 法人姓名
     */
    private String legalName;

    /**
     * 最新开户许可证/印鉴卡
     */
    private String bankPicUrl;

    /**
     * 加盖公章
     */
    private String officalSealUrl;

    /**
     * 申请更新状态
     */
    private Integer applierStatus;


    private String cityName; //城市名称

    private String mainCityName;//主城名称

    private String supplierName; //合作商

    private Long accountApplyId;//supplier_account_apply 表主键id
    private Set<Integer> cityIds;
    private Set<Integer> supplierIds;

    private String agreementStartTime;//协议开始日期

    private String agreementEndTime;//协议终止日期

    private String limitLowMonthWater;//月流水下限


    /**
     * 供应商全称
     */
    private String supplierFullName;

    /**
     * 加盟商审核记录
     */
    private List<SupplierCheckFail> list;

    /**
     *客服电话
     */
    private String customerPhone;

    /**
     *客服座机号
     */
    private String customerLineNumber;

    public enum  opeStatus{
        REJERECT(3);//审核驳回
        private Integer code;

        opeStatus(Integer code) {
            this.code = code;
        }

        public Integer getCode() {

            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }
    }

    public enum modelEnum{
        COOPERATEION_FREE(1,"自主"),
        COOPERATEION_UNFREE(2,"非自主");
        private int code;

        private String name;

        modelEnum(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getSupplierShortName() {
        return supplierShortName;
    }

    public void setSupplierShortName(String supplierShortName) {
        this.supplierShortName = supplierShortName == null ? null : supplierShortName.trim();
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public Integer getTwoLevelCooperation() {
        return twoLevelCooperation;
    }

    public void setTwoLevelCooperation(Integer twoLevelCooperation) {
        this.twoLevelCooperation = twoLevelCooperation;
    }

    public Integer getSettlementType() {
        return settlementType;
    }

    public void setSettlementType(Integer settlementType) {
        this.settlementType = settlementType;
    }

    public Integer getSettlementCycle() {
        return settlementCycle;
    }

    public void setSettlementCycle(Integer settlementCycle) {
        this.settlementCycle = settlementCycle;
    }

    public Integer getSettlementDay() {
        return settlementDay;
    }

    public void setSettlementDay(Integer settlementDay) {
        this.settlementDay = settlementDay;
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

    public Date getFirstSignTime() {
        return firstSignTime;
    }

    public void setFirstSignTime(Date firstSignTime) {
        this.firstSignTime = firstSignTime;
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate == null ? null : taxRate.trim();
    }

    public Integer getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(Integer invoiceType) {
        this.invoiceType = invoiceType;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getMainCityId() {
        return mainCityId;
    }

    public void setMainCityId(Integer mainCityId) {
        this.mainCityId = mainCityId;
    }

    public String getLargeArea() {
        return largeArea;
    }

    public void setLargeArea(String largeArea) {
        this.largeArea = largeArea;
    }

    public Integer getCooperationMode() {
        return cooperationMode;
    }

    public void setCooperationMode(Integer cooperationMode) {
        this.cooperationMode = cooperationMode;
    }

    public String getGardenPlanLevel() {
        return gardenPlanLevel;
    }

    public void setGardenPlanLevel(String gardenPlanLevel) {
        this.gardenPlanLevel = gardenPlanLevel;
    }

    public String getSignSponsor() {
        return signSponsor;
    }

    public void setSignSponsor(String signSponsor) {
        this.signSponsor = signSponsor;
    }

    public String getContractTime() {
        return contractTime;
    }

    public void setContractTime(String contractTime) {
        this.contractTime = contractTime;
    }

    public Integer getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(Integer carNumber) {
        this.carNumber = carNumber;
    }

    public String getMonthWater() {
        return monthWater;
    }

    public void setMonthWater(String monthWater) {
        this.monthWater = monthWater;
    }

    public String getAmountDeposit() {
        return amountDeposit;
    }

    public void setAmountDeposit(String amountDeposit) {
        this.amountDeposit = amountDeposit;
    }

    public String getReceiveAccount() {
        return receiveAccount;
    }

    public void setReceiveAccount(String receiveAccount) {
        this.receiveAccount = receiveAccount;
    }

    public String getAccountUser() {
        return accountUser;
    }

    public void setAccountUser(String accountUser) {
        this.accountUser = accountUser;
    }

    public String getDepositReceipt() {
        return depositReceipt;
    }

    public void setDepositReceipt(String depositReceipt) {
        this.depositReceipt = depositReceipt;
    }



    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    public Date getCancellationDate() {
        return cancellationDate;
    }

    public void setCancellationDate(Date cancellationDate) {
        this.cancellationDate = cancellationDate;
    }

    public Date getCompanyDate() {
        return companyDate;
    }

    public void setCompanyDate(Date companyDate) {
        this.companyDate = companyDate;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getBankPicUrl() {
        return AliyunImgUtils.transUrl(bankPicUrl);
    }

    public void setBankPicUrl(String bankPicUrl) {
        this.bankPicUrl = bankPicUrl;
    }

    public String getOfficalSealUrl() {
        return AliyunImgUtils.transUrl(bankPicUrl);
    }

    public void setOfficalSealUrl(String officalSealUrl) {
        this.officalSealUrl = officalSealUrl;
    }

    public Set<Integer> getCityIds() {
        return cityIds;
    }

    public void setCityIds(Set<Integer> cityIds) {
        this.cityIds = cityIds;
    }

    public Set<Integer> getSupplierIds() {
        return supplierIds;
    }

    public void setSupplierIds(Set<Integer> supplierIds) {
        this.supplierIds = supplierIds;
    }

    public Integer getApplierStatus() {
        return applierStatus;
    }

    public void setApplierStatus(Integer applierStatus) {
        this.applierStatus = applierStatus;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getMainCityName() {
        return mainCityName;
    }

    public void setMainCityName(String mainCityName) {
        this.mainCityName = mainCityName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Long getAccountApplyId() {
        return accountApplyId;
    }

    public void setAccountApplyId(Long accountApplyId) {
        this.accountApplyId = accountApplyId;
    }

    public String getAgreementStartTime() {
        return agreementStartTime;
    }

    public void setAgreementStartTime(String agreementStartTime) {
        this.agreementStartTime = agreementStartTime;
    }

    public String getAgreementEndTime() {
        return agreementEndTime;
    }

    public void setAgreementEndTime(String agreementEndTime) {
        this.agreementEndTime = agreementEndTime;
    }

    public String getLimitLowMonthWater() {
        return limitLowMonthWater;
    }

    public void setLimitLowMonthWater(String limitLowMonthWater) {
        this.limitLowMonthWater = limitLowMonthWater;
    }

    public String getSupplierFullName() {
        return supplierFullName;
    }

    public void setSupplierFullName(String supplierFullName) {
        this.supplierFullName = this.getSupplierShortName();
    }

    public List<SupplierCheckFail> getList() {
        return list;
    }

    public void setList(List<SupplierCheckFail> list) {
        this.list = list;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerLineNumber() {
        return customerLineNumber;
    }

    public void setCustomerLineNumber(String customerLineNumber) {
        this.customerLineNumber = customerLineNumber;
    }
}