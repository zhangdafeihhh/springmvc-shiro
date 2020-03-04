package com.zhuanche.entity.bigdata;

public class CiOrderStatisticSection {
    private String date;
    private String date2;
    private String value;
    private Integer specialValue;
    private Integer driverNum;
    public CiOrderStatisticSection() {
    }

    public String getDate() {
        return this.date;
    }

    public String getValue() {
        return this.value;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getSpecialValue() {
        return specialValue;
    }

    public void setSpecialValue(Integer specialValue) {
        this.specialValue = specialValue;
    }

    public Integer getDriverNum() {
        return driverNum;
    }

    public void setDriverNum(Integer driverNum) {
        this.driverNum = driverNum;
    }

    public String getDate2() {
        return date2;
    }

    public void setDate2(String date2) {
        this.date2 = date2;
    }
}
