package com.zhuanche.dto.rentcar;

import com.alibaba.fastjson.JSON;
import com.zhuanche.util.DateUtil;
import com.zhuanche.util.DateUtils;

import java.util.Date;

/**
 * 司机派单分每日更新记录查询条件
 *
 * @author wuqiang
 * @date 2020.03.10
 */
public class DriverDispatchScoreDailyUpdateRecordQuery {

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
     * 开始日期<br>
     *     格式：YYYY-MM-DD
     */
    private String updateTime = DateUtils.formatDate(DateUtils.addDays(new Date(),-30), DateUtil.DATE_FORMAT);

    /**
     * 结束日期<br>
     *     格式：YYYY-MM-DD
     */
    private String endUpdateTime = DateUtils.formatDate(new Date(), DateUtil.DATE_FORMAT);

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

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getEndUpdateTime() {
        return endUpdateTime;
    }

    public void setEndUpdateTime(String endUpdateTime) {
        this.endUpdateTime = endUpdateTime;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
