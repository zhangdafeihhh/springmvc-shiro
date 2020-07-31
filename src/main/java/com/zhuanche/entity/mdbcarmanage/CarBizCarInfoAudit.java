package com.zhuanche.entity.mdbcarmanage;

import com.zhuanche.common.enums.CarInfoAuditEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 车辆审核状态实体
 * <p>
 * Created by jiamingku on 2020/7/30.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarBizCarInfoAudit {

    private Integer id;

    private Integer carBizCarInfoTempId;

    private Integer statusCode;

    private String statusDesc;

    private String remark;

    private String createUser;

    private Date createDate;

    private Date updateDate;

    public static CarBizCarInfoAudit init(Integer carBizCarInfoTempId) {
        return CarBizCarInfoAudit.builder().carBizCarInfoTempId(carBizCarInfoTempId)
                .statusCode(CarInfoAuditEnum.STATUS_1.getStatusCode())
                .statusDesc(CarInfoAuditEnum.STATUS_1.getStatusDesc())
                .remark("sass系统添加车辆,初始化待提交状态")
                .createDate(new Date())
                .updateDate(new Date())
                .createUser("系统刷新").build();
    }
}
