package com.zhuanche.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class IndexBiDriverMeasureDto  implements Serializable {
    private Integer operationVerifyDriverDay; //当天司机运营合规数
    private Integer inUseDriverNum; //运营司机数

    private Integer finishClOrderNum;//'派后订单量(投诉率分母)'
    private Integer responsibleComplaintNum;//'有责投诉单量(投诉率分子)'

    private String passRateOfHeadPortrait;//司机头像运营率 = 当日运营司机头像合规数 / 当天运营司机数

}
