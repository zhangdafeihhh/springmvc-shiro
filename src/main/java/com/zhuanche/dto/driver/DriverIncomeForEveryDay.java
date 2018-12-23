package com.zhuanche.dto.driver;

/**
 * @Author fanht
 * @Description
 * @Date 2018/12/23 下午2:33
 * @Version 1.0
 */
public class DriverIncomeForEveryDay {

    /**运营次数**/
    private Integer operationNum;

    /**实际收入**/
    private double actualPay;

    /**服务里程**/
    private double serviceMileage;

    /**司机代付**/
    private double driverOutPay;


    public DriverIncomeForEveryDay(Integer operationNum, double actualPay, double serviceMileage, double driverOutPay) {
        this.operationNum = operationNum;
        this.actualPay = actualPay;
        this.serviceMileage = serviceMileage;
        this.driverOutPay = driverOutPay;
    }

    public Integer getOperationNum() {
        return operationNum;
    }

    public void setOperationNum(Integer operationNum) {
        this.operationNum = operationNum;
    }

    public double getActualPay() {
        return actualPay;
    }

    public void setActualPay(double actualPay) {
        this.actualPay = actualPay;
    }

    public double getServiceMileage() {
        return serviceMileage;
    }

    public void setServiceMileage(double serviceMileage) {
        this.serviceMileage = serviceMileage;
    }

    public double getDriverOutPay() {
        return driverOutPay;
    }

    public void setDriverOutPay(double driverOutPay) {
        this.driverOutPay = driverOutPay;
    }
}
