package com.zhuanche.request;

import java.util.Set;

/**
  * @description: 排班接口请求参数封装类
  *
  * <PRE>
  * <BR>	修改记录
  * <BR>-----------------------------------------------
  * <BR>	修改日期			修改人			修改内容
  * </PRE>
  *
  * @author lunan
  * @version 1.0
  * @since 1.0
  * @create: 2018-08-30 10:10
  *
*/

public class DutyParamRequest extends PageRequest{

    private static final long serialVersionUID = -6293630291498667107L;

    private Integer id;

    private Integer driverId; // 司机ID
    private String driverName;// 司机姓名
    private String phone; // 司机手机号
    private Integer cityId;
    private Integer supplierId;
    private Integer teamId;
    private String time; // 排班时间
    private String forcedIds; // 强制司机上班设置id集，用 ,分隔
    private String dutyIds; // 排班时长设置id集，用 ,分隔
    private String forcedTimes; // 强制司机上班设置
    private String dutyTimes; // 排班时长设置
    private Integer type; // 类型
    private Integer status; // 状态：1 未发布；2 已发布

    private String licensePlates;

    private String cityName;
    private String supplierName;
    private String teamName;

    /** 保存排班信息参数*/
    // 批量处理字段
    private String times; // 要设置的日期集， 用 ,分隔
    private String driverIds; // 要设置的司机id集， 用 ,分隔
    private Set<Integer> driverIdSet;//司机id列表，新版本使用

    /** 高峰时间*/
    private String peakTimes;

    /** 是否查看发布排班列表 0查看所有 1查看未发布 */
    private Integer unpublishedFlag;

    /** 权限相关*/
    private Set<Integer> cityIds;//普通管理员可以管理的所有城市ID

    private Set<Integer> supplierIds;//普通管理员可以管理的所有供应商ID

    private Set<Integer> teamIds;//普通管理员可以管理的所有车队ID

    // 查询
    private String startTime;
    private String endTime;

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getDriverIds() {
        return driverIds;
    }

    public void setDriverIds(String driverIds) {
        this.driverIds = driverIds;
    }

    public String getPeakTimes() {
        return peakTimes;
    }

    public void setPeakTimes(String peakTimes) {
        this.peakTimes = peakTimes;
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

    public Set<Integer> getTeamIds() {
        return teamIds;
    }

    public void setTeamIds(Set<Integer> teamIds) {
        this.teamIds = teamIds;
    }

    public Integer getUnpublishedFlag() {
        return unpublishedFlag;
    }

    public void setUnpublishedFlag(Integer unpublishedFlag) {
        this.unpublishedFlag = unpublishedFlag;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getForcedIds() {
        return forcedIds;
    }

    public void setForcedIds(String forcedIds) {
        this.forcedIds = forcedIds;
    }

    public String getDutyIds() {
        return dutyIds;
    }

    public void setDutyIds(String dutyIds) {
        this.dutyIds = dutyIds;
    }

    public String getForcedTimes() {
        return forcedTimes;
    }

    public void setForcedTimes(String forcedTimes) {
        this.forcedTimes = forcedTimes;
    }

    public String getDutyTimes() {
        return dutyTimes;
    }

    public void setDutyTimes(String dutyTimes) {
        this.dutyTimes = dutyTimes;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Set<Integer> getDriverIdSet() {
        return driverIdSet;
    }

    public void setDriverIdSet(Set<Integer> driverIdSet) {
        this.driverIdSet = driverIdSet;
    }
}
