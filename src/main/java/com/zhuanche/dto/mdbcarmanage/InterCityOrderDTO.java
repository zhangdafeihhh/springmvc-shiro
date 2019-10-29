package com.zhuanche.dto.mdbcarmanage;

/**
 * @Author fanht
 * @Description
 * @Date 2019/10/22 下午2:27
 * @Version 1.0
 */
public class InterCityOrderDTO {

    private String bookingUserId;

    private String reserveName;

    private String reservePhone;

    private Integer isSameRider;

    private String riderName;

    private String riderPhone;

    private Integer riderCount;

    private String boardingTime;

    private String boardingCityId;

    private String boardingCityName;

    private String boardingGetOffCityId;

    private String boardingGetOffCityName;

    private String bookingStartPoint;

    private String bookingEndPoint;

    private Integer status;//订单状态

    private Integer type;//服务类型

    private String orderTime;//下单时间


    private String boardOnAddr;//上车地点

    private String boardOffAddr;//下车地点

    private Integer cityId;

    private String routeName;

    private String mainOrderNo;

    private String orderNo;




    private Integer driverId;

    private String driverName;

    private String driverPhone;

    private String licensePlates;


    private String cityName;

    private Integer supplierId;

    private String supplierName;


    private String mainTime;

    private String carGroup;




    public String getReserveName() {
        return reserveName;
    }

    public void setReserveName(String reserveName) {
        this.reserveName = reserveName;
    }

    public String getReservePhone() {
        return reservePhone;
    }

    public void setReservePhone(String reservePhone) {
        this.reservePhone = reservePhone;
    }

    public Integer getIsSameRider() {
        return isSameRider;
    }

    public void setIsSameRider(Integer isSameRider) {
        this.isSameRider = isSameRider;
    }

    public String getRiderName() {
        return riderName;
    }

    public void setRiderName(String riderName) {
        this.riderName = riderName;
    }

    public String getRiderPhone() {
        return riderPhone;
    }

    public void setRiderPhone(String riderPhone) {
        this.riderPhone = riderPhone;
    }

    public Integer getRiderCount() {
        return riderCount;
    }

    public void setRiderCount(Integer riderCount) {
        this.riderCount = riderCount;
    }

    public String getBoardingTime() {
        return boardingTime;
    }

    public void setBoardingTime(String boardingTime) {
        this.boardingTime = boardingTime;
    }

    public String getBoardingCityId() {
        return boardingCityId;
    }

    public void setBoardingCityId(String boardingCityId) {
        this.boardingCityId = boardingCityId;
    }

    public String getBoardingCityName() {
        return boardingCityName;
    }

    public void setBoardingCityName(String boardingCityName) {
        this.boardingCityName = boardingCityName;
    }

    public String getBoardingGetOffCityId() {
        return boardingGetOffCityId;
    }

    public void setBoardingGetOffCityId(String boardingGetOffCityId) {
        this.boardingGetOffCityId = boardingGetOffCityId;
    }

    public String getBoardingGetOffCityName() {
        return boardingGetOffCityName;
    }

    public void setBoardingGetOffCityName(String boardingGetOffCityName) {
        this.boardingGetOffCityName = boardingGetOffCityName;
    }

    public String getBookingStartPoint() {
        return bookingStartPoint;
    }

    public void setBookingStartPoint(String bookingStartPoint) {
        this.bookingStartPoint = bookingStartPoint;
    }

    public String getBookingEndPoint() {
        return bookingEndPoint;
    }

    public void setBookingEndPoint(String bookingEndPoint) {
        this.bookingEndPoint = bookingEndPoint;
    }

    public String getBookingUserId() {
        return bookingUserId;
    }

    public void setBookingUserId(String bookingUserId) {
        this.bookingUserId = bookingUserId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }


    public String getBoardOnAddr() {
        return boardOnAddr;
    }

    public void setBoardOnAddr(String boardOnAddr) {
        this.boardOnAddr = boardOnAddr;
    }

    public String getBoardOffAddr() {
        return boardOffAddr;
    }

    public void setBoardOffAddr(String boardOffAddr) {
        this.boardOffAddr = boardOffAddr;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }


    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getMainOrderNo() {
        return mainOrderNo;
    }

    public void setMainOrderNo(String mainOrderNo) {
        this.mainOrderNo = mainOrderNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

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

    public String getMainTime() {
        return mainTime;
    }

    public void setMainTime(String mainTime) {
        this.mainTime = mainTime;
    }

    public String getCarGroup() {
        return carGroup;
    }

    public void setCarGroup(String carGroup) {
        this.carGroup = carGroup;
    }
}
