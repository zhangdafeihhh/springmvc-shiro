package com.zhuanche.dto.mdbcarmanage;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author fanht
 * @Description
 * @Date 2019/8/8 下午4:37
 * @Version 1.0
 */
@Data
public class ScoreDetailDTO {

    private Integer driverId;

    private String name;

    private String phone;

    private String hourScore; //时长分

    private Integer isTotal;//是否计入总分 1 计入总分 0 不计入总分

    private String scoreDetailDate;

    private String baseTripScore;//基础分时长分

    private String sumOfTripScore;//计分周期时长分(含新手时长分)之和

    private String sumOfDispatchScore;//计分周期调度分之和

    private String calScoreDay;//服务时长分回滚统计周期（自然日）

    private String finishOrderDay;//服务时长分计分周期（完单日）

    private String dispatchRollDay;//调度计分周期
    //空闲时长分
    private BigDecimal dayOfKongXianSC;
    //特殊奖励分
    private BigDecimal dayOfTeShuJL;
    //新人时长分
    private BigDecimal dayOfXinRenSC;
    //调度分
    private BigDecimal dayOfDiaoDu;
    //应答分
    private BigDecimal dayOfYingDa;
    //该日服务时长分
    private BigDecimal dayOfFuWuSC;

}
