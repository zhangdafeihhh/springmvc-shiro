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
}
