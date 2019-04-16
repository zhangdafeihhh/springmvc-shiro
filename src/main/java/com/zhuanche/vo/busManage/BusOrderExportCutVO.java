package com.zhuanche.vo.busManage;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: car-manage
 * @description:导出巴士订单列表及详情(精简版)
 * @author: admin
 * @create: 2018-08-20 16:43
 **/
@Data
public class BusOrderExportCutVO {
    /**
     * 订单id
     */
    private Integer orderId;
    /**
     * 订单编号
     */
    private String orderNo;
    /**
     * 下单时间
     */
    private Date createDate;
    /**
     * 预定用车时间
     */
    private Date bookingDate;
    /**
     * 预约上车地点
     */
    private String bookingStartAddr;
    /**
     * 预约下车地点
     */
    private String bookingEndAddr;
    /**
     * 城市
     */
    private String cityName;
    /**
     * 服务类别,根据serviceTypeId查找
     */
    private String serviceName;
    /**
     * 预约车型类别
     */
    private String bookingGroupName;
    /**
     * 车型类别 carGroupName 得到
     */
    private String factGroupName;
    /**
     * 订单类别(订单类型 0 安卓 1 | IOS 2 客服后台创建 | 3机构后台创建)
     */
    private String orderType;
    /**
     * 乘车人数量
     */
    private Integer riderCount;
    /**
     * 行李数数量
     */
    private Integer luggageCount;

    /**
     * 车牌号
     */
    private String licensePlates;
    /**
     * 完成时间
     */
    private Date factEndDate;
    /**
     * 实际上车时间
     */
    private Date factDate;
    /**
     * 实际上车地点
     */
    private String factStartAddr;
    /**
     * 实际下车地点
     */
    private String factEndAddr;
    /**
     * 单程/往返
     */
    private String isReturn;
    /**
     * 状态
     */
    private String status;
    /**
     * 取消原因
     */
    private String memo;
    /**
     * 总支付金额
     */
    private BigDecimal amount;
    /**
     * 预收费用(订单)
     */
    private Double estimatedAmountYuan;
    /**
     * 代收费用
     */
    private BigDecimal settleAmount;

    /**
     * 违约金额
     */
    private BigDecimal damageFee;
    /**
     * 优惠金额
     */
    private BigDecimal couponAmount;
    /**
     * 供应商
     */
    private String supplierName;
    /**
     * 订单来源
     */
    private String type;
    /**
     * 实际里程
     */
    private BigDecimal distance;
    /**
     * 实际时长
     */
    private Integer duration;
    /**
     * 预收支付方式(支付)
     */
    private String payToolName;
    /**
     * 代收支付方式(计费)
     */
    private String payType;
    /**
     * 司机姓名
     */
    private String driverName;
    /**
     * 司机手机号
     */
    private String driverPhone;
    /**
     * 停车费
     */
    private BigDecimal tcFee;
    /**
     * 高速费
     */
    private BigDecimal gsFee;
    /**
     * 司机住宿费
     */
    private BigDecimal hotelFee;
    /**
     * 司机餐费
     */
    private BigDecimal mealFee ;
    /**
     * 其他费
     */
    private BigDecimal qtFee;
    /**
     * 预付费支付时间
     */
    private Date finishDate;
    /**
     * 代收费支付时
     */
    private Date settleDate;
    /**
     * 指派时间
     */
    private String assigTime;
    /**
     * 改派时间
     */
    private String reassingTime;
    /**
     * 评分
     * @return
     */
    private String evaluateScore;

}
