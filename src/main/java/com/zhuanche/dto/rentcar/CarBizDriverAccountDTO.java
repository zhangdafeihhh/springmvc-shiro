package com.zhuanche.dto.rentcar;

public class CarBizDriverAccountDTO {

    private static final long serialVersionUID = 1L;

    private java.lang.Integer accountId;
    //
    private java.lang.Integer driverId;
    //
    private Double creditBalance;
    //
    private Double creditBalanceHis;
    //
    private Double accountAmount;
    //
    private Double frozenAmount;
    //
    private Double withdrawDeposit;
    //待结算（收入）
    private Double settleAccount;
    //可结算（收入）
    private Double settleAccountHis;
    //代付待结算（价外）
    private Double outCurrAccount;
    //代付可结算（价外）
    private Double outHisAccount;
    //操作类型
    private String operation;
    //操作类型名称
    private String operationName;
    //
    private Double amount;
    //@Length(max=30)
    private java.lang.String dealNo;

    private java.lang.Integer dealId;
    //订单类型
    private java.lang.Integer dealTyep;
    //订单名称
    private String dealTypaName;

    private double dealAmount;
    //@NotNull
    private java.util.Date createDate;
    //
    private java.lang.Integer createBy;

    //chenlei add 司机账户流水
    private String isLimit;
    private String name;
    private String phone;
    private String supplierName;
    private String cityName;
    private int cityId;
    private int supplierId;
    private int orderId;

    //columns END

    /*
     * update by cuiw 2015/10/8
     * 新增司机代收方式
     * 新增司机代收结算状态
     * 新增司机代收实收金额
     * 新增司机代收应交平台金额
     */
    private int collectWay;
    private int  settleStatus;
    private double paidAmount;
    private double handedAmount;

    //开始时间
    private String startDate;
    //结束时间
    private String endDate;
    //用户名称
    private String userName;
    //用户手机号
    private String userPhone;
    //操作类型
    private Integer newType;

    private String createDateBegin;
    private String createDateEnd;
    private String serviceTypeId;

    private String createDateStr;

    private int version;
    //提成可结算
    private double historyAmount;
    //提成待结算
    private double currAmount;
    //司机代付待结算
    private double outCurrAmount;
    //司机代付可结算
    private double outHisallAmount;
    //
    private int serviceCityId;

