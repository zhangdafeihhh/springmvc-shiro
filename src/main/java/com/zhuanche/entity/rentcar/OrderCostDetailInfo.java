package com.zhuanche.entity.rentcar;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单明细返回信息
 */
@Data
public class OrderCostDetailInfo {
    private Integer detailId;  //主键

    private Integer orderId;  //订单主键

    private String orderNo; //订单号

    private Integer travelTime;  //行驶时长--总时长（单位：毫秒）

    private BigDecimal travelMileage;  //行驶里程--总里程（单位：公里）

    private BigDecimal totalAmount;  //订单总额

    private BigDecimal decimalsFees;  //抹零

    private BigDecimal actualPayAmount;   //乘客实际支付的钱数，抹零后的金额同时减去优惠券的金额（账户支付金额）

    private BigDecimal accountSettleAmount;  //账户结算金额（充值账户结算金额+赠送账户结算金额）

    private BigDecimal chargeSettleAmount;  //充值账户结算金额

    private BigDecimal giftSettleAmount;  //赠送账户结算金额

    private BigDecimal depositAmount;   //账户定金支付金额（充值账户定金支付金额+赠送账户定金支付金额）

    private BigDecimal depositAccountAmount;  //充值账户定金支付金额

    private BigDecimal depositGiftAmount;  //赠送账户定金支付金额

    private BigDecimal depositSettleAmount;  //账户定金支付金额（充值账户定金支付金额+赠送账户定金支付金额）

    private BigDecimal depositCreditAmount;  //信用卡账户定金支付金额

    private BigDecimal basePrice;  //基础资费（套餐费用）

    private Integer includeMinute;   //基础价包含时长(单位,分钟) --car_biz_pricing_plan_snapshoot字段

    private Integer includeMileage;  //基础价包含公里(单位,公里) --car_biz_pricing_plan_snapshoot字段

    private BigDecimal overMileageNum;  //超里程数（单位：公里）

    /**
     * 超里程单价 (元/分钟) --car_biz_pricing_plan_snapshoot字段 over_mileage_price  也叫平峰里程单价
     * 由于cost_detail 也有over_mileage_price 作为超时长费
     * 所以用overMileageUintPrice作为单价
     */
    private BigDecimal overMileageUnitPrice;

    private BigDecimal overMileagePrice;  //超里程费（超里程数*超里程单价）

    private BigDecimal overTimeNum;  //超时长数（单位：分钟）

    /**
     * 超时长单价 (元/分钟) --car_biz_pricing_plan_snapshoot字段 over_time_price   也叫平峰时长单价
     * 由于cost_detail 也有over_time_price 作为超时长费
     * 所以用overTimeUintPrice作为单价
     */
    private BigDecimal overTimeUintPrice;

    private BigDecimal overTimePrice;  //超时长费（超时长数*超时长单价）

    private String hotDuration;  //高峰时长数 （单位：分钟）

    private BigDecimal peakPriceTime;  //高峰时长单价 (单位：元/分钟) --car_biz_pricing_plan_snapshoot字段

    private BigDecimal hotDurationFees;  //高峰时长费用（高峰时长数*高峰时长单价）

    private String hotMileage;  //高峰里程数  （单位：公里）

    private BigDecimal peakPrice;  //高峰里程单价 (元/公里) --car_biz_pricing_plan_snapshoot字段

    private BigDecimal hotMileageFees;  //高峰里程费用（高峰里程数*高峰里程单价）

    private BigDecimal overMilageNumTotal;  //里程费小计 -- 平峰里程费overMileagePrice+高峰里程费hotMileageFees

    private BigDecimal overTimeNumTotal;  //时长费小计 -- 平峰时长费overTimePrice+高峰时长费hotDurationFees

    private BigDecimal longDistanceNum;  //空驶里程数（单位：公里）

    private BigDecimal longPrice;  //空驶单价(元/里程) --car_biz_pricing_plan_snapshoot字段

    private BigDecimal longDistancePrice;  //空驶费 （空驶里程数*空驶单价）

    private BigDecimal nightDistanceNum;  //夜间里程数（单位：公里）

    private BigDecimal nightPrice;  //夜间里程单价 (元/公里) --car_biz_pricing_plan_snapshoot字段

    private BigDecimal nightDistancePrice;  //夜间里程服务费 （夜间里程数*夜间里程单价）

    private String nighitDuration;  //夜间时长数（单位：分钟）

    private BigDecimal nightPriceTime;  //夜间时长单价（元/分钟） --car_biz_pricing_plan_snapshoot字段

    private BigDecimal nighitDurationFees;  //夜间时长费用 （夜间时长数*夜间时长单价）

    private BigDecimal othersFee;  //价外费用(如果乘客结账时付现金给司机,司机不能填写价外费用)(生成司机代付流水)  包括：高速费、停车费、机场服务费、食宿费

    private BigDecimal gsFees = BigDecimal.ZERO;  //高速费(价外费用)

    private BigDecimal tcFees = BigDecimal.ZERO;  //停车费(价外费用)

    private BigDecimal jcFees = BigDecimal.ZERO;  //机场服务费(价外费用)

