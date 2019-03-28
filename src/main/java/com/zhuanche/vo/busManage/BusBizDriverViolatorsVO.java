package com.zhuanche.vo.busManage;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName: BusBizDriverViolatorsVO
 * @Description: 违规司机处理VO
 * @author: tianye
 * @date: 2019年03月25日 下午18:02:12
 *
 */

@Data
public class BusBizDriverViolatorsVO {
    private Integer id;

    private Integer busDriverId;

    private String busDriverName;

    private String busDriverPhone;

    private Integer cityId;

    private String cityName;

    private Integer supplierId;

    private String supplierName;

    private Integer groupId;

    private String groupName;

    private String evaluateScore;

    private String idNumber;

    private Short punishType;

    private String punishReason;

    private Integer punishDuration;

    private Date punishStartTime;

    private Date punishEndTime;

    private Date createTime;

    private Date updateTime;

    private Short punishStatus;
}
