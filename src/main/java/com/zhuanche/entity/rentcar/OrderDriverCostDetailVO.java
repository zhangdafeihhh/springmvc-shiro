package com.zhuanche.entity.rentcar;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderDriverCostDetailVO {

    private Integer detailId;
    //订单id
    private Integer orderId;
    //订单号
    private String orderNo;
    //行驶时间 毫秒
    private Integer travelTime;
    //行驶里程 公里
    private BigDecimal travelMileage;
    //订单总额
    private BigDecimal totalAmount;
    //抹零金额
    private BigDecimal decimalsFees;
    //订单实际支付
    private BigDecimal actualPayAmount;
    //基础价
    private BigDecimal basePrice;
    //超里程费
    private BigDecimal overMileagePrice;
    //超里程公里
    private BigDecimal overMileageNum;
    //超时长分钟
    private BigDecimal overTimeNum;
    //超时长费
    private BigDecimal overTimePrice;
    //长途费
    private BigDecimal longDistancePrice;
    //长途公里
    private BigDecimal longDistanceNum;
    //夜间里程 公里
    private BigDecimal nightDistanceNum;
    //夜间里程费
    private BigDecimal nightDistancePrice;
    //夜间时长 分钟
    private BigDecimal nighitDuration;
    //夜间时长费
    private BigDecimal nighitDurationFees;
    //高峰时长费
    private BigDecimal hotDurationFees;
    //高峰时长 分钟
    private BigDecimal hotDuration;
    //高峰里程费
    private BigDecimal hotMileageFees;
    //高峰里程
    private BigDecimal hotMileage;
    //等候时长 分钟
    private BigDecimal waitingMinutes;
    //等候时长费
    private BigDecimal waitingFee;
    //指定司机费
    private Double designatedDriverFee;
    //价外费
    private Double othersFee;
    //服务完成时时间
    private Date endDate;
    //计价前行驶里程
    private BigDecimal travelMileageStart;
    //计价后行驶里程
    private BigDecimal travelMileageEnd;
    //计价前行驶时长
    private Long travelTimeStart;
    //计价后行驶时长
    private Long travelTimeEnd;
    //行程结束时间
    private Date finalServiceTime;
    //创建时间
    private Date createDate;
    //更新时间
    private Date updateDate;
}