    private BigDecimal ssFees = BigDecimal.ZERO;  //食宿费(价外费用)

    private BigDecimal yyFees = BigDecimal.ZERO;  //语音服务费(价外费用)

    private BigDecimal ddFees = BigDecimal.ZERO;  //调度费(价外费用)

    private BigDecimal designatedDriverFee = BigDecimal.ZERO;  //指定司机附加费

    private Integer couponId;  //优惠券ID

    private BigDecimal couponSettleAmout;  //优惠券抵扣

    private BigDecimal couponAmout;  //优惠券抵扣

    private String detail; //优惠文案

    private BigDecimal aliPay;  //支付宝支付 --car_biz_order_settle_detail_extension字段

    private BigDecimal wxPay;  //微信支付 --car_biz_order_settle_detail_extension字段

    private BigDecimal creditPay;  //信用卡支付 --car_biz_order_settle_detail_extension字段

    private BigDecimal accountPay;  //账户支付 --字段

    private BigDecimal waitingMinutes;  //等待时长

    private BigDecimal waitingFee;  //等待费用

    private BigDecimal reductionPrice;  //减免金额

    private BigDecimal reductionTotalprice;  //订单最终金额

    private String reductionReason;  //减免原因

    private Integer reductionPerson;  //减免人

    private Date reductionDate;  //减免时间

    private BigDecimal outServiceMileage;

    private BigDecimal outServicePrice;

    private BigDecimal nightServiceMileage;

    private BigDecimal nightServicePrice;

    private BigDecimal forecastAmount;  //预估费用

    private String endDateString;

    private Date endDate;  //订单完成时间

    private Integer travelTimeStart;  //计价前行驶时间

    private Long travelMileageStart;  //计价前行驶里程

    private Integer travelTimeEnd;  //计价后行驶时间

    private Long travelMileageEnd;  //计价后行驶里程

    private BigDecimal driverPay;  //司机代收金额

    private BigDecimal posPay;  //pos刷卡支付金额

    private BigDecimal customerOweAmount;  //乘客此订单结算后账户欠款金额

    private BigDecimal customerRejectPay;  //乘客拒付金额

    private Date finalServiceTime;  //行程结束时间

    private BigDecimal hottimeFee;  //高峰时期费用

    private Integer autoCouponsFlag;  //乘客是否手动选择优惠券 1.没有手动选择   2.手动选择

    private String channelName;  //渠道号

    private Integer channelDiscountType;  //渠道优惠类型

    private BigDecimal channelDiscountAmount;  //渠道优惠金额

    private BigDecimal channelPay;  //渠道代收  和司机代收一样

    private BigDecimal channelFlodAmount;  //渠道固定优惠

    private Date channelPayDate;  //渠道代收时间  和更新时间

    private BigDecimal thirdPay;  //三方代收

    private Double channelDiscountPercent; //渠道优惠折扣

    private BigDecimal channelDiscountDriver;  //渠道司机折扣

    private Integer buyOutFlag;  //订单标示

    private BigDecimal cancelAmount; //违约金

    private BigDecimal settleAmout;  //结算金额  与totalAmount一样

    private BigDecimal energyDiscountAmout = BigDecimal.ZERO;  //新能量源折扣金额  --car_biz_order_settle_detail_extension字段 extend10

    private Integer orderStatus; //订单状态

    private Date createDate; //费用明细创建时间
    /**
     * 机构订单个人部分充值账户结算金额
     */
    private BigDecimal generalChargeSettleAmount;

    /**
     * 机构订单个人部分赠送账户结算金额
     */
    private BigDecimal generalGiftSettleAmount;
    /**
     * 机构订单个人部分信用卡结算金额
     */
    private BigDecimal generalCreditcardAmount;

    private BigDecimal otherSettleAmount;  //其他支付方式  比如：微信支付宝免密支付

    private BigDecimal paymentDiscountAmount;  //支付渠道优惠

    private Integer payType;  //支付方式

    private BigDecimal dynamic_price; //动态折扣，针对摩拜新用户优惠   如果新用户优惠高则用新用户   如果渠道优惠高则用渠道优惠

    private Integer chargeType = 0;  //公务卡支付  1-公务卡支付    0-非公务卡支付

    private BigDecimal otherDepositAmount;   //其他定金支付金额

    private Integer isDispatchFree = 0; //是否免调度费   0-否  1-是

    private Integer isAdvance = 0; //是否垫付   0-否  1-是

    private BigDecimal redPacketsAmount = BigDecimal.ZERO;//红包抵扣金额

    List<CostTimeDetailDTO> costMileageDetailDTOList;  //里程时间段

    List<CostTimeDetailDTO> costDurationDetailDTOList; //时长时间段

    List<CostTimeDetailDTO> costLongDistanceDetailDTOList; //长途里程段

    private Integer isNewPrice = 0; //是否是新价格计划   0-否  1-是

    private BigDecimal overMileageFee = new BigDecimal(0);//超套餐里程费
    private BigDecimal overTimeFee = new BigDecimal(0);//超套餐时长费
}
