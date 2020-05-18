package com.zhuanche.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kjeakiry
 * 司机处罚处理状态
 */

@AllArgsConstructor
public enum PunishStatusEnum {
    //dealStatus
    PENDING_APPEAL(1,"待申诉"),

    PENDING_AUDIT(2,"待审核"),

    AUDIT_PASS(3,"审核通过"),

    AUDIT_REFUSE(4,"审核拒绝"),

    REJECTED(5,"已驳回"),

    EXPIRED(6,"已过期");


    private final static Map<Integer, String> DEAL_STATUS_ENUM_MAP = new HashMap<Integer,String>() {{
        Arrays.stream(PunishStatusEnum.values()).forEach(e -> put(e.getStatus(), e.getDesc()));
    }};

    @Getter
    private int status;

    @Getter
    private String desc;


    /**
     * 司机处罚处理状态映射对应的描述
     * @param status
     * @return desc
     */
    public static String ofValueEmptyStringIfNull(Integer status) {
       return  DEAL_STATUS_ENUM_MAP.getOrDefault(status, "");
    }

}
