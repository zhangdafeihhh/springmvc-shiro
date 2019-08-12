package com.zhuanche.dto.mdbcarmanage;

/**
 * @Author fanht
 * @Description
 * @Date 2019/8/8 下午4:37
 * @Version 1.0
 */
public class ScoreDetailDTO {

    private Integer driverId;

    private String name;

    private String phone;

    private String hourScore; //时长分

    private Integer isTotal;//是否计入总分 1 计入总分 0 不计入总分

    private String scoreDetailDate;

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
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

    public String getHourScore() {
        return hourScore;
    }

    public void setHourScore(String hourScore) {
        this.hourScore = hourScore;
    }

    public Integer getIsTotal() {
        return isTotal;
    }

    public void setIsTotal(Integer isTotal) {
        this.isTotal = isTotal;
    }

    public String getScoreDetailDate() {
        return scoreDetailDate;
    }

    public void setScoreDetailDate(String scoreDetailDate) {
        this.scoreDetailDate = scoreDetailDate;
    }
}
