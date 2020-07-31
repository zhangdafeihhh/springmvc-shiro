package com.zhuanche.common.enums;

import java.util.Arrays;
import java.util.List;

/**
 * 车辆审核状态枚举
 *
 * Created by jiamingku on 2020/7/31.
 */
public enum CarInfoAuditEnum {
    /**
     * 新增,导入初始化
     */
    STATUS_1(1, "待提交", Arrays.asList("query","update")),
    STATUS_2(2, "待审核", Arrays.asList("query")),

    STATUS_3(3, "已驳回", Arrays.asList("query","update")),
    STATUS_4(4, "已拒绝", Arrays.asList("query")),
    STATUS_5(5, "已通过", Arrays.asList("query"));

    public List<String> getOps() {
        return ops;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    private int statusCode;
    private String statusDesc;
    private List<String> ops;

    CarInfoAuditEnum(int statusCode, String statusDesc, List<String> ops) {
        this.ops = ops;
        this.statusCode = statusCode;
        this.statusDesc = statusDesc;
    }
}