    public int getVersion() {
        return version;
    }
    public void setVersion(int version) {
        this.version = version;
    }
    public double getHistoryAmount() {
        return historyAmount;
    }
    public void setHistoryAmount(double historyAmount) {
        this.historyAmount = historyAmount;
    }
    public double getCurrAmount() {
        return currAmount;
    }
    public void setCurrAmount(double currAmount) {
        this.currAmount = currAmount;
    }
    public double getOutCurrAmount() {
        return outCurrAmount;
    }
    public void setOutCurrAmount(double outCurrAmount) {
        this.outCurrAmount = outCurrAmount;
    }
    public double getOutHisallAmount() {
        return outHisallAmount;
    }
    public void setOutHisallAmount(double outHisallAmount) {
        this.outHisallAmount = outHisallAmount;
    }
    public int getServiceCityId() {
        return serviceCityId;
    }
    public void setServiceCityId(int serviceCityId) {
        this.serviceCityId = serviceCityId;
    }
    public String getCreateDateStr() {
        return createDateStr;
    }
    public void setCreateDateStr(String createDateStr) {
        this.createDateStr = createDateStr;
    }
    public String getOperationName() {
        return operationName;
    }
    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }
    public String getCreateDateBegin() {
        return createDateBegin;
    }
    public void setCreateDateBegin(String createDateBegin) {
        this.createDateBegin = createDateBegin;
    }
    public String getCreateDateEnd() {
        return createDateEnd;
    }
    public void setCreateDateEnd(String createDateEnd) {
        this.createDateEnd = createDateEnd;
    }
    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getUserPhone() {
        return userPhone;
    }
    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
    public Integer getNewType() {
        return newType;
    }
    public void setNewType(Integer newType) {
        this.newType = newType;
    }
    public java.lang.Integer getAccountId() {
        return accountId;
    }
    public void setAccountId(java.lang.Integer accountId) {
        this.accountId = accountId;
    }
    public java.lang.Integer getDriverId() {
        return driverId;
    }
    public void setDriverId(java.lang.Integer driverId) {
        this.driverId = driverId;
    }
    public Double getCreditBalance() {
        return creditBalance;
    }
    public void setCreditBalance(Double creditBalance) {
        this.creditBalance = creditBalance;
    }
    public Double getCreditBalanceHis() {
        return creditBalanceHis;
    }
    public void setCreditBalanceHis(Double creditBalanceHis) {
        this.creditBalanceHis = creditBalanceHis;
    }
    public Double getAccountAmount() {
        return accountAmount;
    }
    public void setAccountAmount(Double accountAmount) {
        this.accountAmount = accountAmount;
    }
    public Double getFrozenAmount() {
        return frozenAmount;
    }
    public void setFrozenAmount(Double frozenAmount) {
        this.frozenAmount = frozenAmount;
    }
    public Double getWithdrawDeposit() {
        return withdrawDeposit;
    }
    public void setWithdrawDeposit(Double withdrawDeposit) {
        this.withdrawDeposit = withdrawDeposit;
    }
    public Double getSettleAccount() {
        return settleAccount;
    }
    public void setSettleAccount(Double settleAccount) {
        this.settleAccount = settleAccount;
    }
    public Double getSettleAccountHis() {
        return settleAccountHis;
    }
    public void setSettleAccountHis(Double settleAccountHis) {
        this.settleAccountHis = settleAccountHis;
    }
    public Double getOutCurrAccount() {
        return outCurrAccount;
    }
    public void setOutCurrAccount(Double outCurrAccount) {
        this.outCurrAccount = outCurrAccount;
    }
    public Double getOutHisAccount() {
        return outHisAccount;
    }
    public void setOutHisAccount(Double outHisAccount) {
        this.outHisAccount = outHisAccount;
    }

    public String getOperation() {
        return operation;
    }
    public void setOperation(String operation) {
        this.operation = operation;
    }
    public String getServiceTypeId() {
        return serviceTypeId;
    }
    public void setServiceTypeId(String serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }
    public Double getAmount() {
        return amount;
    }
    public void setAmount(Double amount) {
        this.amount = amount;
    }
    public java.lang.String getDealNo() {
        return dealNo;
    }
    public void setDealNo(java.lang.String dealNo) {
        this.dealNo = dealNo;
    }
    public java.lang.Integer getDealId() {
        return dealId;
    }
    public void setDealId(java.lang.Integer dealId) {
        this.dealId = dealId;
    }
    public java.lang.Integer getDealTyep() {
        return dealTyep;
    }
    public void setDealTyep(java.lang.Integer dealTyep) {
        this.dealTyep = dealTyep;
    }
    public String getDealTypaName() {
        return dealTypaName;
    }
    public void setDealTypaName(String dealTypaName) {
        this.dealTypaName = dealTypaName;
    }
    public double getDealAmount() {
        return dealAmount;
    }
    public void setDealAmount(double dealAmount) {
        this.dealAmount = dealAmount;
    }
    public java.util.Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(java.util.Date createDate) {
        this.createDate = createDate;
    }
    public java.lang.Integer getCreateBy() {
        return createBy;
    }
    public void setCreateBy(java.lang.Integer createBy) {
        this.createBy = createBy;
    }
    public String getIsLimit() {
        return isLimit;
    }
    public void setIsLimit(String isLimit) {
        this.isLimit = isLimit;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
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
    public int getCityId() {
        return cityId;
    }
    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
    public int getSupplierId() {
        return supplierId;
    }
    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }
    public int getOrderId() {
        return orderId;
    }
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    public int getCollectWay() {
        return collectWay;
    }
    public void setCollectWay(int collectWay) {
        this.collectWay = collectWay;
    }
    public int getSettleStatus() {
        return settleStatus;
    }
    public void setSettleStatus(int settleStatus) {
        this.settleStatus = settleStatus;
    }
    public double getPaidAmount() {
        return paidAmount;
    }
    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }
    public double getHandedAmount() {
        return handedAmount;
    }
    public void setHandedAmount(double handedAmount) {
        this.handedAmount = handedAmount;
    }
    public static long getSerialversionuid() {
        return serialVersionUID;
    }
}