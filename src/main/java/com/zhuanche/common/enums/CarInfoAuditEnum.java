package com.zhuanche.common.enums;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 车辆审核状态枚举
 *
 * Created by jiamingku on 2020/7/31.
 */
public enum CarInfoAuditEnum {
    /**
     * 新增,导入初始化
     */
    STATUS_1(1, "待提交", "update", "修改"),
    STATUS_2(2, "待审核", "query", "查看"),
    STATUS_3(3, "已驳回", "update", "修改"),
    STATUS_4(4, "已拒绝", "query", "查看"),
    STATUS_5(5, "已通过", "query", "查看");

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    private int statusCode;
    private String statusDesc;
    private String operation;
    private String operationDesc;

    public String getOperation() {
        return operation;
    }

    public String getOperationDesc() {
        return operationDesc;
    }

    CarInfoAuditEnum(int statusCode, String statusDesc, String operation, String operationDesc) {
        this.operation = operation;
        this.statusCode = statusCode;
        this.statusDesc = statusDesc;
        this.operationDesc = operationDesc;
    }

    public static Pair<String, String> getOperationInfo(Integer statusCode) {
        CarInfoAuditEnum carInfoAuditEnum =  Stream.of(CarInfoAuditEnum.values()).filter( e-> Objects.equals(e.getStatusCode(), statusCode)).findFirst().orElse(null);

        if (Objects.isNull(carInfoAuditEnum)) {
            return null;
        }
        return Pair.of(carInfoAuditEnum.getOperation(), carInfoAuditEnum.getOperationDesc());
    }


}
