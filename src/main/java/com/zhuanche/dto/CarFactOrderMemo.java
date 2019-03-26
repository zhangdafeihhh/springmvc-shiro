package com.zhuanche.dto;


import lombok.Data;

@Data
public class CarFactOrderMemo {
    /**
     * 新能源汽车标识
     */
    private String newEnergyFlag;

    /**
     * 派单时派给新能源汽车标识
     */
    private String assignNewEnergyFlag;

    /**
     * 周边游线路ID
     */
    private String lineNo;

    /**
     * 周边游线路名称
     */
    private String lineName;

    /**
     * 蔚来汽车标示
     */
    private String chargingExtendedFlag;

    /**
     * 多语言标示
     */
    private String lang;

    /**
     * 预定上车地址 - 英文
     */
    private String bookingStartAddrEn;

    /**
     * 预定下车地址-英文
     */
    private String bookingEndAddrEn;

    /**
     * 预定上车短地址- 英文
     */
    private String bookingStartShortAddrEn;

    /**
     * 预定下车短地址 - 英文
     */
    private String bookingEndShortAddrEn;

    /**
     * 是否支持加盟司机
     */
    private String joinDriverSign;

    /**
     * 备注
     */
    private String comment;

    /**
     * 混派标示
     */
    private String mixDispatcherSign;

    /**
     * 是否支持公务卡用车 0：不支持 1：支持
     */
    private String officialCardFlag;

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 风控预付费支付ID 当preparePay=1时 传支付ID
     */
    private String payId;

    /**
     * 是否为预付款订单 1 预付款订单、2 普通订单
     */
    private Integer preparePay;

    /**
     * 是否固定区域一口价订单标识，1表示为固定区域一口价订单
     */
    private String fixAreaBuyoutFlag;
    /**
     * 是否预约接送机服务，1表示为已预约接送机服务
     */
    private Integer isPickUp;

    /**
     * 亲情账户 子账户userId
     */
    private Integer riderUserId;
    /**
     * 是否司乘分离订单
     * 1:是
     */
    private Integer isDriverPassengerPriceSeparate;
}

