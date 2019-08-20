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

    private Integer travelTime=0;  //行驶时长--总时长（单位：毫秒）

    private BigDecimal travelMileage = BigDecimal.ZERO;  //行驶里程--总里程（单位：公里）

    private BigDecimal totalAmount = BigDecimal.ZERO;  //订单总额

    private BigDecimal decimalsFees = BigDecimal.ZERO;  //抹零

    private BigDecimal actualPayAmount = BigDecimal.ZERO;   //乘客实际支付的钱数，抹零后的金额同时减去优惠券的金额（账户支付金额）

    private BigDecimal accountSettleAmount = BigDecimal.ZERO;  //账户结算金额（充值账户结算金额+赠送账户结算金额）

    private BigDecimal chargeSettleAmount = BigDecimal.ZERO;  //充值账户结算金额

    private BigDecimal giftSettleAmount = BigDecimal.ZERO;  //赠送账户结算金额

    private BigDecimal depositAmount = BigDecimal.ZERO;   //账户定金支付金额（充值账户定金支付金额+赠送账户定金支付金额）

    private BigDecimal depositAccountAmount = BigDecimal.ZERO;  //充值账户定金支付金额

    private BigDecimal depositGiftAmount = BigDecimal.ZERO;  //赠送账户定金支付金额

    private BigDecimal depositSettleAmount = BigDecimal.ZERO;  //账户定金支付金额（充值账户定金支付金额+赠送账户定金支付金额）

    private BigDecimal depositCreditAmount = BigDecimal.ZERO;  //信用卡账户定金支付金额

    private BigDecimal basePrice = BigDecimal.ZERO;  //基础资费（套餐费用）

    private Integer includeMinute=0;   //基础价包含时长(单位,分钟) --car_biz_pricing_plan_snapshoot字段

    private Integer includeMileage=0;  //基础价包含公里(单位,公里) --car_biz_pricing_plan_snapshoot字段

    private BigDecimal overMileageNum = BigDecimal.ZERO;  //超里程数（单位：公里）

    /**
     * 超里程单价 (元/分钟) --car_biz_pricing_plan_snapshoot字段 over_mileage_price  也叫平峰里程单价
     * 由于cost_detail 也有over_mileage_price 作为超时长费
     * 所以用overMileageUintPrice作为单价
     */
    private BigDecimal overMileageUnitPrice = BigDecimal.ZERO;

    private BigDecimal overMileagePrice = BigDecimal.ZERO;  //超里程费（超里程数*超里程单价）

    private BigDecimal overTimeNum = BigDecimal.ZERO;  //超时长数（单位：分钟）

    /**
     * 超时长单价 (元/分钟) --car_biz_pricing_plan_snapshoot字段 over_time_price   也叫平峰时长单价
     * 由于cost_detail 也有over_time_price 作为超时长费
     * 所以用overTimeUintPrice作为单价
     */
    private BigDecimal overTimeUintPrice = BigDecimal.ZERO;

    private BigDecimal overTimePrice= BigDecimal.ZERO;  //超时长费（超时长数*超时长单价）

    private String hotDuration="0";  //高峰时长数 （单位：分钟）

    private BigDecimal peakPriceTime = BigDecimal.ZERO;  //高峰时长单价 (单位：元/分钟) --car_biz_pricing_plan_snapshoot字段

    private BigDecimal hotDurationFees = BigDecimal.ZERO;  //高峰时长费用（高峰时长数*高峰时长单价）

    private String hotMileage="0";  //高峰里程数  （单位：公里）

    private BigDecimal peakPrice = BigDecimal.ZERO;  //高峰里程单价 (元/公里) --car_biz_pricing_plan_snapshoot字段

    private BigDecimal hotMileageFees = BigDecimal.ZERO;  //高峰里程费用（高峰里程数*高峰里程单价）

    private BigDecimal overMilageNumTotal = BigDecimal.ZERO;  //里程费小计 -- 平峰里程费overMileagePrice+高峰里程费hotMileageFees

    private BigDecimal overTimeNumTotal = BigDecimal.ZERO;  //时长费小计 -- 平峰时长费overTimePrice+高峰时长费hotDurationFees

    private BigDecimal longDistanceNum = BigDecimal.ZERO;  //空驶里程数（单位：公里）

    private BigDecimal longPrice = BigDecimal.ZERO;  //空驶单价(元/里程) --car_biz_pricing_plan_snapshoot字段

    private BigDecimal longDistancePrice = BigDecimal.ZERO;  //空驶费 （空驶里程数*空驶单价）

    private BigDecimal nightDistanceNum = BigDecimal.ZERO;  //夜间里程数（单位：公里）

    private BigDecimal nightPrice = BigDecimal.ZERO;  //夜间里程单价 (元/公里) --car_biz_pricing_plan_snapshoot字段

    private BigDecimal nightDistancePrice = BigDecimal.ZERO;  //夜间里程服务费 （夜间里程数*夜间里程单价）

    private String nighitDuration="0";  //夜间时长数（单位：分钟）

    private BigDecimal nightPriceTime = BigDecimal.ZERO;  //夜间时长单价（元/分钟） --car_biz_pricing_plan_snapshoot字段

    private BigDecimal nighitDurationFees = BigDecimal.ZERO;  //夜间时长费用 （夜间时长数*夜间时长单价）

    private BigDecimal othersFee = BigDecimal.ZERO;  //价外费用(如果乘客结账时付现金给司机,司机不能填写价外费用)(生成司机代付流水)  包括：高速费、停车费、机场服务费、食宿费

    private BigDecimal gsFees = BigDecimal.ZERO;  //高速费(价外费用)

    private BigDecimal tcFees = BigDecimal.ZERO;  //停车费(价外费用)

    private BigDecimal jcFees = BigDecimal.ZERO;  //机场服务费(价外费用)

    private BigDecimal ssFees = BigDecimal.ZERO;  //食宿费(价外费用)

    private BigDecimal yyFees = BigDecimal.ZERO;  //语音服务费(价外费用)

    private BigDecimal ddFees = BigDecimal.ZERO;  //调度费(价外费用)

    private BigDecimal designatedDriverFee = BigDecimal.ZERO;  //指定司机附加费

    private Integer couponId;  //优惠券ID

    private BigDecimal couponSettleAmout = BigDecimal.ZERO;  //优惠券抵扣

    private BigDecimal couponAmout = BigDecimal.ZERO;  //优惠券抵扣

    private String detail; //优惠文案

    private BigDecimal aliPay = BigDecimal.ZERO;  //支付宝支付 --car_biz_order_settle_detail_extension字段

    private BigDecimal wxPay = BigDecimal.ZERO;  //微信支付 --car_biz_order_settle_detail_extension字段

    private BigDecimal creditPay = BigDecimal.ZERO;  //信用卡支付 --car_biz_order_settle_detail_extension字段

    private BigDecimal accountPay = BigDecimal.ZERO;  //账户支付 --字段

    private BigDecimal waitingMinutes = BigDecimal.ZERO;  //等待时长

    private BigDecimal waitingFee = BigDecimal.ZERO;  //等待费用

    private BigDecimal reductionPrice = BigDecimal.ZERO;  //减免金额

    private BigDecimal reductionTotalprice = BigDecimal.ZERO;  //订单最终金额

    private String reductionReason;  //减免原因

    private Integer reductionPerson;  //减免人

    private Date reductionDate;  //减免时间

    private BigDecimal outServiceMileage = BigDecimal.ZERO;

    private BigDecimal outServicePrice = BigDecimal.ZERO;

    private BigDecimal nightServiceMileage = BigDecimal.ZERO;

    private BigDecimal nightServicePrice = BigDecimal.ZERO;

    private BigDecimal forecastAmount = BigDecimal.ZERO;  //预估费用

    private String endDateString;

    private Date endDate;  //订单完成时间

    private Integer travelTimeStart;  //计价前行驶时间

    private Long travelMileageStart;  //计价前行驶里程

    private Integer travelTimeEnd;  //计价后行驶时间

    private Long travelMileageEnd;  //计价后行驶里程

    private BigDecimal driverPay = BigDecimal.ZERO;  //司机代收金额

    private BigDecimal posPay = BigDecimal.ZERO;  //pos刷卡支付金额

    private BigDecimal customerOweAmount = BigDecimal.ZERO;  //乘客此订单结算后账户欠款金额

    private BigDecimal customerRejectPay = BigDecimal.ZERO;  //乘客拒付金额

    private Date finalServiceTime;  //行程结束时间

    private BigDecimal hottimeFee = BigDecimal.ZERO;  //高峰时期费用

    private Integer autoCouponsFlag;  //乘客是否手动选择优惠券 1.没有手动选择   2.手动选择

    private String channelName;  //渠道号

    private Integer channelDiscountType;  //渠道优惠类型

    private BigDecimal channelDiscountAmount = BigDecimal.ZERO;  //渠道优惠金额

    private BigDecimal channelPay = BigDecimal.ZERO;  //渠道代收  和司机代收一样

    private BigDecimal channelFlodAmount = BigDecimal.ZERO;  //渠道固定优惠

    private Date channelPayDate;  //渠道代收时间  和更新时间

    private BigDecimal thirdPay = BigDecimal.ZERO;  //三方代收

    private Double channelDiscountPercent; //渠道优惠折扣

    private BigDecimal channelDiscountDriver = BigDecimal.ZERO;  //渠道司机折扣

    private Integer buyOutFlag;  //订单标示

    private BigDecimal cancelAmount = BigDecimal.ZERO; //违约金

    private BigDecimal settleAmout;  //结算金额  与totalAmount一样

    private BigDecimal energyDiscountAmout = BigDecimal.ZERO;  //新能量源折扣金额  --car_biz_order_settle_detail_extension字段 extend10

    private Integer orderStatus; //订单状态

    private Date createDate; //费用明细创建时间
    /**
     * 机构订单个人部分充值账户结算金额
     */
    private BigDecimal generalChargeSettleAmount = BigDecimal.ZERO;

    /**
     * 机构订单个人部分赠送账户结算金额
     */
    private BigDecimal generalGiftSettleAmount = BigDecimal.ZERO;
    /**
     * 机构订单个人部分信用卡结算金额
     */
    private BigDecimal generalCreditcardAmount = BigDecimal.ZERO;

    private BigDecimal otherSettleAmount = BigDecimal.ZERO;  //其他支付方式  比如：微信支付宝免密支付

    private BigDecimal paymentDiscountAmount = BigDecimal.ZERO;  //支付渠道优惠

    private Integer payType;  //支付方式

    private BigDecimal dynamic_price = BigDecimal.ZERO; //动态折扣，针对摩拜新用户优惠   如果新用户优惠高则用新用户   如果渠道优惠高则用渠道优惠

    private Integer chargeType = 0;  //公务卡支付  1-公务卡支付    0-非公务卡支付

    private BigDecimal otherDepositAmount = BigDecimal.ZERO;   //其他定金支付金额

    private Integer isDispatchFree = 0; //是否免调度费   0-否  1-是

    private Integer isAdvance = 0; //是否垫付   0-否  1-是

    private BigDecimal redPacketsAmount = BigDecimal.ZERO;//红包抵扣金额

    List<CostTimeDetailDTO> costMileageDetailDTOList;  //里程时间段

    List<CostTimeDetailDTO> costDurationDetailDTOList; //时长时间段

    List<CostTimeDetailDTO> costLongDistanceDetailDTOList; //长途里程段

    private Integer isNewPrice = 0; //是否是新价格计划   0-否  1-是

    private BigDecimal overMileageFee = new BigDecimal(0);//超套餐里程费
    private BigDecimal overTimeFee = new BigDecimal(0);//超套餐时长费
    private BigDecimal overMileageTotal = BigDecimal.ZERO;//超套餐里程公里
    private BigDecimal travelTimeShow = BigDecimal.ZERO;//超套餐时长
    private BigDecimal costAmount = BigDecimal.ZERO;//容器费值
    private String costName ;

    private  BigDecimal systemDispatchFee = BigDecimal.ZERO;//平台调度费

    private BigDecimal dynamicDoublyFee = BigDecimal.ZERO;//  动态加价 金额
    private BigDecimal cleanFee  = BigDecimal.ZERO;//  基础清洁费
    private BigDecimal deepCleanFee = BigDecimal.ZERO;//  深度清洁费
    private BigDecimal cleanDeepFeeCount  = BigDecimal.ZERO;//清洁费总计=（基础清洁费+深度清洁费）
    private Integer cleanFeeFree = 0;//是否减免清洁费 0：不减免 1：减免


}
