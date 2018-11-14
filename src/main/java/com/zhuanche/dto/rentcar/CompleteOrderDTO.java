package com.zhuanche.dto.rentcar;


import java.io.Serializable;

/**
 * 完成订单列表
 * @author jiadongdong
 *
 */
public class CompleteOrderDTO implements Serializable{
	/**
	 * 订单号
	 */
    private String orderNo;
    /**
     * 下单城市
     */
    private String orderCityName;

    /**
     * 订单完成时间 yyyy-MM-dd HH:mm:ss 字符串
     */
    private String completeTime;

    /**
     * 产品类型
     */
    private String productTypeName;
    /**
     * 预定车型
     */
    private String orderVehicleTypeName;
    /**
     * 总流水
     */
    private String totalPrice;
	/**
	 * 折扣后金额
	 */
    private String priceAfterDiscount;
    /**
     * 司机端金额
     */
    private String priceForDriver;
    /**
     * 优惠金额
     */
    private String discountAmount;
	/**
	 * 优惠券折扣
	 */
    private String couponDiscount;
    /**
     * 返现结算
     */
    private String returnCash;
    /**
     * 价外费用
     */
    private String specialFee;
    /**
     * 订单类别
     */
    private String orderTypeName;
	/**
	 * 订车人ID
	 */
    private String customerId;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 是否带人叫车
     */
    private String isReplace;
    /**
	 * 司机ID
	 */
    private String driverId;
    /**
     * 司机名称
     */
    private String driverName;

    /**
     * 车牌号
     */
    private String vehiclePlateNo;

    /**
     * 服务车型
     */
    private String serviceVehicleTypeName;

    /**
     * 加盟商名称
     */
    private String allianceName;

    /**
     * 计价前行驶里程
     */
    private String beforeChargeMiles;

    /**
     * 载客里程
     */
    private String loadedMiles;

    /**
     * 计价后行驶里程
     */
    private String chargeMiles;

    /**
     * 计价前行驶时长
     */
    private String beforeChargeDuration;

    /**
     * 载客时长
     */
    private String loadedDuration;

    /**
     * 计价后行驶时长
     */
    private String afterChargeDuration;

    /**
     * 实际上车地址
     */
    private String actualAbordLocation;

    /**
     * 实际下车地址
     */
    private String actualDebusLocation;

    /**
     * 机构名称
     */
    private String orgnizationName;

    /**
     * 酒店名称
     */
    private String hotelName;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 等候分钟
     */
    private String waitingMinutes;
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getOrderCityName() {
		return orderCityName;
	}
	public void setOrderCityName(String orderCityName) {
		this.orderCityName = orderCityName;
	}
	public String getCompleteTime() {
		return completeTime;
	}
	public void setCompleteTime(String completeTime) {
		this.completeTime = completeTime;
	}
	public String getProductTypeName() {
		return productTypeName;
	}
	public void setProductTypeName(String productTypeName) {
		this.productTypeName = productTypeName;
	}
	public String getOrderVehicleTypeName() {
		return orderVehicleTypeName;
	}
	public void setOrderVehicleTypeName(String orderVehicleTypeName) {
		this.orderVehicleTypeName = orderVehicleTypeName;
	}
	public String getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}
	public String getPriceAfterDiscount() {
		return priceAfterDiscount;
	}
	public void setPriceAfterDiscount(String priceAfterDiscount) {
		this.priceAfterDiscount = priceAfterDiscount;
	}
	public String getPriceForDriver() {
		return priceForDriver;
	}
	public void setPriceForDriver(String priceForDriver) {
		this.priceForDriver = priceForDriver;
	}
	public String getDiscountAmount() {
		return discountAmount;
	}
	public void setDiscountAmount(String discountAmount) {
		this.discountAmount = discountAmount;
	}
	public String getCouponDiscount() {
		return couponDiscount;
	}
	public void setCouponDiscount(String couponDiscount) {
		this.couponDiscount = couponDiscount;
	}
	public String getReturnCash() {
		return returnCash;
	}
	public void setReturnCash(String returnCash) {
		this.returnCash = returnCash;
	}
	public String getSpecialFee() {
		return specialFee;
	}
	public void setSpecialFee(String specialFee) {
		this.specialFee = specialFee;
	}
	public String getOrderTypeName() {
		return orderTypeName;
	}
	public void setOrderTypeName(String orderTypeName) {
		this.orderTypeName = orderTypeName;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getIsReplace() {
		return isReplace;
	}
	public void setIsReplace(String isReplace) {
		this.isReplace = isReplace;
	}
	public String getDriverId() {
		return driverId;
	}
	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

    public String getVehiclePlateNo() {
        return vehiclePlateNo;
    }

    public void setVehiclePlateNo(String vehiclePlateNo) {
        this.vehiclePlateNo = vehiclePlateNo;
    }

    public String getServiceVehicleTypeName() {
        return serviceVehicleTypeName;
    }

    public void setServiceVehicleTypeName(String serviceVehicleTypeName) {
        this.serviceVehicleTypeName = serviceVehicleTypeName;
    }

    public String getAllianceName() {
        return allianceName;
    }

    public void setAllianceName(String allianceName) {
        this.allianceName = allianceName;
    }

    public String getBeforeChargeMiles() {
        return beforeChargeMiles;
    }

    public void setBeforeChargeMiles(String beforeChargeMiles) {
        this.beforeChargeMiles = beforeChargeMiles;
    }

    public String getLoadedMiles() {
        return loadedMiles;
    }

    public void setLoadedMiles(String loadedMiles) {
        this.loadedMiles = loadedMiles;
    }

    public String getChargeMiles() {
        return chargeMiles;
    }

    public void setChargeMiles(String chargeMiles) {
        this.chargeMiles = chargeMiles;
    }

    public String getBeforeChargeDuration() {
        return beforeChargeDuration;
    }

    public void setBeforeChargeDuration(String beforeChargeDuration) {
        this.beforeChargeDuration = beforeChargeDuration;
    }

    public String getLoadedDuration() {
        return loadedDuration;
    }

    public void setLoadedDuration(String loadedDuration) {
        this.loadedDuration = loadedDuration;
    }

    public String getAfterChargeDuration() {
        return afterChargeDuration;
    }

    public void setAfterChargeDuration(String afterChargeDuration) {
        this.afterChargeDuration = afterChargeDuration;
    }

    public String getActualAbordLocation() {
        return actualAbordLocation;
    }

    public void setActualAbordLocation(String actualAbordLocation) {
        this.actualAbordLocation = actualAbordLocation;
    }

    public String getActualDebusLocation() {
        return actualDebusLocation;
    }

    public void setActualDebusLocation(String actualDebusLocation) {
        this.actualDebusLocation = actualDebusLocation;
    }

    public String getOrgnizationName() {
        return orgnizationName;
    }

    public void setOrgnizationName(String orgnizationName) {
        this.orgnizationName = orgnizationName;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getWaitingMinutes() {
        return waitingMinutes;
    }

    public void setWaitingMinutes(String waitingMinutes) {
        this.waitingMinutes = waitingMinutes;
    }
}