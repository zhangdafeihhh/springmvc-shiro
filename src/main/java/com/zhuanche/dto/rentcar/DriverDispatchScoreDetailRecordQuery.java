package com.zhuanche.dto.rentcar;

import com.zhuanche.util.DateUtil;
import com.zhuanche.util.DateUtils;

import java.util.Date;

/**
 * 司机派单分明细记录查询条件
 *
 * @author wuqiang
 * @date 2020.03.10
 */
public class DriverDispatchScoreDetailRecordQuery {

    /**
     * 司机ID
     */
    private Integer driverId;

    /**
     * 司机姓名
     */
    private String driverName;

    /**
     * 司机手机号
     */
    private String driverPhone;

    /**
     * 父派单分分类
     */
    private String parentDispatchScoreType;

    /**
     * 具体类型
     */
    private String type;

    /**
     * 关联单号
     */
    private String orderNo;

    /**
     * 开始日期<br>
     *     示例：2020-02-28
     */
    private String startDate = DateUtils.formatDate(DateUtils.addDays(new Date(),-30), DateUtil.DATE_FORMAT);

    /**
     * 结束日期<br>
     *     示例：2020-02-28
     */
    private String endDate = DateUtils.formatDate(new Date(), DateUtil.DATE_FORMAT);

    /**
     * 页码
     */
    private int page = 1;

    /**
     * 每页条数
     */
    private int pageSize = 20;

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

    public String getParentDispatchScoreType() {
        return parentDispatchScoreType;
    }

    public void setParentDispatchScoreType(String parentDispatchScoreType) {
        this.parentDispatchScoreType = parentDispatchScoreType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
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

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
