package com.zhuanche.dto.rentcar;

import java.math.BigDecimal;
import java.util.List;

/**
 * 司机时长分计算概括
 *
 * @author wuqiang
 * @date 2020.03.11
 */
public class DriverTimeLengthScoreCalculateGeneralize {

    /**
     * 总时长分<br>
     *     总时长分 = 总时长加速分 + 总调度分 + 总应答保障分
     */
    private BigDecimal durationScore = BigDecimal.ZERO;

    /**
     * 总基础时长分
     */
    private BigDecimal totalBaseDurationScore = BigDecimal.ZERO;

    /**
     * 日期<br>
     *     示例：2020-02-28
     */
    private String day;

    /**
     * 司机时长分计算明细
     */
    private List<DriverTimeLengthScoreCalculateDetail> calculateDetailList;

    public BigDecimal getDurationScore() {
        return durationScore;
    }

    public void setDurationScore(BigDecimal durationScore) {
        this.durationScore = durationScore;
    }

    public BigDecimal getTotalBaseDurationScore() {
        return totalBaseDurationScore;
    }

    public void setTotalBaseDurationScore(BigDecimal totalBaseDurationScore) {
        this.totalBaseDurationScore = totalBaseDurationScore;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public List<DriverTimeLengthScoreCalculateDetail> getCalculateDetailList() {
        return calculateDetailList;
    }

    public void setCalculateDetailList(List<DriverTimeLengthScoreCalculateDetail> calculateDetailList) {
        this.calculateDetailList = calculateDetailList;
    }
}
