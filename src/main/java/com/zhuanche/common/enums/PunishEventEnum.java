package com.zhuanche.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author kjeakiry
 * 司机处罚处理状态
 */

@AllArgsConstructor
public enum PunishEventEnum {
    //dealStatus
    AUDIT_PASS(3,"审核通过"),

    AUDIT_REFUSE(4,"审核拒绝"),

    REJECT(5, "驳回");


    @Getter
    private int status;

    @Getter
    private String desc;



}
