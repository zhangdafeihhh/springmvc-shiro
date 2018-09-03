package com.zhuanche.entity.rentcar;

import java.util.Date;

public class CarBizUserAction {
    private Integer chatUserId;

    private String activePositionX;

    private String activePositionY;

    private Date activeDate;

    private Date loginDate;

    private Date registerDate;

    private String registerPositionX;

    private String registerPositionY;

    private Date firstDate;

    public Integer getChatUserId() {
        return chatUserId;
    }

    public void setChatUserId(Integer chatUserId) {
        this.chatUserId = chatUserId;
    }

    public String getActivePositionX() {
        return activePositionX;
    }

    public void setActivePositionX(String activePositionX) {
        this.activePositionX = activePositionX == null ? null : activePositionX.trim();
    }

    public String getActivePositionY() {
        return activePositionY;
    }

    public void setActivePositionY(String activePositionY) {
        this.activePositionY = activePositionY == null ? null : activePositionY.trim();
    }

    public Date getActiveDate() {
        return activeDate;
    }

    public void setActiveDate(Date activeDate) {
        this.activeDate = activeDate;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getRegisterPositionX() {
        return registerPositionX;
    }

    public void setRegisterPositionX(String registerPositionX) {
        this.registerPositionX = registerPositionX == null ? null : registerPositionX.trim();
    }

    public String getRegisterPositionY() {
        return registerPositionY;
    }

    public void setRegisterPositionY(String registerPositionY) {
        this.registerPositionY = registerPositionY == null ? null : registerPositionY.trim();
    }

    public Date getFirstDate() {
        return firstDate;
    }

    public void setFirstDate(Date firstDate) {
        this.firstDate = firstDate;
    }
}